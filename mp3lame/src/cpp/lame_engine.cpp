//
// Created by 蔡锡华 on 2020-11-17.
//

#include "lame_engine.h"
#include "lame3_encorder.h"


extern "C" JNIEXPORT jstring JNICALL
Java_com_cxh_mp3lame_LameEngine_getNameVersion(JNIEnv *env, jclass clazz)
{

    return env->NewStringUTF(get_lame_version());
}

extern "C" JNIEXPORT void JNICALL
Java_com_cxh_mp3lame_LameEngine_native_1Init(JNIEnv *env, jclass clazz, jint sampleRate, jint channels, jint bitRate, jint quality)
{
    init_encoder(sampleRate, channels, bitRate, quality);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_cxh_mp3lame_LameEngine_native_1Encoder(JNIEnv *env, jclass clazz, jstring pcmPath, jstring mp3Path)
{
    jboolean isCopy;
    const char *pcmFile = env->GetStringUTFChars(pcmPath, &isCopy);
    const char *mp3File = env->GetStringUTFChars(mp3Path, &isCopy);

    if(encode(pcmFile, mp3File))
    {
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

extern "C" JNIEXPORT void JNICALL
Java_com_cxh_mp3lame_LameEngine_native_1Release(JNIEnv *env, jclass clazz)
{
    release_encoder();
}