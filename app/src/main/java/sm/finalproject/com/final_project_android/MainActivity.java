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
import sm.finalproject.com.final_project_android.model.PostChatResponse;
import sm.finalproject.com.final_project_android.model.PostChatResponseData;
import sm.finalproject.com.final_project_android.model.QueryInput;
import sm.finalproject.com.final_project_android.model.QueryInputData;
import sm.finalproject.com.final_project_android.networkService.NetworkService;
import sm.finalproject.com.final_project_android.start.VoiceChatActivty;
import sm.finalproject.com.final_project_android.util.ApplicationController;

import static com.kakao.util.helper.Utility.getKeyHash;
import static com.kakao.util.helper.Utility.getPackageInfo;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    Intent i;
    SpeechRecognizer mRecognizer;
    Button btn_start;
    Handler handler;

    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_start = findViewById(R.id.btn_start);
        handler = new Handler();

        tts = new TextToSpeech(this, this);

        handler.postDelayed(new Runnable() {
            public void run() {
                tts.stop();
                startListening();
            }
        }, 3000);  // 2000은 2초를 의미합니다.

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

        }

        @Override
        public void onResults(Bundle results) {
            final ArrayList<String> texts = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            String inputText = texts.get(0);
            Toast.makeText(getApplicationContext(), inputText, Toast.LENGTH_SHORT);

            if (inputText.equals("시작")) {
                Intent intent = new Intent(MainActivity.this, VoiceChatActivty.class);
                startActivity(intent);
            }
            
            else {
                String sttError = "다시 말해주세요.";
                tts.speak(sttError, TextToSpeech.QUEUE_FLUSH, null);

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


