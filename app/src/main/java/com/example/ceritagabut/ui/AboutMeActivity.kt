package com.example.ceritagabut.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ceritagabut.R

class AboutMeActivity : AppCompatActivity() {

    private val name = "WAFIQ MUHAZ"
    private val mail = "wafiqmuhaz@gmail.com"
    private val ig = "https://www.instagram.com/az.wamuh_/"
    private val link = "https://www.linkedin.com/in/wafiqmuhaz"
    private val web = "https://wafiqmuhaz.netlify.app/"
    private val tiktok = "https://www.tiktok.com/@az.wamuh_/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_about_me)

        val myName: TextView = findViewById(R.id.tv_name)
        val myMail: TextView = findViewById(R.id.tv_mail)
        val imgMe: ImageView = findViewById(R.id.imgv_me)
        val imgIg: ImageView = findViewById(R.id.imgvIg)
        val imgWeb: ImageView = findViewById(R.id.imgvWeb)
        val imgLinkedin: ImageView = findViewById(R.id.imgvLinkedin)
        val imgTiktok: ImageView = findViewById(R.id.imgvTiktok)

        myName.text = name
        myMail.text = mail
        Glide.with(this).load(R.drawable.me).apply(RequestOptions().override(250, 250)).into(imgMe)
        Glide.with(this).load(R.drawable.instagram).into(imgIg)
        Glide.with(this).load(R.drawable.linkedin).into(imgLinkedin)
        Glide.with(this).load(R.drawable.web).into(imgWeb)
        Glide.with(this).load(R.drawable.tiktok).into(imgTiktok)

        imgIg.setOnClickListener {
            val accessWebIg = Intent(Intent.ACTION_VIEW)
            accessWebIg.data = Uri.parse(ig)
            startActivity(accessWebIg)
        }

        imgLinkedin.setOnClickListener {
            val accessWebLink = Intent(Intent.ACTION_VIEW)
            accessWebLink.data = Uri.parse(link)
            startActivity(accessWebLink)
        }

        imgWeb.setOnClickListener {
            val accessWebLink = Intent(Intent.ACTION_VIEW)
            accessWebLink.data = Uri.parse(web)
            startActivity(accessWebLink)
        }

        imgTiktok.setOnClickListener {
            val accessWebLink = Intent(Intent.ACTION_VIEW)
            accessWebLink.data = Uri.parse(tiktok)
            startActivity(accessWebLink)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}