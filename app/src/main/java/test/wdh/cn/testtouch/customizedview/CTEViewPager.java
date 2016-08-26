package test.wdh.cn.testtouch.customizedview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by wdh on 2016/8/26.
 * 在ViewPager里面使用多点触碰的ImageView会有报错，自定义这个类解决问题
 * 参考http://blog.csdn.net/nnmmbb/article/details/28419779
 */
public class CTEViewPager extends ViewPager {
    public CTEViewPager(Context context) {
        super(context);
    }

    public CTEViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
