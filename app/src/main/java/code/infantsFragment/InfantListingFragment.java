package code.infantsFragment;

import android.content.ContentValues;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableBabyAdmission;
import code.database.TableBabyMonitoring;
import code.database.TableBabyRegistration;
import code.database.TableDutyChange;
import code.main.BabyDetailActivity;
import code.main.MainActivity;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;


public class InfantListingFragment extends BaseFragment implements View.OnClickListener {

    //RecyclerView
    private RecyclerView recyclerView;

    //GridLayoutManager
    private GridLayoutManager mGridLayoutManager;

    private Adapter adapter;

    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();

    //TextView
    private TextView tvTotalBabies;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_infant,container,false);

        initialize(view);

        setListeners();

        return view;
    }

    private void initialize(View v) {

        //TextView
        tvTotalBabies = v.findViewById(R.id.tvTotalBabies);

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

        if(AppSettings.getString(AppSettings.userType).equalsIgnoreCase("1"))
        {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                getAllBabiesApi();
            } else {
                AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
            }
        }
        else
        {
            arrayList.clear();
            arrayList.addAll(DatabaseController.getAllBabies());
            checkRecord();
        }
    }

    private  void checkRecord()
    {
        tvTotalBabies.setText(getString(R.string.totalBabies)+" "+arrayList.size());
        if(arrayList.size()<=1)
        {
            tvTotalBabies.setText(getString(R.string.totalBaby)+" "+arrayList.size());
        }

        recyclerView.setVisibility(View.VISIBLE);
        adapter = new Adapter(arrayList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.llCbTakenPermission:

                /*if (permissionCb == 2) {
                    ivCheckbox.setImageResource(R.drawable.ic_check_box_selected);
                    ivCheckbox.setColorFilter(getResources().getColor(R.color.r_color), PorterDuff.Mode.SRC_IN);
                    permissionCb = 1;
                } else {
                    ivCheckbox.setImageResource(R.drawable.ic_check_box);
                    ivCheckbox.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
                    permissionCb = 2;
                }*/

                break;


            default:

                break;

        }

    }

    private class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

        ArrayList<HashMap<String, String>> data;
        int type;

        public Adapter(ArrayList<HashMap<String, String>> arrayList) {

            data = arrayList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_child, viewGroup, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

            if(data.get(position).get("motherName")==null
                       ||data.get(position).get("motherName").isEmpty())
            {
                holder.tvName.setText(getString(R.string.babyOf) +" "+getString(R.string.unknown)+" ("+data.get(position).get("babyFileId")+")");
            }
            else
            {
                holder.tvName.setText(getString(R.string.babyOf) +" "+data.get(position).get("motherName")+" ("+data.get(position).get("babyFileId")+")");
            }

            if(!data.get(position).get("babyPhoto").isEmpty())
            {
                if(data.get(position).get("babyPhoto").contains("http"))
                {
                    Picasso.get().load(data.get(position).get("babyPhoto")).into(holder.ivPic);
                }
                else
                {
                    try {
                        byte[] decodedString = Base64.decode(data.get(position).get("babyPhoto"), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        holder.ivPic.setImageBitmap(decodedByte);

                    } catch (Exception e) {
                        holder.ivPic.setImageResource(R.mipmap.baby);
                    }
                }
            }else {
                holder.ivPic.setImageResource(R.mipmap.baby);
            }


            String dateOfAdmi = data.get(position).get("admissionDate");

            String[] parts = dateOfAdmi.split(" ");

            Log.d("dateOfAdmi",parts[0]);

            holder.tvDob.setText(getString(R.string.dateOfBirth)+" "+data.get(position).get("deliveryDate"));
            holder.tvDoa.setText(getString(R.string.dateOfAdmission)+" "+parts[0]);
            holder.tvBWeight.setText(getString(R.string.birthWeight)
                    +" "+data.get(position).get("birthWeight")
                    +" "+getString(R.string.grams));

            holder.tvCWeight.setText(getString(R.string.currentWeight)
                    +" "+data.get(position).get("currentWeight")
                    +" "+getString(R.string.grams));


            int checkPulse=0,checkSpo2=0,checkTemp=0,checkResp=0;

            int pulse = 0;
            try {
                pulse = Integer.parseInt(data.get(position).get("pulseRate"));
                if(pulse<75||pulse>200) {
                    checkPulse=0;
                }
                else {
                    checkPulse=1;
                }
            } catch (NumberFormatException e) {
                //e.printStackTrace();
                checkPulse=1;
            }


            int spo2=0;
            try {
                spo2 = Integer.parseInt(data.get(position).get("spO2"));

                if(spo2<95)
                {
                    checkSpo2=0;
                }
                else
                {
                    checkSpo2=1;
                }

            } catch (NumberFormatException e) {
                //e.printStackTrace();
                checkSpo2=0;
            }

            float temp=0;
            try {
                temp = Float.parseFloat(data.get(position).get("temperature"));

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
                res = Float.parseFloat(data.get(position).get("respiratoryRate"));

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

            if(data.get(position).get("isPulseOximatoryDeviceAvailable").equalsIgnoreCase(getString(R.string.yesValue)))
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


            holder.rlMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //((MainActivity)getActivity()).displayView(6);

                    AppConstants.hashMap.clear();
                    AppConstants.hashMap.putAll(data.get(position));

                    AppSettings.putString(AppSettings.motherName,data.get(position).get("motherName"));
                    AppSettings.putString(AppSettings.babyPic,data.get(position).get("babyPhoto"));
                    AppSettings.putString(AppSettings.babyId,data.get(position).get("babyId"));
                    AppSettings.putString(AppSettings.babyAdmissionId,data.get(position).get("babyAdmissionId"));
                    startActivity(new Intent(mActivity, BabyDetailActivity.class));


                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            //TextView
            TextView tvName,tvDob,tvDoa,tvBWeight,tvCWeight;

            //ImageView
            ImageView ivPic, ivStatus;

            //RelativeLayout
            RelativeLayout rlMain;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                //TextView
                tvName = itemView.findViewById(R.id.tvName);
                tvDob = itemView.findViewById(R.id.tvDob);
                tvDoa = itemView.findViewById(R.id.tvDoa);
                tvBWeight = itemView.findViewById(R.id.tvBWeight);
                tvCWeight = itemView.findViewById(R.id.tvCWeight);

                //ImageView
                ivPic = itemView.findViewById(R.id.ivPic);
                ivStatus = itemView.findViewById(R.id.ivStatus);

                //RelativeLayout
                rlMain = itemView.findViewById(R.id.rlMain);

            }
        }
    }

    //Get All Babies
    private void getAllBabiesApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {

            jsonData.put("coachId", AppSettings.getString(AppSettings.coachId));
            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            json.put(AppConstants.projectName, jsonData);

            Log.v("getAllBabiesApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.getBaby, json, true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("babyList");

                        arrayList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject arrayJSONObject = jsonArray.getJSONObject(i);

                            HashMap<String, String> hashlist = new HashMap();

                            hashlist.put("babyId", arrayJSONObject.getString("babyId"));
                            hashlist.put("babyAdmissionId", arrayJSONObject.getString("babyAdmissionId"));
                            hashlist.put("lastAssessmentDateTime", arrayJSONObject.getString("lastAssessmentDateTime"));
                            hashlist.put("dob", arrayJSONObject.getString("dob"));
                            hashlist.put("babyPhoto", arrayJSONObject.getString("babyPhoto"));
                            hashlist.put("pulseRate", arrayJSONObject.getString("pulseRate"));
                            hashlist.put("spO2", arrayJSONObject.getString("spO2"));
                            hashlist.put("respiratoryRate", arrayJSONObject.getString("respiratoryRate"));
                            hashlist.put("temperature", arrayJSONObject.getString("temperature"));
                            hashlist.put("isPulseOximatoryDeviceAvailable",arrayJSONObject.getString("isPulseOximatoryDeviceAvail"));
                            hashlist.put("motherName", arrayJSONObject.getString("motherName"));
                            hashlist.put("motherId", arrayJSONObject.getString("motherId"));
                            hashlist.put("status", arrayJSONObject.getString("status"));
                            hashlist.put("deliveryDate", arrayJSONObject.getString("dob"));
                            hashlist.put("addDate", arrayJSONObject.getString("addDate"));
                            hashlist.put("admissionDate", arrayJSONObject.getString("addDate"));
                            hashlist.put("babyFileId", arrayJSONObject.getString("babyFileId"));
                            hashlist.put("birthWeight", arrayJSONObject.getString("birthWeight"));
                            hashlist.put("currentWeight", arrayJSONObject.getString("currentWeight"));

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


}
