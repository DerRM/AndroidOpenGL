package com.chrissierigk.androidbinaryio;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.chrissierigk.androidbinaryio.Mesh.FloatData.Vertex;
import com.chrissierigk.androidbinaryio.Mesh.TriangleData;

public class MainActivity extends Activity {

	private GLSurfaceView mGlSurfaceView;
	private Mesh mesh;

	private float[] positionArray;
	private float[] normalArray;
	private float[] texCoordArray;
	
	private short[] positionIndices;
	private short[] normalIndices;
	private short[] texCoordIndices;
	
	private float mPreviousX = 0;
	private float mPreviousY = 0;
	private GLRenderer mRenderer;
	private float mDensity;
	
	/**
	 * TODO:

For each input vertex
    Try to find a similar ( = same for all attributes ) vertex between all those we already output
    If found :
        A similar vertex is already in the VBO, use it instead !
    If not found :
        No similar vertex found, add it to the VBO

http://code.google.com/p/opengl-tutorial-org/source/browse/common/vboindexer.cpp
	 */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mGlSurfaceView = new GLSurfaceView(this);        
        mGlSurfaceView.setEGLContextClientVersion(2);
        
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mDensity = displayMetrics.density;
        
        try {
        	mesh = new Mesh();
			InputStream is = getResources().openRawResource(R.raw.test);
			DataInputStream input = new DataInputStream(is);
			
			mesh.setMagicNumber(readChar(input));
			mesh.setVersionNumber(readInt(input));
			mesh.setChunkId(readInt(input));
			mesh.setNoOfSubChunks(readInt(input));
			mesh.getGeometryHeader().setChunkId(readInt(input));
			mesh.getGeometryHeader().setNoOfTriangleGroups(readInt(input));
			mesh.getGeometryHeader().setTotalNoOfTriangles(readInt(input));
			mesh.getGeometryHeader().setDataOffset(readInt(input));
			
			int verticesCount = readInt(input) / 3;
			positionArray = new float[verticesCount * 3];

			
			Log.d("MainTest", "Vertices Count: " + verticesCount);
			
			for (int i = 0; i < verticesCount; i++) {
				positionArray[i * 3] = readFloat(input);
				positionArray[(i * 3) + 1] = readFloat(input);
				positionArray[(i * 3) + 2] = readFloat(input);
			}
						
			int normalCount = readInt(input) / 3;
			normalArray = new float[normalCount * 3];

			Log.d("MainTest", "Normals Count: " + normalCount);
			
			for (int i = 0; i < normalCount; i++) {
				normalArray[i * 3] = readFloat(input);
				normalArray[(i * 3) + 1] = readFloat(input);
				normalArray[(i * 3) + 2] = readFloat(input);
			}
			
			int textureCoordCount = readInt(input);
			texCoordArray = new float[textureCoordCount * 2];

			Log.d("MainTest", "TexCoord Count: " + textureCoordCount);
			
			for (int i = 0; i < textureCoordCount; i++) {
				texCoordArray[i * 2] = readFloat(input);
				texCoordArray[(i * 2) + 1] = readFloat(input);
			}
			
			for (int triangleGroups = 0; triangleGroups < mesh.getGeometryHeader().getNoOfTriangleGroups(); triangleGroups++) {
			
				int length = readInt(input);
				
				Log.d("MainTest", "Triangle Group " + triangleGroups + " Count: " + length);
				
				positionIndices = new short[length * 3];
				normalIndices = new short[length * 3];
				texCoordIndices = new short[length * 3];
				
				for (int i = 0; i < length; i++) {					
					positionIndices[i * 3] = readShort(input);
					positionIndices[(i * 3) + 1] = readShort(input);
					positionIndices[(i * 3) + 2] = readShort(input);
				
					normalIndices[i * 3] = readShort(input);
					normalIndices[(i * 3) + 1] = readShort(input);
					normalIndices[(i * 3) + 2] = readShort(input);
					
					texCoordIndices[i * 3] = readShort(input);
					texCoordIndices[(i * 3) + 1] = readShort(input);
					texCoordIndices[(i * 3) + 2] = readShort(input);
				}
								
				TriangleData triangleData = new TriangleData();
				triangleData.setPositionIndices(positionIndices);
				triangleData.setNormalIndices(normalIndices);
				triangleData.setTextureIndices(texCoordIndices);
				mesh.getTriangleData().add(triangleData);
			}
			
			rearrangeIndices();
			
			//Log.d("MainTest", readInt(input) + "");
			
			Log.d("MainTest", mesh.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        mRenderer = new GLRenderer();
        mGlSurfaceView.setRenderer(mRenderer);
        mGlSurfaceView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x = event.getX();
				float y = event.getY();
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					
					float dx = x - mPreviousX;
					float dy = y - mPreviousY;
					
					if (y > v.getHeight() / 2) {
						dx = dx * -1;
					}
					
					if (x < v.getWidth() / 2) {
						dy = dy * -1;
					}
					
					mRenderer.mAngleX += dx / mDensity / 2f;
					mRenderer.mAngleY += dy / mDensity / 2f;
					((GLSurfaceView) v).requestRender();
					
					break;
				}
				
				mPreviousX = x;
				mPreviousY = y;
				
				return true;
			}
		});
        
        setContentView(mGlSurfaceView);
    }
    
    private void rearrangeIndices() {
    	
    	HashMap<String, Vertex> vertexMap = new LinkedHashMap<String, Vertex>();
    	
		short index = 0;
    	
    	for (TriangleData data : mesh.getTriangleData()) {
    		
    		ArrayList<Short> indexList = new ArrayList<Short>();
    		
    		for (int i = 0; i < data.getPositionIndices().length; i++) {
    			
    			short positionIndex = data.getPositionIndices()[i];
    			short normalIndex = data.getNormalIndices()[i];
    			short texCoordIndex = data.getTextureIndices()[i];
    			
    			String key =  positionIndex + "." + normalIndex + "." + texCoordIndex;
    			
    			if (!vertexMap.containsKey(key)) {
    				    				
    				Vertex vertex = new Vertex();
    				vertex.setIndex(index);
    				vertex.setPositionIndex(positionIndex);
    				vertex.setNormalIndex(normalIndex);
    				vertex.setTexCoordIndex(texCoordIndex);
    				vertexMap.put(key, vertex);
    				
    				indexList.add(index);
    				
    				index++;
    			} else {
    				Vertex vertex = vertexMap.get(key);
    				indexList.add(vertex.getIndex());
    			}
    		}
    		
    		ByteBuffer bb = ByteBuffer.allocateDirect(indexList.size() * 2);
    		bb.order(ByteOrder.nativeOrder());
    		ShortBuffer indexBuffer = bb.asShortBuffer();
    		
    		Iterator<Short> iter = indexList.iterator();
    		
    		for (int i = 0; i < indexList.size(); i++) {
    			indexBuffer.put(iter.next().shortValue());
    		}
    		
    		indexBuffer.position(0);
    		data.setIndexBuffer(indexBuffer);
    		
    		Log.d("MainTest", "Capacity: " + data.getIndexBuffer().capacity());
    	}
    	
    	Log.d("MainTest", "HashMapSize: " + vertexMap.size());
    	
    	ByteBuffer bb = ByteBuffer.allocateDirect(vertexMap.size() * 3 * 4);
    	bb.order(ByteOrder.nativeOrder());
    	FloatBuffer positionBuffer = bb.asFloatBuffer();
    	
    	ByteBuffer bb1 = ByteBuffer.allocateDirect(vertexMap.size() * 3 * 4);
    	bb1.order(ByteOrder.nativeOrder());
    	FloatBuffer normalBuffer = bb1.asFloatBuffer();
    	
    	ByteBuffer bb2 = ByteBuffer.allocateDirect(vertexMap.size() * 2 * 4);
    	bb2.order(ByteOrder.nativeOrder());
    	FloatBuffer texCoordBuffer = bb2.asFloatBuffer();
    	
    	for (String key : vertexMap.keySet()) {
    		
    		Vertex vertex = vertexMap.get(key);
    		
    		short positionIndex = vertex.getPositionIndex();
    		short normalIndex = vertex.getNormalIndex();
    		short texCoordIndex = vertex.getTexCoordIndex();
    		
    		positionBuffer.put(positionArray[positionIndex * 3]);
    		positionBuffer.put(positionArray[(positionIndex * 3) + 1]);
    		positionBuffer.put(positionArray[(positionIndex * 3) + 2]);
    		
    		normalBuffer.put(normalArray[normalIndex * 3]);
    		normalBuffer.put(normalArray[(normalIndex * 3) + 1]);
    		normalBuffer.put(normalArray[(normalIndex * 3) + 2]);
    		
    		texCoordBuffer.put(texCoordArray[texCoordIndex * 2]);
    		texCoordBuffer.put(texCoordArray[(texCoordIndex * 2) + 1]);
		}
    	
    	positionBuffer.position(0);
    	normalBuffer.position(0);
    	texCoordBuffer.position(0);
    	
    	mesh.getFloatData().setPositionBuffer(positionBuffer);
    	mesh.getFloatData().setNormalBuffer(normalBuffer);
    	mesh.getFloatData().setTextureCoordBuffer(texCoordBuffer);
    }
    
    private char[] readChar(DataInputStream stream) throws IOException {
    	byte[] buffer = new byte[4];
    	stream.read(buffer);
    	
    	char[] out = new char[buffer.length];
    	
    	for (int i = 0; i < buffer.length; i++) {
    		out[i] = (char) (buffer[i] & 0xFF);
    	}
    	
    	return out;
    }
    
    private int readInt(DataInputStream stream) throws IOException {
    	byte[] buffer = new byte[4];
    	stream.read(buffer);
    	
    	int out = (buffer[0] & 0xFF) | (buffer[1] & 0xFF) << 8 | (buffer[2] & 0xFF) << 16 | (buffer[3] & 0xFF) << 24;
    	return out;
    }
    
    private float readFloat(DataInputStream stream) throws IOException {
    	byte[] buffer = new byte[4];
    	stream.read(buffer);
    	
    	int out = (buffer[0] & 0xFF) | (buffer[1] & 0xFF) << 8 | (buffer[2] & 0xFF) << 16 | (buffer[3] & 0xFF) << 24;
    	return Float.intBitsToFloat(out);
    }
    
    private short readShort(DataInputStream stream) throws IOException {
    	byte[] buffer = new byte[2];
    	stream.read(buffer);
    	
    	short out = (short) ((buffer[0] & 0xFF) | (buffer[1] & 0xFF) << 8);
    	return out;
    }
    
    private class GLRenderer implements GLSurfaceView.Renderer {

    	private int mProgram;
    	private int mPositionHandle;
    	private int mNormalHandle;
    	private int mTextureUniformHandle;
    	private int mTextureCoordinateHandle;
    	private int mTextureDataHandle;
    	private int mColorHandle;
    	private int mMVPMatrixHandle;
    	private int mMVMatrixHandle;
    	private int mLightPosHandle;
    	private int mVertices[] = new int[1];
    	private int mIndices[] = new int[mesh.getTriangleData().size()];
    	private int mNormals[] = new int[1];
    	private int mTexCoords[] = new int[1];
    	float mAngleX = 0.0f;
    	float mAngleY = -90.0f;
    	
    	private final float[] mLightPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
    	private final float[] mLightPosInWorldSpace = new float[4];
    	private final float[] mLightPosInEyeSpace = new float[4];
    	
    	private final float[] mProjMatrix = new float[16];
    	private final float[] mVMatrix = new float[16];
    	private final float[] mMVPMatrix = new float[16];
    	private float[] mRotationMatrix = new float[16];
    	private float[] mAccumulatedRotationMatrix = new float[16];
    	private float[] mTempMatrix = new float[16];
    	private float[] mModelMatrix = new float[16];
    	private float[] mLightModelMatrix = new float[16];	
    	//private float[] mTranslationMatrix = new float[16];
    	
    	private static final int COORDS_PER_VERTEX = 3;
    	private final int vertexStride = COORDS_PER_VERTEX * 4;
    	
		@Override
		public void onDrawFrame(GL10 gl) {
			GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
			
			GLES20.glUseProgram(mProgram);
			
			Matrix.setIdentityM(mLightModelMatrix, 0);
			Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, -3.0f);
			
			Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
			Matrix.multiplyMV(mLightPosInEyeSpace, 0, mVMatrix, 0, mLightPosInWorldSpace, 0);
			
			Matrix.setIdentityM(mModelMatrix, 0);
			Matrix.translateM(mModelMatrix, 0, 0.0f, -3.0f, -5.5f);
			
			Matrix.setIdentityM(mRotationMatrix, 0);
			Matrix.rotateM(mRotationMatrix, 0, -mAngleX, 0.0f, 1.0f, 0.0f);
			Matrix.rotateM(mRotationMatrix, 0, mAngleY, 1.0f, 0.0f, 0.0f);
			
			mAngleX = 0.0f;
			mAngleY = 0.0f;
			
			Matrix.multiplyMM(mTempMatrix, 0, mRotationMatrix, 0, mAccumulatedRotationMatrix, 0);
			System.arraycopy(mTempMatrix, 0, mAccumulatedRotationMatrix, 0, 16);
			
			Matrix.multiplyMM(mTempMatrix, 0, mModelMatrix, 0, mAccumulatedRotationMatrix, 0);
			System.arraycopy(mTempMatrix, 0, mModelMatrix, 0, 16);
			
			Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mModelMatrix, 0);
			
			GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);
			
			Matrix.multiplyMM(mTempMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
			System.arraycopy(mTempMatrix, 0, mMVPMatrix, 0, 16);
			
			GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1],	mLightPosInEyeSpace[2]);
			
			GLES20.glUniform4fv(mColorHandle, 1, new float[] {1.0f, 0.0f, 0,0f, 0.0f}, 0);
			
			GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
			
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
			GLES20.glUniform1i(mTextureUniformHandle, 0);
		    
		    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertices[0]);
		    GLES20Fix.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, 0);
		    GLES20.glEnableVertexAttribArray(mPositionHandle);
		    
		    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mNormals[0]);
		    GLES20Fix.glVertexAttribPointer(mNormalHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, 0);
		    GLES20.glEnableVertexAttribArray(mNormalHandle);
		    
		    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mTexCoords[0]);
		    GLES20Fix.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 4 * 2, 0);
		    GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
		    
		    for (int i = 0; i < mIndices.length; i++) {
		    	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mIndices[i]);
		    	GLES20Fix.glDrawElements(GLES20.GL_TRIANGLES, mesh.getTriangleData().get(i).getIndexBuffer().capacity(), GLES20.GL_UNSIGNED_SHORT, 0);
		    	
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		    }
	    			
		    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			GLES20.glViewport(0, 0, width, height);
			
			float ratio = (float) width / height;
			
			Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 1.0f, 1000.0f);
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			GLES20.glEnable(GLES20.GL_CULL_FACE);
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);
			
			Matrix.setLookAtM(mVMatrix, 0, 0.0f, 0.0f, -0.5f, 0.0f, 0.0f, -5.0f, 0.0f, 1.0f, 0.0f);
		
			Matrix.setIdentityM(mRotationMatrix, 0);
			Matrix.rotateM(mRotationMatrix, 0, 90.0f, 1.0f, 0.0f, 0.0f);
			
			try {
				AssetManager manager = MainActivity.this.getAssets();
				InputStream fragmentStream = manager.open("shader/simple.fs");
				InputStream vertexStream = manager.open("shader/simple.vs");
				
				Shader shader = new Shader(vertexStream, fragmentStream);
				mProgram = shader.getProgram();
			} catch (IOException e) {
				e.printStackTrace();
			}
						
			GLES20.glGenBuffers(mVertices.length, mVertices, 0);
			GLES20.glGenBuffers(mIndices.length, mIndices, 0);
			GLES20.glGenBuffers(mNormals.length, mNormals, 0);
			GLES20.glGenBuffers(mTexCoords.length, mTexCoords, 0);
			
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertices[0]);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mesh.getFloatData().getPositionBuffer().capacity() * 4, mesh.getFloatData().getPositionBuffer(), GLES20.GL_STATIC_DRAW);
			
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mNormals[0]);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mesh.getFloatData().getNormalBuffer().capacity() * 4, mesh.getFloatData().getNormalBuffer(), GLES20.GL_STATIC_DRAW);
			
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mTexCoords[0]);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mesh.getFloatData().getTextureCoordBuffer().capacity() * 4, mesh.getFloatData().getTextureCoordBuffer(), GLES20.GL_STATIC_DRAW);
			
//			Log.d("Test", "VertexBuffer Capacity: " + vertexBuffer.capacity() + " NormalBuffer Capacity: " + newNormalBuffer.capacity());
			
			int i = 0;
			
			for (TriangleData data : mesh.getTriangleData()) {
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mIndices[i]);
				GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, data.getIndexBuffer().capacity() * 2, data.getIndexBuffer(), GLES20.GL_STATIC_DRAW);
				i++;
			}
			
			//Log.d("Test", mesh.getTriangleData().get(0).getVertexIndiceBuffer().capacity() + "");
			
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
			
			mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
			mNormalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
			mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");	
			mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
			mMVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVMatrix");
			mLightPosHandle = GLES20.glGetUniformLocation(mProgram, "uLightPos");
			mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "uTexture");
			mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
			
			mTextureDataHandle = loadTexture();
			
			Matrix.setIdentityM(mAccumulatedRotationMatrix, 0);
		}
    	
		private int loadTexture() {
			android.graphics.Matrix flip = new android.graphics.Matrix();
		    flip.postScale(1f, -1f);
			
			final int[] textureHandle = new int[1];
			
			GLES20.glGenTextures(1, textureHandle, 0);
			
			if (textureHandle[0] != 0) {
				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inScaled = false;
				
				Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.raw.boy_10, options);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), flip, true);
				
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
				
				GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
				GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
				
				GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
				
				bitmap.recycle();
			}
			
			if (textureHandle[0] == 0) {
				throw new RuntimeException("Error loading texture");
			}
			
			return textureHandle[0];
		}
    }
}
