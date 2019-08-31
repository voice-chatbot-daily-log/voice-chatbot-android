package sm.finalproject.com.final_project_android.start.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import sm.finalproject.com.final_project_android.MainActivity;
import sm.finalproject.com.final_project_android.R;
import sm.finalproject.com.final_project_android.lastdiary.LastDiaryActivty;
import sm.finalproject.com.final_project_android.start.VoiceChatActivty;

public class QuestionDialog extends Dialog {


    Activity mActivity;

    RadioButton yesRadio;
    RadioButton noRadio;

    ImageView btn_send;

    SaveDialog saveDialog;


    public QuestionDialog(@NonNull final VoiceChatActivty activity, final String dialogContext) {
        super(activity);
        mActivity = activity;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //다이얼로그의 타이틀바를 없애주는 옵션입니다.
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  //다이얼로그의 배경을 투명으로 만듭니다.
        setContentView(R.layout.dialog_question);     //다이얼로그에서 사용할 레이아웃입니다.

        yesRadio = findViewById(R.id.radio_yes);
        noRadio = findViewById(R.id.radio_no);

        btn_send = findViewById(R.id.btn_send);

        saveDialog = new SaveDialog(mActivity,dialogContext);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(yesRadio.isChecked()){
                    saveDialog.show();
                    dismiss();

                }else if(noRadio.isChecked()){

                    Intent intent = new Intent(mActivity, MainActivity.class);
                    mActivity.startActivity(intent);
                    mActivity.finish();

                    dismiss();


                }

            }
        });


    }

}
