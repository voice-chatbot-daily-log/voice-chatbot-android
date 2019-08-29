package sm.finalproject.com.final_project_android.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceController {
    private static final String USER_IDX = "user_idx";
    private static final String USER_FLAG = "user_flag";

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

    public static void setUserFlag(Context context, int flag){
        SharedPreferences pref = context.getSharedPreferences(USER_FLAG,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(USER_FLAG,flag);
        editor.commit();
    }

    public static int getUserFlag(Context context){
        SharedPreferences pref = context.getSharedPreferences(USER_FLAG,Context.MODE_PRIVATE);
        int user_flag = pref.getInt(USER_FLAG,0);
        return user_flag;
    }

}
