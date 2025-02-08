package com.example.locationtrackerapplication.network

/**
 * @author Prem
 */
class NetworkConstants {

    object URL {
        const val API_BASE_PATH = "http://www.google.co.in/"
        const val url="https://newsapi.org/v2/"

    }

    object Status {
        const val FAILED = "failed"
        const val SUCCESS = "ok"
    }

    object Headers {
        const val PLATFORM: String = "platform"
        const val ANDROID: String = "android"
        const val OS_VERSION = "os_version"
        const val APP_VERSION = "app_version"
        const val DEVICE_BRAND = "device_brand"
        const val DEVICE_MODEL = "device_model"
        const val DEVICE_ID = "device_id"
        const val IP_ADDRESS = "ip_address"
    }

}