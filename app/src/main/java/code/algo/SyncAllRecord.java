package code.algo;

import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.kmcapp.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import code.basic.LoginActivity;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableBabyAdmission;
import code.database.TableBabyMonitoring;
import code.database.TableBabyRegistration;
import code.database.TableBirthReview;
import code.database.TableBreastFeeding;
import code.database.TableComments;
import code.database.TableCounsellingPosters;
import code.database.TableDoctorRound;
import code.database.TableDutyChange;
import code.database.TableInvestigation;
import code.database.TableKMC;
import code.database.TableLoungeAssessment;
import code.database.TableLoungeServices;
import code.database.TableMotherMonitoring;
import code.database.TableMotherPastInformation;
import code.database.TableMotherRegistration;
import code.database.TableNotification;
import code.database.TableStaff;
import code.database.TableStuck;
import code.database.TableTreatment;
import code.database.TableUser;
import code.database.TableVaccination;
import code.database.TableWeight;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseActivity;

public class SyncAllRecord {

    static BaseActivity mActivity;
    static String uuid = "";

    static ArrayList<HashMap<String, String>> dutyListCheckIn = new ArrayList();
    static ArrayList<HashMap<String, String>> dutyListCheckOut = new ArrayList();
    static ArrayList<HashMap<String, String>> loungeAssessment = new ArrayList();
    static ArrayList<HashMap<String, String>> stuckList = new ArrayList();
    static ArrayList<HashMap<String, String>> birthReviewList = new ArrayList();
    static ArrayList<HashMap<String, String>> loungeServiceList = new ArrayList();

    static ArrayList<HashMap<String, String>> weightList = new ArrayList();
    static ArrayList<HashMap<String, String>> feedingList = new ArrayList();
    static ArrayList<HashMap<String, String>> sscList = new ArrayList();
    static ArrayList<HashMap<String, String>> medicationsList = new ArrayList();
    static ArrayList<HashMap<String, String>> investigationSampleList = new ArrayList();
    static ArrayList<HashMap<String, String>> investigationList = new ArrayList();
    static ArrayList<HashMap<String, String>> vaccinationsList = new ArrayList();
    static ArrayList<HashMap<String, String>> babyUpdateList = new ArrayList();
    static ArrayList<HashMap<String, String>> assessmentList = new ArrayList();
    static ArrayList<HashMap<String, String>> commentList = new ArrayList();
    static ArrayList<HashMap<String, String>> doctorRound = new ArrayList();
    static ArrayList<HashMap<String, String>> babyAdmissionList = new ArrayList();

    static ArrayList<HashMap<String, String>> motherUpdateList = new ArrayList();
    static ArrayList<HashMap<String, String>> mAssessmentList = new ArrayList();
    static ArrayList<HashMap<String, String>> babyConsullingByPostersList = new ArrayList();

    //TODO DutyChange All Data
    public static void postDutyChange(BaseActivity baseActivity) {

        mActivity = baseActivity;

        AppUtils.showRequestDialog(mActivity);

        dutyListCheckIn.clear();
        dutyListCheckIn.addAll(DatabaseController.getDutyChange(AppSettings.getString(AppSettings.loungeId)));

        Log.v("jfsdf", String.valueOf(dutyListCheckIn.size()));

        if (dutyListCheckIn.size() > 0) {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                checkInApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        } else {

            postDutyCheckOut(mActivity);
        }
    }

    //TODO DutyChange All Data
    public static void postDutyCheckOut(BaseActivity baseActivity) {

        mActivity = baseActivity;

        dutyListCheckOut.clear();
        dutyListCheckOut.addAll(DatabaseController.getDutyCheckOutChange(AppSettings.getString(AppSettings.loungeId)));

        if (dutyListCheckOut.size() > 0) {
            if (AppUtils.isNetworkAvailable(mActivity)) {

                Log.v("Second","checkout ke liya gya yha se");

                checkOutApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        } else {

            Log.v("Second","size zero hai");

            postLoungeAssessment(mActivity);
        }
    }

    //TODO Lounge Assessment All Data
    public static void postLoungeAssessment(BaseActivity baseActivity) {

        mActivity = baseActivity;

        loungeAssessment.clear();
        loungeAssessment.addAll(DatabaseController.getLoungeAssessment(AppSettings.getString(AppSettings.loungeId)));

        if (loungeAssessment.size() > 0) {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                loungeAssessmentApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        } else {
            postHelp(mActivity);
        }

    }

    //TODO Logout Api
    public static void postLogoutApi(BaseActivity baseActivity) {

        mActivity = baseActivity;

        if (AppUtils.isNetworkAvailable(mActivity)) {
            logoutApi();
        } else {
            AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
        }
    }

    //TODO Post Help
    public static void postHelp(BaseActivity baseActivity) {

        mActivity = baseActivity;

        stuckList.clear();
        stuckList.addAll(DatabaseController.getStuckData());

        if (stuckList.size() > 0) {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postStuckApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        } else {
            postBirthReview(mActivity);
        }

    }

    //TODO Birth Review
    public static void postBirthReview(BaseActivity baseActivity) {

        mActivity = baseActivity;

        birthReviewList.clear();
        birthReviewList.addAll(DatabaseController.getBirthData());

        if (birthReviewList.size() > 0) {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postBirthReviewApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        } else {
            postLoungeService(mActivity);
        }

    }

    //TODO Lounge Service
    public static void postLoungeService(BaseActivity baseActivity) {

        mActivity = baseActivity;

        loungeServiceList.clear();
        loungeServiceList.addAll(DatabaseController.getLoungeServiceData());

        if (loungeServiceList.size() > 0) {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postLoungeServiceApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        } else {
            getBabyDataToUpdate();
        }

    }

    //CheckIn
    public static void checkInApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonProject = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        try {

            jsonProject.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            for (int i = 0; i < dutyListCheckIn.size(); i++) {
                JSONObject jsonData = null;
                try {
                    jsonData = new JSONObject(dutyListCheckIn.get(i).get("json"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(jsonData);
            }
            jsonProject.put("checkInData", jsonArray);
            json.put(AppConstants.projectName, jsonProject);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonProject.toString()));
            Log.v("checkInApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.nurseCheckIn, json, true, false, new WebServicesCallback() {
            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        JSONArray idsArrayList = jsonObject.getJSONArray("id");

                        try {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < idsArrayList.length(); i++) {

                                JSONObject arrayListJSONObject = idsArrayList.getJSONObject(i);
                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableDutyChange.tableColumn.serverId.toString(), arrayListJSONObject.getString("id"));
                                mContentValues.put(TableDutyChange.tableColumn.uuid.toString(), arrayListJSONObject.getString("localId"));
                                mContentValues.put(TableDutyChange.tableColumn.isDataSynced.toString(), "1");
                                mContentValues.put(TableDutyChange.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());

                                DatabaseController.updateEqual(mContentValues, TableDutyChange.tableName
                                        , "uuid ", arrayListJSONObject.getString("localId"));
                            }
                            DatabaseController.myDataBase.setTransactionSuccessful();

                        } finally {
                            DatabaseController.myDataBase.endTransaction();
                        }
                    } else {

                    }


                    Log.v("chechinhogya", "ok hain");

                    postDutyCheckOut(mActivity);

                } catch (JSONException e) {
                    e.printStackTrace();
                    postDutyCheckOut(mActivity);
                }

            }

            @Override
            public void OnFail(String responce) {

                //  AppUtils.showToastSort(mActivity, responce);

                postDutyCheckOut(mActivity);
            }
        });

    }

    //CheckOut
    public static void checkOutApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {

            jsonObject.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            for (int i = 0; i < dutyListCheckOut.size(); i++) {
                JSONObject jsonData = new JSONObject(dutyListCheckOut.get(i).get("json"));

                jsonArray.put(jsonData);
            }

            jsonObject.put("checkOutData", jsonArray);

            json.put(AppConstants.projectName, jsonObject);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonObject.toString()));

            Log.v("checkOutApi", json.toString());

            Log.v("Third", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.nurseCheckOut, json, false, false, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {
                    Log.v("Fourth", jsonObject1.toString());
                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        JSONArray idsArrayList = jsonObject.getJSONArray("id");

                        try {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < idsArrayList.length(); i++) {

                                JSONObject arrayListJSONObject = idsArrayList.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableDutyChange.tableColumn.isDataSyncedCheckOut.toString(), "1");
                                mContentValues.put(TableDutyChange.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());

                                DatabaseController.updateEqual(mContentValues, TableDutyChange.tableName
                                        , "uuid ", arrayListJSONObject.getString("localId"));
                            }

                            DatabaseController.myDataBase.setTransactionSuccessful();

                        } finally {
                            DatabaseController.myDataBase.endTransaction();
                        }
                    } else {

                        Log.v("Fourth", jsonObject1.toString());
                    }

                    postLoungeAssessment(mActivity);

                } catch (JSONException e) {

                    Log.v("Fourth", e.getMessage());

                    e.printStackTrace();
                }
            }

            @Override
            public void OnFail(String responce) {
                AppUtils.showToastSort(mActivity, responce);
                Log.v("Fourth", responce.toString());
                postLoungeAssessment(mActivity);
            }
        });

    }

    //Lounge Assessment
    public static void loungeAssessmentApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonObject = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        try {

            jsonObject.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            for (int i = 0; i < loungeAssessment.size(); i++) {
                JSONObject jsonData = null;
                try {
                    jsonData = new JSONObject(loungeAssessment.get(i).get("json"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jsonArray.put(jsonData);
            }

            jsonObject.put("assessmentData", jsonArray);

            json.put(AppConstants.projectName, jsonObject);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonObject.toString()));

            Log.v("loungeAssessmentApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.loungeAssessment, json, false, false, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        JSONArray idsArrayList = jsonObject.getJSONArray("id");

                        try {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < idsArrayList.length(); i++) {

                                JSONObject arrayListJSONObject = idsArrayList.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableLoungeAssessment.tableColumn.serverId.toString(), arrayListJSONObject.getString("id"));
                                mContentValues.put(TableLoungeAssessment.tableColumn.uuid.toString(), arrayListJSONObject.getString("localId"));
                                mContentValues.put(TableLoungeAssessment.tableColumn.isDataSynced.toString(), "1");
                                mContentValues.put(TableLoungeAssessment.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());
                                mContentValues.put(TableLoungeAssessment.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());

                                DatabaseController.updateEqual(mContentValues, TableLoungeAssessment.tableName
                                        , "uuid ", arrayListJSONObject.getString("localId"));
                            }

                            DatabaseController.myDataBase.setTransactionSuccessful();

                        } finally {
                            DatabaseController.myDataBase.endTransaction();
                        }
                    } else {

                    }

                    postHelp(mActivity);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

                AppUtils.showToastSort(mActivity, responce);

                postHelp(mActivity);
            }
        });

    }

    //Lounge logoutApi
    public static void logoutApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();

        try {

            jsonData.put("loginId", AppSettings.getString(AppSettings.loginId));
            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            json.put(AppConstants.projectName, jsonData);

            Log.v("setLogoutApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.logout, json, true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        String syncTime = AppSettings.getString(AppSettings.syncTime);
                        String geoSyncTime = AppSettings.getString(AppSettings.geoSyncTime);
                        String staffSyncTime = AppSettings.getString(AppSettings.staffSyncTime);
                        String villageCount = AppSettings.getString(AppSettings.villageCountNew);
                        String checkIMEI = AppSettings.getString(AppSettings.checkIMEI);
                        String loungeId = AppSettings.getString(AppSettings.loungeId);
                        String facId = AppSettings.getString(AppSettings.facId);
                        String district = AppSettings.getString(AppSettings.loginDistrict);

                        AppSettings.clearSharedPreference();

                        AppSettings.putString(AppSettings.syncTime, syncTime);
                        AppSettings.putString(AppSettings.geoSyncTime, geoSyncTime);
                        AppSettings.putString(AppSettings.staffSyncTime, staffSyncTime);
                        AppSettings.putString(AppSettings.villageCountNew, villageCount);
                        AppSettings.putString(AppSettings.checkIMEI, checkIMEI);
                        AppSettings.putString(AppSettings.loungeId, loungeId);
                        AppSettings.putString(AppSettings.facId, facId);
                        AppSettings.putString(AppSettings.loginDistrict, district);

                        DatabaseController.delete(TableUser.tableName, null, null);
                        DatabaseController.delete(TableBabyRegistration.tableName, null, null);
                        DatabaseController.delete(TableBabyAdmission.tableName, null, null);
                        DatabaseController.delete(TableBabyMonitoring.tableName, null, null);
                        DatabaseController.delete(TableBreastFeeding.tableName, null, null);
                        DatabaseController.delete(TableComments.tableName, null, null);
                        DatabaseController.delete(TableWeight.tableName, null, null);
                        DatabaseController.delete(TableKMC.tableName, null, null);
                        DatabaseController.delete(TableDoctorRound.tableName, null, null);
                        DatabaseController.delete(TableInvestigation.tableName, null, null);
                        DatabaseController.delete(TableTreatment.tableName, null, null);
                        DatabaseController.delete(TableVaccination.tableName, null, null);
                        DatabaseController.delete(TableCounsellingPosters.tableName, null, null);

                        DatabaseController.delete(TableBirthReview.tableName, null, null);
                        DatabaseController.delete(TableLoungeAssessment.tableName, null, null);
                        DatabaseController.delete(TableLoungeServices.tableName, null, null);
                        DatabaseController.delete(TableNotification.tableName, null, null);
                        DatabaseController.delete(TableStaff.tableName, null, null);
                        DatabaseController.delete(TableStuck.tableName, null, null);


                        AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorLogout));

                        mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                        mActivity.finishAffinity();
                    } else {
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

    //Lounge postStuckApi
    public static void postStuckApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonObject = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        try {

            for (int i = 0; i < stuckList.size(); i++) {
                JSONObject jsonData = null;
                try {
                    jsonData = new JSONObject(stuckList.get(i).get("json"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jsonArray.put(jsonData);
            }

            jsonObject.put("stuckData", jsonArray);

            json.put(AppConstants.projectName, jsonObject);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonObject.toString()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.postStuckData, json, false, false, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        JSONArray idsArrayList = jsonObject.getJSONArray("id");

                        try {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < idsArrayList.length(); i++) {

                                JSONObject arrayListJSONObject = idsArrayList.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableStuck.tableColumn.uuid.toString(), arrayListJSONObject.getString("localId"));
                                mContentValues.put(TableStuck.tableColumn.status.toString(), "1");

                                DatabaseController.updateEqual(mContentValues, TableStuck.tableName
                                        , "uuid ", arrayListJSONObject.getString("localId"));
                            }

                            DatabaseController.myDataBase.setTransactionSuccessful();

                        } finally {
                            DatabaseController.myDataBase.endTransaction();
                        }
                    } else {

                    }
                    AppUtils.showToastSort(mActivity, mActivity.getString(R.string.helpToast));
                    postBirthReview(mActivity);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {
                AppUtils.showToastSort(mActivity, responce);

                postBirthReview(mActivity);
            }
        });

    }

    //Lounge postBirthReviewApi
    public static void postBirthReviewApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonObject = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        try {

            jsonObject.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            for (int i = 0; i < birthReviewList.size(); i++) {
                JSONObject jsonData = null;
                try {
                    jsonData = new JSONObject(birthReviewList.get(i).get("json"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jsonArray.put(jsonData);
            }

            jsonObject.put("reviewData", jsonArray);

            json.put(AppConstants.projectName, jsonObject);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonObject.toString()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.loungeBirthReview, json, true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        JSONArray idsArrayList = jsonObject.getJSONArray("id");

                        try {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < idsArrayList.length(); i++) {

                                JSONObject arrayListJSONObject = idsArrayList.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableBirthReview.tableColumn.serverId.toString(), arrayListJSONObject.getString("id"));
                                mContentValues.put(TableBirthReview.tableColumn.uuid.toString(), arrayListJSONObject.getString("localId"));
                                mContentValues.put(TableBirthReview.tableColumn.status.toString(), "1");

                                DatabaseController.updateEqual(mContentValues, TableBirthReview.tableName
                                        , "uuid ", arrayListJSONObject.getString("localId"));
                            }

                            DatabaseController.myDataBase.setTransactionSuccessful();

                        } finally {
                            DatabaseController.myDataBase.endTransaction();
                        }
                    } else {

                    }

                    AppSettings.putString(AppSettings.syncTime, AppUtils.currentTimestampFormat());
                    AppUtils.hideDialog();

                    postLoungeService(mActivity);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

                AppUtils.showToastSort(mActivity, responce);

                AppSettings.putString(AppSettings.syncTime, AppUtils.currentTimestampFormat());
                AppUtils.hideDialog();
                ;

                postLoungeService(mActivity);
            }
        });


    }

    //Lounge postLoungeServiceApi
    public static void postLoungeServiceApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonObject = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        try {

            jsonObject.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            for (int i = 0; i < loungeServiceList.size(); i++) {
                JSONObject jsonData = new JSONObject();

                jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                jsonData.put("nurseId", loungeServiceList.get(i).get("nurseId"));
                jsonData.put("latitude", loungeServiceList.get(i).get("latitude"));
                jsonData.put("longitude", loungeServiceList.get(i).get("longitude"));
                jsonData.put("type", loungeServiceList.get(i).get("type"));
                jsonData.put("shift", loungeServiceList.get(i).get("slot"));
                jsonData.put("value", loungeServiceList.get(i).get("value"));
                jsonData.put("localId", loungeServiceList.get(i).get("uuid"));
                jsonData.put("localDateTime", loungeServiceList.get(i).get("addDate"));

                jsonArray.put(jsonData);
            }

            jsonObject.put("serviceData", jsonArray);

            json.put(AppConstants.projectName, jsonObject);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonObject.toString()));

            Log.v("postLoungeServiceApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.loungeServices, json, true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        JSONArray idsArrayList = jsonObject.getJSONArray("id");

                        try {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < idsArrayList.length(); i++) {

                                JSONObject arrayListJSONObject = idsArrayList.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableLoungeServices.tableColumn.serverId.toString(), arrayListJSONObject.getString("id"));
                                mContentValues.put(TableLoungeServices.tableColumn.uuid.toString(), arrayListJSONObject.getString("localId"));
                                mContentValues.put(TableLoungeServices.tableColumn.status.toString(), "1");

                                DatabaseController.updateEqual(mContentValues, TableLoungeServices.tableName
                                        , "uuid ", arrayListJSONObject.getString("localId"));
                            }

                            DatabaseController.myDataBase.setTransactionSuccessful();

                        } finally {
                            DatabaseController.myDataBase.endTransaction();
                        }
                    } else {

                    }

                    AppSettings.putString(AppSettings.syncTime, AppUtils.currentTimestampFormat());
                    AppUtils.hideDialog();
                    ;

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

                Log.v("erroe", responce.toString());

                AppUtils.showToastSort(mActivity, responce);

                AppSettings.putString(AppSettings.syncTime, AppUtils.currentTimestampFormat());
                AppUtils.hideDialog();
                ;
            }
        });


    }


    //For Baby Data
    public static void getBabyDataToUpdate() {

        babyUpdateList.clear();
        babyUpdateList.addAll(DatabaseController.getBabyToSyncUpdates(
                AppSettings.getString(AppSettings.loungeId)));

        if (babyUpdateList.size() > 0) {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                doBabyUpdationApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        } else {
            getBabyWeightDataToUpdate();
        }
    }

    public static void getBabyWeightDataToUpdate() {

        weightList.clear();
        weightList.addAll(DatabaseController.getWeightToSync(AppSettings.getString(AppSettings.loungeId)));

        if (weightList.size() > 0) {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postDailyWeightApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        } else {
            getBabyAssessDataToUpdate();
        }
    }

    public static void getBabyAssessDataToUpdate() {

        assessmentList.clear();
        assessmentList.addAll(DatabaseController.getBabyAssessToSync(AppSettings.getString(AppSettings.loungeId)));

        if (assessmentList.size() > 0) {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postAssessmentApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        } else {
            getBabyKMCDataToUpdate();
        }
    }

    public static void getBabyKMCDataToUpdate() {

        sscList.clear();
        sscList.addAll(DatabaseController.getSSCDateToSync(AppSettings.getString(AppSettings.loungeId)));

        if (sscList.size() > 0) {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postSSCApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        } else {
            getBabyFeedingDataToUpdate();
        }
    }

    public static void getBabyFeedingDataToUpdate() {

        feedingList.clear();
        feedingList.addAll(DatabaseController.getFeedingDataToSync(AppSettings.getString(AppSettings.loungeId)));

        if (feedingList.size() > 0) {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postFeedingApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        } else {
            getDoctorRound();
        }
    }

    public static void getDoctorRound() {

        doctorRound.clear();
        doctorRound.addAll(DatabaseController.getDoctorRoundDataToSync(AppSettings.getString(AppSettings.loungeId)));

        if (doctorRound.size() > 0) {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postDoctorRoundApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        } else {
            getMedicationDataToUpdate();
        }
    }

    public static void getMedicationDataToUpdate() {

        medicationsList.clear();
        medicationsList.addAll(DatabaseController.getMedicationDataToSync(AppSettings.getString(AppSettings.loungeId)));

        if (medicationsList.size() > 0) {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postMedicationsApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        } else {
            getInvestigationSampleDataToUpdate();
        }
    }

    public static void getInvestigationSampleDataToUpdate() {

        investigationSampleList.clear();
        investigationSampleList.addAll(DatabaseController.getInvestSampleDataToSync(AppSettings.getString(AppSettings.loungeId)));

        if (investigationSampleList.size() > 0) {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postInvesSampleApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        } else {
            getInvestigationDataToUpdate();
        }
    }

    public static void getInvestigationDataToUpdate() {

        investigationList.clear();
        investigationList.addAll(DatabaseController.getInvestDataToSync(AppSettings.getString(AppSettings.loungeId)));

        if (investigationList.size() > 0) {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postInvesApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        } else {
            getVaccinationDataToUpdate();
        }
    }

    public static void getVaccinationDataToUpdate() {

        vaccinationsList.clear();
        vaccinationsList.addAll(DatabaseController.getVaccinationDataToSync(AppSettings.getString(AppSettings.loungeId)));

        if (vaccinationsList.size() > 0) {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postVaccApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        } else {
            getCounsellingDataToUpdate();
            getConsuellingByPosters();
        }
    }

    private static void getConsuellingByPosters() {
        babyConsullingByPostersList.clear();
        babyConsullingByPostersList.addAll(DatabaseController.getBabyCounsellingposters());

        if (babyConsullingByPostersList.size() > 0) {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postCounsellingBypostersApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        }

    }

    public static void getCounsellingDataToUpdate() {

        babyAdmissionList.clear();
        babyAdmissionList.addAll(DatabaseController.getBabyCounsellingUpdates());

        if (babyAdmissionList.size() > 0) {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postCounsellingApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        } else {
            getAllComments();
        }
    }

    public static void doBabyUpdationApi() {

        WebServices.postApi(mActivity, AppUrls.updateBabyProfile, createJsonForBabyUpdation(), true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        ContentValues contentValues = new ContentValues();

                        contentValues.put(TableBabyRegistration.tableColumn.uuid.toString(), babyUpdateList.get(0).get("uuid"));
                        contentValues.put(TableBabyRegistration.tableColumn.isDataSynced.toString(), "1");

                        DatabaseController.insertUpdateData(contentValues, TableBabyRegistration.tableName, TableBabyRegistration.tableColumn.uuid.toString(), babyUpdateList.get(0).get("uuid"));


                    } else {

                    }

                    getBabyWeightDataToUpdate();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

                AppUtils.showToastSort(mActivity, responce);

                getBabyWeightDataToUpdate();

            }
        });
    }

    public static JSONObject createJsonForBabyUpdation() {

        JSONObject json = new JSONObject();

        JSONObject jsonData = new JSONObject();

        try {

            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("babyId", babyUpdateList.get(0).get("babyId"));
            jsonData.put("babyMCTSNumber", babyUpdateList.get(0).get("babyMCTSNumber"));
            jsonData.put("babyFileId", babyUpdateList.get(0).get("babyFileId"));
            jsonData.put("deliveryDate", babyUpdateList.get(0).get("deliveryDate"));
            jsonData.put("deliveryTime", AppUtils.convertTimeTo24HoursFormat(babyUpdateList.get(0).get("deliveryTime")));
            jsonData.put("babyGender", babyUpdateList.get(0).get("babyGender"));
            jsonData.put("deliveryType", babyUpdateList.get(0).get("deliveryType"));
            jsonData.put("babyWeight", babyUpdateList.get(0).get("babyWeight"));
            jsonData.put("babyCryAfterBirth", babyUpdateList.get(0).get("babyCryAfterBirth"));
            jsonData.put("babyNeedBreathingHelp", babyUpdateList.get(0).get("babyNeedBreathingHelp"));
            jsonData.put("babyPhoto", babyUpdateList.get(0).get("babyPhoto"));
            jsonData.put("birthWeightAvail", babyUpdateList.get(0).get("birthWeightAvail"));
            jsonData.put("reason", babyUpdateList.get(0).get("reason"));
            jsonData.put("firstTimeFeed", babyUpdateList.get(0).get("firstTimeFeed"));
            jsonData.put("typeOfBorn", babyUpdateList.get(0).get("typeOfBorn"));
            jsonData.put("typeOfOutBorn", babyUpdateList.get(0).get("typeOfOutBorn"));
            jsonData.put("vitaminKGiven", babyUpdateList.get(0).get("vitaminKGiven"));
            jsonData.put("wasApgarScoreRecorded", babyUpdateList.get(0).get("wasApgarScoreRecorded"));
            jsonData.put("apgarScoreVal", "");

            if (babyUpdateList.get(0).get("wasApgarScoreRecorded").
                    equalsIgnoreCase(mActivity.getString(R.string.yesValue))) {
                jsonData.put("apgarScoreVal", babyUpdateList.get(0).get("apgarScore"));
            }

            jsonData.put("babyPhoto", babyUpdateList.get(0).get("babyPhoto"));
            //jsonData.put( "babyPhoto","");

            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));

            Log.v("doBabyUpdationApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    //Post Daily Weight saved in local database
    public static void postDailyWeightApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        try {

            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            for (int i = 0; i < weightList.size(); i++) {
                JSONObject jsonWeight = new JSONObject();

                jsonWeight.put("babyId", weightList.get(i).get("babyId"));
                jsonWeight.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                jsonWeight.put("babyWeight", weightList.get(i).get("babyWeight"));
                jsonWeight.put("nurseId", weightList.get(i).get("nurseId"));
                jsonWeight.put("localId", weightList.get(i).get("uuid"));
                jsonWeight.put("weightDate", weightList.get(i).get("weightDate"));
                jsonWeight.put("isDeviceAvailAndWorking", weightList.get(i).get("isDeviceAvailAndWorking"));
                jsonWeight.put("reason", weightList.get(i).get("reason"));
                jsonWeight.put("image", weightList.get(i).get("weightImage"));
                jsonWeight.put("localDateTime", weightList.get(i).get("addDate"));

                jsonArray.put(jsonWeight);
            }

            jsonData.put("babyWeightData", jsonArray);

            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));

            Log.v("postDailyWeightApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.submitBabyWeight, json, true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));

                        JSONArray idsArrayList = jsonObject.getJSONArray("id");

                        try {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < idsArrayList.length(); i++) {

                                JSONObject arrayListJSONObject = idsArrayList.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableWeight.tableColumn.serverId.toString(), arrayListJSONObject.getString("id"));
                                mContentValues.put(TableWeight.tableColumn.uuid.toString(), arrayListJSONObject.getString("localId"));
                                mContentValues.put(TableWeight.tableColumn.isDataSynced.toString(), "1");
                                mContentValues.put(TableWeight.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());

                                DatabaseController.insertUpdateData(mContentValues, TableWeight.tableName, "uuid ", arrayListJSONObject.getString("localId"));

                            }

                            DatabaseController.myDataBase.setTransactionSuccessful();

                        } finally {
                            DatabaseController.myDataBase.endTransaction();
                        }
                    } else {

                    }

                    getBabyAssessDataToUpdate();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

                AppUtils.showToastSort(mActivity, responce);

                getBabyAssessDataToUpdate();

            }
        });
    }

    //Post Assessment saved in local database
    public static void postAssessmentApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        try {

            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            for (int i = 0; i < assessmentList.size(); i++) {
                JSONObject jsonObject = new JSONObject(assessmentList.get(i).get("json"));
                jsonArray.put(jsonObject);
            }

            jsonData.put("monitoringData", jsonArray);

            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));

            Log.v("postAssessmentApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.babyMonitoring, json, true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));

                        JSONArray idsArrayList = jsonObject.getJSONArray("id");

                        try {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < idsArrayList.length(); i++) {

                                JSONObject arrayListJSONObject = idsArrayList.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableBabyMonitoring.tableColumn.serverId.toString(), arrayListJSONObject.getString("id"));
                                mContentValues.put(TableBabyMonitoring.tableColumn.uuid.toString(), arrayListJSONObject.getString("localId"));
                                mContentValues.put(TableBabyMonitoring.tableColumn.isDataSynced.toString(), "1");
                                mContentValues.put(TableBabyMonitoring.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());

                                DatabaseController.insertUpdateData(mContentValues, TableBabyMonitoring.tableName, "uuid ", arrayListJSONObject.getString("localId"));

                            }

                            DatabaseController.myDataBase.setTransactionSuccessful();

                        } finally {
                            DatabaseController.myDataBase.endTransaction();
                        }
                    } else {

                    }

                    getBabyKMCDataToUpdate();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

                AppUtils.showToastSort(mActivity, responce);

                getBabyKMCDataToUpdate();

            }
        });
    }

    //Post Skin to Skin saved in local database
    public static void postSSCApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        try {

            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            for (int i = 0; i < sscList.size(); i++) {
                JSONObject jsonSsc = new JSONObject();

                jsonSsc.put("babyId", sscList.get(i).get("babyId"));
                jsonSsc.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                jsonSsc.put("startTime", sscList.get(i).get("startTime"));
                jsonSsc.put("endTime", sscList.get(i).get("endTime"));
                jsonSsc.put("provider", sscList.get(i).get("provider"));
                jsonSsc.put("startDate", sscList.get(i).get("startDate"));
                jsonSsc.put("endDate", sscList.get(i).get("endDate"));
                jsonSsc.put("nurseId", sscList.get(i).get("nurseId"));
                jsonSsc.put("localId", sscList.get(i).get("uuid"));

                jsonSsc.put("localDateTime", sscList.get(i).get("addDate"));

                jsonArray.put(jsonSsc);
            }

            jsonData.put("kmcData", jsonArray);

            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.skinToSkinTouch, json, true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));

                        JSONArray feedId = jsonObject.getJSONArray("id");

                        try {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < feedId.length(); i++) {

                                JSONObject jsonObjectId = feedId.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableKMC.tableColumn.serverId.toString(), jsonObjectId.getString("id"));
                                mContentValues.put(TableKMC.tableColumn.uuid.toString(), jsonObjectId.getString("localId"));
                                mContentValues.put(TableKMC.tableColumn.isDataSynced.toString(), "1");
                                mContentValues.put(TableKMC.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());

                                DatabaseController.insertUpdateData(mContentValues, TableKMC.tableName, "uuid ", jsonObjectId.getString("localId"));

                            }

                            DatabaseController.myDataBase.setTransactionSuccessful();

                        } finally {
                            DatabaseController.myDataBase.endTransaction();
                        }
                    } else {

                    }

                    getBabyFeedingDataToUpdate();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

                AppUtils.showToastSort(mActivity, responce);

                getBabyFeedingDataToUpdate();

            }
        });

    }

    //Post Feeding data saved in local database
    public static void postFeedingApi() {

        JSONObject json = new JSONObject();

        JSONObject jsonData = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        try {

            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            for (int i = 0; i < feedingList.size(); i++) {
                JSONObject jsonFeeding = new JSONObject();

                jsonFeeding.put("babyId", feedingList.get(i).get("babyId"));
                jsonFeeding.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                jsonFeeding.put("feedTime", feedingList.get(i).get("feedTime"));
                jsonFeeding.put("breastFeedDuration", feedingList.get(i).get("breastFeedDuration"));
                jsonFeeding.put("milkQuantity", feedingList.get(i).get("milkQuantity"));
                jsonFeeding.put("breastFeedMethod", feedingList.get(i).get("method"));
                jsonFeeding.put("localDateTime", feedingList.get(i).get("addDate"));

                if (feedingList.get(i).get("method").equalsIgnoreCase(mActivity.getString(R.string.breastValue))) {
                    jsonFeeding.put("feedingType", "1");
                } else if (feedingList.get(i).get("method").equalsIgnoreCase(mActivity.getString(R.string.ebfValue))) {
                    jsonFeeding.put("feedingType", "2");
                } else {
                    jsonFeeding.put("feedingType", "3");
                }

                jsonFeeding.put("nurseId", feedingList.get(i).get("nurseId"));
                jsonFeeding.put("localId", feedingList.get(i).get("uuid"));

                jsonArray.put(jsonFeeding);
            }

            jsonData.put("feedingData", jsonArray);

            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.postFeedingData, json, true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));

                        JSONArray feedId = jsonObject.getJSONArray("id");

                        try {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < feedId.length(); i++) {

                                JSONObject jsonObjectIds = feedId.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableBreastFeeding.tableColumn.serverId.toString(), jsonObjectIds.getString("id"));
                                mContentValues.put(TableBreastFeeding.tableColumn.uuid.toString(), jsonObjectIds.getString("localId"));
                                mContentValues.put(TableBreastFeeding.tableColumn.isDataSynced.toString(), "1");
                                mContentValues.put(TableBreastFeeding.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());

                                DatabaseController.insertUpdateData(mContentValues, TableBreastFeeding.tableName, "uuid ", jsonObjectIds.getString("localId"));
                            }

                            DatabaseController.myDataBase.setTransactionSuccessful();

                        } finally {
                            DatabaseController.myDataBase.endTransaction();
                        }
                    } else {

                    }

                    getDoctorRound();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

                AppUtils.showToastSort(mActivity, responce);

                getDoctorRound();

            }
        });

    }

    //Post DoctorRound saved in local database
    public static void postDoctorRoundApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {

            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            for (int i = 0; i < doctorRound.size(); i++) {
                JSONObject jsonRound = new JSONObject(doctorRound.get(i).get("json"));
                jsonArray.put(jsonRound);
            }

            jsonData.put("doctorRoundData", jsonArray);

            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));

            Log.v("postDoctorRoundApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.babyWiseDoctorRound, json, true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));

                        JSONArray roundId = jsonObject.getJSONArray("id");

                        try {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < roundId.length(); i++) {

                                JSONObject roundIdJSONObject = roundId.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableDoctorRound.tableColumn.serverId.toString(), roundIdJSONObject.getString("id"));
                                mContentValues.put(TableDoctorRound.tableColumn.uuid.toString(), roundIdJSONObject.getString("localId"));
                                mContentValues.put(TableDoctorRound.tableColumn.isDataSynced.toString(), "1");
                                mContentValues.put(TableDoctorRound.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());

                                DatabaseController.insertUpdateData(mContentValues, TableDoctorRound.tableName, "uuid ", roundIdJSONObject.getString("localId"));
                            }

                            DatabaseController.myDataBase.setTransactionSuccessful();

                        } finally {
                            DatabaseController.myDataBase.endTransaction();
                        }
                    } else {

                    }

                    getMedicationDataToUpdate();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

                AppUtils.showToastSort(mActivity, responce);

                getMedicationDataToUpdate();

            }
        });

    }

    //Post Medications saved in local database
    public static void postMedicationsApi() {


        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {

            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            for (int i = 0; i < medicationsList.size(); i++) {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("babyId", medicationsList.get(i).get("babyId"));
                jsonObject.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                jsonObject.put("prescriptionTime",
                        AppUtils.getTimeFromDate(medicationsList.get(i).get("addDate")));
                jsonObject.put("treatmentName", medicationsList.get(i).get("treatmentName"));
                jsonObject.put("quantity", medicationsList.get(i).get("quantity"));
                jsonObject.put("unit", medicationsList.get(i).get("unit"));
                jsonObject.put("comment", medicationsList.get(i).get("comment"));
                jsonObject.put("nurseId", medicationsList.get(i).get("nurseId"));
                jsonObject.put("localId", medicationsList.get(i).get("uuid"));
                jsonObject.put("localDateTime", medicationsList.get(i).get("addDate"));

                jsonArray.put(jsonObject);
            }

            jsonData.put("nursePrescriptionData", jsonArray);

            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));

            Log.v("postMedicationsApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.nurseWisePrescription, json, true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));

                        JSONArray objectJSONArray = jsonObject.getJSONArray("id");

                        try {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < objectJSONArray.length(); i++) {

                                JSONObject jsonArrayJSONObject = objectJSONArray.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableTreatment.tableColumn.serverId.toString(), jsonArrayJSONObject.getString("id"));
                                mContentValues.put(TableTreatment.tableColumn.uuid.toString(), jsonArrayJSONObject.getString("localId"));
                                mContentValues.put(TableTreatment.tableColumn.isDataSynced.toString(), "1");
                                mContentValues.put(TableTreatment.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());

                                DatabaseController.insertUpdateData(mContentValues, TableTreatment.tableName, "uuid ", jsonArrayJSONObject.getString("localId"));
                            }

                            DatabaseController.myDataBase.setTransactionSuccessful();

                        } finally {
                            DatabaseController.myDataBase.endTransaction();
                        }
                    } else {

                    }

                    getInvestigationSampleDataToUpdate();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

                AppUtils.showToastSort(mActivity, responce);

                getInvestigationSampleDataToUpdate();

            }
        });

    }

    //Post Investigation Sample saved in local database
    public static void postInvesSampleApi() {


        JSONObject json = new JSONObject();
        JSONObject jsonObject = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        try {

            jsonObject.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            for (int i = 0; i < investigationSampleList.size(); i++) {
                JSONObject jsonData = new JSONObject();

                jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                jsonData.put("localId", investigationSampleList.get(i).get("uuid"));
                jsonData.put("takenByNurse", investigationSampleList.get(i).get("sampleTakenBy"));
                jsonData.put("sampleDate", investigationSampleList.get(i).get("sampleTakenOn"));
                jsonData.put("sampleComment", investigationSampleList.get(i).get("sampleComment"));
                jsonData.put("sampleImage", investigationSampleList.get(i).get("sampleImage"));

                jsonArray.put(jsonData);
            }

            jsonObject.put("sampleTakenByNurse", jsonArray);

            json.put(AppConstants.projectName, jsonObject);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonObject.toString()));

            Log.v("postInvesSampleApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.sampleTakenByNurse, json, true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));
                    }

                    getInvestigationDataToUpdate();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

                AppUtils.showToastSort(mActivity, responce);

                getInvestigationDataToUpdate();

            }
        });
    }

    //Post Investigation saved in local database
    public static void postInvesApi() {


        JSONObject json = new JSONObject();
        JSONObject jsonObject = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        try {

            jsonObject.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            for (int i = 0; i < investigationList.size(); i++) {
                JSONObject jsonData = new JSONObject();

                jsonData.put("babyId", investigationList.get(i).get("babyId"));
                jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                jsonData.put("investigationName", investigationList.get(i).get("investigationName"));
                jsonData.put("resultImage", investigationList.get(i).get("resultImage"));
                jsonData.put("comment", investigationList.get(i).get("nurseComment"));
                jsonData.put("result", investigationList.get(i).get("result"));
                jsonData.put("nurseId", investigationList.get(i).get("nurseId"));
                jsonData.put("localId", investigationList.get(i).get("uuid"));
                jsonData.put("localDateTime", investigationList.get(i).get("addDate"));

                jsonArray.put(jsonData);
            }

            jsonObject.put("nurseInvestigationData", jsonArray);

            json.put(AppConstants.projectName, jsonObject);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonObject.toString()));

            Log.v("postInvesApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.nurseWiseInvestigation, json, true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));

                        JSONArray objectJSONArray = jsonObject.getJSONArray("id");

                        try {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < objectJSONArray.length(); i++) {

                                JSONObject jsonArrayJSONObject = objectJSONArray.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableInvestigation.tableColumn.serverId.toString(), jsonArrayJSONObject.getString("id"));
                                mContentValues.put(TableInvestigation.tableColumn.uuid.toString(), jsonArrayJSONObject.getString("localId"));
                                mContentValues.put(TableInvestigation.tableColumn.isDataSynced.toString(), "1");
                                mContentValues.put(TableInvestigation.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());
                                mContentValues.put(TableInvestigation.tableColumn.status.toString(), "1");

                                DatabaseController.insertUpdateData(mContentValues, TableInvestigation.tableName,
                                        "uuid ", jsonArrayJSONObject.getString("localId"));
                            }

                            DatabaseController.myDataBase.setTransactionSuccessful();

                        } finally {
                            DatabaseController.myDataBase.endTransaction();
                        }
                    }

                    getVaccinationDataToUpdate();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

                AppUtils.showToastSort(mActivity, responce);

                getVaccinationDataToUpdate();

            }
        });
    }

    //Post Vaccination saved in local database
    public static void postVaccApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonObject = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        try {

            jsonObject.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            for (int i = 0; i < vaccinationsList.size(); i++) {
                JSONObject jsonData = new JSONObject();

                jsonData.put("babyId", vaccinationsList.get(i).get("babyId"));
                jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                jsonData.put("quantity", vaccinationsList.get(i).get("quantity"));
                jsonData.put("vaccinationName", vaccinationsList.get(i).get("vaccName"));
                jsonData.put("date", vaccinationsList.get(i).get("date"));
                jsonData.put("time", AppUtils.convertTimeTo24HoursFormat(vaccinationsList.get(i).get("time")));
                jsonData.put("localId", vaccinationsList.get(i).get("uuid"));
                jsonData.put("localDateTime", vaccinationsList.get(i).get("addDate"));
                jsonData.put("nurseId", vaccinationsList.get(i).get("nurseId"));

                jsonArray.put(jsonData);
            }

            jsonObject.put("vaccinationData", jsonArray);

            json.put(AppConstants.projectName, jsonObject);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonObject.toString()));

            Log.v("postVaccApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.babyVaccination, json, true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));

                        getCounsellingDataToUpdate();

                        JSONArray objectJSONArray = jsonObject.getJSONArray("id");

                        try {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < objectJSONArray.length(); i++) {

                                JSONObject arrayJSONObject = objectJSONArray.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableVaccination.tableColumn.serverId.toString(), arrayJSONObject.getString("id"));
                                mContentValues.put(TableVaccination.tableColumn.uuid.toString(), arrayJSONObject.getString("localId"));
                                mContentValues.put(TableVaccination.tableColumn.isDataSynced.toString(), "1");
                                mContentValues.put(TableVaccination.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());

                                DatabaseController.insertUpdateData(mContentValues, TableVaccination.tableName, "uuid ", arrayJSONObject.getString("localId"));
                            }

                            DatabaseController.myDataBase.setTransactionSuccessful();

                        } finally {
                            DatabaseController.myDataBase.endTransaction();
                        }
                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                    AppUtils.hideDialog();
                }

            }

            @Override
            public void OnFail(String responce) {

                AppUtils.showToastSort(mActivity, responce);
                getCounsellingDataToUpdate();
            }
        });

    }

    //TODO Sync All Comments
    public static void getAllComments() {

        commentList.clear();
        commentList.addAll(DatabaseController.getCommentDataToSync(AppSettings.getString(AppSettings.loungeId)));

        Log.v("kjhgwgj", String.valueOf(commentList.size()));

        if (commentList.size() > 0) {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postCommentApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        } else {
            getMotherDataToUpdate();
        }
    }

    //Post Comments saved in local database
    public static void postCommentApi() {

        JSONObject json = new JSONObject();
        JSONObject json_data = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        try {

            json_data.put("userType", AppSettings.getString(AppSettings.userType));
            if (AppSettings.getString(AppSettings.userType).equals("1")) {
                json_data.put("loungeId", AppSettings.getString(AppSettings.userId));
            } else if (AppSettings.getString(AppSettings.userType).equals("0")) {
                json_data.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            }

            for (int i = 0; i < commentList.size(); i++) {
                JSONObject jsonData = new JSONObject();

                jsonData.put("motherOrBabyId", commentList.get(i).get("motherOrBabyId"));

                if (AppSettings.getString(AppSettings.userType).equals("1")) {
                    jsonData.put("loungeId", AppSettings.getString(AppSettings.userId));
                } else if (AppSettings.getString(AppSettings.userType).equals("0")) {
                    jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                }

                jsonData.put("doctorId", commentList.get(i).get("doctorId"));
                jsonData.put("type", commentList.get(i).get("type"));
                jsonData.put("comment", commentList.get(i).get("doctorComment"));
                jsonData.put("localId", commentList.get(i).get("uuid"));
                jsonData.put("localDateTime", commentList.get(i).get("addDate"));
                jsonData.put("status", commentList.get(i).get("status"));

                jsonArray.put(jsonData);
            }

            json_data.put("commentData", jsonArray);

            json.put(AppConstants.projectName, json_data);
            json.put(AppConstants.md5Data, AppUtils.md5(json_data.toString()));

            Log.v("postCommentApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.postComment, json, false, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        JSONArray idsArrayList = jsonObject.getJSONArray("id");

                        try {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < idsArrayList.length(); i++) {

                                JSONObject arrayListJSONObject = idsArrayList.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableComments.tableColumn.serverId.toString(), arrayListJSONObject.getString("id"));
                                mContentValues.put(TableComments.tableColumn.uuid.toString(), arrayListJSONObject.getString("localId"));
                                mContentValues.put(TableComments.tableColumn.isDataSynced.toString(), "1");
                                mContentValues.put(TableComments.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());

                                DatabaseController.updateEqual(mContentValues, TableComments.tableName, "uuid ", arrayListJSONObject.getString("localId"));

                            }

                            DatabaseController.myDataBase.setTransactionSuccessful();

                        } finally {
                            DatabaseController.myDataBase.endTransaction();
                        }
                    } else {

                    }

                    getMotherDataToUpdate();

                } catch (JSONException e) {
                    e.printStackTrace();

                    AppUtils.hideDialog();
                }

            }

            @Override
            public void OnFail(String responce) {

                AppUtils.showToastSort(mActivity, responce);
                getMotherDataToUpdate();
            }
        });
    }

    //Post Counselling saved in local database
    public static void postCounsellingApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        try {

            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            for (int i = 0; i < babyAdmissionList.size(); i++) {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("babyId", babyAdmissionList.get(i).get("babyId"));
                jsonObject.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                jsonObject.put("kmcPosition", babyAdmissionList.get(i).get("kmcPosition"));
                jsonObject.put("kmcMonitoring", babyAdmissionList.get(i).get("kmcMonitoring"));
                jsonObject.put("kmcNutrition", babyAdmissionList.get(i).get("kmcNutrition"));
                jsonObject.put("kmcRespect", babyAdmissionList.get(i).get("kmcRespect"));
                jsonObject.put("kmcHygiene", babyAdmissionList.get(i).get("kmcHygiene"));
                jsonObject.put("whatisKmc", babyAdmissionList.get(i).get("whatisKmc"));
                jsonObject.put("localId", babyAdmissionList.get(i).get("uuid"));
                jsonObject.put("localDateTime", babyAdmissionList.get(i).get("modifyDate"));

                if (!babyAdmissionList.get(i).get("babyId").isEmpty()) {
                    jsonArray.put(jsonObject);
                }
            }

            jsonData.put("counsellingData", jsonArray);

            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));

            Log.v("postCounsellingApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.babyWiseCounsellingAll, json, false, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        getAllComments();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

                AppUtils.showToastSort(mActivity, responce);
                getAllComments();
            }
        });
    }
//////khushboo pandey//////////

    private static void postCounsellingBypostersApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        try {

            for (int i = 0; i < babyConsullingByPostersList.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("babyId", babyConsullingByPostersList.get(i).get("babyId"));
                jsonObject.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                jsonObject.put("counsellingType", babyConsullingByPostersList.get(i).get("type"));
                jsonObject.put("posterId", babyConsullingByPostersList.get(i).get("posterId"));
                jsonObject.put("duration", babyConsullingByPostersList.get(i).get("ConsumeTime"));
                jsonObject.put("addDate", babyConsullingByPostersList.get(i).get("date"));

                if (!babyConsullingByPostersList.get(i).get("babyId").isEmpty()) {
                    jsonArray.put(jsonObject);
                }
            }
            jsonData.put("counsellingData", jsonArray);
            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.PostCounsellingPosterData, json, false, true, new WebServicesCallback() {
            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {
                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {
                        DatabaseController.delete(TableCounsellingPosters.tableName, null, null);
                        getAllComments();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void OnFail(String responce) {

//                AppUtils.showToastSort(mActivity, responce);
                getAllComments();
            }
        });
    }

    public static void getMotherDataToUpdate() {

        motherUpdateList.clear();
        motherUpdateList.addAll(DatabaseController.getMotherIdToSyncUpdates(AppSettings.getString(AppSettings.loungeId)));

        if (motherUpdateList.size() > 0) {
            if (motherUpdateList.get(0).get("type").equalsIgnoreCase("1")) {
                if (AppUtils.isNetworkAvailable(mActivity)) {
                    doMotherDataUpdateApi();
                } else {
                    AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
                }
            } else if (motherUpdateList.get(0).get("type").equalsIgnoreCase("2")) {
                if (AppUtils.isNetworkAvailable(mActivity)) {
                    doUnknownRegisApi();
                } else {
                    AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
                }
            }
        } else {
            getMotherAssessData();
        }
    }

    private static void getMotherAssessData() {

        mAssessmentList.clear();
        mAssessmentList.addAll(DatabaseController.getMotherAssessmentToSync(AppSettings.getString(AppSettings.loungeId)));

        if (mAssessmentList.size() > 0) {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                doMonitoringApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        } else {
            //     AppUtils.showToastSort(mActivity,mActivity.getString(R.string.alreadySynced));
            AppUtils.hideDialog();
        }
    }

    private static void doMotherDataUpdateApi() {

        WebServices.postApi(mActivity, AppUrls.updateMotherProfile, createJsonForMotherUpdate(), true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        ContentValues contentValues = new ContentValues();

                        contentValues.put(TableMotherRegistration.tableColumn.isDataSynced.toString(), "1");
                        contentValues.put(TableMotherRegistration.tableColumn.uuid.toString(), motherUpdateList.get(0).get("uuid"));

                        DatabaseController.insertUpdateData(contentValues, TableMotherRegistration.tableName,
                                TableMotherRegistration.tableColumn.uuid.toString(), motherUpdateList.get(0).get("uuid"));
                    } else {

                    }

                    getMotherAssessData();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

                AppUtils.showToastSort(mActivity, responce);

                getMotherAssessData();

            }
        });
    }

    private static JSONObject createJsonForMotherUpdate() {

        JSONObject json = new JSONObject();

        JSONObject jsonData = new JSONObject();

        try {

            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("motherId", motherUpdateList.get(0).get("motherId"));
            jsonData.put("motherName", motherUpdateList.get(0).get("motherName"));
            jsonData.put("isMotherAdmitted", motherUpdateList.get(0).get("isMotherAdmitted"));
            jsonData.put("notAdmittedReason", motherUpdateList.get(0).get("reasonForNotAdmitted"));
            //jsonData.put("hospitalRegistrationNumber", motherUpdateList.get(0).get("hospitalRN"));
            jsonData.put("motherMCTSNumber", motherUpdateList.get(0).get("motherMCTSNumber"));
            jsonData.put("motherAadharNumber", motherUpdateList.get(0).get("motherAadharNumber"));
            jsonData.put("motherDob", motherUpdateList.get(0).get("motherDob"));
            jsonData.put("motherAge", motherUpdateList.get(0).get("motherAge"));
            jsonData.put("motherEducation", motherUpdateList.get(0).get("motherEducation"));
            jsonData.put("motherCaste", motherUpdateList.get(0).get("motherCaste"));
            jsonData.put("motherReligion", motherUpdateList.get(0).get("motherReligion"));
            jsonData.put("motherMobileNumber", motherUpdateList.get(0).get("motherMoblieNo"));
            jsonData.put("fatherName", motherUpdateList.get(0).get("fatherName"));
            jsonData.put("fatherAadharNumber", motherUpdateList.get(0).get("fatherAadharNumber"));
            jsonData.put("fatherMobileNumber", motherUpdateList.get(0).get("fatherMoblieNo"));
            jsonData.put("rationCardType", motherUpdateList.get(0).get("rationCardType"));
            jsonData.put("guardianName", motherUpdateList.get(0).get("guardianName"));
            jsonData.put("guardianNumber", motherUpdateList.get(0).get("guardianNumber"));
            jsonData.put("presentResidenceType", motherUpdateList.get(0).get("presentResidenceType"));
            jsonData.put("presentCountry", motherUpdateList.get(0).get("presentCountry"));
            jsonData.put("presentState", motherUpdateList.get(0).get("presentState"));
            jsonData.put("presentAddress", motherUpdateList.get(0).get("presentAddress"));
            jsonData.put("presentVillageName", motherUpdateList.get(0).get("presentVillageName"));
            jsonData.put("presentBlockName", motherUpdateList.get(0).get("presentBlockName"));
            jsonData.put("presentDistrictName", motherUpdateList.get(0).get("presentDistrictName"));
            jsonData.put("presentAddNearByLocation", motherUpdateList.get(0).get("presentAddressNearBy"));
            jsonData.put("presentPinCode", motherUpdateList.get(0).get("presentPinCode"));
            jsonData.put("permanentAddress", motherUpdateList.get(0).get("permanentAddress"));
            jsonData.put("permanentPinCode", motherUpdateList.get(0).get("permanentPinCode"));
            jsonData.put("permanentResidenceType", motherUpdateList.get(0).get("permanentResidenceType"));
            jsonData.put("permanentVillageName", motherUpdateList.get(0).get("permanentVillageName"));
            jsonData.put("permanentBlockName", motherUpdateList.get(0).get("permanentBlockName"));
            jsonData.put("permanentDistrictName", motherUpdateList.get(0).get("permanentDistrictName"));
            jsonData.put("permanentCountry", motherUpdateList.get(0).get("permanentCountry"));
            jsonData.put("permanentState", motherUpdateList.get(0).get("permanentState"));
            jsonData.put("permanentAddNearByLocation", motherUpdateList.get(0).get("permanentAddressNearBy"));
            jsonData.put("staffId", motherUpdateList.get(0).get("staffId"));
            jsonData.put("gravida", motherUpdateList.get(0).get("gravida"));
            jsonData.put("para", motherUpdateList.get(0).get("para"));
            jsonData.put("abortion", motherUpdateList.get(0).get("abortion"));
            jsonData.put("live", motherUpdateList.get(0).get("live"));
            jsonData.put("type", motherUpdateList.get(0).get("type"));
            jsonData.put("multipleBirth", motherUpdateList.get(0).get("multipleBirth"));
            jsonData.put("deliveryDistrict", motherUpdateList.get(0).get("motherDeliveryDistrict"));
            jsonData.put("guardianRelation", motherUpdateList.get(0).get("guardianRelation"));
            jsonData.put("ashaId", motherUpdateList.get(0).get("ashaID"));
            jsonData.put("ashaNumber", motherUpdateList.get(0).get("ashaNumber"));
            jsonData.put("ashaName", motherUpdateList.get(0).get("ashaName"));
            jsonData.put("motherLmpDate", motherUpdateList.get(0).get("motherLmpDate"));
            jsonData.put("facilityId", motherUpdateList.get(0).get("facilityId"));
            jsonData.put("deliveryPlace", motherUpdateList.get(0).get("motherDeliveryPlace"));
            jsonData.put("motherPicture", motherUpdateList.get(0).get("motherPicture"));
            //jsonData.put("motherPicture", "");
            jsonData.put("consanguinity", motherUpdateList.get(0).get("consanguinity"));
            jsonData.put("motherWeight", motherUpdateList.get(0).get("motherWeight"));
            jsonData.put("ageAtMarriage", motherUpdateList.get(0).get("ageOfMarriage"));
            jsonData.put("birthSpacing", motherUpdateList.get(0).get("birthSpacing"));
            jsonData.put("estimatedDateOfDelivery", motherUpdateList.get(0).get("estimatedDateOfDelivery"));
            jsonData.put("localId", motherUpdateList.get(0).get("uuid"));
            jsonData.put("latitude", AppSettings.getString(AppSettings.latitude));
            jsonData.put("longitude", AppSettings.getString(AppSettings.longitude));
            jsonData.put("localDateTime", motherUpdateList.get(0).get("modifyDate"));

            jsonData.put("temporaryFileId", "");

            jsonData.put("sameAddress", motherUpdateList.get(0).get("sameAddress"));

            if (motherUpdateList.get(0).get("sameAddress").equalsIgnoreCase("1")) {
                jsonData.put("permanentResidenceType", motherUpdateList.get(0).get("presentResidenceType"));
                jsonData.put("permanentCountry", motherUpdateList.get(0).get("presentCountry"));
                jsonData.put("permanentState", motherUpdateList.get(0).get("presentState"));
                jsonData.put("permanentAddress", motherUpdateList.get(0).get("presentAddress"));
                jsonData.put("permanentVillageName", motherUpdateList.get(0).get("presentVillageName"));
                jsonData.put("permanentBlockName", motherUpdateList.get(0).get("presentBlockName"));
                jsonData.put("permanentDistrictName", motherUpdateList.get(0).get("presentDistrictName"));
                jsonData.put("permanentPinCode", motherUpdateList.get(0).get("presentPinCode"));
                jsonData.put("permanentAddNearByLocation", motherUpdateList.get(0).get("presentAddressNearBy"));
            }

            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));

            Log.v("doMotherDataUpdateApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private static void parseMotherUpdateJSON(JSONObject response) {

        Log.d("response ", response.toString());

        AppUtils.hideDialog();

        try {
            if (response.has(AppConstants.projectName)) {

                JSONObject resobj = response.getJSONObject(AppConstants.projectName);

                if (resobj.getString("resCode").equals("1")) {

                    ContentValues contentValues = new ContentValues();

                    contentValues.put(TableMotherRegistration.tableColumn.isDataSynced.toString(), "1");
                    contentValues.put(TableMotherRegistration.tableColumn.uuid.toString(), motherUpdateList.get(0).get("uuid"));

                    DatabaseController.insertUpdateData(contentValues, TableMotherRegistration.tableName,
                            TableMotherRegistration.tableColumn.uuid.toString(), motherUpdateList.get(0).get("uuid"));
                } else {
                    AppUtils.showToastSort(mActivity, resobj.getString("resMsg"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getMotherAssessData();

    }

    private static void doMonitoringApi() {

        WebServices.postApi(mActivity, AppUrls.motherMonitoring, createJsonMMoni(), true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        JSONArray idsArrayList = jsonObject.getJSONArray("id");

                        try {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < idsArrayList.length(); i++) {

                                JSONObject listJSONObject = idsArrayList.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableMotherMonitoring.tableColumn.serverId.toString(), listJSONObject.getString("id"));
                                mContentValues.put(TableMotherMonitoring.tableColumn.uuid.toString(), listJSONObject.getString("localId"));
                                mContentValues.put(TableMotherMonitoring.tableColumn.isDataSynced.toString(), "1");
                                mContentValues.put(TableMotherMonitoring.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());

                                DatabaseController.insertUpdateData(mContentValues, TableMotherMonitoring.tableName,
                                        "uuid ", listJSONObject.getString("localId"));

                            }

                            DatabaseController.myDataBase.setTransactionSuccessful();

                        } finally {
                            DatabaseController.myDataBase.endTransaction();
                        }
                    } else {

                    }

                    getMotherAssessData();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

                AppUtils.showToastSort(mActivity, responce);

                getMotherAssessData();

            }
        });

    }

    private static JSONObject createJsonMMoni() {

        JSONObject json = new JSONObject();

        JSONObject json_data = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        try {

            json_data.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            for (int i = 0; i < mAssessmentList.size(); i++) {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("motherId", AppSettings.getString(AppSettings.motherId));
                jsonObject.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                jsonObject.put("motherTemperature", mAssessmentList.get(i).get("motherTemperature"));
                jsonObject.put("temperatureUnit", mAssessmentList.get(i).get("temperatureUnit"));
                jsonObject.put("motherSystolicBP", mAssessmentList.get(i).get("motherSystolicBP"));
                jsonObject.put("motherDiastolicBP", mAssessmentList.get(i).get("motherDiastolicBP"));
                jsonObject.put("motherUterineTone", mAssessmentList.get(i).get("motherUterineTone"));
                jsonObject.put("motherPulse", mAssessmentList.get(i).get("motherPulse"));
                jsonObject.put("motherUrinationAfterDelivery", mAssessmentList.get(i).get("motherUrinationAfterDelivery"));
                jsonObject.put("episitomyCondition", mAssessmentList.get(i).get("episitomyCondition"));
                jsonObject.put("sanitoryPadStatus", mAssessmentList.get(i).get("sanitoryPadStatus"));
                jsonObject.put("isSanitoryPadStink", mAssessmentList.get(i).get("isSanitoryPadStink"));
                jsonObject.put("other", mAssessmentList.get(i).get("other"));
                jsonObject.put("staffId", mAssessmentList.get(i).get("staffId"));
                jsonObject.put("padPicture", mAssessmentList.get(i).get("padPicture"));
                jsonObject.put("admittedSign", mAssessmentList.get(i).get("admittedSign"));
                //jsonObject.put("admittedSign","");
                jsonObject.put("localId", mAssessmentList.get(i).get("uuid"));
                jsonObject.put("padNotChangeReason", mAssessmentList.get(i).get("padNotChangeReason"));
                jsonObject.put("localDateTime", mAssessmentList.get(i).get("addDate"));

                jsonArray.put(jsonObject);
            }

            json_data.put("monitoringData", jsonArray);

            json.put(AppConstants.projectName, json_data);
            json.put(AppConstants.md5Data, AppUtils.md5(json_data.toString()));

            Log.v("doMonitoringApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    private static void doUnknownRegisApi() {

        AppUtils.showRequestDialog(mActivity);

        String url = AppUrls.unknownMotherRegistration;
        Log.v("doUnknownRegisApi-URL", url);

        AndroidNetworking.post(url)
                .addJSONObjectBody(createJsonForUnknownReg())
                .setPriority(Priority.HIGH)
                .addHeaders("package", AppUtils.md5(mActivity.getPackageName()))
                .addHeaders("token", AppSettings.getString(AppSettings.token))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseUnknownRegiJSON(response);
                    }

                    @Override
                    public void onError(ANError error) {
                        AppUtils.hideDialog();
                        // handle error
                        if (error.getErrorCode() != 0) {
                            AppUtils.showToastSort(mActivity, String.valueOf(error.getErrorCode()));
                            Log.d("onError errorCode ", "onError errorCode : " + error.getErrorCode());
                            Log.d("onError errorBody", "onError errorBody : " + error.getErrorBody());
                            Log.d("onError errorDetail", "onError errorDetail : " + error.getErrorDetail());
                        } else {
                            AppUtils.showToastSort(mActivity, String.valueOf(error.getErrorDetail()));
                        }
                    }
                });
    }

    private static JSONObject createJsonForUnknownReg() {

        JSONObject json = new JSONObject();

        JSONObject json_data = new JSONObject();

        try {

            json_data.put("guardianName", motherUpdateList.get(0).get("guardianName"));
            json_data.put("guardianNumber", motherUpdateList.get(0).get("guardianNumber"));
            json_data.put("type", motherUpdateList.get(0).get("type"));
            json_data.put("organisationName", motherUpdateList.get(0).get("organisationName"));
            json_data.put("organisationNumber", motherUpdateList.get(0).get("organisationNumber"));
            json_data.put("organisationAddress", motherUpdateList.get(0).get("organisationAddress"));
            json_data.put("hospitalRegistrationNumber", "");

            json.put(AppConstants.projectName, json_data);
            json.put(AppConstants.md5Data, AppUtils.md5(json_data.toString()));

            Log.v("doUnknownRegisApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    private static void parseUnknownRegiJSON(JSONObject response) {

        Log.d("response ", response.toString());

        AppUtils.hideDialog();

        try {
            if (response.has(AppConstants.projectName)) {

                JSONObject resobj = response.getJSONObject(AppConstants.projectName);

                if (resobj.getString("resCode").equals("1")) {

                    ContentValues contentValues = new ContentValues();

                    contentValues.put(TableMotherRegistration.tableColumn.isDataSynced.toString(), "1");
                    contentValues.put(TableMotherRegistration.tableColumn.uuid.toString(), motherUpdateList.get(0).get("uuid"));

                    DatabaseController.insertUpdateData(contentValues, TableMotherRegistration.tableName,
                            TableMotherRegistration.tableColumn.uuid.toString(), motherUpdateList.get(0).get("uuid"));
                } else {
                    AppUtils.showToastSort(mActivity, resobj.getString("resMsg"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getMotherAssessData();
    }


    public static void saveMotherInfoApi(BaseActivity baseActivity, String motherAdmissionId) {

        mActivity = baseActivity;
        //uuid = uuidNew;

        ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
        arrayList.addAll(DatabaseController.getMotherMonitoringData(motherAdmissionId));

        if (arrayList.size() > 0) {
            AppUtils.showRequestDialog(mActivity);

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(arrayList.get(0).get("json"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = AppUrls.updateMotherData;
            Log.v("saveMotherInfoApi-URL", url);

            AndroidNetworking.post(url)
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.HIGH)
                    .addHeaders("package", AppUtils.md5(mActivity.getPackageName()))
                    .addHeaders("token", AppSettings.getString(AppSettings.token))
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            parseSaveMothInfoJSON(response);
                        }

                        @Override
                        public void onError(ANError error) {
                            AppUtils.hideDialog();
                            Log.d("onError errorCode ", "onError errorCode : " + error.toString());

                            // handle error
                            if (error.getErrorCode() != 0) {
                                AppUtils.showToastSort(mActivity, String.valueOf(error.getErrorCode()));
                                Log.d("onError errorCode ", "onError errorCode : " + error.getErrorCode());
                                Log.d("onError errorBody", "onError errorBody : " + error.getErrorBody());
                                Log.d("onError errorDetail", "onError errorDetail : " + error.getErrorDetail());
                            } else {
                                AppUtils.showToastSort(mActivity, String.valueOf(error.getErrorDetail()));
                            }
                        }
                    });
        }


    }

    public static void parseSaveMothInfoJSON(JSONObject response) {

        Log.d("response ", response.toString());

        AppUtils.hideDialog();

        try {
            if (response.has(AppConstants.projectName)) {

                JSONObject resobj = response.getJSONObject(AppConstants.projectName);

                if (resobj.getString("resCode").equals("1")) {

                    AppUtils.showToastSort(mActivity, resobj.getString("resMsg"));

                    ContentValues contentValues = new ContentValues();

                    contentValues.put(TableMotherPastInformation.tableColumn.status.toString(), "1");
                    contentValues.put(TableMotherPastInformation.tableColumn.motherAdmissionId.toString(), AppSettings.getString(AppSettings.motherAdmissionId));

                    String where = "motherAdmissionId  ='" + AppSettings.getString(AppSettings.motherAdmissionId) + "'";

                    DatabaseController.insertUpdateDataMultiple(contentValues,
                            TableMotherPastInformation.tableName, where);

                } else {
                    AppUtils.showToastSort(mActivity, resobj.getString("resMsg"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

}
