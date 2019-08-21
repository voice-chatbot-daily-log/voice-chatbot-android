package sm.finalproject.com.final_project_android.start.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import sm.finalproject.com.final_project_android.R;
import sm.finalproject.com.final_project_android.start.data.ChatData;

public class ChatAdapter extends RecyclerView.Adapter {

    private ArrayList<ChatData> chatData;
    public View view;

    public ChatAdapter(ArrayList<ChatData> chatData) {
        this.chatData = chatData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatbot,parent,false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ChatViewHolder chatViewHolder = (ChatViewHolder) holder;

        int isChatbot = chatData.get(position).chat_flag; //챗봇이 말한거면 0, 내가 말한거면 1
        String chatMsg = chatData.get(position).chat_message;


        chatViewHolder.chatmsg.setText(chatMsg);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

        if(isChatbot==0){
            chatViewHolder.chatbrg.setBackgroundResource(R.drawable.dialogflow);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
            chatViewHolder.chatbrg.setLayoutParams(params);
        }else{
            chatViewHolder.chatbrg.setBackgroundResource(R.drawable.me);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
            chatViewHolder.chatbrg.setLayoutParams(params);
        }

    }

    @Override
    public int getItemCount() {
        return chatData.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder{

        TextView chatmsg;
        RelativeLayout chatbrg;


        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatmsg = itemView.findViewById(R.id.chat_msg);
            chatbrg = itemView.findViewById(R.id.chat_brg);

        }
    }

    //풀리퀘 예시
}
