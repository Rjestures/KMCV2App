package code.mothersFragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kmcapp.android.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.main.MainActivity;
import code.main.MotherDetailActivity;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;


public class MotherListingFragment extends BaseFragment implements View.OnClickListener {

    //RecyclerView
    private RecyclerView recyclerView;

    //GridLayoutManager
    private GridLayoutManager mGridLayoutManager;

    private Adapter adapter;

    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();

    //TextView
    private TextView tvTotalMothers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mother, container, false);

        initialize(view);

        setListeners();

        return view;
    }

    private void initialize(View v) {

        //TextView
        tvTotalMothers = v.findViewById(R.id.tvTotalMothers);

        //RecyclerView
        recyclerView = v.findViewById(R.id.recyclerView);

        mGridLayoutManager = new GridLayoutManager(mActivity, 1);
        recyclerView.setLayoutManager(mGridLayoutManager);


    }

    private void setListeners() {


    }

    @Override
    public void onResume() {
        super.onResume();

        if (AppSettings.getString(AppSettings.userType).equalsIgnoreCase("1")) {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                getAllMothersApi();
            } else {
                AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
            }
        } else {
            arrayList.clear();
            arrayList.addAll(DatabaseController.getAllMothers());
            checkRecord();
        }
    }

    private void checkRecord() {
        tvTotalMothers.setText(getString(R.string.totalMothers) + " " + arrayList.size());
        if (arrayList.size() <= 1) {
            tvTotalMothers.setText(getString(R.string.totalMother) + " " + arrayList.size());
        }

        recyclerView.setVisibility(View.VISIBLE);
        adapter = new Adapter(arrayList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            default:

                break;

        }

    }

    //Get All Babies
    private void getAllMothersApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {

            jsonData.put("coachId", AppSettings.getString(AppSettings.coachId));
            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            json.put(AppConstants.projectName, jsonData);

            Log.v("getAllMothersApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.getMother, json, true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("motherList");

                        arrayList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject arrayJSONObject = jsonArray.getJSONObject(i);

                            HashMap<String, String> hashlist = new HashMap();

                            hashlist.put("motherId", arrayJSONObject.getString("motherId"));
                            hashlist.put("motherPhoto", arrayJSONObject.getString("motherPicture"));
                            hashlist.put("motherName", arrayJSONObject.getString("motherName"));
                            hashlist.put("motherAdmissionId", arrayJSONObject.getString("motherAdmissionId"));
                            hashlist.put("lastAssessment", arrayJSONObject.getString("addDate"));

                            arrayList.add(hashlist);

                        }

                        checkRecord();
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

    private class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

        ArrayList<HashMap<String, String>> data;
        int type;
        ArrayList<HashMap<String, String>> arr = new ArrayList<>();

        public Adapter(ArrayList<HashMap<String, String>> arrayList) {

            data = arrayList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_mother, viewGroup, false);
            return new MyViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
            int checkTmp = 0, checkHeartBeat = 0, checkSystolic = 0, checkDiastolic = 0;

            holder.tvName.setText(data.get(position).get("motherName"));

            String motherId = data.get(position).get("motherId");

            arr.clear();
            arr.addAll(DatabaseController.getMotherMonitoringDataViaId(data.get(position).get("motherId")));


            holder.tvLastAssessment.setText(getString(R.string.lastAssessment) + ": " + getString(R.string.notApplicableShortValue));

            if (!data.get(position).get("lastAssessment").isEmpty()) {
                holder.tvLastAssessment.setText(getString(R.string.lastAssessment) + ": " + AppUtils.convertDateTimeTo12HoursFormat(data.get(position).get("lastAssessment")));
            }

            if (!data.get(position).get("motherPhoto").isEmpty()) {

                if (data.get(position).get("motherPhoto").contains("http")) {

                    Picasso.get().load(data.get(position).get("motherPhoto")).into(holder.ivPic);

                } else {

                    try {

                        byte[] decodedString = Base64.decode(data.get(position).get("motherPhoto"), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        holder.ivPic.setImageBitmap(decodedByte);
                    } catch (Exception e) {
                        holder.ivPic.setImageResource(R.mipmap.mother);
                    }
                }
            }else {
                    holder.ivPic.setImageResource(R.mipmap.mother);
            }

            holder.rlMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppSettings.putString(AppSettings.motherName, data.get(position).get("motherName"));
                    AppSettings.putString(AppSettings.motherPhoto, data.get(position).get("motherPhoto"));
                    AppSettings.putString(AppSettings.motherId, data.get(position).get("motherId"));
                    AppSettings.putString(AppSettings.motherAdmissionId, data.get(position).get("motherAdmissionId"));
                    AppSettings.putString(AppSettings.lastAssessmentDate, data.get(position).get("lastAssessment"));
                    startActivity(new Intent(mActivity, MotherDetailActivity.class));
                }
            });

            if (arr.size() > 0) {
                holder.ivStatus.setVisibility(View.VISIBLE);

                float temp = 0;
                try {
                    temp = Float.parseFloat(arr.get(0).get("motherTemperature"));
                    if (temp < 95.9 || temp > 99.5) {
                        checkTmp = 0;
                    } else {
                        checkTmp = 1;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                float heartBeat = 0;
                try {
                    heartBeat = Float.parseFloat(arr.get(0).get("motherPulse"));
                    if (heartBeat < 50 || heartBeat > 120) {
                        checkHeartBeat = 0;
                    } else {
                        checkHeartBeat = 1;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                float systolic = 0;
                try {
                    systolic = Float.parseFloat(arr.get(0).get("motherSystolicBP"));
                    if (systolic >= 140 || systolic <= 90)
                    {
                        checkSystolic = 0;
                    } else {
                        checkSystolic = 1;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }


                float diastolic = 0;
                try {
                    diastolic = Float.parseFloat(arr.get(0).get("motherDiastolicBP"));
                    if (diastolic >= 90 || diastolic <= 60) {
                        checkDiastolic = 0;
                    } else {
                        checkDiastolic = 1;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (checkTmp == 0 || checkDiastolic == 0 || checkSystolic == 0 || checkHeartBeat == 0) {
                    holder.ivStatus.setImageResource(R.drawable.ic_sad_smily);
                } else {
                    holder.ivStatus.setImageResource(R.drawable.ic_happy_smily);
                }
            }
            else {
                holder.ivStatus.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            //TextView
            TextView tvName, tvLastAssessment;

            //ImageView
            ImageView ivPic, ivStatus;

            //RelativeLayout
            RelativeLayout rlMain;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                //TextView
                tvName = itemView.findViewById(R.id.tvName);
                tvLastAssessment = itemView.findViewById(R.id.tvLastAssessment);

                //ImageView
                ivPic = itemView.findViewById(R.id.ivPic);

                //RelativeLayout
                rlMain = itemView.findViewById(R.id.rlMain);
                ivStatus = itemView.findViewById(R.id.ivStatus);

            }
        }
    }


}
