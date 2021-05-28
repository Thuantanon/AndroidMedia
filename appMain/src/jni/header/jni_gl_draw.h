//
// Created by 蔡锡华 on 2020-06-23.
//

#include "jni.h"

#ifndef ANDROIDMEDIA_JNI_GL_DRAW_H
#define ANDROIDMEDIA_JNI_GL_DRAW_H

extern "C" {


JNIEXPORT void JNICALL
Java_com_cxh_androidmedia_jni_OpenGLHelper_glInit(JNIEnv *env, jclass instance, jobject assetsManager);


JNIEXPORT void JNICALL
Java_com_cxh_androidmedia_jni_OpenGLHelper_glOnCreateSurface(JNIEnv *env, jclass instance);


JNIEXPORT void JNICALL
Java_com_cxh_androidmedia_jni_OpenGLHelper_glSizeChanged(JNIEnv *env, jclass instance, jint w, jint h);


JNIEXPORT void JNICALL
Java_com_cxh_androidmedia_jni_OpenGLHelper_glDrawFrame(JNIEnv *env, jclass instance);


JNIEXPORT void JNICALL
Java_com_cxh_androidmedia_jni_OpenGLHelper_glRelease(JNIEnv *env, jclass instance);

JNIEXPORT jbyteArray JNICALL
Java_com_cxh_androidmedia_jni_OpenGLHelper_native_1readPixels(JNIEnv *env, jclass clazz, jint width, jint height);

}


#endif //ANDROIDMEDIA_JNI_GL_DRAW_H
