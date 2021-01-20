package code.motherDischarge.discharge;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import code.main.MainActivity;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;


public class MotherAbscondedDischargeFragment extends BaseFragment implements View.OnClickListener {

    //Spinner
    private Spinner spinnerNurse;

    //ImageView
    private ImageView ivClear,ivYes,ivNo;

    //SignaturePad
    private SignaturePad signaturePad;

    private ArrayList<String> nurseId = new ArrayList<String>();
    private ArrayList<String> nurseName = new ArrayList<String>();

    //TextView
    private TextView tvSubmit,tvYes,tvNo;

    private String absconded="",encodedSign="";

    //RelativeLayout
    private RelativeLayout rlNo,rlYes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_mother_absconded_discharge,container,false);

        initialize(view);

        return view;
    }

    private void initialize(View v) {

        //ImageView
        ivClear= v.findViewById(R.id.ivClear);
        ivYes= v.findViewById(R.id.ivYes);
        ivNo= v.findViewById(R.id.ivNo);

        //TextView
        tvSubmit= v.findViewById(R.id.tvSubmit);
        tvYes= v.findViewById(R.id.tvYes);
        tvNo= v.findViewById(R.id.tvNo);

        //RelativeLayout
        rlYes= v.findViewById(R.id.rlYes);
        rlNo= v.findViewById(R.id.rlNo);

        //Spinner
        spinnerNurse =  v.findViewById(R.id.spinnerNurse);

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

        //setOnClickListener
        ivClear.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
        rlYes.setOnClickListener(this);
        rlNo.setOnClickListener(this);

        setDefault();

    }

    private void setDefault() {

        absconded = "";

        rlYes.setBackgroundResource(R.drawable.rectangle_grey_oval);
        rlNo.setBackgroundResource(R.drawable.rectangle_grey_oval);

        tvYes.setTextColor(getResources().getColor(R.color.grey));
        tvNo.setTextColor(getResources().getColor(R.color.grey));

        ivYes.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
        ivNo.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tvSubmit:

                validation();

                break;

            case R.id.ivClear:

                signaturePad.clear();

                break;

            case R.id.rlYes:

                setDefault();
                absconded = getString(R.string.yesValue);
                rlYes.setBackgroundResource(R.drawable.rectangle_teal_round);
                tvYes.setTextColor(getResources().getColor(R.color.white));
                ivYes.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

                break;

            case R.id.rlNo:

                setDefault();
                absconded = getString(R.string.noValue);
                rlNo.setBackgroundResource(R.drawable.rectangle_red_round);
                tvNo.setTextColor(getResources().getColor(R.color.white));
                ivNo.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

                break;

            default:

                break;
        }
    }

    private void validation() {

        if(absconded.isEmpty())
        {
            AppUtils.showToastSort(mActivity,getString(R.string.familyAbsconded));
        }
        else if(spinnerNurse.getSelectedItemPosition()==0)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.selectNurse));
        }
        else if(signaturePad.isEmpty())
        {
            AppUtils.showToastSort(mActivity,getString(R.string.nurseSignature));
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
            jsonData.put( "dischargeNotes","");
            jsonData.put( "attendantName","");
            jsonData.put( "relationWithMother","");
            jsonData.put( "otherRelation","");
            jsonData.put( "dischargeByDoctor","");
            jsonData.put( "dischargeByNurse",nurseId.get(spinnerNurse.getSelectedItemPosition()));
            jsonData.put( "transportation","");
            jsonData.put( "signOfNurse",encodedSign);
            jsonData.put( "typeOfDischarge",getString(R.string.discharge5Value));
            jsonData.put( "guardianName","");
            jsonData.put( "bodyHandover","");
            jsonData.put( "referredDistrict","");
            jsonData.put( "referredFacilityName","");
            jsonData.put( "referredUnit","");
            jsonData.put( "referredReason","");
            jsonData.put( "referralNotes","");
            jsonData.put( "sehmatiPatr","");
            jsonData.put( "earlyDishargeReason","");
            jsonData.put( "isAbsconded",absconded);
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

                        AppSettings.putString(AppSettings.from,"0");
                        AppSettings.putString(AppSettings.babyId,"");
                        AppSettings.putString(AppSettings.babyAdmissionId,"");
                        AppSettings.putString(AppSettings.motherId,"");
                        Intent intent = new Intent(mActivity, MainActivity.class);
                        startActivity(intent);
                        mActivity.finishAffinity();

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
