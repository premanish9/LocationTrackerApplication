package com.example.locationtrackerapplication.utils


import android.app.Application
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.StrictMode
import android.util.Log
import java.lang.ref.WeakReference
import java.util.Locale


/**
 * This is an application class that holds the RUNNING INSTANCE of an application whenever it is
 * loaded into memory. DO NOT HOLD TOO MANY INSTANCE DATA, as it might act as static data and slower
 * the performance.
 *
 * @author Prem
 */
class NewsApplication : Application() {      //InAppCustomHTMLListener

    companion object {

        val buttonclick="Button Click"
        val pageload="page load"




        init {

        }




        private var instance = NewsApplication()

        fun getInstance(): NewsApplication {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

    }





}