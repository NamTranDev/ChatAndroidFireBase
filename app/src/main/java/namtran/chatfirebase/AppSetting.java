package namtran.chatfirebase;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSetting {

    /**
     * normal configurations
     */
    public static final String SHARED_REFERENCE_NAME = "CHATFIREBASE_CONFIG";

    /**
     * set if user saved name
     * @param context
     * @param username
     */
    public static void setUserName(Context context, String username){
        SharedPreferences.Editor editor = context.getSharedPreferences(
                SHARED_REFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("ChonLuyenTapTheoThoiGian", username);
        editor.commit();
    }

    /**
     * get value from SharedPreferences . Default ""
     * @param context
     * @return
     */
    public static String getUserName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                SHARED_REFERENCE_NAME, Context.MODE_PRIVATE);
        return prefs.getString("ChonLuyenTapTheoThoiGian", "");
    }
}
