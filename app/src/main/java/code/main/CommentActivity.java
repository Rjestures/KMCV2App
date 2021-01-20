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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kmcapp.android.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import code.algo.SyncAllRecord;
import code.algo.SyncBabyRecord;
import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.common.LocaleHelper;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableComments;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseActivity;

public class CommentActivity extends BaseActivity implements View.OnClickListener {

    //RecyclerView for doctorComment listing
    private RecyclerView recyclerView;

    //LinearLayout
    private LinearLayout llSync, llHelp, llLogout, llLanguage;

    //RelativeLayout
    private RelativeLayout rlHelp, rlStuck, rlOwn, rlBottom;

    //Adapter
    private Adapter adapter;

    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> arrayListDocName = new ArrayList<HashMap<String, String>>();

    private String uuid = UUID.randomUUID().toString();

    //String
    private String selectedDoctorId = "";

    //EditText for entering doctorComment
    private EditText etComment;

    //ImageView for sending doctorComment
    private ImageView ivSend;

    private ArrayList<String> doctorId = new ArrayList<String>();
    private ArrayList<String> doctorName = new ArrayList<String>();

    //Baby and Mother Header

    //RelativeLayout
    private RelativeLayout rlMother, rlBaby;

    //TextView
    private TextView tvHeading, tvName, tvDob, tvDoa, tvBWeight, tvCWeight, tvMotherName, tvLastAssessment, tvSelectDoctor;

    //ImageView
    private ImageView ivPic, ivStatus, ivPicMother;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        initialize();
    }

    private void initialize() {

        //recyclerView
        recyclerView = findViewById(R.id.recyclerView);

        //GridLayoutManager
        GridLayoutManager mCommentGridLayoutManager = new GridLayoutManager(mActivity, 1);
        recyclerView.setLayoutManager(mCommentGridLayoutManager);

        //TextView
        tvHeading = findViewById(R.id.tvHeading);
        tvName = findViewById(R.id.tvName);
        tvDob = findViewById(R.id.tvDob);
        tvDoa = findViewById(R.id.tvDoa);
        tvBWeight = findViewById(R.id.tvBWeight);
        tvCWeight = findViewById(R.id.tvCWeight);
        tvMotherName = findViewById(R.id.tvMotherName);
        tvLastAssessment = findViewById(R.id.tvLastAssessment);
        tvSelectDoctor = findViewById(R.id.tvSelectDoctor);

        //RelativeLayout
        rlMother = findViewById(R.id.rlMother);
        rlBaby = findViewById(R.id.rlBaby);
        rlHelp = findViewById(R.id.rlHelp);
        rlStuck = findViewById(R.id.rlStuck);
        rlOwn = findViewById(R.id.rlOwn);
        rlBottom = findViewById(R.id.rlBottom);

        //LinearLayout
        llSync = findViewById(R.id.llSync);
        llHelp = findViewById(R.id.llHelp);
        llLogout = findViewById(R.id.llLogout);
        llLanguage = findViewById(R.id.llLanguage);

        //ImageView
        ivPic = findViewById(R.id.ivPic);
        ivStatus = findViewById(R.id.ivStatus);
        ivPicMother = findViewById(R.id.ivPicMother);

        //Comment Section
        etComment = findViewById(R.id.etComment);

        //ImageView
        ivSend = findViewById(R.id.ivSend);

        doctorId.clear();
        //doctorId.add(getString(R.string.selectDoctor));
        doctorId.addAll(DatabaseController.getDocIdData());

        doctorName.clear();
        //doctorName.add(getString(R.string.selectDoctor));
        doctorName.addAll(DatabaseController.getDocNameData());

        arrayListDocName.clear();

        for (int i = 0; i < doctorName.size(); i++) {

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("name", doctorName.get(i));
            hashMap.put("status", "0");

            arrayListDocName.add(hashMap);

        }

        //setOnClickListener
        ivSend.setOnClickListener(this);

        //setOnClickListener
        llSync.setOnClickListener(this);
        llHelp.setOnClickListener(this);
        llLogout.setOnClickListener(this);
        llLanguage.setOnClickListener(this);
        rlHelp.setOnClickListener(this);
        rlStuck.setOnClickListener(this);
        rlOwn.setOnClickListener(this);

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
        recyclerView.setAdapter(new AdapterDocList(arrayListDocName, dialog));

        rlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });

        rlOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedDoctorId.isEmpty()) {

                    AppUtils.showToastSort(mActivity, getString(R.string.selectDoctor));

                } else {

                    setHeader();

                    dialog.dismiss();
                }


            }
        });

    }


    private void setHeader() {
        if (AppSettings.getString(AppSettings.from).equalsIgnoreCase("1")) {
            if (AppSettings.getString(AppSettings.userType).equalsIgnoreCase("1")) {
                rlBottom.setVisibility(View.GONE);

                if (AppUtils.isNetworkAvailable(mActivity)) {
                    getAllCommentsApi();
                } else {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                }
            } else {
                getCommentList();
            }

            rlMother.setVisibility(View.VISIBLE);
            rlBaby.setVisibility(View.GONE);
            setMotherValues();
        } else {
            if (AppSettings.getString(AppSettings.userType).equalsIgnoreCase("1")) {
                rlBottom.setVisibility(View.GONE);

                if (AppUtils.isNetworkAvailable(mActivity)) {
                    getAllCommentsApi();
                } else {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                }
            } else {
                getCommentList();

            }

            setInfantsValue();

            rlMother.setVisibility(View.GONE);
            rlBaby.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setMotherValues() {

        tvHeading.setText(DatabaseController.getLoungeNameData(AppSettings.getString(AppSettings.loungeId)));

        tvLastAssessment.setText(getString(R.string.lastAssessment) + ": " + getString(R.string.notApplicableShortValue));

        if (!AppSettings.getString(AppSettings.lastAssessmentDate).isEmpty()) {
            tvLastAssessment.setText(getString(R.string.lastAssessment) + ": " + AppUtils.convertDateTimeTo12HoursFormat(AppSettings.getString(AppSettings.lastAssessmentDate)));
        }

        tvMotherName.setText(AppSettings.getString(AppSettings.motherName));

        if (!AppSettings.getString(AppSettings.motherPhoto).isEmpty()) {

            if (AppSettings.getString(AppSettings.motherPhoto).contains("http")) {

                Picasso.get().load(AppSettings.getString(AppSettings.motherPhoto)).into(ivPicMother);

            } else {

                try {

                    byte[] decodedString = Base64.decode(AppSettings.getString(AppSettings.motherPhoto), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    ivPicMother.setImageBitmap(decodedByte);

                    //Picasso.get().load(arrayList.get(0).get("babyPhoto")).into(ivPic);
                } catch (Exception e) {
                    //e.printStackTrace();
                    ivPicMother.setImageResource(R.mipmap.mother);
                }
            }
        }else {
            ivPicMother.setImageResource(R.mipmap.mother);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        //   setHeader();

        //getCommentList();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void setInfantsValue() {

        if (AppConstants.hashMap.get("motherName") == null
                || AppConstants.hashMap.get("motherName").isEmpty()) {
            tvName.setText(getString(R.string.babyOf) + " " + getString(R.string.unknown));
        } else {
            tvName.setText(getString(R.string.babyOf) + " " + AppConstants.hashMap.get("motherName"));
        }

        if (!AppConstants.hashMap.get("babyPhoto").isEmpty()) {
            if (AppConstants.hashMap.get("babyPhoto").contains("http")) {
                Picasso.get().load(AppConstants.hashMap.get("babyPhoto")).into(ivPic);
            } else {
                try {

                    byte[] decodedString = Base64.decode(AppConstants.hashMap.get("babyPhoto"), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    ivPic.setImageBitmap(decodedByte);

                } catch (Exception e) {
                    ivPic.setImageResource(R.mipmap.baby);
                }
            }
        }

        String dateOfAdmi = AppConstants.hashMap.get("admissionDate");

        String[] parts = dateOfAdmi.split(" ");

        Log.d("dateOfAdmi", parts[0]);

        tvDob.setText(getString(R.string.dateOfBirth) + " " + AppConstants.hashMap.get("deliveryDate"));
        tvDoa.setText(getString(R.string.dateOfAdmission) + " " + parts[0]);
        tvBWeight.setText(getString(R.string.birthWeight)
                + " " + AppConstants.hashMap.get("birthWeight")
                + " " + getString(R.string.grams));

        tvCWeight.setText(getString(R.string.currentWeight)
                + " " + AppConstants.hashMap.get("currentWeight")
                + " " + getString(R.string.grams));


        int checkPulse = 0, checkSpo2 = 0, checkTemp = 0, checkResp = 0;

        int pulse = 0;
        try {
            pulse = Integer.parseInt(AppConstants.hashMap.get("pulseRate"));
            if (pulse < 75 || pulse > 200) {
                checkPulse = 0;
            } else {
                checkPulse = 1;
            }
        } catch (NumberFormatException e) {
            //e.printStackTrace();
            checkPulse = 1;
        }


        int spo2 = 0;
        try {
            spo2 = Integer.parseInt(AppConstants.hashMap.get("spO2"));

            if (spo2 < 95) {
                checkSpo2 = 0;
            } else {
                checkSpo2 = 1;
            }

        } catch (NumberFormatException e) {
            //e.printStackTrace();
            checkSpo2 = 0;
        }

        float temp = 0;
        try {
            temp = Float.parseFloat(AppConstants.hashMap.get("temperature"));

            if (temp > 99.5 || temp < 95.9) {
                checkTemp = 0;
            } else {
                checkTemp = 1;
            }

        } catch (NumberFormatException e) {
            //e.printStackTrace();
            checkTemp = 0;
        }

        float res = 0;
        try {
            res = Float.parseFloat(AppConstants.hashMap.get("respiratoryRate"));

            if (res < 30 || res > 60) {
                checkResp = 0;
            } else {
                checkResp = 1;
            }
        } catch (NumberFormatException e) {
            //e.printStackTrace();
            checkResp = 0;
        }

        if (AppConstants.hashMap.get("isPulseOximatoryDeviceAvailable").equalsIgnoreCase(getString(R.string.yesValue))) {
            if (checkPulse == 0 || checkSpo2 == 0 || checkTemp == 0 || checkResp == 0) {
                ivStatus.setImageResource(R.drawable.ic_sad_smily);
            } else {
                ivStatus.setImageResource(R.drawable.ic_happy_smily);
            }
        } else if (checkTemp == 0 || checkResp == 0) {
            ivStatus.setImageResource(R.drawable.ic_sad_smily);
        } else {
            ivStatus.setImageResource(R.drawable.ic_happy_smily);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.llLanguage:

                AppUtils.AlertLanguageConfirm(mActivity, getString(R.string.languageAlert));

                break;

            case R.id.ivSend:

                if (selectedDoctorId.isEmpty()) {
                    AppUtils.showToastSort(mActivity, getString(R.string.selectDoctor));
                } else if (etComment.getText().toString().trim().isEmpty()) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorComments));
                } else {
                    uuid = UUID.randomUUID().toString();
                    saveComment(etComment.getText().toString().trim(), selectedDoctorId, "1", uuid);
                    etComment.setText("");
                }
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
        }
    }

    private void getCommentList() {

        if (AppSettings.getString(AppSettings.from).equalsIgnoreCase("1")) {
            arrayList.clear();
            arrayList.addAll(DatabaseController.getCommentData(AppSettings.getString(AppSettings.motherId), "1"));
        } else if (AppSettings.getString(AppSettings.from).equalsIgnoreCase("2")) {
            arrayList.clear();
            arrayList.addAll(DatabaseController.getCommentData(AppSettings.getString(AppSettings.babyId), "2"));
        }

        Log.v("lsdkhjlwkjs", String.valueOf(arrayList));


       /* if (arrayList.size() > 0) {
            adapter = new Adapter(arrayList);
            recyclerView.setAdapter(adapter);
        }*/
        adapter = new Adapter(arrayList);
        recyclerView.setAdapter(adapter);
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.CommentHolder> {
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        public Adapter(ArrayList<HashMap<String, String>> favList) {
            data = favList;
        }

        public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CommentHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_comment, parent, false));
        }

        @SuppressLint("SetTextI18n")
        public void onBindViewHolder(CommentHolder holder, final int position) {

            holder.tvName.setText(data.get(position).get("doctorName"));
            holder.tvTime.setText(AppUtils.changeDateFormat(data.get(position).get("addDate")));
            holder.tvComment.setText(data.get(position).get("doctorComment"));

            if (data.get(position).get("doctorId").equals(selectedDoctorId) && AppUtils.getSecondsDifference(Long.parseLong(AppUtils.changeDateToTimestamp3
                    (data.get(position).get("addDate"))), AppUtils.getCurrentTimestamp()) < 900) {

                holder.rlRight.setVisibility(View.VISIBLE);
            } else {
                holder.rlRight.setVisibility(View.GONE);
            }

            holder.ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showEditCommentDialog(data.get(position), position);

                }
            });

            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showDeleteAlert(data.get(position), position);

                }
            });


            if (!data.get(position).get("doctorImage").isEmpty()) {
                Picasso.get().load(data.get(position).get("doctorImage")).into(holder.ivPic);
            }
        }

        public int getItemCount() {
            return data.size();
        }

        private class CommentHolder extends RecyclerView.ViewHolder {

            //TextView
            TextView tvName, tvComment, tvTime;

            //RelativeLayout
            private RelativeLayout rlRight;

            //ImageView
            ImageView ivPic, ivEdit, ivDelete;

            public CommentHolder(View itemView) {
                super(itemView);

                //TextView
                tvName = itemView.findViewById(R.id.tvName);
                tvComment = itemView.findViewById(R.id.tvComment);
                tvTime = itemView.findViewById(R.id.tvTime);

                //ImageView
                ivPic = itemView.findViewById(R.id.ivPic);
                ivEdit = itemView.findViewById(R.id.ivEdit);
                ivDelete = itemView.findViewById(R.id.ivDelete);

                rlRight = itemView.findViewById(R.id.rlRight);
            }
        }

    }

    private void showEditCommentDialog(HashMap<String, String> hashMap, int position) {

        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_edit_comment);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();

        EditText etComment = dialog.findViewById(R.id.etComment);

        TextView tvDoctorName = dialog.findViewById(R.id.tvDoctorName);

        etComment.setText(hashMap.get("doctorComment"));
        tvDoctorName.setText(hashMap.get("doctorName"));

        RelativeLayout rlCancel, rlOk;

        rlCancel = dialog.findViewById(R.id.rlCancel);
        rlOk = dialog.findViewById(R.id.rlOk);

        rlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        rlOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveComment(etComment.getText().toString().trim(), selectedDoctorId, "1", hashMap.get("uuid"));


                dialog.dismiss();

            }
        });

    }

    private void showDeleteAlert(HashMap<String, String> hashMap, int position) {

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

        tvMessage.setText(getString(R.string.areYouSureWantDeleteComment));

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

                dialog.dismiss();

                saveComment(hashMap.get("doctorComment"), selectedDoctorId, "2", hashMap.get("uuid"));

            }
        });

    }

    public void saveComment(String comment, String doctorId, String status, String uuid) {

        AppUtils.hideSoftKeyboard(mActivity);

        ContentValues mContentValues = new ContentValues();

        mContentValues.put(TableComments.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        mContentValues.put(TableComments.tableColumn.serverId.toString(), "");
        mContentValues.put(TableComments.tableColumn.uuid.toString(), uuid);
        mContentValues.put(TableComments.tableColumn.doctorId.toString(), doctorId);
        mContentValues.put(TableComments.tableColumn.comment.toString(), comment);
        mContentValues.put(TableComments.tableColumn.isDataSynced.toString(), "0");
        mContentValues.put(TableComments.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
        mContentValues.put(TableComments.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());
        mContentValues.put(TableComments.tableColumn.syncTime.toString(), "");
        mContentValues.put(TableComments.tableColumn.status.toString(), status);

        if (AppSettings.getString(AppSettings.from).equalsIgnoreCase("1")) {
            mContentValues.put(TableComments.tableColumn.motherOrBabyId.toString(), AppSettings.getString(AppSettings.motherId));
            mContentValues.put(TableComments.tableColumn.type.toString(), "1");
        } else if (AppSettings.getString(AppSettings.from).equalsIgnoreCase("2")) {
            mContentValues.put(TableComments.tableColumn.motherOrBabyId.toString(), AppSettings.getString(AppSettings.babyId));
            mContentValues.put(TableComments.tableColumn.type.toString(), "2");
        }

        DatabaseController.insertUpdateData(mContentValues, TableComments.tableName,
                TableComments.tableColumn.uuid.toString(),uuid);

        getCommentList();

        if (AppUtils.isNetworkAvailable(mActivity)) {
            SyncBabyRecord.getAllComments(mActivity, AppSettings.getString(AppSettings.babyId));
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

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View row = inflater.inflate(R.layout.inflate_spinner_new, parent, false);
            TextView label = row.findViewById(R.id.tvName);

            label.setText(data.get(position));

            return row;
        }
    }


    //Get All Comments
    private void getAllCommentsApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {

            jsonData.put("coachId", AppSettings.getString(AppSettings.coachId));
            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("type", AppSettings.getString(AppSettings.from));

            if (AppSettings.getString(AppSettings.from).equalsIgnoreCase("1")) {
                jsonData.put("id", AppSettings.getString(AppSettings.motherId));
            } else if (AppSettings.getString(AppSettings.from).equalsIgnoreCase("2")) {
                jsonData.put("id", AppSettings.getString(AppSettings.babyId));
            }

            json.put(AppConstants.projectName, jsonData);

            Log.v("getAllCommentsApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.getComment, json, true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        arrayList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject arrayJSONObject = jsonArray.getJSONObject(i);

                            HashMap<String, String> hashlist = new HashMap();

                            hashlist.put("doctorName", arrayJSONObject.getString("doctorName"));
                            hashlist.put("doctorImage", arrayJSONObject.getString("doctorImage"));
                            hashlist.put("doctorComment", arrayJSONObject.getString("comment"));
                            hashlist.put("addDate", arrayJSONObject.getString("addDate"));

                            arrayList.add(hashlist);

                        }

                        if (arrayList.size() > 0) {
                            adapter = new Adapter(arrayList);
                            recyclerView.setAdapter(adapter);
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

    private class AdapterDocList extends RecyclerView.Adapter<AdapterDocList.MyViewHolder> {

        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        Dialog dialog;

        public AdapterDocList(ArrayList<HashMap<String, String>> arrayList, Dialog dialog1) {

            data = arrayList;
            dialog = dialog1;
        }

        @NonNull
        @Override
        public AdapterDocList.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_doctor, viewGroup, false);
            return new AdapterDocList.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterDocList.MyViewHolder holder, final int position) {

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

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            //TextView
            TextView tvName;

            //LinearLayout
            LinearLayout llMain;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                //TextView
                tvName = itemView.findViewById(R.id.tvName);

                //LinearLayout
                llMain = itemView.findViewById(R.id.llMain);

            }
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

}
