package sm.finalproject.com.final_project_android.start.data;

public class ChatData {
    public String chat_message;
    public int chat_flag;//챗봇이 말하는건지, 내가 말하는 건지 확인
    public ChatData(String chat_message,int chat_flag) {
        this.chat_message = chat_message;
        this.chat_flag = chat_flag;
    }



}
