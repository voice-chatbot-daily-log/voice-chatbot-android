package sm.finalproject.com.final_project_android.model;

import org.w3c.dom.Text;

public class PostLastDiaryResponseData {

    public PostLastDiaryResponseData(int userIdx, String diaryContent, int diaryYear, int diaryMonth, int diaryDay) {
        this.userIdx = userIdx;
        this.diaryContent = diaryContent;
        this.diaryYear = diaryYear;
        this.diaryMonth = diaryMonth;
        this.diaryDay = diaryDay;
    }

    public int userIdx;
    public String diaryContent;
    public int diaryYear;
    public int diaryMonth;
    public int diaryDay;

}
