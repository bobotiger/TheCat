package com.example.thecat

import android.widget.ImageView

interface ImageLoader {
    fun loadImage(imageUrl:String, imageView:ImageView)
}