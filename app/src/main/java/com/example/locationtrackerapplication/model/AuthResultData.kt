package com.example.locationtrackerapplication.model


/**
 * @author Prem
 */
data class AuthResultData(
    val state: Boolean? = null,
    val status: String? = null,
    var  baseResponse: NewsResponse?=null,
    val response: Any? = null,
    val message: String? = null,

    )