package code.infantsFragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kmcapp.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import code.algo.SyncBabyRecord;
import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableBabyMonitoring;
import code.database.TableBabyRegistration;
import code.database.TableInvestigation;
import code.database.TableWeight;
import code.main.BabyAssessmentActivity;
import code.main.WeightAddActivity;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;


public class WeightAssessmentFragment extends BaseFragment implements View.OnClickListener {

    //RecyclerView
    private RecyclerView rvWeight,rvAssessment,rvTests;

    //GridLayoutManager
    private GridLayoutManager mGridLayoutManager, mAssessGridLayoutManager,mTestsGridLayoutManager;

    //MedicineAdapter
    private WeightAdapter weightAdapter;

    //Adapter
    private AssessmentAdapter assessmentAdapter;

    //Adapter
    private TestsAdapter testsAdapter;

    //RelativeLayout
    private RelativeLayout rlMenu,rlCircle,rlExpandMenu;

    private ArrayList<String> nurseId = new ArrayList<String>();
    private ArrayList<String> nurseName = new ArrayList<String>();

    private ArrayList<String> investigationName = new ArrayList<String>();

    //LinearLayout
    private LinearLayout llInvestigation,llAssessment,llWeight;

    //ArrayList
    private ArrayList<HashMap<String, String>> arrayListWeight = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> arrayListAssessment = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> arrayListTests = new ArrayList<HashMap<String, String>>();

    //ImageView
    private ImageView ivPlus;

    String uuid="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_weight_assessment,container,false);

        initialize(view);

        setListeners();

        return view;
    }

    private void initialize(View v) {

        //ImageView
        ivPlus      = v.findViewById(R.id.ivPlus);

        //RecyclerView
        rvWeight = v.findViewById(R.id.rvWeight);
        rvAssessment = v.findViewById(R.id.rvAssessment);
        rvTests = v.findViewById(R.id.rvTests);

        //RelativeLayout
        rlMenu= v.findViewById(R.id.rlMenu);
        rlExpandMenu= v.findViewById(R.id.rlExpandMenu);
        rlCircle= v.findViewById(R.id.rlCircle);

        //LinearLayout
        llWeight= v.findViewById(R.id.llWeight);
        llAssessment= v.findViewById(R.id.llAssessment);
        llInvestigation= v.findViewById(R.id.llInvestigation);

        mGridLayoutManager = new GridLayoutManager(mActivity, 1);
        rvWeight.setLayoutManager(mGridLayoutManager);

        mAssessGridLayoutManager = new GridLayoutManager(mActivity, 1);
        rvAssessment.setLayoutManager(mAssessGridLayoutManager);

        mTestsGridLayoutManager = new GridLayoutManager(mActivity, 1);
        rvTests.setLayoutManager(mTestsGridLayoutManager);

    }

    private void setListeners() {

        ivPlus.setOnClickListener(this);
        rlMenu.setOnClickListener(this);
        rlExpandMenu.setOnClickListener(this);
        rlCircle.setOnClickListener(this);
        llWeight.setOnClickListener(this);
        llAssessment.setOnClickListener(this);
        llInvestigation.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(AppSettings.getString(AppSettings.userType).equalsIgnoreCase("0"))
        {
            checkRecord();
        }
        else
        {
            rlMenu.setVisibility(View.GONE);

            if(AppUtils.isNetworkAvailable(mActivity))
            {
                getBabyDataApi();
            }
            else
            {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        }
    }

    private  void checkRecord() {

        String count = String.valueOf(DatabaseController.isDataExit(TableBabyRegistration.tableName));

        Log.d("count.isDataExit",count);

        arrayListWeight.clear();
        arrayListWeight.addAll(DatabaseController.getWeightData(AppSettings.getString(AppSettings.babyId)));

        arrayListAssessment.clear();
        arrayListAssessment.addAll(DatabaseController.getAssessmentData(AppSettings.getString(AppSettings.babyId)));

        arrayListTests.clear();
        arrayListTests.addAll(DatabaseController.getInvestigationSearchData(AppSettings.getString(AppSettings.babyId),"",""));

        Log.d("arrayListWeight",arrayListWeight.toString());

        weightAdapter = new WeightAdapter(arrayListWeight);
        rvWeight.setAdapter(weightAdapter);

        assessmentAdapter = new AssessmentAdapter(arrayListAssessment);
        rvAssessment.setAdapter(assessmentAdapter);

        Log.d("arrayListTests",arrayListTests.toString());

        testsAdapter = new TestsAdapter(arrayListTests);
        rvTests.setAdapter(testsAdapter);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ivPlus:

            case R.id.rlCircle:

                if (rlExpandMenu.getVisibility()==View.GONE)
                {
                    Animation aniRotateClk = AnimationUtils.loadAnimation(getActivity(),R.anim.rorate45);
                    ivPlus.startAnimation(aniRotateClk);
                    AppUtils.expand(rlExpandMenu);
                }
                else
                {
                    Animation aniRotateClk = AnimationUtils.loadAnimation(getActivity(),R.anim.rorate0);
                    ivPlus.startAnimation(aniRotateClk);
                    AppUtils.collapse(rlExpandMenu);
                }

                break;

            case R.id.llInvestigation:

                Animation aniRotateClk = AnimationUtils.loadAnimation(getActivity(),R.anim.rorate0);
                ivPlus.startAnimation(aniRotateClk);
                AppUtils.collapse(rlExpandMenu);
                //AlertMedicines();

                break;

            case R.id.llWeight:

                aniRotateClk = AnimationUtils.loadAnimation(getActivity(),R.anim.rorate0);
                ivPlus.startAnimation(aniRotateClk);
                AppUtils.collapse(rlExpandMenu);

                boolean weigth = DatabaseController.checkRecordExistWhere(TableWeight.tableName,
                        TableWeight.tableColumn.weightDate+" = '"+AppUtils.getCurrentDateFormatted()+"' and "+TableWeight.tableColumn.babyId +" = '"+AppSettings.getString(AppSettings.babyId)+"'");

                if(weigth) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorWeightTaken));
                }
                else
                {
                    startActivity(new Intent(mActivity, WeightAddActivity.class));
                }


                break;

            case R.id.llAssessment:

                aniRotateClk = AnimationUtils.loadAnimation(getActivity(),R.anim.rorate0);
                ivPlus.startAnimation(aniRotateClk);
                AppUtils.collapse(rlExpandMenu);
                startActivity(new Intent(mActivity, BabyAssessmentActivity.class));

                break;


            default:

                break;

        }

    }

    private class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.MyViewHolder> {

        ArrayList<HashMap<String, String>> data;

        private AssessmentAdapter(ArrayList<HashMap<String, String>> arrayList) {

            data = arrayList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_assessment, viewGroup, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

            holder.tvBy.setText(arrayListAssessment.get(position).get("nurseName"));

            Log.d("data",arrayListAssessment.get(position).toString());

            try {
                String[] parts = arrayListAssessment.get(position).get("addDate").split(" ");

                holder.tvDate.setText(parts[0]);
                holder.tvTime.setText(AppUtils.convertTimeTo12HoursFormat(parts[1]));
            } catch (Exception e) {
                e.printStackTrace();
            }


            int checkPulse=0,checkSpo2=0,checkTemp=0,checkResp=0;

            int pulse = 0;
            try {

                if(!data.get(position).get("babyPulseRate").isEmpty())
                {
                    pulse = Integer.parseInt(arrayListAssessment.get(position).get("babyPulseRate"));
                    if(pulse<75||pulse>200)
                    {
                        checkPulse=0;
                    }
                    else
                    {
                        checkPulse=1;
                    }
                }

            } catch (NumberFormatException e) {
                //e.printStackTrace();
                checkPulse=1;
            }


            int spo2=0;
            try {

                if(!data.get(position).get("babySpO2").isEmpty())
                {
                    spo2 = Integer.parseInt(arrayListAssessment.get(position).get("babySpO2"));

                    if(spo2<95)
                    {
                        checkSpo2=0;
                    }
                    else
                    {
                        checkSpo2=1;
                    }
                }
            } catch (NumberFormatException e) {
                //e.printStackTrace();
                checkSpo2=0;
            }

            float temp=0;
            try {
                try {
                    temp = Float.parseFloat(arrayListAssessment.get(position).get("babyTemperature"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if(temp>99.5||temp<95.9)
                {
                    checkTemp=0;
                }
                else
                {
                    checkTemp=1;
                }

            } catch (NumberFormatException e) {
                //e.printStackTrace();
                checkTemp=0;
            }

            float res=0;
            try {
                try {
                    res = Float.parseFloat(arrayListAssessment.get(position).get("babyRespiratoryRate"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if(res<30||res>60)
                {
                    checkResp=0;
                }
                else
                {
                    checkResp=1;
                }
            } catch (NumberFormatException e) {
                //e.printStackTrace();
                checkResp=0;
            }

            if(arrayListAssessment.get(position).get("isPulseOximatoryDeviceAvailable").equalsIgnoreCase(getString(R.string.yesValue)))
            {
                if(checkPulse==0||checkSpo2==0||checkTemp==0||checkResp==0)
                {
                   holder.ivStatus.setImageResource(R.drawable.ic_sad_smily);
                }
                else
                {
                    holder.ivStatus.setImageResource(R.drawable.ic_happy_smily);
                }
            }
            else  if(checkTemp==0||checkResp==0)
            {
                holder.ivStatus.setImageResource(R.drawable.ic_sad_smily);
            }
            else
            {
                holder.ivStatus.setImageResource(R.drawable.ic_happy_smily);
            }

            holder.tvReport.setText(getString(R.string.details));

            holder.tvReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertAssessment(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {

            //TextView
            TextView tvDate,tvTime,tvReport,tvValue,tvBy;

            //ImageView
            ImageView ivStatus;

            private MyViewHolder(@NonNull View itemView) {
                super(itemView);

                //TextView
                tvDate = itemView.findViewById(R.id.tvDate);
                tvTime = itemView.findViewById(R.id.tvTime);
                tvReport = itemView.findViewById(R.id.tvReport);
                tvValue = itemView.findViewById(R.id.tvValue);
                tvBy = itemView.findViewById(R.id.tvBy);

                //ImageView
                ivStatus = itemView.findViewById(R.id.ivStatus);

                ivStatus.setVisibility(View.VISIBLE);
                tvValue.setVisibility(View.GONE);
            }
        }
    }

    private class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.MyViewHolder> {

        ArrayList<HashMap<String, String>> data;

        private WeightAdapter(ArrayList<HashMap<String, String>> arrayList) {

            data = arrayList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_weight_assess, viewGroup, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

            holder.tvValue.setText(data.get(position).get("babyWeight"));
            holder.tvBy.setText(data.get(position).get("nurseName"));

            String[] parts = data.get(position).get("addDate").split(" ");

            holder.tvDate.setText(parts[0]);
            holder.tvTime.setText(AppUtils.convertTimeTo12HoursFormat(parts[1]));

            holder.tvReport.setText(getString(R.string.viewImage));

            holder.tvReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!data.get(position).get("weightImage").isEmpty() && !data.get(position).get("weightImage").equals("null") &&  !data.get(position).get("weightImage").equals("")){
                        AppUtils.AlertImage((data.get(position).get("weightImage")),mActivity);
                    }else {
                        AppUtils.AlertImage((AppConstants.hashMap.get("babyPhoto")),mActivity);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {

            //TextView
            TextView tvDate,tvTime,tvReport,tvValue,tvBy;

            //ImageView
            ImageView ivStatus;

            private MyViewHolder(@NonNull View itemView) {
                super(itemView);

                //TextView
                tvDate = itemView.findViewById(R.id.tvDate);
                tvTime = itemView.findViewById(R.id.tvTime);
                tvReport = itemView.findViewById(R.id.tvReport);
                tvValue = itemView.findViewById(R.id.tvValue);
                tvBy = itemView.findViewById(R.id.tvBy);

                //ImageView
                ivStatus = itemView.findViewById(R.id.ivStatus);

                ivStatus.setVisibility(View.GONE);
                tvValue.setVisibility(View.VISIBLE);
            }
        }
    }

    private class TestsAdapter extends RecyclerView.Adapter<TestsAdapter.MyViewHolder> {

        ArrayList<HashMap<String, String>> data;

        private TestsAdapter(ArrayList<HashMap<String, String>> arrayList) {

            data = arrayList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_invest, viewGroup, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

            holder.tvName.setText(data.get(position).get("investigationName"));
            holder.tvDate.setText(AppUtils.convertDateTimeTo12HoursFormat(data.get(position).get("addDate")));
            String str=AppUtils.convertDateTimeTo12HoursFormat(data.get(position).get("addDate"));

            String[] splited = str.split("\\s+");


            if(data.get(position).get("result").isEmpty()&&data.get(position).get("sampleTakenBy").isEmpty())
            {
                holder.tvReport.setText(getString(R.string.submitSample));
                holder.tvSample.setText(getString(R.string.notApplicableShortValue));

                if(AppSettings.getString(AppSettings.userType).equalsIgnoreCase("1"))
                {
                    holder.tvReport.setText(getString(R.string.notSubmitted));
                }
            }
            else  if(data.get(position).get("result").isEmpty())
            {
                holder.tvReport.setText(getString(R.string.submitResult));

               if(AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en"))
               {
                   holder.tvSample.setText(getString(R.string.sampleSubmitted)
                           +" "+splited[0]+","+splited[1]+splited[2]
                           +" by "+data.get(position).get("sampleTakenByNurse"));
               }
               else
               {
                   holder.tvSample.setText("सैंपल को "+splited[1]+splited[2]+" बजे "+splited[0]+"  को "+data.get(position).get("sampleTakenByNurse")+" के द्वारा प्रस्तुत किया गया");
               }



                if(AppSettings.getString(AppSettings.userType).equalsIgnoreCase("1"))
                {
                    holder.tvReport.setText(getString(R.string.notSubmitted));
                }
            }
            else
            {
                holder.tvReport.setText(getString(R.string.viewReport));


                if(AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en"))
                {
                    holder.tvSample.setText(getString(R.string.resultSubmitted)
                            +" "+ splited[0]+","+splited[1]+splited[2]
                            +" by "+data.get(position).get("nurseName"));
                }
                else
                {
                    holder.tvSample.setText(""+getString(R.string.resultSubmitted)
                            +" "+data.get(position).get("sampleTakenOn")
                            +" by "+data.get(position).get("nurseName"));
                }


            }

            holder.rlAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(AppSettings.getString(AppSettings.userType).equalsIgnoreCase("1"))
                    {
                        if(data.get(position).get("result").isEmpty()&&data.get(position).get("sampleTakenBy").isEmpty())
                        {
                        }
                        else  if(data.get(position).get("result").isEmpty())
                        {
                        }
                        else
                        {
                            AppUtils.AlertOk(data.get(position).get("result"),mActivity,3,getString(R.string.result));
                        }
                    }
                    else
                    {
                        uuid = data.get(position).get("uuid");

                        if(data.get(position).get("result").isEmpty()&&data.get(position).get("sampleTakenBy").isEmpty())
                        {
                            AlertInvestigation(1);
                        }
                        else  if(data.get(position).get("result").isEmpty())
                        {
                            AlertInvestigation(2);
                        }
                        else
                        {
                            AppUtils.AlertOk(data.get(position).get("result"),mActivity,3,getString(R.string.result));
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {

            //TextView
            TextView tvDate,tvName,tvSample,tvReport;

            //LinearLayout
            LinearLayout llMain;

            //RelativeLayout
            RelativeLayout rlAction;

            private MyViewHolder(@NonNull View itemView) {
                super(itemView);

                //TextView
                tvDate = itemView.findViewById(R.id.tvDate);
                tvName = itemView.findViewById(R.id.tvName);
                tvSample = itemView.findViewById(R.id.tvSample);
                tvReport = itemView.findViewById(R.id.tvReport);

                //LinearLayout
                llMain = itemView.findViewById(R.id.llMain);

                //RelativeLayout
                rlAction = itemView.findViewById(R.id.rlAction);

            }
        }
    }

    //AlertInvestigation
    private void AlertInvestigation(int type) {
        final Dialog dialog = new Dialog(mActivity,android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_investigation);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //RelativeLayout
        RelativeLayout rlStartDate  = dialog.findViewById(R.id.rlStartDate);
        RelativeLayout rlOk         = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlCancel     = dialog.findViewById(R.id.rlCancel);

        //TextView
        TextView tvStartDate = dialog.findViewById(R.id.tvStartDate);
        TextView tvComment = dialog.findViewById(R.id.tvComment);

        //EditText
        EditText etComment =  dialog.findViewById(R.id.etComment);

        //etComment.setHint(getString(R.string.sampleCollectionStatus));
        if(type==1)
        {
            tvComment.setText(R.string.sampleComment);
        }
        else  if(type==2)
        {
            tvComment.setText(getString(R.string.result));
        }

        //Spinner
        Spinner spinnerName = dialog.findViewById(R.id.spinnerName);
        Spinner spinnerEnteredByNurse = dialog.findViewById(R.id.spinnerEnteredByNurse);

        investigationName.clear();
        investigationName.add(getString(R.string.selectInvestigation));
        investigationName.addAll(DatabaseController.getOrderedMedicine(AppSettings.getString(AppSettings.babyId)));

        nurseId.clear();
        nurseId.add(getString(R.string.erroselectNurse));
        nurseId.addAll(DatabaseController.getNurseIdCheckedInData());

        nurseName.clear();
        nurseName.add(getString(R.string.erroselectNurse));
        nurseName.addAll(DatabaseController.getNurseNameCheckedInData());

        spinnerEnteredByNurse.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, nurseName));
        spinnerEnteredByNurse.setSelection(0);

        spinnerName.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, investigationName));
        spinnerName.setSelection(0);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        rlStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppUtils.dateDialog(mActivity,tvStartDate,0);

            }
        });

        rlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        rlOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tvStartDate.getText().toString().trim().isEmpty())
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectDate));
                }
                else if(etComment.getText().toString().trim().isEmpty()&&type==2)
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.enterResult));
                }
                else if(spinnerEnteredByNurse.getSelectedItemPosition()==0)
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectYourName));
                }
                else
                {
                    ContentValues mContentValues = new ContentValues();

                    if(type==1)
                    {
                        mContentValues.put(TableInvestigation.tableColumn.uuid.toString(), uuid);
                        mContentValues.put(TableInvestigation.tableColumn.babyId.toString(), AppSettings.getString(AppSettings.babyId));
                        mContentValues.put(TableInvestigation.tableColumn.sampleTakenBy.toString(), nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()));
                        mContentValues.put(TableInvestigation.tableColumn.isDataSynced.toString(), "0");
                        mContentValues.put(TableInvestigation.tableColumn.sampleComment.toString(),etComment.getText().toString().trim());
                        mContentValues.put(TableInvestigation.tableColumn.sampleTakenOn.toString(), AppUtils.getCurrentDateFormatted());
                        mContentValues.put(TableInvestigation.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
                        mContentValues.put(TableInvestigation.tableColumn.modifyDate.toString(),AppUtils.currentTimestampFormat());
                        mContentValues.put(TableInvestigation.tableColumn.syncTime.toString(),"");
                        mContentValues.put(TableInvestigation.tableColumn.status.toString(), "2");

                        DatabaseController.insertUpdateData(mContentValues, TableInvestigation.tableName,
                                TableInvestigation.tableColumn.uuid.toString(), uuid);
                    }
                    else if(type==2)
                    {
                        mContentValues.put(TableInvestigation.tableColumn.uuid.toString(), uuid);
                        mContentValues.put(TableInvestigation.tableColumn.nurseId.toString(), nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()));
                        mContentValues.put(TableInvestigation.tableColumn.result.toString(), etComment.getText().toString().trim());
                        mContentValues.put(TableInvestigation.tableColumn.resultImage.toString(), "");
                        mContentValues.put(TableInvestigation.tableColumn.modifyDate.toString(),AppUtils.currentTimestampFormat());
                        mContentValues.put(TableInvestigation.tableColumn.status.toString(),  "3");

                        DatabaseController.insertUpdateData(mContentValues, TableInvestigation.tableName,
                                TableInvestigation.tableColumn.uuid.toString(),uuid);
                    }

                    AppUtils.showToastSort(mActivity, getString(R.string.dataSaved));

                    if (AppUtils.isNetworkAvailable(mActivity)) {
                        SyncBabyRecord.getBabyDataToUpdate(mActivity,AppSettings.getString(AppSettings.babyId));
                        dialog.dismiss();
                        checkRecord();
                    }
                    else
                    {
                        AppUtils.showToastSort(mActivity, getString(R.string.dataSaved));
                        dialog.dismiss();
                        checkRecord();
                    }

                }

            }
        });

    }

    private class adapterSpinner extends ArrayAdapter<String> {

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

    //AlertAssessment
    @SuppressLint("SetTextI18n")
    private void AlertAssessment(int position) {
        final Dialog dialog = new Dialog(mActivity,android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_assessment);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //TextView
        TextView tvTemperatureResult,tvRespiRateResult,tvPulseResult,tvSpO2Result,tvCftResult,tvAlertResult,
                tvToneResult,tvColorResult,tvApneaResult,tvGruntingResult,tvChestResult,tvInterestResult,tvLactationResult,
                tvSuckingResult,tvUmbilicusResult,tvSkinResult,tvBulgingResult,tvBleedingResult,
                tvUrineResult,tvStoolResult;

        //RelativeLayout
        RelativeLayout rlOk;

        //TextView
        tvTemperatureResult= dialog.findViewById(R.id.tvTemperatureResult);
        tvRespiRateResult= dialog.findViewById(R.id.tvRespiRateResult);
        tvPulseResult= dialog.findViewById(R.id.tvPulseResult);
        tvSpO2Result= dialog.findViewById(R.id.tvSpO2Result);
        tvCftResult= dialog.findViewById(R.id.tvCftResult);
        tvAlertResult= dialog.findViewById(R.id.tvAlertResult);
        tvToneResult= dialog.findViewById(R.id.tvToneResult);
        tvColorResult= dialog.findViewById(R.id.tvColorResult);
        tvApneaResult= dialog.findViewById(R.id.tvApneaResult);
        tvGruntingResult= dialog.findViewById(R.id.tvGruntingResult);
        tvChestResult= dialog.findViewById(R.id.tvChestResult);
        tvInterestResult= dialog.findViewById(R.id.tvInterestResult);
        tvLactationResult= dialog.findViewById(R.id.tvLactationResult);
        tvSuckingResult= dialog.findViewById(R.id.tvSuckingResult);
        tvUmbilicusResult= dialog.findViewById(R.id.tvUmbilicusResult);
        tvSkinResult= dialog.findViewById(R.id.tvSkinResult);
        tvBulgingResult= dialog.findViewById(R.id.tvBulgingResult);
        tvBleedingResult= dialog.findViewById(R.id.tvBleedingResult);
        tvUrineResult= dialog.findViewById(R.id.tvUrineResult);
        tvStoolResult= dialog.findViewById(R.id.tvStoolResult);

        //RelativeLayout
        rlOk= dialog.findViewById(R.id.rlOk);


        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
//
//        if(arrayListAssessment.get(position).get("isThermometerAvailable").equals(getString(R.string.yesValue)))
//        {
//            tvTemperatureResult.setText(getString(R.string.bullet)
//                                                +"  "+getString(R.string.temperature)
//                                                +" = "+arrayListAssessment.get(position).get("babyTemperature")
//                                                +getString(R.string.degree)
//                                                +arrayListAssessment.get(position).get("temperatureUnit"));
//
//            try {
//                float temp= Float.parseFloat(arrayListAssessment.get(position).get("babyTemperature"));
//                if (temp < 95.9 || temp > 99.5) {
//                    tvTemperatureResult.setTextColor(mActivity.getResources().getColor(R.color.red));
//                }
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//
//
//        }
//        else
//        {
//            tvTemperatureResult.setText(getString(R.string.bullet)
//                                                +"  "+getString(R.string.temperature)
//                                                +" = "+getString(R.string.notApplicableShortValue));
//        }
//        tvRespiRateResult.setText(getString(R.string.bullet)
//                                          +"  "+getString(R.string.respiratoryRate)
//                                          +" = "+arrayListAssessment.get(position).get("babyRespiratoryRate")
//                                          +getString(R.string.min));
//        try {
//            float rr= Float.parseFloat(arrayListAssessment.get(position).get("babyRespiratoryRate"));
//            if (rr <  30 || rr > 60) {
//                tvRespiRateResult.setTextColor(mActivity.getResources().getColor(R.color.red));
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        if(arrayListAssessment.get(position).get("isPulseOximatoryDeviceAvailable").equals(getString(R.string.yesValue)))
//        {
//            tvPulseResult.setText(getString(R.string.bullet)
//                                          +"  "+getString(R.string.pulseRate)
//                                          +" = "+arrayListAssessment.get(position).get("babyPulseRate")
//                                          +getString(R.string.bpm));
//
//            tvSpO2Result.setText(getString(R.string.bullet)
//                                         +"  "+getString(R.string.spo2)
//                                         +" = "+arrayListAssessment.get(position).get("babySpO2")+"%");
//
//            try {
//                float bpr= Float.parseFloat(arrayListAssessment.get(position).get("babyPulseRate"));
//                if (bpr <  75 || bpr > 200) {
//                    tvPulseResult.setTextColor(mActivity.getResources().getColor(R.color.red));
//                }
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//
//
//            try {
//                float spo= Float.parseFloat(arrayListAssessment.get(position).get("babySpO2"));
//                if (spo < 95) {
//                    tvPulseResult.setTextColor(mActivity.getResources().getColor(R.color.red));
//                }
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//
//        }
//        else
//        {
//            tvPulseResult.setText(getString(R.string.bullet)
//                                          +"  "+getString(R.string.pulseRate)
//                                          +" = "+getString(R.string.notApplicableShortValue));
//
//            tvSpO2Result.setText(getString(R.string.bullet)
//                                         +"  "+getString(R.string.spo2)
//                                         +" = "+getString(R.string.notApplicableShortValue));
//        }
//        if(arrayListAssessment.get(position).get("crtKnowledge").equals(getString(R.string.yesValue)))
//        {
//            if(arrayListAssessment.get(position).get("isCftGreaterThree").equals(getString(R.string.yesValue)))
//            {
//                tvCftResult.setText(getString(R.string.bullet)
//                                            +"  "+getString(R.string.capillaryFillingTime));
//            }
//            else
//            {
//                tvCftResult.setText(getString(R.string.bullet)
//                                            +"  "+getString(R.string.capillaryFillingTimeLess3));
//            }
//        }
//        else
//        {
//            tvCftResult.setText(getString(R.string.bullet)
//                                        +"  "+getString(R.string.isCrt3)
//                                        +" = "+getString(R.string.notApplicableShortValue));
//        }

////////////////////////////////////////////////////

        if(arrayListAssessment.get(position).get("isThermometerAvailable").equals(getString(R.string.yesValue)))
        {
            tvTemperatureResult.setText(getString(R.string.bullet)
                    +"  "+getString(R.string.temperature)
                    +" = "+arrayListAssessment.get(position).get("babyTemperature")
                    +arrayListAssessment.get(position).get("temperatureUnit"));

            try {
                float temp= Float.parseFloat(arrayListAssessment.get(position).get("babyTemperature"));

                if(arrayListAssessment.get(position).get("temperatureUnit").equals("° F"))
                {
                    if (temp < 95.9 || temp > 99.5) {
                        tvTemperatureResult.setTextColor(mActivity.getResources().getColor(R.color.red));
                    }
                }else {
                    if (temp < 36.4 || temp > 38) {
                        tvTemperatureResult.setTextColor(mActivity.getResources().getColor(R.color.red));
                    }
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


        }
        else
        {
            tvTemperatureResult.setText(getString(R.string.bullet)
                    +"  "+getString(R.string.temperature)
                    +" = "+getString(R.string.notApplicable));
        }

        tvRespiRateResult.setText(getString(R.string.bullet)
                +"  "+getString(R.string.respiratoryRate)
                +" = "+ AppSettings.getString(AppSettings.respiratoryRate)

                +getString(R.string.min));


        try {
            int rr= Integer.parseInt(AppSettings.getString(AppSettings.respiratoryRate));
            if (rr <  30 || rr > 60) {
                tvRespiRateResult.setTextColor(mActivity.getResources().getColor(R.color.red));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(arrayListAssessment.get(position).get("isPulseOximatoryDeviceAvailable").equals(getString(R.string.yesValue)))
        {
            tvPulseResult.setText(getString(R.string.bullet)
                    +"  "+getString(R.string.pulseRate)
                    +" = "+arrayListAssessment.get(position).get("babyPulseRate")
                    +getString(R.string.bpm));

            tvSpO2Result.setText(getString(R.string.bullet) +"  "+getString(R.string.spo2) +" = "+arrayListAssessment.get(position).get("babySpO2")+"%");

            try {
                int spo2= Integer.parseInt(arrayListAssessment.get(position).get("babySpO2"));
                if (spo2 < 95) {
                    tvSpO2Result.setTextColor(mActivity.getResources().getColor(R.color.red));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            try {
                int bpr= Integer.parseInt(arrayListAssessment.get(position).get("babySpO2"));
                if (bpr <  75 || bpr > 200) {
                    tvPulseResult.setTextColor(mActivity.getResources().getColor(R.color.red));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


//            try {
//                float spo= Float.parseFloat(etSpo2.getText().toString().trim());
//                if (spo < 95) {
//                    tvPulseResult.setTextColor(mActivity.getResources().getColor(R.color.red));
//                }
////            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }

        }
        else
        {
            tvPulseResult.setText(getString(R.string.bullet)
                    +"  "+getString(R.string.pulseRate)
                    +" = "+getString(R.string.notApplicable));

            tvSpO2Result.setText(getString(R.string.bullet)
                    +"  "+getString(R.string.spo2)
                    +" = "+getString(R.string.notApplicable));
        }

        if(arrayListAssessment.get(position).get("crtKnowledge").equals(getString(R.string.yesValue))) {
            if(arrayListAssessment.get(position).get("isCftGreaterThree").equals(getString(R.string.yesValue)))
            {
                tvCftResult.setText(getString(R.string.bullet)+"  "+getString(R.string.crtResult) +" = "+getString(R.string.yesValue));
            }
            else
            {
                tvCftResult.setText(getString(R.string.bullet)+"  "+getString(R.string.crtResult) +" = "+getString(R.string.no));
            }
        }
        else
        {
            tvCftResult.setText(getString(R.string.bullet)+"  "+getString(R.string.crtResult) +" = "+getString(R.string.notApplicable));
        }


        tvAlertResult.setText(getString(R.string.bullet)
                                      +"  "+getString(R.string.alertness)
                                      +" = "+arrayListAssessment.get(position).get("generalCondition"));

        if(arrayListAssessment.get(position).get("generalCondition").equalsIgnoreCase(mActivity.getString(R.string.comatose)))
        {
            tvAlertResult.setTextColor(mActivity.getResources().getColor(R.color.red));
        }


        tvToneResult.setText(getString(R.string.bullet)
                                     +"  "+getString(R.string.tone)
                                     +" = "+arrayListAssessment.get(position).get("tone"));

        tvColorResult.setText(getString(R.string.bullet)
                                      +"  "+getString(R.string.color)
                                      +" = "+arrayListAssessment.get(position).get("color"));

        tvApneaResult.setText(getString(R.string.bullet)
                                      +"  "+getString(R.string.apnea)
                                      +" = "+arrayListAssessment.get(position).get("apneaOrGasping"));


        if(arrayListAssessment.get(position).get("apneaOrGasping").equals(getString(R.string.presentValue)))
        {
            tvApneaResult.setTextColor(mActivity.getResources().getColor(R.color.red));
        }


        tvGruntingResult.setText(getString(R.string.bullet)
                                         +"  "+getString(R.string.grunting)
                                         +" = "+arrayListAssessment.get(position).get("grunting"));

        tvChestResult.setText(getString(R.string.bullet)
                                      +"  "+getString(R.string.chestIndrawing)
                                      +" = "+arrayListAssessment.get(position).get("chestIndrawing"));

        tvInterestResult.setText(getString(R.string.bullet)
                                         +"  "+getString(R.string.interestInFeedingMandResult)
                                         +" = "+arrayListAssessment.get(position).get("isInterestInFeeding"));

        tvLactationResult.setText(getString(R.string.bullet)
                                          +"  "+getString(R.string.sufficientLactation)
                                          +" = "+arrayListAssessment.get(position).get("lactation"));

        tvSuckingResult.setText(getString(R.string.bullet)
                                        +"  "+getString(R.string.suckingMandResult)
                                        +" = "+arrayListAssessment.get(position).get("sucking"));

        tvUmbilicusResult.setText(getString(R.string.bullet)
                                          +"  "+getString(R.string.umbilicusResult)
                                          +" = "+arrayListAssessment.get(position).get("umbilicus"));

        tvSkinResult.setText(getString(R.string.bullet)
                                     +"  "+getString(R.string.skin)
                                     +" = "+arrayListAssessment.get(position).get("skinPustules"));

        tvBulgingResult.setText(getString(R.string.bullet)
                                        +"  "+getString(R.string.bulgingAnteriorFontanelleMandResult)
                                        +" = "+arrayListAssessment.get(position).get("bulgingAnteriorFontanel"));

        tvBleedingResult.setText(getString(R.string.bullet)
                                         +"  "+getString(R.string.bleedingFromAnySiteMandResult)
                                         +" = "+arrayListAssessment.get(position).get("isBleeding"));

        tvUrineResult.setText(getString(R.string.bullet)
                                      +"  "+getString(R.string.urineIn)
                                      +" = "+arrayListAssessment.get(position).get("urinePassedIn24Hrs"));

        tvStoolResult.setText(getString(R.string.bullet)
                                      +"  "+getString(R.string.stoolIn)
                                      +" = "+arrayListAssessment.get(position).get("stoolPassedIn24Hrs"));


        rlOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });
    }

    //Get All Babies
    private void getBabyDataApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {

            jsonData.put("coachId", AppSettings.getString(AppSettings.coachId));
            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("babyId", AppSettings.getString(AppSettings.babyId));
            jsonData.put("type", "4");
            jsonData.put("date", "");

            json.put(AppConstants.projectName, jsonData);

            Log.v("getBabyDataApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.getBabyDetailByType, json, true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        arrayListWeight.clear();
                        arrayListAssessment.clear();
                        arrayListTests.clear();

                        String weightImageURL = jsonObject.getString("weightImageURL");

                        JSONArray jsonArray = jsonObject.getJSONArray("weightData");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject arrayJSONObject = jsonArray.getJSONObject(i);

                            HashMap<String, String> hashlist = new HashMap();

                            hashlist.put("nurseName", arrayJSONObject.getString("nurseName"));
                            hashlist.put("babyWeight", arrayJSONObject.getString("babyWeight"));
                            hashlist.put("weightImage", "");

                            if(!arrayJSONObject.getString("babyWeightImage").isEmpty())
                            {
                                hashlist.put("weightImage", weightImageURL+""+arrayJSONObject.getString("babyWeightImage"));
                            }

                            hashlist.put("weightDate", arrayJSONObject.getString("weightDate"));
                            hashlist.put("addDate", arrayJSONObject.getString("addDate"));

                            arrayListWeight.add(hashlist);
                        }

                        JSONArray assessmentDataArray = jsonObject.getJSONArray("assessmentData");

                        for (int i = 0; i < assessmentDataArray.length(); i++) {

                            JSONObject arrayJSONObject = assessmentDataArray.getJSONObject(i);

                            HashMap<String, String> hashlist = new HashMap();

                            hashlist.put("uuid", arrayJSONObject.getString("nurseName"));
                            hashlist.put("serverId", arrayJSONObject.getString("id"));
                            hashlist.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                            hashlist.put("babyAdmissionId", arrayJSONObject.getString("babyAdmissionId"));
                            hashlist.put("babyMeasuredWeight", arrayJSONObject.getString("babyMeasuredWeight"));
                            hashlist.put("isHeadCircumferenceAvail", arrayJSONObject.getString("isHeadMeasurngTapeAvailable"));
                            hashlist.put("isLengthAvail",arrayJSONObject.getString("isLengthMeasurngTapeAvailable"));
                            hashlist.put("lengthValue", arrayJSONObject.getString("lengthValue"));
                            hashlist.put("measuringTapeNotAvailReason", arrayJSONObject.getString("measuringTapeNotAvailReason"));
                            hashlist.put("babyHeadCircumference", arrayJSONObject.getString("headCircumferenceVal"));
                            hashlist.put("babyRespiratoryRate", arrayJSONObject.getString("respiratoryRate"));
                            hashlist.put("babyTemperature", arrayJSONObject.getString("temperatureValue"));
                            hashlist.put("temperatureUnit", arrayJSONObject.getString("temperatureUnit"));
                            hashlist.put("isPulseOximatoryDeviceAvailable",arrayJSONObject.getString("isPulseOximatoryDeviceAvail"));
                            hashlist.put("crtKnowledge", arrayJSONObject.getString("crtKnowledge"));
                            hashlist.put("isCftGreaterThree",arrayJSONObject.getString("isCrtGreaterThree"));
                            hashlist.put("type", arrayJSONObject.getString("type"));
                            hashlist.put("urinePassedIn24Hrs",arrayJSONObject.getString("urinePassedIn24Hrs"));
                            hashlist.put("stoolPassedIn24Hrs", arrayJSONObject.getString("stoolPassedIn24Hrs"));
                            hashlist.put("generalCondition",arrayJSONObject.getString("generalCondition"));
                            hashlist.put("tone", arrayJSONObject.getString("tone"));
                            hashlist.put("sucking", arrayJSONObject.getString("sucking"));
                            hashlist.put("apneaOrGasping", arrayJSONObject.getString("apneaOrGasping"));
                            hashlist.put("grunting",arrayJSONObject.getString("grunting"));
                            hashlist.put("chestIndrawing",arrayJSONObject.getString("chestIndrawing"));
                            hashlist.put("color", arrayJSONObject.getString("color"));
                            hashlist.put("isBleeding",arrayJSONObject.getString("isBleeding"));
                            hashlist.put("bulgingAnteriorFontanel", arrayJSONObject.getString("bulgingAnteriorFontanel"));
                            hashlist.put("umbilicus",arrayJSONObject.getString("umbilicus"));
                            hashlist.put("skinPustules",arrayJSONObject.getString("skinPustules"));
                            hashlist.put("babySpO2",arrayJSONObject.getString("spo2"));
                            hashlist.put("babyPulseRate", arrayJSONObject.getString("pulseRate"));

                            //hashlist.put("assessmentNumber", arrayJSONObject.getString("nurseName"));

                            hashlist.put("staffId", arrayJSONObject.getString("staffId"));
                            hashlist.put("staffSign", arrayJSONObject.getString("staffSign"));
                            hashlist.put("isThermometerAvailable", arrayJSONObject.getString("isThermometerAvailable"));
                            hashlist.put("formattedDate", arrayJSONObject.getString("addDate"));
                            hashlist.put("isInterestInFeeding", arrayJSONObject.getString("interestInFeeding"));
                            hashlist.put("lactation", arrayJSONObject.getString("sufficientLactation"));
                            hashlist.put("addDate", arrayJSONObject.getString("addDate"));
                            hashlist.put("nurseName", arrayJSONObject.getString("nurseName"));

                            arrayListAssessment.add(hashlist);
                        }

                        JSONArray investigationDataArray = jsonObject.getJSONArray("investigationData");

                        for (int i = 0; i < investigationDataArray.length(); i++) {

                            JSONObject arrayJSONObject = investigationDataArray.getJSONObject(i);

                            HashMap<String, String> hashlist = new HashMap();

                            hashlist.put("uuid", arrayJSONObject.getString("id"));
                            hashlist.put("investigationName", arrayJSONObject.getString("investigationName"));
                            hashlist.put("addDate", arrayJSONObject.getString("addDate"));
                            hashlist.put("doctorId", arrayJSONObject.getString("doctorId"));

                            String result = arrayJSONObject.getString("result");
                            hashlist.put("result", result == null ? "" : result);

                            String resultImage = arrayJSONObject.getString("resultImage");
                            hashlist.put("resultImage", resultImage == null ? "" : resultImage);

                            String nurseComment = arrayJSONObject.getString("nurseComment");
                            hashlist.put("nurseComment", nurseComment == null ? "" : nurseComment);

                            String nurseId = arrayJSONObject.getString("nurseId");
                            hashlist.put("nurseId", nurseId == null ? "" : nurseId);

                            String sampleTakenByNurse =arrayJSONObject.getString("nurseId");
                            hashlist.put("sampleTakenByNurse", sampleTakenByNurse == null ? "" : sampleTakenByNurse);

                            //String nurseName = arrayJSONObject.getString("nurseName");
                            String nurseName = "";
                            hashlist.put("nurseName", nurseName == null ? "" : nurseName);

                            String sampleComment = arrayJSONObject.getString("sampleComment");
                            hashlist.put("sampleComment", sampleComment == null ? "" : sampleComment);

                            String sampleImage = arrayJSONObject.getString("sampleImage");
                            hashlist.put("sampleImage", sampleImage == null ? "" : sampleImage);

                            String sampleTakenBy = arrayJSONObject.getString("nurseId");
                            hashlist.put("sampleTakenBy", sampleTakenBy == null ? "" : sampleTakenBy);

                            String sampleTakenOn = arrayJSONObject.getString("sampleDate");
                            hashlist.put("sampleTakenOn", sampleTakenOn == null ? "" : sampleTakenOn);

                            String doctorComment = arrayJSONObject.getString("doctorComment");
                            hashlist.put("doctorComment", doctorComment == null ? "" : doctorComment);

                            String modifyDate = arrayJSONObject.getString("modifyDate");
                            hashlist.put("modifyDate", modifyDate == null ? "" : modifyDate);

                            arrayListTests.add(hashlist);
                        }

                        weightAdapter = new WeightAdapter(arrayListWeight);
                        rvWeight.setAdapter(weightAdapter);

                        assessmentAdapter = new AssessmentAdapter(arrayListAssessment);
                        rvAssessment.setAdapter(assessmentAdapter);

                        testsAdapter = new TestsAdapter(arrayListTests);
                        rvTests.setAdapter(testsAdapter);
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
