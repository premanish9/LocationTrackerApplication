package com.example.locationtrackerapplication.view


import android.util.ArrayMap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.locationtrackerapplication.database.ArticleEntity
import com.example.locationtrackerapplication.database.NewsDatabase
import com.example.locationtrackerapplication.database.NewsRepository
import com.example.locationtrackerapplication.datasource.AuthDataSource
import com.example.locationtrackerapplication.model.AuthResultData
import com.example.locationtrackerapplication.model.NewsResponse
import com.example.locationtrackerapplication.network.*
import com.example.locationtrackerapplication.utils.NewsApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityViewModel(private val dataSource: AuthDataSource) : ViewModel() {


    private val _apiNewsResultData= MutableLiveData<AuthResultData>()
    val apiNewsResultData: LiveData<AuthResultData> = _apiNewsResultData


    private val repository: NewsRepository

    init {
        val articleDao = NewsDatabase.getDatabase(NewsApplication.getInstance()).articleDao()
        repository = NewsRepository(articleDao)
    }

    fun saveArticles(articles: List<ArticleEntity>) {
        GlobalScope.launch {
            repository.insertArticles(articles)
        }
    }

    fun fetchArticles(onResult: (List<ArticleEntity>) -> Unit) {
        GlobalScope.launch {
            val articles = repository.getAllArticles()
            onResult(articles)
        }
    }

    fun apicallNews(request: String) {
        GlobalScope.launch(Dispatchers.Main) {
           // dataSource.getNews(request, responseHandlerRiderRating())

            val call = RetrofitClient.getInstance().myApi.getnews("Apple","2025-02-06","2025-02-06","d13cf6c214994189ae061e5bf06bec47")
            call!!.enqueue(object : Callback<NewsResponse?> {
                override fun onResponse(
                    call: Call<NewsResponse?>,
                    response: Response<NewsResponse?>
                ) {
                    Log.d("mainactivity", "onResponse: " + response.body())

                    _apiNewsResultData.value = AuthResultData(state = true, baseResponse = response.body(), message = "success")

                }

                override fun onFailure(call: Call<NewsResponse?>, t: Throwable) {
                    _apiNewsResultData.value = AuthResultData(state = false, message = t.message)

                }
            })


        }
    }




}