precision highp float;
uniform sampler2D uTextureUnit;
varying vec2 vTexCoord;
uniform float mScale;
uniform float mWidth;
uniform float mHeight;
uniform vec2 mTop;
uniform vec2 mBottom;
uniform vec2 mControlPoint;

vec2 farFromPoint(vec2 center, vec2 direct, float dist)
{
    float dis = distance(center, direct);
    float sinA = (direct.y - center.y) / dis;
    float cosA = (direct.x - center.x) / dis;
    return center + vec2(dist * cosA, dist * sinA);
}

vec2 faceThin(vec2 control, vec2 center, vec2 texCoord, float radius, vec2 texSize)
{
    vec2 pos = texCoord;
    // 避免精度损失
    vec2 controlPoint = control * texSize;
    vec2 centerPoint = center * texSize;
    vec2 coord = texCoord * texSize;
    float r = distance(controlPoint, coord);

    if (r < radius)
    {
        float scale = 1.0 -  r / radius;
        vec2 tempVec = (centerPoint - controlPoint) * pow(scale, 2.0) * mScale * 0.18;
        pos = (coord - tempVec) / texSize;

    }
    return pos;
}

vec4 face()
{
    vec2 texSize = vec2(mWidth, mHeight);
    vec2 top = vec2(mTop.x, clamp(1.0 - mTop.y, 0.0, 1.0));
    vec2 bottom = vec2(mBottom.x, clamp(1.0 - mBottom.y, 0.0, 1.0));
    vec2 center = (top + bottom) / 2.0;
    vec2 control = vec2(mControlPoint.x, clamp(1.0 - mControlPoint.y, 0.0, 1.0));
    float controlFar = distance(mTop, mBottom) / 2.0 * 0.8;

    control = farFromPoint(center, control, controlFar);

    float effectRadius = distance(mTop * texSize, mBottom * texSize) * 0.7;
    vec2 newCoord = faceThin(control, center, vTexCoord, effectRadius, vec2(mWidth, mHeight));
    vec4 color = texture2D(uTextureUnit, newCoord);

    //    if (distance(control, vTexCoord) < 0.01 || distance(center, vTexCoord) < 0.01)
    //    {
    //        color.r = 0.0;
    //    }

    return color;
}

void main()
{
    gl_FragColor = face();
}
