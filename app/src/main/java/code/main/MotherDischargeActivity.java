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
import code.motherDischarge.discharge.MotherAbscondedDischargeFragment;
import code.motherDischarge.discharge.MotherDiedDischargeFragment;
import code.motherDischarge.discharge.MotherDischargeFindingsFragment;
import code.motherDischarge.discharge.MotherDoctorDischargeFragment;
import code.motherDischarge.discharge.MotherDoprDischargeFragment;
import code.motherDischarge.discharge.MotherLamaDischargeFragment;
import code.motherDischarge.discharge.MotherNormalDischargeFragment;
import code.motherDischarge.discharge.MotherReferralDischargeFragment;
import code.utils.AppUtils;
import code.view.BaseActivity;
import code.view.BaseFragment;

public class MotherDischargeActivity extends BaseActivity implements View.OnClickListener {

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

        if(AppSettings.getString(AppSettings.dischargeCondtion).equalsIgnoreCase(getString(R.string.normal)))
        {
            displayView(1);
        }
        else if(AppSettings.getString(AppSettings.dischargeCondtion).equalsIgnoreCase(getString(R.string.discharge1Value)))
        {
            displayView(2);
        }
        else if(AppSettings.getString(AppSettings.dischargeCondtion).equalsIgnoreCase(getString(R.string.discharge2Value)))
        {
            displayView(3);
        }
        else if(AppSettings.getString(AppSettings.dischargeCondtion).equalsIgnoreCase(getString(R.string.discharge3Value)))
        {
            displayView(4);
        }
        else if(AppSettings.getString(AppSettings.dischargeCondtion).equalsIgnoreCase(getString(R.string.discharge4Value)))
        {
            displayView(5);
        }
        else if(AppSettings.getString(AppSettings.dischargeCondtion).equalsIgnoreCase(getString(R.string.discharge5Value)))
        {
            displayView(6);
        }
        else if(AppSettings.getString(AppSettings.dischargeCondtion).equalsIgnoreCase(getString(R.string.discharge6Value)))
        {
            displayView(7);
        }
    }

    private void setValues() {

        rlMain.setVisibility(View.GONE);

        tvHeading.setText(DatabaseController.getLoungeNameData(AppSettings.getString(AppSettings.loungeId)));

    }

    //displayView
    public void  displayView(int position) {

        currentPos = position;

        switch (position) {

            case 0:

                fragment = new MotherDischargeFindingsFragment();
                break;

            case 1:

                fragment = new MotherNormalDischargeFragment();
                break;

            case 2:

                fragment = new MotherReferralDischargeFragment();
                break;

            case 3:

                fragment = new MotherLamaDischargeFragment();
                break;

            case 4:

                fragment = new MotherDoprDischargeFragment();
                break;

            case 5:

                fragment = new MotherDoctorDischargeFragment();
                break;

            case 6:

                fragment = new MotherAbscondedDischargeFragment();
                break;

            case 7:

                fragment = new MotherDiedDischargeFragment();
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

                SyncAllRecord.postDutyChange(mActivity);

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
