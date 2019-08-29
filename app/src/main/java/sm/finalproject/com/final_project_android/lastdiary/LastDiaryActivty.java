package sm.finalproject.com.final_project_android.lastdiary;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kakao.sdk.newtoneapi.SpeechRecognizeListener;
import com.kakao.sdk.newtoneapi.impl.util.PermissionUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sm.finalproject.com.final_project_android.MainActivity;
import sm.finalproject.com.final_project_android.R;
import sm.finalproject.com.final_project_android.lastdiary.adapter.LastDiaryAdapter;
import sm.finalproject.com.final_project_android.lastdiary.data.LastDiaryData;
import sm.finalproject.com.final_project_android.lastdiary.dialog.DateDialog;
import sm.finalproject.com.final_project_android.lastdiary.dialog.DeleteDialog;
import sm.finalproject.com.final_project_android.lastdiary.dialog.HashTagDialog;
import sm.finalproject.com.final_project_android.model.DeleteLastDiaryResponseData;
import sm.finalproject.com.final_project_android.model.GetLastDiaryResponse;
import sm.finalproject.com.final_project_android.networkService.NetworkService;
import sm.finalproject.com.final_project_android.start.VoiceChatActivty;
import sm.finalproject.com.final_project_android.util.SharePreferenceController;

public class LastDiaryActivty extends AppCompatActivity implements TextToSpeech.OnInitListener{

    //리사이클러뷰
    RecyclerView last_diary_rcv;
    public RecyclerView.LayoutManager mLayoutManager_lastDiary;
    public ArrayList<LastDiaryData> lastDiaryData;
    public LastDiaryAdapter lastDiaryAdapter;
    //

    Button btn_search_date;
    Button btn_search_tag;
    Button btn_search_all;
    Button btn_delete;

    TextToSpeech textToSpeech;
    SpeechRecognizer speechRecognizer;

    Handler handler;

    //통신//
    Retrofit.Builder builder;
    NetworkService networkService;
    Retrofit lastDiaryNetwork;
    //

    DateDialog dateDialog;
    HashTagDialog hashTagDialog;
    DeleteDialog deleteDialog;

    int searchByDate_flag = 0;
    int searchByTag_flag = 0;
    int searchAll_flag = 0;

    int first_delete_flag = 0;
    int second_delete_flag = 0;

    int getContentByVoice_flag = 0;

    int stop_flag=0;
    int tts_stop_flag=0;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_diary_activty);

        btn_search_date=findViewById(R.id.btn_search_date);
        btn_search_tag=findViewById(R.id.btn_search_tag);
        btn_search_all=findViewById(R.id.btn_search_all);
        btn_delete=findViewById(R.id.btn_delete);

        dateDialog = new DateDialog(this);
        hashTagDialog = new HashTagDialog(this);
        deleteDialog = new DeleteDialog(this);

        //통신//
        builder = new Retrofit.Builder();

        lastDiaryNetwork = builder
                .baseUrl("http://13.209.245.84:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        networkService = lastDiaryNetwork.create(NetworkService.class);
        //////

        last_diary_rcv = findViewById(R.id.last_diary_rcv);
        mLayoutManager_lastDiary = new LinearLayoutManager(this.getApplicationContext());
        last_diary_rcv.setHasFixedSize(true);
        last_diary_rcv.setLayoutManager(mLayoutManager_lastDiary);

        textToSpeech = new TextToSpeech(this, this);
        handler = new Handler();

        handler.postDelayed(new Runnable() {
            public void run() {
                //textToSpeech.stop();
                    startListening();
            }
        }, 10000);  // 2000은 2초를 의미합니다.


        btn_search_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.stop();
                if(isSTTPlaying(LastDiaryActivty.this)){
                    speechRecognizer.cancel();
                }
                else{
                    stop_flag=1;
                }
                getLastDiary();

            }
        });

        btn_search_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.stop();
                if(isSTTPlaying(LastDiaryActivty.this)){
                    speechRecognizer.cancel();
                }
                else{
                    stop_flag=1;
                }
                dateDialog.show();


            }
        });

        btn_search_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.stop();
                if(isSTTPlaying(LastDiaryActivty.this)){
                    speechRecognizer.cancel();
                }
                else{
                    stop_flag=1;
                }
                hashTagDialog.show();

            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.stop();
                if(isSTTPlaying(LastDiaryActivty.this)){
                    speechRecognizer.cancel();
                }
                else{
                    stop_flag=1;
                }
                deleteDialog.show();
            }
        });

    }

    public void getLastDiary(){
        final Call<GetLastDiaryResponse> getLastDiaryResponseCall = networkService.getLastDiary(SharePreferenceController.getUserIdx(LastDiaryActivty.this));
        getLastDiaryResponseCall.enqueue(new Callback<GetLastDiaryResponse>() {
            @Override
            public void onResponse(Call<GetLastDiaryResponse> call, Response<GetLastDiaryResponse> response) {
                if(response.isSuccessful()){

                    lastDiaryData = response.body().data;
                    lastDiaryAdapter = new LastDiaryAdapter(lastDiaryData,LastDiaryActivty.this);

                    last_diary_rcv.setAdapter(lastDiaryAdapter);

                    for(i=0;i<lastDiaryData.size();i++){
                        textToSpeech.speak(lastDiaryData.get(i).last_diary_date, TextToSpeech.QUEUE_ADD, null);
                    }

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (tts_stop_flag == 0) {
                                while (textToSpeech.isSpeaking()) {
                                    tts_stop_flag = 0;
                                }
                                tts_stop_flag = 1;
                            }
                            if (tts_stop_flag==1) {
                                if(lastDiaryData.size()==0){
                                    textToSpeech.speak("작성한 일기가 없습니다.", TextToSpeech.QUEUE_FLUSH, null);
                                    tts_stop_flag=0;
                                }
                                else {
                                    textToSpeech.speak("내용을 불러올 일기의 날짜를 말해주세요.", TextToSpeech.QUEUE_FLUSH, null);
                                    tts_stop_flag = 0;
                                    stop_flag=0;
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                                startListening();
                                                getContentByVoice_flag = 1;
                                        }
                                    }, 3000);  // 2000은 2초를 의미합니다.
                                }
                            }
                        }
                    }, 200);
                }
            }

            @Override
            public void onFailure(Call<GetLastDiaryResponse> call, Throwable t) {

            }
        });
    }

    public void getLastDiaryByHashtag(String hashTag){
        final Call<GetLastDiaryResponse> getLastDiaryResponseByHashtagCall = networkService.getLastDiaryByHashTag(SharePreferenceController.getUserIdx(LastDiaryActivty.this),hashTag);
        getLastDiaryResponseByHashtagCall.enqueue(new Callback<GetLastDiaryResponse>() {
            @Override
            public void onResponse(Call<GetLastDiaryResponse> call, Response<GetLastDiaryResponse> response) {
                lastDiaryData = response.body().data;
                lastDiaryAdapter = new LastDiaryAdapter(lastDiaryData,LastDiaryActivty.this);

                last_diary_rcv.setAdapter(lastDiaryAdapter);

                for(i=0;i<lastDiaryData.size();i++){
                    textToSpeech.speak(lastDiaryData.get(i).last_diary_date, TextToSpeech.QUEUE_ADD, null);
                }

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (tts_stop_flag == 0) {
                            while (textToSpeech.isSpeaking()) {
                                tts_stop_flag = 0;
                            }
                            tts_stop_flag = 1;
                        }
                        if (tts_stop_flag==1) {
                            if(lastDiaryData.size()==0){
                                textToSpeech.speak("작성한 일기가 없습니다.", TextToSpeech.QUEUE_FLUSH, null);
                                tts_stop_flag=0;
                            }
                            else {
                                textToSpeech.speak("내용을 불러올 일기의 날짜를 말해주세요.", TextToSpeech.QUEUE_FLUSH, null);
                                tts_stop_flag = 0;
                                stop_flag=0;
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                            startListening();
                                            getContentByVoice_flag = 1;
                                    }
                                }, 3000);  // 2000은 2초를 의미합니다.
                            }
                        }
                    }
                }, 200);
            }

            @Override
            public void onFailure(Call<GetLastDiaryResponse> call, Throwable t) {

            }
        });

    }

    public void getLastDiaryByDate(String date){

        final Call<GetLastDiaryResponse> getLastDiaryResponseByDateCall = networkService.getLastDiaryByDate(SharePreferenceController.getUserIdx(LastDiaryActivty.this), date);
        getLastDiaryResponseByDateCall.enqueue(new Callback<GetLastDiaryResponse>() {
            @Override
            public void onResponse(Call<GetLastDiaryResponse> call, Response<GetLastDiaryResponse> response) {
                lastDiaryData = response.body().data;
                lastDiaryAdapter = new LastDiaryAdapter(lastDiaryData,LastDiaryActivty.this);

                last_diary_rcv.setAdapter(lastDiaryAdapter);
                for(i=0;i<lastDiaryData.size();i++){
                    textToSpeech.speak(lastDiaryData.get(i).last_diary_date, TextToSpeech.QUEUE_ADD, null);
                }

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (tts_stop_flag == 0) {
                            while (textToSpeech.isSpeaking()) {
                                tts_stop_flag = 0;
                            }
                            tts_stop_flag = 1;
                        }
                        if (tts_stop_flag==1) {
                            if(lastDiaryData.size()==0){
                                textToSpeech.speak("작성한 일기가 없습니다.", TextToSpeech.QUEUE_FLUSH, null);
                                tts_stop_flag=0;
                            }
                            else {
                                textToSpeech.speak("내용을 불러올 일기의 날짜를 말해주세요.", TextToSpeech.QUEUE_FLUSH, null);
                                tts_stop_flag = 0;
                                stop_flag=0;
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                            startListening();
                                            getContentByVoice_flag = 1;
                                    }
                                }, 3000);  // 2000은 2초를 의미합니다.
                            }
                        }
                    }
                }, 200);
            }

            @Override
            public void onFailure(Call<GetLastDiaryResponse> call, Throwable t) {

            }
        });

    }

    public void deleteLastDiary(String date, int flag){
        DeleteLastDiaryResponseData deleteLastDiaryResponseData = new DeleteLastDiaryResponseData(SharePreferenceController.getUserIdx(LastDiaryActivty.this), date, flag);
        final Call<GetLastDiaryResponse> getLastDiaryResponseCall = networkService.deleteLastDiary(deleteLastDiaryResponseData);
        getLastDiaryResponseCall.enqueue(new Callback<GetLastDiaryResponse>() {
            @Override
            public void onResponse(Call<GetLastDiaryResponse> call, Response<GetLastDiaryResponse> response) {
                if(response.isSuccessful()){

                    if(response.body().message.equals("해당 데이터가 존재하지 않습니다.")){
                        //Toast.makeText(getApplicationContext(), "데이터 존재하지 않음", Toast.LENGTH_SHORT).show();
                        textToSpeech.speak("날짜에 해당하는 일기가 존재하지 않습니다.", TextToSpeech.QUEUE_FLUSH, null);


                    }else{
//                        Toast.makeText(getApplicationContext(), "삭제완료", Toast.LENGTH_SHORT).show();
                          textToSpeech.speak("삭제가 완료되었습니다.", TextToSpeech.QUEUE_FLUSH, null);

                    }

                }
            }

            @Override
            public void onFailure(Call<GetLastDiaryResponse> call, Throwable t) {

            }
        });

    }

    @Override
    public void onInit(int status) {
        Log.d("메뉴", "ㅇㅋ");
        String selectText = "날짜로 검색하려면 날짜, 태그로 검색하려면 태그, 전체를 불러오려면 전체, 삭제를 하려면 삭제라고 말해주세요.";
        textToSpeech.speak(selectText, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textToSpeech.shutdown();

    }

    private void startListening() {

        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);;
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(LastDiaryActivty.this);

        if (PermissionUtils.checkAudioRecordPermission(LastDiaryActivty.this)) {
            if(stop_flag!=1) {
                Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
                speechRecognizer.setRecognitionListener(speechToTextListener);
                speechRecognizer.startListening(i);
            }
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
                    //textToSpeech.speak(sttError1, TextToSpeech.QUEUE_FLUSH, null);

                    handler.postDelayed(new Runnable() {
                        public void run() {
                            textToSpeech.stop();
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
                    //textToSpeech.speak(sttError2, TextToSpeech.QUEUE_FLUSH, null);

                    handler.postDelayed(new Runnable() {
                        public void run() {
                            textToSpeech.stop();
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

            final String inputText = texts.get(0);
            Toast.makeText(getApplicationContext(), inputText, Toast.LENGTH_SHORT).show();

            if (inputText.equals("날짜")) {
                //Toast.makeText(getApplicationContext(), "날짜로 검색", Toast.LENGTH_SHORT).show();
                textToSpeech.speak("날짜를 말해주세요.", TextToSpeech.QUEUE_FLUSH, null);
                handler.postDelayed(new Runnable() {
                    public void run() {
                        startListening();
                        searchByDate_flag = 1;
                    }
                }, 1000);  // 2000은 2초를 의미합니다.
            }

            else if(inputText.equals("태그")) {
                Toast.makeText(getApplicationContext(), "태그로 검색", Toast.LENGTH_SHORT).show();
                textToSpeech.speak("태그를 말해주세요.", TextToSpeech.QUEUE_FLUSH, null);
                handler.postDelayed(new Runnable() {
                    public void run() {
                        startListening();
                        searchByTag_flag = 1;
                    }
                }, 1100);  // 2000은 2초를 의미합니다.
            }
            else if(inputText.equals("전체")) {
                Toast.makeText(getApplicationContext(), "전체 불러오기", Toast.LENGTH_SHORT).show();
                getLastDiary();
                searchAll_flag =1;
            }
            else if(inputText.equals("삭제")) {
                Toast.makeText(getApplicationContext(), "삭제하기", Toast.LENGTH_SHORT).show();
                textToSpeech.speak("전체를 삭제하시려면 전체, 일부를 삭제하시려면 일부라고 말해주세요.", TextToSpeech.QUEUE_FLUSH, null);
                handler.postDelayed(new Runnable() {
                    public void run() {
                        startListening();
                        first_delete_flag = 1;

                    }
                }, 5200);  // 2000은 2초를 의미합니다.

            }
            else if(searchByTag_flag==0 && searchByDate_flag==0 && searchAll_flag==0 && first_delete_flag == 0 && second_delete_flag == 0){
                handler.postDelayed(new Runnable() {
                    public void run() {
                        textToSpeech.stop();
                        Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
                        startListening();
                    }
                }, 1800);  // 2000은 2초를 의미합니다.

            }

            if(getContentByVoice_flag == 1){
                getContentByVoice(inputText,lastDiaryData);
            }

            if(searchByDate_flag == 1){ //날짜로 검색시 stt
                getLastDiaryByDate(inputText);
            }

            if(searchByTag_flag == 1){//태그로 검색시 stt
                getLastDiaryByHashtag(inputText);
            }

            if(first_delete_flag == 1) {//전체 삭제 혹은 일부 삭제일때
                if (inputText.equals("전체")) {//전체 삭제

                    deleteLastDiary(" ", 0);
                    first_delete_flag=0;

                } else if (inputText.equals("일부")) { //일부 삭제 tts & stt

                    textToSpeech.speak("삭제할 일기의 날짜를 말해주세요.", TextToSpeech.QUEUE_FLUSH, null);
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            startListening();
                            Log.d("어디?", "1");
                            first_delete_flag =0;
                            second_delete_flag = 1;

                        }
                    }, 2200);  // 2000은 2초를 의미합니다.

                } else {
                    textToSpeech.speak("다시 말해주세요", TextToSpeech.QUEUE_FLUSH, null);
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            textToSpeech.stop();
                            Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
                            startListening();
                            Log.d("어디?", "2");
                        }
                    }, 1800);  // 2000은 2초를 의미합니다.

                }
            }

                if(second_delete_flag == 1){//일부 삭제
                    deleteLastDiary(inputText,1);
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

        Intent intent = new Intent(LastDiaryActivty.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public Boolean isSTTPlaying(Context mContext){

        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.google.android.voicesearch.serviceapi.GoogleRecognitionService".equals(service.service.getClassName())) {
                Log.d("stt확인", "ㅇㅇ");
                return true;
            }
        }
        return false;
    }


    public void getContentByVoice(String inputText, ArrayList<LastDiaryData> lastDiaryData){
        for(int i = 0; i< lastDiaryData.size();i++){
            if(inputText.equals(lastDiaryData.get(i).last_diary_date)){
                Intent intent = new Intent(this, LastDiaryContentActivity.class);
                intent.putExtra("diary_content",lastDiaryData.get(i).last_diary_content);
                startActivity(intent);
                finish();
            }
        }

    }


}
