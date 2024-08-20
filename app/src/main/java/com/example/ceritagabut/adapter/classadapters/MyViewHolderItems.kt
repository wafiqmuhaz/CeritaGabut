package com.example.ceritagabut.adapter.classadapters

import android.app.Activity
import android.content.Intent
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ceritagabut.databinding.ItemsBinding
import com.example.ceritagabut.responses.ListResultItems
import com.example.ceritagabut.ui.DetailAppActivity
import com.example.ceritagabut.utils.withDateFormat

class MyViewHolderItems(private val viewBinding: ItemsBinding) :
    RecyclerView.ViewHolder(viewBinding.root) {

    private val storyImg: ImageView = viewBinding.ivAppItemPhotoApp
    private val avatarApp = viewBinding.avatarApp
    private val tvItemCreated = viewBinding.tvItemCreated
    private val tvAppItemDescriptionApp = viewBinding.tvAppItemDescriptionApp
    private val lat = viewBinding.lat
    private val lon = viewBinding.lon

    fun bind(items: ListResultItems) {
        Glide.with(itemView.context)
            .load(items.userPhotoUrl)
            .into(storyImg)

        viewBinding.tvAppItemNameApp.text = items.username
        viewBinding.tvItemCreated.text = items.userCreatedAt.withDateFormat()
        viewBinding.tvAppItemDescriptionApp.text = items.itemDesc

        itemView.setOnClickListener {
            val intent = Intent(itemView.context, DetailAppActivity::class.java)
            intent.putExtra(DetailAppActivity.APP_NAME, items.username)
            intent.putExtra(DetailAppActivity.APP_CREATE_AT, items.userCreatedAt)
            intent.putExtra(DetailAppActivity.APP_DESCRIPTION, items.itemDesc)
            intent.putExtra(DetailAppActivity.APP_PHOTO_URL, items.userPhotoUrl)
            intent.putExtra(DetailAppActivity.APP_LATITUDE, items.latitude.toString())
            intent.putExtra(DetailAppActivity.APP_LONGITUDE, items.longitude.toString())

            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                itemView.context as Activity,
                Pair(storyImg, "photoUrl"),
                Pair(avatarApp, "photo"),
                Pair(tvItemCreated, "createdAt"),
                Pair(tvAppItemDescriptionApp, "description"),
                Pair(lat, "lat"),
                Pair(lon, "lon"),
            )
            itemView.context.startActivity(intent, optionsCompat.toBundle())
        }
    }
}