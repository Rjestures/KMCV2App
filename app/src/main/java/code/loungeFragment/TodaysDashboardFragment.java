package code.loungeFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kmcapp.android.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.database.AppSettings;
import code.database.DataBaseHelper;
import code.database.DatabaseController;
import code.database.TableBabyMonitoring;
import code.database.TableLounge;
import code.database.TableMotherMonitoring;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;


public class TodaysDashboardFragment extends BaseFragment implements View.OnClickListener {

    //Textview
    TextView tvTotalLiveBirth,tvInfant2500_2000,tvInfant_2000,tvNewAdmissions,tvInfantNotAdmitted,tvInfantRecievedKmcPosition,
            tvBedOccupancy,tvInfantsExclusively,tvInfantsWeighed,tvInfants,tvMother,tvInfantAssessed,tvStable,tvModerate,tvUnstable;

    //ImageView
    ImageView ivLoungeSlot1,ivLoungeSlot2,ivLoungeSlot3,ivDutySlot1,ivDutySlot2,ivDutySlot3;

    //ScrollView
    ScrollView scrollView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_todays_dashboard,container,false);

        init(view);
        setClickListeners();

        DataBaseHelper.copyDatabase(mActivity);

        return view;
    }

    private void init(View view) {

        //ImageView
        ivLoungeSlot1=view.findViewById(R.id.ivLoungeSlot1);
        ivLoungeSlot2=view.findViewById(R.id.ivLoungeSlot2);
        ivLoungeSlot3=view.findViewById(R.id.ivLoungeSlot3);
        ivDutySlot1=view.findViewById(R.id.ivDutySlot1);
        ivDutySlot2=view.findViewById(R.id.ivDutySlot2);
        ivDutySlot3=view.findViewById(R.id.ivDutySlot3);
        
        //ScrollView
        scrollView=view.findViewById(R.id.scrollView);

        tvTotalLiveBirth=view.findViewById(R.id.tvTotalLiveBirth);
        tvInfant2500_2000=view.findViewById(R.id.tvInfant2500_2000);
        tvInfant_2000=view.findViewById(R.id.tvInfant_2000);
        tvNewAdmissions=view.findViewById(R.id.tvNewAdmissions);
        tvInfantNotAdmitted=view.findViewById(R.id.tvInfantNotAdmitted);
        tvInfantRecievedKmcPosition=view.findViewById(R.id.tvInfantRecievedKmcPosition);
        tvBedOccupancy=view.findViewById(R.id.tvBedOccupancy);
        tvInfantsExclusively=view.findViewById(R.id.tvInfantsExclusively);
        tvInfantsWeighed=view.findViewById(R.id.tvInfantsWeighed);
        tvInfants=view.findViewById(R.id.tvInfants);
        tvMother=view.findViewById(R.id.tvMother);
        tvInfantAssessed=view.findViewById(R.id.tvInfantAssessed);
        tvStable=view.findViewById(R.id.tvStable);
        tvModerate=view.findViewById(R.id.tvModerate);
        tvUnstable=view.findViewById(R.id.tvUnstable);

        TextView tvShiftTime=view.findViewById(R.id.tvShiftTime);
        tvShiftTime.setText(AppUtils.getCurrentDateFormatted());

        AppUtils.hideSoftKeyboard(mActivity);
    }

    private void setValues() {

        String totalBirth = DatabaseController.getTotalBirth(1);
        String totalStable = DatabaseController.getTotalBirth(2);
        String totalUnstable = DatabaseController.getTotalBirth(3);

        tvTotalLiveBirth.setText(AppUtils.setSpannable(getString(R.string.totalLiveBirth)+":", totalBirth,"",mActivity));
        tvInfant2500_2000.setText(AppUtils.setSpannable(getString(R.string.totalInfants2500_2000)+":",totalStable,"",mActivity));
        tvInfant_2000.setText(AppUtils.setSpannable(getString(R.string.totalInfants_2000)+":",totalUnstable,"",mActivity));

        int totalBabies = DatabaseController.getAllBabies().size();
        int totalMothers = DatabaseController.getAllMothers().size();

        String where = TableLounge.tableColumn.loungeId + " ='"+ AppSettings.getString(AppSettings.loungeId)+"'";

        int noOfBeds =  0;
        try {
            noOfBeds = Integer.parseInt(DatabaseController.getSpecificValue(TableLounge.tableName,TableLounge.tableColumn.numberOfBeds.toString(),where));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        int available=0;
        if(noOfBeds>0)
        {
            available = noOfBeds - totalBabies;
        }



        if (AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en")) {

            tvBedOccupancy.setText(AppUtils.setSpannable(getString(R.string.bedOccupancy)+": ",
                    String.valueOf(noOfBeds),getString(R.string.outOf)+" "+totalBabies,mActivity));
        } else {

            tvBedOccupancy.setText(AppUtils.setSpannable(getString(R.string.bedOccupancy)+": ",
                    String.valueOf(totalBabies),getString(R.string.outOf)+" "+noOfBeds,mActivity));

        }



        String totalAdmissionToday = DatabaseController.getAdmitted(AppUtils.getCurrentDateFormatted());

        int notAdmitted = Integer.parseInt(totalBirth) - Integer.parseInt(totalAdmissionToday) ;

        tvNewAdmissions.setText(AppUtils.setSpannable(getString(R.string.newAdmissions) +":",totalAdmissionToday,"",mActivity));
        tvInfantNotAdmitted.setText(AppUtils.setSpannable(getString(R.string.totalInfantsNotAdmitted)+":", String.valueOf(notAdmitted),"",mActivity));

        String weighted = DatabaseController.getWeight();

        if (AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en")) {

            tvInfantsWeighed.setText(AppUtils.setSpannable(getString(R.string.infantsWeighed)+":", String.valueOf(weighted)," "+getString(R.string.outOf)+" "+totalBabies,mActivity));


        } else {

            tvInfantsWeighed.setText(AppUtils.setSpannable(getString(R.string.infantsWeighed)+":", String.valueOf(totalBabies)," "+getString(R.string.outOf)+" "+weighted,mActivity));


        }



        String times = DatabaseController.getTotalMethod(mActivity, AppSettings.getString(AppSettings.babyId), AppUtils.getCurrentDateFormatted(),"2");

        if (AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en")) {

            tvInfantsExclusively.setText(AppUtils.setSpannable(getString(R.string.infantsExclusiveBreastfeed)+":", String.valueOf(times)," "+getString(R.string.outOf)+" "+totalBabies,mActivity));


        } else {

            tvInfantsExclusively.setText(AppUtils.setSpannable(getString(R.string.infantsExclusiveBreastfeed)+":", String.valueOf(totalBabies)," "+getString(R.string.outOf)+" "+times,mActivity));


        }



        String sst = String.valueOf(DatabaseController.getSSCAll(AppUtils.getCurrentDateAsPerHospital()).size());


        if (AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en")) {

            tvInfantRecievedKmcPosition.setText(AppUtils.setSpannable(getString(R.string.infantRecievedKmcPosition)+ ":",String.valueOf(sst) ," "+getString(R.string.outOf)+" "+totalBabies,mActivity));


        } else {

            tvInfantRecievedKmcPosition.setText(AppUtils.setSpannable(getString(R.string.infantRecievedKmcPosition)+ ":",String.valueOf(totalBabies) ," "+getString(R.string.outOf)+" "+sst,mActivity));


        }




        String whereNew =  " addDate "+AppUtils.getAddDateAsPerHospital()+ " group by babyId";
        int countAssessment = DatabaseController.getCount(TableBabyMonitoring.tableName,whereNew);

        if (AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en")) {
            tvInfants.setText(AppUtils.setSpannable(getString(R.string.infant)+" "+":", String.valueOf(countAssessment)," "+getString(R.string.outOf)+" "+totalBabies,mActivity));
        } else {
            tvInfants.setText(AppUtils.setSpannable(getString(R.string.infant)+" "+":", String.valueOf(totalBabies)," "+getString(R.string.outOf)+" "+countAssessment,mActivity));
        }




        whereNew = " addDate "+ AppUtils.getAddDateAsPerHospital()+ " group by motherId";
        int motherAssessment = DatabaseController.getCount(TableMotherMonitoring.tableName,whereNew);

        if (AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en")) {
            tvMother.setText(AppUtils.setSpannable(getString(R.string.mother)+" "+":", String.valueOf(motherAssessment)," "+getString(R.string.outOf)+" "+totalMothers,mActivity));
        } else {
            tvMother.setText(AppUtils.setSpannable(getString(R.string.mother)+" "+":", String.valueOf(totalMothers)," "+getString(R.string.outOf)+" "+motherAssessment,mActivity));
        }



        String doctorRound = DatabaseController.getDoctorRoundDateWise(AppUtils.getCurrentDateFormatted());
        tvInfantAssessed.setText(AppUtils.setSpannable(getString(R.string.infantsAssessedByDoctor)+" "+":", doctorRound,"",mActivity));

        ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>(DatabaseController.getAllBabies());

        int danger=0,stable=0,moderate=0;

        for(int i=0;i<arrayList.size();i++)
        {
            int checkPulse=0,checkSpo2=0,checkTemp=0,checkResp=0;

            int pulse = 0;
            try {
                pulse = Integer.parseInt(arrayList.get(i).get("pulseRate"));
                if(pulse<75||pulse>200)
                {
                    checkPulse=0;
                }
                else
                {
                    checkPulse=1;
                }
            } catch (NumberFormatException e) {
                //e.printStackTrace();
                checkPulse=1;
            }


            int spo2=0;
            try {
                spo2 = Integer.parseInt(arrayList.get(i).get("spO2"));

                if(spo2<95)
                {
                    checkSpo2=0;
                }
                else
                {
                    checkSpo2=1;
                }

            } catch (NumberFormatException e) {
                //e.printStackTrace();
                checkSpo2=0;
            }

            float temp=0;
            try {
                temp = Float.parseFloat(arrayList.get(i).get("temperature"));

                if(temp>99.5||temp<95.9)
                {
                    checkTemp=0;
                }
                else
                {
                    checkTemp=1;
                }

            } catch (NumberFormatException e) {
                //e.printStackTrace();
                checkTemp=0;
            }

            float res=0;
            try {
                res = Float.parseFloat(arrayList.get(i).get("respiratoryRate"));

                if(res<30||res>60)
                {
                    checkResp=0;
                }
                else
                {
                    checkResp=1;
                }
            } catch (NumberFormatException e) {
                //e.printStackTrace();
                checkResp=0;
            }

            if(arrayList.get(i).get("isPulseOximatoryDeviceAvailable").equalsIgnoreCase(getString(R.string.yesValue)))
            {
                if(checkPulse==0||checkSpo2==0||checkTemp==0||checkResp==0)
                {
                    danger = danger+1;
                }
                else
                {
                    stable = stable+1;
                }
            }
            else  if(checkTemp==0||checkResp==0)
            {
                danger = danger+1;
            }
            else
            {
                stable = stable+1;
            }
        }

        tvStable.setText(String.valueOf(stable));
        tvModerate.setText(String.valueOf(moderate));
        tvUnstable.setText(String.valueOf(danger));

        arrayList.clear();
        arrayList.addAll(DatabaseController.getLoungeServiceData(AppUtils.getCurrentDateFormatted()));

        ivLoungeSlot1.setImageResource(R.drawable.ic_check_box_grey);
        ivLoungeSlot2.setImageResource(R.drawable.ic_check_box_grey);
        ivLoungeSlot3.setImageResource(R.drawable.ic_check_box_grey);

        for(int i=0;i<arrayList.size();i++) {

            if (arrayList.get(i).get("slot").equals("1") && arrayList.get(i).get("type").equals("2")) {

                if (arrayList.get(i).get("value").equals(getString(R.string.yesValue))) {
                    ivLoungeSlot1.setImageResource(R.drawable.ic_check_box_selected);
                } else {
                    ivLoungeSlot1.setImageResource(R.drawable.ic_cross_new);
                }
            }

            if (arrayList.get(i).get("slot").equals("2") && arrayList.get(i).get("type").equals("2")) {

                if (arrayList.get(i).get("value").equals(getString(R.string.yesValue))) {
                    ivLoungeSlot2.setImageResource(R.drawable.ic_check_box_selected);
                } else {
                    ivLoungeSlot2.setImageResource(R.drawable.ic_cross_new);
                }
            }

            if (arrayList.get(i).get("slot").equals("3") && arrayList.get(i).get("type").equals("2")) {

                if (arrayList.get(i).get("value").equals(getString(R.string.yesValue))) {
                    ivLoungeSlot3.setImageResource(R.drawable.ic_check_box_selected);
                } else {
                    ivLoungeSlot3.setImageResource(R.drawable.ic_cross_new);
                }
            }
        }

        ivDutySlot1.setImageResource(R.drawable.ic_check_box_grey);
        ivDutySlot2.setImageResource(R.drawable.ic_check_box_grey);
        ivDutySlot3.setImageResource(R.drawable.ic_check_box_grey);

        String time = AppUtils.getCurrentTime();

        Log.d("time-before",time);

        String[] parts = time.split(":");

        Log.d("time-after",parts[0]);

        int currentTime= Integer.parseInt(parts[0]);

        if(currentTime>=8&&currentTime<14)
        {
            where = " addDate >= '"+AppUtils.getCurrentDateFormatted()+" 07:30:00' and addDate <= '"+AppUtils.getCurrentDateFormatted()+" 13:29:00'";

            if(DatabaseController.getTodaysDuty(where))
            {
                ivDutySlot1.setImageResource(R.drawable.ic_check_box_selected);
            }
            else
            {
                ivDutySlot1.setImageResource(R.drawable.ic_cross_new);
            }
        }
        else if(currentTime>=14&&currentTime<20)
        {
            where = " addDate >= '"+AppUtils.getCurrentDateFormatted()+" 07:30:00' and addDate <= '"+AppUtils.getCurrentDateFormatted()+" 13:29:00'";

            if(DatabaseController.getTodaysDuty(where))
            {
                ivDutySlot1.setImageResource(R.drawable.ic_check_box_selected);
            }
            else
            {
                ivDutySlot1.setImageResource(R.drawable.ic_cross_new);
            }

            where = " addDate >= '"+AppUtils.getCurrentDateFormatted()+" 13:30:00' and addDate <= '"+AppUtils.getCurrentDateFormatted()+" 19:29:00'";

            if(DatabaseController.getTodaysDuty(where))
            {
                ivDutySlot2.setImageResource(R.drawable.ic_check_box_selected);
            }
            else
            {
                ivDutySlot2.setImageResource(R.drawable.ic_cross_new);
            }
        }
        else if(currentTime>=20&&currentTime<=24)
        {
            where = " addDate >= '"+AppUtils.getCurrentDateFormatted()+" 07:30:00' and addDate <= '"+AppUtils.getCurrentDateFormatted()+" 13:29:00'";

            if(DatabaseController.getTodaysDuty(where))
            {
                ivDutySlot1.setImageResource(R.drawable.ic_check_box_selected);
            }
            else
            {
                ivDutySlot1.setImageResource(R.drawable.ic_cross_new);
            }

            where = " addDate >= '"+AppUtils.getCurrentDateFormatted()+" 13:30:00' and addDate <= '"+AppUtils.getCurrentDateFormatted()+" 19:29:00'";

            if(DatabaseController.getTodaysDuty(where))
            {
                ivDutySlot2.setImageResource(R.drawable.ic_check_box_selected);
            }
            else
            {
                ivDutySlot2.setImageResource(R.drawable.ic_cross_new);
            }

            where = " addDate >= '"+AppUtils.getCurrentDateFormatted()+" 19:30:00' and addDate <= '"+AppUtils.getCurrentDateFormatted()+" 24:00:00'";

            if(DatabaseController.getTodaysDuty(where))
            {
                ivDutySlot3.setImageResource(R.drawable.ic_check_box_selected);
            }
            else
            {
                ivDutySlot3.setImageResource(R.drawable.ic_cross_new);
            }

        }
        else
        {
            where = " addDate >= '"+AppUtils.getCurrentDateYMD(-1)+" 07:30:00' and addDate <= '"+AppUtils.getCurrentDateYMD(-1)+" 13:29:00'";

            if(DatabaseController.getTodaysDuty(where))
            {
                ivDutySlot1.setImageResource(R.drawable.ic_check_box_selected);
            }
            else
            {
                ivDutySlot1.setImageResource(R.drawable.ic_cross_new);
            }

            where = " addDate >= '"+AppUtils.getCurrentDateYMD(-1)+" 13:30:00' and addDate <= '"+AppUtils.getCurrentDateYMD(-1)+" 19:29:00'";

            if(DatabaseController.getTodaysDuty(where))
            {
                ivDutySlot2.setImageResource(R.drawable.ic_check_box_selected);
            }
            else
            {
                ivDutySlot2.setImageResource(R.drawable.ic_cross_new);
            }

            where = " addDate >= '"+AppUtils.getCurrentDateYMD(-1)+" 19:30:00'";

            if(DatabaseController.getTodaysDuty(where))
            {
                ivDutySlot3.setImageResource(R.drawable.ic_check_box_selected);
            }
            else
            {
                ivDutySlot3.setImageResource(R.drawable.ic_cross_new);
            }

        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if(AppSettings.getString(AppSettings.userType).equalsIgnoreCase("1"))
        {
            AppUtils.enableDisable(scrollView,false);

            if (AppUtils.isNetworkAvailable(mActivity)) {
                getStatusApi();
            } else {
                AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
            }
        }
        else
        {
            setValues();
        }
    }

    private void setClickListeners() {



    }


    @Override
    public void onClick(View v) {

    }

    private void getStatusApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {

            jsonData.put("coachId", AppSettings.getString(AppSettings.coachId));
            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            json.put(AppConstants.projectName, jsonData);

            Log.v("getStatusApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.getTodayDashboardData, json,true,true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        tvTotalLiveBirth.setText(AppUtils.setSpannable(getString(R.string.totalLiveBirth)+":", jsonObject.getString("totalLiveBirth"),"",mActivity));
                        tvInfant2500_2000.setText(AppUtils.setSpannable(getString(R.string.totalInfants2500_2000)+":",jsonObject.getString("getTotalAvgWtBaby"),"",mActivity));
                        tvInfant_2000.setText(AppUtils.setSpannable(getString(R.string.totalInfants_2000)+":",jsonObject.getString("getTotalLBWBaby"),"",mActivity));

//


                        if (AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en")) {

                            tvBedOccupancy.setText(AppUtils.setSpannable(getString(R.string.bedOccupancy)+": ",
                                    jsonObject.getString("totalAdmissionBaby"),getString(R.string.outOf)+" "+ jsonObject.getString("bedCount"),mActivity));


                        } else {
                            tvBedOccupancy.setText(AppUtils.setSpannable(getString(R.string.bedOccupancy)+": ",
                                    jsonObject.getString("bedCount"),getString(R.string.outOf)+" "+ jsonObject.getString("totalAdmissionBaby"),mActivity));
                        }


                        tvNewAdmissions.setText(AppUtils.setSpannable(getString(R.string.newAdmissions) +":", jsonObject.getString("newAdmission"),"",mActivity));
                        tvInfantNotAdmitted.setText(AppUtils.setSpannable(getString(R.string.totalInfantsNotAdmitted)+":",jsonObject.getString("notAdmitted"),"",mActivity));


                        if (AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en")) {
                              tvInfantsWeighed.setText(AppUtils.setSpannable(getString(R.string.infantsWeighed)+":", jsonObject.getString("infantsWeightCount")," "+getString(R.string.outOf)+" "+jsonObject.getString("totalAdmissionBaby"),mActivity));
                        } else {
                            tvInfantsWeighed.setText(AppUtils.setSpannable(getString(R.string.infantsWeighed)+":", jsonObject.getString("totalAdmissionBaby")," "+getString(R.string.outOf)+" "+jsonObject.getString("infantsWeightCount"),mActivity));

                        }

                        if (AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en")) {
                            tvInfantsExclusively.setText(AppUtils.setSpannable(getString(R.string.infantsExclusiveBreastfeed)+":", jsonObject.getString("totalExclusiveFeeding")," "+getString(R.string.outOf)+" "+jsonObject.getString("totalAdmissionBaby"),mActivity));
                        } else {
                            tvInfantsExclusively.setText(AppUtils.setSpannable(getString(R.string.infantsExclusiveBreastfeed)+":", jsonObject.getString("totalAdmissionBaby")," "+getString(R.string.outOf)+" "+jsonObject.getString("totalExclusiveFeeding"),mActivity));
                        }

                        tvInfantRecievedKmcPosition.setText(AppUtils.setSpannable(getString(R.string.infantRecievedKmcPosition)+ ":",jsonObject.getString("kmcStatus") ," "+getString(R.string.outOf)+" "+jsonObject.getString("totalAdmissionBaby"),mActivity));


                        if (AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en")) {
                             tvInfants.setText(AppUtils.setSpannable(getString(R.string.infant)+" "+":", jsonObject.getString("onTimeAssessmentBaby")," "+getString(R.string.outOf)+" "+jsonObject.getString("totalAdmissionBaby"),mActivity));
                        } else {
                            tvInfants.setText(AppUtils.setSpannable(getString(R.string.infant)+" "+":", jsonObject.getString("totalAdmissionBaby")," "+getString(R.string.outOf)+" "+jsonObject.getString("onTimeAssessmentBaby"),mActivity));
                        }


                        if (AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en")) {
                            tvMother.setText(AppUtils.setSpannable(getString(R.string.mother)+" "+":", jsonObject.getString("onTimeAssessmentMother")," "+getString(R.string.outOf)+" "+jsonObject.getString("totalAdmissionMother"),mActivity));
                        } else {
                            tvMother.setText(AppUtils.setSpannable(getString(R.string.mother)+" "+":", jsonObject.getString("totalAdmissionMother")," "+getString(R.string.outOf)+" "+jsonObject.getString("onTimeAssessmentMother"),mActivity));
                        }



                        tvInfantAssessed.setText(AppUtils.setSpannable(getString(R.string.infantsAssessedByDoctor)+" "+":",  jsonObject.getString("infantsAssessedByDoctor"),"",mActivity));

                        tvStable.setText( jsonObject.getString("infantHappyStatus"));
                        tvModerate.setText("0");
                        tvUnstable.setText( jsonObject.getString("infantSadStatus"));

                        ivLoungeSlot1.setImageResource(R.drawable.ic_check_box_grey);
                        ivLoungeSlot2.setImageResource(R.drawable.ic_check_box_grey);
                        ivLoungeSlot3.setImageResource(R.drawable.ic_check_box_grey);

                        if ( jsonObject.getString("loungeSanitationShiftOne").equals(getString(R.string.yesValue))) {
                            ivLoungeSlot1.setImageResource(R.drawable.ic_check_box_selected);
                        } else if ( jsonObject.getString("loungeSanitationShiftOne").equals(getString(R.string.noValue))) {
                            ivLoungeSlot1.setImageResource(R.drawable.ic_cross_new);
                        }

                        if ( jsonObject.getString("loungeSanitationShiftTwo").equals(getString(R.string.yesValue))) {
                            ivLoungeSlot2.setImageResource(R.drawable.ic_check_box_selected);
                        } else if ( jsonObject.getString("loungeSanitationShiftTwo").equals(getString(R.string.noValue))) {
                            ivLoungeSlot2.setImageResource(R.drawable.ic_cross_new);
                        }

                        if ( jsonObject.getString("loungeSanitationShiftThree").equals(getString(R.string.yesValue))) {
                            ivLoungeSlot3.setImageResource(R.drawable.ic_check_box_selected);
                        } else if ( jsonObject.getString("loungeSanitationShiftThree").equals(getString(R.string.noValue))) {
                            ivLoungeSlot3.setImageResource(R.drawable.ic_cross_new);
                        }

                        ivDutySlot1.setImageResource(R.drawable.ic_check_box_grey);
                        ivDutySlot2.setImageResource(R.drawable.ic_check_box_grey);
                        ivDutySlot3.setImageResource(R.drawable.ic_check_box_grey);

                        if ( jsonObject.getString("onTimeDutyCheckInShiftOne").equals(getString(R.string.yesValue))) {
                            ivDutySlot1.setImageResource(R.drawable.ic_check_box_selected);
                        } else if ( jsonObject.getString("onTimeDutyCheckInShiftOne").equals(getString(R.string.noValue))) {
                            ivDutySlot1.setImageResource(R.drawable.ic_cross_new);
                        }

                        if ( jsonObject.getString("onTimeDutyCheckInShiftTwo").equals(getString(R.string.yesValue))) {
                            ivDutySlot2.setImageResource(R.drawable.ic_check_box_selected);
                        } else if ( jsonObject.getString("onTimeDutyCheckInShiftTwo").equals(getString(R.string.noValue))) {
                            ivDutySlot2.setImageResource(R.drawable.ic_cross_new);
                        }

                        if ( jsonObject.getString("onTimeDutyCheckInShiftThree").equals(getString(R.string.yesValue))) {
                            ivDutySlot3.setImageResource(R.drawable.ic_check_box_selected);
                        } else if ( jsonObject.getString("onTimeDutyCheckInShiftThree").equals(getString(R.string.noValue))) {
                            ivDutySlot3.setImageResource(R.drawable.ic_cross_new);
                        }
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
