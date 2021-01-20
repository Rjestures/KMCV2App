package code.loungeFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kmcapp.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.database.AppSettings;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;


public class MonthlyDashboardFragment extends BaseFragment implements View.OnClickListener {

    //TestView
    private TextView tvTotalAdmission, tvAdmission2500, tvAdmission2000, tvBedOccupancy, tvLoungeCleaned, tvWeightGain, tvDurationKmc,
            tvInfantBreastfeed, tvInfantsWeighed, tvInfantsTimely, tvDischargeCriteria, tvOnTimeDuty, tvDoctorRound;

    //PieChartView
    private PieChartView chart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mothly_dashboard, container, false);

        init(view);
        setListeners();

        return view;
    }

    private void init(View view) {

        //PieChartView
        chart = view.findViewById(R.id.pieChart);

        //TextView
        tvTotalAdmission = view.findViewById(R.id.tvTotalAdmission);
        tvAdmission2500 = view.findViewById(R.id.tvAdmission2500);
        tvAdmission2000 = view.findViewById(R.id.tvAdmission2000);

        tvBedOccupancy = view.findViewById(R.id.tvBedOccupancy);
        tvLoungeCleaned = view.findViewById(R.id.tvLoungeCleaned);
        tvWeightGain = view.findViewById(R.id.tvWeightGain);
        tvDurationKmc = view.findViewById(R.id.tvDurationKmc);
        tvInfantBreastfeed = view.findViewById(R.id.tvInfantBreastfeed);
        tvInfantsWeighed = view.findViewById(R.id.tvInfantsWeighed);
        tvInfantsTimely = view.findViewById(R.id.tvInfantsTimely);
        tvDischargeCriteria = view.findViewById(R.id.tvDischargeCriteria);
        tvOnTimeDuty = view.findViewById(R.id.tvOnTimeDuty);
        tvDoctorRound = view.findViewById(R.id.tvDoctorRound);

        //tvBedOccupancy

        TextView tvShiftTime = view.findViewById(R.id.tvShiftTime);
        tvShiftTime.setText(AppUtils.getOnlyCurrentMonth());

        if (AppUtils.isNetworkAvailable(mActivity)) {
            getMonthlyDashbaordApi();
        } else {
            AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
        }
    }

    private void setPieGraph(int totalBabies2500, int totalBabies2000) {

        PieChartData data;

        List<SliceValue> values = new ArrayList<SliceValue>();
        SliceValue sliceValue = new SliceValue(totalBabies2500, getResources().getColor(R.color.r_color));
        values.add(sliceValue);

        sliceValue = new SliceValue(totalBabies2000, getResources().getColor(R.color.oo_color));
        values.add(sliceValue);

        data = new PieChartData(values);
        chart.setPieChartData(data);
    }

    private void setListeners() {
    }

    @Override
    public void onClick(View v) {

    }

    private void getMonthlyDashbaordApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {

            if (AppSettings.getString(AppSettings.userType).equalsIgnoreCase("1")) {
                jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                jsonData.put("coachId", AppSettings.getString(AppSettings.coachId));
                jsonData.put("type", "2");
            } else {
                jsonData.put("coachId", "");
                jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                jsonData.put("type", "1");
            }


            json.put(AppConstants.projectName, jsonData);

            Log.v("getMonthylDashbaordApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.getMonthlyDashboardData, json, true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        if (AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en")) {

                            tvLoungeCleaned.setText(AppUtils.setSpannable2("", jsonObject.getString("outOfDay"), getString(R.string.days) + " " + getString(R.string.outOf) + " " + jsonObject.getString("loungeCleaned") + " " + getString(R.string.days), mActivity));

                        } else {

                            tvLoungeCleaned.setText(AppUtils.setSpannable2("", jsonObject.getString("loungeCleaned"), getString(R.string.days) + " " + getString(R.string.outOf) + " " + jsonObject.getString("outOfDay") + " " + getString(R.string.days), mActivity));

                        }


                        if (AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en")) {

                            tvOnTimeDuty.setText(AppUtils.setSpannable2("", jsonObject.getString("onTimeNurseCheckin"), getString(R.string.outOf) + " " + jsonObject.getString("totalNurseCheckin"), mActivity));

                        } else {

                            tvOnTimeDuty.setText(AppUtils.setSpannable2("", jsonObject.getString("totalNurseCheckin"), getString(R.string.outOf) + " " + jsonObject.getString("onTimeNurseCheckin"), mActivity));

                        }





                        tvAdmission2500.setText(AppUtils.setSpannable(getString(R.string._2500_2000_g) + " ", jsonObject.getString("getTotalAvgWtBaby"), "", mActivity));
                        tvAdmission2000.setText(AppUtils.setSpannable(getString(R.string._2000_g) + " ", jsonObject.getString("getTotalLBWBaby"), "", mActivity));

                        //tvBedOccupancy.setText(jsonObject.getString("tvOnTimeDuty"));
                        tvInfantsWeighed.setText(jsonObject.getString("percentageOfDailyWeight"));
                        tvInfantsTimely.setText(jsonObject.getString("avgTotalKmc"));
                        tvDischargeCriteria.setText(jsonObject.getString("dischargePercentage") + "%");


                        tvDoctorRound.setText(jsonObject.getString("doctorRoundPercentage") + "%");

                        if (AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en")) {
                            tvLoungeCleaned.setText(AppUtils.setSpannable2("", jsonObject.getString("loungeCleaned"), getString(R.string.days) + " " + getString(R.string.outOf) + " " + jsonObject.getString("outOfDay") + " " + getString(R.string.days), mActivity));

                        } else {
                            tvLoungeCleaned.setText(AppUtils.setSpannable2("", jsonObject.getString("outOfDay"), getString(R.string.days) + " " + getString(R.string.outOf) + " " + jsonObject.getString("loungeCleaned") + " " + getString(R.string.days), mActivity));

                        }

                        tvTotalAdmission.setText(jsonObject.getString("getTotalAdmission"));
                        tvWeightGain.setText(jsonObject.getString("totalWeightGain") + " " + getString(R.string.kgPerDay));
                        tvDurationKmc.setText(jsonObject.getString("avgTotalKmc") + " " + getString(R.string.hoursPerDay));
                        tvInfantBreastfeed.setText(jsonObject.getString("percentageOfExpressed"));

                        int weight2500 = 0;

                        try {
                            weight2500 = Integer.parseInt(jsonObject.getString("getTotalAvgWtBaby"));
                        } catch (NumberFormatException | JSONException e) {
                            e.printStackTrace();
                        }

                        int weight2000 = 0;
                        try {
                            weight2000 = Integer.parseInt(jsonObject.getString("getTotalLBWBaby"));
                        } catch (NumberFormatException | JSONException e) {
                            e.printStackTrace();
                        }

                        setPieGraph(weight2500, weight2000);

                    } else {

                        AppUtils.hideDialog();
                        AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));
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
