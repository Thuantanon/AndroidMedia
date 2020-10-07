precision highp float;
uniform sampler2D uTextureUnit;
varying vec2 vTexCoord;
uniform float mScale;

void main()
{
    vec4 color = texture2D(uTextureUnit, vTexCoord);
    gl_FragColor = color;
}
