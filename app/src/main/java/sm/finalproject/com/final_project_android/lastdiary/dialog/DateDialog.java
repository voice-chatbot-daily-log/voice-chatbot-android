package sm.finalproject.com.final_project_android.lastdiary.dialog;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import sm.finalproject.com.final_project_android.R;
import sm.finalproject.com.final_project_android.lastdiary.LastDiaryActivty;

public class DateDialog extends Dialog {


    Activity mActivity;

    EditText et_searchByDate;
    ImageView btn_searchByDate;


    public DateDialog(@NonNull final LastDiaryActivty activity) {
        super(activity);
        mActivity = activity;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //다이얼로그의 타이틀바를 없애주는 옵션입니다.
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  //다이얼로그의 배경을 투명으로 만듭니다.
        setContentView(R.layout.dialog_date);     //다이얼로그에서 사용할 레이아웃입니다.

        et_searchByDate = findViewById(R.id.et_searchByDate);
        btn_searchByDate = findViewById(R.id.btn_searchByDate);

        btn_searchByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LastDiaryActivty)mActivity).getLastDiaryByDate(et_searchByDate.getText().toString());
                dismiss();
            }
        });

    }

}