package code.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.kmcapp.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import code.algo.SyncAllRecord;
import code.checkIn.CheckInActivity;
import code.checkOut.CheckOutActivity;
import code.common.GetBackFragment;
import code.common.LocaleHelper;
import code.database.AppSettings;
import code.database.DataBaseHelper;
import code.database.DatabaseController;
import code.database.TableMotherRegistration;
import code.database.TableUser;
import code.fragment.LoungeAssessmentFragment;
import code.fragment.LoungeMonitoringFragment;
import code.fragment.SettingFragment;
import code.infantsFragment.InfantListingFragment;
import code.loungeFragment.FacilityBirthFragment;
import code.mothersFragment.MotherListingFragment;
import code.registration.RegistrationActivity;
import code.utils.AppConstants;
import code.utils.AppUtils;
import code.view.BaseActivity;
import code.view.BaseFragment;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    //Fragment
    static BaseFragment fragment;

    //TextView
    TextView tvHeading;

    //LinearLayout
    LinearLayout llSync,llHelp,llLogout,llLanguage,llAdmission,llDutyIn,llDutyOut,llLounge,llInfant,llMother,llSetting;

    //RelativeLayout
    RelativeLayout rlBottom,rlMenu,rlCircle,rlExpandMenu,rlHelp,rlStuck,rlOwn;

    //ImageView
    ImageView ivLounge,ivInfant,ivMother,ivSetting,ivPlus;

    //TextView
    TextView tvLounge,tvInfant,tvMother,tvSetting;

    //For Location
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private SettingsClient mSettingsClient;
    private LocationCallback mLocationCallback;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Location mCurrentLocation;
    final int REQUEST_CHECK_SETTINGS = 111;
    final int REQUEST_LOCATION = 222;
    public Boolean locUpdates= false;  // GPS/Network Location Updates are currently functioning
    public Boolean useGPS = false;     // tableName wants to use GPS/Network (based on setting) - turn off if permissions unsuccessful

    boolean doubleBackToExitPressedOnce;

    DraftAdapter draftAdapter;

    Dialog dialogDraft;

    ArrayList<HashMap<String, String>> searchList = new ArrayList<HashMap<String, String>>();

    int position=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById();

    }


    private void findViewById() {

        //TextView
        tvHeading = findViewById(R.id.tvHeading);
        tvLounge = findViewById(R.id.tvLounge);
        tvInfant = findViewById(R.id.tvInfant);
        tvMother = findViewById(R.id.tvMother);
        tvSetting = findViewById(R.id.tvSetting);

        //LinearLayout
        llSync = findViewById(R.id.llSync);
        llHelp = findViewById(R.id.llHelp);
        llLogout = findViewById(R.id.llLogout);
        llLanguage = findViewById(R.id.llLanguage);
        llAdmission = findViewById(R.id.llAdmission);
        llDutyIn = findViewById(R.id.llDutyIn);
        llDutyOut = findViewById(R.id.llDutyOut);
        llLounge = findViewById(R.id.llLounge);
        llInfant = findViewById(R.id.llInfant);
        llMother = findViewById(R.id.llMother);
        llSetting = findViewById(R.id.llSetting);

        //ImageView
        ivLounge = findViewById(R.id.ivLounge);
        ivInfant = findViewById(R.id.ivInfant);
        ivMother = findViewById(R.id.ivMother);
        ivSetting = findViewById(R.id.ivSetting);
        ivPlus = findViewById(R.id.ivPlus);

        //RelativeLayout
        rlBottom = findViewById(R.id.rlBottom);
        rlMenu = findViewById(R.id.rlMenu);
        rlExpandMenu = findViewById(R.id.rlExpandMenu);
        rlCircle = findViewById(R.id.rlCircle);
        rlHelp = findViewById(R.id.rlHelp);
        rlStuck = findViewById(R.id.rlStuck);
        rlOwn = findViewById(R.id.rlOwn);

        setValues();

        //setOnClickListener
        llSync.setOnClickListener(this);
        llHelp.setOnClickListener(this);
        llLogout.setOnClickListener(this);
        llLanguage.setOnClickListener(this);
        llAdmission.setOnClickListener(this);
        llDutyIn.setOnClickListener(this);
        llDutyOut.setOnClickListener(this);
        //rlMenu.setOnClickListener(this);
        rlCircle.setOnClickListener(this);
        rlHelp.setOnClickListener(this);
        rlStuck.setOnClickListener(this);
        rlOwn.setOnClickListener(this);
        llLounge.setOnClickListener(this);
        llInfant.setOnClickListener(this);
        llMother.setOnClickListener(this);
        llSetting.setOnClickListener(this);

        displayView(0);

        forOpeningGeoLocation();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void setValues() {
        tvHeading.setText(DatabaseController.getLoungeNameData(AppSettings.getString(AppSettings.loungeId)));
    }

    //displayView
    public void  displayView(int position) {
        this.position = position;
        if(rlExpandMenu.getVisibility()==View.VISIBLE)
        {
            Animation aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate0);
            ivPlus.startAnimation(aniRotateClk);
            AppUtils.collapse(rlExpandMenu);
        }

        switch (position) {

            case 0:
                GetBackFragment.Addpos(position);
                setDefault();
                tvLounge.setTextColor(getResources().getColor(R.color.oo_color));
                ivLounge.setColorFilter(getResources().getColor(R.color.oo_color), PorterDuff.Mode.SRC_IN);
                fragment = new LoungeMonitoringFragment();
                break;

            case 1:

                GetBackFragment.Addpos(position);
                fragment = new LoungeAssessmentFragment();
                break;

            case 2:

                GetBackFragment.Addpos(position);
                fragment = new FacilityBirthFragment();
                break;

            case 3:

                GetBackFragment.Addpos(position);
                setDefault();
                tvInfant.setTextColor(getResources().getColor(R.color.oo_color));
                ivInfant.setColorFilter(getResources().getColor(R.color.oo_color), PorterDuff.Mode.SRC_IN);
                fragment = new InfantListingFragment();
                break;

             case 4:

                 GetBackFragment.Addpos(position);
                 setDefault();
                 tvMother.setTextColor(getResources().getColor(R.color.oo_color));
                 ivMother.setColorFilter(getResources().getColor(R.color.oo_color), PorterDuff.Mode.SRC_IN);
                fragment = new MotherListingFragment();
                break;

            case 5:

                GetBackFragment.Addpos(position);
                setDefault();
                tvSetting.setTextColor(getResources().getColor(R.color.oo_color));
                ivSetting.setColorFilter(getResources().getColor(R.color.oo_color), PorterDuff.Mode.SRC_IN);
                fragment = new SettingFragment();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            //fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frameLayout, fragment, "fragment");
            fragmentTransaction.commitAllowingStateLoss();
//            fragmentTransaction.commit();
            getSupportFragmentManager().executePendingTransactions();
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.llSync:
                if (AppUtils.isNetworkAvailable(mActivity)) {
                    AppSettings.putString(AppSettings.syncTime,AppUtils.currentTimestampFormat());
                    SyncAllRecord.postDutyChange(mActivity);
                } else {
                    AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
                }

                break;

            case R.id.llLogout:

                AppUtils.AlertLogoutConfirm(mActivity);

                break;

            case R.id.llLanguage:

                AppUtils.AlertLanguageConfirm(mActivity,getString(R.string.languageAlert));

                break;

            case R.id.llHelp:

            case R.id.rlHelp:
                if(rlHelp.getVisibility()==View.VISIBLE) {
                    rlHelp.setVisibility(View.GONE);
                }
                else {
                    rlHelp.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.rlStuck:
                rlHelp.setVisibility(View.GONE);
                DatabaseController.saveHelp(mActivity);
                AppUtils.AlertHelpConfirm(getString(R.string.callRegardingIssue),mActivity,1,"");
                break;

            case R.id.rlOwn:

                rlHelp.setVisibility(View.GONE);
                startActivity(new Intent(mActivity, TutorialActivity.class));

                break;

            case R.id.rlCircle:

                if(!AppSettings.getString(AppSettings.userType).equalsIgnoreCase("1"))
                {
                    if (rlExpandMenu.getVisibility()==View.GONE)
                    {
                        Animation aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate45);
                        ivPlus.startAnimation(aniRotateClk);
                        AppUtils.expand(rlExpandMenu);
                    }
                    else
                    {
                        Animation aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate0);
                        ivPlus.startAnimation(aniRotateClk);
                        AppUtils.collapse(rlExpandMenu);
                    }
                }

                break;

            case R.id.llAdmission:

                Animation aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate0);
                ivPlus.startAnimation(aniRotateClk);
                AppUtils.collapse(rlExpandMenu);

                if(DatabaseController.getStepData().size()>0)
                {
                    //displayView(11);
                    AlertDraft();
                }
                else
                {
                    AppSettings.putString(AppSettings.isSibling,"0");
                    AppSettings.putString(AppSettings.from,"0");
                    AppSettings.putString(AppSettings.moveStatus,"2");
                    startActivity(new Intent(mActivity, RegistrationActivity.class));
                }

                break;

            case R.id.llDutyIn:

                aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate0);
                ivPlus.startAnimation(aniRotateClk);
                AppUtils.collapse(rlExpandMenu);
                startActivity(new Intent(mActivity, CheckInActivity.class));

                break;

            case R.id.llDutyOut:

                aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate0);
                ivPlus.startAnimation(aniRotateClk);
                AppUtils.collapse(rlExpandMenu);
                startActivity(new Intent(mActivity, CheckOutActivity.class));

                break;

            case R.id.llLounge:

                setDefault();
                displayView(0);

                break;

            case R.id.llInfant:

                setDefault();
                displayView(3);

                break;

            case R.id.llMother:

                setDefault();
                displayView(4);

                break;

            case R.id.llSetting:

                setDefault();
                displayView(5);

                break;

            default:

                break;
        }
    }

    private void setDefault() {

        tvLounge.setTextColor(getResources().getColor(R.color.grey));
        tvInfant.setTextColor(getResources().getColor(R.color.grey));
        tvMother.setTextColor(getResources().getColor(R.color.grey));
        tvSetting.setTextColor(getResources().getColor(R.color.grey));

        ivLounge.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
        ivInfant.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
        ivMother.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
        ivSetting.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);

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

        stopLocationUpdates();
    }


//    onBackPressed
    @Override
    public void onBackPressed() {
        try {
            int ast = GetBackFragment.Lastpos();

            if (position == 0) {

                if (rlExpandMenu.getVisibility() == View.VISIBLE) {
                    Animation aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate0);
                    ivPlus.startAnimation(aniRotateClk);
                    AppUtils.collapse(rlExpandMenu);
                } else {
                    if (doubleBackToExitPressedOnce) {
                        super.onBackPressed();
                        // finishAffinity();

                        if (Build.VERSION.SDK_INT >= 21) {
                            finishAndRemoveTask();
                            finishAffinity();
                        } else {
                            finish();
                        }

                        return;
                    }

                    this.doubleBackToExitPressedOnce = true;
//                    Toast.makeText(this, R.string.exit, Toast.LENGTH_SHORT).show();
//                    AppUtils.showErrorMessage(tvHeader, getString(R.string.exittoasttext), MainActivityRec.this);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                }
            }
            else {
                if (ast == 1) {
                    long g = GetBackFragment.LastUUID();
                    if (g != 0) {
                        GetBackFragment.Removelast();
                    }

                }

                Log.d("position", String.valueOf(ast));

                displayView(ast);
                GetBackFragment.Removepos();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void AlertDraft() {
        dialogDraft = new Dialog(mActivity,android.R.style.Theme_Translucent_NoTitleBar);
        dialogDraft.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDraft.setContentView(R.layout.alert_draft);
        Window window = dialogDraft.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialogDraft.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        ImageView ivClose = dialogDraft.findViewById(R.id.ivClose);

        RelativeLayout rlOk =  dialogDraft.findViewById(R.id.rlOk);

        //RecyclerView
        RecyclerView recyclerView = dialogDraft.findViewById(R.id.recyclerView);

        //GridLayoutManager
        GridLayoutManager mGridLayoutManager;

        mGridLayoutManager = new GridLayoutManager(mActivity, 1);
        recyclerView.setLayoutManager(mGridLayoutManager);

        dialogDraft.setCancelable(true);
        dialogDraft.setCanceledOnTouchOutside(true);
        dialogDraft.show();

        searchList.clear();

        //Fetch and then divide data
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
        arrayList.addAll(DatabaseController.getStepData());

        for (int i = 0; i < arrayList.size(); i++) {

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(arrayList.get(i).get("step1"));

                JSONObject newJSON = jsonObject.getJSONObject(AppConstants.projectName);

                HashMap<String, String> hashMap = new HashMap();

                if(arrayList.get(i).get("isSibling").equals("1"))
                {
                    if(arrayList.get(i).get("type").equalsIgnoreCase("1")
                               ||arrayList.get(i).get("type").equalsIgnoreCase("3"))
                    {
                        hashMap.put("motherName",
                                DatabaseController.getMotherColumnData(
                                        arrayList.get(i).get("motherId"),
                                        TableMotherRegistration.tableColumn.motherName.toString()));
                        hashMap.put("isMotherAdmitted",
                                DatabaseController.getMotherColumnData(
                                        arrayList.get(i).get("motherId"),
                                        TableMotherRegistration.tableColumn.isMotherAdmitted.toString()));
                        hashMap.put("motherPicture",
                                DatabaseController.getMotherColumnData(
                                        arrayList.get(i).get("motherId"),
                                        TableMotherRegistration.tableColumn.motherPicture.toString()));
                        hashMap.put("hospitalRegistrationNumber", newJSON.getString("hospitalRegistrationNumber"));
                        hashMap.put("type", arrayList.get(i).get("type"));
                        hashMap.put("from", arrayList.get(i).get("from"));
                        hashMap.put("uuid", arrayList.get(i).get("uuid"));
                        hashMap.put("isSibling", arrayList.get(i).get("isSibling"));
                        hashMap.put("motherId", newJSON.getString("motherId"));
                    }
                    else
                    {
                        hashMap.put("motherName",
                                DatabaseController.getMotherColumnData(
                                        arrayList.get(i).get("motherId"),
                                        TableMotherRegistration.tableColumn.guardianName.toString()));
                        hashMap.put("isMotherAdmitted", getString(R.string.noValue));
                        hashMap.put("motherPicture", "");
                        hashMap.put("hospitalRegistrationNumber", newJSON.getString("hospitalRegistrationNumber"));
                        hashMap.put("type", arrayList.get(i).get("type"));
                        hashMap.put("from", arrayList.get(i).get("from"));
                        hashMap.put("uuid", arrayList.get(i).get("uuid"));
                        hashMap.put("isSibling", arrayList.get(i).get("isSibling"));
                        hashMap.put("motherId", newJSON.getString("motherId"));
                    }
                }
                else
                {
                    if(newJSON.getString("type").equalsIgnoreCase("1")
                               ||newJSON.getString("type").equalsIgnoreCase("3"))
                    {
                        hashMap.put("motherName", newJSON.getString("motherName"));
                        hashMap.put("isMotherAdmitted", newJSON.getString("isMotherAdmitted"));
                        hashMap.put("motherPicture", newJSON.getString("motherPicture"));
                        hashMap.put("hospitalRegistrationNumber", newJSON.getString("hospitalRegistrationNumber"));
                        hashMap.put("type", newJSON.getString("type"));
                        hashMap.put("from", arrayList.get(i).get("from"));
                        hashMap.put("uuid", arrayList.get(i).get("uuid"));
                        hashMap.put("isSibling", arrayList.get(i).get("isSibling"));
                        hashMap.put("motherId", arrayList.get(i).get("motherId"));
                    }
                    else
                    {
                        hashMap.put("motherName", newJSON.getString("guardianName"));
                        hashMap.put("isMotherAdmitted", getString(R.string.noValue));
                        hashMap.put("motherPicture", "");
                        hashMap.put("hospitalRegistrationNumber", newJSON.getString("hospitalRegistrationNumber"));
                        hashMap.put("type", newJSON.getString("type"));
                        hashMap.put("from", arrayList.get(i).get("from"));
                        hashMap.put("uuid", arrayList.get(i).get("uuid"));
                        hashMap.put("isSibling", arrayList.get(i).get("isSibling"));
                        hashMap.put("motherId", arrayList.get(i).get("motherId"));
                    }
                }

                searchList.add(hashMap);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        draftAdapter = new DraftAdapter(searchList);
        recyclerView.setAdapter(draftAdapter);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogDraft.dismiss();

            }
        });

        rlOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppSettings.putString(AppSettings.isSibling,"0");
                AppSettings.putString(AppSettings.from,"0");
                AppSettings.putString(AppSettings.moveStatus,"2");
                startActivity(new Intent(mActivity, RegistrationActivity.class));
                dialogDraft.dismiss();

            }
        });
    }


    private class DraftAdapter extends RecyclerView.Adapter<DraftAdapter.DraftHolder> {
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        public DraftAdapter(ArrayList<HashMap<String, String>> favList) {
            data = favList;
        }

        public DraftHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DraftHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_draft_child, parent, false));
        }

        @SuppressLint("SetTextI18n")
        public void onBindViewHolder(DraftHolder holder, final int position) {

            if(data.get(position).get("type").equalsIgnoreCase("2"))
            {
                holder.tvName.setText(getString(R.string.unknown));
            }
            else
            {
                holder.tvName.setText(data.get(position).get("motherName"));
            }

            holder.tvHRN.setText(getString(R.string.RegistrationNumber) +": "+data.get(position).get("hospitalRegistrationNumber"));

            holder.rlMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AppSettings.putString(AppSettings.from,data.get(position).get("from"));

                    if(data.get(position).get("from").equalsIgnoreCase("1"))
                    {
                        AppSettings.putString(AppSettings.checkHRN,"2");
                    }
                    else if(data.get(position).get("from").equalsIgnoreCase("2"))
                    {
                        AppSettings.putString(AppSettings.checkHRN,"1");
                    }
                    else if(data.get(position).get("from").equalsIgnoreCase("0"))
                    {
                        AppSettings.putString(AppSettings.from,"2");
                        AppSettings.putString(AppSettings.checkHRN,"1");
                    }

                    AppSettings.putString(AppSettings.isSibling,data.get(position).get("isSibling"));

                    if(data.get(position).get("isSibling").equals("1"))
                    {
                        AppSettings.putString(AppSettings.from,"1");
                        AppSettings.putString(AppSettings.motherId,data.get(position).get("motherId"));
                    }

                    AppSettings.putString(AppSettings.moveStatus,"2");
                    AppSettings.putString(AppSettings.uuid,data.get(position).get("uuid"));
                    AppSettings.putString(AppSettings.hrn,data.get(position).get("hospitalRegistrationNumber"));
                    startActivity(new Intent(mActivity, RegistrationActivity.class));

                    dialogDraft.dismiss();

                }
            });

            if(data.get(position).get("motherPicture").isEmpty())
            {
                holder.ivPic.setImageResource(R.mipmap.mother);
            }
            else
            {
                try {
                    byte[] decodedString = Base64.decode(data.get(position).get("motherPicture"), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.ivPic.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    e.printStackTrace();
                    holder.ivPic.setImageResource(R.mipmap.mother);
                }
            }

            holder.ivStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Alert(data.get(position).get("uuid"));

                }
            });

        }

        public int getItemCount() {
            return data.size();
        }

        private class DraftHolder extends RecyclerView.ViewHolder {

            //TextView
            TextView tvName, tvHRN;

            //RelativeLayout
            RelativeLayout rlMain;

            //ImageView
            ImageView ivPic,ivStatus;

            //View
            View view;

            public DraftHolder(View itemView) {
                super(itemView);

                //TextView
                tvName = itemView.findViewById(R.id.tvName);
                tvHRN = itemView.findViewById(R.id.tvHRN);

                //RelativeLayout
                rlMain = itemView.findViewById(R.id.rlMain);

                //ImageView
                ivPic = itemView.findViewById(R.id.ivPic);
                ivStatus = itemView.findViewById(R.id.ivStatus);

            }
        }
    }

    public void Alert(final String uuid) {
        final Dialog dialog = new Dialog(mActivity,android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_yes_no);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //TextView
        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        TextView tvOk = dialog.findViewById(R.id.tvOk);
        TextView tvCancel = dialog.findViewById(R.id.tvCancel);

        //RelativeLayout
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);

        tvMessage.setText(getString(R.string.deleteAlert));
        tvOk.setText(getString(R.string.yes));

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        rlOk.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                dialog.dismiss();
                DatabaseController.delete(TableUser.tableName,TableUser.tableColumn.uuid + " = '" + uuid + "'",null);
                dialogDraft.dismiss();
                AlertDraft();
            }
        });

        rlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();


        DataBaseHelper.copyDatabase(mActivity);

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
