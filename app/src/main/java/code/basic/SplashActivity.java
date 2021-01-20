package code.basic;
import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.kmcapp.android.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.algo.WebServicesImageCallback;
import code.checkIn.CheckInActivity;
import code.coach.CoachLoungeSelectionActivity;
import code.common.LocaleHelper;
import code.common.MCrypt;
import code.database.AppSettings;
import code.database.DataBaseHelper;
import code.database.DatabaseController;
import code.database.TableAsha;
import code.database.TableBlock;
import code.database.TableDistrict;
import code.database.TableFacility;
import code.database.TableLounge;
import code.database.TablePosters;
import code.database.TableStaff;
import code.database.TableState;
import code.database.TableVideos;
import code.database.TableVillage;
import code.main.MainActivity;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseActivity;

public class SplashActivity extends BaseActivity  {

    public static ArrayList<HashMap<String, String>> filedownload = new ArrayList<HashMap<String, String>>();
    public static ArrayList<HashMap<String, String>> posterdownload = new ArrayList<HashMap<String, String>>();

    private int downloadcount = 0;
    private int downloadpostercount = 0;

    String pathl = "";
    String pathnew = "";
    HashMap<String, String> vidList = new HashMap<String, String>();

    private static final int REQUEST_PERMISSIONS = 1;

    //TextView
    TextView tvEnglish,tvHindi;

    FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        findViewById();
    }

    private void findViewById() {
        //TextView
        tvEnglish = findViewById(R.id.tvEnglish);
        tvHindi = findViewById(R.id.tvHindi);

        tvEnglish.setText(AppUtils.setSpannable(getString(R.string.splashLine1English),getString(R.string.splashLine2English),getString(R.string.splashLine3English),mActivity));
        tvHindi.setText(AppUtils.setSpannable(getString(R.string.splashLine1Hindi),getString(R.string.splashLine2Hindi),getString(R.string.splashLine3Hindi),mActivity));

//
//        try {
//            DatabaseController.delete(TableDutyChange.tableName,null,null);
//
//        }
//        catch (Exception e)
//        {
//            Log.v("kgjk",e.getMessage());
//            e.printStackTrace();
//        }


        DataBaseHelper.copyDatabase(mActivity);


        //Check for Runtime Permissions
        checkPermissions();

        //setLogin();

        AppSettings.putString(AppSettings.checkIMEI,"1");

        if (AppSettings.getString(AppSettings.keyLanguageCode).isEmpty())
            AppSettings.putString(AppSettings.keyLanguageCode,"en");

        vidList.clear();

    }

    // check if Manifest (application) location permissions enabled and if not, request permissions
    public void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_PHONE_STATE) !=
                        PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                }, REQUEST_PERMISSIONS);
            }
        } else {

            if(AppSettings.getString(AppSettings.userType).equalsIgnoreCase("1"))
            {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setCoachLogin();
                    }
                }, 1000);
            }
            else
            {
                if (AppUtils.isNetworkAvailable(this.mActivity)) {
                    getAllLoungeApi();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setLogin();
                        }
                    }, 1000);
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Toast.makeText(Drawer_Activity.this, "Permission granted", Toast.LENGTH_SHORT).show();

                if (AppUtils.isNetworkAvailable(this.mActivity)) {
                    getAllLoungeApi();
                } else {
                    //AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setLogin();
                        }
                    }, 1000);
                }

            } else {

                //Toast.makeText(Drawer_Activity.this, "Permission denied", Toast.LENGTH_SHORT).show();

                checkPermissions();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void getAllLoungeApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {

            //jsonData.put("timestamp", AppSettings.getString(AppSettings.syncTime));
            jsonData.put("timestamp", "");

            json.put(AppConstants.projectName, jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.getLounge, json,true,false, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {
                try {
                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);
                    if (jsonObject.getString("resCode").equals("1")) {
                        JSONArray loungeArray = jsonObject.getJSONArray("loungDetail");

                        try {

                            DatabaseController.myDataBase.beginTransaction();
                            for (int i = 0; i < loungeArray.length(); i++) {
                                JSONObject arrayJSONObject = loungeArray.getJSONObject(i);
                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableLounge.tableColumn.facilityName.toString(), arrayJSONObject.getString("facilityName"));
                                mContentValues.put(TableLounge.tableColumn.contactNumber.toString(), arrayJSONObject.getString("loungeContactNumber"));
                                mContentValues.put(TableLounge.tableColumn.loungeId.toString(), arrayJSONObject.getString("loungeId"));
                                mContentValues.put(TableLounge.tableColumn.loungeName.toString(), arrayJSONObject.getString("loungeName"));
                                mContentValues.put(TableLounge.tableColumn.facilityId.toString(), arrayJSONObject.getString("facilityId"));
                                try {
                                    JSONObject jsonObject2 = arrayJSONObject.getJSONObject("amenitiesData");
                                    mContentValues.put(TableLounge.tableColumn.numberOfBeds.toString(), jsonObject2.getString("totalRecliningBeds"));
                                    Log.v("TableLoungeData",arrayJSONObject.toString());
                                    Log.v("loungeArray",loungeArray.toString());
                                } catch (JSONException e) {
                                    //e.printStackTrace();
                                }

                                /*mContentValues.put(TableLounge.tableColumn.numberOfBeds.toString(), arrayJSONObject.getString("numberOfBed"));
                                mContentValues.put(TableLounge.tableColumn.recliningChairs.toString(), arrayJSONObject.getString("numberOfChair"));
                                mContentValues.put(TableLounge.tableColumn.bedSideTable.toString(), arrayJSONObject.getString("numberOfTable"));
                                mContentValues.put(TableLounge.tableColumn.acAvailability.toString(), arrayJSONObject.getString("acAvailability"));
                                mContentValues.put(TableLounge.tableColumn.roomHeater.toString(), arrayJSONObject.getString("roomHeaterAvailability"));
                                mContentValues.put(TableLounge.tableColumn.weighingScale.toString(), arrayJSONObject.getString("isDigitalWeighingScaleAvail"));
                                mContentValues.put(TableLounge.tableColumn.roomThermometer.toString(), arrayJSONObject.getString("roomThermometerAvailability"));
                                mContentValues.put(TableLounge.tableColumn.wallClock.toString(), arrayJSONObject.getString("wallClockAvailability"));
                                mContentValues.put(TableLounge.tableColumn.ledTv.toString(), arrayJSONObject.getString("tvAvailability"));
                                mContentValues.put(TableLounge.tableColumn.musicSystem.toString(), arrayJSONObject.getString("musicSystemAvailability"));
                                mContentValues.put(TableLounge.tableColumn.almirah.toString(), arrayJSONObject.getString("almirahAvailability"));
                                mContentValues.put(TableLounge.tableColumn.chargingPoint.toString(), arrayJSONObject.getString("chargingPointAvailability"));
                                mContentValues.put(TableLounge.tableColumn.nursingStation.toString(), arrayJSONObject.getString("nursingStationAvailability"));
                                mContentValues.put(TableLounge.tableColumn.locker.toString(), arrayJSONObject.getString("lockersAvailabilty"));
                                mContentValues.put(TableLounge.tableColumn.tabletWithLoungeApp.toString(), arrayJSONObject.getString("tabletLoungeAppAvailability"));
                                mContentValues.put(TableLounge.tableColumn.powerBackup.toString(), arrayJSONObject.getString("powerBackupAvailability"));
                                mContentValues.put(TableLounge.tableColumn.thermometer.toString(), arrayJSONObject.getString("thermometerAvailability"));*/
                                mContentValues.put(TableLounge.tableColumn.status.toString(), arrayJSONObject.getString("status"));
                                mContentValues.put(TableLounge.tableColumn.addDate.toString(), arrayJSONObject.getString("addDate"));
                                mContentValues.put(TableLounge.tableColumn.modifyDate.toString(), arrayJSONObject.getString("modifyDate"));
                                mContentValues.put(TableLounge.tableColumn.type.toString(), arrayJSONObject.getString("type"));

                                DatabaseController.insertUpdateData(mContentValues, TableLounge.tableName, TableLounge.tableColumn.loungeId.toString(), arrayJSONObject.getString("loungeId"));

                            }

                            DatabaseController.myDataBase.setTransactionSuccessful();

                        } finally {
                            DatabaseController.myDataBase.endTransaction();
                            //AppSettings.putString(AppSettings.syncTime, jsonObject.getString("syncTime"));
                        }

                    } else {

                    }

                    if (!AppSettings.getString(AppSettings.loginId).isEmpty()) {

                        if (AppUtils.isNetworkAvailable(mActivity)) {
                            getAllStaffApi();
                        } else {
                            //AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                        }

                    } else {

                        if (AppUtils.isNetworkAvailable(mActivity)) {
                            getAllLocationApi();
                        }
                        /*if (AppUtils.isNetworkAvailable(mActivity)) {
                            getAllAshaApi();
                        } else {
                            setLogin();
                        }*/
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void OnFail(String responce) {
                 try{
//                     AppUtils.showToastSort(mActivity, responce);
                 }catch (NullPointerException e){
                     e.printStackTrace();
                 }
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

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.getStaff, json,false,false, new WebServicesCallback() {

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

                                DatabaseController.insertUpdateData(contentValues, TableStaff.tableName, TableStaff.tableColumn.staffId.toString(),detailJSONObject.getString("staffId"));

                            }

                            DatabaseController.myDataBase.setTransactionSuccessful();

                        } finally {
                            DatabaseController.myDataBase.endTransaction();
                        }
                    } else {

                    }

                    if (AppUtils.isNetworkAvailable(mActivity)) {
                        getAllLocationApi();
                    } else {
                    }

                    /*if (AppUtils.isNetworkAvailable(mActivity)) {
                        getAllAshaApi();
                    } else {
                        setLogin();
                    }*/

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

//                AppUtils.showToastSort(mActivity, responce);

            }
        });


    }

    private void getAllLocationApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {

            jsonData.put("timestamp", AppSettings.getString(AppSettings.geoSyncTime));

            json.put(AppConstants.projectName, jsonData);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.getRevenueData, json,false,false, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        if(AppSettings.getString(AppSettings.geoSyncTime).isEmpty())
                        {
                            JSONArray stateArray = jsonObject.getJSONArray("stateArray");
                            JSONArray districtArray = jsonObject.getJSONArray("districtArray");
                            JSONArray blockArray = jsonObject.getJSONArray("blockArray");
                            JSONArray villageArray = jsonObject.getJSONArray("villageArray");

                            try {

                                DatabaseController.myDataBase.beginTransaction();

                                for (int i = 0; i < stateArray.length(); i++) {

                                    JSONObject arrayJSONObject = stateArray.getJSONObject(i);

                                    ContentValues mContentValues = new ContentValues();

                                    mContentValues.put(TableState.tableColumn.id.toString(), arrayJSONObject.getString("StateCode"));
                                    mContentValues.put(TableState.tableColumn.name.toString(), arrayJSONObject.getString("StateNameProperCase"));
                                    mContentValues.put(TableState.tableColumn.status.toString(), arrayJSONObject.getString("STATUS"));

                                    DatabaseController.insertData(mContentValues,TableState.tableName);

                                /*DatabaseController.insertUpdateData(mContentValues, TableState.tableName,
                                        TableState.tableColumn.id.toString(), arrayJSONObject.getString("StateCode"));*/

                                }

                                for (int i = 0; i < districtArray.length(); i++) {

                                    JSONObject arrayJSONObject = districtArray.getJSONObject(i);

                                    ContentValues mContentValues = new ContentValues();

                                    mContentValues.put(TableDistrict.tableColumn.districtId.toString(), arrayJSONObject.getString("PRIDistrictCode"));
                                    mContentValues.put(TableDistrict.tableColumn.districtName.toString(), arrayJSONObject.getString("DistrictNameProperCase"));
                                    mContentValues.put(TableDistrict.tableColumn.urbanRural.toString(), arrayJSONObject.getString("UrbanRural"));
                                    mContentValues.put(TableDistrict.tableColumn.stateId.toString(), arrayJSONObject.getString("StateCode"));
                                    mContentValues.put(TableDistrict.tableColumn.json.toString(), arrayJSONObject.toString());
                                    mContentValues.put(TableDistrict.tableColumn.status.toString(), arrayJSONObject.getString("STATUS"));

                                    DatabaseController.insertData(mContentValues,TableDistrict.tableName);

                                /*DatabaseController.insertUpdateData(mContentValues, TableDistrict.tableName,
                                        TableDistrict.tableColumn.districtId.toString(), arrayJSONObject.getString("PRIDistrictCode"));*/

                                }

                                for (int i = 0; i < blockArray.length(); i++) {

                                    JSONObject arrayJSONObject = blockArray.getJSONObject(i);

                                    ContentValues mContentValues = new ContentValues();

                                    mContentValues.put(TableBlock.tableColumn.districtId.toString(), arrayJSONObject.getString("PRIDistrictCode"));
                                    mContentValues.put(TableBlock.tableColumn.blockName.toString(), arrayJSONObject.getString("BlockPRINameProperCase"));
                                    mContentValues.put(TableBlock.tableColumn.urbanRural.toString(), arrayJSONObject.getString("UrbanRural"));
                                    mContentValues.put(TableBlock.tableColumn.blockId.toString(), arrayJSONObject.getString("BlockPRICode"));
                                    mContentValues.put(TableBlock.tableColumn.json.toString(), arrayJSONObject.toString());
                                    mContentValues.put(TableBlock.tableColumn.status.toString(), arrayJSONObject.getString("STATUS"));

                                    DatabaseController.insertData(mContentValues,TableBlock.tableName);

                                /*DatabaseController.insertUpdateData(mContentValues, TableBlock.tableName,
                                        TableBlock.tableColumn.blockId.toString(), arrayJSONObject.getString("BlockPRICode"));*/

                                }

                                for (int i = 0; i < villageArray.length(); i++) {

                                    JSONObject arrayJSONObject = villageArray.getJSONObject(i);

                                    ContentValues mContentValues = new ContentValues();

                                    mContentValues.put(TableVillage.tableColumn.villageId.toString(), arrayJSONObject.getString("GPPRICode"));
                                    mContentValues.put(TableVillage.tableColumn.villageName.toString(), arrayJSONObject.getString("GPNameProperCase"));
                                    mContentValues.put(TableVillage.tableColumn.urbanRural.toString(), arrayJSONObject.getString("UrbanRural"));
                                    mContentValues.put(TableVillage.tableColumn.blockId.toString(), arrayJSONObject.getString("BlockPRICode"));
                                    mContentValues.put(TableVillage.tableColumn.json.toString(), arrayJSONObject.toString());
                                    mContentValues.put(TableVillage.tableColumn.status.toString(), arrayJSONObject.getString("STATUS"));

                                    DatabaseController.insertData(mContentValues,TableVillage.tableName);

                                /*DatabaseController.insertUpdateData(mContentValues, TableVillage.tableName,
                                        TableVillage.tableColumn.villageId.toString(), arrayJSONObject.getString("GPPRICode"));*/

                                }

                                DatabaseController.myDataBase.setTransactionSuccessful();

                            } finally {
                                DatabaseController.myDataBase.endTransaction();
                            }
                        }
                        else
                        {
                            JSONArray stateArray = jsonObject.getJSONArray("stateArray");
                            JSONArray districtArray = jsonObject.getJSONArray("districtArray");
                            JSONArray blockArray = jsonObject.getJSONArray("blockArray");
                            JSONArray villageArray = jsonObject.getJSONArray("villageArray");

                            try {

                                DatabaseController.myDataBase.beginTransaction();

                                for (int i = 0; i < stateArray.length(); i++) {

                                    JSONObject arrayJSONObject = stateArray.getJSONObject(i);

                                    ContentValues mContentValues = new ContentValues();

                                    mContentValues.put(TableState.tableColumn.id.toString(), arrayJSONObject.getString("StateCode"));
                                    mContentValues.put(TableState.tableColumn.name.toString(), arrayJSONObject.getString("StateNameProperCase"));
                                    mContentValues.put(TableState.tableColumn.status.toString(), arrayJSONObject.getString("STATUS"));

                                    DatabaseController.insertUpdateData(mContentValues, TableState.tableName,
                                        TableState.tableColumn.id.toString(), arrayJSONObject.getString("StateCode"));

                                }

                                for (int i = 0; i < districtArray.length(); i++) {

                                    JSONObject arrayJSONObject = districtArray.getJSONObject(i);

                                    ContentValues mContentValues = new ContentValues();

                                    mContentValues.put(TableDistrict.tableColumn.districtId.toString(), arrayJSONObject.getString("PRIDistrictCode"));
                                    mContentValues.put(TableDistrict.tableColumn.districtName.toString(), arrayJSONObject.getString("DistrictNameProperCase"));
                                    mContentValues.put(TableDistrict.tableColumn.urbanRural.toString(), arrayJSONObject.getString("UrbanRural"));
                                    mContentValues.put(TableDistrict.tableColumn.stateId.toString(), arrayJSONObject.getString("StateCode"));
                                    mContentValues.put(TableDistrict.tableColumn.json.toString(), arrayJSONObject.toString());
                                    mContentValues.put(TableDistrict.tableColumn.status.toString(), arrayJSONObject.getString("STATUS"));

                                    DatabaseController.insertUpdateData(mContentValues, TableDistrict.tableName,
                                        TableDistrict.tableColumn.districtId.toString(), arrayJSONObject.getString("PRIDistrictCode"));

                                }

                                for (int i = 0; i < blockArray.length(); i++) {

                                    JSONObject arrayJSONObject = blockArray.getJSONObject(i);

                                    ContentValues mContentValues = new ContentValues();

                                    mContentValues.put(TableBlock.tableColumn.districtId.toString(), arrayJSONObject.getString("PRIDistrictCode"));
                                    mContentValues.put(TableBlock.tableColumn.blockName.toString(), arrayJSONObject.getString("BlockPRINameProperCase"));
                                    mContentValues.put(TableBlock.tableColumn.urbanRural.toString(), arrayJSONObject.getString("UrbanRural"));
                                    mContentValues.put(TableBlock.tableColumn.blockId.toString(), arrayJSONObject.getString("BlockPRICode"));
                                    mContentValues.put(TableBlock.tableColumn.json.toString(), arrayJSONObject.toString());
                                    mContentValues.put(TableBlock.tableColumn.status.toString(), arrayJSONObject.getString("STATUS"));

                                    DatabaseController.insertUpdateData(mContentValues, TableBlock.tableName,
                                        TableBlock.tableColumn.blockId.toString(), arrayJSONObject.getString("BlockPRICode"));

                                }

                                for (int i = 0; i < villageArray.length(); i++) {

                                    JSONObject arrayJSONObject = villageArray.getJSONObject(i);

                                    ContentValues mContentValues = new ContentValues();

                                    mContentValues.put(TableVillage.tableColumn.villageId.toString(), arrayJSONObject.getString("GPPRICode"));
                                    mContentValues.put(TableVillage.tableColumn.villageName.toString(), arrayJSONObject.getString("GPNameProperCase"));
                                    mContentValues.put(TableVillage.tableColumn.urbanRural.toString(), arrayJSONObject.getString("UrbanRural"));
                                    mContentValues.put(TableVillage.tableColumn.blockId.toString(), arrayJSONObject.getString("BlockPRICode"));
                                    mContentValues.put(TableVillage.tableColumn.json.toString(), arrayJSONObject.toString());
                                    mContentValues.put(TableVillage.tableColumn.status.toString(), arrayJSONObject.getString("STATUS"));

                                    DatabaseController.insertUpdateData(mContentValues, TableVillage.tableName,
                                        TableVillage.tableColumn.villageId.toString(), arrayJSONObject.getString("GPPRICode"));

                                }

                                DatabaseController.myDataBase.setTransactionSuccessful();

                            } finally {
                                DatabaseController.myDataBase.endTransaction();
                                }
                        }

                        AppSettings.putString(AppSettings.geoSyncTime, jsonObject.getString("syncTime"));
                        AppSettings.putString(AppSettings.villageCountNew, jsonObject.getString("count"));

                    } else {

                    }

                    if (AppUtils.isNetworkAvailable(mActivity)) {
                       // getAllVideosApi();
                        getAllPosterApi();
                    } else {
                        //AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                    }

                    if (AppUtils.isNetworkAvailable(mActivity)) {
                        getAllFacilityApi();
                    } else {
                        setLogin();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

//                AppUtils.showToastSort(mActivity, responce);

            }
        });

    }

    private void getAllVideosApi() {
        WebServices.getApi(mActivity, AppUrls.getVideo,false,false, new WebServicesCallback() {
            @Override
            public void ArrayData(ArrayList arrayList) {

            }
            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        DatabaseController.myDataBase.delete(TableVideos.tableName, null, null);

                        JSONArray videoList = jsonObject.getJSONArray("videos");

                        for (int i = 0; i < videoList.length(); i++) {

                            JSONObject arrayJSONObject = videoList.getJSONObject(i);

                            //DISTRICT DATA
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(TableVideos.tableColumn.videoId.toString(), arrayJSONObject.getString("id"));
                            contentValues.put(TableVideos.tableColumn.videoName.toString(), arrayJSONObject.getString("videoTitle"));
                            contentValues.put(TableVideos.tableColumn.videoType.toString(), arrayJSONObject.getString("videoType"));
                            contentValues.put(TableVideos.tableColumn.videoUrl.toString(), arrayJSONObject.getString("videoName"));
                            contentValues.put(TableVideos.tableColumn.videoThumb.toString(), "");
                            contentValues.put(TableVideos.tableColumn.addDate.toString(), arrayJSONObject.getString("modifyDate"));

                            DatabaseController.insertUpdateData(contentValues, TableVideos.tableName, TableVideos.tableColumn.videoId.toString(), arrayJSONObject.getString("id"));

                            HashMap<String, String> vidList = new HashMap<String, String>();

                            vidList.put("id", arrayJSONObject.getString("id"));
                            vidList.put("videoName", arrayJSONObject.getString("videoName"));

                            filedownload.add(vidList);

                        }
                    } else {

                    }

                    if (filedownload.size() > 0) {
                        AppUtils.showRequestDialog(mActivity, "Downloading Video");
//                        downloadFile(filedownload.get(downloadcount).get("videoName"));
                    } else {
                        //saveGeoData();
                        if (AppUtils.isNetworkAvailable(mActivity)) {
                            getAllFacilityApi();
                        } else {
                            setLogin();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

//                AppUtils.showToastSort(mActivity, responce);

            }
        });
    }

    //khushboo pandey

    private void getAllPosterApi() {
        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("timestamp", AppSettings.getString(AppSettings.posterSyncTime));
            json.put(AppConstants.projectName, jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.getCounsellingPoster,json,false,false, new WebServicesCallback() {
            @Override
            public void ArrayData(ArrayList arrayList) {

            }
            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {
                try {
                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);
                    if (jsonObject.getString("resCode").equals("1")) {

                        JSONArray posterList = jsonObject.getJSONArray("posters");
                        if(AppSettings.getString(AppSettings.posterSyncTime).isEmpty()) {
                            for (int i = 0; i < posterList.length(); i++) {
                                JSONObject arrayJSONObject = posterList.getJSONObject(i);
                                ContentValues mContentValuesPoster = new ContentValues();
                                mContentValuesPoster.put(TablePosters.tableColumn.posterId.toString(), arrayJSONObject.getString("id"));
                                mContentValuesPoster.put(TablePosters.tableColumn.posterName.toString(), arrayJSONObject.getString("videoTitle"));
                                mContentValuesPoster.put(TablePosters.tableColumn.posterType.toString(), arrayJSONObject.getString("posterType"));
                                mContentValuesPoster.put(TablePosters.tableColumn.posterUrl.toString(), arrayJSONObject.getString("videoName"));
                                mContentValuesPoster.put(TablePosters.tableColumn.status.toString(), arrayJSONObject.getString("status"));
                                mContentValuesPoster.put(TablePosters.tableColumn.addDate.toString(), arrayJSONObject.getString("modifyDate"));

                                DatabaseController.insertData(mContentValuesPoster, TablePosters.tableName);

                                downloadImage(arrayJSONObject.getString("videoName"), arrayJSONObject.getString("id"));

                            }
                       }
                        else {
                            for (int i = 0; i < posterList.length(); i++) {

                                JSONObject arrayJSONObject = posterList.getJSONObject(i);

                                ContentValues contentValues = new ContentValues();
                                contentValues.put(TablePosters.tableColumn.posterId.toString(), arrayJSONObject.getString("id"));
                                contentValues.put(TablePosters.tableColumn.posterName.toString(), arrayJSONObject.getString("videoTitle"));
                                contentValues.put(TablePosters.tableColumn.posterType.toString(), arrayJSONObject.getString("posterType"));
                                contentValues.put(TablePosters.tableColumn.posterUrl.toString(), arrayJSONObject.getString("videoName"));
                                contentValues.put(TablePosters.tableColumn.status.toString(), arrayJSONObject.getString("status"));
                                contentValues.put(TablePosters.tableColumn.addDate.toString(), arrayJSONObject.getString("modifyDate"));
                                DatabaseController.insertUpdateData(contentValues, TablePosters.tableName, TablePosters.tableColumn.posterId.toString(), arrayJSONObject.getString("id"));


                                downloadImage(arrayJSONObject.getString("videoName"), arrayJSONObject.getString("id"));

                            }
                        }

                    } else {
                    }
                    AppSettings.putString(AppSettings.posterSyncTime, jsonObject.getString("syncTime"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void OnFail(String responce) {
//                AppUtils.showToastSort(mActivity, responce);
            }
        });
    }

    public static String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteFormat = stream.toByteArray();
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;
    }

    private void downloadImage(String url,String posterid) {

        WebServices.downloadImageApi_new(mActivity, url, new WebServicesImageCallback() {
            @Override
            public void OnFail(String responce) {

            }
            @Override
            public void OnBitmapSuccess(Bitmap bitmap) {
                String encodedString = getEncoded64ImageStringFromBitmap(bitmap);
                ContentValues mContentValuesDistrict = new ContentValues();
                mContentValuesDistrict.put(TablePosters.tableColumn.posterId.toString(), posterid);
                mContentValuesDistrict.put(TablePosters.tableColumn.posterUrl_base64.toString(), encodedString);
                DatabaseController.updateEqual(mContentValuesDistrict, TablePosters.tableName, TablePosters.tableColumn.posterId.toString(), posterid);
                AndroidNetworking.evictAllBitmap(); // clear LruCache
            }
        });
    }

    public void downloadFile(String URL) {
        //EditText et=(EditText)findViewById(R.id.txturl);
        String url = URL;

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), AppConstants.projectName);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }

        String filename = url.substring(url.lastIndexOf('/') + 1);
        pathl = mediaStorageDir + "/" + filename;

        Log.d("File", pathl);

        File varTmpDir = new File(pathl);
        boolean exists = varTmpDir.exists();

        if (!exists) {
            try {
                Log.d("File", pathl + " Does Not Exist");
                Log.d("File Name", filedownload.get(downloadcount).get("videoName"));
                if (!url.equals("")) {
                    BackTask bt = new BackTask();
                    bt.execute(url);
                } else {
                }
            }catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }
            //Toast.makeText(getApplicationContext(), "File does not exist", Toast.LENGTH_SHORT).show();
        } else {

            ContentValues mContentValuesDistrict = new ContentValues();
            mContentValuesDistrict.put(TableVideos.tableColumn.videoId.toString(), filedownload.get(downloadcount).get("id"));
            mContentValuesDistrict.put(TableVideos.tableColumn.videoLocation.toString(), pathl);

            DatabaseController.updateEqual(mContentValuesDistrict, TableVideos.tableName, TableVideos.tableColumn.videoId.toString(), filedownload.get(downloadcount).get("id"));

            downloadcount = downloadcount + 1;

            if (downloadcount < filedownload.size()) {
                //Toast.makeText(getApplicationContext(), "File exist", Toast.LENGTH_SHORT).show();
                Log.d("File", pathl + " Is Exist");
                Log.d("File exist", filedownload.get(downloadcount).get("videoName"));
                Log.e("", "downloadcount < ChildList.size() i.e : " + downloadcount + " < " + filedownload.size() + "    i.e  " + (downloadcount < filedownload.size()));

                AppUtils.showRequestDialog(mActivity, "Downloading Video");

                downloadFile(filedownload.get(downloadcount).get("videoName"));
            } else {

                if (AppUtils.isNetworkAvailable(this.mActivity)) {
                    getAllAshaApi();
                } else {
                    //AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                    setLogin();
                }
            }
        }

    }
//     background task to download file
    private class BackTask extends AsyncTask<String, Integer, Void> {
        NotificationManager mNotifyManager;
        NotificationCompat.Builder mBuilder;

        protected void onPreExecute() {
            super.onPreExecute();
            mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder = new NotificationCompat.Builder(getApplicationContext());
            mBuilder.setContentTitle("Video Downloading")
                    .setContentText("Download in progress")
                    .setSmallIcon(R.mipmap.logo);
            Toast.makeText(getApplicationContext(), "Downloading the file... The download progress is on tableName bar.", Toast.LENGTH_SHORT).show();

        }

        String storeDir;

        protected Void doInBackground(String... params) {
            URL url;
            int count;

            // External sdcard location
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), AppConstants.projectName);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdirs();
            }

            try {
                url = new URL(params[0]);

                try {
                    File f = new File(String.valueOf(mediaStorageDir));
                    if (f.exists()) {
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        InputStream is = con.getInputStream();
                        String pathr = url.getPath();
                        String filename = pathr.substring(pathr.lastIndexOf('/') + 1);
                        pathl = mediaStorageDir + "/" + filename;
                        FileOutputStream fos = new FileOutputStream(pathl);
                        int lenghtOfFile = con.getContentLength();
                        byte data[] = new byte[1024];
                        long total = 0;
                        while ((count = is.read(data)) != -1) {
                            total += count;
                            // publishing the progress
                            publishProgress((int) ((total * 100) / lenghtOfFile));
                            // writing data to output file
                            fos.write(data, 0, count);
                        }

                        is.close();
                        fos.flush();
                        fos.close();

                        String[] selectionArgs = new String[]{pathl};

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                            getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                        } else {


                            MediaScannerConnection.scanFile(getApplicationContext(), selectionArgs, null, new MediaScannerConnection.OnScanCompletedListener() {
                                /*
                                 *   (non-Javadoc)
                                 * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
                                 */
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.d("ExternalStorage", "Scanned " + path + ":");
                                    Log.d("ExternalStorage", "-> uri=" + uri);
                                }
                            });

                        }
                    } else {
                        Log.e("Error", "Not found: " + storeDir);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch tableName
                e.printStackTrace();
            }

            return null;

        }

        protected void onProgressUpdate(Integer... progress) {

            mBuilder.setProgress(100, progress[0], false);
            // Displays the progress bar on tableName
            mNotifyManager.notify(0, mBuilder.build());
        }

        protected void onPostExecute(Void result) {
            mBuilder.setContentText("Media Downloaded");
            // Removes the progress bar
            mBuilder.setProgress(0, 0, false);
            mNotifyManager.notify(0, mBuilder.build());
            mNotifyManager.cancel(0);
            Toast.makeText(getApplicationContext(), "Media Downloaded", Toast.LENGTH_SHORT).show();

            ContentValues mContentValuesDistrict = new ContentValues();
            mContentValuesDistrict.put(TableVideos.tableColumn.videoId.toString(), filedownload.get(downloadcount).get("id"));
            mContentValuesDistrict.put(TableVideos.tableColumn.videoLocation.toString(), pathl);

            DatabaseController.updateEqual(mContentValuesDistrict, TableVideos.tableName, TableVideos.tableColumn.videoId.toString(), filedownload.get(downloadcount).get("id"));

            downloadcount = downloadcount + 1;

            if (downloadcount < filedownload.size()) {
                AppUtils.showRequestDialog(mActivity, "Downloading Video");

                downloadFile(filedownload.get(downloadcount).get("videoName"));
            } else {
                if (AppUtils.isNetworkAvailable(mActivity)) {
                    getAllAshaApi();
                } else {
                    //AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                    setLogin();
                }
            }

        }

    }

    private void setLogin() {


        final long dataExit = DatabaseController.isDataExit(TableVillage.tableName);

        if (!AppSettings.getString(AppSettings.villageCountNew).isEmpty()) {
            if (Integer.parseInt(AppSettings.getString(AppSettings.villageCountNew)) == dataExit) {

                AppUtils.hideDialog();

                if (AppSettings.getString(AppSettings.loginId).isEmpty()) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                    finish();
                } else {
                    if(DatabaseController.getNurseIdCheckedInData().size()==0)
                    {
                        startActivity(new Intent(mActivity, CheckInActivity.class));
                        finish();
                    }
                    else {
                        DataBaseHelper.copyDatabase(mActivity);
                        AppSettings.putString(AppSettings.from, "0");
                        startActivity(new Intent(mActivity, MainActivity.class));
                        finish();
                    }
                }

            } else {
                if (AppUtils.isNetworkAvailable(mActivity)) {
                    getAllLocationApi();
                }
            }
        }
        else if (AppUtils.isNetworkAvailable(mActivity)) {
            getAllLocationApi();
        }

    }

    private void setCoachLogin() {

        if (AppSettings.getString(AppSettings.loginId).isEmpty()) {
            startActivity(new Intent(mActivity, LoginActivity.class));
            finish();
        } else {

            if (!AppSettings.getString(AppSettings.loungeId).isEmpty()) {
                startActivity(new Intent(mActivity, MainActivity.class));
                finish();
            } else {
                startActivity(new Intent(mActivity, CoachLoungeSelectionActivity.class));
                finish();
            }

        }

    }

    public void saveGpUrban() {
        String mCSVfile = "GPUrban.csv";
        AssetManager manager = this.getAssets();
        InputStream inStream = null;
        try {
            inStream = manager.open(mCSVfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
        String line = "";
        DatabaseController.myDataBase.beginTransaction();

        try {
            while ((line = buffer.readLine()) != null) {
                //Log.d("line", line);
                String[] colums = line.split(",");
                if (colums.length != 5) {
                    //Log.d("CSVParser","Skipping Bad CSV Row");
                    continue;
                }

                //VILLAGE DATA
                ContentValues mContentValuesVillage = new ContentValues();
                mContentValuesVillage.put(TableVillage.tableColumn.villageId.toString(), colums[0].trim());
                mContentValuesVillage.put(TableVillage.tableColumn.villageName.toString(), colums[1].trim());
                mContentValuesVillage.put(TableVillage.tableColumn.blockId.toString(), colums[2].trim());
                mContentValuesVillage.put(TableVillage.tableColumn.urbanRural.toString(), colums[3].trim());
                mContentValuesVillage.put(TableVillage.tableColumn.status.toString(), colums[4].trim());

                DatabaseController.insertData(mContentValuesVillage, TableVillage.tableName);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        DatabaseController.myDataBase.setTransactionSuccessful();
        DatabaseController.myDataBase.endTransaction();
    }

    private void getAllAshaApi() {

        WebServices.getApi(mActivity, AppUrls.getAshaMaster,false,false, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        JSONArray staffDetail = jsonObject.getJSONArray("ashaList");

                        try {

                            DatabaseController.myDataBase.beginTransaction();

                            DatabaseController.myDataBase.delete(TableAsha.tableName, null, null);

                            for (int i = 0; i < staffDetail.length(); i++) {

                                JSONObject detailJSONObject = staffDetail.getJSONObject(i);

                                //DISTRICT DATA
                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableAsha.tableColumn.ashaId.toString(), detailJSONObject.getString("ashaId"));
                                mContentValues.put(TableAsha.tableColumn.ashaName.toString(), detailJSONObject.getString("ashaName"));
                                mContentValues.put(TableAsha.tableColumn.blockName.toString(), detailJSONObject.getString("blockName"));
                                mContentValues.put(TableAsha.tableColumn.mobName.toString(), detailJSONObject.getString("mobileNumber"));

                                DatabaseController.insertData(mContentValues, TableAsha.tableName);

                            }

                            DatabaseController.myDataBase.setTransactionSuccessful();

                        } finally {
                            DatabaseController.myDataBase.endTransaction();
                        }
                    } else {

                    }

                    if (AppUtils.isNetworkAvailable(mActivity)) {
                        getAllFacilityApi();
                    } else {
                        setLogin();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

//                AppUtils.showToastSort(mActivity, responce);

            }
        });

    }

    private void getAllFacilityApi() {
        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("districtId", "");
            json.put(AppConstants.projectName, jsonData);
            Log.v("getAllStaffApi", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.getFacility, json,false,false, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {
            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {
                try {
                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("facilityList");

                        DatabaseController.myDataBase.delete(TableFacility.tableName, null, null);

                        try {

                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject arrayJSONObject = jsonArray.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableFacility.tableColumn.facilityId.toString(), arrayJSONObject.getString("facilityId"));
                                mContentValues.put(TableFacility.tableColumn.facilityType.toString(), arrayJSONObject.getString("facilityType"));
                                mContentValues.put(TableFacility.tableColumn.facilityTypeId.toString(), arrayJSONObject.getString("facilityTypeId"));
                                mContentValues.put(TableFacility.tableColumn.facilityName.toString(), arrayJSONObject.getString("facilityName"));
                                mContentValues.put(TableFacility.tableColumn.districtName.toString(), arrayJSONObject.getString("districtName"));
                                mContentValues.put(TableFacility.tableColumn.priority.toString(), arrayJSONObject.getString("proiority"));
                                mContentValues.put(TableFacility.tableColumn.priCodeDistrict.toString(), arrayJSONObject.getString("priCodeDistrict"));

                                DatabaseController.insertData(mContentValues, TableFacility.tableName);

                            }

                            DatabaseController.myDataBase.setTransactionSuccessful();

                        } finally {
                            DatabaseController.myDataBase.endTransaction();
                        }

                        setLogin();


                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

//                AppUtils.showToastSort(mActivity, responce);

            }
        });

    }

    private void saveGeoData() {

        final long dataExit = DatabaseController.isDataExit(TableVillage.tableName);

        if (!AppSettings.getString(AppSettings.villageCountNew).isEmpty()) {
            if (Integer.parseInt(AppSettings.getString(AppSettings.villageCountNew)) == dataExit) {

                setLogin();

            } else {
                //saveDistrict();
                //saveBlock();
                //saveGpUrban();
                //saveGpRural();
            }
        } else {
            //saveDistrict();
            //saveBlock();
            //saveGpUrban();
            //saveGpRural();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                        PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) !=
                                        PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                                        PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                                        PackageManager.PERMISSION_GRANTED) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.CAMERA,
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                }, REQUEST_PERMISSIONS);
                            }
                        } else {
                            // Start location updates
                            // Note - in emulator location appears to be null if no other app is using GPS at time.
                            // So if just turning on device's location services getLastLocation will likely not
                            // return anything
                            //Toast.makeText(context, "starting GPS updates", Toast.LENGTH_LONG).show();

                            if (AppUtils.isNetworkAvailable(this.mActivity)) {
                                getAllLoungeApi();
                            } else {

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setLogin();
                                    }
                                }, 1000);
                            }
                        }
                        break;
                    case Activity.RESULT_CANCELED:

                        checkPermissions();

                        break;
                }
                break;
        }
    }

    private void getIMEINumberApi() {

        AppUtils.showRequestDialog(mActivity);

        String url = AppUrls.checkImei;
        Log.v("getAllLoungeApi-URL", url);

        JSONObject json = new JSONObject();
        JSONObject json_data = new JSONObject();

        try {

            json_data.put("imeiNumber",  AppUtils.getDeviceIMEI(mActivity));

            json.put(AppConstants.projectName, json_data);

            Log.v("getAllLoungeApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(url)
                .addJSONObjectBody(json)
                .setPriority(Priority.HIGH)
                .addHeaders("package", AppUtils.md5(getPackageName()))
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        parseNumberJSON(response);
                    }

                    @Override
                    public void onError(ANError error) {
                        AppUtils.hideDialog();
                        // handle error
                        if (error.getErrorCode() != 0) {
//                            AppUtils.showToastSort(mActivity,String.valueOf(error.getErrorCode()));
                            Log.d("onError errorCode ", "onError errorCode : " + error.getErrorCode());
                            Log.d("onError errorBody", "onError errorBody : " + error.getErrorBody());
                            Log.d("onError errorDetail", "onError errorDetail : " + error.getErrorDetail());
                        } else {
//                            AppUtils.showToastSort(mActivity, String.valueOf(error.getErrorDetail()));
                        }
                    }
                });
    }

    private void parseNumberJSON(String result){

        Log.d("response ", result);

        AppUtils.hideDialog();

        AppSettings.putString(AppSettings.checkIMEI,"0");

        try {

            JSONObject response = new JSONObject(result);

            if (response.has(AppConstants.projectName)) {

                JSONObject resobj= response.getJSONObject(AppConstants.projectName);

                if (resobj.getString("resCode").equals("1")) {

                    if (resobj.getString("status").equals("1")) {

                        AppSettings.putString(AppSettings.checkIMEI,"1");

                        if (AppUtils.isNetworkAvailable(this.mActivity)) {
                            getAllLoungeApi();
                        } else {
//                            AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setLogin();
                                }
                            },1000);
                        }

                    }
                    else
                    {
                        AppSettings.putString(AppSettings.checkIMEI,"0");
                        setLogin();
                    }
                }
                else {
                    setLogin();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    private void getEncypApi() {

        AppUtils.showRequestDialog(mActivity);

        String url = AppUrls.testEncryption;
        Log.v("getAllLoungeApi-URL", url);

        JSONObject json = new JSONObject();
        JSONObject json_data = new JSONObject();

        try {

            //String str = "नमस्ते आप कैसे हैं?";
            String str = "Hello";

            byte[] by = MCrypt.encrypt(MCrypt.convertStringToUTF8(str.trim()));
            String data = MCrypt.bytesToHex(by);

            //String data = MCrypt.encryptNew(str.trim());

            json_data.put("text",  data);

            json.put(AppConstants.projectName, json_data);

            Log.v("getAllLoungeApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(url)
                .addJSONObjectBody(json)
                .setPriority(Priority.HIGH)
                .addHeaders("package", AppUtils.md5(getPackageName()))
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        parseEncypJSON(response);
                    }

                    @Override
                    public void onError(ANError error) {
                        AppUtils.hideDialog();
                        // handle error
                        if (error.getErrorCode() != 0) {
                            AppUtils.showToastSort(mActivity,String.valueOf(error.getErrorCode()));
                            Log.d("onError errorCode ", "onError errorCode : " + error.getErrorCode());
                            Log.d("onError errorBody", "onError errorBody : " + error.getErrorBody());
                            Log.d("onError errorDetail", "onError errorDetail : " + error.getErrorDetail());
                        } else {
//                            AppUtils.showToastSort(mActivity, String.valueOf(error.getErrorDetail()));
                        }
                    }
                });
    }

    private void parseEncypJSON(String result){

        Log.d("response ", result);

        AppUtils.hideDialog();

        try {

            JSONObject response = new JSONObject(result);

            if (response.has(AppConstants.projectName)) {

                JSONObject resobj= response.getJSONObject(AppConstants.projectName);

                String desc = resobj.getString("decrypted");
                String ency = resobj.getString("encrypted");

                byte[] decrypt = MCrypt.decrypt(ency);

                //Convert byte[] to String
                String s = new String(decrypt);

                s = MCrypt.convertUTF8ToString(s);

                Log.d("decrypt",s);
            }
        } catch (JSONException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
