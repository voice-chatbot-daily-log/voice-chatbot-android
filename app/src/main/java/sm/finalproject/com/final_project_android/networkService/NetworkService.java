package sm.finalproject.com.final_project_android.networkService;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import sm.finalproject.com.final_project_android.lastdiary.data.LastDiaryData;
import sm.finalproject.com.final_project_android.model.GetLastDiaryResponse;
import sm.finalproject.com.final_project_android.model.PostChatResponse;
import sm.finalproject.com.final_project_android.model.PostChatResponseData;
import sm.finalproject.com.final_project_android.model.PostLastDiaryResponse;
import sm.finalproject.com.final_project_android.model.PostLastDiaryResponseData;
import sm.finalproject.com.final_project_android.model.QueryInput;

public interface NetworkService {

    @POST(" ")
    Call<PostChatResponse>postChat(@Header("Authorization") String header,
                                   @Body QueryInput queryInput);

    @GET("/diary/lastdiarylist/{userIdx}")
    Call<GetLastDiaryResponse>getLastDiary(@Path("userIdx") int userIdx);

    @POST("/diary/save")
    Call<PostLastDiaryResponse>postLastDiary(@Body PostLastDiaryResponseData postLastDiaryResponseData);


}
