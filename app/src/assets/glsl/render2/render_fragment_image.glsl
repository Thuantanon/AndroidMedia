precision mediump float;
uniform sampler2D mTextureUnit;
uniform int mFilterType;
uniform float mWhiteScale;
varying vec2 mTexCoord;

vec4 changeLight(vec4 color)
{
    mediump float R = color.r;
    mediump float G = color.g;
    mediump float B = color.b;
    color.r = clamp(R + mWhiteScale * 0.5f, 0.0f, 1.0f);
    color.g = clamp(G + mWhiteScale * 0.5f, 0.0f, 1.0f);
    color.b = clamp(B + mWhiteScale * 0.5f, 0.0f, 1.0f);
    return color;
}

vec4 filterColor(vec4 color)
{
    mediump float R = color.r;
    mediump float G = color.g;
    mediump float B = color.b;
    if(1 == mFilterType)
    {
        float f = clamp((R + G + B) / 3.0f, 0.0f, 1.0f);
        color.r = f;
        color.g = f;
        color.b = f;
    }
    else if(2 == mFilterType)
    {
        color.r = clamp(1.0f - R, 0.0f, 1.0f);
        color.g = clamp(1.0f - G, 0.0f, 1.0f);
        color.b = clamp(1.0f - B, 0.0f, 1.0f);
    }
    else if(3 == mFilterType)
    {
        float f = clamp((3.0f - R - G - B) / 3.0f, 0.0f, 1.0f);
        color.r = f;
        color.g = f;
        color.b = f;
    }
    else if(4 == mFilterType)
    {

    }
    return color;
}

void main()
{
     vec4 color = texture2D(mTextureUnit, mTexCoord);

     // 亮度
     color = changeLight(color);

     // 滤镜
     color = filterColor(color);

     gl_FragColor = color;
}