package test.wdh.cn.testtouch.customizedview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import test.wdh.cn.testtouch.R;
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
    private final float MAX_SCALE = 2.0f;
    private float MIN_SCALE;
    private float[] mMatrixValues = new float[9];
    private float[] initMatrixValues = new float[9];
    private Matrix mMatrix = new Matrix();
    private PointF scalePointF;
    private PointF dragPointF;
    private float preDistance = 0;
    private static final int SCALE_RATE = 6;//修改此值用以修改缩放速率

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
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        adjustImageMatrix();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        adjustImageMatrix();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                scalePointF = null;
                preDistance = 0;
                dragPointF = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 2) { //两点触摸事件处理
                    handleScaleEvent(event);
                }
                if (event.getPointerCount() == 1) { //处理移动事件
                    handleDragEvent(event);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() != 1) {
                    scalePointF = null;
                    preDistance = 0;
                    dragPointF = null;
                }
                break;
            case MotionEvent.ACTION_UP:
                dragPointF = null;
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 处理拖拽手势
     *
     * @param event
     */
    private void handleDragEvent(MotionEvent event) {
        scalePointF = null;
        if (dragPointF == null) {
            dragPointF = new PointF(event.getX(), event.getY());
        } else {
            float x = event.getX();
            float y = event.getY();

            drag(x - dragPointF.x,
                    y - dragPointF.y);

            dragPointF = new PointF(x, y);
        }
    }

    /**
     * 处理缩放手势
     *
     * @param event 事件
     */
    private void handleScaleEvent(MotionEvent event) {
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
            float scale = (distance - preDistance) / mDistance * SCALE_RATE;//修改这个值可以更改缩放速率
            float judge = currentScale + scale;
            scale = judge > MAX_SCALE || judge < MIN_SCALE ?
                    (judge > MAX_SCALE ? MAX_SCALE - currentScale : MIN_SCALE - currentScale) :
                    scale;
            currentScale += scale;
            scale(currentScale / (currentScale - scale), scalePointF);
        }
        preDistance = distance;
    }

    /**
     * 根据图像矩形处理拖拽的X值
     *
     * @param rect  图像矩形
     * @param dragX 拖拽的X值
     * @return 处理以后dragX值
     */
    private float handleDragX(RectF rect, float dragX) {
        if (rect.left < 0 || rect.right > mWidth) { // 放大时，如果内容宽度大于屏幕，左右边界不能出现黑边
            if (rect.left + dragX > 0) {
                dragX = 0 - rect.left;
            } else if (rect.right + dragX < mWidth) {
                dragX = mWidth - rect.right;
            }
        } else {
            dragX = 0;
        }
        return dragX;
    }

    /**
     * 根据图像矩形处理拖拽的Y值
     *
     * @param rect  图像矩形
     * @param dragY 拖拽的Y值
     * @return 处理以后dragY值
     */
    private float handleDragY(RectF rect, float dragY) {
        if (rect.top <= 0 && rect.bottom >= mHeight) { // 放大时，如果内容宽度大于屏幕，左右边界不能出现黑边
            if (rect.top + dragY > 0) {
                dragY = 0 - rect.top;
            } else if (rect.bottom + dragY < mHeight) {
                dragY = mHeight - rect.bottom;
            }
        } else {
            dragY = 0;
        }
        return dragY;
    }

    /**
     * 拖拽imageView
     *
     * @param dragX X轴的移动距离
     * @param dragY Y轴的移动距离
     */
    private void drag(float dragX, float dragY) {
        RectF rect = getImageRectF();
        dragX = handleDragX(rect, dragX);
        dragY = handleDragY(rect, dragY);
        mMatrix.postTranslate(dragX, dragY);
        setImageMatrix(mMatrix);
        mMatrix.getValues(mMatrixValues);
        getImageRectF();
    }

    /**
     * 缩放时，处理边缘不过界
     *
     * @param matrix 拷贝的矩阵
     * @param scale  缩放比例
     * @param f      缩放中心点
     * @return 返回X坐标的点
     */
    private float handleScaleDragX(Matrix matrix, float scale, PointF f) {
        matrix.postScale(scale, scale, f.x, mHeight / 2.0f);
        RectF rect = getImageRectF(matrix); // 根据拷贝过后的矩阵计算得出的矩形
        float x=f.x;
        if (rect.left > 0) {// 如果计算后的左端超过边界
            x=0;
        } else if (rect.right < mWidth) {
            x=mWidth;
        }
        return x;
    }

    /**
     * 按照缩放比例和点进行缩放
     *
     * @param scale 缩放比例
     * @param f     包含缩放中心点的PointF
     */
    private void scale(float scale, PointF f) {
        Matrix matrix = new Matrix(mMatrix);
        mMatrix.postScale(scale, scale, handleScaleDragX(matrix,scale,f), mHeight / 2.0f);
        mMatrix.getValues(mMatrixValues);
        getImageRectF();
        if (currentScale == MIN_SCALE) {// 缩放到初始位置，将图片设置为初始位置,此处可以添加动画，请随意发挥。
            mMatrix.setValues(initMatrixValues);
            setImageMatrix(mMatrix);
        }
        setImageMatrix(mMatrix);
    }


    /**
     * 将图像调整到适合的位置
     */
    private void adjustImageMatrix() {
        Drawable d = getDrawable();
        if (d == null) {
            setImageResource(R.drawable.ic_launcher);
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
        mMatrix = new Matrix();
        mMatrix.postTranslate((mWidth - width) / 2.0f, (mHeight - height) / 2.0f);
        mMatrix.postScale(currentScale, currentScale, mWidth / 2.0f, mHeight / 2.0f);
        mMatrix.getValues(mMatrixValues);
        mMatrix.getValues(initMatrixValues);
        setImageMatrix(mMatrix);
        getImageRectF();

    }

    /**
     * 返回Drawable的矩形
     *
     * @param matrix 拷贝出来的矩阵
     * @return imageView矩形数据
     */
    private RectF getImageRectF(Matrix matrix) {
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (d != null) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    /**
     * 返回Drawable的矩形
     *
     * @return imageView矩形数据
     */
    private RectF getImageRectF() {
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (d != null) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            mMatrix.mapRect(rect);
        }
        return rect;
    }

    @Override
    public void onGlobalLayout() {
        if (isOnce) {
            isOnce = false;
            adjustImageMatrix();
        }
    }
}
