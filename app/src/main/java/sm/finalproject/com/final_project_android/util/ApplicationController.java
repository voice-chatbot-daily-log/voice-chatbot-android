package sm.finalproject.com.final_project_android.util;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sm.finalproject.com.final_project_android.networkService.NetworkService;

public class ApplicationController extends Application {
    private NetworkService networkService;
    public static Context appContext = null;
    private static ApplicationController instance;
    //test용 url
    private static String baseUrl = "https://dialogflow.googleapis.com/v2/projects/dflow-88b76/agent/sessions/90fad3c2-eba6-7e7b-4062-8bb77c629132:detectIntent/";

    public static ApplicationController getInstance() {
        return instance;
    }
    public NetworkService getNetworkService() {
        return networkService;
    }
    public void makeToast(String message){
        Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        instance = this;

        buildNetwork();
    }
    public void buildNetwork() {
        Retrofit.Builder builder = new Retrofit.Builder();
        Retrofit retrofit = builder
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        networkService = retrofit.create(NetworkService.class);

    }
}
