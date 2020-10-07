precision highp float;
uniform sampler2D uTextureUnit;
varying vec2 vTexCoord;
uniform float mScale;
uniform float mWidth;
uniform float mHeight;
uniform vec2 mNoseLeft;
uniform vec2 mNoseRight;
uniform vec2 mNoseCenter;

vec2 farFromPoint(vec2 center, vec2 direct, float dist)
{
    float dis = distance(center, direct);
    float sinA = (direct.y - center.y) / dis;
    float cosA = (direct.x - center.x) / dis;
    return center + vec2(dist * cosA, dist * sinA);
}

vec4 filterMagnifire(vec4 color, vec2 eye, float r)
{
    float scale = 0.6 * mScale;
    float radius = r;
    float ratio = mWidth / mHeight;
    vec2 center = vec2(eye.x, eye.y / ratio);
    vec2 coord = vec2(vTexCoord.x, vTexCoord.y / ratio);
    float dis = distance(center, coord);
    if (dis < radius)
    {
        float realDis = dis * (1.0 + pow(dis / radius - 1.0, 2.0) * scale);
        vec2 samplerCoord = farFromPoint(center, coord, realDis);
        color = texture2D(uTextureUnit, vec2(samplerCoord.x, samplerCoord.y * ratio));
    }

    return color;
}

vec4 nose()
{
    vec4 color = texture2D(uTextureUnit, vTexCoord);
    float noPointDis = distance(mNoseCenter, vec2(0.0, 0.0));
    vec2 leftPoint = vec2(mNoseLeft.x, clamp(1.0 - mNoseLeft.y, 0.0, 1.0));
    vec2 rightPoint = vec2(mNoseRight.x, clamp(1.0 - mNoseRight.y, 0.0, 1.0));
    vec2 center = vec2(mNoseCenter.x, clamp(1.0 - mNoseCenter.y, 0.0, 1.0));
    if ((mScale >= 0.001 && mScale <= 1.0) && noPointDis > 0.0)
    {
        float radius = distance(leftPoint, rightPoint) * 1.2;
        color = filterMagnifire(color, center, radius);
    }

    return color;
}

void main()
{
    gl_FragColor = nose();
}
