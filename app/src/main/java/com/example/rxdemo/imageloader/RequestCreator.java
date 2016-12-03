package com.example.rxdemo.imageloader;

import android.content.Context;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by huangyanzhen on 2016/12/3.
 */

public class RequestCreator {

    private MemoryCacheObservable mMemoryCacheObservable;
    private DiskCacheObservable mDiskCacheObservable;
    private NetworkCacheObservable mNetworkCacheObservable;

    public RequestCreator(Context context) {
        mMemoryCacheObservable = new MemoryCacheObservable();
        mDiskCacheObservable = new DiskCacheObservable(context);
        mNetworkCacheObservable = new NetworkCacheObservable();
    }

    public Observable<Image> getImageFromMemory(String url) {
        return mMemoryCacheObservable.getImage(url);
    }

    public Observable<Image> getImageFromDisk(String url) {
        return mDiskCacheObservable.getImage(url);
    }

    public Observable<Image> getImageFromNetwork(String url) {
        return mNetworkCacheObservable.getImage(url)
                .filter(new Predicate<Image>() {
                    @Override
                    public boolean test(Image image) throws Exception {
                        return image != null;
                    }
                })
                .doOnNext(new Consumer<Image>() {
            @Override
            public void accept(Image image) throws Exception {
                mDiskCacheObservable.putDataIntoCache(image);
                mMemoryCacheObservable.putDataIntoCache(image);
            }
        });
    }
}
