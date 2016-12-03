package com.example.rxdemo.imageloader;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by huangyanzhen on 2016/12/3.
 */

public class MemoryCacheObservable extends CacheObservable {

    int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024); // KB
    int cacheSize = maxMemory /8;

    LruCache<String, Bitmap> mLruCache = new LruCache<String, Bitmap>(cacheSize) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight() / 1024;
        }
    };

    @Override
    public Image getDataFromCache(String url) {
        Bitmap bitmap = mLruCache.get(url);
        if (bitmap != null) {
            return new Image(url, bitmap);
        }
        return null;
    }

    @Override
    public void putDataIntoCache(Image image) {

    }
}
