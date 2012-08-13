#include <jni.h>
#include <GLES2/gl2.h>

JNIEXPORT void JNICALL Java_com_chrissierigk_androidbinaryio_GLES20Fix_glDrawElements(
    JNIEnv *env, jclass c, jint mode, jint count, jint type, jint offset) {
  glDrawElements(mode, count, type, (void*) offset);
}

JNIEXPORT void JNICALL Java_com_chrissierigk_androidbinaryio_GLES20Fix_glVertexAttribPointer(
    JNIEnv *env, jclass clazz, jint index, jint size, jint type,
    jboolean normalized, jint stride, jint offset) {
  glVertexAttribPointer(
      index, size, type, normalized, stride, (void*)offset);
}
