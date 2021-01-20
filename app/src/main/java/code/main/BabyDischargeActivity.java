package code.main;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;

import com.kmcapp.android.R;

import java.util.ArrayList;
import java.util.HashMap;

import code.algo.SyncAllRecord;
import code.common.LocaleHelper;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.discharge.AbscondedDischargeFragment;
import code.discharge.DiedDischargeFragment;
import code.discharge.DischargeFindingsFragment;
import code.discharge.DoctorDischargeFragment;
import code.discharge.DoprDischargeFragment;
import code.discharge.LamaDischargeFragment;
import code.discharge.NormalDischargeFragment;
import code.discharge.ReferralDischargeFragment;
import code.utils.AppUtils;
import code.view.BaseActivity;
import code.view.BaseFragment;

public class BabyDischargeActivity extends BaseActivity implements View.OnClickListener {

    //Fragment
    private static BaseFragment fragment;

    //LinearLayout
    private LinearLayout llSync,llHelp,llLogout,llLanguage;

    //RelativeLayout
    private RelativeLayout rlHelp,rlStuck,rlOwn,rlMain;

    //TextView
    private TextView tvHeading,tvName,tvDob,tvDoa,tvBWeight,tvCWeight;

    //ImageView
    private ImageView ivPic, ivStatus;

    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();

    int currentPos =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_discharge);

        findViewById();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
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

        //ImageView
        ivPic       = findViewById(R.id.ivPic);
        ivStatus    = findViewById(R.id.ivStatus);

        //RelativeLayout
        rlHelp      = findViewById(R.id.rlHelp);
        rlStuck     = findViewById(R.id.rlStuck);
        rlOwn       = findViewById(R.id.rlOwn);
        rlMain       = findViewById(R.id.rlMain);

        setValues();

        //setOnClickListener
        llSync.setOnClickListener(this);
        llHelp.setOnClickListener(this);
        llLogout.setOnClickListener(this);
        llLanguage.setOnClickListener(this);
        rlHelp.setOnClickListener(this);
        rlStuck.setOnClickListener(this);
        rlOwn.setOnClickListener(this);

        displayView(0);
    }

    private void setValues() {

        tvHeading.setText(DatabaseController.getLoungeNameData(AppSettings.getString(AppSettings.loungeId)));

        arrayList.clear();
        arrayList.addAll(DatabaseController.getBabyRegistrationDataViaId(AppSettings.getString(AppSettings.babyId)));

        if(arrayList.get(0).get("motherName")==null)
        {
            tvName.setText(getString(R.string.babyOf) +" "+getString(R.string.unknown));
        }
        else
        {
            tvName.setText(getString(R.string.babyOf) +" "+arrayList.get(0).get("motherName"));
        }

        try {

            byte[] decodedString = Base64.decode(arrayList.get(0).get("babyPhoto"), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            ivPic.setImageBitmap(decodedByte);

            //Picasso.get().load(arrayList.get(0).get("babyPhoto")).into(ivPic);
        } catch (Exception e) {
            //e.printStackTrace();
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
        rlMain.setVisibility(View.GONE);

        switch (position) {

            case 0:

                rlMain.setVisibility(View.VISIBLE);
                fragment = new DischargeFindingsFragment();
                break;

            case 1:

                fragment = new NormalDischargeFragment();
                break;

            case 2:

                fragment = new ReferralDischargeFragment();
                break;

            case 3:

                fragment = new LamaDischargeFragment();
                break;

            case 4:

                fragment = new DoprDischargeFragment();
                break;

            case 5:

                fragment = new DoctorDischargeFragment();
                break;

            case 6:

                fragment = new AbscondedDischargeFragment();
                break;

            case 7:

                fragment = new DiedDischargeFragment();
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

            default:

                break;
        }
    }

    //onBackPressed
    @Override
    public void onBackPressed() {

        AppUtils.AlertCloseActivity(mActivity,getString(R.string.sureToCancel));

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
