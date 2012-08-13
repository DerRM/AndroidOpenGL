package com.chrissierigk.androidbinaryio;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

public class Mesh {
	
	private char[] mMagicNumber;
	private int mVersionNumber;
	private int mChunkId;
	private int mNoOfSubChunks;
	private GeometryHeader mGeometryHeader = new GeometryHeader();
	private FloatData mFloatData = new FloatData();
	private ArrayList<TriangleData> mTriangleData = new ArrayList<Mesh.TriangleData>();
	
	public char[] getMagicNumber() {
		return mMagicNumber;
	}

	public void setMagicNumber(char[] mMagicNumber) {
		this.mMagicNumber = mMagicNumber;
	}

	public int getVersionNumber() {
		return mVersionNumber;
	}

	public void setVersionNumber(int mVersionNumber) {
		this.mVersionNumber = mVersionNumber;
	}

	public int getChunkId() {
		return mChunkId;
	}

	public void setChunkId(int mChunkId) {
		this.mChunkId = mChunkId;
	}

	public int getNoOfSubChunks() {
		return mNoOfSubChunks;
	}

	public void setNoOfSubChunks(int mNoOfSubChunks) {
		this.mNoOfSubChunks = mNoOfSubChunks;
	}

	public GeometryHeader getGeometryHeader() {
		return mGeometryHeader;
	}

	public void setGeometryHeader(GeometryHeader mGeometryHeader) {
		this.mGeometryHeader = mGeometryHeader;
	}

	public FloatData getFloatData() {
		return mFloatData;
	}

	public void setFloatData(FloatData mFloatData) {
		this.mFloatData = mFloatData;
	}

	public ArrayList<TriangleData> getTriangleData() {
		return mTriangleData;
	}

	public void setTriangleData(ArrayList<TriangleData> mTriangleData) {
		this.mTriangleData = mTriangleData;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Magic Number: ");
		sb.append(this.mMagicNumber);
		sb.append("\n");
		sb.append("Version Number: ");
		sb.append(this.mVersionNumber);
		sb.append("\n");
		sb.append("Chunk ID: ");
		sb.append(this.mChunkId);
		sb.append("No of Subchunks: ");
		sb.append(this.mNoOfSubChunks);
		sb.append("\n");
		sb.append("Geometry Chunk ID: ");
		sb.append(this.mGeometryHeader.mChunkId);
		sb.append("\n");
		sb.append("No of Triangle Groups: ");
		sb.append(this.mGeometryHeader.mNoOfTriangleGroups);
		sb.append("\n");
		sb.append("Total No of Triangles: ");
		sb.append(this.mGeometryHeader.mTotalNoOfTriangles);
		sb.append("\n");
		//sb.append(this.mFloatData.toString());
		
		return sb.toString();
	}

	public static class GeometryHeader {
		
		private int mChunkId;
		private int mNoOfTriangleGroups;
		private int mTotalNoOfTriangles;
		private int mDataOffset;
		
		public int getChunkId() {
			return mChunkId;
		}
		
		public void setChunkId(int mChunkId) {
			this.mChunkId = mChunkId;
		}
		
		public int getNoOfTriangleGroups() {
			return mNoOfTriangleGroups;
		}
		
		public void setNoOfTriangleGroups(int mNoOfTriangleGroups) {
			this.mNoOfTriangleGroups = mNoOfTriangleGroups;
		}
		
		public int getTotalNoOfTriangles() {
			return mTotalNoOfTriangles;
		}
		
		public void setTotalNoOfTriangles(int mTotalNoOfTriangles) {
			this.mTotalNoOfTriangles = mTotalNoOfTriangles;
		}
		
		public int getDataOffset() {
			return mDataOffset;
		}
		
		public void setDataOffset(int mDataOffset) {
			this.mDataOffset = mDataOffset;
		}
	}
	
	public static class FloatData {

		private FloatBuffer mVertexBuffer;
		private FloatBuffer mNormalBuffer;
		
		public FloatBuffer getVertexBuffer() {
			return mVertexBuffer;
		}

		public void setVertexBuffer(FloatBuffer vertexBuffer) {
			mVertexBuffer = vertexBuffer;
		}
		
		public FloatBuffer getNormalBuffer() {
			return mNormalBuffer;
		}

		public void setNormalBuffer(FloatBuffer normalBuffer) {
			mNormalBuffer = normalBuffer;
		}
		
		public static class Vertex {
			
			private float mX;
			private float mY;
			private float mZ;
			
			public float getX() {
				return mX;
			}
			
			public void setX(float mX) {
				this.mX = mX;
			}
			
			public float getY() {
				return mY;
			}
			
			public void setY(float mY) {
				this.mY = mY;
			}
			
			public float getZ() {
				return mZ;
			}
			
			public void setZ(float mZ) {
				this.mZ = mZ;
			}
			
			public float[] toArray() {
				return new float[] {mX, mY, mZ};
			}
			
			@Override
			public String toString() {
				
				StringBuilder sb = new StringBuilder();
				
				sb.append("X: ");
				sb.append(mX);
				sb.append("\n");
				sb.append("Y: ");
				sb.append(mY);
				sb.append("\n");
				sb.append("Z: ");
				sb.append(mZ);
				sb.append("\n");
				
				return sb.toString();
			}
		}
		
		public static class Normal {
			
			private float mX;
			private float mY;
			private float mZ;
			
			public float getX() {
				return mX;
			}
			
			public void setX(float mX) {
				this.mX = mX;
			}
			
			public float getY() {
				return mY;
			}
			
			public void setY(float mY) {
				this.mY = mY;
			}
			
			public float getZ() {
				return mZ;
			}
			
			public void setZ(float mZ) {
				this.mZ = mZ;
			}
			
			public float[] toArray() {
				return new float[] {mX, mY, mZ};
			}
			
			@Override
			public String toString() {
				
				StringBuilder sb = new StringBuilder();
				
				sb.append("X: ");
				sb.append(mX);
				sb.append("\n");
				sb.append("Y: ");
				sb.append(mY);
				sb.append("\n");
				sb.append("Z: ");
				sb.append(mZ);
				sb.append("\n");
				
				return sb.toString();
			}
		}
	}
	
	public static class TriangleData {
		
		private ShortBuffer mVertexIndiceBuffer;
		private ShortBuffer mNormalIndiceBuffer;
		
		public ShortBuffer getVertexIndiceBuffer() {
			return mVertexIndiceBuffer;
		}
		
		public void setVertexIndiceBuffer(ShortBuffer vertexIndiceBuffer) {
			mVertexIndiceBuffer = vertexIndiceBuffer;
		}
		
		public ShortBuffer getNormalIndiceBuffer() {
			return mNormalIndiceBuffer;
		}
		
		public void setNormalIndiceBuffer(ShortBuffer normalIndiceBuffer) {
			mNormalIndiceBuffer = normalIndiceBuffer;
		}
	}
}
