package code.main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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

import androidx.recyclerview.widget.RecyclerView;

import com.kmcapp.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import code.algo.SyncAllRecord;
import code.algo.SyncBabyRecord;
import code.common.AdapterSpinner;
import code.common.DecimalDigitsInputFilter;
import code.common.LocaleHelper;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableBabyMonitoring;
import code.registration.BabyAssessmentFragment;
import code.utils.AppUtils;
import code.view.BaseActivity;


public class BabyAssessmentActivity extends BaseActivity implements View.OnClickListener {

    //RelativeLayout
    private RelativeLayout rlPulseCrtNote,rlNext,rlPrevious,rlBottom,rlPulseNote;
    LinearLayout llPulseReason,llCrtReason;
    Spinner spinnerPulseReason;
    EditText etPulseReason,etPulseCrtReason;
    String selectedReason="";
    EditText etReason;
    JSONObject jsonData = new JSONObject();

    //ArrayList
    private ArrayList<String> tempText = new ArrayList<String>();
    private ArrayList<String> tempValue = new ArrayList<String>();

    //View
    private View viewVitals,viewGeneral,viewRespiratory,viewFeeding,viewMiscellaneous;

    //LinearLayout
    private LinearLayout llHeaderAssessment,llVitalTemp,llVitalResp,llVitalPulse,llGenExamination,
            llRespAssessment,llFeedingAssessment,llMiscellaneousStep1,llMiscellaneousStep2;

    int currentPage=1,checkPulse=0;

    String tempUnit="",thermometerAvailable = "",pulseAvailable="",crt="",crtValue="", alertness="",tone="",
            color="",apnea="",grunting="",chest="",interest="",lactation="",sucking="",umbilicus="",skinRash="",bulging="", bleeding="";

    int press=1,timer=0;
    int selectedReasonSpinnerPosition;

    //Vitals
    private RelativeLayout rlTempe,rltemplay,rlReason,rlNote, rlYes,rlNo,rlPress,rlPulseYes,rlPulseNo,rlCrtKnowYes,rlCrtKnowNo,rlCrtYes,rlCrtNo,
            rlCrt,rlAlert,rlLethargic,rlComatose,
            rlActive,rlLimp,rlTone,rloo_color,rlPale,rlCentral,rlPeripheral,rlJaundice,rlApeanPresent,rlApeanAbsent,rlApeanUnable,
            rlGruntingPresent,rlGruntingAbsent,rlGruntingUnable,rlChestPresent,rlChestAbsent,rlChestUnable,
            rlInterestYes,rlInterestNo,rlSufficientYes,rlSufficientNo,rlGood,rlPoor,rlNoSucking, rlRed,
            rlDischarge,rlNormal,rlNoneObserved,rlYesLessThen10,rlYesGreaterThen10,rlAbscess,rlPresent,
            rlAbsent,rlBleedingPresent,rlBleedingAbsent;

    //ImageView
    private ImageView ivYes,ivNo,ivPulseYes,ivPulseNo,ivCrtKnowYes,ivCrtKnowNo,ivCrtYes,ivCrtNo, ivAlertnessInfo,ivAlert,ivLethargic,
            ivComatose,ivComatoseInfo,ivToneInfo,ivActive,ivLimp, ivTone,ivoo_color,ivPale,ivCentral,ivCentralInfo,
            ivPeripheral,ivPeripheralInfo,ivJaundice, ivJaundiceInfo,ivApneaInfo,ivApeanPresent,ivApeanAbsent,
            ivApeanUnable,ivGruntingInfo, ivGruntingPresent, ivGruntingAbsent,ivGruntingUnable,ivChestInfo,
            ivChestPresent,ivChestAbsent,ivChestUnable,ivInterestYes, ivInterestNo,ivSufficientYes,ivSufficientNo,
            ivGood,ivPoor,ivNoSucking,ivUmbiliusInfo, ivRed, ivDischarge,ivNormal,ivNoneObserved,ivYesLessThen10,
            ivYesGreaterThen10,ivAbscess,
            ivPresent,ivAbsent,ivBleedingPresent,ivBleedingAbsent,ivNext;

    private ScrollView svFindings;

    //EditText
    private EditText etTemperature,etSpo2,etPulse,etUrineTimes,etStoolTimes;

    //Spinner
    private Spinner spinnerTempUnit;

    //Chronometer
    private Chronometer chronometer;

    //TextView
    private TextView tvCounter,tvStart,tvRespiRateCount,
            tvTemperatureResult,tvRespiRateResult,tvPulseResult,tvSpO2Result,tvCftResult,tvAlertResult,
            tvToneResult,tvColorResult,tvApneaResult,tvGruntingResult,tvChestResult,tvInterestResult,tvLactationResult,
            tvSuckingResult,tvUmbilicusResult,tvSkinResult,tvBulgingResult,tvBleedingResult,
            tvUrineResult,tvStoolResult;

    //LinearLayout
    private LinearLayout llOximeter;

    private  String uuid = "";

    //Spinner
    private Spinner spinnerEnteredByNurse;

    private ArrayList<String> nurseId = new ArrayList<String>();
    private ArrayList<String> nurseName = new ArrayList<String>();

    //LinearLayout
    private LinearLayout llSync,llHelp,llLogout,llLanguage;

    //RelativeLayout
    private RelativeLayout rlHelp,rlStuck,rlOwn;

    ImageView ivBulgingInfo;

    private Spinner spinnerCrtReason,spinnerReason;
    private ArrayList<String> reasonValue ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_assessment);

        findViewById();
        findViewByIdVital();
        findViewByIdGen();
        findViewByIdResp();
        findViewByIdFeeding();
        findViewByIdMiss1();
        findViewByIdMiss2();
        findViewByIdFindings();
        setClickListner();

        String where = TableBabyMonitoring.tableColumn.babyId + " = '" + AppSettings.getString(AppSettings.babyId)
                +"' and isDataComplete = '0' ";

        boolean countStep1 = DatabaseController.
                checkRecordExistWhere(TableBabyMonitoring.tableName, where);

        if(countStep1)
        {
            uuid = DatabaseController.getBabyMoniUUID(AppSettings.getString(AppSettings.babyId));
            AlertYesNo();
        }
        else
        {
            uuid  = UUID.randomUUID().toString();
            if(chronometer!=null)
            {
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.stop();
            }
            AppSettings.putString(AppSettings.respiratoryRate,"");
            currentPage=1;
            setViewVisibility(currentPage);
        }

        /*ContentValues mContentValues = new ContentValues();
        mContentValues.put(TableBabyMonitoring.tableColumn.isDataSynced.toString(),"1");

        DatabaseController.updateNotEqual(mContentValues,
                TableBabyMonitoring.tableName,"uuid", "0");*/



        rltemplay=findViewById(R.id.rlTemp);
        rlNote = findViewById(R.id.rlNote);
        rlReason = findViewById(R.id.rlReason);
        spinnerReason = findViewById(R.id.spinnerReason);
        rlPulseNote = findViewById(R.id.rlPulseNote);
        llPulseReason = findViewById(R.id.llPulseReason);
        spinnerPulseReason = findViewById(R.id.spinnerPulseReason);
        etPulseReason = findViewById(R.id.etPulseReason);
        etReason = findViewById(R.id.etReason);
        rlTempe = findViewById(R.id.rlTempe);


        reasonValue=new ArrayList<String>();
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
                if(selectedReason.equalsIgnoreCase(mActivity.getString(R.string.other)))
                {
                    rlNote.setVisibility(View.VISIBLE);
                }
                else
                {
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
                String  ss = reasonValue.get(position);

                if(ss.equalsIgnoreCase(mActivity.getString(R.string.other)))
                {
                    rlPulseNote.setVisibility(View.VISIBLE);
                }
                else
                {
                    rlPulseNote.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void findViewById() {

        uuid =  UUID.randomUUID().toString();

        //LinearLayout
        ivBulgingInfo      = findViewById(R.id.ivBulgingInfo);
        llSync      = findViewById(R.id.llSync);
        llHelp      = findViewById(R.id.llHelp);
        llLogout = findViewById(R.id.llLogout);
        llLanguage = findViewById(R.id.llLanguage);

        //RelativeLayout
        rlHelp= findViewById(R.id.rlHelp);
        rlStuck= findViewById(R.id.rlStuck);
        rlOwn= findViewById(R.id.rlOwn);

        //RelativeLayout
        rlNext = findViewById(R.id.rlNext);
        rlPrevious = findViewById(R.id.rlPrevious);
        rlBottom = findViewById(R.id.rlBottom);

        //ImageView
        ivNext= findViewById(R.id.ivNext);

        //View
        viewVitals= findViewById(R.id.viewVitals);
        viewGeneral= findViewById(R.id.viewGeneral);
        viewRespiratory= findViewById(R.id.viewRespiratory);
        viewFeeding= findViewById(R.id.viewFeeding);
        viewMiscellaneous= findViewById(R.id.viewMiscellaneous);

        //LinearLayout
        llHeaderAssessment= findViewById(R.id.llHeaderAssessment);
        llVitalTemp= findViewById(R.id.llVitalTemp);
        llVitalResp= findViewById(R.id.llVitalResp);
        llVitalPulse= findViewById(R.id.llVitalPulse);
        llGenExamination= findViewById(R.id.llGenExamination);
        llRespAssessment= findViewById(R.id.llRespAssessment);
        llFeedingAssessment= findViewById(R.id.llFeedingAssessment);
        llMiscellaneousStep1= findViewById(R.id.llMiscellaneousStep1);
        llMiscellaneousStep2= findViewById(R.id.llMiscellaneousStep2);

        svFindings = findViewById(R.id.svFindings);
    }

    private void findViewByIdVital() {

        //RelativeLayout
        rlYes = findViewById(R.id.rlYes);
        rlNo = findViewById(R.id.rlNo);
        rlPress = findViewById(R.id.rlPress);
        rlPulseYes = findViewById(R.id.rlPulseYes);
        rlPulseNo = findViewById(R.id.rlPulseNo);
        rlCrtKnowYes= findViewById(R.id.rlCrtKnowYes);
        rlCrtKnowNo= findViewById(R.id.rlCrtKnowNo);
        rlCrtYes= findViewById(R.id.rlCrtYes);
        rlCrtNo= findViewById(R.id.rlCrtNo);
        rlCrt = findViewById(R.id.rlCrt);

        //ImageView
        ivYes = findViewById(R.id.ivYes);
        ivNo = findViewById(R.id.ivNo);
        ivPulseYes = findViewById(R.id.ivPulseYes);
        ivPulseNo = findViewById(R.id.ivPulseNo);
        ivCrtKnowYes= findViewById(R.id.ivCrtKnowYes);
        ivCrtKnowNo= findViewById(R.id.ivCrtKnowNo);
        ivCrtYes= findViewById(R.id.ivCrtYes);
        ivCrtNo= findViewById(R.id.ivCrtNo);

        //EditText
        etTemperature = findViewById(R.id.etTemperature);
        etSpo2 = findViewById(R.id.etSpo2);
        etPulse = findViewById(R.id.etPulse);

        etTemperature.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(3,2)});

        //Spinner
        spinnerTempUnit = findViewById(R.id.spinnerTempUnit);

        //Chronometer
        chronometer = findViewById(R.id.chronometer);

        //TextView
        tvCounter = findViewById(R.id.tvCounter);
        tvStart = findViewById(R.id.tvStart);
        tvRespiRateCount = findViewById(R.id.tvRespiRateCount);

        //LinearLayout
        llOximeter = findViewById(R.id.llOximeter);

        tempText.clear();
        tempText.add(getString(R.string.degreeF));
        tempText.add(getString(R.string.degreeC));

        tempValue.clear();
        tempValue.add(getString(R.string.degreeFValue));
        tempValue.add(getString(R.string.degreeCValue));

        spinnerTempUnit.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, tempText));
        spinnerTempUnit.setSelection(0);

        //Spinner for Relation
        spinnerTempUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

//                tempUnit = tempValue.get(position);
                tempUnit =   spinnerTempUnit.getSelectedItem().toString();

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
    }

    private void findViewByIdGen() {

        //RelativeLayout
        rlAlert= findViewById(R.id.rlAlert);
        rlLethargic= findViewById(R.id.rlLethargic);
        rlComatose= findViewById(R.id.rlComatose);
        rloo_color= findViewById(R.id.rloo_color);
        rlActive= findViewById(R.id.rlActive);
        rlLimp= findViewById(R.id.rlLimp);
        rlTone= findViewById(R.id.rlTone);
        rlPale= findViewById(R.id.rlPale);
        rlCentral= findViewById(R.id.rlCentral);
        rlPeripheral= findViewById(R.id.rlPeripheral);
        rlJaundice= findViewById(R.id.rlJaundice);

        //ImageView
        ivAlertnessInfo= findViewById(R.id.ivAlertnessInfo);
        ivAlert= findViewById(R.id.ivAlert);
        ivLethargic= findViewById(R.id.ivLethargic);
        ivComatose= findViewById(R.id.ivComatose);
        ivComatoseInfo= findViewById(R.id.ivComatoseInfo);
        ivToneInfo= findViewById(R.id.ivToneInfo);
        ivActive= findViewById(R.id.ivActive);
        ivLimp= findViewById(R.id.ivLimp);
        ivTone = findViewById(R.id.ivTone);
        ivoo_color= findViewById(R.id.ivoo_color);
        ivPale= findViewById(R.id.ivPale);
        ivCentral= findViewById(R.id.ivCentral);
        ivCentralInfo= findViewById(R.id.ivCentralInfo);
        ivPeripheral= findViewById(R.id.ivPeripheral);
        ivPeripheralInfo= findViewById(R.id.ivPeripheralInfo);
        ivJaundice= findViewById(R.id.ivJaundice);
        ivJaundiceInfo= findViewById(R.id.ivJaundiceInfo);

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

    private void findViewByIdResp() {

        //ImageView
        ivApneaInfo = findViewById(R.id.ivApneaInfo);
        ivApeanPresent= findViewById(R.id.ivApeanPresent);
        ivApeanAbsent= findViewById(R.id.ivApeanAbsent);
        ivApeanUnable= findViewById(R.id.ivApeanUnable);
        ivGruntingInfo= findViewById(R.id.ivGruntingInfo);
        ivGruntingPresent= findViewById(R.id.ivGruntingPresent);
        ivGruntingAbsent= findViewById(R.id.ivGruntingAbsent);
        ivGruntingUnable= findViewById(R.id.ivGruntingUnable);
        ivChestInfo= findViewById(R.id.ivChestInfo);
        ivChestPresent= findViewById(R.id.ivChestPresent);
        ivChestAbsent= findViewById(R.id.ivChestAbsent);
        ivChestUnable= findViewById(R.id.ivChestUnable);

        //RelativeLayout
        rlApeanPresent= findViewById(R.id.rlApeanPresent);
        rlApeanAbsent= findViewById(R.id.rlApeanAbsent);
        rlApeanUnable= findViewById(R.id.rlApeanUnable);
        rlGruntingPresent= findViewById(R.id.rlGruntingPresent);
        rlGruntingAbsent= findViewById(R.id.rlGruntingAbsent);
        rlGruntingUnable= findViewById(R.id.rlGruntingUnable);
        rlChestPresent= findViewById(R.id.rlChestPresent);
        rlChestAbsent= findViewById(R.id.rlChestAbsent);
        rlChestUnable= findViewById(R.id.rlChestUnable);

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

    private void findViewByIdFeeding() {

        //RelativeLayout
        rlInterestYes= findViewById(R.id.rlInterestYes);
        rlInterestNo= findViewById(R.id.rlInterestNo);
        rlSufficientYes= findViewById(R.id.rlSufficientYes);
        rlSufficientNo= findViewById(R.id.rlSufficientNo);
        rlGood= findViewById(R.id.rlGood);
        rlPoor= findViewById(R.id.rlPoor);
        rlNoSucking= findViewById(R.id.rlNoSucking);

        //ImageView
        ivInterestYes= findViewById(R.id.ivInterestYes);
        ivInterestNo= findViewById(R.id.ivInterestNo);
        ivSufficientYes= findViewById(R.id.ivSufficientYes);
        ivSufficientNo= findViewById(R.id.ivSufficientNo);
        ivGood= findViewById(R.id.ivGood);
        ivPoor= findViewById(R.id.ivPoor);
        ivNoSucking= findViewById(R.id.ivNoSucking);

        //setOnClickListener
        rlInterestYes.setOnClickListener(this);
        rlInterestNo.setOnClickListener(this);
        rlSufficientYes.setOnClickListener(this);
        rlSufficientNo.setOnClickListener(this);
        rlGood.setOnClickListener(this);
        rlPoor.setOnClickListener(this);
        rlNoSucking.setOnClickListener(this);
    }

    private void findViewByIdMiss1() {

        //RelativeLayout
        rlRed = findViewById(R.id.rlRed);
        rlDischarge= findViewById(R.id.rlDischarge);
        rlNormal= findViewById(R.id.rlNormal);
        rlNoneObserved= findViewById(R.id.rlNoneObserved);
        rlYesLessThen10= findViewById(R.id.rlYesLessThen10);
        rlYesGreaterThen10= findViewById(R.id.rlYesGreaterThen10);
        rlAbscess= findViewById(R.id.rlAbscess);
        rlPresent= findViewById(R.id.rlPresent);
        rlAbsent= findViewById(R.id.rlAbsent);

        //ImageView
        ivUmbiliusInfo= findViewById(R.id.ivUmbiliusInfo);
        ivRed = findViewById(R.id.ivRed);
        ivDischarge= findViewById(R.id.ivDischarge);
        ivNormal= findViewById(R.id.ivNormal);
        ivNoneObserved= findViewById(R.id.ivNoneObserved);
        ivYesLessThen10= findViewById(R.id.ivYesLessThen10);
        ivYesGreaterThen10= findViewById(R.id.ivYesGreaterThen10);
        ivAbscess= findViewById(R.id.ivAbscess);
        ivPresent= findViewById(R.id.ivPresent);
        ivAbsent= findViewById(R.id.ivAbsent);

        ivUmbiliusInfo.setOnClickListener(this);
        rlRed.setOnClickListener(this);
        rlDischarge.setOnClickListener(this);
        rlNormal.setOnClickListener(this);
        rlNoneObserved.setOnClickListener(this);
        rlYesLessThen10.setOnClickListener(this);
        rlYesGreaterThen10.setOnClickListener(this);
        rlAbscess.setOnClickListener(this);
        rlPresent.setOnClickListener(this);
        rlAbsent.setOnClickListener(this);
    }

    private void findViewByIdMiss2() {

        //RelativeLayout
        rlBleedingPresent= findViewById(R.id.rlBleedingPresent);
        rlBleedingAbsent= findViewById(R.id.rlBleedingAbsent);

        //ImageView
        ivBleedingPresent= findViewById(R.id.ivBleedingPresent);
        ivBleedingAbsent= findViewById(R.id.ivBleedingAbsent);

        //EditText
        etUrineTimes= findViewById(R.id.etUrineTimes);
        etStoolTimes= findViewById(R.id.etStoolTimes);

        //RelativeLayout
        rlBleedingPresent.setOnClickListener(this);
        rlBleedingAbsent.setOnClickListener(this);
    }

    private void findViewByIdFindings() {

        //Spinner
        spinnerEnteredByNurse = findViewById(R.id.spinnerEnteredByNurse);

        //TextView
        tvTemperatureResult= findViewById(R.id.tvTemperatureResult);
        tvRespiRateResult= findViewById(R.id.tvRespiRateResult);
        tvPulseResult= findViewById(R.id.tvPulseResult);
        tvSpO2Result= findViewById(R.id.tvSpO2Result);
        tvCftResult= findViewById(R.id.tvCftResult);
        tvAlertResult= findViewById(R.id.tvAlertResult);
        tvToneResult= findViewById(R.id.tvToneResult);
        tvColorResult= findViewById(R.id.tvColorResult);
        tvApneaResult= findViewById(R.id.tvApneaResult);
        tvGruntingResult= findViewById(R.id.tvGruntingResult);
        tvChestResult= findViewById(R.id.tvChestResult);
        tvInterestResult= findViewById(R.id.tvInterestResult);
        tvLactationResult= findViewById(R.id.tvLactationResult);
        tvSuckingResult= findViewById(R.id.tvSuckingResult);
        tvUmbilicusResult= findViewById(R.id.tvUmbilicusResult);
        tvSkinResult= findViewById(R.id.tvSkinResult);
        tvBulgingResult= findViewById(R.id.tvBulgingResult);
        tvBleedingResult= findViewById(R.id.tvBleedingResult);
        tvUrineResult= findViewById(R.id.tvUrineResult);
        tvStoolResult= findViewById(R.id.tvStoolResult);

        nurseId.clear();
        nurseId.add(getString(R.string.erroselectNurse));
        nurseId.addAll(DatabaseController.getNurseIdCheckedInData());

        nurseName.clear();
        nurseName.add(getString(R.string.erroselectNurse));
        nurseName.addAll(DatabaseController.getNurseNameCheckedInData());

        spinnerEnteredByNurse.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, nurseName));
        spinnerEnteredByNurse.setSelection(0);

        currentPage=1;
        setViewVisibility(currentPage);
    }

    private void setClickListner() {

        //RelativeLayout SetOnClickListener
        rlNext.setOnClickListener(this);
        rlPrevious.setOnClickListener(this);

        //setOnClickListener
        llSync.setOnClickListener(this);
        llHelp.setOnClickListener(this);
        llLogout.setOnClickListener(this);
        llLanguage.setOnClickListener(this);
        rlHelp.setOnClickListener(this);
        rlStuck.setOnClickListener(this);
        rlOwn.setOnClickListener(this);
        ivBulgingInfo.setOnClickListener(this);

    }

    private void setDefault() {

        //View
        viewVitals.setBackgroundResource(R.color.lightgreyback);
        viewGeneral.setBackgroundResource(R.color.lightgreyback);
        viewRespiratory.setBackgroundResource(R.color.lightgreyback);
        viewFeeding.setBackgroundResource(R.color.lightgreyback);
        viewMiscellaneous.setBackgroundResource(R.color.lightgreyback);
        ivNext.setImageResource(R.drawable.ic_next);

        //LinearLayout
        rlBottom.setVisibility(View.VISIBLE);
        llHeaderAssessment.setVisibility(View.VISIBLE);
        llVitalTemp.setVisibility(View.GONE);
        llVitalResp.setVisibility(View.GONE);
        llVitalPulse.setVisibility(View.GONE);
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

            case R.id.llLanguage:

                AppUtils.AlertLanguageConfirm(mActivity,getString(R.string.languageAlert));

                break;

            case R.id.llSync:

                if (AppUtils.isNetworkAvailable(mActivity)) {
                    SyncAllRecord.postDutyChange(mActivity);
                } else {
                    AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
                }

                break;


            case R.id.llLogout:

                AppUtils.AlertLogoutConfirm(mActivity);

                break;

            case R.id.llHelp:

            case R.id.rlHelp:

                if(rlHelp.getVisibility()==View.VISIBLE)
                {
                    rlHelp.setVisibility(View.GONE);
                }
                else
                {
                    rlHelp.setVisibility(View.VISIBLE);
                }

                break;

            case R.id.rlStuck:

                rlHelp.setVisibility(View.GONE);
                DatabaseController.saveHelp(mActivity);
                AppUtils.AlertHelpConfirm(getString(R.string.callRegardingIssue),mActivity,1,"");

                break;

            case R.id.rlOwn:

                rlHelp.setVisibility(View.GONE);
                startActivity(new Intent(mActivity, TutorialActivity.class));

                break;


            case R.id.rlNext:

                validation();

                break;

            case R.id.rlPrevious:

                currentPage = currentPage-1;
                setViewVisibility(currentPage);

                break;

            case R.id.ivAlertnessInfo:

                AppUtils.AlertOk(getString(R.string.alertnessDetail),mActivity,3,getString(R.string.alertness));

                break;

            case R.id.ivComatoseInfo:

                AppUtils.AlertOk(getString(R.string.comatoseDetail),mActivity,3,getString(R.string.comatose));

                break;

            case R.id.ivToneInfo:

                AppUtils.AlertOk(getString(R.string.toneDetail),mActivity,3,getString(R.string.tone));

                break;

            case R.id.ivCentralInfo:

                AppUtils.AlertOk(getString(R.string.centralDetail),mActivity,3,getString(R.string.centralCyanosis));

                break;

            case R.id.ivPeripheralInfo:

                AppUtils.AlertOk(getString(R.string.peripheralDetail),mActivity,3,getString(R.string.peripheralCyanosis));

                break;

            case R.id.ivJaundiceInfo:

                AppUtils.AlertOk(getString(R.string.jaundiceDetail),mActivity,3,getString(R.string.jaundice));

                break;

            case R.id.ivApneaInfo:

                AppUtils.AlertOk(getString(R.string.apneaDetail),mActivity,3,getString(R.string.apnea));

                break;

            case R.id.ivGruntingInfo:

                AppUtils.AlertOk(getString(R.string.gruntingDetail),mActivity,3,getString(R.string.grunting));

                break;

            case R.id.ivChestInfo:

                AppUtils.AlertOk(getString(R.string.chestDetail),mActivity,3,getString(R.string.chestIndrawing));

                break;

            case R.id.ivUmbiliusInfo:

                AppUtils.AlertOk(getString(R.string.umbilicusDetail),mActivity,3,getString(R.string.umbilicus));

                break;

            case R.id.ivPerinatalInfo:

                AppUtils.AlertOk(getString(R.string.perinatalDetail),mActivity,3,getString(R.string.perinatalAsphyxia));

                break;

            case R.id.ivMeconiumAspirationInfo:

                AppUtils.AlertOk(getString(R.string.meconiumDetail),mActivity,3,getString(R.string.meconiumAspiration));

                break;

            case R.id.ivBulgingInfo:

                AppUtils.AlertOk(getString(R.string.bulgingDetail),mActivity,3,getString(R.string.bulgingAnteriorFontanelleMandResult));

                break;

            case R.id.rlYes:
                rlReason.setVisibility(View.GONE);
                rlNote.setVisibility(View.GONE);
                rltemplay.setVisibility(View.VISIBLE);
                etTemperature.setEnabled(true);
                spinnerTempUnit.setEnabled(true);
                setDefaultTemp();
                thermometerAvailable=getString(R.string.yesValue);
                ivYes.setImageResource(R.drawable.ic_check_box_selected);
                rlTempe.setVisibility(View.VISIBLE);
                rlReason.setVisibility(View.GONE);
                break;

            case R.id.rlNo:
                rlTempe.setVisibility(View.GONE);
                rlReason.setVisibility(View.VISIBLE);
                etTemperature.setEnabled(false);
                spinnerTempUnit.setEnabled(false);
                rltemplay.setVisibility(View.GONE);
                setDefaultTemp();
                thermometerAvailable=getString(R.string.noValue);
                ivNo.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlPress:

                if(press==1)
                {
                    timer=1;
                    press=1;

                    int n = Integer.parseInt(tvCounter.getText().toString().trim());

                    if(n==0)
                    {
                        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                            @Override
                            public void onChronometerTick(Chronometer chronometerChanged) {
                                chronometer = chronometerChanged;

                                long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();

                                int time = 10000;
                                if(AppUtils.checkServer())
                                {
                                    time = 10000;
                                }

                                if(elapsedMillis> time)
                                //if(elapsedMillis>10000)
                                {
                                    chronometer.stop();
                                    tvStart.setText(getString(R.string.start));
                                    tvRespiRateCount.setVisibility(View.VISIBLE);
                                    press=0;
                                    timer=0;
                                    rlPress.setClickable(false);
                                    tvRespiRateCount.setText(getString(R.string.respiratoryRate)+": " +tvCounter.getText().toString().trim()+" / min");
                                    AppSettings.putString(AppSettings.respiratoryRate,tvCounter.getText().toString().trim());
                                    int c = Integer.parseInt(tvCounter.getText().toString().trim());
                                    if(c<30||c>60)
                                    {
                                        AppUtils.AlertOk(getString(R.string.dangerSituation),mActivity,2,"");
                                    }

                                    tvCounter.setText("0");
                                    chronometer.setText("00:00");
                                    rlPress.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            rlPress.setClickable(true);
                                            press=1;
                                        }
                                    }, 10000);
                                }
                            }
                        });

                        n = n+1;
                        tvCounter.setText(String.valueOf(n));
                        tvStart.setText(getString(R.string.press));

                        if(chronometer!=null)
                        {
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            chronometer.stop();
                        }

                        chronometer.start();
                        chronometer.setFormat("%s"); // set the format for a chronometer
                    }
                    else
                    {
                        n = n+1;
                        tvCounter.setText(String.valueOf(n));
                        tvStart.setText(getString(R.string.press));
                    }
                }
                else
                {

                }

                break;

            case R.id.rlPulseYes:

                setDefaultPulse();
                llPulseReason.setVisibility(View.GONE);
                pulseAvailable=getString(R.string.yesValue);
                ivPulseYes.setImageResource(R.drawable.ic_check_box_selected);
                llOximeter.setVisibility(View.VISIBLE);
                rlPulseNote.setVisibility(View.GONE);

                break;

            case R.id.rlPulseNo:

                setDefaultPulse();
                pulseAvailable=getString(R.string.noValue);
                llPulseReason.setVisibility(View.VISIBLE);
                ivPulseNo.setImageResource(R.drawable.ic_check_box_selected);
                if(spinnerPulseReason.getSelectedItem().equals(mActivity.getString(R.string.other)))
                {
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
                color = getString(R.string.normalValue);
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
        }

    }

    private void setDefaultTemp() {

        thermometerAvailable="";
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

    private void setDefaultBleeding() {

        bleeding = "";
        ivBleedingPresent.setImageResource(R.drawable.ic_check_box);
        ivBleedingAbsent.setImageResource(R.drawable.ic_check_box);
    }

    private void setViewVisibility(int position) {

        setDefault();

        if(position==1)
        {
            rlPrevious.setVisibility(View.GONE);
            viewVitals.setBackgroundResource(R.color.oo_color);
            llVitalTemp.setVisibility(View.VISIBLE);
        }
        else  if(position==2)
        {
            viewVitals.setBackgroundResource(R.color.oo_color);
            llVitalResp.setVisibility(View.VISIBLE);
        }
        else  if(position==3)
        {
            viewVitals.setBackgroundResource(R.color.oo_color);
            llVitalPulse.setVisibility(View.VISIBLE);
        }
        else  if(position==4)
        {
            viewGeneral.setBackgroundResource(R.color.oo_color);
            llGenExamination.setVisibility(View.VISIBLE);
        }
        else  if(position==5)
        {
            viewRespiratory.setBackgroundResource(R.color.oo_color);
            llRespAssessment.setVisibility(View.VISIBLE);
        }
        else  if(position==6)
        {
            viewFeeding.setBackgroundResource(R.color.oo_color);
            llFeedingAssessment.setVisibility(View.VISIBLE);
        }
        else  if(position==7)
        {
            viewMiscellaneous.setBackgroundResource(R.color.oo_color);
            llMiscellaneousStep1.setVisibility(View.VISIBLE);
        }
        else  if(position==8)
        {
            rlPrevious.setVisibility(View.VISIBLE);
            viewMiscellaneous.setBackgroundResource(R.color.oo_color);
            llMiscellaneousStep2.setVisibility(View.VISIBLE);
        }
        else  if(position==9)
        {
            setValues();
            ivNext.setImageResource(R.drawable.ic_tick);
            llHeaderAssessment.setVisibility(View.GONE);
            rlPrevious.setVisibility(View.VISIBLE);
            svFindings.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setValues() {

        if(thermometerAvailable.equals(getString(R.string.yesValue)))
        {
            tvTemperatureResult.setText(getString(R.string.bullet)
                    +"  "+getString(R.string.temperature)
                    +" = "+etTemperature.getText().toString().trim()
                    +spinnerTempUnit.getSelectedItem());

            try {
                float temp= Float.parseFloat(etTemperature.getText().toString().trim());
                if(spinnerTempUnit.getSelectedItem().equals("° F"))
                {
                    if (temp < 95.9 || temp > 99.5) {
                        tvTemperatureResult.setTextColor(mActivity.getResources().getColor(R.color.red));
                    }
                }else {
                    if (temp < 36.4 || temp > 38) {
                        tvTemperatureResult.setTextColor(mActivity.getResources().getColor(R.color.red));
                    }
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


        }
        else
        {
            tvTemperatureResult.setText(getString(R.string.bullet)
                    +"  "+getString(R.string.temperature)
                    +" = "+getString(R.string.notApplicable));
        }

        tvRespiRateResult.setText(getString(R.string.bullet)
                +"  "+getString(R.string.respiratoryRate)
                +" = "+ AppSettings.getString(AppSettings.respiratoryRate)

                +getString(R.string.min));


        try {
            int rr= Integer.parseInt(AppSettings.getString(AppSettings.respiratoryRate));
            if (rr <  30 || rr > 60) {
                tvRespiRateResult.setTextColor(mActivity.getResources().getColor(R.color.red));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(pulseAvailable.equals(getString(R.string.yesValue)))
        {
            tvPulseResult.setText(getString(R.string.bullet)
                    +"  "+getString(R.string.pulseRate)
                    +" = "+etPulse.getText().toString().trim()
                    +getString(R.string.bpm));

            tvSpO2Result.setText(getString(R.string.bullet) +"  "+getString(R.string.spo2) +" = "+etSpo2.getText().toString().trim()+"%");

            try {
                int spo2= Integer.parseInt(etSpo2.getText().toString().trim());
                if (spo2 < 95) {
                    tvSpO2Result.setTextColor(mActivity.getResources().getColor(R.color.red));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            try {
                int bpr= Integer.parseInt(etPulse.getText().toString().trim());
                if (bpr <  75 || bpr > 200) {
                    tvPulseResult.setTextColor(mActivity.getResources().getColor(R.color.red));
                }
            }
            catch (Exception e)
            {
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

        }
        else
        {
            tvPulseResult.setText(getString(R.string.bullet)
                    +"  "+getString(R.string.pulseRate)
                    +" = "+getString(R.string.notApplicable));

            tvSpO2Result.setText(getString(R.string.bullet)
                    +"  "+getString(R.string.spo2)
                    +" = "+getString(R.string.notApplicable));
        }

        if(crt.equals(getString(R.string.yesValue))) {
            if(crtValue.equals(getString(R.string.yesValue)))
            {
                tvCftResult.setText(getString(R.string.bullet)+"  "+getString(R.string.crtResult) +" = "+getString(R.string.yesValue));
            }
            else
            {
                tvCftResult.setText(getString(R.string.bullet)+"  "+getString(R.string.crtResult) +" = "+getString(R.string.no));
            }
        }
        else
        {
            tvCftResult.setText(getString(R.string.bullet)+"  "+getString(R.string.crtResult) +" = "+getString(R.string.notApplicable));
        }

        tvAlertResult.setText(getString(R.string.bullet)
                        +"  "+getString(R.string.alertness)
                        +" = "+alertness);

        tvToneResult.setText(getString(R.string.bullet)
                +"  "+getString(R.string.tone)
                +" = "+tone);

        tvColorResult.setText(getString(R.string.bullet)
                +"  "+getString(R.string.color)
                +" = "+color);

        tvApneaResult.setText(getString(R.string.bullet)
                +"  "+getString(R.string.apnea)
                +" = "+apnea);

        tvGruntingResult.setText(getString(R.string.bullet)
                +"  "+getString(R.string.grunting)
                +" = "+grunting);

        tvChestResult.setText(getString(R.string.bullet)
                +"  "+getString(R.string.chestIndrawing)
                +" = "+chest);

        tvInterestResult.setText(getString(R.string.bullet)
                +"  "+getString(R.string.interestInFeedingMandResult)
                +" = "+interest);

        tvLactationResult.setText(getString(R.string.bullet)
                +"  "+getString(R.string.sufficientLactation)
                +" = "+lactation);

        tvSuckingResult.setText(getString(R.string.bullet)
                +"  "+getString(R.string.suckingMandResult)
                +" = "+sucking);

        tvUmbilicusResult.setText(getString(R.string.bullet)
                +"  "+getString(R.string.umbilicusResult)
                +" = "+umbilicus);

        tvSkinResult.setText(getString(R.string.bullet)
                +"  "+getString(R.string.skin)
                +" = "+skinRash);

        tvBulgingResult.setText(getString(R.string.bullet)
                +"  "+getString(R.string.bulgingAnteriorFontanelleMandResult)
                +" = "+bulging);

        tvBleedingResult.setText(getString(R.string.bullet)
                +"  "+getString(R.string.bleedingFromAnySiteMandResult)
                +" = "+bleeding);

        tvUrineResult.setText(getString(R.string.bullet)
                +"  "+getString(R.string.urineIn)
                +" = "+etUrineTimes.getText().toString().trim());

        tvStoolResult.setText(getString(R.string.bullet)
                +"  "+getString(R.string.stoolIn)
                +" = "+etStoolTimes.getText().toString().trim());
    }

    private void validation() {

        if(!thermometerAvailable.isEmpty())
        {
            saveBabyMonitoringData("0");
        }

        int spo2=0;
        try {
            spo2 = Integer.parseInt(etSpo2.getText().toString().trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        int pulse=0;
        try {
            pulse = Integer.parseInt(etPulse.getText().toString().trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        double temp=0;
        try {
            temp = Float.parseFloat(etTemperature.getText().toString().trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        double minTempF = 90;
        double maxTempF = 107;
        double minTempC = 32.2;
        double maxTempC = 41.6;


        if(currentPage==1 && thermometerAvailable.equalsIgnoreCase(mActivity.getString(R.string.noValue)) && spinnerReason.getSelectedItem().equals(mActivity.getString(R.string.reason)))
        {
            AppUtils.showToastSort(mActivity, getString(R.string.errorEnterReason));
            return;
        }

        if(pulseAvailable.equalsIgnoreCase(mActivity.getString(R.string.noValue)) && currentPage==3 && spinnerPulseReason.getSelectedItem().equals(mActivity.getString(R.string.reason)) )
        {
            AppUtils.showToastSort(mActivity, getString(R.string.errorSelectReason));
            return;
        }



        if(thermometerAvailable.isEmpty()
                &&currentPage==1)
        {
            AppUtils.showToastSort(mActivity, getString(R.string.ErrorisThermometerAvailable));
        }
        else if(thermometerAvailable.equals(getString(R.string.yesValue))
                &&currentPage==1
                &&etTemperature.getText().toString().trim().isEmpty())
        {
            etTemperature.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorTemp));
        }
        else if(currentPage==1
                &&thermometerAvailable.equalsIgnoreCase(getString(R.string.yesValue))
                &&tempUnit.equalsIgnoreCase("° F")
                &&(temp<minTempF||temp>maxTempF))
        {
            AppUtils.showToastSort(mActivity, getString(R.string.errorTempF));
        }
        else if(currentPage==1
                &&thermometerAvailable.equalsIgnoreCase(getString(R.string.yesValue))
                &&tempUnit.equalsIgnoreCase("° C")
                &&(temp<minTempC||temp>maxTempC))
        {
            AppUtils.showToastSort(mActivity, getString(R.string.errorTempC));
        }
        else if(tvRespiRateCount.getText().toString().isEmpty()
                &&currentPage==2)
        {
            AppUtils.showToastSort(mActivity, getString(R.string.errorRespiratory));
        }
        else if(timer==1
                &&currentPage==2)
        {
            AppUtils.showToastSort(mActivity, getString(R.string.errorRespiratory));
        }
        else if(pulseAvailable.isEmpty()&&currentPage==3)
        {
            AppUtils.showToastSort(mActivity, getString(R.string.havePulseOximetryerror));
        }
        else if(etSpo2.getText().toString().trim().isEmpty()
                &&etSpo2.getVisibility()==View.VISIBLE
                &&pulseAvailable.equalsIgnoreCase(getString(R.string.yesValue)))
        {
            etSpo2.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorSpo2Empty));
        }
        else if((spo2 < 20 || spo2 > 100)&&currentPage==3&&pulseAvailable.equalsIgnoreCase(getString(R.string.yesValue)))
        {
            AppUtils.showToastSort(mActivity, getString(R.string.errorSpo2));
        }
        else  if(etPulse.getText().toString().trim().isEmpty()
                &&etPulse.getVisibility()==View.VISIBLE &&currentPage==3
                &&pulseAvailable.equalsIgnoreCase(getString(R.string.yesValue)))
        {
            etPulse.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorPulseEmpty));
        }
        else if((pulse>300||pulse<10)
                &&etPulse.getVisibility()==View.VISIBLE&&currentPage==3
                &&checkPulse==0&&pulseAvailable.equalsIgnoreCase(getString(R.string.yesValue)))
        {
            etPulse.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorPulse));

//            checkPulse=1;
//            String str = getString(R.string.areYouSurePart1)+" "+pulse+" "+getString(R.string.bpm)+getString(R.string.areYouSurePart2);
//            AppUtils.AlertOk(str,mActivity,2,"");
        }
        else if(crt.isEmpty()&&currentPage==3)
        {
            AppUtils.showToastSort(mActivity, getString(R.string.knowledgeCapiliaryFillingerror));
        }
        else if(crt.equalsIgnoreCase(getString(R.string.yesValue))&&crtValue.isEmpty()&&currentPage==3)
        {
            AppUtils.showToastSort(mActivity, getString(R.string.isCrt3));
        }
        else if(alertness.isEmpty()&&currentPage==4)
        {
            AppUtils.showToastSort(mActivity, getString(R.string.errorAlertness));
        }
        else if(tone.isEmpty()&&currentPage==4)
        {
            AppUtils.showToastSort(mActivity, getString(R.string.errorMuscleTone));
        }
        else if(color.isEmpty()&&currentPage==4)
        {
            AppUtils.showToastSort(mActivity, getString(R.string.errorColor));
        }
        else if(apnea.isEmpty()&&currentPage==5)
        {
            AppUtils.showToastSort(mActivity, getString(R.string.errorApnea));
        }
        else if(grunting.isEmpty()&&currentPage==5)
        {
            AppUtils.showToastSort(mActivity, getString(R.string.errorGrunting));
        }
        else if(chest.isEmpty()&&currentPage==5)
        {
            AppUtils.showToastSort(mActivity, getString(R.string.errorChest));
        }
        else if(interest.isEmpty()&&currentPage==6)
        {
            AppUtils.showToastSort(mActivity, getString(R.string.interestInFeeding));
        }
        else if(lactation.isEmpty()&&currentPage==6)
        {
            AppUtils.showToastSort(mActivity, getString(R.string.isMotherSufficientLactation));
        }
        else if(sucking.isEmpty()&&currentPage==6)
        {
            AppUtils.showToastSort(mActivity, getString(R.string.sucking));
        }
        else if(umbilicus.isEmpty()&&currentPage==7)
        {
            AppUtils.showToastSort(mActivity, getString(R.string.umbilicus));
        }
        else if(skinRash.isEmpty()&&currentPage==7)
        {
            AppUtils.showToastSort(mActivity, getString(R.string.skinRashOnPustules));
        }
        else if(bulging.isEmpty()&&currentPage==7)
        {
            AppUtils.showToastSort(mActivity, getString(R.string.bulgingAnteriorFontanelle));
        }
        else if(bleeding.isEmpty()&&currentPage==8)
        {
            AppUtils.showToastSort(mActivity, getString(R.string.bleedingFromAnySite));
        }
        else if(etUrineTimes.getText().toString().trim().isEmpty()&&currentPage==8)
        {
            etUrineTimes.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.noOfTimesUrine));
        } else if (!etUrineTimes.getText().toString().trim().isEmpty() && Integer.valueOf(etUrineTimes.getText().toString()) > 15 && currentPage == 8) {
            etUrineTimes.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.urineValidation));
        }

        else if(etStoolTimes.getText().toString().trim().isEmpty()&&currentPage==8)
        {
            etStoolTimes.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.noOfTimesStool));
        }

        else if (!etStoolTimes.getText().toString().trim().isEmpty() && Integer.valueOf(etStoolTimes.getText().toString()) > 15 && currentPage == 8) {
            etStoolTimes.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.stoolValidation));
        }




        else if(spinnerEnteredByNurse.getSelectedItemPosition()==0&&currentPage==9)
        {
            AppUtils.showToastSort(mActivity, getString(R.string.erroselectNurse));
        }
        else if(currentPage<9)
        {
            currentPage = currentPage+1;
            setViewVisibility(currentPage);
        }
        else
        {
            saveBabyMonitoringData("1");

            if (AppUtils.isNetworkAvailable(mActivity)) {
                SyncBabyRecord.getBabyDataToUpdate(mActivity,AppSettings.getString(AppSettings.babyId));
                finish();
            }
            else
            {
                AppUtils.showToastSort(mActivity, getString(R.string.dataSaved));
                finish();
            }
        }

    }

    private JSONObject createJsonForBabyAssessment() {

        JSONArray jsonArray = new JSONArray();
        try {

            jsonData.put("loungeId" , AppSettings.getString(AppSettings.loungeId));
            jsonData.put("babyId", AppSettings.getString(AppSettings.babyId));
            jsonData.put("staffId", nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()));
            jsonData.put("staffSign", "");
            jsonData.put("localId",uuid);

            jsonData.put("respiratoryRate", AppSettings.getString(AppSettings.respiratoryRate));

            jsonData.put("isThermometerAvailable", thermometerAvailable);
            jsonData.put("temperatureValue","");
            jsonData.put("temperatureUnit","");
            jsonData.put("reasonValue", "");
            jsonData.put("otherValue","");

            if(thermometerAvailable.equalsIgnoreCase(getString(R.string.yesValue)))
            {
                jsonData.put( "temperatureValue", etTemperature.getText().toString().trim());
                jsonData.put("temperatureUnit",tempUnit);
            }
            else
            {
                jsonData.put("reasonValue", spinnerReason.getSelectedItem());
                jsonData.put("otherValue",etReason.getText().toString());
            }

            jsonData.put("isPulseOximatoryDeviceAvail",pulseAvailable);
            jsonData.put("spo2","");
            jsonData.put("pulseRate","");
            jsonData.put("pulseReasonValue", "");
            jsonData.put("pulseOtherValue","");

            if(pulseAvailable.equalsIgnoreCase(getString(R.string.yesValue)))
            {
                jsonData.put("spo2",etSpo2.getText().toString().trim());
                jsonData.put("pulseRate",etPulse.getText().toString().trim());
            }
            else
            {
                jsonData.put("pulseReasonValue", spinnerPulseReason.getSelectedItem());
                jsonData.put("pulseOtherValue",etPulseReason.getText().toString());
            }
            jsonData.put("crtKnowledge",crt);
            jsonData.put("isCrtGreaterThree",crtValue);
            jsonData.put("crtReason","");
            jsonData.put("crtOtherReason","");
            if(crt.equalsIgnoreCase(getString(R.string.yesValue)))
            {
                jsonData.put("crtReason","");
                jsonData.put("crtOtherReason","");
            }
            else
            {
                jsonData.put("crtReason","");
                jsonData.put("crtOtherReason","");
            }



            jsonData.put("tone",tone);
            jsonData.put("alertness",alertness);
            jsonData.put("color",color);
            jsonData.put("apneaGasping",apnea);
            jsonData.put("grunting",grunting);
            jsonData.put("chestIndrawing",chest);
            jsonData.put("interestInFeeding",interest);
            jsonData.put("sufficientLactation",lactation);
            jsonData.put("sucking",sucking);
            jsonData.put("umbilicus",umbilicus);
            jsonData.put("skinPustules",skinRash);
            jsonData.put("bulgingAnteriorFontanel",bulging);
            jsonData.put("localDateTime",AppUtils.currentTimestampFormat());
            jsonData.put("latitude",AppSettings.getString(AppSettings.latitude));
            jsonData.put("longitude",AppSettings.getString(AppSettings.longitude));
            jsonData.put("isBleeding",bleeding);
            jsonData.put("urinePassedIn24Hrs",etUrineTimes.getText().toString().trim());
            jsonData.put("stoolPassedIn24Hrs",etStoolTimes.getText().toString().trim());

            Log.v("doBAdmissionApi", jsonData.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonData;

    }

    private class adapterSpinner extends ArrayAdapter<String> {

        ArrayList<String> data;

        private adapterSpinner(Context context, int textViewResourceId, ArrayList <String> arraySpinner_time) {

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

        private View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            View row=inflater.inflate(R.layout.inflate_spinner_new, parent, false);

            TextView tvName=row.findViewById(R.id.tvName);

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

            holder.tvName.setText(getString(R.string.bullet)
                    +"  "+data.get(position).get("name"));

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

    private void saveBabyMonitoringData(String isDataComplete) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(TableBabyMonitoring.tableColumn.uuid.toString(), uuid);
        contentValues.put(TableBabyMonitoring.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        contentValues.put(TableBabyMonitoring.tableColumn.babyId.toString(), AppSettings.getString(AppSettings.babyId));
        contentValues.put(TableBabyMonitoring.tableColumn.babyAdmissionId.toString(), AppSettings.getString(AppSettings.bAdmId));
        contentValues.put(TableBabyMonitoring.tableColumn.babyRespiratoryRate.toString(), AppSettings.getString(AppSettings.respiratoryRate));
        contentValues.put(TableBabyMonitoring.tableColumn.isPulseOximatoryDeviceAvailable.toString(), pulseAvailable);
        contentValues.put(TableBabyMonitoring.tableColumn.type.toString(), "1");

        contentValues.put(TableBabyMonitoring.tableColumn.crtKnowledge.toString(), crt);
        contentValues.put(TableBabyMonitoring.tableColumn.isDataComplete.toString(), isDataComplete);
        contentValues.put(TableBabyMonitoring.tableColumn.isThermometerAvailable.toString(), thermometerAvailable);

        if (thermometerAvailable.equals(getString(R.string.yesValue))) {
            contentValues.put(TableBabyMonitoring.tableColumn.babyTemperature.toString(), etTemperature.getText().toString().trim());
            contentValues.put(TableBabyMonitoring.tableColumn.temperatureUnit.toString(), tempUnit);
        } else {
            contentValues.put(TableBabyMonitoring.tableColumn.babyTemperature.toString(), "");
            contentValues.put(TableBabyMonitoring.tableColumn.temperatureUnit.toString(), "");
        }

        if (pulseAvailable.equals(getString(R.string.yesValue))) {
            contentValues.put(TableBabyMonitoring.tableColumn.babySpO2.toString(), etSpo2.getText().toString().trim());
            contentValues.put(TableBabyMonitoring.tableColumn.babyPulseRate.toString(), etPulse.getText().toString().trim());
        } else {
            contentValues.put(TableBabyMonitoring.tableColumn.babySpO2.toString(), "");
            contentValues.put(TableBabyMonitoring.tableColumn.babyPulseRate.toString(), "");
        }

        if (crt.equals(getString(R.string.yesValue))) {
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
        contentValues.put(TableBabyMonitoring.tableColumn.staffId.toString(),nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()));
        contentValues.put(TableBabyMonitoring.tableColumn.assessmentNumber.toString(),  DatabaseController.getBabyAssessmentNumber(AppSettings.getString(AppSettings.babyId)));
        contentValues.put(TableBabyMonitoring.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableBabyMonitoring.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableBabyMonitoring.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableBabyMonitoring.tableColumn.formattedDate.toString(), AppUtils.getDateInFormat());
        contentValues.put(TableBabyMonitoring.tableColumn.status.toString(), "1");
        contentValues.put(TableBabyMonitoring.tableColumn.staffSign.toString(), "");
        contentValues.put(TableBabyMonitoring.tableColumn.isDataSynced.toString(), "2");
        contentValues.put(TableBabyMonitoring.tableColumn.json.toString(), createJsonForBabyAssessment().toString());

        DatabaseController.insertUpdateData(contentValues, TableBabyMonitoring.tableName,
                TableBabyMonitoring.tableColumn.uuid.toString(), String.valueOf(uuid));
    }

    //AlertYesNo
    private void AlertYesNo() {
        final Dialog dialog = new Dialog(mActivity,android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_yes_no);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //ImageView
        ImageView ivImage = dialog.findViewById(R.id.ivImage);

        //TextView
        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        TextView tvOk = dialog.findViewById(R.id.tvOk);
        TextView tvCancel = dialog.findViewById(R.id.tvCancel);

        ivImage.setVisibility(View.VISIBLE);
        ivImage.setImageResource(R.drawable.ic_warning);

        //RelativeLayout
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        tvMessage.setText(mActivity.getString(R.string.lastMonitoring));
        tvCancel.setText(mActivity.getString(R.string.startNew));
        tvOk.setText(mActivity.getString(R.string.yes));

        rlCancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                dialog.dismiss();

                String where = TableBabyMonitoring.tableColumn.babyId + " = '" + AppSettings.getString(AppSettings.babyId)
                        +"' and " + TableBabyMonitoring.tableColumn.uuid + " = '" +uuid+ "' ";

                DatabaseController.delete(TableBabyMonitoring.tableName, where,null);

                AppSettings.putString(AppSettings.respiratoryRate,"");

                uuid  = UUID.randomUUID().toString();

                if(chronometer!=null)
                {
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.stop();
                }

                currentPage =1;
                setViewVisibility(currentPage);
            }
        });

        rlOk.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                dialog.dismiss();

                if(chronometer!=null)
                {
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.stop();
                }

                currentPage =1;
                setViewVisibility(currentPage);
                setStepValues();
            }
        });
    }

    public void setStepValues() {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();

        arrayList.addAll(DatabaseController.getLastBabyMonitoring(AppSettings.getString(AppSettings.babyId)));

        for (int i = 0; i < arrayList.size(); i++) {

            thermometerAvailable = arrayList.get(i).get("isThermometerAvailable");

            tempText.clear();
            tempText.add(getString(R.string.degreeF));
            tempText.add(getString(R.string.degreeC));

            tempValue.clear();
            tempValue.add(getString(R.string.degreeFValue));
            tempValue.add(getString(R.string.degreeCValue));

            spinnerTempUnit.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, tempText));

            try {
                if(thermometerAvailable.equals(getString(R.string.yesValue)))
                {
                    ivYes.setImageResource(R.drawable.ic_check_box_selected);
                    ivNo.setImageResource(R.drawable.ic_check_box);
                    etTemperature.setEnabled(true);
                    spinnerTempUnit.setEnabled(true);
                    rlTempe.setVisibility(View.VISIBLE);

                    etTemperature.setText(arrayList.get(i).get("babyTemperature"));

                    if(arrayList.get(i).get("temperatureUnit").equals(getString(R.string.degreeF)))
                    {
                        spinnerTempUnit.setSelection(0);
                    }
                    else
                    {
                        spinnerTempUnit.setSelection(1);
                    }
                }
                else
                {
                    ivYes.setImageResource(R.drawable.ic_check_box);
                    ivNo.setImageResource(R.drawable.ic_check_box_selected);
                    etTemperature.setEnabled(false);
                    spinnerTempUnit.setEnabled(false);
                    rlReason.setVisibility(View.VISIBLE);
                    rlTempe.setVisibility(View.GONE);
                    rltemplay.setVisibility(View.GONE);
                    etTemperature.setText("");
                    spinnerTempUnit.setSelection(0);

                }
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }

            AppSettings.putString(AppSettings.respiratoryRate,arrayList.get(i).get("babyRespiratoryRate"));

            if(!AppSettings.getString(AppSettings.respiratoryRate).isEmpty())
            {
                chronometer.stop();
                tvStart.setText(getString(R.string.start));
                tvRespiRateCount.setVisibility(View.VISIBLE);
                rlPress.setClickable(true);
                press=1;
                timer=0;
                tvRespiRateCount.setText(getString(R.string.respiratoryRate)+": " +AppSettings.getString(AppSettings.respiratoryRate)+" / min");
                tvCounter.setText("0");
                chronometer.setText("00:00");
            }

            pulseAvailable = arrayList.get(i).get("isPulseOximatoryDeviceAvailable");

            try {
                if(pulseAvailable.equals(getString(R.string.yesValue)))
                {
                    ivPulseYes.setImageResource(R.drawable.ic_check_box_selected);
                    ivPulseNo.setImageResource(R.drawable.ic_check_box);
                    etPulse.setText(arrayList.get(i).get("babySpO2"));
                    etSpo2.setText(arrayList.get(i).get("babyPulseRate"));
                    llOximeter.setVisibility(View.VISIBLE);
                    llPulseReason.setVisibility(View.GONE);
                }
                else  if(pulseAvailable.equals(getString(R.string.noValue)))
                {
                    ivPulseNo.setImageResource(R.drawable.ic_check_box_selected);
                    ivPulseYes.setImageResource(R.drawable.ic_check_box);
                    etPulse.setText("");
                    etSpo2.setText("");
                    llOximeter.setVisibility(View.GONE);
                    llPulseReason.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            setDefaultCft();
            setDefaultCftTime();

            crt = arrayList.get(i).get("crtKnowledge");
            crtValue = arrayList.get(i).get("isCftGreaterThree");

            if(crt.equals(getString(R.string.yesValue)))
            {
                crt = getString(R.string.yesValue);
                ivCrtKnowYes.setImageResource(R.drawable.ic_check_box_selected);
                rlCrt.setVisibility(View.VISIBLE);

                if(crtValue.equals(getString(R.string.yesValue)))
                {
                    ivCrtYes.setImageResource(R.drawable.ic_check_box_selected);
                }
                else
                {
                    ivCrtYes.setImageResource(R.drawable.ic_check_box);
                }

            }
            else
            {
                setDefaultCft();
                crt = getString(R.string.noValue);
                ivCrtKnowNo.setImageResource(R.drawable.ic_check_box_selected);
            }

            setDefaultAlert();
            alertness =  arrayList.get(i).get("generalCondition");

            if(alertness.equals(getString(R.string.alertValue)))
            {
                ivAlert.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(alertness.equals(getString(R.string.lethargicValue)))
            {
                ivLethargic.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(alertness.equals(getString(R.string.comatoseValue)))
            {
                ivComatose.setImageResource(R.drawable.ic_check_box_selected);
            }

            setDefaultTone();
            tone =  arrayList.get(i).get("tone");


            if(tone.equals(getString(R.string.activeValue)))
            {
                ivActive.setImageResource(R.drawable.ic_check_box_selected);
            }
            else if(tone.equals(getString(R.string.limpValue)))
            {
                ivLimp.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(tone.equals(getString(R.string.toneValue)))
            {
                ivTone.setImageResource(R.drawable.ic_check_box_selected);
            }

            setDefaultColor();
            color =  arrayList.get(i).get("color");

            if(color.equals(getString(R.string.normalValue)))
            {
                ivoo_color.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(color.equals(getString(R.string.paleValue)))
            {
                ivPale.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(color.equals(getString(R.string.centralCyanosisValue)))
            {
                ivCentral.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(color.equals(getString(R.string.peripheralCyanosisValue)))
            {
                ivPeripheral.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(color.equals(getString(R.string.jaundiceValue)))
            {
                ivJaundice.setImageResource(R.drawable.ic_check_box_selected);
            }

            setDefaultApnea();
            apnea =  arrayList.get(i).get("apneaOrGasping");

            if(apnea.equals(getString(R.string.presentValue)))
            {
                ivApeanPresent.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(apnea.equals(getString(R.string.absentValue)))
            {
                ivApeanAbsent.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(apnea.equals(getString(R.string.unableToConfirmValue)))
            {
                ivApeanUnable.setImageResource(R.drawable.ic_check_box_selected);
            }

            setDefaultGrunting();
            grunting =  arrayList.get(i).get("grunting");

            if(grunting.equals(getString(R.string.presentValue)))
            {
                ivGruntingPresent.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(grunting.equals(getString(R.string.absentValue)))
            {
                ivGruntingAbsent.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(grunting.equals(getString(R.string.unableToConfirmValue)))
            {
                ivGruntingUnable.setImageResource(R.drawable.ic_check_box_selected);
            }


            setDefaultChest();
            chest =  arrayList.get(i).get("chestIndrawing");

            if(chest.equals(getString(R.string.presentValue)))
            {
                ivChestPresent.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(chest.equals(getString(R.string.absentValue)))
            {
                ivChestAbsent.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(chest.equals(getString(R.string.unableToConfirmValue)))
            {
                ivChestUnable.setImageResource(R.drawable.ic_check_box_selected);
            }

            setDefaultInterest();
            interest =  arrayList.get(i).get("isInterestInFeeding");

            if(interest.equals(getString(R.string.yesValue)))
            {
                ivInterestYes.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(interest.equals(getString(R.string.noValue)))
            {
                ivInterestNo.setImageResource(R.drawable.ic_check_box_selected);
            }

            setDefaultLactation();
            lactation =  arrayList.get(i).get("lactation");

            if(lactation.equals(getString(R.string.yesValue)))
            {
                ivSufficientYes.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(lactation.equals(getString(R.string.noValue)))
            {
                ivSufficientNo.setImageResource(R.drawable.ic_check_box_selected);
            }

            setDefaultSucking();
            sucking =  arrayList.get(i).get("sucking");

            if(sucking.equals(getString(R.string.goodValue)))
            {
                ivGood.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(sucking.equals(getString(R.string.poorValue)))
            {
                ivPoor.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(sucking.equals(getString(R.string.unableToConfirmValue)))
            {
                ivNoSucking.setImageResource(R.drawable.ic_check_box_selected);
            }

            setDefaultUmbilicus();
            umbilicus =  arrayList.get(i).get("umbilicus");

            if(umbilicus.equals(getString(R.string.redValue)))
            {
                ivRed.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(umbilicus.equals(getString(R.string.dischargeValue)))
            {
                ivDischarge.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(umbilicus.equals(getString(R.string.normalValue)))
            {
                ivNormal.setImageResource(R.drawable.ic_check_box_selected);
            }

            setDefaultSkinRash();
            skinRash =  arrayList.get(i).get("skinPustules");

            if(skinRash.equals(getString(R.string.noneObservedValue)))
            {
                ivNoneObserved.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(skinRash.equals(getString(R.string.yes_10Value)))
            {
                ivYesLessThen10.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(skinRash.equals(getString(R.string.yesGreater10Value)))
            {
                ivYesGreaterThen10.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(skinRash.equals(getString(R.string.abscessValue)))
            {
                ivAbscess.setImageResource(R.drawable.ic_check_box_selected);
            }

            //complication1="",complication2="",complication3="",complication4="",complication5=""

            setDefaultBulging();
            bulging =  arrayList.get(i).get("bulgingAnteriorFontanel");

            if(bulging.equals(getString(R.string.presentValue)))
            {
                ivPresent.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(bulging.equals(getString(R.string.absentValue)))
            {
                ivAbsent.setImageResource(R.drawable.ic_check_box_selected);
            }

            setDefaultBleeding();
            bleeding =  arrayList.get(i).get("isBleeding");

            if(bleeding.equals(getString(R.string.presentValue)))
            {
                ivBleedingPresent.setImageResource(R.drawable.ic_check_box_selected);
            }
            else  if(bleeding.equals(getString(R.string.absentValue)))
            {
                ivBleedingAbsent.setImageResource(R.drawable.ic_check_box_selected);
            }

            etUrineTimes.setText(arrayList.get(i).get("urinePassedIn24Hrs"));
            etStoolTimes.setText(arrayList.get(i).get("stoolPassedIn24Hrs"));
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {

        AppUtils.AlertCloseActivity(mActivity,getString(R.string.sureToCancel));

    }
}
