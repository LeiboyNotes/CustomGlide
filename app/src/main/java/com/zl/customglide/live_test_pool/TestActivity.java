package com.zl.customglide.live_test_pool;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.zl.customglide.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestActivity extends AppCompatActivity implements Runnable {
    private final String path = "https://cn.bing.com/sa/simg/hpb/LaDigue_EN-CA1115245085_1920x1080.jpg";
    private ImageView imageView;
    private BitmapPool bitmapPool = new BitmapPoolImpl(1024 * 1024 * 6);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        imageView = findViewById(R.id.image);
    }

    public void testAction(View view) {
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(5000);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                //BitmapFactory.Options options = new BitmapFactory.Options();
                //需要拿到图片的宽和高才能到复用池里寻找，
                //options.inJustDecodeBounds = true;


                //执行此代码后  outWidth , outHeight才有值
//                BitmapFactory.decodeStream(inputStream,null,options);
//                int width = options.outWidth;
//                int height = options.outHeight;
                int width = 1920;
                int height = 1080;
                //开始到复用池里拿
                Bitmap bitmapPoolResult = bitmapPool.get(width, height, Bitmap.Config.RGB_565);
                //开始复用
                BitmapFactory.Options options = new BitmapFactory.Options();
                //只接受可被复用的bitmap内存
                //证明是否可以复用（不开辟内存空间   不会内存抖动  内存碎片的问题）
                options.inBitmap = bitmapPoolResult;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inJustDecodeBounds = false;//默认是false   拿到所有的信息
                options.inMutable = true;//必须为true   才有复用资格
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream,null,options);
                //添加到复用池
                bitmapPool.put(bitmap);

                //显示图片
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
