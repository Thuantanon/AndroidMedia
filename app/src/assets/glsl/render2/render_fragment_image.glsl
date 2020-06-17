precision mediump float;
uniform sampler2D mTextureUnit;
varying vec2 mTexCoord;
varying float mWhiteScale;

void main()
{
     vec4 color = texture2D(mTextureUnit, mTexCoord);
     float R = color.r;
     float G = color.g;
     float B = color.b;
     color.r = R + (1.0f - R) * mWhiteScale;
     color.g = G + (1.0f - G) * mWhiteScale;
     color.b = B + (1.0f - B) * mWhiteScale;
     gl_FragColor = color;
}