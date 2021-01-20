package code.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.kmcapp.android.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import code.algo.SyncAllRecord;
import code.checkIn.CheckInActivity;
import code.checkOut.CheckOutActivity;
import code.common.GetBackFragment;
import code.common.LocaleHelper;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.main.MainActivity;
import code.view.BaseActivity;

public class AppUtils {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    public static Toast mToast;
    static ProgressDialog progressDialog;

    public static float convertDpToPixel(float dp) {
        return dp * (((float) Resources.getSystem().getDisplayMetrics().densityDpi) / 160.0f);
    }

    public static float convertPixelsToDp(float px) {
        return px / (((float) Resources.getSystem().getDisplayMetrics().densityDpi) / 160.0f);
    }

    public static String print(String mString) {
        return mString;
    }

    public static String printD(String Tag, String mString) {
        return mString;
    }

    public static String printE(String Tag, String mString) {
        return mString;
    }

    public static int startPosition(String word, String sourceString) {
        int startingPosition = sourceString.indexOf(word);
        print("startingPosition" + word + " " + startingPosition);
        return startingPosition;
    }

    public static int endPosition(String word, String sourceString) {
        int endingPosition = sourceString.indexOf(word) + word.length();
        print("startingPosition" + word + " " + endingPosition);
        return endingPosition;
    }

    public static void showToastSort(Context context, String text) {
        String newText = text;
        if (!text.isEmpty()) {
            if (newText.equalsIgnoreCase("Invalid Password")) {
                newText = context.getString(R.string.invalidPassword);
            }
            if (mToast != null && mToast.getView().isShown()) {
                mToast.cancel();
            }
            mToast = Toast.makeText(context, newText, Toast.LENGTH_LONG);
            mToast.show();
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity != null) {
            try {
                @SuppressLint("WrongConstant") InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService("input_method");
                View view = activity.getCurrentFocus();
                if (view != null) {
                    IBinder binder = view.getWindowToken();
                    if (binder != null) {
                        inputMethodManager.hideSoftInputFromWindow(binder, 0);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static float convertDpToPixel(float dp, Context context) {
        return (((float) getDisplayMetrics(context).densityDpi) / 160.0f) * dp;
    }

    public static int convertDpToPixelSize(float dp, Context context) {
        float pixels = convertDpToPixel(dp, context);
        int res = (int) (0.5f + pixels);
        if (res != 0) {
            return res;
        }
        if (pixels == 0.0f) {
            return 0;
        }
        if (pixels > 0.0f) {
            return 1;
        }
        return -1;
    }

    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPhone(String pass) {
        return pass != null && pass.length() == 10;
    }


    public static void setCustomFont(Activity mActivity, TextView mTextView, String asset) {
        mTextView.setTypeface(Typeface.createFromAsset(mActivity.getAssets(), asset));
    }

    public static void showRequestDialog(Activity activity) {

        Log.d("Token-Number", AppSettings.getString(AppSettings.token));

        AppUtils.hideDialog();

        try {
            if (!((Activity) activity).isFinishing()) {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(activity);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage(activity.getString(R.string.pleaseWait));
                    progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                    progressDialog.show();
                } else {
                    progressDialog = new ProgressDialog(activity);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage(activity.getString(R.string.pleaseWait));
                    progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                    progressDialog.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void showRequestDialog(Activity activity, String message) {
        if (progressDialog == null) {
            //progressDialog = new ProgressDialog(activity, R.style.MyAlertDialogStyle);
            progressDialog = new ProgressDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(message);
            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            progressDialog.show();
        }
    }

    public static void hideDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getTncDate() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        return df.format(new Date());
    }

    /*public static void showErrorMessage(View mView, String errorMessage, Context mActivity) {
        Snackbar snackbar = Snackbar.make(mView, errorMessage, Snackbar.LENGTH_SHORT);
        TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
        *//*Typeface font = Typeface.createFromAsset(mActivity.getAssets(), "centurygothic.otf");
        tv.setTypeface(font);*//*

        snackbar.show();
    }*/


    public static String toCamelCaseSentence(String s) {
        if (s == null) {
            return "";
        }
        String[] words = s.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String toCamelCaseWord : words) {
            sb.append(toCamelCaseWord(toCamelCaseWord));
        }
        return sb.toString().trim();
    }

    public static String toCamelCaseWord(String word) {
        if (word == null) {
            return "";
        }
        switch (word.length()) {
            case 0:
                return "";
            case 1:
                return word.toUpperCase(Locale.getDefault()) + " ";
            default:
                return Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase(Locale.getDefault()) + " ";
        }
    }

    public static String split(String str) {
        String result = "";
        if (str.contains(" ")) {
            return toCamelCaseWord(str.split("\\s+")[0]);
        }
        return toCamelCaseWord(str);
    }

    public static void expand(final View v) {
        v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? WindowManager.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {

        //v.setVisibility(View.GONE);

        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    /**
     * Returns the unique identifier for the device
     *
     * @return unique identifier for the device
     */
    public static String getDeviceIMEI(Context context) {
        String deviceUniqueIdentifier = "";
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the tableName grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                return deviceUniqueIdentifier;
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    try {
                        deviceUniqueIdentifier = String.valueOf(tm.getImei());
                    } catch (Exception e) {
                        deviceUniqueIdentifier = getDeviceID(context);
                        e.printStackTrace();
                    }
                } else {
                    deviceUniqueIdentifier = String.valueOf(tm.getDeviceId());
                }
            }
        }
       /* if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }*/
        return deviceUniqueIdentifier;
    }

    // GetDeviceId
    public static String getDeviceID(Context ctx) {
        return Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getDateCurrentTimeZone(long timestamp) {

        timestamp = timestamp * 1000;

        DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy, hh:mm aa");

        //System.out.println(timestamp);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        //System.out.println(formatter.format(calendar.getTime()));

        String ret = formatter.format(calendar.getTime());

        return ret;
    }

    public static String getDateFromTimestamp(long timestamp) {

        DateFormat formatter = new SimpleDateFormat("dd MMM hh:mm");

        //System.out.println(timestamp);

        Calendar calendar = Calendar.getInstance();
        if (timestamp < 1000000000000L) {
            calendar.setTimeInMillis(timestamp * 1000);
        }
        //System.out.println(formatter.format(calendar.getTime()));

        String ret = formatter.format(calendar.getTime());

        return ret;
    }

    public static String getTimeLineDate(long timestamp) {

        DateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");

        //System.out.println(timestamp);

        Calendar calendar = Calendar.getInstance();
        if (timestamp < 1000000000000L) {
            calendar.setTimeInMillis(timestamp * 1000);
        }
        //System.out.println(formatter.format(calendar.getTime()));

        String ret = formatter.format(calendar.getTime());

        return ret;
    }

    public static String getTimeLineTime(long timestamp) {

        DateFormat formatter = new SimpleDateFormat("hh:mm aa");

        //System.out.println(timestamp);

        Calendar calendar = Calendar.getInstance();
        if (timestamp < 1000000000000L) {
            calendar.setTimeInMillis(timestamp * 1000);
        }
        //System.out.println(formatter.format(calendar.getTime()));

        String ret = formatter.format(calendar.getTime());

        return ret;
    }

    public static String getTimeFromDate(String dateTime) {

        String result = "";

        String[] time = dateTime.split(" ");

        return time[1];
    }

    public static String getTimeFromTimestamp(long timestamp) {

        DateFormat formatter = new SimpleDateFormat("hh:mm aa");

        //System.out.println(timestamp);

        Calendar calendar = Calendar.getInstance();
        if (timestamp < 1000000000000L) {
            calendar.setTimeInMillis(timestamp * 1000);
        }
        //System.out.println(formatter.format(calendar.getTime()));

        String ret = formatter.format(calendar.getTime());

        return ret;
    }

    public static String getCurrentDate() {

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current Date => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
        String formattedDate = df.format(c);

        return formattedDate;
    }


    public static String getYesterdayDate() {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        Date c = calendar.getTime();
        System.out.println("Current Date => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy");
        String formattedDate = df.format(c);

        return formattedDate;
    }


    public static String getCurrentDateFormatted() {

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current Date => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);

        return formattedDate;
    }

    public static String getCurrentMonth() {

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current Date => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);

        String[] parts = formattedDate.split("-");

        formattedDate = parts[0] + "-" + parts[1] + "-" + "01 00:00:00";

        return formattedDate;
    }

    public static String getOnlyCurrentMonth() {

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current Date => " + c);

        SimpleDateFormat df = new SimpleDateFormat("MMMM yyyy");
        String formattedDate = df.format(c);

        return formattedDate;
    }

    public static String getCurrentTime() {

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current Time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String formattedDate = df.format(c);

        return formattedDate;
    }

    public static String getCurrentTimeIn12Hour() {

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current Time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("hh:mm aa");
        String formattedDate = df.format(c);

        return formattedDate;
    }

    public static String getCurrentDateNew() {

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);

        return formattedDate;
    }


    public static String getCurrentDateTime() {

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yy hh:mm aa");
        String formattedDate = df.format(c);

        return formattedDate;
    }

    public static String getCurrentDateYMD(int addDays) {

        Calendar mcurrentDate = Calendar.getInstance();
        mcurrentDate.add(Calendar.DAY_OF_MONTH, addDays);

        Date c = mcurrentDate.getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c);

        return formattedDate;
    }

    public static String addDay(String oldDate, int numberOfDays) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(dateFormat.parse(oldDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DAY_OF_YEAR, numberOfDays);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = new Date(c.getTimeInMillis());
        String resultDate = dateFormat.format(newDate);
        return resultDate;
    }

    public static String getCurrentDateYMDHMS(int addDays) {

        Calendar mcurrentDate = Calendar.getInstance();
        mcurrentDate.add(Calendar.DAY_OF_MONTH, addDays);

        Date c = mcurrentDate.getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String formattedDate = df.format(c);

        return formattedDate;
    }

    public static String getCurrentDateDMY(int addDays) {

        Calendar mcurrentDate = Calendar.getInstance();
        mcurrentDate.add(Calendar.DAY_OF_MONTH, addDays);

        Date c = mcurrentDate.getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        //SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c);

        return formattedDate;
    }

    public static String getNewDateTimeFromTimestamp(long timestamp) {

        //DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        System.out.println(timestamp);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp * 1000);
        //System.out.println(formatter.format(calendar.getTime()));

        String ret = formatter.format(calendar.getTime());

        return ret;
    }

    public static String parseDateToFormat(String time) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "dd-MMM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseDateToDMYFormat(String oldDate) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(oldDate);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getDateTimeFromTimestampNew(long timestamp) {

        DateFormat formatter = new SimpleDateFormat("dd-MM-yy hh:mm aa");

        System.out.println(timestamp);

        Calendar calendar = Calendar.getInstance();

        if (timestamp < 1000000000000L) {
            calendar.setTimeInMillis(timestamp * 1000);
        } else {
            calendar.setTimeInMillis(timestamp);
        }

        System.out.println(formatter.format(calendar.getTime()));

        String ret = formatter.format(calendar.getTime());

        return ret;
    }

    public static String getDateTimeFromTimestamp(long timestamp) {

        DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm aa");

        System.out.println(timestamp);

        Calendar calendar = Calendar.getInstance();

        if (timestamp < 1000000000000L) {
            calendar.setTimeInMillis(timestamp * 1000);
        } else {
            calendar.setTimeInMillis(timestamp);
        }

        System.out.println(formatter.format(calendar.getTime()));

        String ret = formatter.format(calendar.getTime());

        return ret;
    }

    public static String covertTimeToText(long createdAt) {
        DateFormat userDateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
        DateFormat dateFormatNeeded = new SimpleDateFormat("MM/dd/yyyy HH:MM:SS");
        Date date = null;
        date = new Date(createdAt);
        String crdate1 = dateFormatNeeded.format(date);

        // Date Calculation
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        crdate1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(date);

        // get current date time with Calendar()
        Calendar cal = Calendar.getInstance();
        String currenttime = dateFormat.format(cal.getTime());

        Date CreatedAt = null;
        Date current = null;
        try {
            CreatedAt = dateFormat.parse(crdate1);
            current = dateFormat.parse(currenttime);
        } catch (ParseException e) {
            // TODO Auto-generated catch tableName
            e.printStackTrace();
        }

        // Get msec from each, and subtract.
        long diff = current.getTime() - CreatedAt.getTime();
        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        String time = null;
        if (diffDays > 0) {
            if (diffDays == 1) {
                time = diffDays + " day ago ";
            } else {
                time = diffDays + " hours ago ";
            }
        } else {
            if (diffHours > 0) {
                if (diffHours == 1) {
                    time = diffHours + " hr ago";
                } else {
                    time = diffHours + " hrs ago";
                }
            } else {
                if (diffMinutes > 0) {
                    if (diffMinutes == 1) {
                        time = diffMinutes + " min ago";
                    } else {
                        time = diffMinutes + " mins ago";
                    }
                } else {
                    if (diffSeconds > 0) {
                        time = diffSeconds + " secs ago";
                    }
                }

            }

        }
        return time;
    }

    public static String covertTimeToHours(String createdAt) {

        // Date Calculation
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // get current date time with Calendar()
        Calendar cal = Calendar.getInstance();
        String currenttime = dateFormat.format(cal.getTime());

        Date CreatedAt = null;
        Date current = null;
        try {
            CreatedAt = dateFormat.parse(createdAt);
            current = dateFormat.parse(currenttime);
        } catch (ParseException e) {
            // TODO Auto-generated catch tableName
            e.printStackTrace();
        }

        // Get msec from each, and subtract.
        long diff = current.getTime() - CreatedAt.getTime();
        //long diffSeconds = diff / 1000;
        //long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        String time = "0";
        if (diffDays > 0) {
            diffDays = diffDays * 24;
        }

        if (diffHours > 0) {
            if (diffHours == 1) {
                time = String.valueOf(diffHours + diffDays);
            } else {
                time = String.valueOf(diffHours + diffDays);
            }

        }
        return time;
    }

    public static String parseDate(String givenDateString) {
        if (givenDateString.equalsIgnoreCase("")) {
            return "";
        }

        long timeInMilliseconds = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
        try {

            Date mDate = sdf.parse(givenDateString);
            timeInMilliseconds = mDate.getTime();
            System.out.println("Date in milli :: " + timeInMilliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        String result = "0";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");

        String todayDate = formatter.format(new Date());
        Calendar calendar = Calendar.getInstance();

        long dayagolong = timeInMilliseconds;
        calendar.setTimeInMillis(dayagolong);
        String agoformater = formatter.format(calendar.getTime());

        Date CurrentDate = null;
        Date CreateDate = null;

        try {
            CurrentDate = formatter.parse(todayDate);
            CreateDate = formatter.parse(agoformater);

            long different = Math.abs(CurrentDate.getTime() - CreateDate.getTime());

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            if (elapsedDays > 0) {
                elapsedDays = elapsedDays * 24;
            }

            if (elapsedHours > 0) {
                result = String.valueOf(elapsedHours + elapsedDays);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.v("result-Data", result);

        return result;
    }

    public static boolean isEmailValid(String email) {

        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static String getmiliTimeStamp() {

        long LIMIT = 10000000000L;

        long t = Calendar.getInstance().getTimeInMillis();

        return String.valueOf(t).substring(0, 10);
    }

    public static String changeHrFormat(String time) {

        String input = time;
        //Format of the date defined in the input String
        DateFormat df = new SimpleDateFormat("hh:mm aa");
        //Desired format: 24 hour format: Change the pattern as per the need
        DateFormat outputformat = new SimpleDateFormat("HH:mm:ss");
        Date date = null;
        String output = null;
        try {
            //Converting the input String to Date
            date = df.parse(input);
            //Changing the format of date and storing it in String
            output = outputformat.format(date);
            //Displaying the date
            System.out.println(output);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        return output;
    }

    public static String getDifference(String del, String lmp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = sdf.parse(del);
            Date now = sdf.parse(lmp);
            long days = getDateDiff(date, now, TimeUnit.DAYS);
            if (days < 7)
                return days + " Days";
            else
                return days / 7 + " Weeks";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    public static int getWeekDifference(String lmpDate, String delDate) {
        int week = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(lmpDate);
            Date now = sdf.parse(delDate);
            long days = getDateDiff(date, now, TimeUnit.DAYS);
            if (days < 7)
                week = 0;
                //return hours + " Days";
            else
                week = (int) (days / 7);
            //return hours / 7 + " Weeks";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return week;
    }


    public static String getDateTimeDifference(String time1, String time2) {

        String timeDiff = "-1";

        if (!time1.isEmpty() && !time2.isEmpty()) {
            Log.d("time1", time1);
            Log.d("time2", time2);

            time1 = time1.replaceAll(":", "").replaceAll(" ", "").replaceAll("-", "");
            time2 = time2.replaceAll(":", "").replaceAll(" ", "").replaceAll("-", "");

            Long oldTime = Long.parseLong(time2);
            Long newTime = Long.parseLong(time1);

            Long difference = oldTime - newTime;

            Log.d("oldTime", String.valueOf(oldTime));
            Log.d("newTime", String.valueOf(newTime));
            Log.d("difference", String.valueOf(difference));

            if (difference <= 0) {
                timeDiff = "-1";
            } else {
                timeDiff = "1";
            }
        }

        return timeDiff;
    }

    public static String getTimeDifference(String time1, String time2) {

        String timeDiff = "-1";

        if (!time1.isEmpty() && !time2.isEmpty()) {
            Log.d("time1", time1);
            Log.d("time2", time2);

            time1 = AppUtils.convertTimeTo24HoursFormat(time1);
            time2 = AppUtils.convertTimeTo24HoursFormat(time2);

            time1 = time1.replaceAll(":", "");
            time2 = time2.replaceAll(":", "");

            int oldTime = Integer.parseInt(time2);
            int newTime = Integer.parseInt(time1);

            int difference = oldTime - newTime;

            Log.d("oldTime", String.valueOf(oldTime));
            Log.d("newTime", String.valueOf(newTime));
            Log.d("difference", String.valueOf(difference));

            if (difference <= 0) {
                timeDiff = "-1";
            } else {
                timeDiff = "1";
            }
        }

        return timeDiff;
    }

    public static long getSecondsDifference(long time1, long time2) {

        Log.v("ljhs", time1 + "  --  " + time2);

        long diffrence = time2 - time1;

        diffrence = diffrence / 1000;

        Log.v("ljqshjhqs", String.valueOf(diffrence));

        return diffrence;

    }

    public static long getSecondsToMinutes(long seconds) {

        seconds = seconds / 60;

        Log.v("ljqshjhqs", String.valueOf(seconds));

        return seconds;

    }

    public static String getDateAgo(String del) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(del);
            Date now = new Date(System.currentTimeMillis());
            long days = getDateDiff(date, now, TimeUnit.DAYS);
            return days + " Days";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    public static String getDateDifference(String dt) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(dt);
            Date now = new Date(System.currentTimeMillis());
            long days = getDateDiff(date, now, TimeUnit.DAYS);
            long daysDiff = TimeUnit.MILLISECONDS.toDays(days);
            return String.valueOf(daysDiff);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "0";
    }

    public static String getDateDiff(String dt) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(dt);
            Date now = new Date(System.currentTimeMillis());
            long days = getDateDiff(date, now, TimeUnit.DAYS);
            return String.valueOf(days);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "0";
    }

    public static String getDateTimeDiff(String dt1, String dt2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
        try {
            Date delDate = sdf.parse(dt1);
            Date feedDate = sdf.parse(dt2);
            //Date now = new Date(System.currentTimeMillis());
            long days = getDateDiff(delDate, feedDate, TimeUnit.MINUTES);

            Log.d("days", String.valueOf(days));

            //long daysDiff = TimeUnit.MILLISECONDS.toDays(days);
            return String.valueOf(days);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "0";
    }


    public static String getWeightDaysDiff(String dt) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(dt);
            Date now = new Date(System.currentTimeMillis());
            long days = getDateDiff(date, now, TimeUnit.DAYS);
            return String.valueOf(days);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "0";
    }

    private static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static int getAgeFromDOB(String dobDate) {

        int age = 0;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(dobDate);

            try {

                if (dobDate != null) {

                    Date currDate = Calendar.getInstance().getTime();
                    // Log.d("Curr year === "+currDate.getYear()+" DOB Date == "+dobDate.getYear());
                    age = currDate.getYear() - date.getYear();
                    // Log.d("Calculated Age == "+age);
                }

            } catch (Exception e) {
                //Log.d(SyncStateContract.Constants.kApiExpTag, e.getMessage()+ "at Get Age From DOB mehtod.");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(date); // Sat Jan 02 00:00:00 GMT 2010

        return age;

    }

    public static void saveScreenShotsAppCache(Activity context, View view) throws IOException {
        try {
            AppUtils.print("===saveScreenShotsAppCache");
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());

            view.setDrawingCacheEnabled(false);
            File cachePath = new File(context.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            File imagePath = new File(context.getCacheDir(), "images");
            File newFile = new File(imagePath, "image.png");
            Uri contentUri = FileProvider.getUriForFile(context, "com.mncu.android.provider", newFile);
            AppUtils.print("===saveScreenShotsAppCache" + contentUri);
            if (contentUri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                shareIntent.setDataAndType(contentUri, context.getContentResolver().getType(contentUri));
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.shareReportText));
                context.startActivity(Intent.createChooser(shareIntent, "Choose an app"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Current Activity instance will go through its lifecycle to onDestroy() and a new instance then created after it.
     */
    @SuppressLint("NewApi")
    public static final void recreateActivityCompat(final Activity a) {
        GetBackFragment.ClearStack();
        a.recreate();
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static Locale getCurrentLocale(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return activity.getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return activity.getResources().getConfiguration().locale;
        }
    }

    public static long dateDifference(String dob) {
        long day = 0;
        try {
            Date userDob = null;
            try {
                userDob = new SimpleDateFormat("dd-MM-yyyy HH:mm aa").parse(dob);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date today = new Date();
            long diff = today.getTime() - userDob.getTime();
            day = diff / (1000 * 60 * 60 * 24);
        } catch (Exception e) {
            e.printStackTrace();
            return day;
        }

        return day;
    }

    public static long getCurrentTimestamp() {

        return System.currentTimeMillis();
    }

    public static long currentTimestamp() {

        long timestamp = 0;

        Calendar mcurrentDate = Calendar.getInstance();

        // 2) get a java.util.Date from the calendar instance.
        //    this date will represent the current instant, or "now".
        Date now = mcurrentDate.getTime();

        // 3) a java current time (now) instance
        Timestamp currentTimestamp = new Timestamp(now.getTime());

        //timestamp = mcurrentDate.getTimeInMillis();
        timestamp = currentTimestamp.getTime() / 1000L;

        return timestamp;
    }


    public static String currentTimestampFormat() {

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c);

        return formattedDate;
    }


    public static void enableDisable(ViewGroup layout, boolean b) {
        layout.setEnabled(b);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ViewGroup) {
                enableDisable((ViewGroup) child, b);
            } else {
                child.setEnabled(b);
            }
        }
    }

    public static String getDateInFormat() {

        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return sdf.format(date);
    }

    public static String dateToTimestamp(String time) {

        Timestamp ts = null;  //declare timestamp
        Date d = new Date(time); // Intialize date with the string date
        if (d != null) {  // simple null check
            ts = new Timestamp(d.getTime()); // convert gettime from date and assign it to your timestamp.
        }

        return ts.toString();
    }

    public static String changeDateToTimestamp(String time) {

        DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy, hh:mm aa");
        Date date = null;
        try {
            date = formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long output = date.getTime() / 1000L;
        String str = Long.toString(output);
        long timestamp = Long.parseLong(str) * 1000;

        return String.valueOf(timestamp);
    }

    public static String changeDateToTimestamp2(String time) {

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd, hh:mm aa");
        Date date = null;
        try {
            date = (Date) formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long output = date.getTime() / 1000L;
        String str = Long.toString(output);
        long timestamp = Long.parseLong(str) * 1000;

        return String.valueOf(timestamp);
    }

    public static String changeDateToTimestamp3(String time) {

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = (Date) formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long output = date.getTime() / 1000L;
        String str = Long.toString(output);
        long timestamp = Long.parseLong(str) * 1000;

        return String.valueOf(timestamp);
    }


    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }


        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static String getSSTTime(String stDnT, String endSnT) throws ParseException {

        String result = "00:00";

        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date date1 = simpleDateFormat.parse(stDnT);
        Date date2 = simpleDateFormat.parse(endSnT);

        long difference = date2.getTime() - date1.getTime();
        int days = (int) (difference / (1000 * 60 * 60 * 24));
        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
        if (hours <= 0) {
            hours = 0;
        }
        hours = (hours < 0 ? -hours : hours);
        Log.i("======= Hours", " :: " + hours);
        Log.i("======= Mins", " :: " + min);

        if (min < 10) {
            result = hours + ":0" + min;
        } else {
            result = hours + ":" + min;
        }


        /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
        Date startDate = null;
        try {
            startDate = simpleDateFormat.parse(stDnT);
            Log.i("startDate", startDate.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date endDate = null;
        try {
            endDate = simpleDateFormat.parse(endSnT);
            Log.i("endDate", endDate.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long difference = endDate.getTime() - startDate.getTime();
        Log.i("log_tag", "difference: " + difference);

        float days = (difference / (1000 * 60 * 60 * 24));
        float hours = ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
        float min = (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
        Log.i("log_tag", "Hours: " + hours + ", Mins: " + min);*/

        return result;
    }

    public static void SettingLanguageNew(Context context) {

        String languageToLoad = AppSettings.getString(AppSettings.keyLanguageCode); // your language

        LocaleHelper.setLocale(context, languageToLoad);
    }

    public static void SettingLanguage(Context context) {



        /*Locale locale;
        locale = new Locale(languageToLoad);
        Configuration config = new Configuration(context.getResources().getConfiguration());
        Locale.setDefault(locale);
        config.setLocale(locale);

        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());*/

        /*Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,context.getResources().getDisplayMetrics());*/

        /*Resources activityRes = context.getResources();
        Configuration activityConf = activityRes.getConfiguration();
        Locale newLocale = new Locale(languageToLoad);
        activityConf.setLocale(newLocale);
        activityRes.updateConfiguration(activityConf, activityRes.getDisplayMetrics());

        Resources applicationRes = context.getApplicationContext().getResources();
        Configuration applicationConf = applicationRes.getConfiguration();
        applicationConf.setLocale(newLocale);
        applicationRes.updateConfiguration(applicationConf, applicationRes.getDisplayMetrics());*/
    }

    public static String sumTimes(String time1, String time2) {

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Log.d("time1", time1);
        Log.d("time2", time2);

        Date date1 = null;
        try {
            date1 = timeFormat.parse(time1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = timeFormat.parse(time2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long sum = date1.getTime() + date2.getTime();

        String date3 = timeFormat.format(new Date(sum));
        System.out.println("The sum is " + date3);

        return date3;
    }

    public static void deleteDirectory(File path) {
        path.delete();
    }

    public static boolean isValidMobileNo(String number) {
        // The given argument to compile() method
        // is regular expression. With the help of
        // regular expression we can validate mobile
        // number.
        // 1) Begins with 0 or 91
        // 2) Then contains 7 or 8 or 9.
        // 3) Then contains 9 digits
        Pattern p = Pattern.compile("(0/91)?[6-9][0-9]{9}");

        // Pattern class contains matcher() method
        // to find matching between given number
        // and regular expression
        Matcher m = p.matcher(number);
        return (m.find() && m.group().equals(number));
    }

    public static final String md5(String str) {

        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(str.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }

            //Log.v("md5",hexString.toString());
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void setAutoOrientationEnabled(Context context, boolean enabled) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
    }

    public static String getMd5(String result, String breakFrom) {

        String md5String = "";

        String[] separated = result.split(breakFrom);
        String newResult = separated[1];

        String[] separated2 = newResult.split("],");

        String finalResult = separated2[0] + "]";

        Log.d(breakFrom, finalResult);
        Log.d("md5", AppUtils.md5(finalResult));

        md5String = md5(finalResult);

        return md5String;
    }

    /**
     * This method can be check internet connection is available or not.
     *
     * @param mActivity reference of activity.
     * @return
     */
    public static boolean isNetworkAvailable(@NonNull Context mActivity) {

        boolean available = false;
        /** Getting the system's connectivity service */
        ConnectivityManager cm = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        /** Getting active network interface to get the network's staffMobile */
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                available = true;
                AppUtils.print("====activeNetwork" + activeNetwork.getTypeName());
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                available = true;
                AppUtils.print("====activeNetwork" + activeNetwork.getTypeName());
            }
        } else {
            // not connected to the internet
            available = false;
            AppUtils.print("====not connected to the internet");
        }
        /** Returning the staffMobile of the network */
        return available;
    }

    public static Boolean checkServer() {
        if (AppUrls.baseUrl.contains("kmcV2Development")) {
            return true;
        }

        return false;
    }


    //Change Language AlertLanguage
    public static void AlertLanguage(BaseActivity mActivity) {
        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_language);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        TextView tvEnglish = dialog.findViewById(R.id.tvEnglish);
        TextView tvHindi = dialog.findViewById(R.id.tvHindi);
        ImageView ivCross = dialog.findViewById(R.id.ivCross);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        if (AppSettings.getString(AppSettings.keyLanguageCode).equals("en")) {
            tvEnglish.setBackgroundResource(R.drawable.rectangle_teal_round);
            tvHindi.setBackgroundResource(R.drawable.rectangle_grey);
            tvEnglish.setTextColor(mActivity.getResources().getColor(R.color.white));
            tvHindi.setTextColor(mActivity.getResources().getColor(R.color.black));
        } else {
            tvEnglish.setBackgroundResource(R.drawable.rectangle_grey);
            tvHindi.setBackgroundResource(R.drawable.rectangle_teal_round);
            tvHindi.setTextColor(mActivity.getResources().getColor(R.color.white));
            tvEnglish.setTextColor(mActivity.getResources().getColor(R.color.black));
        }

        tvEnglish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                tvEnglish.setBackgroundResource(R.drawable.rectangle_teal_round);
                tvHindi.setBackgroundResource(R.drawable.rectangle_grey);
                tvEnglish.setTextColor(mActivity.getResources().getColor(R.color.white));
                tvHindi.setTextColor(mActivity.getResources().getColor(R.color.black));
                AppSettings.putString(AppSettings.keyLanguageCode, "en");
                AppUtils.recreateActivityCompat(mActivity);
                dialog.dismiss();

            }
        });

        tvHindi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                tvEnglish.setBackgroundResource(R.drawable.rectangle_grey);
                tvHindi.setBackgroundResource(R.drawable.rectangle_teal_round);
                tvHindi.setTextColor(mActivity.getResources().getColor(R.color.white));
                tvEnglish.setTextColor(mActivity.getResources().getColor(R.color.black));
                AppSettings.putString(AppSettings.keyLanguageCode, "hi");
                AppUtils.recreateActivityCompat(mActivity);
                dialog.dismiss();

            }
        });

        ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath) {

        Log.d("videoPath", videoPath);

        return ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
    }

    //Alert with ok button
    public static void AlertOk(String text, BaseActivity mActivity, int type, String title) {
        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_ok);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //ImageView
        ImageView ivImage = dialog.findViewById(R.id.ivImage);

        //TextView
        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        TextView tvTitle = dialog.findViewById(R.id.tvTitle);

        //RelativeLayout
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        ivImage.setVisibility(View.VISIBLE);

        tvMessage.setText(text);


        if (type == 3) {
            ivImage.setVisibility(View.GONE);
            tvTitle.setVisibility(View.VISIBLE);
            if (title.contains("\n")) {
                title = title.replace("\n", " ");
            }
            tvTitle.setText(title);
        }

        rlOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (type == 1) {
                    if (AppUtils.isNetworkAvailable(mActivity)) {
                        SyncAllRecord.postDutyChange(mActivity);
//                        postHelp(mActivity);
                    } else {
                        AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
                    }
                }
            }
        });

        rlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });


    }

    public static void AlertHelpConfirm(String text, BaseActivity mActivity, int type, String title) {
        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_ok);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //ImageView
        ImageView ivImage = dialog.findViewById(R.id.ivImage);

        //TextView
        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        TextView tvTitle = dialog.findViewById(R.id.tvTitle);

        //RelativeLayout
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        ivImage.setVisibility(View.VISIBLE);

        tvMessage.setText(text);


        if (type == 3) {
            ivImage.setVisibility(View.GONE);
            tvTitle.setVisibility(View.VISIBLE);
            if (title.contains("\n")) {
                title = title.replace("\n", " ");
            }
            tvTitle.setText(title);
        }

        rlOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (type == 1) {
                    if (AppUtils.isNetworkAvailable(mActivity)) {
                        SyncAllRecord.postDutyChange(mActivity);
//                        postHelp(mActivity);
                    } else {
                        AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
                    }
                }
            }
        });

        rlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });


    }


    public static void AlertOkMother(String text, BaseActivity mActivity) {
        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_ok);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //ImageView
        ImageView ivImage = dialog.findViewById(R.id.ivImage);

        //TextView
        TextView tvMessage = dialog.findViewById(R.id.tvMessage);

        //RelativeLayout
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        ivImage.setVisibility(View.VISIBLE);
        ivImage.setImageResource(R.drawable.ic_mother);
        ivImage.setColorFilter(mActivity.getResources().getColor(R.color.r_color), PorterDuff.Mode.SRC_IN);

        tvMessage.setText(text);

        rlOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();
                AppSettings.putString(AppSettings.from, "0");
                AppSettings.putString(AppSettings.babyId, "");
                AppSettings.putString(AppSettings.babyAdmissionId, "");
                AppSettings.putString(AppSettings.motherId, "");
                Intent intent = new Intent(mActivity, MainActivity.class);
                mActivity.startActivity(intent);
                mActivity.finishAffinity();
            }
        });
    }

    //Logout Alert Confirmation
    public static void AlertLogoutConfirm(BaseActivity mActivity) {
        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_yes_no);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //ImageView
        ImageView ivImage = dialog.findViewById(R.id.ivImage);

        //TextView
        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        TextView tvOk = dialog.findViewById(R.id.tvOk);

        ivImage.setVisibility(View.VISIBLE);
        ivImage.setImageResource(R.drawable.ic_warning);

        //RelativeLayout
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        tvMessage.setText(mActivity.getString(R.string.logoutAlert));
        tvOk.setText(mActivity.getString(R.string.logout));

        rlCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        rlOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                SyncAllRecord.postLogoutApi(mActivity);

                dialog.dismiss();

            }
        });
    }

    //Change Language Alert Confirmation
    public static void AlertLanguageConfirm(BaseActivity mActivity, String message) {

        if (AppSettings.getString(AppSettings.keyLanguageCode).equals("en") || AppSettings.getString(AppSettings.keyLanguageCode).equals("")) {
            message = message + " in Hindi?";
        } else {
            message = "क्या आप वाकई भाषा को अंग्रेज़ी में बदलना चाहते हैं?";
        }

        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_yes_no);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);

        TextView tvMessage = dialog.findViewById(R.id.tvMessage);

        tvMessage.setText(message);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        rlCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        rlOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (AppSettings.getString(AppSettings.keyLanguageCode).equals("en")) {
                    AppSettings.putString(AppSettings.keyLanguageCode, "hi");
                } else {
                    AppSettings.putString(AppSettings.keyLanguageCode, "en");
                }

                dialog.dismiss();
                GetBackFragment.ClearStack();
                AppUtils.SettingLanguageNew(mActivity);
                AppSettings.putString(AppSettings.from, "0");
                mActivity.startActivity(new Intent(mActivity, MainActivity.class));
                mActivity.finishAffinity();

                //mActivity.finish();
                //mActivity.startActivity(mActivity.getIntent());
            }
        });
    }

    //Change Language Alert Confirmation
    public static void AlertLanguageConfirmForCheckin(BaseActivity mActivity, String message, String flg) {

        if (AppSettings.getString(AppSettings.keyLanguageCode).equals("en") || AppSettings.getString(AppSettings.keyLanguageCode).equals("")) {
            message = message + " in Hindi?";
        } else {
            message = "क्या आप वाकई भाषा को अंग्रेज़ी में बदलना चाहते हैं?";
        }

        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_yes_no);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);

        TextView tvMessage = dialog.findViewById(R.id.tvMessage);

        tvMessage.setText(message);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        rlCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        rlOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (AppSettings.getString(AppSettings.keyLanguageCode).equals("en")) {
                    AppSettings.putString(AppSettings.keyLanguageCode, "hi");
                } else {
                    AppSettings.putString(AppSettings.keyLanguageCode, "en");
                }

                dialog.dismiss();
                GetBackFragment.ClearStack();
                AppUtils.SettingLanguageNew(mActivity);
                AppSettings.putString(AppSettings.from, "0");

                if (flg.equalsIgnoreCase("in")) {
                    mActivity.startActivity(new Intent(mActivity, CheckInActivity.class));
                    mActivity.finish();
                } else {
                    mActivity.startActivity(new Intent(mActivity, CheckOutActivity.class));
                    mActivity.finish();
                }

                //mActivity.finish();
                //mActivity.startActivity(mActivity.getIntent());
            }
        });
    }


    //Dialog Box to close activity by confirmation
    public static void AlertCloseActivity(BaseActivity mActivity, String message) {
        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_yes_no);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //RelativeLayout
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);

        //TextView
        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        tvMessage.setText(message);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        rlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        rlOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (DatabaseController.getNurseIdCheckedInData().size() == 0) {
                    mActivity.startActivity(new Intent(mActivity, CheckInActivity.class));
                    mActivity.finish();
                } else {
                    mActivity.finish();
                }
            }
        });

    }

    public static void AlertCloseCheckActivity(BaseActivity mActivity, String message) {
        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_yes_no);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //RelativeLayout
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);

        //TextView
        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        tvMessage.setText(message);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        rlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        rlOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mActivity.finish();
            }
        });

    }

    public static SpannableStringBuilder setSpannable(String str1, String str2, String str3, BaseActivity mActivity) {

        String text = str1 + " " + str2 + " " + str3;

        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(text);

        Log.d("asdf-1", text + " " + text.length());
        Log.d("asdf-2", str1 + " " + str1.length());
        Log.d("asdf-3", str2 + " " + str2.length());
        Log.d("asdf-4", str3 + " " + str3.length());

        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.blackNew)),
                text.indexOf(str1),
                text.indexOf(str1) + str1.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.oo_color)),
                str1.length(),
                str1.length() + 1 + str2.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        if (!str3.isEmpty()) {
            ssBuilder.setSpan(
                    new ForegroundColorSpan(mActivity.getResources().getColor(R.color.blackNew)),
                    str1.length() + 1 + str2.length(),
                    str1.length() + 1 + str2.length() + 1 + str3.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        return ssBuilder;
    }


    public static SpannableStringBuilder setSpannableMiddleBold(String str1, String str2, String str3, BaseActivity mActivity) {

        String text = str1 + " " + str2 + " " + str3;

        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(text);

        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.black)),
                text.indexOf(str1),
                text.indexOf(str1) + str1.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        ssBuilder.setSpan(new RelativeSizeSpan(1.2f),
                str1.length(),
                str1.length() + 1 + str2.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.oo_color)),
                text.indexOf(str2),
                text.indexOf(str2) + str2.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.black)),
                str1.length() + 1 + str2.length(),
                str1.length() + 1 + str2.length() + 1 + str3.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        return ssBuilder;
    }

    public static SpannableStringBuilder setSpannableFirstLastBold(String str1, String str2, String str3, BaseActivity mActivity) {

        String text = str1 + " " + str2 + " " + str3;

        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(text);

        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.oo_color)),
                text.indexOf(str1),
                text.indexOf(str1) + str1.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        ssBuilder.setSpan(new RelativeSizeSpan(1.2f),
                str1.length(),
                str1.length() + 1 + str2.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.black)),
                text.indexOf(str2),
                text.indexOf(str2) + str2.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.oo_color)),
                str1.length() + 1 + str2.length(),
                str1.length() + 1 + str2.length() + 1 + str3.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        return ssBuilder;
    }


    public static SpannableStringBuilder setSpannableForTime(String mainString,
                                                             String slotSection1,
                                                             String slotSection2,
                                                             String loungeName,
                                                             String time, BaseActivity mActivity) {

        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(mainString);

        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.black)),
                mainString.indexOf(mActivity.getString(R.string.youHaveCheckedIn)),
                mainString.indexOf(mActivity.getString(R.string.youHaveCheckedIn))
                        + mActivity.getString(R.string.youHaveCheckedIn).length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.oo_color)),
                mainString.indexOf(slotSection1),
                mainString.indexOf(slotSection1) + slotSection1.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.black)),
                mainString.indexOf(mActivity.getString(R.string.to)),
                mainString.indexOf(mActivity.getString(R.string.to))
                        + mActivity.getString(R.string.to).length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.oo_color)),
                mainString.indexOf(slotSection2),
                mainString.indexOf(slotSection2) + slotSection2.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.black)),
                mainString.indexOf(loungeName),
                mainString.indexOf(loungeName) + loungeName.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        ssBuilder.setSpan(new RelativeSizeSpan(1.2f),
                mainString.indexOf(time),
                mainString.indexOf(time) + time.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.oo_color)),
                mainString.indexOf(time),
                mainString.indexOf(time) + time.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        return ssBuilder;
    }


    public static SpannableStringBuilder setNewSpannableForTime(String mainString,
                                                                String slotSection1,
                                                                String slotSection2,
                                                                String time, BaseActivity mActivity) {

        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(mainString);


        if (AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en")) {
            ssBuilder.setSpan(
                    new ForegroundColorSpan(mActivity.getResources().getColor(R.color.black)),
                    mainString.indexOf(mActivity.getString(R.string.youHaveCheckedIn)),
                    mainString.indexOf(mActivity.getString(R.string.youHaveCheckedIn))
                            + mActivity.getString(R.string.youHaveCheckedIn).length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        } else {
            ssBuilder.setSpan(
                    new ForegroundColorSpan(mActivity.getResources().getColor(R.color.black)),
                    mainString.indexOf("आपने"),
                    mainString.indexOf("आपने")
                            + "आपने".length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.oo_color)),
                mainString.indexOf(slotSection1),
                mainString.indexOf(slotSection1) + slotSection1.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.black)),
                mainString.indexOf(mActivity.getString(R.string.to)),
                mainString.indexOf(mActivity.getString(R.string.to))
                        + mActivity.getString(R.string.to).length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.oo_color)),
                mainString.indexOf(slotSection2),
                mainString.indexOf(slotSection2) + slotSection2.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        ssBuilder.setSpan(new RelativeSizeSpan(1.2f),
                mainString.indexOf(time),
                mainString.indexOf(time) + time.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.oo_color)),
                mainString.indexOf(time),
                mainString.indexOf(time) + time.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        return ssBuilder;
    }


    public static SpannableStringBuilder setNewSpannableForTimeHindi(String mainString,
                                                                     String slotSection1,
                                                                     String slotSection2,
                                                                     String time, BaseActivity mActivity) {

        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(mainString);


        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.oo_color)),
                mainString.indexOf(slotSection1),
                mainString.indexOf(slotSection1) + slotSection1.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.black)),
                mainString.indexOf(mActivity.getString(R.string.to)),
                mainString.indexOf(mActivity.getString(R.string.to))
                        + mActivity.getString(R.string.to).length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.oo_color)),
                mainString.indexOf(slotSection2),
                mainString.indexOf(slotSection2) + slotSection2.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        ssBuilder.setSpan(new RelativeSizeSpan(1.2f),
                mainString.indexOf(time),
                mainString.indexOf(time) + time.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.oo_color)),
                mainString.indexOf(time),
                mainString.indexOf(time) + time.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        return ssBuilder;
    }


    public static String convertTimeTo24HoursFormat(String oldTime) {

        String time = oldTime;
        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
            Date date = parseFormat.parse(oldTime);
            System.out.println(parseFormat.format(date) + " = " + displayFormat.format(date));

            time = displayFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            time = oldTime;
        }

        return time;
    }


    public static String convertTimeTo12HoursFormat(String oldTime) {

        String time = oldTime;
        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm");
            Date date = parseFormat.parse(oldTime);
            System.out.println(parseFormat.format(date) + " = " + displayFormat.format(date));

            time = displayFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }

    public static String convertDateTimeTo12HoursFormat(String oldTime) {

        String time = oldTime;
        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
            SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = parseFormat.parse(oldTime);
            System.out.println(parseFormat.format(date) + " = " + displayFormat.format(date));

            time = displayFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }

    public static String changeDateFormat(String prev_date) {

        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        Date date = null;
        try {
            date = originalFormat.parse(prev_date);
        } catch (ParseException e) {
            e.printStackTrace();

        }
        String formattedDate = "";
        if (date != null)
            formattedDate = targetFormat.format(date);
        else
            formattedDate = prev_date;

        return formattedDate;
    }

    public static SpannableStringBuilder setSpannable2(String str1, String str2, String str3, Activity mActivity) {

        String text = str1 + " " + str2 + " " + str3;

        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(text);

        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.blackNew)),
                text.indexOf(str1),
                text.indexOf(str1) + str1.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.blackNew)),
                text.indexOf(str2),
                text.indexOf(str2) + str2.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        ssBuilder.setSpan(
                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.oo_color)),
//                new ForegroundColorSpan(mActivity.getResources().getColor(R.color.blackNew)),
                text.indexOf(str3),
                text.indexOf(str3) + str3.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        return ssBuilder;
    }

    public static String timeDialog(final Context context, final TextView textView) {

        final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_time_picker);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        TextView tvHeading = dialog.findViewById(R.id.tvHeading);
        TextView tvOk = dialog.findViewById(R.id.tvOk);
        final NumberPicker npHour = dialog.findViewById(R.id.npHour);
        final NumberPicker npMin = dialog.findViewById(R.id.npMin);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        npHour.setMinValue(1);
        npHour.setMaxValue(12);
        //setNumberPickerTextColor(npHour,context);

        npMin.setMinValue(0);
        npMin.setMaxValue(59);

        npMin.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setNumberPickerTextColor(picker, context);
            }
        });

        npHour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setNumberPickerTextColor(picker, context);
            }
        });

        tvHeading.setText(context.getString(R.string.errorSelectTime));
        tvOk.setText(context.getString(R.string.submit));

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (npHour.getValue() < 10 && npMin.getValue() < 10) {
                    textView.setText("0" + npHour.getValue() + ":0" + npMin.getValue());
                    /*try {
                        BabyRegistationFragment.delTime = textView.getText().toString().trim();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                } else if (npHour.getValue() < 10) {
                    textView.setText("0" + npHour.getValue() + ":" + npMin.getValue());
                    /*try {
                        BabyRegistationFragment.delTime = textView.getText().toString().trim();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                } else if (npHour.getValue() >= 10 && npMin.getValue() < 10) {
                    textView.setText(npHour.getValue() + ":0" + npMin.getValue());
                    /*try {
                        BabyRegistationFragment.delTime = textView.getText().toString().trim();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                } else {
                    textView.setText(npHour.getValue() + ":" + npMin.getValue());
                    /*try {
                        BabyRegistationFragment.delTime = textView.getText().toString().trim();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }

                if (textView.getText().toString().trim().isEmpty()) {
                    AppUtils.showToastSort(context, context.getString(R.string.errorSelectProperTime));
                } else {
                    dialog.dismiss();
                    textView.setText(AlertAdmit(context, npHour.getValue(), npMin.getValue(), textView));
                }

            }
        });

        return textView.getText().toString().trim();
    }

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, Context context) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(context.getResources().getColor(R.color.black));
                    ((EditText) child).setTextColor(context.getResources().getColor(R.color.black));
                    ((EditText) child).setTextSize(R.dimen._9sdp);
                    numberPicker.invalidate();
                    return true;
                } catch (NoSuchFieldException e) {
                    //Log.w("setNumberPickerTextColor", e);
                } catch (IllegalAccessException e) {
                    //Log.w("setNumberPickerTextColor", e);
                } catch (IllegalArgumentException e) {
                    //Log.w("setNumberPickerTextColor", e);
                }
            }
        }
        return false;
    }


    public static String AlertAdmit(final Context context, final int hour, final int min, final TextView textView) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_peher);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        RelativeLayout rlMorning = dialog.findViewById(R.id.rlMorning);
        RelativeLayout rlAfternoon = dialog.findViewById(R.id.rlAfternoon);
        RelativeLayout rlEvening = dialog.findViewById(R.id.rlEvening);
        RelativeLayout rlNight = dialog.findViewById(R.id.rlNight);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        rlMorning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textView.setText(TimeCalculation(hour, min, 1));
                if (textView.getText().toString().trim().isEmpty()) {
                    AppUtils.showToastSort(context, context.getString(R.string.errorSelectProperTime));
                }
                dialog.dismiss();
            }
        });

        rlAfternoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textView.setText(TimeCalculation(hour, min, 2));
                if (textView.getText().toString().trim().isEmpty()) {
                    AppUtils.showToastSort(context, context.getString(R.string.errorSelectProperTime));
                }
                dialog.dismiss();
            }
        });

        rlEvening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textView.setText(TimeCalculation(hour, min, 3));
                if (textView.getText().toString().trim().isEmpty()) {
                    AppUtils.showToastSort(context, context.getString(R.string.errorSelectProperTime));
                }
                dialog.dismiss();
            }
        });

        rlNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textView.setText(TimeCalculation(hour, min, 4));
                if (textView.getText().toString().trim().isEmpty()) {
                    AppUtils.showToastSort(context, context.getString(R.string.errorSelectProperTime));
                }
                dialog.dismiss();
            }
        });

        return textView.getText().toString().trim();
    }

    public static String TimeCalculation(int hour, int min, int type) {
        String newTime = "";
        String mins = "";
        String hours = "";

        if (min < 10) {
            mins = ":0" + min;
        } else {
            mins = ":" + min;
        }

        hours = String.valueOf(hour);
        if (hour < 10) {
            hours = "0" + hour;
        }

        if (type == 1 && hour < 12) {
            newTime = hours + mins + " " + "AM";
        } else if (type == 2 && hour < 12) {
            newTime = hours + mins + " " + "PM";
        } else if (type == 2 && hour == 12) {
            newTime = hours + mins + " " + "PM";
        } else if (type == 3 && hour < 12) {
            newTime = hours + mins + " " + "PM";
        } else if (type == 4 && hour == 12) {
            newTime = hours + mins + " " + "AM";
        } else if (type == 4 && hour < 4) {
            newTime = hours + mins + " " + "AM";
        } else if (type == 4 && hour > 4) {
            newTime = hours + mins + " " + "PM";
        }

        /*try {
            BabyRegistationFragment.delTime = newTime;
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        return newTime;
    }

    public static String dateDialog(Context context, final TextView textView, int type) {
        final String[] times = {""};

        Calendar mcurrentDate = Calendar.getInstance();
        int year = mcurrentDate.get(Calendar.YEAR);
        int month = mcurrentDate.get(Calendar.MONTH);
        int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        if (!textView.getText().toString().isEmpty()) {
            String[] parts = textView.getText().toString().split("-");
            String part1 = parts[2];
            String part2 = parts[1];
            String part3 = parts[0];

            day = Integer.parseInt(part1);
            month = Integer.parseInt(part2);
            year = Integer.parseInt(part3);
        }

        DatePickerDialog mDatePicker1 = new DatePickerDialog(context, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                String dob = String.valueOf(new StringBuilder().append(selectedday).append("-").append(selectedmonth + 1).append("-").append(selectedyear));
                Log.d("dob", dob);
                Log.d("dob", formatDate(selectedyear, selectedmonth, selectedday));
                //AppSettings.putString(AppSettings.from,formatDate(selectedyear,selectedmonth,selectedday));
                //tvDob.setText(dob);
                textView.setText(formatDate(selectedyear, selectedmonth, selectedday));
            }
        }, year, month, day);
        //mDatePicker1.setTitle("Select Date");
        // TODO Hide Future Date Here

        if (type == 1) {
            mDatePicker1.getDatePicker().setMaxDate(System.currentTimeMillis());
        } else if (type == 2) {
            mDatePicker1.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        } else if (type == 3) {
            mDatePicker1.getDatePicker().setMaxDate(System.currentTimeMillis());

            mcurrentDate.add(Calendar.YEAR, -60);
            mDatePicker1.getDatePicker().setMinDate(mcurrentDate.getTimeInMillis());
            mcurrentDate.add(Calendar.YEAR, +60);
            // Subtract 6 days from Calendar updated date
            mcurrentDate.add(Calendar.YEAR, -15);
            mDatePicker1.getDatePicker().setMaxDate(mcurrentDate.getTimeInMillis());
            //mDatePicker1.getDatePicker().setMinDate(System.currentTimeMillis());
            //mDatePicker1.getDatePicker().setMinDate(mcurrentDate.getTimeInMillis());
        } else if (type == 4) {
            mDatePicker1.getDatePicker().setMaxDate(System.currentTimeMillis());
            mcurrentDate.add(Calendar.DATE, -30);
            mDatePicker1.getDatePicker().setMinDate(mcurrentDate.getTimeInMillis());
        } else if (type == 5) {
            mDatePicker1.getDatePicker().setMaxDate(System.currentTimeMillis());
            mDatePicker1.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        } else if (type == 6) {

            mcurrentDate.add(Calendar.DATE, -1);
            mDatePicker1.getDatePicker().setMinDate(mcurrentDate.getTimeInMillis());
            mcurrentDate.add(Calendar.DATE, +2);
            mDatePicker1.getDatePicker().setMaxDate(mcurrentDate.getTimeInMillis());
        } else {
            mDatePicker1.getDatePicker().setMaxDate(System.currentTimeMillis());
            // Subtract 6 days from Calendar updated date
            mcurrentDate.add(Calendar.DATE, -1);
            mDatePicker1.getDatePicker().setMinDate(mcurrentDate.getTimeInMillis());
        }

        // TODO Hide Past Date Here
        //set min todays date
        //mDatePicker1.getDatePicker().setMinDate(System.currentTimeMillis());

        mDatePicker1.show();

        return times[0];
    }

    public static String formatDate(int year, int month, int day) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(year, month, day);
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return sdf.format(date);
    }


    //AlertGestational
    public static void AlertGestational(BaseActivity mActivity, int type) {
        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_gestational);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //ImageView
        ImageView ivImage1 = dialog.findViewById(R.id.ivImage1);
        ImageView ivImage2 = dialog.findViewById(R.id.ivImage2);
        ImageView ivImage3 = dialog.findViewById(R.id.ivImage3);
        ImageView ivImage4 = dialog.findViewById(R.id.ivImage4);
        ImageView ivImage5 = dialog.findViewById(R.id.ivImage5);

        //TextView
        TextView tvHeading1 = dialog.findViewById(R.id.tvHeading1);
        TextView tvHeading2 = dialog.findViewById(R.id.tvHeading2);
        TextView tvHeading3 = dialog.findViewById(R.id.tvHeading3);
        TextView tvHeading4 = dialog.findViewById(R.id.tvHeading4);
        TextView tvHeading5 = dialog.findViewById(R.id.tvHeading5);

        TextView ivBelowHeading1 = dialog.findViewById(R.id.ivBelowHeading1);
        TextView ivBelowHeading2 = dialog.findViewById(R.id.ivBelowHeading2);
        TextView ivBelowHeading3 = dialog.findViewById(R.id.ivBelowHeading3);
        TextView ivBelowHeading4 = dialog.findViewById(R.id.ivBelowHeading4);
        TextView ivBelowHeading5 = dialog.findViewById(R.id.ivBelowHeading5);
        TextView tvTitle = dialog.findViewById(R.id.tvTitle);


        //LinearLayout
        LinearLayout llFive = dialog.findViewById(R.id.llFive);

        if (type == 1) {
            ivImage1.setImageResource(R.mipmap.ears1);
            ivImage2.setImageResource(R.mipmap.feet1);
            ivImage3.setImageResource(R.mipmap.chest1);
            ivImage4.setImageResource(R.mipmap.male1);
            ivImage5.setImageResource(R.mipmap.female1);

            tvHeading1.setText(mActivity.getString(R.string.ears));
            tvHeading2.setText(mActivity.getString(R.string.feet));
            tvHeading3.setText(mActivity.getString(R.string.chest));
            tvHeading4.setText(mActivity.getString(R.string.maleGenitalia));
            tvHeading5.setText(mActivity.getString(R.string.femaleGenitalia));

            ivBelowHeading1.setText(mActivity.getString(R.string.ears1));
            ivBelowHeading2.setText(mActivity.getString(R.string.feet1));
            ivBelowHeading3.setText(mActivity.getString(R.string.chest1));
            ivBelowHeading4.setText(mActivity.getString(R.string.maleGenitalia1));
            ivBelowHeading5.setText(mActivity.getString(R.string.femaleGenitalia1));

            llFive.setVisibility(View.VISIBLE);

            tvTitle.setText(mActivity.getString(R.string.gestOption1Title));
        } else if (type == 2) {
            ivImage1.setImageResource(R.mipmap.skin2);
            ivImage2.setImageResource(R.mipmap.chest2);
            ivImage3.setImageResource(R.mipmap.male2);
            ivImage4.setImageResource(R.mipmap.female2);

            tvHeading1.setText(mActivity.getString(R.string.skin));
            tvHeading2.setText(mActivity.getString(R.string.chest));
            tvHeading3.setText(mActivity.getString(R.string.maleGenitalia));
            tvHeading4.setText(mActivity.getString(R.string.femaleGenitalia));

            ivBelowHeading1.setText(mActivity.getString(R.string.skin2New));
            ivBelowHeading2.setText(mActivity.getString(R.string.chest2));
            ivBelowHeading3.setText(mActivity.getString(R.string.maleGenitalia2));
            ivBelowHeading4.setText(mActivity.getString(R.string.femaleGenitalia2));

            llFive.setVisibility(View.INVISIBLE);
            tvTitle.setText(mActivity.getString(R.string.gestOption2Title));

        } else if (type == 3) {
            ivImage1.setImageResource(R.mipmap.ears3);
            ivImage2.setImageResource(R.mipmap.feet3);
            ivImage3.setImageResource(R.mipmap.chest3);
            ivImage4.setImageResource(R.mipmap.male3);
            ivImage5.setImageResource(R.mipmap.female3);

            tvHeading1.setText(mActivity.getString(R.string.ears));
            tvHeading2.setText(mActivity.getString(R.string.feet));
            tvHeading3.setText(mActivity.getString(R.string.chest));
            tvHeading4.setText(mActivity.getString(R.string.maleGenitalia));
            tvHeading5.setText(mActivity.getString(R.string.femaleGenitalia));

            ivBelowHeading1.setText(mActivity.getString(R.string.ears3));
            ivBelowHeading2.setText(mActivity.getString(R.string.feet3));
            ivBelowHeading3.setText(mActivity.getString(R.string.chest3));
            ivBelowHeading4.setText(mActivity.getString(R.string.maleGenitalia3));
            ivBelowHeading5.setText(mActivity.getString(R.string.femaleGenitalia3));

            llFive.setVisibility(View.VISIBLE);
            tvTitle.setText(mActivity.getString(R.string.gestOption3Title));

        } else if (type == 4) {
            ivImage1.setImageResource(R.mipmap.skin4);
            ivImage2.setImageResource(R.mipmap.chest4);
            ivImage3.setImageResource(R.mipmap.male4);
            ivImage4.setImageResource(R.mipmap.female4);

            tvHeading1.setText(mActivity.getString(R.string.skin));
            tvHeading2.setText(mActivity.getString(R.string.chest));
            tvHeading3.setText(mActivity.getString(R.string.maleGenitalia));
            tvHeading4.setText(mActivity.getString(R.string.femaleGenitalia));

            ivBelowHeading1.setText(mActivity.getString(R.string.skin4));
            ivBelowHeading2.setText(mActivity.getString(R.string.chest4));
            ivBelowHeading3.setText(mActivity.getString(R.string.maleGenitalia4));
            ivBelowHeading4.setText(mActivity.getString(R.string.femaleGenitalia4));

            llFive.setVisibility(View.INVISIBLE);
            tvTitle.setText(mActivity.getString(R.string.gestOption4Title));
        }


        //RelativeLayout
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        rlOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });
    }


    public static Bitmap setWaterMark(Bitmap src, BaseActivity mActivity) {

        String fac = AppSettings.getString(AppSettings.facName);
        String lounge = DatabaseController.getLoungeNameData(AppSettings.getString(AppSettings.loungeId));

        String facAndLoungeName = lounge + " - " + fac;

        /*String lat = AppSettings.getString(AppSettings.latitude);
        String lng = AppSettings.getString(AppSettings.longitude);

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(mActivity, Locale.getDefault());

        String address = "",city = "",state = "",feature="",locality="";
        try {

            double latitude = Double.parseDouble(AppSettings.getString(AppSettings.latitude));
            double longitude = Double.parseDouble(AppSettings.getString(AppSettings.longitude));

            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            Log.d("addresses",addresses.toString());

            address = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            feature = addresses.get(0).getFeatureName();
            locality = addresses.get(0).getLocality();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

        } catch (IOException e) {
            e.printStackTrace();
        }*/

        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(30);
        paint.setAntiAlias(true);
        paint.setUnderlineText(false);
        canvas.drawText("KMC", 20, 40, paint);
        canvas.drawText(facAndLoungeName, 20, 80, paint);
        canvas.drawText(currentTimestampFormat(), 20, 120, paint);
        //canvas.drawText(lat+","+lng, 20, 160, paint);
        //canvas.drawText(feature + ", "+locality, 20, 200, paint);

        return result;
    }

    public static String getPreviousSlot1(BaseActivity mActivity) {

        String time = AppUtils.getCurrentTime();

        Log.d("time-before", time);

        String[] parts = time.split(":");

        Log.d("time-after", parts[0]);

        String slotSection1 = "";

        int newTime = Integer.parseInt(time.replace(":", ""));

        if (newTime < 730) {
            slotSection1 = mActivity.getString(R.string.slot2pm);
        } else if (newTime < 1330) {
            slotSection1 = mActivity.getString(R.string.slot8pm);
        } else {
            slotSection1 = mActivity.getString(R.string.slot8am);
        }

        return slotSection1;
    }

    public static String getPreviousSlot2(BaseActivity mActivity) {

        String time = AppUtils.getCurrentTime();

        Log.d("time-before", time);

        String[] parts = time.split(":");

        Log.d("time-after", parts[0]);

        String slotSection2 = "";

        int newTime = Integer.parseInt(time.replace(":", ""));

        if (newTime < 730) {
            slotSection2 = mActivity.getString(R.string.slot8pm);
        } else if (newTime < 1330) {
            slotSection2 = mActivity.getString(R.string.slot8am);
        } else {
            slotSection2 = mActivity.getString(R.string.slot2pm);
        }

        return slotSection2;
    }


    public static String getCurrentSlot1(BaseActivity mActivity) {

        String time = AppUtils.getCurrentTime();

        Log.d("time-before", time);

        String[] parts = time.split(":");

        Log.d("time-after", parts[0]);

        String slotSection1 = "";

        int newTime = Integer.parseInt(time.replace(":", ""));

        if (newTime < 730) {
            slotSection1 = mActivity.getString(R.string.slot8pm);
        } else if (newTime < 1330) {
            slotSection1 = mActivity.getString(R.string.slot8am);
        } else {
            slotSection1 = mActivity.getString(R.string.slot2pm);
        }

        return slotSection1;
    }

    public static String getCurrentSlot2(BaseActivity mActivity) {

        String time = AppUtils.getCurrentTime();

        Log.d("time-before", time);

        String[] parts = time.split(":");

        Log.d("time-after", parts[0]);

        String slotSection2 = "";

        int newTime = Integer.parseInt(time.replace(":", ""));

        if (newTime < 730) {
            slotSection2 = mActivity.getString(R.string.slot8am);
        } else if (newTime < 1330) {
            slotSection2 = mActivity.getString(R.string.slot2pm);
        } else {
            slotSection2 = mActivity.getString(R.string.slot8pm);
        }

        return slotSection2;
    }


    public static String getCurrentShift() {

        String time = AppUtils.getCurrentTime();

        Log.d("time-before", time);

        String[] parts = time.split(":");

        Log.d("time-after", parts[0]);

        String shift = "";

        int newTime = Integer.parseInt(time.replace(":", ""));

        if (newTime < 730) {
            shift = "1";
        } else if (newTime < 1330) {
            shift = "2";
        } else {
            shift = "3";
        }

        return shift;
    }

    public static String getPreviousShift() {

        String time = AppUtils.getCurrentTime();

        Log.d("time-before", time);

        String[] parts = time.split(":");

        Log.d("time-after", parts[0]);

        String shift = "";

        int newTime = Integer.parseInt(time.replace(":", ""));

        if (newTime < 730) {
            shift = "2";
        } else if (newTime < 1330) {
            shift = "3";
        } else {
            shift = "1";
        }

        return shift;
    }


    public static String[] getAllSlot() {
        String time = AppUtils.getCurrentTime();

        Log.d("time-before", time);

        String[] parts = time.split(":");

        Log.d("time-after", parts[0]);

        int currentTime = Integer.parseInt(parts[0]);

        return parts;
    }


    public static String getAddDateCurrentSlot() {

        String time = AppUtils.getCurrentTime();

        Log.d("time-before", time);

        String[] parts = time.split(":");

        Log.d("time-after", parts[0]);

        String where = " ";
        String startDate = "";
        String endDate = "";

        int currentTime = Integer.parseInt(parts[0]);

        if (currentTime >= 8 && currentTime < 14) {
            startDate = getCurrentDateFormatted() + " 08:00:00";
            endDate = getCurrentDateFormatted() + " 13:59:59";
        } else if (currentTime >= 14 && currentTime < 20) {
            startDate = getCurrentDateFormatted() + " 14:00:00";
            endDate = getCurrentDateFormatted() + " 19:59:59";
        } else if (currentTime >= 20) {
            startDate = getCurrentDateFormatted() + " 20:00:00";
            endDate = getCurrentDateYMD(1) + " 07:59:59";
        } else {
            startDate = getCurrentDateYMD(-1) + " 20:00:00";
            endDate = getCurrentDateFormatted() + " 07:59:59";
        }

        where = " BETWEEN '" + startDate
                + "' AND '" + endDate + "' ";

        return where;
    }

    public static String getAddDateAsPerHospital() {

        String startDate = "";
        String endDate = "";

        String time = AppUtils.getCurrentTime();

        Log.d("time-before", time);

        String[] parts = time.split(":");

        Log.d("time-after", parts[0]);

        int currentTime = Integer.parseInt(parts[0]);

        if (currentTime >= 8 && currentTime <= 24) {
            startDate = getCurrentDateFormatted() + " 08:00:00";
            endDate = getCurrentDateYMD(1) + " 07:59:59";
        } else {
            startDate = getCurrentDateYMD(-1) + " 08:00:00";
            endDate = getCurrentDateFormatted() + " 07:59:59";
        }

        String where = " BETWEEN '" + startDate
                + "' AND '" + endDate + "' ";

        return where;

    }

    public static String getCurrentDateAsPerHospital() {

        String startDate = "";
        String endDate = "";

        String time = AppUtils.getCurrentTime();

        Log.d("time-before", time);

        String[] parts = time.split(":");

        Log.d("time-after", parts[0]);

        int currentTime = Integer.parseInt(parts[0]);

        if (currentTime >= 8 && currentTime <= 24) {
            startDate = getCurrentDateFormatted() + " 08:00:00";
            endDate = getCurrentDateYMD(1) + " 07:59:59";
        } else {
            startDate = getCurrentDateYMD(-1) + " 08:00:00";
            endDate = getCurrentDateFormatted() + " 07:59:59";
        }

        String where = "dateTimeText BETWEEN '" + startDate
                + "' AND '" + endDate + "' ";

        return where;
    }

    public static String getHoursDayAsPerHospital(String date) {

        String startDate = "";
        String endDate = "";

        startDate = date + " 08:00:00";
        endDate = addDay(date, 1) + " 07:59:59";

        String where = "dateTimeText BETWEEN '" + startDate
                + "' AND '" + endDate + "' ";

        return where;
    }

    public static String getHourDayAsPerHospital(String date) {

        String startDate = "";
        String endDate = "";

        startDate = date + " 08:00:00";
        endDate = addDay(date, 1) + " 07:59:59";

        String where = " BETWEEN '" + startDate
                + "' AND '" + endDate + "' ";

        return where;
    }

    public static String isDateTimeBetweenSlots(String currentDate) {

        String startDate = "";
        String endDate = "";

        String time = AppUtils.getCurrentTime();

        Log.d("time-before", time);

        String[] parts = time.split(":");

        Log.d("time-after", parts[0]);

        int currentTime = Integer.parseInt(parts[0]);

        if (currentTime >= 8 && currentTime <= 24) {
            startDate = getCurrentDateFormatted() + " 08:00:00";
            endDate = getCurrentDateYMD(1) + " 07:59:59";
        } else {
            startDate = getCurrentDateYMD(-1) + " 08:00:00";
            endDate = getCurrentDateFormatted() + " 07:59:59";
        }

        startDate = startDate.replaceAll(":", "").replaceAll(" ", "").replaceAll("-", "");
        endDate = endDate.replaceAll(":", "").replaceAll(" ", "").replaceAll("-", "");
        currentDate = currentDate.replaceAll(":", "").replaceAll(" ", "").replaceAll("-", "");

        Log.d("startDateXXX", startDate);
        Log.d("endDateXXX", endDate);
        Log.d("currentDateXXX", currentDate);

        if (Long.parseLong(currentDate) < Long.parseLong(startDate)) {
            return "1";
        } else if (Long.parseLong(currentDate) > Long.parseLong(endDate)) {
            return "2";
        } else {
            return "0";
        }
    }

    public static String isDateTimeLess(String lastEntry, String enteredDate) {

        lastEntry = lastEntry.replaceAll(":", "").replaceAll(" ", "").replaceAll("-", "");
        enteredDate = enteredDate.replaceAll(":", "").replaceAll(" ", "").replaceAll("-", "");

        Log.d("lastEntryXXX", lastEntry);
        Log.d("enteredDateXXX", enteredDate);

        if (Long.parseLong(lastEntry) < Long.parseLong(enteredDate)) {
            return "1";
        } else {
            return "0";
        }
    }

    //Alert Image with ok button
    public static void AlertImage(String base64, BaseActivity mActivity) {
        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_image);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //ImageView
        ImageView ivImage = dialog.findViewById(R.id.ivImage);

        //RelativeLayout
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        if (!base64.isEmpty()) {
            if (base64.contains("http")) {
                Picasso.get().load(base64).into(ivImage);
            } else {
                try {

                    byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    ivImage.setImageBitmap(decodedByte);

                    //Picasso.get().load(arrayList.get(0).get("babyPhoto")).into(ivPic);
                } catch (Exception e) {
                    //e.printStackTrace();
                    ivImage.setImageResource(R.mipmap.baby);
                }
            }
        } else {
            if (!AppConstants.hashMap.get("babyPhoto").isEmpty()) {
                if (AppConstants.hashMap.get("babyPhoto").contains("http")) {
                    Picasso.get().load(AppConstants.hashMap.get("babyPhoto")).into(ivImage);
                } else {
                    try {
                        byte[] decodedString = Base64.decode(AppConstants.hashMap.get("babyPhoto"), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        ivImage.setImageBitmap(decodedByte);

                    } catch (Exception e) {
                        ivImage.setImageResource(R.mipmap.baby);
                    }
                }
            } else {
                ivImage.setImageResource(R.mipmap.baby);
            }
        }

        rlOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });
    }

    public static float getQuantity(int weight, int age) {

        float quantity = 0;

        if (weight < 1500) {
            if (age < 6) {
                float exp1 = (float) weight / 1000;
                float exp2 = 80 + (age * 15);
                quantity = exp1 * exp2;
            } else {
                float exp1 = (float) weight / 1000;
                float exp2 = 150;
                quantity = exp1 * exp2;
            }
        } else if (weight > 1500) {
            if (age < 7) {
                float exp1 = (float) weight / 1000;
                float exp2 = 60 + (age * 15);
                quantity = exp1 * exp2;
            } else {
                float exp1 = (float) weight / 1000;
                float exp2 = 150;
                quantity = exp1 * exp2;
            }
        }

        return quantity / 2;
    }

    public static String getBase64fromBitmap(String url) {

        Bitmap bitmap = getBitmapFromURL(url);

        return getEncoded64ImageStringFromBitmap(bitmap);
    }

    public static String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
