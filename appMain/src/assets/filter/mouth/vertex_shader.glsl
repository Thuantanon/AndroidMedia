attribute vec4 aTexPosition;
attribute vec2 aTexCoord;
varying vec2 vTexCoord;

void main()
{
    gl_Position = aTexPosition;
    vTexCoord = aTexCoord;
}