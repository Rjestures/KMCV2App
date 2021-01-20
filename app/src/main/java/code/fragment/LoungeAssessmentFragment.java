package code.fragment;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.kmcapp.android.BuildConfig;
import com.kmcapp.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import code.algo.SyncAllRecord;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableLoungeAssessment;
import code.main.MainActivity;
import code.utils.AppConstants;
import code.utils.AppUtils;
import code.view.BaseFragment;

import static android.app.Activity.RESULT_OK;


public class LoungeAssessmentFragment extends BaseFragment implements View.OnClickListener {

    //Static Int
    private static final int CameraCode = 100, MediaType = 1;
    //int ivChekBox // 1= checked, 2=unchecked;
    int permissionCb = 2, thermoMeterCb = 2, viewPosition = 1, safety = 0, toilet = 0, washroom = 0, commonArea = 0;
    //Imagview
    private ImageView ivCheckbox, ivPhoto, ivCbThermoMeter, ivNext, ivLoungePic, ivTempResult, ivSafetyResult,
            ivToiletResult, ivWashroomResult, ivCommonResult, ivNoPads, ivShoes, ivNoOneInside, ivHandSanitizers, ivGeneral, ivPPE,
            ivToiletsClean, ivToiletsTap, ivToiletFloor, ivHand, ivDustbin,
            ivWashroom, ivCleanMug, ivRunningWater, ivSoap, ivWashroomDustbin,
            ivCommon, ivArea, ivAreaDustbin;
    //Linearlayout
    private LinearLayout llCbTakenPermission, llCbTickThermometer, llStep1, llStep2, llStep3, llStep4, llStep5, llStep6, llStep7;
    //RelativeLayout
    private RelativeLayout rlUnhappySafety, rlHappySafety, rlUnhappyToilet, rlHappyToilet,
            rlUnhappyWashroom, rlHappyWashroom, rlUnhappyCommon, rlHappyCommon,
            rlNext, rlPrevious, rlPhoto, rlCircle, rlTemp;
    //TextView
    private TextView tvUnhappySafety, tvHappySafety, tvUnhappyToilet, tvHappyToilet,
            tvUnhappyWashroom, tvHappyWashroom, tvUnhappyCommon, tvHappyCommon, tvSubmit,
            tvTempResult, tvLoungeResult, tvSurveyResult, tvWashResult, tvCommonResult;
    //String
    private String picturePath = "", encodedString = "",
            noPad = "", shoes = "", noOneInside = "", handSanitizers = "", general = "", ppe = "",
            toiletsClean = "", toiletsTap = "", toiletFloor = "", hand = "", dustbin = "",
            washroomSurvey = "", cleanMug = "", runningWater = "", soap = "", washroomDustbin = "",
            common = "", area = "", areaDustbin = "";
    //Uri
    private Uri fileUri;
    //Bitmap
    private Bitmap bitmap;

    //EditText
    private EditText etRoomTemp;

    private String uuid = UUID.randomUUID().toString();
    //Spinner
    private Spinner spinnerEnteredByNurse;

    private ArrayList<String> nurseId = new ArrayList<String>();
    private ArrayList<String> nurseName = new ArrayList<String>();

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), AppConstants.projectName);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MediaType) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + AppConstants.projectName + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    public static int getImageOrientation(String imagePath) {
        int rotate = 0;
        try {

            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }

//    @Override
//    public boolean onBackPressed() {
//        displayView(0,  mActivity);
//
//        return true;
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lounge_assessment, container, false);

        initialize(view);

        setListeners();

        return view;
    }

    private void initialize(View v) {

        //ImageView
        ivCheckbox = v.findViewById(R.id.ivCheckbox);
        ivPhoto = v.findViewById(R.id.ivPhoto);
        ivCbThermoMeter = v.findViewById(R.id.ivCbThermoMeter);
        ivNext = v.findViewById(R.id.ivNext);
        ivLoungePic = v.findViewById(R.id.ivLoungePic);
        ivTempResult = v.findViewById(R.id.ivTempResult);
        ivSafetyResult = v.findViewById(R.id.ivSafetyResult);
        ivToiletResult = v.findViewById(R.id.ivToiletResult);
        ivWashroomResult = v.findViewById(R.id.ivWashroomResult);
        ivCommonResult = v.findViewById(R.id.ivCommonResult);
        ivNoPads = v.findViewById(R.id.ivNoPads);
        ivShoes = v.findViewById(R.id.ivShoes);
        ivNoOneInside = v.findViewById(R.id.ivNoOneInside);
        ivHandSanitizers = v.findViewById(R.id.ivHandSanitizers);
        ivGeneral = v.findViewById(R.id.ivGeneral);
        ivPPE = v.findViewById(R.id.ivPPE);
        ivToiletsClean = v.findViewById(R.id.ivToiletsClean);
        ivToiletsTap = v.findViewById(R.id.ivToiletsTap);
        ivToiletFloor = v.findViewById(R.id.ivToiletFloor);
        ivHand = v.findViewById(R.id.ivHand);
        ivDustbin = v.findViewById(R.id.ivDustbin);
        ivWashroom = v.findViewById(R.id.ivWashroom);
        ivCleanMug = v.findViewById(R.id.ivCleanMug);
        ivRunningWater = v.findViewById(R.id.ivRunningWater);
        ivSoap = v.findViewById(R.id.ivSoap);
        ivWashroomDustbin = v.findViewById(R.id.ivWashroomDustbin);
        ivCommon = v.findViewById(R.id.ivCommon);
        ivArea = v.findViewById(R.id.ivArea);
        ivAreaDustbin = v.findViewById(R.id.ivAreaDustbin);

        //LinearLayout
        llCbTakenPermission = v.findViewById(R.id.llCbTakenPermission);
        llCbTickThermometer = v.findViewById(R.id.llCbTickThermometer);
        llStep1 = v.findViewById(R.id.llStep1);
        llStep2 = v.findViewById(R.id.llStep2);
        llStep3 = v.findViewById(R.id.llStep3);
        llStep4 = v.findViewById(R.id.llStep4);
        llStep5 = v.findViewById(R.id.llStep5);
        llStep6 = v.findViewById(R.id.llStep6);
        llStep7 = v.findViewById(R.id.llStep7);

        //RelativeLayout
        rlUnhappySafety = v.findViewById(R.id.rlUnhappySafety);
        rlHappySafety = v.findViewById(R.id.rlHappySafety);
        rlUnhappyToilet = v.findViewById(R.id.rlUnhappyToilet);
        rlHappyToilet = v.findViewById(R.id.rlHappyToilet);
        rlUnhappyWashroom = v.findViewById(R.id.rlUnhappyWashroom);
        rlHappyWashroom = v.findViewById(R.id.rlHappyWashroom);
        rlUnhappyCommon = v.findViewById(R.id.rlUnhappyCommon);
        rlHappyCommon = v.findViewById(R.id.rlHappyCommon);
        rlNext = v.findViewById(R.id.rlNext);
        rlPrevious = v.findViewById(R.id.rlPrevious);
        rlPhoto = v.findViewById(R.id.rlPhoto);
        rlCircle = v.findViewById(R.id.rlCircle);
        rlTemp = v.findViewById(R.id.rlTemp);

        //Spinner
        spinnerEnteredByNurse = v.findViewById(R.id.spinnerEnteredByNurse);

        //EditText
        etRoomTemp = v.findViewById(R.id.etRoomTemp);

        //TextView
        tvUnhappySafety = v.findViewById(R.id.tvUnhappySafety);
        tvHappySafety = v.findViewById(R.id.tvHappySafety);
        tvUnhappyToilet = v.findViewById(R.id.tvUnhappyToilet);
        tvHappyToilet = v.findViewById(R.id.tvHappyToilet);
        tvUnhappyWashroom = v.findViewById(R.id.tvUnhappyWashroom);
        tvHappyWashroom = v.findViewById(R.id.tvHappyWashroom);
        tvUnhappyCommon = v.findViewById(R.id.tvUnhappyCommon);
        tvHappyCommon = v.findViewById(R.id.tvHappyCommon);
        tvSubmit = v.findViewById(R.id.tvSubmit);
        tvTempResult = v.findViewById(R.id.tvTempResult);
        tvLoungeResult = v.findViewById(R.id.tvLoungeResult);
        tvSurveyResult = v.findViewById(R.id.tvSurveyResult);
        tvWashResult = v.findViewById(R.id.tvWashResult);
        tvCommonResult = v.findViewById(R.id.tvCommonResult);

        nurseId.clear();
        nurseId.add(getString(R.string.selectNurse));
        nurseId.addAll(DatabaseController.getNurseIdCheckedInData());

        nurseName.clear();
        nurseName.add(getString(R.string.selectNurse));
        nurseName.addAll(DatabaseController.getNurseNameCheckedInData());

        spinnerEnteredByNurse.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, nurseName));
        spinnerEnteredByNurse.setSelection(0);


    }

    private void setListeners() {

        //LinearLayout
        llCbTakenPermission.setOnClickListener(this);
        llCbTickThermometer.setOnClickListener(this);

        //RelativeLayout
        //rlUnhappySafety.setOnClickListener(this);
        //rlHappySafety.setOnClickListener(this);
        rlUnhappyToilet.setOnClickListener(this);
        rlHappyToilet.setOnClickListener(this);
        rlUnhappyWashroom.setOnClickListener(this);
        rlHappyWashroom.setOnClickListener(this);
        rlUnhappyCommon.setOnClickListener(this);
        rlHappyCommon.setOnClickListener(this);
        rlNext.setOnClickListener(this);
        rlPrevious.setOnClickListener(this);
        rlPhoto.setOnClickListener(this);
        rlTemp.setOnClickListener(this);

        //ImageView
        ivPhoto.setOnClickListener(this);
        ivNoPads.setOnClickListener(this);
        ivShoes.setOnClickListener(this);
        ivNoOneInside.setOnClickListener(this);
        ivHandSanitizers.setOnClickListener(this);
        ivGeneral.setOnClickListener(this);
        ivPPE.setOnClickListener(this);
        ivToiletsClean.setOnClickListener(this);
        ivToiletsTap.setOnClickListener(this);
        ivToiletFloor.setOnClickListener(this);
        ivHand.setOnClickListener(this);
        ivDustbin.setOnClickListener(this);
        ivWashroom.setOnClickListener(this);
        ivCleanMug.setOnClickListener(this);
        ivRunningWater.setOnClickListener(this);
        ivSoap.setOnClickListener(this);
        ivWashroomDustbin.setOnClickListener(this);
        ivCommon.setOnClickListener(this);
        ivArea.setOnClickListener(this);
        ivAreaDustbin.setOnClickListener(this);

        //TextView
        tvSubmit.setOnClickListener(this);


        etRoomTemp.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    rlCircle.setBackgroundResource(R.drawable.circle_teal);
                } else {
                    rlCircle.setBackgroundResource(R.drawable.circle_grey);
                }

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llCbTakenPermission:

                if (permissionCb == 2) {
                    ivCheckbox.setImageResource(R.drawable.ic_check_box_selected);
                    ivCheckbox.setColorFilter(getResources().getColor(R.color.r_color), PorterDuff.Mode.SRC_IN);
                    permissionCb = 1;
                }
//                else {
//                    ivCheckbox.setImageResource(R.drawable.ic_check_box);
//                    ivCheckbox.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
//                    permissionCb = 2;
//                }
                break;

            case R.id.llCbTickThermometer:

                if (thermoMeterCb == 2) {
                    ivCbThermoMeter.setImageResource(R.drawable.ic_check_box_selected);
                    ivCbThermoMeter.setColorFilter(getResources().getColor(R.color.r_color), PorterDuff.Mode.SRC_IN);
                    thermoMeterCb = 1;
                    rlTemp.setEnabled(false);
                    etRoomTemp.setEnabled(false);
                    etRoomTemp.setText("");
                    ivTempResult.setImageResource(R.drawable.ic_sad_smily);
                    tvTempResult.setText(getString(R.string.loungeThermoNotWorking));
                    rlCircle.setBackgroundResource(R.drawable.circle_teal);




                } else {
                    ivCbThermoMeter.setImageResource(R.drawable.ic_check_box);
                    ivCbThermoMeter.setColorFilter(getResources().getColor(R.color.r_color), PorterDuff.Mode.SRC_IN);
                    thermoMeterCb = 2;
                    rlTemp.setEnabled(true);
                    etRoomTemp.setEnabled(true);
                    ivTempResult.setImageResource(R.drawable.ic_happy_smily);
                    tvTempResult.setText(getString(R.string.tempIsGood));

                    if (!etRoomTemp.getText().toString().equalsIgnoreCase("") ) {
                        rlCircle.setBackgroundResource(R.drawable.circle_teal);
                    } else {
                        rlCircle.setBackgroundResource(R.drawable.circle_grey);
                    }


                }

                break;

            case R.id.rlPrevious:

                hideAllView();
                makePrevAction();

                break;

            case R.id.rlNext:

                float temp = 0;

                try {
                    temp = Float.parseFloat(etRoomTemp.getText().toString().trim());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    temp = 0;
                }

                if (viewPosition == 1 && permissionCb == 2) {
                    AppUtils.showToastSort(mActivity, getString(R.string.takePermission));
                } else if (viewPosition == 1 && encodedString.isEmpty()) {
                    AppUtils.showToastSort(mActivity, getString(R.string.pleaseClickLounge));
                } else if (viewPosition == 2 && thermoMeterCb == 2 && etRoomTemp.getText().toString().trim().isEmpty()) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorEnterRoomTemperature));
                } else if (viewPosition == 2 && thermoMeterCb == 2 && temp < 27) {
                    AlertRoomTemp(1);
                } else if (viewPosition == 2 && thermoMeterCb == 2 && temp > 30) {
                    AlertRoomTemp(2);
                } else if (viewPosition == 2 && thermoMeterCb == 2 && (temp <= 30 && temp >= 27)) {
                    AlertRoomTemp(3);
                } else if (viewPosition == 2 && thermoMeterCb == 1) {
                    AlertRoomTemp(4);
                } else if (viewPosition == 3 && safety == 0) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectLoungeSafety));
                } else if (viewPosition == 4 && toilet == 0) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectLoungeToilet));
                } else if (viewPosition == 5 && washroom == 0) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectLoungeWashroom));
                } else if (viewPosition == 6 && commonArea == 0) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectLoungeCommon));
                } else if (viewPosition == 7 && spinnerEnteredByNurse.getSelectedItemPosition() == 0) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectYourName));
                } else if (viewPosition == 7) {
                    saveLoungeAssessment();
                } else {
                    hideAllView();
                    makeNextAction();
                }

                break;

            case R.id.ivPhoto:
                if (viewPosition == 1 && permissionCb == 2) {
                    AppUtils.showToastSort(mActivity, getString(R.string.takePermission));
                }
//                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
//                {
//                    fileUri = FileProvider.getUriForFile(mActiv
//                    ity, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(MediaType));
//
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
//                    intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
//                    intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//                    startActivityForResult(intent, CameraCode);
//                }
//                else
//                {
//                    // create Intent to take a picture and return control to the calling application
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    fileUri = getOutputMediaFileUri(MediaType); // create a file to save the image
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
//                    intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
//                    // start the image capture Intent
//                    startActivityForResult(intent, CameraCode);
//
//                }
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    fileUri = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(MediaType));
                    Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    it.putExtra("android.intent.extras.CAMERA_FACING", 0);
                    it.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(it, CameraCode);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri(MediaType); // create a file to save the image
                    intent.putExtra("android.intent.extras.CAMERA_FACING", 0);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                    startActivityForResult(intent, CameraCode);
                }

                break;

            case R.id.ivNoPads:

                showConfirmationDialog(1, ivNoPads, getString(R.string.noPadsFilthLying));

                break;

            case R.id.ivShoes:

                showConfirmationDialog(2, ivShoes, getString(R.string.outsideShoesAreNotWorn));

                break;

            case R.id.ivNoOneInside:

                showConfirmationDialog(3, ivNoOneInside, getString(R.string.thereIsNoOneInside));

                break;

            case R.id.ivHandSanitizers:

                showConfirmationDialog(4, ivHandSanitizers, getString(R.string.handSanitizersAvailable));

                break;

            case R.id.ivGeneral:

                showConfirmationDialog(5, ivGeneral, getString(R.string.generalCleanliness));

                break;

            case R.id.ivPPE:

                showConfirmationDialog(19, ivPPE, getString(R.string.ppeIsAvailable));

                break;

            case R.id.ivToiletsClean:

                showConfirmationDialog(6, ivToiletsClean, getString(R.string.toiletIsClean));

                break;

            case R.id.ivToiletsTap:

                showConfirmationDialog(7, ivToiletsTap, getString(R.string.toiletTapRunningWater));

                break;

            case R.id.ivToiletFloor:

                showConfirmationDialog(8, ivToiletFloor, getString(R.string.toiletFloorClean));

                break;

            case R.id.ivHand:

                showConfirmationDialog(9, ivHand, getString(R.string.handwashingSoupAvail));

                break;

            case R.id.ivDustbin:

                showConfirmationDialog(10, ivDustbin, getString(R.string.dustbinIsPresent));

                break;

            case R.id.ivWashroom:

                showConfirmationDialog(11, ivWashroom, getString(R.string.washroomIsClean));

                break;

            case R.id.ivCleanMug:

                showConfirmationDialog(12, ivCleanMug, getString(R.string.cleanMugBucketArePresent));

                break;

            case R.id.ivRunningWater:

                showConfirmationDialog(13, ivRunningWater, getString(R.string.runningWaterInTap));

                break;

            case R.id.ivSoap:

                showConfirmationDialog(14, ivSoap, getString(R.string.soapIsAvailable));

                break;

            case R.id.ivWashroomDustbin:

                showConfirmationDialog(15, ivWashroomDustbin, getString(R.string.dustbinIsPresent));

                break;

            case R.id.ivCommon:

                showConfirmationDialog(16, ivCommon, getString(R.string.commonAreaIsClean));

                break;

            case R.id.ivArea:

                showConfirmationDialog(17, ivArea, getString(R.string.areaForWashing));

                break;

            case R.id.ivAreaDustbin:

                showConfirmationDialog(18, ivAreaDustbin, getString(R.string.dustbinArePresentOnLocation));

                break;

            default:

                break;

        }

    }

    private void hideAllView() {

        llStep1.setVisibility(View.GONE);
        llStep2.setVisibility(View.GONE);
        llStep3.setVisibility(View.GONE);
        llStep4.setVisibility(View.GONE);
        llStep5.setVisibility(View.GONE);
        llStep6.setVisibility(View.GONE);
        llStep7.setVisibility(View.GONE);
        tvSubmit.setVisibility(View.GONE);
    }

    private void makeNextAction() {

        ivNext.setImageResource(R.drawable.ic_next);

        if (viewPosition == 1) {
            rlCircle.setBackgroundResource(R.drawable.circle_grey);
            llStep2.setVisibility(View.VISIBLE);

            if (!etRoomTemp.getText().toString().equalsIgnoreCase("") || thermoMeterCb == 1) {
                rlCircle.setBackgroundResource(R.drawable.circle_teal);
            } else {
                rlCircle.setBackgroundResource(R.drawable.circle_grey);
            }

        } else if (viewPosition == 2) {
            checkSafety();
            llStep3.setVisibility(View.VISIBLE);
        } else if (viewPosition == 3) {
            checkToilets();
            llStep4.setVisibility(View.VISIBLE);
        } else if (viewPosition == 4) {
            checkWashroom();
            llStep5.setVisibility(View.VISIBLE);
        } else if (viewPosition == 5) {
            checkCommon();
            llStep6.setVisibility(View.VISIBLE);
        } else if (viewPosition == 6) {
            llStep7.setVisibility(View.VISIBLE);
            ivNext.setImageResource(R.drawable.ic_tick);
        }

        ivNext.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        viewPosition = viewPosition + 1;
        rlPrevious.setVisibility(View.VISIBLE);
    }

    private void makePrevAction() {

        if (viewPosition == 2) {
            llStep1.setVisibility(View.VISIBLE);
            rlPrevious.setVisibility(View.INVISIBLE);
            if (!encodedString.equalsIgnoreCase("")) {
                rlCircle.setBackgroundResource(R.drawable.circle_teal);
            } else {
                rlCircle.setBackgroundResource(R.drawable.circle_grey);
            }
        } else if (viewPosition == 3) {

            if (!etRoomTemp.getText().toString().equalsIgnoreCase("") || thermoMeterCb == 1 ) {
                rlCircle.setBackgroundResource(R.drawable.circle_teal);
            } else {
                rlCircle.setBackgroundResource(R.drawable.circle_grey);
            }
            llStep2.setVisibility(View.VISIBLE);
        } else if (viewPosition == 4) {
            checkSafety();
            llStep3.setVisibility(View.VISIBLE);
        } else if (viewPosition == 5) {
            checkToilets();
            llStep4.setVisibility(View.VISIBLE);
        } else if (viewPosition == 6) {
            checkWashroom();
            llStep5.setVisibility(View.VISIBLE);
        } else if (viewPosition == 7) {
            checkCommon();
            llStep6.setVisibility(View.VISIBLE);
        }

        rlNext.setVisibility(View.VISIBLE);
        viewPosition = viewPosition - 1;

    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {

                picturePath = fileUri.getPath().toString();

                String selectedImagePath = picturePath;

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 500;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFile(selectedImagePath, options);

                Matrix matrix = new Matrix();
                matrix.postRotate(getImageOrientation(picturePath));
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 30, bao);

                rotatedBitmap = AppUtils.setWaterMark(rotatedBitmap, mActivity);

                ivPhoto.setImageBitmap(rotatedBitmap);
                ivLoungePic.setImageBitmap(rotatedBitmap);
                encodedString = getEncoded64ImageStringFromBitmap(rotatedBitmap);

                rlCircle.setBackgroundResource(R.drawable.circle_teal);
                rlPhoto.setBackgroundResource(0);

                File file = new File(picturePath);
                AppUtils.deleteDirectory(file);
            }
        }

    }

    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;
    }

    //Alert For Room Temperature
    private void AlertRoomTemp(int type) {
        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_image_ok);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //ImageView
        ImageView ivImage = dialog.findViewById(R.id.ivImage);

        //TextView
        TextView tvMessage = dialog.findViewById(R.id.tvMessage);

        //RelativeLayout
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        ivImage.setVisibility(View.VISIBLE);

        if (type == 1) {
            tvMessage.setText(getString(R.string.tooCold));

            ivImage.setImageResource(R.drawable.ic_cold_face);

            ivTempResult.setImageResource(R.drawable.ic_sad_smily);
            tvTempResult.setText(getString(R.string.tooCold));
        } else if (type == 2) {
            tvMessage.setText(getString(R.string.tooWarm));

            ivImage.setImageResource(R.drawable.ic_hot_face);

            ivTempResult.setImageResource(R.drawable.ic_sad_smily);
            tvTempResult.setText(getString(R.string.tooWarm));
        } else if (type == 3) {
            tvMessage.setText(getString(R.string.tempIsGood));

            ivImage.setImageResource(R.drawable.ic_happy_smily);

            ivTempResult.setImageResource(R.drawable.ic_happy_smily);
            tvTempResult.setText(getString(R.string.tempIsGood));
        } else if (type == 4) {
            tvMessage.setText(getString(R.string.loungeThermoNotWorking));

            ivImage.setImageResource(R.drawable.ic_warning);

            ivTempResult.setImageResource(R.drawable.ic_sad_smily);
            tvTempResult.setText(getString(R.string.loungeThermoNotWorking));
        }

        rlOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                hideAllView();
                makeNextAction();
                dialog.dismiss();

            }
        });
    }

    private void saveLoungeAssessment() {

        ContentValues mContentValues = new ContentValues();

        mContentValues.put(TableLoungeAssessment.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        mContentValues.put(TableLoungeAssessment.tableColumn.serverId.toString(), "");
        mContentValues.put(TableLoungeAssessment.tableColumn.uuid.toString(), uuid);
        mContentValues.put(TableLoungeAssessment.tableColumn.nurseId.toString(), nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()));
        mContentValues.put(TableLoungeAssessment.tableColumn.json.toString(), createJson().toString());
        mContentValues.put(TableLoungeAssessment.tableColumn.motherPermission.toString(), permissionCb);
        mContentValues.put(TableLoungeAssessment.tableColumn.loungePicture.toString(), encodedString);
        mContentValues.put(TableLoungeAssessment.tableColumn.latitude.toString(), AppSettings.getString(AppSettings.latitude));
        mContentValues.put(TableLoungeAssessment.tableColumn.longitude.toString(), AppSettings.getString(AppSettings.longitude));
        mContentValues.put(TableLoungeAssessment.tableColumn.loungeTemperature.toString(), etRoomTemp.getText().toString().trim());
        mContentValues.put(TableLoungeAssessment.tableColumn.loungeThermometerCondition.toString(), thermoMeterCb);
        mContentValues.put(TableLoungeAssessment.tableColumn.loungeSafety.toString(), safety);
        mContentValues.put(TableLoungeAssessment.tableColumn.toiletCondition.toString(), toilet);
        mContentValues.put(TableLoungeAssessment.tableColumn.washroomCondition.toString(), washroom);
        mContentValues.put(TableLoungeAssessment.tableColumn.commonAreaCondition.toString(), commonArea);
        mContentValues.put(TableLoungeAssessment.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
        mContentValues.put(TableLoungeAssessment.tableColumn.isDataSynced.toString(), "0");
        mContentValues.put(TableLoungeAssessment.tableColumn.status.toString(), "1");

        DatabaseController.insertUpdateData(mContentValues, TableLoungeAssessment.tableName,
                TableLoungeAssessment.tableColumn.uuid.toString(), uuid);

        if (AppUtils.isNetworkAvailable(mActivity)) {
            if (AppUtils.isNetworkAvailable(mActivity)) {
                AppSettings.putString(AppSettings.syncTime, AppUtils.currentTimestampFormat());
                SyncAllRecord.postDutyChange(mActivity);
            } else {
                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
            ((MainActivity) getActivity()).displayView(0);
        } else {
            AppUtils.showToastSort(mActivity, getString(R.string.dataSaved));
            ((MainActivity) getActivity()).displayView(0);
        }
    }

    private JSONObject createJson() {
        JSONObject jsonData = new JSONObject();

        try {

            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("nurseId", nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()));
            jsonData.put("loungePicture", encodedString);
            jsonData.put("localId", uuid);
            jsonData.put("localDateTime", AppUtils.currentTimestampFormat());
            jsonData.put("latitude", AppSettings.getString(AppSettings.latitude));
            jsonData.put("longitude", AppSettings.getString(AppSettings.longitude));
            jsonData.put("motherPermission", permissionCb);
            jsonData.put("loungeTemperature", etRoomTemp.getText().toString().trim());
            jsonData.put("loungeThermometerCondition", thermoMeterCb);
            jsonData.put("loungeSafety", safety);
            jsonData.put("toiletCondition", toilet);
            jsonData.put("washroomCondition", washroom);
            jsonData.put("commonAreaCondition", commonArea);
            jsonData.put("noFilthLyingAround", noPad);
            jsonData.put("noOutsideShoesWornInside", shoes);
            jsonData.put("noOneWithInfection", noOneInside);
            jsonData.put("sanitizerAvailability", handSanitizers);
            jsonData.put("cleanlinessMaintained", general);
            jsonData.put("toiletClean", toiletsClean);
            jsonData.put("runningWaterToilet", toiletsTap);
            jsonData.put("toiletFloorClean", toiletFloor);
            jsonData.put("handwashAvailability", hand);
            jsonData.put("dustinPresenceToilet", dustbin);
            jsonData.put("washroomClean", washroomSurvey);
            jsonData.put("cleanBucketPresent", cleanMug);
            jsonData.put("runningWaterWashroom", runningWater);
            jsonData.put("soapAvailability", soap);
            jsonData.put("dustinPresenceWashroom", washroomDustbin);
            jsonData.put("commonAreaClean", common);
            jsonData.put("washingAreaClean", area);
            jsonData.put("dustinPresenceCommonArea", areaDustbin);
            jsonData.put("ppeAvailable", ppe);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonData;
    }

    private void showConfirmationDialog(final int type, ImageView imageView, String text) {

        Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_yes_no);
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

        tvText.setText(text);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        rlOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageView.setImageResource(R.drawable.ic_check_box_selected);
                if (type == 1) {
                    noPad = getString(R.string.yesValue);
                    checkSafety();
                } else if (type == 2) {
                    shoes = getString(R.string.yesValue);
                    checkSafety();
                } else if (type == 3) {
                    noOneInside = getString(R.string.yesValue);
                    checkSafety();
                } else if (type == 4) {
                    handSanitizers = getString(R.string.yesValue);
                    checkSafety();
                } else if (type == 5) {
                    general = getString(R.string.yesValue);
                    checkSafety();
                } else if (type == 19) {
                    ppe = getString(R.string.yesValue);
                    checkSafety();
                }
                //For Toilets
                else if (type == 6) {
                    toiletsClean = getString(R.string.yesValue);
                    checkToilets();
                } else if (type == 7) {
                    toiletsTap = getString(R.string.yesValue);
                    checkToilets();
                } else if (type == 8) {
                    toiletFloor = getString(R.string.yesValue);
                    checkToilets();
                } else if (type == 9) {
                    hand = getString(R.string.yesValue);
                    checkToilets();
                } else if (type == 10) {
                    dustbin = getString(R.string.yesValue);
                    checkToilets();
                }
                //For Washroom
                else if (type == 11) {
                    washroomSurvey = getString(R.string.yesValue);
                    checkWashroom();
                } else if (type == 12) {
                    cleanMug = getString(R.string.yesValue);
                    checkWashroom();
                } else if (type == 13) {
                    runningWater = getString(R.string.yesValue);
                    checkWashroom();
                } else if (type == 14) {
                    soap = getString(R.string.yesValue);
                    checkWashroom();
                } else if (type == 15) {
                    washroomDustbin = getString(R.string.yesValue);
                    checkWashroom();
                }
                //For Common Area
                else if (type == 16) {
                    common = getString(R.string.yesValue);
                    checkCommon();
                } else if (type == 17) {
                    area = getString(R.string.yesValue);
                    checkCommon();
                } else if (type == 18) {
                    areaDustbin = getString(R.string.yesValue);
                    checkCommon();
                }

                dialog.dismiss();
            }
        });

        rlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageView.setImageResource(R.drawable.ic_cross_new);
                if (type == 1) {
                    noPad = getString(R.string.noValue);
                    checkSafety();
                } else if (type == 2) {
                    shoes = getString(R.string.noValue);
                    checkSafety();
                } else if (type == 3) {
                    noOneInside = getString(R.string.noValue);
                    checkSafety();
                } else if (type == 4) {
                    handSanitizers = getString(R.string.noValue);
                    checkSafety();
                } else if (type == 5) {
                    general = getString(R.string.noValue);
                    checkSafety();
                } else if (type == 19) {
                    ppe = getString(R.string.noValue);
                    checkSafety();
                }

                //For Toilets
                else if (type == 6) {
                    toiletsClean = getString(R.string.noValue);
                    checkToilets();
                } else if (type == 7) {
                    toiletsTap = getString(R.string.noValue);
                    checkToilets();
                } else if (type == 8) {
                    toiletFloor = getString(R.string.noValue);
                    checkToilets();
                } else if (type == 9) {
                    hand = getString(R.string.noValue);
                    checkToilets();
                } else if (type == 10) {
                    dustbin = getString(R.string.noValue);
                    checkToilets();
                }
                //For Washroom
                else if (type == 11) {
                    washroomSurvey = getString(R.string.noValue);
                    checkWashroom();
                } else if (type == 12) {
                    cleanMug = getString(R.string.noValue);
                    checkWashroom();
                } else if (type == 13) {
                    runningWater = getString(R.string.noValue);
                    checkWashroom();
                } else if (type == 14) {
                    soap = getString(R.string.noValue);
                    checkWashroom();
                } else if (type == 15) {
                    washroomDustbin = getString(R.string.noValue);
                    checkWashroom();
                }
                //For Common Area
                else if (type == 16) {
                    common = getString(R.string.noValue);
                    checkCommon();
                } else if (type == 17) {
                    area = getString(R.string.noValue);
                    checkCommon();
                } else if (type == 18) {
                    areaDustbin = getString(R.string.noValue);
                    checkCommon();
                }

                dialog.dismiss();
            }
        });

        rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

    }

    private void checkCommon() {

        if (common.isEmpty()
                || area.isEmpty()
                || areaDustbin.isEmpty()) {
            commonArea = 0;
            rlCircle.setBackgroundResource(R.drawable.circle_grey);
        } else if (common.equalsIgnoreCase(getString(R.string.yesValue))
                && area.equalsIgnoreCase(getString(R.string.yesValue))
                && areaDustbin.equalsIgnoreCase(getString(R.string.yesValue))) {
            commonArea = 1;
            tvHappyCommon.setTextColor(getResources().getColor(R.color.white));
            tvUnhappyCommon.setTextColor(getResources().getColor(R.color.blackNew));
            rlHappyCommon.setBackgroundResource(R.drawable.rectangle_teal_selected);
            rlUnhappyCommon.setBackgroundResource(R.drawable.rectangle_grey);
            ivCommonResult.setImageResource(R.drawable.ic_happy_smily);
            tvCommonResult.setText(getString(R.string.commonAreaWasMaintained));
            rlCircle.setBackgroundResource(R.drawable.circle_teal);
        } else if (common.equalsIgnoreCase(getString(R.string.noValue))
                || area.equalsIgnoreCase(getString(R.string.noValue))
                || areaDustbin.equalsIgnoreCase(getString(R.string.noValue))) {
            commonArea = 2;
            tvHappyCommon.setTextColor(getResources().getColor(R.color.blackNew));
            tvUnhappyCommon.setTextColor(getResources().getColor(R.color.white));
            rlHappyCommon.setBackgroundResource(R.drawable.rectangle_grey);
            rlUnhappyCommon.setBackgroundResource(R.drawable.rectangular_light_red);
            ivCommonResult.setImageResource(R.drawable.ic_sad_smily);
            tvCommonResult.setText(getString(R.string.youSurveyedCommonAreaMaintain));
            rlCircle.setBackgroundResource(R.drawable.circle_teal);
        }
    }

    private void checkWashroom() {

        if (washroomSurvey.isEmpty()
                || cleanMug.isEmpty()
                || runningWater.isEmpty()
                || soap.isEmpty()
                || washroomDustbin.isEmpty()) {
            washroom = 0;
            rlCircle.setBackgroundResource(R.drawable.circle_grey);
        } else if (washroomSurvey.equalsIgnoreCase(getString(R.string.yesValue))
                && cleanMug.equalsIgnoreCase(getString(R.string.yesValue))
                && runningWater.equalsIgnoreCase(getString(R.string.yesValue))
                && soap.equalsIgnoreCase(getString(R.string.yesValue))
                && washroomDustbin.equalsIgnoreCase(getString(R.string.yesValue))) {
            washroom = 1;
            tvHappyWashroom.setTextColor(getResources().getColor(R.color.white));
            tvUnhappyWashroom.setTextColor(getResources().getColor(R.color.blackNew));
            rlHappyWashroom.setBackgroundResource(R.drawable.rectangle_teal_selected);
            rlUnhappyWashroom.setBackgroundResource(R.drawable.rectangle_grey);
            ivWashroomResult.setImageResource(R.drawable.ic_happy_smily);
            tvWashResult.setText(getString(R.string.washroomWasMaintained));
            rlCircle.setBackgroundResource(R.drawable.circle_teal);
        } else if (washroomSurvey.equalsIgnoreCase(getString(R.string.noValue))
                || cleanMug.equalsIgnoreCase(getString(R.string.noValue))
                || runningWater.equalsIgnoreCase(getString(R.string.noValue))
                || soap.equalsIgnoreCase(getString(R.string.noValue))
                || washroomDustbin.equalsIgnoreCase(getString(R.string.noValue))) {
            washroom = 2;
            tvHappyWashroom.setTextColor(getResources().getColor(R.color.blackNew));
            tvUnhappyWashroom.setTextColor(getResources().getColor(R.color.white));
            rlHappyWashroom.setBackgroundResource(R.drawable.rectangle_grey);
            rlUnhappyWashroom.setBackgroundResource(R.drawable.rectangular_light_red);
            ivWashroomResult.setImageResource(R.drawable.ic_sad_smily);
            tvWashResult.setText(getString(R.string.youSurveyedWashroomNotMaintain));
            rlCircle.setBackgroundResource(R.drawable.circle_teal);
        }
    }

    private void checkToilets() {

        if (toiletsClean.isEmpty()
                || toiletsTap.isEmpty()
                || toiletFloor.isEmpty()
                || hand.isEmpty()
                || dustbin.isEmpty()) {
            toilet = 0;
            rlCircle.setBackgroundResource(R.drawable.circle_grey);
        } else if (toiletsClean.equalsIgnoreCase(getString(R.string.yesValue))
                && toiletsTap.equalsIgnoreCase(getString(R.string.yesValue))
                && toiletFloor.equalsIgnoreCase(getString(R.string.yesValue))
                && hand.equalsIgnoreCase(getString(R.string.yesValue))
                && dustbin.equalsIgnoreCase(getString(R.string.yesValue))) {
            toilet = 1;
            tvHappyToilet.setTextColor(getResources().getColor(R.color.white));
            tvUnhappyToilet.setTextColor(getResources().getColor(R.color.blackNew));
            rlHappyToilet.setBackgroundResource(R.drawable.rectangle_teal_selected);
            rlUnhappyToilet.setBackgroundResource(R.drawable.rectangle_grey);
            ivToiletResult.setImageResource(R.drawable.ic_happy_smily);
            tvSurveyResult.setText(getString(R.string.toiletWasInCondition));
            rlCircle.setBackgroundResource(R.drawable.circle_teal);
        } else if (toiletsClean.equalsIgnoreCase(getString(R.string.noValue))
                || toiletsTap.equalsIgnoreCase(getString(R.string.noValue))
                || toiletFloor.equalsIgnoreCase(getString(R.string.noValue))
                || hand.equalsIgnoreCase(getString(R.string.noValue))
                || dustbin.equalsIgnoreCase(getString(R.string.noValue))) {
            toilet = 2;
            tvHappyToilet.setTextColor(getResources().getColor(R.color.blackNew));
            tvUnhappyToilet.setTextColor(getResources().getColor(R.color.white));
            rlHappyToilet.setBackgroundResource(R.drawable.rectangle_grey);
            rlUnhappyToilet.setBackgroundResource(R.drawable.rectangular_light_red);
            ivToiletResult.setImageResource(R.drawable.ic_sad_smily);
            tvSurveyResult.setText(getString(R.string.youSurveyedToiletNotMaintain));
            rlCircle.setBackgroundResource(R.drawable.circle_teal);
        }
    }

    private void checkSafety() {

        if (noPad.isEmpty()
                || shoes.isEmpty()
                || noOneInside.isEmpty()
                || handSanitizers.isEmpty()
                || general.isEmpty()
                || ppe.isEmpty()) {
            safety = 0;

            rlCircle.setBackgroundResource(R.drawable.circle_grey);

        } else if (noPad.equalsIgnoreCase(getString(R.string.yesValue))
                && shoes.equalsIgnoreCase(getString(R.string.yesValue))
                && noOneInside.equalsIgnoreCase(getString(R.string.yesValue))
                && handSanitizers.equalsIgnoreCase(getString(R.string.yesValue))
                && general.equalsIgnoreCase(getString(R.string.yesValue))
                && ppe.equalsIgnoreCase(getString(R.string.yesValue))) {
            safety = 1;
            tvHappySafety.setTextColor(getResources().getColor(R.color.white));
            tvUnhappySafety.setTextColor(getResources().getColor(R.color.blackNew));
            rlHappySafety.setBackgroundResource(R.drawable.rectangle_light_teal_selected);
            rlUnhappySafety.setBackgroundResource(R.drawable.rectangle_grey);
            ivSafetyResult.setImageResource(R.drawable.ic_happy_smily);
            tvLoungeResult.setText(getString(R.string.yesMyLoungeWasSafe));
            rlCircle.setBackgroundResource(R.drawable.circle_teal);

        } else if (noPad.equalsIgnoreCase(getString(R.string.noValue))
                || shoes.equalsIgnoreCase(getString(R.string.noValue))
                || noOneInside.equalsIgnoreCase(getString(R.string.noValue))
                || handSanitizers.equalsIgnoreCase(getString(R.string.noValue))
                || general.equalsIgnoreCase(getString(R.string.noValue))
                || ppe.equalsIgnoreCase(getString(R.string.noValue))) {
            safety = 2;
            tvHappySafety.setTextColor(getResources().getColor(R.color.blackNew));
            tvUnhappySafety.setTextColor(getResources().getColor(R.color.white));
            rlHappySafety.setBackgroundResource(R.drawable.rectangle_grey);
            rlUnhappySafety.setBackgroundResource(R.drawable.rectangular_light_red);
            ivSafetyResult.setImageResource(R.drawable.ic_sad_smily);
            tvLoungeResult.setText(getString(R.string.youObservedLoungeFoundSafe));
            rlCircle.setBackgroundResource(R.drawable.circle_teal);
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


}
