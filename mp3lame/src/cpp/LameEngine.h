//
// Created by 蔡锡华 on 2020-11-17.
//

#ifndef ANDROIDMEDIA_LAMEENGINE_H
#define ANDROIDMEDIA_LAMEENGINE_H

#include "jni.h"
#include "lame/lame.h"


extern "C" JNIEXPORT jstring JNICALL
Java_com_cxh_mp3lame_LameEngine_getNameVersion(JNIEnv *env, jclass clazz);


extern "C" JNIEXPORT void JNICALL
Java_com_cxh_mp3lame_LameEngine_encodeMp3(JNIEnv *env, jclass clazz);


#endif //ANDROIDMEDIA_LAMEENGINE_H
