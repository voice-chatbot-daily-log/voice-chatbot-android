package sm.finalproject.com.final_project_android.start.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.EditText;

import sm.finalproject.com.final_project_android.R;

public class SaveDialog extends Dialog {
    EditText et_hashTag;
    public SaveDialog(@NonNull Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //다이얼로그의 타이틀바를 없애주는 옵션입니다.
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  //다이얼로그의 배경을 투명으로 만듭니다.
        setContentView(R.layout.dialog_save);     //다이얼로그에서 사용할 레이아웃입니다.
        et_hashTag = findViewById(R.id.et_hashtag);
    }
    public void setEditText(String hastag){
        et_hashTag.setText(hastag);
    }


}
