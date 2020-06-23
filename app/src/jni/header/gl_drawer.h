//
// Created by 蔡锡华 on 2020-06-20.
//

#ifndef ANDROIDMEDIA_GL_DRAWER_H
#define ANDROIDMEDIA_GL_DRAWER_H

#include "jni.h"
#include "GLES3/gl3.h"
#include "GLES3/gl3ext.h"

class GlDrawer {

public:

    GlDrawer();

    ~GlDrawer();

    void glInit(JNIEnv *,jobject);

    void glCreateSurface();

    void glSizeChanged(GLint width, GLint height);

    void glDrawFrame();

    void glRelease();

private:

    const GLchar *const *vertexShader;
    const GLchar *const *fragShader;

    GLuint mGLProgram;

};


#endif //ANDROIDMEDIA_GL_DRAWER_H
