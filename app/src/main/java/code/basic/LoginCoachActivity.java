package code.basic;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import code.coach.CoachLoungeSelectionActivity;
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

public class LoginCoachActivity extends BaseActivity implements View.OnClickListener {

    //EditText
    private EditText etMobileNumber,etPassword;

    //RelativeLayout
    private RelativeLayout rlLogin, rlHelp, rlStuck, rlOwn;

    //LinearLayout
    private LinearLayout llLanguage, llHelp;

    //ImageView
    private ImageView ivLogin;

    //TextView
    private TextView tvLoginAsLounge;

    //For Location
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private SettingsClient mSettingsClient;
    private LocationCallback mLocationCallback;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Location mCurrentLocation;
    final int REQUEST_CHECK_SETTINGS = 111;
    final int REQUEST_LOCATION = 222;
    public Boolean locUpdates = false;  // GPS/Network Location Updates are currently functioning
    public Boolean useGPS = false;     // tableName wants to use GPS/Network (based on setting) - turn off if permissions unsuccessful
    int position = 0;
    int imgPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_coach);

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
    }

    private void findViewById() {

        //EditText
        etPassword = findViewById(R.id.etPassword);
        etMobileNumber = findViewById(R.id.etMobileNumber);

        //TextView
        tvLoginAsLounge = findViewById(R.id.tvLoginAsLounge);

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

        forOpeningGeoLocation();

        rlLogin.setOnClickListener(this);
        rlHelp.setOnClickListener(this);
        rlStuck.setOnClickListener(this);
        rlOwn.setOnClickListener(this);
        llLanguage.setOnClickListener(this);
        llHelp.setOnClickListener(this);
        tvLoginAsLounge.setOnClickListener(this);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rlLogin:

                if (etMobileNumber.getText().toString().trim().isEmpty()) {
                    etMobileNumber.requestFocus();
                    AppUtils.showToastSort(mActivity, getString(R.string.errorContactNumber));
                }
                else  if(!AppUtils.isValidMobileNo(etMobileNumber.getText().toString()))
                {
                    etMobileNumber.requestFocus();
                    AppUtils.showToastSort(mActivity, getString(R.string.errorOrganisationNumber));
                }else if (etPassword.getText().toString().trim().isEmpty()) {
                    etPassword.requestFocus();
                    AppUtils.showToastSort(mActivity, getString(R.string.errorPassword));
                } else if (AppUtils.isNetworkAvailable(this.mActivity)) {
                    getTokenApi();
                } else {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                }

                break;

            case R.id.llLanguage:

                AppUtils.AlertLanguageConfirm(mActivity, getString(R.string.dataWillBeLost));

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

            case R.id.tvLoginAsLounge:

                startActivity(new Intent(mActivity, LoginActivity.class));
                finish();

                break;

            default:

                break;
        }
    }

    private void getTokenApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {

            jsonData.put("mobileNumber", etMobileNumber.getText().toString().trim());
            jsonData.put("password", etPassword.getText().toString().trim());
            jsonData.put("deviceId", AppUtils.getDeviceID(mActivity));

            json.put(AppConstants.projectName, jsonData);

            Log.v("getTokenApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.getCoachToken, json, true, false, new WebServicesCallback() {

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
                    } else {
                        AppUtils.hideDialog();
                        AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

                AppUtils.showToastSort(mActivity, responce);

            }
        });

    }

    private void loginApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();

        try {

            jsonData.put("mobileNumber", etMobileNumber.getText().toString().trim());
            jsonData.put("password", etPassword.getText().toString().trim());
            jsonData.put("deviceId", AppUtils.getDeviceID(mActivity));
            jsonData.put("imeiNumber", AppUtils.getDeviceIMEI(mActivity));
            jsonData.put("latitude", AppSettings.getString(AppSettings.latitude));
            jsonData.put("longitude", AppSettings.getString(AppSettings.longitude));
            jsonData.put("fcmId", AppSettings.getString(AppSettings.fcmId));

            json.put(AppConstants.projectName, jsonData);

            Log.v("getAllStaffApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.coachLogin, json, false, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        JSONObject loungeDetails = jsonObject.getJSONObject("loungeDetails");

                        AppSettings.putString(AppSettings.coachId, loungeDetails.getString("coachId"));
                        AppSettings.putString(AppSettings.loginId, loungeDetails.getString("loginId"));
                        AppSettings.putString(AppSettings.name, loungeDetails.getString("name"));
                        AppSettings.putString(AppSettings.userType, "1");

                        startActivity(new Intent(mActivity, CoachLoungeSelectionActivity.class));
                        finish();

                    } else {
                        AppUtils.hideDialog();
                        AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

                AppUtils.showToastSort(mActivity, responce);

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

    private void setLogin()
    {
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

    public static String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
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
                ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}