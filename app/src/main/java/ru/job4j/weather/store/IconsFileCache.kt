package ru.job4j.weather.store

import android.content.Context
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso


class IconsFileCache (val mContext: Context) {
    fun uploadIcon(icon: String, view: ShapeableImageView) {
        val url = "https://openweathermap.org/img/wn/$icon@2x.png"
        Picasso.with(mContext).load(url).networkPolicy(NetworkPolicy.OFFLINE).into(view, object : Callback {
            override fun onSuccess() {}
            override fun onError() {
                Picasso.with(mContext).load(url).into(view)
            }
        })
    }
}