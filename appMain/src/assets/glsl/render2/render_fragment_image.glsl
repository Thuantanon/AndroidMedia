precision mediump float;
uniform sampler2D mTextureUnit;
uniform int mFilterType;
uniform float mTexWidth;
uniform float mTexHeight;
uniform float mWhiteScale;
varying vec2 mTexCoord;

const float mosaicSize = 20.0f;
const int gaussBlurRadius = 10;
const int moduleCount = 3;

vec4 strongColor(vec4 color)
{
    float scale = 1.1f;
    if(color.r > color.g && color.r > color.b)
    {
        color.r = clamp(color.r * scale, 0.0, 1.0);
    }
    else if(color.g > color.r && color.g > color.b)
    {
        color.g = clamp(color.g * scale, 0.0, 1.0);
    }
    else if(color.b > color.r && color.b > color.g)
    {
        color.b = clamp(color.b * scale, 0.0, 1.0);
    }
    return color;
}

vec4 averageBlur(vec4 color)
{
    // 高斯模糊，求一定区域颜色平均值
    float realX = mTexWidth * mTexCoord.x;
    float realY = mTexHeight * mTexCoord.y;
    float colorCount = 0.0f;
    float tR,tG,tB;
    for(float i = 0.0f; i < float(gaussBlurRadius); i += 1.0f)
    {
        for(float j = 0.0f; j < float(gaussBlurRadius); j += 1.0f)
        {
            float x,y;
            vec4 c;
            x = realX - i;
            y = realY - j;
            x = x > 0.0 ? x : 0.0;
            y = y > 0.0 ? y : 0.0;
            c = texture2D(mTextureUnit, vec2(x / mTexWidth, y / mTexHeight));
            tR += c.r;
            tG += c.g;
            tB += c.b;
            colorCount ++;
            x = realX + i;
            y = realY + j;
            x = x > mTexWidth ? mTexWidth : x;
            y = y > mTexHeight ? mTexHeight : y;
            c = texture2D(mTextureUnit, vec2(x / mTexWidth, y / mTexHeight));
            tR += c.r;
            tG += c.g;
            tB += c.b;
            colorCount ++;
        }
    }
    color.r = tR / float(colorCount);
    color.g = tG / float(colorCount);
    color.b = tB / float(colorCount);
    return color;
}

vec4 gaussBlur(vec4 color, int vertical)
{
    // 权重因子，离中心点越近权重越高
    float weightUnit = 1.0 / float(gaussBlurRadius);
    float weight = 1.0f;
    float weightSum = weight;
    vec3 centerColor = color.rgb * weight;
    if(1 == vertical)
    {
        for(int i = 1; i < gaussBlurRadius; i ++)
        {
            float minUnit = float(i) / mTexHeight;
            weight -= weightUnit;
            weightSum += weight * 2.0;
            centerColor += texture2D(mTextureUnit, vec2(mTexCoord.x, mTexCoord.y + minUnit)).rgb * weight;
            centerColor += texture2D(mTextureUnit, vec2(mTexCoord.x, mTexCoord.y - minUnit)).rgb * weight;
        }
    }
    else
    {
        for(int i = 1; i < gaussBlurRadius; i ++)
        {
            float minUnit = float(i) / mTexWidth;
            weight -= weightUnit;
            weightSum += weight * 2.0;
            centerColor += texture2D(mTextureUnit, vec2(mTexCoord.x + minUnit, mTexCoord.y)).rgb * weight;
            centerColor += texture2D(mTextureUnit, vec2(mTexCoord.x - minUnit, mTexCoord.y)).rgb * weight;
        }
    }

    return vec4(centerColor / weightSum, color.a);
}

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
    mediump float A = color.a;
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
        // 方形马赛克
        float x = floor(mTexWidth * mTexCoord.x / mosaicSize) * mosaicSize + mosaicSize / 2.0f;
        float y = floor(mTexHeight * mTexCoord.y / mosaicSize) * mosaicSize + mosaicSize / 2.0f;
        x = x / mTexWidth;
        y = y / mTexHeight;
        color = texture2D(mTextureUnit, vec2(x, y));
    }
    else if(7 == mFilterType)
    {
        // 圆形马赛克
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
    else if(8 == mFilterType)
    {
        float f = (R + G + B) / 3.0f;
        if(f >= 0.33f)
        {
            color.r = 1.0f;
            color.g = 1.0f;
            color.b = 1.0f;
        }
        else
        {
            color.r = 0.0f;
            color.g = 0.0f;
            color.b = 0.0f;
        }
    }
    else if(9 == mFilterType)
    {
        // 均值模糊，求一定区域颜色平均值
        color = averageBlur(color);
    }
    else if(10 == mFilterType)
    {
        // 高斯模糊，求临近区域权重值
        vec4 vertColor = gaussBlur(color, 1);
        vec4 horiColor = gaussBlur(color, 0);
        color = (vertColor + horiColor) / 2.0;
    }
    else if(11 == mFilterType)
    {
        vec4 bkColor = vec4(0.4f, 0.4f, 0.4f, 1.0f);
        vec2 upLeftUV = vec2(mTexCoord.x - 1.0f / mTexWidth, mTexCoord.y - 1.0f / mTexHeight);
        vec4 upLeftColor = texture2D(mTextureUnit, upLeftUV);
        vec4 delColor = color - upLeftColor;

        float luminance = (delColor.r * 0.2f + delColor.g * 0.7f + delColor.b * 0.07f) / 3.0f;
        color = vec4(vec3(luminance), 0.0f) + bkColor;
    }
    else if(12 == mFilterType)
    {
        color = strongColor(color);
    }
    else if(13 == mFilterType)
    {
        // 得到真实坐标
        float realX = mTexWidth * mTexCoord.x;
        float realY = mTexHeight * mTexCoord.y;
        float moduleWidth = mTexWidth / float(moduleCount);
        float moduleHeight = mTexHeight / float(moduleCount);
        while(realX >= moduleWidth)
        {
            realX -= moduleWidth;
        }
        while(realY >= moduleHeight)
        {
            realY -= moduleHeight;
        }

        float x = (realX * float(moduleCount)) / mTexWidth;
        float y = (realY * float(moduleCount)) / mTexHeight;
        color = texture2D(mTextureUnit, vec2(x, y));
    }

    return color;
}


// 这里为了方便，将很多渲染代码写在一个shader里面，实际使用一般是用一个个FBO组成流水线的
// 关于FBO自行了解，它就是一个绘制目的地址，正常情况屏幕是从默认的FBO取数据显示
void main()
{
     vec4 color = texture2D(mTextureUnit, mTexCoord);

     // 亮度
     color = changeLight(color);

     // 滤镜
     color = filterColor(color);

     gl_FragColor = color;
}