package sm.finalproject.com.final_project_android.lastdiary.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
    //private ArrayList<LastDiaryData> lastDiaryData;

//    public LastDiaryAdapter(ArrayList<LastDiaryData> lastDiaryData, View view){
//        this.lastDiaryData = lastDiaryData;
//        this.view = view;
//    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_last_diary,parent,false);

        return new LastDiaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}

