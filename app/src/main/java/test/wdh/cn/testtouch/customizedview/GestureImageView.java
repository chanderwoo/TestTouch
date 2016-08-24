package test.wdh.cn.testtouch.customizedview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import test.wdh.cn.testtouch.constant.GlobalConstant;

/**
 * Created by wdh on 2016/8/24.
 * 具备手势处理功能的ImageView
 */
public class GestureImageView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener {

    private static final String TAG = GestureImageView.class.getSimpleName();
    private boolean isOnce = true;
    //初始缩放值
    private float currentScale;
    private static int mWidth = GlobalConstant.getDeviceWidth();
    private static int mHeight = GlobalConstant.getDeviceHeight();
    private static int mDistance = (int) Math.sqrt((Math.pow(mWidth, 2) + Math.pow(mHeight, 2)));
    private final float MAX_SCALE = 4.0f;
    private float MIN_SCALE;
    private Matrix mMatrix = new Matrix();
    private float[] mMatrixValues = new float[9];
    private PointF scalePointF;
    private PointF dragPointF;
    private float preDistance = 0;
    private float[] initMatrixValues = new float[9];

    public GestureImageView(Context context) {
        super(context);
        initImageAttribute();
    }

    public GestureImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initImageAttribute();
    }

    public GestureImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initImageAttribute();
    }

    /**
     * 初始化图像属性
     */
    private void initImageAttribute() {
        setScaleType(ScaleType.MATRIX);
        setFocusable(true);// 触摸事件开启此参数用以搞事情
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            getViewTreeObserver().addOnGlobalLayoutListener(null);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                scalePointF = null;
                preDistance = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 2) {//两点触摸事件处理
                    dragPointF = null;
                    float fX = event.getX(0);
                    float fY = event.getY(0);
                    float sX = event.getX(1);
                    float sY = event.getY(1);
                    float distance = (float) Math.sqrt((Math.pow(fX - sX, 2) + Math.pow(fY - sY, 2)));
                    if (scalePointF == null) {
                        scalePointF = new PointF((fX + sX) / 2.0f, (fY + sY) / 2.0f);
                        preDistance = distance;
                    } else {
                        float scale = (distance - preDistance) / mDistance * 8;
                        float judge = currentScale + scale;
                        scale = judge > MAX_SCALE || judge < MIN_SCALE ?
                                (judge > MAX_SCALE ? MAX_SCALE - currentScale : MIN_SCALE - currentScale) :
                                scale;
                        currentScale += scale;
                        scale(currentScale / (currentScale - scale), scalePointF);
                    }
                    preDistance = distance;
                }
                if (event.getPointerCount() == 1) { //处理移动事件
                    Log.i(TAG, "处理移动事件");
                    scalePointF = null;
                    if (dragPointF == null) {
                        dragPointF = new PointF(event.getX(), event.getY());
                    } else {
                        float dragX = event.getX() - dragPointF.x;
                        float dragY = event.getY() - dragPointF.y;
                        dragPointF = new PointF(event.getX(), event.getY());
                        Log.i(TAG, "dragX = " + dragX + "  dragY = " + dragY);
                        drag(dragX, dragY);
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() == 1) {
                    scalePointF = null;
                    preDistance = 0;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 拖拽imageView
     *
     * @param dragX X轴的移动距离
     * @param dragY Y轴的移动距离
     */
    private void drag(float dragX, float dragY) {
        mMatrix.postTranslate(dragX, dragY);
        setImageMatrix(mMatrix);
        mMatrix.setValues(mMatrixValues);
    }

    /**
     * 按照缩放比例和点进行缩放
     *
     * @param scale 缩放比例
     * @param f     包含缩放中心点的PointF
     */
    private void scale(float scale, PointF f) {
        mMatrix.postScale(scale, scale, f.x, f.y);
        mMatrix.getValues(mMatrixValues);
        if (currentScale == MIN_SCALE) {//缩放到初始位置，将图片设置为初始位置,此处可以添加动画，请随意发挥。
            mMatrix.setValues(initMatrixValues);
            setImageMatrix(mMatrix);
        }
        setImageMatrix(mMatrix);
    }

    @Override
    public void onGlobalLayout() {
        Drawable d = getDrawable();
        if (isOnce) {
            isOnce = false;
            if (d == null) {
                return;
            }
            int width = d.getIntrinsicWidth();
            int height = d.getIntrinsicHeight();
            float scale = 1.0f;
            // 有3种情况，1、宽大于屏幕宽。2、高大于屏幕高。3、宽和高均大于屏幕，图像宽高均小于设备的暂时不考虑
            if (width > mWidth && height < mHeight) {
                scale = mWidth * 1.0f / width;
            }
            if (height > mHeight && width < mWidth) {
                scale = mHeight * 1.0f / height;
            }
            if (height > mHeight && width > mWidth) {
                scale = Math.min(mWidth * 1.0f / width, mHeight * 1.0f / height);
            }
            currentScale = scale;
            MIN_SCALE = scale;
            mMatrix.postTranslate((mWidth - width) / 2.0f, (mHeight - height) / 2.0f);

            mMatrix.postScale(currentScale, currentScale, mWidth / 2.0f, mHeight / 2.0f);
            mMatrix.getValues(mMatrixValues);
            mMatrix.getValues(initMatrixValues);
            setImageMatrix(mMatrix);
        }

    }
}
