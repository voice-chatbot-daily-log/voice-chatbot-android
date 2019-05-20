package sm.finalproject.com.final_project_android.start;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import sm.finalproject.com.final_project_android.start.adapter.ChatAdapter;
import sm.finalproject.com.final_project_android.start.data.ChatData;
import sm.finalproject.com.final_project_android.util.ApplicationController;

import static com.kakao.util.helper.Utility.getPackageInfo;

public class VoiceChatActivty extends AppCompatActivity implements SpeechRecognizeListener,TextToSpeechListener {

    NetworkService networkService;

    //newtone API 사용////////
    private TextToSpeechClient ttsClient;
    private SpeechRecognizerClient sttClient;
    ///////////////////////////
    private static final int REQUEST_CODE_AUDIO_AND_WRITE_EXTERNAL_STORAGE = 0;
    final String auth_head = "Bearer ";
    final String auth_body = "ya29.c.El8JB-df6l00lMfi_OUATrQAH7G1wYWq1-wDm3sb0aePqKZTwVO5xBsad_oEiQJvqUt8mO0BKqOsUn_7RIHwHjb1c9Ll8qQhkyR_8Mj_m0ODzHF4hO2vyxRt6u0kzkAPAg";

    Handler handler;

    public String first_start_msg = "";

    //리사이클러뷰
    public RecyclerView chat_rcv;

    public RecyclerView.LayoutManager mLayoutManager_chat;
    public ArrayList<ChatData> chatData;
    public ChatAdapter chatAdapter;
    //리사이클러뷰


    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_chat);

        //ui 세팅

        handler = new Handler();

        mLayoutManager_chat = new LinearLayoutManager(this.getApplicationContext());
        chat_rcv = findViewById(R.id.chat_rcv);
        chat_rcv.setHasFixedSize(true);
        chat_rcv.setLayoutManager(mLayoutManager_chat);

        chatData = new ArrayList<>();

        //chatData.add(new ChatData("첫시작",1));
        //


        /////////hash key 받는곳//////////

        getHashKey();
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





        postInputText(auth_head+auth_body,"첫시작");


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



    }//onCreate() 끝

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
        TextToSpeechManager.getInstance().finalizeLibrary();
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
        Toast.makeText(getApplicationContext(),"Dd", Toast.LENGTH_SHORT).show();

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
        runOnUiThread(new Runnable() {  //stt를 할때 받는곳 (사용자가 말하고 나서 텍스트로 보여주는것)
            @Override
            public void run() {
                String inputText = texts.get(0);
                if (activity.isFinishing()) return;
                Log.d("확인3",String.valueOf(ttsClient.isPlaying()));
               // tv_result.setText(inputText);
                Toast.makeText(getApplicationContext(),inputText, Toast.LENGTH_SHORT).show();



                chatData.add(new ChatData(inputText,1));
                chat_rcv.setAdapter(chatAdapter);

                postInputText(auth_head+auth_body,inputText);

            }
        });

    }

    @Override
    public void onAudioLevel(float audioLevel) {

    }



    public void postInputText(String auth,String input){

        PostChatResponseData postChatResponseData = new PostChatResponseData(input, "ko");

        final QueryInputData queryInputData = new QueryInputData(postChatResponseData);
        final QueryInput queryInput = new QueryInput(queryInputData);

        Call<PostChatResponse> postChatResponseCall = networkService.postChat(auth,queryInput);
        postChatResponseCall.enqueue(new Callback<PostChatResponse>() {
            @Override
            public void onResponse(Call<PostChatResponse> call, Response<PostChatResponse> response) {
                if(response.isSuccessful()) { //사용자의 응답을 받고 DialogFlow가 응답을 나타내는 곳
                    //Toast.makeText(getApplicationContext(),response.body().queryResult.fulfillmentText,Toast.LENGTH_LONG).show();
                    first_start_msg = response.body().queryResult.fulfillmentText;
                    if (first_start_msg.equals("안녕?")) {
                        ttsClient.play(first_start_msg);

                    } else {
                        sttClient.stopRecording();//tts플레이 하기전에 stt 멈춰주기!!!!!!!!!!!
                        ttsClient.play(response.body().queryResult.fulfillmentText);
                    }

                    chatData.add(new ChatData(response.body().queryResult.fulfillmentText, 0));
                    chatAdapter = new ChatAdapter(chatData);
                    chat_rcv.setAdapter(chatAdapter);

                    Log.d("확인",String.valueOf(ttsClient.isPlaying()));
                    if (PermissionUtils.checkAudioRecordPermission(VoiceChatActivty.this)) {
                        String serviceType = SpeechRecognizerClient.SERVICE_TYPE_WEB;
                        SpeechRecognizerClient.Builder builder = new SpeechRecognizerClient.Builder().setServiceType(serviceType);


                        sttClient = builder.build();

                        sttClient.setSpeechRecognizeListener(VoiceChatActivty.this);


                        try {
                            Thread.sleep(4000);
                            ttsClient.stop();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.d("확인2",String.valueOf(ttsClient.isPlaying()));


                        sttClient.startRecording(false);


                    }
                }



            }

            @Override
            public void onFailure(Call<PostChatResponse> call, Throwable t) {


            }
        });
    }

    private void getHashKey(){

        try{

            PackageInfo info = getPackageManager().getPackageInfo("sm.finalproject.com.final_project_android", PackageManager.GET_SIGNATURES);

            for(Signature signature : info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("TAG: ","key_hash: "+ Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }


    }

    public void btnMethod(View v) {



    }

}
