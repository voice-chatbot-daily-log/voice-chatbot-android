package sm.finalproject.com.final_project_android.start;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sm.finalproject.com.final_project_android.MainActivity;
import sm.finalproject.com.final_project_android.R;
import sm.finalproject.com.final_project_android.lastdiary.LastDiaryActivty;
import sm.finalproject.com.final_project_android.model.GetLastDiaryResponse;
import sm.finalproject.com.final_project_android.model.PostChatResponse;
import sm.finalproject.com.final_project_android.model.PostChatResponseData;
import sm.finalproject.com.final_project_android.model.PostLastDiaryResponse;
import sm.finalproject.com.final_project_android.model.PostLastDiaryResponseData;
import sm.finalproject.com.final_project_android.model.QueryInput;
import sm.finalproject.com.final_project_android.model.QueryInputData;
import sm.finalproject.com.final_project_android.networkService.NetworkService;
import sm.finalproject.com.final_project_android.start.adapter.ChatAdapter;
import sm.finalproject.com.final_project_android.start.data.ChatData;
import sm.finalproject.com.final_project_android.start.dialog.QuestionDialog;
import sm.finalproject.com.final_project_android.start.dialog.SaveDialog;
import sm.finalproject.com.final_project_android.util.ApplicationController;
import sm.finalproject.com.final_project_android.util.SharePreferenceController;

import static com.kakao.util.helper.Utility.getPackageInfo;

public class VoiceChatActivty extends AppCompatActivity implements SpeechRecognizeListener,TextToSpeechListener {

    String dialogContext ;

    NetworkService networkService;

    //newtone API 사용////////
    private TextToSpeechClient ttsClient;
    private SpeechRecognizerClient sttClient;
    ///////////////////////////

    private static final int REQUEST_CODE_AUDIO_AND_WRITE_EXTERNAL_STORAGE = 0;
    final String auth_head = "Bearer ";
    final String auth_body = "ya29.c.El97BwyHOCoXnqM1gT92Q2ZujgSCyt_gwqvx-Hhz_qcRbgUeD1X8rYA3NFKuhcA30m85JIPpBGEv3wqobNHTSsCEIMGO_ELlaF7lCHyp5Xgav7Wp_q9-9RLI51R5Ouca4w";

    Handler handler;

    public String first_start_msg = "";

    //리사이클러뷰
    public RecyclerView chat_rcv;

    public RecyclerView.LayoutManager mLayoutManager_chat;
    public ArrayList<ChatData> chatData;
    public ChatAdapter chatAdapter;
    //리사이클러뷰

    public String hashTag = ""; //저장할때 해쉬태그 담을 변수

    //저장 다이얼로그
    public SaveDialog saveDialog;


    public int menu_flag=0;
    public int save_flag=0;

    public EditText editText;

    int cancel_flag=0;


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

        saveDialog = new SaveDialog(this,dialogContext);


        //다음 open api 통신 설정//
        networkService = ApplicationController.getInstance().getNetworkService();
        ///////////////


        /////////hash key 받는곳//////////

        getHashKey();
        ////////////////////////////////

        dialogContext = " "; //대화 전체를 받아올 변수

        editText = findViewById(R.id.editText);



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

        }

        ////////동적 권한 부여 코드 끝////////////

    }//onCreate() 끝

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_AUDIO_AND_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

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
        switch (code) {

            case SpeechRecognizerClient.ERROR_AUDIO_FAIL:
                message = "음성입력이 불가능하거나 마이크 접근이 허용되지 않았을 경우.";
                break;
            case SpeechRecognizerClient.ERROR_AUTH_FAIL:
                message = "apikey 인증이 실패한 경우";
                break;
            case SpeechRecognizerClient.ERROR_NETWORK_FAIL:
                message = "네트워크 오류가 발생한 경우";
                break;
            case SpeechRecognizerClient.ERROR_NETWORK_TIMEOUT:
                message = "네트워크 타임아웃이 발생한 경우.";
                break;
            case SpeechRecognizerClient.ERROR_SERVER_FAIL:
                message = "서버에서 오류가 발생한 경우.";
                break;
            case SpeechRecognizerClient.ERROR_SERVER_TIMEOUT:
                message ="서버 응답 시간이 초과한 경우";
                if(cancel_flag==1){
                    sttClient.cancelRecording();
                }
                else {
                    String sttError1 = "다시 말해주세요.";
                    ttsClient.play(sttError1);

                    handler.postDelayed(new Runnable() {
                        public void run() {
                            ttsClient.stop();
                            Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
                            sttClient.startRecording(false);
                        }
                    }, 1800);  // 2000은 2초를 의미합니다.
                }
                break;
            case SpeechRecognizerClient.ERROR_NO_RESULT:
                message = "인식된 결과 목록이 없는 경우";
                if(cancel_flag==1){
                    sttClient.cancelRecording();
                }
                else {
                    String sttError3 = "다시 말해주세요.";
                    ttsClient.play(sttError3);

                    handler.postDelayed(new Runnable() {
                        public void run() {
                            ttsClient.stop();
                            sttClient.startRecording(false);
                        }
                    }, 1800);  // 2000은 2초를 의미합니다.
                }

                break;
            case SpeechRecognizerClient.ERROR_CLIENT:
                message = "클라이언트 내부 로직에서 오류가 발생한 경우";
                break;
            case SpeechRecognizerClient.ERROR_RECOGNITION_TIMEOUT:
                if(cancel_flag==1){
                    sttClient.cancelRecording();
                }
                else {
                    String sttError2 = "다시 말해주세요.";
                    ttsClient.play(sttError2);

                    handler.postDelayed(new Runnable() {
                        public void run() {
                            ttsClient.stop();
                            sttClient.startRecording(false);
                        }
                    }, 1800);  // 2000은 2초를 의미합니다.
                }
                break;
            case SpeechRecognizerClient.ERROR_SERVER_UNSUPPORT_SERVICE:
                message = "전체 소요시간에 대한 타임아웃이 발생한 경우.";
                break;
            case SpeechRecognizerClient.ERROR_SERVER_USERDICT_EMPTY:
                message = "제공하지 않는 서비스 타입이 지정됐을 경우";
                break;
            case SpeechRecognizerClient.ERROR_SERVER_ALLOWED_REQUESTS_EXCESS:
                message = "입력된 사용자 사전에 내용이 없는 경우";
                break;
            default:
                message = "알 수 없는 오류임";
                break;

        }
        Log.e("에러",message);
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

                if(inputText.equals("송이야")){

                    chatData.add(new ChatData(inputText, 1));
                    chat_rcv.setAdapter(chatAdapter);

                    dialogContext += "나 : "+ inputText + "<br/>";

                    postInputText(auth_head + auth_body, inputText);
                    menu_flag=1;
                }

                if(menu_flag==1) {
                    if (inputText.equals("대화 끝내기")) {
                        Intent intent = new Intent(VoiceChatActivty.this, MainActivity.class);
                        startActivity(intent);
                        menu_flag=0;
                    }

                    else if (inputText.equals("지난 일기 불러오기") || inputText.equals("지난1기") || inputText.equals("지난 일기") || inputText.equals("지난 1기")) {
                        sttClient.stopRecording();

                        String q_save = "저장하시겠습니까? 네 또는 아니요 라고 말해주세요.";
                        Toast.makeText(getApplicationContext(), q_save, Toast.LENGTH_SHORT).show();

                        ttsClient.play(q_save);
                        save_flag = 1;

                        handler.postDelayed(new Runnable() {
                            public void run() {
                                ttsClient.stop();
                                sttClient.startRecording(false);
                            }
                        }, 5000);  // 2000은 2초를 의미합니다.

                        menu_flag=0;


                    }

                    else if(inputText.equals("저장하기")) {
                        //저장하는 코드 삽입

                        saveDialog.show();


                        String q_hashTag = "해쉬태그 하나를 말해주세요.";
                        ttsClient.play(q_hashTag);

                        handler.postDelayed(new Runnable() {
                            public void run() {
                                ttsClient.stop();
                                sttClient.startRecording(false);
                            }
                        }, 2000);  // 2000은 2초를 의미합니다.

                        menu_flag = 0;
                        save_flag = -1;

                    }

                    else{ //3개 메뉴가 아닌 것을 말했을 때 들어 오는 곳
                        if(inputText.equals("송이야")) {
                            Toast.makeText(getApplicationContext(), inputText, Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), inputText, Toast.LENGTH_SHORT).show();
                            Log.d("ㅇㄹ", "" + inputText);

                            String sttError1 = "다시 말해주세요.";
                            ttsClient.play(sttError1);

                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    ttsClient.stop();
                                    Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
                                    sttClient.startRecording(false);
                                }
                            }, 1800);  // 2000은 2초를 의미합니다.
                        }
                    }

                }

                else { //menu_flag == 0
                    if(save_flag==1){
                        if(inputText.equals("네")) {

                            //다이얼로그 켜기

                            saveDialog.show();

                            String q_hashTag = "해쉬태그 하나를 말해주세요.";
                            ttsClient.play(q_hashTag);

                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    ttsClient.stop();
                                    sttClient.startRecording(false);
                                }
                            }, 2000);  // 2000은 2초를 의미합니다.

                            menu_flag = 0;
                            save_flag = -1;

                        }
                        else if(inputText.equals("아니요")){
                            Toast.makeText(getApplicationContext(), "저장안함", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if(save_flag == -1){ //해쉬태그 입력
                        hashTag = inputText;
                        saveDialog.setEditText(hashTag);

                        //저장하기
                        saveDialog.postSaveLastDiary(SharePreferenceController.getUserIdx(VoiceChatActivty.this),dialogContext,hashTag);

                    }
                    else { //그냥 대화를 할때 들어가는 곳

                        dialogContext += "나 : " + inputText + "<br/>";
                        chatData.add(new ChatData(inputText, 1));
                        chat_rcv.setAdapter(chatAdapter);

                        postInputText(auth_head + auth_body, inputText);
                    }
                }
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
                    first_start_msg = response.body().queryResult.fulfillmentText;
                    if (first_start_msg.equals("안녕?")) {
                        dialogContext += "챗봇 : "+first_start_msg + "<br/>";
                        ttsClient.play(first_start_msg);

                    } else {
                        sttClient.stopRecording(); //tts플레이 하기전에 stt 멈춰주기!!!!!!!!!!!
                        dialogContext += "챗봇 : "+response.body().queryResult.fulfillmentText +"<br/>";
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


                        Log.d("확인2",String.valueOf(ttsClient.isPlaying()));

                        if(cancel_flag!=1) {
                            sttClient.startRecording(false);
                        }
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

        cancel_flag=1;

        saveDialog = new SaveDialog(this, dialogContext);

        QuestionDialog questionDialog = new QuestionDialog(VoiceChatActivty.this,dialogContext);


        Log.d("에딧텍스트 값 확인",editText.getText().toString());

        chatData.add(new ChatData(editText.getText().toString(), 1));
        chat_rcv.setAdapter(chatAdapter);

        if(editText.getText().toString().equals("저장하기")){
            saveDialog = new SaveDialog(this, dialogContext);
            saveDialog.show();

        }else if(editText.getText().toString().equals("지난 일기")){
            questionDialog.show();

        }else if(editText.getText().toString().equals("대화 끝내기")){
            Intent intent = new Intent(VoiceChatActivty.this, MainActivity.class);
            startActivity(intent);
            finish();

        }else{
            dialogContext += "나 : "+ editText.getText().toString() + "<br/>";

            postInputText(auth_head + auth_body, editText.getText().toString());
        }


        editText.setText(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(VoiceChatActivty.this, MainActivity.class);
        startActivity(intent);
        finish();
    }



}
