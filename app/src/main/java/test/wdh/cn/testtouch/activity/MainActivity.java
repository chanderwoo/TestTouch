package test.wdh.cn.testtouch.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import test.wdh.cn.testtouch.R;

/**
 * Created by wdh on 2016/8/24.
 * 主页面
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.TYPE_STATUS_BAR,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.aty_main);
        findViewById(R.id.gestureIV).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
    }
}
