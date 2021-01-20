package code.registration;


import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.kmcapp.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.common.AdapterSpinner;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableBabyAdmission;
import code.database.TableBabyMonitoring;
import code.database.TableMotherAdmission;
import code.database.TableWeight;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;

import static code.registration.RegistrationActivity.uuid;

public class BaselineFragment extends BaseFragment implements View.OnClickListener {

    //EditText
    private EditText etBabyWeight, etBabyLength, etBabyHead, etBabyLengthReason, etBabyHeadReason;

    //RelativeLayout
    private RelativeLayout rlHeadCircumference, rlLengthOfbaby, rlLengthYes, rlLengthNo, rlHeadYes, rlHeadNo, rlNext, rlBabyLengthReason, rlBabyHeadReason;

    //Spinner
    private Spinner spinnerBabyLengthReason, spinnerBabyHeadReason;

    //ImageView
    private ImageView ivLengthYes, ivLengthNo, ivHeadYes, ivHeadNo;

    private String inchiTape = "", lengthTape = "", babyLengthReason = "", babyHeadReason = "";

    //ArrayList
    private ArrayList<String> arrayListBabyLengthReason = new ArrayList<String>();
    private ArrayList<String> arrayListBabyHeadReason = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_baseline, container, false);

        initialize(v);

        RegistrationActivity.tvMotherAdmission.setBackgroundResource(R.drawable.rectangle_teal_selected);
        RegistrationActivity.tvMotherAdmission.setTextColor(getResources().getColor(R.color.white));
        RegistrationActivity.tvBabyAdmission.setBackgroundResource(R.drawable.rectangle_teal_selected);
        RegistrationActivity.tvBabyAdmission.setTextColor(getResources().getColor(R.color.white));
        RegistrationActivity.tvAssessment.setBackgroundResource(R.drawable.rectangle_teal_selected);
        RegistrationActivity.tvAssessment.setTextColor(getResources().getColor(R.color.white));
        RegistrationActivity.tvAdmission.setBackgroundResource(R.drawable.rectangle_teal_selected);
        RegistrationActivity.tvAdmission.setTextColor(getResources().getColor(R.color.white));


        return v;
    }

    private void initialize(View v) {

        //EditText
        etBabyWeight = v.findViewById(R.id.etBabyWeight);
        etBabyLength = v.findViewById(R.id.etBabyLength);
        etBabyHead = v.findViewById(R.id.etBabyHead);
        etBabyLengthReason = v.findViewById(R.id.etBabyLengthReason);
        etBabyHeadReason = v.findViewById(R.id.etBabyHeadReason);

        //RelativeLayout
        rlLengthYes = v.findViewById(R.id.rlLengthYes);
        rlLengthNo = v.findViewById(R.id.rlLengthNo);
        rlHeadYes = v.findViewById(R.id.rlHeadYes);
        rlHeadNo = v.findViewById(R.id.rlHeadNo);
        rlNext = v.findViewById(R.id.rlNext);
        rlBabyLengthReason = v.findViewById(R.id.rlBabyLengthReason);
        rlBabyHeadReason = v.findViewById(R.id.rlBabyHeadReason);
        rlLengthOfbaby = v.findViewById(R.id.rlLengthOfbaby);
        rlHeadCircumference = v.findViewById(R.id.rlHeadCircumference);

        //ImageView
        ivLengthYes = v.findViewById(R.id.ivLengthYes);
        ivLengthNo = v.findViewById(R.id.ivLengthNo);
        ivHeadYes = v.findViewById(R.id.ivHeadYes);
        ivHeadNo = v.findViewById(R.id.ivHeadNo);

        //Spinner
        spinnerBabyLengthReason = v.findViewById(R.id.spinnerBabyLengthReason);
        spinnerBabyHeadReason = v.findViewById(R.id.spinnerBabyHeadReason);

        arrayListBabyLengthReason.clear();
        arrayListBabyLengthReason.add(getString(R.string.reason));
        arrayListBabyLengthReason.add(getString(R.string.equipmentNot));
        arrayListBabyLengthReason.add(getString(R.string.dontKnowMeasurement));
        arrayListBabyLengthReason.add(getString(R.string.motherNotAllowed));
        arrayListBabyLengthReason.add(getString(R.string.other));

        arrayListBabyHeadReason.clear();
        arrayListBabyHeadReason.add(getString(R.string.reason));
        arrayListBabyHeadReason.add(getString(R.string.equipmentNot));
        arrayListBabyHeadReason.add(getString(R.string.dontKnowMeasurement));
        arrayListBabyHeadReason.add(getString(R.string.motherNotAllowed));
        arrayListBabyHeadReason.add(getString(R.string.other));

        spinnerBabyLengthReason.setAdapter(new AdapterSpinner(mActivity, R.layout.inflate_spinner_new, arrayListBabyLengthReason));
        spinnerBabyHeadReason.setAdapter(new AdapterSpinner(mActivity, R.layout.inflate_spinner_new, arrayListBabyHeadReason));

        rlLengthYes.setOnClickListener(this);
        rlLengthNo.setOnClickListener(this);
        rlHeadYes.setOnClickListener(this);
        rlHeadNo.setOnClickListener(this);
        rlNext.setOnClickListener(this);

        spinnerBabyLengthReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if (position == 0) {
                    babyLengthReason = "";
                    etBabyLengthReason.setVisibility(View.GONE);
                    etBabyLengthReason.setText("");
                } else {
                    babyLengthReason = arrayListBabyLengthReason.get(position);

                    if (position == 4) {
                        etBabyLengthReason.setVisibility(View.VISIBLE);
                    } else {
                        etBabyLengthReason.setText("");
                        etBabyLengthReason.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        spinnerBabyHeadReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if (position == 0) {
                    babyHeadReason = "";
                    etBabyHeadReason.setVisibility(View.GONE);
                    etBabyHeadReason.setText("");
                } else {
                    babyHeadReason = arrayListBabyHeadReason.get(position);

                    if (position == 4) {
                        etBabyHeadReason.setVisibility(View.VISIBLE);
                    } else {
                        etBabyHeadReason.setText("");
                        etBabyHeadReason.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rlNext:
                validation();
                break;

            case R.id.rlLengthYes:
                setDefaultLength();
                ivLengthYes.setImageResource(R.drawable.ic_check_box_selected);
                etBabyLength.setEnabled(true);
                lengthTape = getString(R.string.yesValue);
                rlLengthOfbaby.setVisibility(View.VISIBLE);
                break;

            case R.id.rlLengthNo:
                setDefaultLength();
                ivLengthNo.setImageResource(R.drawable.ic_check_box_selected);
                etBabyLength.setEnabled(false);
                rlBabyLengthReason.setVisibility(View.VISIBLE);
                rlLengthOfbaby.setVisibility(View.GONE);
                lengthTape = getString(R.string.noValue);
                etBabyLength.setText("");
                break;

            case R.id.rlHeadYes:
                setDefaultHead();
                ivHeadYes.setImageResource(R.drawable.ic_check_box_selected);
                etBabyHead.setEnabled(true);
                inchiTape = getString(R.string.yesValue);
                rlHeadCircumference.setVisibility(View.VISIBLE);

                break;

            case R.id.rlHeadNo:
                setDefaultHead();
                ivHeadNo.setImageResource(R.drawable.ic_check_box_selected);
                etBabyHead.setEnabled(false);
                rlBabyHeadReason.setVisibility(View.VISIBLE);
                inchiTape = getString(R.string.noValue);
                rlHeadCircumference.setVisibility(View.GONE);
                etBabyHead.setText("");
                break;

            default:

                break;
        }
    }

    private void validation() {

        int weight = 0;
        try {
            weight = Integer.parseInt(etBabyWeight.getText().toString().trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        if (etBabyWeight.getText().toString().isEmpty()) {
            etBabyWeight.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorEnterWeight));
        } else if ((weight < 400 || weight > 6000)) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorWeightLimit));
        } else if (lengthTape.isEmpty()) {
            AppUtils.showToastSort(mActivity, getString(R.string.lengthOfBaby));
        } else if (etBabyLength.getText().toString().trim().isEmpty() && lengthTape.equalsIgnoreCase(getString(R.string.yesValue))) {
            etBabyLength.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorLength));
        } else if (!etBabyLength.getText().toString().trim().isEmpty() && (Integer.valueOf(etBabyLength.getText().toString()) < 30 || Integer.valueOf(etBabyLength.getText().toString()) > 70) && lengthTape.equalsIgnoreCase(getString(R.string.yesValue))) {
            etBabyLength.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorBabyLength));
        } else if (lengthTape.equalsIgnoreCase(getString(R.string.noValue)) && spinnerBabyLengthReason.getSelectedItemPosition() == 0) {

            AppUtils.showToastSort(mActivity, getString(R.string.selectReasonNotMeasurLength));
        } else if (lengthTape.equalsIgnoreCase(getString(R.string.noValue)) && spinnerBabyLengthReason.getSelectedItemPosition() == 4 &&
                etBabyLengthReason.getText().toString().trim().isEmpty()) {

            AppUtils.showToastSort(mActivity, getString(R.string.enterReasonNotMeasurLength));
        } else if (inchiTape.isEmpty()) {
            AppUtils.showToastSort(mActivity, getString(R.string.isHeadTapeAvailable));
        } else if (etBabyHead.getText().toString().trim().isEmpty() && inchiTape.equalsIgnoreCase(getString(R.string.yesValue))) {
            etBabyHead.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorHead));
        }

        else if (!etBabyHead.getText().toString().trim().isEmpty() && (Integer.valueOf(etBabyHead.getText().toString())<25 || Integer.valueOf(etBabyHead.getText().toString())>45) && inchiTape.equalsIgnoreCase(getString(R.string.yesValue))) {
            etBabyHead.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorHeadMeasure));
        }

        else if (inchiTape.equalsIgnoreCase(getString(R.string.noValue)) && spinnerBabyHeadReason.getSelectedItemPosition() == 0) {

            AppUtils.showToastSort(mActivity, getString(R.string.selectReasonNotMeasurHead));
        } else if (inchiTape.equalsIgnoreCase(getString(R.string.noValue)) && spinnerBabyHeadReason.getSelectedItemPosition() == 4 &&
                etBabyHeadReason.getText().toString().trim().isEmpty()) {

            AppUtils.showToastSort(mActivity, getString(R.string.enterReasonNotMeasurHead));
        } else if (AppUtils.isNetworkAvailable(mActivity)) {
            //createJsonForBabyAssessment();
           /* Log.v("ljkqhskjqhs", AppUtils.md5(spinnerBabyHeadReason.getSelectedItem().toString()));
            Log.v("ljkqhskjqhs", spinnerBabyHeadReason.getSelectedItem().toString());*/
            doAssessmentApi();
        } else {
            AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
        }
    }

    private void setDefaultLength() {

        ivLengthYes.setImageResource(R.drawable.ic_check_box);
        ivLengthNo.setImageResource(R.drawable.ic_check_box);
        etBabyLength.setText("");
        etBabyLengthReason.setVisibility(View.GONE);
        rlBabyLengthReason.setVisibility(View.GONE);
        spinnerBabyLengthReason.setSelection(0);
    }

    private void setDefaultHead() {

        ivHeadYes.setImageResource(R.drawable.ic_check_box);
        ivHeadNo.setImageResource(R.drawable.ic_check_box);
        etBabyHead.setText("");
        etBabyHeadReason.setVisibility(View.GONE);
        rlBabyHeadReason.setVisibility(View.GONE);
        spinnerBabyHeadReason.setSelection(0);
    }


    private JSONObject createJsonForBabyAssessment() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();

        try {

            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("babyId", AppSettings.getString(AppSettings.babyId));
            jsonData.put("nurseId", AppSettings.getString(AppSettings.nurseId));
            jsonData.put("babyAdmissionWeight", etBabyWeight.getText().toString().trim());
            jsonData.put("localId", uuid);
            jsonData.put("isLengthMeasurngTapeAvailable", lengthTape);
            jsonData.put("isHeadMeasurngTapeAvailable", inchiTape);
            jsonData.put("lengthValue", "");
            jsonData.put("headCircumferenceVal", "");

            jsonData.put("lengthMeasureNotAvlReason", "");
            jsonData.put("lengthMeasureNotAvlReasonOther", "");
            jsonData.put("headMeasureNotAvlReason", "");
            jsonData.put("headMeasureNotAvlReasonOther", "");

            jsonData.put("latitude", AppSettings.getString(AppSettings.latitude));
            jsonData.put("longitude", AppSettings.getString(AppSettings.longitude));
            jsonData.put("weightDate", AppUtils.getCurrentDateFormatted());
            jsonData.put("localDateTime", AppUtils.currentTimestampFormat());

            if (lengthTape.equals(getString(R.string.yesValue))) {
                jsonData.put("lengthValue", etBabyLength.getText().toString().trim());
            } else {
                jsonData.put("lengthMeasureNotAvlReason", spinnerBabyLengthReason.getSelectedItem().toString());

                if (etBabyLengthReason.getVisibility() == View.VISIBLE) {
                    jsonData.put("lengthMeasureNotAvlReasonOther", etBabyLengthReason.getText().toString().trim());
                }
            }

            if (inchiTape.equals(getString(R.string.yesValue))) {
                jsonData.put("headCircumferenceVal", etBabyHead.getText().toString().trim());
            } else {
                jsonData.put("headMeasureNotAvlReason", spinnerBabyHeadReason.getSelectedItem().toString());

                if (etBabyHeadReason.getVisibility() == View.VISIBLE) {
                    jsonData.put("headMeasureNotAvlReasonOther", etBabyHeadReason.getText().toString().trim());
                }
            }

            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));

            Log.v("doBAdmissionApi", json.toString());
            Log.v("doBAdmissionApi2", jsonData.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;

    }

    private void doAssessmentApi() {

        WebServices.postApi(mActivity, AppUrls.baselineMeasurements, createJsonForBabyAssessment(), true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        saveBabyMonitoringData();

                        ((RegistrationActivity) getActivity()).displayView(4);

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

    private void saveBabyMonitoringData() {

        ContentValues cvBabyAdm = new ContentValues();

        cvBabyAdm.put(TableBabyAdmission.tableColumn.uuid.toString(), RegistrationActivity.uuid);
        cvBabyAdm.put(TableBabyAdmission.tableColumn.status.toString(), "1");

        DatabaseController.insertUpdateData(cvBabyAdm, TableBabyAdmission.tableName,
                TableBabyAdmission.tableColumn.uuid.toString(), String.valueOf(RegistrationActivity.uuid));

        ContentValues cvMotherAdm = new ContentValues();

        cvMotherAdm.put(TableMotherAdmission.tableColumn.uuid.toString(), RegistrationActivity.uuid);
        cvMotherAdm.put(TableMotherAdmission.tableColumn.status.toString(), "1");

        DatabaseController.insertUpdateData(cvMotherAdm, TableMotherAdmission.tableName,
                TableMotherAdmission.tableColumn.uuid.toString(), String.valueOf(RegistrationActivity.uuid));

        ContentValues contentValues = new ContentValues();

        contentValues.put(TableBabyMonitoring.tableColumn.uuid.toString(), uuid);
        contentValues.put(TableBabyMonitoring.tableColumn.babyMeasuredWeight.toString(), etBabyWeight.getText().toString().trim());
        contentValues.put(TableBabyMonitoring.tableColumn.isHeadCircumferenceAvail.toString(), inchiTape);
        contentValues.put(TableBabyMonitoring.tableColumn.lengthValue.toString(), etBabyLength.getText().toString().trim());
        contentValues.put(TableBabyMonitoring.tableColumn.isLengthAvail.toString(), lengthTape);
        contentValues.put(TableBabyMonitoring.tableColumn.babyHeadCircumference.toString(), etBabyHead.getText().toString().trim());
        contentValues.put(TableBabyMonitoring.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableBabyMonitoring.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableBabyMonitoring.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableBabyMonitoring.tableColumn.formattedDate.toString(), AppUtils.getDateInFormat());
        contentValues.put(TableBabyMonitoring.tableColumn.status.toString(), "1");
        contentValues.put(TableBabyMonitoring.tableColumn.isDataSynced.toString(), "1");

        if (lengthTape.equalsIgnoreCase(getString(R.string.noValue)) && spinnerBabyLengthReason.getSelectedItemPosition() == 4) {

            contentValues.put(TableBabyMonitoring.tableColumn.lengthReason.toString(), spinnerBabyLengthReason.getSelectedItem().toString());
            contentValues.put(TableBabyMonitoring.tableColumn.lengthReasonOther.toString(), etBabyLengthReason.getText().toString().trim());
        } else if (lengthTape.equalsIgnoreCase(getString(R.string.noValue))) {
            contentValues.put(TableBabyMonitoring.tableColumn.lengthReason.toString(), spinnerBabyLengthReason.getSelectedItem().toString());
            contentValues.put(TableBabyMonitoring.tableColumn.lengthReasonOther.toString(), "");
        } else {

            contentValues.put(TableBabyMonitoring.tableColumn.lengthReason.toString(), "");
            contentValues.put(TableBabyMonitoring.tableColumn.lengthReasonOther.toString(), "");
        }

        if (inchiTape.equalsIgnoreCase(getString(R.string.noValue)) && spinnerBabyHeadReason.getSelectedItemPosition() == 4) {

            contentValues.put(TableBabyMonitoring.tableColumn.babyHeadCircumferenceReason.toString(), spinnerBabyHeadReason.getSelectedItem().toString());
            contentValues.put(TableBabyMonitoring.tableColumn.babyHeadCircumferenceOtherReason.toString(), etBabyHeadReason.getText().toString().trim());
        } else if (inchiTape.equalsIgnoreCase(getString(R.string.noValue))) {
            contentValues.put(TableBabyMonitoring.tableColumn.babyHeadCircumferenceReason.toString(), spinnerBabyHeadReason.getSelectedItem().toString());
            contentValues.put(TableBabyMonitoring.tableColumn.babyHeadCircumferenceOtherReason.toString(), "");
        } else {

            contentValues.put(TableBabyMonitoring.tableColumn.babyHeadCircumferenceReason.toString(), "");
            contentValues.put(TableBabyMonitoring.tableColumn.babyHeadCircumferenceOtherReason.toString(), "");
        }

        DatabaseController.insertUpdateData(contentValues, TableBabyMonitoring.tableName,
                TableBabyMonitoring.tableColumn.uuid.toString(), String.valueOf(uuid));

        ContentValues cvBabyWeight = new ContentValues();

        cvBabyWeight.put(TableWeight.tableColumn.uuid.toString(), uuid);
        cvBabyWeight.put(TableWeight.tableColumn.babyAdmissionId.toString(), AppSettings.getString(AppSettings.bAdmId));
        cvBabyWeight.put(TableWeight.tableColumn.babyId.toString(), AppSettings.getString(AppSettings.babyId));
        cvBabyWeight.put(TableWeight.tableColumn.nurseId.toString(), AppSettings.getString(AppSettings.nurseId));
        cvBabyWeight.put(TableWeight.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        cvBabyWeight.put(TableWeight.tableColumn.weightDate.toString(), AppUtils.getDateInFormat());
        cvBabyWeight.put(TableWeight.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
        cvBabyWeight.put(TableWeight.tableColumn.isDataSynced.toString(), "1");
        cvBabyWeight.put(TableWeight.tableColumn.babyWeight.toString(), etBabyWeight.getText().toString().trim());

        DatabaseController.insertUpdateData(cvBabyWeight, TableWeight.tableName,
                TableWeight.tableColumn.uuid.toString(), String.valueOf(uuid));
    }


}
