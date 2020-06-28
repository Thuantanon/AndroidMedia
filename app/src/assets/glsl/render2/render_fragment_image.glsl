precision mediump float;
uniform sampler2D mTextureUnit;
uniform int mFilterType;
uniform float mTexWidth;
uniform float mTexHeight;
uniform float mWhiteScale;
varying vec2 mTexCoord;

const float mosaicSize = 20.0f;

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
        // 黑白
        float f = clamp((R + G + B) / 3.0f, 0.0f, 1.0f);
        color.r = f;
        color.g = f;
        color.b = f;
    }
    else if(2 == mFilterType)
    {
        // 暖色
        color.r = clamp(R + 0.1f, 0.0f, 1.0f);
        color.g = clamp(G + 0.1f, 0.0f, 1.0f);
        color.b = B;
    }
    else if(3 == mFilterType)
    {
        // 冷色
        color.r = R;
        color.g = G;
        color.b = clamp(B + 0.2f, 0.0f, 1.0f);
    }
    else if(4 == mFilterType)
    {
        // 暖色
        color.r = clamp(1.0f - R, 0.0f, 1.0f);
        color.g = clamp(1.0f - G, 0.0f, 1.0f);
        color.b = clamp(1.0f - B, 0.0f, 1.0f);
    }
    else if(5 == mFilterType)
    {
        // 冷色
        float f = clamp((3.0f - R - G - B) / 3.0f, 0.0f, 1.0f);
        color.r = f;
        color.g = f;
        color.b = f;
    }
    else if(6 == mFilterType)
    {
        // 马赛克
        // 计算真实坐标，取方格中央颜色
        float x = floor(mTexWidth * mTexCoord.x / mosaicSize) * mosaicSize + mosaicSize / 2.0f;
        float y = floor(mTexHeight * mTexCoord.y / mosaicSize) * mosaicSize + mosaicSize / 2.0f;
        x = x / mTexWidth;
        y = y / mTexHeight;
        color = texture2D(mTextureUnit, vec2(x, y));
    }
    else if(7 == mFilterType)
    {
        // 马赛克
        // 计算真实坐标，取方格中央颜色
        float realX = mTexWidth * mTexCoord.x;
        float realY = mTexHeight * mTexCoord.y;
        float centerX = floor(realX / mosaicSize) * mosaicSize + mosaicSize / 2.0f;
        float centerY = floor(realY / mosaicSize) * mosaicSize + mosaicSize / 2.0f;
        // 计算半径
        float radius = sqrt(pow(centerX - realX, 2.0f) + pow(centerY - realY, 2.0f));
        if(radius <= mosaicSize / 2.0f)
        {
            float x = centerX / mTexWidth;
            float y = centerY / mTexHeight;
            color = texture2D(mTextureUnit, vec2(x, y));
        }
        else
        {
            color = texture2D(mTextureUnit, mTexCoord);
        }
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