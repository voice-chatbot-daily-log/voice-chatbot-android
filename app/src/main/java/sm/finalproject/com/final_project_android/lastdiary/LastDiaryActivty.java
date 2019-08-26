package sm.finalproject.com.final_project_android.lastdiary;

import android.app.Dialog;
import android.content.Intent;
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
import sm.finalproject.com.final_project_android.lastdiary.dialog.HashTagDialog;
import sm.finalproject.com.final_project_android.model.GetLastDiaryResponse;
import sm.finalproject.com.final_project_android.networkService.NetworkService;
import sm.finalproject.com.final_project_android.start.VoiceChatActivty;

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

    int searchByDate_flag = 0;
    int searchByTag_flag = 0;
    int searchAll_flag = 0;

    int getContentByVoice_flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_diary_activty);

        btn_search_date=findViewById(R.id.btn_search_date);
        btn_search_tag=findViewById(R.id.btn_search_tag);
        btn_search_all=findViewById(R.id.btn_search_all);

        dateDialog = new DateDialog(this);
        hashTagDialog = new HashTagDialog(this);

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
        }, 9000);  // 2000은 2초를 의미합니다.


        btn_search_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.stop();
                getLastDiary();


            }
        });

        btn_search_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.stop();
                dateDialog.show();


            }
        });

        btn_search_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.stop();
                hashTagDialog.show();

            }
        });

    }

    public void getLastDiary(){
        final Call<GetLastDiaryResponse> getLastDiaryResponseCall = networkService.getLastDiary(1);
        getLastDiaryResponseCall.enqueue(new Callback<GetLastDiaryResponse>() {
            @Override
            public void onResponse(Call<GetLastDiaryResponse> call, Response<GetLastDiaryResponse> response) {
                if(response.isSuccessful()){

                    lastDiaryData = response.body().data;
                    lastDiaryAdapter = new LastDiaryAdapter(lastDiaryData,LastDiaryActivty.this);

                    last_diary_rcv.setAdapter(lastDiaryAdapter);

                    speechRecognizer.cancel();

                    for(int i=0;i<lastDiaryData.size();i++){
                        textToSpeech.speak(lastDiaryData.get(i).last_diary_date, TextToSpeech.QUEUE_ADD, null);
                    }

                    textToSpeech.speak("내용을 알고싶은 일기의 날짜를 말해주세요.", TextToSpeech.QUEUE_FLUSH, null);
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            startListening();
                            getContentByVoice_flag = 1;
                        }
                    }, 2000);  // 2000은 2초를 의미합니다.


                }
            }

            @Override
            public void onFailure(Call<GetLastDiaryResponse> call, Throwable t) {

            }
        });
    }

    public void getLastDiaryByHashtag(String hashTag){
        final Call<GetLastDiaryResponse> getLastDiaryResponseByHashtagCall = networkService.getLastDiaryByHashTag(1,hashTag);
        getLastDiaryResponseByHashtagCall.enqueue(new Callback<GetLastDiaryResponse>() {
            @Override
            public void onResponse(Call<GetLastDiaryResponse> call, Response<GetLastDiaryResponse> response) {
                lastDiaryData = response.body().data;
                lastDiaryAdapter = new LastDiaryAdapter(lastDiaryData,LastDiaryActivty.this);

                last_diary_rcv.setAdapter(lastDiaryAdapter);

                speechRecognizer.cancel();

                for(int i=0;i<lastDiaryData.size();i++){
                    textToSpeech.speak(lastDiaryData.get(i).last_diary_date, TextToSpeech.QUEUE_ADD, null);
                }

            }

            @Override
            public void onFailure(Call<GetLastDiaryResponse> call, Throwable t) {

            }
        });

    }

    public void getLastDiaryByDate(String date){

        final Call<GetLastDiaryResponse> getLastDiaryResponseByDateCall = networkService.getLastDiaryByDate(1, date);
        getLastDiaryResponseByDateCall.enqueue(new Callback<GetLastDiaryResponse>() {
            @Override
            public void onResponse(Call<GetLastDiaryResponse> call, Response<GetLastDiaryResponse> response) {
                lastDiaryData = response.body().data;
                lastDiaryAdapter = new LastDiaryAdapter(lastDiaryData,LastDiaryActivty.this);



                last_diary_rcv.setAdapter(lastDiaryAdapter);
                for(int i=0;i<lastDiaryData.size();i++){
                    textToSpeech.speak(lastDiaryData.get(i).last_diary_date, TextToSpeech.QUEUE_ADD, null);
                }

                speechRecognizer.stopListening();

            }

            @Override
            public void onFailure(Call<GetLastDiaryResponse> call, Throwable t) {

            }
        });

    }

    @Override
    public void onInit(int status) {
        Log.d("메뉴", "ㅇㅋ");
        String selectText = "날짜로 검색하시려면 날짜, 태그로 검색하시려면 태그, 전체를 보시려면 전체라고 말해주세요";
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
        Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();

        if (PermissionUtils.checkAudioRecordPermission(LastDiaryActivty.this)) {
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
            String message;
//            int i=0;
//            while(i<2){
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
//            i++;
//            }
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
                }, 1000);  // 2000은 2초를 의미합니다.
            }
            else if(inputText.equals("전체")) {
                Toast.makeText(getApplicationContext(), "전체 보기", Toast.LENGTH_SHORT).show();
                getLastDiary();
                searchAll_flag =1;
            }

            else if(searchByTag_flag==0 && searchByDate_flag==0 && searchAll_flag==0){
                handler.postDelayed(new Runnable() {
                    public void run() {
                        textToSpeech.stop();
                        Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
                        startListening();
                    }
                }, 1800);  // 2000은 2초를 의미합니다.

            }

            if(getContentByVoice_flag == 1){
                getContentByVocie(inputText,lastDiaryData);
            }

            if(searchByDate_flag == 1){ //날짜로 검색시 stt
                getLastDiaryByDate(inputText);
            }

            if(searchByTag_flag == 1){//태그로 검색시 stt
                getLastDiaryByHashtag(inputText);
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

    public void getContentByVocie(String inputText, ArrayList<LastDiaryData> lastDiaryData){
        for(int i = 0; i< lastDiaryData.size();i++){
            if(inputText.equals(lastDiaryData.get(i).last_diary_date)){
                Intent intent = new Intent(this, LastDiaryContentActivity.class);
                intent.putExtra("diary_content",lastDiaryData.get(i).last_diary_content);
                startActivity(intent);
            }
        }

    }
}
