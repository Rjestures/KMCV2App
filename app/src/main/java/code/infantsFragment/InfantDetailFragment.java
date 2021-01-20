package code.infantsFragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kmcapp.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import code.algo.SyncAllRecord;
import code.algo.SyncBabyRecord;
import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableBabyAdmission;
import code.database.TableBabyMonitoring;
import code.database.TableLoungeServices;
import code.database.TableWeight;
import code.loungeFragment.LoungeServicesFragment;
import code.main.BabyDetailActivity;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

import static code.database.TableBabyAdmission.tableColumn.whatisKmc;


public class InfantDetailFragment extends BaseFragment implements View.OnClickListener {

    //LinearLayout
    private LinearLayout llToday,llCaseSheet,llGraphs,llTodayMain,llCaseSheetMain,llGraphsMain,llGrowthMain,llRespiratoryRateMain,
            llPulseOxidationMain,llKMCPosition,llKMCMonitoring,llKMCNutrition,llKMCRespect,llKMCHydiene,llKMCWhatis;

    //View
    private View viewToday,viewCaseSheet,viewGraphs;

    //TextView
    private TextView tvToday,tvCaseSheet,tvGraphs,tvGrowthMonitoring,tvRespiratoryRate,tvPulseOxidation,tvPrescribedTime,tvReceivedTime,
            tvPrescribedNutrition,tvFluids,tvModeFeeding,tvTotalQuantity,tvMedication,tvFrequency,tvVitamidDmL1,tvVitamidDmL2,tvRoutineAssessment,
            tvWeightChangeBirth,tvInfantWeightBirth,tvWeightChangePrevious,tvInfantWeightPrevious,tvPrescribedTimeCS,tvReceivedTimeCS,
            tvNutritionQuantity,tvNutritionTime,tvVitamidDmL1CS,tvVitamidDmL2CS,tvPrescribedNutritionCS,tvReceivedNutritionCS,tvDoctorNotes;

    //ImageView
    private ImageView ivKmcEmoji,ivNutrition,ivVaccination,ivDailyWeighing,ivRoutineAssessment,ivKmcPosition,ivKmcMonitoring,ivKmcNutrition,ivKmcRespect,
            ivKmcHygiene,ivKmcWhatis,ivInfantGrowthStatus,ivKmcEmojiCS,ivNutritionCS,ivDailyWeighingCS,ivRoutineAssessmentCS,ivExcBreastFeed,ivVaccinationCS,ivMedicationEmoji,
            ivCounselling;

    //EditText
    private EditText etOrdered,etSampleCollected,etReportAvail,etOrderedCS,etSampleCollectedCS,
            etReportAvailCS;

    //RelativeLayout
    private RelativeLayout rlKmc,rlNutrition,rlMedication,rlWeight;

    public   static int kmcPostion=0;
    public   String getWpostion="",getpostion="",getNpostion="", getHpostion="",getMpostion="",getRpostion="";

    String kmcCheckResult="";
    int birthWeight,currentWeight;
    //ArrayList
    private ArrayList arrayListDates = new ArrayList<String>();

    //TextView
    private TextView tvSF1,tvSF2,tvSF3,tvSF4,tvSF1CS,tvSF2CS,tvSF3CS,tvSF4CS;

    private ArrayList<HashMap<String, String>> arrayListTests = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> arrayListAssessment = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> arrayListBabyWeight = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> arrayListFeeding = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> arrayListMedicine = new ArrayList<HashMap<String, String>>();

    private ArrayList<HashMap<String, String>> babyDetailList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> respiratoryRateList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> temperatureList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> breastFeedingList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> skinToSkinList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> babyWeightList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> babyPulseList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> babySpo2List = new ArrayList<HashMap<String, String>>();

    private ArrayList<String> nurseId = new ArrayList<String>();
    private ArrayList<String> nurseName = new ArrayList<String>();

    //RecyclerView
    private RecyclerView rvDates;

    //Adapter
    private Adapter adapter;

    //View
    private View view;

    private String selectedDate = "",kmcPosition="",kmcMonitoring="",kmcNutrition="",kmcRespect="",kmcHygiene="",kmcWhatis="", whatisKMC="",uuid="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_infant_details,container,false);

      //  init(view);

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void init(View view) {

        //RelativeLayout
        rlKmc =view.findViewById(R.id.rlKmc);
        rlNutrition =view.findViewById(R.id.rlNutrition);
        rlMedication=view.findViewById(R.id.rlMedication);
        rlWeight=view.findViewById(R.id.rlWeight);

        //TextView
        tvSF1=view.findViewById(R.id.tvSF1);
        tvSF2=view.findViewById(R.id.tvSF2);
        tvSF3=view.findViewById(R.id.tvSF3);
        tvSF4=view.findViewById(R.id.tvSF4);
        tvSF1CS=view.findViewById(R.id.tvSF1CS);
        tvSF2CS=view.findViewById(R.id.tvSF2CS);
        tvSF3CS=view.findViewById(R.id.tvSF3CS);
        tvSF4CS=view.findViewById(R.id.tvSF4CS);

        //LinearLayout
        llToday=view.findViewById(R.id.llToday);
        llCaseSheet=view.findViewById(R.id.llCaseSheet);
        llGraphs=view.findViewById(R.id.llGraphs);
        llTodayMain=view.findViewById(R.id.llTodayMain);
        llCaseSheetMain=view.findViewById(R.id.llCaseSheetMain);
        llGraphsMain=view.findViewById(R.id.llGraphsMain);
        llGrowthMain=view.findViewById(R.id.llGrowthMain);
        llRespiratoryRateMain=view.findViewById(R.id.llRespiratoryRateMain);
        llPulseOxidationMain=view.findViewById(R.id.llPulseOxidationMain);
        llKMCPosition=view.findViewById(R.id.llKMCPosition);
        llKMCMonitoring=view.findViewById(R.id.llKMCMonitoring);
        llKMCNutrition=view.findViewById(R.id.llKMCNutrition);
        llKMCRespect=view.findViewById(R.id.llKMCRespect);
        llKMCHydiene=view.findViewById(R.id.llKMCHydiene);
        llKMCWhatis=view.findViewById(R.id.llKMCWhatis);

        //ImageView
        ivKmcEmoji=view.findViewById(R.id.ivKmcEmoji);
        ivNutrition=view.findViewById(R.id.ivNutrition);
        ivVaccination=view.findViewById(R.id.ivVaccination);
        ivDailyWeighing=view.findViewById(R.id.ivDailyWeighing);
        ivRoutineAssessment=view.findViewById(R.id.ivRoutineAssessment);
        ivKmcPosition=view.findViewById(R.id.ivKmcPosition);
        ivKmcMonitoring=view.findViewById(R.id.ivKmcMonitoring);
        ivKmcNutrition=view.findViewById(R.id.ivKmcNutrition);
        ivKmcRespect=view.findViewById(R.id.ivKmcRespect);
        ivKmcHygiene=view.findViewById(R.id.ivKmcHygiene);
        ivKmcWhatis=view.findViewById(R.id.ivKmcWhatis);

        ivInfantGrowthStatus=view.findViewById(R.id.ivInfantGrowthStatus);
        ivKmcEmojiCS=view.findViewById(R.id.ivKmcEmojiCS);
        ivNutritionCS=view.findViewById(R.id.ivNutritionCS);
        ivExcBreastFeed=view.findViewById(R.id.ivExcBreastFeed);
        ivVaccinationCS=view.findViewById(R.id.ivVaccinationCS);
        ivMedicationEmoji=view.findViewById(R.id.ivMedicationEmoji);
        ivDailyWeighingCS=view.findViewById(R.id.ivDailyWeighingCS);
        ivRoutineAssessmentCS=view.findViewById(R.id.ivRoutineAssessmentCS);
        ivCounselling=view.findViewById(R.id.ivCounselling);

        //TextView
        tvToday=view.findViewById(R.id.tvToday);
        tvCaseSheet=view.findViewById(R.id.tvCaseSheet);
        tvGraphs=view.findViewById(R.id.tvGraphs);
        tvGrowthMonitoring=view.findViewById(R.id.tvGrowthMonitoring);
        tvRespiratoryRate=view.findViewById(R.id.tvRespiratoryRate);
        tvPulseOxidation=view.findViewById(R.id.tvPulseOxidation);
        tvPrescribedTime=view.findViewById(R.id.tvPrescribedTime);
        tvReceivedTime=view.findViewById(R.id.tvReceivedTime);
        tvPrescribedNutrition=view.findViewById(R.id.tvPrescribedNutrition);
        tvFluids=view.findViewById(R.id.tvFluids);
        tvModeFeeding=view.findViewById(R.id.tvModeFeeding);
        tvTotalQuantity=view.findViewById(R.id.tvTotalQuantity);
        tvFrequency=view.findViewById(R.id.tvFrequency);
        tvVitamidDmL1=view.findViewById(R.id.tvVitamidDmL1);
        tvVitamidDmL2=view.findViewById(R.id.tvVitamidDmL2);
        tvRoutineAssessment=view.findViewById(R.id.tvRoutineAssessment);
        tvPrescribedTimeCS=view.findViewById(R.id.tvPrescribedTimeCS);
        tvReceivedTimeCS=view.findViewById(R.id.tvReceivedTimeCS);
        tvNutritionQuantity=view.findViewById(R.id.tvNutritionQuantity);
        tvNutritionTime=view.findViewById(R.id.tvNutritionTime);
        tvVitamidDmL1CS=view.findViewById(R.id.tvVitamidDmL1CS);
        tvVitamidDmL2CS=view.findViewById(R.id.tvVitamidDmL2CS);
        tvPrescribedNutritionCS=view.findViewById(R.id.tvPrescribedNutritionCS);
        tvMedication=view.findViewById(R.id.tvMedication);
        tvReceivedNutritionCS=view.findViewById(R.id.tvReceivedNutritionCS);
        tvDoctorNotes=view.findViewById(R.id.tvDoctorNotes);

        tvWeightChangeBirth=view.findViewById(R.id.tvWeightChangeBirth);
        tvInfantWeightBirth=view.findViewById(R.id.tvInfantWeightBirth);
        tvWeightChangePrevious=view.findViewById(R.id.tvWeightChangePrevious);
        tvInfantWeightPrevious=view.findViewById(R.id.tvInfantWeightPrevious);

        //EditText
        etOrdered=view.findViewById(R.id.etOrdered);
        etSampleCollected=view.findViewById(R.id.etSampleCollected);
        etReportAvail=view.findViewById(R.id.etReportAvail);
        etOrderedCS=view.findViewById(R.id.etOrderedCS);
        etSampleCollectedCS=view.findViewById(R.id.etSampleCollectedCS);
        etReportAvailCS=view.findViewById(R.id.etReportAvailCS);

        //View
        viewToday=view.findViewById(R.id.viewToday);
        viewCaseSheet=view.findViewById(R.id.viewCaseSheet);
        viewGraphs=view.findViewById(R.id.viewGraphs);

        //RelativeLayout
        rvDates = view.findViewById(R.id.rvDates);

        setDefault();
        viewToday.setVisibility(View.VISIBLE);
        llTodayMain.setVisibility(View.VISIBLE);
        tvToday.setTextColor(getResources().getColor(R.color.blackNew));
        llToday.setBackgroundResource(R.color.white);

        setListeners();

        if(AppSettings.getString(AppSettings.userType).equalsIgnoreCase("0"))
        {
            //String delDate = DatabaseController.getDelDate(AppSettings.getString(AppSettings.babyId));
            String delDate = DatabaseController.getAddDate(AppSettings.getString(AppSettings.babyId));

            showDates(delDate);

            setOfflineGraph();

            generateWeightData(view);
            generateKMCData(view);
            generateFeedingData(view);
            generateRespiratoryData(view);
            generateTempData(view);
            generatePulseData(view);
            generateSpo2Data(view);

            selectedDate = AppUtils.getCurrentDateFormatted();

            setValues(AppUtils.getCurrentDateFormatted());
            setValuesViaDate(AppUtils.getCurrentDateFormatted());

            ArrayList<HashMap<String, String>> arrayListComment
                    = new ArrayList<HashMap<String, String>>(DatabaseController.getCommentData(AppSettings.getString(AppSettings.babyId), "2"));

            if(arrayListComment.size()>0)
            {
                tvDoctorNotes.setText(arrayListComment.get(0).get("doctorName") +" - "+arrayListComment.get(0).get("doctorComment")+"\n"+
                        getString(R.string.totalComments)+" - "+arrayListComment.size());
            }
        }
        else {
            selectedDate = AppUtils.getCurrentDateFormatted();

            if(AppUtils.isNetworkAvailable(mActivity)) {
                getBabyDetailApi(view);
            }
            else
            {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        }




        if(!AppSettings.getString(AppSettings.WhatisKmcViewer).isEmpty() && !AppSettings.getString(AppSettings.WhatisKmcposition).isEmpty() && !AppSettings.getString(AppSettings.WhatisKmcposition).equals("")){
            whatisKMC=AppSettings.getString(AppSettings.WhatisKmcViewer) ;
        }
        if(!AppSettings.getString(AppSettings.kmcPositionViewer).isEmpty() && !AppSettings.getString(AppSettings.kmcPositionposition).isEmpty() && !AppSettings.getString(AppSettings.kmcPositionposition).equals("")){
            kmcPosition=AppSettings.getString(AppSettings.kmcPositionViewer) ;
        }
        if(!AppSettings.getString(AppSettings.kmcNutitionViewer).isEmpty() && !AppSettings.getString(AppSettings.kmcNutitionposition).isEmpty()  && !AppSettings.getString(AppSettings.kmcNutitionposition).equals("")){
            kmcNutrition=AppSettings.getString(AppSettings.kmcNutitionViewer) ;
        }
        if(!AppSettings.getString(AppSettings.kmcHygieneViewer).isEmpty() && !AppSettings.getString(AppSettings.kmcHygieneposition).isEmpty() && !AppSettings.getString(AppSettings.kmcHygieneposition).equals("")){
            kmcHygiene=AppSettings.getString(AppSettings.kmcHygieneViewer) ;
        }
        if(!AppSettings.getString(AppSettings.kmcMonitoringViewer).isEmpty() && !AppSettings.getString(AppSettings.kmcMonitoringposition).isEmpty() && !AppSettings.getString(AppSettings.kmcMonitoringposition).equals("")){
            kmcMonitoring=AppSettings.getString(AppSettings.kmcMonitoringViewer) ;
        }
        if(!AppSettings.getString(AppSettings.kmcRespectViewer).isEmpty() && !AppSettings.getString(AppSettings.kmcRespectposition).isEmpty() && !AppSettings.getString(AppSettings.kmcRespectposition).equals("") ){
            kmcRespect=AppSettings.getString(AppSettings.kmcRespectViewer) ;
        }

        if(AppSettings.getString(AppSettings.WhatisKmcViewer).equals("Yes") && AppSettings.getString(AppSettings.WhatisKmcposition).equals("1")){
            showConfirmationDialog(String.valueOf(kmcPostion),ivKmcWhatis,whatisKMC);
        }
        if(AppSettings.getString(AppSettings.kmcPositionViewer).equals("Yes") && AppSettings.getString(AppSettings.kmcPositionposition).equals("2")){
            showConfirmationDialog(String.valueOf(kmcPostion),ivKmcPosition,kmcPosition);
        }
        if(AppSettings.getString(AppSettings.kmcNutitionViewer).equals("Yes") && AppSettings.getString(AppSettings.kmcNutitionposition).equals("3")){
            showConfirmationDialog(String.valueOf(kmcPostion),ivKmcNutrition,kmcNutrition);
        }

        if(AppSettings.getString(AppSettings.kmcHygieneViewer).equals("Yes") && AppSettings.getString(AppSettings.kmcHygieneposition).equals("4")){
            showConfirmationDialog(String.valueOf(kmcPostion),ivKmcHygiene,kmcHygiene);
        }
        if(AppSettings.getString(AppSettings.kmcMonitoringViewer).equals("Yes") && AppSettings.getString(AppSettings.kmcMonitoringposition).equals("5")){

            showConfirmationDialog(String.valueOf(kmcPostion),ivKmcMonitoring,kmcMonitoring);
        }
        if(AppSettings.getString(AppSettings.kmcRespectViewer).equals("Yes") && AppSettings.getString(AppSettings.kmcRespectposition).equals("6")){
//                ivKmcRespect.setImageResource(R.drawable.ic_check_box_selected);
            showConfirmationDialog(String.valueOf(kmcPostion),ivKmcRespect,kmcRespect);
        }

    }

    private void setValues(String date) {

        tvReceivedTime.setText("0");
        ivKmcEmoji.setImageResource(R.drawable.ic_sad_smily);
        ivDailyWeighing.setImageResource(R.drawable.ic_sad_smily);
        etOrdered.setText("0");
        etSampleCollected.setText("0");
        etReportAvail.setText("0");
        tvSF1.setText(getString(R.string.notApplicableShortValue));
        tvSF2.setText(getString(R.string.notApplicableShortValue));
        tvSF3.setText(getString(R.string.notApplicableShortValue));
        tvSF4.setText(getString(R.string.notApplicableShortValue));
        ivRoutineAssessment.setImageResource(R.drawable.ic_sad_smily);

        String duration = DatabaseController.getSSC(AppSettings.getString(AppSettings.babyId),date);

        Log.d("duration",duration);

        String unit = "";
        if(duration.equals("0"))
        {
            unit = getString(R.string.hour);
        }
        else
        {
            String[] parts = duration.split(":");

            Log.d("time-hour",parts[0]);
            Log.d("time-min",parts[1]);

            if(parts[0].equals("0")||parts[0].equals("1")||parts[0].equals("01"))
            {
                unit = getString(R.string.hour);
            }
            else
            {
                unit = getString(R.string.hours);
            }
        }

        tvReceivedTime.setText(duration+" "+unit);

        if(duration.contains(":"))
        {
            String[] parts = duration.split(":");

            Log.d("hour",parts[0]);
            Log.d("min",parts[1]);

            int hour = Integer.parseInt(parts[0]);
            int min = Integer.parseInt(parts[1]);

            if(hour<11)
            {
                ivKmcEmoji.setImageResource(R.drawable.ic_sad_smily);
            }
            else   if(hour>11&&hour<21)
            {
                ivKmcEmoji.setImageResource(R.drawable.ic_neutral_status);
            }
            else
            {
                ivKmcEmoji.setImageResource(R.drawable.ic_happy_smily);
            }
        }
        else
        {
            ivKmcEmoji.setImageResource(R.drawable.ic_sad_smily);
        }


        boolean weigth = DatabaseController.checkRecordExistWhere(TableWeight.tableName,
                TableWeight.tableColumn.weightDate+" = '"+ date+"' and "+TableWeight.tableColumn.babyId +" = '"+AppSettings.getString(AppSettings.babyId)+"'");

        if(weigth)
        {
            ivDailyWeighing.setImageResource(R.drawable.ic_happy_smily);
        }
        else
        {
            ivDailyWeighing.setImageResource(R.drawable.ic_sad_smily);
        }

        arrayListTests.clear();
        arrayListTests.addAll(DatabaseController.getInvestigationSearchData(AppSettings.getString(AppSettings.babyId),date,""));

        int sample=0,report=0;

        for (int i=0;i<arrayListTests.size();i++)
        {
            if(!arrayListTests.get(i).get("sampleTakenBy").isEmpty())
            {
               sample = sample+1;
            }

            if(!arrayListTests.get(i).get("result").isEmpty())
            {
                report = report+1;
            }
        }

        etOrdered.setText(String.valueOf(arrayListTests.size()));
        etSampleCollected.setText(String.valueOf(sample));
        etReportAvail.setText(String.valueOf(report));

        arrayListAssessment.clear();
        arrayListAssessment.addAll(DatabaseController.getAssessmentDataNew(AppSettings.getString(AppSettings.babyId),date));

        if(arrayListAssessment.size()>0)
        {
            int checkPulse=0,checkSpo2=0,checkTemp=0,checkResp=0;

            int pulse = 0;
            try {

                if(!arrayListAssessment.get(0).get("babyPulseRate").isEmpty())
                {
                    pulse = Integer.parseInt(arrayListAssessment.get(0).get("babyPulseRate"));
                    if(pulse<75||pulse>200)
                    {
                        checkPulse=0;
                    }
                    else
                    {
                        checkPulse=1;
                    }
                }

            } catch (NumberFormatException e) {
                //e.printStackTrace();
                checkPulse=1;
            }


            int spo2=0;
            try {

                if(!arrayListAssessment.get(0).get("babySpO2").isEmpty())
                {
                    spo2 = Integer.parseInt(arrayListAssessment.get(0).get("babySpO2"));

                    if(spo2<95)
                    {
                        checkSpo2=0;
                    }
                    else
                    {
                        checkSpo2=1;
                    }
                }
            } catch (NumberFormatException e) {
                //e.printStackTrace();
                checkSpo2=0;
            }

            float temp=0;
            try {
                try {
                    temp = Float.parseFloat(arrayListAssessment.get(0).get("babyTemperature"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

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
                try {
                    res = Float.parseFloat(arrayListAssessment.get(0).get("babyRespiratoryRate"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

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

            if(arrayListAssessment.get(0).get("babyRespiratoryRate").equals(getString(R.string.degreeCValue)))
            {
                tvSF1.setText(getString(R.string.temperature)+": "+arrayListAssessment.get(0).get("babyTemperature") +" "+ getString(R.string.degreeC));
            }
            else
            {
                tvSF1.setText(getString(R.string.temperature)+": "+arrayListAssessment.get(0).get("babyTemperature") +" "+ getString(R.string.degreeF));
            }

            tvSF2.setText(getString(R.string.pulseOxidation)+": "+getString(R.string.notApplicableShortValue));
            tvSF3.setText(getString(R.string.spo2)+": "+getString(R.string.notApplicableShortValue));
            tvSF4.setText(getString(R.string.respiratoryRate)+": "+res+" "+getString(R.string.min));

            if(arrayListAssessment.get(0).get("isPulseOximatoryDeviceAvailable").equalsIgnoreCase(getString(R.string.yesValue)))
            {
                tvSF2.setText(getString(R.string.pulseOxidation)+": "+pulse+" "+getString(R.string.bpm));
                tvSF3.setText(getString(R.string.spo2)+": "+spo2 +"%");

                if(checkPulse==0||checkSpo2==0||checkTemp==0||checkResp==0)
                {
                    ivRoutineAssessment.setImageResource(R.drawable.ic_sad_smily);
                }
                else
                {
                    ivRoutineAssessment.setImageResource(R.drawable.ic_happy_smily);
                }
            }
            else  if(checkTemp==0||checkResp==0)
            {
                ivRoutineAssessment.setImageResource(R.drawable.ic_sad_smily);
            }
            else
            {
                ivRoutineAssessment.setImageResource(R.drawable.ic_happy_smily);
            }
        }

        setFeeding(date);

    }

    @SuppressLint("SetTextI18n")
    private void setFeeding(String date) {

        ivNutrition.setImageResource(R.drawable.ic_sad_smily);
        tvTotalQuantity.setText("0");
        tvFluids.setText(getString(R.string.notApplicableShortValue));
        tvModeFeeding.setText(getString(R.string.notApplicableShortValue));
        tvFrequency.setText("0");
        tvMedication.setText(getString(R.string.notApplicableShortValue));

        arrayListBabyWeight.clear();
        arrayListBabyWeight.addAll(DatabaseController.getBabyWeightViaId(AppSettings.getString(AppSettings.babyId)
                ,DatabaseController.getDelDate(AppSettings.getString(AppSettings.babyId))));

        int days = Integer.parseInt((AppUtils.getDateDiff(DatabaseController.getDelDate(AppSettings.getString(AppSettings.babyId)))));

        for(int i=arrayListBabyWeight.size();i>=0;i--)
        {
            if(!arrayListBabyWeight.get(i-1).get("babyWeight").equalsIgnoreCase("0"))
            {
                float  quantity = 0;
                try {
                    quantity = AppUtils.getQuantity(Integer.parseInt(arrayListBabyWeight.get(i-1).get("babyWeight")),days);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                /*quantity = AppUtils.getQuantity(Integer.parseInt(arrayListBabyWeight.get(i-1).get("babyWeight")),
                        Integer.parseInt(arrayListBabyWeight.get(i-1).get("day")));*/

                Log.d("days-quantity", String.valueOf(days));
                Log.d("days-weight", String.valueOf(arrayListBabyWeight.get(i-1).get("babyWeight")));
                Log.d("days-quantity", String.valueOf(quantity));

                Float sum = Float.parseFloat(DatabaseController.getTotalQuantity(
                        AppSettings.getString(AppSettings.babyId),date));

                int  perc = (int) ((sum/quantity)*100);

                if(perc<=75)
                {
                    ivNutrition.setImageResource(R.drawable.ic_sad_smily);
                }
                else if(perc>76&&perc<=90)
                {
                    ivNutrition.setImageResource(R.drawable.ic_neutral_status);
                }
                else if(perc>91)
                {
                    ivNutrition.setImageResource(R.drawable.ic_happy_smily);
                }

                float hourly = quantity/12;

                try {
                    tvPrescribedNutrition.setText(quantity+" "+getString(R.string.ml)
                                                          +" ("+new DecimalFormat("##.##").format(hourly)
                                                          +" "+getString(R.string.ml) +" "+getString(R.string.
                            every2hours));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                tvTotalQuantity.setText(DatabaseController.getTotalQuantity(AppSettings.getString(AppSettings.babyId),date)+" "+getString(R.string.ml));
                break;
            }
        }

        String times = DatabaseController.getTotalMethod(mActivity, AppSettings.getString(AppSettings.babyId), date,"1");

        if(times.equals("0")||times.equals("1"))
        {
            times = times + " "+ getString(R.string.noOfTime);
        }
        else
        {
            times = times + " "+ getString(R.string.noOfTimes);
        }

        arrayListFeeding.clear();
        arrayListFeeding.addAll(DatabaseController.getFeedingData(AppSettings.getString(AppSettings.babyId),date));

        if(arrayListFeeding.size()>0)
        {
            tvFluids.setText(arrayListFeeding.get(0).get("fluid"));
            tvModeFeeding.setText(arrayListFeeding.get(0).get("method"));
            tvFrequency.setText(times);
        }

        arrayListMedicine.clear();
        arrayListMedicine.addAll(DatabaseController.getMedicineDataViaDate(AppSettings.getString(AppSettings.babyId),date));

        if(arrayListMedicine.size()>0)
        {
            tvMedication.setText(arrayListMedicine.get(0).get("name"));
        }
    }

    private void setValuesViaDate(String date) {

        tvReceivedTimeCS.setText("0");
        ivKmcEmojiCS.setImageResource(R.drawable.ic_sad_smily);
        ivDailyWeighingCS.setImageResource(R.drawable.ic_sad_smily);
        etOrderedCS.setText("0");
        etSampleCollectedCS.setText("0");
        etReportAvailCS.setText("0");
        tvSF1CS.setText(getString(R.string.notApplicableShortValue));
        tvSF2CS.setText(getString(R.string.notApplicableShortValue));
        tvSF3CS.setText(getString(R.string.notApplicableShortValue));
        tvSF4CS.setText(getString(R.string.notApplicableShortValue));
        tvInfantWeightBirth.setText(getString(R.string.notApplicableShortValue));
        tvInfantWeightPrevious.setText(getString(R.string.notApplicableShortValue));
        ivRoutineAssessmentCS.setImageResource(R.drawable.ic_sad_smily);

        if(babyDetailList.size()>0)
        {
            try{

                 birthWeight = Integer.parseInt(babyDetailList. get(0).get("birthWeight"));
                Log.v("lkhsqhsq",AppSettings.getString(AppSettings.babyId)+" "+ babyDetailList.get(0).get("currentWeight"));

                 currentWeight = Integer.parseInt(babyDetailList.get(0).get("currentWeight"));
                //  int currentWeight = 2500;
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
            int gainLoss = currentWeight - birthWeight;


            if(gainLoss>0)
            {

                if(AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en"))
                {
                    tvInfantWeightBirth.setText(getString(R.string.infantHasGained)+" "+ gainLoss +" "+ getString(R.string.grams) +" "+getString(R.string.sinceBirthWeight));
                }
                else
                {
                    tvInfantWeightBirth.setText("शिशु ने जन्म के बाद से "+gainLoss+" ग्राम प्राप्त किया है|");

                }
            }
            else
            {
                if(gainLoss==0)
                {
                    if(AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en"))
                    {
                        tvInfantWeightBirth.setText(getString(R.string.infantHasGained)+" "+ gainLoss +" "+ getString(R.string.grams) +" "+getString(R.string.sinceBirthWeight));

                    }
                    else
                    {
                        tvInfantWeightBirth.setText("शिशु ने जन्म के बाद से "+gainLoss+" ग्राम प्राप्त किया है|");

                    }

                }
                else
                {
                    String weight = String.valueOf(gainLoss);
                    weight = weight.replace("-","");
                    if(AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en"))
                    {

                        tvInfantWeightBirth.setText(getString(R.string.infantHasLoss)+" "+ weight +" "+ getString(R.string.grams) +" "+getString(R.string.sinceBirthWeight));
                    }
                    else
                    {
                        tvInfantWeightBirth.setText("शिशु ने जन्म के बाद से "+gainLoss+" ग्राम प्राप्त किया है|");

                    }


                }
            }

        }

        int todaysWeight = DatabaseController.getBabyWeightViaDays(AppSettings.getString(AppSettings.babyId),0);
        int yesterdayWeight = DatabaseController.getBabyWeightViaDays(AppSettings.getString(AppSettings.babyId),-1);

        if(todaysWeight!=0&&yesterdayWeight!=0)
        {
            int gain = todaysWeight-yesterdayWeight;

            if(gain>0)
            {
                tvInfantWeightPrevious.setText(getString(R.string.infantHasGained)+" "+ gain +" "+ getString(R.string.grams)+" "+getString(R.string.sincePreviousWeight));
            }
            else
            {
                if(gain==0)
                {
                    tvInfantWeightPrevious.setText(getString(R.string.infantHasLoss)+" "+ gain +" "+ getString(R.string.grams) +" "+getString(R.string.sincePreviousWeight));
                }
                else
                {
                    String weight = String.valueOf(gain);
                    weight = weight.replace("-","");
                    tvInfantWeightPrevious.setText(getString(R.string.infantHasLoss)+" "+ weight +" "+ getString(R.string.grams) +" "+getString(R.string.sincePreviousWeight));
                }
            }
        }
        else
        {
            tvInfantWeightPrevious.setText(getString(R.string.notApplicableShortValue));
        }

        String duration = DatabaseController.getSSC(AppSettings.getString(AppSettings.babyId),date);

        Log.d("duration",duration);

        String unit = "";
        if(duration.equals("0"))
        {
            unit = getString(R.string.hour);
        }
        else
        {
            String[] parts = duration.split(":");

            Log.d("time-hour",parts[0]);
            Log.d("time-min",parts[1]);

            if(parts[0].equals("0")||parts[0].equals("1")||parts[0].equals("01"))
            {
                unit = getString(R.string.hour);
            }
            else
            {
                unit = getString(R.string.hours);
            }
        }

        tvReceivedTimeCS.setText(duration+" "+unit);

        if(duration.contains(":"))
        {
            String[] parts = duration.split(":");

            Log.d("hour",parts[0]);
            Log.d("min",parts[1]);

            int hour = Integer.parseInt(parts[0]);
            int min = Integer.parseInt(parts[1]);

            if(hour<11)
            {
                ivKmcEmojiCS.setImageResource(R.drawable.ic_sad_smily);
            }
            else   if(hour>11&&hour<21)
            {
                ivKmcEmojiCS.setImageResource(R.drawable.ic_neutral_status);
            }
            else
            {
                ivKmcEmojiCS.setImageResource(R.drawable.ic_happy_smily);
            }
        }
        else
        {
            ivKmcEmojiCS.setImageResource(R.drawable.ic_sad_smily);
        }


        boolean weigth = DatabaseController.checkRecordExistWhere(TableWeight.tableName,
                TableWeight.tableColumn.weightDate+" = '"+ date+"' and "+TableWeight.tableColumn.babyId +" = '"+AppSettings.getString(AppSettings.babyId)+"'");

        if(weigth)
        {
            ivDailyWeighingCS.setImageResource(R.drawable.ic_happy_smily);
        }
        else
        {
            ivDailyWeighingCS.setImageResource(R.drawable.ic_sad_smily);
        }

        arrayListTests.clear();
        arrayListTests.addAll(DatabaseController.getInvestigationSearchData(AppSettings.getString(AppSettings.babyId),date,""));

        int sample=0,report=0;

        for (int i=0;i<arrayListTests.size();i++)
        {
            if(!arrayListTests.get(i).get("sampleTakenBy").isEmpty())
            {
                sample = sample+1;
            }

            if(!arrayListTests.get(i).get("result").isEmpty())
            {
                report = report+1;
            }
        }

        etOrderedCS.setText(String.valueOf(arrayListTests.size()));
        etSampleCollectedCS.setText(String.valueOf(sample));
        etReportAvailCS.setText(String.valueOf(report));

        arrayListAssessment.clear();
        arrayListAssessment.addAll(DatabaseController.getAssessmentDataNew(AppSettings.getString(AppSettings.babyId),date));

        if(arrayListAssessment.size()>0)
        {
            int checkPulse=0,checkSpo2=0,checkTemp=0,checkResp=0;

            int pulse = 0;
            try {

                if(!arrayListAssessment.get(0).get("babyPulseRate").isEmpty())
                {
                    pulse = Integer.parseInt(arrayListAssessment.get(0).get("babyPulseRate"));
                    if(pulse<75||pulse>200)
                    {
                        checkPulse=0;
                    }
                    else
                    {
                        checkPulse=1;
                    }
                }

            } catch (NumberFormatException e) {
                //e.printStackTrace();
                checkPulse=1;
            }

            int spo2=0;
            try {
                if(!arrayListAssessment.get(0).get("babySpO2").isEmpty()) {
                    spo2 = Integer.parseInt(arrayListAssessment.get(0).get("babySpO2"));
                    if(spo2<95) {
                        checkSpo2=0;
                    }
                    else {
                        checkSpo2=1;
                    }
                }
            } catch (NumberFormatException e) {
                //e.printStackTrace();
                checkSpo2=0;
            }

            float temp=0;
            try {
                try {
                    temp = Float.parseFloat(arrayListAssessment.get(0).get("babyTemperature"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

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
                try {
                    res = Float.parseFloat(arrayListAssessment.get(0).get("babyRespiratoryRate"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

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

            if(arrayListAssessment.get(0).get("temperatureUnit").equals(getString(R.string.degreeCValue)))
            {
                tvSF1CS.setText(getString(R.string.temperature)+": "+arrayListAssessment.get(0).get("babyTemperature") +" "+ getString(R.string.degreeC));
            }
            else
            {
                tvSF1CS.setText(getString(R.string.temperature)+": "+arrayListAssessment.get(0).get("babyTemperature") +" "+ getString(R.string.degreeF));
            }

            tvSF2CS.setText(getString(R.string.pulseOxidation)+": "+getString(R.string.notApplicableShortValue));
            tvSF3CS.setText(getString(R.string.spo2)+": "+getString(R.string.notApplicableShortValue));
            tvSF4CS.setText(getString(R.string.respiratoryRate)+": "+res+" "+getString(R.string.min));

            if(arrayListAssessment.get(0).get("isPulseOximatoryDeviceAvailable").equalsIgnoreCase(getString(R.string.yesValue)))
            {
                tvSF2CS.setText(getString(R.string.pulseOxidation)+": "+pulse+" "+getString(R.string.bpm));
                tvSF3CS.setText(getString(R.string.spo2)+": "+spo2+"%");

                if(checkPulse==0||checkSpo2==0||checkTemp==0||checkResp==0)
                {
                    ivRoutineAssessmentCS.setImageResource(R.drawable.ic_sad_smily);
                }
                else
                {
                    ivRoutineAssessmentCS.setImageResource(R.drawable.ic_happy_smily);
                }
            }
            else  if(checkTemp==0||checkResp==0)
            {
                ivRoutineAssessmentCS.setImageResource(R.drawable.ic_sad_smily);
            }
            else
            {
                ivRoutineAssessmentCS.setImageResource(R.drawable.ic_happy_smily);
            }
        }

        setFeedingViaDate(date);

    }

    private void setFeedingViaDate(String date) {

        ivNutritionCS.setImageResource(R.drawable.ic_sad_smily);
        tvPrescribedNutritionCS.setText(getString(R.string.prescribed)+" 0 "+getString(R.string.ml));
        tvReceivedNutritionCS.setText(getString(R.string.received)+" 0 "+getString(R.string.ml));
        tvNutritionQuantity.setText("0 "+getString(R.string.ml));
        tvNutritionTime.setText("0");
        tvFluids.setText("0");
        tvModeFeeding.setText(getString(R.string.notApplicableShortValue));
        tvFrequency.setText("0");
        tvMedication.setText(getString(R.string.notApplicableShortValue));

        arrayListBabyWeight.clear();
        arrayListBabyWeight.addAll(DatabaseController.getBabyWeightViaId(AppSettings.getString(AppSettings.babyId)
                ,DatabaseController.getDelDate(AppSettings.getString(AppSettings.babyId))));

        int days = Integer.parseInt((AppUtils.getDateDiff(DatabaseController.getDelDate(AppSettings.getString(AppSettings.babyId)))));

        for(int i=arrayListBabyWeight.size();i>=0;i--)
        {
            if(!arrayListBabyWeight.get(i-1).get("babyWeight").equalsIgnoreCase("0"))
            {
                float  quantity = 0;
                try {
                    quantity = AppUtils.getQuantity(Integer.parseInt(arrayListBabyWeight.get(i-1).get("babyWeight")),days);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                Log.d("days-quantity", String.valueOf(days));
                Log.d("days-weight", String.valueOf(arrayListBabyWeight.get(i-1).get("babyWeight")));
                Log.d("days-quantity", String.valueOf(quantity));

                float sum = Float.parseFloat(DatabaseController.getTotalQuantity(
                        AppSettings.getString(AppSettings.babyId),date));

                int  perc = (int) ((sum/quantity)*100);

                if(perc<=75)
                {
                    ivNutritionCS.setImageResource(R.drawable.ic_sad_smily);
                }
                else if(perc>76&&perc<=90)
                {
                    ivNutritionCS.setImageResource(R.drawable.ic_neutral_status);
                }
                else if(perc>91)
                {
                    ivNutritionCS.setImageResource(R.drawable.ic_happy_smily);
                }

                try {
                    tvPrescribedNutritionCS.setText(getString(R.string.prescribed)+" "+quantity+" "+getString(R.string.ml));

                    tvReceivedNutritionCS.setText(getString(R.string.received)+" "+DatabaseController.getTotalQuantity(
                            AppSettings.getString(AppSettings.babyId),date)+" "+getString(R.string.ml));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                tvNutritionQuantity.setText(DatabaseController.getTotalQuantity(
                        AppSettings.getString(AppSettings.babyId),date)+" "+getString(R.string.ml));

                tvNutritionTime.setText(DatabaseController.getTotalMethod(mActivity,AppSettings.getString(AppSettings.babyId),date,"1"));

                break;
            }
        }


        String times = DatabaseController.getTotalMethod(mActivity, AppSettings.getString(AppSettings.babyId), date,"1");

        if(times.equals("0")||times.equals("1"))
        {
            times = times + " "+ getString(R.string.noOfTime);
        }
        else
        {
            times = times + " "+ getString(R.string.noOfTimes);
        }

        arrayListFeeding.clear();
        arrayListFeeding.addAll(DatabaseController.getFeedingData(AppSettings.getString(AppSettings.babyId),date));

        if(arrayListFeeding.size()>0)
        {
            tvFluids.setText(arrayListFeeding.get(0).get("fluid"));
            tvModeFeeding.setText(arrayListFeeding.get(0).get("method"));
            tvFrequency.setText(times);
        }

        arrayListMedicine.clear();
        arrayListMedicine.addAll(DatabaseController.getMedicineDataViaDate(AppSettings.getString(AppSettings.babyId),date));

        if(arrayListMedicine.size()>0)
        {
            tvMedication.setText(arrayListMedicine.get(0).get("name"));
        }
    }

    private void setListeners() {

        llToday.setOnClickListener(this);
        llCaseSheet.setOnClickListener(this);
        llGraphs.setOnClickListener(this);
        llGrowthMain.setOnClickListener(this);

        llKMCPosition.setOnClickListener(this);
        llKMCMonitoring.setOnClickListener(this);
        llKMCNutrition.setOnClickListener(this);
        llKMCRespect.setOnClickListener(this);
        llKMCHydiene.setOnClickListener(this);
        llKMCWhatis.setOnClickListener(this);

        tvGrowthMonitoring.setOnClickListener(this);
        tvRespiratoryRate.setOnClickListener(this);
        tvPulseOxidation.setOnClickListener(this);

        rlKmc.setOnClickListener(this);
        rlNutrition.setOnClickListener(this);
        rlMedication.setOnClickListener(this);
        rlWeight.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.rlKmc:

                ((BabyDetailActivity)getActivity()).displayView(1);

                break;

            case R.id.rlNutrition:

                ((BabyDetailActivity)getActivity()).displayView(2);

                break;

            case R.id.rlMedication:

                ((BabyDetailActivity)getActivity()).displayView(3);

                break;

            case R.id.rlWeight:

                ((BabyDetailActivity)getActivity()).displayView(4);

                break;

            case R.id.llToday:

                setDefault();
                viewToday.setVisibility(View.VISIBLE);
                llTodayMain.setVisibility(View.VISIBLE);
                tvToday.setTextColor(getResources().getColor(R.color.blackNew));
                llToday.setBackgroundResource(R.color.white);

                break;

            case R.id.llCaseSheet:

                setDefault();
                viewCaseSheet.setVisibility(View.VISIBLE);
                llCaseSheetMain.setVisibility(View.VISIBLE);
                tvCaseSheet.setTextColor(getResources().getColor(R.color.blackNew));
                llCaseSheet.setBackgroundResource(R.color.white);

                if(AppSettings.getString(AppSettings.userType).equalsIgnoreCase("1"))
                {
                    if(AppUtils.isNetworkAvailable(mActivity))
                    {
                        getBabyCaseDetailApi(AppUtils.getCurrentDateFormatted());
                    }
                    else
                    {
                        AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
                    }
                }

                break;

            case R.id.llGraphs:

                setDefault();
                viewGraphs.setVisibility(View.VISIBLE);
                llGraphsMain.setVisibility(View.VISIBLE);
                tvGraphs.setTextColor(getResources().getColor(R.color.blackNew));
                llGraphs.setBackgroundResource(R.color.white);
                tvGrowthMonitoring.setTextColor(getResources().getColor(R.color.oo_color));
                tvRespiratoryRate.setTextColor(getResources().getColor(R.color.blackNew));
                tvPulseOxidation.setTextColor(getResources().getColor(R.color.blackNew));
                llGrowthMain.setVisibility(View.VISIBLE);
                llRespiratoryRateMain.setVisibility(View.GONE);
                llPulseOxidationMain.setVisibility(View.GONE);

                break;

            case R.id.tvGrowthMonitoring:

                tvGrowthMonitoring.setTextColor(getResources().getColor(R.color.oo_color));
                tvRespiratoryRate.setTextColor(getResources().getColor(R.color.blackNew));
                tvPulseOxidation.setTextColor(getResources().getColor(R.color.blackNew));
                llGrowthMain.setVisibility(View.VISIBLE);
                llRespiratoryRateMain.setVisibility(View.GONE);
                llPulseOxidationMain.setVisibility(View.GONE);

                break;

            case R.id.tvRespiratoryRate:

                tvGrowthMonitoring.setTextColor(getResources().getColor(R.color.blackNew));
                tvRespiratoryRate.setTextColor(getResources().getColor(R.color.oo_color));
                tvPulseOxidation.setTextColor(getResources().getColor(R.color.blackNew));
                llGrowthMain.setVisibility(View.GONE);
                llRespiratoryRateMain.setVisibility(View.VISIBLE);
                llPulseOxidationMain.setVisibility(View.GONE);

                break;

            case R.id.tvPulseOxidation:

                tvGrowthMonitoring.setTextColor(getResources().getColor(R.color.blackNew));
                tvRespiratoryRate.setTextColor(getResources().getColor(R.color.blackNew));
                tvPulseOxidation.setTextColor(getResources().getColor(R.color.oo_color));
                llGrowthMain.setVisibility(View.GONE);
                llRespiratoryRateMain.setVisibility(View.GONE);
                llPulseOxidationMain.setVisibility(View.VISIBLE);

                break;

            case R.id.llKMCWhatis:
                kmcPostion=1;
                ((BabyDetailActivity)getActivity()).displayView(5);

                AppSettings.putString(AppSettings.kmcHygieneViewer, "");
                AppSettings.putString(AppSettings.kmcHygieneposition, "");

                AppSettings.putString(AppSettings.kmcNutitionViewer, "");
                AppSettings.putString(AppSettings.kmcNutitionposition, "");

                AppSettings.putString(AppSettings.kmcPositionViewer, "");
                AppSettings.putString(AppSettings.kmcPositionposition, "");

                AppSettings.putString(AppSettings.WhatisKmcViewer, "");
                AppSettings.putString(AppSettings.WhatisKmcposition, "");

                AppSettings.putString(AppSettings.kmcRespectViewer, "");
                AppSettings.putString(AppSettings.kmcRespectposition, "");

                AppSettings.putString(AppSettings.kmcMonitoringViewer, "");
                AppSettings.putString(AppSettings.kmcMonitoringposition, "");
//
                break;

            case R.id.llKMCPosition:
                kmcPostion=2;
                ((BabyDetailActivity)getActivity()).displayView(5);
                AppSettings.putString(AppSettings.kmcHygieneViewer, "");
                AppSettings.putString(AppSettings.kmcHygieneposition, "");

                AppSettings.putString(AppSettings.kmcNutitionViewer, "");
                AppSettings.putString(AppSettings.kmcNutitionposition, "");

                AppSettings.putString(AppSettings.kmcPositionViewer, "");
                AppSettings.putString(AppSettings.kmcPositionposition, "");

                AppSettings.putString(AppSettings.WhatisKmcViewer, "");
                AppSettings.putString(AppSettings.WhatisKmcposition, "");

                AppSettings.putString(AppSettings.kmcRespectViewer, "");
                AppSettings.putString(AppSettings.kmcRespectposition, "");

                AppSettings.putString(AppSettings.kmcMonitoringViewer, "");
                AppSettings.putString(AppSettings.kmcMonitoringposition, "");
                break;

            case R.id.llKMCNutrition:
                kmcPostion=3;
                ((BabyDetailActivity)getActivity()).displayView(5);
                AppSettings.putString(AppSettings.kmcHygieneViewer, "");
                AppSettings.putString(AppSettings.kmcHygieneposition, "");

                AppSettings.putString(AppSettings.kmcNutitionViewer, "");
                AppSettings.putString(AppSettings.kmcNutitionposition, "");

                AppSettings.putString(AppSettings.kmcPositionViewer, "");
                AppSettings.putString(AppSettings.kmcPositionposition, "");

                AppSettings.putString(AppSettings.WhatisKmcViewer, "");
                AppSettings.putString(AppSettings.WhatisKmcposition, "");

                AppSettings.putString(AppSettings.kmcRespectViewer, "");
                AppSettings.putString(AppSettings.kmcRespectposition, "");

                AppSettings.putString(AppSettings.kmcMonitoringViewer, "");
                AppSettings.putString(AppSettings.kmcMonitoringposition, "");
                break;

            case R.id.llKMCHydiene:
                kmcPostion=4;
                ((BabyDetailActivity)getActivity()).displayView(5);
                AppSettings.putString(AppSettings.kmcHygieneViewer, "");
                AppSettings.putString(AppSettings.kmcHygieneposition, "");

                AppSettings.putString(AppSettings.kmcNutitionViewer, "");
                AppSettings.putString(AppSettings.kmcNutitionposition, "");

                AppSettings.putString(AppSettings.kmcPositionViewer, "");
                AppSettings.putString(AppSettings.kmcPositionposition, "");

                AppSettings.putString(AppSettings.WhatisKmcViewer, "");
                AppSettings.putString(AppSettings.WhatisKmcposition, "");

                AppSettings.putString(AppSettings.kmcRespectViewer, "");
                AppSettings.putString(AppSettings.kmcRespectposition, "");

                AppSettings.putString(AppSettings.kmcMonitoringViewer, "");
                AppSettings.putString(AppSettings.kmcMonitoringposition, "");
                break;

            case R.id.llKMCMonitoring:
                kmcPostion=5;
                ((BabyDetailActivity)getActivity()).displayView(5);
                AppSettings.putString(AppSettings.kmcHygieneViewer, "");
                AppSettings.putString(AppSettings.kmcHygieneposition, "");

                AppSettings.putString(AppSettings.kmcNutitionViewer, "");
                AppSettings.putString(AppSettings.kmcNutitionposition, "");

                AppSettings.putString(AppSettings.kmcPositionViewer, "");
                AppSettings.putString(AppSettings.kmcPositionposition, "");

                AppSettings.putString(AppSettings.WhatisKmcViewer, "");
                AppSettings.putString(AppSettings.WhatisKmcposition, "");

                AppSettings.putString(AppSettings.kmcRespectViewer, "");
                AppSettings.putString(AppSettings.kmcRespectposition, "");

                AppSettings.putString(AppSettings.kmcMonitoringViewer, "");
                AppSettings.putString(AppSettings.kmcMonitoringposition, "");
                break;

            case R.id.llKMCRespect:
                kmcPostion=6;

                ((BabyDetailActivity)getActivity()).displayView(5);
                AppSettings.putString(AppSettings.kmcHygieneViewer, "");
                AppSettings.putString(AppSettings.kmcHygieneposition, "");

                AppSettings.putString(AppSettings.kmcNutitionViewer, "");
                AppSettings.putString(AppSettings.kmcNutitionposition, "");

                AppSettings.putString(AppSettings.kmcPositionViewer, "");
                AppSettings.putString(AppSettings.kmcPositionposition, "");

                AppSettings.putString(AppSettings.WhatisKmcViewer, "");
                AppSettings.putString(AppSettings.WhatisKmcposition, "");

                AppSettings.putString(AppSettings.kmcRespectViewer, "");
                AppSettings.putString(AppSettings.kmcRespectposition, "");

                AppSettings.putString(AppSettings.kmcMonitoringViewer, "");
                AppSettings.putString(AppSettings.kmcMonitoringposition, "");
                break;


        }

    }

    private void setDefault() {

        viewToday.setVisibility(View.INVISIBLE);
        viewCaseSheet.setVisibility(View.INVISIBLE);
        viewGraphs.setVisibility(View.INVISIBLE);

        llTodayMain.setVisibility(View.GONE);
        llCaseSheetMain.setVisibility(View.GONE);
        llGraphsMain.setVisibility(View.GONE);

        tvToday.setTextColor(getResources().getColor(R.color.blackNew));
        tvCaseSheet.setTextColor(getResources().getColor(R.color.blackNew));
        tvGraphs.setTextColor(getResources().getColor(R.color.blackNew));

        llToday.setBackgroundResource(R.color.lightgreyback);
        llCaseSheet.setBackgroundResource(R.color.lightgreyback);
        llGraphs.setBackgroundResource(R.color.lightgreyback);
    }

    private void showDates(String delDate) {

        arrayListDates.clear();

        List<Date> dates = getDates(delDate,AppUtils.getDateInFormat());
        arrayListDates.addAll(dates);

        Collections.reverse(arrayListDates);

        rvDates.setLayoutManager(new LinearLayoutManager(mActivity,LinearLayoutManager.HORIZONTAL,false));
        adapter =new Adapter(arrayListDates);
        rvDates.setAdapter(adapter);

    }

    private static List<Date> getDates(String dateString1, String dateString2) {

        ArrayList<Date> dates = new ArrayList<Date>();

        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1.parse(dateString1);
            date2 = df1.parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while (!cal1.after(cal2)) {
            dates.add(cal1.getTime());
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder>{

        ArrayList data;

        public Adapter(ArrayList arrayListDates) {

            data=arrayListDates;

        }

        @NonNull
        @Override
        public Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_dates, viewGroup,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Adapter.MyViewHolder holder, int position) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            //holder.tvDays.setText(""+data.get(position));
            holder.tvDays.setText(""+sdf.format(data.get(position)));

            if(sdf.format(data.get(position)).equals(AppUtils.getCurrentDateFormatted()))
            {
                holder.tvDays.setText(getString(R.string.today));
            }

            holder.tvDays.setTextColor(getResources().getColor(R.color.blackNew));
            if(selectedDate.equals(sdf.format(data.get(position))))
            {
                holder.tvDays.setTextColor(getResources().getColor(R.color.oo_color));
            }

            holder.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    selectedDate = sdf.format(data.get(position));
                    adapter.notifyDataSetChanged();

                    if(AppSettings.getString(AppSettings.userType).equalsIgnoreCase("1"))
                    {
                        if(AppUtils.isNetworkAvailable(mActivity))
                        {
                            getBabyCaseDetailApi(sdf.format(data.get(position)));
                        }
                        else
                        {
                            AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
                        }
                    }
                    else
                    {
                        setValuesViaDate(sdf.format(data.get(position)));
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            //TextView
            TextView tvDays;

            //LinearLayout
            LinearLayout llMain;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                //TextView
                tvDays=itemView.findViewById(R.id.tvDays);

                //LinearLayout
                llMain=itemView.findViewById(R.id.llMain);
            }
        }
    }

    private void setOfflineGraph() {

        babyDetailList.clear();
        babyDetailList.addAll(DatabaseController.getBabyRegistrationDataViaId(AppSettings.getString(AppSettings.babyId)));

        babyWeightList.clear();
        babyWeightList.addAll(DatabaseController.getBabyWeightViaId(AppSettings.getString(AppSettings.babyId), babyDetailList.get(0).get("deliveryDate")));

        respiratoryRateList.clear();
        respiratoryRateList.addAll(DatabaseController.getResRateViaId(AppSettings.getString(AppSettings.babyId), babyDetailList.get(0).get("deliveryDate")));

        temperatureList.clear();
        temperatureList.addAll(DatabaseController.getTempViaId(AppSettings.getString(AppSettings.babyId), babyDetailList.get(0).get("deliveryDate")));

        babyPulseList.clear();
        babyPulseList.addAll(DatabaseController.getPulseRateViaId(AppSettings.getString(AppSettings.babyId), babyDetailList.get(0).get("deliveryDate")));

        babySpo2List.clear();
        babySpo2List.addAll(DatabaseController.getSpO2ViaId(AppSettings.getString(AppSettings.babyId), babyDetailList.get(0).get("deliveryDate")));

        skinToSkinList.clear();
        skinToSkinList.addAll(DatabaseController.getSSCViaId(AppSettings.getString(AppSettings.babyId),babyDetailList.get(0).get("deliveryDate")));

        breastFeedingList.clear();
        breastFeedingList.addAll(DatabaseController.getFeedingViaId(AppSettings.getString(AppSettings.babyId), babyDetailList.get(0).get("deliveryDate")));

        if(babyDetailList.size()>0)
        {
            uuid = babyDetailList.get(0).get("uuid");

            if(!babyDetailList.get(0).get("kmcPosition").isEmpty())
            {
                kmcPosition = babyDetailList.get(0).get("kmcPosition");

                if(kmcPosition.equals(getString(R.string.yesValue)))
                {
                    ivKmcPosition.setImageResource(R.drawable.ic_check_box_selected);
                }
                else if(kmcPosition.equals(getString(R.string.noValue)))
                {
                    ivKmcPosition.setImageResource(R.drawable.ic_check_box);
                }
            }

            if(!babyDetailList.get(0).get("kmcMonitoring").isEmpty())
            {
                kmcMonitoring = babyDetailList.get(0).get("kmcMonitoring");

                if(kmcMonitoring.equals(getString(R.string.yesValue)))
                {
                    ivKmcMonitoring.setImageResource(R.drawable.ic_check_box_selected);
                }
                else if(kmcMonitoring.equals(getString(R.string.noValue)))
                {
                    ivKmcMonitoring.setImageResource(R.drawable.ic_check_box);
                }
            }

            if(!babyDetailList.get(0).get("kmcNutrition").isEmpty())
            {
                kmcNutrition = babyDetailList.get(0).get("kmcNutrition");

                if(kmcNutrition.equals(getString(R.string.yesValue)))
                {
                    ivKmcNutrition.setImageResource(R.drawable.ic_check_box_selected);
                }
                else if(kmcNutrition.equals(getString(R.string.noValue)))
                {
                    ivKmcNutrition.setImageResource(R.drawable.ic_check_box);
                }
            }

            if(!babyDetailList.get(0).get("kmcRespect").isEmpty())
            {
                kmcRespect = babyDetailList.get(0).get("kmcRespect");

                if(kmcRespect.equals(getString(R.string.yesValue)))
                {
                    ivKmcRespect.setImageResource(R.drawable.ic_check_box_selected);
                }
                else if(kmcRespect.equals(getString(R.string.noValue)))
                {
                    ivKmcRespect.setImageResource(R.drawable.ic_check_box);
                }
            }

            if(!babyDetailList.get(0).get("kmcHygiene").isEmpty())
            {
                kmcHygiene = babyDetailList.get(0).get("kmcHygiene");

                if(kmcHygiene.equals(getString(R.string.yesValue)))
                {
                    ivKmcHygiene.setImageResource(R.drawable.ic_check_box_selected);
                }
                else if(kmcHygiene.equals(getString(R.string.noValue)))
                {
                    ivKmcHygiene.setImageResource(R.drawable.ic_check_box);
                }
            }
            if(!babyDetailList.get(0).get("whatisKmc").isEmpty())
            {
                kmcWhatis= babyDetailList.get(0).get("whatisKmc");

                if(kmcWhatis.equals(getString(R.string.yesValue)))
                {
                    ivKmcWhatis.setImageResource(R.drawable.ic_check_box_selected);
                }
                else if(kmcWhatis.equals(getString(R.string.noValue)))
                {
                    ivKmcWhatis.setImageResource(R.drawable.ic_check_box);
                }
            }

            if(kmcPosition.equals(getString(R.string.yesValue))&&
                       kmcMonitoring.equals(getString(R.string.yesValue))&&
                       kmcNutrition.equals(getString(R.string.yesValue))&&
                       kmcRespect.equals(getString(R.string.yesValue))&&
                       kmcWhatis.equals(getString(R.string.yesValue))&&
                       kmcHygiene.equals(getString(R.string.yesValue)))
            {
                ivCounselling.setImageResource(R.drawable.ic_happy_smily);
            }
            else
            {
                ivCounselling.setImageResource(R.drawable.ic_sad_smily);
            }
        }
    }

    private void generateWeightData(View v) {
        LineChartView weightChart = v.findViewById(R.id.weightChart);

        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();

        for (int i = 0; i < babyWeightList.size(); i++) {
            axisValues.add(i, new AxisValue(i).setLabel(babyWeightList.get(i).get("age")));

            if(!babyWeightList.get(i).get("babyWeight").isEmpty()
                       &&!babyWeightList.get(i).get("babyWeight").equalsIgnoreCase("0"))
            {
                float f = Float.parseFloat(babyWeightList.get(i).get("babyWeight"));
                yAxisValues.add(new PointValue(i, f));
            }
        }

        Line line = new Line(yAxisValues).setColor(ChartUtils.COLOR_BLUE);
        line.setHasLabels(true);

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setName(getString(R.string.age));
        axis.setValues(axisValues);
        axis.setHasLines(true);
        axis.setHasTiltedLabels(true);
        axis.setTextColor(ChartUtils.COLOR_GREEN);
        data.setAxisXBottom(axis);

        Axis yAxis = new Axis();
        yAxis.setName(getString(R.string.grams));
        yAxis.setHasLines(true);
        yAxis.setHasTiltedLabels(true);
        yAxis.setHasSeparationLine(true);
        yAxis.setTextColor(ChartUtils.COLOR_BLUE);
        data.setAxisYLeft(yAxis);

        weightChart.setLineChartData(data);
        Viewport viewport = new Viewport(weightChart.getMaximumViewport());
        viewport.top = (viewport.top+50);
        viewport.bottom = 0;
        viewport.left = 0;
        viewport.right = (viewport.right+2);
        weightChart.setMaximumViewport(viewport);
        weightChart.setCurrentViewport(viewport);

    }

    private void generateKMCData(View v) {

        LineChartView kmcChart = v.findViewById(R.id.kmcChart);

        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();

        for (int i = 0; i < skinToSkinList.size(); i++) {
            axisValues.add(i, new AxisValue(i).setLabel(skinToSkinList.get(i).get("age")));

            String newValue = skinToSkinList.get(i).get("duration");

            if(newValue.contains(":"))
            {
                newValue = newValue.replaceAll(":",".");
            }

            //yAxisValues.add(new PointValue(i, yAxisData[i]));
            if(!newValue.equalsIgnoreCase("0")
                       &&!newValue.isEmpty())
            {
                float f = Float.parseFloat(newValue);
                yAxisValues.add(new PointValue(i, f));
            }
        }

        Line line = new Line(yAxisValues).setColor(ChartUtils.COLOR_BLUE);
        line.setHasLabels(true);

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setName(getString(R.string.age));
        axis.setValues(axisValues);
        axis.setHasLines(true);
        axis.setHasTiltedLabels(true);
        axis.setTextColor(ChartUtils.COLOR_GREEN);
        data.setAxisXBottom(axis);

        Axis yAxis = new Axis();
        yAxis.setName(getString(R.string.hours));
        yAxis.setHasLines(true);
        yAxis.setHasTiltedLabels(true);
        yAxis.setHasSeparationLine(true);
        yAxis.setTextColor(ChartUtils.COLOR_BLUE);
        data.setAxisYLeft(yAxis);

        kmcChart.setLineChartData(data);
        Viewport viewport = new Viewport(kmcChart.getMaximumViewport());
        viewport.top = (viewport.top+5);
        viewport.bottom = 0;
        viewport.left = 0;
        viewport.right = (viewport.right+2);
        kmcChart.setMaximumViewport(viewport);
        kmcChart.setCurrentViewport(viewport);

    }

    private void generateFeedingData(View v) {

        LineChartView feedingChart = v.findViewById(R.id.feedingChart);

        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();

        for (int i = 0; i < breastFeedingList.size(); i++) {
            axisValues.add(i, new AxisValue(i).setLabel(breastFeedingList.get(i).get("age")));

            //yAxisValues.add(new PointValue(i, yAxisData[i]));
            if(!breastFeedingList.get(i).get("milkQuantity").equalsIgnoreCase("0")
                       &&!breastFeedingList.get(i).get("milkQuantity").isEmpty())
            {
                float f = Float.parseFloat(breastFeedingList.get(i).get("milkQuantity"));
                yAxisValues.add(new PointValue(i, f));
            }
        }

        Line line = new Line(yAxisValues).setColor(ChartUtils.COLOR_BLUE);
        line.setHasLabels(true);

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setName(getString(R.string.age));
        axis.setValues(axisValues);
        axis.setHasLines(true);
        axis.setHasTiltedLabels(true);
        axis.setTextColor(ChartUtils.COLOR_GREEN);
        data.setAxisXBottom(axis);

        Axis yAxis = new Axis();
        yAxis.setName(getString(R.string.mlYAxis));
        yAxis.setHasLines(true);
        yAxis.setHasTiltedLabels(true);
        yAxis.setHasSeparationLine(true);
        yAxis.setTextColor(ChartUtils.COLOR_BLUE);
        data.setAxisYLeft(yAxis);

        feedingChart.setLineChartData(data);
        Viewport viewport = new Viewport(feedingChart.getMaximumViewport());
        viewport.top = (viewport.top+10);
        viewport.bottom = 0;
        viewport.left = 0;
        viewport.right = (viewport.right+2);
        feedingChart.setMaximumViewport(viewport);
        feedingChart.setCurrentViewport(viewport);

    }

    private void generateRespiratoryData(View v) {

        LineChartView lineChartRespView = v.findViewById(R.id.respiratoryGraph);

        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();

        for (int i = 0; i < respiratoryRateList.size(); i++) {
            //axisValues.add(i, new AxisValue(i).setLabel(AppUtils.getDateFromTimestamp(Long.parseLong(RespiratoryRate.get(i).get("addDate")))));
            axisValues.add(i, new AxisValue(i).setLabel(respiratoryRateList.get(i).get("age")));

            if(!respiratoryRateList.get(i).get("babyRespiratoryRate").isEmpty()
                       &&!respiratoryRateList.get(i).get("babyRespiratoryRate").equalsIgnoreCase("0"))
            {
                float f = Float.parseFloat(respiratoryRateList.get(i).get("babyRespiratoryRate"));
                yAxisValues.add(new PointValue(i, f));
            }
        }

        Line line = new Line(yAxisValues).setColor(ChartUtils.COLOR_BLUE);
        line.setHasLabels(true);

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setName(getString(R.string.age));
        axis.setValues(axisValues);
        axis.setHasLines(true);
        axis.setHasTiltedLabels(true);
        axis.setTextColor(ChartUtils.COLOR_GREEN);
        data.setAxisXBottom(axis);

        Axis yAxis = new Axis();
        yAxis.setName(getString(R.string.rpmYAxis));
        yAxis.setHasLines(true);
        yAxis.setHasTiltedLabels(true);
        yAxis.setHasSeparationLine(true);
        yAxis.setTextColor(ChartUtils.COLOR_BLUE);
        data.setAxisYLeft(yAxis);

        lineChartRespView.setLineChartData(data);
        Viewport viewport = new Viewport(lineChartRespView.getMaximumViewport());
        viewport.top =(viewport.top+10);
        viewport.bottom = 0;
        viewport.left = 0;
        viewport.right = (viewport.right+2);
        lineChartRespView.setMaximumViewport(viewport);
        lineChartRespView.setCurrentViewport(viewport);

    }

    private void generateTempData(View v) {

        LineChartView lineChartTempView = v.findViewById(R.id.tempGraph);

        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();

        for (int i = 0; i < temperatureList.size(); i++) {
            axisValues.add(i, new AxisValue(i).setLabel(temperatureList.get(i).get("age")));

            if(!temperatureList.get(i).get("babyTemperature").isEmpty()
                       &&!temperatureList.get(i).get("babyTemperature").equalsIgnoreCase("0")
                       &&!temperatureList.get(i).get("babyTemperature").equalsIgnoreCase("0.0"))
            {
                float f = Float.parseFloat(temperatureList.get(i).get("babyTemperature"));
                yAxisValues.add(new PointValue(i, f));
            }
        }

        Line line = new Line(yAxisValues).setColor(ChartUtils.COLOR_BLUE);
        line.setHasLabels(true);

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setName(getString(R.string.age));
        axis.setValues(axisValues);
        axis.setHasLines(true);
        axis.setHasTiltedLabels(true);
        axis.setTextColor(ChartUtils.COLOR_GREEN);
        data.setAxisXBottom(axis);

        Axis yAxis = new Axis();
        yAxis.setName(getString(R.string.tempYAxis));
        yAxis.setHasLines(true);
        yAxis.setHasTiltedLabels(true);
        yAxis.setHasSeparationLine(true);
        yAxis.setTextColor(ChartUtils.COLOR_BLUE);
        data.setAxisYLeft(yAxis);

        lineChartTempView.setLineChartData(data);
        Viewport viewport = new Viewport(lineChartTempView.getMaximumViewport());
        viewport.top = 120;
        viewport.bottom = 30;
        viewport.left = 0;
        viewport.right = (viewport.right+2);
        lineChartTempView.setMaximumViewport(viewport);
        lineChartTempView.setCurrentViewport(viewport);

    }

    private void generatePulseData(View v) {

        LineChartView pulseGraph = v.findViewById(R.id.pulseGraph);

        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();

        for (int i = 0; i < babyPulseList.size(); i++) {
            axisValues.add(i, new AxisValue(i).setLabel(babyPulseList.get(i).get("age")));

            if(!babyPulseList.get(i).get("babyPulseRate").isEmpty()
                       &&!babyPulseList.get(i).get("babyPulseRate").equalsIgnoreCase("0"))
            {
                float f = Float.parseFloat(babyPulseList.get(i).get("babyPulseRate"));
                yAxisValues.add(new PointValue(i, f));
            }
        }

        Line line = new Line(yAxisValues).setColor(ChartUtils.COLOR_BLUE);
        line.setHasLabels(true);

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setName(getString(R.string.age));
        axis.setValues(axisValues);
        axis.setHasLines(true);
        axis.setHasTiltedLabels(true);
        axis.setTextColor(ChartUtils.COLOR_GREEN);
        data.setAxisXBottom(axis);

        Axis yAxis = new Axis();
        yAxis.setName(getString(R.string.bpmYAxis));
        yAxis.setHasLines(true);
        yAxis.setHasTiltedLabels(true);
        yAxis.setHasSeparationLine(true);
        yAxis.setTextColor(ChartUtils.COLOR_BLUE);
        data.setAxisYLeft(yAxis);

        pulseGraph.setLineChartData(data);
        Viewport viewport = new Viewport(pulseGraph.getMaximumViewport());
        viewport.top = (viewport.top+50);
        viewport.bottom = 0;
        viewport.left = 0;
        viewport.right = (viewport.right+2);
        pulseGraph.setMaximumViewport(viewport);
        pulseGraph.setCurrentViewport(viewport);

    }

    private void generateSpo2Data(View v) {

        LineChartView spo2Graph = v.findViewById(R.id.spo2Graph);

        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();

        for (int i = 0; i < babySpo2List.size(); i++) {
            axisValues.add(i, new AxisValue(i).setLabel(babySpo2List.get(i).get("age")));

            if(!babySpo2List.get(i).get("babySpO2").isEmpty()
                       &&!babySpo2List.get(i).get("babySpO2").equalsIgnoreCase("0"))
            {
                float f = Float.parseFloat(babySpo2List.get(i).get("babySpO2"));
                yAxisValues.add(new PointValue(i, f));
            }
        }

        Line line = new Line(yAxisValues).setColor(ChartUtils.COLOR_BLUE);
        line.setHasLabels(true);

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setName(getString(R.string.age));
        axis.setValues(axisValues);
        axis.setHasLines(true);
        axis.setHasTiltedLabels(true);
        axis.setTextColor(ChartUtils.COLOR_GREEN);
        data.setAxisXBottom(axis);

        Axis yAxis = new Axis();
        yAxis.setName(getString(R.string.spo2YAxis));
        yAxis.setHasLines(true);
        yAxis.setHasTiltedLabels(true);
        yAxis.setHasSeparationLine(true);
        yAxis.setTextColor(ChartUtils.COLOR_BLUE);
        data.setAxisYLeft(yAxis);

        spo2Graph.setLineChartData(data);
        Viewport viewport = new Viewport(spo2Graph.getMaximumViewport());
        viewport.top = (viewport.top+50);
        viewport.bottom = 0;
        viewport.left = 0;
        viewport.right = (viewport.right+2);
        spo2Graph.setMaximumViewport(viewport);
        spo2Graph.setCurrentViewport(viewport);

    }

    private void showConfirmationDialog(final String type,ImageView imageView,String value) {

//        Dialog dialog = new Dialog(mActivity,android.R.style.Theme_Translucent_NoTitleBar);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.alert_yes_no);
//        Window window = dialog.getWindow();
//        WindowManager.LayoutParams wlp = window.getAttributes();
//
//        wlp.gravity = Gravity.CENTER;
//        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
//        window.setAttributes(wlp);
//        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//
//        TextView tvText = dialog.findViewById(R.id.tvMessage);
//        TextView tvCancel = dialog.findViewById(R.id.tvCancel);
//        TextView tvOk = dialog.findViewById(R.id.tvOk);
//
//        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);
//        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);
//
//        tvCancel.setText(getString(R.string.no));
//        tvOk.setText(getString(R.string.yes));
//
//        if (type.equals("1")) {
//            tvText.setText(getString(R.string.confirmCounsellingPosition));
//        }  else if (type.equals("2")) {
//            tvText.setText(getString(R.string.confirmCounsellingMonitoring));
//        }  else if (type.equals("3")) {
//            tvText.setText(getString(R.string.confirmCounsellingNutrition));
//        }else if (type.equals("4")) {
//            tvText.setText(getString(R.string.confirmCounsellingRespect));
//        } else if (type.equals("5")) {
//            tvText.setText(getString(R.string.confirmCounsellingHygiene));
//        }
//
//        final String[] newValue = {value};
//
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();
//
//        rlCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                newValue[0] = getString(R.string.noValue);
//                imageView.setImageResource(R.drawable.ic_cross_new);
//
//                save(getString(R.string.noValue),type);
//                dialog.dismiss();
//                setOfflineGraph();
//            }
//        });
//
//        rlOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                newValue[0] = getString(R.string.yesValue);
//                imageView.setImageResource(R.drawable.ic_check_box_selected);
//
//                save(getString(R.string.yesValue),type);
//                dialog.dismiss();
//                setOfflineGraph();
//            }
//        });

        if(value.equals("Yes")){
            imageView.setImageResource(R.drawable.ic_check_box_selected);
            save(getString(R.string.yesValue),type);
            setOfflineGraph();
        }else {
            save(getString(R.string.noValue),type);
        }

    }

    private void save(String value,String type) {

        ContentValues mContentValues = new ContentValues();


        if(type.equals("2"))
        {
            mContentValues.put(TableBabyAdmission.tableColumn.kmcPosition.toString(), value);
        }
        else  if(type.equals("3"))
        {
            mContentValues.put(TableBabyAdmission.tableColumn.kmcNutrition.toString(), value);
        }
        else  if(type.equals("4"))
        {
            mContentValues.put(TableBabyAdmission.tableColumn.kmcHygiene.toString(), value);
        }
        else  if(type.equals("5"))
        {
            mContentValues.put(TableBabyAdmission.tableColumn.kmcMonitoring.toString(), value);
        }
        else  if(type.equals("6"))
        {
            mContentValues.put(TableBabyAdmission.tableColumn.kmcRespect.toString(), value);
        }

        if(type.equals("1"))
        {
            mContentValues.put(TableBabyAdmission.tableColumn.whatisKmc.toString(), value);
        }

        mContentValues.put(TableBabyAdmission.tableColumn.type.toString(), type);
        mContentValues.put(TableBabyAdmission.tableColumn.uuid.toString(), uuid);
        mContentValues.put(TableBabyAdmission.tableColumn.modifyDate.toString(),AppUtils.currentTimestampFormat());

        DatabaseController.insertUpdateData(mContentValues, TableBabyAdmission.tableName,
                TableBabyAdmission.tableColumn.uuid.toString(), uuid);

        SyncBabyRecord.getCounsellingDataToUpdate(mActivity,AppSettings.getString(AppSettings.babyId),type);

        if(kmcPosition.equals(getString(R.string.yesValue))&&
                   kmcMonitoring.equals(getString(R.string.yesValue))&&
                   kmcNutrition.equals(getString(R.string.yesValue))&&
                   kmcRespect.equals(getString(R.string.yesValue))&&
                   kmcHygiene.equals(getString(R.string.yesValue))&&
                   whatisKMC.equals(getString(R.string.yesValue)) )
        {
            ivCounselling.setImageResource(R.drawable.ic_happy_smily);
            AppSettings.putString(AppSettings.kmcHygieneViewer, "");
            AppSettings.putString(AppSettings.kmcHygieneposition, "");

            AppSettings.putString(AppSettings.kmcNutitionViewer, "");
            AppSettings.putString(AppSettings.kmcNutitionposition, "");

            AppSettings.putString(AppSettings.kmcPositionViewer, "");
            AppSettings.putString(AppSettings.WhatisKmcposition, "");

            AppSettings.putString(AppSettings.WhatisKmcViewer, "");
            AppSettings.putString(AppSettings.WhatisKmcposition, "");

            AppSettings.putString(AppSettings.kmcRespectViewer, "");
            AppSettings.putString(AppSettings.kmcRespectposition, "");

            AppSettings.putString(AppSettings.kmcMonitoringViewer, "");
            AppSettings.putString(AppSettings.kmcMonitoringposition, "");
        }
        else {
            ivCounselling.setImageResource(R.drawable.ic_sad_smily);
        }
    }

    //Get Baby Details
    private void getBabyDetailApi(View view) {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {

            jsonData.put("coachId", AppSettings.getString(AppSettings.coachId));
            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("babyId", AppSettings.getString(AppSettings.babyId));

            json.put(AppConstants.projectName, jsonData);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.getBabyDetail, json, true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        JSONObject jsonObjectMain = jsonObject.getJSONObject("babyDetails");

                        Log.v("getBabyDetailApi", json.toString());
                     //   showDates(jsonObjectMain.getString("deliveryDate"));
                        showDates(jsonObjectMain.getString("addDate"));

                        tvReceivedTime.setText("0");
                        ivKmcEmoji.setImageResource(R.drawable.ic_sad_smily);
                        ivDailyWeighing.setImageResource(R.drawable.ic_sad_smily);
                        etOrdered.setText("0");
                        etSampleCollected.setText("0");
                        etReportAvail.setText("0");
                        tvSF1.setText(getString(R.string.notApplicableShortValue));
                        tvSF2.setText(getString(R.string.notApplicableShortValue));
                        tvSF3.setText(getString(R.string.notApplicableShortValue));
                        tvSF4.setText(getString(R.string.notApplicableShortValue));
                        ivRoutineAssessment.setImageResource(R.drawable.ic_sad_smily);

                        String duration = jsonObjectMain.getString("totalKMC");

                        Log.d("duration",duration);

                        String unit = "";
                        if(duration.equals("0"))
                        {
                            unit = getString(R.string.hour);
                        }
                        else
                        {
                            try {
                                String[] parts = duration.split(":");

                                Log.d("time-hour",parts[0]);
                                Log.d("time-min",parts[1]);

                                if(parts[0].equals("0")||parts[0].equals("1")||parts[0].equals("01"))
                                {
                                    unit = getString(R.string.hour);
                                }
                                else
                                {
                                    unit = getString(R.string.hours);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        tvReceivedTime.setText(duration+" "+unit);

                        if(duration.contains(":"))
                        {
                            String[] parts = duration.split(":");

                            Log.d("hour",parts[0]);
                            Log.d("min",parts[1]);

                            int hour = Integer.parseInt(parts[0]);
                            int min = Integer.parseInt(parts[1]);

                            if(hour<11)
                            {
                                ivKmcEmoji.setImageResource(R.drawable.ic_sad_smily);
                            }
                            else   if(hour>11&&hour<21)
                            {
                                ivKmcEmoji.setImageResource(R.drawable.ic_neutral_status);
                            }
                            else
                            {
                                ivKmcEmoji.setImageResource(R.drawable.ic_happy_smily);
                            }
                        }
                        else
                        {
                            ivKmcEmoji.setImageResource(R.drawable.ic_sad_smily);
                        }

                        String weigth = jsonObjectMain.getString("dailyWeighing");

                        if(weigth.equalsIgnoreCase(getString(R.string.yesValue)))
                        {
                            ivDailyWeighing.setImageResource(R.drawable.ic_happy_smily);
                        }
                        else
                        {
                            ivDailyWeighing.setImageResource(R.drawable.ic_sad_smily);
                        }

                        float hourly = Float.parseFloat(jsonObjectMain.getString("prescribedFeeding"))/12;

                        try {
                            tvPrescribedNutrition.setText(jsonObjectMain.getString("prescribedFeeding")+" "+getString(R.string.ml)
                                                                  +"("+new DecimalFormat("##.##").format(hourly)
                                                                  +" "+getString(R.string.ml) +" "+getString(R.string.every2hours));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        tvFluids.setText(jsonObjectMain.getString("fluid"));
                        tvModeFeeding.setText(jsonObjectMain.getString("modeOfFeeding"));
                        tvFrequency.setText(jsonObjectMain.getString("feedingFrequency"));
                        tvTotalQuantity.setText(jsonObjectMain.getString("todayFeedingQuantity")+" "+getString(R.string.ml));

                        try {

                            arrayListAssessment.clear();
                            JSONArray jsonArray = jsonObjectMain.getJSONArray("monitoringDetails");

                            for(int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject jsonObjectMonitoring = jsonArray.getJSONObject(i);

                                HashMap<String, String> hashlist = new HashMap();

                                hashlist.put("babyPulseRate", jsonObjectMonitoring.getString("pulseRate"));
                                hashlist.put("babySpO2", jsonObjectMonitoring.getString("spo2"));
                                hashlist.put("babyTemperature", jsonObjectMonitoring.getString("temperatureValue"));
                                hashlist.put("temperatureUnit", jsonObjectMonitoring.getString("temperatureUnit"));
                                hashlist.put("babyRespiratoryRate", jsonObjectMonitoring.getString("respiratoryRate"));
                                hashlist.put("isPulseOximatoryDeviceAvailable", jsonObjectMonitoring.getString("isPulseOximatoryDeviceAvail"));

                                arrayListAssessment.add(hashlist);
                            }

                            if(arrayListAssessment.size()>0)
                            {
                                int checkPulse=0,checkSpo2=0,checkTemp=0,checkResp=0;

                                int pulse = 0;
                                try {

                                    if(!arrayListAssessment.get(0).get("babyPulseRate").isEmpty())
                                    {
                                        pulse = Integer.parseInt(arrayListAssessment.get(0).get("babyPulseRate"));
                                        if(pulse<75||pulse>200)
                                        {
                                            checkPulse=0;
                                        }
                                        else
                                        {
                                            checkPulse=1;
                                        }
                                    }

                                } catch (NumberFormatException e) {
                                    //e.printStackTrace();
                                    checkPulse=1;
                                }


                                int spo2=0;
                                try {

                                    if(!arrayListAssessment.get(0).get("babySpO2").isEmpty())
                                    {
                                        spo2 = Integer.parseInt(arrayListAssessment.get(0).get("babySpO2"));

                                        if(spo2<95)
                                        {
                                            checkSpo2=0;
                                        }
                                        else
                                        {
                                            checkSpo2=1;
                                        }
                                    }
                                } catch (NumberFormatException e) {
                                    //e.printStackTrace();
                                    checkSpo2=0;
                                }

                                float temp=0;
                                try {
                                    try {
                                        temp = Float.parseFloat(arrayListAssessment.get(0).get("babyTemperature"));
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }

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
                                    try {
                                        res = Float.parseFloat(arrayListAssessment.get(0).get("babyRespiratoryRate"));
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }

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

                                if(arrayListAssessment.get(0).get("temperatureUnit").equals(getString(R.string.degreeCValue)))
                                {
                                    tvSF1.setText(getString(R.string.temperature)+": "+temp +" "+ getString(R.string.degreeC));
                                }
                                else
                                {
                                    tvSF1.setText(getString(R.string.temperature)+": "+temp +" "+ getString(R.string.degreeF));
                                }

                                tvSF2.setText(getString(R.string.pulseOxidation)+": "+getString(R.string.notApplicableShortValue));
                                tvSF3.setText(getString(R.string.spo2)+": "+getString(R.string.notApplicableShortValue));
                                tvSF4.setText(getString(R.string.respiratoryRate)+": "+res+" "+getString(R.string.min));

                                if(arrayListAssessment.get(0).get("isPulseOximatoryDeviceAvailable").equalsIgnoreCase(getString(R.string.yesValue)))
                                {
                                    tvSF2.setText(getString(R.string.pulseOxidation)+": "+pulse+" "+getString(R.string.bpm));
                                    tvSF3.setText(getString(R.string.spo2)+": "+spo2+"%");

                                    if(checkPulse==0||checkSpo2==0||checkTemp==0||checkResp==0)
                                    {
                                        ivRoutineAssessment.setImageResource(R.drawable.ic_sad_smily);
                                    }
                                    else
                                    {
                                        ivRoutineAssessment.setImageResource(R.drawable.ic_happy_smily);
                                    }
                                }
                                else  if(checkTemp==0||checkResp==0)
                                {
                                    ivRoutineAssessment.setImageResource(R.drawable.ic_sad_smily);
                                }
                                else
                                {
                                    ivRoutineAssessment.setImageResource(R.drawable.ic_happy_smily);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        etOrdered.setText( jsonObjectMain.getString("orderCount"));
                        etSampleCollected.setText( jsonObjectMain.getString("sampleCount"));
                        etReportAvail.setText( jsonObjectMain.getString("resultCount"));

                        kmcPosition = jsonObjectMain.getString("kmcPosition");

                        if(kmcPosition.equals(getString(R.string.yesValue)))
                        {
                            ivKmcPosition.setImageResource(R.drawable.ic_check_box_selected);
                        }
                        else if(kmcPosition.equals(getString(R.string.noValue)))
                        {
                            ivKmcPosition.setImageResource(R.drawable.ic_cross_new);
                        }

                        kmcMonitoring = jsonObjectMain.getString("kmcMonitoring");

                        if(kmcMonitoring.equals(getString(R.string.yesValue)))
                        {
                            ivKmcMonitoring.setImageResource(R.drawable.ic_check_box_selected);
                        }
                        else if(kmcMonitoring.equals(getString(R.string.noValue)))
                        {
                            ivKmcMonitoring.setImageResource(R.drawable.ic_cross_new);
                        }

                        kmcNutrition = jsonObjectMain.getString("kmcNutrition");

                        if(kmcNutrition.equals(getString(R.string.yesValue)))
                        {
                            ivKmcNutrition.setImageResource(R.drawable.ic_check_box_selected);
                        }
                        else if(kmcNutrition.equals(getString(R.string.noValue)))
                        {
                            ivKmcNutrition.setImageResource(R.drawable.ic_cross_new);
                        }

                        kmcRespect = jsonObjectMain.getString("kmcRespect");

                        if(kmcRespect.equals(getString(R.string.yesValue)))
                        {
                            ivKmcRespect.setImageResource(R.drawable.ic_check_box_selected);
                        }
                        else if(kmcRespect.equals(getString(R.string.noValue)))
                        {
                            ivKmcRespect.setImageResource(R.drawable.ic_cross_new);
                        }

                        kmcHygiene = jsonObjectMain.getString("kmcHygiene");

                        if(kmcHygiene.equals(getString(R.string.yesValue)))
                        {
                            ivKmcHygiene.setImageResource(R.drawable.ic_check_box_selected);
                        }
                        else if(kmcHygiene.equals(getString(R.string.noValue)))
                        {
                            ivKmcHygiene.setImageResource(R.drawable.ic_cross_new);
                        }

                        if(kmcPosition.equals(getString(R.string.yesValue))&&
                                   kmcMonitoring.equals(getString(R.string.yesValue))&&
                                   kmcNutrition.equals(getString(R.string.yesValue))&&
                                   kmcRespect.equals(getString(R.string.yesValue))&&
                                   kmcHygiene.equals(getString(R.string.yesValue)))
                        {
                            ivCounselling.setImageResource(R.drawable.ic_happy_smily);
                        }
                        else
                        {
                            ivCounselling.setImageResource(R.drawable.ic_sad_smily);
                        }

                        try {
                            JSONObject jsonObjectComment = jsonObjectMain.getJSONObject("commentData");

                            tvDoctorNotes.setText(jsonObjectComment.getString("doctorName") +" - "+jsonObjectComment.getString("comment"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JSONArray respiratoryRate = jsonObjectMain.getJSONArray("respiratoryRate");

                        respiratoryRateList.clear();

                        for (int i = 0; i < respiratoryRate.length(); i++) {

                            JSONObject arrayJsonObject = respiratoryRate.getJSONObject(i);

                            HashMap<String, String> hashMap = new HashMap();

                            hashMap.put("age", arrayJsonObject.getString("age"));
                            hashMap.put("babyRespiratoryRate",arrayJsonObject.getString("respiratoryRate"));

                            respiratoryRateList.add(hashMap);
                        }


                        JSONArray temperature = jsonObjectMain.getJSONArray("temperature");

                        temperatureList.clear();

                        for (int i = 0; i < temperature.length(); i++) {

                            JSONObject arrayJsonObject = temperature.getJSONObject(i);

                            HashMap<String, String> hashMap = new HashMap();

                            hashMap.put("age",arrayJsonObject.getString("age"));
                            hashMap.put("babyTemperature",arrayJsonObject.getString("temperature"));

                            temperatureList.add(hashMap);
                        }


                        JSONArray breastFeeding = jsonObjectMain.getJSONArray("breastFeeding");

                        breastFeedingList.clear();

                        for (int i = 0; i < breastFeeding.length(); i++) {

                            JSONObject arrayJSONObject = breastFeeding.getJSONObject(i);

                            HashMap<String, String> hashMap = new HashMap();

                            hashMap.put("age",arrayJSONObject.getString("day"));
                            hashMap.put("milkQuantity",arrayJSONObject.getString("milkQuantity"));

                            breastFeedingList.add(hashMap);
                        }


                        JSONArray sstDetail = jsonObjectMain.getJSONArray("kmcData");

                        skinToSkinList.clear();

                        for (int i = 0; i < sstDetail.length(); i++) {

                            JSONObject arrayJSONObject = sstDetail.getJSONObject(i);

                            HashMap<String, String> hashMap = new HashMap();

                            hashMap.put("age",arrayJSONObject.getString("day"));

                            String dur = arrayJSONObject.getString("duration");

                            if(dur.contains(" hr"))
                            {
                                dur = dur.replace(" hr","");
                            }

                            if(dur.contains(":"))
                            {
                                dur = dur.replace(":",".");
                            }

                            if(dur.equalsIgnoreCase("00.00")
                                       ||dur.equalsIgnoreCase("0.0"))
                            {
                                dur = "0";
                            }

                            hashMap.put("duration",dur);

                            skinToSkinList.add(hashMap);
                        }


                        JSONArray babyWeight = jsonObjectMain.getJSONArray("babyWeight");

                       babyWeightList.clear();

                        for (int i = 0; i < babyWeight.length(); i++) {

                            JSONObject arrayJSONObject = babyWeight.getJSONObject(i);

                            HashMap<String, String> hashMap = new HashMap();

                            hashMap.put("age",arrayJSONObject.getString("day"));
                            hashMap.put("babyWeight",arrayJSONObject.getString("weigth"));

                            babyWeightList.add(hashMap);

                        }


                        JSONArray babyPulseRate = jsonObjectMain.getJSONArray("pulseRate");

                        babyPulseList.clear();

                        for (int i = 0; i < babyPulseRate.length(); i++) {

                            JSONObject ArrayJSONObject = babyPulseRate.getJSONObject(i);

                            HashMap<String, String> hashMap = new HashMap();

                            hashMap.put("age",ArrayJSONObject.getString("age"));
                            hashMap.put("babyPulseRate",ArrayJSONObject.getString("pulseRate"));

                            if(!ArrayJSONObject.getString("pulseRate").isEmpty())
                            {
                                babyPulseList.add(hashMap);
                            }
                        }


                        JSONArray babySpo2 = jsonObjectMain.getJSONArray("spo2");

                        babySpo2List.clear();

                        for (int i = 0; i < babySpo2.length(); i++) {

                            JSONObject ArrayJSONObject = babySpo2.getJSONObject(i);

                            HashMap<String, String> hashMap = new HashMap();

                            hashMap.put("age",ArrayJSONObject.getString("age"));
                            hashMap.put("babySpO2",ArrayJSONObject.getString("spo2"));

                            if(!ArrayJSONObject.getString("spo2").isEmpty())
                            {
                                babySpo2List.add(hashMap);
                            }
                        }

                        generateWeightData(view);
                        generateKMCData(view);
                        generateFeedingData(view);
                        generateRespiratoryData(view);
                        generateTempData(view);
                        generatePulseData(view);
                        generateSpo2Data(view);

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


    //Get Baby Ccase Details
    private void getBabyCaseDetailApi(String date) {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {

            jsonData.put("coachId", AppSettings.getString(AppSettings.coachId));
            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("babyId", AppSettings.getString(AppSettings.babyId));
            jsonData.put("date", date);

            json.put(AppConstants.projectName, jsonData);

            Log.v("getBabyDetailApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.getCaseSheetDetail, json, true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        tvReceivedTimeCS.setText("0");
                        ivKmcEmojiCS.setImageResource(R.drawable.ic_sad_smily);
                        ivDailyWeighingCS.setImageResource(R.drawable.ic_sad_smily);
                        etOrderedCS.setText("0");
                        etSampleCollectedCS.setText("0");
                        etReportAvailCS.setText("0");
                        tvInfantWeightBirth.setText(getString(R.string.notApplicableShortValue));
                        tvInfantWeightPrevious.setText(getString(R.string.notApplicableShortValue));
                        ivRoutineAssessmentCS.setImageResource(R.drawable.ic_sad_smily);

                        String duration = jsonObject.getString("kmcHours");

                        Log.d("duration",duration);

                        String unit = "";
                        if(duration.equals("0"))
                        {
                            unit = getString(R.string.hour);
                        }
                        else
                        {
                            String[] parts = duration.split(":");

                            Log.d("time-hour",parts[0]);
                            Log.d("time-min",parts[1]);

                            if(parts[0].equals("0")||parts[0].equals("1")||parts[0].equals("01"))
                            {
                                unit = getString(R.string.hour);
                            }
                            else
                            {
                                unit = getString(R.string.hours);
                            }
                        }

                        tvReceivedTimeCS.setText(duration+" "+unit);

                        if(duration.contains(":"))
                        {
                            String[] parts = duration.split(":");

                            Log.d("hour",parts[0]);
                            Log.d("min",parts[1]);

                            int hour = Integer.parseInt(parts[0]);
                            int min = Integer.parseInt(parts[1]);

                            if(hour<11)
                            {
                                ivKmcEmojiCS.setImageResource(R.drawable.ic_sad_smily);
                            }
                            else   if(hour>11&&hour<21)
                            {
                                ivKmcEmojiCS.setImageResource(R.drawable.ic_neutral_status);
                            }
                            else
                            {
                                ivKmcEmojiCS.setImageResource(R.drawable.ic_happy_smily);
                            }
                        }
                        else
                        {
                            ivKmcEmojiCS.setImageResource(R.drawable.ic_sad_smily);
                        }

                        if(jsonObject.getString("weightChangeSinceBirthStatus").equalsIgnoreCase("2"))
                        {
                            tvInfantWeightBirth.setText(getString(R.string.infantHasGained)+" "+ jsonObject.getString("weightChangeSinceBirth") +" "+ getString(R.string.grams) +" "+getString(R.string.sinceBirthWeight));
                        }
                        else  if(jsonObject.getString("weightChangeSinceBirthStatus").equalsIgnoreCase("1"))
                        {
                            tvInfantWeightBirth.setText(getString(R.string.infantHasLoss)+" "+ jsonObject.getString("weightChangeSinceBirth") +" "+ getString(R.string.grams) +" "+getString(R.string.sinceBirthWeight));
                        }
                        else
                        {
                            tvInfantWeightBirth.setText(getString(R.string.infantHasGained)+" "+ jsonObject.getString("weightChangeSinceBirth") +" "+ getString(R.string.grams) +" "+getString(R.string.sinceBirthWeight));
                        }


                        if(jsonObject.getString("weightChangeSinceYesterdayStatus").equalsIgnoreCase("2"))
                        {
                            tvInfantWeightPrevious.setText(getString(R.string.infantHasGained)+" "+ jsonObject.getString("weightChangeSinceYesterday") +" "+ getString(R.string.grams) +" "+getString(R.string.sinceBirthWeight));
                        }
                        else  if(jsonObject.getString("weightChangeSinceYesterdayStatus").equalsIgnoreCase("1"))
                        {
                            tvInfantWeightPrevious.setText(getString(R.string.infantHasLoss)+" "+ jsonObject.getString("weightChangeSinceYesterday") +" "+ getString(R.string.grams) +" "+getString(R.string.sinceBirthWeight));
                        }
                        else
                        {
                            tvInfantWeightPrevious.setText(getString(R.string.infantHasGained)+" "+ jsonObject.getString("weightChangeSinceYesterday") +" "+ getString(R.string.grams) +" "+getString(R.string.sinceBirthWeight));
                        }

                        etOrderedCS.setText( jsonObject.getString("orderCount"));
                        etSampleCollectedCS.setText(jsonObject.getString("sampleCount"));
                        etReportAvailCS.setText(jsonObject.getString("resultCount"));

                        String weigth = jsonObject.getString("dailyWeighing");

                        if(weigth.equalsIgnoreCase("1"))
                        {
                            ivDailyWeighingCS.setImageResource(R.drawable.ic_happy_smily);
                        }
                        else
                        {
                            ivDailyWeighingCS.setImageResource(R.drawable.ic_sad_smily);
                        }

                        try {

                            arrayListAssessment.clear();
                            JSONObject jsonObjectMonitoring = jsonObject.getJSONObject("lastMonitoringData");

                            HashMap<String, String> hashlist = new HashMap();

                            hashlist.put("babyPulseRate", jsonObjectMonitoring.getString("pulseRate"));
                            hashlist.put("babySpO2", jsonObjectMonitoring.getString("spo2"));
                            hashlist.put("babyTemperature", jsonObjectMonitoring.getString("temperatureValue"));
                            hashlist.put("temperatureUnit", jsonObjectMonitoring.getString("temperatureUnit"));
                            hashlist.put("babyRespiratoryRate", jsonObjectMonitoring.getString("respiratoryRate"));
                            hashlist.put("isPulseOximatoryDeviceAvailable", jsonObjectMonitoring.getString("isPulseOximatoryDeviceAvail"));

                            arrayListAssessment.add(hashlist);

                            if(arrayListAssessment.size()>0)
                            {
                                int checkPulse=0,checkSpo2=0,checkTemp=0,checkResp=0;

                                int pulse = 0;
                                try {

                                    if(!arrayListAssessment.get(0).get("babyPulseRate").isEmpty())
                                    {
                                        pulse = Integer.parseInt(arrayListAssessment.get(0).get("babyPulseRate"));
                                        if(pulse<75||pulse>200)
                                        {
                                            checkPulse=0;
                                        }
                                        else
                                        {
                                            checkPulse=1;
                                        }
                                    }

                                } catch (NumberFormatException e) {
                                    //e.printStackTrace();
                                    checkPulse=1;
                                }


                                int spo2=0;
                                try {

                                    if(!arrayListAssessment.get(0).get("babySpO2").isEmpty())
                                    {
                                        spo2 = Integer.parseInt(arrayListAssessment.get(0).get("babySpO2"));

                                        if(spo2<95)
                                        {
                                            checkSpo2=0;
                                        }
                                        else
                                        {
                                            checkSpo2=1;
                                        }
                                    }
                                } catch (NumberFormatException e) {
                                    //e.printStackTrace();
                                    checkSpo2=0;
                                }

                                float temp=0;
                                try {
                                    try {
                                        temp = Float.parseFloat(arrayListAssessment.get(0).get("babyTemperature"));
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }

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
                                    try {
                                        res = Float.parseFloat(arrayListAssessment.get(0).get("babyRespiratoryRate"));
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }

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

                                if(arrayListAssessment.get(0).get("temperatureUnit").equals(getString(R.string.degreeCValue)))
                                {
                                    tvSF1CS.setText(getString(R.string.temperature)+": "+temp +" "+ getString(R.string.degreeC));
                                }
                                else
                                {
                                    tvSF1CS.setText(getString(R.string.temperature)+": "+temp +" "+ getString(R.string.degreeF));
                                }

                                tvSF2CS.setText(getString(R.string.pulseOxidation)+": "+getString(R.string.notApplicableShortValue));
                                tvSF3CS.setText(getString(R.string.spo2)+": "+getString(R.string.notApplicableShortValue));
                                tvSF4CS.setText(getString(R.string.respiratoryRate)+": "+res+" "+getString(R.string.min));

                                if(arrayListAssessment.get(0).get("isPulseOximatoryDeviceAvailable").equalsIgnoreCase(getString(R.string.yesValue)))
                                {
                                    tvSF2CS.setText(getString(R.string.pulseOxidation)+": "+pulse+" "+getString(R.string.bpm));
                                    tvSF3CS.setText(getString(R.string.spo2)+": "+spo2);

                                    if(checkPulse==0||checkSpo2==0||checkTemp==0||checkResp==0)
                                    {
                                        ivRoutineAssessmentCS.setImageResource(R.drawable.ic_sad_smily);
                                        ivInfantGrowthStatus.setImageResource(R.drawable.ic_sad_smily);
                                    }
                                    else
                                    {
                                        ivRoutineAssessmentCS.setImageResource(R.drawable.ic_happy_smily);
                                        ivInfantGrowthStatus.setImageResource(R.drawable.ic_happy_smily);
                                    }
                                }
                                else  if(checkTemp==0||checkResp==0)
                                {
                                    ivRoutineAssessmentCS.setImageResource(R.drawable.ic_sad_smily);
                                    ivInfantGrowthStatus.setImageResource(R.drawable.ic_sad_smily);
                                }
                                else
                                {
                                    ivRoutineAssessmentCS.setImageResource(R.drawable.ic_happy_smily);
                                    ivInfantGrowthStatus.setImageResource(R.drawable.ic_happy_smily);
                                }
                            }

                            tvPrescribedNutritionCS.setText(getString(R.string.prescribed)+" "+jsonObject.getString("prescribedFeeding")+" "+getString(R.string.ml));
                            tvReceivedNutritionCS.setText(getString(R.string.received)+" "+jsonObject.getString("feedingQuantity")+" "+getString(R.string.ml));
                            tvNutritionQuantity.setText(jsonObject.getString("feedingQuantity"));
                            tvNutritionTime.setText(jsonObject.getString("feedingTimes"));

                        } catch (Exception e) {
                            e.printStackTrace();
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

    @Override
    public void onResume() {
        super.onResume();

        init(view);
    }
}

