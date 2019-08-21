package sm.finalproject.com.final_project_android.model;

import org.w3c.dom.Text;

public class PostLastDiaryResponseData {

    public PostLastDiaryResponseData(int userIdx, String diaryContent, String diaryDate, String diaryHashTag) {
        this.userIdx = userIdx;
        this.diaryContent = diaryContent;
        this.diaryDate = diaryDate;
        this.diaryHashTag = diaryHashTag;
    }

    public int userIdx;
    public String diaryContent;
    public String diaryDate;
    public String diaryHashTag;

}
