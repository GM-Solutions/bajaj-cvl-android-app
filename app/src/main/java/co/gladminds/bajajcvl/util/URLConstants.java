package co.gladminds.bajajcvl.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ANDROID on 12-01-2017.
 */

public class URLConstants {
    public static Context context;
    public static final String BASE_API_URL = "https://book.zealheal.com/api/";
    public static final String LOGIN_URL = BASE_API_URL + "v1/otpsms/";


    public static final String SEARCH_URL = "https://book.zealheal.com/api/v1/search";
    //public static final String PUB_KEY_NO_LOGIN = "89e495e7941cf9e40e6980d14a16bf023ccd4c91";
    public static final String PUB_KEY_NO_LOGIN = "0f307c12a72c7ed2e1d60847f2c9564017221120";

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean permissionForMemory() {
        String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static boolean permissonForCalender() {
        String permission = "android.permission.WRITE_CALENDAR";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static boolean permissonForReadCalender() {
        String permission = "android.permission.READ_CALENDAR";
        int res = context.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static final String Host = "http//124.153.104.69:8059/api/";

    public static final int INVALID = 0;
    public static final int VALID = 1;
    public static final int USED = 2;

}
