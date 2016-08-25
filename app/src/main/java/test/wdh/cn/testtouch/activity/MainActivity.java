package test.wdh.cn.testtouch.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import test.wdh.cn.testtouch.R;
import test.wdh.cn.testtouch.customizedview.GestureImageView;

/**
 * Created by wdh on 2016/8/24.
 * 主页面
 */
public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private MyHandler myHandler;
    private static final String IMAGE_URL = "http://b.hiphotos.baidu.com/album/pic/item/caef76094b36acafe72d0e667cd98d1000e99c5f.jpg?psign=e72d0e667cd98d1001e93901213fb80e7aec54e737d1b867";
    private GestureImageView mGestureIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.TYPE_STATUS_BAR,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.aty_main);
        mGestureIV = (GestureImageView) findViewById(R.id.gestureIV);
        mGestureIV.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        findViewById(R.id.tvBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        myHandler = new MyHandler(this);
//        new CustomizedThread(this).start();
    }


    private void swapImageViewResource() {
        mGestureIV.setImageResource(R.drawable.a);
    }

    private static class MyHandler extends Handler {
        private WeakReference<MainActivity> reference;

        public MyHandler(MainActivity aty) {
            reference = new WeakReference<MainActivity>(aty);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity aty = reference.get();
            if (aty != null) {
                switch (msg.what) {
                    case 1:
                        aty.swapImageViewResource();
                        break;
                    case 2:
                        aty.mGestureIV.setImageBitmap((Bitmap) msg.obj);
                        break;
                }
            }
        }
    }

    private static class CustomizedThread extends Thread {
        private WeakReference<MainActivity> reference;

        public CustomizedThread(MainActivity aty) {
            reference = new WeakReference<MainActivity>(aty);
        }

        @Override
        public void run() {
            super.run();
//            SystemClock.sleep(2000);
//            MainActivity aty = reference.get();
//            if (aty != null) {
//                aty.myHandler.sendEmptyMessage(1);
//            }
            MainActivity aty = reference.get();
            if (aty != null) {
                File file = new File(aty.getFilesDir(), File.separator + "imageCache" + File.separator + "cache.png");
                Log.i(TAG, file.getAbsolutePath());
                try {
                    URL url = new URL(IMAGE_URL);
                    HttpURLConnection hc = (HttpURLConnection) url.openConnection();
                    hc.connect();
                    if (hc.getResponseCode() == 200) {
                        InputStream fis = hc.getInputStream();
                        file.getParentFile().mkdir();
                        file.createNewFile();
                        FileOutputStream fos = new FileOutputStream(file);
                        byte[] buff = new byte[1024];
                        int len = 0;
                        while ((len = fis.read(buff)) != -1) {
                            fos.write(buff, 0, len);
                        }
                        fis.close();
                        fos.close();
                        hc.disconnect();
                        if (aty != null) {
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            Message message = Message.obtain();
                            message.what = 2;
                            message.obj = bitmap;
                            aty.myHandler.sendMessage(message);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG, "图片下载线程出现问题 " + e.toString());
                }

            }
        }
    }

}
