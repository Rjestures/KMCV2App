package code.discharge;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.kmcapp.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableBabyAdmission;
import code.database.TableBabyMonitoring;
import code.database.TableBabyRegistration;
import code.database.TableBreastFeeding;
import code.database.TableComments;
import code.database.TableDoctorRound;
import code.database.TableInvestigation;
import code.database.TableKMC;
import code.database.TableNotification;
import code.database.TableTreatment;
import code.database.TableVaccination;
import code.database.TableWeight;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;


public class DoprDischargeFragment extends BaseFragment implements View.OnClickListener {

    RelativeLayout rl_length,rlReason,rlNote;
    Spinner spinnerReason;
    EditText etReason;

    RelativeLayout rl_Hlength,rlHReason,rlHNote;
    Spinner spinnerHReason;
    EditText etHReason;


    //EditText
    private EditText etDischargeNotes, etGuardianName, etSpecify, etBabyLength, etBabyHead;

    //RelativeLayout
    private RelativeLayout rlSpecify, rlNext, rlPrevious, rlTrainedYes, rlTrainedNo, rlLengthYes, rlLengthNo, rlHeadYes, rlHeadNo;

    //ImageView
    private ImageView ivTrainedYes, ivTrainedNo, ivLengthYes, ivLengthNo, ivHeadYes, ivHeadNo, ivClear;

    //LinearLayout
    private LinearLayout llStep1, llStep2, llStep3;

    //Spinner
    private Spinner spinnerRelation, spinnerDoctor, spinnerNurse, spinnerTransportation;

    //SignaturePad
    private SignaturePad signaturePad;

    //TextView
    private TextView tvSubmit;

    private ArrayList<String> nurseId = new ArrayList<String>();
    private ArrayList<String> nurseName = new ArrayList<String>();
    private ArrayList<String> doctorId = new ArrayList<String>();
    private ArrayList<String> doctorName = new ArrayList<String>();
    private ArrayList<String> relationList = new ArrayList<String>();
    private ArrayList<String> relationValue = new ArrayList<String>();
    private ArrayList<String> transportationName = new ArrayList<String>();
    private ArrayList<String> transportationNameValue = new ArrayList<String>();

    private String trained = "", inchiTape = "", lengthTape = "", encodedSign = "";

    private int step = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dopr_discharge, container, false);

        reasonInit(view);

        initialize(view);


        return view;
    }

    public void reasonInit(View v) {
        final ArrayList<String> reasonValue = new ArrayList<String>();

        reasonValue.add(mActivity.getString(R.string.reason));
        reasonValue.add(mActivity.getString(R.string.equipmentNot));
        reasonValue.add(mActivity.getString(R.string.dontKnowMeasurement));
        reasonValue.add(mActivity.getString(R.string.motherNotAllowed));
        reasonValue.add(mActivity.getString(R.string.other));

        rlReason = v.findViewById(R.id.rlReason);
        rl_length = v.findViewById(R.id.rl_length);
        spinnerReason = v.findViewById(R.id.spinnerReason);
        rlNote = v.findViewById(R.id.rlNote);
        etReason = v.findViewById(R.id.etReason);

        rlHReason = v.findViewById(R.id.rlHReason);
        rl_Hlength = v.findViewById(R.id.rl_Hlength);
        spinnerHReason = v.findViewById(R.id.spinnerHReason);
        rlHNote = v.findViewById(R.id.rlHNote);
        etHReason = v.findViewById(R.id.etHReason);

        spinnerReason.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, reasonValue));
        spinnerReason.setSelection(0);

        spinnerHReason.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, reasonValue));
        spinnerHReason.setSelection(0);

        spinnerReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedReason = reasonValue.get(position);
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


        spinnerHReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedReason = reasonValue.get(position);
                if (selectedReason.equalsIgnoreCase(mActivity.getString(R.string.other))) {
                    rlHNote.setVisibility(View.VISIBLE);
                } else {
                    rlHNote.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }


    private void initialize(View v) {

        //EditText
        etDischargeNotes = v.findViewById(R.id.etDischargeNotes);
        etGuardianName = v.findViewById(R.id.etGuardianName);
        etSpecify = v.findViewById(R.id.etSpecify);
        etBabyLength = v.findViewById(R.id.etBabyLength);
        etBabyHead = v.findViewById(R.id.etBabyHead);

        //RelativeLayout
        rlTrainedYes = v.findViewById(R.id.rlTrainedYes);
        rlTrainedNo = v.findViewById(R.id.rlTrainedNo);
        rlLengthYes = v.findViewById(R.id.rlLengthYes);
        rlLengthNo = v.findViewById(R.id.rlLengthNo);
        rlHeadYes = v.findViewById(R.id.rlHeadYes);
        rlHeadNo = v.findViewById(R.id.rlHeadNo);
        rlSpecify = v.findViewById(R.id.rlSpecify);
        rlNext = v.findViewById(R.id.rlNext);
        rlPrevious = v.findViewById(R.id.rlPrevious);

        //LinearLayout
        llStep1 = v.findViewById(R.id.llStep1);
        llStep2 = v.findViewById(R.id.llStep2);
        llStep3 = v.findViewById(R.id.llStep3);

        //TextView
        tvSubmit = v.findViewById(R.id.tvSubmit);

        //ImageView
        ivTrainedYes = v.findViewById(R.id.ivTrainedYes);
        ivTrainedNo = v.findViewById(R.id.ivTrainedNo);
        ivLengthYes = v.findViewById(R.id.ivLengthYes);
        ivLengthNo = v.findViewById(R.id.ivLengthNo);
        ivHeadYes = v.findViewById(R.id.ivHeadYes);
        ivHeadNo = v.findViewById(R.id.ivHeadNo);
        ivClear = v.findViewById(R.id.ivClear);

        //Spinner
        spinnerRelation = v.findViewById(R.id.spinnerRelation);
        spinnerDoctor = v.findViewById(R.id.spinnerDoctor);
        spinnerNurse = v.findViewById(R.id.spinnerNurse);
        spinnerTransportation = v.findViewById(R.id.spinnerTransportation);

        //SignaturePad
        signaturePad = v.findViewById(R.id.signaturePad);

        nurseId.clear();
        nurseId.add(getString(R.string.selectNurse));
        nurseId.addAll(DatabaseController.getNurseIdCheckedInData());

        nurseName.clear();
        nurseName.add(getString(R.string.selectNurse));
        nurseName.addAll(DatabaseController.getNurseNameCheckedInData());

        spinnerNurse.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, nurseName));
        spinnerNurse.setSelection(0);

        doctorId.clear();
        doctorId.add(getString(R.string.selectDoctor));
        doctorId.addAll(DatabaseController.getDocIdData());

        doctorName.clear();
        doctorName.add(getString(R.string.selectDoctor));
        doctorName.addAll(DatabaseController.getDocNameData());

        spinnerDoctor.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, doctorName));
        spinnerDoctor.setSelection(0);

        transportationName.clear();
        transportationName.add(getString(R.string.selectTransportation));
        transportationName.add(getString(R.string.ambulance));
        transportationName.add(getString(R.string.privateTransportation));

        transportationNameValue.clear();
        transportationNameValue.add(getString(R.string.selectTransportation));
        transportationNameValue.add(getString(R.string.ambulanceValue));
        transportationNameValue.add(getString(R.string.trans2Value));

        spinnerTransportation.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, transportationName));
        spinnerTransportation.setSelection(0);

        relationList.clear();
        relationList.add(getString(R.string.relation));
        relationList.add(getString(R.string.mother));
        relationList.add(getString(R.string.father));
//        relationList.add(getString(R.string.otherRelative));
        relationList.add(getString(R.string.otherSpecify));

        relationValue.clear();
        relationValue.add(getString(R.string.relation));
        relationValue.add(getString(R.string.motherValue));
        relationValue.add(getString(R.string.fatherValue));
//        relationValue.add(getString(R.string.otherRelativeValue));
        relationValue.add(getString(R.string.otherValue));

        spinnerRelation.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, relationList));
        spinnerRelation.setSelection(0);

        //Spinner for relationList
        spinnerRelation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                etSpecify.setText("");
                if (position == 3) {
                    rlSpecify.setVisibility(View.VISIBLE);
                } else {
                    rlSpecify.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        //setOnClickListener
        rlTrainedYes.setOnClickListener(this);
        rlTrainedNo.setOnClickListener(this);
        rlLengthYes.setOnClickListener(this);
        rlLengthNo.setOnClickListener(this);
        rlHeadYes.setOnClickListener(this);
        rlHeadNo.setOnClickListener(this);
        rlNext.setOnClickListener(this);
        rlPrevious.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
        ivClear.setOnClickListener(this);

        step = 1;
        llStep1.setVisibility(View.VISIBLE);
        llStep2.setVisibility(View.GONE);
        llStep3.setVisibility(View.GONE);
        rlNext.setVisibility(View.VISIBLE);
        rlPrevious.setVisibility(View.GONE);
        tvSubmit.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tvSubmit:
            case R.id.rlNext:

                validation();

                break;

            case R.id.rlPrevious:

                if (step == 3) {
                    step = 2;
                    llStep1.setVisibility(View.GONE);
                    llStep2.setVisibility(View.VISIBLE);
                    llStep3.setVisibility(View.GONE);
                    rlNext.setVisibility(View.VISIBLE);
                    rlPrevious.setVisibility(View.VISIBLE);
                    tvSubmit.setVisibility(View.GONE);
                } else if (step == 2) {
                    step = 1;
                    llStep1.setVisibility(View.VISIBLE);
                    llStep2.setVisibility(View.GONE);
                    llStep3.setVisibility(View.GONE);
                    rlNext.setVisibility(View.VISIBLE);
                    rlPrevious.setVisibility(View.GONE);
                    tvSubmit.setVisibility(View.GONE);
                }

            case R.id.rlTrainedYes:

                setDefaultTrained();
                ivTrainedYes.setImageResource(R.drawable.ic_check_box_selected);
                trained = getString(R.string.yesValue);

                break;

            case R.id.rlTrainedNo:

                setDefaultTrained();
                ivTrainedNo.setImageResource(R.drawable.ic_check_box_selected);
                trained = getString(R.string.noValue);

                break;

            case R.id.rlLengthYes:

                setDefaultLength();
                ivLengthYes.setImageResource(R.drawable.ic_check_box_selected);
                etBabyLength.setEnabled(true);
                lengthTape = getString(R.string.yesValue);
                rlReason.setVisibility(View.GONE);
                rl_length.setVisibility(View.VISIBLE);
                rlNote.setVisibility(View.GONE);

                break;

            case R.id.rlLengthNo:

                setDefaultLength();
                ivLengthNo.setImageResource(R.drawable.ic_check_box_selected);
                etBabyLength.setEnabled(false);
                lengthTape = getString(R.string.noValue);
                rlReason.setVisibility(View.VISIBLE);
                rl_length.setVisibility(View.GONE);


                break;

            case R.id.rlHeadYes:

                setDefaultHead();
                ivHeadYes.setImageResource(R.drawable.ic_check_box_selected);
                etBabyHead.setEnabled(true);
                inchiTape = getString(R.string.yesValue);

                rlHReason.setVisibility(View.GONE);
                rl_Hlength.setVisibility(View.VISIBLE);
                rlHNote.setVisibility(View.GONE);

                break;

            case R.id.rlHeadNo:

                setDefaultHead();
                ivHeadNo.setImageResource(R.drawable.ic_check_box_selected);
                etBabyHead.setEnabled(false);
                inchiTape = getString(R.string.noValue);

                rlHReason.setVisibility(View.VISIBLE);
                rl_Hlength.setVisibility(View.GONE);

                break;

            case R.id.ivClear:

                signaturePad.clear();

                break;

            default:

                break;
        }
    }

    private void setDefaultTrained() {
        trained = "";
        ivTrainedYes.setImageResource(R.drawable.ic_check_box);
        ivTrainedNo.setImageResource(R.drawable.ic_check_box);
    }

    private void setDefaultLength() {
        lengthTape = "";
        ivLengthYes.setImageResource(R.drawable.ic_check_box);
        ivLengthNo.setImageResource(R.drawable.ic_check_box);
        etBabyLength.setText("");
    }

    private void setDefaultHead() {
        inchiTape = "";
        ivHeadYes.setImageResource(R.drawable.ic_check_box);
        ivHeadNo.setImageResource(R.drawable.ic_check_box);
        etBabyHead.setText("");
    }


    private void validation() {

        if (lengthTape.equals(getString(R.string.noValue)) && spinnerReason.getSelectedItemPosition()==0 && step == 2)
        {
            AppUtils.showToastSort(mActivity, getString(R.string.selectReasonNotMeasurLength));
            return;
        }

        if (lengthTape.equals(getString(R.string.noValue)) && spinnerReason.getSelectedItem().equals(getResources().getString(R.string.other)) && step == 2 && etReason.getText().toString().equalsIgnoreCase(""))
        {
            AppUtils.showToastSort(mActivity, getString(R.string.enterReasonNotMeasurLength));
            return;
        }


        if (inchiTape.equals(getString(R.string.noValue)) && spinnerHReason.getSelectedItemPosition()==0 && step == 2)
        {
            AppUtils.showToastSort(mActivity, getString(R.string.selectReasonNotMeasurHead));
            return;
        }

        if (inchiTape.equals(getString(R.string.noValue)) && spinnerHReason.getSelectedItem().equals(getResources().getString(R.string.other)) && step == 2 && etHReason.getText().toString().equalsIgnoreCase(""))
        {
            AppUtils.showToastSort(mActivity, getString(R.string.enterReasonNotMeasurHead));
            return;
        }



        if (spinnerDoctor.getSelectedItemPosition() == 0 && step == 1) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorselectDoctor));
        } else if (step == 1) {
            step = 2;
            llStep1.setVisibility(View.GONE);
            llStep2.setVisibility(View.VISIBLE);
            llStep3.setVisibility(View.GONE);
            rlNext.setVisibility(View.VISIBLE);
            rlPrevious.setVisibility(View.VISIBLE);
            tvSubmit.setVisibility(View.GONE);
        } else if (trained.isEmpty() && step == 2) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorsufficientlyTrained));
        } else if (lengthTape.isEmpty() && step == 2) {
            AppUtils.showToastSort(mActivity, getString(R.string.isLengthTapeAvailable));
        } else if (lengthTape.equals(getString(R.string.yesValue)) && etBabyLength.getText().toString().trim().isEmpty() && step == 2) {
            etBabyLength.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorLength));
        } else if (inchiTape.isEmpty() && step == 2) {
            AppUtils.showToastSort(mActivity, getString(R.string.isHeadTapeAvailable));
        } else if (inchiTape.equals(getString(R.string.yesValue)) && etBabyHead.getText().toString().trim().isEmpty() && step == 2) {
            etBabyHead.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorHead));
        } else if (etDischargeNotes.getText().toString().trim().isEmpty() && step == 2) {
            etDischargeNotes.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorDischargeNotes));
        } else if (step == 2) {
            step = 3;
            llStep1.setVisibility(View.GONE);
            llStep2.setVisibility(View.GONE);
            llStep3.setVisibility(View.VISIBLE);
            rlNext.setVisibility(View.GONE);
            rlPrevious.setVisibility(View.VISIBLE);
            tvSubmit.setVisibility(View.VISIBLE);
        } else if (etGuardianName.getText().toString().trim().isEmpty() && step == 3) {
            etGuardianName.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorGuardianName));
        } else if (spinnerRelation.getSelectedItemPosition() == 0 && step == 3) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorrelationWithChildMand));
        } else if ((spinnerRelation.getSelectedItemPosition() == 3
                || spinnerRelation.getSelectedItemPosition() == 4)
                && etSpecify.getText().toString().trim().isEmpty()
                && step == 3) {
            etSpecify.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.anyOtherPleaseSpecify));
        } else if (spinnerNurse.getSelectedItemPosition() == 0 && step == 3) {
            AppUtils.showToastSort(mActivity, getString(R.string.erroselectNurse));
        } else if (signaturePad.isEmpty() && step == 3) {
            AppUtils.showToastSort(mActivity, getString(R.string.errornurseSignature));
        } else if (spinnerTransportation.getSelectedItemPosition() == 0 && step == 3) {
            AppUtils.showToastSort(mActivity, getString(R.string.selectTransportation));
        } else {
            Bitmap bm = signaturePad.getSignatureBitmap();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            encodedSign = Base64.encodeToString(byteArray, Base64.DEFAULT);

            if (AppUtils.isNetworkAvailable(mActivity)) {
                doDischargeApi();
            } else {
                AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
            }
        }


    }

    private void doDischargeApi() {
        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("babyId", AppSettings.getString(AppSettings.babyId));
            jsonData.put("trainForKMCAtHome", trained);
            jsonData.put("infantometerAvailability", lengthTape);
            jsonData.put("babyLength", etBabyLength.getText().toString().trim());
            jsonData.put("measuringTapeAvailability", inchiTape);
            jsonData.put("headCircumference", etBabyHead.getText().toString().trim());
            jsonData.put("isMotherDischarge", "");
            jsonData.put("dischargeNotes", etDischargeNotes.getText().toString().trim());
            jsonData.put("attendantName", etGuardianName.getText().toString().trim());
            jsonData.put("relationWithInfant", relationValue.get(spinnerRelation.getSelectedItemPosition()));
            jsonData.put("otherRelation", etSpecify.getText().toString().trim());
            jsonData.put("dischargeByDoctor", doctorId.get(spinnerDoctor.getSelectedItemPosition()));
            jsonData.put("dischargeByNurse", nurseId.get(spinnerNurse.getSelectedItemPosition()));
            jsonData.put("transportation", transportationNameValue.get(spinnerTransportation.getSelectedItemPosition()));
            jsonData.put("signOfNurse", encodedSign);
            jsonData.put("bodyHandover", "");
            jsonData.put("typeOfDischarge", getString(R.string.discharge3Value));
            jsonData.put("guardianName", etGuardianName.getText().toString().trim());
            jsonData.put("babyHandoverImage", "");
            jsonData.put("guardianIdImage", "");
            jsonData.put("referredDistrict", "");
            jsonData.put("referredFacilityName", "");
            jsonData.put("referredUnit", "");
            jsonData.put("referredReason", "");
            jsonData.put("referralNotes", etDischargeNotes.getText().toString().trim());
            jsonData.put("sehmatiPatr", "");
            jsonData.put("earlyDishargeReason", "");
            jsonData.put("isAbsconded", "");
            jsonData.put("dateOfDischarge", "");

            jsonData.put("lengthMeasureNotAvlReasonDischarge", "");
            jsonData.put("lengthMeasureNotAvlReasonOtherDischarge", "");
            jsonData.put("headMeasureNotAvlReasonDischarge", "");
            jsonData.put("headMeasureNotAvlReasonOtherDischarge", "");

            if(lengthTape.equalsIgnoreCase(getString(R.string.noValue)))
            {
                jsonData.put("lengthMeasureNotAvlReasonDischarge", spinnerReason.getSelectedItem());

            }
            if(lengthTape.equalsIgnoreCase(getString(R.string.noValue))  && spinnerReason.getSelectedItem().equals(getResources().getString(R.string.other)))
            {
                jsonData.put("lengthMeasureNotAvlReasonOtherDischarge", etReason.getText().toString());

            }

            if(inchiTape.equalsIgnoreCase(getString(R.string.noValue)))
            {
                jsonData.put("headMeasureNotAvlReasonDischarge", spinnerHReason.getSelectedItem());

            }

            if(inchiTape.equalsIgnoreCase(getString(R.string.noValue))  && spinnerHReason.getSelectedItem().equals(getResources().getString(R.string.other)))
            {
                jsonData.put("headMeasureNotAvlReasonOtherDischarge", etReason.getText().toString());

            }


            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));

            Log.v("doDischargeApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.babyDischarge, json, true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));

                        DatabaseController.delete(TableBabyRegistration.tableName, "babyId = '" + AppSettings.getString(AppSettings.babyId) + "'", null);
                        DatabaseController.delete(TableBabyAdmission.tableName, "babyId = '" + AppSettings.getString(AppSettings.babyId) + "'", null);
                        DatabaseController.delete(TableBabyMonitoring.tableName, "babyId = '" + AppSettings.getString(AppSettings.babyId) + "'", null);
                        DatabaseController.delete(TableBreastFeeding.tableName, "babyId = '" + AppSettings.getString(AppSettings.babyId) + "'", null);
                        DatabaseController.delete(TableComments.tableName, "motherOrBabyId = '" + AppSettings.getString(AppSettings.babyId) + "'", null);
                        DatabaseController.delete(TableWeight.tableName, "babyId = '" + AppSettings.getString(AppSettings.babyId) + "'", null);
                        DatabaseController.delete(TableKMC.tableName, "babyId = '" + AppSettings.getString(AppSettings.babyId) + "'", null);
                        DatabaseController.delete(TableDoctorRound.tableName, "babyId = '" + AppSettings.getString(AppSettings.babyId) + "'", null);
                        DatabaseController.delete(TableInvestigation.tableName, "babyId = '" + AppSettings.getString(AppSettings.babyId) + "'", null);
                        DatabaseController.delete(TableTreatment.tableName, "babyId = '" + AppSettings.getString(AppSettings.babyId) + "'", null);
                        DatabaseController.delete(TableVaccination.tableName, "babyId = '" + AppSettings.getString(AppSettings.babyId) + "'", null);
                        DatabaseController.delete(TableNotification.tableName, "babyId = '" + AppSettings.getString(AppSettings.babyId) + "'", null);

                        AppUtils.AlertOkMother(getString(R.string.kmcPositionDuringDischarge), mActivity);

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
