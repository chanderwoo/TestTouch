package test.wdh.cn.testtouch;

import android.app.Application;

import test.wdh.cn.testtouch.constant.GlobalConstant;

/**
 * Created by wdh on 2016/8/24.
 * 程序入口
 */
public class Boot extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        GlobalConstant.initDeviceInfo(this);
    }
}
