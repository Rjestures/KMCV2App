package code.algo;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Bitmap;
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
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableBabyMonitoring;
import code.database.TableBabyRegistration;
import code.database.TableBreastFeeding;
import code.database.TableComments;
import code.database.TableDoctorRound;
import code.database.TableDutyChange;
import code.database.TableInvestigation;
import code.database.TableKMC;
import code.database.TableTreatment;
import code.database.TableVaccination;
import code.database.TableWeight;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseActivity;

public class SyncBabyRecord {

    static BaseActivity mActivity;
    static String babyId="";
    static String uuid="";

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

    public static void getBabyDataToUpdate(BaseActivity baseActivity, String babyid) {

        mActivity = baseActivity;
        babyId = babyid;
        uuid = DatabaseController.getUUID(babyid);

        babyUpdateList.clear();
        babyUpdateList.addAll(DatabaseController.getBabyIdToSyncUpdates(babyId,
                AppSettings.getString(AppSettings.loungeId)));

        if(babyUpdateList.size()>0)
        {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                doBabyUpdationApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        }
        else
        {
            getBabyWeightDataToUpdate();
        }
    }

    public static void getBabyWeightDataToUpdate() {

        weightList.clear();
        weightList.addAll(DatabaseController.getWeightToSync(babyId, AppSettings.getString(AppSettings.loungeId)));

        if(weightList.size()>0)
        {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postDailyWeightApi() ;
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        }
        else
        {
            getBabyAssessDataToUpdate();
        }
    }

    public static void getBabyAssessDataToUpdate() {

        assessmentList.clear();
        assessmentList.addAll(DatabaseController.getBabyAssessToSync(babyId, AppSettings.getString(AppSettings.loungeId)));

        if(assessmentList.size()>0)
        {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postAssessmentApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        }
        else
        {
            getBabyKMCDataToUpdate();
        }
    }

    public static void getBabyKMCDataToUpdate() {

        sscList.clear();
        sscList.addAll(DatabaseController.getSSCDateToSync(babyId, AppSettings.getString(AppSettings.loungeId)));

        if(sscList.size()>0)
        {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postSSCApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        }
        else
        {
            getBabyFeedingDataToUpdate();
        }
    }

    public static void getBabyFeedingDataToUpdate() {

        feedingList.clear();
        feedingList.addAll(DatabaseController.getFeedingDataToSync(babyId, AppSettings.getString(AppSettings.loungeId)));

        if(feedingList.size()>0)
        {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postFeedingApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        }
        else
        {
            getDoctorRound();
        }
    }

    public static void getDoctorRound() {

        doctorRound.clear();
        doctorRound.addAll(DatabaseController.getDoctorRoundDataToSync(babyId, AppSettings.getString(AppSettings.loungeId)));

        if(doctorRound.size()>0)
        {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postDoctorRoundApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        }
        else
        {
            getMedicationDataToUpdate();
        }
    }

    public static void getMedicationDataToUpdate() {

        medicationsList.clear();
        medicationsList.addAll(DatabaseController.getMedicationDataToSync(babyId, AppSettings.getString(AppSettings.loungeId)));

        if(medicationsList.size()>0)
        {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postMedicationsApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        }
        else
        {
            getInvestigationSampleDataToUpdate();
        }
    }

    public static void getInvestigationSampleDataToUpdate() {

        investigationSampleList.clear();
        investigationSampleList.addAll(DatabaseController.getInvestSampleDataToSync(babyId, AppSettings.getString(AppSettings.loungeId)));

        if(investigationSampleList.size()>0)
        {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postInvesSampleApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        }
        else
        {
            getInvestigationDataToUpdate();
        }
    }

    public static void getInvestigationDataToUpdate() {

        investigationList.clear();
        investigationList.addAll(DatabaseController.getInvestDataToSync(babyId, AppSettings.getString(AppSettings.loungeId)));

        if(investigationList.size()>0)
        {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postInvesApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        }
        else
        {
            getVaccinationDataToUpdate();
        }
    }

    public static void getVaccinationDataToUpdate() {

        vaccinationsList.clear();
        vaccinationsList.addAll(DatabaseController.getVaccinationDataToSync(babyId, AppSettings.getString(AppSettings.loungeId)));

        if(vaccinationsList.size()>0)
        {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postVaccApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        }
    }

    public static void getCounsellingDataToUpdate(BaseActivity baseActivity, String babyid,String type) {

        mActivity = baseActivity;
        babyId = babyid;

        babyAdmissionList.clear();
        babyAdmissionList.addAll(DatabaseController.getBabyCounsellingUpdates(babyId));

        if(babyAdmissionList.size()>0)
        {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postCounsellingApi(type);
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        }
    }

    public static void doBabyUpdationApi() {

        WebServices.postApi(mActivity, AppUrls.updateBabyProfile, createJsonForBabyUpdation(),true,true, new WebServicesCallback() {

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

                        DatabaseController.insertUpdateData(contentValues, TableBabyRegistration.tableName,
                                TableBabyRegistration.tableColumn.uuid.toString(), babyUpdateList.get(0).get("uuid"));
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

            jsonData.put( "loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put( "babyId", AppSettings.getString(AppSettings.babyId));
            jsonData.put( "babyMCTSNumber", babyUpdateList.get(0).get("babyMCTSNumber"));
            jsonData.put( "babyFileId", babyUpdateList.get(0).get("babyFileId"));
            jsonData.put( "deliveryDate",babyUpdateList.get(0).get("deliveryDate"));
            jsonData.put( "deliveryTime", AppUtils.convertTimeTo24HoursFormat(babyUpdateList.get(0).get("deliveryTime")));
            jsonData.put( "babyGender",babyUpdateList.get(0).get("babyGender"));
            jsonData.put( "deliveryType",babyUpdateList.get(0).get("deliveryType"));
            jsonData.put( "babyWeight",babyUpdateList.get(0).get("babyWeight"));
            jsonData.put( "babyCryAfterBirth",babyUpdateList.get(0).get("babyCryAfterBirth"));
            jsonData.put( "babyNeedBreathingHelp",babyUpdateList.get(0).get("babyNeedBreathingHelp"));
            jsonData.put( "babyPhoto",babyUpdateList.get(0).get("babyPhoto"));
            jsonData.put( "birthWeightAvail",babyUpdateList.get(0).get("birthWeightAvail"));
            jsonData.put( "reason",babyUpdateList.get(0).get("reason"));
            jsonData.put( "firstTimeFeed",babyUpdateList.get(0).get("firstTimeFeed"));
            jsonData.put( "typeOfBorn",babyUpdateList.get(0).get("typeOfBorn"));
            jsonData.put( "typeOfOutBorn",babyUpdateList.get(0).get("typeOfOutBorn"));
            jsonData.put( "vitaminKGiven",babyUpdateList.get(0).get("vitaminKGiven"));
            jsonData.put( "wasApgarScoreRecorded",babyUpdateList.get(0).get("wasApgarScoreRecorded"));
            jsonData.put( "apgarScoreVal","");

            if(babyUpdateList.get(0).get("wasApgarScoreRecorded").
                    equalsIgnoreCase(mActivity.getString(R.string.yesValue)))
            {
                jsonData.put( "apgarScoreVal",babyUpdateList.get(0).get("apgarScore"));
            }

            jsonData.put( "babyPhoto",babyUpdateList.get(0).get("babyPhoto"));
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

            for(int i=0;i<weightList.size();i++)
            {
                JSONObject jsonWeight = new JSONObject();

                jsonWeight.put("babyId", weightList.get(i).get("babyId"));
                jsonWeight.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                jsonWeight.put("babyWeight",weightList.get(i).get("babyWeight"));
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

        WebServices.postApi(mActivity, AppUrls.submitBabyWeight, json,true,true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

//                        AppUtils.showToastSort(mActivity,jsonObject.getString("resMsg"));
                        AppUtils.showToastSort(mActivity, mActivity.getString(R.string.dataSaved));

                        JSONArray idsArrayList = jsonObject.getJSONArray("id");

                        try
                        {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < idsArrayList.length(); i++) {

                                JSONObject arrayListJSONObject = idsArrayList.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableWeight.tableColumn.serverId.toString(), arrayListJSONObject.getString("id"));
                                mContentValues.put(TableWeight.tableColumn.uuid.toString(), arrayListJSONObject.getString("localId"));
                                mContentValues.put(TableWeight.tableColumn.isDataSynced.toString(),"1");
                                mContentValues.put(TableWeight.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());

                                DatabaseController.insertUpdateData(mContentValues, TableWeight.tableName,"uuid ", arrayListJSONObject.getString("localId"));

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

            for(int i=0;i<assessmentList.size();i++)
            {
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

        WebServices.postApi(mActivity, AppUrls.babyMonitoring, json,true,true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

//                        AppUtils.showToastSort(mActivity,jsonObject.getString("resMsg"));

                        JSONArray idsArrayList = jsonObject.getJSONArray("id");

                        try
                        {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < idsArrayList.length(); i++) {

                                JSONObject arrayListJSONObject = idsArrayList.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableBabyMonitoring.tableColumn.serverId.toString(), arrayListJSONObject.getString("id"));
                                mContentValues.put(TableBabyMonitoring.tableColumn.uuid.toString(), arrayListJSONObject.getString("localId"));
                                mContentValues.put(TableBabyMonitoring.tableColumn.isDataSynced.toString(),"1");
                                mContentValues.put(TableBabyMonitoring.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());

                                DatabaseController.insertUpdateData(mContentValues, TableBabyMonitoring.tableName,"uuid ", arrayListJSONObject.getString("localId"));

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

            for(int i=0;i<sscList.size();i++)
            {
                JSONObject jsonSsc = new JSONObject();

                jsonSsc.put("babyId", babyId);
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

        WebServices.postApi(mActivity, AppUrls.skinToSkinTouch, json,true,true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        AppUtils.showToastSort(mActivity,jsonObject.getString("resMsg"));

                        JSONArray feedId = jsonObject.getJSONArray("id");

                        try
                        {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < feedId.length(); i++) {

                                JSONObject jsonObjectId = feedId.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableKMC.tableColumn.serverId.toString(), jsonObjectId.getString("id"));
                                mContentValues.put(TableKMC.tableColumn.uuid.toString(), jsonObjectId.getString("localId"));
                                mContentValues.put(TableKMC.tableColumn.isDataSynced.toString(),"1");
                                mContentValues.put(TableKMC.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());

                                DatabaseController.insertUpdateData(mContentValues, TableKMC.tableName,"uuid ", jsonObjectId.getString("localId"));

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
                jsonFeeding.put("fluid", feedingList.get(i).get("fluid"));
                jsonFeeding.put("specify", feedingList.get(i).get("specify"));
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

                        AppUtils.showToastSort(mActivity, mActivity.getString(R.string.dataSaved));

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

            for(int i = 0; i< doctorRound.size(); i++)
            {
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

                        AppUtils.showToastSort(mActivity, mActivity.getString(R.string.dataSaved));

                        JSONArray roundId = jsonObject.getJSONArray("id");

                        try
                        {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < roundId.length(); i++) {

                                JSONObject roundIdJSONObject = roundId.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableDoctorRound.tableColumn.serverId.toString(), roundIdJSONObject.getString("id"));
                                mContentValues.put(TableDoctorRound.tableColumn.uuid.toString(), roundIdJSONObject.getString("localId"));
                                mContentValues.put(TableDoctorRound.tableColumn.isDataSynced.toString(),"1");
                                mContentValues.put(TableDoctorRound.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());

                                DatabaseController.insertUpdateData(mContentValues, TableDoctorRound.tableName,"uuid ", roundIdJSONObject.getString("localId"));
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

            for(int i=0;i<medicationsList.size();i++)
            {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("babyId", babyId);
                jsonObject.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                jsonObject.put("prescriptionTime",
                        AppUtils.getTimeFromDate(medicationsList.get(i).get("addDate")));
                jsonObject.put("treatmentName",medicationsList.get(i).get("treatmentName"));
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

                        AppUtils.showToastSort(mActivity, mActivity.getString(R.string.dataSaved));

                        JSONArray objectJSONArray = jsonObject.getJSONArray("id");

                        try
                        {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < objectJSONArray.length(); i++) {

                                JSONObject jsonArrayJSONObject = objectJSONArray.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableTreatment.tableColumn.serverId.toString(), jsonArrayJSONObject.getString("id"));
                                mContentValues.put(TableTreatment.tableColumn.uuid.toString(), jsonArrayJSONObject.getString("localId"));
                                mContentValues.put(TableTreatment.tableColumn.isDataSynced.toString(),"1");
                                mContentValues.put(TableTreatment.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());

                                DatabaseController.insertUpdateData(mContentValues, TableTreatment.tableName,"uuid ", jsonArrayJSONObject.getString("localId"));
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

            for(int i=0;i<investigationSampleList.size();i++)
            {
                JSONObject jsonData = new JSONObject();

                jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                jsonData.put("localId", investigationSampleList.get(i).get("uuid"));
                jsonData.put("takenByNurse",investigationSampleList.get(i).get("sampleTakenBy"));
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

//                        AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));
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

            for(int i=0;i<investigationList.size();i++)
            {
                JSONObject jsonData = new JSONObject();

                jsonData.put("babyId", babyId);
                jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                jsonData.put("investigationName",investigationList.get(i).get("investigationName"));
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

                        AppUtils.showToastSort(mActivity, mActivity.getString(R.string.dataSaved));

                        JSONArray objectJSONArray = jsonObject.getJSONArray("id");

                        try
                        {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < objectJSONArray.length(); i++) {

                                JSONObject jsonArrayJSONObject = objectJSONArray.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableInvestigation.tableColumn.serverId.toString(), jsonArrayJSONObject.getString("id"));
                                mContentValues.put(TableInvestigation.tableColumn.uuid.toString(), jsonArrayJSONObject.getString("localId"));
                                mContentValues.put(TableInvestigation.tableColumn.isDataSynced.toString(),"1");
                                mContentValues.put(TableInvestigation.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());
                                mContentValues.put(TableInvestigation.tableColumn.status.toString(),  "1");

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

            for(int i=0;i<vaccinationsList.size();i++)
            {
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

                        AppUtils.showToastSort(mActivity, mActivity.getString(R.string.dataSaved));

                        JSONArray objectJSONArray = jsonObject.getJSONArray("id");

                        try
                        {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < objectJSONArray.length(); i++) {

                                JSONObject arrayJSONObject = objectJSONArray.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableVaccination.tableColumn.serverId.toString(), arrayJSONObject.getString("id"));
                                mContentValues.put(TableVaccination.tableColumn.uuid.toString(), arrayJSONObject.getString("localId"));
                                mContentValues.put(TableVaccination.tableColumn.isDataSynced.toString(),"1");
                                mContentValues.put(TableVaccination.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());

                                DatabaseController.insertUpdateData(mContentValues, TableVaccination.tableName,"uuid ", arrayJSONObject.getString("localId"));
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

            }
        });

    }

    //TODO Sync All Comments
    public static void getAllComments(BaseActivity baseActivity, String babyid) {

        mActivity = baseActivity;
        babyId = babyid;

        commentList.clear();
        commentList.addAll(DatabaseController.getCommentDataToSync(AppSettings.getString(AppSettings.loungeId)));


        if(commentList.size()>0)
        {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                postCommentApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        }
    }

    //Post Comments saved in local database
    public static void postCommentApi() {

        JSONObject json = new JSONObject();
        JSONObject json_data = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        try {

            json_data.put("userType", AppSettings.getString(AppSettings.userType));
            if(AppSettings.getString(AppSettings.userType).equals("1"))
            {
                json_data.put("loungeId", AppSettings.getString(AppSettings.userId));
            }
            else  if(AppSettings.getString(AppSettings.userType).equals("0"))
            {
                json_data.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            }

            for(int i=0;i<commentList.size();i++)
            {
                JSONObject jsonData = new JSONObject();

                jsonData.put("motherOrBabyId", commentList.get(i).get("motherOrBabyId"));

                if(AppSettings.getString(AppSettings.userType).equals("1"))
                {
                    jsonData.put("loungeId", AppSettings.getString(AppSettings.userId));
                }
                else  if(AppSettings.getString(AppSettings.userType).equals("0"))
                {
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

                        try
                        {
                            DatabaseController.myDataBase.beginTransaction();

                            for (int i = 0; i < idsArrayList.length(); i++) {

                                JSONObject arrayListJSONObject = idsArrayList.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableComments.tableColumn.serverId.toString(), arrayListJSONObject.getString("id"));
                                mContentValues.put(TableComments.tableColumn.uuid.toString(), arrayListJSONObject.getString("localId"));
                                mContentValues.put(TableComments.tableColumn.isDataSynced.toString(),"1");
                                mContentValues.put(TableComments.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());

                                DatabaseController.updateEqual(mContentValues, TableComments.tableName,"uuid ", arrayListJSONObject.getString("localId"));

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

            }
        });
    }

    //Post Counselling saved in local database
    public static void postCounsellingApi(String type) {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        try {

            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            for(int i=0;i<babyAdmissionList.size();i++)
            {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("babyId", babyAdmissionList.get(i).get("babyId"));
                jsonObject.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                jsonObject.put("type",type);
                jsonObject.put("localId", babyAdmissionList.get(i).get("uuid"));
                jsonObject.put("localDateTime", babyAdmissionList.get(i).get("modifyDate"));


                if(type.equals("1")) {
                    jsonObject.put("value", babyAdmissionList.get(i).get("whatisKmc"));
                }

                if(type.equals("2")) {
                    jsonObject.put("value", babyAdmissionList.get(i).get("kmcPosition"));
                }

                if(type.equals("3")) {
                    jsonObject.put("value", babyAdmissionList.get(i).get("kmcNutrition"));
                }

                if(type.equals("4")) {
                    jsonObject.put("value", babyAdmissionList.get(i).get("kmcHygiene"));
                }

                else  if(type.equals("5")) {
                    jsonObject.put("value", babyAdmissionList.get(i).get("kmcMonitoring"));
                }

                else  if(type.equals("6")) {
                    jsonObject.put("value", babyAdmissionList.get(i).get("kmcRespect"));
                }


                jsonArray.put(jsonObject);
            }

            jsonData.put("counsellingData", jsonArray);

            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));

            Log.v("postCounsellingApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.babyWiseCounselling, json,false,true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

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

}
