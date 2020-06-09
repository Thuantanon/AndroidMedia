attribute vec4 vertexPosition;
attribute vec2 textureCoord;
uniform mat4 uMatrix;
varying vec2 mTexCoord;
void main() {
    gl_Position = vertexPosition * uMatrix;
    mTexCoord = textureCoord;
}