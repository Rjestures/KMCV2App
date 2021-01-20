package code.registration;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kmcapp.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.common.DecimalDigitsInputFilter;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableBabyAdmission;
import code.database.TableBabyMonitoring;
import code.database.TableUser;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;

import static code.registration.RegistrationActivity.uuid;

public class BabyAssessmentFragment extends BaseFragment implements View.OnClickListener {

    String selectedReason = "";
    EditText etReason;
    int currentPage = 1, checkPulse = 0;
    String tempUnit = "", thermometerAvailable = "", pulseAvailable = "", crt = "", crtValue = "", gestAge = "", alertness = "", tone = "",
            color = "", apnea = "", grunting = "", chest = "", interest = "", lactation = "", sucking = "", umbilicus = "", skinRash = "",
            complication1 = "", complication2 = "", complication3 = "", complication4 = "", complication5 = "", bulging = "", convulsions = "",
            bleeding = "";
    int press = 1, timer = 0;
    String selectedReasonValue = "";
    int selectedReasonSpinnerPosition;
    JSONObject jsonObject = new JSONObject();
    //GridLayoutManager
    GridLayoutManager mGridLayoutManager;
    Adapter adapter;
    ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
    //RelativeLayout
    private RelativeLayout rlNext, rlPrevious, rlBottom;
    //ArrayList
    private ArrayList<String> tempText = new ArrayList<String>();
    private ArrayList<String> tempValue = new ArrayList<String>();
    private ArrayList<String> reasonValue;
    private ArrayList<String> pulseReasonValue;
    //View
    private View viewVitals, viewGest, viewGeneral, viewRespiratory, viewFeeding, viewMiscellaneous;
    //LinearLayout
    private LinearLayout llCrtReason, llHeaderAssessment, llVitalTemp, llVitalResp, llVitalPulse, llGestAge, llGenExamination,
            llRespAssessment, llFeedingAssessment, llMiscellaneousStep1, llMiscellaneousStep2;
    //ImageView
    private ImageView ivGestOption1Info, ivGestOption2Info, ivGestOption3Info, ivGestOption4Info;
    //Vitals
    private RelativeLayout rlPulseCrtNote, rlPulseNote, rlNote, rlYes, rlNo, rlReason, rlPress, rlPulseYes, rlPulseNo, rlCrtKnowYes, rlCrtKnowNo, rlCrtYes, rlCrtNo,
            rlCrt, rlGestOption1, rlGestOption2, rlGestOption3, rlGestOption4, rlAlert, rlLethargic, rlComatose,
            rlActive, rlLimp, rlTone, rloo_color, rlPale, rlCentral, rlPeripheral, rlJaundice, rlApeanPresent, rlApeanAbsent, rlApeanUnable,
            rlGruntingPresent, rlGruntingAbsent, rlGruntingUnable, rlChestPresent, rlChestAbsent, rlChestUnable,
            rlInterestYes, rlInterestNo, rlSufficientYes, rlSufficientNo, rlGood, rlPoor, rlNoSucking, rlRed,
            rlDischarge, rlNormal, rlNoneObserved, rlYesLessThen10, rlYesGreaterThen10, rlAbscess, rlPerinatalAsphyxia,
            rlBabyOfDiabeticMother, rlMeconiumAspiration, rlMajorCongenitalMalformation, rlAnyOther, rlPresent,
            rlAbsent, rlPresentOnAdmis, rlPastHistory, rlConvulsionsNo, rlBleedingPresent, rlBleedingAbsent, rlTemp,
            rlAdmit, rlRefer, rlTempe;
    //ImageView
    private ImageView ivYes, ivNo, ivPulseYes, ivPulseNo, ivCrtKnowYes, ivCrtKnowNo, ivCrtYes, ivCrtNo,
            ivGestOption1, ivGestOption2, ivGestOption3, ivGestOption4, ivAlertnessInfo, ivAlert, ivLethargic,
            ivComatose, ivComatoseInfo, ivToneInfo, ivActive, ivLimp, ivTone, ivoo_color, ivPale, ivCentral, ivCentralInfo,
            ivPeripheral, ivPeripheralInfo, ivJaundice, ivJaundiceInfo, ivApneaInfo, ivApeanPresent, ivApeanAbsent,
            ivApeanUnable, ivGruntingInfo, ivGruntingPresent, ivGruntingAbsent, ivGruntingUnable, ivChestInfo,
            ivChestPresent, ivChestAbsent, ivChestUnable, ivInterestYes, ivInterestNo, ivSufficientYes, ivSufficientNo,
            ivGood, ivPoor, ivNoSucking, ivUmbiliusInfo, ivRed, ivDischarge, ivNormal, ivNoneObserved, ivYesLessThen10,
            ivYesGreaterThen10, ivAbscess, ivPerinatalAsphyxia, ivPerinatalInfo, ivBabyOfDiabeticMother,
            ivMeconiumAspiration, ivMeconiumAspirationInfo, ivBulgingInfo, ivMajorCongenitalMalformation, ivAnyOther,
            ivPresent, ivAbsent, ivPresentOnAdmis, ivPastHistory, ivConvulsionsNo, ivBleedingPresent, ivBleedingAbsent;
    private ScrollView svFindings;
    private Spinner spinnerCrtReason, spinnerReason, spinnerPulseReason;
    //EditText
    private EditText etPulseCrtReason, etPulseReason, etTemperature, etSpo2, etPulse, etUrineTimes, etStoolTimes, etOtherSpecify;
    //Spinner
    private Spinner spinnerTempUnit;
    //Chronometer
    private Chronometer chronometer;
    //TextView
    private TextView tvCounter, tvStart, tvRespiRateCount, tvGestOption1, tvGestOption2, tvGestOption3, tvGestOption4,
            tvTemperatureResult, tvRespiRateResult, tvPulseResult, tvSpO2Result, tvCftResult, tvGestResult, tvAlertResult,
            tvToneResult, tvColorResult, tvApneaResult, tvGruntingResult, tvChestResult, tvInterestResult, tvLactationResult,
            tvSuckingResult, tvUmbilicusResult, tvSkinResult, tvBulgingResult, tvConvulsionsResult, tvBleedingResult,
            tvUrineResult, tvStoolResult;
    //RecyclerView
    private RecyclerView rvComplications;
    //LinearLayout
    private LinearLayout llOximeter, llPulseReason;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_baby_assessment, container, false);

        RegistrationActivity.tvMotherAdmission.setBackgroundResource(R.drawable.rectangle_teal_selected);
        RegistrationActivity.tvMotherAdmission.setTextColor(getResources().getColor(R.color.white));
        RegistrationActivity.tvBabyAdmission.setBackgroundResource(R.drawable.rectangle_teal_selected);
        RegistrationActivity.tvBabyAdmission.setTextColor(getResources().getColor(R.color.white));
        RegistrationActivity.tvAssessment.setBackgroundResource(R.drawable.rectangle_teal_selected);
        RegistrationActivity.tvAssessment.setTextColor(getResources().getColor(R.color.white));

        findViewById(v);
        findViewByIdVital(v);
        findViewByIdGest(v);
        findViewByIdGen(v);
        findViewByIdResp(v);
        findViewByIdFeeding(v);
        findViewByIdMiss1(v);
        findViewByIdMiss2(v);
        findViewByIdFindings(v);
        setClickListner();

        setStepValues();

        return v;
    }

    private void findViewById(View v) {

        //RelativeLayout
        rlNext = v.findViewById(R.id.rlNext);
        rlPrevious = v.findViewById(R.id.rlPrevious);
        rlBottom = v.findViewById(R.id.rlBottom);
        //View
        viewVitals = v.findViewById(R.id.viewVitals);
        viewGest = v.findViewById(R.id.viewGest);
        viewGeneral = v.findViewById(R.id.viewGeneral);
        viewRespiratory = v.findViewById(R.id.viewRespiratory);
        viewFeeding = v.findViewById(R.id.viewFeeding);
        viewMiscellaneous = v.findViewById(R.id.viewMiscellaneous);
        //LinearLayout
        llHeaderAssessment = v.findViewById(R.id.llHeaderAssessment);
        llVitalTemp = v.findViewById(R.id.llVitalTemp);
        llVitalResp = v.findViewById(R.id.llVitalResp);
        llVitalPulse = v.findViewById(R.id.llVitalPulse);
        llGestAge = v.findViewById(R.id.llGestAge);
        llGenExamination = v.findViewById(R.id.llGenExamination);
        llRespAssessment = v.findViewById(R.id.llRespAssessment);
        llFeedingAssessment = v.findViewById(R.id.llFeedingAssessment);
        llMiscellaneousStep1 = v.findViewById(R.id.llMiscellaneousStep1);
        llMiscellaneousStep2 = v.findViewById(R.id.llMiscellaneousStep2);
        svFindings = v.findViewById(R.id.svFindings);
        etReason = v.findViewById(R.id.etReason);
    }

    private void findViewByIdVital(View v) {

        //RelativeLayout
        rlYes = v.findViewById(R.id.rlYes);
        rlNote = v.findViewById(R.id.rlNote);
        rlPulseNote = v.findViewById(R.id.rlPulseNote);
        rlNo = v.findViewById(R.id.rlNo);
        spinnerReason = v.findViewById(R.id.spinnerReason);
        spinnerPulseReason = v.findViewById(R.id.spinnerPulseReason);
        rlReason = v.findViewById(R.id.rlReason);
        rlPress = v.findViewById(R.id.rlPress);
        rlPulseYes = v.findViewById(R.id.rlPulseYes);
        rlPulseNo = v.findViewById(R.id.rlPulseNo);
        rlCrtKnowYes = v.findViewById(R.id.rlCrtKnowYes);
        rlCrtKnowNo = v.findViewById(R.id.rlCrtKnowNo);
        rlCrtYes = v.findViewById(R.id.rlCrtYes);
        rlCrtNo = v.findViewById(R.id.rlCrtNo);
        rlCrt = v.findViewById(R.id.rlCrt);
        rlTemp = v.findViewById(R.id.rlTemp);
        rlTempe = v.findViewById(R.id.rlTempe);

        //ImageView
        ivYes = v.findViewById(R.id.ivYes);
        ivNo = v.findViewById(R.id.ivNo);
        ivPulseYes = v.findViewById(R.id.ivPulseYes);
        ivPulseNo = v.findViewById(R.id.ivPulseNo);
        ivCrtKnowYes = v.findViewById(R.id.ivCrtKnowYes);
        ivCrtKnowNo = v.findViewById(R.id.ivCrtKnowNo);
        ivCrtYes = v.findViewById(R.id.ivCrtYes);
        ivCrtNo = v.findViewById(R.id.ivCrtNo);

        //EditText
        etTemperature = v.findViewById(R.id.etTemperature);
        etPulseReason = v.findViewById(R.id.etPulseReason);
        etSpo2 = v.findViewById(R.id.etSpo2);
        etPulse = v.findViewById(R.id.etPulse);

        etTemperature.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});

        //Spinner
        spinnerTempUnit = v.findViewById(R.id.spinnerTempUnit);


        //Chronometer
        chronometer = v.findViewById(R.id.chronometer);

        //TextView
        tvCounter = v.findViewById(R.id.tvCounter);
        tvStart = v.findViewById(R.id.tvStart);
        tvRespiRateCount = v.findViewById(R.id.tvRespiRateCount);

        //LinearLayout
        llOximeter = v.findViewById(R.id.llOximeter);
        llPulseReason = v.findViewById(R.id.llPulseReason);

//        SharedPreferences sharedpreferences = .getSharedPreferences("MyPref", Context.MODE_PRIVATE);
//        try {
//            jsonObject=  new JSONObject(sharedpreferences.getString("AllDataRecord",""));
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


        tempText.clear();
        tempText.add(getString(R.string.degreeF));
        tempText.add(getString(R.string.degreeC));

        tempValue.clear();
        tempValue.add(getString(R.string.degreeFValue));
        tempValue.add(getString(R.string.degreeCValue));

        spinnerTempUnit.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, tempText));
        spinnerTempUnit.setSelection(0);

        rlTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerTempUnit.performClick();
            }
        });

        //Spinner for Relation
        spinnerTempUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

//                tempUnit =   tempValue.get(position);
                tempUnit = spinnerTempUnit.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        //setOnClickListener
        rlYes.setOnClickListener(this);
        rlNo.setOnClickListener(this);
        rlPress.setOnClickListener(this);
        rlPulseYes.setOnClickListener(this);
        rlPulseNo.setOnClickListener(this);
        rlCrt.setOnClickListener(this);
        rlCrtKnowYes.setOnClickListener(this);
        rlCrtKnowNo.setOnClickListener(this);
        rlCrtYes.setOnClickListener(this);
        rlCrtNo.setOnClickListener(this);
        rlReason.setOnClickListener(this);
        rlNote.setOnClickListener(this);


    }

    private void findViewByIdGest(View v) {

        //RelativeLayout
        rlGestOption1 = v.findViewById(R.id.rlGestOption1);
        rlGestOption2 = v.findViewById(R.id.rlGestOption2);
        rlGestOption3 = v.findViewById(R.id.rlGestOption3);
        rlGestOption4 = v.findViewById(R.id.rlGestOption4);

        //ImageView
        ivGestOption1 = v.findViewById(R.id.ivGestOption1);
        ivGestOption2 = v.findViewById(R.id.ivGestOption2);
        ivGestOption3 = v.findViewById(R.id.ivGestOption3);
        ivGestOption4 = v.findViewById(R.id.ivGestOption4);
        ivGestOption1Info = v.findViewById(R.id.ivGestOption1Info);
        ivGestOption2Info = v.findViewById(R.id.ivGestOption2Info);
        ivGestOption3Info = v.findViewById(R.id.ivGestOption3Info);
        ivGestOption4Info = v.findViewById(R.id.ivGestOption4Info);

        //TextView
        tvGestOption1 = v.findViewById(R.id.tvGestOption1);
        tvGestOption2 = v.findViewById(R.id.tvGestOption2);
        tvGestOption3 = v.findViewById(R.id.tvGestOption3);
        tvGestOption4 = v.findViewById(R.id.tvGestOption4);

        //ImageView SetOnClickListener
        ivGestOption1Info.setOnClickListener(this);
        ivGestOption2Info.setOnClickListener(this);
        ivGestOption3Info.setOnClickListener(this);
        ivGestOption4Info.setOnClickListener(this);
        rlGestOption1.setOnClickListener(this);
        rlGestOption2.setOnClickListener(this);
        rlGestOption3.setOnClickListener(this);
        rlGestOption4.setOnClickListener(this);
    }

    private void findViewByIdGen(View v) {

        //RelativeLayout
        rlAlert = v.findViewById(R.id.rlAlert);
        rlLethargic = v.findViewById(R.id.rlLethargic);
        rlComatose = v.findViewById(R.id.rlComatose);
        rloo_color = v.findViewById(R.id.rloo_color);
        rlActive = v.findViewById(R.id.rlActive);
        rlLimp = v.findViewById(R.id.rlLimp);
        rlTone = v.findViewById(R.id.rlTone);
        rlPale = v.findViewById(R.id.rlPale);
        rlCentral = v.findViewById(R.id.rlCentral);
        rlPeripheral = v.findViewById(R.id.rlPeripheral);
        rlJaundice = v.findViewById(R.id.rlJaundice);

        //ImageView
        ivAlertnessInfo = v.findViewById(R.id.ivAlertnessInfo);
        ivAlert = v.findViewById(R.id.ivAlert);
        ivLethargic = v.findViewById(R.id.ivLethargic);
        ivComatose = v.findViewById(R.id.ivComatose);
        ivComatoseInfo = v.findViewById(R.id.ivComatoseInfo);
        ivToneInfo = v.findViewById(R.id.ivToneInfo);
        ivActive = v.findViewById(R.id.ivActive);
        ivLimp = v.findViewById(R.id.ivLimp);
        ivTone = v.findViewById(R.id.ivTone);
        ivoo_color = v.findViewById(R.id.ivoo_color);
        ivPale = v.findViewById(R.id.ivPale);
        ivCentral = v.findViewById(R.id.ivCentral);
        ivCentralInfo = v.findViewById(R.id.ivCentralInfo);
        ivPeripheral = v.findViewById(R.id.ivPeripheral);
        ivPeripheralInfo = v.findViewById(R.id.ivPeripheralInfo);
        ivJaundice = v.findViewById(R.id.ivJaundice);
        ivJaundiceInfo = v.findViewById(R.id.ivJaundiceInfo);

        rlAlert.setOnClickListener(this);
        rlLethargic.setOnClickListener(this);
        rlComatose.setOnClickListener(this);
        ivAlertnessInfo.setOnClickListener(this);
        ivComatoseInfo.setOnClickListener(this);
        ivToneInfo.setOnClickListener(this);
        ivCentralInfo.setOnClickListener(this);
        ivPeripheralInfo.setOnClickListener(this);
        ivJaundiceInfo.setOnClickListener(this);
        rlActive.setOnClickListener(this);
        rlLimp.setOnClickListener(this);
        rlTone.setOnClickListener(this);
        rloo_color.setOnClickListener(this);
        rlPale.setOnClickListener(this);
        rlCentral.setOnClickListener(this);
        rlPeripheral.setOnClickListener(this);
        rlJaundice.setOnClickListener(this);
    }

    private void findViewByIdResp(View v) {

        //ImageView
        ivApneaInfo = v.findViewById(R.id.ivApneaInfo);
        ivApeanPresent = v.findViewById(R.id.ivApeanPresent);
        ivApeanAbsent = v.findViewById(R.id.ivApeanAbsent);
        ivApeanUnable = v.findViewById(R.id.ivApeanUnable);
        ivGruntingInfo = v.findViewById(R.id.ivGruntingInfo);
        ivGruntingPresent = v.findViewById(R.id.ivGruntingPresent);
        ivGruntingAbsent = v.findViewById(R.id.ivGruntingAbsent);
        ivGruntingUnable = v.findViewById(R.id.ivGruntingUnable);
        ivChestInfo = v.findViewById(R.id.ivChestInfo);
        ivChestPresent = v.findViewById(R.id.ivChestPresent);
        ivChestAbsent = v.findViewById(R.id.ivChestAbsent);
        ivChestUnable = v.findViewById(R.id.ivChestUnable);

        //RelativeLayout
        rlApeanPresent = v.findViewById(R.id.rlApeanPresent);
        rlApeanAbsent = v.findViewById(R.id.rlApeanAbsent);
        rlApeanUnable = v.findViewById(R.id.rlApeanUnable);
        rlGruntingPresent = v.findViewById(R.id.rlGruntingPresent);
        rlGruntingAbsent = v.findViewById(R.id.rlGruntingAbsent);
        rlGruntingUnable = v.findViewById(R.id.rlGruntingUnable);
        rlChestPresent = v.findViewById(R.id.rlChestPresent);
        rlChestAbsent = v.findViewById(R.id.rlChestAbsent);
        rlChestUnable = v.findViewById(R.id.rlChestUnable);

        ivApneaInfo.setOnClickListener(this);
        ivGruntingInfo.setOnClickListener(this);
        ivChestInfo.setOnClickListener(this);
        rlApeanPresent.setOnClickListener(this);
        rlApeanAbsent.setOnClickListener(this);
        rlApeanUnable.setOnClickListener(this);
        rlGruntingPresent.setOnClickListener(this);
        rlGruntingAbsent.setOnClickListener(this);
        rlGruntingUnable.setOnClickListener(this);
        rlChestPresent.setOnClickListener(this);
        rlChestAbsent.setOnClickListener(this);
        rlChestUnable.setOnClickListener(this);
    }

    private void findViewByIdFeeding(View v) {

        //RelativeLayout
        rlInterestYes = v.findViewById(R.id.rlInterestYes);
        rlInterestNo = v.findViewById(R.id.rlInterestNo);
        rlSufficientYes = v.findViewById(R.id.rlSufficientYes);
        rlSufficientNo = v.findViewById(R.id.rlSufficientNo);
        rlGood = v.findViewById(R.id.rlGood);
        rlPoor = v.findViewById(R.id.rlPoor);
        rlNoSucking = v.findViewById(R.id.rlNoSucking);

        //ImageView
        ivInterestYes = v.findViewById(R.id.ivInterestYes);
        ivInterestNo = v.findViewById(R.id.ivInterestNo);
        ivSufficientYes = v.findViewById(R.id.ivSufficientYes);
        ivSufficientNo = v.findViewById(R.id.ivSufficientNo);
        ivGood = v.findViewById(R.id.ivGood);
        ivPoor = v.findViewById(R.id.ivPoor);
        ivNoSucking = v.findViewById(R.id.ivNoSucking);

        //setOnClickListener
        rlInterestYes.setOnClickListener(this);
        rlInterestNo.setOnClickListener(this);
        rlSufficientYes.setOnClickListener(this);
        rlSufficientNo.setOnClickListener(this);
        rlGood.setOnClickListener(this);
        rlPoor.setOnClickListener(this);
        rlNoSucking.setOnClickListener(this);
    }

    private void findViewByIdMiss1(View v) {

        //RelativeLayout
        rlRed = v.findViewById(R.id.rlRed);
        rlDischarge = v.findViewById(R.id.rlDischarge);
        rlNormal = v.findViewById(R.id.rlNormal);
        rlNoneObserved = v.findViewById(R.id.rlNoneObserved);
        rlYesLessThen10 = v.findViewById(R.id.rlYesLessThen10);
        rlYesGreaterThen10 = v.findViewById(R.id.rlYesGreaterThen10);
        rlAbscess = v.findViewById(R.id.rlAbscess);
        rlPerinatalAsphyxia = v.findViewById(R.id.rlPerinatalAsphyxia);
        rlBabyOfDiabeticMother = v.findViewById(R.id.rlBabyOfDiabeticMother);
        rlMeconiumAspiration = v.findViewById(R.id.rlMeconiumAspiration);
        rlMajorCongenitalMalformation = v.findViewById(R.id.rlMajorCongenitalMalformation);
        rlAnyOther = v.findViewById(R.id.rlAnyOther);
        rlPresent = v.findViewById(R.id.rlPresent);
        rlAbsent = v.findViewById(R.id.rlAbsent);

        //EditText
        etOtherSpecify = v.findViewById(R.id.etOtherSpecify);

        //ImageView
        ivUmbiliusInfo = v.findViewById(R.id.ivUmbiliusInfo);
        ivRed = v.findViewById(R.id.ivRed);
        ivDischarge = v.findViewById(R.id.ivDischarge);
        ivNormal = v.findViewById(R.id.ivNormal);
        ivNoneObserved = v.findViewById(R.id.ivNoneObserved);
        ivYesLessThen10 = v.findViewById(R.id.ivYesLessThen10);
        ivYesGreaterThen10 = v.findViewById(R.id.ivYesGreaterThen10);
        ivAbscess = v.findViewById(R.id.ivAbscess);
        ivPerinatalAsphyxia = v.findViewById(R.id.ivPerinatalAsphyxia);
        ivPerinatalInfo = v.findViewById(R.id.ivPerinatalInfo);
        ivBabyOfDiabeticMother = v.findViewById(R.id.ivBabyOfDiabeticMother);
        ivMeconiumAspiration = v.findViewById(R.id.ivMeconiumAspiration);
        ivMeconiumAspirationInfo = v.findViewById(R.id.ivMeconiumAspirationInfo);
        ivBulgingInfo = v.findViewById(R.id.ivBulgingInfo);
        ivMajorCongenitalMalformation = v.findViewById(R.id.ivMajorCongenitalMalformation);
        ivAnyOther = v.findViewById(R.id.ivAnyOther);
        ivPresent = v.findViewById(R.id.ivPresent);
        ivAbsent = v.findViewById(R.id.ivAbsent);

        ivUmbiliusInfo.setOnClickListener(this);
        ivPerinatalInfo.setOnClickListener(this);
        ivMeconiumAspirationInfo.setOnClickListener(this);
        ivBulgingInfo.setOnClickListener(this);
        rlRed.setOnClickListener(this);
        rlDischarge.setOnClickListener(this);
        rlNormal.setOnClickListener(this);
        rlNoneObserved.setOnClickListener(this);
        rlYesLessThen10.setOnClickListener(this);
        rlYesGreaterThen10.setOnClickListener(this);
        rlAbscess.setOnClickListener(this);
        rlPerinatalAsphyxia.setOnClickListener(this);
        rlBabyOfDiabeticMother.setOnClickListener(this);
        rlMeconiumAspiration.setOnClickListener(this);
        rlMajorCongenitalMalformation.setOnClickListener(this);
        rlAnyOther.setOnClickListener(this);
        rlPresent.setOnClickListener(this);
        rlAbsent.setOnClickListener(this);
    }

    private void findViewByIdMiss2(View v) {

        //RelativeLayout
        rlPresentOnAdmis = v.findViewById(R.id.rlPresentOnAdmis);
        rlPastHistory = v.findViewById(R.id.rlPastHistory);
        rlConvulsionsNo = v.findViewById(R.id.rlConvulsionsNo);
        rlBleedingPresent = v.findViewById(R.id.rlBleedingPresent);
        rlBleedingAbsent = v.findViewById(R.id.rlBleedingAbsent);

        //ImageView
        ivPresentOnAdmis = v.findViewById(R.id.ivPresentOnAdmis);
        ivPastHistory = v.findViewById(R.id.ivPastHistory);
        ivConvulsionsNo = v.findViewById(R.id.ivConvulsionsNo);
        ivBleedingPresent = v.findViewById(R.id.ivBleedingPresent);
        ivBleedingAbsent = v.findViewById(R.id.ivBleedingAbsent);

        //EditText
        etUrineTimes = v.findViewById(R.id.etUrineTimes);
        etStoolTimes = v.findViewById(R.id.etStoolTimes);

        //RelativeLayout
        rlPresentOnAdmis.setOnClickListener(this);
        rlPastHistory.setOnClickListener(this);
        rlConvulsionsNo.setOnClickListener(this);
        rlBleedingPresent.setOnClickListener(this);
        rlBleedingAbsent.setOnClickListener(this);
    }

    private void findViewByIdFindings(View v) {

        //TextView
        tvTemperatureResult = v.findViewById(R.id.tvTemperatureResult);
        tvRespiRateResult = v.findViewById(R.id.tvRespiRateResult);
        tvPulseResult = v.findViewById(R.id.tvPulseResult);
        tvSpO2Result = v.findViewById(R.id.tvSpO2Result);
        tvCftResult = v.findViewById(R.id.tvCftResult);

        tvGestResult = v.findViewById(R.id.tvGestResult);
        tvAlertResult = v.findViewById(R.id.tvAlertResult);
        tvToneResult = v.findViewById(R.id.tvToneResult);

        tvColorResult = v.findViewById(R.id.tvColorResult);
        tvApneaResult = v.findViewById(R.id.tvApneaResult);
        tvGruntingResult = v.findViewById(R.id.tvGruntingResult);
        tvChestResult = v.findViewById(R.id.tvChestResult);
        tvInterestResult = v.findViewById(R.id.tvInterestResult);
        tvLactationResult = v.findViewById(R.id.tvLactationResult);
        tvSuckingResult = v.findViewById(R.id.tvSuckingResult);
        tvUmbilicusResult = v.findViewById(R.id.tvUmbilicusResult);
        tvSkinResult = v.findViewById(R.id.tvSkinResult);
        tvBulgingResult = v.findViewById(R.id.tvBulgingResult);
        tvConvulsionsResult = v.findViewById(R.id.tvConvulsionsResult);
        tvBleedingResult = v.findViewById(R.id.tvBleedingResult);
        tvUrineResult = v.findViewById(R.id.tvUrineResult);
        tvStoolResult = v.findViewById(R.id.tvStoolResult);

        //RelativeLayout
        rlAdmit = v.findViewById(R.id.rlAdmit);
        rlRefer = v.findViewById(R.id.rlRefer);

        //RecyclerView
        rvComplications = v.findViewById(R.id.rvComplications);

        mGridLayoutManager = new GridLayoutManager(mActivity, 2);
        rvComplications.setLayoutManager(mGridLayoutManager);

        rlAdmit.setOnClickListener(this);
        rlRefer.setOnClickListener(this);

        currentPage = 1;
        setViewVisibility(currentPage);


    }

    private void setClickListner() {

        //RelativeLayout SetOnClickListener
        rlNext.setOnClickListener(this);
        rlPrevious.setOnClickListener(this);
    }

    private void setDefault() {

        //View
        viewVitals.setBackgroundResource(R.color.lightgreyback);
        viewGest.setBackgroundResource(R.color.lightgreyback);
        viewGeneral.setBackgroundResource(R.color.lightgreyback);
        viewRespiratory.setBackgroundResource(R.color.lightgreyback);
        viewFeeding.setBackgroundResource(R.color.lightgreyback);
        viewMiscellaneous.setBackgroundResource(R.color.lightgreyback);

        //LinearLayout
        rlBottom.setVisibility(View.VISIBLE);
        llHeaderAssessment.setVisibility(View.VISIBLE);
        llVitalTemp.setVisibility(View.GONE);
        llVitalResp.setVisibility(View.GONE);
        llVitalPulse.setVisibility(View.GONE);
        llGestAge.setVisibility(View.GONE);
        llGenExamination.setVisibility(View.GONE);
        llRespAssessment.setVisibility(View.GONE);
        llFeedingAssessment.setVisibility(View.GONE);
        llMiscellaneousStep1.setVisibility(View.GONE);
        llMiscellaneousStep2.setVisibility(View.GONE);
        rlPrevious.setVisibility(View.VISIBLE);
        svFindings.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rlNext:

                validation();

                break;

            case R.id.rlPrevious:
                RegistrationActivity.tvBabyAdmission.setTextColor(getResources().getColor(R.color.white));
                currentPage = currentPage - 1;
                setViewVisibility(currentPage);

                break;

            case R.id.ivGestOption1Info:

                AppUtils.AlertGestational(mActivity, 1);

                break;

            case R.id.ivGestOption2Info:

                AppUtils.AlertGestational(mActivity, 2);

                break;

            case R.id.ivGestOption3Info:

                AppUtils.AlertGestational(mActivity, 3);

                break;

            case R.id.ivGestOption4Info:

                AppUtils.AlertGestational(mActivity, 4);

                break;

            case R.id.ivAlertnessInfo:

                AppUtils.AlertOk(getString(R.string.alertnessDetail), mActivity, 3, getString(R.string.alertness));

                break;

            case R.id.ivComatoseInfo:

                AppUtils.AlertOk(getString(R.string.comatoseDetail), mActivity, 3, getString(R.string.comatose));

                break;

            case R.id.ivToneInfo:

                AppUtils.AlertOk(getString(R.string.toneDetail), mActivity, 3, getString(R.string.tone));

                break;

            case R.id.ivCentralInfo:

                AppUtils.AlertOk(getString(R.string.centralDetail), mActivity, 3, getString(R.string.centralCyanosis));

                break;

            case R.id.ivPeripheralInfo:

                AppUtils.AlertOk(getString(R.string.peripheralDetail), mActivity, 3, getString(R.string.peripheralCyanosis));

                break;

            case R.id.ivJaundiceInfo:

                AppUtils.AlertOk(getString(R.string.jaundiceDetail), mActivity, 3, getString(R.string.jaundice));

                break;

            case R.id.ivApneaInfo:

                AppUtils.AlertOk(getString(R.string.apneaDetail), mActivity, 3, getString(R.string.apnea));

                break;

            case R.id.ivGruntingInfo:

                AppUtils.AlertOk(getString(R.string.gruntingDetail), mActivity, 3, getString(R.string.grunting));

                break;

            case R.id.ivChestInfo:

                AppUtils.AlertOk(getString(R.string.chestDetail), mActivity, 3, getString(R.string.chestIndrawing));

                break;

            case R.id.ivUmbiliusInfo:

                AppUtils.AlertOk(getString(R.string.umbilicusDetail), mActivity, 3, getString(R.string.umbilicus));

                break;

            case R.id.ivPerinatalInfo:

                AppUtils.AlertOk(getString(R.string.perinatalDetail), mActivity, 3, getString(R.string.perinatalAsphyxia));

                break;

            case R.id.ivMeconiumAspirationInfo:

                AppUtils.AlertOk(getString(R.string.meconiumDetail), mActivity, 3, getString(R.string.meconiumAspiration));

                break;

            case R.id.ivBulgingInfo:

                AppUtils.AlertOk(getString(R.string.bulgingDetail), mActivity, 3, getString(R.string.bulgingAnteriorFontanelleMandResult));

                break;

            case R.id.rlYes:
                rlReason.setVisibility(View.GONE);
                rlNote.setVisibility(View.GONE);
                etTemperature.setEnabled(true);
                spinnerTempUnit.setEnabled(true);
                rlTempe.setVisibility(View.VISIBLE);
                setDefaultTemp();
                thermometerAvailable = getString(R.string.yesValue);
                ivYes.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlNo:
                rlReason.setVisibility(View.VISIBLE);
                etTemperature.setEnabled(false);
                spinnerTempUnit.setEnabled(false);
                rlTempe.setVisibility(View.GONE);
                setDefaultTemp();
                thermometerAvailable = getString(R.string.noValue);
                ivNo.setImageResource(R.drawable.ic_check_box_selected);

                if (spinnerReason.getSelectedItem().equals(mActivity.getString(R.string.other))) {
                    rlNote.setVisibility(View.VISIBLE);
                }

                spinnerReason.setSelection(0);
                break;

            case R.id.rlPress:

                if (press == 1) {
                    timer = 1;
                    press = 1;

                    int n = Integer.parseInt(tvCounter.getText().toString().trim());

                    if (n == 0) {
                        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                            @Override
                            public void onChronometerTick(Chronometer chronometerChanged) {
                                chronometer = chronometerChanged;

                                long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();

                                int time = 10000;

                                if (AppUtils.checkServer()) {
                                    //time = 10000;
                                    time = 30000;
                                }

                                if (elapsedMillis > time)
                                //if(elapsedMillis>10000)
                                {
                                    chronometer.stop();
                                    tvStart.setText(getString(R.string.start));
                                    tvRespiRateCount.setVisibility(View.VISIBLE);
                                    press = 0;
                                    timer = 0;
                                    rlPress.setClickable(false);
                                    tvRespiRateCount.setText(getString(R.string.respiratoryRate) + ": " + tvCounter.getText().toString().trim() + "/ min");
                                    AppSettings.putString(AppSettings.respiratoryRate, tvCounter.getText().toString().trim());
                                    int c = Integer.parseInt(tvCounter.getText().toString().trim());
                                    if (c < 30 || c > 60) {
                                        AppUtils.AlertOk(getString(R.string.dangerSituation), mActivity, 2, "");
                                    }

                                    tvCounter.setText("0");
                                    chronometer.setText("00:00");
                                    rlPress.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            rlPress.setClickable(true);
                                            press = 1;
                                        }
                                    }, 10000);
                                }
                            }
                        });

                        n = n + 1;
                        tvCounter.setText(String.valueOf(n));
                        tvStart.setText(getString(R.string.press));

                        if (chronometer != null) {
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            chronometer.stop();
                        }

                        chronometer.start();
                        chronometer.setFormat("%s"); // set the format for a chronometer
                    } else {
                        n = n + 1;
                        tvCounter.setText(String.valueOf(n));
                        tvStart.setText(getString(R.string.press));
                    }
                } else {

                }

                break;

            case R.id.rlPulseYes:

                setDefaultPulse();
                llPulseReason.setVisibility(View.GONE);
                pulseAvailable = getString(R.string.yesValue);
                ivPulseYes.setImageResource(R.drawable.ic_check_box_selected);
                llOximeter.setVisibility(View.VISIBLE);
                rlPulseNote.setVisibility(View.GONE);
                spinnerPulseReason.setSelection(0);

                break;

            case R.id.rlPulseNo:

                setDefaultPulse();
                llPulseReason.setVisibility(View.VISIBLE);
                pulseAvailable = getString(R.string.noValue);
                ivPulseNo.setImageResource(R.drawable.ic_check_box_selected);

                if (spinnerPulseReason.getSelectedItem().equals(mActivity.getString(R.string.other))) {
                    ivPulseNo.setVisibility(View.VISIBLE);
                }


                break;

            case R.id.rlCrtKnowYes:

                setDefaultCft();
                crt = getString(R.string.yesValue);
                ivCrtKnowYes.setImageResource(R.drawable.ic_check_box_selected);
                rlCrt.setVisibility(View.VISIBLE);
                break;

            case R.id.rlCrtKnowNo:

                setDefaultCft();
                crt = getString(R.string.noValue);
                ivCrtKnowNo.setImageResource(R.drawable.ic_check_box_selected);


                break;

            case R.id.rlCrtYes:

                setDefaultCftTime();
                crtValue = getString(R.string.yesValue);
                ivCrtYes.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlCrtNo:

                setDefaultCftTime();
                crtValue = getString(R.string.noValue);
                ivCrtNo.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlGestOption1:

                setDefaultGestAge();
                gestAge = getString(R.string.gestOption1Value);
                ivGestOption1.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlGestOption2:

                setDefaultGestAge();
                gestAge = getString(R.string.gestOption2Value);
                ivGestOption2.setImageResource(R.drawable.ic_check_box_selected);

                break;


            case R.id.rlGestOption3:

                setDefaultGestAge();
                gestAge = getString(R.string.gestOption3Value);
                ivGestOption3.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlGestOption4:

                setDefaultGestAge();
                gestAge = getString(R.string.gestOption4Value);
                ivGestOption4.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlAlert:

                setDefaultAlert();
                alertness = getString(R.string.alertValue);
                ivAlert.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlLethargic:

                setDefaultAlert();
                alertness = getString(R.string.lethargicValue);
                ivLethargic.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlComatose:

                setDefaultAlert();
                alertness = getString(R.string.comatoseValue);
                ivComatose.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlActive:

                setDefaultTone();
                tone = getString(R.string.activeValue);
                ivActive.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlLimp:

                setDefaultTone();
                tone = getString(R.string.limpValue);
                ivLimp.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlTone:

                setDefaultTone();
                tone = getString(R.string.toneValue);
                ivTone.setImageResource(R.drawable.ic_check_box_selected);

                break;


            case R.id.rloo_color:

                setDefaultColor();
                color = getString(R.string.pink);
                ivoo_color.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlPale:

                setDefaultColor();
                color = getString(R.string.paleValue);
                ivPale.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlCentral:

                setDefaultColor();
                color = getString(R.string.centralCyanosisValue);
                ivCentral.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlPeripheral:

                setDefaultColor();
                color = getString(R.string.peripheralCyanosisValue);
                ivPeripheral.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlJaundice:

                setDefaultColor();
                color = getString(R.string.jaundiceValue);
                ivJaundice.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlApeanPresent:

                setDefaultApnea();
                apnea = getString(R.string.presentValue);
                ivApeanPresent.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlApeanAbsent:

                setDefaultApnea();
                apnea = getString(R.string.absentValue);
                ivApeanAbsent.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlApeanUnable:

                setDefaultApnea();
                apnea = getString(R.string.unableToConfirmValue);
                ivApeanUnable.setImageResource(R.drawable.ic_check_box_selected);

                break;


            case R.id.rlGruntingPresent:

                setDefaultGrunting();
                grunting = getString(R.string.presentValue);
                ivGruntingPresent.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlGruntingAbsent:

                setDefaultGrunting();
                grunting = getString(R.string.absentValue);
                ivGruntingAbsent.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlGruntingUnable:

                setDefaultGrunting();
                grunting = getString(R.string.unableToConfirmValue);
                ivGruntingUnable.setImageResource(R.drawable.ic_check_box_selected);

                break;


            case R.id.rlChestPresent:

                setDefaultChest();
                chest = getString(R.string.presentValue);
                ivChestPresent.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlChestAbsent:

                setDefaultChest();
                chest = getString(R.string.absentValue);
                ivChestAbsent.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlChestUnable:

                setDefaultChest();
                chest = getString(R.string.unableToConfirmValue);
                ivChestUnable.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlInterestYes:

                setDefaultInterest();
                interest = getString(R.string.yesValue);
                ivInterestYes.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlInterestNo:

                setDefaultInterest();
                interest = getString(R.string.noValue);
                ivInterestNo.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlSufficientYes:

                setDefaultLactation();
                lactation = getString(R.string.yesValue);
                ivSufficientYes.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlSufficientNo:

                setDefaultLactation();
                lactation = getString(R.string.noValue);
                ivSufficientNo.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlGood:

                setDefaultSucking();
                sucking = getString(R.string.goodValue);
                ivGood.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlPoor:

                setDefaultSucking();
                sucking = getString(R.string.poorValue);
                ivPoor.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlNoSucking:

                setDefaultSucking();
                sucking = getString(R.string.unableToConfirmValue);
                ivNoSucking.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlRed:

                setDefaultUmbilicus();
                umbilicus = getString(R.string.redValue);
                ivRed.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlDischarge:

                setDefaultUmbilicus();
                umbilicus = getString(R.string.dischargeValue);
                ivDischarge.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlNormal:

                setDefaultUmbilicus();
                umbilicus = getString(R.string.normalValue);
                ivNormal.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlNoneObserved:

                setDefaultSkinRash();
                skinRash = getString(R.string.noneObservedValue);
                ivNoneObserved.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlYesLessThen10:

                setDefaultSkinRash();
                skinRash = getString(R.string.yes_10Value);
                ivYesLessThen10.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlYesGreaterThen10:

                setDefaultSkinRash();
                skinRash = getString(R.string.yesGreater10Value);
                ivYesGreaterThen10.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlAbscess:

                setDefaultSkinRash();
                skinRash = getString(R.string.abscessValue);
                ivAbscess.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlPerinatalAsphyxia:

                if (complication1.isEmpty()) {
                    complication1 = getString(R.string.complications1Value);
                    ivPerinatalAsphyxia.setImageResource(R.drawable.ic_check_box_selected);
                } else {
                    complication1 = "";
                    ivPerinatalAsphyxia.setImageResource(R.drawable.ic_check_box);
                }

                break;

            case R.id.rlBabyOfDiabeticMother:

                if (complication2.isEmpty()) {
                    complication2 = getString(R.string.complications2Value);
                    ivBabyOfDiabeticMother.setImageResource(R.drawable.ic_check_box_selected);
                } else {
                    complication2 = "";
                    ivBabyOfDiabeticMother.setImageResource(R.drawable.ic_check_box);
                }

                break;

            case R.id.rlMeconiumAspiration:

                if (complication3.isEmpty()) {
                    complication3 = getString(R.string.complications3Value);
                    ivMeconiumAspiration.setImageResource(R.drawable.ic_check_box_selected);
                } else {
                    complication3 = "";
                    ivMeconiumAspiration.setImageResource(R.drawable.ic_check_box);
                }

                break;

            case R.id.rlMajorCongenitalMalformation:

                if (complication4.isEmpty()) {
                    complication4 = getString(R.string.complications4Value);
                    ivMajorCongenitalMalformation.setImageResource(R.drawable.ic_check_box_selected);
                } else {
                    complication4 = "";
                    ivMajorCongenitalMalformation.setImageResource(R.drawable.ic_check_box);
                }

                break;

            case R.id.rlAnyOther:

                if (complication5.isEmpty()) {
                    complication5 = getString(R.string.yesValue);
                    ivAnyOther.setImageResource(R.drawable.ic_check_box_selected);
                    etOtherSpecify.setVisibility(View.VISIBLE);
                } else {
                    complication5 = "";
                    ivAnyOther.setImageResource(R.drawable.ic_check_box);
                    etOtherSpecify.setText("");
                    etOtherSpecify.setVisibility(View.GONE);
                }

                break;

            case R.id.rlPresent:

                setDefaultBulging();
                bulging = getString(R.string.presentValue);
                ivPresent.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlAbsent:

                setDefaultBulging();
                bulging = getString(R.string.absentValue);
                ivAbsent.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlPresentOnAdmis:

                setDefaultConvulsions();
                convulsions = getString(R.string.presentOnAdmissionValue);
                ivPresentOnAdmis.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlPastHistory:

                setDefaultConvulsions();
                convulsions = getString(R.string.pastHistoryValue);
                ivPastHistory.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlConvulsionsNo:

                setDefaultConvulsions();
                convulsions = getString(R.string.noValue);
                ivConvulsionsNo.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlBleedingPresent:

                setDefaultBleeding();
                bleeding = getString(R.string.presentValue);
                ivBleedingPresent.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlBleedingAbsent:

                setDefaultBleeding();
                bleeding = getString(R.string.absentValue);
                ivBleedingAbsent.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlRefer:

                if (AppUtils.isNetworkAvailable(mActivity)) {
                    doAssessmentApi("3");
                } else {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                }

                break;

            case R.id.rlAdmit:

                if (AppUtils.isNetworkAvailable(mActivity)) {
                    doAssessmentApi("1");
                } else {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                }

                break;
        }

    }

    private void setDefaultTemp() {

        thermometerAvailable = "";
        ivYes.setImageResource(R.drawable.ic_check_box);
        ivNo.setImageResource(R.drawable.ic_check_box);
        etTemperature.setText("");
    }

    private void setDefaultPulse() {

        pulseAvailable = "";
        ivPulseYes.setImageResource(R.drawable.ic_check_box);
        ivPulseNo.setImageResource(R.drawable.ic_check_box);
        etPulse.setText("");
        etSpo2.setText("");
        llOximeter.setVisibility(View.GONE);
    }

    private void setDefaultCft() {

        crt = "";
        crtValue = "";
        ivCrtKnowYes.setImageResource(R.drawable.ic_check_box);
        ivCrtKnowNo.setImageResource(R.drawable.ic_check_box);
        ivCrtYes.setImageResource(R.drawable.ic_check_box);
        ivCrtNo.setImageResource(R.drawable.ic_check_box);

        rlCrt.setVisibility(View.GONE);
    }

    private void setDefaultCftTime() {

        crtValue = "";
        ivCrtYes.setImageResource(R.drawable.ic_check_box);
        ivCrtNo.setImageResource(R.drawable.ic_check_box);
    }

    private void setDefaultGestAge() {

        gestAge = "";
        ivGestOption1.setImageResource(R.drawable.ic_check_box);
        ivGestOption2.setImageResource(R.drawable.ic_check_box);
        ivGestOption3.setImageResource(R.drawable.ic_check_box);
        ivGestOption4.setImageResource(R.drawable.ic_check_box);
    }

    private void setDefaultAlert() {

        alertness = "";
        ivAlert.setImageResource(R.drawable.ic_check_box);
        ivLethargic.setImageResource(R.drawable.ic_check_box);
        ivComatose.setImageResource(R.drawable.ic_check_box);
    }

    private void setDefaultTone() {

        tone = "";
        ivActive.setImageResource(R.drawable.ic_check_box);
        ivLimp.setImageResource(R.drawable.ic_check_box);
        ivTone.setImageResource(R.drawable.ic_check_box);
    }

    private void setDefaultColor() {

        color = "";
        ivoo_color.setImageResource(R.drawable.ic_check_box);
        ivPale.setImageResource(R.drawable.ic_check_box);
        ivCentral.setImageResource(R.drawable.ic_check_box);
        ivPeripheral.setImageResource(R.drawable.ic_check_box);
        ivJaundice.setImageResource(R.drawable.ic_check_box);
    }

    private void setDefaultApnea() {

        apnea = "";
        ivApeanPresent.setImageResource(R.drawable.ic_check_box);
        ivApeanAbsent.setImageResource(R.drawable.ic_check_box);
        ivApeanUnable.setImageResource(R.drawable.ic_check_box);
    }

    private void setDefaultGrunting() {

        grunting = "";
        ivGruntingPresent.setImageResource(R.drawable.ic_check_box);
        ivGruntingAbsent.setImageResource(R.drawable.ic_check_box);
        ivGruntingUnable.setImageResource(R.drawable.ic_check_box);
    }

    private void setDefaultChest() {

        chest = "";
        ivChestPresent.setImageResource(R.drawable.ic_check_box);
        ivChestAbsent.setImageResource(R.drawable.ic_check_box);
        ivChestUnable.setImageResource(R.drawable.ic_check_box);
    }

    private void setDefaultInterest() {

        interest = "";
        ivInterestYes.setImageResource(R.drawable.ic_check_box);
        ivInterestNo.setImageResource(R.drawable.ic_check_box);
    }

    private void setDefaultLactation() {

        lactation = "";
        ivSufficientYes.setImageResource(R.drawable.ic_check_box);
        ivSufficientNo.setImageResource(R.drawable.ic_check_box);
    }

    private void setDefaultSucking() {

        sucking = "";
        ivGood.setImageResource(R.drawable.ic_check_box);
        ivPoor.setImageResource(R.drawable.ic_check_box);
        ivNoSucking.setImageResource(R.drawable.ic_check_box);
    }

    private void setDefaultUmbilicus() {

        umbilicus = "";
        ivRed.setImageResource(R.drawable.ic_check_box);
        ivDischarge.setImageResource(R.drawable.ic_check_box);
        ivNormal.setImageResource(R.drawable.ic_check_box);
    }

    private void setDefaultSkinRash() {

        skinRash = "";
        ivNoneObserved.setImageResource(R.drawable.ic_check_box);
        ivYesLessThen10.setImageResource(R.drawable.ic_check_box);
        ivYesGreaterThen10.setImageResource(R.drawable.ic_check_box);
        ivAbscess.setImageResource(R.drawable.ic_check_box);
    }

    private void setDefaultBulging() {

        bulging = "";
        ivPresent.setImageResource(R.drawable.ic_check_box);
        ivAbsent.setImageResource(R.drawable.ic_check_box);
    }

    private void setDefaultConvulsions() {

        convulsions = "";
        ivPresentOnAdmis.setImageResource(R.drawable.ic_check_box);
        ivPastHistory.setImageResource(R.drawable.ic_check_box);
        ivConvulsionsNo.setImageResource(R.drawable.ic_check_box);
    }

    private void setDefaultBleeding() {
        bleeding = "";
        ivBleedingPresent.setImageResource(R.drawable.ic_check_box);
        ivBleedingAbsent.setImageResource(R.drawable.ic_check_box);
    }

    private void setViewVisibility(int position) {

        setDefault();

        if (position == 1) {
            rlPrevious.setVisibility(View.GONE);
            viewVitals.setBackgroundResource(R.color.oo_color);
            llVitalTemp.setVisibility(View.VISIBLE);
        } else if (position == 2) {
            viewVitals.setBackgroundResource(R.color.oo_color);
            llVitalResp.setVisibility(View.VISIBLE);
        } else if (position == 3) {
            viewVitals.setBackgroundResource(R.color.oo_color);
            llVitalPulse.setVisibility(View.VISIBLE);
        } else if (position == 4) {
            viewGest.setBackgroundResource(R.color.oo_color);
            llGestAge.setVisibility(View.VISIBLE);
        } else if (position == 5) {
            viewGeneral.setBackgroundResource(R.color.oo_color);
            llGenExamination.setVisibility(View.VISIBLE);
        } else if (position == 6) {
            viewRespiratory.setBackgroundResource(R.color.oo_color);
            llRespAssessment.setVisibility(View.VISIBLE);
        } else if (position == 7) {
            viewFeeding.setBackgroundResource(R.color.oo_color);
            llFeedingAssessment.setVisibility(View.VISIBLE);
        } else if (position == 8) {
            viewMiscellaneous.setBackgroundResource(R.color.oo_color);
            llMiscellaneousStep1.setVisibility(View.VISIBLE);
        } else if (position == 9) {
            rlPrevious.setVisibility(View.VISIBLE);
            viewMiscellaneous.setBackgroundResource(R.color.oo_color);
            llMiscellaneousStep2.setVisibility(View.VISIBLE);
        } else if (position == 10) {
            setValues();
            rlBottom.setVisibility(View.GONE);
            llHeaderAssessment.setVisibility(View.GONE);
            rlPrevious.setVisibility(View.VISIBLE);
            svFindings.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setValues() {

        if (thermometerAvailable.equals(getString(R.string.yesValue))) {
            tvTemperatureResult.setText(getString(R.string.bullet)
                    + "  " + getString(R.string.temperature)
                    + " = " + etTemperature.getText().toString().trim()
                    + spinnerTempUnit.getSelectedItem());

            try {
                float temp = Float.parseFloat(etTemperature.getText().toString().trim());
                if (spinnerTempUnit.getSelectedItem().equals("° F")) {
                    if (temp < 95.9 || temp > 99.5) {
                        tvTemperatureResult.setTextColor(mActivity.getResources().getColor(R.color.red));
                    }
                } else {
                    if (temp < 36.4 || temp > 38) {
                        tvTemperatureResult.setTextColor(mActivity.getResources().getColor(R.color.red));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            tvTemperatureResult.setText(getString(R.string.bullet)
                    + "  " + getString(R.string.temperature)
                    + " = " + getString(R.string.notApplicable));
        }

        tvRespiRateResult.setText(getString(R.string.bullet)
                + "  " + getString(R.string.respiratoryRate)
                + " = " + AppSettings.getString(AppSettings.respiratoryRate)

                + getString(R.string.min));


        try {
            int rr = Integer.parseInt(AppSettings.getString(AppSettings.respiratoryRate));
            if (rr < 30 || rr > 60) {
                tvRespiRateResult.setTextColor(mActivity.getResources().getColor(R.color.red));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (pulseAvailable.equals(getString(R.string.yesValue))) {
            tvPulseResult.setText(getString(R.string.bullet)
                    + "  " + getString(R.string.pulseRate)
                    + " = " + etPulse.getText().toString().trim()
                    + getString(R.string.bpm));

            tvSpO2Result.setText(getString(R.string.bullet) + "  " + getString(R.string.spo2) + " = " + etSpo2.getText().toString().trim() + "%");

            try {
                int spo2 = Integer.parseInt(etSpo2.getText().toString().trim());
                if (spo2 < 95) {
                    tvSpO2Result.setTextColor(mActivity.getResources().getColor(R.color.red));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                int bpr = Integer.parseInt(etPulse.getText().toString().trim());
                if (bpr < 75 || bpr > 200) {
                    tvPulseResult.setTextColor(mActivity.getResources().getColor(R.color.red));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


//            try {
//                float spo= Float.parseFloat(etSpo2.getText().toString().trim());
//                if (spo < 95) {
//                    tvPulseResult.setTextColor(mActivity.getResources().getColor(R.color.red));
//                }
////            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }

        } else {
            tvPulseResult.setText(getString(R.string.bullet)
                    + "  " + getString(R.string.pulseRate)
                    + " = " + getString(R.string.notApplicable));

            tvSpO2Result.setText(getString(R.string.bullet)
                    + "  " + getString(R.string.spo2)
                    + " = " + getString(R.string.notApplicable));
        }

        if (crt.equals(getString(R.string.yesValue))) {
            if (crtValue.equals(getString(R.string.yesValue))) {
                tvCftResult.setText(getString(R.string.bullet) + "  " + getString(R.string.crtResult) + " = " + getString(R.string.yesValue));
            } else {
                tvCftResult.setText(getString(R.string.bullet) + "  " + getString(R.string.crtResult) + " = " + getString(R.string.no));
            }
        } else {
            tvCftResult.setText(getString(R.string.bullet) + "  " + getString(R.string.crtResult) + " = " + getString(R.string.notApplicable));
        }


        if (gestAge.equalsIgnoreCase(getString(R.string.gestOption1Value)))
            tvGestResult.setText(getString(R.string.bullet) + "  " + getString(R.string.gestOption1));

        else if (gestAge.equalsIgnoreCase(getString(R.string.gestOption2Value)))
            tvGestResult.setText(getString(R.string.bullet) + "  " + getString(R.string.gestOption2));

        else if (gestAge.equalsIgnoreCase(getString(R.string.gestOption3Value)))
            tvGestResult.setText(getString(R.string.bullet) + "  " + getString(R.string.gestOption3));

        else if (gestAge.equalsIgnoreCase(getString(R.string.gestOption4Value)))
            tvGestResult.setText(getString(R.string.bullet) + "  " + getString(R.string.gestOption4));

        if (alertness.equals(getString(R.string.alertValue))) {
            tvAlertResult.setText(getString(R.string.bullet) + "  " + getString(R.string.alertness) + " = " + getString(R.string.alert));

        } else if (alertness.equals(getString(R.string.lethargicValue))) {
            tvAlertResult.setText(getString(R.string.bullet) + "  " + getString(R.string.alertness) + " = " + getString(R.string.lethargic));

        } else if (alertness.equals(getString(R.string.comatoseValue))) {
            tvAlertResult.setText(getString(R.string.bullet) + "  " + getString(R.string.alertness) + " = " + getString(R.string.comatose));

            tvAlertResult.setTextColor(mActivity.getResources().getColor(R.color.red));

        }

        if (tone.equals(getString(R.string.activeValue))) {
            tvToneResult.setText(getString(R.string.bullet) + "  " + getString(R.string.tone) + " = " + getString(R.string.active));
        } else if (tone.equals(getString(R.string.limpValue))) {
            tvToneResult.setText(getString(R.string.bullet) + "  " + getString(R.string.tone) + " = " + getString(R.string.limp));
        } else if (tone.equals(getString(R.string.toneValue))) {
            tvToneResult.setText(getString(R.string.bullet) + "  " + getString(R.string.tone) + " = " + getString(R.string.IncreasedtoneResult));
        }

        if (color.equals(getString(R.string.normalValue))) {
            tvColorResult.setText(getString(R.string.bullet) + "  " + getString(R.string.color) + " = " + getString(R.string.pink));
        } else if (color.equals(getString(R.string.paleValue))) {
            tvColorResult.setText(getString(R.string.bullet) + "  " + getString(R.string.color) + " = " + getString(R.string.pale));
        } else if (color.equals(getString(R.string.centralCyanosisValue))) {
            tvColorResult.setText(getString(R.string.bullet) + "  " + getString(R.string.color) + " = " + getString(R.string.centralCyanosis));
        } else if (color.equals(getString(R.string.peripheralCyanosisValue))) {
            tvColorResult.setText(getString(R.string.bullet) + "  " + getString(R.string.color) + " = " + getString(R.string.peripheralCyanosis));
        } else if (color.equals(getString(R.string.jaundiceValue))) {
            tvColorResult.setText(getString(R.string.bullet) + "  " + getString(R.string.color) + " = " + getString(R.string.jaundice));
        }

        if (apnea.equals(getString(R.string.presentValue))) {
            tvApneaResult.setText(getString(R.string.bullet) + "  " + getString(R.string.apnea) + " = " + getString(R.string.yes));
            tvApneaResult.setTextColor(mActivity.getResources().getColor(R.color.red));
        } else if (apnea.equals(getString(R.string.absentValue))) {
            tvApneaResult.setText(getString(R.string.bullet) + "  " + getString(R.string.apnea) + " = " + getString(R.string.no));
        } else if (apnea.equals(getString(R.string.unableToConfirmValue))) {
            tvApneaResult.setText(getString(R.string.bullet) + "  " + getString(R.string.apnea) + " = " + getString(R.string.unableToConfirm));
        }

        if (grunting.equals(getString(R.string.presentValue))) {
            tvGruntingResult.setText(getString(R.string.bullet) + "  " + getString(R.string.grunting) + " = " + getString(R.string.yes));
        } else if (grunting.equals(getString(R.string.absentValue))) {
            tvGruntingResult.setText(getString(R.string.bullet) + "  " + getString(R.string.grunting) + " = " + getString(R.string.no));
        } else if (grunting.equals(getString(R.string.unableToConfirmValue))) {
            tvGruntingResult.setText(getString(R.string.bullet) + "  " + getString(R.string.grunting) + " = " + getString(R.string.unableToConfirm));
        }

        if (chest.equals(getString(R.string.presentValue))) {
            tvChestResult.setText(getString(R.string.bullet) + "  " + getString(R.string.chestIndrawing) + " = " + getString(R.string.yes));
        } else if (chest.equals(getString(R.string.absentValue))) {
            tvChestResult.setText(getString(R.string.bullet) + "  " + getString(R.string.chestIndrawing) + " = " + getString(R.string.no));
        } else if (chest.equals(getString(R.string.unableToConfirmValue))) {
            tvChestResult.setText(getString(R.string.bullet) + "  " + getString(R.string.chestIndrawing) + " = " + getString(R.string.unableToConfirm));
        }

        if (interest.equals(getString(R.string.yesValue))) {
            tvInterestResult.setText(getString(R.string.bullet) + "  " + getString(R.string.interestInFeedingMandResult) + " = " + getString(R.string.yes));
        } else if (interest.equals(getString(R.string.noValue))) {
            tvInterestResult.setText(getString(R.string.bullet) + "  " + getString(R.string.interestInFeedingMandResult) + " = " + getString(R.string.no));
        }

        if (lactation.equals(getString(R.string.yesValue))) {
            tvLactationResult.setText(getString(R.string.bullet) + "  " + getString(R.string.sufficientLactation) + " = " + getString(R.string.yes));
        } else if (lactation.equals(getString(R.string.noValue))) {
            tvLactationResult.setText(getString(R.string.bullet) + "  " + getString(R.string.sufficientLactation) + " = " + getString(R.string.no));
        }

        if (sucking.equals(getString(R.string.goodValue))) {
            tvSuckingResult.setText(getString(R.string.bullet) + "  " + getString(R.string.suckingMandResult) + " = " + getString(R.string.good));
        } else if (sucking.equals(getString(R.string.poorValue))) {
            tvSuckingResult.setText(getString(R.string.bullet) + "  " + getString(R.string.suckingMandResult) + " = " + getString(R.string.poor));
        } else if (sucking.equals(getString(R.string.unableToConfirmValue))) {
            tvSuckingResult.setText(getString(R.string.bullet) + "  " + getString(R.string.suckingMandResult) + " = " + getString(R.string.unableToConfirm));
        }

        if (umbilicus.equals(getString(R.string.redValue))) {
            tvUmbilicusResult.setText(getString(R.string.bullet) + "  " + getString(R.string.umbilicusResult) + " = " + getString(R.string.red));
        } else if (umbilicus.equals(getString(R.string.dischargeValue))) {
            tvUmbilicusResult.setText(getString(R.string.bullet) + "  " + getString(R.string.umbilicusResult) + " = " + getString(R.string.discharge));
        } else if (umbilicus.equals(getString(R.string.normalValue))) {
            tvUmbilicusResult.setText(getString(R.string.bullet) + "  " + getString(R.string.umbilicusResult) + " = " + getString(R.string.normal));
        }

        if (skinRash.equals(getString(R.string.noneObservedValue))) {
            tvSkinResult.setText(getString(R.string.bullet) + "  " + getString(R.string.skinNew) + " = " + getString(R.string.noneObserved));
        } else if (skinRash.equals(getString(R.string.yes_10Value))) {
            tvSkinResult.setText(getString(R.string.bullet) + "  " + getString(R.string.skinNew) + " = " + getString(R.string.yes_10));
        } else if (skinRash.equals(getString(R.string.yesGreater10Value))) {
            tvSkinResult.setText(getString(R.string.bullet) + "  " + getString(R.string.skinNew) + " = " + getString(R.string.yesGreater10));
        } else if (skinRash.equals(getString(R.string.abscessValue))) {
            tvSkinResult.setText(getString(R.string.bullet) + "  " + getString(R.string.skinNew) + " = " + getString(R.string.abscess));
        }

        if (bulging.equals(getString(R.string.presentValue))) {
            tvBulgingResult.setText(getString(R.string.bullet) + "  " + getString(R.string.bulgingAnteriorFontanelleMandResult) + " = " + getString(R.string.presentYes));
        } else if (bulging.equals(getString(R.string.absentValue))) {
            tvBulgingResult.setText(getString(R.string.bullet) + "  " + getString(R.string.bulgingAnteriorFontanelleMandResult) + " = " + getString(R.string.presentNo));
        }

        if (convulsions.equals(getString(R.string.presentOnAdmissionValue))) {
            tvConvulsionsResult.setText(getString(R.string.bullet) + "  " + getString(R.string.convulsionsMandResult) + " = " + getString(R.string.presentOnAdmission));
        } else if (convulsions.equals(getString(R.string.pastHistoryValue))) {
            tvConvulsionsResult.setText(getString(R.string.bullet) + "  " + getString(R.string.convulsionsMandResult) + " = " + getString(R.string.pastHistory));
        } else if (convulsions.equals(getString(R.string.noValue))) {
            tvConvulsionsResult.setText(getString(R.string.bullet) + "  " + getString(R.string.convulsionsMandResult) + " = " + getString(R.string.no));
        }

        if (bleeding.equals(getString(R.string.presentValue))) {
            tvBleedingResult.setText(getString(R.string.bullet) + "  " + getString(R.string.bleedingFromAnySiteMandResult) + " = " + getString(R.string.presentYes));
        } else if (bleeding.equals(getString(R.string.absentValue))) {
            tvBleedingResult.setText(getString(R.string.bullet) + "  " + getString(R.string.bleedingFromAnySiteMandResult) + " = " + getString(R.string.presentNo));
        }

        tvUrineResult.setText(getString(R.string.bullet) + "  " + getString(R.string.urineIn) + " = " + etUrineTimes.getText().toString().trim());

        tvStoolResult.setText(getString(R.string.bullet) + "  " + getString(R.string.stoolIn) + " = " + etStoolTimes.getText().toString().trim());
        arrayList.clear();

        if (!complication1.isEmpty()) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("name", getString(R.string.perinatalAsphyxia));
            arrayList.add(hashMap);
        }

        if (!complication2.isEmpty()) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("name", getString(R.string.babyOfDiabeticMother));
            arrayList.add(hashMap);
        }

        if (!complication3.isEmpty()) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("name", getString(R.string.meconiumAspiration));
            arrayList.add(hashMap);
        }

        if (!complication4.isEmpty()) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("name", getString(R.string.majorCongenitalMalformation));
            arrayList.add(hashMap);
        }

        if (!complication5.isEmpty()) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("name", etOtherSpecify.getText().toString().trim());
            arrayList.add(hashMap);
        }

        if (arrayList.size() == 0) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("name", "");
            arrayList.add(hashMap);
        }

        adapter = new Adapter(arrayList);
        rvComplications.setAdapter(adapter);
    }

    private void validation() {

        ContentValues mContentValues = new ContentValues();

        mContentValues.put(TableUser.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        mContentValues.put(TableUser.tableColumn.uuid.toString(), String.valueOf(uuid));
        mContentValues.put(TableUser.tableColumn.step4.toString(), createJsonForBabyAssessment("4").toString());
        mContentValues.put(TableUser.tableColumn.bAStatus.toString(), "1");

        DatabaseController.insertUpdateData(mContentValues, TableUser.tableName,
                TableUser.tableColumn.uuid.toString(), String.valueOf(uuid));

        int spo2 = 0;
        try {
            spo2 = Integer.parseInt(etSpo2.getText().toString().trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        int pulse = 0;
        try {
            pulse = Integer.parseInt(etPulse.getText().toString().trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        double temp = 0;
        try {
            temp = Float.parseFloat(etTemperature.getText().toString().trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        double minTempF = 90;
        double maxTempF = 107;
        double minTempC = 32.2;
        double maxTempC = 41.6;

        if (currentPage == 1 && thermometerAvailable.equalsIgnoreCase(mActivity.getString(R.string.noValue)) && spinnerReason.getSelectedItem().equals(mActivity.getString(R.string.reason))) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorSelectReason));
            return;
        }

        if (pulseAvailable.equalsIgnoreCase(mActivity.getString(R.string.noValue)) && currentPage == 3 && spinnerPulseReason.getSelectedItem().equals(mActivity.getString(R.string.reason))) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorSelectReason));
            return;
        }

//        if(crt.equalsIgnoreCase(mActivity.getString(R.string.noValue)) && currentPage==3 )
//        {
//            AppUtils.showToastSort(mActivity, getString(R.string.errorEnterReason));
//            return;
//        }

        if (currentPage == 1 && thermometerAvailable.equalsIgnoreCase(mActivity.getString(R.string.noValue)) && spinnerReason.getSelectedItem().equals(mActivity.getString(R.string.other)) && etReason.getText().toString().equalsIgnoreCase("")) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorEnterReason));
            return;
        }

        if (pulseAvailable.equalsIgnoreCase(mActivity.getString(R.string.noValue)) && currentPage == 3 && spinnerPulseReason.getSelectedItem().equals(mActivity.getString(R.string.other)) && etPulseReason.getText().toString().equalsIgnoreCase("")) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorEnterReason));
            return;
        }


        if (thermometerAvailable.isEmpty()
                && currentPage == 1) {
            AppUtils.showToastSort(mActivity, getString(R.string.ErrorisThermometerAvailable));
        } else if (thermometerAvailable.equals(getString(R.string.yesValue))
                && currentPage == 1
                && etTemperature.getText().toString().trim().isEmpty()) {
            etTemperature.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorTemp));
        } else if (currentPage == 1 && thermometerAvailable.equalsIgnoreCase(getString(R.string.yesValue)) && tempUnit.equalsIgnoreCase("° F") && (temp < minTempF || temp > maxTempF)) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorTempF));
        } else if (currentPage == 1 && thermometerAvailable.equalsIgnoreCase(getString(R.string.yesValue)) && tempUnit.equalsIgnoreCase("° C") && (temp < minTempC || temp > maxTempC)) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorTempC));
        } else if (tvRespiRateCount.getText().toString().isEmpty()
                && currentPage == 2) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorRespiratory));
        } else if (timer == 1
                && currentPage == 2) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorRespiratory));
        } else if (pulseAvailable.isEmpty() && currentPage == 3) {
            AppUtils.showToastSort(mActivity, getString(R.string.havePulseOximetryerror));

        }

//        if(etSpo2.getText().toString().trim().isEmpty()){
//            etSpo2.requestFocus();
//            AppUtils.showToastSort(mActivity, getString(R.string.errorSpo2Empty));
//        }
//
//        if(etPulse.getText().toString().trim().isEmpty()){
//            etPulse.requestFocus();
//            AppUtils.showToastSort(mActivity, getString(R.string.errorPulseEmpty));
//        }

        else if (etSpo2.getText().toString().trim().isEmpty()
                && currentPage == 3
                && pulseAvailable.equalsIgnoreCase(getString(R.string.yesValue))) {
            etSpo2.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorSpo2Empty));
        }

        else if ((spo2 < 20 || spo2 > 100)   && currentPage == 3 && etSpo2.getVisibility()==View.VISIBLE && pulseAvailable.equalsIgnoreCase(getString(R.string.yesValue))) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorSpo2));
        }


        else if (etPulse.getText().toString().trim().isEmpty()  && currentPage == 3 && etPulse.getVisibility()==View.VISIBLE && pulseAvailable.equalsIgnoreCase(getString(R.string.yesValue))) {
            etPulse.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorPulseEmpty));
        }

        else if ((pulse > 300 || pulse < 10)
                && currentPage == 3 && etPulse.getVisibility()==View.VISIBLE
                && checkPulse == 0 && pulseAvailable.equalsIgnoreCase(getString(R.string.yesValue))) {
            etPulse.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorPulse));

        }

//        else if((pulse>300||pulse<10)
//                &&currentPage==3
//                &&checkPulse==0&&pulseAvailable.equalsIgnoreCase(getString(R.string.yesValue)))
//        {
//            checkPulse=1;
//            String str = getString(R.string.areYouSurePart1)+" "+pulse+" "+getString(R.string.bpm)+getString(R.string.areYouSurePart2);
//            AppUtils.AlertOk(str,mActivity,2,"");
//        }

        else if (crt.isEmpty() && currentPage == 3) {
            AppUtils.showToastSort(mActivity, getString(R.string.knowledgeCapiliaryFillingerror));
        } else if (crt.equalsIgnoreCase(getString(R.string.yesValue)) && crtValue.isEmpty() && currentPage == 3) {
            AppUtils.showToastSort(mActivity, getString(R.string.isCrt3));
        } else if (gestAge.isEmpty() && currentPage == 4) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorGestWeek));
        } else if (alertness.isEmpty() && currentPage == 5) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorAlertness));
        } else if (tone.isEmpty() && currentPage == 5) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorMuscleTone));
        } else if (color.isEmpty() && currentPage == 5 && color.equals("")) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorColor));
        } else if (apnea.isEmpty() && currentPage == 6) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorApnea));
        } else if (grunting.isEmpty() && currentPage == 6) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorGrunting));
        } else if (chest.isEmpty() && currentPage == 6) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorChest));
        } else if (interest.isEmpty() && currentPage == 7) {
            AppUtils.showToastSort(mActivity, getString(R.string.interestInFeeding));
        } else if (lactation.isEmpty() && currentPage == 7) {
            AppUtils.showToastSort(mActivity, getString(R.string.isMotherSufficientLactation));
        } else if (sucking.isEmpty() && currentPage == 7) {
            AppUtils.showToastSort(mActivity, getString(R.string.sucking));
        } else if (umbilicus.isEmpty() && currentPage == 8) {
            AppUtils.showToastSort(mActivity, getString(R.string.umbilicus));
        } else if (skinRash.isEmpty() && currentPage == 8) {
            AppUtils.showToastSort(mActivity, getString(R.string.skinRashOnPustules));
        } else if (complication5.equals(getString(R.string.yesValue))
                && currentPage == 8
                && etOtherSpecify.getText().toString().trim().isEmpty()) {
            etOtherSpecify.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.anyOtherPleaseSpecify));
        } else if (bulging.isEmpty() && currentPage == 8) {
            AppUtils.showToastSort(mActivity, getString(R.string.bulgingAnteriorFontanelle));
        } else if (convulsions.isEmpty() && currentPage == 9) {
            AppUtils.showToastSort(mActivity, getString(R.string.convulsions));
        } else if (bleeding.isEmpty() && currentPage == 9) {
            AppUtils.showToastSort(mActivity, getString(R.string.bleedingFromAnySite));
        } else if (etUrineTimes.getText().toString().trim().isEmpty() && currentPage == 9) {
            etUrineTimes.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.noOfTimesUrine));
        } else if (!etUrineTimes.getText().toString().trim().isEmpty() && Integer.valueOf(etUrineTimes.getText().toString()) > 15 && currentPage == 9) {
            etUrineTimes.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.urineValidation));
        }

        else if (etStoolTimes.getText().toString().trim().isEmpty() && currentPage == 9) {
            etStoolTimes.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.noOfTimesStool));
        } else if (!etStoolTimes.getText().toString().trim().isEmpty() && Integer.valueOf(etStoolTimes.getText().toString()) > 15 && currentPage == 9) {
            etStoolTimes.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.stoolValidation));
        } else if (currentPage < 10) {
            currentPage = currentPage + 1;
            setViewVisibility(currentPage);
        }

    }

    private JSONObject createJsonForBabyAssessment(String status) {
        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        JSONObject jsonNew = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONArray jsonMainArray = new JSONArray();
        try {
            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("babyId", AppSettings.getString(AppSettings.babyId));
            jsonData.put("staffId", AppSettings.getString(AppSettings.nurseId));
            jsonData.put("staffSign", "");
            jsonData.put("localId", uuid);

            jsonData.put("respiratoryRate", AppSettings.getString(AppSettings.respiratoryRate));
            jsonData.put("isThermometerAvailable", thermometerAvailable);

            jsonData.put("temperatureValue", "");
            jsonData.put("temperatureUnit", "");
            jsonData.put("reasonValue", "");
            jsonData.put("otherValue", "");

            if (thermometerAvailable.equalsIgnoreCase(getString(R.string.yesValue))) {
                jsonData.put("temperatureValue", etTemperature.getText().toString().trim());
                jsonData.put("temperatureUnit", tempUnit);
            } else {
                jsonData.put("reasonValue", spinnerReason.getSelectedItem());
                jsonData.put("otherValue", etReason.getText().toString());
            }

            jsonData.put("isPulseOximatoryDeviceAvail", pulseAvailable);
            jsonData.put("spo2", "");
            jsonData.put("pulseRate", "");
            jsonData.put("pulseReasonValue", "");
            jsonData.put("pulseOtherValue", "");

            if (pulseAvailable.equalsIgnoreCase(getString(R.string.yesValue))) {
                jsonData.put("spo2", etSpo2.getText().toString().trim());
                jsonData.put("pulseRate", etPulse.getText().toString().trim());
            } else {
                jsonData.put("pulseReasonValue", spinnerPulseReason.getSelectedItem());
                jsonData.put("pulseOtherValue", etPulseReason.getText().toString());
            }

            jsonData.put("crtKnowledge", crt);
            jsonData.put("isCrtGreaterThree", crtValue);

            jsonData.put("crtReason", "");
            jsonData.put("crtOtherReason", "");

            if (crt.equalsIgnoreCase(getString(R.string.yesValue))) {
                jsonData.put("crtReason", "");
                jsonData.put("crtOtherReason", "");
            } else {
                jsonData.put("crtReason", "");
                jsonData.put("crtOtherReason", "");
            }

            jsonData.put("gestationalAge", gestAge);
            jsonData.put("tone", tone);
            jsonData.put("alertness", alertness);
            jsonData.put("color", color);
            jsonData.put("apneaGasping", apnea);
            jsonData.put("grunting", grunting);
            jsonData.put("chestIndrawing", chest);
            jsonData.put("interestInFeeding", interest);
            jsonData.put("sufficientLactation", lactation);
            jsonData.put("sucking", sucking);
            jsonData.put("umbilicus", umbilicus);
            jsonData.put("skinPustules", skinRash);
            jsonData.put("isAnyComplicationAtBirth", "");
            jsonData.put("otherComplications", etOtherSpecify.getText().toString().trim());
            jsonData.put("bulgingAnteriorFontanel", bulging);
            jsonData.put("localDateTime", AppUtils.currentTimestampFormat());
            jsonData.put("latitude", AppSettings.getString(AppSettings.latitude));
            jsonData.put("longitude", AppSettings.getString(AppSettings.longitude));
            jsonData.put("convulsions", convulsions);
            jsonData.put("isBleeding", bleeding);
            jsonData.put("urinePassedIn24Hrs", etUrineTimes.getText().toString().trim());
            jsonData.put("stoolPassedIn24Hrs", etStoolTimes.getText().toString().trim());
            jsonData.put("status", status);

            JSONObject jsonNewData = new JSONObject();

            if (!complication1.isEmpty()) {
                jsonNewData.put("name", complication1);
                jsonArray.put(jsonNewData);
            }

            jsonNewData = new JSONObject();

            if (!complication2.isEmpty()) {
                jsonNewData.put("name", complication2);
                jsonArray.put(jsonNewData);
            }

            jsonNewData = new JSONObject();

            if (!complication3.isEmpty()) {
                jsonNewData.put("name", complication3);
                jsonArray.put(jsonNewData);
            }

            jsonNewData = new JSONObject();

            if (!complication4.isEmpty()) {
                jsonNewData.put("name", complication4);
                jsonArray.put(jsonNewData);
            }

            jsonNewData = new JSONObject();

            if (!complication5.isEmpty()) {
                jsonNewData.put("name", etOtherSpecify.getText().toString().trim());
                jsonArray.put(jsonNewData);
            }

            jsonData.put("isAnyComplicationAtBirth", jsonArray.toString());

            jsonMainArray.put(jsonData);

            jsonNew.put("monitoringData", jsonMainArray);
            jsonNew.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            json.put(AppConstants.projectName, jsonNew);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonNew.toString()));


            Log.v("doBAdmissionApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;

    }

    private void doAssessmentApi(String status) {

        WebServices.postApi(mActivity, AppUrls.admissionTimeMonitoring, createJsonForBabyAssessment(status), true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);
                    if (jsonObject.getString("resCode").equals("1")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("id");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                            saveBabyMonitoringData("1", jsonObject2.getString("id"));
                        }

                        if (status.equals("1")) {
                            ((RegistrationActivity) getActivity()).displayView(2);
                        } else {
                            ((RegistrationActivity) getActivity()).displayView(3);
                        }

//                        DatabaseController.myDataBase.delete(TableUser.tableName, null, null);


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

    private void saveBabyMonitoringData(String status, String serverId) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(TableBabyMonitoring.tableColumn.uuid.toString(), uuid);
        contentValues.put(TableBabyMonitoring.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        contentValues.put(TableBabyMonitoring.tableColumn.serverId.toString(), serverId);
        contentValues.put(TableBabyMonitoring.tableColumn.babyId.toString(), AppSettings.getString(AppSettings.babyId));
        contentValues.put(TableBabyMonitoring.tableColumn.babyAdmissionId.toString(), AppSettings.getString(AppSettings.bAdmId));
        contentValues.put(TableBabyMonitoring.tableColumn.babyRespiratoryRate.toString(), AppSettings.getString(AppSettings.respiratoryRate));
        contentValues.put(TableBabyMonitoring.tableColumn.isPulseOximatoryDeviceAvailable.toString(), pulseAvailable);
        contentValues.put(TableBabyMonitoring.tableColumn.type.toString(), "1");

        contentValues.put(TableBabyMonitoring.tableColumn.crtKnowledge.toString(), crt);
        contentValues.put(TableBabyMonitoring.tableColumn.isDataComplete.toString(), "1");
        contentValues.put(TableBabyMonitoring.tableColumn.isThermometerAvailable.toString(), thermometerAvailable);

        if (thermometerAvailable.equalsIgnoreCase(getString(R.string.yesValue))) {
            contentValues.put(TableBabyMonitoring.tableColumn.babyTemperature.toString(), etTemperature.getText().toString().trim());
            contentValues.put(TableBabyMonitoring.tableColumn.temperatureUnit.toString(), tempUnit);
        } else {
            contentValues.put(TableBabyMonitoring.tableColumn.babyTemperature.toString(), "");
            contentValues.put(TableBabyMonitoring.tableColumn.temperatureUnit.toString(), "");
        }

        if (pulseAvailable.equalsIgnoreCase(getString(R.string.yesValue))) {
            contentValues.put(TableBabyMonitoring.tableColumn.babySpO2.toString(), etSpo2.getText().toString().trim());
            contentValues.put(TableBabyMonitoring.tableColumn.babyPulseRate.toString(), etPulse.getText().toString().trim());
        } else {
            contentValues.put(TableBabyMonitoring.tableColumn.babySpO2.toString(), "");
            contentValues.put(TableBabyMonitoring.tableColumn.babyPulseRate.toString(), "");
        }

        if (crt.equalsIgnoreCase(getString(R.string.yesValue))) {
            contentValues.put(TableBabyMonitoring.tableColumn.isCftGreaterThree.toString(), crtValue);
        } else {
            contentValues.put(TableBabyMonitoring.tableColumn.isCftGreaterThree.toString(), getString(R.string.noValue));
        }

        contentValues.put(TableBabyMonitoring.tableColumn.urinePassedIn24Hrs.toString(), etUrineTimes.getText().toString().trim());
        contentValues.put(TableBabyMonitoring.tableColumn.stoolPassedIn24Hrs.toString(), etStoolTimes.getText().toString().trim());
        contentValues.put(TableBabyMonitoring.tableColumn.generalCondition.toString(), alertness);
        contentValues.put(TableBabyMonitoring.tableColumn.tone.toString(), tone);
        contentValues.put(TableBabyMonitoring.tableColumn.apneaOrGasping.toString(), apnea);
        contentValues.put(TableBabyMonitoring.tableColumn.grunting.toString(), grunting);
        contentValues.put(TableBabyMonitoring.tableColumn.chestIndrawing.toString(), chest);
        contentValues.put(TableBabyMonitoring.tableColumn.color.toString(), color);
        contentValues.put(TableBabyMonitoring.tableColumn.sucking.toString(), sucking);
        contentValues.put(TableBabyMonitoring.tableColumn.isBleeding.toString(), bleeding);
        contentValues.put(TableBabyMonitoring.tableColumn.isInterestInFeeding.toString(), interest);
        contentValues.put(TableBabyMonitoring.tableColumn.lactation.toString(), lactation);
        contentValues.put(TableBabyMonitoring.tableColumn.bulgingAnteriorFontanel.toString(), bulging);
        contentValues.put(TableBabyMonitoring.tableColumn.umbilicus.toString(), umbilicus);
        contentValues.put(TableBabyMonitoring.tableColumn.skinPustules.toString(), skinRash);
        contentValues.put(TableBabyMonitoring.tableColumn.staffId.toString(), AppSettings.getString(AppSettings.nurseId));
        contentValues.put(TableBabyMonitoring.tableColumn.assessmentNumber.toString(), "1");
        contentValues.put(TableBabyMonitoring.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableBabyMonitoring.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableBabyMonitoring.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableBabyMonitoring.tableColumn.formattedDate.toString(), AppUtils.getDateInFormat());
        contentValues.put(TableBabyMonitoring.tableColumn.status.toString(), "1");
        contentValues.put(TableBabyMonitoring.tableColumn.staffSign.toString(), "");
        contentValues.put(TableBabyMonitoring.tableColumn.isDataSynced.toString(), "1");
        contentValues.put(TableBabyMonitoring.tableColumn.json.toString(), createJsonForBabyAssessment(status).toString());

        DatabaseController.insertUpdateData(contentValues, TableBabyMonitoring.tableName,
                TableBabyMonitoring.tableColumn.uuid.toString(), String.valueOf(uuid));


        ContentValues cvBabyAdm = new ContentValues();

        cvBabyAdm.put(TableBabyAdmission.tableColumn.uuid.toString(), RegistrationActivity.uuid);
        cvBabyAdm.put(TableBabyAdmission.tableColumn.isAnyComplicationAtBirth.toString(), "");
        cvBabyAdm.put(TableBabyAdmission.tableColumn.convulsions.toString(), convulsions);
        cvBabyAdm.put(TableBabyAdmission.tableColumn.gestAge.toString(), gestAge);

        try {
            JSONArray jsonArray = new JSONArray();

            JSONObject jsonNewData = new JSONObject();

            if (!complication1.isEmpty()) {
                jsonNewData.put("name", complication1);
                jsonArray.put(jsonNewData);
            }

            jsonNewData = new JSONObject();

            if (!complication2.isEmpty()) {
                jsonNewData.put("name", complication2);
                jsonArray.put(jsonNewData);
            }

            jsonNewData = new JSONObject();

            if (!complication3.isEmpty()) {
                jsonNewData.put("name", complication3);
                jsonArray.put(jsonNewData);
            }

            jsonNewData = new JSONObject();

            if (!complication4.isEmpty()) {
                jsonNewData.put("name", complication4);
                jsonArray.put(jsonNewData);
            }

            jsonNewData = new JSONObject();

            if (!complication5.isEmpty()) {
                jsonNewData.put("name", etOtherSpecify.getText().toString().trim());
                jsonArray.put(jsonNewData);
            }

            cvBabyAdm.put(TableBabyAdmission.tableColumn.isAnyComplicationAtBirth.toString(), jsonArray.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        DatabaseController.insertUpdateData(cvBabyAdm, TableBabyAdmission.tableName,
                TableBabyAdmission.tableColumn.uuid.toString(), String.valueOf(RegistrationActivity.uuid));

        SharedPreferences pref = getContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        JSONObject jsonObject = new JSONObject();
        jsonObject = createJsonForBabyAssessment("1");
        editor.putString("AllDataRecord", jsonObject.toString());
        editor.commit();
    }

    public void setStepValues() {

        reasonValue = new ArrayList<String>();
        reasonValue.add(mActivity.getString(R.string.reason));
        reasonValue.add(mActivity.getString(R.string.equipmentNot));
        reasonValue.add(mActivity.getString(R.string.dontKnowMeasurement));
        reasonValue.add(mActivity.getString(R.string.motherNotAllowed));
        reasonValue.add(mActivity.getString(R.string.other));

        spinnerReason.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, reasonValue));
        spinnerReason.setSelection(0);

        spinnerReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedReason = reasonValue.get(position);
                if (selectedReason.equalsIgnoreCase(mActivity.getString(R.string.other))) {
                    rlNote.setVisibility(View.VISIBLE);
                } else {
                    rlNote.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerPulseReason.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, reasonValue));
        spinnerPulseReason.setSelection(0);

        spinnerPulseReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String ss = reasonValue.get(position);

                if (ss.equalsIgnoreCase(mActivity.getString(R.string.other))) {
                    rlPulseNote.setVisibility(View.VISIBLE);
                } else {
                    rlPulseNote.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
        arrayList.clear();
        arrayList.addAll(DatabaseController.getStepData(RegistrationActivity.uuid));
        for (int i = 0; i < arrayList.size(); i++) {

            JSONObject jsonObjectNew = null;

            try {
                jsonObjectNew = new JSONObject(arrayList.get(i).get("step4"));

                JSONObject jsonObject = jsonObjectNew.getJSONObject(AppConstants.projectName);
                JSONArray jsonArray = jsonObject.getJSONArray("monitoringData");

                for (int j = 0; j < jsonArray.length(); j++) {

                    JSONObject jsonObjectArray = jsonArray.getJSONObject(j);

                    Log.d("jsonObjectArray", jsonObjectArray.toString());

                    thermometerAvailable = jsonObjectArray.getString("isThermometerAvailable");

                    tempText.clear();
                    tempText.add(getString(R.string.degreeF));
                    tempText.add(getString(R.string.degreeC));

                    tempValue.clear();
                    tempValue.add(getString(R.string.degreeFValue));
                    tempValue.add(getString(R.string.degreeCValue));

                    spinnerTempUnit.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, tempText));

                    try {
                        if (thermometerAvailable.equals(getString(R.string.yesValue))) {
                            ivYes.setImageResource(R.drawable.ic_check_box_selected);
                            ivNo.setImageResource(R.drawable.ic_check_box);
                            etTemperature.setEnabled(true);
                            spinnerTempUnit.setEnabled(true);
                            Log.v("temperatureUnitssgs",jsonObjectArray.getString("temperatureUnit"));
                            etTemperature.setText(jsonObjectArray.getString("temperatureValue"));

                            if (jsonObjectArray.getString("temperatureUnit").equals(getString(R.string.degreeF))) {

                                spinnerTempUnit.setSelection(0);
                            } else {
                                spinnerTempUnit.setSelection(1);
                            }
                        } else {
                            ivYes.setImageResource(R.drawable.ic_check_box);
                            ivNo.setImageResource(R.drawable.ic_check_box_selected);
                            etTemperature.setEnabled(false);
                            spinnerTempUnit.setEnabled(false);
                            etTemperature.setText("");
                            spinnerTempUnit.setSelection(0);
                            rlReason.setVisibility(View.VISIBLE);
                            rlTempe.setVisibility(View.GONE);
                            selectedReasonValue = jsonObjectArray.getString("reasonValue");
                            selectedReasonSpinnerPosition = new adapterSpinner(mActivity, R.layout.inflate_spinner_new, reasonValue).getPosition(selectedReasonValue);
                            spinnerReason.setSelection(selectedReasonSpinnerPosition);

                        }
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }

                    AppSettings.putString(AppSettings.respiratoryRate, jsonObjectArray.getString("respiratoryRate"));

                    if (!AppSettings.getString(AppSettings.respiratoryRate).isEmpty()) {
                        chronometer.stop();
                        tvStart.setText(getString(R.string.start));
                        tvRespiRateCount.setVisibility(View.VISIBLE);
                        rlPress.setClickable(true);
                        press = 1;
                        timer = 0;
                        tvRespiRateCount.setText(getString(R.string.respiratoryRate) + ": " + AppSettings.getString(AppSettings.respiratoryRate) + " / min");
                        tvCounter.setText("0");
                        chronometer.setText("00:00");
                    }

                    pulseAvailable = jsonObjectArray.getString("isPulseOximatoryDeviceAvail");

                    try {
                        if (pulseAvailable.equals(getString(R.string.yesValue))) {
                            ivPulseYes.setImageResource(R.drawable.ic_check_box_selected);
                            ivPulseNo.setImageResource(R.drawable.ic_check_box);
                            etPulse.setText(jsonObjectArray.getString("pulseRate"));
                            etSpo2.setText(jsonObjectArray.getString("spo2"));
                            llOximeter.setVisibility(View.VISIBLE);
                            llPulseReason.setVisibility(View.GONE);
                        } else if (pulseAvailable.equals(getString(R.string.noValue))) {
                            ivPulseNo.setImageResource(R.drawable.ic_check_box_selected);
                            ivPulseYes.setImageResource(R.drawable.ic_check_box);
                            etPulse.setText("");
                            etSpo2.setText("");
                            llOximeter.setVisibility(View.GONE);
                            llPulseReason.setVisibility(View.VISIBLE);

                            String selectedPulseValue = jsonObjectArray.getString("pulseReasonValue");
                            int selectedPulseSpinnerPosition = new adapterSpinner(mActivity, R.layout.inflate_spinner_new, reasonValue).getPosition(selectedPulseValue);
                            spinnerPulseReason.setSelection(selectedPulseSpinnerPosition);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    setDefaultCft();
                    setDefaultCftTime();

                    crt = jsonObjectArray.getString("crtKnowledge");
                    crtValue = jsonObjectArray.getString("isCrtGreaterThree");

                    if (crt.equals(getString(R.string.yesValue))) {
                        crt = getString(R.string.yesValue);
                        ivCrtKnowYes.setImageResource(R.drawable.ic_check_box_selected);
                        rlCrt.setVisibility(View.VISIBLE);

                        if (crtValue.equals(getString(R.string.yesValue))) {
                            ivCrtYes.setImageResource(R.drawable.ic_check_box_selected);
                            ivCrtNo.setImageResource(R.drawable.ic_check_box);
                        } else {
                            ivCrtNo.setImageResource(R.drawable.ic_check_box_selected);
                            ivCrtYes.setImageResource(R.drawable.ic_check_box);
                        }
                    } else {
                        setDefaultCft();
                        crt = getString(R.string.noValue);
                        ivCrtKnowNo.setImageResource(R.drawable.ic_check_box_selected);
                    }

                    setDefaultGestAge();
                    gestAge = jsonObjectArray.getString("gestationalAge");

                    if (gestAge.equalsIgnoreCase(getString(R.string.gestOption1Value)))
                        ivGestOption1.setImageResource(R.drawable.ic_check_box_selected);

                    if (gestAge.equalsIgnoreCase(getString(R.string.gestOption2Value)))
                        ivGestOption2.setImageResource(R.drawable.ic_check_box_selected);

                    if (gestAge.equalsIgnoreCase(getString(R.string.gestOption3Value)))
                        ivGestOption3.setImageResource(R.drawable.ic_check_box_selected);

                    if (gestAge.equalsIgnoreCase(getString(R.string.gestOption4Value)))
                        ivGestOption4.setImageResource(R.drawable.ic_check_box_selected);

                    setDefaultAlert();
                    alertness = jsonObjectArray.getString("alertness");

                    if (alertness.equals(getString(R.string.alertValue))) {
                        ivAlert.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (alertness.equals(getString(R.string.lethargicValue))) {
                        ivLethargic.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (alertness.equals(getString(R.string.comatoseValue))) {
                        ivComatose.setImageResource(R.drawable.ic_check_box_selected);
                    }

                    setDefaultTone();
                    tone = jsonObjectArray.getString("tone");

                    if (tone.equals(getString(R.string.activeValue))) {
                        ivActive.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (tone.equals(getString(R.string.limpValue))) {
                        ivLimp.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (tone.equals(getString(R.string.toneValue))) {
                        ivTone.setImageResource(R.drawable.ic_check_box_selected);
                    }

                    setDefaultColor();
                    color = jsonObjectArray.getString("color");

                    if (color.equals(getString(R.string.normalValue))) {
                        ivoo_color.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (color.equals(getString(R.string.paleValue))) {
                        ivPale.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (color.equals(getString(R.string.centralCyanosisValue))) {
                        ivCentral.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (color.equals(getString(R.string.peripheralCyanosisValue))) {
                        ivPeripheral.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (color.equals(getString(R.string.jaundiceValue))) {
                        ivJaundice.setImageResource(R.drawable.ic_check_box_selected);
                    }

                    setDefaultApnea();
                    apnea = jsonObjectArray.getString("apneaGasping");

                    if (apnea.equals(getString(R.string.presentValue))) {
                        ivApeanPresent.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (apnea.equals(getString(R.string.absentValue))) {
                        ivApeanAbsent.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (apnea.equals(getString(R.string.unableToConfirmValue))) {
                        ivApeanUnable.setImageResource(R.drawable.ic_check_box_selected);
                    }

                    setDefaultGrunting();
                    grunting = jsonObjectArray.getString("grunting");

                    if (grunting.equals(getString(R.string.presentValue))) {
                        ivGruntingPresent.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (grunting.equals(getString(R.string.absentValue))) {
                        ivGruntingAbsent.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (grunting.equals(getString(R.string.unableToConfirmValue))) {
                        ivGruntingUnable.setImageResource(R.drawable.ic_check_box_selected);
                    }

                    setDefaultChest();
                    chest = jsonObjectArray.getString("chestIndrawing");

                    if (chest.equals(getString(R.string.presentValue))) {
                        ivChestPresent.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (chest.equals(getString(R.string.absentValue))) {
                        ivChestAbsent.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (chest.equals(getString(R.string.unableToConfirmValue))) {
                        ivChestUnable.setImageResource(R.drawable.ic_check_box_selected);
                    }

                    setDefaultInterest();
                    interest = jsonObjectArray.getString("interestInFeeding");

                    if (interest.equals(getString(R.string.yesValue))) {
                        ivInterestYes.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (interest.equals(getString(R.string.noValue))) {
                        ivInterestNo.setImageResource(R.drawable.ic_check_box_selected);
                    }

                    setDefaultLactation();
                    lactation = jsonObjectArray.getString("sufficientLactation");

                    if (lactation.equals(getString(R.string.yesValue))) {
                        ivSufficientYes.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (lactation.equals(getString(R.string.noValue))) {
                        ivSufficientNo.setImageResource(R.drawable.ic_check_box_selected);
                    }

                    setDefaultSucking();
                    sucking = jsonObjectArray.getString("sucking");

                    if (sucking.equals(getString(R.string.goodValue))) {
                        ivGood.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (sucking.equals(getString(R.string.poorValue))) {
                        ivPoor.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (sucking.equals(getString(R.string.unableToConfirmValue))) {
                        ivNoSucking.setImageResource(R.drawable.ic_check_box_selected);
                    }

                    setDefaultUmbilicus();
                    umbilicus = jsonObjectArray.getString("umbilicus");

                    if (umbilicus.equals(getString(R.string.redValue))) {
                        ivRed.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (umbilicus.equals(getString(R.string.dischargeValue))) {
                        ivDischarge.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (umbilicus.equals(getString(R.string.normalValue))) {
                        ivNormal.setImageResource(R.drawable.ic_check_box_selected);
                    }

                    setDefaultSkinRash();
                    skinRash = jsonObjectArray.getString("skinPustules");

                    if (skinRash.equals(getString(R.string.noneObservedValue))) {
                        ivNoneObserved.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (skinRash.equals(getString(R.string.yes_10Value))) {
                        ivYesLessThen10.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (skinRash.equals(getString(R.string.yesGreater10Value))) {
                        ivYesGreaterThen10.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (skinRash.equals(getString(R.string.abscessValue))) {
                        ivAbscess.setImageResource(R.drawable.ic_check_box_selected);
                    }

                    //complication1="",complication2="",complication3="",complication4="",complication5=""

                    setDefaultBulging();
                    bulging = jsonObjectArray.getString("bulgingAnteriorFontanel");

                    if (bulging.equals(getString(R.string.presentValue))) {
                        ivPresent.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (bulging.equals(getString(R.string.absentValue))) {
                        ivAbsent.setImageResource(R.drawable.ic_check_box_selected);
                    }

                    setDefaultConvulsions();
                    convulsions = jsonObjectArray.getString("convulsions");

                    if (convulsions.equals(getString(R.string.presentOnAdmissionValue))) {
                        ivPresentOnAdmis.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (convulsions.equals(getString(R.string.pastHistoryValue))) {
                        ivPastHistory.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (convulsions.equals(getString(R.string.noValue))) {
                        ivConvulsionsNo.setImageResource(R.drawable.ic_check_box_selected);
                    }

                    setDefaultBleeding();
                    bleeding = jsonObjectArray.getString("isBleeding");

                    if (bleeding.equals(getString(R.string.presentValue))) {
                        ivBleedingPresent.setImageResource(R.drawable.ic_check_box_selected);
                    } else if (bleeding.equals(getString(R.string.absentValue))) {
                        ivBleedingAbsent.setImageResource(R.drawable.ic_check_box_selected);
                    }

                    etUrineTimes.setText(jsonObjectArray.getString("urinePassedIn24Hrs"));
                    etStoolTimes.setText(jsonObjectArray.getString("stoolPassedIn24Hrs"));

                    try {
                        JSONArray jsonArrayNew = new JSONArray(jsonObjectArray.getString("isAnyComplicationAtBirth"));
                        for (int k = 0; k < jsonArrayNew.length(); k++) {
                            JSONObject jsonObjectComplication = jsonArrayNew.getJSONObject(k);

                            if (jsonObjectComplication.getString("name").equals(getString(R.string.complications1Value))) {
                                complication1 = jsonObjectComplication.getString("name");
                                ivPerinatalAsphyxia.setImageResource(R.drawable.ic_check_box_selected);
                            } else if (jsonObjectComplication.getString("name").equals(getString(R.string.complications2Value))) {
                                complication2 = jsonObjectComplication.getString("name");
                                ivBabyOfDiabeticMother.setImageResource(R.drawable.ic_check_box_selected);
                            } else if (jsonObjectComplication.getString("name").equals(getString(R.string.complications3Value))) {
                                complication3 = jsonObjectComplication.getString("name");
                                ivMeconiumAspiration.setImageResource(R.drawable.ic_check_box_selected);
                            } else if (jsonObjectComplication.getString("name").equals(getString(R.string.complications4Value))) {
                                complication4 = jsonObjectComplication.getString("name");
                                ivMajorCongenitalMalformation.setImageResource(R.drawable.ic_check_box_selected);
                            } else if (!jsonObjectComplication.getString("name").isEmpty()) {
                                complication5 = jsonObjectComplication.getString("name");
                                ivAnyOther.setImageResource(R.drawable.ic_check_box_selected);
                                etOtherSpecify.setVisibility(View.VISIBLE);
                                etOtherSpecify.setText(jsonObjectComplication.getString("name"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }

    private static class adapterSpinner extends ArrayAdapter<String> {

        ArrayList<String> data;

        public adapterSpinner(Context context, int textViewResourceId, ArrayList<String> arraySpinner_time) {

            super(context, textViewResourceId, arraySpinner_time);

            this.data = arraySpinner_time;

        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            View row = inflater.inflate(R.layout.inflate_spinner_new, parent, false);

            TextView tvName = row.findViewById(R.id.tvName);

            tvName.setText(data.get(position));

            return row;
        }
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        public Adapter(ArrayList<HashMap<String, String>> favList) {
            data = favList;
        }

        public Adapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Adapter.Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_complications, parent, false));
        }

        @SuppressLint("SetTextI18n")
        public void onBindViewHolder(Adapter.Holder holder, final int position) {

            if (data.get(position).get("name").isEmpty()) {
                holder.tvName.setText("N/A");
            } else {
                holder.tvName.setText(getString(R.string.bullet) + "  " + data.get(position).get("name"));
            }

            Log.v("lkqshjqk", data.get(position).get("name"));

        }

        public int getItemCount() {
            return data.size();
        }

        private class Holder extends RecyclerView.ViewHolder {

            //TextView
            TextView tvName;


            public Holder(View itemView) {
                super(itemView);

                tvName = itemView.findViewById(R.id.tvName);
            }
        }
    }

}
