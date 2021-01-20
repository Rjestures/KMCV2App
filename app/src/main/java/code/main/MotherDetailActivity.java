package code.main;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
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

import java.util.ArrayList;
import java.util.HashMap;

import code.algo.SyncAllRecord;
import code.common.LocaleHelper;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.motherDischarge.discharge.MotherDischargeFindingsFragment;
import code.mothersFragment.MotherDetailFragment;
import code.utils.AppConstants;
import code.utils.AppUtils;
import code.view.BaseActivity;
import code.view.BaseFragment;


public class MotherDetailActivity extends BaseActivity implements View.OnClickListener {

    //Fragment
    private static BaseFragment fragment;

    //LinearLayout
    private LinearLayout llSync,llHelp,llLogout,llLanguage,llComment,llAssessment,llDischarge;

    //RelativeLayout
    private RelativeLayout rlCircle,rlHelp,rlStuck,rlOwn,rlExpandMenu;

    //ImageView
    private ImageView ivPlus;

    //TextView
    private TextView tvHeading,tvName,tvLastAssessment;

    //ImageView
    private ImageView ivPic;

    //Mother Section
    private static ArrayList<String> dischargeTypeList = new ArrayList<String>();
    private static ArrayList<String> dischargeTypeListValue = new ArrayList<String>();
    int checkTmp = 0, checkHeartBeat = 0, checkSystolic = 0, checkDiastolic = 0;

    int currentPos =0;

    ArrayList<HashMap<String, String>> motherUpdateList = new ArrayList();
    ArrayList<HashMap<String, String>> mAssessmentList = new ArrayList();
    ArrayList<HashMap<String, String>> commentList = new ArrayList();
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();


    ImageView ivStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mother_details);

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
        tvLastAssessment      = findViewById(R.id.tvLastAssessment);

        //LinearLayout
        llSync      = findViewById(R.id.llSync);
        llHelp      = findViewById(R.id.llHelp);
        llLogout    = findViewById(R.id.llLogout);
        llLanguage    = findViewById(R.id.llLanguage);
        llComment= findViewById(R.id.llComment);
        llAssessment= findViewById(R.id.llAssessment);
        llDischarge= findViewById(R.id.llDischarge);
        ivStatus= findViewById(R.id.ivStatus);

        //ImageView
        ivPic       = findViewById(R.id.ivPic);
        ivPlus      = findViewById(R.id.ivPlus);

        //RelativeLayout
        rlCircle    = findViewById(R.id.rlCircle);
        rlHelp      = findViewById(R.id.rlHelp);
        rlStuck     = findViewById(R.id.rlStuck);
        rlOwn       = findViewById(R.id.rlOwn);
        rlExpandMenu       = findViewById(R.id.rlExpandMenu);

        //setOnClickListener
        llSync.setOnClickListener(this);
        llHelp.setOnClickListener(this);
        llLogout.setOnClickListener(this);
        llLanguage.setOnClickListener(this);
        llComment.setOnClickListener(this);
        llAssessment.setOnClickListener(this);
        llDischarge.setOnClickListener(this);
        rlCircle.setOnClickListener(this);
        rlHelp.setOnClickListener(this);
        rlStuck.setOnClickListener(this);
        rlOwn.setOnClickListener(this);
        ivPlus.setOnClickListener(this);

        displayView(0);

        if(AppSettings.getString(AppSettings.userType).equalsIgnoreCase("1")) {
            llDischarge.setVisibility(View.GONE);
            llAssessment.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setValues();
    }

    private void setValues() {

        tvHeading.setText(DatabaseController.getLoungeNameData(AppSettings.getString(AppSettings.loungeId)));

        tvLastAssessment.setText(getString(R.string.lastAssessment)+ ": " +getString(R.string.notApplicableShortValue));

        if(!AppSettings.getString(AppSettings.lastAssessmentDate).isEmpty())
        {
            tvLastAssessment.setText(getString(R.string.lastAssessment)+ ": " +AppUtils.convertDateTimeTo12HoursFormat(AppSettings.getString(AppSettings.lastAssessmentDate)));
        }

        tvName.setText(AppSettings.getString(AppSettings.motherName));

        if(!AppSettings.getString(AppSettings.motherPhoto).isEmpty()) {

            if (AppSettings.getString(AppSettings.motherPhoto).contains("http")) {
                Picasso.get().load(AppSettings.getString(AppSettings.motherPhoto)).into(ivPic);
            } else {
                try {
                    byte[] decodedString = Base64.decode(AppSettings.getString(AppSettings.motherPhoto), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    ivPic.setImageBitmap(decodedByte);
                    //Picasso.get().load(arrayList.get(0).get("babyPhoto")).into(ivPic);
                } catch (Exception e) {
                    //e.printStackTrace();
                    ivPic.setImageResource(R.mipmap.mother);
                }
            }
        }else {
            ivPic.setImageResource(R.mipmap.mother);
        }


        arrayList.clear();
        arrayList.addAll(DatabaseController.getMotherMonitoringDataViaId(AppSettings.getString(AppSettings.motherId)));


//
//        try {
//            float temp= Float.parseFloat(arrayList.get(0).get("motherTemperature"));
//            if (temp < 95.9 || temp > 99.5) {
//                ivStatus.setImageResource(R.drawable.ic_sad_smily);
//            }
//            else
//            {
//               ivStatus.setImageResource(R.drawable.ic_happy_smily);
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }

        try {
            float temp= Float.parseFloat(arrayList.get(0).get("motherTemperature"));
            if (temp < 95.9 || temp > 99.5) {
                checkTmp=0;
            }
            else
            {
                checkTmp=1;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            float bpr= Float.parseFloat(arrayList.get(0).get("motherPulse"));
            if (bpr <  50 || bpr > 120) {
                checkHeartBeat=0;
            }
            else
            {
                checkHeartBeat=1;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            float msp= Float.parseFloat(arrayList.get(0).get("motherSystolicBP"));
            if (msp <=  90 || msp >= 140) {
                checkSystolic = 0;
            }
            else {
                checkSystolic = 1;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            float dsp= Float.parseFloat(arrayList.get(0).get("motherDiastolicBP"));
            if (dsp <=  60 || dsp >= 90) {
                checkDiastolic = 0;
            }
            else {
                checkDiastolic = 1;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (checkTmp == 0 || checkDiastolic == 0 || checkSystolic == 0 || checkHeartBeat == 0) {
            ivStatus.setImageResource(R.drawable.ic_sad_smily);
        } else {
            ivStatus.setImageResource(R.drawable.ic_happy_smily);
        }

    }

    //displayView
    public void  displayView(int position) {

        currentPos = position;

        switch (position) {

            case 0:

                fragment = new MotherDetailFragment();
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

            case R.id.llComment:

                Animation aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate0);
                ivPlus.startAnimation(aniRotateClk);
                AppUtils.collapse(rlExpandMenu);
                AppSettings.putString(AppSettings.from,"1");
                startActivity(new Intent(mActivity, CommentActivity.class));

                break;

            case R.id.llAssessment:

                aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate0);
                ivPlus.startAnimation(aniRotateClk);
                AppUtils.collapse(rlExpandMenu);
                startActivity(new Intent(mActivity, MotherAssessmentActivity.class));

                break;

            case R.id.llDischarge:

                aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate0);
                ivPlus.startAnimation(aniRotateClk);
                AppUtils.collapse(rlExpandMenu);
                getData();

                break;

            default:

                break;
        }
    }

    //onBackPressed
    @Override
    public void onBackPressed() {
       finish();
    }


    //Alert For DischargeType
    private void AlertDischargeType() {
        final Dialog dialog = new Dialog(mActivity,android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        dialog.setContentView(R.layout.alert_discharge_type);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //TextView
        TextView tvSubmit = dialog.findViewById(R.id.tvSubmit);
        TextView tvMessage = dialog.findViewById(R.id.tvMessage);

        //ImageView
        ImageView ivImage = dialog.findViewById(R.id.ivImage);

        //Spinner
        Spinner spinnerDischarge = dialog.findViewById(R.id.spinnerDischarge);

        //RelativeLayout
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);
        RelativeLayout rlDischarge = dialog.findViewById(R.id.rlDischarge);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        dischargeTypeList.clear();
        dischargeTypeList.add(getString(R.string.reason));
        dischargeTypeList.add(getString(R.string.discharge7));
        dischargeTypeList.add(getString(R.string.discharge1));
        dischargeTypeList.add(getString(R.string.discharge2));
        dischargeTypeList.add(getString(R.string.discharge3));
        dischargeTypeList.add(getString(R.string.discharge4));
        dischargeTypeList.add(getString(R.string.discharge5));
        dischargeTypeList.add(getString(R.string.discharge6));

        dischargeTypeListValue.clear();
        dischargeTypeListValue.add(getString(R.string.reason));
        dischargeTypeListValue.add(getString(R.string.normalValue));
        dischargeTypeListValue.add(getString(R.string.discharge1Value));
        dischargeTypeListValue.add(getString(R.string.discharge2Value));
        dischargeTypeListValue.add(getString(R.string.discharge3Value));
        dischargeTypeListValue.add(getString(R.string.discharge4Value));
        dischargeTypeListValue.add(getString(R.string.discharge5Value));
        dischargeTypeListValue.add(getString(R.string.discharge6Value));

        spinnerDischarge.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, dischargeTypeList));
        spinnerDischarge.setSelection(0);

        //Spinner for Discharge
        spinnerDischarge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        ivImage.setVisibility(View.VISIBLE);
        ivImage.setImageResource(R.drawable.ic_warning);
        tvMessage.setText(R.string.sureToDischargeMother);

        rlCancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        rlOk.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                rlDischarge.setVisibility(View.VISIBLE);
                tvSubmit.setVisibility(View.VISIBLE);

            }
        });

        tvSubmit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(spinnerDischarge.getSelectedItemPosition()==0)
                {
                    AppUtils.showToastSort(mActivity,getString(R.string.errorSelectReason));
                }
                else if(spinnerDischarge.getSelectedItemPosition()==1)
                {
                    dialog.dismiss();
                    AppSettings.putString(AppSettings.dischargeCondtion,dischargeTypeListValue.get(spinnerDischarge.getSelectedItemPosition()));

                    startActivity(new Intent(mActivity, MotherDischargeActivity.class));

                }
                else if(spinnerDischarge.getSelectedItemPosition()==2)
                {
                    dialog.dismiss();
                    AppSettings.putString(AppSettings.dischargeCondtion,dischargeTypeListValue.get(spinnerDischarge.getSelectedItemPosition()));

                    startActivity(new Intent(mActivity, MotherDischargeActivity.class));

                }
                else if(spinnerDischarge.getSelectedItemPosition()==3)
                {
                    dialog.dismiss();
                    AppSettings.putString(AppSettings.dischargeCondtion,dischargeTypeListValue.get(spinnerDischarge.getSelectedItemPosition()));

                    startActivity(new Intent(mActivity, MotherDischargeActivity.class));

                }
                else if(spinnerDischarge.getSelectedItemPosition()==4)
                {
                    dialog.dismiss();
                    AppSettings.putString(AppSettings.dischargeCondtion,dischargeTypeListValue.get(spinnerDischarge.getSelectedItemPosition()));

                    startActivity(new Intent(mActivity, MotherDischargeActivity.class));

                }
                else if(spinnerDischarge.getSelectedItemPosition()==5)
                {
                    dialog.dismiss();
                    AppSettings.putString(AppSettings.dischargeCondtion,dischargeTypeListValue.get(spinnerDischarge.getSelectedItemPosition()));

                    startActivity(new Intent(mActivity, MotherDischargeActivity.class));

                }
                else if(spinnerDischarge.getSelectedItemPosition()==6)
                {
                    dialog.dismiss();
                    AppSettings.putString(AppSettings.dischargeCondtion,dischargeTypeListValue.get(spinnerDischarge.getSelectedItemPosition()));

                    startActivity(new Intent(mActivity, MotherDischargeActivity.class));

                }
                else
                {
                    dialog.dismiss();
                    AppSettings.putString(AppSettings.dischargeCondtion,dischargeTypeListValue.get(spinnerDischarge.getSelectedItemPosition()));

                    startActivity(new Intent(mActivity, MotherDischargeActivity.class));

                }

            }
        });
    }

    private static class adapterSpinner extends ArrayAdapter<String> {

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

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            View row=inflater.inflate(R.layout.inflate_spinner_new, parent, false);

            TextView tvName=row.findViewById(R.id.tvName);

            tvName.setText(data.get(position));

            return row;
        }
    }

    private void getData() {

        getMotherDataToUpdate();
        getMotherAssessData();
        getCommentDataToUpdate();

        if(motherUpdateList.size()>0||
                   mAssessmentList.size()>0||
                   commentList.size()>0)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.dischargeError));
        }
        else
        {
            AlertDischargeType();
        }
    }

    private void getMotherDataToUpdate() {

        motherUpdateList.clear();
        motherUpdateList.addAll(DatabaseController.getMotherIdToSyncUpdates(AppSettings.getString(AppSettings.motherId),AppSettings.getString(AppSettings.loungeId)));

    }

    private void getMotherAssessData() {

        mAssessmentList.clear();
        mAssessmentList.addAll(DatabaseController.getMotherIdToSyncUpdates(AppSettings.getString(AppSettings.motherId),AppSettings.getString(AppSettings.loungeId)));

    }

    private void getCommentDataToUpdate() {

        commentList.clear();
        commentList.addAll(DatabaseController.getCommentDataToSync(AppSettings.getString(AppSettings.motherId),AppSettings.getString(AppSettings.loungeId),"1"));
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
