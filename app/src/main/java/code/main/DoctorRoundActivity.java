package code.main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kmcapp.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import code.algo.SyncAllRecord;
import code.algo.SyncBabyRecord;
import code.common.LocaleHelper;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableDoctorRound;
import code.database.TableInvestigation;
import code.database.TableTreatment;
import code.utils.AppConstants;
import code.utils.AppUtils;
import code.view.BaseActivity;

public class DoctorRoundActivity extends BaseActivity implements View.OnClickListener {

    //RecyclerView
    RecyclerView recyclerView;

    //GridLayoutManager
    GridLayoutManager mGridLayoutManager;

    //TreatmentAdapter
    TreatmentAdapter treatmentAdapter;

    //InvesAdapter
    InvesAdapter invesAdapter;

    ArrayList<String> investigationType = new ArrayList<String>();
    ArrayList<String> investigationTypeValue = new ArrayList<String>();
    ArrayList<String> investigationNameValue = new ArrayList<String>();
    ArrayList<String> investigationName = new ArrayList<String>();

    //LinearLayout
    private LinearLayout llSync, llHelp, llLogout, llLanguage;

    //RelativeLayout
    private RelativeLayout rlHelp, rlStuck, rlOwn, rlSelectDoctor;

    String uuid = UUID.randomUUID().toString();

    //ImageView
    private ImageView ivPlus;

    //TextView
    TextView tvHeading, tvTreatment, tvInvestigation,/*tvSubmit,*/
            tvSelectDoctor;

    //LinearLayout
    LinearLayout llMedicine, llInvestigation;

    //RelativeLayout
    RelativeLayout rlMenu, rlExpandMenu, rlCircle;

    //String
    private String selectedDoctorId = "";

    static int check = 0, from = 0;

    //ArrayList
    ArrayList<String> doctorId = new ArrayList<String>();
    ArrayList<String> doctorName = new ArrayList<String>();

    // for doctor name and id
    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

  /*  //Spinner
    Spinner spinnerDoctor;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_round);

        initialize();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void initialize() {

        //RecyclerView
        recyclerView = findViewById(R.id.recyclerView);

        //TextView
        tvHeading = findViewById(R.id.tvHeading);
        tvTreatment = findViewById(R.id.tvTreatment);
        tvInvestigation = findViewById(R.id.tvInvestigation);
        //tvSubmit            = findViewById(R.id.tvSubmit);
        tvSelectDoctor = findViewById(R.id.tvSelectDoctor);

        //LinearLayout
        llMedicine = findViewById(R.id.llMedicine);
        llInvestigation = findViewById(R.id.llInvestigation);

        //RelativeLayout
        rlMenu = findViewById(R.id.rlMenu);
        rlExpandMenu = findViewById(R.id.rlExpandMenu);
        rlCircle = findViewById(R.id.rlCircle);
        rlHelp = findViewById(R.id.rlHelp);
        rlStuck = findViewById(R.id.rlStuck);
        rlOwn = findViewById(R.id.rlOwn);
        rlSelectDoctor = findViewById(R.id.rlSelectDoctor);

        //LinearLayout
        llSync = findViewById(R.id.llSync);
        llHelp = findViewById(R.id.llHelp);
        llLogout = findViewById(R.id.llLogout);
        llLanguage = findViewById(R.id.llLanguage);

        //ImageView
        ivPlus = findViewById(R.id.ivPlus);

        mGridLayoutManager = new GridLayoutManager(mActivity, 1);
        recyclerView.setLayoutManager(mGridLayoutManager);

        tvHeading.setText(getString(R.string.doctorRound));

        //Spinner
        //spinnerDoctor = findViewById(R.id.spinnerDoctor);

        doctorId.clear();
        // doctorId.add(getString(R.string.selectDoctor));
        doctorId.addAll(DatabaseController.getDocIdData());

        doctorName.clear();
        //   doctorName.add(getString(R.string.selectDoctor));
        doctorName.addAll(DatabaseController.getDocNameData());

        arrayList.clear();

        for (int i = 0; i < doctorName.size(); i++) {

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("name", doctorName.get(i));
            hashMap.put("status", "0");

            arrayList.add(hashMap);

        }

        //spinnerDoctor.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, doctorName));
        //spinnerDoctor.setSelection(0);

        //setOnClickListener
        llMedicine.setOnClickListener(this);
        llInvestigation.setOnClickListener(this);
        tvTreatment.setOnClickListener(this);
        tvInvestigation.setOnClickListener(this);
        rlCircle.setOnClickListener(this);
        ivPlus.setOnClickListener(this);
        //tvSubmit.setOnClickListener(this);
        llSync.setOnClickListener(this);
        llHelp.setOnClickListener(this);
        llLogout.setOnClickListener(this);
        llLanguage.setOnClickListener(this);
        rlHelp.setOnClickListener(this);
        rlStuck.setOnClickListener(this);
        rlOwn.setOnClickListener(this);
        rlSelectDoctor.setOnClickListener(this);

        setDefault();
        tvTreatment.setTextColor(getResources().getColor(R.color.white));
        tvTreatment.setBackgroundResource(R.drawable.rectangle_teal_round_new);

        showDoctorDialog();

    }

    private void showDoctorDialog() {

        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_doctor_list);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();

        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);

        RelativeLayout rlCancel, rlOk;

        rlCancel = dialog.findViewById(R.id.rlCancel);
        rlOk = dialog.findViewById(R.id.rlOk);

        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 1));
        recyclerView.setAdapter(new Adapter(arrayList, dialog));

        rlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });

        rlOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedDoctorId.isEmpty()){

                    AppUtils.showToastSort(mActivity, getString(R.string.selectDoctor));

                }

                else {
                    ssetTreatmentAndInvestigationList();
                    dialog.dismiss();
                }


            }
        });

    }

    private void setDefault() {

        tvTreatment.setTextColor(getResources().getColor(R.color.blackNew));
        tvInvestigation.setTextColor(getResources().getColor(R.color.blackNew));
        tvTreatment.setBackgroundResource(0);
        tvInvestigation.setBackgroundResource(0);
    }


    private void setTreatment() {

        setDefault();
        tvTreatment.setTextColor(getResources().getColor(R.color.white));
        tvTreatment.setBackgroundResource(R.drawable.rectangle_teal_round_new);

        treatmentAdapter = new TreatmentAdapter(AppConstants.treatmentList);
        recyclerView.setAdapter(treatmentAdapter);

    }

    private void setInvestigation() {

        setDefault();
        tvInvestigation.setTextColor(getResources().getColor(R.color.white));
        tvInvestigation.setBackgroundResource(R.drawable.rectangle_teal_round_new);

        invesAdapter = new InvesAdapter(AppConstants.investigationList);
        recyclerView.setAdapter(invesAdapter);

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (from == 0) {
            setTreatment();
        } else if (from == 1) {
            setInvestigation();
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.llLanguage:

                AppUtils.AlertLanguageConfirm(mActivity, getString(R.string.languageAlert));

                break;

            case R.id.llSync:

                if (AppUtils.isNetworkAvailable(mActivity)) {
                    SyncAllRecord.postDutyChange(mActivity);
                } else {
                    AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
                }

                break;

            case R.id.llLogout:

                AppUtils.AlertLogoutConfirm(mActivity);

                break;

            case R.id.llHelp:

            case R.id.rlHelp:

                if (rlHelp.getVisibility() == View.VISIBLE) {
                    rlHelp.setVisibility(View.GONE);
                } else {
                    rlHelp.setVisibility(View.VISIBLE);
                }

                break;

            case R.id.rlStuck:

                rlHelp.setVisibility(View.GONE);
                DatabaseController.saveHelp(mActivity);
                AppUtils.AlertHelpConfirm(getString(R.string.callRegardingIssue), mActivity, 1, "");

                break;

            case R.id.rlOwn:

                rlHelp.setVisibility(View.GONE);
                startActivity(new Intent(mActivity, TutorialActivity.class));

                break;

            case R.id.llBack:

                onBackPressed();

                break;

            case R.id.llMedicine:

                Animation aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rorate0);
                ivPlus.startAnimation(aniRotateClk);
                AppUtils.collapse(rlExpandMenu);
                startActivity(new Intent(mActivity, TreatmentAddActivity.class));

                break;

            case R.id.llInvestigation:

                aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rorate0);
                ivPlus.startAnimation(aniRotateClk);
                AppUtils.collapse(rlExpandMenu);
                startActivity(new Intent(mActivity, InvestigationAddActivity.class));

                break;

            case R.id.tvTreatment:

                from = 0;
                setTreatment();

                break;

            case R.id.tvInvestigation:

                from = 1;
                setInvestigation();

                break;

            case R.id.ivPlus:

            case R.id.rlCircle:

                if (from == 0) {
                    uuid = UUID.randomUUID().toString();
                    AlertTreatment(uuid, "", "", AppConstants.treatmentList.size());
                } else if (from == 1) {
                    uuid = UUID.randomUUID().toString();
                    AlertInvestigation(uuid, "", "", "", AppConstants.investigationList.size());
                }

                /*if (rlExpandMenu.getVisibility()==View.GONE)
                {
                    aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate45);
                    ivPlus.startAnimation(aniRotateClk);
                    AppUtils.expand(rlExpandMenu);
                }
                else
                {
                    aniRotateClk = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rorate0);
                    ivPlus.startAnimation(aniRotateClk);
                    AppUtils.collapse(rlExpandMenu);
                }*/

                break;

           /* case R.id.tvSubmit:

                if(selectedDoctorId.isEmpty())
                {
                    AppUtils.showToastSort(mActivity,getString(R.string.selectDoctor));
                }
                else
                {
                    saveDoctorRound();
                    saveTreatment();
                    saveInvestigation();

                    if (AppUtils.isNetworkAvailable(mActivity)) {
                        DoctorRoundActivity.check=1;
                        SyncBabyRecord.getBabyDataToUpdate(mActivity,AppSettings.getString(AppSettings.babyId));
                        onBackPressed();
                    }
                    else
                    {
                        AppUtils.showToastSort(mActivity, getString(R.string.dataSaved));
                        DoctorRoundActivity.check=1;
                        onBackPressed();
                    }
                }

                break;*/


            default:

                break;
        }
    }


    public static String convertMillieToHMmSs(long millie) {
        long seconds = (millie / 1000);
        long second = seconds % 60;
        long minute = (seconds / 60) % 60;
        long hour = (seconds / (60 * 60)) % 24;

        String result = "";
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            return String.format("%02d:%02d", minute, second);
        }

    }


    private class TreatmentAdapter extends RecyclerView.Adapter<TreatmentAdapter.Holder> {
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        public TreatmentAdapter(ArrayList<HashMap<String, String>> favList) {
            data = favList;
        }

        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_medications, parent, false));
        }

        @SuppressLint("SetTextI18n")
        public void onBindViewHolder(Holder holder, final int position) {

            holder.tvTreatmentName.setText(data.get(position).get("treatmentName"));
            holder.tvDoctorName.setText(data.get(position).get("doctorName"));

            if (!data.get(position).get("comment").isEmpty()) {
                holder.tvComment.setText("- " + data.get(position).get("comment"));
            }

            if (data.get(position).get("doctorId").equals(selectedDoctorId) && data.get(position).get("nurseId").isEmpty() &&
                    AppUtils.getSecondsDifference(Long.parseLong(AppUtils.changeDateToTimestamp3(data.get(position).get("addDate"))),
                            AppUtils.getCurrentTimestamp())<900){

                holder.ivDelete.setVisibility(View.VISIBLE);
                holder.ivEdit.setVisibility(View.VISIBLE);

            }
            else {

                holder.ivDelete.setVisibility(View.GONE);
                holder.ivEdit.setVisibility(View.GONE);

            }

            if (!data.get(position).get("notePicture").isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(data.get(position).get("notePicture"), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.ivPicture.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showDeleteAlert(data.get(position).get("uuid"), data.get(position),position, 1);
                }
            });

            holder.ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertTreatment(data.get(position).get("uuid"),
                            data.get(position).get("treatmentName"),
                            data.get(position).get("comment"),
                            position);
                }
            });

            Log.v("qksjqsq", String.valueOf(AppUtils.getSecondsDifference(Long.valueOf(AppUtils.changeDateToTimestamp3(data.get(position).get("addDate"))),
                    AppUtils.getCurrentTimestamp())));

        }

        public int getItemCount() {
            return data.size();
        }

        private class Holder extends RecyclerView.ViewHolder {

            //ImageView
            ImageView ivPicture, ivDelete, ivEdit;

            //TextView
            TextView tvTreatmentName, tvComment, tvDoctorName;

            public Holder(View itemView) {
                super(itemView);

                //TextView
                tvTreatmentName = itemView.findViewById(R.id.tvTreatmentName);
                tvComment = itemView.findViewById(R.id.tvComment);
                tvDoctorName = itemView.findViewById(R.id.tvDoctorName);

                //ImageView
                ivDelete = itemView.findViewById(R.id.ivDelete);
                ivEdit = itemView.findViewById(R.id.ivEdit);
                ivPicture = itemView.findViewById(R.id.ivPicture);
            }
        }
    }

    private void showDeleteAlert(String uuid, HashMap<String, String> hashMap, int position, int type) {

        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_yes_no);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //RelativeLayout
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);

        //TextView
        TextView tvMessage = dialog.findViewById(R.id.tvMessage);

        if (type == 1)
            tvMessage.setText(getString(R.string.areYouSureTreatmentDelete));
        else
            tvMessage.setText(getString(R.string.areYouSureInvestigationDelete));

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        rlCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        rlOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (type == 1) {

                    AppConstants.treatmentList.remove(position);
                    treatmentAdapter.notifyDataSetChanged();

                    HashMap<String, String> hashlist = new HashMap<>();

                    hashlist.put("uuid", uuid);
                    hashlist.put("babyId", AppSettings.getString(AppSettings.babyId));
                    hashlist.put("treatmentName", hashMap.get("treatmentName"));
                    hashlist.put("comment", hashMap.get("comment"));
                    hashlist.put("notePicture",  hashMap.get("notePicture"));
                    hashlist.put("doctorName",  hashMap.get("doctorName"));
                    hashlist.put("doctorId",hashMap.get("doctorId"));
                    hashlist.put("nurseId",hashMap.get("nurseId"));
                    hashlist.put("addDate", String.valueOf(AppUtils.currentTimestampFormat()));
                    hashlist.put("status","3");

                    dialog.dismiss();
                //    setTreatment();

                    saveDoctorRound(hashlist,"1", uuid);
                    saveTreatment(hashlist, uuid);

                } else {

                    AppConstants.investigationList.remove(position);
                    invesAdapter.notifyDataSetChanged();

                    HashMap<String, String> hashlist = new HashMap<>();

                    hashlist.put("uuid", uuid);
                    hashlist.put("babyId", AppSettings.getString(AppSettings.babyId));
                    hashlist.put("investigationType", hashMap.get("investigationType"));
                    hashlist.put("investigationName", hashMap.get("investigationName"));
                    hashlist.put("comment",hashMap.get("comment"));
                    hashlist.put("doctorName",hashMap.get("doctorName"));
                    hashlist.put("doctorId",hashMap.get("doctorId"));
                    hashlist.put("nurseId",hashMap.get("nurseId"));
                    hashlist.put("status","3");
                    hashlist.put("addDate", hashMap.get("addDate"));

                    //setInvestigation();

                    saveDoctorRound(hashlist,"2", uuid);
                    saveInvestigation(hashlist,uuid);


                }

                if (AppUtils.isNetworkAvailable(mActivity)) {
                    DoctorRoundActivity.check = 1;
                    SyncBabyRecord.getBabyDataToUpdate(mActivity, AppSettings.getString(AppSettings.babyId));

                } else {
                    AppUtils.showToastSort(mActivity, getString(R.string.dataSaved));
                    DoctorRoundActivity.check = 1;

                }



                dialog.dismiss();
            }
        });

    }



    private class InvesAdapter extends RecyclerView.Adapter<InvesAdapter.InvesHolder> {
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        public InvesAdapter(ArrayList<HashMap<String, String>> favList) {
            data = favList;
        }

        public InvesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new InvesHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_investigation, parent, false));
        }

        @SuppressLint("SetTextI18n")
        public void onBindViewHolder(InvesHolder holder, final int position) {

            holder.tvName.setText(data.get(position).get("investigationType") + " - " + data.get(position).get("investigationName"));
            holder.tvDoctorName.setText(data.get(position).get("doctorName"));

            if (!data.get(position).get("comment").isEmpty()) {
                holder.tvComment.setText("- " + data.get(position).get("comment"));
            }

            if (data.get(position).get("doctorId").equals(selectedDoctorId) && data.get(position).get("nurseId").isEmpty()  &&
                    AppUtils.getSecondsDifference(Long.valueOf(AppUtils.changeDateToTimestamp3(data.get(position).get("addDate"))),
                            AppUtils.getCurrentTimestamp())<900){

                holder.ivDelete.setVisibility(View.VISIBLE);
                holder.ivEdit.setVisibility(View.VISIBLE);

            }
            else {

                holder.ivDelete.setVisibility(View.GONE);
                holder.ivEdit.setVisibility(View.GONE);

            }


            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showDeleteAlert(data.get(position).get("uuid"), data.get(position),position, 2);

                }
            });


            holder.ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertInvestigation(data.get(position).get("uuid"),
                            data.get(position).get("investigationType"),
                            data.get(position).get("investigationName"),
                            data.get(position).get("comment"),
                            position);
                }
            });
        }

        public int getItemCount() {
            return data.size();
        }

        private class InvesHolder extends RecyclerView.ViewHolder {

            //TextView
            TextView tvName, tvComment, tvDoctorName;

            //ImageView
            ImageView ivDelete, ivEdit;

            public InvesHolder(View itemView) {
                super(itemView);

                //TextView
                tvName = itemView.findViewById(R.id.tvName);
                tvComment = itemView.findViewById(R.id.tvComment);
                tvDoctorName = itemView.findViewById(R.id.tvDoctorName);

                //ImageView
                ivDelete = itemView.findViewById(R.id.ivDelete);
                ivEdit = itemView.findViewById(R.id.ivEdit);
            }
        }
    }


    @Override
    public void onBackPressed() {

        if (DoctorRoundActivity.check == 0) {
            AppUtils.AlertCloseActivity(mActivity, getString(R.string.sureToCancel));
        } else {
            finish();
        }
    }

    private class adapterSpinner extends ArrayAdapter<String> {

        ArrayList<String> data;

        private adapterSpinner(Context context, int textViewResourceId, ArrayList<String> arraySpinner_time) {

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

            View row = inflater.inflate(R.layout.inflate_spinner_new, parent, false);

            TextView tvName = row.findViewById(R.id.tvName);

            tvName.setText(data.get(position));

            return row;
        }
    }

    private void saveDoctorRound(HashMap<String, String> hashlist, String type, String uuid) {

        ContentValues mContentValues = new ContentValues();

        mContentValues.put(TableDoctorRound.tableColumn.uuid.toString(), uuid);
        //mContentValues.put(TableDoctorRound.tableColumn.doctorId.toString(), doctorId.get(spinnerDoctor.getSelectedItemPosition()));
        mContentValues.put(TableDoctorRound.tableColumn.doctorId.toString(), selectedDoctorId);
        mContentValues.put(TableDoctorRound.tableColumn.babyId.toString(), AppSettings.getString(AppSettings.babyId));
        mContentValues.put(TableDoctorRound.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        mContentValues.put(TableDoctorRound.tableColumn.signature.toString(), "");
        mContentValues.put(TableDoctorRound.tableColumn.syncTime.toString(), "");
        mContentValues.put(TableDoctorRound.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
        mContentValues.put(TableDoctorRound.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());
        mContentValues.put(TableDoctorRound.tableColumn.json.toString(), createJson(hashlist,type,uuid).toString());
        mContentValues.putNull(TableDoctorRound.tableColumn.isDataSynced.toString());

        DatabaseController.insertUpdateData(mContentValues, TableDoctorRound.tableName,
                TableDoctorRound.tableColumn.uuid.toString(), uuid);

        Log.v("lkjhkhkk", uuid);

        AppSettings.putString(AppSettings.doctorRoundSyncTime, AppUtils.currentTimestampFormat());
        //AppSettings.putString(AppSettings.doctorName,DatabaseController.getNurseName(doctorId.get(spinnerDoctor.getSelectedItemPosition())));
        AppSettings.putString(AppSettings.doctorName, DatabaseController.getNurseName(selectedDoctorId));
    }

    private void saveTreatment(HashMap<String, String> hashMap, String uuid) {

        ContentValues mContentValues = new ContentValues();

        mContentValues.put(TableTreatment.tableColumn.uuid.toString(), uuid);
        mContentValues.put(TableTreatment.tableColumn.treatmentName.toString(), hashMap.get("treatmentName"));
        mContentValues.put(TableTreatment.tableColumn.babyId.toString(), AppSettings.getString(AppSettings.babyId));
        mContentValues.put(TableTreatment.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        mContentValues.put(TableTreatment.tableColumn.comment.toString(), hashMap.get("comment"));
        mContentValues.put(TableTreatment.tableColumn.image.toString(), hashMap.get("notePicture"));
        mContentValues.put(TableTreatment.tableColumn.quantity.toString(), "");
        //  mContentValues.put(TableTreatment.tableColumn.doctorId.toString(), doctorId.get(spinnerDoctor.getSelectedItemPosition()));
        mContentValues.put(TableTreatment.tableColumn.doctorId.toString(), selectedDoctorId);
        mContentValues.put(TableTreatment.tableColumn.doctorName.toString(), tvSelectDoctor.getText().toString().trim());
        mContentValues.put(TableTreatment.tableColumn.type.toString(), "1");
        mContentValues.put(TableTreatment.tableColumn.status.toString(), hashMap.get("status"));
        mContentValues.put(TableTreatment.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());

        Log.v("ljkshkhqsqsq", hashMap.get("status"));

        DatabaseController.insertUpdateData(mContentValues, TableTreatment.tableName,
                TableTreatment.tableColumn.uuid.toString(), uuid);

/*
        for (int i = 0; i < AppConstants.treatmentList.size(); i++) {
            ContentValues mContentValues = new ContentValues();

            mContentValues.put(TableTreatment.tableColumn.uuid.toString(), AppConstants.treatmentList.get(i).get("uuid"));
            mContentValues.put(TableTreatment.tableColumn.treatmentName.toString(), AppConstants.treatmentList.get(i).get("treatmentName"));
            mContentValues.put(TableTreatment.tableColumn.babyId.toString(), AppSettings.getString(AppSettings.babyId));
            mContentValues.put(TableTreatment.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
            mContentValues.put(TableTreatment.tableColumn.comment.toString(), AppConstants.treatmentList.get(i).get("comment"));
            mContentValues.put(TableTreatment.tableColumn.image.toString(), AppConstants.treatmentList.get(i).get("notePicture"));
            mContentValues.put(TableTreatment.tableColumn.quantity.toString(), "");
            //  mContentValues.put(TableTreatment.tableColumn.doctorId.toString(), doctorId.get(spinnerDoctor.getSelectedItemPosition()));
            mContentValues.put(TableTreatment.tableColumn.doctorId.toString(), selectedDoctorId);
            mContentValues.put(TableTreatment.tableColumn.doctorName.toString(), tvSelectDoctor.getText().toString().trim());
            mContentValues.put(TableTreatment.tableColumn.type.toString(), "1");
            mContentValues.put(TableTreatment.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());

            DatabaseController.insertUpdateData(mContentValues, TableTreatment.tableName,
                    TableTreatment.tableColumn.uuid.toString(), AppConstants.treatmentList.get(i).get("uuid"));

            */
    }

    private void saveInvestigation(HashMap<String, String> hashlist, String uuid) {

        ContentValues mContentValues = new ContentValues();

        mContentValues.put(TableInvestigation.tableColumn.uuid.toString(), uuid);
        mContentValues.put(TableInvestigation.tableColumn.investigationType.toString(), hashlist.get("investigationType"));
        mContentValues.put(TableInvestigation.tableColumn.investigationName.toString(), hashlist.get("investigationName"));
        mContentValues.put(TableInvestigation.tableColumn.babyId.toString(), AppSettings.getString(AppSettings.babyId));
        mContentValues.put(TableInvestigation.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        mContentValues.put(TableInvestigation.tableColumn.doctorComment.toString(), hashlist.get("comment"));
        //  mContentValues.put(TableInvestigation.tableColumn.doctorId.toString(), doctorId.get(spinnerDoctor.getSelectedItemPosition()));
        mContentValues.put(TableInvestigation.tableColumn.doctorId.toString(), selectedDoctorId);
        mContentValues.put(TableInvestigation.tableColumn.doctorName.toString(), tvSelectDoctor.getText().toString().trim());
        mContentValues.put(TableInvestigation.tableColumn.isDataSynced.toString(), "0");
        mContentValues.put(TableInvestigation.tableColumn.resultImage.toString(), "");
        mContentValues.put(TableInvestigation.tableColumn.result.toString(), "");
        mContentValues.put(TableInvestigation.tableColumn.sampleComment.toString(), "");
        mContentValues.put(TableInvestigation.tableColumn.sampleImage.toString(), "");
        mContentValues.put(TableInvestigation.tableColumn.sampleTakenOn.toString(), "");
        mContentValues.put(TableInvestigation.tableColumn.nurseId.toString(), hashlist.get("nurseId"));
        //mContentValues.put(TableInvestigation.tableColumn.doctorComment.toString(), "");
        mContentValues.put(TableInvestigation.tableColumn.sampleTakenBy.toString(), "");
        mContentValues.put(TableInvestigation.tableColumn.nurseComment.toString(), "");
        mContentValues.put(TableInvestigation.tableColumn.invesDate.toString(), AppUtils.getCurrentDateNew());
        mContentValues.put(TableInvestigation.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
        mContentValues.put(TableInvestigation.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());
        mContentValues.put(TableInvestigation.tableColumn.status.toString(), hashlist.get("status"));

        DatabaseController.insertUpdateData(mContentValues, TableInvestigation.tableName,
                TableInvestigation.tableColumn.uuid.toString(), uuid);

        Log.v("wlkdjjwdwd", hashlist.get("status"));

/*
        for (int i = 0; i < AppConstants.investigationList.size(); i++) {
            ContentValues mContentValues = new ContentValues();

            mContentValues.put(TableInvestigation.tableColumn.uuid.toString(), AppConstants.investigationList.get(i).get("uuid"));
            mContentValues.put(TableInvestigation.tableColumn.investigationType.toString(), AppConstants.investigationList.get(i).get("investigationType"));
            mContentValues.put(TableInvestigation.tableColumn.investigationName.toString(), AppConstants.investigationList.get(i).get("investigationName"));
            mContentValues.put(TableInvestigation.tableColumn.babyId.toString(), AppSettings.getString(AppSettings.babyId));
            mContentValues.put(TableInvestigation.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
            mContentValues.put(TableInvestigation.tableColumn.doctorComment.toString(), AppConstants.investigationList.get(i).get("comment"));
            //  mContentValues.put(TableInvestigation.tableColumn.doctorId.toString(), doctorId.get(spinnerDoctor.getSelectedItemPosition()));
            mContentValues.put(TableInvestigation.tableColumn.doctorId.toString(), selectedDoctorId);
            mContentValues.put(TableInvestigation.tableColumn.doctorName.toString(), tvSelectDoctor.getText().toString().trim());
            mContentValues.put(TableInvestigation.tableColumn.isDataSynced.toString(), "0");
            mContentValues.put(TableInvestigation.tableColumn.resultImage.toString(), "");
            mContentValues.put(TableInvestigation.tableColumn.result.toString(), "");
            mContentValues.put(TableInvestigation.tableColumn.sampleComment.toString(), "");
            mContentValues.put(TableInvestigation.tableColumn.sampleImage.toString(), "");
            mContentValues.put(TableInvestigation.tableColumn.sampleTakenOn.toString(), "");
            //mContentValues.put(TableInvestigation.tableColumn.doctorComment.toString(), "");
            mContentValues.put(TableInvestigation.tableColumn.sampleTakenBy.toString(), "");
            mContentValues.put(TableInvestigation.tableColumn.nurseComment.toString(), "");
            mContentValues.put(TableInvestigation.tableColumn.invesDate.toString(), AppUtils.getCurrentDateNew());
            mContentValues.put(TableInvestigation.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
            mContentValues.put(TableInvestigation.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());
            mContentValues.put(TableInvestigation.tableColumn.status.toString(), "1");

            DatabaseController.insertUpdateData(mContentValues, TableInvestigation.tableName,
                    TableInvestigation.tableColumn.uuid.toString(), AppConstants.investigationList.get(i).get("uuid"));
        }
*/
    }

    private JSONObject createJson(HashMap<String, String> hashlist, String type, String uuid) {

        JSONObject jsonData = new JSONObject();

        JSONArray jsonArray = new JSONArray();
        JSONArray jsonArrayInves = new JSONArray();

        try {

            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("babyId", AppSettings.getString(AppSettings.babyId));
            //   jsonData.put("doctorId", doctorId.get(spinnerDoctor.getSelectedItemPosition()));
            jsonData.put("doctorId", selectedDoctorId);
            jsonData.put("doctorSign", "");
            jsonData.put("localId", uuid);
            jsonData.put("comment", "");
            jsonData.put("localDateTime", AppUtils.currentTimestampFormat());

            if (type.equals("1")){

                JSONObject jsonObject = new JSONObject();

                jsonObject.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                jsonObject.put("treatmentName", hashlist.get("treatmentName"));
                jsonObject.put("comment", hashlist.get("comment"));
                jsonObject.put("image", hashlist.get("notePicture"));
                jsonObject.put("localId", hashlist.get("uuid"));
                jsonObject.put("localDateTime", hashlist.get("addDate"));
                jsonObject.put("status", hashlist.get("status"));

                jsonArray.put(jsonObject);
            }
            else {

                JSONObject jsonObject = new JSONObject();

                jsonObject.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                jsonObject.put("investigationType", hashlist.get("investigationType"));
                jsonObject.put("investigationName", hashlist.get("investigationName"));
                jsonObject.put("comment", hashlist.get("comment"));
                jsonObject.put("localId", hashlist.get("uuid"));
                jsonObject.put("localDateTime", hashlist.get("addDate"));
                jsonObject.put("status", hashlist.get("status"));

                jsonArrayInves.put(jsonObject);

            }

          /*commented on 14/08/2020
           for (int i = 0; i < AppConstants.treatmentList.size(); i++) {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                jsonObject.put("treatmentName", AppConstants.treatmentList.get(i).get("treatmentName"));
                jsonObject.put("comment", AppConstants.treatmentList.get(i).get("comment"));
                jsonObject.put("image", AppConstants.treatmentList.get(i).get("notePicture"));
                jsonObject.put("localId", AppConstants.treatmentList.get(i).get("uuid"));
                jsonObject.put("localDateTime", AppConstants.treatmentList.get(i).get("addDate"));

                jsonArray.put(jsonObject);
            }


            for (int i = 0; i < AppConstants.investigationList.size(); i++) {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("loungeId", AppSettings.getString(AppSettings.loungeId));
                jsonObject.put("investigationType", AppConstants.investigationList.get(i).get("investigationType"));
                jsonObject.put("investigationName", AppConstants.investigationList.get(i).get("investigationName"));
                jsonObject.put("comment", AppConstants.investigationList.get(i).get("comment"));
                jsonObject.put("localId", AppConstants.investigationList.get(i).get("uuid"));
                jsonObject.put("localDateTime", AppConstants.investigationList.get(i).get("addDate"));

                jsonArrayInves.put(jsonObject);
            }*/

            jsonData.put("treatment", jsonArray);
            jsonData.put("investigation", jsonArrayInves);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonData;
    }


    //AlertTreatment
    private void AlertTreatment(String uuid, String treatment, String comment, int position) {
        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_treatment_add);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //EditText
        EditText etTreatment = dialog.findViewById(R.id.etTreatment);
        EditText etComment = dialog.findViewById(R.id.etComment);

        //RelativeLayout
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);

        etTreatment.setText(treatment);
        etComment.setText(comment);

        //uuid  = UUID.randomUUID().toString();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        rlOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etTreatment.getText().toString().trim().isEmpty()) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorTreatment));
                } else {
                    HashMap<String, String> hashlist = new HashMap<>();

                    hashlist.put("uuid", uuid);
                    hashlist.put("babyId", AppSettings.getString(AppSettings.babyId));
                    hashlist.put("treatmentName", etTreatment.getText().toString().trim());
                    hashlist.put("comment", etComment.getText().toString().trim());
                    hashlist.put("notePicture", "");
                    hashlist.put("doctorName", tvSelectDoctor.getText().toString().trim());
                    hashlist.put("doctorId",selectedDoctorId);
                    hashlist.put("nurseId","");
                    hashlist.put("addDate", String.valueOf(AppUtils.currentTimestampFormat()));
                    hashlist.put("status","1");

                    if (treatment.isEmpty()) {

                        AppConstants.treatmentList.add(hashlist);

                    } else {
                        AppConstants.treatmentList.set(position, hashlist);
                    }

                    dialog.dismiss();
                    setTreatment();

                    saveDoctorRound(hashlist,"1",uuid);
                    saveTreatment(hashlist,uuid);

                    if (AppUtils.isNetworkAvailable(mActivity)) {
                        DoctorRoundActivity.check = 1;
                        SyncBabyRecord.getBabyDataToUpdate(mActivity, AppSettings.getString(AppSettings.babyId));
                    } else {
                        AppUtils.showToastSort(mActivity, getString(R.string.dataSaved));
                        DoctorRoundActivity.check = 1;
                    }
                }

            }
        });

        rlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

    }

    private void AlertInvestigation(String uuid, String invesType, String investigation, String comment, int position) {
        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_investigation_add);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //EditText
        EditText etComment = dialog.findViewById(R.id.etComment);

        //Spinner
        Spinner spinnerInvestigationType, spinnerInvestigation;

        //Spinner
        spinnerInvestigation = dialog.findViewById(R.id.spinner);
        spinnerInvestigationType = dialog.findViewById(R.id.spinnerInvestigationType);

        investigationType.clear();
        investigationType.add(getString(R.string.selectInvestigationType));
        investigationType.add(getString(R.string.bloodnvestigations));
        investigationType.add(getString(R.string.serum));
        investigationType.add(getString(R.string.imaging));
        investigationType.add(getString(R.string.miscellaneousTests));

        investigationTypeValue.clear();
        investigationTypeValue.add(getString(R.string.selectInvestigationType));
        investigationTypeValue.add(getString(R.string.bloodnvestigationsValue));
        investigationTypeValue.add(getString(R.string.serumValue));
        investigationTypeValue.add(getString(R.string.imagingValue));
        investigationTypeValue.add(getString(R.string.miscellaneousTestsValue));

        //RelativeLayout
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);

        spinnerInvestigationType.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, investigationType));
        spinnerInvestigationType.setSelection(0);

        spinnerInvestigation.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, investigationName));
        spinnerInvestigation.setSelection(0);

        etComment.setText(comment);

        int pos = 0;
        if (!invesType.isEmpty()) {
            for (int i = 0; i < investigationTypeValue.size(); i++) {
                if (invesType.equalsIgnoreCase(investigationTypeValue.get(i))) {
                    pos = i;
                    break;
                }
            }
        }

        spinnerInvestigationType.setSelection(pos);

        //Spinner for Type
        spinnerInvestigationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if (position == 0) {
                    investigationName.clear();
                    investigationNameValue.clear();

                    spinnerInvestigation.setSelection(0);
                } else if (position == 1) {
                    investigationName.clear();
                    investigationName.add(getString(R.string.selectInvestigation));
                    investigationName.add(getString(R.string.hbValue));
                    investigationName.add(getString(R.string.pcv));
                    investigationName.add(getString(R.string.wbc));
                    investigationName.add(getString(R.string.diffWbc));
                    investigationName.add(getString(R.string.bandForm));
                    investigationName.add(getString(R.string.plateletCount));
                    investigationName.add(getString(R.string.periSmear));
                    investigationName.add(getString(R.string.pt));
                    investigationName.add(getString(R.string.crp));

                    investigationNameValue.clear();
                    investigationNameValue.add(getString(R.string.selectInvestigation));
                    investigationNameValue.add(getString(R.string.hbValue));
                    investigationNameValue.add(getString(R.string.pcvValue));
                    investigationNameValue.add(getString(R.string.wbcValue));
                    investigationNameValue.add(getString(R.string.diffWbcValue));
                    investigationNameValue.add(getString(R.string.bandFormValue));
                    investigationNameValue.add(getString(R.string.plateletCountValue));
                    investigationNameValue.add(getString(R.string.periSmearValue));
                    investigationNameValue.add(getString(R.string.ptValue));
                    investigationNameValue.add(getString(R.string.crpValue));
                } else if (position == 2) {
                    investigationName.clear();
                    investigationName.add(getString(R.string.selectInvestigation));
                    investigationName.add(getString(R.string.rbg));
                    investigationName.add(getString(R.string.bun));
                    investigationName.add(getString(R.string.serumCreatioine));
                    investigationName.add(getString(R.string.serumCalcium));
                    investigationName.add(getString(R.string.serumSodium));
                    investigationName.add(getString(R.string.serumPotassium));
                    investigationName.add(getString(R.string.serumBilirubin));
                    investigationName.add(getString(R.string.sgpt));
                    investigationName.add(getString(R.string.totalProtein));

                    investigationNameValue.clear();
                    investigationNameValue.add(getString(R.string.selectInvestigation));
                    investigationNameValue.add(getString(R.string.rbgValue));
                    investigationNameValue.add(getString(R.string.bunValue));
                    investigationNameValue.add(getString(R.string.serumCreatioineValue));
                    investigationNameValue.add(getString(R.string.serumCalciumValue));
                    investigationNameValue.add(getString(R.string.serumSodiumValue));
                    investigationNameValue.add(getString(R.string.serumPotassiumValue));
                    investigationNameValue.add(getString(R.string.serumBilirubinValue));
                    investigationNameValue.add(getString(R.string.sgptValue));
                    investigationNameValue.add(getString(R.string.totalProteinValue));
                } else if (position == 3) {
                    investigationName.clear();
                    investigationName.add(getString(R.string.selectInvestigation));
                    investigationName.add(getString(R.string.xRay));
                    investigationName.add(getString(R.string.usg));
                    investigationName.add(getString(R.string.ctMRI));

                    investigationNameValue.clear();
                    investigationNameValue.add(getString(R.string.selectInvestigation));
                    investigationNameValue.add(getString(R.string.xRayValue));
                    investigationNameValue.add(getString(R.string.usgValue));
                    investigationNameValue.add(getString(R.string.ctMRIValue));
                } else if (position == 4) {
                    investigationName.clear();
                    investigationName.add(getString(R.string.selectInvestigation));
                    investigationName.add(getString(R.string.urineRnM));
                    investigationName.add(getString(R.string.stoolForOccultBlood));
                    investigationName.add(getString(R.string.csf));
                    investigationName.add(getString(R.string.bloodCollection));
                    investigationName.add(getString(R.string.other));

                    investigationNameValue.clear();
                    investigationNameValue.add(getString(R.string.selectInvestigation));
                    investigationNameValue.add(getString(R.string.urineRnMValue));
                    investigationNameValue.add(getString(R.string.stoolForOccultBloodValue));
                    investigationNameValue.add(getString(R.string.csfValue));
                    investigationNameValue.add(getString(R.string.bloodCollectionValue));
                    investigationNameValue.add(getString(R.string.otherValue));
                }

                spinnerInvestigation.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, investigationName));
                spinnerInvestigation.setSelection(0);

                int pos = 0;
                if (!investigation.isEmpty()) {
                    for (int i = 0; i < investigationNameValue.size(); i++) {
                        if (investigation.equalsIgnoreCase(investigationNameValue.get(i))) {
                            pos = i;
                            break;
                        }
                    }
                }

                spinnerInvestigation.setSelection(pos);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        rlOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (spinnerInvestigationType.getSelectedItemPosition() == 0) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectInvesType));
                } else if (spinnerInvestigation.getSelectedItemPosition() == 0) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectInvesMethod));
                } else {
                    HashMap<String, String> hashlist = new HashMap();

                    hashlist.put("uuid", uuid);
                    hashlist.put("babyId", AppSettings.getString(AppSettings.babyId));
                    hashlist.put("investigationType", investigationTypeValue.get(spinnerInvestigationType.getSelectedItemPosition()));
                    hashlist.put("investigationName", investigationNameValue.get(spinnerInvestigation.getSelectedItemPosition()));
                    hashlist.put("comment", etComment.getText().toString().trim());
                    hashlist.put("doctorName", tvSelectDoctor.getText().toString().trim());
                    hashlist.put("doctorId",selectedDoctorId);
                    hashlist.put("nurseId","");
                    hashlist.put("status","1");
                    hashlist.put("addDate", String.valueOf(AppUtils.currentTimestampFormat()));

                    if (invesType.isEmpty()) {
                        AppConstants.investigationList.add(hashlist);
                    } else {
                        AppConstants.investigationList.set(position, hashlist);
                    }

                    dialog.dismiss();
                    setInvestigation();

                    saveDoctorRound(hashlist,"2", uuid);
                    saveInvestigation(hashlist,uuid);

                    if (AppUtils.isNetworkAvailable(mActivity)) {
                        DoctorRoundActivity.check = 1;
                        SyncBabyRecord.getBabyDataToUpdate(mActivity, AppSettings.getString(AppSettings.babyId));

                    } else {
                        AppUtils.showToastSort(mActivity, getString(R.string.dataSaved));
                        DoctorRoundActivity.check = 1;

                    }
                }

            }
        });

        rlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

    }

    private class Adapter extends RecyclerView.Adapter<Holder> {
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        Dialog dialog;

        public Adapter(ArrayList<HashMap<String, String>> arrayList, Dialog dialog1) {
            data = arrayList;
            dialog = dialog1;
        }

        @NonNull
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_doctor, parent, false));
        }

        @SuppressLint("SetTextI18n")
        public void onBindViewHolder(Holder holder, final int position) {

            if (data.get(position).get("status").equals("0")) {

                holder.llMain.setBackgroundResource(R.drawable.rectangle_grey);
                holder.tvName.setTextColor(getResources().getColor(R.color.blackNew));
            } else {

                holder.llMain.setBackgroundResource(R.drawable.rectangle_teal_selected);
                holder.tvName.setTextColor(getResources().getColor(R.color.white));

            }

            holder.tvName.setText(data.get(position).get("name"));

            holder.llMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    selectedDoctorId = doctorId.get(position);

                    tvSelectDoctor.setText(data.get(position).get("name"));

//                    dialog.dismiss();

                    for (int i = 0; i < data.size(); i++) {

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("name", data.get(i).get("name"));
                        hashMap.put("status", "0");

                        data.set(i, hashMap);
                    }

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("name", data.get(position).get("name"));
                    hashMap.put("status", "1");

                    data.set(position, hashMap);

                    notifyDataSetChanged();

                }
            });

        }

        public int getItemCount() {
            return data.size();
        }
    }

    private void ssetTreatmentAndInvestigationList() {

        AppConstants.treatmentList.addAll(DatabaseController.getTreatmentList(AppSettings.getString(AppSettings.babyId),
                AppSettings.getString(AppSettings.loungeId),selectedDoctorId));

         AppConstants.investigationList.addAll(DatabaseController.getInvestigationList(AppSettings.getString(AppSettings.babyId),
                AppSettings.getString(AppSettings.loungeId),selectedDoctorId));

        Log.v("sdasdad", AppConstants.treatmentList.toString());
        Log.v("dadassdas", AppConstants.investigationList.toString());

        setTreatment();
      //  setInvestigation();

    }

    private class Holder extends RecyclerView.ViewHolder {

        //TextView
        TextView tvName;

        //LinearLayout
        LinearLayout llMain;

        public Holder(View itemView) {
            super(itemView);

            //TextView
            tvName = itemView.findViewById(R.id.tvName);

            //LinearLayout
            llMain = itemView.findViewById(R.id.llMain);

        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
    public static String convertSecondsToHMmSs(long millis) {
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = millis / (1000 * 60 * 60);

        Log.v("khslkqs", String.valueOf(millis));

        StringBuilder b = new StringBuilder();
        b.append(hours == 0 ? "00" : hours < 10 ? String.valueOf("0" + hours) :
                String.valueOf(hours));
        b.append(":");
        b.append(minutes == 0 ? "00" : minutes < 10 ? String.valueOf("0" + minutes) :
                String.valueOf(minutes));
        b.append(":");
        b.append(seconds == 0 ? "00" : seconds < 10 ? String.valueOf("0" + seconds) :
                String.valueOf(seconds));
        return b.toString();
    }
}
