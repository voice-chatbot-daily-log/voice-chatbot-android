package sm.finalproject.com.final_project_android.start.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
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
import sm.finalproject.com.final_project_android.util.SharePreferenceController;

public class SaveDialog extends Dialog {

    //통신//
    Retrofit.Builder builder;
    NetworkService networkService;
    Retrofit saveDiaryNetwork;
    //

    Activity mActivty;

    EditText et_hashTag;

    ImageView btn_save;

    public SaveDialog(@NonNull Activity activity, final String dialogContext) {
        super(activity);

        mActivty = activity;
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

        btn_save = findViewById(R.id.btn_save);

        et_hashTag = findViewById(R.id.et_hashtag);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postSaveLastDiary(SharePreferenceController.getUserIdx(getContext()),dialogContext,et_hashTag.getText().toString());
                dismiss();
            }
        });

    }
    public void setEditText(String hashtag){
        et_hashTag.setText(hashtag);
    }
    public String getEditText(){
        return et_hashTag.getText().toString();
    }

    public void postSaveLastDiary(int userIdx, final String content, String hashTag){

        //날짜구하기
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 M월 dd일");
        String realDate = dateFormat.format(date);


        PostLastDiaryResponseData postLastDiaryResponseData = new PostLastDiaryResponseData(userIdx,content,realDate,hashTag);



        networkService = saveDiaryNetwork.create(NetworkService.class);

        final Call<PostLastDiaryResponse> postLastDiaryResponseCall = networkService.postLastDiary(postLastDiaryResponseData);
        postLastDiaryResponseCall.enqueue(new Callback<PostLastDiaryResponse>() {
            @Override
            public void onResponse(Call<PostLastDiaryResponse> call, Response<PostLastDiaryResponse> response) {
                if(response.isSuccessful()){

                    Toast.makeText(getContext(), "!!저장성공!!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mActivty, MainActivity.class);
                    mActivty.startActivity(intent);
                    mActivty.finish();

                }else{
                    Log.d("통신에러", "에러남?");

                }
            }

            @Override
            public void onFailure(Call<PostLastDiaryResponse> call, Throwable t) {
                Log.d("통신에러2",t.getMessage());

            }
        });



    }


}
