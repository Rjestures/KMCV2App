package code.main;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.kmcapp.android.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import code.algo.SyncAllRecord;
import code.common.LocaleHelper;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.utils.AppConstants;
import code.utils.AppUtils;
import code.view.BaseActivity;

public class InvestigationAddActivity extends BaseActivity implements View.OnClickListener {

    //TextView
    TextView tvName,tvSubmit;

    //EditText
    EditText etComment;

    //Spinner
    Spinner spinnerInvestigationType,spinnerInvestigation;

    ArrayList<String> investigationType = new ArrayList<String>();
    ArrayList<String> investigationTypeValue = new ArrayList<String>();
    ArrayList<String> investigationNameValue = new ArrayList<String>();
    ArrayList<String> investigationName = new ArrayList<String>();

    //LinearLayout
    private LinearLayout llSync,llHelp,llLogout,llLanguage;

    //RelativeLayout
    private RelativeLayout rlHelp,rlStuck,rlOwn;

    String uuid  = UUID.randomUUID().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investigation_add);

        findViewById();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void findViewById() {

        //TextView
        tvSubmit  = findViewById(R.id.tvSubmit);

        //Edittext
        etComment= findViewById(R.id.etComment);

        //LinearLayout
        llSync      = findViewById(R.id.llSync);
        llHelp      = findViewById(R.id.llHelp);
        llLogout = findViewById(R.id.llLogout);
        llLanguage = findViewById(R.id.llLanguage);

        //RelativeLayout
        rlHelp= findViewById(R.id.rlHelp);
        rlStuck= findViewById(R.id.rlStuck);
        rlOwn= findViewById(R.id.rlOwn);

        //Spinner
        spinnerInvestigation= findViewById(R.id.spinner);
        spinnerInvestigationType= findViewById(R.id.spinnerInvestigationType);

        investigationType.clear();
        investigationType.add(getString(R.string.selectInvestigationType));
        investigationType.add(getString(R.string.bloodnvestigations));
        investigationType.add(getString(R.string.serum));
        investigationType.add(getString(R.string.imaging));
        investigationType.add(getString(R.string.miscellaneousTests));

        investigationTypeValue.clear();
        investigationTypeValue.add(getString(R.string.selectInvestigationType));
        investigationTypeValue.add(getString(R.string.bloodnvestigationsValue));
        investigationTypeValue.add(getString(R.string.serumValue));
        investigationTypeValue.add(getString(R.string.imagingValue));
        investigationTypeValue.add(getString(R.string.miscellaneousTestsValue));

        /*investigationName.clear();
        investigationName.add(getString(R.string.selectInvestigation));
        investigationName.add(getString(R.string.hb));


        investigationName.add(getString(R.string.urineRnM));
        investigationName.add(getString(R.string.stoolForOccultBlood));
        investigationName.add(getString(R.string.bloodGas));
        investigationName.add(getString(R.string.csf));
        investigationName.add(getString(R.string.urineCulture));
        investigationName.add(getString(R.string.bloodCulture));
        investigationName.add(getString(R.string.xRay));
        investigationName.add(getString(R.string.usg));
        investigationName.add(getString(R.string.ctMRI));
        investigationName.add(getString(R.string.bloodSugar));
        investigationName.add(getString(R.string.bloodCollection));
        investigationName.add(getString(R.string.other));


        investigationNameValue.clear();
        investigationNameValue.add(getString(R.string.selectInvestigation));



        investigationNameValue.add(getString(R.string.xRayValue));
        investigationNameValue.add(getString(R.string.usgValue));
        investigationNameValue.add(getString(R.string.ctMRIValue));
        investigationNameValue.add(getString(R.string.bloodSugarValue));
        investigationNameValue.add(getString(R.string.otherValue));*/

        spinnerInvestigationType.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, investigationType));
        spinnerInvestigationType.setSelection(0);

        spinnerInvestigation.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, investigationName));
        spinnerInvestigation.setSelection(0);

        //Spinner for Type
        spinnerInvestigationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                    if(position==0)
                    {
                        spinnerInvestigation.setSelection(0);
                    }
                    else  if(position==1)
                    {
                        investigationName.clear();
                        investigationName.add(getString(R.string.selectInvestigation));
                        investigationName.add(getString(R.string.hbValue));
                        investigationName.add(getString(R.string.pcv));
                        investigationName.add(getString(R.string.wbc));
                        investigationName.add(getString(R.string.diffWbc));
                        investigationName.add(getString(R.string.bandForm));
                        investigationName.add(getString(R.string.plateletCount));
                        investigationName.add(getString(R.string.periSmear));
                        investigationName.add(getString(R.string.pt));
                        investigationName.add(getString(R.string.crp));

                        investigationNameValue.clear();
                        investigationNameValue.add(getString(R.string.selectInvestigation));
                        investigationNameValue.add(getString(R.string.hbValue));
                        investigationNameValue.add(getString(R.string.pcvValue));
                        investigationNameValue.add(getString(R.string.wbcValue));
                        investigationNameValue.add(getString(R.string.diffWbcValue));
                        investigationNameValue.add(getString(R.string.bandFormValue));
                        investigationNameValue.add(getString(R.string.plateletCountValue));
                        investigationNameValue.add(getString(R.string.periSmearValue));
                        investigationNameValue.add(getString(R.string.ptValue));
                        investigationNameValue.add(getString(R.string.crpValue));
                    }
                    else  if(position==2)
                    {
                        investigationName.clear();
                        investigationName.add(getString(R.string.selectInvestigation));
                        investigationName.add(getString(R.string.rbg));
                        investigationName.add(getString(R.string.bun));
                        investigationName.add(getString(R.string.serumCreatioine));
                        investigationName.add(getString(R.string.serumCalcium));
                        investigationName.add(getString(R.string.serumSodium));
                        investigationName.add(getString(R.string.serumPotassium));
                        investigationName.add(getString(R.string.serumBilirubin));
                        investigationName.add(getString(R.string.sgpt));
                        investigationName.add(getString(R.string.totalProtein));

                        investigationNameValue.clear();
                        investigationNameValue.add(getString(R.string.selectInvestigation));
                        investigationNameValue.add(getString(R.string.rbgValue));
                        investigationNameValue.add(getString(R.string.bunValue));
                        investigationNameValue.add(getString(R.string.serumCreatioineValue));
                        investigationNameValue.add(getString(R.string.serumCalciumValue));
                        investigationNameValue.add(getString(R.string.serumSodiumValue));
                        investigationNameValue.add(getString(R.string.serumPotassiumValue));
                        investigationNameValue.add(getString(R.string.serumBilirubinValue));
                        investigationNameValue.add(getString(R.string.sgptValue));
                        investigationNameValue.add(getString(R.string.totalProteinValue));
                    }
                    else  if(position==3)
                    {
                        investigationName.clear();
                        investigationName.add(getString(R.string.selectInvestigation));
                        investigationName.add(getString(R.string.xRay));
                        investigationName.add(getString(R.string.usg));
                        investigationName.add(getString(R.string.ctMRI));

                        investigationNameValue.clear();
                        investigationNameValue.add(getString(R.string.selectInvestigation));
                        investigationNameValue.add(getString(R.string.xRayValue));
                        investigationNameValue.add(getString(R.string.usgValue));
                        investigationNameValue.add(getString(R.string.ctMRIValue));
                    }
                    else  if(position==4)
                    {
                        investigationName.clear();
                        investigationName.add(getString(R.string.selectInvestigation));
                        investigationName.add(getString(R.string.urineRnM));
                        investigationName.add(getString(R.string.stoolForOccultBlood));
                        investigationName.add(getString(R.string.csf));
                        investigationName.add(getString(R.string.bloodCollection));
                        investigationName.add(getString(R.string.other));

                        investigationNameValue.clear();
                        investigationNameValue.add(getString(R.string.selectInvestigation));
                        investigationNameValue.add(getString(R.string.urineRnMValue));
                        investigationNameValue.add(getString(R.string.stoolForOccultBloodValue));
                        investigationNameValue.add(getString(R.string.csfValue));
                        investigationNameValue.add(getString(R.string.bloodCollectionValue));
                        investigationNameValue.add(getString(R.string.otherValue));
                    }

                    spinnerInvestigation.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, investigationName));
                    spinnerInvestigation.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        //ClickListener
        tvSubmit.setOnClickListener(this);
        llSync.setOnClickListener(this);
        llHelp.setOnClickListener(this);
        llLogout.setOnClickListener(this);
        llLanguage.setOnClickListener(this);
        rlHelp.setOnClickListener(this);
        rlStuck.setOnClickListener(this);
        rlOwn.setOnClickListener(this);

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

            case R.id.tvSubmit:

                if(spinnerInvestigationType.getSelectedItemPosition()==0)
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectInvesType));
                }
                else if(spinnerInvestigation.getSelectedItemPosition()==0)
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectInvesMethod));
                }
                else
                {
                    HashMap<String, String> hashlist = new HashMap();

                    hashlist.put("uuid", uuid);
                    hashlist.put("babyId", AppSettings.getString(AppSettings.babyId));
                    hashlist.put("investigationType", investigationTypeValue.get(spinnerInvestigationType.getSelectedItemPosition()));
                    hashlist.put("investigationName", investigationNameValue.get(spinnerInvestigation.getSelectedItemPosition()));
                    hashlist.put("comment", etComment.getText().toString().trim());
                    hashlist.put("addDate", String.valueOf(AppUtils.currentTimestampFormat()));

                    AppConstants.investigationList.add(hashlist);

                    AlertSave();
                }

                break;
        }
    }

    public class adapterSpinner extends ArrayAdapter<String> {

        ArrayList<String> data;

        public adapterSpinner(Context context, int textViewResourceId, ArrayList <String> arraySpinner_time) {

            super(context, textViewResourceId, arraySpinner_time);

            this.data = arraySpinner_time;

        }

        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row=inflater.inflate(R.layout.inflate_spinner_new, parent, false);
            TextView label=row.findViewById(R.id.tvName);

            label.setText(data.get(position));

            return row;
        }
    }

    public void AlertSave() {
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

        //RelativeLayout
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        tvMessage.setText(getString(R.string.addMore));
        tvOk.setText(getString(R.string.yes));
        tvCancel.setText(getString(R.string.no));

        ivImage.setVisibility(View.VISIBLE);
        ivImage.setImageResource(R.drawable.ic_warning);

        rlOk.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                dialog.dismiss();
                reset();
            }
        });

        rlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                onBackPressed();
            }
        });
    }

    private void reset() {
        uuid  = UUID.randomUUID().toString();
        etComment.setText("");
        spinnerInvestigationType.setSelection(0);
        spinnerInvestigation.setSelection(0);
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
