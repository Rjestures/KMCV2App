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
import android.widget.ArrayAdapter;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import code.algo.SyncBabyRecord;
import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.database.AppSettings;
import code.database.DataBaseHelper;
import code.database.DatabaseController;
import code.database.TableBabyRegistration;
import code.database.TableKMC;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;


public class KMCFragment extends BaseFragment implements View.OnClickListener {

    //RecyclerView
    private RecyclerView recyclerView;

    //GridLayoutManager
    private GridLayoutManager mGridLayoutManager;

    //Adapter
    private Adapter adapter;

    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();

    //ArrayList
    private ArrayList<String> providerName = new ArrayList<String>();
    private ArrayList<String> providerValue = new ArrayList<String>();
    private ArrayList<String> nurseId = new ArrayList<String>();
    private ArrayList<String> nurseName = new ArrayList<String>();

    private String lastSSCEntry="",uuid = "" ;

    private String duration = "00:00";

    //RelativeLayout
    private RelativeLayout rlCircle;

    //TextView
    private TextView tvDuration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_kmc,container,false);

        initialize(view);

        setListeners();

        return view;
    }

    private void initialize(View v) {

        //RecyclerView
        recyclerView = v.findViewById(R.id.recyclerView);

        //TextView
        tvDuration = v.findViewById(R.id.tvDuration);

        //RelativeLayout
        rlCircle = v.findViewById(R.id.rlCircle);

        mGridLayoutManager = new GridLayoutManager(mActivity, 1);
        recyclerView.setLayoutManager(mGridLayoutManager);

        //DatabaseController.myDataBase.delete(TableKMC.tableName, null, null);

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

        DataBaseHelper.copyDatabase(mActivity);

        String where = AppUtils.getCurrentDateAsPerHospital();
        //String where = AppUtils.getCurrentDateFormatted();

        arrayList.clear();
        arrayList.addAll(DatabaseController.getKMCBabyWiseData(
                AppSettings.getString(AppSettings.babyId),
                where));

        String duration = DatabaseController.getSSCViaIdToday(AppSettings.getString(AppSettings.babyId),where);

        Log.d("duration",duration);

        String unit = "";
        if(duration.equals("0"))
        {
            unit = getString(R.string.hour);
        }
        else
        {
            String[] parts = duration.split(":");

            Log.d("time-hour",parts[0]);
            Log.d("time-min",parts[1]);

            if(parts[0].equals("0")||parts[0].equals("1")||parts[0].equals("01"))
            {
                unit = getString(R.string.hour);
            }
            else
            {
                unit = getString(R.string.hours);
            }
        }

        tvDuration.setText(getString(R.string.today)+" ("+AppUtils.getCurrentDateFormatted()+") : "
                +duration
                +" "+unit +" "+getString(R.string.since8AM));

        if(arrayList.size()>0)
        {
            lastSSCEntry = arrayList.get(0).get("endDate") + " "+arrayList.get(0).get("endTime");
        }

        recyclerView.setVisibility(View.VISIBLE);
        adapter = new Adapter(arrayList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rlCircle:

                AlertKmc();

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
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_kmc, viewGroup, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

            //holder.tvName.setText(data.get(position).get("name"));

            holder.tvStartDateAndTime.setText(data.get(position).get("startDate")+" "+AppUtils.convertTimeTo12HoursFormat(data.get(position).get("startTime")));
            holder.tvStopDateAndTime.setText(data.get(position).get("endDate")+" "+AppUtils.convertTimeTo12HoursFormat(data.get(position).get("endTime")));
            //holder.tvEndTime.setText(AppUtils.convertTimeTo12HoursFormat(data.get(position).get("endTime")));
            holder.tvProvider.setText(data.get(position).get("provider"));

            String stTime="",endTime="";

            stTime=data.get(position).get("startDate") + " " +data.get(position).get("startTime");
            endTime=data.get(position).get("endDate") + " " +data.get(position).get("endTime");

            duration="00:00";

            try {
                duration = AppUtils.getSSTTime(stTime,endTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.tvDuration.setText(String.valueOf(duration));

            /*if(duration==0)
            {
                duration = AppUtils.getSSTTime(stTime,endTime);
            }
            else
            {
                duration = duration + AppUtils.getSSTTime(stTime,endTime);
            }*/

            /*float twoDigitsF = 0;
            try {
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                twoDigitsF = Float.valueOf(decimalFormat.format(duration));
                Log.i("log_tag", "Hours: " + twoDigitsF);

                holder.tvDuration.setText(String.valueOf(twoDigitsF));

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }*/

            //tvTotalTime.setText(twoDigitsF+" "+getString(R.string.hrs));

            if(AppSettings.getString(AppSettings.userType).equals("1"))
            {
                //holder.rlEdit.setVisibility(View.GONE);
            }
            else if(AppSettings.getString(AppSettings.userType).equals("0"))
            {
                //holder.rlEdit.setVisibility(View.VISIBLE);
            }

            /*holder.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ((MainActivity)getActivity()).displayView(6);

                }
            });*/
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tvStartDateAndTime,tvStopDateAndTime,tvProvider,tvDuration;

            public MyViewHolder(View itemView) {
                super(itemView);

                tvStartDateAndTime =  itemView.findViewById(R.id.tvStartDateAndTime);
                tvStopDateAndTime =  itemView.findViewById(R.id.tvStopDateAndTime);
                //tvEndTime =  itemView.findViewById(R.id.tvStopTime);
                tvDuration =  itemView.findViewById(R.id.tvDuration);
                tvProvider =  itemView.findViewById(R.id.tvProvider);
            }
        }
    }


    //AlertKmc
    private void AlertKmc() {
        final Dialog dialog = new Dialog(mActivity,android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_kmc);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        RelativeLayout rlStartDate  = dialog.findViewById(R.id.rlStartDate);
        RelativeLayout rlStartTime  = dialog.findViewById(R.id.rlStartTime);
        RelativeLayout rlStopDate   = dialog.findViewById(R.id.rlStopDate);
        RelativeLayout rlStopTime   = dialog.findViewById(R.id.rlStopTime);
        RelativeLayout rlOk         = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlCancel     = dialog.findViewById(R.id.rlCancel);

        TextView tvStartDate = dialog.findViewById(R.id.tvStartDate);
        TextView tvStartTime = dialog.findViewById(R.id.tvStartTime);
        TextView tvStopDate = dialog.findViewById(R.id.tvStopDate);
        TextView tvStopTime = dialog.findViewById(R.id.tvStopTime);

        uuid  = UUID.randomUUID().toString();

        Spinner spinnerProvider = dialog.findViewById(R.id.spinnerProvider);
        Spinner spinnerEnteredByNurse = dialog.findViewById(R.id.spinnerEnteredByNurse);

        providerName.clear();
        providerName.add(getString(R.string.selectProvider));
        providerName.add(getString(R.string.mother));
        providerName.add(getString(R.string.grandMother));
        providerName.add(getString(R.string.aunty));
        providerName.add(getString(R.string.father));
        providerName.add(getString(R.string.other));

        providerValue.clear();
        providerValue.add(getString(R.string.selectProvider));
        providerValue.add(getString(R.string.motherValue));
        providerValue.add(getString(R.string.grandMotherValue));
        providerValue.add(getString(R.string.auntyValue));
        providerValue.add(getString(R.string.fatherValue));
        providerValue.add(getString(R.string.otherValue));

        spinnerProvider.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, providerName));
        spinnerProvider.setSelection(0);

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

                AppUtils.dateDialog(mActivity,tvStartDate,0);

            }
        });

        rlStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppUtils.timeDialog(mActivity,tvStartTime);

            }
        });

        rlStopDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppUtils.dateDialog(mActivity,tvStopDate,0);

            }
        });

        rlStopTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppUtils.timeDialog(mActivity,tvStopTime);

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


                String time[] = AppUtils.getAllSlot();

                int currentTimeSlot= Integer.parseInt(time[0]);

                if(!tvStartTime.getText().toString().trim().isEmpty()&&spinnerProvider.getSelectedItemPosition()!=0)
                {
                    uuid  = UUID.randomUUID().toString();

                    saveKMC("0",tvStartTime.getText().toString().trim(),
                            tvStopTime.getText().toString().trim(),
                            tvStartDate.getText().toString().trim(),
                            tvStopDate.getText().toString().trim(),
                            nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()),
                            providerValue.get(spinnerProvider.getSelectedItemPosition()),uuid);
                }

                long startDnT = 0,endSnT = 0;
                long currentTime = 0;

                if(!tvStartTime.getText().toString().trim().isEmpty()&&!tvStartDate.getText().toString().trim().isEmpty())
                {
                    String formattedDate = tvStartDate.getText().toString().trim()+", "+tvStartTime.getText().toString().trim();

                    startDnT = Long.parseLong(AppUtils.changeDateToTimestamp2(formattedDate));
                    currentTime = AppUtils.getCurrentTimestamp();
                }

                if(!tvStopDate.getText().toString().trim().isEmpty()&&!tvStopTime.getText().toString().trim().isEmpty())
                {
                    String formattedDate = tvStopDate.getText().toString().trim()+", "+tvStopTime.getText().toString().trim();

                    endSnT = Long.parseLong(AppUtils.changeDateToTimestamp2(formattedDate));
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
                else if(currentTime<=startDnT)
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorCurrentTime));
                }
                else if(tvStopDate.getText().toString().trim().isEmpty())
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectDate));
                }
                else   if(tvStopTime.getText().toString().trim().isEmpty())
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectTime));
                }
                else if(currentTime<=endSnT)
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorCurrentTime));
                }
                else if(spinnerProvider.getSelectedItemPosition()==0)
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorProvider));
                }
                else if(spinnerEnteredByNurse.getSelectedItemPosition()==0)
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectYourName));
                }
                else if(AppUtils.getDateTimeDifference(tvStartDate.getText().toString().trim()+" "+AppUtils.convertTimeTo24HoursFormat(tvStartTime.getText().toString().trim()),
                        tvStopDate.getText().toString().trim()+" "+AppUtils.convertTimeTo24HoursFormat(tvStopTime.getText().toString().trim())).equalsIgnoreCase("1"))
                {
                    if(AppUtils.isDateTimeBetweenSlots(tvStartDate.getText().toString().trim()+" "+AppUtils.convertTimeTo24HoursFormat(tvStartTime.getText().toString().trim())).equalsIgnoreCase("0")
                               &&AppUtils.isDateTimeBetweenSlots(tvStopDate.getText().toString().trim()+" "+AppUtils.convertTimeTo24HoursFormat(tvStopTime.getText().toString().trim())).equalsIgnoreCase("0"))
                    {
                        if(!lastSSCEntry.isEmpty())
                        {
                            if(AppUtils.isDateTimeLess(lastSSCEntry,
                                    tvStartDate.getText().toString().trim()+" "+AppUtils.convertTimeTo24HoursFormat(tvStartTime.getText().toString().trim())).equalsIgnoreCase("1"))
                            {
                                saveKMC("1",tvStartTime.getText().toString().trim(),
                                        tvStopTime.getText().toString().trim(),
                                        tvStartDate.getText().toString().trim(),
                                        tvStopDate.getText().toString().trim(),
                                        nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()),
                                        providerValue.get(spinnerProvider.getSelectedItemPosition()),uuid);

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
                            else
                            {
                                AppUtils.showToastSort(mActivity, getString(R.string.errorSSCLastTime));
                            }
                        }
                        else
                        {
                            saveKMC("1",tvStartTime.getText().toString().trim(),
                                    tvStopTime.getText().toString().trim(),
                                    tvStartDate.getText().toString().trim(),
                                    tvStopDate.getText().toString().trim(),
                                    nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()),
                                    providerValue.get(spinnerProvider.getSelectedItemPosition()),uuid);

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
                    else if(AppUtils.isDateTimeBetweenSlots(tvStartDate.getText().toString().trim()+" "+AppUtils.convertTimeTo24HoursFormat(tvStartTime.getText().toString().trim())).equalsIgnoreCase("1")
                                      &&AppUtils.isDateTimeBetweenSlots(tvStopDate.getText().toString().trim()+" "+AppUtils.convertTimeTo24HoursFormat(tvStopTime.getText().toString().trim())).equalsIgnoreCase("0"))
                    {
                        if(!AppUtils.getCurrentDateFormatted().equalsIgnoreCase(tvStartDate.getText().toString().trim()))
                        {
                            if(tvStartTime.getText().toString().trim().contains("AM"))
                            {
                                saveKMC("1",tvStartTime.getText().toString().trim(),
                                        "07:59 AM",
                                        tvStartDate.getText().toString().trim(),
                                        tvStartDate.getText().toString().trim(),
                                        nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()),
                                        providerValue.get(spinnerProvider.getSelectedItemPosition()),uuid);

                                uuid  = UUID.randomUUID().toString();

                                saveKMC("1","08:00 AM",
                                        "07:59 AM",
                                        tvStartDate.getText().toString().trim(),
                                        tvStopDate.getText().toString().trim(),
                                        nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()),
                                        providerValue.get(spinnerProvider.getSelectedItemPosition()),uuid);
                            }
                            else  if(tvStartTime.getText().toString().trim().contains("PM"))
                            {
                                saveKMC("1",tvStartTime.getText().toString().trim(),
                                        "07:59 AM",
                                        tvStartDate.getText().toString().trim(),
                                        tvStopDate.getText().toString().trim(),
                                        nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()),
                                        providerValue.get(spinnerProvider.getSelectedItemPosition()),uuid);
                            }
                        }
                        else
                        {
                            if(tvStartTime.getText().toString().trim().contains("AM"))
                            {
                                saveKMC("1",tvStartTime.getText().toString().trim(),
                                        "07:59 AM",
                                        tvStartDate.getText().toString().trim(),
                                        tvStartDate.getText().toString().trim(),
                                        nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()),
                                        providerValue.get(spinnerProvider.getSelectedItemPosition()),uuid);
                            }
                        }

                        uuid  = UUID.randomUUID().toString();

                        saveKMC("1","08:00 AM",
                                tvStopTime.getText().toString().trim(),
                                tvStopDate.getText().toString().trim(),
                                tvStopDate.getText().toString().trim(),
                                nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()),
                                providerValue.get(spinnerProvider.getSelectedItemPosition()),uuid);

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
                    else if(AppUtils.isDateTimeBetweenSlots(tvStartDate.getText().toString().trim()+" "+AppUtils.convertTimeTo24HoursFormat(tvStartTime.getText().toString().trim())).equalsIgnoreCase("1")
                                    &&AppUtils.isDateTimeBetweenSlots(tvStopDate.getText().toString().trim()+" "+AppUtils.convertTimeTo24HoursFormat(tvStopTime.getText().toString().trim())).equalsIgnoreCase("1"))
                    {
                        saveKMC("1",tvStartTime.getText().toString().trim(),
                                tvStopTime.getText().toString().trim(),
                                tvStartDate.getText().toString().trim(),
                                tvStopDate.getText().toString().trim(),
                                nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()),
                                providerValue.get(spinnerProvider.getSelectedItemPosition()),uuid);

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
                    else
                    {
                        AppUtils.showToastSort(mActivity, getString(R.string.errorSelectDateTime));
                    }
                }
                else
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorStartEndDate));
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


    private void saveKMC(String status,String startTime,String endTime,String date,String endDate,String nurseId,String kmcProvider,String uuid) {

        ContentValues mContentValues = new ContentValues();

        mContentValues.put(TableKMC.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        mContentValues.put(TableKMC.tableColumn.serverId.toString(), "");
        mContentValues.put(TableKMC.tableColumn.uuid.toString(), uuid);
        mContentValues.put(TableKMC.tableColumn.babyId.toString(), AppSettings.getString(AppSettings.babyId));
        mContentValues.put(TableKMC.tableColumn.startTime.toString(),AppUtils.convertTimeTo24HoursFormat(startTime));
        mContentValues.put(TableKMC.tableColumn.endTime.toString(), AppUtils.convertTimeTo24HoursFormat(endTime));
        mContentValues.put(TableKMC.tableColumn.provider.toString(), kmcProvider);
        mContentValues.put(TableKMC.tableColumn.startDate.toString(), date);
        mContentValues.put(TableKMC.tableColumn.endDate.toString(),endDate);
        mContentValues.put(TableKMC.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
        mContentValues.put(TableKMC.tableColumn.modifyDate.toString(),AppUtils.currentTimestampFormat());
        mContentValues.put(TableKMC.tableColumn.syncTime.toString(),"");
        mContentValues.put(TableKMC.tableColumn.isDataSynced.toString(),"0");
        mContentValues.put(TableKMC.tableColumn.status.toString(), status);
        mContentValues.put(TableKMC.tableColumn.nurseId.toString(), nurseId);

        DatabaseController.insertUpdateData(mContentValues, TableKMC.tableName, TableKMC.tableColumn.uuid.toString(), uuid);
    }


    //Get All Babies
    private void getBabyDataApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {

            jsonData.put("coachId", AppSettings.getString(AppSettings.coachId));
            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("babyId", AppSettings.getString(AppSettings.babyId));
            jsonData.put("type", "1");
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

                        JSONArray jsonArray = jsonObject.getJSONArray("kmcData");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject arrayJSONObject = jsonArray.getJSONObject(i);

                            HashMap<String, String> hashlist = new HashMap();

                            hashlist.put("uuid", arrayJSONObject.getString("androidUuid"));
                            hashlist.put("startDate", arrayJSONObject.getString("startDate"));
                            hashlist.put("provider",arrayJSONObject.getString("provider"));
                            hashlist.put("startTime", arrayJSONObject.getString("startTime"));
                            hashlist.put("endTime", arrayJSONObject.getString("endTime"));
                            hashlist.put("endDate",arrayJSONObject.getString("endDate"));
                            hashlist.put("nurseId", arrayJSONObject.getString("nurseId"));

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

        String duration="0";

        for (int i = 0; i < arrayList.size(); i++) {

            if(duration.equals("0"))
            {
                try {
                    duration =AppUtils.getSSTTime(arrayList.get(i).get("startDate")+ " "+arrayList.get(i).get("startTime")
                            ,arrayList.get(i).get("endDate")+ " "+arrayList.get(i).get("endTime"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                try {
                    duration = AppUtils.sumTimes(duration ,AppUtils.getSSTTime(arrayList.get(i).get("startDate")+ " "+arrayList.get(i).get("startTime")
                            ,arrayList.get(i).get("endDate")+ " "+arrayList.get(i).get("endTime")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        Log.d("duration",duration);

        String unit = "";
        if(duration.equals("0"))
        {
            unit = getString(R.string.hour);
        }
        else
        {
            String[] parts = duration.split(":");

            Log.d("time-hour",parts[0]);
            Log.d("time-min",parts[1]);

            if(parts[0].equals("0")||parts[0].equals("1")||parts[0].equals("01"))
            {
                unit = getString(R.string.hour);
            }
            else
            {
                unit = getString(R.string.hours);
            }
        }

        tvDuration.setText(getString(R.string.today)+" ("+AppUtils.getCurrentDateFormatted()+") : "
                                   +duration
                                   +" "+unit +" "+getString(R.string.since8AM));
    }

}
