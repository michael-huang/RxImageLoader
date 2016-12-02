package com.example.rxdemo.imageloader;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by huangyanzhen on 2016/12/3.
 */

public abstract class CacheObservable {

    public Observable<Image> getImage(final String url) {
        return Observable.create(new ObservableOnSubscribe<Image>() {
            @Override
            public void subscribe(ObservableEmitter<Image> e) throws Exception {
                if (!e.isDisposed()) {
                    Image image = getDataFromCache(url);
                    e.onNext(image);
                    e.onComplete();
                }
            }
        });
    }

    public abstract Image getDataFromCache(String url);
    public abstract void putDataIntoCache(Image image);
}
