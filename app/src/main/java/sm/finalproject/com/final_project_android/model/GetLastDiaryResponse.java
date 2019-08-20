package sm.finalproject.com.final_project_android.model;

import java.util.ArrayList;

import sm.finalproject.com.final_project_android.lastdiary.data.LastDiaryData;

public class GetLastDiaryResponse {

    public GetLastDiaryResponse(String message, ArrayList<LastDiaryData> data) {
        this.message = message;
        this.data = data;
    }

    public String message;
    public ArrayList<LastDiaryData> data;
}
