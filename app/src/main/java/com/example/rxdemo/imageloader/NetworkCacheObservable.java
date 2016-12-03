package com.example.rxdemo.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by huangyanzhen on 2016/12/3.
 */

public class NetworkCacheObservable extends CacheObservable {
    @Override
    public Image getDataFromCache(String url) {

        Bitmap bitmap = downloadImage(url);
        if (bitmap != null) {
            return new Image(url, bitmap);
        }
        return null;
    }

    @Override
    public void putDataIntoCache(Image image) {

    }

    private Bitmap downloadImage(String url) {

        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            final URLConnection connection = new URL(url).openConnection();
            inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }
}
