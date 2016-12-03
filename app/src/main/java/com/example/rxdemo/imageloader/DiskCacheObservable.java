package com.example.rxdemo.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.rxdemo.libcore.io.DiskCacheUtil;
import com.example.rxdemo.libcore.io.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by huangyanzhen on 2016/12/3.
 */

public class DiskCacheObservable extends CacheObservable {

    private DiskLruCache mDiskLruCache;
    private Context mContext;
    private int maxSize = 20*1024*1024;

    public DiskCacheObservable(Context mContext) {
        this.mContext = mContext;
        initDiskLruCache();
    }

    @Override
    public Image getDataFromCache(String url) {
        Bitmap bitmap = getDataFromDiskLruCache(url);
        if (bitmap != null) {
            return new Image(url, bitmap);
        }
        return null;
    }

    @Override
    public void putDataIntoCache(final Image image) {
        Observable.create(new ObservableOnSubscribe<Image>() {
            @Override
            public void subscribe(ObservableEmitter<Image> e) throws Exception {
                putDataToDiskLruCache(image);
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private void initDiskLruCache() {
        try {
            File cacheDir = DiskCacheUtil.getDiskCacheDir(mContext, "image_cache");
            if (!cacheDir.exists()) {
                cacheDir.mkdir();
            }
            int versionCode = DiskCacheUtil.getAppVersionCode(mContext);
            mDiskLruCache = DiskLruCache.open(cacheDir, versionCode, 1, maxSize);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getDataFromDiskLruCache(String url) {
        FileDescriptor fileDescriptor = null;
        FileInputStream fileInputStream = null;
        DiskLruCache.Snapshot snapshot = null;
        try {
            // generate corresponding key from url
            final String key = DiskCacheUtil.getMd5String(url);
            // get corresponding cache from key
            snapshot = mDiskLruCache.get(key);

            if (fileDescriptor != null) {
                fileInputStream = (FileInputStream) snapshot.getInputStream(0);
                fileDescriptor = fileInputStream.getFD();
            }
            // parse cache data to Bitmap
            Bitmap bitmap = null;
            if (fileDescriptor != null) {
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            }
            return bitmap;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileDescriptor == null && fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void putDataToDiskLruCache(Image image) {

        try {
            // generate corresponding key from url
            String key = DiskCacheUtil.getMd5String(image.getUrl());
            // get Editor
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);
            if (editor != null) {
                // get Outputstream from editor
                OutputStream outputStream = editor.newOutputStream(0);
                // download image and save to DiskLruCache
                boolean isSuccessful = download(image.getUrl(), outputStream);

                if (isSuccessful) {
                    editor.commit();
                } else {
                    editor.abort();
                }
                mDiskLruCache.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean download(String urlString, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream bufferedOutputStream = null;
        BufferedInputStream bufferedInputStream = null;

        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            bufferedOutputStream = new BufferedOutputStream(outputStream, 8 * 1024);
            int b;
            while ((b = bufferedInputStream.read()) != -1) {
                bufferedOutputStream.write(b);
            }
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
