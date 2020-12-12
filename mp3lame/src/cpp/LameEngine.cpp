//
// Created by 蔡锡华 on 2020-11-17.
//

#include "LameEngine.h"


extern "C" JNIEXPORT jstring JNICALL
Java_com_cxh_mp3lame_LameEngine_getNameVersion(JNIEnv *env, jclass clazz)
{

    return env->NewStringUTF(get_lame_version());
}

extern "C" JNIEXPORT void JNICALL
Java_com_cxh_mp3lame_LameEngine_encodeMp3(JNIEnv *env, jclass clazz)
{

}