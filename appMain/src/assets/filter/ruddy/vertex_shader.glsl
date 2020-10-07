attribute vec4 aTexPosition;
attribute vec2 aTexCoord;
attribute vec2 aTexCoordRuddy;
varying vec2 vTexCoord;
varying vec2 vTexCoordRuddy;

void main()
{
    gl_Position = aTexPosition;
    vTexCoord = aTexCoord;
    vTexCoordRuddy = aTexCoordRuddy;
}