package code.discharge;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.kmcapp.android.R;

import java.util.ArrayList;
import java.util.HashMap;

import code.algo.SyncAllRecord;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableBabyMonitoring;
import code.database.TableWeight;
import code.main.BabyDischargeActivity;
import code.main.WeightAddActivity;
import code.registration.MotherBabyRegistationFragment;
import code.registration.RegistrationActivity;
import code.utils.AppUtils;
import code.view.BaseFragment;


public class DischargeFindingsFragment extends BaseFragment implements View.OnClickListener {


    RelativeLayout rl_length,rlReason,rlNote;
    Spinner spinnerReason;
    EditText etReason;

    RelativeLayout rl_Hlength,rlHReason,rlHNote;
    Spinner spinnerHReason;
    EditText etHReason;


    //TextView
    private TextView tvDischargeType,tvResult;

    //ImageView
    private ImageView ivDischarge1,ivDischarge2,ivDischarge3,ivDischarge4,ivDischarge5;

    //RelativeLayout
    private RelativeLayout rlNext, rlPrevious;

    //Mother Section
    private static ArrayList<String> dischargeTypeList = new ArrayList<String>();
    private static ArrayList<String> dischargeTypeListValue = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_discharge_findings,container,false);

        initialize(view);

        setListeners();

        return view;
    }

    private void initialize(View v) {

        //TextView
        tvDischargeType = v.findViewById(R.id.tvDischargeType);
        tvResult = v.findViewById(R.id.tvResult);

        //ImageView
        ivDischarge1= v.findViewById(R.id.ivDischarge1);
        ivDischarge2= v.findViewById(R.id.ivDischarge2);
        ivDischarge3= v.findViewById(R.id.ivDischarge3);
        ivDischarge4= v.findViewById(R.id.ivDischarge4);
        ivDischarge5= v.findViewById(R.id.ivDischarge5);

        //RelativeLayout
        rlNext= v.findViewById(R.id.rlNext);
        rlPrevious= v.findViewById(R.id.rlPrevious);

        setValues();

    }

    private void setValues() {

        int weight = DatabaseController.getBabyWeightDays(AppSettings.getString(AppSettings.babyId));
        int assessment = DatabaseController.getBabyAssessmentDays(AppSettings.getString(AppSettings.babyId));
        int feeding = DatabaseController.getBabyFeedingDays(AppSettings.getString(AppSettings.babyId));

        Log.d("weight", String.valueOf(weight));

        boolean assessmentOnce = DatabaseController.checkRecordExistWhere(TableBabyMonitoring.tableName,
                TableBabyMonitoring.tableColumn.addDate+" > '"+AppUtils.getCurrentDateFormatted()+" 00:00:00 ' and "+TableWeight.tableColumn.babyId +" = '"+AppSettings.getString(AppSettings.babyId)+"'");

        Log.d("assessmentOnce", String.valueOf(assessmentOnce));

        ivDischarge1.setImageResource(R.drawable.ic_tick);
        ivDischarge1.setBackgroundResource(R.drawable.circle_teal);

        ivDischarge2.setImageResource(R.drawable.ic_tick);
        ivDischarge2.setBackgroundResource(R.drawable.circle_teal);

        ivDischarge3.setImageResource(R.drawable.ic_tick);
        ivDischarge3.setBackgroundResource(R.drawable.circle_teal);

        ivDischarge4.setImageResource(R.drawable.ic_tick);
        ivDischarge4.setBackgroundResource(R.drawable.circle_teal);

        ivDischarge5.setImageResource(R.drawable.ic_tick);
        ivDischarge5.setBackgroundResource(R.drawable.circle_teal);

         if(!assessmentOnce)
        {
            ivDischarge1.setImageResource(R.drawable.ic_cross);
            ivDischarge1.setBackgroundResource(R.drawable.circle_red);
        }
         else if(weight==0)
        {
            ivDischarge2.setImageResource(R.drawable.ic_cross);
            ivDischarge2.setBackgroundResource(R.drawable.circle_red);
        }
         else if(assessment==0)
        {
            ivDischarge3.setImageResource(R.drawable.ic_cross);
            ivDischarge3.setBackgroundResource(R.drawable.circle_red);
        }
         else if(feeding==0)
        {
            ivDischarge4.setImageResource(R.drawable.ic_cross);
            ivDischarge4.setBackgroundResource(R.drawable.circle_red);
        }

         if(assessmentOnce&&weight==1&&assessment==1&&feeding==1)
         {
            AppSettings.putString(AppSettings.dischargeCondtion,"1");

             tvDischargeType.setText(getString(R.string.normalDischarge));

//             tvResult.setText(AppUtils.setSpannable2(
//                     getString(R.string.theInfant),
//                     getString(R.string.eligible),
//                     getString(R.string.forNormalDischarge),
//                     mActivity));

             if(AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en"))
             {
                 tvResult.setText(AppUtils.setSpannable2(
                         getString(R.string.asPerCriteria),
                         getString(R.string.theInfant),
                         getString(R.string.eligible)+getString(R.string.forNormalDischarge),
                         mActivity));
             }
             else
             {
                 tvResult.setText(AppUtils.setSpannable2(
                         getString(R.string.theInfant),
                         getString(R.string.asPerCriteria),
                         getString(R.string.forNormalDischarge)+" तैयार हैं ।",
                         mActivity));
             }



         }
         else
         {
             AppSettings.putString(AppSettings.dischargeCondtion,"0");

             tvDischargeType.setText(getString(R.string.exceptionalDischarge));


             if(AppSettings.getString(AppSettings.keyLanguageCode).equalsIgnoreCase("en"))
             {
                 tvResult.setText(AppUtils.setSpannable2(
                         getString(R.string.asPerCriteria),
                         getString(R.string.theInfant),
                         getString(R.string.notEligible),

                         mActivity));
             }
             else
             {
                 tvResult.setText(AppUtils.setSpannable2(
                         getString(R.string.theInfant),
                         getString(R.string.asPerCriteria),
                         getString(R.string.notEligible),

                         mActivity));
             }


         }

    }

    private void setListeners() {

        rlNext.setOnClickListener(this);
        rlPrevious.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rlNext:

                if(AppSettings.getString(AppSettings.dischargeCondtion).equals("1"))
                {
                    AppSettings.putString(AppSettings.dischargeCondtion,getString(R.string.normalDischargeValues));
                    ((BabyDischargeActivity)getActivity()).displayView(1);
                }
                else
                {
                    AlertDischargeType();
                }

                break;

            case R.id.rlPrevious:

                mActivity.onBackPressed();

                break;

            default:

                break;

        }

    }

    //Alert For DischargeType
    private void AlertDischargeType() {
        final Dialog dialog = new Dialog(mActivity,android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        dialog.setContentView(R.layout.alert_discharge_type);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //TextView
        TextView tvSubmit = dialog.findViewById(R.id.tvSubmit);

        //ImageView
        ImageView ivImage = dialog.findViewById(R.id.ivImage);

        //Spinner
        Spinner spinnerDischarge = dialog.findViewById(R.id.spinnerDischarge);

        //RelativeLayout
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);
        RelativeLayout rlDischarge = dialog.findViewById(R.id.rlDischarge);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        dischargeTypeList.clear();
        dischargeTypeList.add(getString(R.string.reason));
        dischargeTypeList.add(getString(R.string.discharge1));
        dischargeTypeList.add(getString(R.string.discharge2));
        dischargeTypeList.add(getString(R.string.discharge3));
        dischargeTypeList.add(getString(R.string.discharge4));
        dischargeTypeList.add(getString(R.string.discharge5));
        dischargeTypeList.add(getString(R.string.discharge6));

        dischargeTypeListValue.clear();
        dischargeTypeListValue.add(getString(R.string.reason));
        dischargeTypeListValue.add(getString(R.string.discharge1Value));
        dischargeTypeListValue.add(getString(R.string.discharge2Value));
        dischargeTypeListValue.add(getString(R.string.discharge3Value));
        dischargeTypeListValue.add(getString(R.string.discharge4Value));
        dischargeTypeListValue.add(getString(R.string.discharge5Value));
        dischargeTypeListValue.add(getString(R.string.discharge6Value));

        spinnerDischarge.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, dischargeTypeList));
        spinnerDischarge.setSelection(0);

        //Spinner for Discharge
        spinnerDischarge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        ivImage.setVisibility(View.VISIBLE);
        ivImage.setImageResource(R.drawable.ic_warning);

        rlCancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

               dialog.dismiss();
               mActivity.finish();

            }
        });

        rlOk.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                rlDischarge.setVisibility(View.VISIBLE);
                tvSubmit.setVisibility(View.VISIBLE);

            }
        });

        tvSubmit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(spinnerDischarge.getSelectedItemPosition()==0)
                {
                    AppUtils.showToastSort(mActivity,getString(R.string.errorSelectReason));
                }
                else if(spinnerDischarge.getSelectedItemPosition()==1)
                {
                    dialog.dismiss();
                    AppSettings.putString(AppSettings.dischargeCondtion,dischargeTypeListValue.get(spinnerDischarge.getSelectedItemPosition()));

                    ((BabyDischargeActivity)getActivity()).displayView(2);
                }
                else if(spinnerDischarge.getSelectedItemPosition()==2)
                {
                    dialog.dismiss();
                    AppSettings.putString(AppSettings.dischargeCondtion,dischargeTypeListValue.get(spinnerDischarge.getSelectedItemPosition()));

                    ((BabyDischargeActivity)getActivity()).displayView(3);
                }
                else if(spinnerDischarge.getSelectedItemPosition()==3)
                {
                    dialog.dismiss();
                    AppSettings.putString(AppSettings.dischargeCondtion,dischargeTypeListValue.get(spinnerDischarge.getSelectedItemPosition()));

                    ((BabyDischargeActivity)getActivity()).displayView(4);
                }
                else if(spinnerDischarge.getSelectedItemPosition()==4)
                {
                    dialog.dismiss();
                    AppSettings.putString(AppSettings.dischargeCondtion,dischargeTypeListValue.get(spinnerDischarge.getSelectedItemPosition()));

                    ((BabyDischargeActivity)getActivity()).displayView(5);
                }
                else if(spinnerDischarge.getSelectedItemPosition()==5)
                {
                    dialog.dismiss();
                    AppSettings.putString(AppSettings.dischargeCondtion,dischargeTypeListValue.get(spinnerDischarge.getSelectedItemPosition()));

                    ((BabyDischargeActivity)getActivity()).displayView(6);
                }
                else if(spinnerDischarge.getSelectedItemPosition()==6)
                {
                    dialog.dismiss();
                    AppSettings.putString(AppSettings.dischargeCondtion,dischargeTypeListValue.get(spinnerDischarge.getSelectedItemPosition()));

                    ((BabyDischargeActivity)getActivity()).displayView(7);
                }
                else
                {
                    dialog.dismiss();
                    AppSettings.putString(AppSettings.dischargeCondtion,dischargeTypeListValue.get(spinnerDischarge.getSelectedItemPosition()));

                    ((BabyDischargeActivity)getActivity()).displayView(1);
                }

            }
        });
    }

    private static class adapterSpinner extends ArrayAdapter<String> {

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

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            View row=inflater.inflate(R.layout.inflate_spinner_new, parent, false);

            TextView tvName=row.findViewById(R.id.tvName);

            tvName.setText(data.get(position));

            return row;
        }
    }
}
