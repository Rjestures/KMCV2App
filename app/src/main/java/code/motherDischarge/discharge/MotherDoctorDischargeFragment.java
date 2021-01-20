package code.motherDischarge.discharge;

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
import code.database.TableMotherAdmission;
import code.database.TableMotherMonitoring;
import code.database.TableMotherRegistration;
import code.database.TableNotification;
import code.database.TableTreatment;
import code.database.TableVaccination;
import code.database.TableWeight;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;


public class MotherDoctorDischargeFragment extends BaseFragment implements View.OnClickListener {

    //EditText
    private EditText etReasonForDischarge,etDischargeNotes,etGuardianName,etSpecify;

    //RelativeLayout
    private RelativeLayout rlSpecify,rlNext,rlPrevious,rlTrainedYes,rlTrainedNo;

    //ImageView
    private ImageView ivTrainedYes,ivTrainedNo,ivClear;

    //LinearLayout
    private LinearLayout llStep1,llStep2;

    //Spinner
    private Spinner spinnerRelation,spinnerDoctor,spinnerNurse,spinnerTransportation;

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

    private String trained = "",encodedSign="";

    private  int step=1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_mother_doctor_discharge,container,false);

        initialize(view);

        return view;
    }

    private void initialize(View v) {

        //EditText
        etReasonForDischarge= v.findViewById(R.id.etReasonForDischarge);
        etDischargeNotes= v.findViewById(R.id.etDischargeNotes);
        etGuardianName= v.findViewById(R.id.etGuardianName);
        etSpecify= v.findViewById(R.id.etSpecify);

        //RelativeLayout
        rlTrainedYes= v.findViewById(R.id.rlTrainedYes);
        rlTrainedNo= v.findViewById(R.id.rlTrainedNo);
        rlSpecify= v.findViewById(R.id.rlSpecify);
        rlNext= v.findViewById(R.id.rlNext);
        rlPrevious= v.findViewById(R.id.rlPrevious);

        //LinearLayout
        llStep1 =  v.findViewById(R.id.llStep1);
        llStep2 =  v.findViewById(R.id.llStep2);

        //TextView
        tvSubmit =  v.findViewById(R.id.tvSubmit);

        //ImageView
        ivTrainedYes= v.findViewById(R.id.ivTrainedYes);
        ivTrainedNo= v.findViewById(R.id.ivTrainedNo);
        ivClear= v.findViewById(R.id.ivClear);

        //Spinner
        spinnerRelation =  v.findViewById(R.id.spinnerRelation);
        spinnerDoctor =  v.findViewById(R.id.spinnerDoctor);
        spinnerNurse =  v.findViewById(R.id.spinnerNurse);
        spinnerTransportation =  v.findViewById(R.id.spinnerTransportation);

        //SignaturePad
        signaturePad=  v.findViewById(R.id.signaturePad);

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
        relationList.add(getString(R.string.husband));
        relationList.add(getString(R.string.otherRelative));
        relationList.add(getString(R.string.otherSpecify));

        relationValue.clear();
        relationValue.add(getString(R.string.relation));
        relationValue.add(getString(R.string.motherValue));
        relationValue.add(getString(R.string.fatherValue));
        relationValue.add(getString(R.string.husbandValue));
        relationValue.add(getString(R.string.otherRelativeValue));
        relationValue.add(getString(R.string.otherValue));

        spinnerRelation.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, relationList));
        spinnerRelation.setSelection(0);

        //Spinner for relationList
        spinnerRelation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                etSpecify.setText("");
                if(position==4||position==5)
                {
                    rlSpecify.setVisibility(View.VISIBLE);
                }
                else
                {
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
        rlNext.setOnClickListener(this);
        rlPrevious.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
        ivClear.setOnClickListener(this);

        step=1;
        llStep1.setVisibility(View.VISIBLE);
        llStep2.setVisibility(View.GONE);
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

                if(step==2)
                {
                    step=1;
                    llStep1.setVisibility(View.VISIBLE);
                    llStep2.setVisibility(View.GONE);
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

            case R.id.ivClear:

                signaturePad.clear();

                break;

            default:

                break;
        }
    }

    private void setDefaultTrained() {
        trained="";
        ivTrainedYes.setImageResource(R.drawable.ic_check_box);
        ivTrainedNo.setImageResource(R.drawable.ic_check_box);
    }


    private void validation() {

        if(etReasonForDischarge.getText().toString().trim().isEmpty()&&step==1)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.errorEnterReason));
        }
        else if(spinnerDoctor.getSelectedItemPosition()==0&&step==1)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.selectDoctor));
        }
        else if(step==1)
        {
            step=2;
            llStep1.setVisibility(View.GONE);
            llStep2.setVisibility(View.VISIBLE);
            rlNext.setVisibility(View.GONE);
            rlPrevious.setVisibility(View.VISIBLE);
            tvSubmit.setVisibility(View.VISIBLE);
        }
        else if(trained.isEmpty()&&step==2)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.motherSufficientlyTrained));
        }
        else if(etDischargeNotes.getText().toString().trim().isEmpty()&&step==2)
        {
            etDischargeNotes.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.errorDischargeNotes));
        }
        else if(etGuardianName.getText().toString().trim().isEmpty()&&step==2)
        {
            etGuardianName.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.errorGuardianName));
        }
        else if(spinnerRelation.getSelectedItemPosition()==0&&step==2)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.relationWithMotherMand));
        }
//        else if((spinnerRelation.getSelectedItemPosition()==3
//                         ||spinnerRelation.getSelectedItemPosition()==4)
//                        &&etSpecify.getText().toString().trim().isEmpty()
//                        &&step==2)
//        {
//            etSpecify.requestFocus();
//            AppUtils.showToastSort(mActivity,getString(R.string.anyOtherPleaseSpecify));
//        }
        else if(spinnerNurse.getSelectedItemPosition()==0&&step==2)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.selectNurse));
        }
        else if(signaturePad.isEmpty()&&step==2)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.nurseSignature));
        }
        else if(spinnerTransportation.getSelectedItemPosition()==0&&step==2)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.selectTransportation));
        }
        else
        {
            Bitmap bm =  signaturePad.getSignatureBitmap();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();

            encodedSign = Base64.encodeToString(byteArray, Base64.DEFAULT);

            if (AppUtils.isNetworkAvailable(mActivity)) {
                doDischargeApi();
            } else {
                AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
            }
        }


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

        private View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            View row=inflater.inflate(R.layout.inflate_spinner_new, parent, false);

            TextView tvName=row.findViewById(R.id.tvName);

            tvName.setText(data.get(position));

            return row;
        }
    }

    private void doDischargeApi() {

        JSONObject json = new JSONObject();

        JSONObject jsonData = new JSONObject();

        try {

            jsonData.put( "loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put( "motherId", AppSettings.getString(AppSettings.motherId));
            jsonData.put( "trainForKMCAtHome",trained);
            jsonData.put( "dischargeNotes",etDischargeNotes.getText().toString().trim());
            jsonData.put( "attendantName",etGuardianName.getText().toString().trim());
            jsonData.put( "relationWithMother",relationValue.get(spinnerRelation.getSelectedItemPosition()));
            jsonData.put( "otherRelation",etSpecify.getText().toString().trim());
            jsonData.put( "dischargeByDoctor",doctorId.get(spinnerDoctor.getSelectedItemPosition()));
            jsonData.put( "dischargeByNurse",nurseId.get(spinnerNurse.getSelectedItemPosition()));
            jsonData.put( "transportation",transportationNameValue.get(spinnerTransportation.getSelectedItemPosition()));
            jsonData.put( "signOfNurse",encodedSign);
            jsonData.put( "typeOfDischarge",getString(R.string.discharge4Value));
            jsonData.put( "guardianName",etGuardianName.getText().toString().trim());
            jsonData.put( "bodyHandover","");
            jsonData.put( "referredDistrict","");
            jsonData.put( "referredFacilityName","");
            jsonData.put( "referredUnit","");
            jsonData.put( "referredReason","");
            jsonData.put( "referralNotes",etDischargeNotes.getText().toString().trim());
            jsonData.put( "sehmatiPatr","");
            jsonData.put( "earlyDishargeReason",etReasonForDischarge.getText().toString().trim());
            jsonData.put( "isAbsconded","");
            jsonData.put( "dateOfDischarge","");

            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));

            Log.v("doDischargeApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.motherDischarge, json,true,true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));

                        DatabaseController.delete(TableMotherRegistration.tableName,"motherId = '"+AppSettings.getString(AppSettings.motherId)+"'",null);
                        DatabaseController.delete(TableMotherAdmission.tableName,"motherId = '"+AppSettings.getString(AppSettings.motherId)+"'",null);
                        DatabaseController.delete(TableMotherMonitoring.tableName,"motherId = '"+AppSettings.getString(AppSettings.motherId)+"'",null);

                        AppUtils.AlertOkMother(getString(R.string.motherDischargeSuccessfullt),mActivity);

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
}
