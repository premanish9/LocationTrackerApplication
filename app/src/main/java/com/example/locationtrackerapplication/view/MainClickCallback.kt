package com.example.locationtrackerapplication.view

import android.app.Activity
import com.example.locationtrackerapplication.model.Article


/**
 * @author Prem
 */
interface MainClickCallback {

    fun onPaymentSelected(type: Article, activity: Activity)


}