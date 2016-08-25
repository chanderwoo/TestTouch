package test.wdh.cn.testtouch.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import test.wdh.cn.testtouch.R;
import test.wdh.cn.testtouch.customizedview.GestureImageView;

/**
 * Created by wdh on 2016/8/25.
 * 测试ImageView事件分发的activity
 */
public class SecondActivity extends Activity {


    private ViewPager mViewPager;
    private static final int[] IMAGES = {R.drawable.a,
            R.drawable.b_meitu_1, R.drawable.c_meitu_2,
            R.drawable.d_meitu_3, R.drawable.b_meitu_1,
            R.drawable.a, R.drawable.c_meitu_2};
    private static final String MATCH_STR = "%s/%s";
    private TextView tvPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.TYPE_STATUS_BAR,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.aty_second);

        initView();
    }


    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        findViewById(R.id.tvBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvPage = (TextView) findViewById(R.id.tvPager);
        final MyPagerAdapter adapter = new MyPagerAdapter(this, IMAGES);
        mViewPager.setAdapter(adapter);
        tvPage.setText(String.format(MATCH_STR, 1 + "", adapter.getCount() + ""));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvPage.setText(String.format(MATCH_STR, position + 1 + "", adapter.getCount() + ""));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private static class MyPagerAdapter extends PagerAdapter {

        private int[] mImages;
        private Context mContext;
        private Map<Integer, ImageView> map;

        private MyPagerAdapter(Context context, int[] images) {
            this.mImages = images;
            this.mContext = context;
            map = new HashMap<>();
        }

        @Override
        public int getCount() {
            return mImages.length;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(map.get(position));
            map.remove(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (map.get(position) == null) {
                GestureImageView iv = new GestureImageView(mContext);
                iv.setImageResource(mImages[position % mImages.length]);
                container.addView(iv);
                map.put(position, iv);
            }
            return map.get(position);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}
