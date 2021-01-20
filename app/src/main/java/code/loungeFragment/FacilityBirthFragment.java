package code.loungeFragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kmcapp.android.R;

import java.util.ArrayList;
import java.util.HashMap;

import code.algo.SyncAllRecord;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableLounge;
import code.main.MainActivity;
import code.utils.AppUtils;
import code.view.BaseFragment;

public class FacilityBirthFragment extends BaseFragment implements View.OnClickListener {

    //LinearLayout
    private LinearLayout llStep1,llStep2,llStep3,llBabies;

    //TextView
    private TextView tvLabourRoom,tvBornBabies,tvBornBabies2,tvBedsRemaining,tvTotalInfants;

    //ImageView
    private ImageView ivMinusTotal,ivAddTotal,ivMinusTotalBetween,ivAddTotalBetween,ivMinus,ivAdd,ivNext;

    //EditText
    private EditText etTotal,etTotalBetween,etTotalInfants;

    //RelativeLayout
    private RelativeLayout rlNext,rlPrevious;

    //RecyclerView
    private RecyclerView recyclerView;

    //GridLayoutManager
    private GridLayoutManager mGridLayoutManager;

    private Adapter adapter;

    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();

    private ArrayList<String> nurseId = new ArrayList<String>();
    private ArrayList<String> nurseName = new ArrayList<String>();

    //Spinner
    private Spinner spinnerEnteredByNurse;

    int position=0;

    String shift = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_facility_birth,container,false);

        initialize(view);

        setListeners();

        return view;
    }

    private void initialize(View v) {

        //LinearLayout
        llStep1 = v.findViewById(R.id.llStep1);
        llStep2 = v.findViewById(R.id.llStep2);
        llStep3 = v.findViewById(R.id.llStep3);
        llBabies = v.findViewById(R.id.llBabies);

        //Spinner
        spinnerEnteredByNurse= v.findViewById(R.id.spinnerEnteredByNurse);

        //ImageView
        ivMinusTotal = v.findViewById(R.id.ivMinusTotal);
        ivAddTotal= v.findViewById(R.id.ivAddTotal);
        ivMinusTotalBetween= v.findViewById(R.id.ivMinusTotalBetween);
        ivAddTotalBetween= v.findViewById(R.id.ivAddTotalBetween);
        ivMinus= v.findViewById(R.id.ivMinus);
        ivAdd= v.findViewById(R.id.ivAdd);
        ivNext= v.findViewById(R.id.ivNext);

        //TextView
        tvLabourRoom= v.findViewById(R.id.tvLabourRoom);
        tvBornBabies= v.findViewById(R.id.tvBornBabies);
        tvBornBabies2= v.findViewById(R.id.tvBornBabies2);
        tvBedsRemaining= v.findViewById(R.id.tvBedsRemaining);
        tvTotalInfants= v.findViewById(R.id.tvTotalInfants);

        //RelativeLayout
        rlNext= v.findViewById(R.id.rlNext);
        rlPrevious= v.findViewById(R.id.rlPrevious);

        //EditText
        etTotal= v.findViewById(R.id.etTotal);
        etTotalBetween= v.findViewById(R.id.etTotalBetween);
        etTotalInfants= v.findViewById(R.id.etTotalInfants);

        nurseId.clear();
        nurseId.add(getString(R.string.selectNurse));
        nurseId.addAll(DatabaseController.getNurseIdCheckedInData());

        nurseName.clear();
        nurseName.add(getString(R.string.selectNurse));
        nurseName.addAll(DatabaseController.getNurseNameCheckedInData());

        spinnerEnteredByNurse.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, nurseName));
        spinnerEnteredByNurse.setSelection(0);

        position=0;
        setDefault();
        llStep1.setVisibility(View.VISIBLE);

        // String slot = AppUtils.getCurrentSlot1(mActivity)+" "+getString(R.string.to)+" "+AppUtils.getCurrentSlot2(mActivity);
        String slot = AppUtils.getPreviousSlot1(mActivity)+" "+getString(R.string.to)+" "+AppUtils.getPreviousSlot2(mActivity);

        //  shift = AppUtils.getCurrentShift();
        shift = AppUtils.getPreviousShift();

        String date = "";

        if (slot.startsWith(getString(R.string.slot8pm)))
            date = AppUtils.getYesterdayDate();
        else
            date = AppUtils.getCurrentDate();

        tvLabourRoom.setText(AppUtils.setSpannableMiddleBold(getString(R.string.labourLine1English),getString(R.string.labourLine2English),getString(R.string.labourLine3English),mActivity));

        if (AppSettings.getString(AppSettings.keyLanguageCode).equals("hi")){

            tvBornBabies.setText(AppUtils.setSpannableFirstLastBold(date,getString(R.string.koHindi),slot,mActivity));
            tvBornBabies2.setText(AppUtils.setSpannableMiddleBold(getString(R.string.babiesBorn),"","",mActivity));
        }
        else {
            tvBornBabies.setText(AppUtils.setSpannableMiddleBold(getString(R.string.babiesBorn),date,"",mActivity));
            tvBornBabies2.setText(AppUtils.setSpannableMiddleBold(getString(R.string.between),slot,getString(R.string.birthshift),mActivity));
        }

        //RecyclerView
        recyclerView = v.findViewById(R.id.recyclerView);

        mGridLayoutManager = new GridLayoutManager(mActivity, 1);
        recyclerView.setLayoutManager(mGridLayoutManager);

    }

    private void setListeners() {

        ivMinusTotal.setOnClickListener(this);
        ivAddTotal.setOnClickListener(this);
        ivMinusTotalBetween.setOnClickListener(this);
        ivAddTotalBetween.setOnClickListener(this);
        ivMinus.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
        rlNext.setOnClickListener(this);
        rlPrevious.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ivMinusTotal:

                int total = Integer.parseInt(etTotal.getText().toString().trim());

                if(total>0)
                {
                    total = total-1;
                }

                etTotal.setText(String.valueOf(total));

                break;

            case R.id.ivAddTotal:

                total = Integer.parseInt(etTotal.getText().toString().trim());

                total = total+1;

                etTotal.setText(String.valueOf(total));

                break;

            case R.id.ivMinusTotalBetween:

                total = Integer.parseInt(etTotalBetween.getText().toString().trim());

                if(total>0)
                {
                    total = total-1;
                }

                etTotalBetween.setText(String.valueOf(total));

                break;

            case R.id.ivAddTotalBetween:

                total = Integer.parseInt(etTotalBetween.getText().toString().trim());

                total = total+1;

                etTotalBetween.setText(String.valueOf(total));

                break;

            case R.id.ivMinus:

                total = Integer.parseInt(etTotalInfants.getText().toString().trim());

                if(total>0)
                {
                    total = total-1;
                }

                etTotalInfants.setText(String.valueOf(total));

                break;

            case R.id.ivAdd:

                total = Integer.parseInt(etTotalInfants.getText().toString().trim());

                total = total+1;

                etTotalInfants.setText(String.valueOf(total));

                break;

            case R.id.rlNext:

                ivNext.setImageResource(R.drawable.ic_next);

                int totalBirth = Integer.parseInt(etTotal.getText().toString().trim());
                int totalBetween = Integer.parseInt(etTotalBetween.getText().toString().trim());
                total = Integer.parseInt(etTotalInfants.getText().toString().trim());

                if(position==0)
                {
                    position = 1;
                    setDefault();
                    llStep2.setVisibility(View.VISIBLE);
                    rlPrevious.setVisibility(View.VISIBLE);
                }
                else if(spinnerEnteredByNurse.getSelectedItemPosition()==0&&position==1)
                {
                    AppUtils.showToastSort(mActivity,getString(R.string.errorSelectYourName));
                }
                else if(totalBirth==0){
                    AppUtils.showToastSort(mActivity,getString(R.string.totalbirthReviewerror));
                }

                else if((totalBetween+total) !=totalBirth   && position==1) {
                    AppUtils.showToastSort(mActivity,getString(R.string.birthLessThan));
                }
                else if(position==1)
                {
                    String time = AppUtils.getCurrentTime();

                    Log.d("time-before",time);

                    String[] parts = time.split(":");

                    Log.d("time-after",parts[0]);

                    int currentTime= Integer.parseInt(parts[0]);

                    String whereCondition = "";

                    if(currentTime>=8&&currentTime<14)
                    {
                        //whereCondition = " bA.addDate >= '"+AppUtils.getCurrentDateFormatted()+" 08:00:00' and bA.addDate <= '"+AppUtils.getCurrentDateFormatted()+" 13:59:59'";
                        whereCondition = " bR.addDate >= '"+AppUtils.getCurrentDateYMD(-1)+" 20:00:00' and bR.addDate <= '"+AppUtils.getCurrentDateFormatted()+" 07:59:59'";

                    }
                    else if(currentTime>=14&&currentTime<20)
                    {
                        // whereCondition = " bA.addDate >= '"+AppUtils.getCurrentDateFormatted()+" 14:00:00' and bA.addDate <= '"+AppUtils.getCurrentDateFormatted()+" 19:59:59'";

                        whereCondition = " bR.addDate >= '"+AppUtils.getCurrentDateFormatted()+" 08:00:00' and bR.addDate <= '"+AppUtils.getCurrentDateFormatted()+" 13:59:59'";


                    }
                    else if(currentTime>=20&&currentTime<=24)
                    {

                        whereCondition = " bR.addDate >= '"+AppUtils.getCurrentDateFormatted()+" 14:00:00' and bR.addDate <= '"+AppUtils.getCurrentDateFormatted()+" 19:59:59'";

                        // whereCondition = " bA.addDate >= '"+AppUtils.getCurrentDateFormatted()+" 20:00:00' and bA.addDate <= '"+AppUtils.getCurrentDateFormatted()+" 23:59:59'";
                    }
                    else
                    {
                        whereCondition = " bR.addDate >= '"+AppUtils.getCurrentDateYMD(-1)+" 20:00:00' and bR.addDate <= '"+AppUtils.getCurrentDateFormatted()+" 07:59:59'";
                    }


                    arrayList.clear();
                    arrayList.addAll(DatabaseController.getAllBabiesAdmitted(whereCondition));

                    int value = Integer.parseInt(etTotalInfants.getText().toString().trim());

                    if(arrayList.size()==0 )
                    {
                        llBabies.setVisibility(View.GONE);
                    }
                    else
                    {
                        llBabies.setVisibility(View.VISIBLE);
                    }

                    if(arrayList.size()==0 && value==0)
                    {
                        if (etTotalInfants.getText().toString().trim().equals("0") || etTotalInfants.getText().toString().trim().equals("1")){
//                            tvTotalInfants.setText(getString(R.string.totalInfantsIs)
//                                    +" "+ etTotalInfants.getText().toString().trim());
                            if(AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en") || AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase(""))
                            {
                                tvTotalInfants.setText(getString(R.string.totalInfantsIs)
                                        +" "+ etTotalInfants.getText().toString().trim()
                                        +" "+getString(R.string.butNone)
                                        +" "+getString(R.string.haveNotBeenAdmitted));
                            }
                            else
                            {
                                tvTotalInfants.setText("कुल शिशु "+etTotalInfants.getText().toString().trim()+" हैं जो कि 2000 ग्राम से कम हैं , लेकिन कोई भर्ती नहीं किए गए हैं।");
                            }
                        }
                        else {
                            tvTotalInfants.setText(getString(R.string.totalInfants)
                                    +" "+ etTotalInfants.getText().toString().trim());
                        }
                    }
                    else  if(arrayList.size() == 0)
                    {

                        if (etTotalInfants.getText().toString().trim().equals("0") || etTotalInfants.getText().toString().trim().equals("1")){
                            if(AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en") || AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase(""))
                            {
                                tvTotalInfants.setText(getString(R.string.totalInfantsIs)
                                        +" "+ etTotalInfants.getText().toString().trim()
                                        +" "+getString(R.string.butNone)
                                        +" "+getString(R.string.haveNotBeenAdmitted));
                            }
                            else
                            {
                                tvTotalInfants.setText("कुल शिशु "+etTotalInfants.getText().toString().trim()+" हैं जो कि 2000 ग्राम से कम हैं , लेकिन कोई भर्ती नहीं किए गए हैं।");

                            }

                        }
                        else {
                            if(AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en") || AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase(""))
                            {
                                tvTotalInfants.setText(getString(R.string.totalInfants)
                                        +" "+ etTotalInfants.getText().toString().trim()
                                        +" "+getString(R.string.butNone)
                                        +" "+getString(R.string.haveNotBeenAdmitted));
                            }
                            else
                            {
                                tvTotalInfants.setText("कुल शिशु "+etTotalInfants.getText().toString().trim()+" हैं जो कि 2000 ग्राम से कम हैं , लेकिन कोई भर्ती नहीं किए गए हैं।");

                            }
                        }

                    }
                    else
                    {
                        if (etTotalInfants.getText().toString().trim().equals("0") || etTotalInfants.getText().toString().trim().equals("1")){

                            if(AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en") || AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("")) {
                                tvTotalInfants.setText(getString(R.string.totalInfantsIs)
                                        + " " + etTotalInfants.getText().toString().trim()
                                        + " " + getString(R.string.butOnly)
                                        + " " + arrayList.size()
                                        + " " + getString(R.string.haveBeenAdmitted));
                            }
                            else
                            {
                                tvTotalInfants.setText("कुल शिशु "+etTotalInfants.getText().toString().trim()+"  हैं जो कि 2000 ग्राम से कम हैं , लेकिन केवल "+arrayList.size()+"  भर्ती  किए गए हैं।");
                            }

                        }
                        else {
                            if(AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en") || AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("")) {
                                tvTotalInfants.setText(getString(R.string.totalInfants)
                                        +" "+ etTotalInfants.getText().toString().trim()
                                        +" "+getString(R.string.butOnly)
                                        +" "+arrayList.size()
                                        +" "+getString(R.string.haveBeenAdmitted));
                            }
                            else
                            {
                                tvTotalInfants.setText("कुल शिशु "+etTotalInfants.getText().toString().trim()+"  हैं जो कि 2000 ग्राम से कम हैं , लेकिन केवल "+arrayList.size()+"  भर्ती  किए गए हैं।");
                            }
                        }

                    }

                    int totalBabies = DatabaseController.getAllBabies().size();

                    String where = TableLounge.tableColumn.loungeId + " ='"+ AppSettings.getString(AppSettings.loungeId)+"'";

                    int noOfBeds =  0;
                    try {
                        noOfBeds = Integer.parseInt(DatabaseController.getSpecificValue(TableLounge.tableName,TableLounge.tableColumn.numberOfBeds.toString(),where));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    int available=0;
                    if(noOfBeds>0)
                    {
                        available = noOfBeds - totalBabies;
                    }

                    String count = available+" " + getString(R.string.outOf)+" " +noOfBeds;

                    if (available==0){

                        tvBedsRemaining.setText(AppUtils.setSpannableMiddleBold("","",getString(R.string.noBedsAvailable),mActivity));

                    }
                    else if (available==1){
                        tvBedsRemaining.setText(AppUtils.setSpannableMiddleBold("",count,getString(R.string.bedIsAvailableLine),mActivity));
                    }
                    else
                        tvBedsRemaining.setText(AppUtils.setSpannableMiddleBold("",count,getString(R.string.bedsAvailableLine),mActivity));


                    recyclerView.setVisibility(View.VISIBLE);
                    adapter = new Adapter(arrayList);
                    recyclerView.setAdapter(adapter);

                    position = 2;
                    setDefault();
                    llStep3.setVisibility(View.VISIBLE);
                    rlPrevious.setVisibility(View.VISIBLE);
                    ivNext.setImageResource(R.drawable.ic_tick);
                    ivNext.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                }
                else
                {
                    DatabaseController.saveBirthReview(shift,totalBirth,totalBetween,total,nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()));

                    if (AppUtils.isNetworkAvailable(mActivity)) {
                        AppSettings.putString(AppSettings.syncTime,AppUtils.currentTimestampFormat());
                        SyncAllRecord.postDutyChange(mActivity);
                    } else {
                        AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
                    }

                    getActivity().onBackPressed();
                }

                break;

            case R.id.rlPrevious:

                ivNext.setImageResource(R.drawable.ic_next);

                if(position==2)
                {
                    position = 1;
                    setDefault();
                    llStep2.setVisibility(View.VISIBLE);
                    rlPrevious.setVisibility(View.VISIBLE);
                }
                else if(position==1)
                {
                    position = 0;
                    setDefault();
                    llStep1.setVisibility(View.VISIBLE);
                    rlPrevious.setVisibility(View.GONE);
                }

                break;

            default:

                break;

        }

    }

    private void setDefault() {

        llStep1.setVisibility(View.GONE);
        llStep2.setVisibility(View.GONE);
        llStep3.setVisibility(View.GONE);

    }

    private class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

        ArrayList<HashMap<String, String>> data;
        int type;

        public Adapter(ArrayList<HashMap<String, String>> arrayList) {

            data = arrayList;
        }

        @NonNull
        @Override
        public Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_child, viewGroup, false);
            return new Adapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Adapter.MyViewHolder holder, final int position) {

            if(data.get(position).get("motherName")==null)
            {
                holder.tvName.setText(getString(R.string.babyOf) +" "+getString(R.string.unknown));
            }
            else
            {
                holder.tvName.setText(getString(R.string.babyOf) +" "+data.get(position).get("motherName"));
            }

            try {
                byte[] decodedString = Base64.decode(data.get(position).get("babyPhoto"), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                holder.ivPic.setImageBitmap(decodedByte);

                //Picasso.get().load(data.get(position).get("babyPhoto")).into(holder.ivPic);
            } catch (Exception e) {
                //e.printStackTrace();
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
                if(pulse<75||pulse>200)
                {
                    checkPulse=0;
                }
                else
                {
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

            View row=inflater.inflate(R.layout.inflate_spinner, parent, false);

            TextView tvName=row.findViewById(R.id.tvName);

            tvName.setText(data.get(position));

            return row;
        }
    }




}
