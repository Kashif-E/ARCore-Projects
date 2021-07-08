package com.infinity.modelviewerapp.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

@BindingAdapter("imageUrl")
fun categoryImage(view : ImageView, url : String)
{
    Glide.with(view).load(url).into(view)
    Glide.with(view)
            .load(url)
            .apply(RequestOptions.bitmapTransform( RoundedCorners(15)))
            .into(view)
}
@BindingAdapter("modelImageUrl")
fun modelImage(view : ImageView, url : String)
{
    Glide.with(view).load(url).into(view)
    Glide.with(view)
            .load(url)
            .apply(RequestOptions.bitmapTransform( RoundedCorners(10)))
            .into(view)
}