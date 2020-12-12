package com.cxh.androidmedia.render_new.render;

import android.graphics.SurfaceTexture;
import android.opengl.GLES30;

import com.cxh.androidmedia.render_new.TestFaceFilter;
import com.cxh.androidmedia.render_new.filter.TextureBlurFilter;
import com.cxh.androidmedia.render_new.filter.TextureEyeFilter;
import com.cxh.androidmedia.render_new.filter.TextureEyeShadowFilter;
import com.cxh.androidmedia.render_new.filter.TextureFaceFilter;
import com.cxh.androidmedia.render_new.filter.TextureMouthFilter;
import com.cxh.androidmedia.render_new.filter.TextureNoseFilter;
import com.cxh.androidmedia.render_new.filter.TexturePreviewFilter;
import com.cxh.androidmedia.render_new.filter.TextureRuddyFilter;
import com.cxh.androidmedia.render_new.filter.TextureWhiteFilter;
import com.cxh.androidmedia.utils.OpenGLUtils;

/**
 * Created by Cxh
 * Time : 2020-09-17  21:51
 * Desc :
 */
public class SurfaceViewRender extends BaseGLRender {

    private SurfaceTexture mSurfaceTexture;
    private int mOESTextureId;

    private TextureWhiteFilter mInputFilter;
    private TextureBlurFilter mBlurFilter;
    private TextureRuddyFilter mRuddyLeftFilter;
    private TextureRuddyFilter mRuddyRightFilter;
    private TextureEyeShadowFilter mEyeShadowLeftFilter;
    private TextureEyeShadowFilter mEyeShadowRightFilter;
    private TextureEyeFilter mLeftEyeFilter;
    private TextureEyeFilter mRightEyeFilter;
    private TextureNoseFilter mNoseFilter;
    private TextureMouthFilter mMouthFilter;
    private TextureFaceFilter mFaceLeftFilter;
    private TextureFaceFilter mFaceRightFilter;

    private TexturePreviewFilter mPreviewFilter;
    private float[] mFacePoints;

    private TestFaceFilter mFaceFilter;

    @Override
    public void initRender(int width, int height) {
        super.initRender(width, height);
        mOESTextureId = OpenGLUtils.createOESTexture();
        mSurfaceTexture = new SurfaceTexture(mOESTextureId);

        mInputFilter = new TextureWhiteFilter(width, height);
        mBlurFilter = new TextureBlurFilter(width, height);
        mRuddyLeftFilter = new TextureRuddyFilter(width, height);
        mRuddyRightFilter = new TextureRuddyFilter(width, height, true);
        mEyeShadowLeftFilter = new TextureEyeShadowFilter(width, height);
        mEyeShadowRightFilter = new TextureEyeShadowFilter(width, height, true);
        mLeftEyeFilter = new TextureEyeFilter(width, height);
        mRightEyeFilter = new TextureEyeFilter(width, height);
        mNoseFilter = new TextureNoseFilter(width, height);
        mMouthFilter = new TextureMouthFilter(width, height);
        mFaceLeftFilter = new TextureFaceFilter(width, height);
        mFaceRightFilter = new TextureFaceFilter(width, height);
        mPreviewFilter = new TexturePreviewFilter(width, height);

        mFaceFilter = new TestFaceFilter();
    }

    @Override
    public void drawFrame() {
        super.drawFrame();
        // 更新纹理
        if (null != mSurfaceTexture) {
            mSurfaceTexture.updateTexImage();

            long renderStart = System.currentTimeMillis();

            // 开始绘制
            int fboTextureId = mInputFilter.draw(mOESTextureId, mWidth, mHeight);
            fboTextureId = mBlurFilter.draw(fboTextureId, mWidth, mHeight);
            fboTextureId = mRuddyLeftFilter.draw(fboTextureId, mWidth, mHeight);
            fboTextureId = mRuddyRightFilter.draw(fboTextureId, mWidth, mHeight);
            fboTextureId = mLeftEyeFilter.draw(fboTextureId, mWidth, mHeight);
            fboTextureId = mRightEyeFilter.draw(fboTextureId, mWidth, mHeight);
            fboTextureId = mNoseFilter.draw(fboTextureId, mWidth, mHeight);
            fboTextureId = mMouthFilter.draw(fboTextureId, mWidth, mHeight);
            fboTextureId = mFaceLeftFilter.draw(fboTextureId, mWidth, mHeight);
            fboTextureId = mFaceRightFilter.draw(fboTextureId, mWidth, mHeight);
            mPreviewFilter.draw(fboTextureId, mWidth, mHeight);

//        if(null != mFacePoints) {
//            mFaceFilter.draw(mFacePoints);
//        }

//            CCLog.i("render time : " + (System.currentTimeMillis() - renderStart));

        } else {
            GLES30.glClearColor(1, 0, 0, 0);
        }
    }

    @Override
    public void release() {
        super.release();
        mSurfaceTexture.release();
        mSurfaceTexture = null;
        mInputFilter.release();
        mBlurFilter.release();
        mRuddyLeftFilter.release();
        mRuddyRightFilter.release();
        mNoseFilter.release();
        mMouthFilter.release();
        mPreviewFilter.release();
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    public void setFacePoint(float[] points) {
        mFacePoints = points;
        // 眼睛两只
        float[] leftLeftEye = new float[]{0, 0};
        float[] leftRightEye = new float[]{0, 0};
        float[] rightLeftEye = new float[]{0, 0};
        float[] rightRightEye = new float[]{0, 0};
        // 眼球位置
        float[] leftEyeCenter = new float[]{0, 0};
        float[] rightEyeCenter = new float[]{0, 0};
        // 嘴角
        float[] leftMouth = new float[]{0, 0};
        float[] rightMouth = new float[]{0, 0};
        // 鼻子
        float[] noseLeft = new float[]{0, 0};
        float[] noseRight = new float[]{0, 0};
        float[] noseCenter = new float[]{0, 0};
        // 左脸
        float[] ruddyLeftLeft = new float[]{0, 0};
        float[] ruddyLeftRight = new float[]{0, 0};
        // 右脸
        float[] ruddyRightLeft = new float[]{0, 0};
        float[] ruddyRightRight = new float[]{0, 0};
        // 左脸控制点
        float[] faceLeftTop = new float[]{0, 0};
        float[] faceLeftBottom = new float[]{0, 0};
        // 右脸控制点
        float[] faceRightTop = new float[]{0, 0};
        float[] faceRightBottom = new float[]{0, 0};

        // 瘦脸控制点
        float[] leftFaceControl = new float[]{0, 0};
        float[] rightFaceControl = new float[]{0, 0};

        if (null != points) {
            leftLeftEye[0] = points[94 * 2];
            leftLeftEye[1] = points[94 * 2 + 1];
            leftRightEye[0] = points[59 * 2];
            leftRightEye[1] = points[59 * 2 + 1];

            rightLeftEye[0] = points[27 * 2];
            rightLeftEye[1] = points[27 * 2 + 1];
            rightRightEye[0] = points[20 * 2];
            rightRightEye[1] = points[20 * 2 + 1];

            leftEyeCenter[0] = points[54 * 2];
            leftEyeCenter[1] = points[54 * 2 + 1];
            rightEyeCenter[0] = points[52 * 2];
            rightEyeCenter[1] = points[52 * 2 + 1];

            leftMouth[0] = points[45 * 2];
            leftMouth[1] = points[45 * 2 + 1];
            rightMouth[0] = points[50 * 2];
            rightMouth[1] = points[50 * 2 + 1];

            noseLeft[0] = points[31 * 2];
            noseLeft[1] = points[31 * 2 + 1];
            noseRight[0] = points[93 * 2];
            noseRight[1] = points[93 * 2 + 1];
            noseCenter[0] = points[69 * 2];
            noseCenter[1] = points[69 * 2 + 1];

            ruddyLeftLeft[0] = points[8 * 2];
            ruddyLeftLeft[1] = points[8 * 2 + 1];
            ruddyLeftRight[0] = points[31 * 2];
            ruddyLeftRight[1] = points[31 * 2 + 1];
            ruddyRightLeft[0] = points[93 * 2];
            ruddyRightLeft[1] = points[93 * 2 + 1];
            ruddyRightRight[0] = points[17 * 2];
            ruddyRightRight[1] = points[17 * 2 + 1];

            faceLeftTop[0] = points[9 * 2];
            faceLeftTop[1] = points[9 * 2 + 1];
            faceLeftBottom[0] = points[77 * 2];
            faceLeftBottom[1] = points[77 * 2 + 1];

            faceRightTop[0] = points[14 * 2];
            faceRightTop[1] = points[14 * 2 + 1];
            faceRightBottom[0] = points[95 * 2];
            faceRightBottom[1] = points[95 * 2 + 1];

            leftFaceControl[0] = points[66 * 2];
            leftFaceControl[1] = points[66 * 2 + 1];
            rightFaceControl[0] = points[49 * 2];
            rightFaceControl[1] = points[49 * 2 + 1];
        }

        mLeftEyeFilter.setEye(leftLeftEye, leftRightEye);
        mRightEyeFilter.setEye(rightLeftEye, rightRightEye);
        mMouthFilter.setMouth(leftMouth, rightMouth);
        mNoseFilter.setNose(noseLeft, noseRight, noseCenter);
        mRuddyLeftFilter.setRuddy(ruddyLeftLeft, ruddyLeftRight);
        mRuddyRightFilter.setRuddy(ruddyRightLeft, ruddyRightRight);
        mEyeShadowLeftFilter.setEye(leftLeftEye, leftRightEye, leftEyeCenter);
        mEyeShadowRightFilter.setEye(rightLeftEye, rightRightEye, rightEyeCenter);
        mFaceLeftFilter.setFace(faceLeftTop, faceLeftBottom, leftFaceControl);
        mFaceRightFilter.setFace(faceRightTop, faceRightBottom, rightFaceControl);
    }

    public TextureWhiteFilter getInputWhiteFilter() {
        return mInputFilter;
    }

    public TextureBlurFilter getBlurFilter() {
        return mBlurFilter;
    }

    public TextureRuddyFilter getRuddyLeftFilter() {
        return mRuddyLeftFilter;
    }

    public TextureRuddyFilter getRuddyRightFilter() {
        return mRuddyRightFilter;
    }

    public TextureEyeShadowFilter getEyeShadowLeftFilter() {
        return mEyeShadowLeftFilter;
    }

    public TextureEyeShadowFilter getEyeShadowRightFilter() {
        return mEyeShadowRightFilter;
    }

    public TextureEyeFilter getLeftEyeFilter() {
        return mLeftEyeFilter;
    }

    public TextureEyeFilter getRightEyeFilter() {
        return mRightEyeFilter;
    }

    public TextureNoseFilter getNoseFilter() {
        return mNoseFilter;
    }

    public TextureMouthFilter getMouthFilter() {
        return mMouthFilter;
    }

    public void setFaceScale(float scale) {
        mFaceLeftFilter.setScale(scale);
        mFaceRightFilter.setScale(scale);
    }
}
