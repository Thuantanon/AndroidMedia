package com.cxh.androidmedia;

/**
 * Created by Cxh
 * Time : 2019-04-17  01:15
 * Desc :
 */
public class Note {

    /**
     *
     * GLSL（OpenGL Shader Language）语法
     *  概念
     *  顶点着色器对每个顶点执行一次，用于确定顶点的位置，以及将颜色信息传递给片元着色器。
     *  片元着色器，可以理解成一个像素点，确定每个像素点的颜色。
     *
     *  GLSL是一种面向过程的语言，和C相同。
     *  GLSL的基本语法与C/C++基本相同。
     *  它完美的支持向量和矩阵操作。
     *  它是通过限定符操作来管理输入输出类型的。
     *  GLSL提供了大量的内置函数来提供丰富的扩展功能。
     *
     *  2.1、基本数据类型
     *  标量、向量、矩阵、采样器、结构体、数组、空类型
     *  标量是只有大小，没有方向的量。GLSL中的标量只有bool、int、float。
     *  向量可以看作是数组。vec2、vec3、vec4，ivec2、ivec3、ivec4、bvec2、bvec3和bvec4九种类型，数组代表维数、i表示int类型、b表示bool类型。
     *      作为颜色向量时，用rgba表示分量，就如同取数组的中具体数据的索引值。
     *      作为位置向量时，用xyzw表示分量，xyz分别表示xyz坐标，w表示向量的模。
     *      作为纹理向量时，用stpq表示分量，三维用stp表示分量，二维用st表示分量。
     *  在GLSL中矩阵拥有22、33、4*4三种类型的矩阵，分别用mat2、mat3、mat4表示。
     *  采样器是专门用来对纹理进行采样工作的，在GLSL中一般来说，一个采样器变量表示一副或者一套纹理贴图。
     *  和C语言中的结构体相同，用struct来定义结构体，关于结构体参考C语言中的结构体。
     *  数组知识也和C中相同，不同的是数组声明时可以不指定大小。
     *  空类型用void表示，仅用来声明不返回任何值得函数。
     *
     *  2.2、运算符
     *  GLSL中类型不能自动提升，也不能强制转换，但是可以使用内置函数转换。例如float f = float(1);
     *  GLSL中的限定符号主要有：
     *      attritude：一般用于各个顶点各不相同的量。如顶点颜色、坐标等。
     *      uniform：一般用于对于3D物体中所有顶点都相同的量。比如光源位置，统一变换矩阵等。
     *      varying：表示易变量，一般用于顶点着色器传递到片元着色器的量。
     *      const：常量。
     *  函数
     *      in：输入参数，无修饰符时默认为此修饰符。
     *      out：输出参数。
     *      inout：既可以作为输入参数，又可以作为输出参数。
     *  浮点精度
     *      lowp：低精度。8位。
     *      mediump：中精度。10位。
     *      highp：高精度。16位。
     *
     *  3、常见内置变量
     *  gl_Position：顶点坐标
     *  gl_PointSize：点的大小，没有赋值则为默认值1，通常设置绘图为点绘制才有意义。
     *  gl_FragCoord：当前片元相对窗口位置所处的坐标。
     *  gl_FragFacing：bool型，表示是否为属于光栅化生成此片元的对应图元的正面。
     *  gl_FragColor：当前片元颜色
     *  gl_FragData：vec4类型的数组。向其写入的信息，供渲染管线的后继过程使用。
     *
     *  4、常见内置函数
     *  常见函数：，几何函数，矩阵函数，纹理相关函数（常用）
     *  radians(x)：角度转弧度
     *  degrees(x)：弧度转角度
     *  sin(x)：正弦函数，传入值为弧度。相同的还有cos余弦函数、tan正切函数、asin反正弦、acos反余弦、atan反正切
     *  pow(x,y)：xy
     *  exp(x)：ex
     *  exp2(x)：2x
     *  log(x)：logex
     *  log2(x)：log2x
     *  sqrt(x)：x√
     *  inversesqr(x)：1x√
     *  abs(x)：取x的绝对值
     *  sign(x)：x>0返回1.0，x<0返回-1.0，否则返回0.0
     *  ceil(x)：返回大于或者等于x的整数
     *  floor(x)：返回小于或者等于x的整数
     *  fract(x)：返回x-floor(x)的值
     *  mod(x,y)：取模（求余）
     *  min(x,y)：获取xy中小的那个
     *  max(x,y)：获取xy中大的那个
     *  mix(x,y,a)：返回x∗(1−a)+y∗a
     *  step(x,a)：x< a返回0.0，否则返回1.0
     *  smoothstep(x,y,a)：a < x返回0.0，a>y返回1.0，否则返回0.0-1.0之间平滑的Hermite插值。
     *  dFdx(p)：p在x方向上的偏导数
     *  dFdy(p)：p在y方向上的偏导数
     *
     *  几何函数：
     *  length(x)：计算向量x的长度
     *  distance(x,y)：返回向量xy之间的距离
     *  dot(x,y)：返回向量xy的点积
     *  cross(x,y)：返回向量xy的差积
     *  normalize(x)：返回与x向量方向相同，长度为1的向量
     *
     *  矩阵函数：
     *  matrixCompMult(x,y)：将矩阵相乘
     *  lessThan(x,y)：返回向量xy的各个分量执行x< y的结果，类似的有greaterThan,equal,notEqual
     *  lessThanEqual(x,y)：返回向量xy的各个分量执行x<= y的结果，类似的有类似的有greaterThanEqual
     *  any(bvec x)：x有一个元素为true，则为true
     *  all(bvec x)：x所有元素为true，则返回true，否则返回false
     *
     *  纹理采样相关函数：
     *  texture2D、texture2DProj、texture2DLod、texture2DProjLod、textureCube、textureCubeLod及texture3D、
     *  texture3DProj、texture3DLod、texture3DProjLod等等
     *
     */


    /**
     *
     * GLES30类和Matrix类常用API
     *
     * 1、GLES30获取着色器程序内成员变量的id（句柄、指针）
     *  GLES30.glGetAttribLocation() 方法：获取着色器程序中，指定为attribute类型变量的id。
     *  GLES30.glGetUniformLocation() 方法：获取着色器程序中，指定为uniform类型变量的id。
     *
     * 2、向着色器传递数据
     *  // 使用shader程序
     *  GLES30.glUseProgram(mProgram);
     *  // 将最终变换矩阵传入shader程序
     *  GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
     *  // 设置缓冲区起始位置
     *  mRectBuffer.position(0);
     *  // 顶点位置数据传入着色器
     *  GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 20, mRectBuffer);
     *  // 顶点颜色数据传入着色器中
     *  GLES30.glVertexAttribPointer(maColorHandle, 4, GLES30.GL_FLOAT, false, 4*4, mColorBuffer);
     *  // 顶点坐标传递到顶点着色器
     *  GLES30.glVertexAttribPointer(maTextureHandle, 2, GLES30.GL_FLOAT, false, 20, mRectBuffer);
     *  // 允许使用顶点坐标数组
     *  GLES30.glEnableVertexAttribArray(maPositionHandle);
     *  // 允许使用顶点颜色数组
     *  GLES30.glDisableVertexAttribArray(maColorHandle);
     *  // 允许使用定点纹理数组
     *  GLES30.glEnableVertexAttribArray(maTextureHandle);
     *  // 绑定纹理
     *  GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
     *  GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture);
     *  // 图形绘制
     *  GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 4);
     *
     *  2.2、定义顶点属性数组
     *  void glVertexAttribPointer (int index, int size, int type, boolean normalized, int stride, Buffer ptr)
     *      index 指定要修改的顶点着色器中顶点变量id
     *      size 指定每个顶点属性的组件数量。必须为1、2、3或者4。如position是由3个（x,y,z）组成，而颜色是4个（r,g,b,a）。
     *      type 指定数组中每个组件的数据类型。可用的符号常量有GL_BYTE, GL_UNSIGNED_BYTE, GL_SHORT,GL_UNSIGNED_SHORT, GL_FIXED, 和 GL_FLOAT，初始值为GL_FLOAT；
     *      normalized 指定当被访问时，固定点数据值是否应该被归一化（GL_TRUE）或者直接转换为固定点值（GL_FALSE）
     *      stride 指定连续顶点属性之间的偏移量。如果为0，那么顶点属性会被理解为：它们是紧密排列在一起的。
     *      ptr 顶点的缓冲数据。
     *
     *  启用或者禁用顶点属性数组
     *  GLES30.glEnableVertexAttribArray(glHPosition);
     *  GLES30.glEnableVertexAttribArray(glHCoordinate);
     *
     *  2.3、选择活动纹理单元。
     *  void glActiveTexture (int texture)
     *
     *
     * Matrix
     * 1、矩阵和向量的重要性：
     *  我们知道OpenGl中实现图形的操作大量使用了矩阵，在OpenGL中使用的向量为列向量，我们通过利用矩阵与列向量（颜色、坐标都可看做列向量）相乘，得到一个新的列向量。
     *  利用这点，我们构建一个的矩阵，与图形所有的顶点坐标坐标相乘，得到新的顶点坐标集合。
     *  当这个矩阵构造恰当的话，新得到的顶点坐标集合形成的图形相对原图形就会出现平移、旋转、缩放或拉伸、抑或扭曲的效果。
     *
     * 2、Matrix作用
     *  Matrix就是专门设计出来帮助我们简化矩阵和向量运算操作的，里面所有的实现原理都是线性代数中的运算。
     *
     * 3、常用举证变换函数（不例举，需要时查找）
     *
     *
     */


    /**
     *
     * Matrix辅助矩阵变换方法
     * // 相机设置
     * 1. 相机位置：相机的位置是比较好理解的，就是相机在3D空间里面的坐标点。
     * 2. 相机观察方向：相机的观察方向，表示的是相机镜头的朝向，你可以朝前拍、朝后拍、也可以朝左朝右，或者其他的方向。
     * 3. 相机UP方向：相机的UP方向，可以理解为相机顶端指向的方向。比如你把相机斜着拿着，拍出来的照片就是斜着的，你倒着拿着，拍出来的就是倒着的。
     *  public static void setLookAtM(float[] rm, //接收相机变换矩阵
     *                 				int rmOffset, //变换矩阵的起始位置（偏移量）
     *                 				float eyeX,float eyeY, float eyeZ, //相机位置
     *                 				float centerX,float centerY,float centerZ,  //观测点位置
     *                 				float upX,float upY,float upZ)  //up向量在xyz上的分量，也就是相机正上方位置) {
     *  }
     *
     *
     *  // 正交投影，物体呈现出来的大小不会随着其距离视点的远近而发生变化。
     * public static void orthoM(float[] m, //接收正交投影的变换矩阵
     *                 int mOffset, //变换矩阵的起始位置（偏移量）
     *                 float left, //相对观察点近面的左边距
     *                 float right,//相对观察点近面的右边距
     *                 float bottom, //相对观察点近面的下边距
     *                 float top,//相对观察点近面的上边距
     *                 float near,//相对观察点近面距离
     *                 float far) //相对观察点远面距离{
     *
     *  }
     *
     *
     *  // 透视投影，物体离视点越远，呈现出来的越小。
     *  public static void frustumM(float[] m, //接收透视投影的变换矩阵
     * 				                int mOffset, //变换矩阵的起始位置（偏移量）
     * 				                float left,//相对观察点近面的左边距
     * 				                float right,//相对观察点近面的右边距
     * 				                float bottom, //相对观察点近面的下边距
     * 				                float top, //相对观察点近面的上边距
     * 				                float near, //相对观察点近面距离
     * 				                float far) //相对观察点远面距离 {
     *  }
     *
     *  // 计算变换方阵
     *  Matrix.multiplyMM (float[] result, //接收相乘结果
     *                 int resultOffset,  //接收矩阵的起始位置（偏移量）
     *                 float[] lhs,       //左矩阵
     *                 int lhsOffset,     //左矩阵的起始位置（偏移量）
     *                 float[] rhs,       //右矩阵
     *                 int rhsOffset)     //右矩阵的起始位置（偏移量）{
     *
     *  }
     *
     */


}
