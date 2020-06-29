
precision mediump float;
uniform sampler2D mTextureUnit;
varying vec2 mTexCoord;

void main() {

     gl_FragColor = texture2D(mTextureUnit, mTexCoord);
}