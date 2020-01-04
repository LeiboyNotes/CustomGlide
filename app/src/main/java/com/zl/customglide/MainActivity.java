package com.zl.customglide;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.zl.customglide.fragment.LifecycleCallback;

public class MainActivity extends Activity {

    private final String path = "http://mmbiz.qpic.cn/mmbiz/PwIlO51l7wuFyoFwAXfqPNETWCibjNACIt6ydN7vw8LeIwT7IjyG3eeribmK4rhibecvNKiaT2qeJRIWXLuKYPiaqtQ/0";

    private ImageView imageView1,imageView2,imageView3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView1 = findViewById(R.id.image1);
        imageView2 = findViewById(R.id.image2);
        imageView3 = findViewById(R.id.image3);
    }

    public void t1(View view) {
        Glide.width(this).load(path).into(imageView1);
    }

    public void t2(View view) {
        Glide.width(this).load(path).into(imageView1);
    }
    public void t3(View view) {
        Glide.width(this).load(path).into(imageView1);
    }
}
