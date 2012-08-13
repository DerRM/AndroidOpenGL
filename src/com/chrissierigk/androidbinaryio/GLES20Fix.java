package com.chrissierigk.androidbinaryio;

public class GLES20Fix {

	public native static void glDrawElements(int mode, int count, int type, int offset);
	public native static void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, int offset);
	
	private GLES20Fix() {}
	
	static {
		System.loadLibrary("GLES20Fix");
	}
}
