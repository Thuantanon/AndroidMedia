attribute vec4 aTexPosition;
attribute vec2 aTexCoord;
uniform mat4 uMatrix;
varying vec2 vTexCoord;

void main()
{
    gl_Position = aTexPosition * uMatrix;
    vTexCoord = aTexCoord;
}