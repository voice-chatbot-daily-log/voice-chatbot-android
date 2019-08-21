package sm.finalproject.com.final_project_android.start.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sm.finalproject.com.final_project_android.MainActivity;
import sm.finalproject.com.final_project_android.R;
import sm.finalproject.com.final_project_android.model.PostLastDiaryResponse;
import sm.finalproject.com.final_project_android.model.PostLastDiaryResponseData;
import sm.finalproject.com.final_project_android.networkService.NetworkService;
import sm.finalproject.com.final_project_android.start.VoiceChatActivty;

public class SaveDialog extends Dialog {

    //통신//
    Retrofit.Builder builder;
    NetworkService networkService;
    Retrofit saveDiaryNetwork;
    //

    EditText et_hashTag;
    public SaveDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //다이얼로그의 타이틀바를 없애주는 옵션입니다.
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  //다이얼로그의 배경을 투명으로 만듭니다.
        setContentView(R.layout.dialog_save);     //다이얼로그에서 사용할 레이아웃입니다.
        //수정's server 통신설정//
        builder = new Retrofit.Builder();

        saveDiaryNetwork = builder
                .baseUrl("http://13.209.245.84:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //////

        et_hashTag = findViewById(R.id.et_hashtag);
    }
    public void setEditText(String hashtag){
        et_hashTag.setText(hashtag);
    }

    public void postSaveLastDiary(int userIdx, final String content, String hashTag){

        //날짜구하기
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        String realDate = dateFormat.format(date);


        PostLastDiaryResponseData postLastDiaryResponseData = new PostLastDiaryResponseData(userIdx,content,realDate,hashTag);

        networkService = saveDiaryNetwork.create(NetworkService.class);

        final Call<PostLastDiaryResponse> postLastDiaryResponseCall = networkService.postLastDiary(postLastDiaryResponseData);
        postLastDiaryResponseCall.enqueue(new Callback<PostLastDiaryResponse>() {
            @Override
            public void onResponse(Call<PostLastDiaryResponse> call, Response<PostLastDiaryResponse> response) {
                if(response.isSuccessful()){

                    Toast.makeText(getContext(), "!!저장성공!!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    getContext().startActivity(intent);


                }else{

                }
            }

            @Override
            public void onFailure(Call<PostLastDiaryResponse> call, Throwable t) {

            }
        });



    }


}
