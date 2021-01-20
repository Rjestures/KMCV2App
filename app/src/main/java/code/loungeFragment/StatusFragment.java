package code.loungeFragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.checkOut.NurseSelectionCheckOutFragment;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableAsha;
import code.database.TableBabyMonitoring;
import code.database.TableBirthReview;
import code.database.TableDutyChange;
import code.database.TableFacility;
import code.database.TableLounge;
import code.database.TableLoungeAssessment;
import code.database.TableLoungeServices;
import code.database.TableMotherMonitoring;
import code.fragment.LoungeMonitoringFragment;
import code.main.MainActivity;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;
import lecho.lib.hellocharts.model.Line;


public class StatusFragment extends BaseFragment implements View.OnClickListener {
     String positiotnValue="";
    //RelativeLayout
    private RelativeLayout rlLoungeAssessment, rlBirthReview, rlLoungeServices, rlInfantsAssessment, rlMothersAssessment;

    //TextView
    private TextView tvShiftTime, tvDutyDate, tvDutyTime, tvNoOfInfants, tvNoOfMothers, tvBedOccupancy, tvDoctorDutyDate, tvDoctorDutyTime, tvDoctorName, tvLoungeAssessment,
            tvFacilityBirthReview, tvLoungServices, tvInfantAssessment, tvMotherAssessment, tvNurseName1, tvNurseName2, tvCount;

    //ImageView
    private ImageView ivLoungeAssessment, ivFacilityBirthReview, ivLoungServices, ivInfantAssessment, ivMotherAssessment, ivNurse1, ivNurse2;

    //LinearLayout
    private LinearLayout llNurse2, llMain;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_status, container, false);

        initialize(view);

        return view;
    }

    private void initialize(View v) {

        //TextView
        tvShiftTime = v.findViewById(R.id.tvShiftTime);
        tvDutyDate = v.findViewById(R.id.tvDutyDate);
        tvDutyTime = v.findViewById(R.id.tvDutyTime);
        tvNoOfInfants = v.findViewById(R.id.tvNoOfInfants);
        tvNoOfMothers = v.findViewById(R.id.tvNoOfMothers);
        tvBedOccupancy = v.findViewById(R.id.tvBedOccupancy);
        tvDoctorDutyDate = v.findViewById(R.id.tvDoctorDutyDate);
        tvDoctorDutyTime = v.findViewById(R.id.tvDoctorDutyTime);
        tvDoctorName = v.findViewById(R.id.tvDoctorName);
        tvLoungeAssessment = v.findViewById(R.id.tvLoungeAssessment);
        tvFacilityBirthReview = v.findViewById(R.id.tvFacilityBirthReview);
        tvLoungServices = v.findViewById(R.id.tvLoungServices);
        tvInfantAssessment = v.findViewById(R.id.tvInfantAssessment);
        tvMotherAssessment = v.findViewById(R.id.tvMotherAssessment);
        tvNurseName1 = v.findViewById(R.id.tvNurseName1);
        tvNurseName2 = v.findViewById(R.id.tvNurseName2);
        tvCount = v.findViewById(R.id.tvCount);

        //ImageView
        ivLoungeAssessment = v.findViewById(R.id.ivLoungeAssessment);
        ivFacilityBirthReview = v.findViewById(R.id.ivFacilityBirthReview);
        ivLoungServices = v.findViewById(R.id.ivLoungServices);
        ivInfantAssessment = v.findViewById(R.id.ivInfantAssessment);
        ivMotherAssessment = v.findViewById(R.id.ivMotherAssessment);
        ivNurse1 = v.findViewById(R.id.ivNurse1);
        ivNurse2 = v.findViewById(R.id.ivNurse2);

        //LinearLayout
        llNurse2 = v.findViewById(R.id.llNurse2);
        llMain = v.findViewById(R.id.llMain);

        //RelativeLayout
        rlLoungeAssessment = v.findViewById(R.id.rlLoungeAssessment);
        rlBirthReview = v.findViewById(R.id.rlBirthReview);
        rlLoungeServices = v.findViewById(R.id.rlLoungeServices);
        rlInfantsAssessment = v.findViewById(R.id.rlInfantsAssessment);
        rlMothersAssessment = v.findViewById(R.id.rlMothersAssessment);

        String time = AppUtils.getCurrentTime();

        Log.d("time-before", time);

        String[] parts = time.split(":");

        String slotSection1 = "", slotSection2 = "";

        Log.d("time-after", parts[0]);

        int currentTime = Integer.parseInt(parts[0]);

        if (currentTime >= 8 && currentTime < 14) {
            slotSection1 = getString(R.string.slot8am);
            slotSection2 = getString(R.string.slot2pm);
        } else if (currentTime >= 14 && currentTime < 20) {
            slotSection1 = getString(R.string.slot2pm);
            slotSection2 = getString(R.string.slot8pm);
        } else {
            slotSection1 = getString(R.string.slot8pm);
            slotSection2 = getString(R.string.slot8am);
        }

        tvShiftTime.setText(slotSection1 + " " + getString(R.string.to) + " " + slotSection2 + " " + getString(R.string.shift));

        rlLoungeAssessment.setOnClickListener(this);
        rlBirthReview.setOnClickListener(this);
        rlLoungeServices.setOnClickListener(this);
        rlInfantsAssessment.setOnClickListener(this);
        rlMothersAssessment.setOnClickListener(this);

        tvCount.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rlLoungeAssessment:

                if (AppSettings.getString(AppSettings.userType).equalsIgnoreCase("0")) {
                    ((MainActivity) getActivity()).displayView(1);
                }

                break;

            case R.id.rlBirthReview:

                if (AppSettings.getString(AppSettings.userType).equalsIgnoreCase("0")) {
                    ((MainActivity) getActivity()).displayView(2);
                }

                break;

            case R.id.rlLoungeServices:


                LoungeMonitoringFragment.displayView(1, mActivity);

                break;

            case R.id.rlInfantsAssessment:

                if (AppSettings.getString(AppSettings.userType).equalsIgnoreCase("0")) {
                    ((MainActivity) getActivity()).displayView(3);
                }

                break;

            case R.id.rlMothersAssessment:

                if (AppSettings.getString(AppSettings.userType).equalsIgnoreCase("0")) {
                    ((MainActivity) getActivity()).displayView(4);
                }

                break;

            case R.id.tvCount:

                // it will call when more than 2 nurse checked in
                showAllNurseDialog();

                break;

        }
    }

    private void showAllNurseDialog() {

        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_nurse_list);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        ArrayList<HashMap<String, String>> dutyListCheckOut = new ArrayList();
        dutyListCheckOut.addAll(DatabaseController.getAllCheckOutNurses());

        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity,1));
        recyclerView.setAdapter(new Adapter(dutyListCheckOut));

        RelativeLayout rlOk=dialog.findViewById(R.id.rlOk);

        dialog.show();

        rlOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        if (AppSettings.getString(AppSettings.userType).equalsIgnoreCase("1")) {
            AppUtils.enableDisable(llMain, false);

            if (AppUtils.isNetworkAvailable(mActivity)) {
                getStatusApi();
            } else {
                AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
            }
        } else {
            if (!AppSettings.getString(AppSettings.syncTime).isEmpty()) {
                String part[] = AppSettings.getString(AppSettings.syncTime).trim().split(" ");

                tvDutyDate.setText(part[0]);
                tvDutyTime.setText(AppUtils.convertTimeTo12HoursFormat(part[1]));
            }

            if (!AppSettings.getString(AppSettings.doctorRoundSyncTime).isEmpty()) {
                String part[] = AppSettings.getString(AppSettings.doctorRoundSyncTime).trim().split(" ");

                tvDoctorDutyDate.setText(part[0]);
                tvDoctorDutyTime.setText(AppUtils.convertTimeTo12HoursFormat(part[1]));
            }

            int totalBabies = DatabaseController.getAllBabies().size();

            tvNoOfInfants.setText(String.valueOf(totalBabies));
            tvNoOfMothers.setText(String.valueOf(DatabaseController.getAllMothers().size()));
            tvDoctorName.setText(AppSettings.getString(AppSettings.doctorName));

            String where = TableLounge.tableColumn.loungeId + " ='" + AppSettings.getString(AppSettings.loungeId) + "'";

            int noOfBeds = 0;
            try {
                noOfBeds = Integer.parseInt(DatabaseController.getSpecificValue(TableLounge.tableName, TableLounge.tableColumn.numberOfBeds.toString(), where));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            int available = 0;
            if (noOfBeds > 0) {
                available = noOfBeds - totalBabies;
            }

            tvBedOccupancy.setText(String.valueOf(available));

            ArrayList<HashMap<String, String>> dutyListCheckOut = new ArrayList();
            dutyListCheckOut.addAll(DatabaseController.getAllCheckOutNurses());

            if (dutyListCheckOut.size() >= 2) {
                try {
                    Picasso.get().load(dutyListCheckOut.get(0).get("profile")).into(ivNurse1);
                } catch (Exception e) {
                    ivNurse1.setImageResource(R.mipmap.nurse);
                }

                if(ivNurse1.getDrawable()==null){
                    ivNurse1.setImageResource(R.mipmap.nurse);
                }

                try {
                    Picasso.get().load(dutyListCheckOut.get(1).get("profile")).into(ivNurse2);
                } catch (Exception e) {
                    ivNurse2.setImageResource(R.mipmap.nurse);
                }

                if(ivNurse2.getDrawable()==null){
                    ivNurse2.setImageResource(R.mipmap.nurse);
                }

                tvNurseName1.setText(dutyListCheckOut.get(0).get("name"));
                tvNurseName2.setText(dutyListCheckOut.get(1).get("name"));

                int count = dutyListCheckOut.size() - 2;
                tvCount.setText("");

                if (count > 0) {
                    tvCount.setText("+" + count);
                }
            }
            else if (dutyListCheckOut.size() == 1) {
                try {
                    Picasso.get().load(dutyListCheckOut.get(0).get("profile")).into(ivNurse1);
                } catch (Exception e) {
                    ivNurse1.setImageResource(R.mipmap.nurse);
                }

                if(ivNurse1.getDrawable()==null){
                    ivNurse1.setImageResource(R.mipmap.nurse);
                }

                tvNurseName1.setText(dutyListCheckOut.get(0).get("name"));
                llNurse2.setVisibility(View.GONE);
            }

            String whereNew = " addDate " + AppUtils.getAddDateAsPerHospital();

            int loungeAssessment = DatabaseController.getCount(TableLoungeAssessment.tableName, whereNew);

            whereNew = " addDate " + AppUtils.getAddDateCurrentSlot();
            int loungeAssessmentSlotWise = DatabaseController.getCount(TableLoungeAssessment.tableName, whereNew);


            Log.v("kjghsqkjsq", String.valueOf(loungeAssessmentSlotWise));
            Log.v("kjghsqkjsq", String.valueOf(loungeAssessment));

            tvLoungeAssessment.setText("0/3");
            if (loungeAssessmentSlotWise > 0) {
                tvLoungeAssessment.setText(loungeAssessment + "/3");
                ivLoungeAssessment.setImageResource(R.drawable.ic_happy_smily);
                ivLoungeAssessment.setBackgroundResource(0);
                tvLoungeAssessment.setTextColor(getResources().getColor(R.color.r_color));
            } else {
                ivLoungeAssessment.setImageResource(R.drawable.ic_sad_smily);
                ivLoungeAssessment.setBackgroundResource(0);
                tvLoungeAssessment.setTextColor(getResources().getColor(R.color.red));
            }

            int facilityBirthReview = DatabaseController.getCount(TableBirthReview.tableName, whereNew);

            whereNew = " addDate " + AppUtils.getAddDateCurrentSlot();
            int facilityBirthReviewSlotWise = DatabaseController.getCount(TableBirthReview.tableName, whereNew);

            tvFacilityBirthReview.setText("0/3");
            if (facilityBirthReviewSlotWise >= 1) {
                tvFacilityBirthReview.setText(facilityBirthReview + "/3");
                ivFacilityBirthReview.setImageResource(R.drawable.ic_happy_smily);
                //ivFacilityBirthReview.setBackgroundResource(R.drawable.circle_teal);
                tvFacilityBirthReview.setTextColor(getResources().getColor(R.color.r_color));
            } else {
                ivFacilityBirthReview.setImageResource(R.drawable.ic_sad_smily);
                //ivFacilityBirthReview.setBackgroundResource(R.drawable.circle_red);
                tvFacilityBirthReview.setTextColor(getResources().getColor(R.color.red));
            }

            whereNew = " addDate " + AppUtils.getAddDateAsPerHospital() + " group by slot";
            int loungServices = DatabaseController.getCount(TableLoungeServices.tableName, whereNew);

            if(AppSettings.getString(AppSettings.positionValue).equals("6")){
                positiotnValue= (AppSettings.getString(AppSettings.positionValue));
            }

            tvLoungServices.setText("0/3");
            if (positiotnValue.equals("6")) {
                tvLoungServices.setText(loungServices + "/3");
                ivLoungServices.setImageResource(R.drawable.ic_happy_smily);
                //ivLoungServices.setBackgroundResource(R.drawable.circle_teal);
                tvLoungServices.setTextColor(getResources().getColor(R.color.r_color));
            } else {
                ivLoungServices.setImageResource(R.drawable.ic_sad_smily);
                //ivLoungServices.setBackgroundResource(R.drawable.circle_red);
                tvLoungServices.setTextColor(getResources().getColor(R.color.red));
            }


            whereNew = " addDate " + AppUtils.getAddDateAsPerHospital() + " group by babyId";
            int babyAssessment = DatabaseController.getCount(TableBabyMonitoring.tableName, whereNew);
            tvInfantAssessment.setText("0/3");
            if (babyAssessment > 4) {
                tvInfantAssessment.setText(babyAssessment + "/3");
                ivInfantAssessment.setImageResource(R.drawable.ic_happy_smily);
                ///ivInfantAssessment.setBackgroundResource(R.drawable.circle_teal);
                tvInfantAssessment.setTextColor(getResources().getColor(R.color.r_color));
            } else {
                ivInfantAssessment.setImageResource(R.drawable.ic_sad_smily);
                //ivInfantAssessment.setBackgroundResource(R.drawable.circle_red);
                tvInfantAssessment.setTextColor(getResources().getColor(R.color.red));
            }


            whereNew = " addDate " + AppUtils.getAddDateAsPerHospital() + " group by motherId";
            int motherAssessment = DatabaseController.getCount(TableMotherMonitoring.tableName, whereNew);

            Log.v("motherAssessment",String.valueOf(motherAssessment));

            tvMotherAssessment.setText("0/3");
            if (motherAssessment > 4) {
                tvMotherAssessment.setText(motherAssessment + "/3");
                ivMotherAssessment.setImageResource(R.drawable.ic_happy_smily);
                //ivMotherAssessment.setBackgroundResource(R.drawable.circle_teal);
                tvMotherAssessment.setTextColor(getResources().getColor(R.color.r_color));
            } else {
                tvMotherAssessment.setText(motherAssessment + "/3");
                ivMotherAssessment.setImageResource(R.drawable.ic_sad_smily);
                //ivMotherAssessment.setBackgroundResource(R.drawable.circle_red);
                tvMotherAssessment.setTextColor(getResources().getColor(R.color.red));
            }

        }
    }

    private void getStatusApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {

            jsonData.put("coachId", AppSettings.getString(AppSettings.coachId));
            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));

            json.put(AppConstants.projectName, jsonData);

            Log.v("getStatusApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.getLoungeStatusData, json, true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        tvLoungeAssessment.setText(jsonObject.getString("loungeAssessmentCountTotal"));
                        tvNoOfInfants.setText(jsonObject.getString("totalBabyAdmission"));
                        tvNoOfMothers.setText(jsonObject.getString("totalMotherAdmission"));
                        tvBedOccupancy.setText(jsonObject.getString("bedOccupancy"));

                        if (!jsonObject.getString("lastSyncedTime").isEmpty()) {
                            String part[] = jsonObject.getString("lastSyncedTime").split(" ");

                            tvDutyDate.setText(part[0]);
                            tvDutyTime.setText(AppUtils.convertTimeTo12HoursFormat(part[1]));
                        }

                        try {
                            if (!jsonObject.getJSONArray("lastDoctorRound").getJSONObject(0).getString("roundDateTime").isEmpty()) {
                                String part[] = jsonObject.getJSONArray("lastDoctorRound").getJSONObject(0).getString("roundDateTime").trim().split(" ");

                                tvDoctorDutyDate.setText(part[0]);
                                tvDoctorDutyTime.setText(AppUtils.convertTimeTo12HoursFormat(part[1]));

                                tvDoctorName.setText(jsonObject.getJSONArray("lastDoctorRound").getJSONObject(0).getString("name"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //tvDoctorName.setText(jsonObject.getString("resCode"));

                        int loungeAssessmentSlotWise = Integer.parseInt(jsonObject.getString("loungeAssessmentCount"));

                        tvLoungeAssessment.setText(jsonObject.getString("loungeAssessmentCountTotal") + "/3");
                        if (loungeAssessmentSlotWise > 0) {
                            ivLoungeAssessment.setImageResource(R.drawable.ic_happy_smily);
                            ivLoungeAssessment.setBackgroundResource(0);
                            tvLoungeAssessment.setTextColor(getResources().getColor(R.color.r_color));
                        } else {
                            ivLoungeAssessment.setImageResource(R.drawable.ic_sad_smily);
                            ivLoungeAssessment.setBackgroundResource(0);
                            tvLoungeAssessment.setTextColor(getResources().getColor(R.color.red));
                        }

                        int facilityBirthReview = Integer.parseInt(jsonObject.getString("loungeBirthReviewCount"));

                        tvFacilityBirthReview.setText(jsonObject.getString("loungeBirthReviewCountTotal") + "/3");
                        if (facilityBirthReview >= 1) {
                            ivFacilityBirthReview.setImageResource(R.drawable.ic_happy_smily);
                            //ivFacilityBirthReview.setBackgroundResource(R.drawable.circle_teal);
                            tvFacilityBirthReview.setTextColor(getResources().getColor(R.color.r_color));
                        } else {
                            ivFacilityBirthReview.setImageResource(R.drawable.ic_sad_smily);
                            //ivFacilityBirthReview.setBackgroundResource(R.drawable.circle_red);
                            tvFacilityBirthReview.setTextColor(getResources().getColor(R.color.red));
                        }

                        int loungServices = Integer.parseInt(jsonObject.getString("loungeServicesCount"));

                        tvLoungServices.setText(jsonObject.getString("loungeServicesCountTotal") + "/3");
                        if (loungServices >= 2) {
                            ivLoungServices.setImageResource(R.drawable.ic_happy_smily);
                            //ivLoungServices.setBackgroundResource(R.drawable.circle_teal);
                            tvLoungServices.setTextColor(getResources().getColor(R.color.r_color));
                        } else {
                            ivLoungServices.setImageResource(R.drawable.ic_sad_status);
                            //ivLoungServices.setBackgroundResource(R.drawable.circle_red);
                            tvLoungServices.setTextColor(getResources().getColor(R.color.red));
                        }

                        int babyAssessment = Integer.parseInt(jsonObject.getString("babyAssessmentCount"));
                        tvInfantAssessment.setText(babyAssessment + "/3");
                        if (babyAssessment > 4) {
                            ivInfantAssessment.setImageResource(R.drawable.ic_happy_smily);
                            //ivInfantAssessment.setBackgroundResource(R.drawable.circle_teal);
                            tvInfantAssessment.setTextColor(getResources().getColor(R.color.r_color));
                        } else {
                            ivInfantAssessment.setImageResource(R.drawable.ic_sad_smily);
                          //  ivInfantAssessment.setBackgroundResource(R.drawable.circle_red);
                            tvInfantAssessment.setTextColor(getResources().getColor(R.color.red));
                        }


                        int motherAssessment = Integer.parseInt(jsonObject.getString("motherAssessmentCount"));
                        tvMotherAssessment.setText(motherAssessment + "/3");


                        if (motherAssessment > 4) {
                            ivMotherAssessment.setImageResource(R.drawable.ic_happy_smily);
                            //ivMotherAssessment.setBackgroundResource(R.drawable.circle_teal);
                            tvMotherAssessment.setTextColor(getResources().getColor(R.color.r_color));
                        } else {
                            ivMotherAssessment.setImageResource(R.drawable.ic_sad_smily);
                            //ivMotherAssessment.setBackgroundResource(R.drawable.circle_red);
                            tvMotherAssessment.setTextColor(getResources().getColor(R.color.red));
                        }

                        JSONArray jsonArray = jsonObject.getJSONArray("nurseList");

                        ArrayList<HashMap<String, String>> dutyListCheckOut = new ArrayList();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                            HashMap<String, String> hashlist = new HashMap();

                            hashlist.put("id", jsonObject2.getString("staffId"));
                            hashlist.put("name", jsonObject2.getString("name"));
                            hashlist.put("profilePicture", jsonObject2.getString("profilePicture"));

                            dutyListCheckOut.add(hashlist);
                        }

                        if (dutyListCheckOut.size() >= 2) {
                            try {
                                Picasso.get().load(dutyListCheckOut.get(0).get("profilePicture")).into(ivNurse1);
                            } catch (Exception e) {
                                ivNurse1.setImageResource(R.mipmap.nurse);
                            }

                            if(ivNurse1.getDrawable()==null){
                                ivNurse1.setImageResource(R.mipmap.nurse);
                            }


                            try {
                                Picasso.get().load(dutyListCheckOut.get(1).get("profilePicture")).into(ivNurse2);
                            } catch (Exception e) {
                                ivNurse2.setImageResource(R.mipmap.nurse);
                            }

                            if(ivNurse2.getDrawable()==null){
                                ivNurse2.setImageResource(R.mipmap.nurse);
                            }

                            tvNurseName1.setText(dutyListCheckOut.get(0).get("name"));
                            tvNurseName2.setText(dutyListCheckOut.get(1).get("name"));

                            int count = dutyListCheckOut.size() - 2;
                            tvCount.setText("");

                            if (count > 0) {
                                tvCount.setText("+" + count);
                            }
                        } else if (dutyListCheckOut.size() == 1) {
                            try {
                                Picasso.get().load(dutyListCheckOut.get(0).get("profile")).into(ivNurse1);
                            } catch (Exception e) {
                                ivNurse1.setImageResource(R.mipmap.nurse);
                            }

                            if(ivNurse1.getDrawable()==null){
                                ivNurse1.setImageResource(R.mipmap.nurse);
                            }

                            tvNurseName1.setText(dutyListCheckOut.get(0).get("name"));
                            llNurse2.setVisibility(View.GONE);
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

            try {
                Picasso.get().load(data.get(position).get("profile")).into(holder.ivPic);
            } catch (Exception e) {
                holder.ivPic.setImageResource(R.mipmap.nurse);
            }

            if(holder.ivPic.getDrawable()==null){
                holder.ivPic.setImageResource(R.mipmap.nurse);
            }

            if(data.get(position).get("status").equals("1"))
            {
                holder.llMain.setBackgroundResource(R.drawable.rectangle_teal_selected);
                holder.tvName.setTextColor(getResources().getColor(R.color.white));

                AppSettings.putString(AppSettings.newNurseId,data.get(position).get("id"));
                AppSettings.putString(AppSettings.uuid,data.get(position).get("uuid"));
            }
            else
            {
                holder.llMain.setBackgroundResource(R.drawable.rectangle_grey);
                holder.tvName.setTextColor(getResources().getColor(R.color.blackNew));
            }

            holder.tvName.setText(data.get(position).get("name"));

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

}
