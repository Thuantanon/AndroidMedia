precision mediump float;
uniform sampler2D mTextureUnit;
varying vec2 mTexCoord;
varying float mWhiteScale;
void main() {
     vec4 color = texture2D(mTextureUnit, mTexCoord);
     color.r = max(min(color.r + mWhiteScale, 1.0f), 0.0f);
     color.g = max(min(color.g + mWhiteScale, 1.0f), 0.0f);
     color.b = max(min(color.b + mWhiteScale, 1.0f), 0.0f);
     gl_FragColor = color;
}