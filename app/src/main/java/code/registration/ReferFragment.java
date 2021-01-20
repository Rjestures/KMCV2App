package code.registration;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.kmcapp.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableBabyAdmission;
import code.database.TableUser;
import code.main.MainActivity;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;


public class ReferFragment extends BaseFragment implements View.OnClickListener {

    private ArrayList<String> districtId = new ArrayList<String>();
    private ArrayList<String> districtName = new ArrayList<String>();

    private ArrayList<String> facilityId = new ArrayList<String>();
    private ArrayList<String> facilityName = new ArrayList<String>();

    //RelativeLayout
    private RelativeLayout rlNext;

    //Spinner
    private Spinner spinnerDistrict,spinnerFacility;

    //EditText
    private EditText etAddress;

    private ArrayAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.fragment_refer, container, false);

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
        etAddress = v.findViewById(R.id.etFacAddress);

        //RelativeLayout
        rlNext = v.findViewById(R.id.rlNext);

        //Spinner
        spinnerDistrict = v.findViewById(R.id.spinnerDistrict);
        spinnerFacility = v.findViewById(R.id.spinnerFacility);

        getDistrict();

        //Spinner
        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if(position==0)
                {
                    facilityName.clear();
                    facilityName.add(getString(R.string.selectFacility));
                    //facilityName.addAll(DatabaseController.getFacNameData(districtId.get(position)));

                    facilityId.clear();
                    facilityId.add(getString(R.string.selectFacility));
                    //facilityId.addAll(DatabaseController.getFacIdData(districtId.get(position)));

                    spinnerFacility.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, facilityName));
                    spinnerFacility.setSelection(0);
                }
                else
                {
                    facilityName.clear();
                    facilityName.add(getString(R.string.selectFacility));
                    ArrayList<String> facName=new ArrayList<>();
                    facName= DatabaseController.getFacNameData(districtId.get(position));
                    ArrayList<String> generated=removeStringFromArray(facName, AppSettings.getString(AppSettings.facName));
                    facilityName.addAll(generated);

                    facilityId.clear();
                    facilityId.add(getString(R.string.selectFacility));

                    ArrayList<String> facId=new ArrayList<>();
                    facId= DatabaseController.getFacIdData(districtId.get(position));
                    ArrayList<String> generatedList=removeStringFromArray(facId, AppSettings.getString(AppSettings.facId));
                    facilityId.addAll(generatedList);

                    spinnerFacility.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, facilityName));
                    spinnerFacility.setSelection(0);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });


        rlNext.setOnClickListener(this);

        AppUtils.hideSoftKeyboard(mActivity);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rlNext:

                if(spinnerDistrict.getSelectedItemPosition()==0)
                {
                    AppUtils.showToastSort(mActivity,getString(R.string.selectDistrict));
                }
                else   if(spinnerFacility.getSelectedItemPosition()==0)
                {
                    AppUtils.showToastSort(mActivity,getString(R.string.selectFacility));
                }
                else if (AppUtils.isNetworkAvailable(mActivity)) {
                    doReferApi();
                } else {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                }

                break;


            default:

                break;
        }
    }

    public void getDistrict() {

        districtId.clear();
        districtId.add(getString(R.string.selectDistrict));
        districtId.addAll(DatabaseController.getDistrictIdData("Rural"));
        districtId.add("0");

        districtName.clear();
        districtName.add(getString(R.string.selectDistrict));
        districtName.addAll(DatabaseController.getDistrictNameData("Rural"));
        districtName.add(getString(R.string.other));

        spinnerDistrict.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, districtName));
        spinnerDistrict.setSelection(0);

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


    private JSONObject createJsonForBabyAssessment() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();

        try {

            jsonData.put("loungeId" , AppSettings.getString(AppSettings.loungeId));
            jsonData.put("babyId", AppSettings.getString(AppSettings.babyId));
            jsonData.put("nurseId", AppSettings.getString(AppSettings.nurseId));
            jsonData.put("referredDistrict", districtId.get(spinnerDistrict.getSelectedItemPosition()));
            jsonData.put("referredFacility", facilityId.get(spinnerFacility.getSelectedItemPosition()));
            jsonData.put("referredFacilityAddress","");

            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;

    }

    private void doReferApi() {

        WebServices.postApi(mActivity, AppUrls.babyReferral, createJsonForBabyAssessment(),true,true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        saveData();

                        startActivity(new Intent(mActivity, MainActivity.class));
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

    private void saveData() {

        DatabaseController.delete(TableUser.tableName, TableUser.tableColumn.uuid + " = '" + RegistrationActivity.uuid + "'",null);

        ContentValues cvBabyAdm = new ContentValues();

        cvBabyAdm.put(TableBabyAdmission.tableColumn.uuid.toString(), RegistrationActivity.uuid);
        cvBabyAdm.put(TableBabyAdmission.tableColumn.serverId.toString(), AppSettings.getString(AppSettings.bAdmId));
        cvBabyAdm.put(TableBabyAdmission.tableColumn.babyId.toString(), AppSettings.getString(AppSettings.babyId));
        cvBabyAdm.put(TableBabyAdmission.tableColumn.districtReferred.toString(),  districtId.get(spinnerDistrict.getSelectedItemPosition()));
        cvBabyAdm.put(TableBabyAdmission.tableColumn.nameReferredFacility.toString(),  facilityId.get(spinnerFacility.getSelectedItemPosition()));
        cvBabyAdm.put(TableBabyAdmission.tableColumn.referredFacilityAddress.toString(), "");
        cvBabyAdm.put(TableBabyAdmission.tableColumn.admissionDateTime.toString(), AppUtils.currentTimestampFormat());
        cvBabyAdm.put(TableBabyAdmission.tableColumn.status.toString(), "3");

        DatabaseController.insertUpdateData(cvBabyAdm, TableBabyAdmission.tableName,
                TableBabyAdmission.tableColumn.uuid.toString(), String.valueOf(RegistrationActivity.uuid));
    }


    public ArrayList<String> removeStringFromArray(ArrayList<String> arrayList,String value)
    {
        for(int i=0;i<arrayList.size();i++)
        {
            if(arrayList.get(i).equalsIgnoreCase(value))
            {
                arrayList.remove(i);
                return  arrayList;
            }
        }
        return arrayList;
    }


}
