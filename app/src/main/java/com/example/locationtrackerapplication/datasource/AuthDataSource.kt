package com.example.locationtrackerapplication.datasource


import com.example.locationtrackerapplication.model.NewsResponse
import com.example.locationtrackerapplication.network.RetrofitClient

/**
 * @author Prem
 */
class AuthDataSource : BaseDataSource() {

    private val apiBase = "authentication"


    private val uriSMS = "sms"


  /*  fun getNews(request: String, callback: NetworkResponseListener<BaseResponse<NewsResponse>>) {
        // MetroRideApp.addApiEvent(uriSMS)

        RetrofitClient.getInstance().myApi.getnews("Apple","2025-02-06","2025-02-06","d13cf6c214994189ae061e5bf06bec47")
            .enqueue(CommonResponseHandler(callback))

        *//*ApiFactory.API_CALL.getnews("Apple","2025-02-06","2025-02-06","d13cf6c214994189ae061e5bf06bec47")
            .enqueue(CommonResponseHandler(callback))*//*
    }*/


}