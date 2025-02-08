package com.example.locationtrackerapplication.view

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.locationtrackerapplication.R
import com.example.locationtrackerapplication.databinding.ActivityNewsItemBinding
import com.example.locationtrackerapplication.databinding.MainActivityDataBinding
import com.example.locationtrackerapplication.model.Article
import com.google.gson.Gson

class NewsViewActivity : AppCompatActivity() {
    lateinit var mBinding: ActivityNewsItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
       // setContentView(R.layout.activity_news_view)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_news_view)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val jsonArticle = intent.getStringExtra("article_json")
        if (jsonArticle != null) {
            val article: Article = Gson().fromJson(jsonArticle, Article::class.java)
            Log.d("SecondActivity", "Received Article: $article")
            mBinding.tvnewsTitle2.text=article.title
            mBinding.tvnewsTitle3.text=article.content

            mBinding.imgbackicon.setOnClickListener { onBackPressed() }

            Glide.with(this!!).load(article.urlToImage)
                .apply(
                    RequestOptions.circleCropTransform()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        //  .placeholder(R.drawable.ic_user_placeholder)
                        .skipMemoryCache(true)
                )
                .into(mBinding.imgnews)

        }


    }
}