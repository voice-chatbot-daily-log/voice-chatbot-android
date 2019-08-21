package sm.finalproject.com.final_project_android.lastdiary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import sm.finalproject.com.final_project_android.R;

public class LastDiaryContentActivity extends AppCompatActivity {

    TextView tv_last_diary_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_diary_content);

        Intent intent = getIntent();
        String last_diary_content = intent.getStringExtra("diary_content");

        tv_last_diary_content = findViewById(R.id.tv_last_diary_content);
        tv_last_diary_content.setText(last_diary_content);

    }
}
