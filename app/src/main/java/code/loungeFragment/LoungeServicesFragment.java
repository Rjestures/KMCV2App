package code.loungeFragment;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kmcapp.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import code.algo.SyncAllRecord;
import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableBabyMonitoring;
import code.database.TableLounge;
import code.database.TableLoungeServices;
import code.database.TableMotherMonitoring;
import code.fragment.LoungeMonitoringFragment;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;


public class LoungeServicesFragment extends BaseFragment implements View.OnClickListener {

    //Imagview
    private ImageView ivLoungeSlot1, ivLoungeSlot2, ivLoungeSlot3, ivToiletSlot1, ivToiletSlot2, ivToiletSlot3, ivDiningSlot1,
            ivDiningSlot2, ivDiningSlot3, ivBreakFast, ivLunch, ivDinner, ivWaterSlot1, ivWaterSlot2, ivWaterSlot3,
            ivBedSheetResult,ivSanitationResult,ivMealResult,ivDrinkingResult;
            ArrayList <String> positionValue =new ArrayList<>();

    //int
    private int slot=0;


    //RelativeLayout
    private RelativeLayout rlBedSheet,rlNext;

    //TextView
    private TextView tvBedSheetResult;

    private ArrayList<String> nurseId = new ArrayList<String>();
    private ArrayList<String> nurseName = new ArrayList<String>();
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList();

    private String uuid = "" ;

    private int check1 = 0,check2 = 0,check3 = 0;

    //ScrollView
    ScrollView scrollView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lounge_services, container, false);

        init(view);
        setClickListeners();

        return view;
    }


    private void init(View view) {

        ivLoungeSlot1 = view.findViewById(R.id.ivLoungeSlot1);
        ivLoungeSlot2 = view.findViewById(R.id.ivLoungeSlot2);
        ivLoungeSlot3 = view.findViewById(R.id.ivLoungeSlot3);
        ivToiletSlot1 = view.findViewById(R.id.ivToiletSlot1);
        ivToiletSlot2 = view.findViewById(R.id.ivToiletSlot2);
        ivToiletSlot3 = view.findViewById(R.id.ivToiletSlot3);
        ivDiningSlot1 = view.findViewById(R.id.ivDiningSlot1);
        ivDiningSlot2 = view.findViewById(R.id.ivDiningSlot2);
        ivDiningSlot3 = view.findViewById(R.id.ivDiningSlot3);
        ivBreakFast = view.findViewById(R.id.ivBreakFast);
        ivLunch = view.findViewById(R.id.ivLunch);
        ivDinner = view.findViewById(R.id.ivDinner);
        ivWaterSlot1 = view.findViewById(R.id.ivWaterSlot1);
        ivWaterSlot2 = view.findViewById(R.id.ivWaterSlot2);
        ivWaterSlot3 = view.findViewById(R.id.ivWaterSlot3);
        ivBedSheetResult= view.findViewById(R.id.ivBedSheetResult);
        ivSanitationResult= view.findViewById(R.id.ivSanitationResult);
        ivMealResult= view.findViewById(R.id.ivMealResult);
        ivDrinkingResult= view.findViewById(R.id.ivDrinkingResult);

        //RelativeLayout
        rlBedSheet= view.findViewById(R.id.rlBedSheet);
        rlNext= view.findViewById(R.id.rlNext);

        //ScrollView
        scrollView= view.findViewById(R.id.scrollView);

        //TextView
        tvBedSheetResult= view.findViewById(R.id.tvBedSheetResult);

        TextView tvShiftTime=view.findViewById(R.id.tvShiftTime);
        String time = AppUtils.getCurrentTime();

        Log.d("time-before",time);

        String[] parts = time.split(":");

        String slotSection1="",slotSection2="";

        Log.d("time-after",parts[0]);

        int currentTime= Integer.parseInt(parts[0]);

        if(currentTime>=8&&currentTime<14)
        {
            slotSection1 = getString(R.string.slot8am);
            slotSection2 = getString(R.string.slot2pm);
        }
        else if(currentTime>=14&&currentTime<20)
        {
            slotSection1 = getString(R.string.slot2pm);
            slotSection2 = getString(R.string.slot8pm);
        }
        else
        {
            slotSection1 = getString(R.string.slot8pm);
            slotSection2 = getString(R.string.slot8am);
        }

        tvShiftTime.setText(slotSection1+" "+getString(R.string.to)+" "+slotSection2 + " "+getString(R.string.shift));

        setDefault();
        setTimeShot();

        AppUtils.hideSoftKeyboard(mActivity);



    }

    @Override
    public void onResume() {
        super.onResume();

        if(AppSettings.getString(AppSettings.userType).equalsIgnoreCase("1"))
        {
            AppUtils.enableDisable(scrollView,false);

            if (AppUtils.isNetworkAvailable(mActivity)) {
                getStatusApi();
            } else {
                AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
            }
        }
        else
        {
            setValues();
        }
    }

    private void setValues() {

        arrayList.clear();
        arrayList.addAll(DatabaseController.getLoungeServiceData(AppUtils.getCurrentDateFormatted()));

        if(arrayList.size()>0)
        {
            for(int i=0;i<arrayList.size();i++) {

                if (arrayList.get(i).get("type").equals("1")) {
                    rlBedSheet.setEnabled(false);
                    if (arrayList.get(i).get("value").equals(getString(R.string.yesValue))) {
                        ivBedSheetResult.setImageResource(R.drawable.ic_happy_smily);
                        //ivBedSheetResult.setBackgroundResource(R.drawable.circle_teal);
                    } else {
                        ivBedSheetResult.setImageResource(R.drawable.ic_sad_smily);
                        //ivBedSheetResult.setBackgroundResource(R.drawable.circle_red);
                    }
                }

                if (slot >= 1) {
                    if (arrayList.get(i).get("slot").equals("1") && arrayList.get(i).get("type").equals("2")) {
                        if (arrayList.get(i).get("value").equals(getString(R.string.yesValue))) {
                            ivLoungeSlot1.setEnabled(false);
                            ivLoungeSlot1.setImageResource(R.drawable.ic_check_box_selected);

                            if (slot == Integer.parseInt(arrayList.get(i).get("slot"))) {
                                check1 = 1;
                            }
                        } else {
                            check1 = 2;
                            ivLoungeSlot1.setEnabled(false);
                            ivLoungeSlot1.setImageResource(R.drawable.ic_cross_new);
                        }
                    }

                    if (arrayList.get(i).get("slot").equals("1") && arrayList.get(i).get("type").equals("3")) {
                        if (arrayList.get(i).get("value").equals(getString(R.string.yesValue))) {
                            ivToiletSlot1.setEnabled(false);
                            ivToiletSlot1.setImageResource(R.drawable.ic_check_box_selected);

                            if (slot == Integer.parseInt(arrayList.get(i).get("slot"))) {
                                check2 = 1;
                            }
                        } else {
                            check2 = 2;
                            ivToiletSlot1.setEnabled(false);
                            ivToiletSlot1.setImageResource(R.drawable.ic_cross_new);
                        }
                    }

                    if (arrayList.get(i).get("slot").equals("1") && arrayList.get(i).get("type").equals("4")) {
                        if (arrayList.get(i).get("value").equals(getString(R.string.yesValue))) {
                            ivDiningSlot1.setEnabled(false);
                            ivDiningSlot1.setImageResource(R.drawable.ic_check_box_selected);

                            if (slot == Integer.parseInt(arrayList.get(i).get("slot"))) {
                                check3 = 1;
                            }
                        } else {
                            check3 = 2;
                            ivDiningSlot1.setEnabled(false);
                            ivDiningSlot1.setImageResource(R.drawable.ic_cross_new);
                        }
                    }

                    if (arrayList.get(i).get("slot").equals("1") && arrayList.get(i).get("type").equals("5")) {
                        if (arrayList.get(i).get("value").equals(getString(R.string.yesValue))) {
                            ivBreakFast.setEnabled(false);
                            ivBreakFast.setImageResource(R.drawable.ic_check_box_selected);

                            if (slot == Integer.parseInt(arrayList.get(i).get("slot"))) {
                                ivMealResult.setImageResource(R.drawable.ic_happy_smily);
                                //ivMealResult.setBackgroundResource(R.drawable.circle_teal);
                            }
                        } else {
                            ivBreakFast.setEnabled(false);
                            ivBreakFast.setImageResource(R.drawable.ic_cross_new);
                        }
                    }

                    if (arrayList.get(i).get("slot").equals("1") && arrayList.get(i).get("type").equals("6")) {
                        if (arrayList.get(i).get("value").equals(getString(R.string.yesValue))) {
                            ivWaterSlot1.setEnabled(false);
                            ivWaterSlot1.setImageResource(R.drawable.ic_check_box_selected);

                            if (slot == Integer.parseInt(arrayList.get(i).get("slot"))) {
                                ivDrinkingResult.setImageResource(R.drawable.ic_happy_smily);
                                //ivDrinkingResult.setBackgroundResource(R.drawable.circle_teal);
                            }
                        } else {
                            ivWaterSlot1.setEnabled(false);
                            ivWaterSlot1.setImageResource(R.drawable.ic_cross_new);
                        }
                    }
                }


                if (slot >= 2) {
                    if (arrayList.get(i).get("slot").equals("2") && arrayList.get(i).get("type").equals("2")) {
                        if (arrayList.get(i).get("value").equals(getString(R.string.yesValue))) {
                            ivLoungeSlot2.setEnabled(false);
                            ivLoungeSlot2.setImageResource(R.drawable.ic_check_box_selected);

                            if (slot == Integer.parseInt(arrayList.get(i).get("slot"))) {
                                check1 = 1;
                            }
                        } else {
                            check1 = 2;
                            ivLoungeSlot2.setEnabled(false);
                            ivLoungeSlot2.setImageResource(R.drawable.ic_cross_new);
                        }
                    }

                    if (arrayList.get(i).get("slot").equals("2") && arrayList.get(i).get("type").equals("3")) {
                        if (arrayList.get(i).get("value").equals(getString(R.string.yesValue))) {
                            ivToiletSlot2.setEnabled(false);
                            ivToiletSlot2.setImageResource(R.drawable.ic_check_box_selected);

                            if (slot == Integer.parseInt(arrayList.get(i).get("slot"))) {
                                check2 = 1;
                            }
                        } else {
                            check2 = 2;
                            ivToiletSlot2.setEnabled(false);
                            ivToiletSlot2.setImageResource(R.drawable.ic_cross_new);
                        }
                    }

                    if (arrayList.get(i).get("slot").equals("2") && arrayList.get(i).get("type").equals("4")) {
                        if (arrayList.get(i).get("value").equals(getString(R.string.yesValue))) {
                            ivDiningSlot2.setEnabled(false);
                            ivDiningSlot2.setImageResource(R.drawable.ic_check_box_selected);

                            if (slot == Integer.parseInt(arrayList.get(i).get("slot"))) {
                                check3 = 1;
                            }
                        } else {
                            check3 = 2;
                            ivDiningSlot2.setEnabled(false);
                            ivDiningSlot2.setImageResource(R.drawable.ic_cross_new);
                        }
                    }

                    if (arrayList.get(i).get("slot").equals("2") && arrayList.get(i).get("type").equals("5")) {
                        if (arrayList.get(i).get("value").equals(getString(R.string.yesValue))) {
                            ivLunch.setEnabled(false);
                            ivLunch.setImageResource(R.drawable.ic_check_box_selected);

                            if (slot == Integer.parseInt(arrayList.get(i).get("slot"))) {
                                ivMealResult.setImageResource(R.drawable.ic_happy_smily);
                                //ivMealResult.setBackgroundResource(R.drawable.circle_teal);
                            }
                        } else {
                            ivLunch.setEnabled(false);
                            ivLunch.setImageResource(R.drawable.ic_cross_new);
                        }
                    }

                    if (arrayList.get(i).get("slot").equals("2") && arrayList.get(i).get("type").equals("6")) {
                        if (arrayList.get(i).get("value").equals(getString(R.string.yesValue))) {
                            ivWaterSlot2.setEnabled(false);
                            ivWaterSlot2.setImageResource(R.drawable.ic_check_box_selected);

                            if (slot == Integer.parseInt(arrayList.get(i).get("slot"))) {
                                ivDrinkingResult.setImageResource(R.drawable.ic_happy_smily);
                                //ivDrinkingResult.setBackgroundResource(R.drawable.circle_teal);
                            }
                        } else {
                            ivWaterSlot2.setEnabled(false);
                            ivWaterSlot2.setImageResource(R.drawable.ic_cross_new);
                        }
                    }
                }


                if (slot == 3) {
                    if(arrayList.get(i).get("slot").equals("3")&&arrayList.get(i).get("type").equals("2"))
                    {
                        if(arrayList.get(i).get("value").equals(getString(R.string.yesValue)))
                        {
                            ivLoungeSlot3.setEnabled(false);
                            ivLoungeSlot3.setImageResource(R.drawable.ic_check_box_selected);

                            if(slot==Integer.parseInt(arrayList.get(i).get("slot")))
                            {
                                check1 = 1;
                            }
                        }
                        else
                        {
                            check1 = 2;
                            ivLoungeSlot3.setEnabled(false);
                            ivLoungeSlot3.setImageResource(R.drawable.ic_cross_new);
                        }
                    }

                    if(arrayList.get(i).get("slot").equals("3")&&arrayList.get(i).get("type").equals("3"))
                    {
                        if(arrayList.get(i).get("value").equals(getString(R.string.yesValue)))
                        {
                            ivToiletSlot3.setEnabled(false);
                            ivToiletSlot3.setImageResource(R.drawable.ic_check_box_selected);

                            if(slot==Integer.parseInt(arrayList.get(i).get("slot")))
                            {
                                check2 = 1;
                            }
                        }
                        else
                        {
                            check2 = 2;
                            ivToiletSlot3.setEnabled(false);
                            ivToiletSlot3.setImageResource(R.drawable.ic_cross_new);
                        }
                    }

                    if(arrayList.get(i).get("slot").equals("3")&&arrayList.get(i).get("type").equals("4"))
                    {
                        if(arrayList.get(i).get("value").equals(getString(R.string.yesValue)))
                        {
                            ivDiningSlot3.setEnabled(false);
                            ivDiningSlot3.setImageResource(R.drawable.ic_check_box_selected);

                            if(slot==Integer.parseInt(arrayList.get(i).get("slot")))
                            {
                                check3 = 1;
                            }
                        }
                        else
                        {
                            check3 = 2;
                            ivDiningSlot3.setEnabled(false);
                            ivDiningSlot3.setImageResource(R.drawable.ic_cross_new);
                        }
                    }

                    if(arrayList.get(i).get("slot").equals("3")&&arrayList.get(i).get("type").equals("5"))
                    {
                        if(arrayList.get(i).get("value").equals(getString(R.string.yesValue)))
                        {
                            ivDinner.setEnabled(false);
                            ivDinner.setImageResource(R.drawable.ic_check_box_selected);

                            if(slot==Integer.parseInt(arrayList.get(i).get("slot")))
                            {
                                ivMealResult.setImageResource(R.drawable.ic_happy_smily);
                                //ivMealResult.setBackgroundResource(R.drawable.circle_teal);
                            }
                        }
                        else
                        {
                            ivDinner.setEnabled(false);
                            ivDinner.setImageResource(R.drawable.ic_cross_new);
                        }
                    }

                    if(arrayList.get(i).get("slot").equals("3")&&arrayList.get(i).get("type").equals("6"))
                    {
                        if(arrayList.get(i).get("value").equals(getString(R.string.yesValue)))
                        {
                            ivWaterSlot3.setEnabled(false);
                            ivWaterSlot3.setImageResource(R.drawable.ic_check_box_selected);

                            if(slot==Integer.parseInt(arrayList.get(i).get("slot")))
                            {
                                ivDrinkingResult.setImageResource(R.drawable.ic_happy_smily);
                                //ivDrinkingResult.setBackgroundResource(R.drawable.circle_teal);
                            }
                        }
                        else
                        {
                            ivWaterSlot3.setEnabled(false);
                            ivWaterSlot3.setImageResource(R.drawable.ic_cross_new);
                        }
                    }
                }
            }
        }

        if(check1 == 1 && check2 == 1 && check3==1)
        {
            ivSanitationResult.setImageResource(R.drawable.ic_happy_smily);
            //ivSanitationResult.setBackgroundResource(R.drawable.circle_teal);
        }
        else  if(check1 == 2 || check2 == 2 || check3==2 )
        {
            ivSanitationResult.setImageResource(R.drawable.ic_sad_smily);
            //ivSanitationResult.setBackgroundResource(R.drawable.circle_red);
        }
    }

    private void setDefault() {

        ivLoungeSlot1.setEnabled(false);
        ivLoungeSlot2.setEnabled(false);
        ivLoungeSlot3.setEnabled(false);
        ivToiletSlot1.setEnabled(false);
        ivToiletSlot2.setEnabled(false);
        ivToiletSlot3.setEnabled(false);
        ivDiningSlot1.setEnabled(false);
        ivDiningSlot2.setEnabled(false);
        ivDiningSlot3.setEnabled(false);
        ivBreakFast .setEnabled(false);
        ivLunch.setEnabled(false);
        ivDinner.setEnabled(false);
        ivWaterSlot1.setEnabled(false);
        ivWaterSlot2.setEnabled(false);
        ivWaterSlot3.setEnabled(false);

        ivLoungeSlot1.setImageResource(R.drawable.ic_check_box_grey);
        ivLoungeSlot2.setImageResource(R.drawable.ic_check_box_grey);
        ivLoungeSlot3.setImageResource(R.drawable.ic_check_box_grey);
        ivToiletSlot1.setImageResource(R.drawable.ic_check_box_grey);
        ivToiletSlot2.setImageResource(R.drawable.ic_check_box_grey);
        ivToiletSlot3.setImageResource(R.drawable.ic_check_box_grey);
        ivDiningSlot1.setImageResource(R.drawable.ic_check_box_grey);
        ivDiningSlot2.setImageResource(R.drawable.ic_check_box_grey);
        ivDiningSlot3.setImageResource(R.drawable.ic_check_box_grey);
        ivBreakFast .setImageResource(R.drawable.ic_check_box_grey);
        ivLunch.setImageResource(R.drawable.ic_check_box_grey);
        ivDinner.setImageResource(R.drawable.ic_check_box_grey);
        ivWaterSlot1.setImageResource(R.drawable.ic_check_box_grey);
        ivWaterSlot2.setImageResource(R.drawable.ic_check_box_grey);
        ivWaterSlot3.setImageResource(R.drawable.ic_check_box_grey);
    }

    private void setTimeShot() {

        String time = AppUtils.getCurrentTime();

        Log.d("time-before",time);

        String[] parts = time.split(":");

        Log.d("time-after",parts[0]);

        int currentTime= Integer.parseInt(parts[0]);

        if(currentTime>=8&&currentTime<14)
        {
            slot=1;
            ivLoungeSlot1.setEnabled(true);
            ivToiletSlot1.setEnabled(true);
            ivDiningSlot1.setEnabled(true);
            ivBreakFast.setEnabled(true);
            ivWaterSlot1.setEnabled(true);

            ivLoungeSlot1.setImageResource(R.drawable.ic_check_box);
            ivToiletSlot1.setImageResource(R.drawable.ic_check_box);
            ivDiningSlot1.setImageResource(R.drawable.ic_check_box);
            ivBreakFast.setImageResource(R.drawable.ic_check_box);
            ivWaterSlot1.setImageResource(R.drawable.ic_check_box);
        }
        else if(currentTime>=14&&currentTime<20)
        {
            slot=2;
            ivLoungeSlot2.setEnabled(true);
            ivToiletSlot2.setEnabled(true);
            ivDiningSlot2.setEnabled(true);
            ivLunch.setEnabled(true);
            ivWaterSlot2.setEnabled(true);

            ivLoungeSlot2.setImageResource(R.drawable.ic_check_box);
            ivToiletSlot2.setImageResource(R.drawable.ic_check_box);
            ivDiningSlot2.setImageResource(R.drawable.ic_check_box);
            ivLunch.setImageResource(R.drawable.ic_check_box);
            ivWaterSlot2.setImageResource(R.drawable.ic_check_box);
        }
        else
        {
            slot=3;
            ivLoungeSlot3.setEnabled(true);
            ivToiletSlot3.setEnabled(true);
            ivDiningSlot3.setEnabled(true);
            ivDinner.setEnabled(true);
            ivWaterSlot3.setEnabled(true);

            ivLoungeSlot3.setImageResource(R.drawable.ic_check_box);
            ivToiletSlot3.setImageResource(R.drawable.ic_check_box);
            ivDiningSlot3.setImageResource(R.drawable.ic_check_box);
            ivDinner.setImageResource(R.drawable.ic_check_box);
            ivWaterSlot3.setImageResource(R.drawable.ic_check_box);
        }
    }

    private void setClickListeners() {

        rlNext.setOnClickListener(this);
        ivLoungeSlot1.setOnClickListener(this);
        ivLoungeSlot2.setOnClickListener(this);
        ivLoungeSlot3.setOnClickListener(this);
        ivToiletSlot1.setOnClickListener(this);
        ivToiletSlot2.setOnClickListener(this);
        ivToiletSlot3.setOnClickListener(this);
        ivDiningSlot1.setOnClickListener(this);
        ivDiningSlot2.setOnClickListener(this);
        ivDiningSlot3.setOnClickListener(this);
        ivBreakFast.setOnClickListener(this);
        ivLunch.setOnClickListener(this);
        ivDinner.setOnClickListener(this);
        ivWaterSlot1.setOnClickListener(this);
        ivWaterSlot2.setOnClickListener(this);
        ivWaterSlot3.setOnClickListener(this);
        rlBedSheet.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rlBedSheet:

                showConfirmationDialog("1",ivLoungeSlot1);

                break;

            case R.id.ivLoungeSlot1:

                showConfirmationDialog("2",ivLoungeSlot1);

                break;

            case R.id.ivLoungeSlot2:

                showConfirmationDialog("2",ivLoungeSlot2);

                break;

            case R.id.ivLoungeSlot3:

                showConfirmationDialog("2",ivLoungeSlot3);

                break;

            case R.id.ivToiletSlot1:

                showConfirmationDialog("3",ivToiletSlot1);

                break;

            case R.id.ivToiletSlot2:

                showConfirmationDialog("3",ivToiletSlot2);

                break;

            case R.id.ivToiletSlot3:

                showConfirmationDialog("3",ivToiletSlot3);

                break;

            case R.id.ivDiningSlot1:

                showConfirmationDialog("4",ivDiningSlot1);

                break;

            case R.id.ivDiningSlot2:

                showConfirmationDialog("4",ivDiningSlot2);

                break;

            case R.id.ivDiningSlot3:

                showConfirmationDialog("4",ivDiningSlot3);

                break;

            case R.id.ivBreakFast:

                showConfirmationDialog("5",ivBreakFast);

                break;

            case R.id.ivLunch:

                showConfirmationDialog("5",ivLunch);

                break;

            case R.id.ivDinner:

                showConfirmationDialog("5",ivDinner);

                break;

            case R.id.ivWaterSlot1:

                showConfirmationDialog("6",ivWaterSlot1);

                break;

            case R.id.ivWaterSlot2:

                showConfirmationDialog("6",ivWaterSlot2);

                break;

            case R.id.ivWaterSlot3:

                showConfirmationDialog("6",ivWaterSlot3);

                break;

            case R.id.rlNext:

                LoungeMonitoringFragment.displayView(2, mActivity);

                break;
        }

    }

    private void showConfirmationDialog(final String type,ImageView imageView) {

        Dialog dialog = new Dialog(mActivity,android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_yes_no_nurse);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        TextView tvText = dialog.findViewById(R.id.tvMessage);
        TextView tvCancel = dialog.findViewById(R.id.tvCancel);
        TextView tvOk = dialog.findViewById(R.id.tvOk);

        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlMain = dialog.findViewById(R.id.rlMain);

        tvCancel.setText(getString(R.string.no));
        tvOk.setText(getString(R.string.yes));

        Spinner spinnerEnteredByNurse = dialog.findViewById(R.id.spinnerEnteredByNurse);

        nurseId.clear();
        nurseId.add(getString(R.string.selectNurse));
        nurseId.addAll(DatabaseController.getNurseIdCheckedInData());

        nurseName.clear();
        nurseName.add(getString(R.string.selectNurse));
        nurseName.addAll(DatabaseController.getNurseNameCheckedInData());

        spinnerEnteredByNurse.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, nurseName));
        spinnerEnteredByNurse.setSelection(0);

        if (type.equals("1")) {
            tvText.setText(getString(R.string.areYouSureBedsheetChanged));
            AppSettings.putString(AppSettings.positionValue,type);
        }  else if (type.equals("2")) {
            tvText.setText(getString(R.string.youAreConfirmingLoungeClean));
            AppSettings.putString(AppSettings.positionValue,type);
        }  else if (type.equals("3")) {
            tvText.setText(getString(R.string.youAreConfirmingToiletClean));
            AppSettings.putString(AppSettings.positionValue,type);
        }else if (type.equals("4")) {
            tvText.setText(getString(R.string.youAreConfirmingDiningAreaClean));
            AppSettings.putString(AppSettings.positionValue,type);
        } else if (type.equals("5")) {
            tvText.setText(getString(R.string.areYouSureBreakfastProvided));
            AppSettings.putString(AppSettings.positionValue,type);
        } else if (type.equals("6")) {
            tvText.setText(getString(R.string.areYouSureWaterAvailable));
            AppSettings.putString(AppSettings.positionValue,type);
        }

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        rlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(spinnerEnteredByNurse.getSelectedItemPosition()==0)
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.selectNurse));
                }
                else {
                    if(!type.equalsIgnoreCase("1")) {
                        imageView.setImageResource(R.drawable.ic_cross_new);
                    }
                    save(nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()),getString(R.string.noValue),type);
                    dialog.dismiss();
                }
            }
        });

        rlOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(spinnerEnteredByNurse.getSelectedItemPosition()==0)
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.selectNurse));
                }
                else {
                    if(!type.equalsIgnoreCase("1")) {
                        imageView.setImageResource(R.drawable.ic_check_box_selected);
                    }
                    save(nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()),getString(R.string.yesValue),type);
                    dialog.dismiss();
                }
            }
        });

        rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
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

        private View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            View row=inflater.inflate(R.layout.inflate_spinner_new, parent, false);

            TextView tvName=row.findViewById(R.id.tvName);

            tvName.setText(data.get(position));

            return row;
        }
    }

    private void save(String nurseId,String value,String type) {

        uuid  = UUID.randomUUID().toString();

        ContentValues mContentValues = new ContentValues();

        mContentValues.put(TableLoungeServices.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        mContentValues.put(TableLoungeServices.tableColumn.latitude.toString(), AppSettings.getString(AppSettings.latitude));
        mContentValues.put(TableLoungeServices.tableColumn.longitude.toString(), AppSettings.getString(AppSettings.longitude));
        mContentValues.put(TableLoungeServices.tableColumn.uuid.toString(), uuid);
        mContentValues.put(TableLoungeServices.tableColumn.nurseId.toString(),nurseId);
        mContentValues.put(TableLoungeServices.tableColumn.value.toString(), value);
        mContentValues.put(TableLoungeServices.tableColumn.type.toString(), type);
        mContentValues.put(TableLoungeServices.tableColumn.slot.toString(), slot);
        mContentValues.put(TableLoungeServices.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
        mContentValues.put(TableLoungeServices.tableColumn.modifyDate.toString(),AppUtils.currentTimestampFormat());
        mContentValues.put(TableLoungeServices.tableColumn.syncTime.toString(),"0");
        mContentValues.put(TableLoungeServices.tableColumn.isDataSynced.toString(),"0");
        mContentValues.put(TableLoungeServices.tableColumn.status.toString(), "0");

        DatabaseController.insertUpdateData(mContentValues, TableLoungeServices.tableName,
                TableLoungeServices.tableColumn.uuid.toString(), uuid);

        SyncAllRecord.postLoungeService(mActivity);

        setValues();
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

        WebServices.postApi(mActivity, AppUrls.getLoungeServicesData, json,true,true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        JSONArray jsonArray = jsonObject.getJSONArray("loungeServices");


                        if(jsonArray.length()>0)
                        {
                            if(jsonArray.getJSONObject(0).getString("dailyBedsheetChange").equalsIgnoreCase(getString(R.string.yesValue)))
                            {
                                ivBedSheetResult.setImageResource(R.drawable.ic_happy_smily);
                                //ivBedSheetResult.setBackgroundResource(R.drawable.circle_teal);
                            }
                            else  if(jsonArray.getJSONObject(0).getString("dailyBedsheetChange").equalsIgnoreCase(getString(R.string.noValue)))
                            {
                                ivBedSheetResult.setImageResource(R.drawable.ic_sad_smily);
                                //ivBedSheetResult.setBackgroundResource(R.drawable.circle_red);
                            }

                            if(jsonArray.getJSONObject(0).getString("loungeSanitation").equalsIgnoreCase(getString(R.string.yesValue)))
                            {
                                check1=1;
                                ivLoungeSlot1.setImageResource(R.drawable.ic_check_box_selected);
                            }
                            else if(jsonArray.getJSONObject(0).getString("loungeSanitation").equalsIgnoreCase(getString(R.string.noValue)))
                            {
                                check1=2;
                                ivLoungeSlot1.setImageResource(R.drawable.ic_cross_new);
                            }

                            if(jsonArray.getJSONObject(0).getString("toiletSanitation").equalsIgnoreCase(getString(R.string.yesValue)))
                            {
                                check2=1;
                                ivToiletSlot1.setImageResource(R.drawable.ic_check_box_selected);
                            }
                            else if(jsonArray.getJSONObject(0).getString("toiletSanitation").equalsIgnoreCase(getString(R.string.noValue)))
                            {
                                check2=2;
                                ivToiletSlot1.setImageResource(R.drawable.ic_cross_new);
                            }

                            if(jsonArray.getJSONObject(0).getString("commonAreaSanitation").equalsIgnoreCase(getString(R.string.yesValue)))
                            {
                                check3=1;
                                ivDiningSlot1.setImageResource(R.drawable.ic_check_box_selected);
                            }
                            else  if(jsonArray.getJSONObject(0).getString("commonAreaSanitation").equalsIgnoreCase(getString(R.string.noValue)))
                            {
                                check3=2;
                                ivDiningSlot1.setImageResource(R.drawable.ic_cross_new);
                            }

                            if(jsonArray.getJSONObject(0).getString("mealsProvision").equalsIgnoreCase(getString(R.string.yesValue)))
                            {
                                ivBreakFast.setImageResource(R.drawable.ic_check_box_selected);

                                ivMealResult.setImageResource(R.drawable.ic_happy_smily);
                                //ivMealResult.setBackgroundResource(R.drawable.circle_teal);
                            }
                            else if(jsonArray.getJSONObject(0).getString("mealsProvision").equalsIgnoreCase(getString(R.string.noValue)))
                            {
                                ivBreakFast.setImageResource(R.drawable.ic_cross_new);

                                ivMealResult.setImageResource(R.drawable.ic_sad_smily);
                                //ivMealResult.setBackgroundResource(R.drawable.circle_red);
                            }

                            if(jsonArray.getJSONObject(0).getString("cleanWaterAvailability").equalsIgnoreCase(getString(R.string.yesValue)))
                            {
                                ivWaterSlot1.setImageResource(R.drawable.ic_check_box_selected);

                                ivDrinkingResult.setImageResource(R.drawable.ic_happy_smily);
                                //ivDrinkingResult.setBackgroundResource(R.drawable.circle_teal);
                            }
                            else if(jsonArray.getJSONObject(0).getString("cleanWaterAvailability").equalsIgnoreCase(getString(R.string.noValue)))
                            {
                                ivWaterSlot1.setImageResource(R.drawable.ic_cross_new);

                                ivDrinkingResult.setImageResource(R.drawable.ic_sad_smily);
                                //ivDrinkingResult.setBackgroundResource(R.drawable.circle_red);
                            }

                        }

                        if(jsonArray.length()>1)
                        {
                            if(jsonArray.getJSONObject(1).getString("loungeSanitation").equalsIgnoreCase(getString(R.string.yesValue)))
                            {
                                check1=1;
                                ivLoungeSlot2.setImageResource(R.drawable.ic_check_box_selected);
                            }
                            else if(jsonArray.getJSONObject(1).getString("loungeSanitation").equalsIgnoreCase(getString(R.string.noValue)))
                            {
                                check1=2;
                                ivLoungeSlot2.setImageResource(R.drawable.ic_cross_new);
                            }

                            if(jsonArray.getJSONObject(1).getString("toiletSanitation").equalsIgnoreCase(getString(R.string.yesValue)))
                            {
                                check2=1;
                                ivToiletSlot2.setImageResource(R.drawable.ic_check_box_selected);
                            }
                            else if(jsonArray.getJSONObject(1).getString("toiletSanitation").equalsIgnoreCase(getString(R.string.noValue)))
                            {
                                check2=2;
                                ivToiletSlot2.setImageResource(R.drawable.ic_cross_new);
                            }

                            if(jsonArray.getJSONObject(1).getString("commonAreaSanitation").equalsIgnoreCase(getString(R.string.yesValue)))
                            {
                                check3=1;
                                ivDiningSlot2.setImageResource(R.drawable.ic_check_box_selected);
                            }
                            else  if(jsonArray.getJSONObject(1).getString("commonAreaSanitation").equalsIgnoreCase(getString(R.string.noValue)))
                            {
                                check3=2;
                                ivDiningSlot2.setImageResource(R.drawable.ic_cross_new);
                            }

                            if(jsonArray.getJSONObject(1).getString("mealsProvision").equalsIgnoreCase(getString(R.string.yesValue)))
                            {
                                ivLunch.setImageResource(R.drawable.ic_check_box_selected);

                                ivMealResult.setImageResource(R.drawable.ic_happy_smily);
                                //ivMealResult.setBackgroundResource(R.drawable.circle_teal);
                            }
                            else if(jsonArray.getJSONObject(1).getString("mealsProvision").equalsIgnoreCase(getString(R.string.noValue)))
                            {
                                ivLunch.setImageResource(R.drawable.ic_cross_new);

                                ivMealResult.setImageResource(R.drawable.ic_sad_smily);
                                //ivMealResult.setBackgroundResource(R.drawable.circle_red);
                            }

                            if(jsonArray.getJSONObject(1).getString("cleanWaterAvailability").equalsIgnoreCase(getString(R.string.yesValue)))
                            {
                                ivWaterSlot2.setImageResource(R.drawable.ic_check_box_selected);

                                ivDrinkingResult.setImageResource(R.drawable.ic_happy_smily);
                                //ivDrinkingResult.setBackgroundResource(R.drawable.circle_teal);
                            }
                            else if(jsonArray.getJSONObject(1).getString("cleanWaterAvailability").equalsIgnoreCase(getString(R.string.noValue)))
                            {
                                ivWaterSlot2.setImageResource(R.drawable.ic_cross_new);

                                ivDrinkingResult.setImageResource(R.drawable.ic_sad_smily);
                                //ivDrinkingResult.setBackgroundResource(R.drawable.circle_red);
                            }

                        }

                        if(jsonArray.length()>2)
                        {
                            if(jsonArray.getJSONObject(2).getString("loungeSanitation").equalsIgnoreCase(getString(R.string.yesValue)))
                            {
                                check1=2;
                                ivLoungeSlot3.setImageResource(R.drawable.ic_check_box_selected);
                            }
                            else if(jsonArray.getJSONObject(2).getString("loungeSanitation").equalsIgnoreCase(getString(R.string.noValue)))
                            {
                                check1=2;
                                ivLoungeSlot3.setImageResource(R.drawable.ic_cross_new);
                            }

                            if(jsonArray.getJSONObject(2).getString("toiletSanitation").equalsIgnoreCase(getString(R.string.yesValue)))
                            {
                                check2=1;
                                ivToiletSlot3.setImageResource(R.drawable.ic_check_box_selected);
                            }
                            else if(jsonArray.getJSONObject(2).getString("toiletSanitation").equalsIgnoreCase(getString(R.string.noValue)))
                            {
                                check2=2;
                                ivToiletSlot3.setImageResource(R.drawable.ic_cross_new);
                            }

                            if(jsonArray.getJSONObject(2).getString("commonAreaSanitation").equalsIgnoreCase(getString(R.string.yesValue)))
                            {
                                check3=1;
                                ivDiningSlot3.setImageResource(R.drawable.ic_check_box_selected);
                            }
                            else  if(jsonArray.getJSONObject(2).getString("commonAreaSanitation").equalsIgnoreCase(getString(R.string.noValue)))
                            {
                                check3=2;
                                ivDiningSlot3.setImageResource(R.drawable.ic_cross_new);
                            }

                            if(jsonArray.getJSONObject(2).getString("mealsProvision").equalsIgnoreCase(getString(R.string.yesValue)))
                            {
                                ivDinner.setImageResource(R.drawable.ic_check_box_selected);

                                ivMealResult.setImageResource(R.drawable.ic_happy_smily);
                                //ivMealResult.setBackgroundResource(R.drawable.circle_teal);
                            }
                            else if(jsonArray.getJSONObject(2).getString("mealsProvision").equalsIgnoreCase(getString(R.string.noValue)))
                            {
                                ivDinner.setImageResource(R.drawable.ic_cross_new);

                                ivMealResult.setImageResource(R.drawable.ic_sad_smily);
                                //ivMealResult.setBackgroundResource(R.drawable.circle_red);
                            }

                            if(jsonArray.getJSONObject(2).getString("cleanWaterAvailability").equalsIgnoreCase(getString(R.string.yesValue)))
                            {
                                ivWaterSlot3.setImageResource(R.drawable.ic_check_box_selected);

                                ivDrinkingResult.setImageResource(R.drawable.ic_happy_smily);
                                //ivDrinkingResult.setBackgroundResource(R.drawable.circle_teal);
                            }
                            else if(jsonArray.getJSONObject(2).getString("cleanWaterAvailability").equalsIgnoreCase(getString(R.string.noValue)))
                            {
                                ivWaterSlot3.setImageResource(R.drawable.ic_cross_new);

                                ivDrinkingResult.setImageResource(R.drawable.ic_sad_smily);
                                //ivDrinkingResult.setBackgroundResource(R.drawable.circle_red);
                            }

                        }


                        if(check1 == 1 && check2 == 1 && check3==1)
                        {
                            ivSanitationResult.setImageResource(R.drawable.ic_happy_smily);
                            //ivSanitationResult.setBackgroundResource(R.drawable.circle_teal);
                        }
                        else  if(check1 == 2 || check2 == 2 || check3==2 )
                        {
                            ivSanitationResult.setImageResource(R.drawable.ic_sad_smily);
                            //ivSanitationResult.setBackgroundResource(R.drawable.circle_red);
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
}
