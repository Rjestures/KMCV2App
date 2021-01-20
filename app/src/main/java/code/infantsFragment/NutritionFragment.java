package code.infantsFragment;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kmcapp.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import code.algo.SyncBabyRecord;
import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableBabyRegistration;
import code.database.TableBreastFeeding;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;


public class NutritionFragment extends BaseFragment implements View.OnClickListener {

    //RecyclerView
    private RecyclerView recyclerView;

    //GridLayoutManager
    private GridLayoutManager mGridLayoutManager;

    private Adapter adapter;
    public static   int count=0;

    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();

    //ArrayList
    private ArrayList<String> feedingValue = new ArrayList<String>();
    private ArrayList<String> feedingName = new ArrayList<String>();
    private ArrayList<String> feedingTypeName = new ArrayList<String>();
    private ArrayList<String> nurseId = new ArrayList<String>();
    private ArrayList<String> nurseName = new ArrayList<String>();
    private ArrayList<String> fluidType = new ArrayList<String>();
    private ArrayList<String> fluidTypeName = new ArrayList<String>();

    private String lastFeedEntry="",uuid = "",feedingMainType="",feedingMethod="",feedingType="" ;

    private float duration = 0;

    private RelativeLayout rlCircle;

    private  TextView tvDuration,tvQuantity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_nutrition,container,false);

        initialize(view);

        setListeners();

        return view;
    }

    private void initialize(View v) {

        //RecyclerView
        recyclerView = v.findViewById(R.id.recyclerView);

        //RelativeLayout
        rlCircle = v.findViewById(R.id.rlCircle);

        //TextView
        tvDuration = v.findViewById(R.id.tvDuration);
        tvQuantity = v.findViewById(R.id.tvQuantity);

        tvQuantity.setText(getString(R.string.quantity)+" ("+getString(R.string.ml)+")");

        mGridLayoutManager = new GridLayoutManager(mActivity, 1);
        recyclerView.setLayoutManager(mGridLayoutManager);

        //DatabaseController.myDataBase.delete(TableBreastFeeding.tableName, null, null);
    }

    private void setListeners() {

        rlCircle.setOnClickListener(this);
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
            rlCircle.setVisibility(View.GONE);

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

    private  void checkRecord()
    {
        String count = String.valueOf(DatabaseController.isDataExit(TableBabyRegistration.tableName));

        Log.d("count.isDataExit",count);

        String quantityML = DatabaseController.getTotalQuantity(
                AppSettings.getString(AppSettings.babyId),AppUtils.getCurrentDateFormatted())+" "+getString(R.string.ml);

        String times = DatabaseController.getTotalMethod(mActivity, AppSettings.getString(AppSettings.babyId), AppUtils.getCurrentDateFormatted(),"1");

        if(times.equals("0")||times.equals("1"))
        {
            times = times + " "+ getString(R.string.noOfTime);
        }
        else
        {
            times = times + " "+ getString(R.string.noOfTimes);
        }

        tvDuration.setText(getString(R.string.today)+" ("+AppUtils.getCurrentDateFormatted()+") : "
                                   +quantityML
                                   +", "+times +" "+getString(R.string.since8AM));

        arrayList.clear();
        arrayList.addAll(DatabaseController.getFeedingData(AppSettings.getString(AppSettings.babyId),AppUtils.getCurrentDateNew()));

        recyclerView.setVisibility(View.VISIBLE);
        adapter = new Adapter(arrayList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rlCircle:

                AlertFeeding();

                break;


            default:

                break;

        }

    }

    private class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

        ArrayList<HashMap<String, String>> data;

        public Adapter(ArrayList<HashMap<String, String>> arrayList) {

            data = arrayList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_nutrition, viewGroup, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

            if(position==0)
            {
                lastFeedEntry = AppUtils.convertTimeTo12HoursFormat(data.get(0).get("feedTime"));
            }

            holder.tvDate.setText(data.get(position).get("date"));
            holder.tvTime.setText(AppUtils.convertTimeTo12HoursFormat(data.get(position).get("feedTime")));
            holder.tvFluid.setText(data.get(position).get("fluid"));
            holder.tvMethod.setText(data.get(position).get("method"));

            if(!data.get(position).get("quantity").isEmpty()){
                Float qty= Float.valueOf(data.get(position).get("quantity"));
                holder.tvQuantity.setText(String.valueOf(qty));
            }


            if(data.get(position).get("method").equalsIgnoreCase(getString(R.string.breastValue)))
            {
                holder.tvQuantity.setText(getString(R.string.notApplicableShortValue));
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            //TextView
            TextView tvDate,tvTime,tvFluid,tvMethod,tvQuantity;

            //LinearLayout
            LinearLayout llMain;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                //TextView
                tvDate = itemView.findViewById(R.id.tvDate);
                tvTime = itemView.findViewById(R.id.tvTime);
                tvFluid = itemView.findViewById(R.id.tvFluid);
                tvMethod = itemView.findViewById(R.id.tvMethod);
                tvQuantity = itemView.findViewById(R.id.tvQuantity);

                //LinearLayout
                llMain = itemView.findViewById(R.id.llMain);

            }
        }
    }

    //AlertFeeding
    private void AlertFeeding() {
        final Dialog dialog = new Dialog(mActivity,android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_feeding);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        RelativeLayout rlStartDate  = dialog.findViewById(R.id.rlStartDate);
        RelativeLayout rlStartTime  = dialog.findViewById(R.id.rlStartTime);
        RelativeLayout rlCancel  = dialog.findViewById(R.id.rlCancel);
        RelativeLayout rlOk  = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlSpecify = dialog.findViewById(R.id.rlSpecify);
        RelativeLayout rlQuantity = dialog.findViewById(R.id.rlQuantity);

        TextView tvStartDate = dialog.findViewById(R.id.tvStartDate);
        TextView tvStartTime = dialog.findViewById(R.id.tvStartTime);

        Spinner spinnerFluid            = dialog.findViewById(R.id.spinnerFluid);
        Spinner spinnerEnteredByNurse   = dialog.findViewById(R.id.spinnerEnteredByNurse);
        Spinner spinnerFeedingType     = dialog.findViewById(R.id.spinnerFeedingType);

        EditText etQuantity = dialog.findViewById(R.id.etQuantity);
        EditText etSpecify = dialog.findViewById(R.id.etSpecify);

        uuid  = UUID.randomUUID().toString();

        feedingName.clear();
        feedingName.add(getString(R.string.selectFeeding));
        feedingName.add(getString(R.string.breast));
        feedingName.add(getString(R.string.spoon));
        feedingName.add(getString(R.string.ogTube));
        feedingName.add(getString(R.string.ivDrip));

        feedingTypeName.clear();
        feedingTypeName.add(getString(R.string.selectFeeding));
        feedingTypeName.add(getString(R.string.breastValue));
        feedingTypeName.add(getString(R.string.spoonValue));
        feedingTypeName.add(getString(R.string.ogTubeValue));
        feedingTypeName.add(getString(R.string.ivDripValue));

        spinnerFeedingType.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, feedingName));
        spinnerFeedingType.setSelection(0);

        //Spinner for Feeding Type
        spinnerFeedingType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                feedingMethod = feedingTypeName.get(position);

                rlSpecify.setVisibility(View.GONE);
                rlQuantity.setVisibility(View.VISIBLE);

                if(position==0)
                {
                    feedingMainType ="";
                    feedingMethod = "";
                    rlSpecify.setVisibility(View.GONE);
                    rlQuantity.setVisibility(View.GONE);
                }
                else if(position==1)
                {
                    feedingMainType="1";
                    etQuantity.setText("");
                    rlSpecify.setVisibility(View.GONE);
                    rlQuantity.setVisibility(View.GONE);

                    fluidType.clear();
                    fluidType.add(getString(R.string.selectFluid));
                    fluidType.add(getString(R.string.motherOwnMilk));
                    fluidType.add(getString(R.string.otherHumanMilk));

                    fluidTypeName.clear();
                    fluidTypeName.add(getString(R.string.selectFluid));
                    fluidTypeName.add(getString(R.string.motherOwnMilkValue));
                    fluidTypeName.add(getString(R.string.otherHumanMilkValue));

                    spinnerFluid.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, fluidType));
                    spinnerFluid.setSelection(0);

                }
                else if(position==2||position==3)
                {
                    feedingMainType="2";
                    etQuantity.setText("");

                    fluidType.clear();
                    fluidType.add(getString(R.string.selectFluid));
                    fluidType.add(getString(R.string.motherOwnMilk));
                    fluidType.add(getString(R.string.otherHumanMilk));
                    fluidType.add(getString(R.string.formulaMilk));
                    fluidType.add(getString(R.string.animalMilk));

                    fluidTypeName.clear();
                    fluidTypeName.add(getString(R.string.selectFluid));
                    fluidTypeName.add(getString(R.string.motherOwnMilkValue));
                    fluidTypeName.add(getString(R.string.otherHumanMilkValue));
                    fluidTypeName.add(getString(R.string.formulaMilkValue));
                    fluidTypeName.add(getString(R.string.animalMilkValue));

                    spinnerFluid.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, fluidType));
                    spinnerFluid.setSelection(0);
                }
                else if(position==4)
                {
                    feedingMainType="2";
                    etQuantity.setText("");

                    fluidType.clear();
                    fluidType.add(getString(R.string.selectFluid));
                    fluidType.add(getString(R.string.ivFluid));

                    fluidTypeName.clear();
                    fluidTypeName.add(getString(R.string.selectFluid));
                    fluidTypeName.add(getString(R.string.ivFluidValue));

                    spinnerFluid.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, fluidType));
                    spinnerFluid.setSelection(0);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        //Spinner for Feeding Type
        spinnerFluid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                rlSpecify.setVisibility(View.GONE);

                etSpecify.setText("");

                if(fluidTypeName.get(position).equalsIgnoreCase(getString(R.string.animalMilkValue))
                ||fluidTypeName.get(position).equalsIgnoreCase(getString(R.string.ivFluidValue)))
                {
                    rlSpecify.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        nurseId.clear();
        nurseId.add(getString(R.string.selectNurse));
        nurseId.addAll(DatabaseController.getNurseIdCheckedInData());

        nurseName.clear();
        nurseName.add(getString(R.string.selectNurse));
        nurseName.addAll(DatabaseController.getNurseNameCheckedInData());

        spinnerEnteredByNurse.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, nurseName));
        spinnerEnteredByNurse.setSelection(0);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        rlStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppUtils.dateDialog(mActivity,tvStartDate,5);

            }
        });

        rlStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppUtils.timeDialog(mActivity,tvStartTime);

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

                if(!tvStartTime.getText().toString().trim().isEmpty()&&!feedingMethod.isEmpty())
                {
                    saveFeeding("0","","","","","");
                }

                long time1 = 0;
                long currentTime = 0;

                if(!tvStartTime.getText().toString().trim().isEmpty())
                {
                    Date c = Calendar.getInstance().getTime();
                    System.out.println("Current time => " + c);

                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    String formattedDate = df.format(c);

                    formattedDate = formattedDate+", "+tvStartTime.getText().toString().trim();

                    time1 = Long.parseLong(AppUtils.changeDateToTimestamp(formattedDate));
                    currentTime = AppUtils.getCurrentTimestamp();
                }


                if(tvStartDate.getText().toString().trim().isEmpty())
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectDate));
                }
                else if(tvStartTime.getText().toString().trim().isEmpty())
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectTime));
                }
                else if(currentTime<=time1)
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorCurrentTime));
                }
                else  if(feedingMainType.isEmpty())
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectMethod));
                }
                else  if(spinnerFluid.getSelectedItemPosition()==0)
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectFluid));
                }
                else  if((fluidTypeName.get(spinnerFluid.getSelectedItemPosition()).equalsIgnoreCase(getString(R.string.animalMilk))
                ||fluidTypeName.get(spinnerFluid.getSelectedItemPosition()).equalsIgnoreCase(getString(R.string.ivFluidValue)))
                                 && etSpecify.getText().toString().trim().isEmpty())
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.pleaseSpecify));
                }
                else  if(feedingMainType.equalsIgnoreCase("2")&&etQuantity.getText().toString().trim().isEmpty())
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorEnterQuantity));
                }



                else  if(feedingMainType.equalsIgnoreCase("2")&&!etQuantity.getText().toString().trim().isEmpty()  && (Integer.valueOf(etQuantity.getText().toString())<0 ||  Integer.valueOf(etQuantity.getText().toString())>150) )
                {
                    etQuantity.requestFocus();
                    AppUtils.showToastSort(mActivity, getString(R.string.errorQuantity));
                }


                else if(spinnerEnteredByNurse.getSelectedItemPosition()==0)
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectYourName));
                }
                else if(!lastFeedEntry.isEmpty()) {
                    if (AppUtils.getTimeDifference(lastFeedEntry,
                            tvStartTime.getText().toString().trim()).equalsIgnoreCase("1")) {

                        if(spinnerFeedingType.getSelectedItem().equals("Direct breastfeeding")) {
                            count++;
                        }
                        saveFeeding("1",
                                nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()),
                                tvStartTime.getText().toString().trim(),
                                etQuantity.getText().toString().trim(),
//                                String.valueOf(count),
                                fluidTypeName.get(spinnerFluid.getSelectedItemPosition()),
                                etSpecify.getText().toString().trim());

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

                    } else {
                        AppUtils.showToastSort(mActivity, getString(R.string.errorSSCLastTime));
                    }
                }
                else
                {
                    saveFeeding("1",
                            nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()),
                            tvStartTime.getText().toString().trim(),
                            etQuantity.getText().toString().trim(),
                            fluidTypeName.get(spinnerFluid.getSelectedItemPosition()),
                            etSpecify.getText().toString().trim());

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


    public void saveFeeding(String status,String nurseId,String time,String quantity,String fluid,String specify) {
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(TableBreastFeeding.tableColumn.uuid.toString(), uuid);
        mContentValues.put(TableBreastFeeding.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        mContentValues.put(TableBreastFeeding.tableColumn.babyId.toString(), AppSettings.getString(AppSettings.babyId));
        mContentValues.put(TableBreastFeeding.tableColumn.nurseId.toString(), nurseId);
        mContentValues.put(TableBreastFeeding.tableColumn.feedTime.toString(),AppUtils.convertTimeTo24HoursFormat(time));
//        mContentValues.put(TableBreastFeeding.tableColumn.feedTime.toString(),count);
        mContentValues.put(TableBreastFeeding.tableColumn.duration.toString(), "");
        mContentValues.put(TableBreastFeeding.tableColumn.method.toString(), feedingMethod);
        mContentValues.put(TableBreastFeeding.tableColumn.fluid.toString(), fluid);
        mContentValues.put(TableBreastFeeding.tableColumn.specify.toString(), specify);
        mContentValues.put(TableBreastFeeding.tableColumn.quantity.toString(), quantity);
        mContentValues.put(TableBreastFeeding.tableColumn.date.toString(), AppUtils.getCurrentDateNew());
        mContentValues.put(TableBreastFeeding.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
        mContentValues.put(TableBreastFeeding.tableColumn.modifyDate.toString(),AppUtils.currentTimestampFormat());
        mContentValues.put(TableBreastFeeding.tableColumn.syncTime.toString(),"");
        mContentValues.put(TableBreastFeeding.tableColumn.status.toString(), status);

        DatabaseController.insertUpdateData(mContentValues, TableBreastFeeding.tableName,
                TableBreastFeeding.tableColumn.uuid.toString(), uuid);
    }

    //Get All Babies
    private void getBabyDataApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {

            jsonData.put("coachId", AppSettings.getString(AppSettings.coachId));
            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("babyId", AppSettings.getString(AppSettings.babyId));
            jsonData.put("type", "2");
            jsonData.put("date", AppUtils.getCurrentDateFormatted());

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

                        arrayList.clear();

                        JSONArray jsonArray = jsonObject.getJSONArray("feedingData");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject arrayJSONObject = jsonArray.getJSONObject(i);

                            HashMap<String, String> hashlist = new HashMap();

                            hashlist.put("uuid", arrayJSONObject.getString("androidUuid"));
                            hashlist.put("feedTime", arrayJSONObject.getString("feedTime"));
                            hashlist.put("duration","");
                            hashlist.put("method", arrayJSONObject.getString("breastFeedMethod"));
                            hashlist.put("quantity", arrayJSONObject.getString("milkQuantity"));
                            hashlist.put("date",arrayJSONObject.getString("feedDate"));
                            hashlist.put("fluid",arrayJSONObject.getString("fluid"));

                            arrayList.add(hashlist);
                        }

                        adapter = new Adapter(arrayList);
                        recyclerView.setAdapter(adapter);

                        setDuration();
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

    private void setDuration() {

        int times = 0;
        float quantity = 0;

        for (int i = 0; i < arrayList.size(); i++) {

            if(arrayList.get(i).get("method").equalsIgnoreCase(getString(R.string.breastValue)))
            {
                times = times + 1;
            }
            else
            {
                quantity = (quantity + Float.parseFloat(arrayList.get(i).get("quantity")));
            }

        }

        String timesString = "";
        if(times<=1)
        {
            timesString = times + " "+ getString(R.string.noOfTime);
        }
        else
        {
            timesString = times + " "+ getString(R.string.noOfTimes);
        }

        tvDuration.setText(getString(R.string.today)+" ("+AppUtils.getCurrentDateFormatted()+") : "
                                   +quantity+" "+getString(R.string.ml)
                                   +", "+timesString +" "+getString(R.string.since8AM));

    }
}
