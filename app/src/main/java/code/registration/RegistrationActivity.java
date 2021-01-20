package code.registration;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import code.algo.SyncAllRecord;
import code.common.GetBackFragment;
import code.common.LocaleHelper;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableUser;
import code.main.TutorialActivity;
import code.utils.AppConstants;
import code.utils.AppUtils;
import code.view.BaseActivity;
import code.view.BaseFragment;

public class RegistrationActivity extends BaseActivity implements View.OnClickListener {
    //TextView
    public static TextView tvMotherAdmission, tvBabyAdmission, tvAssessment, tvAdmission;
    public static String admitted = "", uuid = "";
    public static RelativeLayout rlClose;
    //Fragment
    static BaseFragment fragment;
    final int REQUEST_CHECK_SETTINGS = 111;
    final int REQUEST_LOCATION = 222;
    public Boolean locUpdates = false;  // GPS/Network Location Updates are currently functioning
    public Boolean useGPS = false;     // tableName wants to use GPS/Network (based on setting) - turn off if permissions unsuccessful
    //TextView
    TextView tvHeading;
    //LinearLayout
    LinearLayout llSync, llHelp, llLogout, llLanguage;
    //RelativeLayout
    RelativeLayout rlHelp, rlStuck, rlOwn;
    //For Location
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private SettingsClient mSettingsClient;
    private LocationCallback mLocationCallback;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        findViewById();
    }

    private void findViewById() {
        //TextView
        rlClose = findViewById(R.id.rlClose);
        tvHeading = findViewById(R.id.tvHeading);
        tvMotherAdmission = findViewById(R.id.tvMotherAdmission);
        tvBabyAdmission = findViewById(R.id.tvBabyAdmission);
        tvAssessment = findViewById(R.id.tvAssessment);
        tvAdmission = findViewById(R.id.tvAdmission);
        //LinearLayout
        llSync = findViewById(R.id.llSync);
        llHelp = findViewById(R.id.llHelp);
        llLogout = findViewById(R.id.llLogout);
        llLanguage = findViewById(R.id.llLanguage);
        //RelativeLayout
        rlHelp = findViewById(R.id.rlHelp);
        rlStuck = findViewById(R.id.rlStuck);
        rlOwn = findViewById(R.id.rlOwn);
        tvHeading.setText(getString(R.string.admission));

        //setOnClickListener
        llSync.setOnClickListener(this);
        llHelp.setOnClickListener(this);
        llLogout.setOnClickListener(this);
        llLanguage.setOnClickListener(this);
        rlHelp.setOnClickListener(this);
        rlStuck.setOnClickListener(this);
        rlOwn.setOnClickListener(this);
        rlClose.setOnClickListener(this);
        setPreFilledValues();
        forOpeningGeoLocation();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void setPreFilledValues() {

        AppSettings.putString(AppSettings.respiratoryRate, "");

        if (AppSettings.getString(AppSettings.isSibling).equalsIgnoreCase("1")) {

            Log.v("froms", AppSettings.getString(AppSettings.from));


            if (AppSettings.getString(AppSettings.from).equalsIgnoreCase("0")) {

                uuid = UUID.randomUUID().toString();
                displayView(5);
            } else {

                uuid = AppSettings.getString(AppSettings.uuid);

                boolean countStep1 = DatabaseController.checkRecordExistWhere(TableUser.tableName,
                        String.valueOf(TableUser.tableColumn.mRStatus) + " = '0' and " + TableUser.tableColumn.uuid + " = '" + uuid + "'");
                boolean countStep3 = DatabaseController.checkRecordExistWhere(TableUser.tableName,
                        String.valueOf(TableUser.tableColumn.bAStatus) + " = '0' and " + TableUser.tableColumn.uuid + " = '" + uuid + "'");
                boolean countStep6 = DatabaseController.checkRecordExistWhere(TableUser.tableName,
                        String.valueOf(TableUser.tableColumn.cStatus) + " = '0' and " + TableUser.tableColumn.uuid + " = '" + uuid + "'");

                admitted = DatabaseController.getAdmissionStatus(uuid);
                //type = DatabaseController.getAdmissionType(uuid);

                ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
                arrayList.addAll(DatabaseController.getStepData(uuid));
                AppSettings.putString(AppSettings.babyId, arrayList.get(0).get("babyId"));
                AppSettings.putString(AppSettings.motherId, arrayList.get(0).get("motherId"));

                if (!countStep1) {
                    displayView(5);
                } else if (!countStep3) {
                    displayView(1);
                } else if (!countStep6) {
                    displayView(4);
                }
            }
        } else {
            if (AppSettings.getString(AppSettings.from).equalsIgnoreCase("0")) {
                uuid = UUID.randomUUID().toString();
                displayView(0);
            } else if (AppSettings.getString(AppSettings.from).equalsIgnoreCase("1")) {
                //uuid = UUID.randomUUID().toString();
                setValues();
                displayView(0);
            } else {
                uuid = AppSettings.getString(AppSettings.uuid);

                boolean countStep1 = DatabaseController.checkRecordExistWhere(TableUser.tableName,
                        String.valueOf(TableUser.tableColumn.mRStatus) + " = '0' and " + TableUser.tableColumn.uuid + " = '" + uuid + "'");
                boolean countStep3 = DatabaseController.checkRecordExistWhere(TableUser.tableName,
                        String.valueOf(TableUser.tableColumn.bAStatus) + " = '0' and " + TableUser.tableColumn.uuid + " = '" + uuid + "'");
                boolean countStep6 = DatabaseController.checkRecordExistWhere(TableUser.tableName,
                        String.valueOf(TableUser.tableColumn.cStatus) + " = '0' and " + TableUser.tableColumn.uuid + " = '" + uuid + "'");

                admitted = DatabaseController.getAdmissionStatus(uuid);
                //type = DatabaseController.getAdmissionType(uuid);

                ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
                arrayList.addAll(DatabaseController.getStepData(uuid));
                AppSettings.putString(AppSettings.babyId, arrayList.get(0).get("babyId"));
                AppSettings.putString(AppSettings.motherId, arrayList.get(0).get("motherId"));

                if (!countStep1) {
                    displayView(0);
                } else if (!countStep3) {
                    displayView(1);
                } else if (!countStep6) {
                    displayView(4);
                }
            }
        }

    }

    public void setValues() {

        try {
            String type = AppConstants.jsonObject.getString("type");

            AppSettings.putString(AppSettings.type, type);
            uuid = AppSettings.getString(AppSettings.uuid);

            if (type.equalsIgnoreCase("1")) {
                admitted = getString(R.string.yesValue);
            } else if (type.equalsIgnoreCase("3")) {
                admitted = getString(R.string.noValue);
            } else if (type.equalsIgnoreCase("2")) {
                admitted = getString(R.string.noValue);
            } else {
                admitted = getString(R.string.noValue);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //displayView
    public void displayView(int position) {


        Log.v("mycurrentposition", String.valueOf(position));
        Log.v("isShibling", AppSettings.getString(AppSettings.isSibling));

        switch (position) {

            case 0:
                rlClose.setVisibility(View.GONE);
                GetBackFragment.Addpos(position);
                fragment = new MotherBabyRegistationFragment();
                break;

            case 1:
                rlClose.setVisibility(View.GONE);
                GetBackFragment.Addpos(position);
                fragment = new BabyAssessmentFragment();
                break;

            case 2:
                rlClose.setVisibility(View.GONE);
                GetBackFragment.Addpos(position);
                fragment = new BaselineFragment();
                break;

            case 3:
                rlClose.setVisibility(View.GONE);
                GetBackFragment.Addpos(position);
                fragment = new ReferFragment();
                break;

            case 4:
                rlClose.setVisibility(View.GONE);
                GetBackFragment.Addpos(position);
                fragment = new ChecklistFragment();
                break;

            case 5:
                if (AppSettings.getString(AppSettings.isSibling).equalsIgnoreCase("1")) {

                    if (AppSettings.getString(AppSettings.from).equalsIgnoreCase("0")) {
                        rlClose.setVisibility(View.VISIBLE);

                    } else {
                        rlClose.setVisibility(View.GONE);

                    }

                }
                GetBackFragment.Addpos(position);
                fragment = new SiblingBabyRegistationFragment();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            //fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frameLayout, fragment, "fragment");
            fragmentTransaction.commitAllowingStateLoss();
            //fragmentTransaction.commit();
            getSupportFragmentManager().executePendingTransactions();
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.llSync:

                if (AppUtils.isNetworkAvailable(mActivity)) {
                    SyncAllRecord.postDutyChange(mActivity);
                } else {
                    AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
                }

                break;


            case R.id.llLogout:

                AppUtils.AlertLogoutConfirm(mActivity);

                break;

            case R.id.llLanguage:

                AppUtils.AlertLanguageConfirm(mActivity, getString(R.string.languageAlert));

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
                AppUtils.AlertOk(getString(R.string.callRegardingIssue), mActivity, 1, "");
                break;

            case R.id.rlOwn:

                rlHelp.setVisibility(View.GONE);
                startActivity(new Intent(mActivity, TutorialActivity.class));

                break;

            case R.id.rlClose:

                AppUtils.AlertCloseActivity(mActivity, getString(R.string.youAreConfirmingBack));

                break;

            default:

                break;
        }
    }

    @Override
    public void onBackPressed() {

        AppUtils.AlertCloseActivity(mActivity, getString(R.string.sureToCancel));

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
}
