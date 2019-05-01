package sm.finalproject.com.final_project_android.networkService;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import sm.finalproject.com.final_project_android.model.PostChatResponse;
import sm.finalproject.com.final_project_android.model.QueryInput;

public interface NetworkService {

    @POST(" ")
    Call<PostChatResponse>postChat(@Header("Authorization") String header,
                                   @Body QueryInput queryInput);

}
