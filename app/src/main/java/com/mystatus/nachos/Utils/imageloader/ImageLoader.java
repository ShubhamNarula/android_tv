package com.mystatus.nachos.Utils.imageloader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

public class ImageLoader {

    private RequestManager requestManager;
    private RequestBuilder<Drawable> requestBuilder;

    private ImageLoader(Context context) {
        requestManager = Glide.with(context);
    }

    public static ImageLoader with(Context context) {
        return new ImageLoader(context);
    }


    public ImageLoader load(File file) {
        requestBuilder = requestManager.load(file);
        return this;
    }

    public ImageLoader load(String url) {
        requestBuilder = requestManager.load(url);
        return this;
    }

    public ImageLoader listener(RequestListener<Drawable> requestListener) {
        requestBuilder = requestBuilder.listener(requestListener);
        return this;
    }

    public ImageLoader load(int drawable) {
        requestBuilder = requestManager.load(drawable);
        return this;
    }

    public ImageLoader resize(int width, int height) {
        return this;
    }

    public ImageLoader centerCrop() {
        return this;
    }

    public ImageLoader error(int drawable) {
        requestBuilder = requestBuilder.apply(RequestOptions.errorOf(drawable));
        return this;
    }

    public ImageLoader placeholder(int drawable) {
        requestBuilder = requestBuilder.apply(RequestOptions.placeholderOf(drawable));
        return this;
    }

    public ImageLoader blur(int radius) {
        requestBuilder = requestBuilder.apply(RequestOptions.bitmapTransform(new BlurTransformation(radius)));
        return this;
    }


    public void into(ImageView imageView) {
        requestBuilder.apply(RequestOptions.noAnimation()).dontTransform().into(imageView);
    }

}
