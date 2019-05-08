package sm.finalproject.com.final_project_android.start;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
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
import sm.finalproject.com.final_project_android.MainActivity;
import sm.finalproject.com.final_project_android.R;
import sm.finalproject.com.final_project_android.model.PostChatResponse;
import sm.finalproject.com.final_project_android.model.PostChatResponseData;
import sm.finalproject.com.final_project_android.model.QueryInput;
import sm.finalproject.com.final_project_android.model.QueryInputData;
import sm.finalproject.com.final_project_android.networkService.NetworkService;
import sm.finalproject.com.final_project_android.util.ApplicationController;

import static com.kakao.util.helper.Utility.getPackageInfo;

public class VoiceChatActivty extends AppCompatActivity implements SpeechRecognizeListener,TextToSpeechListener {

    NetworkService networkService;
    Button click;
    TextView tv_result;

    //newtone API 사용////////
    private TextToSpeechClient ttsClient;
    private SpeechRecognizerClient sttClient;
    ///////////////////////////
    private static final int REQUEST_CODE_AUDIO_AND_WRITE_EXTERNAL_STORAGE = 0;
    final String auth_head = "Bearer ";
    final String auth_body = "ya29.c.El8DBwu1U51bA_mUCBexs1m_TgnoNjVo82oWHwtfRLbDiqxTmVPyHTtbWdjQemGQ1qf57vNMzBGbPZHIzpNtGO2K5jX1FpLM0GItsssjyrbNeUVObhetr_3UZ1p5GDIF1g";


    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_chat);

        //ui 세팅
        click = (Button)findViewById(R.id.click);
        tv_result = findViewById(R.id.tv_result);
        //


        /////////hash key 받는곳//////////
        mContext = getApplicationContext();
        String key = getKeyHash(mContext);
        Log.d("Key", "HashKey:" + key);
        ////////////////////////////////


        //통신 post 설정//
        networkService = ApplicationController.getInstance().getNetworkService();
        ///////////////





// SDK 초기화

        SpeechRecognizerManager.getInstance().initializeLibrary(this);
        TextToSpeechManager.getInstance().initializeLibrary(getApplicationContext());
//


        //Text to Text 세팅////

        ttsClient = new TextToSpeechClient.Builder()
                .setSpeechMode(TextToSpeechClient.NEWTONE_TALK_1)     // 음성합성방식
                .setSpeechSpeed(1.0)            // 발음 속도(0.5~4.0)
                .setSpeechVoice(TextToSpeechClient.VOICE_WOMAN_DIALOG_BRIGHT)  //TTS 음색 모드 설정(여성 차분한 낭독체)
                .setListener(VoiceChatActivty.this)
                .build();



        ////////////////



        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (PermissionUtils.checkAudioRecordPermission(VoiceChatActivty.this)) {
                    //Speaking to Text 세팅////
                    String serviceType = SpeechRecognizerClient.SERVICE_TYPE_WEB;
                    ///////////////

                    SpeechRecognizerClient.Builder builder = new SpeechRecognizerClient.Builder().setServiceType(serviceType);

                    sttClient = builder.build();

                    sttClient.setSpeechRecognizeListener(VoiceChatActivty.this);
                    sttClient.startRecording(true);

                    Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();

                    click.setEnabled(false);

                }


            }
        });


        ////////동적 권한 부여 코드///////////

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_AUDIO_AND_WRITE_EXTERNAL_STORAGE);
            } else {
                // 유저가 거부하면서 다시 묻지 않기를 클릭.. 권한이 없다고 유저에게 직접 알림.
            }
        } else {
            // startUsingSpeechSDK();
        }

        ////////동적 권한 부여 코드 끝////////////



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_AUDIO_AND_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //startUsingSpeechSDK();
                } else {
                    finish();
                }
                break;
            default:
                break;
        }
    }

    // 더 이상 쓰지 않는 경우에는 다음과 같이 해제
    public void onDestroy() {
        super.onDestroy();
        //TextToSpeechManager.getInstance().finalizeLibrary();
    }


    @Override
    public void onFinished() {

        int intSentSize = ttsClient.getSentDataSize();      //세션 중에 전송한 데이터 사이즈
        int intRecvSize = ttsClient.getReceivedDataSize();  //세션 중에 전송받은 데이터 사이즈

        final String strInacctiveText = "handleFinished() SentSize : " + intSentSize + "  RecvSize : " + intRecvSize;

        Log.i("ㅇ", strInacctiveText);

    }

    @Override
    public void onReady() {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int code, String message) {
        // handleError(code);
    }

    @Override
    public void onPartialResult(String partialResult) {

    }

    @Override
    public void onResults(Bundle results) {




        final StringBuilder builder = new StringBuilder();


        final ArrayList<String> texts = results.getStringArrayList(SpeechRecognizerClient.KEY_RECOGNITION_RESULTS);
        ArrayList<Integer> confs = results.getIntegerArrayList(SpeechRecognizerClient.KEY_CONFIDENCE_VALUES);



        for (int i = 0; i < texts.size(); i++) {
            builder.append(texts.get(i));
            builder.append(" (");
            builder.append(confs.get(i).intValue());
            builder.append(")\n");
        }

        final Activity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String inputText = texts.get(0);
                if (activity.isFinishing()) return;

                tv_result.setText(inputText);
                //Toast.makeText(getApplicationContext(),tv_result.getText().toString(), Toast.LENGTH_SHORT).show();

                postInputText(auth_head+auth_body,inputText);

                click.setEnabled(true);
            }
        });

    }

    @Override
    public void onAudioLevel(float audioLevel) {

    }

    public static String getKeyHash(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.w("main", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
    }


    public void postInputText(String auth,String input){

        PostChatResponseData postChatResponseData = new PostChatResponseData(input, "ko");

        final QueryInputData queryInputData = new QueryInputData(postChatResponseData);
        final QueryInput queryInput = new QueryInput(queryInputData);

        Call<PostChatResponse> postChatResponseCall = networkService.postChat(auth,queryInput);
        postChatResponseCall.enqueue(new Callback<PostChatResponse>() {
            @Override
            public void onResponse(Call<PostChatResponse> call, Response<PostChatResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getApplicationContext(),response.body().queryResult.fulfillmentText,Toast.LENGTH_LONG).show();
                    sttClient.stopRecording();//tts플레이 하기전에 stt 멈춰주기!!!!!!!!!!!
                    ttsClient.play(response.body().queryResult.fulfillmentText);
                }else{

                }
            }

            @Override
            public void onFailure(Call<PostChatResponse> call, Throwable t) {


            }
        });
    }
}
