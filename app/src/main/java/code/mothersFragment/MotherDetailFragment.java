package code.mothersFragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;


public class MotherDetailFragment extends BaseFragment {

    //EditText
    private TextView etDoctorNotes;

    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();

    //RecyclerView
    private RecyclerView rvAssessment;

    //GridLayoutManager
    private GridLayoutManager  mAssessGridLayoutManager;

    //AssessmentAdapter
    private  AssessmentAdapter assessmentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_mother_detail, container, false);

        init(view);
        return view;
    }

    private void init(View view) {

        //TextView
        etDoctorNotes =view.findViewById(R.id.etDoctorNotes);

        //RecyclerView
        rvAssessment = view.findViewById(R.id.rvAssessment);

        mAssessGridLayoutManager = new GridLayoutManager(mActivity, 1);
        rvAssessment.setLayoutManager(mAssessGridLayoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(AppSettings.getString(AppSettings.userType).equalsIgnoreCase("1"))
        {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                getMotherDetailApi();
            } else {
                AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
            }
        }
        else
        {
            setValues();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setValues() {

        arrayList.clear();
        arrayList.addAll(DatabaseController.getMotherMonitoringDataViaId(AppSettings.getString(AppSettings.motherId)));

        assessmentAdapter = new AssessmentAdapter(arrayList);
        rvAssessment.setAdapter(assessmentAdapter);

        ArrayList<HashMap<String, String>> arrayListComment
                = new ArrayList<HashMap<String, String>>(DatabaseController.getCommentData(AppSettings.getString(AppSettings.motherId), "1"));

        if(arrayListComment.size()>0)
        {
            etDoctorNotes.setText(arrayListComment.get(0).get("doctorName")
                                          +" - "+arrayListComment.get(0).get("doctorComment"));
        }
    }

    private class AssessmentAdapter extends RecyclerView.Adapter<AssessmentAdapter.MyViewHolder> {

        ArrayList<HashMap<String, String>> data;

        private AssessmentAdapter(ArrayList<HashMap<String, String>> arrayList) {

            data = arrayList;
        }

        @NonNull
        @Override
        public AssessmentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_assessment, viewGroup, false);
            return new AssessmentAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AssessmentAdapter.MyViewHolder holder, final int position) {
            int checkTmp = 0, checkHeartBeat = 0, checkSystolic = 0, checkDiastolic = 0;

            holder.tvBy.setText(data.get(position).get("nurseName"));

            Log.d("data",data.get(position).toString());

            try {
                String[] parts = data.get(position).get("addDate").split(" ");

                holder.tvDate.setText(parts[0]);
                holder.tvTime.setText(AppUtils.convertTimeTo12HoursFormat(parts[1]));
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.tvReport.setText(getString(R.string.viewDetails));

            holder.tvReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertAssessment(position);
                }
            });

            try {
                float temp= Float.parseFloat(data.get(position).get("motherTemperature"));
                if (temp < 95.9 || temp > 99.5) {
                    checkTmp=0;
                }
                else
                {
                    checkTmp=1;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }



            try {
                float bpr= Float.parseFloat(data.get(position).get("motherPulse"));
                if (bpr <  50 || bpr > 120) {
                    checkHeartBeat=0;
                }
                else
                {
                    checkHeartBeat=1;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


            try {
                float msp= Float.parseFloat(data.get(position).get("motherSystolicBP"));
                if (msp <=  90 || msp >= 140) {
                    checkSystolic = 0;
                }
                else
                {
                    checkSystolic = 1;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            try {
                float dsp= Float.parseFloat(data.get(position).get("motherDiastolicBP"));
                if (dsp <=  60 || dsp >= 90) {
                    checkDiastolic = 0;
                }
                else
                {
                    checkDiastolic = 1;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            if (checkTmp == 0 || checkDiastolic == 0 || checkSystolic == 0 || checkHeartBeat == 0) {
                holder.ivStatus.setImageResource(R.drawable.ic_sad_smily);
            } else {
                holder.ivStatus.setImageResource(R.drawable.ic_happy_smily);
            }

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

            //RelativeLayout
            RelativeLayout rlAction;

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

                //RelativeLayout
                rlAction = itemView.findViewById(R.id.rlAction);

                rlAction.setVisibility(View.GONE);
                ivStatus.setVisibility(View.VISIBLE);
                tvValue.setVisibility(View.GONE);
            }
        }
    }

    private void AlertAssessment(int position) {

        final Dialog dialog = new Dialog(mActivity,android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_mother_assessment);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //TextView
        TextView tvSystolic,tvDiastolic,tvPulseRate,tvMothersUterinetone,tvTempUnit,etOtherObservation;

        //RelativeLayout
        RelativeLayout rlOk;

        //TextView
        tvSystolic=dialog.findViewById(R.id.tvSystolic);
        tvDiastolic=dialog.findViewById(R.id.tvDiastolic);
        tvPulseRate=dialog.findViewById(R.id.tvPulseRate);
        tvMothersUterinetone=dialog.findViewById(R.id.tvMothersUterineTone);
        tvTempUnit=dialog.findViewById(R.id.tvTempUnit);
        etOtherObservation=dialog.findViewById(R.id.etOtherObservation);

        //RelativeLayout
        rlOk=dialog.findViewById(R.id.rlOk);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

//        tvSystolic.setText(AppUtils.setSpannable2("",arrayList.get(position).get("motherSystolicBP"),getString(R.string.mmHg),mActivity));
//        tvDiastolic.setText(AppUtils.setSpannable2("",arrayList.get(position).get("motherDiastolicBP"),getString(R.string.mmHg),mActivity));
//        tvPulseRate.setText(AppUtils.setSpannable2("",arrayList.get(position).get("motherPulse"),getString(R.string.min),mActivity));
         tvMothersUterinetone.setText(arrayList.get(position).get("motherUterineTone"));
//        tvTempUnit.setText(AppUtils.setSpannable2("",arrayList.get(position).get("motherTemperature"),getString(R.string.degreeValue)+" "+arrayList.get(0).get("temperatureUnit"),mActivity));
         etOtherObservation.setText(arrayList.get(position).get("other"));


        float temp = 0;
            temp = Float.parseFloat(arrayList.get(position).get("motherTemperature"));
            if (temp < 95.9 || temp > 99.5) {
                tvTempUnit.setText(arrayList.get(position).get("motherTemperature")+getString(R.string.degreeValue)+" "+arrayList.get(0).get("temperatureUnit"));
                tvTempUnit.setTextColor(mActivity.getResources().getColor(R.color.red));
            }else {
                tvTempUnit.setText(arrayList.get(position).get("motherTemperature")+getString(R.string.degreeValue)+" "+arrayList.get(0).get("temperatureUnit"));
            }

        float bpr= Float.parseFloat(arrayList.get(position).get("motherPulse"));
        if (bpr <  50 || bpr > 120) {
            tvPulseRate.setText(arrayList.get(position).get("motherPulse")+getString(R.string.min));
            tvPulseRate.setTextColor(mActivity.getResources().getColor(R.color.red));
        }else {
            tvPulseRate.setText(arrayList.get(position).get("motherPulse")+getString(R.string.min));
        }

        float msp= Float.parseFloat(arrayList.get(position).get("motherSystolicBP"));
        if (msp <=  90 || msp >= 140) {
            tvSystolic.setText(arrayList.get(position).get("motherSystolicBP")+getString(R.string.mmHg));
            tvSystolic.setTextColor(mActivity.getResources().getColor(R.color.red));
        }else {
            tvSystolic.setText(arrayList.get(position).get("motherSystolicBP")+getString(R.string.mmHg));
        }

        float dsp= Float.parseFloat(arrayList.get(position).get("motherDiastolicBP"));
        if (dsp <=  60 || dsp >= 90) {
            tvDiastolic.setText(arrayList.get(position).get("motherDiastolicBP")+getString(R.string.mmHg));
            tvDiastolic.setTextColor(mActivity.getResources().getColor(R.color.red));
        }else {
            tvDiastolic.setText(arrayList.get(position).get("motherDiastolicBP")+getString(R.string.mmHg));
        }

        rlOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    //Get Mother Detail
    private void getMotherDetailApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {

            jsonData.put("coachId", AppSettings.getString(AppSettings.coachId));
            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("motherId", AppSettings.getString(AppSettings.motherId));

            json.put(AppConstants.projectName, jsonData);

            Log.v("getMotherDetailApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.getMotherDetail, json, true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                arrayList.clear();

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("monitoringData");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject arrayJSONObject = jsonArray.getJSONObject(i);

                            HashMap<String, String> hashlist = new HashMap();

                            hashlist.put("uuid", arrayJSONObject.getString("id"));
                            hashlist.put("motherId", AppSettings.getString(AppSettings.motherId));
                            hashlist.put("motherTemperature",arrayJSONObject.getString("motherTemperature"));
                            hashlist.put("motherSystolicBP", arrayJSONObject.getString("motherSystolicBP"));
                            hashlist.put("motherDiastolicBP", arrayJSONObject.getString("motherDiastolicBP"));
                            hashlist.put("motherPulse", arrayJSONObject.getString("motherPulse"));
                            hashlist.put("motherUterineTone", arrayJSONObject.getString("motherUterineTone"));
                            hashlist.put("episitomyCondition",arrayJSONObject.getString("episitomyCondition"));
                            hashlist.put("other", arrayJSONObject.getString("other"));
                            hashlist.put("addDate", arrayJSONObject.getString("addDate"));
                            hashlist.put("nurseName", arrayJSONObject.getString("staffName"));
                            hashlist.put("temperatureUnit", arrayJSONObject.getString("temperatureUnit"));

                            arrayList.add(hashlist);

                        }

                        assessmentAdapter = new AssessmentAdapter(arrayList);
                        rvAssessment.setAdapter(assessmentAdapter);

                        try {
                            JSONObject jsonObjectComment = jsonObject.getJSONObject("commentData");

                            etDoctorNotes.setText(jsonObjectComment.getString("doctorName")
                                                          +" - "+jsonObjectComment.getString("comment"));
                        } catch (JSONException e) {
                            e.printStackTrace();
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
}
