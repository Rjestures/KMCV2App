package code.basic;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.androidnetworking.AndroidNetworking;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kmcapp.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.algo.WebServicesImageCallback;
import code.checkIn.CheckInActivity;
import code.common.LocaleHelper;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableBabyAdmission;
import code.database.TableBabyMonitoring;
import code.database.TableBabyRegistration;
import code.database.TableBreastFeeding;
import code.database.TableComments;
import code.database.TableDoctorRound;
import code.database.TableDutyChange;
import code.database.TableInvestigation;
import code.database.TableKMC;
import code.database.TableMotherAdmission;
import code.database.TableMotherMonitoring;
import code.database.TableMotherRegistration;
import code.database.TableStaff;
import code.database.TableTreatment;
import code.database.TableVaccination;
import code.database.TableWeight;
import code.main.MainActivity;
import code.main.TutorialActivity;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseActivity;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    //TextView
    static TextView tvtCheckInternet;
    final int REQUEST_CHECK_SETTINGS = 111;
    final int REQUEST_LOCATION = 222;
    public Boolean locUpdates = false;  // GPS/Network Location Updates are currently functioning
    public Boolean useGPS = false;     // tableName wants to use GPS/Network (based on setting) - turn off if permissions unsuccessful
    //Spinner
    Spinner spinnerDistrict, spinnerFacility, spinnerLounge;
    int position = 0;
    int imgPosition = 0;
    //TextView
    ArrayList<String> facname = new ArrayList<String>();
    ArrayList<String> facName = new ArrayList<>();
    //EditText
    private EditText etPassword;
    //RelativeLayout
    private RelativeLayout rlLogin, rlHelp, rlStuck, rlOwn, rlCheckInternet;
    //LinearLayout
    private LinearLayout llLanguage, llHelp;
    //ImageView
    private ImageView ivLogin;
    //ArrayList
    private ArrayList<String> districtId = new ArrayList<String>();
    private ArrayList<String> districtName = new ArrayList<String>();
    private ArrayList<String> facilityId = new ArrayList<String>();
    private ArrayList<String> facilityName = new ArrayList<String>();
    private ArrayList<String> loungeId = new ArrayList<String>();
    private ArrayList<String> loungeName = new ArrayList<String>();
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
    private String lounge = "", district = "", facility = "";
    private BroadcastReceiver mNetworkReceiver;
    private TextView tvLoginAsCoach, tvLoginDashboard;
    //For Location
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private SettingsClient mSettingsClient;
    private LocationCallback mLocationCallback;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Location mCurrentLocation;

    public static String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }

    public static void dialog(boolean value) {
        if (value) {

            tvtCheckInternet.setText(R.string.online);
            tvtCheckInternet.setBackgroundColor(Color.WHITE);
            tvtCheckInternet.setTextColor(Color.BLACK);

            Handler handler = new Handler();
            Runnable delayrunnable = new Runnable() {
                @Override
                public void run() {
                    tvtCheckInternet.setVisibility(View.GONE);
                }
            };
            handler.postDelayed(delayrunnable, 3000);
        } else {
            tvtCheckInternet.setVisibility(View.VISIBLE);
            tvtCheckInternet.setText(R.string.internet);
            tvtCheckInternet.setBackgroundColor(Color.WHITE);
            tvtCheckInternet.setTextColor(Color.BLACK);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseCrashlytics.getInstance().setCustomKey("LoginActivity","Login Has Started");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(mActivity, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("newToken", newToken);
                AppSettings.putString(AppSettings.fcmId, newToken);
            }
        });

        findViewById();

        if (AppUtils.isNetworkAvailable(this.mActivity)) {
        } else {
            mNetworkReceiver = new NetworkChangeReceiver();
            registerNetworkBroadcastForNougat();
        }
    }

    private void findViewById() {
        //Spinner
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerFacility = findViewById(R.id.spinnerFacility);
        spinnerLounge = findViewById(R.id.spinnerLounge);
        rlCheckInternet = findViewById(R.id.rlCheckInternet);

        //EditText
        etPassword = findViewById(R.id.etPassword);

        //TextView
        tvLoginAsCoach = findViewById(R.id.tvLoginAsCoach);
        tvLoginDashboard = findViewById(R.id.tvLoginDashboard);
        tvtCheckInternet = findViewById(R.id.tvtCheckInternet);

        //RelativeLayout
        rlLogin = findViewById(R.id.rlLogin);
        rlHelp = findViewById(R.id.rlHelp);
        rlStuck = findViewById(R.id.rlStuck);
        rlOwn = findViewById(R.id.rlOwn);

        //LinearLayout
        llLanguage = findViewById(R.id.llLanguage);
        llHelp = findViewById(R.id.llHelp);

        //ImageView
        ivLogin = findViewById(R.id.ivLogin);

        districtName.clear();
        districtName.add(getString(R.string.selectDistrict));
        districtName.addAll(DatabaseController.getDistrictNameData(""));

        districtId.clear();
        districtId.add(getString(R.string.selectDistrict));
        districtId.addAll(DatabaseController.getDistrictIdData(""));

        spinnerDistrict.setAdapter(new adapterSpinner(getApplicationContext(), R.layout.inflate_spinner, districtName));
        spinnerDistrict.setSelection(0);


        if (!AppSettings.getString(AppSettings.loginDistrict).isEmpty()) {
            for (int i = 0; i < districtId.size(); i++) {
                if (AppSettings.getString(AppSettings.loginDistrict).equals(districtId.get(i))) {
                    district = districtId.get(i);
                    spinnerDistrict.setSelection(i);
                    break;
                }
            }
        }

        //Spinner for District
        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // TODO Auto-generated method stub

//                facName.clear();
//                facName.addAll(DatabaseController.getFacNameData(districtId.get(position)));
//                Collections.sort(facName, new Comparator<String>() {
//                    @Override
//                    public int compare(String o1, String o2) {
//                        return o1.compareToIgnoreCase(o2);
//                    }
//                });

                if (position == 0) {
                    district = "0";
                    facilityName.clear();
                    facilityName.add(getString(R.string.selectFacility));
                    //facilityName.addAll(DatabaseController.getFacNameData(districtId.get(position)));

                    facilityId.clear();
                    facilityId.add(getString(R.string.selectFacility));
                    //facilityId.addAll(DatabaseController.getFacIdData(districtId.get(position)));

                    spinnerFacility.setAdapter(new adapterSpinner(getApplicationContext(), R.layout.inflate_spinner, facilityName));
                    spinnerFacility.setSelection(0);
                } else {
                    district = districtId.get(position);
                    facilityName.clear();
                    facilityName.add(getString(R.string.selectFacility));
//                    facilityName.addAll(facName);
                    facilityName.addAll(DatabaseController.getFacNameData(districtId.get(position)));

                    facilityId.clear();
                    facilityId.add(getString(R.string.selectFacility));
                    facilityId.addAll(DatabaseController.getFacIdData(districtId.get(position)));

                    spinnerFacility.setAdapter(new adapterSpinner(getApplicationContext(), R.layout.inflate_spinner, facilityName));
                    spinnerFacility.setSelection(0);

                    if (!AppSettings.getString(AppSettings.facId).isEmpty()) {
                        for (int i = 0; i < facilityId.size(); i++) {
                            if (AppSettings.getString(AppSettings.facId).equals(facilityId.get(i))) {
                                facility = facilityId.get(i);
                                spinnerFacility.setSelection(i);
                                break;
                            }
                        }
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });


        //Spinner for Facility

        spinnerFacility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if (position == 0) {
                    loungeName.clear();
                    loungeName.add(getString(R.string.selectLounge));
                    //facilityName.addAll(DatabaseController.getFacNameData(districtId.get(position)));

                    loungeId.clear();
                    loungeId.add(getString(R.string.selectLounge));
                    //facilityId.addAll(DatabaseController.getFacIdData(districtId.get(position)));

                    spinnerLounge.setAdapter(new adapterSpinner(getApplicationContext(), R.layout.inflate_spinner, loungeName));
                    spinnerLounge.setSelection(0);

                } else {

                    loungeName.clear();
                    loungeName.add(getString(R.string.selectLounge));
                    loungeName.addAll(DatabaseController.getLoungeName(facilityId.get(position)));

                    loungeId.clear();
                    loungeId.add(getString(R.string.selectLounge));
                    loungeId.addAll(DatabaseController.getLoungeId(facilityId.get(position)));

                    spinnerLounge.setAdapter(new adapterSpinner(getApplicationContext(), R.layout.inflate_spinner, loungeName));
                    spinnerLounge.setSelection(0);

                    if (!AppSettings.getString(AppSettings.loungeId).isEmpty()) {
                        for (int i = 0; i < loungeId.size(); i++) {
                            if (AppSettings.getString(AppSettings.loungeId).equals(loungeId.get(i))) {
                                lounge = loungeId.get(i);
                                spinnerLounge.setSelection(i);
                                break;
                            }
                        }
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        //Spinner for Religion
        spinnerLounge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if (position == 0) {
                    lounge = "";
                } else {
                    lounge = loungeId.get(position).toString();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        forOpeningGeoLocation();

        rlLogin.setOnClickListener(this);
        rlHelp.setOnClickListener(this);
        rlStuck.setOnClickListener(this);
        rlOwn.setOnClickListener(this);
        llLanguage.setOnClickListener(this);
        llHelp.setOnClickListener(this);
        tvLoginAsCoach.setOnClickListener(this);
        tvLoginDashboard.setOnClickListener(this);

    }

    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            FirebaseCrashlytics.getInstance().setCustomKey("RegisterServiceException",e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlLogin:
                if (spinnerDistrict.getSelectedItemPosition() == 0) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorselectDistrict));
                } else if (spinnerFacility.getSelectedItemPosition() == 0 || spinnerFacility.getSelectedItem().toString().equals("Select Facility")) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorselectFacility));
                } else if (spinnerLounge.getSelectedItemPosition() == 0) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorselectLounge));
                } else if (etPassword.getText().toString().trim().isEmpty()) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorPassword));
                } else if (AppUtils.isNetworkAvailable(this.mActivity)) {
                    if (AppSettings.getString(AppSettings.incorrectLoginAttempt).equals("5")) {
                        long remainSeconds = AppUtils.getSecondsDifference(Long.parseLong(AppSettings.getString(AppSettings.incorrectLoginTime)), AppUtils.getCurrentTimestamp());

                        if (remainSeconds < 180) {

                            long remainMinutes = 3 - AppUtils.getSecondsToMinutes(remainSeconds);

                            AppUtils.showToastSort(mActivity, getString(R.string.tryAgainIn) + " " + String.valueOf(remainMinutes) + " " + getString(R.string.minutes));

                        } else {

                            AppSettings.putString(AppSettings.incorrectLoginTime, "");
                            AppSettings.putString(AppSettings.incorrectLoginAttempt, "0");

                            getTokenApi();

                        }
                    } else
                        getTokenApi();
                } else {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                }

                break;

            case R.id.llLanguage:

                showChangeLanguageDialog();

                //  AppUtils.AlertLanguageConfirm(mActivity, getString(R.string.dataWillBeLost));

                //AppUtils.AlertLanguageConfirm(mActivity,getString(R.string.languageAlert));

                break;

            case R.id.llHelp:

            case R.id.rlHelp:

                if (rlHelp.getVisibility() == View.VISIBLE) {
                    rlHelp.setVisibility(View.GONE);
                } else {
                    rlHelp.setVisibility(View.VISIBLE);
                }

                break;

            case R.id.rlStuck:
                rlHelp.setVisibility(View.GONE);
                DatabaseController.saveHelp(mActivity);
                AppUtils.AlertHelpConfirm(getString(R.string.callRegardingIssue), mActivity, 1, "");
                break;

            case R.id.rlOwn:

                rlHelp.setVisibility(View.GONE);
                startActivity(new Intent(mActivity, TutorialActivity.class));

                break;

            case R.id.tvLoginAsCoach:

                startActivity(new Intent(mActivity, LoginCoachActivity.class));
                finish();

                break;

            case R.id.tvLoginDashboard:

                startActivity(new Intent(mActivity, LoginDashboardActivity.class));
                finish();

                break;

            default:

                break;
        }
    }

    private void showChangeLanguageDialog() {

        String msg = getString(R.string.languageAlert);
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
        if (AppSettings.getString(AppSettings.keyLanguageCode).equals("en")) {
            msg = msg + " in Hindi?";
        } else {
            msg = "क्या आप वाकई भाषा को अंग्रेज़ी में बदलना चाहते हैं?";
        }

        tvMessage.setText(msg);

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
                AppUtils.SettingLanguageNew(mActivity);
//                recreate();

                mActivity.finish();
                mActivity.startActivity(mActivity.getIntent());
            }
        });

    }

    private void getTokenApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {

            jsonData.put("loungeId", lounge);
            jsonData.put("loungePassword", etPassword.getText().toString().trim());
            jsonData.put("deviceId", AppUtils.getDeviceID(mActivity));

            json.put(AppConstants.projectName, jsonData);

            Log.v("getAllStaffApi", json.toString());

        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().setCustomKey("GettokenException",e.getMessage());
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.getToken, json, true, false, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {
            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        AppSettings.putString(AppSettings.token, jsonObject.getString("token"));

                        LocationManager locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
                        boolean networkLocationEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                        if (!networkLocationEnabled) {
                            forOpeningGeoLocation();
                        } else {
                            forOpeningGeoLocation();
                            if (AppUtils.isNetworkAvailable(mActivity)) {
                                loginApi();
                            } else {
                                AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                            }
                        }
                    } else if (jsonObject.getString("resCode").equals("3")) {
                        AppUtils.hideDialog();
                        inCorrectLoginCondition();

                    } else {
                        AppUtils.hideDialog();
                        AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));
                    }
                } catch (JSONException e) {
                    FirebaseCrashlytics.getInstance().setCustomKey("GettokenException",e.getMessage());
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

//                AppUtils.showToastSort(mActivity, getString(R.string.passwordWorng));

            }
        });

    }

    private void loginApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {

            jsonData.put("loungeId", lounge);
            jsonData.put("loungePassword", etPassword.getText().toString().trim());
            jsonData.put("deviceId", AppUtils.getDeviceID(mActivity));
            jsonData.put("imeiNumber", AppUtils.getDeviceIMEI(mActivity));
            jsonData.put("latitude", AppSettings.getString(AppSettings.latitude));
            jsonData.put("longitude", AppSettings.getString(AppSettings.longitude));
            jsonData.put("fcmId", AppSettings.getString(AppSettings.fcmId));

            json.put(AppConstants.projectName, jsonData);

            Log.v("getAllStaffApi", json.toString());

        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().setCustomKey("LoginJsonException",e.getMessage());
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.loungeLogin, json, false, false, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        AppSettings.putString(AppSettings.incorrectLoginAttempt, "0");
                        AppSettings.putString(AppSettings.loungeId, lounge);
                        AppSettings.putString(AppSettings.loginDistrict, district);

                        JSONObject facJson = jsonObject.getJSONObject("loungeDetails");

                        AppSettings.putString(AppSettings.facId, facJson.getString("facilityId"));
                        AppSettings.putString(AppSettings.facName, facJson.getString("facilityName"));
                        //String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                        String currentDateTimeString = AppUtils.getCurrentDateTime();
                        AppSettings.putString(AppSettings.loginId, facJson.getString("loginId"));
                        AppSettings.putString(AppSettings.loginTime, currentDateTimeString);

                        AppSettings.putString(AppSettings.loungeType,
                                DatabaseController.getLoungeTypeData(AppSettings.getString(AppSettings.loungeId)));

                        if (AppUtils.isNetworkAvailable(mActivity)) {
                            getAllStaffApi();
                        } else {
                            AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                        }
                    } else {
                        AppUtils.hideDialog();
                        AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));


                    }

                } catch (JSONException e) {
                    FirebaseCrashlytics.getInstance().setCustomKey("LoginparseException",e.getMessage());
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

                AppUtils.showToastSort(mActivity, responce);

            }
        });

    }

    private void inCorrectLoginCondition() {

        int incorrectLoginAttempt = 0;

        if (!AppSettings.getString(AppSettings.incorrectLoginAttempt).isEmpty()) {

            incorrectLoginAttempt = Integer.parseInt(AppSettings.getString(AppSettings.incorrectLoginAttempt));
        }

        incorrectLoginAttempt = incorrectLoginAttempt + 1;

        AppSettings.putString(AppSettings.incorrectLoginAttempt, String.valueOf(incorrectLoginAttempt));

        if (String.valueOf(incorrectLoginAttempt).equals("1")) {

            AppUtils.showToastSort(mActivity, getString(R.string.loginAttemptsLeft4));
        } else if (String.valueOf(incorrectLoginAttempt).equals("2")) {

            AppUtils.showToastSort(mActivity, getString(R.string.loginAttemptsLeft3));
        } else if (String.valueOf(incorrectLoginAttempt).equals("3")) {

            AppUtils.showToastSort(mActivity, getString(R.string.loginAttemptsLeft2));
        } else if (String.valueOf(incorrectLoginAttempt).equals("4")) {

            AppUtils.showToastSort(mActivity, getString(R.string.loginAttemptsLeft1));
        } else {

            AppSettings.putString(AppSettings.incorrectLoginTime, String.valueOf(AppUtils.getCurrentTimestamp()));
            AppUtils.showToastSort(mActivity, getString(R.string.loginIn10Minutes));

            if (AppUtils.isNetworkAvailable(mActivity))
                hitSendInvalidPwdMailApi();
            else
                AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
        }


    }

    private void hitSendInvalidPwdMailApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();

        try {
            jsonData.put("loungeId", lounge);

            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));

        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().setCustomKey("InvalidPasswordException",e.getMessage());
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.SendMailInvalidPassword, json, false, false, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {


                    }

                } catch (JSONException e) {
                    FirebaseCrashlytics.getInstance().setCustomKey("InvalidPasswordApiException",e.getMessage());
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {
                AppUtils.showToastSort(mActivity, responce);

            }
        });
    }

    private void getAllStaffApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {

            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("timestamp", "");

            json.put(AppConstants.projectName, jsonData);

            Log.v("getAllStaffApi", json.toString());

        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().setCustomKey("GetAllStaffJsomException",e.getMessage());
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.getStaff, json, false, false, new WebServicesCallback() {
            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        AppSettings.putString(AppSettings.staffSyncTime, jsonObject.getString("syncTime"));

                        JSONArray staffDetail = jsonObject.getJSONArray("staffDetails");

                        try {

                            DatabaseController.myDataBase.beginTransaction();

                            DatabaseController.myDataBase.delete(TableStaff.tableName, null, null);

                            for (int i = 0; i < staffDetail.length(); i++) {

                                JSONObject detailJSONObject = staffDetail.getJSONObject(i);

                                ContentValues contentValues = new ContentValues();
                                contentValues.put(TableStaff.tableColumn.staffId.toString(), detailJSONObject.getString("staffId"));
                                contentValues.put(TableStaff.tableColumn.staffName.toString(), detailJSONObject.getString("name"));
                                contentValues.put(TableStaff.tableColumn.staffMobile.toString(), detailJSONObject.getString("staffMobileNumber"));
                                contentValues.put(TableStaff.tableColumn.staffType.toString(), detailJSONObject.getString("staffType"));
                                contentValues.put(TableStaff.tableColumn.staffSubType.toString(), detailJSONObject.getString("staffSubType"));
                                contentValues.put(TableStaff.tableColumn.staffAddress.toString(), detailJSONObject.getString("staffAddress"));
                                contentValues.put(TableStaff.tableColumn.emergencyContactNumber.toString(), detailJSONObject.getString("emergencyContactNumber"));
                                contentValues.put(TableStaff.tableColumn.jobType.toString(), detailJSONObject.getString("jobType"));
                                contentValues.put(TableStaff.tableColumn.staffProfile.toString(), detailJSONObject.getString("profilePicture"));
                                contentValues.put(TableStaff.tableColumn.addDate.toString(), detailJSONObject.getString("addDate"));
                                contentValues.put(TableStaff.tableColumn.modifyDate.toString(), detailJSONObject.getString("modifyDate"));
                                contentValues.put(TableStaff.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
                                contentValues.put(TableStaff.tableColumn.status.toString(), detailJSONObject.getString("status"));
                                contentValues.put(TableStaff.tableColumn.facilityId.toString(), detailJSONObject.getString("facilityId"));

                                DatabaseController.insertUpdateData(contentValues, TableStaff.tableName, TableStaff.tableColumn.staffId.toString(), detailJSONObject.getString("staffId"));

                            }

                            DatabaseController.myDataBase.setTransactionSuccessful();

                        } finally {
                            DatabaseController.myDataBase.endTransaction();
                        }

                    } else {
                        AppUtils.hideDialog();
//                        AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));
                    }

                    if (AppUtils.isNetworkAvailable(mActivity)) {
                        getAllStaffDutyApi();
                    } else {
                        AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                    }

                } catch (JSONException e) {
                    FirebaseCrashlytics.getInstance().setCustomKey("GetAllStaffApiException",e.getMessage());
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

//                AppUtils.showToastSort(mActivity, responce);

            }
        });


    }

    //forOpeningGeoLocation
    private void forOpeningGeoLocation() {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
        mSettingsClient = LocationServices.getSettingsClient(mActivity);
        createLocationRequest();
        createLocationCallback();
        buildLocationSettingsRequest();
        checkPermissions();
    }

    // Start Fused Location services
    protected void createLocationRequest() {
        // create the location request and set parameters
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);  // preferred update rate
        mLocationRequest.setFastestInterval(5000);  // fastest rate app can handle updates
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    public void buildLocationSettingsRequest() {
        // get current locations settings of tableName's device
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    // check if Manifest (application) location permissions enabled and if not, request permissions
    public void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            }
        } else {
            startLocationUpdates();
        }
    }

    // Manifest (application) location permissions result: either tableName allowed or denied/cancelled permissions request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        //Toast.makeText(context, "Resolution Required: *" + Integer.toString(requestCode) + "*", Toast.LENGTH_LONG).show();
        switch (requestCode) {
            case REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    // permission granted
                    startLocationUpdates();  // includes checking location settings
                } else {
                    // permission denied -
                    useGPS = false;
                    stopLocationUpdates();
                }
            }
        }
    }

    public void startLocationUpdates() {
        // if settings are satisfied initialize location requests
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest).addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                locUpdates = true;
                // All location settings are satisfied.
                //noinspection MissingPermission - this doctorComment needs to stay here to stop inspection on next line
                if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the tableName grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            }
        })
                // if settings need to be changed prompt tableName
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // location settings are not satisfied, but this can be fixed by showing the tableName a dialog.
                                try {
                                    // show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                                    ResolvableApiException resolvable = (ResolvableApiException) e;
                                    resolvable.startResolutionForResult(mActivity, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sendEx) {
                                    // Ignore the error
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // location settings are not satisfied, however no way to fix the settings so don't show dialog.
                                Toast.makeText(mActivity, "Location Services Unavailable", Toast.LENGTH_LONG).show();
                                useGPS = false;
                                stopLocationUpdates();
                                break;
                        }
                    }
                });
    }

    // Get results from tableName dialog prompt to turn on location services for app
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                        PackageManager.PERMISSION_GRANTED) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                            }
                        } else {
                            // Start location updates
                            // Note - in emulator location appears to be null if no other app is using GPS at time.
                            // So if just turning on device's location services getLastLocation will likely not
                            // return anything
                            //Toast.makeText(context, "starting GPS updates", Toast.LENGTH_LONG).show();
                            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                            locUpdates = true;
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        // tableName does not want to update setting. Handle it in a way that it will to affect your app functionality
                        useGPS = false;
                        startLocationUpdates();
                        break;
                }
                break;
        }
    }

    // stop location updates
    public void stopLocationUpdates() {
        locUpdates = false;
        mFusedLocationClient.removeLocationUpdates(mLocationCallback).addOnCompleteListener((Activity) mActivity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }

    public void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                setLatLong(mCurrentLocation);
            }
        };
    }

    // Set new latitude and longitude based on location results
    public void setLatLong(Location location) {
        double lastLat = location.getLatitude();
        double lastLong = location.getLongitude();
        useGPS = true;
        AppSettings.putString(AppSettings.latitude, String.valueOf(lastLat));
        AppSettings.putString(AppSettings.longitude, String.valueOf(lastLong));

        Log.d("latitude", AppSettings.getString(AppSettings.latitude));
        Log.d("longitude", AppSettings.getString(AppSettings.longitude));

        stopLocationUpdates();
    }

    //Sync old Data in local database
    private void getAllStaffDutyApi() {
        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            json.put(AppConstants.projectName, jsonData);
            Log.v("getAllStaffApi", json.toString());
        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().setCustomKey("GetAllStaffDutyJsonException",e.getMessage());
            e.printStackTrace();
        }
        WebServices.postApi(mActivity, AppUrls.getNurseDutyData, json, false, false, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {
                Log.v("getAllStaffApiRes", jsonObject1.toString());
                try {
                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("dutyChangeData");

                        try {

                            DatabaseController.myDataBase.beginTransaction();

                            DatabaseController.myDataBase.delete(TableDutyChange.tableName, null, null);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject arrayJSONObject = jsonArray.getJSONObject(i);

                                ContentValues contentValues = new ContentValues();
                                contentValues.put(TableDutyChange.tableColumn.serverId.toString(), arrayJSONObject.getString("id"));
                                contentValues.put(TableDutyChange.tableColumn.uuid.toString(), arrayJSONObject.getString("androidUuid"));
                                contentValues.put(TableDutyChange.tableColumn.nurseId.toString(), arrayJSONObject.getString("nurseId"));
                                contentValues.put(TableDutyChange.tableColumn.loungeId.toString(), arrayJSONObject.getString("loungeId"));
                                contentValues.put(TableDutyChange.tableColumn.type.toString(), arrayJSONObject.getString("type"));
                                contentValues.put(TableDutyChange.tableColumn.selfieCheckIn.toString(), arrayJSONObject.getString("selfie"));
                                contentValues.put(TableDutyChange.tableColumn.latitude.toString(), arrayJSONObject.getString("latitude"));
                                contentValues.put(TableDutyChange.tableColumn.longitude.toString(), arrayJSONObject.getString("longitude"));
                                contentValues.put(TableDutyChange.tableColumn.selfieCheckOut.toString(), arrayJSONObject.getString("checkoutSelfie"));
                                contentValues.put(TableDutyChange.tableColumn.addDate.toString(), arrayJSONObject.getString("addDate"));
                                contentValues.put(TableDutyChange.tableColumn.modifyDate.toString(), arrayJSONObject.getString("modifyDate"));
                                contentValues.put(TableDutyChange.tableColumn.syncTime.toString(), arrayJSONObject.getString("lastSyncedTime"));
                                contentValues.put(TableDutyChange.tableColumn.status.toString(), arrayJSONObject.getString("status"));
                                contentValues.put(TableDutyChange.tableColumn.isDataSynced.toString(), "1");

                                DatabaseController.insertData(contentValues, TableDutyChange.tableName);

                            }
                            Log.v("getAllStaffApihjghjgh", jsonArray.toString());

                            DatabaseController.myDataBase.setTransactionSuccessful();

                        } finally {
                            DatabaseController.myDataBase.endTransaction();
                        }

                    }

                    if (AppUtils.isNetworkAvailable(mActivity)) {
                        getAllBabyMotherApi();
                    } else {
                        AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                    }

                } catch (JSONException e) {

                    FirebaseCrashlytics.getInstance().setCustomKey("GetNurseDutydataParseException",e.getMessage());
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

//                AppUtils.showToastSort(mActivity, responce);

            }
        });


    }

    private void getAllBabyMotherApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {

            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            json.put(AppConstants.projectName, jsonData);

            Log.v("getAllBabyMotherApi", json.toString());

        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().setCustomKey("GetAllBabyMotherJsonException",e.getMessage());
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.getAdmittedDataByLounge, json, false, false, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                arrayList.clear();

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        AppSettings.putString(AppSettings.babyDirectoryUrl, jsonObject.getString("babyDirectoryUrl"));
                        AppSettings.putString(AppSettings.motherDirectoryUrl, jsonObject.getString("motherDirectoryUrl"));
                        AppSettings.putString(AppSettings.sehmatiPatraUrl, jsonObject.getString("sehmatiPatraUrl"));
                        AppSettings.putString(AppSettings.signDirectoryUrl, jsonObject.getString("signDirectoryUrl"));
                        AppSettings.putString(AppSettings.babyWeightDirectoryUrl, jsonObject.getString("babyWeightDirectoryUrl"));
                        AppSettings.putString(AppSettings.investigationDirectoryUrl, jsonObject.getString("investigationDirectoryUrl"));

                        JSONArray jsonArray = jsonObject.getJSONArray("result");

                        position = 0;

                        if (jsonArray.length() > 0) {
                            saveData(position, jsonArray);
                        } else {
                            setLogin();
                        }
                    } else {
                        AppUtils.hideDialog();

                        AppSettings.putString(AppSettings.from, "0");
                        AppSettings.putString(AppSettings.userType, "0");

                        if (DatabaseController.getNurseIdCheckedInData().size() == 0) {
                            startActivity(new Intent(mActivity, CheckInActivity.class));
                            finish();
                        } else {
                            AppSettings.putString(AppSettings.from, "0");
                            startActivity(new Intent(mActivity, MainActivity.class));
                            finish();
                        }
                    }

                } catch (JSONException e) {
                    FirebaseCrashlytics.getInstance().setCustomKey("GetAdmittedDataByLoungeParseException",e.getMessage());
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

                AppUtils.showToastSort(mActivity, responce);

            }
        });


    }

    private void saveData(int position, JSONArray jsonArray) {

        Log.d("position - ", String.valueOf(position));

        try {
            try {
                String babyId = "", motherId = "";

                DatabaseController.myDataBase.beginTransaction();

                JSONObject arrayJSONObject = jsonArray.getJSONObject(position);

                if (arrayJSONObject.has("babyRegistrationData")) {

                    Log.d("position step 1 - ", String.valueOf(position));

                    JSONObject jsonBabyRegistration = arrayJSONObject.getJSONObject("babyRegistrationData");

                    Log.d("jsonBabyRegistration", jsonBabyRegistration.toString());

                    babyId = jsonBabyRegistration.getString("babyId");
                    motherId = jsonBabyRegistration.getString("motherId");

                    DatabaseController.delete(TableBabyRegistration.tableName, "babyId = '" + babyId + "'", null);
                    DatabaseController.delete(TableBabyAdmission.tableName, "babyId = '" + babyId + "'", null);
                    DatabaseController.delete(TableBabyMonitoring.tableName, "babyId = '" + babyId + "'", null);
                    DatabaseController.delete(TableBreastFeeding.tableName, "babyId = '" + babyId + "'", null);
                    DatabaseController.delete(TableComments.tableName, "motherOrBabyId = '" + babyId + "'", null);
                    DatabaseController.delete(TableWeight.tableName, "babyId = '" + babyId + "'", null);
                    DatabaseController.delete(TableKMC.tableName, "babyId = '" + babyId + "'", null);
                    DatabaseController.delete(TableDoctorRound.tableName, "babyId = '" + babyId + "'", null);
                    DatabaseController.delete(TableInvestigation.tableName, "babyId = '" + babyId + "'", null);
                    DatabaseController.delete(TableTreatment.tableName, "babyId = '" + babyId + "'", null);
                    DatabaseController.delete(TableVaccination.tableName, "babyId = '" + babyId + "'", null);

                    ContentValues contentValues = new ContentValues();

                    contentValues.put(TableBabyRegistration.tableColumn.uuid.toString(), jsonBabyRegistration.getString("androidUuid"));
                    contentValues.put(TableBabyRegistration.tableColumn.motherId.toString(), jsonBabyRegistration.getString("motherId"));
                    contentValues.put(TableBabyRegistration.tableColumn.babyMCTSNumber.toString(), jsonBabyRegistration.getString("babyMCTSNumber"));
                    contentValues.put(TableBabyRegistration.tableColumn.babyId.toString(), jsonBabyRegistration.getString("babyId"));
                    contentValues.put(TableBabyRegistration.tableColumn.deliveryDate.toString(), jsonBabyRegistration.getString("deliveryDate"));
                    contentValues.put(TableBabyRegistration.tableColumn.deliveryTime.toString(), jsonBabyRegistration.getString("deliveryTime"));
                    contentValues.put(TableBabyRegistration.tableColumn.firstTimeFeed.toString(), jsonBabyRegistration.getString("firstTimeFeed"));
                    contentValues.put(TableBabyRegistration.tableColumn.babyGender.toString(), jsonBabyRegistration.getString("babyGender"));
                    contentValues.put(TableBabyRegistration.tableColumn.deliveryType.toString(), jsonBabyRegistration.getString("deliveryType"));
                    contentValues.put(TableBabyRegistration.tableColumn.babyWeight.toString(), jsonBabyRegistration.getString("babyWeight"));
                    contentValues.put(TableBabyRegistration.tableColumn.babyCryAfterBirth.toString(), jsonBabyRegistration.getString("babyCryAfterBirth"));
                    contentValues.put(TableBabyRegistration.tableColumn.babyNeedBreathingHelp.toString(), jsonBabyRegistration.getString("babyNeedBreathingHelp"));
                    contentValues.put(TableBabyRegistration.tableColumn.birthWeightAvail.toString(), "1");
                    contentValues.put(TableBabyRegistration.tableColumn.reason.toString(), jsonBabyRegistration.getString("reason"));
                    contentValues.put(TableBabyRegistration.tableColumn.babyPhoto.toString(), "");

                    if (!jsonBabyRegistration.getString("babyPhoto").isEmpty()) {
                        HashMap<String, String> hashlist = new HashMap();
                        hashlist.put("url", AppSettings.getString(AppSettings.babyDirectoryUrl) + jsonBabyRegistration.getString("babyPhoto"));
                        hashlist.put("from", "1");
                        hashlist.put("primaryId", jsonBabyRegistration.getString("babyId"));

                        arrayList.add(hashlist);

                    }

                    contentValues.put(TableBabyRegistration.tableColumn.isDataSynced.toString(), "1");
                    contentValues.put(TableBabyRegistration.tableColumn.typeOfBorn.toString(), jsonBabyRegistration.getString("typeOfBorn"));
                    contentValues.put(TableBabyRegistration.tableColumn.typeOfOutBorn.toString(), jsonBabyRegistration.getString("typeOfOutBorn"));
                    contentValues.put(TableBabyRegistration.tableColumn.wasApgarScoreRecorded.toString(), "");
                    contentValues.put(TableBabyRegistration.tableColumn.apgarScore.toString(), "");
                    contentValues.put(TableBabyRegistration.tableColumn.vitaminKGiven.toString(), "");

                    contentValues.put(TableBabyRegistration.tableColumn.syncTime.toString(), jsonBabyRegistration.getString("addDate"));
                    contentValues.put(TableBabyRegistration.tableColumn.addDate.toString(), jsonBabyRegistration.getString("addDate"));
                    contentValues.put(TableBabyRegistration.tableColumn.modifyDate.toString(), jsonBabyRegistration.getString("modifyDate"));
                    contentValues.put(TableBabyRegistration.tableColumn.status.toString(), "1");

                    contentValues.put(TableBabyRegistration.tableColumn.isDataSynced.toString(), "1");
                    DatabaseController.insertData(contentValues, TableBabyRegistration.tableName);

                }

                if (arrayJSONObject.has("babyAdmissionData")) {

                    Log.d("position step 2 - ", String.valueOf(position));

                    JSONObject jsonBabyAdmission = arrayJSONObject.getJSONObject("babyAdmissionData");

                    Log.d("jsonBabyAdmission", jsonBabyAdmission.toString());

                    ContentValues cvBabyAdm = new ContentValues();

                    cvBabyAdm.put(TableBabyAdmission.tableColumn.isDataSynced.toString(), "1");
                    cvBabyAdm.put(TableBabyAdmission.tableColumn.uuid.toString(), jsonBabyAdmission.getString("androidUuid"));
                    cvBabyAdm.put(TableBabyAdmission.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
                    cvBabyAdm.put(TableBabyAdmission.tableColumn.serverId.toString(), jsonBabyAdmission.getString("id"));
                    cvBabyAdm.put(TableBabyAdmission.tableColumn.babyId.toString(), jsonBabyAdmission.getString("babyId"));
                    cvBabyAdm.put(TableBabyAdmission.tableColumn.babyFileId.toString(), jsonBabyAdmission.getString("babyFileId"));
                    cvBabyAdm.put(TableBabyAdmission.tableColumn.admissionDateTime.toString(), jsonBabyAdmission.getString("admissionDateTime"));
                    cvBabyAdm.put(TableBabyAdmission.tableColumn.kmcPosition.toString(), jsonBabyAdmission.getString("kmcPosition"));
                    cvBabyAdm.put(TableBabyAdmission.tableColumn.kmcMonitoring.toString(), jsonBabyAdmission.getString("kmcMonitoring"));
                    cvBabyAdm.put(TableBabyAdmission.tableColumn.kmcNutrition.toString(), jsonBabyAdmission.getString("kmcNutrition"));
                    cvBabyAdm.put(TableBabyAdmission.tableColumn.kmcRespect.toString(), jsonBabyAdmission.getString("kmcRespect"));
                    cvBabyAdm.put(TableBabyAdmission.tableColumn.kmcHygiene.toString(), jsonBabyAdmission.getString("kmcHygiene"));
                    cvBabyAdm.put(TableBabyAdmission.tableColumn.whatisKmc.toString(), jsonBabyAdmission.getString("whatisKmc"));
                    cvBabyAdm.put(TableBabyAdmission.tableColumn.infantComingFrom.toString(), jsonBabyAdmission.getString("infantComingFrom"));
                    cvBabyAdm.put(TableBabyAdmission.tableColumn.status.toString(), "1");
                    cvBabyAdm.put(TableBabyAdmission.tableColumn.isAnyComplicationAtBirth.toString(), jsonBabyAdmission.getString("isAnyComplicationAtBirth"));
                    cvBabyAdm.put(TableBabyAdmission.tableColumn.convulsions.toString(), jsonBabyAdmission.getString("convulsions"));
                    cvBabyAdm.put(TableBabyAdmission.tableColumn.gestAge.toString(), jsonBabyAdmission.getString("gestationalAge"));

                    DatabaseController.insertData(cvBabyAdm, TableBabyAdmission.tableName);
                }

                if (arrayJSONObject.has("babyDailyMonitoring")) {

                    JSONArray jsonArrayMain = arrayJSONObject.getJSONArray("babyDailyMonitoring");

                    for (int j = 0; j < jsonArrayMain.length(); j++) {

                        JSONObject jsonArrayMainJSONObject = jsonArrayMain.getJSONObject(j);

                        ContentValues contentValues = new ContentValues();

                        contentValues.put(TableBabyMonitoring.tableColumn.uuid.toString(), jsonArrayMainJSONObject.getString("androidUuid"));
                        contentValues.put(TableBabyMonitoring.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
                        contentValues.put(TableBabyMonitoring.tableColumn.serverId.toString(), jsonArrayMainJSONObject.getString("id"));
                        contentValues.put(TableBabyMonitoring.tableColumn.babyId.toString(), babyId);
                        contentValues.put(TableBabyMonitoring.tableColumn.babyAdmissionId.toString(), jsonArrayMainJSONObject.getString("babyAdmissionId"));
                        contentValues.put(TableBabyMonitoring.tableColumn.babyRespiratoryRate.toString(), jsonArrayMainJSONObject.getString("respiratoryRate"));
                        contentValues.put(TableBabyMonitoring.tableColumn.isPulseOximatoryDeviceAvailable.toString(), jsonArrayMainJSONObject.getString("isPulseOximatoryDeviceAvail"));
                        contentValues.put(TableBabyMonitoring.tableColumn.type.toString(), "1");

                        contentValues.put(TableBabyMonitoring.tableColumn.crtKnowledge.toString(), jsonArrayMainJSONObject.getString("crtKnowledge"));
                        contentValues.put(TableBabyMonitoring.tableColumn.isDataComplete.toString(), "1");
                        contentValues.put(TableBabyMonitoring.tableColumn.isThermometerAvailable.toString(), jsonArrayMainJSONObject.getString("isThermometerAvailable"));

                        contentValues.put(TableBabyMonitoring.tableColumn.babyTemperature.toString(), jsonArrayMainJSONObject.getString("temperatureValue"));
                        contentValues.put(TableBabyMonitoring.tableColumn.temperatureUnit.toString(), jsonArrayMainJSONObject.getString("temperatureUnit"));

                        contentValues.put(TableBabyMonitoring.tableColumn.babySpO2.toString(), jsonArrayMainJSONObject.getString("spo2"));
                        contentValues.put(TableBabyMonitoring.tableColumn.babyPulseRate.toString(), jsonArrayMainJSONObject.getString("pulseRate"));

                        contentValues.put(TableBabyMonitoring.tableColumn.isCftGreaterThree.toString(), jsonArrayMainJSONObject.getString("isCrtGreaterThree"));

                        contentValues.put(TableBabyMonitoring.tableColumn.urinePassedIn24Hrs.toString(), jsonArrayMainJSONObject.getString("urinePassedIn24Hrs"));
                        contentValues.put(TableBabyMonitoring.tableColumn.stoolPassedIn24Hrs.toString(), jsonArrayMainJSONObject.getString("stoolPassedIn24Hrs"));
                        contentValues.put(TableBabyMonitoring.tableColumn.generalCondition.toString(), jsonArrayMainJSONObject.getString("alertness"));
                        contentValues.put(TableBabyMonitoring.tableColumn.tone.toString(), jsonArrayMainJSONObject.getString("tone"));
                        contentValues.put(TableBabyMonitoring.tableColumn.apneaOrGasping.toString(), jsonArrayMainJSONObject.getString("apneaOrGasping"));
                        contentValues.put(TableBabyMonitoring.tableColumn.grunting.toString(), jsonArrayMainJSONObject.getString("grunting"));
                        contentValues.put(TableBabyMonitoring.tableColumn.chestIndrawing.toString(), jsonArrayMainJSONObject.getString("chestIndrawing"));
                        contentValues.put(TableBabyMonitoring.tableColumn.color.toString(), jsonArrayMainJSONObject.getString("color"));
                        contentValues.put(TableBabyMonitoring.tableColumn.sucking.toString(), jsonArrayMainJSONObject.getString("sucking"));
                        contentValues.put(TableBabyMonitoring.tableColumn.isBleeding.toString(), jsonArrayMainJSONObject.getString("isBleeding"));
                        contentValues.put(TableBabyMonitoring.tableColumn.isInterestInFeeding.toString(), jsonArrayMainJSONObject.getString("interestInFeeding"));
                        contentValues.put(TableBabyMonitoring.tableColumn.lactation.toString(), jsonArrayMainJSONObject.getString("sufficientLactation"));
                        contentValues.put(TableBabyMonitoring.tableColumn.bulgingAnteriorFontanel.toString(), jsonArrayMainJSONObject.getString("bulgingAnteriorFontanel"));
                        contentValues.put(TableBabyMonitoring.tableColumn.umbilicus.toString(), jsonArrayMainJSONObject.getString("umbilicus"));
                        contentValues.put(TableBabyMonitoring.tableColumn.skinPustules.toString(), jsonArrayMainJSONObject.getString("skinPustules"));
                        contentValues.put(TableBabyMonitoring.tableColumn.staffId.toString(), jsonArrayMainJSONObject.getString("staffId"));
                        contentValues.put(TableBabyMonitoring.tableColumn.assessmentNumber.toString(), jsonArrayMainJSONObject.getString("assesmentNumber"));
                        contentValues.put(TableBabyMonitoring.tableColumn.syncTime.toString(), jsonArrayMainJSONObject.getString("addDate"));
                        contentValues.put(TableBabyMonitoring.tableColumn.addDate.toString(), jsonArrayMainJSONObject.getString("addDate"));
                        contentValues.put(TableBabyMonitoring.tableColumn.modifyDate.toString(), jsonArrayMainJSONObject.getString("modifyDate"));
                        contentValues.put(TableBabyMonitoring.tableColumn.formattedDate.toString(), jsonArrayMainJSONObject.getString("assesmentDate"));
                        contentValues.put(TableBabyMonitoring.tableColumn.status.toString(), "1");
                        contentValues.put(TableBabyMonitoring.tableColumn.staffSign.toString(), "");
                        contentValues.put(TableBabyMonitoring.tableColumn.isDataSynced.toString(), "1");
                        contentValues.put(TableBabyMonitoring.tableColumn.json.toString(), "");

                        DatabaseController.insertData(contentValues, TableBabyMonitoring.tableName);
                    }
                }

                if (arrayJSONObject.has("motherRegistrationData")) {

                    Log.d("position step 3 - ", String.valueOf(position));

                    JSONObject jsonMotherRegistration = arrayJSONObject.getJSONObject("motherRegistrationData");

                    ContentValues contentValues = new ContentValues();

                    motherId = jsonMotherRegistration.getString("motherId");

                    contentValues.put(TableMotherRegistration.tableColumn.uuid.toString(), jsonMotherRegistration.getString("androidUuid"));
                    contentValues.put(TableMotherRegistration.tableColumn.motherId.toString(), jsonMotherRegistration.getString("motherId"));
                    contentValues.put(TableMotherRegistration.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
                    contentValues.put(TableMotherRegistration.tableColumn.motherName.toString(), jsonMotherRegistration.getString("motherName"));
                    contentValues.put(TableMotherRegistration.tableColumn.isMotherAdmitted.toString(), jsonMotherRegistration.getString("isMotherAdmitted"));
                    contentValues.put(TableMotherRegistration.tableColumn.reasonForNotAdmitted.toString(), jsonMotherRegistration.getString("notAdmittedReason"));

                    contentValues.put(TableMotherRegistration.tableColumn.isDataSynced.toString(), "1");
                    contentValues.put(TableMotherRegistration.tableColumn.motherPicture.toString(), "");

                    if (!jsonMotherRegistration.getString("motherPicture").isEmpty()) {
                        HashMap<String, String> hashlist = new HashMap();
                        hashlist.put("url", AppSettings.getString(AppSettings.motherDirectoryUrl) + jsonMotherRegistration.getString("motherPicture"));
                        hashlist.put("from", "2");
                        hashlist.put("primaryId", jsonMotherRegistration.getString("motherId"));

                        arrayList.add(hashlist);
                    }

                    contentValues.put(TableMotherRegistration.tableColumn.motherMCTSNumber.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.motherAadharNumber.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.motherDob.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.motherAge.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.motherEducation.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.motherCaste.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.motherReligion.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.motherMoblieNo.toString(), jsonMotherRegistration.getString("motherMobileNumber"));
                    contentValues.put(TableMotherRegistration.tableColumn.fatherName.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.fatherAadharNumber.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.fatherMoblieNo.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.rationCardType.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.guardianName.toString(), jsonMotherRegistration.getString("guardianName"));
                    contentValues.put(TableMotherRegistration.tableColumn.guardianNumber.toString(), jsonMotherRegistration.getString("guardianNumber"));
                    contentValues.put(TableMotherRegistration.tableColumn.guardianRelation.toString(), jsonMotherRegistration.getString("guardianRelation"));
                    contentValues.put(TableMotherRegistration.tableColumn.presentCountry.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.presentState.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.presentResidenceType.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.presentAddress.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.presentVillageName.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.presentBlockName.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.presentDistrictName.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.presentPinCode.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.presentAddressNearBy.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.permanentResidenceType.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.permanentCountry.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.permanentState.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.permanentAddress.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.permanentVillageName.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.permanentBlockName.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.permanentDistrictName.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.permanentPinCode.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.permanentAddressNearBy.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.type.toString(), jsonMotherRegistration.getString("type"));
                    contentValues.put(TableMotherRegistration.tableColumn.para.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.gravida.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.abortion.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.live.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.multipleBirth.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.consanguinity.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.motherWeight.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.ageOfMarriage.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.estimatedDateOfDelivery.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.birthSpacing.toString(), "");

                    contentValues.put(TableMotherRegistration.tableColumn.multipleBirth.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.motherLmpDate.toString(), jsonMotherRegistration.getString("motherLmpDate"));
                    contentValues.put(TableMotherRegistration.tableColumn.motherDeliveryDistrict.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.sameaddress.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.ashaID.toString(), "");

                    contentValues.put(TableMotherRegistration.tableColumn.ashaName.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.ashaNumber.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.facilityID.toString(), "");

                    contentValues.put(TableMotherRegistration.tableColumn.organisationAddress.toString(), jsonMotherRegistration.getString("organisationAddress"));
                    contentValues.put(TableMotherRegistration.tableColumn.organisationName.toString(), jsonMotherRegistration.getString("organisationName"));
                    contentValues.put(TableMotherRegistration.tableColumn.organisationNumber.toString(), jsonMotherRegistration.getString("organisationNumber"));

                    contentValues.put(TableMotherRegistration.tableColumn.syncTime.toString(), jsonMotherRegistration.getString("addDate"));
                    contentValues.put(TableMotherRegistration.tableColumn.addDate.toString(), jsonMotherRegistration.getString("addDate"));
                    contentValues.put(TableMotherRegistration.tableColumn.modifyDate.toString(), jsonMotherRegistration.getString("modifyDate"));
                    contentValues.put(TableMotherRegistration.tableColumn.status.toString(), "1");
                    contentValues.put(TableMotherRegistration.tableColumn.admittedSign.toString(), "");
                    contentValues.put(TableMotherRegistration.tableColumn.isDataSynced.toString(), "1");
                    contentValues.put(TableMotherRegistration.tableColumn.motherDeliveryPlace.toString(), "");

                    DatabaseController.insertData(contentValues, TableMotherRegistration.tableName);

                }

                if (arrayJSONObject.has("motherAdmissionData")) {

                    Log.d("position step 4 - ", String.valueOf(position));

                    JSONObject jsonMotherAdmission = arrayJSONObject.getJSONObject("motherAdmissionData");

                    ContentValues cvMotherAdm = new ContentValues();

                    cvMotherAdm.put(TableMotherAdmission.tableColumn.uuid.toString(), jsonMotherAdmission.getString("androidUuid"));
                    cvMotherAdm.put(TableMotherAdmission.tableColumn.serverId.toString(), jsonMotherAdmission.getString("id"));
                    cvMotherAdm.put(TableMotherAdmission.tableColumn.motherId.toString(), jsonMotherAdmission.getString("motherId"));
                    cvMotherAdm.put(TableMotherAdmission.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
                    cvMotherAdm.put(TableMotherAdmission.tableColumn.status.toString(), "1");
                    cvMotherAdm.put(TableMotherAdmission.tableColumn.isDataSynced.toString(), "1");

                    DatabaseController.insertData(cvMotherAdm, TableMotherAdmission.tableName);
                }

                if (arrayJSONObject.has("motherMonitoring")) {

                    JSONArray jsonArrayMain = arrayJSONObject.getJSONArray("motherMonitoring");

                    for (int j = 0; j < jsonArrayMain.length(); j++) {

                        JSONObject jsonArrayMainJSONObject = jsonArrayMain.getJSONObject(j);

                        ContentValues mContentValues = new ContentValues();

                        mContentValues.put(TableMotherMonitoring.tableColumn.serverId.toString(), jsonArrayMainJSONObject.getString("id"));
                        mContentValues.put(TableMotherMonitoring.tableColumn.uuid.toString(), jsonArrayMainJSONObject.getString("androidUuid"));
                        mContentValues.put(TableMotherMonitoring.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
                        mContentValues.put(TableMotherMonitoring.tableColumn.isDataSynced.toString(), "1");
                        mContentValues.put(TableMotherMonitoring.tableColumn.motherId.toString(), motherId);
                        mContentValues.put(TableMotherMonitoring.tableColumn.motherTemperature.toString(), jsonArrayMainJSONObject.getString("motherTemperature"));
                        mContentValues.put(TableMotherMonitoring.tableColumn.temperatureUnit.toString(), jsonArrayMainJSONObject.getString("temperatureUnit"));

                        mContentValues.put(TableMotherMonitoring.tableColumn.motherAdmissionId.toString(), jsonArrayMainJSONObject.getString("motherAdmissionId"));
                        mContentValues.put(TableMotherMonitoring.tableColumn.motherSystolicBP.toString(), jsonArrayMainJSONObject.getString("motherSystolicBP"));
                        mContentValues.put(TableMotherMonitoring.tableColumn.motherDiastolicBP.toString(), jsonArrayMainJSONObject.getString("motherDiastolicBP"));
                        mContentValues.put(TableMotherMonitoring.tableColumn.motherPulse.toString(), jsonArrayMainJSONObject.getString("motherPulse"));
                        mContentValues.put(TableMotherMonitoring.tableColumn.motherUterineTone.toString(), jsonArrayMainJSONObject.getString("motherUterineTone"));

                        mContentValues.put(TableMotherMonitoring.tableColumn.staffId.toString(), jsonArrayMainJSONObject.getString("staffId"));
                        mContentValues.put(TableMotherMonitoring.tableColumn.other.toString(), jsonArrayMainJSONObject.getString("other"));
                        mContentValues.put(TableMotherMonitoring.tableColumn.assesmentNumber.toString(),
                                jsonArrayMainJSONObject.getString("assesmentNumber"));

                        mContentValues.put(TableMotherMonitoring.tableColumn.isDataComplete.toString(), "1");

                        mContentValues.put(TableMotherMonitoring.tableColumn.addDate.toString(), jsonArrayMainJSONObject.getString("addDate"));
                        mContentValues.put(TableMotherMonitoring.tableColumn.modifyDate.toString(), jsonArrayMainJSONObject.getString("modifyDate"));
                        mContentValues.put(TableMotherMonitoring.tableColumn.status.toString(), "1");

                        DatabaseController.insertData(mContentValues, TableMotherMonitoring.tableName);
                    }
                }

                if (arrayJSONObject.has("babyDailyNutrition")) {

                    JSONArray jsonArrayMain = arrayJSONObject.getJSONArray("babyDailyNutrition");

                    for (int j = 0; j < jsonArrayMain.length(); j++) {

                        JSONObject jsonArrayMainJSONObject = jsonArrayMain.getJSONObject(j);

                        ContentValues mContentValues = new ContentValues();

                        mContentValues.put(TableBreastFeeding.tableColumn.uuid.toString(), jsonArrayMainJSONObject.getString("androidUuid"));
                        mContentValues.put(TableBreastFeeding.tableColumn.serverId.toString(), jsonArrayMainJSONObject.getString("id"));
                        mContentValues.put(TableBreastFeeding.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
                        mContentValues.put(TableBreastFeeding.tableColumn.babyId.toString(), babyId);
                        mContentValues.put(TableBreastFeeding.tableColumn.nurseId.toString(), jsonArrayMainJSONObject.getString("nurseId"));
                        mContentValues.put(TableBreastFeeding.tableColumn.feedTime.toString(), jsonArrayMainJSONObject.getString("feedTime"));
                        mContentValues.put(TableBreastFeeding.tableColumn.duration.toString(), "");
                        mContentValues.put(TableBreastFeeding.tableColumn.method.toString(), jsonArrayMainJSONObject.getString("breastFeedMethod"));
                        mContentValues.put(TableBreastFeeding.tableColumn.fluid.toString(), jsonArrayMainJSONObject.getString("fluid"));
                        mContentValues.put(TableBreastFeeding.tableColumn.specify.toString(), jsonArrayMainJSONObject.getString("specify"));
                        mContentValues.put(TableBreastFeeding.tableColumn.quantity.toString(), jsonArrayMainJSONObject.getString("milkQuantity"));
                        mContentValues.put(TableBreastFeeding.tableColumn.date.toString(), jsonArrayMainJSONObject.getString("feedDate"));
                        mContentValues.put(TableBreastFeeding.tableColumn.addDate.toString(), jsonArrayMainJSONObject.getString("addDate"));
                        mContentValues.put(TableBreastFeeding.tableColumn.modifyDate.toString(), jsonArrayMainJSONObject.getString("addDate"));
                        mContentValues.put(TableBreastFeeding.tableColumn.syncTime.toString(), jsonArrayMainJSONObject.getString("lastSyncedTime"));
                        mContentValues.put(TableBreastFeeding.tableColumn.status.toString(), "1");
                        mContentValues.put(TableBreastFeeding.tableColumn.isDataSynced.toString(), "1");

                        DatabaseController.insertData(mContentValues, TableBreastFeeding.tableName);
                    }
                }

                if (arrayJSONObject.has("babyDailyWeight")) {

                    JSONArray jsonArrayMain = arrayJSONObject.getJSONArray("babyDailyWeight");

                    for (int j = 0; j < jsonArrayMain.length(); j++) {

                        JSONObject jsonArrayMainJSONObject = jsonArrayMain.getJSONObject(j);

                        ContentValues cvBabyWeight = new ContentValues();

                        cvBabyWeight.put(TableWeight.tableColumn.uuid.toString(), jsonArrayMainJSONObject.getString("androidUuid"));
                        cvBabyWeight.put(TableWeight.tableColumn.serverId.toString(), jsonArrayMainJSONObject.getString("id"));
                        cvBabyWeight.put(TableWeight.tableColumn.babyAdmissionId.toString(), jsonArrayMainJSONObject.getString("babyAdmissionId"));
                        cvBabyWeight.put(TableWeight.tableColumn.babyId.toString(), babyId);
                        cvBabyWeight.put(TableWeight.tableColumn.nurseId.toString(), jsonArrayMainJSONObject.getString("nurseId"));
                        cvBabyWeight.put(TableWeight.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
                        cvBabyWeight.put(TableWeight.tableColumn.weightDate.toString(), jsonArrayMainJSONObject.getString("weightDate"));
                        cvBabyWeight.put(TableWeight.tableColumn.isWeighingAvail.toString(), jsonArrayMainJSONObject.getString("isDeviceAvailAndWorking"));

                        cvBabyWeight.put(TableWeight.tableColumn.isDataSynced.toString(), "1");

                        cvBabyWeight.put(TableWeight.tableColumn.weightImage.toString(), "");

                        if (!jsonArrayMainJSONObject.getString("babyWeightImage").isEmpty()) {
                            HashMap<String, String> hashlist = new HashMap();
                            hashlist.put("url", AppSettings.getString(AppSettings.babyWeightDirectoryUrl) + jsonArrayMainJSONObject.getString("babyWeightImage"));
                            hashlist.put("from", "3");
                            hashlist.put("primaryId", jsonArrayMainJSONObject.getString("androidUuid"));

                            arrayList.add(hashlist);
                        }

                        cvBabyWeight.put(TableWeight.tableColumn.babyWeight.toString(), jsonArrayMainJSONObject.getString("babyWeight"));
                        cvBabyWeight.put(TableWeight.tableColumn.weighingReason.toString(), "");

                        cvBabyWeight.put(TableWeight.tableColumn.addDate.toString(), jsonArrayMainJSONObject.getString("addDate"));

                        DatabaseController.insertData(cvBabyWeight, TableWeight.tableName);

                        Log.v("lksjjqksq", babyId + "   " + jsonArrayMainJSONObject.getString("babyWeight"));
                    }
                }

                if (arrayJSONObject.has("doctorRound")) {

                    JSONArray jsonArrayMain = arrayJSONObject.getJSONArray("doctorRound");

                    for (int j = 0; j < jsonArrayMain.length(); j++) {

                        JSONObject jsonArrayMainJSONObject = jsonArrayMain.getJSONObject(j);

                        ContentValues mContentValues = new ContentValues();

                        mContentValues.put(TableDoctorRound.tableColumn.uuid.toString(), jsonArrayMainJSONObject.getString("androidUuid"));
                        mContentValues.put(TableDoctorRound.tableColumn.serverId.toString(), jsonArrayMainJSONObject.getString("id"));
                        mContentValues.put(TableDoctorRound.tableColumn.doctorId.toString(), jsonArrayMainJSONObject.getString("staffId"));
                        mContentValues.put(TableDoctorRound.tableColumn.babyId.toString(), babyId);
                        mContentValues.put(TableDoctorRound.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
                        mContentValues.put(TableDoctorRound.tableColumn.signature.toString(), "");
                        mContentValues.put(TableDoctorRound.tableColumn.syncTime.toString(), "");
                        mContentValues.put(TableDoctorRound.tableColumn.addDate.toString(), jsonArrayMainJSONObject.getString("addDate"));
                        mContentValues.put(TableDoctorRound.tableColumn.modifyDate.toString(), jsonArrayMainJSONObject.getString("addDate"));
                        mContentValues.put(TableDoctorRound.tableColumn.status.toString(), "1");
                        mContentValues.put(TableDoctorRound.tableColumn.isDataSynced.toString(), "1");
                        mContentValues.put(TableDoctorRound.tableColumn.json.toString(), jsonArrayMainJSONObject.toString());

                        DatabaseController.insertData(mContentValues, TableDoctorRound.tableName);

                        JSONArray jsonArrayDocRoundTreatment = jsonArrayMainJSONObject.getJSONArray("treatment");

                        if (jsonArrayDocRoundTreatment.length() > 0) {

                            for (int i = 0; i < jsonArrayDocRoundTreatment.length(); i++) {

                                JSONObject jsonObject = jsonArrayDocRoundTreatment.getJSONObject(i);

                               /* mContentValues.put(TableTreatment.tableColumn.uuid.toString(), AppConstants.treatmentList.get(i).get("uuid"));
                                mContentValues.put(TableTreatment.tableColumn.treatmentName.toString(), AppConstants.treatmentList.get(i).get("treatmentName"));
                                mContentValues.put(TableTreatment.tableColumn.babyId.toString(), AppSettings.getString(AppSettings.babyId));
                                mContentValues.put(TableTreatment.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
                                mContentValues.put(TableTreatment.tableColumn.comment.toString(), AppConstants.treatmentList.get(i).get("comment"));
                                mContentValues.put(TableTreatment.tableColumn.image.toString(), AppConstants.treatmentList.get(i).get("notePicture"));
                                mContentValues.put(TableTreatment.tableColumn.quantity.toString(), "");
                                //  mContentValues.put(TableTreatment.tableColumn.doctorId.toString(), doctorId.get(spinnerDoctor.getSelectedItemPosition()));
                                mContentValues.put(TableTreatment.tableColumn.doctorId.toString(), selectedDoctorId);
                                mContentValues.put(TableTreatment.tableColumn.doctorName.toString(), tvSelectDoctor.getText().toString().trim());
                                mContentValues.put(TableTreatment.tableColumn.type.toString(), "1");
                                mContentValues.put(TableTreatment.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
*/
                                ContentValues mContentValues2 = new ContentValues();

                                mContentValues2.put(TableTreatment.tableColumn.uuid.toString(), jsonObject.getString("localId"));
                                mContentValues2.put(TableTreatment.tableColumn.treatmentName.toString(), jsonObject.getString("treatmentName"));
                                mContentValues2.put(TableTreatment.tableColumn.comment.toString(), jsonObject.getString("comment"));
                                mContentValues2.put(TableTreatment.tableColumn.babyId.toString(), babyId);
                                mContentValues2.put(TableTreatment.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
                                mContentValues2.put(TableTreatment.tableColumn.doctorId.toString(), jsonObject.getString("doctorId"));
                                mContentValues2.put(TableTreatment.tableColumn.doctorName.toString(), jsonObject.getString("doctorName"));
                                mContentValues2.put(TableTreatment.tableColumn.addDate.toString(), jsonObject.getString("localDateTime"));
                                mContentValues2.put(TableTreatment.tableColumn.status.toString(), jsonObject.getString("status"));

                                Log.v("lksjhqkshqjq", babyId + "   " + jsonObject.getString("comment"));
                                Log.v("lksjhqkshqjq", babyId + "   " + jsonObject.getString("status"));

                                DatabaseController.insertData(mContentValues2, TableTreatment.tableName);

                            }
                        }
                    }
                }

                if (arrayJSONObject.has("babyDailyKMC")) {

                    JSONArray jsonArrayMain = arrayJSONObject.getJSONArray("babyDailyKMC");

                    for (int j = 0; j < jsonArrayMain.length(); j++) {

                        JSONObject jsonArrayMainJSONObject = jsonArrayMain.getJSONObject(j);

                        ContentValues mContentValues = new ContentValues();

                        mContentValues.put(TableKMC.tableColumn.serverId.toString(), jsonArrayMainJSONObject.getString("id"));
                        mContentValues.put(TableKMC.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
                        mContentValues.put(TableKMC.tableColumn.uuid.toString(), jsonArrayMainJSONObject.getString("androidUuid"));
                        mContentValues.put(TableKMC.tableColumn.babyId.toString(), babyId);
                        mContentValues.put(TableKMC.tableColumn.startTime.toString(), jsonArrayMainJSONObject.getString("startTime"));
                        mContentValues.put(TableKMC.tableColumn.endTime.toString(), jsonArrayMainJSONObject.getString("endTime"));
                        mContentValues.put(TableKMC.tableColumn.provider.toString(), jsonArrayMainJSONObject.getString("provider"));
                        mContentValues.put(TableKMC.tableColumn.startDate.toString(), jsonArrayMainJSONObject.getString("startDate"));
                        mContentValues.put(TableKMC.tableColumn.endDate.toString(), jsonArrayMainJSONObject.getString("startDate"));
                        mContentValues.put(TableKMC.tableColumn.addDate.toString(), jsonArrayMainJSONObject.getString("addDate"));
                        mContentValues.put(TableKMC.tableColumn.modifyDate.toString(), jsonArrayMainJSONObject.getString("addDate"));
                        mContentValues.put(TableKMC.tableColumn.syncTime.toString(), jsonArrayMainJSONObject.getString("lastSyncedTime"));
                        mContentValues.put(TableKMC.tableColumn.isDataSynced.toString(), "1");
                        mContentValues.put(TableKMC.tableColumn.status.toString(), "1");
                        mContentValues.put(TableKMC.tableColumn.nurseId.toString(), jsonArrayMainJSONObject.getString("nurseId"));

                        DatabaseController.insertData(mContentValues, TableKMC.tableName);
                    }
                }

                if (arrayJSONObject.has("investigation")) {

                    JSONArray jsonArrayMain = arrayJSONObject.getJSONArray("investigation");

                    for (int j = 0; j < jsonArrayMain.length(); j++) {

                        JSONObject jsonArrayMainJSONObject = jsonArrayMain.getJSONObject(j);

                        ContentValues mContentValues = new ContentValues();

                        mContentValues.put(TableInvestigation.tableColumn.serverId.toString(), jsonArrayMainJSONObject.getString("id"));
                        mContentValues.put(TableInvestigation.tableColumn.uuid.toString(), jsonArrayMainJSONObject.getString("androidUuid"));
                        mContentValues.put(TableInvestigation.tableColumn.investigationType.toString(), jsonArrayMainJSONObject.getString("investigationType"));
                        mContentValues.put(TableInvestigation.tableColumn.investigationName.toString(), jsonArrayMainJSONObject.getString("investigationName"));
                        mContentValues.put(TableInvestigation.tableColumn.babyId.toString(), babyId);
                        mContentValues.put(TableInvestigation.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
                        mContentValues.put(TableInvestigation.tableColumn.doctorComment.toString(), jsonArrayMainJSONObject.getString("doctorComment"));
                        mContentValues.put(TableInvestigation.tableColumn.doctorId.toString(), jsonArrayMainJSONObject.getString("doctorId"));
                        mContentValues.put(TableInvestigation.tableColumn.doctorName.toString(), jsonArrayMainJSONObject.getString("doctorName"));
                        mContentValues.put(TableInvestigation.tableColumn.status.toString(), jsonArrayMainJSONObject.getString("status"));
                        mContentValues.put(TableInvestigation.tableColumn.isDataSynced.toString(), "1");
                        mContentValues.put(TableInvestigation.tableColumn.resultImage.toString(), "");

                        Log.v("refgwewefwefw", babyId + "   " + jsonArrayMainJSONObject.getString("status"));
                        Log.v("refgwewefwefw", babyId + "   " + jsonArrayMainJSONObject.getString("doctorComment" + ""));

                        if (!jsonArrayMainJSONObject.getString("resultImage").isEmpty()) {
                            HashMap<String, String> hashlist = new HashMap();
                            hashlist.put("url", AppSettings.getString(AppSettings.investigationDirectoryUrl) + jsonArrayMainJSONObject.getString("resultImage"));
                            hashlist.put("from", "4");
                            hashlist.put("primaryId", jsonArrayMainJSONObject.getString("androidUuid"));
                            arrayList.add(hashlist);
                        }

                        mContentValues.put(TableInvestigation.tableColumn.result.toString(), jsonArrayMainJSONObject.getString("result"));
                        mContentValues.put(TableInvestigation.tableColumn.sampleComment.toString(), jsonArrayMainJSONObject.getString("sampleComment"));

                        mContentValues.put(TableInvestigation.tableColumn.sampleImage.toString(), "");

                        if (!jsonArrayMainJSONObject.getString("sampleImage").isEmpty()) {
                            HashMap<String, String> hashlist = new HashMap();
                            hashlist.put("url", AppSettings.getString(AppSettings.investigationDirectoryUrl) + jsonArrayMainJSONObject.getString("sampleImage"));
                            hashlist.put("from", "5");
                            hashlist.put("primaryId", jsonArrayMainJSONObject.getString("androidUuid"));

                            arrayList.add(hashlist);
                        }

                        mContentValues.put(TableInvestigation.tableColumn.sampleTakenOn.toString(), jsonArrayMainJSONObject.getString("sampleDate"));
                        mContentValues.put(TableInvestigation.tableColumn.sampleTakenBy.toString(), jsonArrayMainJSONObject.getString("nurseId"));
                        mContentValues.put(TableInvestigation.tableColumn.nurseComment.toString(), jsonArrayMainJSONObject.getString("nurseComment"));
                        mContentValues.put(TableInvestigation.tableColumn.invesDate.toString(), jsonArrayMainJSONObject.getString("addDate"));
                        mContentValues.put(TableInvestigation.tableColumn.nurseId.toString(), jsonArrayMainJSONObject.getString("nurseId"));
                        mContentValues.put(TableInvestigation.tableColumn.addDate.toString(), jsonArrayMainJSONObject.getString("addDate"));
                        mContentValues.put(TableInvestigation.tableColumn.modifyDate.toString(), jsonArrayMainJSONObject.getString("modifyDate"));

                        DatabaseController.insertData(mContentValues, TableInvestigation.tableName);
                    }
                }

                if (arrayJSONObject.has("prescriptionNurseWise")) {

                    JSONArray jsonArrayMain = arrayJSONObject.getJSONArray("prescriptionNurseWise");

                    for (int j = 0; j < jsonArrayMain.length(); j++) {

                        JSONObject jsonArrayMainJSONObject = jsonArrayMain.getJSONObject(j);

                        ContentValues mContentValues = new ContentValues();

                        mContentValues.put(TableTreatment.tableColumn.serverId.toString(), jsonArrayMainJSONObject.getString("id"));
                        mContentValues.put(TableTreatment.tableColumn.uuid.toString(), jsonArrayMainJSONObject.getString("androidUuid"));
                        mContentValues.put(TableTreatment.tableColumn.treatmentName.toString(), jsonArrayMainJSONObject.getString("prescriptionName"));
                        mContentValues.put(TableTreatment.tableColumn.givenDate.toString(), jsonArrayMainJSONObject.getString("addDate"));
                        mContentValues.put(TableTreatment.tableColumn.givenTime.toString(), jsonArrayMainJSONObject.getString("prescriptionTime"));
                        mContentValues.put(TableTreatment.tableColumn.comment.toString(), jsonArrayMainJSONObject.getString("comment"));
                        mContentValues.put(TableTreatment.tableColumn.babyId.toString(), babyId);
                        mContentValues.put(TableTreatment.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
                        mContentValues.put(TableTreatment.tableColumn.image.toString(), "");
                        mContentValues.put(TableTreatment.tableColumn.unit.toString(), "");
                        mContentValues.put(TableTreatment.tableColumn.quantity.toString(), jsonArrayMainJSONObject.getString("quantity"));
                        mContentValues.put(TableTreatment.tableColumn.type.toString(), "2");
                        mContentValues.put(TableTreatment.tableColumn.addDate.toString(), jsonArrayMainJSONObject.getString("addDate"));
                        mContentValues.put(TableTreatment.tableColumn.isDataSynced.toString(), "1");

                        DatabaseController.insertData(mContentValues, TableTreatment.tableName);
                    }
                }

                if (arrayJSONObject.has("babyVaccination")) {

                    JSONArray jsonArrayMain = arrayJSONObject.getJSONArray("babyVaccination");

                    for (int j = 0; j < jsonArrayMain.length(); j++) {

                        JSONObject jsonArrayMainJSONObject = jsonArrayMain.getJSONObject(j);

                        ContentValues mContentValues = new ContentValues();

                        mContentValues.put(TableVaccination.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
                        mContentValues.put(TableVaccination.tableColumn.serverId.toString(), jsonArrayMainJSONObject.getString("id"));
                        mContentValues.put(TableVaccination.tableColumn.uuid.toString(), jsonArrayMainJSONObject.getString("androidUuid"));
                        mContentValues.put(TableVaccination.tableColumn.babyId.toString(), babyId);
                        mContentValues.put(TableVaccination.tableColumn.vaccName.toString(), jsonArrayMainJSONObject.getString("vaccinationName"));
                        mContentValues.put(TableVaccination.tableColumn.date.toString(), jsonArrayMainJSONObject.getString("vaccinationDate"));
                        mContentValues.put(TableVaccination.tableColumn.quantity.toString(), "");
                        mContentValues.put(TableVaccination.tableColumn.time.toString(), jsonArrayMainJSONObject.getString("vaccinationTime"));
                        mContentValues.put(TableVaccination.tableColumn.isDataSynced.toString(), "1");
                        mContentValues.put(TableVaccination.tableColumn.addDate.toString(), jsonArrayMainJSONObject.getString("addDate"));
                        mContentValues.put(TableVaccination.tableColumn.modifyDate.toString(), jsonArrayMainJSONObject.getString("addDate"));
                        mContentValues.put(TableVaccination.tableColumn.nurseId.toString(), jsonArrayMainJSONObject.getString("nurseId"));
                        mContentValues.put(TableVaccination.tableColumn.syncTime.toString(), jsonArrayMainJSONObject.getString("lastSyncedTime"));
                        mContentValues.put(TableVaccination.tableColumn.status.toString(), "1");

                        DatabaseController.insertData(mContentValues, TableVaccination.tableName);
                    }
                }

                if (arrayJSONObject.has("motherComments")) {

                    JSONArray jsonArrayMain = arrayJSONObject.getJSONArray("motherComments");

                    for (int j = 0; j < jsonArrayMain.length(); j++) {

                        JSONObject jsonArrayMainJSONObject = jsonArrayMain.getJSONObject(j);

                        ContentValues mContentValues = new ContentValues();

                        mContentValues.put(TableComments.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
                        mContentValues.put(TableComments.tableColumn.serverId.toString(), jsonArrayMainJSONObject.getString("id"));
                        mContentValues.put(TableComments.tableColumn.uuid.toString(), jsonArrayMainJSONObject.getString("androidUuid"));
                        mContentValues.put(TableComments.tableColumn.doctorId.toString(), jsonArrayMainJSONObject.getString("doctorId"));
                        mContentValues.put(TableComments.tableColumn.comment.toString(), jsonArrayMainJSONObject.getString("comment"));
                        mContentValues.put(TableComments.tableColumn.isDataSynced.toString(), "1");
                        mContentValues.put(TableComments.tableColumn.addDate.toString(), jsonArrayMainJSONObject.getString("addDate"));
                        mContentValues.put(TableComments.tableColumn.modifyDate.toString(), jsonArrayMainJSONObject.getString("modifyDate"));
                        mContentValues.put(TableComments.tableColumn.syncTime.toString(), jsonArrayMainJSONObject.getString("lastSyncedTime"));
                        mContentValues.put(TableComments.tableColumn.status.toString(), jsonArrayMainJSONObject.getString("status"));
                        mContentValues.put(TableComments.tableColumn.isDataSynced.toString(), "1");
                        mContentValues.put(TableComments.tableColumn.motherOrBabyId.toString(), motherId);
                        mContentValues.put(TableComments.tableColumn.type.toString(), "1");

                        DatabaseController.insertData(mContentValues, TableComments.tableName);
                    }
                }

                if (arrayJSONObject.has("babyComments")) {

                    Log.d("position step Last - ", String.valueOf(position));

                    JSONArray jsonArrayMain = arrayJSONObject.getJSONArray("babyComments");

                    for (int j = 0; j < jsonArrayMain.length(); j++) {

                        JSONObject jsonArrayMainJSONObject = jsonArrayMain.getJSONObject(j);

                        ContentValues mContentValues = new ContentValues();

                        mContentValues.put(TableComments.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
                        mContentValues.put(TableComments.tableColumn.serverId.toString(), jsonArrayMainJSONObject.getString("id"));
                        mContentValues.put(TableComments.tableColumn.uuid.toString(), jsonArrayMainJSONObject.getString("androidUuid"));
                        mContentValues.put(TableComments.tableColumn.doctorId.toString(), jsonArrayMainJSONObject.getString("doctorId"));
                        mContentValues.put(TableComments.tableColumn.comment.toString(), jsonArrayMainJSONObject.getString("comment"));
                        mContentValues.put(TableComments.tableColumn.isDataSynced.toString(), "1");
                        mContentValues.put(TableComments.tableColumn.addDate.toString(), jsonArrayMainJSONObject.getString("addDate"));
                        mContentValues.put(TableComments.tableColumn.modifyDate.toString(), jsonArrayMainJSONObject.getString("modifyDate"));
                        mContentValues.put(TableComments.tableColumn.syncTime.toString(), jsonArrayMainJSONObject.getString("lastSyncedTime"));
                        mContentValues.put(TableComments.tableColumn.status.toString(), jsonArrayMainJSONObject.getString("status"));
                        mContentValues.put(TableComments.tableColumn.isDataSynced.toString(), "1");

                        mContentValues.put(TableComments.tableColumn.motherOrBabyId.toString(), babyId);
                        mContentValues.put(TableComments.tableColumn.type.toString(), "2");

                        DatabaseController.insertData(mContentValues, TableComments.tableName);
                    }
                }

                DatabaseController.myDataBase.setTransactionSuccessful();

            } finally {

                DatabaseController.myDataBase.endTransaction();

                position = position + 1;
                if (jsonArray.length() > position) {
                    saveData(position, jsonArray);
                } else {
                    if (arrayList.size() > 0) {
                        downloadImage();
                    } else {
                        setLogin();
                    }
                }
            }
        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().setCustomKey("SaveDataException",e.getMessage());
            e.printStackTrace();
        }
    }

    private void downloadImage() {
        Log.d("imagePosition", imgPosition + " of " + arrayList.size());

        WebServices.downloadImageApi(mActivity, arrayList.get(imgPosition).get("url"), new WebServicesImageCallback() {

            @Override
            public void OnFail(String responce) {

                imgPosition = imgPosition + 1;
                if (arrayList.size() > imgPosition) {
                    downloadImage();
                } else {
                    setLogin();
                }

            }

            @Override
            public void OnBitmapSuccess(Bitmap bitmap) {

                Log.d("bitmap-" + arrayList.get(imgPosition).get("from"), String.valueOf(bitmap));

                if (arrayList.get(imgPosition).get("from").equalsIgnoreCase("1")) {

                    String encodedString = getEncoded64ImageStringFromBitmap(bitmap);

                    ContentValues contentValues = new ContentValues();

                    contentValues.put(TableBabyRegistration.tableColumn.babyId.toString(), arrayList.get(imgPosition).get("primaryId"));
                    contentValues.put(TableBabyRegistration.tableColumn.babyPhoto.toString(), encodedString);

                    DatabaseController.insertUpdateData(contentValues,
                            TableBabyRegistration.tableName,
                            TableBabyRegistration.tableColumn.babyId.toString(),
                            arrayList.get(imgPosition).get("primaryId"));

                    AndroidNetworking.evictAllBitmap(); // clear LruCache
                } else if (arrayList.get(imgPosition).get("from").equalsIgnoreCase("2")) {
                    String encodedString = getEncoded64ImageStringFromBitmap(bitmap);

                    ContentValues contentValues = new ContentValues();

                    contentValues.put(TableMotherRegistration.tableColumn.motherId.toString(), arrayList.get(imgPosition).get("primaryId"));
                    contentValues.put(TableMotherRegistration.tableColumn.motherPicture.toString(), encodedString);

                    DatabaseController.insertUpdateData(contentValues,
                            TableMotherRegistration.tableName,
                            TableMotherRegistration.tableColumn.motherId.toString(),
                            arrayList.get(imgPosition).get("primaryId"));

                    AndroidNetworking.evictAllBitmap(); // clear LruCache

                } else if (arrayList.get(imgPosition).get("from").equalsIgnoreCase("3")) {
                    String encodedString = getEncoded64ImageStringFromBitmap(bitmap);

                    ContentValues contentValues = new ContentValues();

                    contentValues.put(TableWeight.tableColumn.uuid.toString(), arrayList.get(imgPosition).get("primaryId"));
                    contentValues.put(TableWeight.tableColumn.weightImage.toString(), encodedString);

                    DatabaseController.insertUpdateData(contentValues,
                            TableWeight.tableName,
                            TableWeight.tableColumn.uuid.toString(),
                            arrayList.get(imgPosition).get("primaryId"));

                    AndroidNetworking.evictAllBitmap(); // clear LruCache

                } else if (arrayList.get(imgPosition).get("from").equalsIgnoreCase("4")) {
                    String encodedString = getEncoded64ImageStringFromBitmap(bitmap);

                    ContentValues contentValues = new ContentValues();

                    contentValues.put(TableInvestigation.tableColumn.uuid.toString(), arrayList.get(imgPosition).get("primaryId"));
                    contentValues.put(TableInvestigation.tableColumn.resultImage.toString(), encodedString);

                    DatabaseController.insertUpdateData(contentValues,
                            TableInvestigation.tableName,
                            TableInvestigation.tableColumn.uuid.toString(),
                            arrayList.get(imgPosition).get("primaryId"));

                    AndroidNetworking.evictAllBitmap(); // clear LruCache

                } else if (arrayList.get(imgPosition).get("from").equalsIgnoreCase("5")) {
                    String encodedString = getEncoded64ImageStringFromBitmap(bitmap);

                    ContentValues contentValues = new ContentValues();

                    contentValues.put(TableInvestigation.tableColumn.uuid.toString(), arrayList.get(imgPosition).get("primaryId"));
                    contentValues.put(TableInvestigation.tableColumn.sampleImage.toString(), encodedString);

                    DatabaseController.insertUpdateData(contentValues,
                            TableInvestigation.tableName,
                            TableInvestigation.tableColumn.uuid.toString(),
                            arrayList.get(imgPosition).get("primaryId"));

                    AndroidNetworking.evictAllBitmap(); // clear LruCache

                }

                imgPosition = imgPosition + 1;
                if (arrayList.size() > imgPosition) {
                    downloadImage();
                } else {
                    setLogin();
                }
            }
        });
    }

    private void setLogin() {
        AppSettings.putString(AppSettings.from, "0");
        AppSettings.putString(AppSettings.userType, "0");

        if (DatabaseController.getNurseIdCheckedInData().size() == 0) {
            startActivity(new Intent(mActivity, CheckInActivity.class));
            finish();
        } else {
            AppSettings.putString(AppSettings.from, "0");
            startActivity(new Intent(mActivity, MainActivity.class));
            finish();
        }

        AppUtils.hideDialog();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    public class adapterSpinner extends ArrayAdapter<String> {

        ArrayList<String> data;

        public adapterSpinner(Context context, int textViewResourceId, ArrayList<String> arraySpinner_time) {

            super(context, textViewResourceId, arraySpinner_time);

            this.data = arraySpinner_time;

        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            View row = inflater.inflate(R.layout.inflate_spinner, parent, false);

            TextView tvName = row.findViewById(R.id.tvName);

            tvName.setText(data.get(position));

            return row;
        }
    }
}