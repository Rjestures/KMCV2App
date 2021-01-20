package code.infantsFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;

import code.algo.SyncBabyRecord;
import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableBabyRegistration;
import code.database.TableTreatment;
import code.database.TableVaccination;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;


public class MedVaccFragment extends BaseFragment implements View.OnClickListener {

    //RecyclerView
    private RecyclerView rvMedicines, rvVaccines;

    //GridLayoutManager
    private GridLayoutManager mGridLayoutManager, mVaccGridLayoutManager;

    //MedicineAdapter
    private MedicineAdapter medicineAdapter;

    //Adapter
    private Adapter adapter;

    //RelativeLayout
    private RelativeLayout rlMenu, rlCircle, rlExpandMenu;

    private ArrayList<String> nurseId = new ArrayList<String>();
    private ArrayList<String> nurseName = new ArrayList<String>();

    private ArrayList<String> unit = new ArrayList<String>();
    private ArrayList<String> unitName = new ArrayList<String>();

    private ArrayList<String> treatmentName = new ArrayList<String>();
    private ArrayList<String> treatmentUuid = new ArrayList<String>();
    private ArrayList<String> vaccinationName = new ArrayList<String>();
    private ArrayList<String> vaccinationNameValue = new ArrayList<String>();

    //LinearLayout
    private LinearLayout llMedicineAdd, llVaccinationAdd;

    //ArrayList
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> arrayListVacc = new ArrayList<HashMap<String, String>>();

    //ImageView
    private ImageView ivPlus;

    String uuid = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_med_vacc, container, false);

        initialize(view);

        setListeners();

        return view;
    }

    private void initialize(View v) {

        //ImageView
        ivPlus = v.findViewById(R.id.ivPlus);

        //RecyclerView
        rvMedicines = v.findViewById(R.id.rvMedicines);
        rvVaccines = v.findViewById(R.id.rvVaccines);

        //RelativeLayout
        rlMenu = v.findViewById(R.id.rlMenu);
        rlExpandMenu = v.findViewById(R.id.rlExpandMenu);
        rlCircle = v.findViewById(R.id.rlCircle);

        //LinearLayout
        llMedicineAdd = v.findViewById(R.id.llMedicineAdd);
        llVaccinationAdd = v.findViewById(R.id.llVaccinationAdd);

        mGridLayoutManager = new GridLayoutManager(mActivity, 1);
        rvMedicines.setLayoutManager(mGridLayoutManager);

        mVaccGridLayoutManager = new GridLayoutManager(mActivity, 1);
        rvVaccines.setLayoutManager(mVaccGridLayoutManager);

    }

    private void setListeners() {

        ivPlus.setOnClickListener(this);
        rlMenu.setOnClickListener(this);
        rlExpandMenu.setOnClickListener(this);
        rlCircle.setOnClickListener(this);
        llMedicineAdd.setOnClickListener(this);
        llVaccinationAdd.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (AppSettings.getString(AppSettings.userType).equalsIgnoreCase("0")) {
            checkRecord();
        } else {
            rlCircle.setVisibility(View.GONE);

            if (AppUtils.isNetworkAvailable(mActivity)) {
                getBabyDataApi();
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        }

    }

    private void checkRecord() {
        String count = String.valueOf(DatabaseController.isDataExit(TableBabyRegistration.tableName));

        Log.d("count.isDataExit", count);

        arrayList.clear();
        arrayList.addAll(DatabaseController.getMedicineData(AppSettings.getString(AppSettings.babyId)));

        arrayListVacc.clear();
        arrayListVacc.addAll(DatabaseController.getVaccinationData(AppSettings.getString(AppSettings.babyId)));

        medicineAdapter = new MedicineAdapter(arrayList);
        rvMedicines.setAdapter(medicineAdapter);

        adapter = new Adapter(arrayListVacc);
        rvVaccines.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ivPlus:

            case R.id.rlCircle:

                if (rlExpandMenu.getVisibility() == View.GONE) {
                    Animation aniRotateClk = AnimationUtils.loadAnimation(getActivity(), R.anim.rorate45);
                    ivPlus.startAnimation(aniRotateClk);
                    AppUtils.expand(rlExpandMenu);
                } else {
                    Animation aniRotateClk = AnimationUtils.loadAnimation(getActivity(), R.anim.rorate0);
                    ivPlus.startAnimation(aniRotateClk);
                    AppUtils.collapse(rlExpandMenu);
                }

                break;

            case R.id.llMedicineAdd:

                Animation aniRotateClk = AnimationUtils.loadAnimation(getActivity(), R.anim.rorate0);
                ivPlus.startAnimation(aniRotateClk);
                AppUtils.collapse(rlExpandMenu);
                AlertMedicines();

                break;

            case R.id.llVaccinationAdd:

                aniRotateClk = AnimationUtils.loadAnimation(getActivity(), R.anim.rorate0);
                ivPlus.startAnimation(aniRotateClk);
                AppUtils.collapse(rlExpandMenu);
                AlertVaccines();

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
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_vaccines, viewGroup, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

            holder.tvName.setText(data.get(position).get("vaccinationName"));
            holder.tvDate.setText(data.get(position).get("date"));

            holder.llMain.setOnClickListener(new View.OnClickListener() {
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
            TextView tvDate, tvName;

            //LinearLayout
            LinearLayout llMain;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                //TextView
                tvDate = itemView.findViewById(R.id.tvDate);
                tvName = itemView.findViewById(R.id.tvName);

                //LinearLayout
                llMain = itemView.findViewById(R.id.llMain);

            }
        }
    }

    private class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MyViewHolder> {

        ArrayList<HashMap<String, String>> data;

        public MedicineAdapter(ArrayList<HashMap<String, String>> arrayList) {

            data = arrayList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_medicines, viewGroup, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

            String[] parts = data.get(position).get("givenDate").split(" ");

            holder.tvDate.setText(parts[0]);

            //holder.tvStartDate.setText(data.get(position).get("startDate"));

            //holder.tvDate.setText(data.get(position).get("givenDate"));
            holder.tvStartTime.setText(AppUtils.convertTimeTo12HoursFormat(data.get(position).get("givenTime")));
            holder.tvName.setText(data.get(position).get("name"));
            holder.tvQuantity.setText(data.get(position).get("quantity"));
            holder.tvUnit.setText(data.get(position).get("unit"));

            if (data.get(position).get("unit").isEmpty()) {
                holder.tvUnit.setText(getString(R.string.ml));
            }

            holder.llMain.setOnClickListener(new View.OnClickListener() {
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
            TextView tvDate, tvStartTime, tvName, tvQuantity, tvUnit;

            //LinearLayout
            LinearLayout llMain;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                //TextView
                tvDate = itemView.findViewById(R.id.tvDate);
                tvStartTime = itemView.findViewById(R.id.tvStartTime);
                tvName = itemView.findViewById(R.id.tvName);
                tvQuantity = itemView.findViewById(R.id.tvQuantity);
                tvUnit = itemView.findViewById(R.id.tvUnit);

                //LinearLayout
                llMain = itemView.findViewById(R.id.llMain);

            }
        }
    }


    //AlertMedicines
    private void AlertMedicines() {

        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_medicines);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        RelativeLayout rlStartDate = dialog.findViewById(R.id.rlStartDate);
        RelativeLayout rlStartTime = dialog.findViewById(R.id.rlStartTime);
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);

        TextView tvStartDate = dialog.findViewById(R.id.tvStartDate);
        TextView tvStartTime = dialog.findViewById(R.id.tvStartTime);

        //  uuid  = UUID.randomUUID().toString();

        //Spinner
        Spinner spinnerName = dialog.findViewById(R.id.spinnerName);
        Spinner spinnerUnit = dialog.findViewById(R.id.spinnerUnit);
        Spinner spinnerEnteredByNurse = dialog.findViewById(R.id.spinnerEnteredByNurse);

        //EditText
        EditText etQuantity = dialog.findViewById(R.id.etQuantity);

        treatmentName.clear();
        treatmentName.add(getString(R.string.selectTreatment));
        treatmentName.addAll(DatabaseController.getOrderedMedicine(AppSettings.getString(AppSettings.babyId)));

        Log.v("sjhq", String.valueOf(treatmentName.size()));

        treatmentUuid.clear();
        treatmentUuid.add("0");
        treatmentUuid.addAll(DatabaseController.getOrderedMedicineUUid(AppSettings.getString(AppSettings.babyId)));

        Log.v("qjkskq", String.valueOf(treatmentUuid.size()));


        unit.clear();
        unit.add(getString(R.string.selectUnit));
        unit.add(getString(R.string.ml));
        unit.add(getString(R.string.drops));

        unitName.clear();
        unitName.add(getString(R.string.selectUnit));
        unitName.add(getString(R.string.mlValue));
        unitName.add(getString(R.string.dropsValue));

        nurseId.clear();
        nurseId.add(getString(R.string.selectNurse));
        nurseId.addAll(DatabaseController.getNurseIdCheckedInData());

        nurseName.clear();
        nurseName.add(getString(R.string.selectNurse));
        nurseName.addAll(DatabaseController.getNurseNameCheckedInData());

        spinnerEnteredByNurse.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, nurseName));
        spinnerEnteredByNurse.setSelection(0);

        spinnerUnit.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, unit));
        spinnerUnit.setSelection(0);

        spinnerName.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, treatmentName));
        spinnerName.setSelection(0);

        spinnerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                uuid = treatmentUuid.get(position);
                Log.v("hshqjhsqs", uuid);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        rlStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppUtils.dateDialog(mActivity, tvStartDate, 0);

            }
        });

        rlStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppUtils.timeDialog(mActivity, tvStartTime);

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

                if (tvStartDate.getText().toString().trim().isEmpty()) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectDate));
                } else if (tvStartTime.getText().toString().trim().isEmpty()) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectTime));
                } else if (spinnerName.getSelectedItemPosition() == 0) {
                    AppUtils.showToastSort(mActivity, getString(R.string.selectTreatment));
                } else if (etQuantity.getText().toString().trim().isEmpty()) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorEnterQuantity));
                } else if (spinnerUnit.getSelectedItemPosition() == 0) {
                    AppUtils.showToastSort(mActivity, getString(R.string.selectUnit));
                } else if (spinnerEnteredByNurse.getSelectedItemPosition() == 0) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectYourName));
                } else {
                    ContentValues mContentValues = new ContentValues();

                    mContentValues.put(TableTreatment.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
                    mContentValues.put(TableTreatment.tableColumn.serverId.toString(), "");
                    mContentValues.put(TableTreatment.tableColumn.uuid.toString(), uuid);
                    mContentValues.put(TableTreatment.tableColumn.babyId.toString(), AppSettings.getString(AppSettings.babyId));
                    mContentValues.put(TableTreatment.tableColumn.treatmentName.toString(), spinnerName.getSelectedItem().toString());
                    mContentValues.put(TableTreatment.tableColumn.quantity.toString(), etQuantity.getText().toString().trim());
                    mContentValues.put(TableTreatment.tableColumn.nurseId.toString(), nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()));
                    mContentValues.put(TableTreatment.tableColumn.unit.toString(), unitName.get(spinnerUnit.getSelectedItemPosition()));
                    mContentValues.put(TableTreatment.tableColumn.givenDate.toString(), tvStartDate.getText().toString().trim());
                    mContentValues.put(TableTreatment.tableColumn.givenTime.toString(), tvStartTime.getText().toString().trim());
                    mContentValues.put(TableTreatment.tableColumn.unit.toString(), unitName.get(spinnerUnit.getSelectedItemPosition()));
                    mContentValues.put(TableTreatment.tableColumn.isDataSynced.toString(), "0");
                    //mContentValues.put(TableTreatment.tableColumn.comment.toString(), etComment.getText().toString().trim());
                    mContentValues.put(TableTreatment.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
                    mContentValues.put(TableTreatment.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());
                    mContentValues.put(TableTreatment.tableColumn.syncTime.toString(), "");
                    mContentValues.put(TableTreatment.tableColumn.status.toString(), "1");
                    mContentValues.put(TableTreatment.tableColumn.type.toString(), "2");

                    DatabaseController.insertUpdateData(mContentValues, TableTreatment.tableName,
                            TableTreatment.tableColumn.uuid.toString(), uuid);


                    if (AppUtils.isNetworkAvailable(mActivity)) {
                        SyncBabyRecord.getBabyDataToUpdate(mActivity, AppSettings.getString(AppSettings.babyId));
                        dialog.dismiss();
                        checkRecord();
                    } else {
                        AppUtils.showToastSort(mActivity, getString(R.string.dataSaved));
                        dialog.dismiss();
                        checkRecord();
                    }
                }
            }
        });
    }

    //AlertVaccines
    private void AlertVaccines() {
        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_vaccines);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        RelativeLayout rlStartDate = dialog.findViewById(R.id.rlStartDate);
        RelativeLayout rlStartTime = dialog.findViewById(R.id.rlStartTime);
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);

        TextView tvStartDate = dialog.findViewById(R.id.tvStartDate);
        TextView tvStartTime = dialog.findViewById(R.id.tvStartTime);

        Log.v("lkqsjkjqs", AppConstants.hashMap.get("admissionDate"));

        uuid = UUID.randomUUID().toString();

        //Spinner
        Spinner spinnerName = dialog.findViewById(R.id.spinnerName);
        Spinner spinnerEnteredByNurse = dialog.findViewById(R.id.spinnerEnteredByNurse);

        uuid = UUID.randomUUID().toString();

        nurseId.clear();
        nurseId.add(getString(R.string.erroselectNurse));
        nurseId.addAll(DatabaseController.getNurseIdCheckedInData());

        nurseName.clear();
        nurseName.add(getString(R.string.erroselectNurse));
        nurseName.addAll(DatabaseController.getNurseNameCheckedInData());

        spinnerEnteredByNurse.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, nurseName));
        spinnerEnteredByNurse.setSelection(0);

        setSpinnerValue();

//        Collections.sort(vaccinationName, new Comparator<String>() {
//            @Override
//            public int compare(String s1, String s2) {
//                return s1.compareToIgnoreCase(s2);
//            }
//        });

        spinnerName.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, vaccinationName));
        spinnerName.setSelection(0);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        rlStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dateDialog(mActivity, tvStartDate);

            }
        });

        rlStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppUtils.timeDialog(mActivity, tvStartTime);

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

                if (tvStartDate.getText().toString().trim().isEmpty()) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectDate));
                } else if (tvStartTime.getText().toString().trim().isEmpty()) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectTime));
                } else if (spinnerName.getSelectedItemPosition() == 0) {
                    AppUtils.showToastSort(mActivity, getString(R.string.selectVaccination));
                } else if (spinnerEnteredByNurse.getSelectedItemPosition() == 0) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectYourName));
                } else {
                    ContentValues mContentValues = new ContentValues();

                    mContentValues.put(TableVaccination.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
                    mContentValues.put(TableVaccination.tableColumn.serverId.toString(), "");
                    mContentValues.put(TableVaccination.tableColumn.uuid.toString(), uuid);
                    mContentValues.put(TableVaccination.tableColumn.babyId.toString(), AppSettings.getString(AppSettings.babyId));
                    mContentValues.put(TableVaccination.tableColumn.vaccName.toString(), vaccinationNameValue.get(spinnerName.getSelectedItemPosition()));
                    mContentValues.put(TableVaccination.tableColumn.date.toString(), AppUtils.getCurrentDateNew());
                    mContentValues.put(TableVaccination.tableColumn.quantity.toString(), "");
                    mContentValues.put(TableVaccination.tableColumn.date.toString(), tvStartDate.getText().toString().trim());
                    mContentValues.put(TableVaccination.tableColumn.time.toString(), tvStartTime.getText().toString().trim());
                    mContentValues.put(TableVaccination.tableColumn.isDataSynced.toString(), "0");
                    mContentValues.put(TableVaccination.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
                    mContentValues.put(TableVaccination.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());
                    mContentValues.put(TableVaccination.tableColumn.nurseId.toString(), nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()));
                    mContentValues.put(TableVaccination.tableColumn.syncTime.toString(), "");
                    mContentValues.put(TableVaccination.tableColumn.status.toString(), "1");

                    DatabaseController.insertUpdateData(mContentValues, TableVaccination.tableName,
                            TableVaccination.tableColumn.uuid.toString(), uuid);

                    AppUtils.showToastSort(mActivity, getString(R.string.dataSaved));

                    if (AppUtils.isNetworkAvailable(mActivity)) {
                        SyncBabyRecord.getBabyDataToUpdate(mActivity, AppSettings.getString(AppSettings.babyId));
                        dialog.dismiss();
                        checkRecord();
                    } else {
                        AppUtils.showToastSort(mActivity, getString(R.string.dataSaved));
                        dialog.dismiss();
                        checkRecord();
                    }
                }

            }
        });
    }

    private void setSpinnerValue() {

        vaccinationName.clear();
        vaccinationNameValue.clear();


        vaccinationName.add(getString(R.string.selectVaccination));
        vaccinationNameValue.add(getString(R.string.selectVaccination));


        String delDate = DatabaseController.getDelDate(AppSettings.getString(AppSettings.babyId));
        String current = AppUtils.getCurrentDateNew();

        int days = Integer.parseInt(AppUtils.getDateDiff(delDate));
        int weeks = AppUtils.getWeekDifference(current, delDate);

        if (weeks < 6) {
            vaccinationName.add(getString(R.string.bcg));
            vaccinationName.add(getString(R.string.opv0));
            vaccinationName.add(getString(R.string.hepB1));
            vaccinationNameValue.add(getString(R.string.bcgValue));
            vaccinationNameValue.add(getString(R.string.opv0Value));
            vaccinationNameValue.add(getString(R.string.hepB1Value));
        } else if (weeks == 6) {
            vaccinationName.add(getString(R.string.dtwP1));
            vaccinationName.add(getString(R.string.ipv1));
            vaccinationName.add(getString(R.string.hepB2));
            vaccinationName.add(getString(R.string.hib1));
            vaccinationName.add(getString(R.string.rotavirus1));
            vaccinationName.add(getString(R.string.pcv1));

            vaccinationNameValue.add(getString(R.string.dtwP1Value));
            vaccinationNameValue.add(getString(R.string.ipv1Value));
            vaccinationNameValue.add(getString(R.string.hepB2Value));
            vaccinationNameValue.add(getString(R.string.hib1Value));
            vaccinationNameValue.add(getString(R.string.rotavirus1Value));
            vaccinationNameValue.add(getString(R.string.pcv1Value));
        } else if (weeks == 10) {
            vaccinationName.add(getString(R.string.dtwp2));
            vaccinationName.add(getString(R.string.ipv2));
            vaccinationName.add(getString(R.string.hib2));
            vaccinationName.add(getString(R.string.rotavirus2));
            vaccinationName.add(getString(R.string.pcv2));

            vaccinationNameValue.add(getString(R.string.dtwp2Value));
            vaccinationNameValue.add(getString(R.string.ipv2Value));
            vaccinationNameValue.add(getString(R.string.hib2Value));
            vaccinationNameValue.add(getString(R.string.rotavirus2Value));
            vaccinationNameValue.add(getString(R.string.pcv2Value));
        } else if (weeks == 14) {
            vaccinationName.add(getString(R.string.dtwp3));
            vaccinationName.add(getString(R.string.ipv3));
            vaccinationName.add(getString(R.string.hib3));
            vaccinationName.add(getString(R.string.rotavirus3));
            vaccinationName.add(getString(R.string.pcv3));

            vaccinationNameValue.add(getString(R.string.dtwp3Value));
            vaccinationNameValue.add(getString(R.string.ipv3Value));
            vaccinationNameValue.add(getString(R.string.hib3Value));
            vaccinationNameValue.add(getString(R.string.rotavirus3Value));
            vaccinationNameValue.add(getString(R.string.pcv3Value));
        } else if (weeks == 26) {
            vaccinationName.add(getString(R.string.opv1));
            vaccinationName.add(getString(R.string.hep_b3));

            vaccinationNameValue.add(getString(R.string.opv1Value));
            vaccinationNameValue.add(getString(R.string.hep_b3Value));
        } else if (weeks == 39) {
            vaccinationName.add(getString(R.string.opv2));
            vaccinationName.add(getString(R.string.mmr_1));

            vaccinationNameValue.add(getString(R.string.opv2Value));
            vaccinationNameValue.add(getString(R.string.mmr_1Value));
        } else if (weeks > 39 && weeks < 52) {
            vaccinationName.add(getString(R.string.typhoid));
            vaccinationName.add(getString(R.string.conjugateVaccine));

            vaccinationNameValue.add(getString(R.string.typhoidValue));
            vaccinationNameValue.add(getString(R.string.conjugateVaccineValue));
        } else if (weeks == 52) {
            vaccinationName.add(getString(R.string.hep_a1));

            vaccinationNameValue.add(getString(R.string.hep_a1Value));
        } else if (weeks == 65) {
            vaccinationName.add(getString(R.string.mmr_2));
            vaccinationName.add(getString(R.string.varicella1));
            vaccinationName.add(getString(R.string.pcvBooster));

            vaccinationNameValue.add(getString(R.string.mmr_2Value));
            vaccinationNameValue.add(getString(R.string.varicella1Value));
            vaccinationNameValue.add(getString(R.string.pcvBoosterValue));
        } else if (weeks >= 69 && weeks < 78) {
            vaccinationName.add(getString(R.string.dtwpB1_dtapB1));
            vaccinationName.add(getString(R.string.ipvb1_hibb1));

            vaccinationNameValue.add(getString(R.string.dtwpB1_dtapB1Value));
            vaccinationNameValue.add(getString(R.string.ipvb1_hibb1Value));
        } else if (weeks >= 78 && weeks <= 104) {
            vaccinationName.add(getString(R.string.hep_a2));
            vaccinationName.add(getString(R.string.typhoidBooster));

            vaccinationNameValue.add(getString(R.string.hep_a2Value));
            vaccinationNameValue.add(getString(R.string.typhoidBoosterValue));
        } else if (weeks >= 208 && weeks <= 313) {
            vaccinationName.add(getString(R.string.dtwpB2_dtapB2));
            vaccinationName.add(getString(R.string.opv3Varicella2));
            vaccinationName.add(getString(R.string.typhoidBooster4Year));

            vaccinationNameValue.add(getString(R.string.dtwpB2_dtapB2Value));
            vaccinationNameValue.add(getString(R.string.opv3Varicella2Value));
            vaccinationNameValue.add(getString(R.string.typhoidBooster4YearValue));
        } else if (weeks >= 521 && weeks <= 625) {
            vaccinationName.add(getString(R.string.tdap_td));
            vaccinationName.add(getString(R.string.hpv));

            vaccinationNameValue.add(getString(R.string.tdap_tdValue));
            vaccinationNameValue.add(getString(R.string.hpvValue));
        }

        vaccinationName.add(getString(R.string.other));
        vaccinationNameValue.add(getString(R.string.otherValue));


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

    //Get All Babies
    private void getBabyDataApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {

            jsonData.put("coachId", AppSettings.getString(AppSettings.coachId));
            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("babyId", AppSettings.getString(AppSettings.babyId));
            jsonData.put("type", "3");
            jsonData.put("date", "");

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
                        arrayListVacc.clear();

                        JSONArray jsonArray = jsonObject.getJSONArray("vaccinationData");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject arrayJSONObject = jsonArray.getJSONObject(i);

                            HashMap<String, String> hashlist = new HashMap();

                            hashlist.put("vaccinationName", arrayJSONObject.getString("vaccinationName"));
                            hashlist.put("quantity", arrayJSONObject.getString("quantity"));
                            hashlist.put("time", arrayJSONObject.getString("vaccinationTime"));
                            hashlist.put("date", arrayJSONObject.getString("vaccinationDate"));

                            arrayListVacc.add(hashlist);
                        }

                        JSONArray jsonObjectJSONArray = jsonObject.getJSONArray("prescriptionData");

                        for (int i = 0; i < jsonObjectJSONArray.length(); i++) {

                            JSONObject arrayJSONObject = jsonObjectJSONArray.getJSONObject(i);

                            HashMap<String, String> hashlist = new HashMap();

                            hashlist.put("name", arrayJSONObject.getString("prescriptionName"));
                            hashlist.put("quantity", arrayJSONObject.getString("quantity"));
                            hashlist.put("givenDate", arrayJSONObject.getString("addDate"));
                            hashlist.put("givenTime", arrayJSONObject.getString("prescriptionTime"));
                            hashlist.put("doctorName", arrayJSONObject.getString("nurseName"));
                            hashlist.put("unit", arrayJSONObject.getString("unit"));

                            arrayList.add(hashlist);
                        }

                        medicineAdapter = new MedicineAdapter(arrayList);
                        rvMedicines.setAdapter(medicineAdapter);

                        adapter = new Adapter(arrayListVacc);
                        rvVaccines.setAdapter(adapter);
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

    private static String dateDialog(Context context, final TextView textView) {
        final String[] times = {""};

        Calendar mcurrentDate = Calendar.getInstance();
        int year = mcurrentDate.get(Calendar.YEAR);
        int month = mcurrentDate.get(Calendar.MONTH);
        int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        if (!textView.getText().toString().isEmpty()) {
            String[] parts = textView.getText().toString().split("-");
            String part1 = parts[2];
            String part2 = parts[1];
            String part3 = parts[0];

            day = Integer.parseInt(part1);
            month = Integer.parseInt(part2);
            year = Integer.parseInt(part3);
        }

        DatePickerDialog mDatePicker1 = new DatePickerDialog(context, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                String dob = String.valueOf(new StringBuilder().append(selectedday).append("-").append(selectedmonth + 1).append("-").append(selectedyear));
                Log.d("dob", dob);
                Log.d("dob", AppUtils.formatDate(selectedyear, selectedmonth, selectedday));

                textView.setText(AppUtils.formatDate(selectedyear, selectedmonth, selectedday));
            }
        }, year, month, day);

        mDatePicker1.getDatePicker().setMinDate(Long.parseLong(AppUtils.changeDateToTimestamp3(AppConstants.hashMap.get("admissionDate"))));
        mDatePicker1.getDatePicker().setMaxDate(AppUtils.getCurrentTimestamp());

        mDatePicker1.show();

        return times[0];
    }


}
