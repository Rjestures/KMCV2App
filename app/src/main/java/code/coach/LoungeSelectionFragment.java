package code.coach;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;


public class LoungeSelectionFragment extends BaseFragment implements View.OnClickListener {

    //TextView
    private TextView tvNoDataFound,tvIdentify;

    //RelativeLayout
    private RelativeLayout rlCircle,rlNext;

    //RecyclerView
    private RecyclerView recyclerView;

    //Adapter for Nurse Listing
    private Adapter adapter;

    //ArrayList for Saving Nurse
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.fragment_nurse_selection, container, false);

        initialize(v);

        return v;
    }

    private void initialize(View v) {

        //TextView
        tvNoDataFound   = v.findViewById(R.id.tvNoDataFound);
        tvIdentify   = v.findViewById(R.id.tvIdentify);

        //RecyclerView
        recyclerView = v.findViewById(R.id.recyclerView);

        //RelativeLayout
        rlCircle   = v.findViewById(R.id.rlCircle);
        rlNext   = v.findViewById(R.id.rlNext);

        tvIdentify.setText(getString(R.string.selectLounge));

        //setOnClickListener
        rlNext.setOnClickListener(this);

        if (AppUtils.isNetworkAvailable(mActivity)) {
            getAllLoungeApi();
        } else {
            AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
        }
    }

    private void setAdapter() {

        rlCircle.setBackgroundResource(R.drawable.circle_grey);

        //GridLayoutManager
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(mActivity, 1);
        recyclerView.setLayoutManager(mGridLayoutManager);

        if(arrayList.size()>0)
        {
            tvNoDataFound.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new Adapter(arrayList);
            recyclerView.setAdapter(adapter);
        }
        else
        {
            tvNoDataFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.rlNext:

                if(AppSettings.getString(AppSettings.loungeId).isEmpty())
                {
                    AppUtils.showToastSort(mActivity,getString(R.string.selectLounge));
                }
                else
                {
                    startActivity(new Intent(mActivity, MainActivity.class));
                    mActivity.finishAffinity();
                }

                break;

            default:

                break;
        }
    }

    private class Adapter extends RecyclerView.Adapter<Holder> {
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        public Adapter(ArrayList<HashMap<String, String>> favList) {
            data = favList;
        }

        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_nurse, parent, false));
        }

        @SuppressLint("SetTextI18n")
        public void onBindViewHolder(Holder holder, final int position) {

            holder.ivPic.setImageResource(R.mipmap.logo);

            if(data.get(position).get("status").equals("1"))
            {
                holder.llMain.setBackgroundResource(R.drawable.rectangle_teal_selected);
                holder.tvName.setTextColor(getResources().getColor(R.color.white));
            }
            else
            {
                holder.llMain.setBackgroundResource(R.drawable.rectangle_grey);
                holder.tvName.setTextColor(getResources().getColor(R.color.blackNew));
            }

            holder.tvName.setText(data.get(position).get("loungeName"));

            holder.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    for(int i=0;i<data.size();i++)
                    {
                        HashMap<String, String> hashlist = new HashMap<>();

                        hashlist.put("loungeId", data.get(i).get("loungeId"));
                        hashlist.put("loungeName",  data.get(i).get("loungeName"));
                        hashlist.put("facilityId",  data.get(i).get("facilityId"));
                        hashlist.put("facilityName",  data.get(i).get("facilityName"));
                        hashlist.put("districtId",  data.get(i).get("districtId"));
                        hashlist.put("districtName",  data.get(i).get("districtName"));
                        hashlist.put("status", "0");

                        data.set(i,hashlist);
                    }

                    HashMap<String, String> hashlist = new HashMap<>();

                    hashlist.put("loungeId", data.get(position).get("loungeId"));
                    hashlist.put("loungeName",  data.get(position).get("loungeName"));
                    hashlist.put("facilityId",  data.get(position).get("facilityId"));
                    hashlist.put("facilityName",  data.get(position).get("facilityName"));
                    hashlist.put("districtId",  data.get(position).get("districtId"));
                    hashlist.put("districtName",  data.get(position).get("districtName"));
                    hashlist.put("status", "1");

                    data.set(position,hashlist);

                    AppSettings.putString(AppSettings.loungeId,data.get(position).get("loungeId"));

                    adapter.notifyDataSetChanged();

                    rlCircle.setBackgroundResource(R.drawable.circle_teal);
                }
            });


        }

        public int getItemCount() {
            return data.size();
        }
    }

    private class Holder extends RecyclerView.ViewHolder {

        //TextView
        TextView tvName;

        //LinearLayout
        LinearLayout llMain;

        //ImageView
        ImageView ivPic;

        public Holder(View itemView) {
            super(itemView);

            //TextView
            tvName =  itemView.findViewById(R.id.tvName);

            //LinearLayout
            llMain = itemView.findViewById(R.id.llMain);

            //ImageView
            ivPic = itemView.findViewById(R.id.ivPic);

        }
    }


    private void getAllLoungeApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {

            jsonData.put("coachId", AppSettings.getString(AppSettings.coachId));

            json.put(AppConstants.projectName, jsonData);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.getCoachLounge, json,true,true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        JSONArray loungeArray = jsonObject.getJSONArray("loungeDetails");

                        for (int i = 0; i < loungeArray.length(); i++) {

                            JSONObject arrayJSONObject = loungeArray.getJSONObject(i);

                            HashMap<String, String> hashMap = new HashMap<>();

                            hashMap.put("loungeId", arrayJSONObject.getString("loungeId"));
                            hashMap.put("loungeName", arrayJSONObject.getString("loungeName"));
                            hashMap.put("facilityId", arrayJSONObject.getString("facilityId"));
                            hashMap.put("facilityName", arrayJSONObject.getString("facilityName"));
                            hashMap.put("districtId", arrayJSONObject.getString("districtId"));
                            hashMap.put("districtName", arrayJSONObject.getString("districtName"));
                            hashMap.put("status","0");

                            arrayList.add(hashMap);
                        }

                        setAdapter();
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
