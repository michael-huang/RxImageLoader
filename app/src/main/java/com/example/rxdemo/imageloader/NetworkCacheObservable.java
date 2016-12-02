package com.example.rxdemo.imageloader;

/**
 * Created by huangyanzhen on 2016/12/3.
 */

public class NetworkCacheObservable extends CacheObservable {
    @Override
    public Image getDataFromCache(String url) {
        return null;
    }

    @Override
    public void putDataIntoCache(Image image) {

    }
}
