package com.chrissierigk.androidbinaryio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.opengl.GLES20;
import android.util.Log;

public class Shader {

	private int mProgram;
	private int mVertexShader;
	private int mFragmentShader;
	
	public Shader(String vertexShader, String fragmentShader) {
		initProgram(vertexShader, fragmentShader);
	}
	
	public Shader(InputStream vertexStream, InputStream fragmentStream) {
		StringBuffer vs = new StringBuffer();
		StringBuffer fs = new StringBuffer();
		
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(vertexStream));
			
			String read;
			
			while ((read = in.readLine()) != null) {
				vs.append(read + "\n");
			}
			
			in = new BufferedReader(new InputStreamReader(fragmentStream));
						
			while ((read = in.readLine()) != null) {
				fs.append(read + "\n");
			}
					
			in.close();
			
			initProgram(vs.toString(), fs.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getProgram() {
		return mProgram;
	}
	
	private void initProgram(String vertexShader, String fragmentShader) {
		mVertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShader);
		
		if (mVertexShader == 0) {
			Log.e("Shader", "Could not create Vertex Shader");
		}
		
		mFragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);
		
		if (mFragmentShader == 0) {
			Log.e("Shader", "Could not create Fragment Shader");
		}
		
		mProgram = GLES20.glCreateProgram();
		
		if (mProgram != 0) {
		
			GLES20.glAttachShader(mProgram, mVertexShader);
			GLES20.glAttachShader(mProgram, mFragmentShader);
			GLES20.glLinkProgram(mProgram);
		} else {
			Log.e("Shader", "Could not create program");
		}
	}
	
	private int loadShader(int type, String shaderCode) {
		int shader = GLES20.glCreateShader(type);
		
		if (shader != 0) {
			
			GLES20.glShaderSource(shader, shaderCode);
			GLES20.glCompileShader(shader);
			
			int[] compiled = new int[1];
			
			GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
			
			if (compiled[0] == 0) {
				
				String shaderType = null;
				
				switch (type) {
				case GLES20.GL_FRAGMENT_SHADER:
					shaderType = "Fragment Shader";
					break;
				case GLES20.GL_VERTEX_SHADER:
					shaderType = "Vertex Shader";
					break;
				}
				
				Log.e("Shader", "Could not compile shader " + shaderType + ":");
				Log.e("Shader", GLES20.glGetShaderInfoLog(shader));
				GLES20.glDeleteShader(shader);
				shader = 0;
			}
		}
		
		return shader;
	}
}
