package code.algo;

import android.app.Activity;
import android.content.ContentValues;
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
import code.database.TableMotherMonitoring;
import code.database.TableMotherPastInformation;
import code.database.TableMotherRegistration;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseActivity;

public class SyncMotherRecord {

    static BaseActivity mActivity;
    static String motherId ="";

    static ArrayList<HashMap<String, String>> motherUpdateList = new ArrayList();
    static ArrayList<HashMap<String, String>> mAssessmentList = new ArrayList();

    public static void getMotherDataToUpdate(BaseActivity baseActivity, String motherid) {

        mActivity = baseActivity;
        motherId = motherid;

        motherUpdateList.clear();
        motherUpdateList.addAll(DatabaseController.getMotherIdToSyncUpdates(motherId,
                AppSettings.getString(AppSettings.loungeId)));

        if(motherUpdateList.size()>0)
        {
            if(motherUpdateList.get(0).get("type").equalsIgnoreCase("1"))
            {
                if (AppUtils.isNetworkAvailable(mActivity)) {
                    doMotherDataUpdateApi();
                } else {
                    AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
                }
            }
            else if(motherUpdateList.get(0).get("type").equalsIgnoreCase("2"))
            {
                if (AppUtils.isNetworkAvailable(mActivity)) {
                    doUnknownRegisApi();
                } else {
                    AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
                }
            }
        }
        else
        {
            getMotherAssessData();
        }
    }

    private static void getMotherAssessData() {

        mAssessmentList.clear();
        mAssessmentList.addAll(DatabaseController.getMotherAssessmentToSync(motherId
                , AppSettings.getString(AppSettings.loungeId)));

        if(mAssessmentList.size()>0)
        {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                doMonitoringApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        }
    }

    private static void doMotherDataUpdateApi() {

        WebServices.postApi(mActivity, AppUrls.updateMotherProfile, createJsonForMotherUpdate(),true,true, new WebServicesCallback() {

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
            jsonData.put("motherDOB",motherUpdateList.get(0).get("motherDob"));
            jsonData.put("motherAge",motherUpdateList.get(0).get("motherAge"));
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
            jsonData.put("permanentVillageName",motherUpdateList.get(0).get("permanentVillageName"));
            jsonData.put("permanentBlockName", motherUpdateList.get(0).get("permanentBlockName"));
            jsonData.put("permanentDistrictName", motherUpdateList.get(0).get("permanentDistrictName"));
            jsonData.put("permanentCountry", motherUpdateList.get(0).get("permanentCountry"));
            jsonData.put("permanentState", motherUpdateList.get(0).get("permanentState"));
            jsonData.put("permanentAddNearByLocation", motherUpdateList.get(0).get("permanentAddressNearBy"));
            jsonData.put("staffId", motherUpdateList.get(0).get("staffId"));
            jsonData.put("gravida", motherUpdateList.get(0).get("gravida"));
            jsonData.put("para", motherUpdateList.get(0).get("para"));
            jsonData.put("abortion",motherUpdateList.get(0).get("abortion"));
            jsonData.put("live",motherUpdateList.get(0).get("live"));
            jsonData.put("type",motherUpdateList.get(0).get("type"));
            jsonData.put("multipleBirth",motherUpdateList.get(0).get("multipleBirth"));
            jsonData.put("deliveryDistrict",motherUpdateList.get(0).get("motherDeliveryDistrict"));
            jsonData.put("guardianRelation",motherUpdateList.get(0).get("guardianRelation"));
            jsonData.put("ashaId",motherUpdateList.get(0).get("ashaID"));
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

            jsonData.put( "sameAddress",motherUpdateList.get(0).get("sameAddress"));

            if(motherUpdateList.get(0).get("sameAddress").equalsIgnoreCase("1"))
            {
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

    private static void parseMotherUpdateJSON(JSONObject response){

        Log.d("response ", response.toString());

        AppUtils.hideDialog();

        try {
            if (response.has(AppConstants.projectName)) {

                JSONObject resobj= response.getJSONObject(AppConstants.projectName);

                if (resobj.getString("resCode").equals("1")) {

                    ContentValues contentValues = new ContentValues();

                    contentValues.put(TableMotherRegistration.tableColumn.isDataSynced.toString(), "1");
                    contentValues.put(TableMotherRegistration.tableColumn.uuid.toString(), motherUpdateList.get(0).get("uuid"));

                    DatabaseController.insertUpdateData(contentValues, TableMotherRegistration.tableName,
                            TableMotherRegistration.tableColumn.uuid.toString(), motherUpdateList.get(0).get("uuid"));
                }
                else {
                    AppUtils.showToastSort(mActivity, resobj.getString("resMsg"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getMotherAssessData();

    }

    private static void doMonitoringApi() {

        WebServices.postApi(mActivity, AppUrls.motherMonitoring, createJsonMMoni(),true,true, new WebServicesCallback() {

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

                                JSONObject listJSONObject = idsArrayList.getJSONObject(i);

                                ContentValues mContentValues = new ContentValues();
                                mContentValues.put(TableMotherMonitoring.tableColumn.serverId.toString(), listJSONObject.getString("id"));
                                mContentValues.put(TableMotherMonitoring.tableColumn.uuid.toString(), listJSONObject.getString("localId"));
                                mContentValues.put(TableMotherMonitoring.tableColumn.isDataSynced.toString(),"1");
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

            for(int i=0;i<mAssessmentList.size();i++)
            {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("motherId", AppSettings.getString(AppSettings.motherId));
                jsonObject.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                jsonObject.put("motherTemperature", mAssessmentList.get(i).get("motherTemperature"));
                jsonObject.put("temperatureUnit" , mAssessmentList.get(i).get("temperatureUnit"));
                jsonObject.put("motherSystolicBP",mAssessmentList.get(i).get("motherSystolicBP"));
                jsonObject.put("motherDiastolicBP",mAssessmentList.get(i).get("motherDiastolicBP"));
                jsonObject.put("motherUterineTone",mAssessmentList.get(i).get("motherUterineTone"));
                jsonObject.put("motherPulse",mAssessmentList.get(i).get("motherPulse"));
                jsonObject.put("motherUrinationAfterDelivery",mAssessmentList.get(i).get("motherUrinationAfterDelivery"));
                jsonObject.put("episitomyCondition",mAssessmentList.get(i).get("episitomyCondition"));
                jsonObject.put("sanitoryPadStatus",mAssessmentList.get(i).get("sanitoryPadStatus"));
                jsonObject.put("isSanitoryPadStink",mAssessmentList.get(i).get("isSanitoryPadStink"));
                jsonObject.put("other",mAssessmentList.get(i).get("other"));
                jsonObject.put("staffId",mAssessmentList.get(i).get("staffId"));
                jsonObject.put("padPicture",mAssessmentList.get(i).get("padPicture"));
                jsonObject.put("admittedSign",mAssessmentList.get(i).get("admittedSign"));
                //jsonObject.put("admittedSign","");
                jsonObject.put("localId",mAssessmentList.get(i).get("uuid"));
                jsonObject.put("padNotChangeReason",mAssessmentList.get(i).get("padNotChangeReason"));
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
                            AppUtils.showToastSort(mActivity,String.valueOf(error.getErrorCode()));
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

            json_data.put( "guardianName", motherUpdateList.get(0).get("guardianName"));
            json_data.put( "guardianNumber",motherUpdateList.get(0).get("guardianNumber"));
            json_data.put( "type",motherUpdateList.get(0).get("type"));
            json_data.put( "organisationName",motherUpdateList.get(0).get("organisationName"));
            json_data.put( "organisationNumber",motherUpdateList.get(0).get("organisationNumber"));
            json_data.put( "organisationAddress",motherUpdateList.get(0).get("organisationAddress"));
            json_data.put( "hospitalRegistrationNumber","");

            json.put(AppConstants.projectName, json_data);
            json.put(AppConstants.md5Data, AppUtils.md5(json_data.toString()));

            Log.v("doUnknownRegisApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    private static void parseUnknownRegiJSON(JSONObject response){

        Log.d("response ", response.toString());

        AppUtils.hideDialog();

        try {
            if (response.has(AppConstants.projectName)) {

                JSONObject resobj= response.getJSONObject(AppConstants.projectName);

                if (resobj.getString("resCode").equals("1")) {

                    ContentValues contentValues = new ContentValues();

                    contentValues.put(TableMotherRegistration.tableColumn.isDataSynced.toString(), "1");
                    contentValues.put(TableMotherRegistration.tableColumn.uuid.toString(), motherUpdateList.get(0).get("uuid"));

                    DatabaseController.insertUpdateData(contentValues, TableMotherRegistration.tableName,
                            TableMotherRegistration.tableColumn.uuid.toString(), motherUpdateList.get(0).get("uuid"));
                }
                else {
                    AppUtils.showToastSort(mActivity,resobj.getString("resMsg"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getMotherAssessData();
    }



    public static void saveMotherInfoApi(BaseActivity baseActivity,String motherAdmissionId) {

        mActivity = baseActivity;
        //uuid = uuidNew;

        ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
        arrayList.addAll(DatabaseController.getMotherMonitoringData(motherAdmissionId));

        if(arrayList.size()>0)
        {
            AppUtils.showRequestDialog(mActivity);

            JSONObject  jsonObject = null;
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
                                AppUtils.showToastSort(mActivity,String.valueOf(error.getErrorCode()));
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

    public static void parseSaveMothInfoJSON(JSONObject response){

        Log.d("response ", response.toString());

        AppUtils.hideDialog();

        try {
            if (response.has(AppConstants.projectName)) {

                JSONObject resobj= response.getJSONObject(AppConstants.projectName);

                if (resobj.getString("resCode").equals("1")) {

                    AppUtils.showToastSort(mActivity, resobj.getString("resMsg"));

                    ContentValues contentValues = new ContentValues();

                    contentValues.put(TableMotherPastInformation.tableColumn.status.toString(), "1");
                    contentValues.put(TableMotherPastInformation.tableColumn.motherAdmissionId.toString(), AppSettings.getString(AppSettings.motherAdmissionId));

                    String where = "motherAdmissionId  ='"+ AppSettings.getString(AppSettings.motherAdmissionId) +"'";

                    DatabaseController.insertUpdateDataMultiple(contentValues,
                            TableMotherPastInformation.tableName, where);

                }
                else {
                    AppUtils.showToastSort(mActivity, resobj.getString("resMsg"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }
}
