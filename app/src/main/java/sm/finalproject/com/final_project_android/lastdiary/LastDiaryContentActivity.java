package sm.finalproject.com.final_project_android.lastdiary;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;

import sm.finalproject.com.final_project_android.R;

import static android.speech.tts.TextToSpeech.ERROR;

public class LastDiaryContentActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    TextView tv_last_diary_content;
    TextToSpeech diary_tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_diary_content);

        diary_tts = new TextToSpeech(this, this);

        Intent intent = getIntent();
        String last_diary_content = intent.getStringExtra("diary_content");

        tv_last_diary_content = findViewById(R.id.tv_last_diary_content);

        String[] array = last_diary_content.split("<br/>");

        for(int i=0; i<array.length; i++){
            tv_last_diary_content.append("\n"+ array[i]);
            //Log.d("Test", "text= "+array[i]);
        }
    }

    @Override
    public void onInit(int status) {
        diary_tts.setLanguage(Locale.KOREAN);
        diary_tts.speak(tv_last_diary_content.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
    }

}
