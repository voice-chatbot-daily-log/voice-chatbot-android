package sm.finalproject.com.final_project_android.model;

public class DeleteLastDiaryResponseData {
    public DeleteLastDiaryResponseData(int userIdx, String diaryDate, int flag) {
        this.userIdx = userIdx;
        this.diaryDate = diaryDate;
        this.flag = flag;
    }

    public int userIdx;
    public String diaryDate;
    public int flag;

}
