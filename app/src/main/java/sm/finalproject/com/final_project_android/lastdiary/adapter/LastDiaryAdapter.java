package sm.finalproject.com.final_project_android.lastdiary.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.sdk.newtoneapi.impl.util.PermissionUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

import sm.finalproject.com.final_project_android.MainActivity;
import sm.finalproject.com.final_project_android.R;
import sm.finalproject.com.final_project_android.lastdiary.LastDiaryActivty;
import sm.finalproject.com.final_project_android.lastdiary.LastDiaryContentActivity;
import sm.finalproject.com.final_project_android.lastdiary.data.LastDiaryData;
import sm.finalproject.com.final_project_android.start.VoiceChatActivty;

import static java.security.AccessController.getContext;

public class LastDiaryAdapter extends RecyclerView.Adapter {

    String last_diary_date;
//    SpeechRecognizer mRecognizer;
//
//    Context mContext;


    public static class LastDiaryViewHolder extends RecyclerView.ViewHolder{

        public TextView date;

        public LastDiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tv_last_diary_date);

        }
    }

    private ArrayList<LastDiaryData> lastDiaryData;
    private Activity mActivity;

    public LastDiaryAdapter(ArrayList<LastDiaryData> lastDiaryData, Activity activity){
        this.lastDiaryData = lastDiaryData;
        this.mActivity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_last_diary,parent,false);

        return new LastDiaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final LastDiaryViewHolder lastDiaryViewHolder = (LastDiaryViewHolder) holder;

        last_diary_date = lastDiaryData.get(position).last_diary_date;
        final String last_diary_content = lastDiaryData.get(position).last_diary_content;

        lastDiaryViewHolder.date.setText(last_diary_date);

        lastDiaryViewHolder.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, LastDiaryContentActivity.class);
                intent.putExtra("diary_content", last_diary_content);
                mActivity.startActivity(intent);
                mActivity.finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return lastDiaryData.size();
    }

}



