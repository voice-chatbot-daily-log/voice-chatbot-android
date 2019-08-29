package sm.finalproject.com.final_project_android.lastdiary.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import sm.finalproject.com.final_project_android.R;
import sm.finalproject.com.final_project_android.lastdiary.LastDiaryActivty;

public class DeleteDialog extends Dialog {

    Activity mActivity;

    RadioButton delete_all;
    RadioButton delete_sub;
    EditText et_del_diaryDate;
    ImageView btn_delete1;


    public DeleteDialog(@NonNull final LastDiaryActivty activity) {
        super(activity);
        mActivity = activity;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //다이얼로그의 타이틀바를 없애주는 옵션입니다.
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  //다이얼로그의 배경을 투명으로 만듭니다.
        setContentView(R.layout.dialog_delete);     //다이얼로그에서 사용할 레이아웃입니다.

        delete_all = findViewById(R.id.radio_delete_all);
        delete_sub = findViewById(R.id.radio_delete_sub);

        et_del_diaryDate = findViewById(R.id.et_del_diaryDate);

        btn_delete1 = findViewById(R.id.btn_delete1);

        btn_delete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(delete_all.isChecked()){
                    ((LastDiaryActivty)mActivity).deleteLastDiary("",0);

                }else if(delete_sub.isChecked()){
                    ((LastDiaryActivty)mActivity).deleteLastDiary(et_del_diaryDate.getText().toString(),1);
                }
                dismiss();
            }
        });

    }
}
