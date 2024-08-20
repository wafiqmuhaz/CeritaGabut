@file:Suppress("UNUSED_EXPRESSION")

package com.example.ceritagabut.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ceritagabut.R
import com.example.ceritagabut.databinding.ActivityDetailAppBinding
import com.example.ceritagabut.utils.getMapAddress
import com.example.ceritagabut.utils.withDateFormat

class DetailAppActivity : AppCompatActivity() {
    private lateinit var appBinding: ActivityDetailAppBinding

    companion object {
        const val APP_NAME = "name"
        const val APP_CREATE_AT = "create_at"
        const val APP_DESCRIPTION = "description"
        const val APP_PHOTO_URL = "photoUrl"
        const val APP_LONGITUDE = "lon"
        const val APP_LATITUDE = "lat"
    }

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appBinding = ActivityDetailAppBinding.inflate(layoutInflater)
        setContentView(appBinding.root)
        appBar()
        displayUserData()
    }

    private fun appBar() {
        supportActionBar?.title = getString(R.string.item_detail_items)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun displayUserData() {
        val user_photoUrl = intent.getStringExtra(APP_PHOTO_URL)
        val user_name = intent.getStringExtra(APP_NAME)
        val user_createAt = intent.getStringExtra(APP_CREATE_AT)
        val user_description = intent.getStringExtra(APP_DESCRIPTION)
        val user_lon = intent.getStringExtra(APP_LONGITUDE)?.toDoubleOrNull() ?: 0.0
        val user_lat = intent.getStringExtra(APP_LATITUDE)?.toDoubleOrNull() ?: 0.0
        val user_location = getMapAddress(this@DetailAppActivity, user_lat, user_lon)
        Glide.with(appBinding.root.context)
            .load(user_photoUrl)
            .placeholder(R.drawable.baseline_not_interested_24)
            .error(R.drawable.baseline_error_24)
            .into(appBinding.ivDetailPhoto)
        user_name?.let {
            appBinding.tvDetailName.apply {
                text = it
                visibility =
                    if (it.isNotBlank()) View.VISIBLE else View.GONE
            }
        }
        user_createAt?.let {
            val formattedDate = it.withDateFormat()
            appBinding.tvDetailCreatedTime.apply {
                text = formattedDate
                visibility =
                    if (formattedDate.isNotBlank()) View.VISIBLE else View.GONE // Menyembunyikan TextView jika waktu kosong
            }
        }
        user_description?.let {
            appBinding.tvDetailDescription.apply {
                text = it
                visibility =
                    if (it.isNotBlank()) View.VISIBLE else View.GONE // Menyembunyikan TextView jika deskripsi kosong
            }
        }
        user_location?.let {
            appBinding.tvDetailLocation.apply {
                text = it
                visibility =
                    if (it.isNotBlank()) View.VISIBLE else View.GONE // Menyembunyikan TextView jika lokasi kosong
            }
        }
        appBinding.avatar2.visibility =
            if (user_lon == 0.0 && user_lat == 0.0) View.INVISIBLE else View.VISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}