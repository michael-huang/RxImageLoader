package com.example.rxdemo.imageloader;

import android.content.Context;
import android.widget.ImageView;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;


/**
 * Created by huangyanzhen on 2016/12/2.
 */

public class RxImageLoader {

    private static RxImageLoader singleton;
    private String mUrl;
    private RequestCreator mRequestCreator;

    private RxImageLoader() {
        mRequestCreator = new RequestCreator();
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

    public void into(final ImageView imageView) {
        Observable.concat(mRequestCreator.getImageFromMemory(mUrl),
                mRequestCreator.getImageFromDisk(mUrl),
                mRequestCreator.getImageFromNetwork(mUrl))
                .filter(new Predicate<Image>() {
                    @Override
                    public boolean test(Image image) throws Exception {
                        return image != null;
                    }
                }).firstElement()
                .subscribe(new Consumer<Image>() {
            @Override
            public void accept(Image image) throws Exception {
                imageView.setImageBitmap(image.getBitmap());
            }
        });
    }
}
