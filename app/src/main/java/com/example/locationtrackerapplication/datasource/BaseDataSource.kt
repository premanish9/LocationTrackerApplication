package com.example.locationtrackerapplication.datasource


/**
 * @author Prem
 */
open class BaseDataSource {


    @Suppress("SpellCheckingInspection")
    fun url(api: String, uri: String): String {


        return "https://newsapi.org/v2/"



    }

}