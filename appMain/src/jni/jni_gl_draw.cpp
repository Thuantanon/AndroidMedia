//
// Created by 蔡锡华 on 2020-06-23.
//

#include "header/jni_gl_draw.h"
#include "header/gl_drawer.h"

GlDrawer *mGlDrawer = nullptr;

extern "C" JNIEXPORT void JNICALL
Java_com_cxh_androidmedia_jni_OpenGLHelper_glInit(JNIEnv *env, jclass instance, jobject assetsManager)
{

    mGlDrawer = new GlDrawer();
    mGlDrawer->glInit(env, assetsManager);
}


extern "C" JNIEXPORT void JNICALL
Java_com_cxh_androidmedia_jni_OpenGLHelper_glOnCreateSurface(JNIEnv *env, jclass instance)
{

    if (mGlDrawer)
    {
        mGlDrawer->glCreateSurface();
    }
}


extern "C" JNIEXPORT void JNICALL
Java_com_cxh_androidmedia_jni_OpenGLHelper_glSizeChanged(JNIEnv *env, jclass instance, jint w, jint h)
{

    GLint width = w;
    GLint height = h;
    if (mGlDrawer)
    {
        mGlDrawer->glSizeChanged(width, height);
    }
}


extern "C" JNIEXPORT void JNICALL
Java_com_cxh_androidmedia_jni_OpenGLHelper_glDrawFrame(JNIEnv *env, jclass instance)
{

    if (mGlDrawer)
    {
        mGlDrawer->glDrawFrame();
    }
}


extern "C" JNIEXPORT void JNICALL
Java_com_cxh_androidmedia_jni_OpenGLHelper_glRelease(JNIEnv *env, jclass instance)
{

    if (mGlDrawer)
    {
        mGlDrawer->glRelease();

        delete mGlDrawer;
    }
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_cxh_androidmedia_jni_OpenGLHelper_native_1readPixels(JNIEnv *env, jclass clazz, jint width, jint height)
{
    // TODO: implement native_readPixels()
    jsize bufferSize = width * height * 4;
    jbyte *buffer = new jbyte[bufferSize];
    glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

    jbyteArray imageBuffer = env->NewByteArray(bufferSize);
    env->SetByteArrayRegion(imageBuffer, 0, bufferSize, buffer);
    delete[] buffer;

    return imageBuffer;
}