precision mediump float;
uniform sampler2D mTextureUnit;
varying vec2 mTexCoord;
varying float mWhiteScale;

void main()
{
     vec4 color = texture2D(mTextureUnit, mTexCoord);
     // mediump float R = color.r;
     // mediump float G = color.g;
     // mediump float B = color.b;
     // color.r = R + (1.0f - R) * mWhiteScale * 0.5;
     // color.g = G + (1.0f - G) * mWhiteScale * 0.5;
     // color.b = B + (1.0f - B) * mWhiteScale * 0.5;
     gl_FragColor = color;
}