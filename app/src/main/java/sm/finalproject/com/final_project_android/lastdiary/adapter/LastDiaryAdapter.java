package sm.finalproject.com.final_project_android.lastdiary.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import sm.finalproject.com.final_project_android.R;
import sm.finalproject.com.final_project_android.lastdiary.data.LastDiaryData;

public class LastDiaryAdapter extends RecyclerView.Adapter {

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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final LastDiaryViewHolder lastDiaryViewHolder = (LastDiaryViewHolder) holder;

        String last_diary_date = lastDiaryData.get(position).last_diary_date;
        String last_diary_content = lastDiaryData.get(position).last_diary_content;

        lastDiaryViewHolder.date.setText(last_diary_date);


    }

    @Override
    public int getItemCount() {
        return lastDiaryData.size();
    }
}

