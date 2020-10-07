precision highp float;
uniform sampler2D uTextureUnit;
varying vec2 vTexCoord;
uniform float mScale;
uniform float mWidth;
uniform float mHeight;


vec4 processBeautyBlur(vec4 color)
{
    // 磨皮
    if (mScale > 0.001 && mScale <= 1.0) {
        vec3 centralColor = color.rgb;

        float unitX = 2.0 / mWidth;
        float unitY = 2.0 / mHeight;
        vec2 blurCoordinates0 = vTexCoord + vec2(0.0 * unitX, -10.0 * unitY);
        vec2 blurCoordinates2 = vTexCoord + vec2(8.0 * unitX, -5.0 * unitY);
        vec2 blurCoordinates4 = vTexCoord + vec2(8.0 * unitX, 5.0 * unitY);
        vec2 blurCoordinates6 = vTexCoord + vec2(0.0 * unitX, 10.0 * unitY);
        vec2 blurCoordinates8 = vTexCoord + vec2(-8.0 * unitX, 5.0 * unitY);
        vec2 blurCoordinates10 = vTexCoord + vec2(-8.0 * unitX, -5.0 * unitY);

        unitX = 1.8 / mWidth;
        unitY = 1.8 / mHeight;
        vec2 blurCoordinates1 = vTexCoord + vec2(5.0 * unitX, -8.0 * unitY);
        vec2 blurCoordinates3 = vTexCoord + vec2(10.0 * unitX, 0.0 * unitY);
        vec2 blurCoordinates5 = vTexCoord + vec2(5.0 * unitX, 8.0 * unitY);
        vec2 blurCoordinates7 = vTexCoord + vec2(-5.0 * unitX, 8.0 * unitY);
        vec2 blurCoordinates9 = vTexCoord + vec2(-10.0 * unitX, 0.0 * unitY);
        vec2 blurCoordinates11 = vTexCoord + vec2(-5.0 * unitX, -8.0 * unitY);

        unitX = 1.6 / mWidth;
        unitY = 1.6 / mHeight;
        vec2 blurCoordinates12 = vTexCoord + vec2(0.0 * unitX, -6.0 * unitY);
        vec2 blurCoordinates14 = vTexCoord + vec2(-6.0 * unitX, 0.0 * unitY);
        vec2 blurCoordinates16 = vTexCoord + vec2(0.0 * unitX, 6.0 * unitY);
        vec2 blurCoordinates18 = vTexCoord + vec2(6.0 * unitX, 0.0 * unitY);

        unitX = 1.4 / mWidth;
        unitY = 1.4 / mHeight;
        vec2 blurCoordinates13 = vTexCoord + vec2(-4.0 * unitX, -4.0 * unitY);
        vec2 blurCoordinates15 = vTexCoord + vec2(-4.0 * unitX, 4.0 * unitY);
        vec2 blurCoordinates17 = vTexCoord + vec2(4.0 * unitX, 4.0 * unitY);
        vec2 blurCoordinates19 = vTexCoord + vec2(4.0 * unitX, -4.0 * unitY);

        float central;
        float gaussWeightTotal;
        float sum;
        float sampler;
        float distanceFromCentralColor;
        float gaussianWeight;
        float distanceNormalizationFactor = 3.6;

        central = texture2D(uTextureUnit, vTexCoord).g;
        gaussWeightTotal = 0.2;
        sum = central * 0.2;

        sampler = texture2D(uTextureUnit, blurCoordinates0).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(uTextureUnit, blurCoordinates1).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(uTextureUnit, blurCoordinates2).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(uTextureUnit, blurCoordinates3).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(uTextureUnit, blurCoordinates4).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(uTextureUnit, blurCoordinates5).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(uTextureUnit, blurCoordinates6).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(uTextureUnit, blurCoordinates7).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(uTextureUnit, blurCoordinates8).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(uTextureUnit, blurCoordinates9).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(uTextureUnit, blurCoordinates10).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(uTextureUnit, blurCoordinates11).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.09 * (1.0 - distanceFromCentralColor);
        gaussWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(uTextureUnit, blurCoordinates12).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.1 * (1.0 - distanceFromCentralColor);
        gaussWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(uTextureUnit, blurCoordinates13).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.1 * (1.0 - distanceFromCentralColor);
        gaussWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(uTextureUnit, blurCoordinates14).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.1 * (1.0 - distanceFromCentralColor);
        gaussWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(uTextureUnit, blurCoordinates15).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.1 * (1.0 - distanceFromCentralColor);
        gaussWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(uTextureUnit, blurCoordinates16).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.1 * (1.0 - distanceFromCentralColor);
        gaussWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(uTextureUnit, blurCoordinates17).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.1 * (1.0 - distanceFromCentralColor);
        gaussWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(uTextureUnit, blurCoordinates18).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.1 * (1.0 - distanceFromCentralColor);
        gaussWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;

        sampler = texture2D(uTextureUnit, blurCoordinates19).g;
        distanceFromCentralColor = min(abs(central - sampler) * distanceNormalizationFactor, 1.0);
        gaussianWeight = 0.1 * (1.0 - distanceFromCentralColor);
        gaussWeightTotal += gaussianWeight;
        sum += sampler * gaussianWeight;
        sum = sum / gaussWeightTotal;
        sampler = centralColor.g - sum + 0.5;

        // 高反差保留
        for (int i = 0; i < 5; ++i) {
            if (sampler <= 0.5) {
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
        smoothColor = mix(centralColor, smoothColor, mScale);

        color = vec4(pow(smoothColor, vec3(0.96)), 1.0);
    }
    return color;
}

void main()
{
    vec4 color = texture2D(uTextureUnit, vTexCoord);
    gl_FragColor = processBeautyBlur(color);
}
