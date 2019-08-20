package sm.finalproject.com.final_project_android.lastdiary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sm.finalproject.com.final_project_android.R;
import sm.finalproject.com.final_project_android.lastdiary.adapter.LastDiaryAdapter;
import sm.finalproject.com.final_project_android.lastdiary.data.LastDiaryData;
import sm.finalproject.com.final_project_android.model.GetLastDiaryResponse;
import sm.finalproject.com.final_project_android.networkService.NetworkService;

public class LastDiaryActivty extends AppCompatActivity {

    //리사이클러뷰
    RecyclerView last_diary_rcv;
    public RecyclerView.LayoutManager mLayoutManager_lastDiary;
    public ArrayList<LastDiaryData> lastDiaryData;
    public LastDiaryAdapter lastDiaryAdapter;
    //

    //통신//
    Retrofit.Builder builder;
    NetworkService networkService;
    Retrofit lastDiaryNetwork;
    //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_diary_activty);

        //통신//
        builder = new Retrofit.Builder();

        lastDiaryNetwork = builder
                .baseUrl("http://13.209.245.84:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        networkService = lastDiaryNetwork.create(NetworkService.class);
        //


        last_diary_rcv = findViewById(R.id.last_diary_rcv);
        mLayoutManager_lastDiary = new LinearLayoutManager(this.getApplicationContext());
        last_diary_rcv.setHasFixedSize(true);
        last_diary_rcv.setLayoutManager(mLayoutManager_lastDiary);

        lastDiaryData = new ArrayList<>();
        getLastDiary();


    }

    public void getLastDiary(){
        final Call<GetLastDiaryResponse> getLastDiaryResponseCall = networkService.getLastDiary(1);
        getLastDiaryResponseCall.enqueue(new Callback<GetLastDiaryResponse>() {
            @Override
            public void onResponse(Call<GetLastDiaryResponse> call, Response<GetLastDiaryResponse> response) {
                if(response.isSuccessful()){

                    lastDiaryData = response.body().data;
                    lastDiaryAdapter = new LastDiaryAdapter(lastDiaryData);

                    last_diary_rcv.setAdapter(lastDiaryAdapter);

                }
                else{


                }

            }

            @Override
            public void onFailure(Call<GetLastDiaryResponse> call, Throwable t) {

            }
        });
    }



}
