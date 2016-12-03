package com.example.rxdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.rxdemo.imageloader.RxImageLoader;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.image_view);
    }

    public void onClick(View view) {
        RxImageLoader.with(this)
                .load("https://s3.amazonaws.com/media-p.slid.es/uploads/263775/images/1763829/687474703a2f2f692e696d6775722e636f6d2f4149696d5138432e6a7067.jpeg")
                .into(mImageView);
    }
}
