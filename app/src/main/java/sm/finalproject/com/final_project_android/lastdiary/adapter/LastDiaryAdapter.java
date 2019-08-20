package sm.finalproject.com.final_project_android.lastdiary.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sm.finalproject.com.final_project_android.R;

public class LastDiaryAdapter extends RecyclerView.Adapter {

    public static class LastDiaryViewHolder extends RecyclerView.ViewHolder{

        public LastDiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            TextView date = itemView.findViewById(R.id.tv_last_diary_date);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}

