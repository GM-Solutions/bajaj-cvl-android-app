package co.gladminds.bajajcvl.Common;
/*
 *Android developer 
 *author by: Koushal rathor(koushalrathor@gmail.com) 
 *
 *   First Web Development 
 *
 */


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


@SuppressLint("SimpleDateFormat")
public class Common {
    // this dialog used for progress dialog

    public static String rslt = "";
    static SharedPreferences sharedpreferences;
    public static final String UserId = "useridkey";
    private static Dialog dialog;
    static String[] formatStrings = {"dd/MM/yyyy", "dd-MMM-yy", "M-d-y"};

        public static String mainurl = "http://124.153.104.69:8059/api/";//QA
//
    //    public static String mainurl="http://124.153.104.69:8061/api/";//Prod
//    public static String mainurl = "http://bajajcvloyalty.gladminds.co/api/";//Prod

    //public static AlbumsAdapter adapter;
    //public static List<Album> product_list;
    //public static List<MenuGetSet> menulist;
    //public static Album menu_item ;
    //public static DataBaseHandlerPierrofino db;
    /****************   Siganature pad work ***********************/

    public static Button mClear, mGetSign, mCancel;
    public static RelativeLayout mContent;
    public static ImageView image;

    public static View mView;
    public static Bitmap mBitmap = null;
    static int RESULT_OK = 100;
    public static Boolean Dialog_pop = false;

    /************************************************** check internet connection ******************************************************/
    public static Boolean isInternetOn(Context con) {
        ConnectivityManager connectivityManager;
        NetworkInfo wifiInfo, mobileInfo;
        boolean connected = false;
        try {
            connectivityManager = (ConnectivityManager) con
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            connected = true;
                        }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return connected;
    }
/*************************************End check internet connection*************************************/


    /************************************************/
    /***************************************************** set shared preferences **************************************************/
    public static void SetPreferences(Context con, String key, String value) {
        // save the data
        SharedPreferences preferences = con.getSharedPreferences(
                "FirstWebDevelopement_pref", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**************************************************** get shared preferences ***************************************************/
    public static String getPreferences(Context con, String key) {
        SharedPreferences sharedPreferences = con.getSharedPreferences(
                "FirstWebDevelopement_pref", 0);
        String value = sharedPreferences.getString(key, "0");
        return value;

    }


    /**************************************************** show toast msg **********************************************************/
    public static void showToast(Context cont, String msg, int duration) {
        Toast.makeText(cont, msg, duration).show();
    }

    /******************************************************************************************************************************
     * used to set activity at bottom (activity set bottom when we used activity
     * as dialog box and for set activity as dialog we set theme in activity in
     * manifest file )
     ******************************************************************************************************************************/
    public static void setActivityAtBottom(Activity act) {
        WindowManager.LayoutParams winlayp = act.getWindow().getAttributes();
        winlayp.gravity = Gravity.BOTTOM;
    }


    /***************************************** Get Current Days*************************************************/

    public static String getCurrentDate() {

        // String cuurent_date = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss")
        String cuurent_date = new SimpleDateFormat("dd-MM-yyyy")
                .format(Calendar.getInstance().getTime());
        //  Date date=Calendar.getInstance().getTime();
        System.out.println("cuurent_date" + cuurent_date);

        return cuurent_date;
    }


    public static String getCurrentDateone() {

        // String cuurent_date = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss")
        String cuurent_date = new SimpleDateFormat("yyyy-MM-dd")
                .format(Calendar.getInstance().getTime());
        //  Date date=Calendar.getInstance().getTime();
        System.out.println("cuurent_date" + cuurent_date);

        return cuurent_date;
    }


    public static String setuploaddate() {

        // String cuurent_date = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss")
        String cuurent_date = new SimpleDateFormat("yyyy-MM-dd")
                .format(Calendar.getInstance().getTime());

        //  Date date=Calendar.getInstance().getTime();
        System.out.println("cuurent_date and time is  " + cuurent_date);

        return cuurent_date;

    }


    /***************************************  Add Days  **************************************************************/
    public static Date addDay(Date date, int i) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, i);

        return cal.getTime();

    }


    /***************************************************** hide progress dialog *****************************************************/
    public static void hideProgressDialog1() {
        // hide dialog
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        }

    }

    /******************************************************************************************************************/

    /*********************** string convert to date ***********************************/


    /**************************************** Get Cover Days Data **************************************/
    public static Date StringConvertDate(Context con, String str) {

        for (String formatString : formatStrings) {
            try {
                try {
                    return new SimpleDateFormat(formatString).parse(str);

                } catch (java.text.ParseException e) {
                    // TODO Auto-generated catch block
                    //e.printStackTrace();
                }
            } catch (ParseException e) {

            }
        }
        return null;
    }

    /**************************************** Get Cover Days Data **************************************/
    public static Date StringToConvertDate(String str) {

        for (String formatString : formatStrings) {
            try {
                try {
                    return new SimpleDateFormat(formatString).parse(str);

                } catch (java.text.ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (ParseException e) {

            }
        }
        return null;
    }

    /*****************************************************************************************************/
    public static boolean StringToBool(String s) {
        if (s.equals("1") || s.equalsIgnoreCase("true"))
            return true;
        else if (s.equals("0") || s.equalsIgnoreCase("false"))
            return false;
        else
            return false;

    }


    /////////////////////////////////

    /*****************************************************************************************************/
    public static String BoolToString(Boolean bol) {
        if (bol.equals(true))
            return "1";
        else
            return "0";

    }


    /***************************************** Get Current Days*************************************************/

    public static String getCurrentDate_dowenload() {

        // String cuurent_date = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss")
        String cuurent_date = new SimpleDateFormat("dd/MM/yyyy")
                .format(Calendar.getInstance().getTime());

        //  Date date=Calendar.getInstance().getTime();
        System.out.println("cuurent_date and time is  " + cuurent_date);

        return cuurent_date;

    }


    /***************************************************************************************************************/

    public static String DateConvertString(Date dt) {
        String reportDate;
        try {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            reportDate = df.format(dt);
        } catch (NullPointerException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            reportDate = "";

        }

        return reportDate;
    }


    /*********************************************  String dtae convert date*****************************************/

    public static Date setStringToDateformate(String date_st) {


        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date dt = null;
        try {
            dt = formatter.parse(date_st);
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return dt;
    }


    /************************************************ Convert Bitmap to Byte Array *************************************************/
    public static byte[] BitmapToByteArray(Bitmap bitmap) {
        byte[] byte_arr;
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte_arr = stream.toByteArray();
            return byte_arr;
        }
        return null;
    }

    /************************************************ Convert Byte Array To Bitmap ************************************************/
    public static Bitmap ByteArrayToBitmap(byte[] byte_array) {
        return BitmapFactory.decodeByteArray(byte_array, 0, byte_array.length);
    }


    /************************************************* Bitmap to String *****************************************************/
    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] arr = baos.toByteArray();
        String result = Base64.encodeToString(arr, Base64.DEFAULT);
        return result;
    }

    /************************************************* String to Bitmap *****************************************************/
    public static Bitmap StringToBitMap(String image) {
        try {
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    /*********************************************** get current time ***********************************************/
    public static String getCurrentTime() {
        String cuurent_time = new SimpleDateFormat("HH:mm:ss").format(Calendar
                .getInstance().getTime());

        return cuurent_time;
    }


    public static boolean checktimings(String time, String endtime) {

        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if (date1.before(date2)) {
                return true;
            } else {

                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean compare_date(Context context, String new_date) {


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date date1 = sdf.parse(getCurrentDate());
            Date date2 = sdf.parse(new_date);

            if (date1.before(date2)) {
                return true;
            } else if (date1.equals(date2)) {
                Common.SetPreferences(context, "TODAY", "TODAY");
                return true;
            } else {

                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /***************************************************** hide progress dialog *****************************************************/
    public static void hidePreloader() {
        // hide dialog
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        }

    }


    /************************************************* create log file in sdcard *****************************************************/

    public static void appendExeption(String text) {
        File logFile = new File("sdcard/ClikElecCert_Error.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            text = Common.getCurrentDate() + " =>\n" + text;
            // BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
                    true));
            buf.append("\n" + text);
            buf.newLine();
            buf.close();
            // App.AnalyticsTracker.sendView(text);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /********************************************** Hide Keyboard ******************************************/
    public static void hide_keyboard(View view, Context context) {

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /********************************************** Is numaric not ******************************************/

    public static boolean isNumeric(String str) {
        try {
            int i = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }


    /**************************************** Hide Keyboard outside edittexts **********************************************/

    public static void setupUI(final View view, final Context context) {

        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {

            view.setOnTouchListener(new OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hide_keyboard(view, context);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);


                setupUI(innerView, context);
            }
        }
    }


    public static java.sql.Date convertDATe(Date uDate) {
        java.sql.Date sDate = new java.sql.Date(uDate.getTime());
        return sDate;
    }


    public static String NumberFormatData(String str) {
        // TODO Auto-generated method stub

        if (Common.isNumeric(str)) {
            return str;
        } else
            return "-1";
    }

    public static String IntegerNullParser(String str) {
        // TODO Auto-generated method stub

        if (str.equalsIgnoreCase("-1") || str.equalsIgnoreCase("0")) {
            return "";
        } else
            return str;
    }


    /**
     * Returns the User device name
     */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

    /*********************************************/

    /*************************************************** show progress dialog *******************************************************/
/*	public static void showProgressDialog(Context context_dilog, String Title) {
        // check privious dialog

		dialog= new Dialog(context_dilog);

		if (dialog != null || dialog.isShowing()) {
			dialog.dismiss();
		}

			*//* dialog.setCancelable(false);
             dialog.setCanceledOnTouchOutside(false);*//*
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable((new ColorDrawable(android.graphics.Color.TRANSPARENT)));
		dialog.setContentView(R.layout.prgress_bar_layout);
		TextView title = (TextView)dialog.findViewById(R.id.tv_info);
		TextView tv_wait= (TextView)dialog.findViewById(R.id.tv_wait);

		Typeface typeFace_medium = Typeface.createFromAsset(context_dilog.getAssets(), "FREESCPT_0.TTF");
		tv_wait.setTypeface(typeFace_medium);

		title.setText(Title);
		dialog.setCancelable(true);
		dialog.show();

	}*/

    /***************************************************** hide progress dialog *****************************************************/
    public static void hideProgressDialog() {
        // hide dialog
            /*dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);*/
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        }

    }

    ////////////////////////////////// remove whitespace from email //////////////
    public static boolean containsWhiteSpace(final String testCode) {
        if (testCode != null) {
            for (int i = 0; i < testCode.length(); i++) {
                if (Character.isWhitespace(testCode.charAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    ////////////////////////////////// Dialog One Button //////////////
    /*public static void DialogOneBtn(Context context, String title_text, String info_text) {
        final Dialog dialogOneBtn = new Dialog(context);
		dialogOneBtn.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//dialogTwoBtn.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		dialogOneBtn.getWindow().setBackgroundDrawable((new ColorDrawable(android.graphics.Color.TRANSPARENT)));
		dialogOneBtn.setContentView(R.layout.dialog_box_single_pierrofino);


		TextView title = (TextView) dialogOneBtn.findViewById(R.id.tv_title);
		TextView info = (TextView) dialogOneBtn.findViewById(R.id.tv_info);
		Button btnOK = (Button) dialogOneBtn.findViewById(R.id.ok_button);

		title.setText(""+title_text);
		info.setText(""+info_text);

		btnOK.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialogOneBtn.dismiss();
			}
		});


		dialogOneBtn.show();


	}
*/
    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static float roundTwoDecimals(float d) {
        //Log.e("to be round", ""+d);

        DecimalFormat twoDForm = new DecimalFormat("#.##");
        //Log.e("twoDForm", ""+twoDForm.format(d));
        String abc = "" + twoDForm.format(d);
        if (abc.contains(",")) {
            return Float.valueOf((twoDForm.format(d)).replace(",", "."));
        } else {
            return Float.valueOf((twoDForm.format(d)));
        }
    }

    ////////////////////////////////// get Day of Year //////////////
    public static int getDayOfYear(Context context) {
        Calendar cal = Calendar.getInstance();
        int currentWeekOfYear = cal.get(Calendar.DAY_OF_YEAR);
        return currentWeekOfYear;

    }


    public static void Customtoast(Activity activity, String msg) {
        LayoutInflater li = activity.getLayoutInflater();
        //Getting the View object as defined in the customtoast.xml file
        View convertView = li.inflate(co.gladminds.bajajcvl.R.layout.custom_toast, null);
        TextView textView = (TextView) convertView.findViewById(co.gladminds.bajajcvl.R.id.custom_toast_message);
        textView.setText(msg);
        Toast toast = new Toast(activity);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 220);
        toast.setView(convertView);
        toast.show();
    }

    public static String deDup(String s) {
        return s.replaceAll("(\\b\\w+\\b)-(?=.*\\b\\1\\b)", "");
    }


}

