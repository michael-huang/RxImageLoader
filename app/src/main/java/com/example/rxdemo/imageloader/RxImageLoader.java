package com.example.rxdemo.imageloader;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by huangyanzhen on 2016/12/2.
 */

public class RxImageLoader {

    private static RxImageLoader singleton;
    private String mUrl;

    private RxImageLoader() {
    }

    public static RxImageLoader with(Context context) {
        if (singleton == null) {
            synchronized (RxImageLoader.class) {
                if (singleton == null) {
                    singleton = new Builder(context).build();
                }
            }
        }

        return singleton;
    }

    public static class Builder {
        private final Context context;

        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }
            this.context = context.getApplicationContext();
        }

        public RxImageLoader build() {
            return new RxImageLoader();
        }
    }

    public RxImageLoader load(String url) {
        mUrl = url;
        return singleton;
    }

    public void into(ImageView imageView) {

    }
}
