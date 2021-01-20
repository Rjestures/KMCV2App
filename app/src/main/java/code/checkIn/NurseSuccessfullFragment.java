package code.checkIn;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kmcapp.android.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import code.algo.SyncAllRecord;
import code.database.AppSettings;
import code.database.DataBaseHelper;
import code.database.DatabaseController;
import code.database.TableDutyChange;
import code.main.MainActivity;
import code.utils.AppUtils;
import code.view.BaseFragment;

import static code.checkIn.NurseSelfieFragment.MyPREFERENCES;


public class NurseSuccessfullFragment extends BaseFragment implements View.OnClickListener {

    //RelativeLayout
    private RelativeLayout rlNext,rlCircle;

    //TextView
    private TextView tvNurseName, tvTime, tvResult, tvOnOffTime, tvTotalShifts, tvOnTime, tvLate, tvtHappy, tvtFine, tvtFeelingNotGood;

    //ImageView
    private ImageView ivPic, ivSmiley,imgHappy,imgFine,imgFeelingNotGood;

    private String uuid = UUID.randomUUID().toString();
    String profile="",feeling="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nurse_success, container, false);
        initialize(v);
        return v;
    }

    private void initialize(View v) {

        //RelativeLayout
        rlNext = v.findViewById(R.id.rlNext);

        //TextView
        tvNurseName = v.findViewById(R.id.tvNurseName);
        tvTime = v.findViewById(R.id.tvTime);
        tvResult = v.findViewById(R.id.tvResult);
        tvOnOffTime = v.findViewById(R.id.tvOnOffTime);
        tvTotalShifts = v.findViewById(R.id.tvTotalShifts);
        tvOnTime = v.findViewById(R.id.tvOnTime);
        tvLate = v.findViewById(R.id.tvLate);
        tvtHappy = v.findViewById(R.id.tvtHappy);
        tvtFine = v.findViewById(R.id.tvtFine);
        tvtFeelingNotGood = v.findViewById(R.id.tvtFeelingNotGood);

        //ImageView
        ivPic = v.findViewById(R.id.ivPic);
        ivSmiley = v.findViewById(R.id.ivSmiley);
        imgHappy = v.findViewById(R.id.imgHappy);
        imgFine = v.findViewById(R.id.imgFine);
        imgFeelingNotGood = v.findViewById(R.id.imgFeelingNotGood);
        rlCircle = v.findViewById(R.id.rlCircle);

        setValues();

        //setOnClickListener
        rlNext.setOnClickListener(this);
        imgHappy.setOnClickListener(this);
        imgFine.setOnClickListener(this);
        imgFeelingNotGood.setOnClickListener(this);

        if(feeling.equals("")){
            rlCircle.setBackgroundResource(R.drawable.circle_grey);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setValues() {
        tvNurseName.setText(getString(R.string.welcome));
        SharedPreferences sharedpreferences = getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        profile=sharedpreferences.getString("profile", "");

        byte[] decodedString = Base64.decode(profile, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        ivPic.setImageBitmap(decodedByte);

//        String profile =DatabaseController.getNurseProfile(AppSettings.getString(AppSettings.newNurseId));
//        try {
//            Picasso.get().load(profile).into(ivPic);
//        } catch (Exception e) {
//            ivPic.setImageResource(R.mipmap.logo);
//        }

        if (ivPic.getDrawable()==null){
            ivPic.setImageResource(R.mipmap.logo);
        }


        String time = AppUtils.getCurrentTime();
        String time12Hour = AppUtils.getCurrentTimeIn12Hour();

        Log.d("time-before", time);

        String[] parts = time.split(":");

        Log.d("time-after", parts[0]);



        String mainString = "", slotSection1 = "", slotSection2 = "", loungeName = "";

        int newTime = Integer.parseInt(time.replace(":", ""));

        if (newTime >= 730 && newTime <= 800) {
            ivSmiley.setImageResource(R.drawable.ic_happy_smily);
            tvResult.setText(getString(R.string.congratulations));
            tvOnOffTime.setText(getString(R.string.youAreOnTime));
        } else if (newTime >= 1330 && newTime <= 1400) {
            ivSmiley.setImageResource(R.drawable.ic_happy_smily);
            tvResult.setText(getString(R.string.congratulations));
            tvOnOffTime.setText(getString(R.string.youAreOnTime));
        } else if (newTime >= 1930 && newTime <= 2000) {
            ivSmiley.setImageResource(R.drawable.ic_happy_smily);
            tvResult.setText(getString(R.string.congratulations));
            tvOnOffTime.setText(getString(R.string.youAreOnTime));
        } else {
            ivSmiley.setImageResource(R.drawable.ic_sad_smily);
            tvResult.setText(getString(R.string.sorry));
            tvOnOffTime.setText(getString(R.string.youAreLate));
        }

        int currentTime = Integer.parseInt(parts[0]);

        if (newTime < 730) {

            slotSection1 = getString(R.string.slot8pm);
            slotSection2 = getString(R.string.slot8am);

        } else if (newTime < 1330) {

            slotSection1 = getString(R.string.slot8am);
            slotSection2 = getString(R.string.slot2pm);

        } else if (newTime < 1930) {

            slotSection1 = getString(R.string.slot2pm);
            slotSection2 = getString(R.string.slot8pm);

        } else {
            slotSection1 = getString(R.string.slot8pm);
            slotSection2 = getString(R.string.slot8am);

        }



       /* if(currentTime>=8&&currentTime<14)
        {
            slotSection1 = getString(R.string.slot8am);
            slotSection2 = getString(R.string.slot2pm);
        }
        else if(currentTime>=14&&currentTime<20)
        {
            slotSection1 = getString(R.string.slot2pm);
            slotSection2 = getString(R.string.slot8pm);
        }
        else
        {
            slotSection1 = getString(R.string.slot8pm);
            slotSection2 = getString(R.string.slot8am);
        }*/

        loungeName = getString(R.string.shift)
                + " " + getString(R.string.at)
                + " " + DatabaseController.getLoungeNameData(AppSettings.getString(AppSettings.loungeId))
                + " " + getString(R.string.at);

//        mainString = getString(R.string.youHaveCheckedIn)
//                + " " + slotSection1
//                + " " + getString(R.string.to)
//                + " " + slotSection2
//                + " " + loungeName
//                + " " + time12Hour;


        if (AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en")) {

            mainString = getString(R.string.youHaveCheckedIn)
                    + " " + DatabaseController.getLoungeNameData(AppSettings.getString(AppSettings.loungeId))
                    + " " + getString(R.string.at)
                    + " " + time12Hour
                    + " " + getString(R.string.where_your_schedule_time_is)
                    + " " + slotSection1
                    + " " + getString(R.string.to)
                    + " " + slotSection2;

            tvTime.setText(AppUtils.setNewSpannableForTime(mainString, slotSection1, slotSection2, time12Hour, mActivity));
        }
        else
        {

            mainString = "आपने"
                    + " " + DatabaseController.getLoungeNameData(AppSettings.getString(AppSettings.loungeId))
                    + " " + "में"
                    + " " + time12Hour
                    + " " + " पर चेक-इन किया, और आपका निर्धारित समय"
                    + " " + slotSection1
                    + " " + getString(R.string.to)
                    + " " + slotSection2
                    + " " + "है।";

            tvTime.setText(AppUtils.setNewSpannableForTime(mainString, slotSection1, slotSection2, time12Hour, mActivity));

//            mainString = getString(R.string.you)
//                    + " " + DatabaseController.getLoungeNameData(AppSettings.getString(AppSettings.loungeId))
//                    + " " + getString(R.string.in)
//                    + " " + time12Hour
//                    + " " + getString(R.string.at)
//                    + " " + getString(R.string.checked_in)
//                    + " " + getString(R.string.where_your_schedule_time_is)
//                    + " " + slotSection1
//                    + " " + getString(R.string.to)
//                    + " " + slotSection2
//                    + " है|";
//
//            tvTime.setText(AppUtils.setNewSpannableForTimeHindi(mainString, slotSection1, slotSection2, time12Hour, mActivity));
        }


    //

       //tvTime.setText(mainString);

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();
        arrayList.addAll(DatabaseController.getStatsNurses(AppSettings.getString(AppSettings.newNurseId)));

        if (arrayList.size() > 0) {
            tvTotalShifts.setText(arrayList.get(0).get("count"));
            tvOnTime.setText(arrayList.get(0).get("onTime"));
            tvLate.setText(arrayList.get(0).get("late"));
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rlNext:
                if(!feeling.equals("")){
                    saveDutyChange();
                }else {
                    Toast.makeText(mActivity, ""+getString(R.string.feelingError), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.imgHappy:
                imgHappy.setImageResource(R.drawable.ic_greensmile);
                imgFine.setImageResource(R.drawable.ic_grey2_01);
                imgFeelingNotGood.setImageResource(R.drawable.ic_grey3_01);
                feeling="1";
                rlCircle.setBackgroundResource(R.drawable.circle_teal);
                break;

            case R.id.imgFine:
                imgFine.setImageResource(R.drawable.ic_yellowsmile);
                imgHappy.setImageResource(R.drawable.ic_grey1_01);
                imgFeelingNotGood.setImageResource(R.drawable.ic_grey3_01);

                feeling="2";
                rlCircle.setBackgroundResource(R.drawable.circle_teal);
                break;

            case R.id.imgFeelingNotGood:
                imgFeelingNotGood.setImageResource(R.drawable.ic_redsmile);
                imgHappy.setImageResource(R.drawable.ic_grey1_01);
                imgFine.setImageResource(R.drawable.ic_grey2_01);
                feeling="3";
                rlCircle.setBackgroundResource(R.drawable.circle_teal);
                break;

            default:

                break;
        }
    }


    private void saveDutyChange() {
        ContentValues mContentValues = new ContentValues();
        Log.v("jhghbouy",AppSettings.getString(AppSettings.newNurseId)+"CHECHINHAi");
        mContentValues.put(TableDutyChange.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        mContentValues.put(TableDutyChange.tableColumn.serverId.toString(), "");
        mContentValues.put(TableDutyChange.tableColumn.uuid.toString(), uuid);
        mContentValues.put(TableDutyChange.tableColumn.nurseId.toString(), AppSettings.getString(AppSettings.newNurseId));
        mContentValues.put(TableDutyChange.tableColumn.json.toString(), createJson().toString());
        mContentValues.put(TableDutyChange.tableColumn.selfieCheckIn.toString(), AppSettings.getString(AppSettings.newNurseSelfie));
        mContentValues.put(TableDutyChange.tableColumn.type.toString(), "1");
        mContentValues.put(TableDutyChange.tableColumn.isDataSynced.toString(), "0");
        mContentValues.put(TableDutyChange.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
        mContentValues.put(TableDutyChange.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());
        mContentValues.put(TableDutyChange.tableColumn.syncTime.toString(), "");
        mContentValues.put(TableDutyChange.tableColumn.status.toString(), "1");

        DatabaseController.insertUpdateData(mContentValues, TableDutyChange.tableName, TableDutyChange.tableColumn.uuid.toString(), uuid);

        if (AppUtils.isNetworkAvailable(mActivity)) {
            AppSettings.putString(AppSettings.newNurseSelfie, "");
            AppSettings.putString(AppSettings.newNurseId, "");
            AppSettings.putString(AppSettings.uuid, "");
            if (AppUtils.isNetworkAvailable(mActivity)) {
                AppSettings.putString(AppSettings.syncTime, AppUtils.currentTimestampFormat());
                SyncAllRecord.postDutyChange(mActivity);
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        } else {
            AppSettings.putString(AppSettings.newNurseSelfie, "");
            AppSettings.putString(AppSettings.newNurseId, "");
            AppSettings.putString(AppSettings.uuid, "");
            AppUtils.showToastSort(mActivity, getString(R.string.dataSaved));
        }

        new Thread(new Runnable() {
            public void run(){
                DataBaseHelper.copyDatabase(mActivity);
            }
        }).start();


        AppSettings.putString(AppSettings.from, "0");
        startActivity(new Intent(mActivity, MainActivity.class));
        getActivity().finishAffinity();
    }

    private JSONObject createJson() {

        JSONObject jsonData = new JSONObject();

        try {
            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("nurseId", AppSettings.getString(AppSettings.newNurseId));
            jsonData.put("selfie",  AppSettings.getString(AppSettings.newNurseSelfie));
            // jsonData.put("selfie","");
            jsonData.put("localId", uuid);
            jsonData.put("localDateTime", AppUtils.currentTimestampFormat());
            jsonData.put("latitude", AppSettings.getString(AppSettings.latitude));
            jsonData.put("longitude", AppSettings.getString(AppSettings.longitude));
            jsonData.put("feeling", feeling);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonData;
    }
}
