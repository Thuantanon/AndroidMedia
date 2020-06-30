precision mediump float;
uniform sampler2D mTextureUnit;
varying vec2 mTexCoord;

// 美颜参数
uniform float mScaleWhite;
uniform float mScaleBlur;
uniform float mScaleBigEyes;
uniform float mScaleThinFace;
uniform float mScaleSmallMouth;
uniform float mScaleSmallNose;
uniform float mScaleBlush;
// 纹理宽高
uniform float mWidth;
uniform float mHeight;


vec4 processBeautyBlur(vec4 color)
{
    // 磨皮
    if(mScaleBlur > 0.01 && mScaleBlur <= 1.0) {
        vec3 centralColor = color.rgb;

        float unitX = 2.0 / mWidth;
        float unitY = 2.0 / mHeight;
        vec2 blurCoordinates0 = mTexCoord + vec2(0.0 * unitX, -10.0 * unitY);
        vec2 blurCoordinates2 = mTexCoord + vec2(8.0 * unitX, -5.0 * unitY);
        vec2 blurCoordinates4 = mTexCoord + vec2(8.0 * unitX, 5.0 * unitY);
        vec2 blurCoordinates6 = mTexCoord + vec2(0.0 * unitX, 10.0 * unitY);
        vec2 blurCoordinates8 = mTexCoord + vec2(-8.0 * unitX, 5.0 * unitY);
        vec2 blurCoordinates10 = mTexCoord + vec2(-8.0 * unitX, -5.0 * unitY);

        unitX = 1.8 / mWidth;
        unitY = 1.8 / mHeight;
        vec2 blurCoordinates1 = mTexCoord + vec2(5.0 * unitX, -8.0 * unitY);
        vec2 blurCoordinates3 = mTexCoord + vec2(10.0 * unitX, 0.0 * unitY);
        vec2 blurCoordinates5 = mTexCoord + vec2(5.0 * unitX, 8.0 * unitY);
        vec2 blurCoordinates7 = mTexCoord + vec2(-5.0 * unitX, 8.0 * unitY);
        vec2 blurCoordinates9 = mTexCoord + vec2(-10.0 * unitX, 0.0 * unitY);
        vec2 blurCoordinates11 = mTexCoord + vec2(-5.0 * unitX, -8.0 * unitY);

        unitX = 1.6 / mWidth;
        unitY = 1.6 / mHeight;
        vec2 blurCoordinates12 = mTexCoord + vec2(0.0 * unitX,-6.0 * unitY);
        vec2 blurCoordinates14 = mTexCoord + vec2(-6.0 * unitX,0.0 * unitY);
        vec2 blurCoordinates16 = mTexCoord + vec2(0.0 * unitX,6.0 * unitY);
        vec2 blurCoordinates18 = mTexCoord + vec2(6.0 * unitX,0.0 * unitY);

        unitX = 1.4 / mWidth;
        unitY = 1.4 / mHeight;
        vec2 blurCoordinates13 = mTexCoord + vec2(-4.0 * unitX,-4.0 * unitY);
        vec2 blurCoordinates15 = mTexCoord + vec2(-4.0 * unitX,4.0 * unitY);
        vec2 blurCoordinates17 = mTexCoord + vec2(4.0 * unitX,4.0 * unitY);
        vec2 blurCoordinates19 = mTexCoord + vec2(4.0 * unitX,-4.0 * unitY);

        float central;
        float gaussianWeightTotal;
        float sum;
        float sampler;
        float distanceFromCentralColor;
        float gaussianWeight;

        float distanceNormalizationFactor = 3.6;

        central = texture2D(mTextureUnit, mTexCoord).g;
        gaussianWeightTotal = 0.2;
        sum = central * 0.2;

        sampler = texture2D(mTextureUnit, blurCoordinates0).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussianWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(mTextureUnit, blurCoordinates1).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussianWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(mTextureUnit, blurCoordinates2).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussianWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(mTextureUnit, blurCoordinates3).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussianWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(mTextureUnit, blurCoordinates4).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussianWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(mTextureUnit, blurCoordinates5).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussianWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(mTextureUnit, blurCoordinates6).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussianWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(mTextureUnit, blurCoordinates7).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussianWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(mTextureUnit, blurCoordinates8).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussianWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(mTextureUnit, blurCoordinates9).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussianWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(mTextureUnit, blurCoordinates10).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussianWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(mTextureUnit, blurCoordinates11).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussianWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(mTextureUnit, blurCoordinates12).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.1 * (1.0 - distanceFromCentralColor);
        gaussianWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(mTextureUnit, blurCoordinates13).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.1 * (1.0 - distanceFromCentralColor);
        gaussianWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(mTextureUnit, blurCoordinates14).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.1 * (1.0 - distanceFromCentralColor);
        gaussianWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(mTextureUnit, blurCoordinates15).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.1 * (1.0 - distanceFromCentralColor);
        gaussianWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(mTextureUnit, blurCoordinates16).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.1 * (1.0 - distanceFromCentralColor);
        gaussianWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(mTextureUnit, blurCoordinates17).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.1 * (1.0 - distanceFromCentralColor);
        gaussianWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(mTextureUnit, blurCoordinates18).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.1 * (1.0 - distanceFromCentralColor);
        gaussianWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(mTextureUnit, blurCoordinates19).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.1 * (1.0 - distanceFromCentralColor);
        gaussianWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sum = sum / gaussianWeightTotal;

        sampler = centralColor.g - sum + 0.5;

        // 高反差保留
        for(int i = 0; i < 5; ++i) {
            if(sampler <= 0.5) {
                sampler = sampler * sampler * 2.0;
            } else {
                sampler = 1.0 - ((1.0 - sampler)*(1.0 - sampler) * 2.0);
            }
        }

        float aa = 1.0 + pow(sum, 0.3) * 0.09;
        vec3 smoothColor = centralColor * aa - vec3(sampler) * (aa - 1.0);
        smoothColor = clamp(smoothColor, vec3(0.0), vec3(1.0));

        smoothColor = mix(centralColor, smoothColor, pow(centralColor.g, 0.33));
        smoothColor = mix(centralColor, smoothColor, pow(centralColor.g, 0.39));

        smoothColor = mix(centralColor, smoothColor, mScaleBlur);

        color = vec4(pow(smoothColor, vec3(0.96)), 1.0);
    }
    return color;
}

vec4 processBeautyWhite(vec4 color)
{
    // 美白
    color.r = clamp(color.r + (1.0 - color.r) * mScaleWhite * 0.25, 0.0, 1.0);
    color.g = clamp(color.g + (1.0 - color.g) * mScaleWhite * 0.25, 0.0, 1.0);
    color.b = clamp(color.b + (1.0 - color.b) * mScaleWhite * 0.25, 0.0, 1.0);
    return processBeautyBlur(color);
}

void main() {

     vec4 color = texture2D(mTextureUnit, mTexCoord);

     // 先磨皮，再美白，不然美白会影响磨皮效果
     color = processBeautyBlur(color);

     color = processBeautyWhite(color);

     gl_FragColor = color;
}