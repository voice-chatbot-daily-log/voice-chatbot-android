package sm.finalproject.com.final_project_android.lastdiary.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sm.finalproject.com.final_project_android.R;
import sm.finalproject.com.final_project_android.lastdiary.LastDiaryActivty;
import sm.finalproject.com.final_project_android.lastdiary.adapter.LastDiaryAdapter;
import sm.finalproject.com.final_project_android.model.GetLastDiaryResponse;
import sm.finalproject.com.final_project_android.networkService.NetworkService;

public class HashTagDialog extends Dialog {


    Activity mActivty;

    EditText et_searchByTag;
    ImageView btn_searchByTag;


    public HashTagDialog(@NonNull final LastDiaryActivty activity) {
        super(activity);
        mActivty = activity;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //다이얼로그의 타이틀바를 없애주는 옵션입니다.
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  //다이얼로그의 배경을 투명으로 만듭니다.
        setContentView(R.layout.dialog_hashtag);     //다이얼로그에서 사용할 레이아웃입니다.

        et_searchByTag = findViewById(R.id.et_searchByTag);
        btn_searchByTag = findViewById(R.id.btn_searchByTag);

        btn_searchByTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LastDiaryActivty)mActivty).getLastDiaryByHashtag(et_searchByTag.getText().toString());
                dismiss();
            }
        });
    }




}
