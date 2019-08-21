package sm.finalproject.com.final_project_android;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.sdk.newtoneapi.SpeechRecognizeListener;
import com.kakao.sdk.newtoneapi.SpeechRecognizerClient;
import com.kakao.sdk.newtoneapi.SpeechRecognizerManager;
import com.kakao.sdk.newtoneapi.TextToSpeechClient;
import com.kakao.sdk.newtoneapi.TextToSpeechListener;
import com.kakao.sdk.newtoneapi.TextToSpeechManager;
import com.kakao.sdk.newtoneapi.impl.util.PermissionUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sm.finalproject.com.final_project_android.lastdiary.LastDiaryActivty;
import sm.finalproject.com.final_project_android.lastdiary.adapter.LastDiaryAdapter;
import sm.finalproject.com.final_project_android.model.PostChatResponse;
import sm.finalproject.com.final_project_android.model.PostChatResponseData;
import sm.finalproject.com.final_project_android.model.QueryInput;
import sm.finalproject.com.final_project_android.model.QueryInputData;
import sm.finalproject.com.final_project_android.networkService.NetworkService;
import sm.finalproject.com.final_project_android.start.VoiceChatActivty;
import sm.finalproject.com.final_project_android.util.ApplicationController;

import static android.speech.SpeechRecognizer.ERROR_SPEECH_TIMEOUT;
import static android.speech.SpeechRecognizer.isRecognitionAvailable;
import static com.kakao.util.helper.Utility.getKeyHash;
import static com.kakao.util.helper.Utility.getPackageInfo;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    Intent i;
    SpeechRecognizer mRecognizer;
    Button btn_start;
    Button btn_record;
    Handler handler;

    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_start = findViewById(R.id.btn_start);
        btn_record = findViewById(R.id.btn_record);
        handler = new Handler();

        tts = new TextToSpeech(this, this);

        handler.postDelayed(new Runnable() {
            public void run() {
                tts.stop();
                startListening();
            }
        }, 3000);  // 2000은 2초를 의미합니다.

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,VoiceChatActivty.class);
                startActivity(intent);
                finish();
            }
        });

        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LastDiaryActivty.class);
                startActivity(intent);

//                try {
//                    tts.stop();
//                    mRecognizer.cancel();
//                }catch (Exception e){
//                    Log.e("에러",e.getMessage());
//                  //  mRecognizer.cancel();
//                }

                finish();
            }
        });

    }

    public void onInit(int status) {
        String startText = "시작하시려면 시작이라고 말하세요.";
        tts.speak(startText, TextToSpeech.QUEUE_FLUSH, null);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.shutdown();

    }

    private void startListening() {

        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
        Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();


        if (PermissionUtils.checkAudioRecordPermission(MainActivity.this)) {
            mRecognizer.setRecognitionListener(mSTTListener);
            mRecognizer.startListening(i);
        }



    }

    public RecognitionListener mSTTListener = new RecognitionListener() {
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
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    String sttError1 = "다시 말해주세요.";
                    tts.speak(sttError1, TextToSpeech.QUEUE_FLUSH, null);

                    handler.postDelayed(new Runnable() {
                        public void run() {
                            tts.stop();
                            Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
                            startListening();
                        }
                    }, 1800);  // 2000은 2초를 의미합니다.
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    String sttError2 = "다시 말해주세요.";
                    tts.speak(sttError2, TextToSpeech.QUEUE_FLUSH, null);

                    handler.postDelayed(new Runnable() {
                        public void run() {
                            tts.stop();
                            Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
                            startListening();
                        }
                    }, 1800);  // 2000은 2초를 의미합니다.
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }


        }

        @Override
        public void onResults(Bundle results) {
            final ArrayList<String> texts = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            String inputText = texts.get(0);
            Toast.makeText(getApplicationContext(), inputText, Toast.LENGTH_SHORT).show();

            Log.d("넘어옴?", "ㅇㅇ");

            if (inputText.equals("시작")) {
                Intent intent = new Intent(MainActivity.this, VoiceChatActivty.class);
                startActivity(intent);
                finish();
            }

            else if(inputText.equals("지난 일기")) {
                Intent intent = new Intent(MainActivity.this, LastDiaryActivty.class);
                startActivity(intent);
                finish();
            }

            else {

                handler.postDelayed(new Runnable() {
                    public void run() {
                        tts.stop();
                        Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
                        startListening();
                    }
                }, 1800);  // 2000은 2초를 의미합니다.

            }

        }


        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }


    };

}


