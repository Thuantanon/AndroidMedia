precision mediump float;
uniform sampler2D mTextureUnit;
varying vec2 mTexCoord;
varying float mWhiteScale;
void main() {
     vec4 color = texture2D(mTextureUnit, mTexCoord);
     color.r = min(color.r + mWhiteScale, 1.0f);
     color.g = min(color.g + mWhiteScale, 1.0f);
     color.b = min(color.b + mWhiteScale, 1.0f);
     gl_FragColor = color;
}