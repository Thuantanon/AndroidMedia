attribute vec4 vertexPosition;
attribute vec2 textureCoord;
attribute float whiteScale;
uniform mat4 uMatrix;
varying vec2 mTexCoord;
varying float mWhiteScale;
void main() {
    gl_Position = vertexPosition * uMatrix;
    mTexCoord = textureCoord;
    mWhiteScale = whiteScale;
}