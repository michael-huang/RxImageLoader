package com.example.rxdemo.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.rxdemo.libcore.io.DiskCacheUtil;
import com.example.rxdemo.libcore.io.DiskLruCache;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

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
    public void putDataIntoCache(Image image) {

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
}
