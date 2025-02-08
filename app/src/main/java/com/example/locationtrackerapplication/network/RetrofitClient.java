package com.example.locationtrackerapplication.network;



import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = NetworkConstants.URL.url;
    private static RetrofitClient instance = null;
    private static RetrofitClient mapinstance = null;
    private static RetrofitClient otpinstance = null;
    private static Retrofit retrofit = null;
    private static ApiInterface myApi,mapapi,otpapi;

    //    OkHttpClient okHttpClient = new OkHttpClient.Builder()
//            // Add any necessary configuration to the OkHttpClient instance
//            .build();
    private RetrofitClient() {

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        // Add logging interceptor for debugging purposes
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClientBuilder.addInterceptor(loggingInterceptor);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClientBuilder.build())
                .build();
        myApi = retrofit.create(ApiInterface.class);
    }

    private RetrofitClient(String url,ApiInterface key) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        // Add logging interceptor for debugging purposes
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClientBuilder.addInterceptor(loggingInterceptor);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClientBuilder.build())
                .build();
        otpapi = retrofit.create(ApiInterface.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }


    public static synchronized RetrofitClient getmapInstance() {
        if (mapinstance == null) {
            mapinstance = new RetrofitClient("https://maps.googleapis.com/", mapapi);
        }
        return mapinstance;
    }







    public ApiInterface getMyApi() {
        return myApi;
    }

    public ApiInterface getMapapi() {
        return mapapi;
    }

    public ApiInterface getOTPapi() {
        return otpapi;
    }

}

