#extension GL_OES_EGL_image_external : require

precision highp float;
uniform samplerExternalOES uPreviewTex;
varying vec2 vTexCoord;
uniform float mScale;

void main()
{
    vec4 color = texture2D(uPreviewTex, vTexCoord);

    if (mScale > 0.001 && mScale <= 1.0){
        color.r = clamp(color.r + 0.25 * mScale, 0.0, 1.0);
        color.g = clamp(color.g + 0.25 * mScale, 0.0, 1.0);
        color.b = clamp(color.b + 0.25 * mScale, 0.0, 1.0);
    }

    gl_FragColor = color;
}
