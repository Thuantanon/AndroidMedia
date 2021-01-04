//
// Created by 蔡锡华 on 2020-11-17.
//

#ifndef ANDROIDMEDIA_LAME_ENGINE_H
#define ANDROIDMEDIA_LAME_ENGINE_H

#include "jni.h"
#include "lame/lame.h"


extern "C" JNIEXPORT jstring JNICALL
Java_com_cxh_mp3lame_LameEngine_getNameVersion(JNIEnv *env, jclass clazz);

extern "C" JNIEXPORT void JNICALL
Java_com_cxh_mp3lame_LameEngine_native_1Init(JNIEnv *env, jclass clazz, jint sampleRate, jint channels, jint bitRate, jint quality);

extern "C" JNIEXPORT jboolean JNICALL
Java_com_cxh_mp3lame_LameEngine_native_1Encoder(JNIEnv *env, jclass clazz, jstring pcmPath, jstring mp3Path);

extern "C" JNIEXPORT void JNICALL
Java_com_cxh_mp3lame_LameEngine_native_1Release(JNIEnv *env, jclass clazz);



#endif //ANDROIDMEDIA_LAME_ENGINE_H
