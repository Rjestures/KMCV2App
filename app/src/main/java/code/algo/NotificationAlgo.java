package code.algo;

import android.content.ContentValues;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableNotification;
import code.utils.AppUtils;

//Created by Mohammad Faiz on 7th Jan 2019
//Creating algo for Creating Notification for below Activities
//BabyAssessment - 4 times a day -  3 - 9 - 15 - 21 - type-2
//MotherAssessment - 4 times a day - 3 - 9 - 15 - 21- type-1
//Lunch,Breakfast,Dinner - 3 times - 8 - 13- 19 - type-3 - assessmentNumber - 1 for breakfast, 2 for lunch, 3 for dinner -
//Room Temperature - 6 times a day - 4 - 8 - 12 - 16 - 20 -24 - type-4 - NA
//Room Cleanliness - 3 times a day - 8 -14 - 20 - type-5 - NA
//Duty Change - 3 times - 8 - 14 - 20 - type-6 -
public class NotificationAlgo {

    static ArrayList<HashMap<String, String>> babyList = new ArrayList();
    static ArrayList<HashMap<String, String>> pendingNotifications = new ArrayList();
    static ArrayList<HashMap<String, String>> motherList = new ArrayList();

    static String uuid  = UUID.randomUUID().toString();

    public static void createAssessment() {

        Log.d("Testing Service","Testing Service");

        babyList.clear();
        babyList.addAll(DatabaseController.getBabyIdToSync());

        motherList.clear();
        motherList.addAll(DatabaseController.getMotherIdToSync(AppSettings.getString(AppSettings.loungeId)));

        String time = AppUtils.getCurrentTime();

        Log.d("time-before",time);

        String[] parts = time.split(":");

        Log.d("time-after",parts[0]);

        int currentTime= Integer.parseInt(parts[0]);

        if(currentTime==1)
        {
            Log.d("currentTime","Slot 1");

            getPendingNotification();
            getLoungePendingNotification(4);
        }

        if(currentTime==2 )
        {
            Log.d("currentTime","Slot 2");

            getPendingNotification();
        }

        if(currentTime==3)
        {
            Log.d("currentTime","Slot 3");

            for(int i=0;i<babyList.size();i++)
            {
                uuid  = UUID.randomUUID().toString();
                saveNotification(uuid,"1",babyList.get(i).get("motherId"),"1","2");
            }

            for(int i=0;i<motherList.size();i++)
            {
                uuid  = UUID.randomUUID().toString();
                saveNotification(uuid,"1",motherList.get(i).get("motherId"),"1","1");
            }
        }
        else if(currentTime==4)
        {
            Log.d("currentTime","Slot 4");

            uuid  = UUID.randomUUID().toString();
            saveLoungeNotification(uuid,"1","1","4");
        }



        else if(currentTime==5)
        {
            Log.d("currentTime","Slot 5");

            getLoungePendingNotification(4);
        }

        else if(currentTime>=6 && currentTime<8)
        {
            Log.d("currentTime","Slot 6");

            getPendingNotification();
        }

        else if(currentTime==8)
        {
            Log.d("currentTime","Slot 7");

            getPendingNotification();

            uuid  = UUID.randomUUID().toString();
            saveLoungeNotification(uuid,"1","1","3");

            uuid  = UUID.randomUUID().toString();
            saveLoungeNotification(uuid,"2","1","4");

            uuid  = UUID.randomUUID().toString();
            saveLoungeNotification(uuid,"1","1","5");

            uuid  = UUID.randomUUID().toString();
            saveLoungeNotification(uuid,"1","1","6");
        }
        else if(currentTime==9)
        {
            Log.d("currentTime","Slot 8");

            getLoungePendingNotification(3);
            getLoungePendingNotification(4);
            getLoungePendingNotification(5);
            getLoungePendingNotification(6);

            for(int i=0;i<babyList.size();i++)
            {
                uuid  = UUID.randomUUID().toString();
                saveNotification(uuid,"2",babyList.get(i).get("motherId"),"1","2");
            }

            for(int i=0;i<motherList.size();i++)
            {
                uuid  = UUID.randomUUID().toString();
                saveNotification(uuid,"2",motherList.get(i).get("motherId"),"1","1");
            }
        }


        else if(currentTime>=10 && currentTime<12)
        {
            Log.d("currentTime","Slot 9");
        }


        else if(currentTime==12)
        {
            Log.d("currentTime","Slot 10");

            getPendingNotification();

            uuid  = UUID.randomUUID().toString();
            saveLoungeNotification(uuid,"3","1","4");
        }

        else if(currentTime==13)
        {
            Log.d("currentTime","Slot 11");

            getPendingNotification();
            getLoungePendingNotification(4);

            uuid  = UUID.randomUUID().toString();
            saveLoungeNotification(uuid,"2","1","3");
        }
        else if(currentTime==14)
        {
            Log.d("currentTime","Slot 12");

            getLoungePendingNotification(3);
            getPendingNotification();

            uuid  = UUID.randomUUID().toString();
            saveLoungeNotification(uuid,"2","1","5");

            uuid  = UUID.randomUUID().toString();
            saveLoungeNotification(uuid,"2","1","6");
        }
        else if(currentTime==15)
        {
            Log.d("currentTime","Slot 13");

            getLoungePendingNotification(5);
            getLoungePendingNotification(6);

            for(int i=0;i<babyList.size();i++)
            {
                uuid  = UUID.randomUUID().toString();
                saveNotification(uuid,"3",babyList.get(i).get("motherId"),"1","2");
            }

            for(int i=0;i<motherList.size();i++)
            {
                uuid  = UUID.randomUUID().toString();
                saveNotification(uuid,"3",motherList.get(i).get("motherId"),"1","1");
            }
        }
        else if(currentTime==16)
        {
            Log.d("currentTime","Slot 14");

            uuid  = UUID.randomUUID().toString();
            saveLoungeNotification(uuid,"4","1","4");
        }


        else if(currentTime==17)
        {
            Log.d("currentTime","Slot 15");
            getLoungePendingNotification(4);
        }


        else if(currentTime==18)
        {
            Log.d("currentTime","Slot 16");

            getPendingNotification();
        }


        else if(currentTime==19)
        {
            Log.d("currentTime","Slot 17");

            getPendingNotification();

            uuid  = UUID.randomUUID().toString();
            saveLoungeNotification(uuid,"3","1","3");
        }
        else if(currentTime==20)
        {
            Log.d("currentTime","Slot 18");

            getLoungePendingNotification(3);
            getPendingNotification();

            uuid  = UUID.randomUUID().toString();
            saveLoungeNotification(uuid,"5","1","4");

            uuid  = UUID.randomUUID().toString();
            saveLoungeNotification(uuid,"3","1","5");

            uuid  = UUID.randomUUID().toString();
            saveLoungeNotification(uuid,"3","1","6");
        }

        else if(currentTime==21)
        {
            Log.d("currentTime","Slot 19");

            getLoungePendingNotification(4);
            getLoungePendingNotification(5);
            getLoungePendingNotification(6);

            for(int i=0;i<babyList.size();i++)
            {
                uuid  = UUID.randomUUID().toString();
                saveNotification(uuid,"4",babyList.get(i).get("motherId"),"1","2");
            }

            for(int i=0;i<motherList.size();i++)
            {
                uuid  = UUID.randomUUID().toString();
                saveNotification(uuid,"4",motherList.get(i).get("motherId"),"1","1");
            }
        }

        else if(currentTime==22 )
        {
            Log.d("currentTime","Slot 20");
        }

        else if(currentTime==23 )
        {
            Log.d("currentTime","Slot 21");
        }

        else if(currentTime==24 ||currentTime==0)
        {
            Log.d("currentTime","Slot 22");

            getPendingNotification();

            uuid  = UUID.randomUUID().toString();
            saveLoungeNotification(uuid,"6","1","4");
        }
    }

    public static void getPendingNotification() {

        pendingNotifications.clear();
        pendingNotifications.addAll(DatabaseController.getPendingNotifications(2));
        pendingNotifications.addAll(DatabaseController.getPendingNotifications(1));

        for(int i = 0; i< pendingNotifications.size(); i++)
        {
            updateNotification(pendingNotifications.get(i).get("uuid"),"3");
        }
    }

    public static void updateNotification(String uuid, String status) {

        ContentValues mContentValues = new ContentValues();

        mContentValues.put(TableNotification.tableColumn.uuid.toString(), uuid);
        mContentValues.put(TableNotification.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        mContentValues.put(TableNotification.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());
        mContentValues.put(TableNotification.tableColumn.status.toString(), status);

        DatabaseController.updateEqual(mContentValues, TableNotification.tableName,
                TableNotification.tableColumn.uuid.toString(), uuid);
    }

    public static void updateNotificationWithValue(String uuid, String value) {

        ContentValues mContentValues = new ContentValues();

        mContentValues.put(TableNotification.tableColumn.uuid.toString(), uuid);
        mContentValues.put(TableNotification.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        mContentValues.put(TableNotification.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());
        mContentValues.put(TableNotification.tableColumn.value.toString(),value);
        mContentValues.put(TableNotification.tableColumn.status.toString(), "2");

        DatabaseController.updateEqual(mContentValues, TableNotification.tableName,
                TableNotification.tableColumn.uuid.toString(), uuid);
    }

    public static void saveNotification(String uuid,String assessNo, String Id, String status,String type) {

        //DatabaseController.delete(TableNotification.tableName, null,null);

        ContentValues mContentValues = new ContentValues();

        mContentValues.put(TableNotification.tableColumn.uuid.toString(), uuid);
        mContentValues.put(TableNotification.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        mContentValues.put(TableNotification.tableColumn.motherId.toString(), "");
        mContentValues.put(TableNotification.tableColumn.babyId.toString(), "");
        mContentValues.put(TableNotification.tableColumn.type.toString(), type);
        mContentValues.put(TableNotification.tableColumn.shiftingType.toString(), "");
        mContentValues.put(TableNotification.tableColumn.value.toString(), "");
        mContentValues.put(TableNotification.tableColumn.assessmentNumber.toString(), assessNo);
        mContentValues.put(TableNotification.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
        mContentValues.put(TableNotification.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());
        mContentValues.put(TableNotification.tableColumn.syncedDate.toString(),"");
        mContentValues.put(TableNotification.tableColumn.date.toString(), AppUtils.getCurrentDateNew());
        mContentValues.put(TableNotification.tableColumn.time.toString(), AppUtils.getCurrentTime());
        mContentValues.put(TableNotification.tableColumn.status.toString(), status);

        String date = AppUtils.getCurrentDateNew();

        String where = " date = '"+date+"' and assessmentNumber = '"+assessNo+"' and ";

        if(type.equalsIgnoreCase("2"))
        {
            mContentValues.put(TableNotification.tableColumn.babyId.toString(), Id);
            where = where + " motherId ='"+Id+"'";
        }
        else
        {
            mContentValues.put(TableNotification.tableColumn.motherId.toString(), Id);
            where = where + " motherId ='"+Id+"'";
        }

        if(DatabaseController.getCurrentNotifications(where).size()==0)
        {
            DatabaseController.insertUpdateData(mContentValues, TableNotification.tableName,
                    TableNotification.tableColumn.uuid.toString(), uuid);
        }

        //DatabaseController.insertData(mContentValues,TableNotification.tableName);
    }

    public static void saveLoungeNotification(String uuid,String assessNo, String status,String type) {

        ContentValues mContentValues = new ContentValues();

        mContentValues.put(TableNotification.tableColumn.uuid.toString(), uuid);
        mContentValues.put(TableNotification.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        mContentValues.put(TableNotification.tableColumn.motherId.toString(), "");
        mContentValues.put(TableNotification.tableColumn.babyId.toString(), "");
        mContentValues.put(TableNotification.tableColumn.type.toString(), type);
        mContentValues.put(TableNotification.tableColumn.shiftingType.toString(), "");
        mContentValues.put(TableNotification.tableColumn.value.toString(), "");
        mContentValues.put(TableNotification.tableColumn.assessmentNumber.toString(), assessNo);
        mContentValues.put(TableNotification.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
        mContentValues.put(TableNotification.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());
        mContentValues.put(TableNotification.tableColumn.syncedDate.toString(),"");
        mContentValues.put(TableNotification.tableColumn.date.toString(), AppUtils.getCurrentDateNew());
        mContentValues.put(TableNotification.tableColumn.time.toString(), AppUtils.getCurrentTime());
        mContentValues.put(TableNotification.tableColumn.status.toString(), status);

        String date = AppUtils.getCurrentDateNew();

        String where = " date = '"+date+"' and assessmentNumber = '"+assessNo+"' and type = '"+type+"'";

        if(DatabaseController.getCurrentNotifications(where).size()==0)
        {
            DatabaseController.insertUpdateData(mContentValues, TableNotification.tableName,
                    TableNotification.tableColumn.uuid.toString(), uuid);
        }

        //DatabaseController.insertData(mContentValues,TableNotification.tableName);
    }

    public static void getLoungePendingNotification(int type) {

        pendingNotifications.clear();
        pendingNotifications.addAll(DatabaseController.getPendingNotifications(type));

        for(int i = 0; i< pendingNotifications.size(); i++)
        {
            updateNotification(pendingNotifications.get(i).get("uuid"),"3");
        }
    }
}
