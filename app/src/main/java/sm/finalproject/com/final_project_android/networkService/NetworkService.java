package sm.finalproject.com.final_project_android.networkService;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import sm.finalproject.com.final_project_android.lastdiary.data.LastDiaryData;
import sm.finalproject.com.final_project_android.model.DeleteLastDiaryResponseData;
import sm.finalproject.com.final_project_android.model.GetLastDiaryResponse;
import sm.finalproject.com.final_project_android.model.PostChatResponse;
import sm.finalproject.com.final_project_android.model.PostChatResponseData;
import sm.finalproject.com.final_project_android.model.PostLastDiaryResponse;
import sm.finalproject.com.final_project_android.model.PostLastDiaryResponseData;
import sm.finalproject.com.final_project_android.model.PostUserUUIDResponse;
import sm.finalproject.com.final_project_android.model.PostUserUUIDResponseData;
import sm.finalproject.com.final_project_android.model.QueryInput;

public interface NetworkService {

    @POST(" ")
    Call<PostChatResponse>postChat(@Header("Authorization") String header,
                                   @Body QueryInput queryInput);

    @GET("/diary/lastdiarylist/{userIdx}")
    Call<GetLastDiaryResponse>getLastDiary(@Path("userIdx") int userIdx);

    @POST("/diary/save")
    Call<PostLastDiaryResponse>postLastDiary(@Body PostLastDiaryResponseData postLastDiaryResponseData);

    @GET("/diary/search/{userIdx}/hashtag/{diaryHashTag}")
    Call<GetLastDiaryResponse>getLastDiaryByHashTag(@Path("userIdx") int userIdx,
                                                    @Path("diaryHashTag") String diaryHashTag);

    @GET("/diary/search/{userIdx}/date/{diaryDate}")
    Call<GetLastDiaryResponse>getLastDiaryByDate(@Path("userIdx") int userIdx,
                                                    @Path("diaryDate") String date);

    @POST("/user/signup")
    Call<PostUserUUIDResponse>postUserUUID(@Body PostUserUUIDResponseData postUserUUIDResponseData);

    @HTTP(method = "DELETE", path = "/diary", hasBody = true)
    Call<GetLastDiaryResponse>deleteLastDiary(@Body DeleteLastDiaryResponseData deleteLastDiaryResponseData);



}
