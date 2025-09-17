package com.example.smishingdetectionapp.utils;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.smishingdetectionapp.R;

public class ImageLoader {

    public static void load(Context context, int drawableRes, ImageView imageView) {
        Glide.with(context)
                .load(drawableRes)
               // .placeholder(R.drawable.ic_placeholder)   // optional, add a placeholder drawable
                // .error(R.drawable.ic_error)               // optional, add an error drawable
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(imageView);
    }
}
