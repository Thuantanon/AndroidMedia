//
// Created by 蔡锡华 on 2020-06-20.
//

#include "header/gl_drawer.h"
#include "android/asset_manager.h"
#include "android/asset_manager_jni.h"
#include "android/log.h"
#include "malloc.h"

GlDrawer::GlDrawer() {
    this->vertexShader = nullptr;
    this->fragShader = nullptr;
}

GlDrawer::~GlDrawer() {
//    glDeleteProgram(mGLProgram);

}

void GlDrawer::glInit(JNIEnv *jniEnv, jobject assetsManger) {

    const char *vertexShaderPath = "glsl/render2/render_vertex_image.glsl";
    const char *fragmentShaderPath = "glsl/render2/render_fragment_image.glsl";
    char *const *vertexShader = nullptr;
    char *const *fragShader = nullptr;
    // 从Android assets加载渲染器
    AAssetManager *aAssetManager = AAssetManager_fromJava(jniEnv, assetsManger);
    AAsset *aAsset = AAssetManager_open(aAssetManager, vertexShaderPath, AASSET_MODE_UNKNOWN);
    if (aAsset) {
        off_t bufSize = AAsset_getLength(aAsset);
        char * pBuf = new char[bufSize];
        int iRealRead = AAsset_read(aAsset, pBuf, bufSize);
        if (iRealRead) {
            vertexShader = &pBuf;
            __android_log_print(ANDROID_LOG_INFO, "cai", "%s", pBuf);
        }
        AAsset_close(aAsset);
    }

    aAsset = AAssetManager_open(aAssetManager, fragmentShaderPath, AASSET_MODE_UNKNOWN);
    if (aAsset) {
        off_t bufSize = AAsset_getLength(aAsset);
        char * pBuf = new char[bufSize];
        int iRealRead = AAsset_read(aAsset, pBuf, bufSize);
        if (iRealRead) {
            fragShader = &pBuf;
            __android_log_print(ANDROID_LOG_INFO, "cai", "%s", pBuf);
        } else{

        }
        AAsset_close(aAsset);
    }
}

void GlDrawer::glCreateSurface() {

    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

    mGLProgram = glCreateProgram();

    GLint vshLength = sizeof(this->vertexShader) / sizeof(GLchar);
    GLuint vsh = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vsh, 1, this->vertexShader, &vshLength);
    glCompileShader(vsh);

    GLint fshLength = sizeof(this->fragShader) / sizeof(GLchar);
    GLuint fsh = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(vsh, 1, this->fragShader, &fshLength);
    glCompileShader(vsh);

    glAttachShader(mGLProgram, vsh);
    glAttachShader(mGLProgram, fsh);
    glLinkProgram(mGLProgram);
    glValidateProgram(mGLProgram);
    glDeleteShader(vsh);
    glDeleteShader(fsh);
}

void GlDrawer::glSizeChanged(GLint width, GLint height) {
    glViewport(0, 0, width, height);

}

void GlDrawer::glDrawFrame() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glClear(GL_DEPTH_TEST);

    // 绘制代码

}

void GlDrawer::glRelease() {

    delete this->vertexShader;
    delete this->fragShader;
}


