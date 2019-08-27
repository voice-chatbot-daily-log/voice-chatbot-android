package sm.finalproject.com.final_project_android.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceController {
    private static final String USER_IDX = "user_idx";

    public static void setUserIdx(Context context, int user_idx){
        SharedPreferences pref = context.getSharedPreferences(USER_IDX,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(USER_IDX,user_idx);
        editor.commit();
    }

    public static int getUserIdx(Context context){
        SharedPreferences pref = context.getSharedPreferences(USER_IDX,Context.MODE_PRIVATE);
        int user_idx = pref.getInt(USER_IDX,0);
        return user_idx;
    }
}
