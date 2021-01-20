package code.main;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
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
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.kmcapp.android.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import code.algo.NotificationAlgo;
import code.algo.SyncAllRecord;
import code.algo.SyncMotherRecord;
import code.common.DecimalDigitsInputFilter;
import code.common.LocaleHelper;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableMotherMonitoring;
import code.utils.AppUtils;
import code.view.BaseActivity;


public class MotherAssessmentActivity extends BaseActivity implements View.OnClickListener {

    ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
    int currentPage = 1, checkPulse = 0;
    String tempUnit = "", utrine = "";
    //RelativeLayout
    private RelativeLayout rlNext, rlPrevious;
    //ArrayList
    private ArrayList<String> tempText = new ArrayList<String>();
    private ArrayList<String> tempValue = new ArrayList<String>();
    //LinearLayout
    private LinearLayout llInform;
    private ScrollView svFindings;

    //EditText
    private EditText etTemperature, etSystolic, etDiastolic, etPulse, etComment;

    //RelativeLayout
    private RelativeLayout rlSoft, rlHard;

    //ImageView
    private ImageView ivSoft, ivHard, ivNext;

    private String uuid = "";

    //Spinner
    private Spinner spinnerEnteredByNurse, spinnerTempUnit;

    private ArrayList<String> nurseId = new ArrayList<String>();
    private ArrayList<String> nurseName = new ArrayList<String>();

    //LinearLayout
    private LinearLayout llSync, llHelp, llLogout, llLanguage;

    //RelativeLayout
    private RelativeLayout rlHelp, rlStuck, rlOwn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mother_assessment);

        findViewById();
        setClickListner();

        String where = TableMotherMonitoring.tableColumn.motherId + " = '" + AppSettings.getString(AppSettings.motherId)
                + "' and isDataComplete = '0' ";

        boolean countStep1 = DatabaseController.
                checkRecordExistWhere(TableMotherMonitoring.tableName, where);

        if (countStep1) {
            uuid = DatabaseController.getMotherMoniUUID(AppSettings.getString(AppSettings.motherId));
            AlertYesNo();
        } else {
            uuid = UUID.randomUUID().toString();
            currentPage = 1;
            setViewVisibility(currentPage);
        }

        /*ContentValues mContentValues = new ContentValues();
        mContentValues.put(TableBabyMonitoring.tableColumn.isDataSynced.toString(),"1");

        DatabaseController.updateNotEqual(mContentValues,
                TableBabyMonitoring.tableName,"uuid", "0");*/
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void findViewById() {

        uuid = UUID.randomUUID().toString();

        //LinearLayout
        llSync = findViewById(R.id.llSync);
        llHelp = findViewById(R.id.llHelp);
        llLogout = findViewById(R.id.llLogout);
        llInform = findViewById(R.id.llInform);
        llLanguage = findViewById(R.id.llLanguage);

        //RelativeLayout
        rlHelp = findViewById(R.id.rlHelp);
        rlStuck = findViewById(R.id.rlStuck);
        rlOwn = findViewById(R.id.rlOwn);

        //RelativeLayout
        rlNext = findViewById(R.id.rlNext);
        rlPrevious = findViewById(R.id.rlPrevious);
        rlSoft = findViewById(R.id.rlSoft);
        rlHard = findViewById(R.id.rlHard);

        //ImageView
        ivSoft = findViewById(R.id.ivSoft);
        ivHard = findViewById(R.id.ivHard);
        ivNext = findViewById(R.id.ivNext);

        //EditText
        etTemperature = findViewById(R.id.etTemperature);
        etSystolic = findViewById(R.id.etSystolic);
        etDiastolic = findViewById(R.id.etDiastolic);
        etPulse = findViewById(R.id.etPulse);
        etComment = findViewById(R.id.etComment);
        etTemperature.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});

        //ScrollView
        svFindings = findViewById(R.id.svFindings);

        //Spinner
        spinnerEnteredByNurse = findViewById(R.id.spinnerEnteredByNurse);
        spinnerTempUnit = findViewById(R.id.spinnerTempUnit);

        nurseId.clear();
        nurseId.add(getString(R.string.selectNurse));
        nurseId.addAll(DatabaseController.getNurseIdCheckedInData());

        nurseName.clear();
        nurseName.add(getString(R.string.selectNurse));
        nurseName.addAll(DatabaseController.getNurseNameCheckedInData());

        spinnerEnteredByNurse.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, nurseName));
        spinnerEnteredByNurse.setSelection(0);

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

                tempUnit = tempValue.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        currentPage = 1;
        setViewVisibility(currentPage);
    }

    private void setClickListner() {

        //RelativeLayout SetOnClickListener
        rlNext.setOnClickListener(this);
        rlPrevious.setOnClickListener(this);
        rlSoft.setOnClickListener(this);
        rlHard.setOnClickListener(this);

        //setOnClickListener
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

                AppUtils.AlertLanguageConfirm(mActivity, getString(R.string.languageAlert));

                break;

            case R.id.llSync:

                if (AppUtils.isNetworkAvailable(mActivity)) {
                    AppSettings.putString(AppSettings.syncTime, AppUtils.currentTimestampFormat());
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

                if (rlHelp.getVisibility() == View.VISIBLE) {
                    rlHelp.setVisibility(View.GONE);
                } else {
                    rlHelp.setVisibility(View.VISIBLE);
                }

                break;

            case R.id.rlStuck:

                rlHelp.setVisibility(View.GONE);
                DatabaseController.saveHelp(mActivity);
                AppUtils.AlertHelpConfirm(getString(R.string.callRegardingIssue), mActivity, 1, "");

                break;

            case R.id.rlOwn:

                rlHelp.setVisibility(View.GONE);
                startActivity(new Intent(mActivity, TutorialActivity.class));

                break;

            case R.id.rlNext:

                validation();

                break;

            case R.id.rlPrevious:

                currentPage = currentPage - 1;
                setViewVisibility(currentPage);

                break;

            case R.id.rlSoft:

                setDefaultSoft();
                utrine = getString(R.string.softRelaxedValue);
                ivSoft.setImageResource(R.drawable.ic_check_box_selected);

                break;

            case R.id.rlHard:

                setDefaultSoft();
                utrine = getString(R.string.hardValue);
                ivHard.setImageResource(R.drawable.ic_check_box_selected);

                break;
        }

    }

    private void setDefaultSoft() {

        utrine = "";
        ivSoft.setImageResource(R.drawable.ic_check_box);
        ivHard.setImageResource(R.drawable.ic_check_box);

    }

    private void setViewVisibility(int position) {

        if (position == 1) {
            ivNext.setImageResource(R.drawable.ic_next);
            rlPrevious.setVisibility(View.GONE);
            llInform.setVisibility(View.VISIBLE);
            svFindings.setVisibility(View.GONE);
        } else if (position == 2) {
            ivNext.setImageResource(R.drawable.ic_tick);
            llInform.setVisibility(View.GONE);
            rlPrevious.setVisibility(View.VISIBLE);
            svFindings.setVisibility(View.VISIBLE);
        }
    }

    private void validation() {

        if (!etSystolic.getText().toString().isEmpty()) {
            saveMonitoring(uuid, "0");
        }

        int systolic = 0;
        try {
            systolic = Integer.parseInt(etSystolic.getText().toString().trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        int diastolic = 0;
        try {
            diastolic = Integer.parseInt(etDiastolic.getText().toString().trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        int pulse = 0;
        try {
            pulse = Integer.parseInt(etPulse.getText().toString().trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        float temp = 0;
        try {
            temp = Float.valueOf(etTemperature.getText().toString().trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        double minTempF = 90;
        double maxTempF = 107;
        double minTempC = 32.2;
        double maxTempC = 41.6;

        if (spinnerEnteredByNurse.getSelectedItemPosition() == 0 && currentPage == 2) {
            AppUtils.showToastSort(mActivity, getString(R.string.selectNurse));
        } else if (etSystolic.getText().toString().trim().isEmpty() && currentPage == 2) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorSystolic));
        } else if (etSystolic.getText().toString().trim().isEmpty() && currentPage == 2) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorSystolic));
        } else if (!etSystolic.getText().toString().trim().isEmpty() && (Integer.valueOf(etSystolic.getText().toString()) < 90 || Integer.valueOf(etSystolic.getText().toString()) > 200) && currentPage == 2) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorSystolicValue));
        } else if (etDiastolic.getText().toString().trim().isEmpty() && currentPage == 2) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorDiastolic));
        } else if (!etDiastolic.getText().toString().trim().isEmpty() && (Integer.valueOf(etDiastolic.getText().toString()) < 50 || Integer.valueOf(etDiastolic.getText().toString()) > 150) && currentPage == 2) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorDistolicValue));
        } else if (etPulse.getText().toString().trim().isEmpty() && currentPage == 2) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorPulseEmpty));
        } else if ((pulse > 150 || pulse < 50) && currentPage == 2 && checkPulse == 0) {
            etPulse.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorPulseMother));
        }

//        else if((pulse>150||pulse<50)&&currentPage==2&&checkPulse==0)
//        {
//            checkPulse=1;
//            AppUtils.AlertOk(getString(R.string.areYouSurePart1)+" "+pulse,mActivity,2,getString(R.string.areYouSurePart1)+" "+pulse+" "+getString(R.string.bpm)+getString(R.string.areYouSurePart2));
//        }
        else if (etTemperature.getText().toString().trim().isEmpty() && currentPage == 2) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorTemp));
        } else if (currentPage == 2
                && tempUnit.equalsIgnoreCase(getString(R.string.degreeFValue))
                && (temp < minTempF || temp > maxTempF)) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorTempF));
        } else if (currentPage == 2
                && tempUnit.equalsIgnoreCase(getString(R.string.degreeCValue))
                && (temp < minTempC || temp > maxTempC)) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorTempC));
        } else if (currentPage == 1) {
            currentPage = currentPage + 1;
            setViewVisibility(currentPage);
        } else {
            saveMonitoring(uuid, "1");

            if (AppUtils.isNetworkAvailable(mActivity)) {
                SyncMotherRecord.getMotherDataToUpdate(mActivity, AppSettings.getString(AppSettings.motherId));
                finish();
            } else {
                AppUtils.showToastSort(mActivity, getString(R.string.dataSaved));
                finish();
            }
        }

    }

    private void saveMonitoring(String uuid, String isDataComplete) {

        ContentValues mContentValues = new ContentValues();

        mContentValues.put(TableMotherMonitoring.tableColumn.uuid.toString(), uuid);
        mContentValues.put(TableMotherMonitoring.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        mContentValues.put(TableMotherMonitoring.tableColumn.motherId.toString(), AppSettings.getString(AppSettings.motherId));
        mContentValues.put(TableMotherMonitoring.tableColumn.motherAdmissionId.toString(), AppSettings.getString(AppSettings.mAdmId));
        mContentValues.put(TableMotherMonitoring.tableColumn.motherTemperature.toString(), etTemperature.getText().toString().trim());
        mContentValues.put(TableMotherMonitoring.tableColumn.motherSystolicBP.toString(), etSystolic.getText().toString().trim());
        mContentValues.put(TableMotherMonitoring.tableColumn.motherDiastolicBP.toString(), etDiastolic.getText().toString().trim());
        mContentValues.put(TableMotherMonitoring.tableColumn.motherPulse.toString(), etPulse.getText().toString().trim());
        mContentValues.put(TableMotherMonitoring.tableColumn.motherUterineTone.toString(), utrine);
        mContentValues.put(TableMotherMonitoring.tableColumn.episitomyCondition.toString(), "");
        mContentValues.put(TableMotherMonitoring.tableColumn.newSanitoryPadCheck.toString(), "");
        mContentValues.put(TableMotherMonitoring.tableColumn.motherUrinationAfterDelivery.toString(), "");

        mContentValues.put(TableMotherMonitoring.tableColumn.sanitoryPadStatus.toString(), "");
        mContentValues.put(TableMotherMonitoring.tableColumn.isSanitoryPadStink.toString(), "");
        mContentValues.put(TableMotherMonitoring.tableColumn.admittedSign.toString(), "");
        mContentValues.put(TableMotherMonitoring.tableColumn.staffId.toString(), nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()));
        mContentValues.put(TableMotherMonitoring.tableColumn.other.toString(), etComment.getText().toString().trim());
        mContentValues.put(TableMotherMonitoring.tableColumn.padPicture.toString(), "");
        mContentValues.put(TableMotherMonitoring.tableColumn.assesmentNumber.toString(), DatabaseController.getMotherAssessmentNumber(AppSettings.getString(AppSettings.motherId)));
        mContentValues.put(TableMotherMonitoring.tableColumn.temperatureUnit.toString(), tempUnit);

        mContentValues.put(TableMotherMonitoring.tableColumn.padChanged.toString(), "");
        mContentValues.put(TableMotherMonitoring.tableColumn.padNotChangeReason.toString(), "");

        mContentValues.put(TableMotherMonitoring.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
        mContentValues.put(TableMotherMonitoring.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());
        mContentValues.put(TableMotherMonitoring.tableColumn.status.toString(), "1");
        mContentValues.put(TableMotherMonitoring.tableColumn.isDataComplete.toString(), isDataComplete);
        mContentValues.put(TableMotherMonitoring.tableColumn.isDataSynced.toString(), "2");

        DatabaseController.insertUpdateData(mContentValues, TableMotherMonitoring.tableName,
                TableMotherMonitoring.tableColumn.uuid.toString(), uuid);

        AppSettings.putString(AppSettings.lastAssessmentDate, AppUtils.currentTimestampFormat());

        if (isDataComplete.equalsIgnoreCase("1")) {
            String notiId = DatabaseController.getPendingNotifications(1, AppSettings.getString(AppSettings.motherId));

            if (!notiId.isEmpty()) {
                NotificationAlgo.updateNotification(notiId, "2");
            }
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
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    //AlertYesNo
    private void AlertYesNo() {
        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
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

        rlCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();

                String where = TableMotherMonitoring.tableColumn.motherId + " = '" + AppSettings.getString(AppSettings.motherId)
                        + "' and " + TableMotherMonitoring.tableColumn.uuid + " = '" + uuid + "' ";

                DatabaseController.delete(TableMotherMonitoring.tableName, where, null);

                uuid = UUID.randomUUID().toString();

                currentPage = 1;
                setViewVisibility(currentPage);
            }
        });

        rlOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();

                currentPage = 1;
                setViewVisibility(currentPage);
                setStepValues();
            }
        });
    }

    public void setStepValues() {

        arrayList.clear();
        arrayList.addAll(DatabaseController.getIncompleteMotherMonitoringData(AppSettings.getString(AppSettings.motherId)));

        if (arrayList.size() > 0) {
            etTemperature.setText(arrayList.get(0).get("motherTemperature"));
            etSystolic.setText(arrayList.get(0).get("motherSystolicBP"));
            etDiastolic.setText(arrayList.get(0).get("motherDiastolicBP"));

            utrine = arrayList.get(0).get("motherUterineTone");

            if (utrine.equals(getString(R.string.softRelaxedValue))) {
                setDefaultSoft();
                ivSoft.setImageResource(R.drawable.ic_check_box_selected);
            } else if (utrine.equalsIgnoreCase(getString(R.string.hardValue))) {
                setDefaultSoft();
                ivHard.setImageResource(R.drawable.ic_check_box_selected);
            }

            etPulse.setText(arrayList.get(0).get("motherPulse"));

            tempText.clear();
            tempText.add(getString(R.string.degreeF));
            tempText.add(getString(R.string.degreeC));

            tempValue.clear();
            tempValue.add(getString(R.string.degreeFValue));
            tempValue.add(getString(R.string.degreeCValue));

            spinnerTempUnit.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, tempText));

            if (arrayList.get(0).get("temperatureUnit").equals("F")) {
                spinnerTempUnit.setSelection(0);
            } else {
                spinnerTempUnit.setSelection(1);
            }
        }

    }

    @Override
    public void onBackPressed() {
        AppUtils.AlertCloseActivity(mActivity, getString(R.string.sureToCancel));
    }

    private static class adapterSpinner extends ArrayAdapter<String> {

        ArrayList<String> data;

        private adapterSpinner(Context context, int textViewResourceId, ArrayList<String> arraySpinner_time) {

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

            View row = inflater.inflate(R.layout.inflate_spinner_new, parent, false);

            TextView tvName = row.findViewById(R.id.tvName);

            tvName.setText(data.get(position));

            return row;
        }
    }
}
