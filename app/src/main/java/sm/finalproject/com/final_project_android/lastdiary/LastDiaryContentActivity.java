package sm.finalproject.com.final_project_android.lastdiary;

import android.content.Intent;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.sdk.newtoneapi.impl.util.PermissionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import sm.finalproject.com.final_project_android.MainActivity;
import sm.finalproject.com.final_project_android.R;

import static android.speech.tts.TextToSpeech.ERROR;

public class LastDiaryContentActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    TextView tv_last_diary_content;
    TextToSpeech diary_tts;
    SpeechRecognizer speechRecognizer;

    Handler handler;

    int tts_stop_flag=0;
    int go_back_flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_diary_content);

        diary_tts = new TextToSpeech(this, this);

        handler = new Handler();

        Intent intent = getIntent();
        String last_diary_content = intent.getStringExtra("diary_content");

        tv_last_diary_content = findViewById(R.id.tv_last_diary_content);

        String[] array = last_diary_content.split("<br/>");

        for(int i=0; i<array.length; i++){
            tv_last_diary_content.append("\n"+ array[i]);
        }

    }

    @Override
    public void onInit(int status) {
        diary_tts.setLanguage(Locale.KOREAN);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");

        diary_tts.speak(tv_last_diary_content.getText().toString(), TextToSpeech.QUEUE_FLUSH, map);

        diary_tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String map) {
                LastDiaryContentActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("tts 넘어옴", "ㅇㅇ");
                        diary_tts.speak("일기가 끝났습니다. 첫화면으로 가려면 첫화면, 뒤로 가려면 뒤로 라고 말해주세요", TextToSpeech.QUEUE_FLUSH, null);
                        tts_stop_flag = 0;
                        go_back_flag = 1;
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                startListening();

                            }
                        }, 6500);  // 2000은 2초를 의미합니다.
                    }
                });
            }

            @Override
            public void onError(String utteranceId) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        diary_tts.shutdown();
    }


    private void startListening() {

        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);;
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(LastDiaryContentActivity.this);

        if (PermissionUtils.checkAudioRecordPermission(LastDiaryContentActivity.this)) {
            //Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
            speechRecognizer.setRecognitionListener(speechToTextListener);
            speechRecognizer.startListening(i);
        }
    }

    public RecognitionListener speechToTextListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {

        }

        @Override
        public void onResults(Bundle results) {
            final ArrayList<String> texts = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            final String inputText = texts.get(0);
            //Toast.makeText(getApplicationContext(), inputText, Toast.LENGTH_SHORT).show();

            if(go_back_flag==1) {
                if (inputText.equals("첫 화면")) {
                    Intent intent = new Intent(LastDiaryContentActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if (inputText.equals("뒤로")) {
                    Intent intent = new Intent(LastDiaryContentActivity.this, LastDiaryActivty.class);
                    startActivity(intent);
                    finish();
                } else {
                    diary_tts.speak("다시 말해주세요", TextToSpeech.QUEUE_FLUSH, null);
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            diary_tts.stop();
                            //Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
                            startListening();
                        }
                    }, 1800);  // 2000은 2초를 의미합니다.
                }
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(LastDiaryContentActivity.this, LastDiaryActivty.class);
        startActivity(intent);
        finish();
    }
}
