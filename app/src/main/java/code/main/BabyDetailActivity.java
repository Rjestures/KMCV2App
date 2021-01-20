package code.main;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.kmcapp.android.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import code.algo.SyncAllRecord;
import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.common.LocaleHelper;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableMotherAdmission;
import code.database.TableMotherRegistration;
import code.infantsFragment.InfantDetailFragment;
import code.infantsFragment.KMCFragment;
import code.infantsFragment.MedVaccFragment;
import code.infantsFragment.NutritionFragment;
import code.infantsFragment.SelectBannerFragment;
import code.infantsFragment.WeightAssessmentFragment;
import code.registration.RegistrationActivity;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseActivity;
import code.view.BaseFragment;

import static code.checkIn.NurseSelfieFragment.MyPREFERENCES;
import static code.infantsFragment.InfantDetailFragment.kmcPostion;


public class BabyDetailActivity extends BaseActivity implements View.OnClickListener {

    //Fragment
    private static BaseFragment fragment;

    //LinearLayout
    private LinearLayout llSync,llHelp,llLogout,llLanguage,llBabyMenu,llKmc,llFeeding,llMedicine,llWeight,
            llDoctorRound,llDischarge,llSibling,llMotherAdmit,llComment,llProfile;

    //RelativeLayout
    private RelativeLayout rlMenu,rlCircle,rlExpandMenu,rlHelp,rlStuck,rlOwn;

    //ImageView
    private ImageView ivPlus;

    //TextView
    private TextView tvHeading,tvName,tvDob,tvDoa,tvBWeight,tvCWeight;

    //ImageView
    private ImageView ivPic, ivStatus;

    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();

    int currentPos =0;

    private ArrayList<String> nurseId = new ArrayList<String>();
    private ArrayList<String> nurseName = new ArrayList<String>();

    String uuid = "";

    //Sync Data
    ArrayList<HashMap<String, String>> weightList = new ArrayList();
    ArrayList<HashMap<String, String>> feedingList = new ArrayList();
    ArrayList<HashMap<String, String>> sscList = new ArrayList();
    ArrayList<HashMap<String, String>> medicationsList = new ArrayList();
    ArrayList<HashMap<String, String>> vaccinationsList = new ArrayList();
    ArrayList<HashMap<String, String>> babyUpdateList = new ArrayList();
    ArrayList<HashMap<String, String>> assessmentList = new ArrayList();
    ArrayList<HashMap<String, String>> commentList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_details);

        findViewById();
    }

    private void findViewById() {

        //TextView
        tvHeading   = findViewById(R.id.tvHeading);
        tvName      = findViewById(R.id.tvName);
        tvDob       = findViewById(R.id.tvDob);
        tvDoa       = findViewById(R.id.tvDoa);
        tvBWeight   = findViewById(R.id.tvBWeight);
        tvCWeight   = findViewById(R.id.tvCWeight);

        //LinearLayout
        llSync      = findViewById(R.id.llSync);
        llHelp      = findViewById(R.id.llHelp);
        llLogout    = findViewById(R.id.llLogout);
        llLanguage    = findViewById(R.id.llLanguage);
        llBabyMenu  = findViewById(R.id.llBabyMenu);
        llKmc       = findViewById(R.id.llKmc);
        llFeeding   = findViewById(R.id.llFeeding);
        llMedicine  = findViewById(R.id.llMedicine);
        llWeight    = findViewById(R.id.llWeight);
        llDoctorRound    = findViewById(R.id.llDoctorRound);
        llDischarge    = findViewById(R.id.llDischarge);
        llSibling   = findViewById(R.id.llSibling);
        llMotherAdmit   = findViewById(R.id.llMotherAdmit);
        llComment   = findViewById(R.id.llComment);
        llProfile   = findViewById(R.id.llProfile);

        //ImageView
        ivPic       = findViewById(R.id.ivPic);
        ivStatus    = findViewById(R.id.ivStatus);
        ivPlus      = findViewById(R.id.ivPlus);

        //RelativeLayout
        rlMenu      = findViewById(R.id.rlMenu);
        rlExpandMenu= findViewById(R.id.rlExpandMenu);
        rlCircle    = findViewById(R.id.rlCircle);
        rlHelp      = findViewById(R.id.rlHelp);
        rlStuck     = findViewById(R.id.rlStuck);
        rlOwn       = findViewById(R.id.rlOwn);

        //setOnClickListener
        llSync.setOnClickListener(this);
        llHelp.setOnClickListener(this);
        llLogout.setOnClickListener(this);
        llLanguage.setOnClickListener(this);
        rlCircle.setOnClickListener(this);
        rlHelp.setOnClickListener(this);
        rlStuck.setOnClickListener(this);
        rlOwn.setOnClickListener(this);
        ivPlus.setOnClickListener(this);
        llKmc.setOnClickListener(this);
        llFeeding.setOnClickListener(this);
        llMedicine.setOnClickListener(this);
        llWeight.setOnClickListener(this);
        llDoctorRound.setOnClickListener(this);
        llDischarge.setOnClickListener(this);
        llSibling.setOnClickListener(this);
        llMotherAdmit.setOnClickListener(this);
        llComment.setOnClickListener(this);
        llProfile.setOnClickListener(this);

        llKmc.setVisibility(View.GONE);
        llFeeding.setVisibility(View.GONE);
        llMedicine.setVisibility(View.GONE);
        llWeight.setVisibility(View.GONE);
        llProfile.setVisibility(View.GONE);

        String type = DatabaseController.getTypeViaMotherId(AppSettings.getString(AppSettings.motherId));

        if(type.equals("1")||type.equals("3"))
        {
            llProfile.setVisibility(View.VISIBLE);
        }

        currentPos=0;
        displayView(0);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(AppSettings.getString(AppSettings.userType).equalsIgnoreCase("0"))
        {
            setValues();
        }
        else
        {
            setInfantsValue();
        }
    }

    private void setInfantsValue() {

        if(AppConstants.hashMap.get("motherName")==null
                   ||AppConstants.hashMap.get("motherName").isEmpty())
        {
            tvName.setText(getString(R.string.babyOf) +" "+getString(R.string.unknown));
        }
        else
        {
            tvName.setText(getString(R.string.babyOf) +" "+AppConstants.hashMap.get("motherName"));
        }

        llMotherAdmit.setVisibility(View.GONE);
        llSibling.setVisibility(View.GONE);
        llDischarge.setVisibility(View.GONE);
        llDoctorRound.setVisibility(View.GONE);

        if(!AppConstants.hashMap.get("babyPhoto").isEmpty())
        {
            if(AppConstants.hashMap.get("babyPhoto").contains("http"))
            {
                Picasso.get().load(AppConstants.hashMap.get("babyPhoto")).into(ivPic);
            }
            else
            {
                try {
                    byte[] decodedString = Base64.decode(AppConstants.hashMap.get("babyPhoto"), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    ivPic.setImageBitmap(decodedByte);

                } catch (Exception e) {
                    ivPic.setImageResource(R.mipmap.baby);
                }
            }
        }else {
            ivPic.setImageResource(R.mipmap.baby);
        }

        String dateOfAdmi = AppConstants.hashMap.get("admissionDate");

        String[] parts = dateOfAdmi.split(" ");

        Log.d("dateOfAdmi",parts[0]);

        tvDob.setText(getString(R.string.dateOfBirth)+" "+AppConstants.hashMap.get("deliveryDate"));
        tvDoa.setText(getString(R.string.dateOfAdmission)+" "+parts[0]);
        tvBWeight.setText(getString(R.string.birthWeight)
                                  +" "+AppConstants.hashMap.get("birthWeight")
                                  +" "+getString(R.string.grams));

        tvCWeight.setText(getString(R.string.currentWeight)
                                  +" "+AppConstants.hashMap.get("currentWeight")
                                  +" "+getString(R.string.grams));


        int checkPulse=0,checkSpo2=0,checkTemp=0,checkResp=0;

        int pulse = 0;
        try {
            pulse = Integer.parseInt(AppConstants.hashMap.get("pulseRate"));
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
            spo2 = Integer.parseInt(AppConstants.hashMap.get("spO2"));

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
            temp = Float.parseFloat(AppConstants.hashMap.get("temperature"));

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
            res = Float.parseFloat(AppConstants.hashMap.get("respiratoryRate"));

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

        if(AppConstants.hashMap.get("isPulseOximatoryDeviceAvailable").equalsIgnoreCase(getString(R.string.yesValue)))
        {
            if(checkPulse==0||checkSpo2==0||checkTemp==0||checkResp==0)
            {
                ivStatus.setImageResource(R.drawable.ic_sad_smily);
            }
            else
            {
                ivStatus.setImageResource(R.drawable.ic_happy_smily);
            }
        }
        else  if(checkTemp==0||checkResp==0)
        {
            ivStatus.setImageResource(R.drawable.ic_sad_smily);
        }
        else
        {
            ivStatus.setImageResource(R.drawable.ic_happy_smily);
        }

    }

    private void setValues() {

        tvHeading.setText(DatabaseController.getLoungeNameData(AppSettings.getString(AppSettings.loungeId)));

        arrayList.clear();
        arrayList.addAll(DatabaseController.getBabyRegistrationDataViaId(AppSettings.getString(AppSettings.babyId)));

        if(arrayList.get(0).get("motherName")==null
                   ||AppConstants.hashMap.get("motherName").isEmpty()) {

            tvName.setText(getString(R.string.babyOf) +" "+getString(R.string.unknown));

        }
        else {

            tvName.setText(getString(R.string.babyOf) +" "+arrayList.get(0).get("motherName"));

        }

        uuid = arrayList.get(0).get("uuid");

        AppSettings.putString(AppSettings.motherId,AppConstants.hashMap.get("motherId"));

        String type = DatabaseController.getTypeViaMotherId(AppSettings.getString(AppSettings.motherId));

        llMotherAdmit.setVisibility(View.GONE);
        llSibling.setVisibility(View.GONE);

        if(type.equals("1"))
        {
            llSibling.setVisibility(View.VISIBLE);
        }
        else  if(type.equals("3"))
        {
            llMotherAdmit.setVisibility(View.VISIBLE);
            llSibling.setVisibility(View.VISIBLE);

            if(DatabaseController.getMotherColumnData(AppSettings.getString(AppSettings.motherId),
                    TableMotherRegistration.tableColumn.reasonForNotAdmitted.toString()).equals(getString(R.string.deadValue)))
            {
                llMotherAdmit.setVisibility(View.GONE);
                llSibling.setVisibility(View.GONE);
            }
        }

        if(!AppConstants.hashMap.get("babyPhoto").isEmpty())
        {
            if(AppConstants.hashMap.get("babyPhoto").contains("http"))
            {
                Picasso.get().load(AppConstants.hashMap.get("babyPhoto")).into(ivPic);
            }
            else
            {
                try {
                    byte[] decodedString = Base64.decode(AppConstants.hashMap.get("babyPhoto"), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    ivPic.setImageBitmap(decodedByte);

                } catch (Exception e) {
                    ivPic.setImageResource(R.mipmap.baby);
                }
            }
        }else {
            ivPic.setImageResource(R.mipmap.baby);
        }

        String dateOfAdmi = arrayList.get(0).get("admissionDate");
        String[] parts = dateOfAdmi.split(" ");
        Log.d("dateOfAdmi",parts[0]);
        tvDob.setText(getString(R.string.dateOfBirth)+" "+arrayList.get(0).get("deliveryDate"));
        tvDoa.setText(getString(R.string.dateOfAdmission)+" "+parts[0]);
        tvBWeight.setText(getString(R.string.birthWeight)
                +" "+arrayList.get(0).get("birthWeight")
                +" "+getString(R.string.grams));

        tvCWeight.setText(getString(R.string.currentWeight)
                +" "+arrayList.get(0).get("currentWeight")
                +" "+getString(R.string.grams));


        int checkPulse=0,checkSpo2=0,checkTemp=0,checkResp=0;

        int pulse = 0;
        try {
            pulse = Integer.parseInt(arrayList.get(0).get("pulseRate"));
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
            spo2 = Integer.parseInt(arrayList.get(0).get("spO2"));

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
            temp = Float.parseFloat(arrayList.get(0).get("temperature"));

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
            res = Float.parseFloat(arrayList.get(0).get("respiratoryRate"));

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

        if(arrayList.get(0).get("isPulseOximatoryDeviceAvailable").equalsIgnoreCase(getString(R.string.yesValue)))
        {
            if(checkPulse==0||checkSpo2==0||checkTemp==0||checkResp==0)
            {
                ivStatus.setImageResource(R.drawable.ic_sad_smily);
            }
            else
            {
                ivStatus.setImageResource(R.drawable.ic_happy_smily);
            }
        }
        else  if(checkTemp==0||checkResp==0)
        {
            ivStatus.setImageResource(R.drawable.ic_sad_smily);
        }
        else
        {
            ivStatus.setImageResource(R.drawable.ic_happy_smily);
        }
    }

    //displayView
    public void  displayView(int position) {

        currentPos = position;
        rlMenu.setVisibility(View.VISIBLE);
        rlExpandMenu.setVisibility(View.GONE);

        switch (position) {

            case 0:

                fragment = new InfantDetailFragment();
                break;

            case 1:

                rlMenu.setVisibility(View.GONE);
                fragment = new KMCFragment();
                break;

            case 2:

                rlMenu.setVisibility(View.GONE);
                fragment = new NutritionFragment();
                break;

            case 3:

                rlMenu.setVisibility(View.GONE);
                fragment = new MedVaccFragment();
                break;

             case 4:

                rlMenu.setVisibility(View.GONE);
                fragment = new WeightAssessmentFragment();
                break;

            case 5:
                SharedPreferences sharedpreferences = mActivity.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("kmcpostion", String.valueOf(kmcPostion));
                editor.commit();

                rlMenu.setVisibility(View.GONE);
                fragment = new SelectBannerFragment();
                break;



            default:
                break;
        }

        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            //fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frameLayout, fragment, "fragment");
            fragmentTransaction.commitAllowingStateLoss();
            //fragmentTransaction.commit();
            getSupportFragmentManager().executePendingTransactions();
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

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

            case R.id.ivPlus:

            case R.id.rlCircle:

                if (rlExpandMenu.getVisibility()==View.GONE)
                {
                    llBabyMenu.setVisibility(View.VISIBLE);
                    Animation aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate45);
                    ivPlus.startAnimation(aniRotateClk);
                    AppUtils.expand(rlExpandMenu);
                }
                else
                {
                    Animation aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate0);
                    ivPlus.startAnimation(aniRotateClk);
                    AppUtils.collapse(rlExpandMenu);
                }

                break;

            case R.id.llKmc:

                Animation aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate0);
                ivPlus.startAnimation(aniRotateClk);
                AppUtils.collapse(rlExpandMenu);
                displayView(1);

                break;

            case R.id.llFeeding:

                aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate0);
                ivPlus.startAnimation(aniRotateClk);
                AppUtils.collapse(rlExpandMenu);
                displayView(2);

                break;

            case R.id.llMedicine:

                aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate0);
                ivPlus.startAnimation(aniRotateClk);
                AppUtils.collapse(rlExpandMenu);
                displayView(3);

                break;

            case R.id.llWeight:

                aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate0);
                ivPlus.startAnimation(aniRotateClk);
                AppUtils.collapse(rlExpandMenu);
                displayView(4);

                break;

            case R.id.llDoctorRound:

                aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate0);
                ivPlus.startAnimation(aniRotateClk);
                AppUtils.collapse(rlExpandMenu);
                AppConstants.treatmentList.clear();
                AppConstants.investigationList.clear();
                startActivity(new Intent(mActivity, DoctorRoundActivity.class));

                break;

             case R.id.llDischarge:

                 aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate0);
                 ivPlus.startAnimation(aniRotateClk);
                 AppUtils.collapse(rlExpandMenu);
                 getBabyList();

                break;

             case R.id.llSibling:

                aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate0);
                ivPlus.startAnimation(aniRotateClk);
                AppUtils.collapse(rlExpandMenu);
                 AppSettings.putString(AppSettings.isSibling,"1");
                 AppSettings.putString(AppSettings.from,"0");
                 startActivity(new Intent(mActivity, RegistrationActivity.class));
                 finish();

                break;

            case R.id.llMotherAdmit:

                aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate0);
                ivPlus.startAnimation(aniRotateClk);
                AppUtils.collapse(rlExpandMenu);
                AlertMotherAdmission();

                break;

            case R.id.llComment:

                aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate0);
                ivPlus.startAnimation(aniRotateClk);
                AppUtils.collapse(rlExpandMenu);
                AppConstants.treatmentList.clear();
                AppConstants.investigationList.clear();
                AppSettings.putString(AppSettings.from,"2");
                startActivity(new Intent(mActivity, CommentActivity.class));

                break;

            case R.id.llProfile:

                aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate0);
                ivPlus.startAnimation(aniRotateClk);
                AppUtils.collapse(rlExpandMenu);
                startActivity(new Intent(mActivity, BasicInfoActivity.class));

                break;

            default:

                break;
        }
    }

    //onBackPressed
    @Override
    public void onBackPressed() {

        try {
            if (currentPos != 0) {
                displayView(0);
            }
            else
            {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject createJsonForCheckList(String nurseId) {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();

        try {

            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("motherId", AppSettings.getString(AppSettings.motherId));
            jsonData.put("staffId", nurseId);
            jsonData.put("localId",uuid);

            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    private void doAdmittApi(String nurseId) {

        WebServices.postApi(mActivity, AppUrls.admitRegisteredMother, createJsonForCheckList(nurseId),true,true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        try {
                            AppSettings.putString(AppSettings.mAdmId,jsonObject.getString("motherAdmissionId"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        AppSettings.putString(AppSettings.motherId,jsonObject.getString("motherId"));

                        ContentValues cvMotherAdm = new ContentValues();

                        cvMotherAdm.put(TableMotherAdmission.tableColumn.serverId.toString(), AppSettings.getString(AppSettings.mAdmId));
                        cvMotherAdm.put(TableMotherAdmission.tableColumn.motherId.toString(), AppSettings.getString(AppSettings.motherId));
                        cvMotherAdm.put(TableMotherAdmission.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
                        cvMotherAdm.put(TableMotherAdmission.tableColumn.status.toString(), "1");

                        DatabaseController.insertUpdateData(cvMotherAdm, TableMotherAdmission.tableName,
                                TableMotherAdmission.tableColumn.motherId.toString(), AppSettings.getString(AppSettings.motherId));

                        ContentValues cvMotherReg = new ContentValues();

                        cvMotherReg.put(TableMotherRegistration.tableColumn.motherId.toString(), AppSettings.getString(AppSettings.motherId));
                        cvMotherReg.put(TableMotherRegistration.tableColumn.isMotherAdmitted.toString(),getString(R.string.yesValue));
                        cvMotherReg.put(TableMotherRegistration.tableColumn.type.toString(),"1");

                        DatabaseController.insertUpdateData(cvMotherReg, TableMotherRegistration.tableName,
                                TableMotherRegistration.tableColumn.motherId.toString(),AppSettings.getString(AppSettings.motherId));

                        AppUtils.showToastSort(mActivity, getString(R.string.motherAdmitted));

                        setValues();

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

    //Alert Mother Admission
    private void AlertMotherAdmission() {
        final Dialog dialog = new Dialog(mActivity,android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        dialog.setContentView(R.layout.alert_mother_admission);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //TextView
        TextView tvSubmit = dialog.findViewById(R.id.tvSubmit);

        //ImageView
        ImageView ivImage = dialog.findViewById(R.id.ivImage);

        //Spinner
        Spinner spinnerEnteredByNurse = dialog.findViewById(R.id.spinnerEnteredByNurse);

        //RelativeLayout
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);
        RelativeLayout rlReason = dialog.findViewById(R.id.rlReason);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        nurseId.clear();
        nurseId.add(getString(R.string.selectNurse));
        nurseId.addAll(DatabaseController.getNurseIdCheckedInData());

        nurseName.clear();
        nurseName.add(getString(R.string.selectNurse));
        nurseName.addAll(DatabaseController.getNurseNameCheckedInData());

        spinnerEnteredByNurse.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, nurseName));
        spinnerEnteredByNurse.setSelection(0);

        ivImage.setVisibility(View.VISIBLE);
        ivImage.setImageResource(R.drawable.ic_new_admission);

        rlCancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        rlOk.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                rlReason.setVisibility(View.VISIBLE);
                tvSubmit.setVisibility(View.VISIBLE);
            }
        });

        tvSubmit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(spinnerEnteredByNurse.getSelectedItemPosition()==0)
                {
                    AppUtils.showToastSort(mActivity,getString(R.string.selectNurse));
                }
                else
                {
                    dialog.dismiss();
                    if (AppUtils.isNetworkAvailable(mActivity)) {
                        doAdmittApi(nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()));
                    } else {
                        AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                    }
                }

            }
        });
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


    private void getBabyList() {

        getBabyDataToUpdate();
        getBabyWeightDataToUpdate();
        getBabyAssessDataToUpdate();
        getBabyKMCDataToUpdate();
        getBabyFeedingDataToUpdate();
        getMedicationDataToUpdate();
        getVaccinationDataToUpdate();
        getCommentDataToUpdate();

        Log.v("kjhgwgj", String.valueOf(commentList.size()));

        if(weightList.size()>0||
                   feedingList.size()>0||
                   sscList.size()>0||
                   medicationsList.size()>0||
                   vaccinationsList.size()>0||
                   assessmentList.size()>0||
                   babyUpdateList.size()>0||
                   commentList.size()>0)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.dischargeError));
        }
        else
        {
            startActivity(new Intent(mActivity, BabyDischargeActivity.class));
        }
    }

    private void getBabyDataToUpdate() {

        babyUpdateList.clear();
        babyUpdateList.addAll(DatabaseController.getBabyIdToSyncUpdates(AppSettings.getString(AppSettings.babyId),AppSettings.getString(AppSettings.loungeId)));

    }

    private void getBabyWeightDataToUpdate() {

        weightList.clear();
        weightList.addAll(DatabaseController.getWeightToSync(AppSettings.getString(AppSettings.babyId),AppSettings.getString(AppSettings.loungeId)));

    }

    private void getBabyAssessDataToUpdate() {

        assessmentList.clear();
        assessmentList.addAll(DatabaseController.getBabyAssessToSync(AppSettings.getString(AppSettings.babyId),AppSettings.getString(AppSettings.loungeId)));
    }

    private void getBabyKMCDataToUpdate() {

        sscList.clear();
        sscList.addAll(DatabaseController.getSSCDateToSync(AppSettings.getString(AppSettings.babyId),AppSettings.getString(AppSettings.loungeId)));
    }

    private void getBabyFeedingDataToUpdate() {

        feedingList.clear();
        feedingList.addAll(DatabaseController.getFeedingDataToSync(AppSettings.getString(AppSettings.babyId),AppSettings.getString(AppSettings.loungeId)));

    }

    private void getMedicationDataToUpdate() {

        medicationsList.clear();
        medicationsList.addAll(DatabaseController.getMedicationDataToSync(AppSettings.getString(AppSettings.babyId),AppSettings.getString(AppSettings.loungeId)));
    }

    private void getVaccinationDataToUpdate() {

        vaccinationsList.clear();
        vaccinationsList.addAll(DatabaseController.getVaccinationDataToSync(AppSettings.getString(AppSettings.babyId),AppSettings.getString(AppSettings.loungeId)));

    }

    private void getCommentDataToUpdate() {

        commentList.clear();
        commentList.addAll(DatabaseController.getCommentDataToSync(AppSettings.getString(AppSettings.babyId),AppSettings.getString(AppSettings.loungeId),"2"));
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

}
