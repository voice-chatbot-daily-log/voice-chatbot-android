package sm.finalproject.com.final_project_android.lastdiary.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class LastDiaryAdapter extends RecyclerView.Adapter implements TextToSpeech.OnInitListener {

    TextToSpeech list_tts;
    String last_diary_date;

    public static class LastDiaryViewHolder extends RecyclerView.ViewHolder{

        public TextView date;

        public LastDiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tv_last_diary_date);

        }
    }

    private ArrayList<LastDiaryData> lastDiaryData;

    public LastDiaryAdapter(ArrayList<LastDiaryData> lastDiaryData){
        this.lastDiaryData = lastDiaryData;
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

        list_tts = new TextToSpeech(((LastDiaryViewHolder) holder).date.getContext(), this);

        lastDiaryViewHolder.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LastDiaryContentActivity.class);
                intent.putExtra("diary_content", last_diary_content);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);

                ((LastDiaryActivty)((LastDiaryViewHolder) holder).date.getContext()).finish();
            }
        });

    }

    @Override
    public int getItemCount() {
        return lastDiaryData.size();
    }

    @Override
    public void onInit(int status) {
        list_tts.setLanguage(Locale.KOREAN);
        list_tts.speak(last_diary_date, TextToSpeech.QUEUE_FLUSH, null);
    }
}


