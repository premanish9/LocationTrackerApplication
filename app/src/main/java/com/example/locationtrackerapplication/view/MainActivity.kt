package com.example.locationtrackerapplication.view


import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.locationtrackerapplication.R
import com.example.locationtrackerapplication.databinding.MainActivityDataBinding
import com.example.locationtrackerapplication.datasource.MainActivityViewModelFactory
import com.example.locationtrackerapplication.model.Article
import com.google.gson.Gson
import android.content.Intent
import android.view.View
import com.example.locationtrackerapplication.database.ArticleEntity
import com.example.locationtrackerapplication.model.Source

class MainActivity : AppCompatActivity() {

    lateinit var mActivityViewModel: MainActivityViewModel
    lateinit var mBinding:MainActivityDataBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
       // setContentView(R.layout.activity_main)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


       // mBinding.progress.show()


        mActivityViewModel = ViewModelProvider(this, MainActivityViewModelFactory())
            .get(MainActivityViewModel::class.java)

        mActivityViewModel.apiNewsResultData.observe(this,
            Observer {
                val dataChange = it ?: return@Observer


                if (dataChange.baseResponse==null) {
                    // Fetch articles from Room
                    mActivityViewModel.fetchArticles { savedArticles ->
                        savedArticles.forEach {
                            Log.d("RoomDB", "Title: ${it.title}, Source: ${it.sourceName}")
                        }

                        newsListFetch(savedArticles.toDomainList())
                    }
                }else {
                    mActivityViewModel.saveArticles(dataChange.baseResponse!!.articles.toEntityList())
                    newsListFetch(dataChange.baseResponse!!.articles)
                }
            })


        mActivityViewModel.apicallNews("")





    }


    fun List<Article>.toEntityList(): List<ArticleEntity> {
        return this.map {
            ArticleEntity(
                sourceName = it.source.name,
                author = it.author,
                title = it.title,
                description = it.description,
                url = it.url,
                urlToImage = it.urlToImage,
                publishedAt = it.publishedAt,
                content = it.content
            )
        }
    }

    fun List<ArticleEntity>.toDomainList(): List<Article> {
        return this.map {
            Article(
                source = Source(id = null, name = it.sourceName),
                author = it.author,
                title = it.title,
                description = it.description,
                url = it.url,
                urlToImage = it.urlToImage,
                publishedAt = it.publishedAt,
                content = it.content
            )
        }
    }



    private fun newsListFetch(baseResponse: List<Article>) {
        Log.d("MainActivity", "newsListFetch: "+baseResponse)
        // Save articles in Room

        val mAdapter = NewListAdapter(this, mActivityViewModel,ArrayList(baseResponse!!),
            object : MainClickCallback {
                override fun onPaymentSelected(type: Article, activity: Activity) {

                    val jsonArticle = Gson().toJson(type)

// Start Next Activity
                    val intent = Intent(activity, NewsViewActivity::class.java)
                    intent.putExtra("article_json", jsonArticle)
                    startActivity(intent)


                }




            })
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

       runOnUiThread {
           mBinding.recyclerview.layoutManager = layoutManager
           mBinding.progress.visibility=View.GONE
           mBinding.recyclerview.adapter = mAdapter
        }


    }



}