package code.main;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kmcapp.android.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import code.algo.SyncAllRecord;
import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.common.LocaleHelper;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.registration.RegistrationActivity;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseActivity;

public class ReferralListActivity extends BaseActivity implements View.OnClickListener {

    //RecyclerView
    RecyclerView recyclerView;

    //GridLayoutManager
    GridLayoutManager mGridLayoutManager;

    Adapter adapter;

    ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();

    //TextView
    TextView tvNoRecord,tvSearchCriteria;

    int type=0,offset=0;

    public static String uuid;

    //RelativeLayout
    RelativeLayout rlCircle,rlName,rlDelDate,rlHRN;

    //ImageView
    ImageView ivSearchCriteria,ivName,ivDelDate,ivHRN,ivDeliveryDate;

    //EditText
    EditText etData;

    private ArrayList<String> districtId = new ArrayList<String>();
    private ArrayList<String> districtName = new ArrayList<String>();

    private ArrayList<String> facilityId = new ArrayList<String>();
    private ArrayList<String> facilityName = new ArrayList<String>();

    //Spinner
    private Spinner spinnerDistrict,spinnerFacility;

    //LinearLayout
    private LinearLayout llSync,llHelp,llLogout,llLanguage;

    //RelativeLayout
    private RelativeLayout rlHelp,rlStuck,rlOwn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_referral);

        initialize();
    }

    private void initialize() {

        //Spinner
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerFacility = findViewById(R.id.spinnerFacility);

        //RecyclerView
        recyclerView = findViewById(R.id.recyclerView);

        //LinearLayout
        llSync      = findViewById(R.id.llSync);
        llHelp      = findViewById(R.id.llHelp);
        llLogout = findViewById(R.id.llLogout);
        llLanguage = findViewById(R.id.llLanguage);

        //RelativeLayout
        rlHelp= findViewById(R.id.rlHelp);
        rlStuck= findViewById(R.id.rlStuck);
        rlOwn= findViewById(R.id.rlOwn);

        //TextView
        tvNoRecord = findViewById(R.id.tvNoRecord);
        tvSearchCriteria = findViewById(R.id.tvSearchCriteria);

        //RelativeLayout
        rlName = findViewById(R.id.rlName);
        rlDelDate = findViewById(R.id.rlDelDate);
        rlHRN = findViewById(R.id.rlHRN);
        rlCircle = findViewById(R.id.rlCircle);

        //ImageView
        ivSearchCriteria = findViewById(R.id.ivSearchCriteria);
        ivName = findViewById(R.id.ivName);
        ivDelDate = findViewById(R.id.ivDelDate);
        ivHRN = findViewById(R.id.ivHRN);
        ivDeliveryDate = findViewById(R.id.ivDeliveryDate);

        //EditText
        etData = findViewById(R.id.etData);

        mGridLayoutManager = new GridLayoutManager(mActivity, 1);
        recyclerView.setLayoutManager(mGridLayoutManager);

        adapter = new Adapter(AppConstants.searchList);
        recyclerView.setAdapter(adapter);

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
                    facilityName.addAll(DatabaseController.getFacNameData(districtId.get(position)));

                    facilityId.clear();
                    facilityId.add(getString(R.string.selectFacility));
                    facilityId.addAll(DatabaseController.getFacIdData(districtId.get(position)));

                    spinnerFacility.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, facilityName));
                    spinnerFacility.setSelection(0);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });


        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int ydy = 0;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {

                } else {
                    // Scrolling down
                    int lastVisiblePosition = mGridLayoutManager.findLastCompletelyVisibleItemPosition();
                    Log.d("LastVisibleItemPosition", String.valueOf(lastVisiblePosition));

                    if(lastVisiblePosition % 20 == 0&&lastVisiblePosition>0)
                    {
                        if (AppUtils.isNetworkAvailable(mActivity)) {
                            getBabyViaLoungeIdApi();
                        } else {
                            AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                        }
                    }

                }
            }
        });

        AppUtils.hideSoftKeyboard(mActivity);

        //setOnClickListener
        rlCircle.setOnClickListener(this);
        rlName.setOnClickListener(this);
        rlDelDate.setOnClickListener(this);
        rlHRN.setOnClickListener(this);
        ivDeliveryDate.setOnClickListener(this);
        llSync.setOnClickListener(this);
        llHelp.setOnClickListener(this);
        llLogout.setOnClickListener(this);
        llLanguage.setOnClickListener(this);
        rlHelp.setOnClickListener(this);
        rlStuck.setOnClickListener(this);
        rlOwn.setOnClickListener(this);

        setDefault();
        type=1;
        ivName.setImageResource(R.drawable.ic_check_box_selected);
        ivSearchCriteria.setImageResource(R.drawable.ic_mother_demo);
        etData.setInputType(InputType.TYPE_CLASS_TEXT);
        etData.setEnabled(true);
        etData.setClickable(false);
        etData.setFilters(new InputFilter[] { new InputFilter.LengthFilter(100) });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
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

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

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

            case R.id.rlCircle:

                if(spinnerDistrict.getSelectedItemPosition()==0)
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.selectDistrict));
                }
                else  if(spinnerFacility.getSelectedItemPosition()==0)
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.selectFacility));
                }
                else  if(type==0)
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.selectToSearch));
                }
                else  if(type==1&&etData.getText().toString().trim().isEmpty())
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorEnterMother));
                }
                else  if(type==2&&etData.getText().toString().trim().isEmpty())
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.deliveryDateMand));
                }
                else  if(type==3&&etData.getText().toString().trim().isEmpty())
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorEnterHRN));
                }
                else if (AppUtils.isNetworkAvailable(mActivity)) {
                    arrayList.clear();
                    getBabyViaLoungeIdApi();
                } else {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                }

                break;

            case R.id.rlName:

                setDefault();
                type=1;
                ivName.setImageResource(R.drawable.ic_check_box_selected);
                ivSearchCriteria.setImageResource(R.drawable.ic_mother_demo);
                tvSearchCriteria.setText(getString(R.string.motherGuardianName));
                etData.setInputType(InputType.TYPE_CLASS_TEXT);
                etData.setEnabled(true);
                etData.setText("");
                ivDeliveryDate.setVisibility(View.GONE);
                etData.setFilters(new InputFilter[] { new InputFilter.LengthFilter(100) });

                break;

            case R.id.rlDelDate:

                setDefault();
                type=2;
                ivDelDate.setImageResource(R.drawable.ic_check_box_selected);
                ivSearchCriteria.setImageResource(R.drawable.ic_calender);
                tvSearchCriteria.setText(getString(R.string.deliveryDate));
                etData.setInputType(InputType.TYPE_CLASS_TEXT);
                etData.setEnabled(false);
                etData.setText("");
                ivDeliveryDate.setVisibility(View.VISIBLE);
                etData.setFilters(new InputFilter[] { new InputFilter.LengthFilter(100) });

                break;

            case R.id.rlHRN:

                setDefault();
                type=3;
                ivHRN.setImageResource(R.drawable.ic_check_box_selected);
                ivSearchCriteria.setImageResource(R.drawable.ic_mother_demo);
                ivSearchCriteria.setImageResource(R.drawable.ic_registration_number);
                tvSearchCriteria.setText(getString(R.string.hospitalRegistrationNumber));
                etData.setEnabled(true);
                etData.setText("");
                ivDeliveryDate.setVisibility(View.GONE);
                etData.setKeyListener(DigitsKeyListener.getInstance("0123456789/"));

                break;

            case R.id.ivDeliveryDate:

                dateDialog(mActivity,etData,1);

                break;

            default:

                break;
        }
    }

    private void setDefault() {

        type=0;

        ivName.setImageResource(R.drawable.ic_check_box);
        ivDelDate.setImageResource(R.drawable.ic_check_box);
        ivHRN.setImageResource(R.drawable.ic_check_box);
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        public Adapter(ArrayList<HashMap<String, String>> favList) {
            data = favList;
        }

        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_search_mother, parent, false));
        }

        @SuppressLint("SetTextI18n")
        public void onBindViewHolder(Holder holder, final int position) {

            String str1= "";
            String mStringDate="";
            String dischargeDateTime="";

            if(!data.get(position).get("admissionDateTime").isEmpty()){
                 mStringDate = data.get(position).get("admissionDateTime");
            }

            if(!data.get(position).get("dischargeDateTime").isEmpty()){
                dischargeDateTime = data.get(position).get("dischargeDateTime");
            }

            String oldFormat= "yyyy-MM-dd HH:mm:ss";
            String newFormat= "yyyy-MM-dd";
            String formatedDate = "";
            SimpleDateFormat dateFormat = new SimpleDateFormat(oldFormat);
            Date myDate = null;
            Date myDatedischarge = null;
            try {

                if(!mStringDate.equals("")){
                    myDate = dateFormat.parse(mStringDate);
                    SimpleDateFormat timeFormat = new SimpleDateFormat(newFormat);
                    formatedDate = timeFormat.format(myDate);
                }

                if(!dischargeDateTime.equals("")){
                    myDatedischarge = dateFormat.parse(dischargeDateTime);

                    SimpleDateFormat timeFormat = new SimpleDateFormat(newFormat);
                    dischargeDateTime = timeFormat.format(myDatedischarge);

                }


            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }


            if(!data.get(position).get("dischargeDateTime").isEmpty())
            {
//                str1= getString(R.string.lastLounge) +": "+data.get(position).get("loungeName")
//                        +"\n"+ getString(R.string.admissionDate) +": "+data.get(position).get("admissionDateTime")
//                        +"\n"+getString(R.string.dischargeDate) +": "+data.get(position).get("dischargeDateTime")
//                        +"\n"+getString(R.string.dischargeByDoctor) +": "+data.get(position).get("dischargeByDoctor");

                str1= getString(R.string.lastLounge) +": "+data.get(position).get("loungeName")
                        +"\n"+ getString(R.string.admissionDate) +": "+formatedDate
                        +"\n"+getString(R.string.dischargeDate) +": "+dischargeDateTime
                        +"\n"+getString(R.string.dischargeByDoctor) +": "+data.get(position).get("dischargeByDoctor");
            }
            else
            {
//                str1= getString(R.string.currentLounge) +": "+data.get(position).get("loungeName")
//                        +"\n"+ getString(R.string.admissionDate) +": "+data.get(position).get("admissionDateTime")
//                        +"\n"+getString(R.string.dischargeByDoctor) +": "+data.get(position).get("dischargeByDoctor");
                str1= getString(R.string.lastLounge) +": "+data.get(position).get("loungeName") +"\n"+ getString(R.string.admissionDate) +": "+formatedDate;
            }

            //mainStr = str1+"\n"+getString(R.string.last_assessment) +": "+data.get(position).get("last_assessment");

            holder.tvData.setText(str1);


            holder.tvMotherName.setText(mActivity.getString(R.string.motherName)+": "+data.get(position).get("motherName"));
            holder.tvRegId.setText(mActivity.getString(R.string.hospitalRegistrationNumber)+": "+data.get(position).get("babyFileId"));

            holder.rlMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    /*AppSettings.putString(AppSettings.motherId,data.get(position).get("mother_id"));
                    AppSettings.putString(AppSettings.motherName,data.get(position).get("mother_name"));
                    AppSettings.putString(AppSettings.motherPic,data.get(position).get("mother_pic"));
                    AppSettings.putString(AppSettings.motherAssDate,data.get(position).get("last_assessment"));
                    AppSettings.putString(AppSettings.motherStep,"0");
                    ((MainActivity)mActivity).displayView(4);*/

                }
            });

            holder.rlReAdmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (AppUtils.isNetworkAvailable(mActivity)) {
                        getDetailApi(data.get(position).get("babyId"));
                    } else {
                        AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                    }
                }
            });

            try {
                Picasso.get().load(data.get(position).get("babyPhoto")).into(holder.ivPic);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (holder.ivPic.getDrawable()==null){
                holder.ivPic.setImageResource(R.mipmap.baby);
            }

        }

        public int getItemCount() {
            return data.size();
        }

        private class Holder extends RecyclerView.ViewHolder {

            TextView tvName, tvData, tvMotherName, tvRegId;

            RelativeLayout rlMain,rlReAdmit;

            ImageView ivPic,ivStatus;

            View view;

            public Holder(View itemView) {
                super(itemView);
                //TextView
                tvName =  itemView.findViewById(R.id.tvName);
                tvData =  itemView.findViewById(R.id.tvData);
                rlReAdmit = itemView.findViewById(R.id.rlReAdmit);
                rlMain = itemView.findViewById(R.id.rlMain);
                //ImageView
                ivPic = itemView.findViewById(R.id.ivPic);
                ivStatus = itemView.findViewById(R.id.ivStatus);
                tvMotherName = itemView.findViewById(R.id.tvMotherName);
                tvRegId = itemView.findViewById(R.id.tvRegId);

            }
        }
    }

    private void getBabyViaLoungeIdApi() {

        AppUtils.hideSoftKeyboard(mActivity);

        JSONObject json = new JSONObject();

        JSONObject jsonData = new JSONObject();

        try {

            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("searchingFacilityId", facilityId.get(spinnerFacility.getSelectedItemPosition()));
            jsonData.put("deliveryDate", "");
            jsonData.put("motherName", "");
            jsonData.put("hospitalRegistrationNumber", "");
            jsonData.put("offset", offset);

            if(type==1)
            {
                jsonData.put("motherName", etData.getText().toString().trim());
            }
            else  if(type==2)
            {
                jsonData.put("deliveryDate", etData.getText().toString().trim());
            }
            else  if(type==3)
            {
                jsonData.put("hospitalRegistrationNumber", etData.getText().toString().trim());
            }

            json.put(AppConstants.projectName, jsonData);

            Log.v("getBabyViaLounge", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.searchBabyViaFacilityId, json,true,true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        JSONArray discharge = jsonObject.getJSONArray("babyData");

                        for (int i = 0; i < discharge.length(); i++) {

                            JSONObject dischargeJSONObject = discharge.getJSONObject(i);

                            HashMap<String, String> hashMap = new HashMap();

                            hashMap.put("babyId",dischargeJSONObject.getString("babyId"));
                            hashMap.put("babyPhoto",dischargeJSONObject.getString("babyPhoto"));
                            hashMap.put("motherId",dischargeJSONObject.getString("motherId"));
                            hashMap.put("motherName",dischargeJSONObject.getString("motherName"));
                            hashMap.put("motherPicture",dischargeJSONObject.getString("motherPicture"));
                            hashMap.put("loungeId",dischargeJSONObject.getString("loungeId"));
                            hashMap.put("loungeName",dischargeJSONObject.getString("loungeName"));
                            hashMap.put("babyFileId",dischargeJSONObject.getString("babyFileId"));

                            try {
                                hashMap.put("admissionDateTime", dischargeJSONObject.getString("admissionDateTime"));
                            } catch (NumberFormatException e) {
                                hashMap.put("admissionDateTime", "");
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            hashMap.put("dischargeDateTime",dischargeJSONObject.getString("dischargeDateTime"));
                            hashMap.put("dischargeByDoctor",dischargeJSONObject.getString("dischargeByDoctor"));

                            arrayList.add(hashMap);
                        }
                    } else {
                        AppUtils.hideDialog();
                        AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(arrayList.size()==0)
                {
                    tvNoRecord.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                else
                {
                    tvNoRecord.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter = new Adapter(arrayList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void OnFail(String responce) {

                arrayList.clear();
                adapter.notifyDataSetChanged();
                tvNoRecord.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                AppUtils.showToastSort(mActivity, responce);

            }
        });
    }

    private void getDetailApi(String babyId) {

        AppUtils.hideSoftKeyboard(mActivity);
        AppUtils.showRequestDialog(mActivity);
        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("babyId", babyId);
            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            json.put(AppConstants.projectName, jsonData);
            Log.v("getDetailApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.getDetailApi, json,true,true, new WebServicesCallback() {
            @Override
            public void ArrayData(ArrayList arrayList) {

            }
            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {
                try {
                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);
                    if (jsonObject.getString("resCode").equals("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            uuid = UUID.randomUUID().toString();
                            AppConstants.jsonObject = jsonArray.getJSONObject(i);
                            AppSettings.putString(AppSettings.isSibling,"0");
                            AppSettings.putString(AppSettings.from,"1");
                            AppSettings.putString(AppSettings.checkHRN,"2");
                            AppSettings.putString(AppSettings.moveStatus,"2");
                            AppSettings.putString(AppSettings.uuid,uuid);

                            startActivity(new Intent(mActivity, RegistrationActivity.class));
                            finish();
                        }
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


    public static String dateDialog(Context context, final EditText textView, int type) {
        final String[] times = {""};

        Calendar mcurrentDate= Calendar.getInstance();
        int year=mcurrentDate.get(Calendar.YEAR);
        int month=mcurrentDate.get(Calendar.MONTH);
        int day=mcurrentDate.get(Calendar.DAY_OF_MONTH);

        if(!textView.getText().toString().isEmpty())
        {
            String[] parts = textView.getText().toString().split("-");
            String part1 = parts[2];
            String part2 = parts[1];
            String part3 = parts[0];

            day = Integer.parseInt(part1);
            month = Integer.parseInt(part2);
            year = Integer.parseInt(part3);
        }

        DatePickerDialog mDatePicker1 =new DatePickerDialog(context, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday)
            {
                String dob = String.valueOf(new StringBuilder().append(selectedday).append("-").append(selectedmonth+1).append("-").append(selectedyear));
                Log.d("dob",dob);
                Log.d("dob",AppUtils.formatDate(selectedyear,selectedmonth,selectedday));
                //AppSettings.putString(AppSettings.from,formatDate(selectedyear,selectedmonth,selectedday));
                //tvDob.setText(dob);
                textView.setText(AppUtils.formatDate(selectedyear,selectedmonth,selectedday));
            }
        },year, month, day);
        //mDatePicker1.setTitle("Select Date");
        // TODO Hide Future Date Here

        if(type==1)
        {
            mDatePicker1.getDatePicker().setMaxDate(System.currentTimeMillis());
        }
        else if(type==2)
        {
            mDatePicker1.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        }
        else if(type==3)
        {
            mDatePicker1.getDatePicker().setMaxDate(System.currentTimeMillis());

            mcurrentDate.add(Calendar.YEAR, -60);
            mDatePicker1.getDatePicker().setMinDate(mcurrentDate.getTimeInMillis());
            mcurrentDate.add(Calendar.YEAR, +60);
            // Subtract 6 days from Calendar updated date
            mcurrentDate.add(Calendar.YEAR, -15);
            mDatePicker1.getDatePicker().setMaxDate(mcurrentDate.getTimeInMillis());
            //mDatePicker1.getDatePicker().setMinDate(System.currentTimeMillis());
            //mDatePicker1.getDatePicker().setMinDate(mcurrentDate.getTimeInMillis());
        }
        else if(type==4)
        {
            mDatePicker1.getDatePicker().setMaxDate(System.currentTimeMillis());
            mcurrentDate.add(Calendar.DATE, -30);
            mDatePicker1.getDatePicker().setMinDate(mcurrentDate.getTimeInMillis());
        }
        else if(type==5)
        {
            mDatePicker1.getDatePicker().setMaxDate(System.currentTimeMillis());
            mDatePicker1.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        }
        else
        {
            mDatePicker1.getDatePicker().setMaxDate(System.currentTimeMillis());
            // Subtract 6 days from Calendar updated date
            mcurrentDate.add(Calendar.DATE, -1);
            mDatePicker1.getDatePicker().setMinDate(mcurrentDate.getTimeInMillis());
        }

        // TODO Hide Past Date Here
        //set min todays date
        //mDatePicker1.getDatePicker().setMinDate(System.currentTimeMillis());

        mDatePicker1.show();

        return times[0];
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
