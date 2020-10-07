precision highp float;
uniform sampler2D uTextureUnit;
uniform sampler2D uRuddyTextureUnit;
varying vec2 vTexCoord;
uniform float mScale;
uniform vec2 mRuddyLeft;
uniform vec2 mRuddyRight;
uniform vec2 mCenter;
uniform float mRuddyWidth;
uniform float mRuddyHeight;
uniform float mScreenRatio;
uniform int mLeftFace;

vec2 getRuddyCoord(vec2 center, float width, float height){
    float widthRate = width * 2.0;
    float heightRate = height * 2.0;
    float x = (vTexCoord.x - (center.x - width)) / widthRate;
    float y = (vTexCoord.y - (center.y - height)) / heightRate;
    if(1 == mLeftFace)
    {
        x = 1.0 - x;
    }
    return vec2(x, 1.0 - y);
}

vec4 getRuddyColor()
{
    vec2 faceLeft = vec2(mRuddyLeft.x, clamp(1.0 - mRuddyLeft.y, 0.0, 1.0));
    vec2 faceRight = vec2(mRuddyRight.x, clamp(1.0 - mRuddyRight.y, 0.0, 1.0));
    // 贴纸宽高
    float width = distance(faceLeft, faceRight) / 1.2;
    float height = width * (mRuddyWidth / mRuddyHeight) * mScreenRatio;
    // 采集点是否在方框内
    if ((vTexCoord.x > (mCenter.x - width) && vTexCoord.x < (mCenter.x + width))
    && (vTexCoord.y > (mCenter.y - height) && vTexCoord.y < (mCenter.y + height)))
    {
        vec2 samplerCoord = getRuddyCoord(mCenter, width, height);
        return texture2D(uRuddyTextureUnit, samplerCoord);
    }

    return vec4(0.0);
}

void main()
{
    vec4 colorPreview = texture2D(uTextureUnit, vTexCoord);
    vec4 colorRuddy = getRuddyColor();
    vec4 color = mix(colorPreview, colorRuddy, colorRuddy.a * mScale);

    gl_FragColor = color;
}
