package code.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.kmcapp.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import code.utils.AppConstants;
import code.utils.AppUtils;
import code.view.BaseActivity;

import static code.infantsFragment.InfantDetailFragment.kmcPostion;
import static code.utils.AppUtils.parseDateToDMYFormat;

public class DatabaseController {
    public static SQLiteDatabase myDataBase;
    private static DataBaseHelper myDataBaseHelper;

    public static void openDataBase(Context mContext) {
        if (myDataBaseHelper == null) {
            myDataBaseHelper = new DataBaseHelper(mContext);
        }
        if (myDataBase == null) {
            myDataBase = myDataBaseHelper.getWritableDatabase();
        }
    }

    public static void removeTable(String tableName,String where) {
        myDataBase.delete(tableName, where, null);
    }

    public static long insertData(ContentValues values, String tableName) {
        long id = -1;
        try {
            id = myDataBase.insert(tableName, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        AppUtils.print("====insertData " + id);
        return id;
    }

    public static long insertUpdateData(ContentValues values, String tableName, String columnName, String uniqueId) {
        try {


            if (checkRecordExist(tableName, columnName, uniqueId)) {
                Log.d("update-data",values.toString());
                Log.d("unique-id",uniqueId);
                return (long) myDataBase.update(tableName, values, columnName + "='" + uniqueId + "'", null);
            }
            Log.d("insert-data",values.toString());
            Log.d("unique-id",uniqueId);
            return myDataBase.insert(tableName, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }

    public static void updateEqual(ContentValues mContentValues, String tableName, String columnName, String columnValue) {
        myDataBase.update(tableName, mContentValues, columnName + " = '" + columnValue + "'", null);
    }

    public static void updateNotEqual(ContentValues mContentValues, String tableName, String columnName, String columnValue) {
        myDataBase.update(tableName, mContentValues, columnName + " != '" + columnValue + "'", null);
    }

    public static long insertUpdateNotData(ContentValues values, String tableName, String columnName, String uniqueId) {
        try {
            if (checkRecordExist(tableName, columnName, uniqueId)) {
                return (long) myDataBase.update(tableName, values, columnName + "!='" + uniqueId + "'", null);
            }
            return myDataBase.insert(tableName, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static long insertUpdateDataMultiple(ContentValues values, String tableName, String where) {
        try {
            if (checkRecordExistMultiple(tableName, where)) {
                return (long) myDataBase.update(tableName, values, where, null);
            }
            return myDataBase.insert(tableName, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean checkRecordExist(String tableName, String columnName, String uniqueId) {
        boolean status = false;
        Cursor cursor = myDataBase.query(tableName, null, columnName + "= '" + uniqueId + "'", null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                status = true;
            }
            cursor.close();
        }
        return status;
    }

    public static boolean checkRecordExistWhere(String tableName, String where) {
        boolean status = false;
        Cursor cursor = myDataBase.query(tableName, null, where, null, null, null, null);
        AppUtils.print("isDataExit-checkRecordExistWhere" + cursor.getCount());
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                status = true;
            }
            cursor.close();
        }
        return status;
    }

    public static boolean checkRecordExistMultiple(String tableName, String where) {
        boolean status = false;
        Cursor cursor = myDataBase.query(tableName, null, where, null, null, null, null);
        AppUtils.print("isDataExit-SubCategory" + cursor.getCount());
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                status = true;
            }
            cursor.close();
        }
        return status;
    }

    public static long isDataExit(String tableName) {
        long cnt = DatabaseUtils.queryNumEntries(myDataBase, tableName);
        AppUtils.print("isDataExit " + cnt);
        Log.d("isDataExit", String.valueOf(cnt));
        return cnt;
    }

    public static boolean deleteRow(String tableName, String keyName, String keyValue) {
        try {
            return myDataBase.delete(tableName, keyName + "= '" + keyValue+"'", null) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public static int delete(String tableName, String where, String[] args) {
        return myDataBase.delete(tableName, where, args);
    }

    public static ArrayList<String> getLoungeName(String facilityId) {

        ArrayList<String> List = new ArrayList();
        String query = "";

        if(facilityId.isEmpty()) {
            query = "SELECT * from "+ TableLounge.tableName;
        }
        else
        {
            query = "SELECT * from "+ TableLounge.tableName + " where " + TableLounge.tableColumn.facilityId + " = '" + facilityId +"'";
        }

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.query", String.valueOf(query));

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                List.add(cur.getString(cur.getColumnIndex(TableLounge.tableColumn.loungeName.toString())));
                cur.moveToNext();
            }
        }
        Log.d("cur.getLoungeName()", String.valueOf(List));
        cur.close();
        return List;
    }

    public static ArrayList<String> getLoungeId(String facilityId) {

        ArrayList<String> List = new ArrayList();

        String query = "";

        if(facilityId.isEmpty())
        {
            query = "SELECT * from "+ TableLounge.tableName;
        }
        else
        {
            query = "SELECT * from "+ TableLounge.tableName + " where " + TableLounge.tableColumn.facilityId + " = '" + facilityId+"'";
        }

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                List.add(cur.getString(cur.getColumnIndex(TableLounge.tableColumn.loungeId.toString())));
                cur.moveToNext();
            }
        }
        cur.close();
        return List;
    }

    public static String getLoungeNameData(String loungeId) {

        String loungeName="";

        String query = "SELECT loungeName from "+ TableLounge.tableName + " where " + TableLounge.tableColumn.loungeId + " = '" + loungeId +"'";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query", query);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                loungeName = cur.getString(cur.getColumnIndex(TableLounge.tableColumn.loungeName.toString()));

                cur.moveToNext();
            }
        }

        cur.close();

        return loungeName;
    }

    public static String getLoungeTypeData(String loungeId) {
        String loungeType="";
        Cursor cur = myDataBase.rawQuery("SELECT type from "+ TableLounge.tableName + " where " + TableLounge.tableColumn.loungeId + " = '" + loungeId +"'", null);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                loungeType = cur.getString(cur.getColumnIndex(TableLounge.tableColumn.type.toString()));
                cur.moveToNext();
            }
        }
        cur.close();
        return loungeType;
    }

    public static ArrayList<String> getDistrictNameData(String type) {
        ArrayList<String> List = new ArrayList();
        List.clear();

        String query = "";

        if (!type.isEmpty())
        {
            query = "SELECT districtName from "+ TableDistrict.tableName + " where " + TableDistrict.tableColumn.urbanRural + " = '" + type + "' order by "+ TableDistrict.tableColumn.districtName +" asc";
        }
        else
        {
            query = "SELECT DISTINCT districtName from "+ TableDistrict.tableName +  " order by "+ TableDistrict.tableColumn.districtName +" asc";
        }

        //query = "SELECT * from "+ TableDistrict.tableName+  " order by "+ TableDistrict.tableColumn.districtName +" asc";

        Log.d("str", query);

        Cursor cur = myDataBase.rawQuery(query, null);

        //Cursor cur = myDataBase.rawQuery("SELECT * from "+TableDistrict.tableName, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                List.add(cur.getString(cur.getColumnIndex(TableDistrict.tableColumn.districtName.toString())));
                cur.moveToNext();
            }
        }
        cur.close();
        return List;
    }

    public static ArrayList<String> getDistrictIdData(String type) {
        ArrayList<String> List = new ArrayList();
        List.clear();

        String query = "";

        if (!type.isEmpty())
        {
            query = "SELECT districtId from "+ TableDistrict.tableName + " where " + TableDistrict.tableColumn.urbanRural + " = '" + type + "' order by "+ TableDistrict.tableColumn.districtName +" asc";
        }
        else
        {
            query = "SELECT DISTINCT districtId from "+ TableDistrict.tableName +  " order by "+ TableDistrict.tableColumn.districtName +" asc";
        }

        //query = "SELECT * from "+ TableDistrict.tableName+  " order by "+ TableDistrict.tableColumn.districtName +" asc";

        Cursor cur = myDataBase.rawQuery(query, null);
        Log.d("str", query);
        //Cursor cur = myDataBase.rawQuery("SELECT * from "+TableDistrict.tableName, null);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                List.add(cur.getString(cur.getColumnIndex(TableDistrict.tableColumn.districtId.toString())));
                cur.moveToNext();
            }
        }
        cur.close();
        return List;
    }

    public static ArrayList<String> getFacNameData(String district) {
        ArrayList<String> List = new ArrayList();
        List.clear();

        String query = "";

        if(district.isEmpty())
        {
            query = "SELECT * from "+ TableFacility.tableName + " order by " + TableFacility.tableColumn.priority + " asc";
        }
        else
        {
            query = "SELECT * from "+ TableFacility.tableName + " where " + TableFacility.tableColumn.priCodeDistrict + " = '" + district + "' ";
        }

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("str", query);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                List.add(cur.getString(cur.getColumnIndex(TableFacility.tableColumn.facilityName.toString())));
                cur.moveToNext();
            }
        }

        cur.close();
        return List;
    }

    public static ArrayList<String> getFacIdData(String district) {
        ArrayList<String> List = new ArrayList();
        List.clear();

        String query = "";

        if(district.isEmpty())
        {
            query = "SELECT * from "+ TableFacility.tableName + " order by " + TableFacility.tableColumn.priority + " asc";
        }
        else
        {
            query = "SELECT * from "+ TableFacility.tableName + " where " + TableFacility.tableColumn.priCodeDistrict + " = '" + district + "' ";
//            query = "SELECT * from "+ TableFacility.tableName + " where " + TableFacility.tableColumn.priCodeDistrict + " = '" + district + "' order by " + TableFacility.tableColumn.priority + " asc";
        }

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("str", query);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                List.add(cur.getString(cur.getColumnIndex(TableFacility.tableColumn.facilityId.toString())));
                cur.moveToNext();
            }
        }
        cur.close();
        return List;
    }

    public static ArrayList<String> getNurseIdCheckedInData() {
        ArrayList<String> List = new ArrayList();
        List.clear();

        String str = "SELECT * from "+ TableDutyChange.tableName + " where "
                + TableDutyChange.tableColumn.type + " = '1' and "
                + TableDutyChange.tableColumn.loungeId +" = "+ AppSettings.getString(AppSettings.loungeId);

        Cursor cur = myDataBase.rawQuery(str, null);

        Log.d("str", str);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                if(!cur.getString(cur.getColumnIndex(TableDutyChange.tableColumn.nurseId.toString())).equals("0")){
                    List.add(cur.getString(cur.getColumnIndex(TableDutyChange.tableColumn.nurseId.toString())));
                }
                cur.moveToNext();
            }
        }
        cur.close();
        return List;
    }

    public static ArrayList<String> getNurseNameCheckedInData() {
        ArrayList<String> List = new ArrayList();
        List.clear();

        String str = "SELECT * from "+ TableDutyChange.tableName + " where "
                + TableDutyChange.tableColumn.type + " = '" + 1 + "' and "
                + TableStaff.tableColumn.loungeId +" = "+ AppSettings.getString(AppSettings.loungeId);

        Cursor cur = myDataBase.rawQuery(str, null);

        Log.d("str", str);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                //List.add(cur.getString(cur.getColumnIndex(TableStaff.tableColumn.staffId.toString())));
                if(getNurseName(cur.getString(cur.getColumnIndex(TableDutyChange.tableColumn.nurseId.toString())))!=""){
                    List.add(getNurseName(cur.getString(cur.getColumnIndex(TableDutyChange.tableColumn.nurseId.toString()))));
                }
                cur.moveToNext();
            }
        }
        cur.close();
        return List;
    }

    public static ArrayList<String> getDocIdData() {
        ArrayList<String> List = new ArrayList();
        List.clear();
        Cursor cur = myDataBase.rawQuery("SELECT * from "+ TableStaff.tableName + " where " + TableStaff.tableColumn.staffType + " = '" + 1 + "' and "+ TableStaff.tableColumn.loungeId +" = "+ AppSettings.getString(AppSettings.loungeId), null);
        String str = "SELECT * from "+ TableStaff.tableName + " where " + TableStaff.tableColumn.staffType + " = '" + 1 + "' and status = '1' and "+ TableStaff.tableColumn.loungeId +" = "+ AppSettings.getString(AppSettings.loungeId);
        Log.d("str", str);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                List.add(cur.getString(cur.getColumnIndex(TableStaff.tableColumn.staffId.toString())));
                cur.moveToNext();
            }
        }
        cur.close();
        return List;
    }

    public static ArrayList<String> getDocNameData() {
        ArrayList<String> List = new ArrayList();
        List.clear();
        Cursor cur = myDataBase.rawQuery("SELECT * from "+ TableStaff.tableName + " where " + TableStaff.tableColumn.staffType + " = '" + 1 + "' and "+ TableStaff.tableColumn.loungeId +" = "+ AppSettings.getString(AppSettings.loungeId), null);
        String str = "SELECT * from "+ TableStaff.tableName + " where " + TableStaff.tableColumn.staffType + " = '" + 1 + "' and status = '1' and "+ TableStaff.tableColumn.loungeId +" = "+ AppSettings.getString(AppSettings.loungeId);
        Log.d("str", str);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                //List.add(cur.getString(cur.getColumnIndex(TableStaff.tableColumn.staffName.toString()))+" - "+cur.getString(cur.getColumnIndex(TableStaff.tableColumn.staffMobile.toString())));
                List.add(cur.getString(cur.getColumnIndex(TableStaff.tableColumn.staffName.toString())));
                cur.moveToNext();
            }
        }
        cur.close();
        return List;
    }

    public static String getSpecificValue(String tableName,String columnName,String where) {
        String value="";
        Cursor cur = myDataBase.rawQuery("SELECT "+columnName+" from "+ tableName + " where " + where , null);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                HashMap<String, String> hashlist = new HashMap();
                value=cur.getString(cur.getColumnIndex(columnName));
                cur.moveToNext();
            }
        }
        cur.close();
        return value;
    }

    public static int getCount(String tableName,String where ) {

        String query ="";

        int count = 0;

        query = "SELECT * from "+ tableName + " where " + where;
        Log.d("query-getAllBabies", query);
        Cursor cur = myDataBase.rawQuery(query, null);

        count = cur.getCount();
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        return count;
    }

    /*public static ArrayList<HashMap<String, String>> getKMCBabyWiseData(String babyId, String date) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();
        arrayList.clear();

        *//*String query ="SELECT * from "+TableKMC.tableName
                + " where " + TableKMC.tableColumn.babyId + " = '" + babyId
                + "' and startDate = '"+date+"' and status = '1' order by startDate desc";*//*

        String query ="SELECT *,  (startDate || ' ' || startTime) AS dateTimeText from "+TableKMC.tableName
                              + " where " + TableKMC.tableColumn.babyId + " = '" + babyId
                              + "' and startDate = '"+date+"' and status = '1' order by startDate desc";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query", String.valueOf(query));
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("id", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.serverId.toString())));
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.uuid.toString())));
                hashlist.put("startDate", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startDate.toString())));
                hashlist.put("provider", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.provider.toString())));
                hashlist.put("startTime", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startTime.toString())));
                hashlist.put("endTime", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.endTime.toString())));
                hashlist.put("nurseId", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.nurseId.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }

        cur.close();

        return arrayList;
    }*/

    public static ArrayList<HashMap<String, String>> getKMCBabyWiseData(String babyId, String where) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();
        arrayList.clear();

        /*String query ="SELECT * from "+TableKMC.tableName
                + " where " + TableKMC.tableColumn.babyId + " = '" + babyId
                + "' and startDate = '"+date+"' and status = '1' order by startDate desc";*/

        String query ="SELECT *,  (startDate || ' ' || startTime) AS dateTimeText from "+TableKMC.tableName
                              + " where " + TableKMC.tableColumn.babyId + " = '" + babyId
                              + "' and " +  where + " and status = '1' order by startDate desc";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query", String.valueOf(query));
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("id", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.serverId.toString())));
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.uuid.toString())));
                hashlist.put("startDate", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startDate.toString())));
                hashlist.put("provider", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.provider.toString())));
                hashlist.put("startTime", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startTime.toString())));
                hashlist.put("endTime", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.endTime.toString())));
                hashlist.put("endDate", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.endDate.toString())));
                hashlist.put("nurseId", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.nurseId.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }

        cur.close();

        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getCounsellingVideo() {
        ArrayList<HashMap<String, String>> CatList = new ArrayList();
        CatList.clear();
        Cursor cur = myDataBase.rawQuery("SELECT * from "+ TableVideos.tableName + " where " + TableVideos.tableColumn.videoType + " = '" + 1 + "' or "
                + TableVideos.tableColumn.videoType + " = '" + 2 + "' or "
                + TableVideos.tableColumn.videoType + " = '" + 3 + "' or "
                + TableVideos.tableColumn.videoType + " = '" + 4 + "' or "
                + TableVideos.tableColumn.videoType + " = '" + 5 + "' or "
                + TableVideos.tableColumn.videoType + " = '" + 6 + "'", null);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("videoLocation", cur.getString(cur.getColumnIndex(TableVideos.tableColumn.videoLocation.toString())));
                hashlist.put("videoName", cur.getString(cur.getColumnIndex(TableVideos.tableColumn.videoName.toString())));
                hashlist.put("videoUrl", cur.getString(cur.getColumnIndex(TableVideos.tableColumn.videoUrl.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableVideos.tableColumn.addDate.toString())));
                hashlist.put("videoThumb", cur.getString(cur.getColumnIndex(TableVideos.tableColumn.videoThumb.toString())));
                CatList.add(hashlist);
                cur.moveToNext();
            }
        }
        cur.close();
        return CatList;
    }

    public static ArrayList<HashMap<String, String>> getCounsellingPoster(String kmcPostition) {
        ArrayList<HashMap<String, String>> posterList = new ArrayList();
        posterList.clear();
//        Cursor cur = myDataBase.rawQuery("SELECT * from "+ TablePosters.tableName + " where " + TablePosters.tableColumn.posterType + " = '" + kmcPostition + "'", null);

        Cursor cur = myDataBase.rawQuery("SELECT * from "+ TablePosters.tableName + " where "
                + TableUser.tableColumn.status + " = '" + 1 + "' and "
                + TablePosters.tableColumn.posterType + " = '"
                + kmcPostition + "'", null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("videoName", cur.getString(cur.getColumnIndex(TablePosters.tableColumn.posterName.toString())));
                hashlist.put("videoUrl", cur.getString(cur.getColumnIndex(TablePosters.tableColumn.posterUrl.toString())));
                hashlist.put("posterUrl_base64", cur.getString(cur.getColumnIndex(TablePosters.tableColumn.posterUrl_base64.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TablePosters.tableColumn.addDate.toString())));
                hashlist.put("posterId", cur.getString(cur.getColumnIndex(TablePosters.tableColumn.posterId.toString())));
                posterList.add(hashlist);
                cur.moveToNext();
            }
        }
        cur.close();
        return posterList;
    }


    public static ArrayList<HashMap<String, String>> getStepData() {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();
        arrayList.clear();

        String query = "SELECT * from "+ TableUser.tableName
                               + " where " + TableUser.tableColumn.status + " = '" + 1 + "' and "
                               + TableUser.tableColumn.loungeId + " = '" + AppSettings.getString(AppSettings.loungeId)+ "' order by "
                               + TableUser.tableColumn.addDate +" desc ";

        Cursor cur = myDataBase.rawQuery(query , null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        Log.d("cur.query", query);

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableUser.tableColumn.uuid.toString())));
                hashlist.put("from", cur.getString(cur.getColumnIndex(TableUser.tableColumn.fromStep.toString())));
                hashlist.put("step1", cur.getString(cur.getColumnIndex(TableUser.tableColumn.step1.toString())));
                hashlist.put("mRStatus", cur.getString(cur.getColumnIndex(TableUser.tableColumn.mRStatus.toString())));
                hashlist.put("bRStatus", cur.getString(cur.getColumnIndex(TableUser.tableColumn.bRStatus.toString())));
                hashlist.put("bAStatus", cur.getString(cur.getColumnIndex(TableUser.tableColumn.bAStatus.toString())));
                hashlist.put("mAStatus", cur.getString(cur.getColumnIndex(TableUser.tableColumn.mAStatus.toString())));
                hashlist.put("cStatus", cur.getString(cur.getColumnIndex(TableUser.tableColumn.cStatus.toString())));
                hashlist.put("isSibling", cur.getString(cur.getColumnIndex(TableUser.tableColumn.isSibling.toString())));
                hashlist.put("motherId", "");

                //String motherId = DatabaseController.getMotherIdViaUUID(cur.getString(cur.getColumnIndex(TableUser.tableColumn.uuid.toString())));
                String motherId = "";

                if(cur.getString(cur.getColumnIndex(TableUser.tableColumn.isSibling.toString())).equals("1"))
                {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(cur.getString(cur.getColumnIndex(TableUser.tableColumn.step1.toString())));

                        Log.d("jsonObject",jsonObject.toString());

                        JSONObject newJSON = jsonObject.getJSONObject(AppConstants.projectName);

                        hashlist.put("motherId", newJSON.getString("motherId"));

                        motherId = newJSON.getString("motherId");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    hashlist.put("type", DatabaseController.getTypeViaMotherId(motherId));
                }

                arrayList.add(hashlist);
                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("arrayList.toString",arrayList.toString());

        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getStepData(String uuid) {

        ArrayList<HashMap<String, String>> CatList = new ArrayList();

        CatList.clear();

        String query = "SELECT * from "+ TableUser.tableName
                               + " where " + TableUser.tableColumn.status + " = '" + 1 + "' and "
                               + TableUser.tableColumn.loungeId + " = '" + AppSettings.getString(AppSettings.loungeId)+ "' and "
                               + TableUser.tableColumn.uuid + " = '" + uuid+ "'";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("getStepData-query", query);

        Log.d("getStepData-Count()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableUser.tableColumn.uuid.toString())));
                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableUser.tableColumn.loungeId.toString())));
                hashlist.put("motherId", cur.getString(cur.getColumnIndex(TableUser.tableColumn.motherId.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableUser.tableColumn.babyId.toString())));
                hashlist.put("babyAdmissionId", cur.getString(cur.getColumnIndex(TableUser.tableColumn.babyAdmissionId.toString())));
                hashlist.put("motherAdmissionId", cur.getString(cur.getColumnIndex(TableUser.tableColumn.motherAdmissionId.toString())));
                hashlist.put("step1", cur.getString(cur.getColumnIndex(TableUser.tableColumn.step1.toString())));
                hashlist.put("step2", cur.getString(cur.getColumnIndex(TableUser.tableColumn.step2.toString())));
                hashlist.put("step3", cur.getString(cur.getColumnIndex(TableUser.tableColumn.step3.toString())));
                hashlist.put("step4", cur.getString(cur.getColumnIndex(TableUser.tableColumn.step4.toString())));
                hashlist.put("step5", cur.getString(cur.getColumnIndex(TableUser.tableColumn.step5.toString())));
                hashlist.put("step6", cur.getString(cur.getColumnIndex(TableUser.tableColumn.step6.toString())));
                hashlist.put("step7", cur.getString(cur.getColumnIndex(TableUser.tableColumn.step7.toString())));
                hashlist.put("mRStatus", cur.getString(cur.getColumnIndex(TableUser.tableColumn.mRStatus.toString())));
                hashlist.put("bRStatus", cur.getString(cur.getColumnIndex(TableUser.tableColumn.bRStatus.toString())));
                hashlist.put("bAStatus", cur.getString(cur.getColumnIndex(TableUser.tableColumn.bAStatus.toString())));
                hashlist.put("mAStatus", cur.getString(cur.getColumnIndex(TableUser.tableColumn.mAStatus.toString())));
                hashlist.put("cStatus", cur.getString(cur.getColumnIndex(TableUser.tableColumn.cStatus.toString())));
                hashlist.put("from", cur.getString(cur.getColumnIndex(TableUser.tableColumn.fromStep.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableUser.tableColumn.addDate.toString())));
                hashlist.put("modifyDate", cur.getString(cur.getColumnIndex(TableUser.tableColumn.modifyDate.toString())));
                hashlist.put("status", cur.getString(cur.getColumnIndex(TableUser.tableColumn.status.toString())));

                CatList.add(hashlist);
                cur.moveToNext();
            }

        }

        cur.close();

        return CatList;
    }

    public static String getFromData(String uuid) {

        String result ="";

        String query = "SELECT * from "+ TableUser.tableName
                               + " where " + TableUser.tableColumn.status + " = '" + 1 + "' and "
                               + TableUser.tableColumn.loungeId + " = '" + AppSettings.getString(AppSettings.loungeId)+ "' and "
                               + TableUser.tableColumn.uuid + " = '" + uuid+ "'";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("getStepData-query", query);
        Log.d("getStepData-Count()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                result = cur.getString(cur.getColumnIndex(TableUser.tableColumn.fromStep.toString()));

                cur.moveToNext();
            }
        }

        cur.close();

        return result;
    }

    public static ArrayList<HashMap<String, String>> getLastBabyMonitoring(String babyId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query = "SELECT * from "+ TableBabyMonitoring.tableName + " where "+ TableBabyMonitoring.tableColumn.babyId + " = '" + babyId
                +"' and isDataComplete = '0'" ;

        Cursor cur = myDataBase.rawQuery(query , null);

        Log.d("getData-query", query);
        Log.d("getData-Count()", String.valueOf(cur.getCount()));
        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.uuid.toString())));
                hashlist.put("serverId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.serverId.toString())));
                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.loungeId.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyId.toString())));
                hashlist.put("babyAdmissionId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyAdmissionId.toString())));
                hashlist.put("babyMeasuredWeight", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyMeasuredWeight.toString())));
                hashlist.put("isHeadCircumferenceAvail", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isHeadCircumferenceAvail.toString())));
                hashlist.put("isLengthAvail", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isLengthAvail.toString())));
                hashlist.put("lengthValue", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.lengthValue.toString())));
                hashlist.put("measuringTapeNotAvailReason", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.measuringTapeNotAvailReason.toString())));
                hashlist.put("babyHeadCircumference", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyHeadCircumference.toString())));
                hashlist.put("babyRespiratoryRate", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyRespiratoryRate.toString())));
                hashlist.put("babyTemperature", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyTemperature.toString())));
                hashlist.put("temperatureUnit", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.temperatureUnit.toString())));
                hashlist.put("isPulseOximatoryDeviceAvailable", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isPulseOximatoryDeviceAvailable.toString())));
                hashlist.put("crtKnowledge", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.crtKnowledge.toString())));
                hashlist.put("isCftGreaterThree", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isCftGreaterThree.toString())));
                hashlist.put("type", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.type.toString())));
                hashlist.put("urinePassedIn24Hrs", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.urinePassedIn24Hrs.toString())));
                hashlist.put("stoolPassedIn24Hrs", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.stoolPassedIn24Hrs.toString())));
                hashlist.put("generalCondition", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.generalCondition.toString())));
                hashlist.put("tone", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.tone.toString())));
                hashlist.put("sucking", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.sucking.toString())));
                hashlist.put("apneaOrGasping", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.apneaOrGasping.toString())));
                hashlist.put("grunting", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.grunting.toString())));
                hashlist.put("chestIndrawing", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.chestIndrawing.toString())));
                hashlist.put("color", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.color.toString())));
                hashlist.put("isBleeding", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isBleeding.toString())));
                hashlist.put("bulgingAnteriorFontanel", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.bulgingAnteriorFontanel.toString())));
                hashlist.put("umbilicus", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.umbilicus.toString())));
                hashlist.put("skinPustules", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.skinPustules.toString())));

                String babySpO2 = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babySpO2.toString()));
                hashlist.put("babySpO2", babySpO2 == null ? "" : babySpO2);

                String babyPulseRate = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyPulseRate.toString()));
                hashlist.put("babyPulseRate", babyPulseRate == null ? "" : babyPulseRate);

                String babyTemperature = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyTemperature.toString()));
                hashlist.put("babyTemperature", babyTemperature == null ? "" : babyTemperature);

                String babyRespiratoryRate = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyRespiratoryRate.toString()));
                hashlist.put("babyRespiratoryRate", babyRespiratoryRate == null ? "" : babyRespiratoryRate);

                String isPulseOximatoryDeviceAvailable = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isPulseOximatoryDeviceAvailable.toString()));
                hashlist.put("isPulseOximatoryDeviceAvailable", isPulseOximatoryDeviceAvailable == null ? "" : isPulseOximatoryDeviceAvailable);

                hashlist.put("assessmentNumber", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.assessmentNumber.toString())));
                hashlist.put("staffId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.staffId.toString())));
                hashlist.put("staffSign", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.staffSign.toString())));
                hashlist.put("isThermometerAvailable", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isThermometerAvailable.toString())));
                hashlist.put("formattedDate", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.formattedDate.toString())));
                hashlist.put("isInterestInFeeding", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isInterestInFeeding.toString())));
                hashlist.put("lactation", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.lactation.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.addDate.toString())));
                hashlist.put("nurseName", getNurseName(cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.staffId.toString()))));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static String getNurseName(String nurseId) {
        String name = "";
        Cursor cur = myDataBase.rawQuery("SELECT * from "+ TableStaff.tableName + " where " + TableStaff.tableColumn.staffId + " = '" + nurseId +"'", null);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                name=cur.getString(cur.getColumnIndex(TableStaff.tableColumn.staffName.toString()));
                cur.moveToNext();
            }
        }
        cur.close();
        return name;
    }

    public static String getNurseMobile(String nurseId) {
        String name = "";
        Cursor cur = myDataBase.rawQuery("SELECT * from "+ TableStaff.tableName + " where " + TableStaff.tableColumn.staffId + " = '" + nurseId +"'", null);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                name=cur.getString(cur.getColumnIndex(TableStaff.tableColumn.staffMobile.toString()));
                cur.moveToNext();
            }
        }
        cur.close();
        return name;
    }

    public static String getNurseProfile(String nurseId) {
        String name = "";
        Cursor cur = myDataBase.rawQuery("SELECT * from "+ TableStaff.tableName + " where " + TableStaff.tableColumn.staffId + " = '" + nurseId +"'", null);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                name=cur.getString(cur.getColumnIndex(TableStaff.tableColumn.staffProfile.toString()));
                cur.moveToNext();
            }
        }
        cur.close();
        return name;
    }

    public static String getMotherName(String MotherId) {
        String result = "";
        Cursor cur = myDataBase.rawQuery("SELECT * from "+ TableMotherRegistration.tableName +
                " where " + TableMotherRegistration.tableColumn.motherId + " = '" + MotherId +"'", null);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                result = cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherName.toString()));
                cur.moveToNext();
            }
        }

        cur.close();
        return result;
    }

    public static ArrayList<HashMap<String, String>> getAllNurses() {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query = "SELECT * from "+ TableStaff.tableName + " where "
                + TableStaff.tableColumn.staffType + " = '" + 2 + "' and status = '1' and "
                + TableStaff.tableColumn.loungeId +" = "+ AppSettings.getString(AppSettings.loungeId) + " order by staffName asc";

        Log.d("query-getAllBabies",query);

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                String where = "nurseId = '"+cur.getString(cur.getColumnIndex(TableStaff.tableColumn.staffId.toString()))
                        +"' and type != '2' and " + TableStaff.tableColumn.loungeId +" = "+ AppSettings.getString(AppSettings.loungeId);

                if(!checkRecordExistWhere(TableDutyChange.tableName, where))
                {
                    HashMap<String, String> hashlist = new HashMap();

                    hashlist.put("id", cur.getString(cur.getColumnIndex(TableStaff.tableColumn.staffId.toString())));
                    hashlist.put("name", cur.getString(cur.getColumnIndex(TableStaff.tableColumn.staffName.toString())));
                    hashlist.put("profile", cur.getString(cur.getColumnIndex(TableStaff.tableColumn.staffProfile.toString())));
                    hashlist.put("mobile", cur.getString(cur.getColumnIndex(TableStaff.tableColumn.staffMobile.toString())));
                    hashlist.put("status", "0");

                    arrayList.add(hashlist);

//                    if(!hashlist.equals(0) && !cur.getString(cur.getColumnIndex(TableDutyChange.tableColumn.staffId.toString())).equals("0")){
//                        arrayList.add(hashlist);
//                    }
                }

                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("arrayList.toString",arrayList.toString());

        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getAllCheckOutNurses() {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        String query = "SELECT * from "+ TableDutyChange.tableName+ " where "
                + TableDutyChange.tableColumn.type + " = '1' and "
                + TableDutyChange.tableColumn.loungeId +" = "+ AppSettings.getString(AppSettings.loungeId);

        Log.d("query-getAllBabies",query);

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                    hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableDutyChange.tableColumn.uuid.toString())));
                    hashlist.put("id", cur.getString(cur.getColumnIndex(TableDutyChange.tableColumn.nurseId.toString())));
                    hashlist.put("name", getNurseName(cur.getString(cur.getColumnIndex(TableDutyChange.tableColumn.nurseId.toString()))));
                    hashlist.put("mobile", getNurseMobile(cur.getString(cur.getColumnIndex(TableDutyChange.tableColumn.nurseId.toString()))));
                    hashlist.put("profile", getNurseProfile(cur.getString(cur.getColumnIndex(TableDutyChange.tableColumn.nurseId.toString()))));
                    hashlist.put("status", "0");

                if(!hashlist.equals(0) && !cur.getString(cur.getColumnIndex(TableDutyChange.tableColumn.nurseId.toString())).equals("0")){
                    arrayList.add(hashlist);
                }

                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("arrayList.toString",arrayList.toString());

        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getStatsNurses(String nurseId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query = "SELECT * from "+ TableDutyChange.tableName + " where "
                + TableDutyChange.tableColumn.nurseId + " = '" + nurseId
                + "' and " + TableDutyChange.tableColumn.loungeId +" = "+ AppSettings.getString(AppSettings.loungeId)
                + " and " + TableDutyChange.tableColumn.addDate +" > '"+ AppUtils.getCurrentMonth()+"'";

        Log.d("query-getStatsNurses",query);

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        int late=0,onTime=0;

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                String dateTime = cur.getString(cur.getColumnIndex(TableDutyChange.tableColumn.addDate.toString()));
                Log.d("time-before",dateTime);

                String[] parts = dateTime.split(" ");

                Log.d("time-after",parts[1]);

                int newTime = Integer.parseInt(parts[1].replace(":",""));

                if(newTime>=73000&&newTime<=80000)
                {
                    onTime = onTime+1;
                }
                else if(newTime>=133000&&newTime<=140000)
                {
                    onTime = onTime+1;
                }
                else if(newTime>=193000&&newTime<=200000)
                {
                    onTime = onTime+1;
                }
                else
                {
                    late=late+1;
                }

//                if(newTime>=730&&newTime<=800)
//                {
//                    onTime = onTime+1;
//                }
//                else if(newTime>=1330&&newTime<=1400)
//                {
//                    onTime = onTime+1;
//                }
//                else if(newTime>=1930&&newTime<=2000)
//                {
//                    onTime = onTime+1;
//                }
                cur.moveToNext();
            }

            HashMap<String, String> hashlist = new HashMap();

            hashlist.put("count", String.valueOf(cur.getCount()));
            hashlist.put("onTime", String.valueOf(onTime));
            hashlist.put("late", String.valueOf(late));

            arrayList.add(hashlist);

        }
        cur.close();

        Log.d("arrayList.toString",arrayList.toString());

        return arrayList;
    }

    public static boolean getTodaysDuty(String where) {

        boolean bool = false;

        String query = "SELECT * from "+ TableDutyChange.tableName + " where "
                + TableDutyChange.tableColumn.loungeId +" = "+ AppSettings.getString(AppSettings.loungeId)
                + " and " +  where ;

        Log.d("query-getTodaysDuty",query);

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        int late=0,onTime=0;

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                String dateTime = cur.getString(cur.getColumnIndex(TableDutyChange.tableColumn.addDate.toString()));

                Log.d("time-before",dateTime);

                String[] parts = dateTime.split(" ");

                Log.d("time-after",parts[1]);

                int newTime = Integer.parseInt(parts[1].replace(":",""));

                Log.v("time-NewTime", String.valueOf(newTime));

                if(newTime>=73000&&newTime<=80000)
                {
                    onTime = onTime+1;
                }
                else if(newTime>=133000&&newTime<=140000)
                {
                    onTime = onTime+1;
                }
                else if(newTime>=193000&&newTime<=200000)
                {
                    onTime = onTime+1;
                }
                else
                {
                    late=late+1;
                }

               /* if(newTime>=730&&newTime<=800)
                {
                    onTime = onTime+1;
                }
                else if(newTime>=1330&&newTime<=1400)
                {
                    onTime = onTime+1;
                }
                else if(newTime>=1930&&newTime<=2000)
                {
                    onTime = onTime+1;
                }
                else
                {
                    late=late+1;
                }*/

                cur.moveToNext();
            }

            if(late==0&&onTime>0)
            {
                bool = true;
            }
        }

        cur.close();

        Log.d("arrayList.toString", String.valueOf(bool));

        return bool;
    }

    public static ArrayList<HashMap<String, String>> getAllBabiesAdmitted(String whereCondition) {
        ArrayList<HashMap<String, String>> CatList = new ArrayList();
        CatList.clear();

        String query = "SELECT bR.deliveryDate,bR.babyId,bR.babyPhoto,bR.motherId,bR.babyWeight" +
                               ",bA.serverId,bA.admissionDateTime,bA.addDate,bM.addDate,bM.babyRespiratoryRate,bM.babyTemperature" +
                               ",bM.isPulseOximatoryDeviceAvailable,bM.babyPulseRate,bM.babySpO2 " +
                               " from " + TableBabyRegistration.tableName + " as bR " +
                               " INNER JOIN  " + TableBabyAdmission.tableName + " as bA" +
                               " INNER JOIN  " + TableBabyMonitoring.tableName + " as bM" +
                               " on ( bR.babyId  = bA.babyId and bR.babyId = bM.babyId )" +
                               " where bA.status = 1 and bA.loungeId = " + AppSettings.getString(AppSettings.loungeId) +
                               "  and bM.isDataComplete = '1' and " +whereCondition+ " GROUP BY bR.babyId order by bR.addDate desc";

        Log.d("query-getAllBabies", query);

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {
                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyId.toString())));
                hashlist.put("babyAdmissionId", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.serverId.toString())));
                hashlist.put("lastAssessmentDateTime", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.addDate.toString())));
                hashlist.put("dob", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.deliveryDate.toString())));
                hashlist.put("babyPhoto", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyPhoto.toString())));
                hashlist.put("pulseRate", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyPulseRate.toString())));
                hashlist.put("spO2", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babySpO2.toString())));
                hashlist.put("deliveryDate", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.deliveryDate.toString())));
                hashlist.put("admissionDate", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.admissionDateTime.toString())));
                hashlist.put("respiratoryRate", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyRespiratoryRate.toString())));
                hashlist.put("temperature", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyTemperature.toString())));
                hashlist.put("birthWeight", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyWeight.toString())));
                hashlist.put("currentWeight", getLastInsertedBabyWeight(cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyId.toString()))));
                hashlist.put("isPulseOximatoryDeviceAvailable", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isPulseOximatoryDeviceAvailable.toString())));
                hashlist.put("motherName", getMotherName(cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.motherId.toString()))));
                hashlist.put("motherId", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.motherId.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.addDate.toString())));

                CatList.add(hashlist);
                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("CatList.toString", CatList.toString());

        return CatList;
    }

    public static ArrayList<HashMap<String, String>> getAllBabies() {
        ArrayList<HashMap<String, String>> CatList = new ArrayList();
        CatList.clear();

        String query = "SELECT bR.deliveryDate,bR.babyId,bR.babyPhoto,bR.motherId,bR.babyWeight" +
                               ",bA.serverId,bA.babyFileId,bA.admissionDateTime,bA.addDate,bM.addDate,bM.babyRespiratoryRate,bM.babyTemperature" +
                               ",bM.isPulseOximatoryDeviceAvailable,bM.babyPulseRate,bM.babySpO2 " +
                               " from " + TableBabyRegistration.tableName + " as bR " +
                               " INNER JOIN  " + TableBabyAdmission.tableName + " as bA" +
                               " INNER JOIN  " + TableBabyMonitoring.tableName + " as bM" +
                               " on ( bR.babyId  = bA.babyId and bR.babyId = bM.babyId )" +
                               " where bA.status = 1 and bA.loungeId = " + AppSettings.getString(AppSettings.loungeId) +
                               "  and bM.isDataComplete = '1'  GROUP BY bR.babyId order by bR.addDate desc";

        Log.d("query-getAllBabies", query);

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyId.toString())));
                hashlist.put("babyAdmissionId", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.serverId.toString())));
                hashlist.put("lastAssessmentDateTime", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.addDate.toString())));
                hashlist.put("dob", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.deliveryDate.toString())));
                hashlist.put("babyPhoto", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyPhoto.toString())));
                hashlist.put("pulseRate", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyPulseRate.toString())));
                hashlist.put("spO2", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babySpO2.toString())));
                hashlist.put("deliveryDate", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.deliveryDate.toString())));
                hashlist.put("admissionDate", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.admissionDateTime.toString())));
                hashlist.put("babyFileId", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.babyFileId.toString())));
                hashlist.put("respiratoryRate", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyRespiratoryRate.toString())));
                hashlist.put("temperature", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyTemperature.toString())));
                hashlist.put("birthWeight", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyWeight.toString())));
                hashlist.put("currentWeight", getLastInsertedBabyWeight(cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyId.toString()))));
                hashlist.put("isPulseOximatoryDeviceAvailable", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isPulseOximatoryDeviceAvailable.toString())));
                hashlist.put("motherName", getMotherName(cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.motherId.toString()))));
                hashlist.put("motherId", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.motherId.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.addDate.toString())));

                CatList.add(hashlist);
                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("CatList.toString", CatList.toString());

        return CatList;
    }

    public static int getAllBabiesMonthWise(String date,int type ) {

        String query ="";

        int count = 0;

        if(type==1)
        {
            query = "SELECT *" +
                                   " from " + TableBabyRegistration.tableName + " as bR " +
                                   " INNER JOIN  " + TableBabyAdmission.tableName + " as bA" +
                                   " on ( bR.babyId  = bA.babyId)" +
                                   " where bA.loungeId = " + AppSettings.getString(AppSettings.loungeId) +
                                   "  and bR.babyWeight <= '2500' and  bR.babyWeight >= '2000'  GROUP BY bR.babyId order by bR.addDate desc";
        }
        else
        {
            query = "SELECT *" +
                            " from " + TableBabyRegistration.tableName + " as bR " +
                            " INNER JOIN  " + TableBabyAdmission.tableName + " as bA" +
                            " on ( bR.babyId  = bA.babyId)" +
                            " where bA.loungeId = " + AppSettings.getString(AppSettings.loungeId) +
                            "  and  bR.babyWeight < '2000'  GROUP BY bR.babyId order by bR.addDate desc";
        }


        Log.d("query-getAllBabies", query);

        Cursor cur = myDataBase.rawQuery(query, null);

        count = cur.getCount();
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        return count;
    }

    public static ArrayList<HashMap<String, String>> getAllMothers() {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();
        arrayList.clear();

        String query ="SELECT mR.motherPicture,mR.motherId,mR.motherName,mR.isMotherAdmitted" +
                ",mA.serverId,mR.isMotherAdmitted " +
                " from "+ TableMotherRegistration.tableName +" as mR " +
                " INNER JOIN  "+ TableMotherAdmission.tableName +" as mA" +
                " on ( mR.motherId  = mA.motherId )" +
                " where mA.status = 1 and mR.loungeId = "+ AppSettings.getString(AppSettings.loungeId) +
                " GROUP BY mR.motherId order by mR.addDate desc";

        Log.d("query-getAllMothers",query);

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {
                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("motherId", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherId.toString())));
                hashlist.put("motherPhoto", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherPicture.toString())));
                hashlist.put("motherName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherName.toString())));
                hashlist.put("motherAdmissionId", cur.getString(cur.getColumnIndex(TableMotherAdmission.tableColumn.serverId.toString())));
                //hashlist.put("last_assessment", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.addDate.toString())));
                hashlist.put("lastAssessment", DatabaseController.getMotherMonitoringColumnData(cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherId.toString())),TableMotherMonitoring.tableColumn.addDate.toString()));
                hashlist.put("motherTemperature", "");
                hashlist.put("motherSystolicBP", "");
                hashlist.put("motherDiastolicBP", "");
                hashlist.put("motherPulse", "");


                //hashlist.put("motherTemperature", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherTemperature.toString())));
                //hashlist.put("motherSystolicBP", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherSystolicBP.toString())));
                //hashlist.put("motherDiastolicBP", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherDiastolicBP.toString())));
                //hashlist.put("motherPulse", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherPulse.toString())));

                arrayList.add(hashlist);
                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("cur.getCount()", String.valueOf(arrayList));

        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getBabyRegistrationDataViaId(String babyId) {
        ArrayList<HashMap<String, String>> CatList = new ArrayList();
        CatList.clear();

        String query ="SELECT * from "+ TableBabyRegistration.tableName +" as bR " +
                " INNER JOIN  "+ TableBabyAdmission.tableName +" as bA" +
                " INNER JOIN  "+ TableBabyMonitoring.tableName +" as bM" +
                " on ( bR.babyId  = bA.babyId and bR.babyId = bM.babyId )" +
                " where bA.status = 1 and bA.loungeId = "+ AppSettings.getString(AppSettings.loungeId) +
                " and bR.babyId  = '"+ babyId +"' and bM.isDataComplete = '1'  order by bM.addDate desc LIMIT 1";

        Log.d("getBabyRegistrationData",query);

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {
                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("motherName", getMotherName(cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.motherId.toString()))));
                hashlist.put("motherId",cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.motherId.toString())));

                hashlist.put("vitaminKGiven", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.vitaminKGiven.toString())));
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.uuid.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyId.toString())));
                hashlist.put("babyMCTSNumber", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyMCTSNumber.toString())));
                hashlist.put("deliveryDate", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.deliveryDate.toString())));
                hashlist.put("deliveryTime", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.deliveryTime.toString())));
                hashlist.put("babyGender", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyGender.toString())));
                hashlist.put("deliveryType", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.deliveryType.toString())));
                hashlist.put("birthWeightAvail", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.birthWeightAvail.toString())));
                hashlist.put("reason", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.reason.toString())));
                hashlist.put("babyCryAfterBirth", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyCryAfterBirth.toString())));
                hashlist.put("babyNeedBreathingHelp", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyNeedBreathingHelp.toString())));
                hashlist.put("registrationDateTime", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.registrationDateTime.toString())));
                hashlist.put("babyPhoto", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyPhoto.toString())));
                hashlist.put("firstTimeFeed", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.firstTimeFeed.toString())));
                hashlist.put("isDataSynced", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.isDataSynced.toString())));
                hashlist.put("typeOfBorn", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.typeOfBorn.toString())));
                hashlist.put("typeOfOutBorn", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.typeOfOutBorn.toString())));
                hashlist.put("wasApgarScoreRecorded", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.wasApgarScoreRecorded.toString())));
                hashlist.put("apgarScore", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.apgarScore.toString())));

                hashlist.put("birthWeight", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyWeight.toString())));
                hashlist.put("currentWeight", getLastInsertedBabyWeight(cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyId.toString()))));


                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.loungeId.toString())));

                hashlist.put("admissionDate", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.admissionDateTime.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.addDate.toString())));
                hashlist.put("babyFileId", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.babyFileId.toString())));
                hashlist.put("babyAdmissionId", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.serverId.toString())));


                String whatisKmc = cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.whatisKmc.toString()));
                hashlist.put("whatisKmc", whatisKmc == null ? "" : whatisKmc);

                String kmcPosition = cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.kmcPosition.toString()));
                hashlist.put("kmcPosition", kmcPosition == null ? "" : kmcPosition);

                String kmcMonitoring = cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.kmcMonitoring.toString()));
                hashlist.put("kmcMonitoring", kmcMonitoring == null ? "" : kmcMonitoring);

                String kmcNutrition = cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.kmcNutrition.toString()));
                hashlist.put("kmcNutrition", kmcNutrition == null ? "" : kmcNutrition);

                String kmcRespect = cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.kmcRespect.toString()));
                hashlist.put("kmcRespect", kmcRespect == null ? "" : kmcRespect);

                String kmcHygiene = cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.kmcHygiene.toString()));
                hashlist.put("kmcHygiene", kmcHygiene == null ? "" : kmcHygiene);


                hashlist.put("isPulseOximatoryDeviceAvailable", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isPulseOximatoryDeviceAvailable.toString())));
                hashlist.put("respiratoryRate", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyRespiratoryRate.toString())));
                hashlist.put("temperature", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyTemperature.toString())));
                hashlist.put("spO2", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babySpO2.toString())));
                hashlist.put("pulseRate", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyPulseRate.toString())));
                //hashlist.put("other", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.other.toString())));
                hashlist.put("staffId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.staffId.toString())));
                hashlist.put("staffName", getNurseName(cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.staffId.toString()))));
                hashlist.put("babyMeasuredWeight", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyMeasuredWeight.toString())));
                hashlist.put("json", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.json.toString())));

                String isThermometerAvailable = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isThermometerAvailable.toString()));
                hashlist.put("isThermometerAvailable", isThermometerAvailable == null ? "" : isThermometerAvailable);

                //hashlist.put("thermoReason", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.thermoReason.toString())));

                CatList.add(hashlist);
                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("cur.getCount()", String.valueOf(CatList));

        return CatList;
    }

    public static ArrayList<HashMap<String, String>> getMotherRegistrationDataViaId(String motherId) {
        ArrayList<HashMap<String, String>> CatList = new ArrayList();
        CatList.clear();

        String query ="SELECT *,mA.serverId " + " from "+ TableMotherRegistration.tableName +" as mR " +
                " INNER JOIN  "+ TableMotherAdmission.tableName +" as mA" +
                " on ( mR.motherId  = mA.motherId )" +
                " where mA.status = 1 and mR.loungeId = "+ AppSettings.getString(AppSettings.loungeId) +
                " and mR.motherId  = '" + motherId + "'";

        Log.d("getMotherRegData",query);

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {
                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.uuid.toString())));
                hashlist.put("motherId", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherId.toString())));
                hashlist.put("motherName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherName.toString())));
                hashlist.put("motherPicture", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherPicture.toString())));
                hashlist.put("isMotherAdmitted", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.isMotherAdmitted.toString())));
                hashlist.put("motherLmpDate", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherLmpDate.toString())));
                hashlist.put("motherAdmissionId", cur.getString(cur.getColumnIndex(TableMotherAdmission.tableColumn.serverId.toString())));

                CatList.add(hashlist);
                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("cur.getCount()", String.valueOf(CatList));

        return CatList;
    }

    public static ArrayList<HashMap<String, String>> getBabyWeightViaId(String babyId,String delDate) {

        ArrayList<HashMap<String, String>> CatList = new ArrayList();

        CatList.clear();

        int days = Integer.parseInt((AppUtils.getDateDiff(delDate)));

        Log.d("days", String.valueOf(days));

        for(int j=0;j<=days;j++)
        {
            if(j==0)
            {
                String query ="SELECT * from "+ TableBabyRegistration.tableName +
                        " where babyId  = '" + babyId + "'";

                Log.d("getBabyWeightViaId",query);

                Cursor cur = myDataBase.rawQuery(query, null);

                Log.d("cur.getCount()", String.valueOf(cur.getCount()));

                if (cur != null && cur.moveToNext()) {

                    for (int i = 0; i < cur.getCount(); i++) {
                        HashMap<String, String> hashlist = new HashMap();
                        hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.uuid.toString())));
                        hashlist.put("age", String.valueOf(j));
                        hashlist.put("weightDate", AppUtils.getDateInFormat());
                        hashlist.put("babyWeight", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyWeight.toString())));
                        hashlist.put("isWeighingAvail", "Yes");
                        hashlist.put("weighingReason", "");

                        CatList.add(hashlist);
                        cur.moveToNext();
                    }
                }
                else
                {
                    HashMap<String, String> hashlist = new HashMap();
                    hashlist.put("uuid", "");
                    hashlist.put("age", String.valueOf(j));
                    hashlist.put("weightDate", "");
                    hashlist.put("babyWeight", "0");
                    hashlist.put("isWeighingAvail", "No");
                    hashlist.put("weighingReason", "");

                    CatList.add(hashlist);
                    cur.moveToNext();
                }
            }
            else
            {
                String query ="SELECT * from "+ TableWeight.tableName +
                        " where babyId  = '" + babyId + "' and weightDate = '"+ AppUtils.getCurrentDateYMD(-(days-j))+"' order by addDate desc LIMIT 1";

                Log.d("getBabyWeightViaId",query);

                Cursor cur = myDataBase.rawQuery(query, null);

                Log.d("cur.getCount()", String.valueOf(cur.getCount()));

                if (cur != null && cur.moveToNext()) {

                    for (int i = 0; i < cur.getCount(); i++) {
                        HashMap<String, String> hashlist = new HashMap();
                        hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.uuid.toString())));
                        hashlist.put("age", String.valueOf(j));
                        hashlist.put("weightDate", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.weightDate.toString())));
                        hashlist.put("babyWeight", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.babyWeight.toString())));
                        hashlist.put("isWeighingAvail", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.isWeighingAvail.toString())));
                        hashlist.put("weighingReason", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.weighingReason.toString())));

                        CatList.add(hashlist);
                        cur.moveToNext();
                    }
                }
                else
                {
                    HashMap<String, String> hashlist = new HashMap();
                    hashlist.put("uuid", "");
                    hashlist.put("age", String.valueOf(j));
                    hashlist.put("weightDate", "");
                    hashlist.put("babyWeight", "0");
                    hashlist.put("isWeighingAvail", "No");
                    hashlist.put("weighingReason", "");

                    CatList.add(hashlist);
                    cur.moveToNext();
                }
                cur.close();
            }

            Log.d("getBabyWeightViaId", String.valueOf(CatList));
        }



        return CatList;
    }

    public static int getBabyWeightDays(String babyId) {

        int result = 0;

        String query ="SELECT * from "+ TableWeight.tableName +
                              " where babyId  = '" + babyId + "' and weightDate >= '"+ AppUtils.getCurrentDateYMD(-3)+"' order by addDate asc";

        Log.d("getBabyWeightViaId",query);

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        int oldWeight=0,newWeight=0;

        if (cur != null && cur.moveToNext()) {

            if(cur.getCount()<3)
            {
                for (int i = 0; i < cur.getCount(); i++) {

                    if(i==0)
                    {
                        try {
                            oldWeight = Integer.parseInt(cur.getString(cur.getColumnIndex(TableWeight.tableColumn.babyWeight.toString())));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            result = 0;
                            break;
                        }
                    }
                    else
                    {
                        newWeight  = Integer.parseInt(cur.getString(cur.getColumnIndex(TableWeight.tableColumn.babyWeight.toString())));

                        if((newWeight - oldWeight)>=15)
                        {
                            result = 1;
                            oldWeight= Integer.parseInt(cur.getString(cur.getColumnIndex(TableWeight.tableColumn.babyWeight.toString())));
                        }
                        else
                        {
                            result = 0;
                            break;
                        }
                    }

                    cur.moveToNext();
                }
            }
            else
            {
                result = 0 ;
            }
        }

        cur.close();

        Log.d("getBabyWeightDays", String.valueOf(result));

        return result;
    }

    public static int getBabyWeightViaDays(String babyId,int days) {

        int result = 0;

        String query ="SELECT * from "+ TableWeight.tableName +
                              " where babyId  = '" + babyId + "' and weightDate = '"+ AppUtils.getCurrentDateYMD(days)+"' order by addDate asc";

        Log.d("getBabyWeightViaId",query);

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            try {
                result = Integer.parseInt(cur.getString(cur.getColumnIndex(TableWeight.tableColumn.babyWeight.toString())));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                result = 0;
            }
        }

        cur.close();

        Log.d("getBabyWeightDays", String.valueOf(result));

        return result;
    }

    public static int getBabyAssessmentDays(String babyId) {

        int result = 0;

        String query ="SELECT * from "+ TableBabyMonitoring.tableName +
                              " where babyId  = '" + babyId + "' and addDate >= '"+ AppUtils.getCurrentDateYMDHMS(-3)+"' order by addDate asc";

        Log.d("getBabyWeightViaId",query);

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                int checkPulse=0,checkSpo2=0,checkTemp=0,checkResp=0;

                int pulse = 0;
                try {
                    pulse =  Integer.parseInt(cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyPulseRate.toString())));
                    if(pulse<75||pulse>200)
                    {
                        checkPulse=0;
                        break;
                    }
                    else
                    {
                        checkPulse=1;
                    }
                } catch (NumberFormatException e) {
                    //e.printStackTrace();
                    checkPulse=0;
                    break;
                }


                int spo2=0;
                try {
                    spo2 = Integer.parseInt(cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babySpO2.toString())));

                    if(spo2<95)
                    {
                        checkSpo2=0;
                        break;
                    }
                    else
                    {
                        checkSpo2=1;
                    }

                } catch (NumberFormatException e) {
                    //e.printStackTrace();
                    checkSpo2=0;
                    break;
                }

                float temp=0;
                try {
                    temp = Float.parseFloat(cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyTemperature.toString())));

                    if(temp>99.5||temp<95.9)
                    {
                        checkTemp=0;
                        break;
                    }
                    else
                    {
                        checkTemp=1;
                    }

                } catch (NumberFormatException e) {
                    //e.printStackTrace();
                    checkTemp=0;
                    break;
                }

                float res=0;
                try {
                    res = Float.parseFloat(cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyRespiratoryRate.toString())));

                    if(res<30||res>60)
                    {
                        checkResp=0;
                        break;
                    }
                    else
                    {
                        checkResp=1;
                    }
                } catch (NumberFormatException e) {
                    //e.printStackTrace();
                    checkResp=0;
                    break;
                }

                if(cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isPulseOximatoryDeviceAvailable.toString())).equalsIgnoreCase("Yes"))
                {
                    if(checkPulse==0||checkSpo2==0||checkTemp==0||checkResp==0)
                    {
                        result=0;
                        break;
                    }
                    else
                    {
                        result=1;
                    }
                }
                else  if(checkTemp==0||checkResp==0)
                {
                    result=0;
                    break;
                }
                else
                {
                    result=1;
                }

                cur.moveToNext();
            }
        }

        cur.close();

        Log.d("getBabyAssessmentDays", String.valueOf(result));

        return result;
    }

    public static int getBabyFeedingDays(String babyId) {

        int result = 0;

        String query ="SELECT * from "+ TableBreastFeeding.tableName +
                              " where babyId  = '" + babyId + "' and addDate >= '"+ AppUtils.getCurrentDateYMDHMS(-3)+"' order by addDate asc";

        Log.d("getBabyWeightViaId",query);

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                if(cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.method.toString())).equals("Breastfeeding"))
                {
                    result=1;
                }
                else
                {
                    result=0;
                    break;
                }

                cur.moveToNext();

            }
        }

        cur.close();

        Log.d("getBabyAssessmentDays", String.valueOf(result));

        return result;
    }

    public static int getBabyAssessment(String date) {

        int count=0;

        String query ="SELECT * from "+ TableBabyMonitoring.tableName + " where addDate > '" + date + " 00:00:00' ";

        Log.d("getAssessmentNumber",query);

        Cursor cur = myDataBase.rawQuery(query, null);

        count = cur.getCount();

        Log.d("cur.getCount()", String.valueOf(count));

        return count;
    }

    public static int getMotherAssessment(String date) {

        int count=0;

        String query ="SELECT * from "+ TableMotherMonitoring.tableName + " where addDate  > '" + date + " 00:00:00' ";

        Log.d("getAssessmentNumber",query);

        Cursor cur = myDataBase.rawQuery(query, null);

        count = cur.getCount();

        Log.d("cur.getCount()", String.valueOf(count));

        return count;
    }

    public static String getLastInsertedBabyWeight(String babyId) {

        String result = "";

        String query ="SELECT babyWeight from "+ TableWeight.tableName +
                " where babyId  = '" + babyId +"' order by uuid desc LIMIT 1";

        Log.d("getBabyWeightViaId",query);

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                result = cur.getString(cur.getColumnIndex(TableWeight.tableColumn.babyWeight.toString()));

                cur.moveToNext();
            }
        }

        return result;
    }

    public static ArrayList<HashMap<String, String>> getResRateViaId(String babyId,String delDate) {

        ArrayList<HashMap<String, String>> CatList = new ArrayList();
        CatList.clear();

        int days = Integer.parseInt((AppUtils.getDateDiff(delDate)));

        Log.d("days", String.valueOf(days));

        for(int j=0;j<=days;j++)
        {
            String query ="SELECT uuid,babyRespiratoryRate,addDate,formattedDate from "+ TableBabyMonitoring.tableName +
                    " where babyId  = '" + babyId + "' and formattedDate = '"+ AppUtils.getCurrentDateYMD(-(days-j))
                    + "' and isDataComplete = '1' order by addDate asc";

            Log.d("getBabyRegistrationData",query);

            Cursor cur = myDataBase.rawQuery(query, null);

            Log.d("cur.getCount()", String.valueOf(cur.getCount()));
            Log.d("cur", String.valueOf(cur));

            int duration=0;

            if (cur != null && cur.moveToNext()) {

                for (int i = 0; i < cur.getCount(); i++) {

                    if(duration==0)
                    {
                        try {
                            duration = Integer.parseInt(cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyRespiratoryRate.toString())));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        try {
                            duration = duration + Integer.parseInt(cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyRespiratoryRate.toString())));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    cur.moveToNext();
                }

                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("age", String.valueOf(j));
                hashlist.put("babyRespiratoryRate", String.valueOf((duration/cur.getCount())));

                CatList.add(hashlist);
            }
            else
            {
                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("age", String.valueOf(j));
                hashlist.put("babyRespiratoryRate","0");

                CatList.add(hashlist);
            }
            cur.close();
        }


        Log.d("cur.getCount()", String.valueOf(CatList));

        return CatList;
    }

    public static ArrayList<HashMap<String, String>> getTempViaId(String babyId,String delDate) {

        ArrayList<HashMap<String, String>> CatList = new ArrayList();
        CatList.clear();

        int days = Integer.parseInt((AppUtils.getDateDiff(delDate)));

        Log.d("days", String.valueOf(days));

        for(int j=0;j<=days;j++)
        {
            String query ="SELECT uuid,babyTemperature,addDate from "+ TableBabyMonitoring.tableName +
                    " where babyId  = '" + babyId + "' and formattedDate = '"+ AppUtils.getCurrentDateYMD(-(days-j))
                    +  "' and isDataComplete = '1' order by addDate asc";

            Log.d("getTempViaId",query);

            Cursor cur = myDataBase.rawQuery(query, null);

            Log.d("cur.getCount()", String.valueOf(cur.getCount()));

            float temperature=0;

            if (cur != null && cur.moveToNext()) {

                for (int i = 0; i < cur.getCount(); i++) {

                    if(temperature==0)
                    {
                        try {
                            temperature = Float.parseFloat(cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyTemperature.toString())));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        try {
                            temperature = temperature + Float.parseFloat(cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyTemperature.toString())));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    cur.moveToNext();
                }

                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("babyTemperature",String.valueOf((temperature/cur.getCount())));
                hashlist.put("age", String.valueOf(j));
                CatList.add(hashlist);
            }
            else
            {
                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("babyTemperature","0");
                hashlist.put("age", String.valueOf(j));

                CatList.add(hashlist);
            }
            cur.close();
        }

        Log.d("cur.getCount()", String.valueOf(CatList));

        return CatList;
    }

    public static ArrayList<HashMap<String, String>> getSpO2ViaId(String babyId,String delDate) {

        ArrayList<HashMap<String, String>> CatList = new ArrayList();
        CatList.clear();

        int days = Integer.parseInt((AppUtils.getDateDiff(delDate)));

        Log.d("days", String.valueOf(days));

        for(int j=0;j<=days;j++)
        {
            String query ="SELECT uuid,babySpO2,addDate from "+ TableBabyMonitoring.tableName +
                    " where babyId  = '" + babyId + "' and formattedDate = '"+ AppUtils.getCurrentDateYMD(-(days-j))
                    +  "' and isDataComplete = '1' order by addDate asc";

            Log.d("getSpo2ViaId",query);

            Cursor cur = myDataBase.rawQuery(query, null);

            Log.d("cur.getCount()", String.valueOf(cur.getCount()));

            int result=0;

            if (cur != null && cur.moveToNext()) {

                for (int i = 0; i < cur.getCount(); i++) {

                    if(result==0)
                    {
                        try {
                            result = Integer.parseInt(cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babySpO2.toString())));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        try {
                            result = result + Integer.parseInt(cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babySpO2.toString())));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    cur.moveToNext();
                }

                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("babySpO2",String.valueOf((result/cur.getCount())));
                hashlist.put("age", String.valueOf(j));
                CatList.add(hashlist);
            }
            else
            {
                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("babySpO2","0");
                hashlist.put("age", String.valueOf(j));

                CatList.add(hashlist);
            }
            cur.close();
        }

        Log.d("cur.getCount()", String.valueOf(CatList));

        return CatList;
    }

    public static ArrayList<HashMap<String, String>> getSSCViaId(String babyId,String delDate) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        int days = Integer.parseInt((AppUtils.getDateDiff(delDate)));

        Log.d("days", String.valueOf(days));

        for(int j=0;j<=days;j++) {

            String query ="SELECT * from "+TableKMC.tableName
                    + " where " + TableKMC.tableColumn.babyId + " = '" + babyId+ "' and " +
                    " startDate = '"+parseDateToDMYFormat(AppUtils.getCurrentDateDMY(-(days-j)))+"' and status = '" + 1 +"'";

            Cursor cur = myDataBase.rawQuery(query, null);

            Log.d("query", String.valueOf(query));
            Log.d("cur.getCount()", String.valueOf(cur.getCount()));

            String duration="0";

            if (cur != null && cur.moveToNext()) {

                for (int i = 0; i < cur.getCount(); i++) {

                    if(duration.equals("0"))
                    {
                        try {
                            duration =AppUtils.getSSTTime(cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startDate.toString()))+ " "+cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startTime.toString()))
                                    ,cur.getString(cur.getColumnIndex(TableKMC.tableColumn.endDate.toString()))+ " "+cur.getString(cur.getColumnIndex(TableKMC.tableColumn.endTime.toString())));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        try {
                            duration = AppUtils.sumTimes(duration ,AppUtils.getSSTTime(cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startDate.toString()))+ " "+cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startTime.toString()))
                                    ,cur.getString(cur.getColumnIndex(TableKMC.tableColumn.endDate.toString()))+ " "+cur.getString(cur.getColumnIndex(TableKMC.tableColumn.endTime.toString()))));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    cur.moveToNext();
                }

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("duration", duration);
                hashlist.put("age", String.valueOf(j));

                arrayList.add(hashlist);
            }
            else
            {
                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("duration", "0");
                hashlist.put("age", String.valueOf(j));

                arrayList.add(hashlist);
            }

            cur.close();
        }

        Log.d("cur.getCount()", String.valueOf(arrayList));

        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getSSCAll(String where) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();
        arrayList.clear();

     /*   String query ="SELECT * from "+TableKMC.tableName
                              + " where  startDate = '"+date+"' order by startDate desc";*/

        String query ="SELECT *,  (startDate || ' ' || startTime) AS dateTimeText from "+TableKMC.tableName
                              + " where " +  where + " order by startDate desc";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query", String.valueOf(query));
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("id", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.serverId.toString())));
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.uuid.toString())));
                hashlist.put("startDate", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startDate.toString())));
                hashlist.put("provider", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.provider.toString())));
                hashlist.put("startTime", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startTime.toString())));
                hashlist.put("endTime", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.endTime.toString())));
                hashlist.put("nurseId", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.nurseId.toString())));

                String stTime="",endTime="";

                stTime= cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startDate.toString()))+ " "+cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startTime.toString()));
                endTime=cur.getString(cur.getColumnIndex(TableKMC.tableColumn.endDate.toString()))+ " "+cur.getString(cur.getColumnIndex(TableKMC.tableColumn.endTime.toString()));

                String duration="00:00";

                try {

                    duration = AppUtils.getSSTTime(stTime,endTime);

                    String[] parts = duration.split(":");

                    Log.d("time-after",parts[0]);

                    int currentTime= Integer.parseInt(parts[0]);

                    if(currentTime>=12)
                    {
                        arrayList.add(hashlist);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                cur.moveToNext();
            }
        }

        cur.close();

        return arrayList;
    }

    public static String getDoctorRoundDateWise(String date) {

        String where = "";
        if(date.equalsIgnoreCase(AppUtils.getAddDateAsPerHospital()))
        {
            where = AppUtils.getAddDateAsPerHospital();
        }
        else
        {
            where = AppUtils.getHourDayAsPerHospital(date);
        }

        String query ="SELECT *,  (addDate) AS dateTimeText from "+TableDoctorRound.tableName
                              + " where  addDate " +  where + " group by babyId order by addDate desc";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query", query);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        return String.valueOf(cur.getCount());
    }

    public static ArrayList<HashMap<String, String>> getFeedingViaId(String babyId,String delDate) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        int days = Integer.parseInt((AppUtils.getDateDiff(delDate)));

        Log.d("days", String.valueOf(days));

        for(int j=0;j<=days;j++) {

            float sum = Float.parseFloat(DatabaseController.getTotalQuantity(
                    babyId, AppUtils.getCurrentDateDMY(-(days-j))));

            HashMap<String, String> hashlist = new HashMap();

            hashlist.put("milkQuantity", String.valueOf(sum));
            hashlist.put("age", String.valueOf(j));

            arrayList.add(hashlist);
        }

        Log.d("cur.getCount()", String.valueOf(arrayList));

        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getPulseRateViaId(String babyId,String delDate) {

        ArrayList<HashMap<String, String>> CatList = new ArrayList();
        CatList.clear();

        int days = Integer.parseInt((AppUtils.getDateDiff(delDate)));

        Log.d("days", String.valueOf(days));

        for(int j=0;j<=days;j++)
        {
            String query ="SELECT uuid,babyPulseRate,addDate from "+ TableBabyMonitoring.tableName +
                    " where babyId  = '" + babyId + "' and formattedDate = '"+ AppUtils.getCurrentDateYMD(-(days-j))
                    +  "' and isDataComplete = '1' order by addDate asc";

            Log.d("getSpo2ViaId",query);

            Cursor cur = myDataBase.rawQuery(query, null);

            Log.d("cur.getCount()", String.valueOf(cur.getCount()));

            int result=0;

            if (cur != null && cur.moveToNext()) {

                for (int i = 0; i < cur.getCount(); i++) {

                    if(result==0)
                    {
                        try {
                            result = Integer.parseInt(cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyPulseRate.toString())));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        try {
                            result = result + Integer.parseInt(cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyPulseRate.toString())));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    cur.moveToNext();
                }

                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("babyPulseRate",String.valueOf((result/cur.getCount())));
                hashlist.put("age", String.valueOf(j));
                CatList.add(hashlist);
            }
            else
            {
                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("babyPulseRate","0");
                hashlist.put("age", String.valueOf(j));

                CatList.add(hashlist);
            }
            cur.close();
        }

        Log.d("cur.getCount()", String.valueOf(CatList));

        return CatList;
    }

    public static String getBabyMoniUUID(String babyId) {

        String result="";

        String query = "SELECT uuid from "+ TableBabyMonitoring.tableName
                + " where " + TableBabyMonitoring.tableColumn.babyId + " = '" + babyId
                +"' and isDataComplete = '0'  ";

        Cursor cur = myDataBase.rawQuery(query , null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                result=cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.uuid.toString()));
                cur.moveToNext();
            }
        }
        cur.close();
        return result;
    }

    public static ArrayList<HashMap<String, String>> getFeedingData(String babyId,String date) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        /*String query ="SELECT * from "+ TableBreastFeeding.tableName
                + " where " + TableBreastFeeding.tableColumn.babyId + " = '" + babyId+ "' and date = '"+date+"' and status = '" + status +"' order by feedTime desc";*/

        String where="";

        if(date.equalsIgnoreCase(AppUtils.getCurrentDateFormatted()))
        {
            where = AppUtils.getCurrentDateAsPerHospital();
        }
        else
        {
            where = AppUtils.getHoursDayAsPerHospital(date);
        }

        String query ="SELECT *,  (date || ' ' || feedTime) AS dateTimeText from "+TableBreastFeeding.tableName
                              + " where " + TableBreastFeeding.tableColumn.babyId + " = '" + babyId
                              + "' and " +  where + " and status = '1' order by date desc,feedTime desc";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.query", query);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.uuid.toString())));
                hashlist.put("feedTime", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.feedTime.toString())));
                hashlist.put("duration", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.duration.toString())));
                hashlist.put("method", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.method.toString())));
                hashlist.put("fluid", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.fluid.toString())));
                hashlist.put("quantity", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.quantity.toString())));
                hashlist.put("date", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.date.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static String getTotalQuantity(String babyId,String date) {

        String result="";
        String where="";

        if(date.equalsIgnoreCase(AppUtils.getCurrentDateFormatted()))
        {
            where = AppUtils.getCurrentDateAsPerHospital();
        }
        else
        {
            where = AppUtils.getHoursDayAsPerHospital(date);
        }

        /*String query ="SELECT sum(quantity) from "+ TableBreastFeeding.tableName
                + " where " + TableBreastFeeding.tableColumn.babyId + " = '" + babyId+ "' and date = '"+date +"' order by addDate desc LIMIT 1";*/

        String query ="SELECT sum(quantity),  (date || ' ' || feedTime) AS dateTimeText from "+TableBreastFeeding.tableName
                              + " where " + TableBreastFeeding.tableColumn.babyId + " = '" + babyId
                              + "' and " +  where + " and status = '1' order by addDate desc ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                result = String.valueOf(cur.getFloat(0));
                cur.moveToNext();
            }
        }
        else
        {
            result = "0";
        }

        cur.close();
        return result;
    }

    public static String getTotalBirth(int type) {

        String query="",result="0";

        if(type==1)
        {
            query ="SELECT SUM(totalLiveBirth) as total from "+ TableBirthReview.tableName
                    + " where " + TableBirthReview.tableColumn.loungeId + " = '" + AppSettings.getString(AppSettings.loungeId)
                    + "' and addDate "+AppUtils.getAddDateAsPerHospital();
        }
        else if(type==2)
        {
            query ="SELECT SUM(totalStableBabies) as total  from "+ TableBirthReview.tableName
                           + " where " + TableBirthReview.tableColumn.loungeId + " = '" + AppSettings.getString(AppSettings.loungeId)
                           + "' and addDate "+AppUtils.getAddDateAsPerHospital();
        }
        else if(type==3)
        {
            query ="SELECT SUM(totalUnstableBabies)  as total  from "+ TableBirthReview.tableName
                           + " where " + TableBirthReview.tableColumn.loungeId + " = '" + AppSettings.getString(AppSettings.loungeId)
                           + "' and addDate "+AppUtils.getAddDateAsPerHospital();
        }

        Log.d("query", query);
        Cursor cur = myDataBase.rawQuery(query, null);

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                result=cur.getString(cur.getColumnIndex("total"));

                try {
                    if(result.isEmpty())
                    {
                        result = "0";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    result = "0";
                }

                cur.moveToNext();
            }
        }

        cur.close();
        return result;
    }

    public static String getAdmitted(String date) {

        String query="SELECT * from "+ TableBabyAdmission.tableName
                       + " where " + TableBabyAdmission.tableColumn.loungeId + " = '" + AppSettings.getString(AppSettings.loungeId)
                       + "' and admissionDateTime "+ AppUtils.getAddDateAsPerHospital();

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query", query);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        return String.valueOf(cur.getCount());
    }

    public static String getWeight() {

        String query="SELECT * from "+ TableWeight.tableName
                       + " where " + TableWeight.tableColumn.loungeId + " = '" + AppSettings.getString(AppSettings.loungeId)
                       + "' and addDate "+ AppUtils.getAddDateAsPerHospital();

        Log.d("query", query);

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        Log.d("query", query);
        return String.valueOf(cur.getCount());
    }

    public static String getTotalMethod(Context context,String babyId,String date,String type) {

        String query="",where="";

        if(date.equalsIgnoreCase(AppUtils.getCurrentDateFormatted()))
        {
            where = AppUtils.getAddDateAsPerHospital();
        }
        else
        {
            where = AppUtils.getHourDayAsPerHospital(date);
        }

        if(type.equalsIgnoreCase("1"))
        {
            query ="SELECT *,  (date || ' ' || feedTime) AS dateTimeText from "+TableBreastFeeding.tableName
                    + " where " + TableBreastFeeding.tableColumn.babyId + " = '" + babyId
                    + "' and dateTimeText "+where+" and method = '" + context.getString(R.string.breastValue) +"'";
        }
        else
        {
            query ="SELECT *,  (date || ' ' || feedTime) AS dateTimeText from "+TableBreastFeeding.tableName
                    + " where " + TableBreastFeeding.tableColumn.babyId + " = '" + babyId
                    + "' and dateTimeText  "+where+" and method != '" + context.getString(R.string.breastValue) +"'";
        }

        Cursor cur = myDataBase.rawQuery(query, null);
        Log.d("cur.getCount()uh", String.valueOf(cur.getCount()));
        return String.valueOf(cur.getCount());
    }

    public static ArrayList<HashMap<String, String>> getWeightData(String babyId) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList();
        arrayList.clear();

        String query ="SELECT * from "+ TableWeight.tableName
                + " where " + TableWeight.tableColumn.babyId + " = '" + babyId+ "' order by addDate desc";

        Cursor cur = myDataBase.rawQuery(query, null);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("nurseName", getNurseName(cur.getString(cur.getColumnIndex(TableWeight.tableColumn.nurseId.toString()))));
                hashlist.put("babyWeight", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.babyWeight.toString())));
                if(cur.getString(cur.getColumnIndex(TableWeight.tableColumn.weightImage.toString()))==null){
                    hashlist.put("weightImage", "");
                }else {
                    hashlist.put("weightImage", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.weightImage.toString())));
                }
                hashlist.put("weightDate", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.weightDate.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.addDate.toString())));
                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getAssessmentData(String babyId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableBabyMonitoring.tableName
                + " where " + TableBabyMonitoring.tableColumn.babyId + " = '" + babyId+"' and isDataComplete = '1'  order by addDate desc";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.uuid.toString())));
                hashlist.put("serverId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.serverId.toString())));
                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.loungeId.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyId.toString())));
                hashlist.put("babyAdmissionId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyAdmissionId.toString())));
                hashlist.put("babyMeasuredWeight", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyMeasuredWeight.toString())));
                hashlist.put("isHeadCircumferenceAvail", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isHeadCircumferenceAvail.toString())));
                hashlist.put("isLengthAvail", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isLengthAvail.toString())));
                hashlist.put("lengthValue", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.lengthValue.toString())));
                hashlist.put("measuringTapeNotAvailReason", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.measuringTapeNotAvailReason.toString())));
                hashlist.put("babyHeadCircumference", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyHeadCircumference.toString())));
                hashlist.put("babyRespiratoryRate", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyRespiratoryRate.toString())));
                hashlist.put("babyTemperature", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyTemperature.toString())));
                hashlist.put("temperatureUnit", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.temperatureUnit.toString())));
                hashlist.put("isPulseOximatoryDeviceAvailable", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isPulseOximatoryDeviceAvailable.toString())));
                hashlist.put("crtKnowledge", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.crtKnowledge.toString())));
                hashlist.put("isCftGreaterThree", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isCftGreaterThree.toString())));
                hashlist.put("type", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.type.toString())));
                hashlist.put("urinePassedIn24Hrs", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.urinePassedIn24Hrs.toString())));
                hashlist.put("stoolPassedIn24Hrs", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.stoolPassedIn24Hrs.toString())));
                hashlist.put("generalCondition", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.generalCondition.toString())));
                hashlist.put("tone", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.tone.toString())));
                hashlist.put("sucking", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.sucking.toString())));
                hashlist.put("apneaOrGasping", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.apneaOrGasping.toString())));
                hashlist.put("grunting", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.grunting.toString())));
                hashlist.put("chestIndrawing", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.chestIndrawing.toString())));
                hashlist.put("color", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.color.toString())));
                hashlist.put("isBleeding", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isBleeding.toString())));
                hashlist.put("bulgingAnteriorFontanel", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.bulgingAnteriorFontanel.toString())));
                hashlist.put("umbilicus", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.umbilicus.toString())));
                hashlist.put("skinPustules", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.skinPustules.toString())));

                String babySpO2 = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babySpO2.toString()));
                hashlist.put("babySpO2", babySpO2 == null ? "" : babySpO2);

                String babyPulseRate = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyPulseRate.toString()));
                hashlist.put("babyPulseRate", babyPulseRate == null ? "" : babyPulseRate);

                String babyTemperature = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyTemperature.toString()));
                hashlist.put("babyTemperature", babyTemperature == null ? "" : babyTemperature);

                String babyRespiratoryRate = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyRespiratoryRate.toString()));
                hashlist.put("babyRespiratoryRate", babyRespiratoryRate == null ? "" : babyRespiratoryRate);

                String isPulseOximatoryDeviceAvailable = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isPulseOximatoryDeviceAvailable.toString()));
                hashlist.put("isPulseOximatoryDeviceAvailable", isPulseOximatoryDeviceAvailable == null ? "" : isPulseOximatoryDeviceAvailable);

                hashlist.put("assessmentNumber", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.assessmentNumber.toString())));
                hashlist.put("staffId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.staffId.toString())));
                hashlist.put("staffSign", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.staffSign.toString())));
                hashlist.put("isThermometerAvailable", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isThermometerAvailable.toString())));
                hashlist.put("formattedDate", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.formattedDate.toString())));
                hashlist.put("isInterestInFeeding", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isInterestInFeeding.toString())));
                hashlist.put("lactation", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.lactation.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.addDate.toString())));
                hashlist.put("nurseName", getNurseName(cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.staffId.toString()))));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getAssessmentDataNew(String babyId,String date) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableBabyMonitoring.tableName
                + " where " + TableBabyMonitoring.tableColumn.babyId + " = '" + babyId
                              +"' and isDataComplete = '1'  and addDate > '"+date+" 00:00:00' and addDate < '"+date+" 23:59:59'  order by addDate desc LIMIT 1";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        Log.d("query",query);

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.uuid.toString())));
                hashlist.put("serverId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.serverId.toString())));
                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.loungeId.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyId.toString())));
                hashlist.put("babyAdmissionId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyAdmissionId.toString())));
                hashlist.put("babyMeasuredWeight", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyMeasuredWeight.toString())));
                hashlist.put("isHeadCircumferenceAvail", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isHeadCircumferenceAvail.toString())));
                hashlist.put("isLengthAvail", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isLengthAvail.toString())));
                hashlist.put("lengthValue", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.lengthValue.toString())));
                hashlist.put("measuringTapeNotAvailReason", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.measuringTapeNotAvailReason.toString())));
                hashlist.put("babyHeadCircumference", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyHeadCircumference.toString())));
                hashlist.put("babyRespiratoryRate", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyRespiratoryRate.toString())));
                hashlist.put("babyTemperature", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyTemperature.toString())));
                hashlist.put("temperatureUnit", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.temperatureUnit.toString())));
                hashlist.put("isPulseOximatoryDeviceAvailable", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isPulseOximatoryDeviceAvailable.toString())));
                hashlist.put("crtKnowledge", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.crtKnowledge.toString())));
                hashlist.put("isCftGreaterThree", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isCftGreaterThree.toString())));
                hashlist.put("type", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.type.toString())));
                hashlist.put("urinePassedIn24Hrs", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.urinePassedIn24Hrs.toString())));
                hashlist.put("stoolPassedIn24Hrs", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.stoolPassedIn24Hrs.toString())));
                hashlist.put("generalCondition", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.generalCondition.toString())));
                hashlist.put("tone", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.tone.toString())));
                hashlist.put("sucking", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.sucking.toString())));
                hashlist.put("apneaOrGasping", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.apneaOrGasping.toString())));
                hashlist.put("grunting", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.grunting.toString())));
                hashlist.put("chestIndrawing", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.chestIndrawing.toString())));
                hashlist.put("color", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.color.toString())));
                hashlist.put("isBleeding", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isBleeding.toString())));
                hashlist.put("bulgingAnteriorFontanel", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.bulgingAnteriorFontanel.toString())));
                hashlist.put("umbilicus", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.umbilicus.toString())));
                hashlist.put("skinPustules", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.skinPustules.toString())));
                hashlist.put("temperatureUnit", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.temperatureUnit.toString())));

                String babySpO2 = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babySpO2.toString()));
                hashlist.put("babySpO2", babySpO2 == null ? "" : babySpO2);

                String babyPulseRate = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyPulseRate.toString()));
                hashlist.put("babyPulseRate", babyPulseRate == null ? "" : babyPulseRate);

                String babyTemperature = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyTemperature.toString()));
                hashlist.put("babyTemperature", babyTemperature == null ? "" : babyTemperature);

                String babyRespiratoryRate = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyRespiratoryRate.toString()));
                hashlist.put("babyRespiratoryRate", babyRespiratoryRate == null ? "" : babyRespiratoryRate);

                String isPulseOximatoryDeviceAvailable = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isPulseOximatoryDeviceAvailable.toString()));
                hashlist.put("isPulseOximatoryDeviceAvailable", isPulseOximatoryDeviceAvailable == null ? "" : isPulseOximatoryDeviceAvailable);

                hashlist.put("temperatureUnit", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.temperatureUnit.toString())));
                hashlist.put("assessmentNumber", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.assessmentNumber.toString())));
                hashlist.put("staffId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.staffId.toString())));
                hashlist.put("staffSign", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.staffSign.toString())));
                hashlist.put("isThermometerAvailable", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isThermometerAvailable.toString())));
                hashlist.put("formattedDate", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.formattedDate.toString())));
                hashlist.put("isInterestInFeeding", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isInterestInFeeding.toString())));
                hashlist.put("lactation", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.lactation.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.addDate.toString())));
                hashlist.put("nurseName", getNurseName(cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.staffId.toString()))));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("arrayList", arrayList.toString());

        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getMedicineData(String babyId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableTreatment.tableName
                + " where " + TableTreatment.tableColumn.babyId + " = '" + babyId+ "' and type ='2' order by addDate desc";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("name", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.treatmentName.toString())));
                hashlist.put("quantity", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.quantity.toString())));
                hashlist.put("givenDate", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.givenDate.toString())));
                hashlist.put("givenTime", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.givenTime.toString())));
                hashlist.put("doctorName", getNurseName(cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.doctorId.toString()))));
                hashlist.put("unit", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.unit.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getMedicineDataViaDate(String babyId,String date) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String where="";

        if(date.equalsIgnoreCase(AppUtils.getCurrentDateFormatted()))
        {
            where = AppUtils.getCurrentDateAsPerHospital();
        }
        else
        {
            where = AppUtils.getHoursDayAsPerHospital(date);
        }

        String query ="SELECT *,  (addDate) AS dateTimeText from "+TableTreatment.tableName
                              + " where " + TableTreatment.tableColumn.babyId + " = '" + babyId
                              + "' and  type ='2' and " +  where + " order by addDate desc";

        /*String query ="SELECT * from "+ TableTreatment.tableName
                + " where " + TableTreatment.tableColumn.babyId + " = '" + babyId+ "' and type ='2' and addDate > '"+date+" 00:00:00"+"' order by addDate desc";*/

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("name", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.treatmentName.toString())));
                hashlist.put("quantity", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.quantity.toString())));
                hashlist.put("givenDate", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.givenDate.toString())));
                hashlist.put("givenTime", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.givenTime.toString())));
                hashlist.put("doctorName", getNurseName(cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.doctorId.toString()))));
                hashlist.put("unit", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.unit.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getVaccinationData(String babyId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableVaccination.tableName
                + " where " + TableVaccination.tableColumn.babyId + " = '" + babyId+ "'";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("vaccinationName", cur.getString(cur.getColumnIndex(TableVaccination.tableColumn.vaccName.toString())));
                hashlist.put("quantity", cur.getString(cur.getColumnIndex(TableVaccination.tableColumn.quantity.toString())));
                hashlist.put("time", cur.getString(cur.getColumnIndex(TableVaccination.tableColumn.time.toString())));
                hashlist.put("date", cur.getString(cur.getColumnIndex(TableVaccination.tableColumn.date.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getInvestigationSearchData(String babyId,String date,String inves) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String whereNew = "";
        if(date.equalsIgnoreCase(AppUtils.getCurrentDateFormatted()))
        {
            whereNew = AppUtils.getCurrentDateAsPerHospital();
        }
        else
        {
            whereNew = AppUtils.getHoursDayAsPerHospital(date);
        }

       /* String query ="SELECT * from "+TableKMC.tableName
                + " where " + TableKMC.tableColumn.babyId + " = '" + babyId+ "' and " +
                " startDate = '"+date+"' and status = '" + 1 +"'";*/

        /*String query ="SELECT *, (modifyDate) AS dateTimeText from "+TableInvestigation.tableName
                              + " where " + TableInvestigation.tableColumn.babyId + " = '" + babyId
                              + "' and " +  where + " and status = '1' ";*/

        String whereCond = "";

        if(!inves.isEmpty()&&!date.isEmpty())
        {
            whereCond =  TableInvestigation.tableColumn.babyId + " = '" + babyId+ "' and investigationName = '"+ inves + "' and = "+ whereNew +" order by addDate desc ";
        }
        else  if(!inves.isEmpty())
        {
            whereCond =  TableInvestigation.tableColumn.babyId + " = '" + babyId+ "' and investigationName = '"+ inves +"' order by addDate desc ";
        }
        else if(!date.isEmpty())
        {
            whereCond =  TableInvestigation.tableColumn.babyId + " = '" + babyId+ "' and "+ whereNew +" order by addDate desc ";
        }
        else
        {
            whereCond = TableInvestigation.tableColumn.babyId + " = '" + babyId+ "' order by addDate desc ";
        }

        String query ="SELECT *, (modifyDate) AS dateTimeText from "+ TableInvestigation.tableName +" where " + whereCond;

        Log.d("query", query);

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.uuid.toString())));
                hashlist.put("investigationName", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.investigationName.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.addDate.toString())));
                hashlist.put("doctorId", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.doctorId.toString())));

                String result = cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.result.toString()));
                hashlist.put("result", result == null ? "" : result);

                String resultImage = cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.resultImage.toString()));
                hashlist.put("resultImage", resultImage == null ? "" : resultImage);

                String nurseComment = cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.nurseComment.toString()));
                hashlist.put("nurseComment", nurseComment == null ? "" : nurseComment);

                String nurseId = cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.nurseId.toString()));
                hashlist.put("nurseId", nurseId == null ? "" : nurseId);

                String sampleTakenByNurse = getNurseName(cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.sampleTakenBy.toString())));
                hashlist.put("sampleTakenByNurse", sampleTakenByNurse == null ? "" : sampleTakenByNurse);

                String nurseName = getNurseName(cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.nurseId.toString())));
                hashlist.put("nurseName", nurseName == null ? "" : nurseName);

                String sampleComment = cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.sampleComment.toString()));
                hashlist.put("sampleComment", sampleComment == null ? "" : sampleComment);

                String sampleImage = cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.sampleImage.toString()));
                hashlist.put("sampleImage", sampleImage == null ? "" : sampleImage);

                String sampleTakenBy = cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.sampleTakenBy.toString()));
                hashlist.put("sampleTakenBy", sampleTakenBy == null ? "" : sampleTakenBy);

                String sampleTakenOn = cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.sampleTakenOn.toString()));
                hashlist.put("sampleTakenOn", sampleTakenOn == null ? "" : sampleTakenOn);

                String doctorComment = cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.doctorComment.toString()));
                hashlist.put("doctorComment", doctorComment == null ? "" : doctorComment);

                String modifyDate = cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.modifyDate.toString()));
                hashlist.put("modifyDate", modifyDate == null ? "" : modifyDate);

                hashlist.put("closeStatus","0");

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getCommentData(String motherOrBabyId,String type) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableComments.tableName
                + " where " + TableComments.tableColumn.motherOrBabyId + " = '" + motherOrBabyId+ "' and type = '"+type+"' and status = '1' order by addDate desc";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("id", cur.getString(cur.getColumnIndex(TableComments.tableColumn.serverId.toString())));
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableComments.tableColumn.uuid.toString())));
                hashlist.put("doctorId", cur.getString(cur.getColumnIndex(TableComments.tableColumn.doctorId.toString())));
                hashlist.put("doctorName", getNurseName(cur.getString(cur.getColumnIndex(TableComments.tableColumn.doctorId.toString()))));
                hashlist.put("doctorImage", getNurseProfile(cur.getString(cur.getColumnIndex(TableComments.tableColumn.doctorId.toString()))));
                hashlist.put("doctorComment", cur.getString(cur.getColumnIndex(TableComments.tableColumn.comment.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableComments.tableColumn.addDate.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getMotherRegDataViaId(String motherId) {

        ArrayList<HashMap<String, String>> CatList = new ArrayList<>();
        CatList.clear();

        String query ="SELECT * from "+ TableMotherRegistration.tableName +
                " where "+ TableMotherRegistration.tableColumn.loungeId +" = '"+ AppSettings.getString(AppSettings.loungeId) +"' and "
                + TableMotherRegistration.tableColumn.motherId + " = '" +motherId +"'";

        Log.d("getMotherRegDataViaId",query);

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {
                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.uuid.toString())));
                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.loungeId.toString())));
                hashlist.put("motherId", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherId.toString())));
                hashlist.put("motherName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherName.toString())));
                hashlist.put("isMotherAdmitted", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.isMotherAdmitted.toString())));
                hashlist.put("notAdmittedReason", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.reasonForNotAdmitted.toString())));
                hashlist.put("motherPicture", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherPicture.toString())));
                hashlist.put("motherMCTSNumber", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherMCTSNumber.toString())));
                hashlist.put("motherAadharNumber", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherAadharNumber.toString())));
                hashlist.put("motherDob", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherDob.toString())));
                hashlist.put("motherAge", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherAge.toString())));
                hashlist.put("motherEducation", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherEducation.toString())));
                hashlist.put("motherCaste", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherCaste.toString())));
                hashlist.put("motherReligion", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherReligion.toString())));
                hashlist.put("motherMobileNumber", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherMoblieNo.toString())));
                hashlist.put("fatherName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.fatherName.toString())));
                hashlist.put("fatherAadharNumber", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.fatherAadharNumber.toString())));
                hashlist.put("fatherMobileNumber", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.fatherMoblieNo.toString())));
                hashlist.put("rationCardType", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.rationCardType.toString())));
                hashlist.put("guardianName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.guardianName.toString())));
                hashlist.put("guardianNumber", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.guardianNumber.toString())));
                hashlist.put("guardianRelation", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.guardianRelation.toString())));
                hashlist.put("presentCountry", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentCountry.toString())));
                hashlist.put("presentState", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentState.toString())));
                hashlist.put("presentResidenceType", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentResidenceType.toString())));
                hashlist.put("presentAddress", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentAddress.toString())));
                hashlist.put("presentVillageName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentVillageName.toString())));
                hashlist.put("presentBlockName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentBlockName.toString())));
                hashlist.put("presentDistrictName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentDistrictName.toString())));
                hashlist.put("permanentResidenceType", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentResidenceType.toString())));
                hashlist.put("permanentCountry", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentCountry.toString())));
                hashlist.put("permanentState", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentState.toString())));
                hashlist.put("permanentAddress", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentAddress.toString())));
                hashlist.put("permanentVillageName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentVillageName.toString())));
                hashlist.put("permanentBlockName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentBlockName.toString())));
                hashlist.put("permanentDistrictName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentDistrictName.toString())));
                hashlist.put("presentPinCode", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentPinCode.toString())));
                hashlist.put("permanentPinCode", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentPinCode.toString())));
                hashlist.put("ashaID", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.ashaID.toString())));
                hashlist.put("ashaName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.ashaName.toString())));
                hashlist.put("ashaNumber", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.ashaNumber.toString())));
                hashlist.put("presentAddressNearBy", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentAddressNearBy.toString())));
                hashlist.put("permanentAddressNearBy", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentAddressNearBy.toString())));
                hashlist.put("staffId", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.staffId.toString())));
                hashlist.put("type", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.type.toString())));
                hashlist.put("organisationName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.organisationName.toString())));
                hashlist.put("organisationNumber", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.organisationNumber.toString())));
                hashlist.put("organisationAddress", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.organisationAddress.toString())));
                hashlist.put("para", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.para.toString())));
                hashlist.put("gravida", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.gravida.toString())));
                hashlist.put("abortion", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.abortion.toString())));
                hashlist.put("live", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.live.toString())));
                hashlist.put("multipleBirth", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.multipleBirth.toString())));
                hashlist.put("admittedSign", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.admittedSign.toString())));
                hashlist.put("motherLmpDate", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherLmpDate.toString())));
                hashlist.put("relationWithChild", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.guardianRelation.toString())));
                hashlist.put("relationWithChildOther", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.relationWithChildOther.toString())));
                hashlist.put("motherDeliveryPlace", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherDeliveryPlace.toString())));
                hashlist.put("motherDeliveryDistrict", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherDeliveryDistrict.toString())));
                hashlist.put("deliveryFacilityId", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.facilityID.toString())));
                hashlist.put("isDataSynced", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.isDataSynced.toString())));
                hashlist.put("syncTime", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.syncTime.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.addDate.toString())));

                hashlist.put("typeOfBorn", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.typeOfBorn.toString())));
                hashlist.put("typeOfOutBorn", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.typeOfOutBorn.toString())));
                hashlist.put("infantComingFrom", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.infantComingFrom.toString())));

                hashlist.put("modifyDate", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.modifyDate.toString())));
                hashlist.put("status", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.status.toString())));
                hashlist.put("sameAddress", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.sameaddress.toString())));
                hashlist.put("motherWeight", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherWeight.toString())));
                hashlist.put("ageOfMarriage", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.ageOfMarriage.toString())));
                hashlist.put("consanguinity", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.consanguinity.toString())));
                hashlist.put("birthSpacing", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.birthSpacing.toString())));
                hashlist.put("estimatedDateOfDelivery", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.estimatedDateOfDelivery.toString())));

                CatList.add(hashlist);
                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("mother-cur.getCount()", String.valueOf(CatList));

        return CatList;
    }

    public static ArrayList<HashMap<String, String>> getIncompleteMotherMonitoringData(String motherId) {

        ArrayList<HashMap<String, String>> CatList = new ArrayList();
        CatList.clear();

        String query ="SELECT * from "+ TableMotherMonitoring.tableName +
                " where "+ TableMotherMonitoring.tableColumn.loungeId +" = '"+ AppSettings.getString(AppSettings.loungeId) +"' and "
                + TableMotherMonitoring.tableColumn.motherId
                + " = '" +motherId +"' and isDataComplete = '0'  order by addDate desc LIMIT 1";

        Log.d("incompleteMotMonitoring",query);

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {
                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.uuid.toString())));
                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.loungeId.toString())));
                hashlist.put("motherId", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherId.toString())));
                hashlist.put("motherAdmissionId", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherAdmissionId.toString())));
                hashlist.put("motherTemperature", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherTemperature.toString())));
                hashlist.put("motherSystolicBP", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherSystolicBP.toString())));
                hashlist.put("motherDiastolicBP", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherDiastolicBP.toString())));
                hashlist.put("motherPulse", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherPulse.toString())));
                hashlist.put("motherUterineTone", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherUterineTone.toString())));
                hashlist.put("episitomyCondition", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.episitomyCondition.toString())));
                hashlist.put("newSanitoryPadCheck", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.newSanitoryPadCheck.toString())));
                hashlist.put("motherUrinationAfterDelivery", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherUrinationAfterDelivery.toString())));
                hashlist.put("sanitoryPadStatus", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.sanitoryPadStatus.toString())));
                hashlist.put("isSanitoryPadStink", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.isSanitoryPadStink.toString())));
                hashlist.put("admittedSign", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.admittedSign.toString())));
                hashlist.put("staffId", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.staffId.toString())));
                hashlist.put("other", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.other.toString())));
                hashlist.put("padPicture", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.padPicture.toString())));
                hashlist.put("assesmentNumber", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.assesmentNumber.toString())));
                hashlist.put("syncTime", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.syncTime.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.addDate.toString())));
                hashlist.put("modifyDate", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.modifyDate.toString())));
                hashlist.put("status", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.status.toString())));
                hashlist.put("temperatureUnit", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.temperatureUnit.toString())));

                CatList.add(hashlist);
                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("cur.getCount()", String.valueOf(CatList));

        return CatList;
    }

    public static ArrayList<HashMap<String, String>> getAllMotherMonitoringData(String motherId) {

        ArrayList<HashMap<String, String>> CatList = new ArrayList();
        CatList.clear();

        String query ="SELECT * from "+ TableMotherMonitoring.tableName +
                " where "+ TableMotherMonitoring.tableColumn.loungeId +" = '"+ AppSettings.getString(AppSettings.loungeId) +"' and "
                + TableMotherMonitoring.tableColumn.motherId
                + " = '" +motherId +"'  order by addDate desc LIMIT 1";

        Log.d("incompleteMotMonitoring",query);

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {
                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.uuid.toString())));
                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.loungeId.toString())));
                hashlist.put("motherId", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherId.toString())));
                hashlist.put("motherAdmissionId", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherAdmissionId.toString())));
                hashlist.put("motherTemperature", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherTemperature.toString())));
                hashlist.put("motherSystolicBP", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherSystolicBP.toString())));
                hashlist.put("motherDiastolicBP", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherDiastolicBP.toString())));
                hashlist.put("motherPulse", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherPulse.toString())));
                hashlist.put("motherUterineTone", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherUterineTone.toString())));
                hashlist.put("episitomyCondition", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.episitomyCondition.toString())));
                hashlist.put("newSanitoryPadCheck", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.newSanitoryPadCheck.toString())));
                hashlist.put("motherUrinationAfterDelivery", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherUrinationAfterDelivery.toString())));
                hashlist.put("sanitoryPadStatus", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.sanitoryPadStatus.toString())));
                hashlist.put("isSanitoryPadStink", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.isSanitoryPadStink.toString())));
                hashlist.put("admittedSign", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.admittedSign.toString())));
                hashlist.put("staffId", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.staffId.toString())));
                hashlist.put("other", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.other.toString())));
                hashlist.put("padPicture", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.padPicture.toString())));
                hashlist.put("assesmentNumber", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.assesmentNumber.toString())));
                hashlist.put("syncTime", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.syncTime.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.addDate.toString())));
                hashlist.put("modifyDate", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.modifyDate.toString())));
                hashlist.put("status", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.status.toString())));
                hashlist.put("temperatureUnit", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.temperatureUnit.toString())));

                CatList.add(hashlist);
                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("cur.getCount()", String.valueOf(CatList));

        return CatList;
    }

    public static ArrayList<HashMap<String, String>> getMotherMonitoringData(String motherAdmissionId) {

        ArrayList<HashMap<String, String>> CatList = new ArrayList();
        CatList.clear();

        String query ="SELECT * from "+ TableMotherPastInformation.tableName + " where "
                + TableMotherPastInformation.tableColumn.loungeId +" = '"+ AppSettings.getString(AppSettings.loungeId) +"' and "
                + TableMotherPastInformation.tableColumn.motherAdmissionId + " = '" +motherAdmissionId +"'";

        Log.d("query",query);

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {
                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableMotherPastInformation.tableColumn.uuid.toString())));
                hashlist.put("json", cur.getString(cur.getColumnIndex(TableMotherPastInformation.tableColumn.json.toString())));

                CatList.add(hashlist);
                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("CatList", String.valueOf(CatList));

        return CatList;
    }

    public static ArrayList<HashMap<String, String>> getMotherMonitoringDataViaId(String motherId) {

        ArrayList<HashMap<String, String>> CatList = new ArrayList();
        CatList.clear();

        String query ="SELECT * from "+ TableMotherMonitoring.tableName +
                " where "+ TableMotherMonitoring.tableColumn.loungeId +" = '"+ AppSettings.getString(AppSettings.loungeId) +"' and "
                + TableMotherMonitoring.tableColumn.motherId
                + " = '" +motherId +"' and isDataComplete = '1'  order by addDate desc";

        Log.d("query",query);

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {
                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.uuid.toString())));
                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.loungeId.toString())));
                hashlist.put("motherId", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherId.toString())));
                hashlist.put("motherAdmissionId", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherAdmissionId.toString())));
                hashlist.put("motherTemperature", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherTemperature.toString())));
                hashlist.put("motherSystolicBP", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherSystolicBP.toString())));
                hashlist.put("motherDiastolicBP", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherDiastolicBP.toString())));
                hashlist.put("motherPulse", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherPulse.toString())));
                hashlist.put("motherUterineTone", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherUterineTone.toString())));
                hashlist.put("episitomyCondition", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.episitomyCondition.toString())));
                hashlist.put("newSanitoryPadCheck", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.newSanitoryPadCheck.toString())));
                hashlist.put("motherUrinationAfterDelivery", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherUrinationAfterDelivery.toString())));
                hashlist.put("sanitoryPadStatus", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.sanitoryPadStatus.toString())));
                hashlist.put("isSanitoryPadStink", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.isSanitoryPadStink.toString())));
                hashlist.put("admittedSign", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.admittedSign.toString())));
                hashlist.put("staffId", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.staffId.toString())));
                hashlist.put("other", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.other.toString())));
                hashlist.put("padPicture", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.padPicture.toString())));
                hashlist.put("assesmentNumber", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.assesmentNumber.toString())));
                hashlist.put("syncTime", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.syncTime.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.addDate.toString())));
                hashlist.put("modifyDate", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.modifyDate.toString())));
                hashlist.put("status", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.status.toString())));
                hashlist.put("temperatureUnit", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.temperatureUnit.toString())));
                hashlist.put("nurseName", getNurseName(cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.staffId.toString()))));

                CatList.add(hashlist);
                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("cur.getCount()", String.valueOf(CatList));

        return CatList;
    }

    public static int getMotherAssessmentNumber(String motherId) {

        int count=0;

        String query ="SELECT * from "+ TableMotherMonitoring.tableName + " where motherId  = '" + motherId + "'";

        Log.d("getAssessmentNumber",query);

        Cursor cur = myDataBase.rawQuery(query, null);

        count = cur.getCount();

        Log.d("cur.getCount()", String.valueOf(count));

        count = count+1;

        return count;
    }


    public static int getBabyAssessmentNumber(String babyId) {

        int count=0;

        String query ="SELECT * from "+ TableBabyMonitoring.tableName + " where babyId  = '" + babyId + "'";

        Log.d("getAssessmentNumber",query);

        Cursor cur = myDataBase.rawQuery(query, null);

        count = cur.getCount();

        Log.d("cur.getCount()", String.valueOf(count));

        count = count+1;

        return count;
    }


    public static String getMotherMoniUUID(String motherId) {

        String result="";

        String query = "SELECT uuid from "+ TableMotherMonitoring.tableName
                + " where " + TableMotherMonitoring.tableColumn.motherId + " = '" + motherId
                +"' and isDataComplete = '0' " ;

        Cursor cur = myDataBase.rawQuery(query , null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                result=cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.uuid.toString()));
                cur.moveToNext();
            }
        }
        cur.close();
        return result;
    }

    public static ArrayList<HashMap<String, String>> getFeedingDataToSync(String babyId,String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableBreastFeeding.tableName
                + " where " + TableBreastFeeding.tableColumn.loungeId + " = '" + loungeId+ "' and babyId  = '"+ babyId + "' and isDataSynced IS NULL ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query.Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.loungeId.toString())));
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.uuid.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.babyId.toString())));
                hashlist.put("feedTime", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.feedTime.toString())));
                hashlist.put("breastFeedDuration", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.duration.toString())));
                hashlist.put("method", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.method.toString())));
                hashlist.put("milkQuantity", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.quantity.toString())));
                hashlist.put("fluid", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.fluid.toString())));
                hashlist.put("specify", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.specify.toString())));
                hashlist.put("date", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.date.toString())));
                hashlist.put("nurseId", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.nurseId.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.addDate.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getDoctorRoundDataToSync(String babyId,String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableDoctorRound.tableName
                + " where " + TableDoctorRound.tableColumn.loungeId + " = '" + loungeId+ "' and babyId  = '"+ babyId + "' and isDataSynced IS NULL ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query.Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableDoctorRound.tableColumn.loungeId.toString())));
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableDoctorRound.tableColumn.uuid.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableDoctorRound.tableColumn.babyId.toString())));
                hashlist.put("doctorId", cur.getString(cur.getColumnIndex(TableDoctorRound.tableColumn.doctorId.toString())));
                hashlist.put("signature", cur.getString(cur.getColumnIndex(TableDoctorRound.tableColumn.signature.toString())));
                hashlist.put("json", cur.getString(cur.getColumnIndex(TableDoctorRound.tableColumn.json.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableDoctorRound.tableColumn.addDate.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getSSCDateToSync(String babyId,String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+TableKMC.tableName
                + " where " + TableKMC.tableColumn.loungeId + " = '" + loungeId+ "' and babyId  = '"+ babyId + "' and isDataSynced = '0' ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("getSSCDateToSync-query", query);
        Log.d("getSSCDateToSync-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.loungeId.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.babyId.toString())));
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.uuid.toString())));
                hashlist.put("startDate", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startDate.toString())));
                hashlist.put("endDate", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.endDate.toString())));
                hashlist.put("provider", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.provider.toString())));
                hashlist.put("startTime", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startTime.toString())));
                hashlist.put("endTime", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.endTime.toString())));
                hashlist.put("nurseId", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.nurseId.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.addDate.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getWeightToSync(String babyId,String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableWeight.tableName
                + " where " + TableWeight.tableColumn.loungeId + " = '" + loungeId+ "' and babyId  = '"+ babyId + "' and isDataSynced IS NULL ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("getSSCDateToSync-query", query);
        Log.d("getSSCDateToSync-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.uuid.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.babyId.toString())));
                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.loungeId.toString())));
                hashlist.put("nurseId", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.nurseId.toString())));
                hashlist.put("babyWeight", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.babyWeight.toString())));
                hashlist.put("weightDate", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.weightDate.toString())));
                hashlist.put("reason", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.weighingReason.toString())));
                hashlist.put("isDeviceAvailAndWorking", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.isWeighingAvail.toString())));
                hashlist.put("weightImage", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.weightImage.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.addDate.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getBabyIdToSync() {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT bR.uuid,bR.babyId,bR.motherId  from "+ TableBabyRegistration.tableName +" as bR " +
                " INNER JOIN  "+ TableBabyAdmission.tableName +" as bA" +
                " on ( bR.babyId = bA.babyId )" +
                " where bA.status = '1'  GROUP BY bR.babyId  ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("getBabyIdToSync-query", query);
        Log.d("getBabyIdToSync-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.uuid.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyId.toString())));
                hashlist.put("motherName", getMotherName(cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.motherId.toString()))));
                hashlist.put("motherId",cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.motherId.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("query-arrayList", String.valueOf(arrayList));

        return arrayList;
    }

    public static String getUUID(String babyId) {

        String result="";

        String query = "SELECT uuid from "+ TableBabyRegistration.tableName
                + " where " + TableBabyRegistration.tableColumn.babyId + " = '" + babyId
                +"' order by addDate desc LIMIT 1 " ;

        Cursor cur = myDataBase.rawQuery(query , null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                result=cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.uuid.toString()));
                cur.moveToNext();
            }
        }
        cur.close();
        return result;
    }

    public static ArrayList<HashMap<String, String>> getBabyIdToSyncUpdates(String babyId,String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableBabyRegistration.tableName +
                " where isDataSynced = '2'  and babyId  = '" + babyId + "'";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.uuid.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyId.toString())));
                hashlist.put("motherId", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.motherId.toString())));
                hashlist.put("babyMCTSNumber", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyMCTSNumber.toString())));
                hashlist.put("deliveryDate", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.deliveryDate.toString())));
                hashlist.put("deliveryTime", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.deliveryTime.toString())));
                hashlist.put("babyGender", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyGender.toString())));
                hashlist.put("deliveryType", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.deliveryType.toString())));
                hashlist.put("babyWeight", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyWeight.toString())));
                hashlist.put("birthWeightAvail", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.birthWeightAvail.toString())));
                hashlist.put("reason", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.reason.toString())));
                hashlist.put("babyCryAfterBirth", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyCryAfterBirth.toString())));
                hashlist.put("babyNeedBreathingHelp", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyNeedBreathingHelp.toString())));
                hashlist.put("registrationDateTime", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.registrationDateTime.toString())));
                hashlist.put("babyPhoto", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyPhoto.toString())));
                hashlist.put("firstTimeFeed", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.firstTimeFeed.toString())));
                hashlist.put("isDataSynced", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.isDataSynced.toString())));
                hashlist.put("babyFileId", getBabyFileId(cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyId.toString()))));
                hashlist.put("typeOfBorn", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.typeOfBorn.toString())));
                hashlist.put("typeOfOutBorn", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.typeOfOutBorn.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.addDate.toString())));
                hashlist.put("wasApgarScoreRecorded", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.wasApgarScoreRecorded.toString())));
                hashlist.put("apgarScore", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.apgarScore.toString())));
                hashlist.put("vitaminKGiven", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.vitaminKGiven.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("query-arrayList", String.valueOf(arrayList));

        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getBabyCounsellingUpdates(String babyId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableBabyAdmission.tableName +
                              " where babyId  = '" + babyId + "'";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.uuid.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.babyId.toString())));
                hashlist.put("kmcPosition", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.kmcPosition.toString())));
                hashlist.put("kmcMonitoring", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.kmcMonitoring.toString())));
                hashlist.put("kmcNutrition", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.kmcNutrition.toString())));
                hashlist.put("kmcRespect", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.kmcRespect.toString())));
                hashlist.put("kmcHygiene", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.kmcHygiene.toString())));
                hashlist.put("whatisKmc", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.whatisKmc.toString())));
                hashlist.put("modifyDate", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.modifyDate.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("query-arrayList", String.valueOf(arrayList));

        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getMedicationDataToSync(String babyId,String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableTreatment.tableName
                + " where " + TableTreatment.tableColumn.loungeId + " = '" + loungeId+ "' and babyId  = '"+ babyId + "' and isDataSynced = '0' ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.loungeId.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.babyId.toString())));
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.uuid.toString())));
                hashlist.put("treatmentName", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.treatmentName.toString())));
                hashlist.put("quantity", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.quantity.toString())));
                hashlist.put("nurseId", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.nurseId.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.addDate.toString())));
                hashlist.put("comment", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.comment.toString())));
                hashlist.put("unit", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.unit.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getInvestSampleDataToSync(String babyId,String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        arrayList.clear();

       /* String query ="SELECT * from "+ TableInvestigation.tableName
                + " where " + TableInvestigation.tableColumn.loungeId + " = '" + loungeId+ "' and babyId  = '"
                + babyId + "' and isDataSynced = '0' and sampleTakenOn != ' ' and result = ' ' ";*/

        String query ="SELECT * from "+ TableInvestigation.tableName
                + " where " + TableInvestigation.tableColumn.loungeId + " = '" + loungeId+ "' and babyId  = '"
                + babyId + "' and isDataSynced = '0' and status = '2' ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.loungeId.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.babyId.toString())));
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.uuid.toString())));
                hashlist.put("sampleTakenOn", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.sampleTakenOn.toString())));
                hashlist.put("sampleTakenBy", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.sampleTakenBy.toString())));
                hashlist.put("sampleImage", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.sampleImage.toString())));
                hashlist.put("sampleComment", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.sampleComment.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getInvestDataToSync(String babyId,String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableInvestigation.tableName
                + " where " + TableInvestigation.tableColumn.loungeId + " = '" + loungeId
                + "' and babyId  = '"+ babyId + "' and isDataSynced = '0' and status = '3' ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.loungeId.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.babyId.toString())));
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.uuid.toString())));
                hashlist.put("investigationName", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.investigationName.toString())));
                hashlist.put("nurseComment", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.nurseComment.toString())));
                hashlist.put("result", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.result.toString())));
                hashlist.put("resultImage", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.resultImage.toString())));
                hashlist.put("nurseId", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.nurseId.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.addDate.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getVaccinationDataToSync(String babyId,String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableVaccination.tableName
                + " where " + TableVaccination.tableColumn.loungeId + " = '" + loungeId+ "' and babyId  = '"+ babyId + "' and isDataSynced = '0' ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableVaccination.tableColumn.loungeId.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableVaccination.tableColumn.babyId.toString())));
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableVaccination.tableColumn.uuid.toString())));
                hashlist.put("vaccName", cur.getString(cur.getColumnIndex(TableVaccination.tableColumn.vaccName.toString())));
                hashlist.put("quantity", cur.getString(cur.getColumnIndex(TableVaccination.tableColumn.quantity.toString())));
                hashlist.put("date", cur.getString(cur.getColumnIndex(TableVaccination.tableColumn.date.toString())));
                hashlist.put("time", cur.getString(cur.getColumnIndex(TableVaccination.tableColumn.time.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableVaccination.tableColumn.addDate.toString())));
                hashlist.put("nurseId", cur.getString(cur.getColumnIndex(TableVaccination.tableColumn.nurseId.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static String getBabyFileId(String babyId) {

        String result="";

        String query = "SELECT babyFileId from "+ TableBabyAdmission.tableName
                + " where " + TableBabyAdmission.tableColumn.babyId + " = '" + babyId
                +"' order by addDate desc LIMIT 1 " ;

        Cursor cur = myDataBase.rawQuery(query , null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                result=cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.babyFileId.toString()));
                cur.moveToNext();
            }
        }
        cur.close();
        return result;
    }

    public static ArrayList<HashMap<String, String>> getBabyAssessToSync(String babyId,String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query = "SELECT * from "+ TableBabyMonitoring.tableName + " where "+ TableBabyMonitoring.tableColumn.babyId + " = '" + babyId
                +"' and isDataSynced = '2' and  isDataComplete = '1' " ;

        Cursor cur = myDataBase.rawQuery(query , null);

        Log.d("query-query", query);
        Log.d("query-Count()", String.valueOf(cur.getCount()));
        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.uuid.toString())));
                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.loungeId.toString())));
                hashlist.put("serverId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.serverId.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyId.toString())));
                hashlist.put("babyAdmissionId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyAdmissionId.toString())));
                hashlist.put("babyMeasuredWeight", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyMeasuredWeight.toString())));
                hashlist.put("babyHeadCircumference", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyHeadCircumference.toString())));
                hashlist.put("babyRespiratoryRate", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyRespiratoryRate.toString())));
                hashlist.put("babyTemperature", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyTemperature.toString())));
                /*hashlist.put("babyHavePustulesOrBoils", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyHavePustulesOrBoils.toString())));
                hashlist.put("locationOfPustulesOrBoils", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.locationOfPustulesOrBoils.toString())));
                hashlist.put("sizeOfPustulesOrBoils", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.sizeOfPustulesOrBoils.toString())));
                hashlist.put("numberOfPustulesOrBoils", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.numberOfPustulesOrBoils.toString())));*/
                hashlist.put("isPulseOximatoryDeviceAvailable", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isPulseOximatoryDeviceAvailable.toString())));
                hashlist.put("type", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.type.toString())));
                //hashlist.put("babyOtherDangerSign", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyOtherDangerSign.toString())));
                hashlist.put("babySpO2", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babySpO2.toString())));
                hashlist.put("babyPulseRate", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyPulseRate.toString())));
                //hashlist.put("other", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.other.toString())));
                /*hashlist.put("motherBreastPain", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.motherBreastPain.toString())));
                hashlist.put("motherBreastStatus", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.motherBreastStatus.toString())));
                hashlist.put("motherBreastCondition", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.motherBreastCondition.toString())));
                hashlist.put("babyMilkConsumption1", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyMilkConsumption1.toString())));
                hashlist.put("babyMilkConsumption2", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyMilkConsumption2.toString())));
                hashlist.put("babyMilkConsumption3", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyMilkConsumption3.toString())));
                hashlist.put("skinColor", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.skinColor.toString())));*/
                hashlist.put("isHeadCircumferenceAvail", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isHeadCircumferenceAvail.toString())));
                hashlist.put("measuringTapeNotAvailReason", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.measuringTapeNotAvailReason.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.addDate.toString())));
                hashlist.put("json", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.json.toString())));

                /*String urination = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.urinationAfterLastAssesment.toString()));
                hashlist.put("urinationAfterLastAssesment", urination == null ? "" : urination);

                String stool = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.stoolAfterLastAssesment.toString()));
                hashlist.put("stoolAfterLastAssesment",  stool == null ? "" : stool);*/

                hashlist.put("staffId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.staffId.toString())));

                String staffSign = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.staffSign.toString()));
                hashlist.put("staffSign", staffSign == null ? "" : staffSign);

                //String assessmentImage = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.tempImage.toString()));
                //hashlist.put("tempImage",  assessmentImage == null ? "" : assessmentImage);

                String isThermometerAvailable = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isThermometerAvailable.toString()));
                hashlist.put("isThermometerAvailable", isThermometerAvailable == null ? "" : isThermometerAvailable);
                //hashlist.put("thermoReason", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.thermoReason.toString())));
                hashlist.put("temperatureUnit", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.temperatureUnit.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static String getDelDate(String babyId) {

        String result="";

        String query = "SELECT deliveryDate from "+ TableBabyRegistration.tableName
                + " where " + TableBabyRegistration.tableColumn.babyId + " = '" + babyId
                +"' order by addDate desc LIMIT 1 " ;

        Cursor cur = myDataBase.rawQuery(query , null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                result=cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.deliveryDate.toString()));
                cur.moveToNext();
            }
        }
        cur.close();
        return result;
    }

    public static String getAddDate(String babyId) {

        String result="";

        String query = "SELECT addDate from "+ TableBabyRegistration.tableName
                + " where " + TableBabyRegistration.tableColumn.babyId + " = '" + babyId
                +"' order by addDate desc LIMIT 1 " ;

        Cursor cur = myDataBase.rawQuery(query , null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                result=cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.addDate.toString()));
                cur.moveToNext();
            }
        }
        cur.close();
        return result;
    }


    public static ArrayList<HashMap<String, String>> getMotherIdToSync(String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT mR.uuid,mR.motherId  from "+ TableMotherRegistration.tableName +" as mR " +
                " INNER JOIN  "+ TableMotherAdmission.tableName +" as mA" +
                " on ( mR.motherId = mA.motherId )" +
                " where mA.status = '1' and mR.loungeId = "+loungeId +
                " GROUP BY mR.motherId";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("getMotherIdToSync-query", query);
        Log.d("getMotherIdToSync-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.uuid.toString())));
                hashlist.put("motherId", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherId.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("query-arrayList", String.valueOf(arrayList));

        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getMotherIdToSyncUpdates(String motherId,String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableMotherRegistration.tableName +
                " where isDataSynced = '2'  and loungeId = '"+loungeId+"' and motherId  = '" + motherId + "'";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.uuid.toString())));
                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.loungeId.toString())));
                hashlist.put("motherId", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherId.toString())));
                hashlist.put("motherName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherName.toString())));
                hashlist.put("isMotherAdmitted", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.isMotherAdmitted.toString())));
                hashlist.put("reasonForNotAdmitted", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.reasonForNotAdmitted.toString())));
                hashlist.put("motherPicture", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherPicture.toString())));
                hashlist.put("motherMCTSNumber", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherMCTSNumber.toString())));
                hashlist.put("motherAadharNumber", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherAadharNumber.toString())));
                hashlist.put("motherDob", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherDob.toString())));
                hashlist.put("motherAge", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherAge.toString())));
                hashlist.put("motherEducation", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherEducation.toString())));
                hashlist.put("motherCaste", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherCaste.toString())));
                hashlist.put("motherReligion", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherReligion.toString())));
                hashlist.put("motherMoblieNo", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherMoblieNo.toString())));
                hashlist.put("fatherName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.fatherName.toString())));
                hashlist.put("fatherAadharNumber", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.fatherAadharNumber.toString())));
                hashlist.put("fatherMoblieNo", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.fatherMoblieNo.toString())));
                hashlist.put("rationCardType", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.rationCardType.toString())));
                hashlist.put("guardianName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.guardianName.toString())));
                hashlist.put("guardianNumber", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.guardianNumber.toString())));
                hashlist.put("guardianRelation", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.guardianRelation.toString())));
                hashlist.put("presentCountry", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentCountry.toString())));
                hashlist.put("presentState", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentState.toString())));
                hashlist.put("presentResidenceType", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentResidenceType.toString())));
                hashlist.put("presentAddress", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentAddress.toString())));
                hashlist.put("presentVillageName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentVillageName.toString())));
                hashlist.put("presentBlockName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentBlockName.toString())));
                hashlist.put("presentDistrictName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentDistrictName.toString())));
                hashlist.put("permanentResidenceType", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentResidenceType.toString())));
                hashlist.put("permanentCountry", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentCountry.toString())));
                hashlist.put("permanentState", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentState.toString())));
                hashlist.put("permanentAddress", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentAddress.toString())));
                hashlist.put("permanentVillageName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentVillageName.toString())));
                hashlist.put("permanentBlockName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentBlockName.toString())));
                hashlist.put("permanentDistrictName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentDistrictName.toString())));
                hashlist.put("presentPinCode", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentPinCode.toString())));
                hashlist.put("permanentPinCode", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentPinCode.toString())));
                hashlist.put("ashaID", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.ashaID.toString())));
                hashlist.put("ashaName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.ashaName.toString())));
                hashlist.put("ashaNumber", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.ashaNumber.toString())));
                hashlist.put("presentAddressNearBy", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentAddressNearBy.toString())));
                hashlist.put("permanentAddressNearBy", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentAddressNearBy.toString())));
                hashlist.put("staffId", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.staffId.toString())));
                hashlist.put("type", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.type.toString())));
                hashlist.put("organisationName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.organisationName.toString())));
                hashlist.put("organisationNumber", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.organisationNumber.toString())));
                hashlist.put("organisationAddress", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.organisationAddress.toString())));
                hashlist.put("para", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.para.toString())));
                hashlist.put("gravida", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.gravida.toString())));
                hashlist.put("abortion", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.abortion.toString())));
                hashlist.put("live", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.live.toString())));
                hashlist.put("multipleBirth", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.multipleBirth.toString())));
                hashlist.put("admittedSign", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.admittedSign.toString())));
                hashlist.put("motherLmpDate", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherLmpDate.toString())));
                hashlist.put("motherDeliveryPlace", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherDeliveryPlace.toString())));
                hashlist.put("motherDeliveryDistrict", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherDeliveryDistrict.toString())));
                hashlist.put("facilityId", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.facilityID.toString())));
                hashlist.put("isDataSynced", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.isDataSynced.toString())));
                hashlist.put("syncTime", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.syncTime.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.addDate.toString())));
                hashlist.put("modifyDate", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.modifyDate.toString())));
                hashlist.put("status", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.status.toString())));
                //hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.addDate.toString())));
                //hashlist.put("hospitalRN", getMotherFileId(cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherId.toString()))));
                hashlist.put("sameAddress", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.sameaddress.toString())));
                hashlist.put("motherWeight", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherWeight.toString())));
                hashlist.put("ageOfMarriage", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.ageOfMarriage.toString())));
                hashlist.put("consanguinity", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.consanguinity.toString())));
                hashlist.put("birthSpacing", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.birthSpacing.toString())));
                hashlist.put("estimatedDateOfDelivery", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.estimatedDateOfDelivery.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("query-arrayList", String.valueOf(arrayList));

        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getMotherAssessmentToSync(String motherId,String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableMotherMonitoring.tableName +
                " where isDataSynced = '2'  and isDataComplete = '1' and loungeId = '"+loungeId+"' and motherId  = '" + motherId + "'";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.uuid.toString())));
                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.loungeId.toString())));
                hashlist.put("motherId", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherId.toString())));
                hashlist.put("motherAdmissionId", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherAdmissionId.toString())));
                hashlist.put("motherTemperature", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherTemperature.toString())));
                hashlist.put("motherSystolicBP", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherSystolicBP.toString())));
                hashlist.put("motherDiastolicBP", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherDiastolicBP.toString())));
                hashlist.put("motherPulse", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherPulse.toString())));
                hashlist.put("motherUterineTone", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherUterineTone.toString())));
                hashlist.put("episitomyCondition", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.episitomyCondition.toString())));
                hashlist.put("newSanitoryPadCheck", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.newSanitoryPadCheck.toString())));
                hashlist.put("motherUrinationAfterDelivery", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherUrinationAfterDelivery.toString())));
                hashlist.put("sanitoryPadStatus", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.sanitoryPadStatus.toString())));
                hashlist.put("isSanitoryPadStink", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.isSanitoryPadStink.toString())));
                hashlist.put("admittedSign", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.admittedSign.toString())));
                hashlist.put("staffId", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.staffId.toString())));
                hashlist.put("other", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.other.toString())));
                hashlist.put("padPicture", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.padPicture.toString())));
                hashlist.put("assesmentNumber", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.assesmentNumber.toString())));
                hashlist.put("syncTime", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.syncTime.toString())));
                //hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.addDate.toString())));
                hashlist.put("modifyDate", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.modifyDate.toString())));
                hashlist.put("status", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.status.toString())));
                hashlist.put("padNotChangeReason", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.padNotChangeReason.toString())));
                hashlist.put("temperatureUnit", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.temperatureUnit.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.addDate.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("query-arrayList", String.valueOf(arrayList));

        return arrayList;
    }

    public static String getMotherFileId(String motherId) {

        String result="";

        String query = "SELECT hospitalRegistrationNumber from "+ TableMotherAdmission.tableName
                + " where " + TableMotherAdmission.tableColumn.motherId + " = '" + motherId
                +"' order by addDate desc LIMIT 1 " ;

        Cursor cur = myDataBase.rawQuery(query , null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                result=cur.getString(cur.getColumnIndex(TableMotherAdmission.tableColumn.hospitalRegistrationNumber.toString()));
                cur.moveToNext();
            }
        }
        cur.close();
        return result;
    }

    public static String getAdmissionStatus(String uuid) {

        String result="No";

        String query = "SELECT isMotherAdmitted from "+ TableMotherRegistration.tableName
                + " where " + TableMotherRegistration.tableColumn.uuid + " = '" + uuid
                +"'" ;

        Cursor cur = myDataBase.rawQuery(query , null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                result=cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.isMotherAdmitted.toString()));
                cur.moveToNext();
            }
        }
        cur.close();
        return result;
    }

    public static String getTypeViaMotherId(String motherId) {

        String result="";

        String query = "SELECT type from "+ TableMotherRegistration.tableName
                + " where " + TableMotherRegistration.tableColumn.motherId + " = '" + motherId +"'" ;

        Cursor cur = myDataBase.rawQuery(query , null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                result=cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.type.toString()));
                cur.moveToNext();
            }
        }
        cur.close();
        return result;
    }

    public static ArrayList<HashMap<String, String>> getDutyChange(String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String syncData = " isDataSynced = '0' and loungeId = '"+AppSettings.getString(AppSettings.loungeId)+"' ";

        String query ="SELECT * from "+ TableDutyChange.tableName
                + " where " + TableDutyChange.tableColumn.loungeId + " = '" + loungeId+ "' and "  + syncData +" order by addDate asc ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableDutyChange.tableColumn.uuid.toString())));
                hashlist.put("nurseId", cur.getString(cur.getColumnIndex(TableDutyChange.tableColumn.nurseId.toString())));
                hashlist.put("json", cur.getString(cur.getColumnIndex(TableDutyChange.tableColumn.json.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }

        cur.close();

        Log.d("postDutyChange", arrayList.toString());

        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getDutyCheckOutChange(String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String syncData = " isDataSyncedCheckOut = '0' and loungeId = '"+AppSettings.getString(AppSettings.loungeId)+"' ";

        String query ="SELECT * from "+ TableDutyChange.tableName
                + " where " + TableDutyChange.tableColumn.loungeId + " = '" + loungeId+ "' and "  + syncData +" order by addDate asc ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount())+query);

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                    HashMap<String, String> hashlist = new HashMap();
                    hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableDutyChange.tableColumn.uuid.toString())));
                    hashlist.put("nurseId", cur.getString(cur.getColumnIndex(TableDutyChange.tableColumn.nurseId.toString())));
                    hashlist.put("type", cur.getString(cur.getColumnIndex(TableDutyChange.tableColumn.type.toString())));
                    hashlist.put("json", cur.getString(cur.getColumnIndex(TableDutyChange.tableColumn.json.toString())));
                    hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableDutyChange.tableColumn.addDate.toString())));
                    arrayList.add(hashlist);


                cur.moveToNext();
            }
        }

        cur.close();

        Log.d("postDutyChange", arrayList.toString());

        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getStuckData() {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList();
        arrayList.clear();
        String syncData = " status = '0' and loungeId = '"+AppSettings.getString(AppSettings.loungeId)+"' ";
        String query ="SELECT * from "+ TableStuck.tableName + " where "  + syncData +" order by addDate asc ";
        Cursor cur = myDataBase.rawQuery(query, null);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("json", cur.getString(cur.getColumnIndex(TableStuck.tableColumn.json.toString())));
                arrayList.add(hashlist);
                cur.moveToNext();
            }
        }
        cur.close();
        Log.d("getStuckData", arrayList.toString());
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getBirthData() {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String syncData = " status = '0' and loungeId = '"+AppSettings.getString(AppSettings.loungeId)+"' ";

        String query ="SELECT * from "+ TableBirthReview.tableName
                + " where "  + syncData +" order by addDate asc ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("json", cur.getString(cur.getColumnIndex(TableBirthReview.tableColumn.json.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }

        cur.close();

        Log.d("getBirthData", arrayList.toString());

        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getLoungeServiceData() {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String syncData = " status = '0' and loungeId = '"+AppSettings.getString(AppSettings.loungeId)+"' ";

        String query ="SELECT * from "+ TableLoungeServices.tableName
                              + " where "  + syncData +" order by addDate asc ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableLoungeServices.tableColumn.uuid.toString())));
                hashlist.put("nurseId", cur.getString(cur.getColumnIndex(TableLoungeServices.tableColumn.nurseId.toString())));
                hashlist.put("value", cur.getString(cur.getColumnIndex(TableLoungeServices.tableColumn.value.toString())));
                hashlist.put("type", cur.getString(cur.getColumnIndex(TableLoungeServices.tableColumn.type.toString())));
                hashlist.put("slot", cur.getString(cur.getColumnIndex(TableLoungeServices.tableColumn.slot.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableLoungeServices.tableColumn.addDate.toString())));
                hashlist.put("modifyDate", cur.getString(cur.getColumnIndex(TableLoungeServices.tableColumn.modifyDate.toString())));
                hashlist.put("latitude", cur.getString(cur.getColumnIndex(TableLoungeServices.tableColumn.latitude.toString())));
                hashlist.put("longitude", cur.getString(cur.getColumnIndex(TableLoungeServices.tableColumn.longitude.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }

        cur.close();

        Log.d("getLoungeServiceData", arrayList.toString());

        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getLoungeServiceData(String date) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String syncData = " addDate > '" + date+ "  00:00:00' and loungeId = '"+AppSettings.getString(AppSettings.loungeId)+"' ";

        String query ="SELECT * from "+ TableLoungeServices.tableName
                              + " where "  + syncData +" order by addDate asc ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableLoungeServices.tableColumn.uuid.toString())));
                hashlist.put("nurseId", cur.getString(cur.getColumnIndex(TableLoungeServices.tableColumn.nurseId.toString())));
                hashlist.put("value", cur.getString(cur.getColumnIndex(TableLoungeServices.tableColumn.value.toString())));
                hashlist.put("type", cur.getString(cur.getColumnIndex(TableLoungeServices.tableColumn.type.toString())));
                hashlist.put("slot", cur.getString(cur.getColumnIndex(TableLoungeServices.tableColumn.slot.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableLoungeServices.tableColumn.addDate.toString())));
                hashlist.put("modifyDate", cur.getString(cur.getColumnIndex(TableLoungeServices.tableColumn.modifyDate.toString())));
                hashlist.put("latitude", cur.getString(cur.getColumnIndex(TableLoungeServices.tableColumn.latitude.toString())));
                hashlist.put("longitude", cur.getString(cur.getColumnIndex(TableLoungeServices.tableColumn.longitude.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }

        cur.close();

        Log.d("getLoungeServiceData", arrayList.toString());

        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getLoungeAssessment(String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String syncData = " isDataSynced = '0' ";

        String query ="SELECT * from "+ TableLoungeAssessment.tableName
                + " where " + TableLoungeAssessment.tableColumn.loungeId + " = '" + loungeId+ "' and "  + syncData +" order by addDate asc ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableLoungeAssessment.tableColumn.uuid.toString())));
                hashlist.put("json", cur.getString(cur.getColumnIndex(TableLoungeAssessment.tableColumn.json.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }

        cur.close();

        Log.d("postDutyChange", arrayList.toString());

        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getCommentDataToSync(String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableComments.tableName
                + " where " + TableComments.tableColumn.loungeId + " = '" + loungeId+ "' and isDataSynced = '0' order by addDate asc";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("motherOrBabyId", cur.getString(cur.getColumnIndex(TableComments.tableColumn.motherOrBabyId.toString())));
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableComments.tableColumn.uuid.toString())));
                hashlist.put("doctorId", cur.getString(cur.getColumnIndex(TableComments.tableColumn.doctorId.toString())));
                hashlist.put("doctorComment", cur.getString(cur.getColumnIndex(TableComments.tableColumn.comment.toString())));
                hashlist.put("type", cur.getString(cur.getColumnIndex(TableComments.tableColumn.type.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableComments.tableColumn.addDate.toString())));
                hashlist.put("status", cur.getString(cur.getColumnIndex(TableComments.tableColumn.status.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getCommentDataToSync(String motherOrBabyId, String loungeId, String type) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableComments.tableName
                + " where " + TableComments.tableColumn.loungeId + " = '" + loungeId+ "' and motherOrBabyId  = '"+ motherOrBabyId + "' and type  = '"+ type + "'" +
                " and isDataSynced = '0' ";

      /*  String query ="SELECT * from "+ TableComments.tableName
                + " where " + TableComments.tableColumn.loungeId + " = '" + loungeId+ "' and isDataSynced = '0' order by addDate asc";*/

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("motherOrBabyId", cur.getString(cur.getColumnIndex(TableComments.tableColumn.motherOrBabyId.toString())));
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableComments.tableColumn.uuid.toString())));
                hashlist.put("doctorId", cur.getString(cur.getColumnIndex(TableComments.tableColumn.doctorId.toString())));
                hashlist.put("doctorComment", cur.getString(cur.getColumnIndex(TableComments.tableColumn.comment.toString())));
                hashlist.put("type", cur.getString(cur.getColumnIndex(TableComments.tableColumn.type.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableComments.tableColumn.addDate.toString())));
                hashlist.put("status", cur.getString(cur.getColumnIndex(TableComments.tableColumn.status.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }


    public static ArrayList<HashMap<String, String>> getPendingNotifications(int type) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableNotification.tableName
                + " where " + TableNotification.tableColumn.loungeId
                + " = '" + AppSettings.getString(AppSettings.loungeId)
                + "' and status = '" + 1 + "' and type = '" + type +"'";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query - cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableNotification.tableColumn.uuid.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableNotification.tableColumn.babyId.toString())));
                hashlist.put("motherId", cur.getString(cur.getColumnIndex(TableNotification.tableColumn.motherId.toString())));
                hashlist.put("message", cur.getString(cur.getColumnIndex(TableNotification.tableColumn.message.toString())));
                hashlist.put("type", cur.getString(cur.getColumnIndex(TableNotification.tableColumn.type.toString())));
                hashlist.put("assessmentNumber", cur.getString(cur.getColumnIndex(TableNotification.tableColumn.assessmentNumber.toString())));
                hashlist.put("status", cur.getString(cur.getColumnIndex(TableNotification.tableColumn.status.toString())));
                hashlist.put("date", cur.getString(cur.getColumnIndex(TableNotification.tableColumn.date.toString())));
                hashlist.put("time", cur.getString(cur.getColumnIndex(TableNotification.tableColumn.time.toString())));
                hashlist.put("shiftingType", cur.getString(cur.getColumnIndex(TableNotification.tableColumn.shiftingType.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableNotification.tableColumn.addDate.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getCurrentNotifications(String condition) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String   query ="SELECT * from "+ TableNotification.tableName
                + " where " + TableNotification.tableColumn.loungeId
                + " = '" + AppSettings.getString(AppSettings.loungeId)
                + "' and "+condition;

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query - query", query);
        Log.d("query - cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableNotification.tableColumn.uuid.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static String getPendingNotifications(int type,String uniqueId) {

        String notiId = "";

        String query = "";

        if (type == 2) {
            query = "SELECT * from " + TableNotification.tableName
                    + " where " + TableNotification.tableColumn.loungeId
                    + " = '" + AppSettings.getString(AppSettings.loungeId)
                    + "' and status = '" + 1 + "' and type = '" + type + "' and babyId = '" + uniqueId + "'";
        }
        else  if (type == 1) {
            query = "SELECT * from " + TableNotification.tableName
                    + " where " + TableNotification.tableColumn.loungeId
                    + " = '" + AppSettings.getString(AppSettings.loungeId)
                    + "' and status = '" + 1 + "' and type = '" + type + "' and motherId = '" + uniqueId + "'";
        }

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query - cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                notiId = cur.getString(cur.getColumnIndex(TableNotification.tableColumn.uuid.toString()));

                cur.moveToNext();
            }
        }
        cur.close();
        return notiId;
    }

    public static String getMotherColumnData(String motherId,String columnName) {

        String result = "";

        String query ="SELECT * from "+ TableMotherRegistration.tableName +
                " where " + TableMotherRegistration.tableColumn.motherId + " = '" + motherId +"'";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query - cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                result = cur.getString(cur.getColumnIndex(columnName));

                cur.moveToNext();
            }
        }
        cur.close();
        return result;
    }

    public static String getMotherAdmissionColumnData(String motherId,String columnName) {

        String result = "";

        String query ="SELECT * from "+ TableMotherAdmission.tableName +
                " where " + TableMotherAdmission.tableColumn.serverId + " = '" + motherId +"'";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query - cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                result = cur.getString(cur.getColumnIndex(columnName));

                cur.moveToNext();
            }
        }
        cur.close();
        return result;
    }

    public static String getMotherMonitoringColumnData(String motherId,String columnName) {

        String result = "";

        String query ="SELECT * from "+ TableMotherMonitoring.tableName +
                              " where " + TableMotherMonitoring.tableColumn.motherId + " = '" + motherId +"' and "
                              +TableMotherMonitoring.tableColumn.isDataComplete +" = '1' ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query - cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                result = cur.getString(cur.getColumnIndex(columnName));

                cur.moveToNext();
            }
        }
        cur.close();
        return result;
    }

    public static ArrayList<String> getOrderedMedicine(String babyId) {

        ArrayList<String> arrayList= new ArrayList<String>();

        String json = "";

     //   String query = "SELECT * from "+ TableDoctorRound.tableName +" where babyId = '"+babyId +"' order by addDate desc LIMIT 1";// Comented on 04/09/1010
        String query = "SELECT * from "+ TableDoctorRound.tableName +" where babyId = '"+babyId +"' order by addDate desc ";

        Cursor cur = myDataBase.rawQuery(query , null);

        Log.d("cur.getCount()ssss", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                json = cur.getString(cur.getColumnIndex(TableDoctorRound.tableColumn.json.toString()));

                Log.v("ljhqjshq", json);

                try {

                    JSONObject jsonObject = new JSONObject(json);

                    JSONArray jsonArray = jsonObject.getJSONArray("treatment");

                    for(int j=0;j<jsonArray.length();j++)
                    {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(j);
                        arrayList.add(jsonObject1.getString("treatmentName"));

                        Log.v("dawdwqa", String.valueOf(jsonArray.length()));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.v("wqsdqsqs", String.valueOf(i));


                cur.moveToNext();
            }
        }
        cur.close();



        return arrayList;
    }

   /* public static ArrayList<String> getOrderedMedicine(String babyId) {

        ArrayList<String> arrayList= new ArrayList<String>();

        String json = "";

        //   String query = "SELECT * from "+ TableDoctorRound.tableName +" where babyId = '"+babyId +"' order by addDate desc LIMIT 1";// Comented on 04/09/1010
        String query = "SELECT * from "+ TableDoctorRound.tableName +" where babyId = '"+babyId +"' order by addDate desc ";

        Cursor cur = myDataBase.rawQuery(query , null);

        Log.d("cur.getCount()ssss", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                json = cur.getString(cur.getColumnIndex(TableDoctorRound.tableColumn.json.toString()));

                Log.v("wqsdqsqs", String.valueOf(i));
            }
        }
        cur.close();

        try {

            JSONObject jsonObject = new JSONObject(json);

            JSONArray jsonArray = jsonObject.getJSONArray("treatment");

            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                arrayList.add(jsonObject1.getString("treatmentName"));

                Log.v("dawdwqa", String.valueOf(jsonArray.length()));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arrayList;
    }

*/

    public static ArrayList<String> getOrderedMedicineUUid(String babyId) {

        ArrayList<String> arrayList= new ArrayList<String>();

        String json = "";

        String query = "SELECT * from "+ TableDoctorRound.tableName +" where babyId = '"+babyId +"' order by addDate desc";

        Cursor cur = myDataBase.rawQuery(query , null);

        Log.d("cur.getCount()Uuid", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                json = cur.getString(cur.getColumnIndex(TableDoctorRound.tableColumn.json.toString()));

                try {

                    JSONObject jsonObject = new JSONObject(json);

                    JSONArray jsonArray = jsonObject.getJSONArray("treatment");

                    for(int j=0;j<jsonArray.length();j++)
                    {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(j);

                        arrayList.add(jsonObject1.getString("localId"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                cur.moveToNext();
            }
        }
        cur.close();

        Log.v("cur.getCount()Uuid2", String.valueOf(arrayList.size()));

        return arrayList;
    }


/*
    public static String getOrderedMedicineUUid(String babyId) {


        String json = "", uuid="";

        String query = "SELECT * from "+ TableDoctorRound.tableName +" where babyId = '"+babyId +"' order by addDate desc LIMIT 1";

        Cursor cur = myDataBase.rawQuery(query , null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                json = cur.getString(cur.getColumnIndex(TableDoctorRound.tableColumn.json.toString()));

            }
        }
        cur.close();

        try {

            JSONObject jsonObject = new JSONObject(json);

            JSONArray jsonArray = jsonObject.getJSONArray("treatment");

            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                uuid = jsonObject1.getString("localId");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return uuid;
    }
*/


    public static void saveHelp(BaseActivity mActivity) {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(mActivity, Locale.getDefault());

        double latitude = Double.parseDouble(AppSettings.getString(AppSettings.latitude));
        double longitude = Double.parseDouble(AppSettings.getString(AppSettings.longitude));

        String address = "";
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL


        } catch (IOException e) {
            e.printStackTrace();
        }

        String uuid = UUID.randomUUID().toString();

        JSONObject jsonData = new JSONObject();

        try {

            jsonData.put("loungeId",        AppSettings.getString(AppSettings.loungeId));
            jsonData.put("latitude",        AppSettings.getString(AppSettings.latitude));
            jsonData.put("longitude",       AppSettings.getString(AppSettings.longitude));
            jsonData.put("location",        address);
            jsonData.put("localId",         uuid);
            jsonData.put("localDateTime",   AppUtils.currentTimestampFormat());

            Log.v("getAllStaffApi", jsonData.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ContentValues contentValues = new ContentValues();

        contentValues.put(TableStuck.tableColumn.uuid.toString(), uuid);
        contentValues.put(TableStuck.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        contentValues.put(TableStuck.tableColumn.latitude.toString(), AppSettings.getString(AppSettings.latitude));
        contentValues.put(TableStuck.tableColumn.longitude.toString(), AppSettings.getString(AppSettings.longitude));
        contentValues.put(TableStuck.tableColumn.location.toString(), "");
        contentValues.put(TableStuck.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableStuck.tableColumn.json.toString(), jsonData.toString());
        contentValues.put(TableStuck.tableColumn.status.toString(), "0");

        DatabaseController.insertData(contentValues, TableStuck.tableName);
    }

    public static void saveBirthReview(String shift,int totalLiveBirth, int totalStableBabies,int totalUnstableBabies,String nurseId) {

        String uuid = UUID.randomUUID().toString();

        JSONObject jsonData = new JSONObject();

        try {

            jsonData.put("loungeId",            AppSettings.getString(AppSettings.loungeId));
            jsonData.put("nurseId",             nurseId);
            jsonData.put("latitude",            AppSettings.getString(AppSettings.latitude));
            jsonData.put("longitude",           AppSettings.getString(AppSettings.longitude));
            jsonData.put("shift",               shift);
            jsonData.put("totalLiveBirth",      totalLiveBirth);
            jsonData.put("totalStableBabies",   totalStableBabies);
            jsonData.put("totalUnstableBabies", totalUnstableBabies);
            jsonData.put("localId",             uuid);
            jsonData.put("localDateTime",       AppUtils.currentTimestampFormat());

            Log.v("saveBirthReview", jsonData.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ContentValues contentValues = new ContentValues();

        contentValues.put(TableBirthReview.tableColumn.uuid.toString(), uuid);
        contentValues.put(TableBirthReview.tableColumn.nurseId.toString(), nurseId);
        contentValues.put(TableBirthReview.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        contentValues.put(TableBirthReview.tableColumn.latitude.toString(), AppSettings.getString(AppSettings.latitude));
        contentValues.put(TableBirthReview.tableColumn.longitude.toString(), AppSettings.getString(AppSettings.longitude));
        contentValues.put(TableBirthReview.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableBirthReview.tableColumn.json.toString(), jsonData.toString());
        contentValues.put(TableBirthReview.tableColumn.status.toString(), "0");
        contentValues.put(TableBirthReview.tableColumn.shift.toString(), shift);
        contentValues.put(TableBirthReview.tableColumn.totalLiveBirth.toString(), totalLiveBirth);
        contentValues.put(TableBirthReview.tableColumn.totalStableBabies.toString(), totalStableBabies);
        contentValues.put(TableBirthReview.tableColumn.totalUnstableBabies.toString(), totalUnstableBabies);

        DatabaseController.insertUpdateData(contentValues, TableBirthReview.tableName,
                TableBirthReview.tableColumn.uuid.toString(), uuid);
    }

    public static String  getSSCViaIdToday(String babyId,String where) {

        /*String query ="SELECT * from "+TableKMC.tableName
                + " where " + TableKMC.tableColumn.babyId + " = '" + babyId+ "' and " +
                " startDate = '"+AppUtils.getCurrentDateFormatted()+"' and status = '" + 1 +"'";*/

        String query ="SELECT *,  (startDate || ' ' || startTime) AS dateTimeText from "+TableKMC.tableName
                              + " where " + TableKMC.tableColumn.babyId + " = '" + babyId
                              + "' and " +  where + " and status = '1' ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query", String.valueOf(query));
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        String duration="0";

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                if(duration.equals("0"))
                {
                    try {
                        duration =AppUtils.getSSTTime(cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startDate.toString()))+ " "+cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startTime.toString()))
                                ,cur.getString(cur.getColumnIndex(TableKMC.tableColumn.endDate.toString()))+ " "+cur.getString(cur.getColumnIndex(TableKMC.tableColumn.endTime.toString())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    try {
                        duration = AppUtils.sumTimes(duration ,AppUtils.getSSTTime(cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startDate.toString()))+ " "+cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startTime.toString()))
                                ,cur.getString(cur.getColumnIndex(TableKMC.tableColumn.endDate.toString()))+ " "+cur.getString(cur.getColumnIndex(TableKMC.tableColumn.endTime.toString()))));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                cur.moveToNext();
            }
        }

        cur.close();

        Log.d("cur.getCount()",duration);

        return duration;
    }

    public static String  getSSC(String babyId,String date) {

        String where = "";

        if(date.equalsIgnoreCase(AppUtils.getCurrentDateFormatted()))
        {
            where = AppUtils.getCurrentDateAsPerHospital();
        }
        else
        {
            where = AppUtils.getHoursDayAsPerHospital(date);
        }

       /* String query ="SELECT * from "+TableKMC.tableName
                + " where " + TableKMC.tableColumn.babyId + " = '" + babyId+ "' and " +
                " startDate = '"+date+"' and status = '" + 1 +"'";*/

        String query ="SELECT *,  (startDate || ' ' || startTime) AS dateTimeText from "+TableKMC.tableName
                              + " where " + TableKMC.tableColumn.babyId + " = '" + babyId
                              + "' and " +  where + " and status = '1' ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query", String.valueOf(query));
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        String duration="0";

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                if(duration.equals("0"))
                {
                    try {
                        duration =AppUtils.getSSTTime(cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startDate.toString()))+ " "+cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startTime.toString()))
                                ,cur.getString(cur.getColumnIndex(TableKMC.tableColumn.endDate.toString()))+ " "+cur.getString(cur.getColumnIndex(TableKMC.tableColumn.endTime.toString())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    try {
                        duration = AppUtils.sumTimes(duration ,AppUtils.getSSTTime(cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startDate.toString()))+ " "+cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startTime.toString()))
                                ,cur.getString(cur.getColumnIndex(TableKMC.tableColumn.endDate.toString()))+ " "+cur.getString(cur.getColumnIndex(TableKMC.tableColumn.endTime.toString()))));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                cur.moveToNext();
            }
        }

        cur.close();

        Log.d("cur.getCount()",duration);

        return duration;
    }

    public static ArrayList<HashMap<String, String>> getMotherAdmissionDataViaId(String motherId) {

        ArrayList<HashMap<String, String>> CatList = new ArrayList();
        CatList.clear();

        String query ="SELECT * from "+ TableMotherAdmission.tableName +
                              " where "+ TableMotherAdmission.tableColumn.loungeId +" = '"+ AppSettings.getString(AppSettings.loungeId) +"' and "
                              + TableMotherAdmission.tableColumn.motherId + " = '" +motherId +"' and status = 1 order by addDate desc LIMIT 1";

        Log.d("getMotherAdmission",query);

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {
                HashMap<String, String> hashlist = new HashMap();
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableMotherAdmission.tableColumn.uuid.toString())));
                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableMotherAdmission.tableColumn.loungeId.toString())));
                hashlist.put("motherId", cur.getString(cur.getColumnIndex(TableMotherAdmission.tableColumn.motherId.toString())));
                hashlist.put("hospitalRegistrationNumber", cur.getString(cur.getColumnIndex(TableMotherAdmission.tableColumn.hospitalRegistrationNumber.toString())));
                //hashlist.put("hospitalRegistrationNumber", "");
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableMotherAdmission.tableColumn.addDate.toString())));
                hashlist.put("modifyDate", cur.getString(cur.getColumnIndex(TableMotherAdmission.tableColumn.modifyDate.toString())));
                hashlist.put("status", cur.getString(cur.getColumnIndex(TableMotherAdmission.tableColumn.status.toString())));
                hashlist.put("serverId", cur.getString(cur.getColumnIndex(TableMotherAdmission.tableColumn.serverId.toString())));

                CatList.add(hashlist);
                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("cur.getCount()", String.valueOf(CatList));

        return CatList;
    }

    public static ArrayList<HashMap<String, String>> getBabyToSyncUpdates(String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableBabyRegistration.tableName +
                              " where isDataSynced = '2' ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.uuid.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyId.toString())));
                hashlist.put("motherId", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.motherId.toString())));
                hashlist.put("babyMCTSNumber", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyMCTSNumber.toString())));
                hashlist.put("deliveryDate", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.deliveryDate.toString())));
                hashlist.put("deliveryTime", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.deliveryTime.toString())));
                hashlist.put("babyGender", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyGender.toString())));
                hashlist.put("deliveryType", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.deliveryType.toString())));
                hashlist.put("babyWeight", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyWeight.toString())));
                hashlist.put("birthWeightAvail", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.birthWeightAvail.toString())));
                hashlist.put("reason", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.reason.toString())));
                hashlist.put("babyCryAfterBirth", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyCryAfterBirth.toString())));
                hashlist.put("babyNeedBreathingHelp", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyNeedBreathingHelp.toString())));
                hashlist.put("registrationDateTime", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.registrationDateTime.toString())));
                hashlist.put("babyPhoto", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyPhoto.toString())));
                hashlist.put("firstTimeFeed", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.firstTimeFeed.toString())));
                hashlist.put("isDataSynced", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.isDataSynced.toString())));
                hashlist.put("babyFileId", getBabyFileId(cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyId.toString()))));
                hashlist.put("typeOfBorn", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.typeOfBorn.toString())));
                hashlist.put("typeOfOutBorn", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.typeOfOutBorn.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.addDate.toString())));
                hashlist.put("wasApgarScoreRecorded", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.wasApgarScoreRecorded.toString())));
                hashlist.put("apgarScore", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.apgarScore.toString())));
                hashlist.put("vitaminKGiven", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.vitaminKGiven.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("query-arrayList", String.valueOf(arrayList));

        return arrayList;
    }


    public static ArrayList<HashMap<String, String>> getBabyDataByMother(String motherId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableBabyRegistration.tableName +
                " where "+ TableMotherAdmission.tableColumn.motherId + " = '" +motherId +"' ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.uuid.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyId.toString())));
                hashlist.put("motherId", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.motherId.toString())));
                hashlist.put("babyMCTSNumber", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyMCTSNumber.toString())));
                hashlist.put("deliveryDate", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.deliveryDate.toString())));
                hashlist.put("deliveryTime", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.deliveryTime.toString())));
                hashlist.put("babyGender", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyGender.toString())));
                hashlist.put("deliveryType", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.deliveryType.toString())));
                hashlist.put("babyWeight", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyWeight.toString())));
                hashlist.put("birthWeightAvail", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.birthWeightAvail.toString())));
                hashlist.put("reason", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.reason.toString())));
                hashlist.put("babyCryAfterBirth", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyCryAfterBirth.toString())));
                hashlist.put("babyNeedBreathingHelp", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyNeedBreathingHelp.toString())));
                hashlist.put("registrationDateTime", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.registrationDateTime.toString())));
                hashlist.put("firstTimeFeed", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.firstTimeFeed.toString())));
                hashlist.put("isDataSynced", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.isDataSynced.toString())));
                hashlist.put("babyFileId", getBabyFileId(cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.babyId.toString()))));
                hashlist.put("typeOfBorn", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.typeOfBorn.toString())));
                hashlist.put("typeOfOutBorn", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.typeOfOutBorn.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.addDate.toString())));
                hashlist.put("wasApgarScoreRecorded", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.wasApgarScoreRecorded.toString())));
                hashlist.put("apgarScore", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.apgarScore.toString())));
                hashlist.put("vitaminKGiven", cur.getString(cur.getColumnIndex(TableBabyRegistration.tableColumn.vitaminKGiven.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("query-arrayList", String.valueOf(arrayList));

        return arrayList;
    }


    public static ArrayList<HashMap<String, String>> getWeightToSync(String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableWeight.tableName
                              + " where " + TableWeight.tableColumn.loungeId + " = '" + loungeId+ "' and isDataSynced IS NULL ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("getSSCDateToSync-query", query);
        Log.d("getSSCDateToSync-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.uuid.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.babyId.toString())));
                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.loungeId.toString())));
                hashlist.put("nurseId", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.nurseId.toString())));
                hashlist.put("babyWeight", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.babyWeight.toString())));
                hashlist.put("weightDate", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.weightDate.toString())));
                hashlist.put("reason", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.weighingReason.toString())));
                hashlist.put("isDeviceAvailAndWorking", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.isWeighingAvail.toString())));
                hashlist.put("weightImage", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.weightImage.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableWeight.tableColumn.addDate.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getBabyAssessToSync(String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query = "SELECT * from "+ TableBabyMonitoring.tableName + " where isDataSynced = '2' and  isDataComplete = '1' " ;

        Cursor cur = myDataBase.rawQuery(query , null);

        Log.d("query-query", query);
        Log.d("query-Count()", String.valueOf(cur.getCount()));
        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.uuid.toString())));
                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.loungeId.toString())));
                hashlist.put("serverId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.serverId.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyId.toString())));
                hashlist.put("babyAdmissionId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyAdmissionId.toString())));
                hashlist.put("babyMeasuredWeight", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyMeasuredWeight.toString())));
                hashlist.put("babyHeadCircumference", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyHeadCircumference.toString())));
                hashlist.put("babyRespiratoryRate", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyRespiratoryRate.toString())));
                hashlist.put("babyTemperature", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyTemperature.toString())));
                /*hashlist.put("babyHavePustulesOrBoils", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyHavePustulesOrBoils.toString())));
                hashlist.put("locationOfPustulesOrBoils", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.locationOfPustulesOrBoils.toString())));
                hashlist.put("sizeOfPustulesOrBoils", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.sizeOfPustulesOrBoils.toString())));
                hashlist.put("numberOfPustulesOrBoils", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.numberOfPustulesOrBoils.toString())));*/
                hashlist.put("isPulseOximatoryDeviceAvailable", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isPulseOximatoryDeviceAvailable.toString())));
                hashlist.put("type", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.type.toString())));
                //hashlist.put("babyOtherDangerSign", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyOtherDangerSign.toString())));
                hashlist.put("babySpO2", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babySpO2.toString())));
                hashlist.put("babyPulseRate", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyPulseRate.toString())));
                //hashlist.put("other", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.other.toString())));
                /*hashlist.put("motherBreastPain", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.motherBreastPain.toString())));
                hashlist.put("motherBreastStatus", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.motherBreastStatus.toString())));
                hashlist.put("motherBreastCondition", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.motherBreastCondition.toString())));
                hashlist.put("babyMilkConsumption1", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyMilkConsumption1.toString())));
                hashlist.put("babyMilkConsumption2", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyMilkConsumption2.toString())));
                hashlist.put("babyMilkConsumption3", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.babyMilkConsumption3.toString())));
                hashlist.put("skinColor", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.skinColor.toString())));*/
                hashlist.put("isHeadCircumferenceAvail", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isHeadCircumferenceAvail.toString())));
                hashlist.put("measuringTapeNotAvailReason", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.measuringTapeNotAvailReason.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.addDate.toString())));
                hashlist.put("json", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.json.toString())));

                /*String urination = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.urinationAfterLastAssesment.toString()));
                hashlist.put("urinationAfterLastAssesment", urination == null ? "" : urination);

                String stool = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.stoolAfterLastAssesment.toString()));
                hashlist.put("stoolAfterLastAssesment",  stool == null ? "" : stool);*/

                hashlist.put("staffId", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.staffId.toString())));

                String staffSign = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.staffSign.toString()));
                hashlist.put("staffSign", staffSign == null ? "" : staffSign);

                //String assessmentImage = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.tempImage.toString()));
                //hashlist.put("tempImage",  assessmentImage == null ? "" : assessmentImage);

                String isThermometerAvailable = cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.isThermometerAvailable.toString()));
                hashlist.put("isThermometerAvailable", isThermometerAvailable == null ? "" : isThermometerAvailable);
                //hashlist.put("thermoReason", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.thermoReason.toString())));
                hashlist.put("temperatureUnit", cur.getString(cur.getColumnIndex(TableBabyMonitoring.tableColumn.temperatureUnit.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getSSCDateToSync(String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+TableKMC.tableName
                              + " where " + TableKMC.tableColumn.loungeId + " = '" + loungeId+ "' and isDataSynced = '0' ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("getSSCDateToSync-query", query);
        Log.d("getSSCDateToSync-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.loungeId.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.babyId.toString())));
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.uuid.toString())));
                hashlist.put("startDate", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startDate.toString())));
                hashlist.put("endDate", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.endDate.toString())));
                hashlist.put("provider", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.provider.toString())));
                hashlist.put("startTime", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.startTime.toString())));
                hashlist.put("endTime", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.endTime.toString())));
                hashlist.put("nurseId", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.nurseId.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableKMC.tableColumn.addDate.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getFeedingDataToSync(String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableBreastFeeding.tableName
                              + " where " + TableBreastFeeding.tableColumn.loungeId + " = '" + loungeId+ "' and isDataSynced IS NULL ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query.Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.loungeId.toString())));
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.uuid.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.babyId.toString())));
                hashlist.put("feedTime", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.feedTime.toString())));
                hashlist.put("breastFeedDuration", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.duration.toString())));
                hashlist.put("method", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.method.toString())));
                hashlist.put("milkQuantity", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.quantity.toString())));
                hashlist.put("date", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.date.toString())));
                hashlist.put("nurseId", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.nurseId.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableBreastFeeding.tableColumn.addDate.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getDoctorRoundDataToSync(String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableDoctorRound.tableName
                              + " where " + TableDoctorRound.tableColumn.loungeId + " = '" + loungeId+ "' and isDataSynced IS NULL ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query.Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableDoctorRound.tableColumn.loungeId.toString())));
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableDoctorRound.tableColumn.uuid.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableDoctorRound.tableColumn.babyId.toString())));
                hashlist.put("doctorId", cur.getString(cur.getColumnIndex(TableDoctorRound.tableColumn.doctorId.toString())));
                hashlist.put("signature", cur.getString(cur.getColumnIndex(TableDoctorRound.tableColumn.signature.toString())));
                hashlist.put("json", cur.getString(cur.getColumnIndex(TableDoctorRound.tableColumn.json.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableDoctorRound.tableColumn.addDate.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getMedicationDataToSync(String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableTreatment.tableName
                              + " where " + TableTreatment.tableColumn.loungeId + " = '" + loungeId+ "' and isDataSynced = '0' ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.loungeId.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.babyId.toString())));
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.uuid.toString())));
                hashlist.put("treatmentName", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.treatmentName.toString())));
                hashlist.put("quantity", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.quantity.toString())));
                hashlist.put("nurseId", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.nurseId.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.addDate.toString())));
                hashlist.put("comment", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.comment.toString())));
                hashlist.put("unit", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.unit.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getInvestSampleDataToSync(String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

       /* String query ="SELECT * from "+ TableInvestigation.tableName
                + " where " + TableInvestigation.tableColumn.loungeId + " = '" + loungeId+ "' and babyId  = '"
                + babyId + "' and isDataSynced = '0' and sampleTakenOn != ' ' and result = ' ' ";*/

        String query ="SELECT * from "+ TableInvestigation.tableName
                              + " where " + TableInvestigation.tableColumn.loungeId + " = '" + loungeId+ "' and isDataSynced = '0' and status = '2' ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.loungeId.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.babyId.toString())));
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.uuid.toString())));
                hashlist.put("sampleTakenOn", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.sampleTakenOn.toString())));
                hashlist.put("sampleTakenBy", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.sampleTakenBy.toString())));
                hashlist.put("sampleImage", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.sampleImage.toString())));
                hashlist.put("sampleComment", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.sampleComment.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getInvestDataToSync(String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableInvestigation.tableName
                              + " where " + TableInvestigation.tableColumn.loungeId + " = '" + loungeId
                              + "' and isDataSynced = '0' and status = '3' ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.loungeId.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.babyId.toString())));
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.uuid.toString())));
                hashlist.put("investigationName", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.investigationName.toString())));
                hashlist.put("nurseComment", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.nurseComment.toString())));
                hashlist.put("result", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.result.toString())));
                hashlist.put("resultImage", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.resultImage.toString())));
                hashlist.put("nurseId", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.nurseId.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.addDate.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getVaccinationDataToSync(String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableVaccination.tableName
                              + " where " + TableVaccination.tableColumn.loungeId + " = '" + loungeId+ "' and isDataSynced = '0' ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableVaccination.tableColumn.loungeId.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableVaccination.tableColumn.babyId.toString())));
                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableVaccination.tableColumn.uuid.toString())));
                hashlist.put("vaccName", cur.getString(cur.getColumnIndex(TableVaccination.tableColumn.vaccName.toString())));
                hashlist.put("quantity", cur.getString(cur.getColumnIndex(TableVaccination.tableColumn.quantity.toString())));
                hashlist.put("date", cur.getString(cur.getColumnIndex(TableVaccination.tableColumn.date.toString())));
                hashlist.put("time", cur.getString(cur.getColumnIndex(TableVaccination.tableColumn.time.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableVaccination.tableColumn.addDate.toString())));
                hashlist.put("nurseId", cur.getString(cur.getColumnIndex(TableVaccination.tableColumn.nurseId.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getBabyCounsellingUpdates() {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableBabyAdmission.tableName ;

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.uuid.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.babyId.toString())));
                hashlist.put("kmcPosition", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.kmcPosition.toString())));
                hashlist.put("kmcMonitoring", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.kmcMonitoring.toString())));
                hashlist.put("kmcNutrition", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.kmcNutrition.toString())));
                hashlist.put("kmcRespect", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.kmcRespect.toString())));
                hashlist.put("kmcHygiene", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.kmcHygiene.toString())));
                hashlist.put("whatisKmc", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.whatisKmc.toString())));
                hashlist.put("modifyDate", cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.modifyDate.toString())));

                String babyId = cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.babyId.toString()));
                hashlist.put("babyId", babyId == null ? "" : babyId);

                String kmcPosition = cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.kmcPosition.toString()));
                hashlist.put("kmcPosition", kmcPosition == null ? "" : kmcPosition);

                String kmcMonitoring = cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.kmcMonitoring.toString()));
                hashlist.put("kmcMonitoring", kmcMonitoring == null ? "" : kmcMonitoring);

                String kmcNutrition = cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.kmcNutrition.toString()));
                hashlist.put("kmcNutrition", kmcNutrition == null ? "" : kmcNutrition);

                String kmcRespect = cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.kmcRespect.toString()));
                hashlist.put("kmcRespect", kmcRespect == null ? "" : kmcRespect);

                String kmcHygiene = cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.kmcHygiene.toString()));
                hashlist.put("kmcHygiene", kmcHygiene == null ? "" : kmcHygiene);

                String whatisKmc = cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.whatisKmc.toString()));
                hashlist.put("whatisKmc", whatisKmc == null ? "" : whatisKmc);


                String modifyDate = cur.getString(cur.getColumnIndex(TableBabyAdmission.tableColumn.modifyDate.toString()));
                hashlist.put("modifyDate", modifyDate == null ? "" : modifyDate);

                if (modifyDate != null
                            && kmcHygiene != null
                            && whatisKmc != null
                            && kmcRespect != null
                            && kmcNutrition != null
                            && kmcMonitoring != null
                            && kmcPosition != null
                            && !kmcPosition.isEmpty()
                            && !kmcMonitoring.isEmpty()
                            && !kmcNutrition.isEmpty()
                            && !kmcRespect.isEmpty()
                            && !kmcHygiene.isEmpty()
                            && !modifyDate.isEmpty()) {
                    arrayList.add(hashlist);
                }

                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("query-arrayList", String.valueOf(arrayList));

        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getBabyCounsellingposters() {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableCounsellingPosters.tableName ;

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("ConsumeTime", cur.getString(cur.getColumnIndex(TableCounsellingPosters.tableColumn.ConsumeTime.toString())));
                hashlist.put("type", cur.getString(cur.getColumnIndex(TableCounsellingPosters.tableColumn.type.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableCounsellingPosters.tableColumn.babyId.toString())));
                hashlist.put("date", cur.getString(cur.getColumnIndex(TableCounsellingPosters.tableColumn.date.toString())));
                hashlist.put("posterId", cur.getString(cur.getColumnIndex(TableCounsellingPosters.tableColumn.posterId.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("query-arrayList", String.valueOf(arrayList));

        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getMotherIdToSyncUpdates(String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableMotherRegistration.tableName +
                              " where isDataSynced = '2'  and loungeId = '"+loungeId+"'";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.uuid.toString())));
                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.loungeId.toString())));
                hashlist.put("motherId", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherId.toString())));
                hashlist.put("motherName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherName.toString())));
                hashlist.put("isMotherAdmitted", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.isMotherAdmitted.toString())));
                hashlist.put("reasonForNotAdmitted", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.reasonForNotAdmitted.toString())));
                hashlist.put("motherPicture", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherPicture.toString())));
                hashlist.put("motherMCTSNumber", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherMCTSNumber.toString())));
                hashlist.put("motherAadharNumber", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherAadharNumber.toString())));
                hashlist.put("motherDob", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherDob.toString())));
                hashlist.put("motherAge", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherAge.toString())));
                hashlist.put("motherEducation", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherEducation.toString())));
                hashlist.put("motherCaste", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherCaste.toString())));
                hashlist.put("motherReligion", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherReligion.toString())));
                hashlist.put("motherMoblieNo", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherMoblieNo.toString())));
                hashlist.put("fatherName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.fatherName.toString())));
                hashlist.put("fatherAadharNumber", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.fatherAadharNumber.toString())));
                hashlist.put("fatherMoblieNo", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.fatherMoblieNo.toString())));
                hashlist.put("rationCardType", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.rationCardType.toString())));
                hashlist.put("guardianName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.guardianName.toString())));
                hashlist.put("guardianNumber", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.guardianNumber.toString())));
                hashlist.put("guardianRelation", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.guardianRelation.toString())));
                hashlist.put("presentCountry", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentCountry.toString())));
                hashlist.put("presentState", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentState.toString())));
                hashlist.put("presentResidenceType", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentResidenceType.toString())));
                hashlist.put("presentAddress", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentAddress.toString())));
                hashlist.put("presentVillageName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentVillageName.toString())));
                hashlist.put("presentBlockName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentBlockName.toString())));
                hashlist.put("presentDistrictName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentDistrictName.toString())));
                hashlist.put("permanentResidenceType", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentResidenceType.toString())));
                hashlist.put("permanentCountry", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentCountry.toString())));
                hashlist.put("permanentState", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentState.toString())));
                hashlist.put("permanentAddress", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentAddress.toString())));
                hashlist.put("permanentVillageName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentVillageName.toString())));
                hashlist.put("permanentBlockName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentBlockName.toString())));
                hashlist.put("permanentDistrictName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentDistrictName.toString())));
                hashlist.put("presentPinCode", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentPinCode.toString())));
                hashlist.put("permanentPinCode", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentPinCode.toString())));
                hashlist.put("ashaID", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.ashaID.toString())));
                hashlist.put("ashaName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.ashaName.toString())));
                hashlist.put("ashaNumber", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.ashaNumber.toString())));
                hashlist.put("presentAddressNearBy", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.presentAddressNearBy.toString())));
                hashlist.put("permanentAddressNearBy", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.permanentAddressNearBy.toString())));
                hashlist.put("staffId", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.staffId.toString())));
                hashlist.put("type", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.type.toString())));
                hashlist.put("organisationName", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.organisationName.toString())));
                hashlist.put("organisationNumber", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.organisationNumber.toString())));
                hashlist.put("organisationAddress", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.organisationAddress.toString())));
                hashlist.put("para", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.para.toString())));
                hashlist.put("gravida", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.gravida.toString())));
                hashlist.put("abortion", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.abortion.toString())));
                hashlist.put("live", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.live.toString())));
                hashlist.put("multipleBirth", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.multipleBirth.toString())));
                hashlist.put("admittedSign", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.admittedSign.toString())));
                hashlist.put("motherLmpDate", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherLmpDate.toString())));
                hashlist.put("motherDeliveryPlace", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherDeliveryPlace.toString())));
                hashlist.put("motherDeliveryDistrict", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherDeliveryDistrict.toString())));
                hashlist.put("facilityId", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.facilityID.toString())));
                hashlist.put("isDataSynced", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.isDataSynced.toString())));
                hashlist.put("syncTime", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.syncTime.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.addDate.toString())));
                hashlist.put("modifyDate", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.modifyDate.toString())));
                hashlist.put("status", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.status.toString())));
                //hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.addDate.toString())));
                //hashlist.put("hospitalRN", getMotherFileId(cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherId.toString()))));
                hashlist.put("sameAddress", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.sameaddress.toString())));
                hashlist.put("motherWeight", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.motherWeight.toString())));
                hashlist.put("ageOfMarriage", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.ageOfMarriage.toString())));
                hashlist.put("consanguinity", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.consanguinity.toString())));
                hashlist.put("birthSpacing", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.birthSpacing.toString())));
                hashlist.put("estimatedDateOfDelivery", cur.getString(cur.getColumnIndex(TableMotherRegistration.tableColumn.estimatedDateOfDelivery.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("query-arrayList", String.valueOf(arrayList));

        return arrayList;
    }

    public static ArrayList<HashMap<String, String>> getMotherAssessmentToSync(String loungeId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();

        arrayList.clear();

        String query ="SELECT * from "+ TableMotherMonitoring.tableName +
                              " where isDataSynced = '2' and isDataComplete = '1' and loungeId = '"+loungeId + "'";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.uuid.toString())));
                hashlist.put("loungeId", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.loungeId.toString())));
                hashlist.put("motherId", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherId.toString())));
                hashlist.put("motherAdmissionId", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherAdmissionId.toString())));
                hashlist.put("motherTemperature", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherTemperature.toString())));
                hashlist.put("motherSystolicBP", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherSystolicBP.toString())));
                hashlist.put("motherDiastolicBP", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherDiastolicBP.toString())));
                hashlist.put("motherPulse", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherPulse.toString())));
                hashlist.put("motherUterineTone", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherUterineTone.toString())));
                hashlist.put("episitomyCondition", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.episitomyCondition.toString())));
                hashlist.put("newSanitoryPadCheck", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.newSanitoryPadCheck.toString())));
                hashlist.put("motherUrinationAfterDelivery", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.motherUrinationAfterDelivery.toString())));
                hashlist.put("sanitoryPadStatus", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.sanitoryPadStatus.toString())));
                hashlist.put("isSanitoryPadStink", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.isSanitoryPadStink.toString())));
                hashlist.put("admittedSign", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.admittedSign.toString())));
                hashlist.put("staffId", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.staffId.toString())));
                hashlist.put("other", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.other.toString())));
                hashlist.put("padPicture", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.padPicture.toString())));
                hashlist.put("assesmentNumber", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.assesmentNumber.toString())));
                hashlist.put("syncTime", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.syncTime.toString())));
                //hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.addDate.toString())));
                hashlist.put("modifyDate", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.modifyDate.toString())));
                hashlist.put("status", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.status.toString())));
                hashlist.put("padNotChangeReason", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.padNotChangeReason.toString())));
                hashlist.put("temperatureUnit", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.temperatureUnit.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableMotherMonitoring.tableColumn.addDate.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();

        Log.d("query-arrayList", String.valueOf(arrayList));

        return arrayList;
    }


    public static ArrayList<String> getBlockIdData(String dId) {

        ArrayList<String> List = new ArrayList();

        List.clear();

        String query = "SELECT * from "+ TableBlock.tableName+ " where "
                                + TableBlock.tableColumn.districtId + " = '" + dId
                               + "' order method "+ TableBlock.tableColumn.blockName +" asc" ;

        Cursor cur = myDataBase.rawQuery( query, null);

        Log.d("query", query);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                List.add(cur.getString(cur.getColumnIndex(TableBlock.tableColumn.blockId.toString())));
                cur.moveToNext();
            }
        }
        cur.close();
        return List;
    }

    public static ArrayList<String> getBlockNameData(String dId) {

        ArrayList<String> list = new ArrayList();

        String query =  "SELECT * from "+ TableBlock.tableName+ " where "
                                + TableBlock.tableColumn.districtId + " = '" + dId
                                + "' order by "+ TableBlock.tableColumn.blockName +" asc ";

        Cursor cur = myDataBase.rawQuery(query, null);
        Log.d("str", query);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                list.add(cur.getString(cur.getColumnIndex(TableBlock.tableColumn.blockName.toString())));
                cur.moveToNext();
            }
        }
        cur.close();
        return list;
    }

    public static ArrayList<String> getBlockIdData(String type,String dId) {

        ArrayList<String> List = new ArrayList();

        List.clear();

        String query = "SELECT * from "+ TableBlock.tableName+ " where "
                               + TableBlock.tableColumn.urbanRural + " = '" + type
                               + "' and " + TableBlock.tableColumn.districtId + " = '" + dId
                               + "' order by "+ TableBlock.tableColumn.blockName +" asc" ;

        Cursor cur = myDataBase.rawQuery( query, null);

        Log.d("query", query);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                List.add(cur.getString(cur.getColumnIndex(TableBlock.tableColumn.blockId.toString())));
                cur.moveToNext();
            }
        }
        cur.close();
        return List;
    }

    public static ArrayList<String> getBlockNameData(String type,String dId) {

        ArrayList<String> list = new ArrayList();

        String query =  "SELECT * from "+ TableBlock.tableName+ " where "
                                + TableBlock.tableColumn.urbanRural + " = '" + type
                                + "' and " + TableBlock.tableColumn.districtId + " = '" + dId
                                + "' order by "+ TableBlock.tableColumn.blockName +" asc ";

        Cursor cur = myDataBase.rawQuery(query, null);
        Log.d("str", query);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                list.add(cur.getString(cur.getColumnIndex(TableBlock.tableColumn.blockName.toString())));
                cur.moveToNext();
            }
        }
        cur.close();
        return list;
    }

    public static ArrayList<String> getGramNameData(String type,String bId) {

        ArrayList<String> List = new ArrayList();

        List.clear();

        String query = "SELECT * from "+ TableVillage.tableName+ " where "
                             + TableVillage.tableColumn.urbanRural + " = '" + type + "' and "
                             + TableVillage.tableColumn.blockId + " = '" + bId
                             + "' order by "+ TableVillage.tableColumn.villageName +" asc";

        Cursor cur = myDataBase.rawQuery(query, null);
        Log.d("query", query);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                List.add(cur.getString(cur.getColumnIndex(TableVillage.tableColumn.villageName.toString())));
                cur.moveToNext();
            }
        }
        cur.close();
        return List;
    }

    public static ArrayList<String> getGramIdData(String type,String bId) {

        ArrayList<String> List = new ArrayList();

        List.clear();

        String query = "SELECT * from "+ TableVillage.tableName+ " where "
                               + TableVillage.tableColumn.urbanRural + " = '" + type + "' and "
                               + TableVillage.tableColumn.blockId + " = '" + bId
                               + "' order by "+ TableVillage.tableColumn.villageName +" asc";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query", query);
        Log.d("cur.getCount()", String.valueOf(cur.getCount()));
        if (cur != null && cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                List.add(cur.getString(cur.getColumnIndex(TableVillage.tableColumn.villageId.toString())));
                cur.moveToNext();
            }
        }
        cur.close();
        return List;
    }

    // for showing list in Doctor round
    public static ArrayList<HashMap<String, String>> getTreatmentList(String babyId,String loungeId, String doctorId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        arrayList.clear();

        String query ="SELECT * from "+ TableTreatment.tableName + " where " + TableTreatment.tableColumn.loungeId + " = '" + loungeId+ "'" +
                " and babyId  = '"+ babyId + "' and status = '1' ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap<>();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.uuid.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.babyId.toString())));
                hashlist.put("treatmentName", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.treatmentName.toString())));

                if (cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.comment.toString()))==null)
                    hashlist.put("comment", "");
                else
                    hashlist.put("comment", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.comment.toString())));

                if (cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.doctorId.toString()))==null)
                    hashlist.put("doctorId", "");
                else
                    hashlist.put("doctorId", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.doctorId.toString())));

                hashlist.put("notePicture", "");

                hashlist.put("doctorName", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.doctorName.toString())));

                if (cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.nurseId.toString()))==null)
                    hashlist.put("nurseId", "");
                else
                    hashlist.put("nurseId", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.nurseId.toString())));
                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.addDate.toString())));
                hashlist.put("status", cur.getString(cur.getColumnIndex(TableTreatment.tableColumn.status.toString())));


                arrayList.add(hashlist);

                cur.moveToNext();
            }
        }
        cur.close();
        return arrayList;
    }


    // for showing list in Doctor round
    public static ArrayList<HashMap<String, String>> getInvestigationList(String babyId,String loungeId, String doctorId) {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        arrayList.clear();

        String query ="SELECT * from "+ TableInvestigation.tableName
                + " where " + TableInvestigation.tableColumn.loungeId + " = '" + loungeId+ "' and babyId  = '"
                + babyId + "' and status = '1' ";

        Cursor cur = myDataBase.rawQuery(query, null);

        Log.d("query-query", query);
        Log.d("query-Count", String.valueOf(cur.getCount()));

        if (cur != null && cur.moveToNext()) {

            for (int i = 0; i < cur.getCount(); i++) {

                HashMap<String, String> hashlist = new HashMap<>();

                hashlist.put("uuid", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.uuid.toString())));
                hashlist.put("babyId", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.babyId.toString())));
                hashlist.put("investigationType", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.investigationType.toString())));
                hashlist.put("investigationName", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.investigationName.toString())));

                if (cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.doctorComment.toString()))==null)
                    hashlist.put("comment", "");
                else
                    hashlist.put("comment", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.doctorComment.toString())));

                hashlist.put("doctorName", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.doctorName.toString())));

                if (cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.doctorId.toString()))==null)
                    hashlist.put("doctorId", "");
                else
                    hashlist.put("doctorId", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.doctorId.toString())));

                if (cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.nurseId.toString()))==null)
                    hashlist.put("nurseId", "");
                else
                    hashlist.put("nurseId", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.nurseId.toString())));

                hashlist.put("addDate", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.addDate.toString())));
                hashlist.put("status", cur.getString(cur.getColumnIndex(TableInvestigation.tableColumn.status.toString())));

                arrayList.add(hashlist);

                cur.moveToNext();

                /*hashlist.put("uuid", uuid);
                hashlist.put("babyId", AppSettings.getString(AppSettings.babyId));
                hashlist.put("investigationType", investigationTypeValue.get(spinnerInvestigationType.getSelectedItemPosition()));
                hashlist.put("investigationName", investigationNameValue.get(spinnerInvestigation.getSelectedItemPosition()));
                hashlist.put("comment", etComment.getText().toString().trim());
                hashlist.put("doctorName", tvSelectDoctor.getText().toString().trim());
                hashlist.put("addDate", String.valueOf(AppUtils.currentTimestampFormat()));*/
            }
        }
        cur.close();
        return arrayList;
    }

}
