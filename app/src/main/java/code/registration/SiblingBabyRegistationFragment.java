package code.registration;

import android.annotation.SuppressLint;
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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableBabyAdmission;
import code.database.TableBabyRegistration;
import code.database.TableUser;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;

import static android.app.Activity.RESULT_OK;


public class SiblingBabyRegistationFragment extends BaseFragment implements View.OnClickListener {

    private ArrayList<String> nurseListId = new ArrayList<String>();
    private ArrayList<String> nurseListName = new ArrayList<String>();

    private String type = "", reason = "";

    //LinearLayout
    private LinearLayout llMotherSection, llUnknown, llGuardian, llMother, llBaby;

    //RelativeLayout
    private RelativeLayout rlNext, rlPrevious, rlMotherPic;

    //EditText for Unknown Mothers
    private EditText etUMGN, etUMGNo, etOName, etONo, etOAddress;

    //EditText for Guardian
    private EditText etGuardianName, etGuardianNumber;

    //EditText for Known Mother
    private EditText etMotherName, etMotherNumber, etHospitalRegistration;

    //Spinner for relation
    private Spinner spinnerRelation;

    //ImageView
    private ImageView ivMotherPic;


    /*Baby Section*/
    private RelativeLayout rlAdmission, rlOutBornFrom, rlOutborn, rlInborn, rlMale, rlFemale, rlAmbiguous, rlTime, rlDate, rlLMPDate, rlBabyPic, rlMotherName, rlInBornFrom;

    //EditText
    private EditText etBabyBirthWeight, edReasionRelation, etOutBornFrom;

    //TextView
    private TextView tvLMPDateTitle, tvDeliveryTime, tvDeliveryDate, tvLmpDate, tvComingFrom;

    //ImageView
    private ImageView ivMale, ivFemale, ivAmbiguous, ivMalePic, ivFemalePic, ivAmbiguousPic, ivBabyPic;

    //Spinner
    private Spinner spinnerDeliveryType, spinnerAdmissionType, spinnerOutbornType, spinnerNurse, spinnerInbornType;

    private ArrayList<String> inbornTypeList = new ArrayList<String>();
    private ArrayList<String> inbornTypeValue = new ArrayList<String>();

    private String encodedBabyString = "";

    //Uri
    private Uri fileUri;

    //ArrayList
    private ArrayList<String> deliveryType = new ArrayList<String>();
    private ArrayList<String> deliveryTypeValue = new ArrayList<String>();
    private ArrayList<String> typeOfBorn = new ArrayList<String>();
    private ArrayList<String> typeOfBornValue = new ArrayList<String>();
    private ArrayList<String> typeOfOutBorn = new ArrayList<String>();
    private ArrayList<String> typeOfOutBornValue = new ArrayList<String>();
    private ArrayList<String> relationList = new ArrayList<String>();
    private ArrayList<String> relationValue = new ArrayList<String>();

    private String gender = "", typeBorn = "", typeOutBorn = "", delivery = "", nurseId = "", relation = "";

    private int from = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_mother_baby_regis, container, false);

        initialize(v);
        initMother(v);
        initBaby(v);

        RegistrationActivity.tvMotherAdmission.setBackgroundResource(R.drawable.rectangle_teal_selected);
        RegistrationActivity.tvMotherAdmission.setTextColor(getResources().getColor(R.color.white));

        llMotherSection.setVisibility(View.VISIBLE);
        AppUtils.enableDisable(rlMotherName, false);
        AppUtils.enableDisable(llUnknown, false);
        AppUtils.enableDisable(llGuardian, false);
        AppUtils.enableDisable(llMother, false);
        AppUtils.enableDisable(rlAdmission, false);
        AppUtils.enableDisable(rlOutborn, false);

        if (AppSettings.getString(AppSettings.from).equalsIgnoreCase("0")) {
            RegistrationActivity.uuid = UUID.randomUUID().toString();
            setValues();
        } else {
            setStep1and2Values();
        }


        return v;
    }


    private void initialize(View v) {

        //LinearLayout
        llMotherSection = v.findViewById(R.id.llMotherSection);
        llUnknown = v.findViewById(R.id.llUnknown);
        llGuardian = v.findViewById(R.id.llGuardian);
        llMother = v.findViewById(R.id.llMother);
        llBaby = v.findViewById(R.id.llBaby);

        //RelativeLayout
        rlNext = v.findViewById(R.id.rlNext);
        rlPrevious = v.findViewById(R.id.rlPrevious);

        rlNext.setOnClickListener(this);
        rlPrevious.setOnClickListener(this);
    }


    private void initMother(View v) {

        //EditText for Unknown Mother
        etUMGN = v.findViewById(R.id.etUMGN);
        etUMGNo = v.findViewById(R.id.etUMGNo);
        etOName = v.findViewById(R.id.etOName);
        etONo = v.findViewById(R.id.etONo);
        etOAddress = v.findViewById(R.id.etOAddress);

        //EditText for Guardian
        etGuardianName = v.findViewById(R.id.etGuardianName);
        etGuardianNumber = v.findViewById(R.id.etGuardianNumber);
        edReasionRelation = v.findViewById(R.id.edReasionRelation);
        etOutBornFrom = v.findViewById(R.id.etOutBornFrom);

        ////EditText for Known Mother
        etMotherName = v.findViewById(R.id.etMotherName);
        etMotherNumber = v.findViewById(R.id.etMotherNumber);
        etHospitalRegistration = v.findViewById(R.id.etHospitalRegistration);
        tvLMPDateTitle = v.findViewById(R.id.tvLMPDateTitle);

        //Spinner
        spinnerRelation = v.findViewById(R.id.spinnerRelation);

        //ImageView
        ivMotherPic = v.findViewById(R.id.ivMotherPic);

        //For Known Mother Pic
        rlMotherName = v.findViewById(R.id.rlMotherName);
        rlMotherPic = v.findViewById(R.id.rlMotherPic);
        rlInborn = v.findViewById(R.id.rlInborn);
        rlOutborn = v.findViewById(R.id.rlOutborn);
        rlOutBornFrom = v.findViewById(R.id.rlOutBornFrom);
        rlAdmission = v.findViewById(R.id.rlAdmission);

        relationList.clear();
        relationList.add(getString(R.string.relation));
        relationList.add(getString(R.string.father));
        relationList.add(getString(R.string.sister));
        relationList.add(getString(R.string.brother));
        relationList.add(getString(R.string.grandMother));
        relationList.add(getString(R.string.grandFather));
        relationList.add(getString(R.string.uncle));
        relationList.add(getString(R.string.aunty));
        relationList.add(getString(R.string.other));

        relationValue.clear();
        relationValue.add(getString(R.string.relation));
        relationValue.add(getString(R.string.fatherValue));
        relationValue.add(getString(R.string.sisterValue));
        relationValue.add(getString(R.string.brotherValue));
        relationValue.add(getString(R.string.grandMotherValue));
        relationValue.add(getString(R.string.grandFatherValue));
        relationValue.add(getString(R.string.uncleValue));
        relationValue.add(getString(R.string.auntyValue));
        relationValue.add(getString(R.string.otherValue));

        spinnerRelation.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, relationList));
        spinnerRelation.setSelection(0);

        //Spinner for relationList
        spinnerRelation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if (position == 0) {
                    relation = "";
                } else {
                    relation = relationValue.get(position);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        rlMotherPic.setOnClickListener(this);
    }


    private void initBaby(View v) {

        //EditText
        etBabyBirthWeight = v.findViewById(R.id.etBabyBirthWeight);

        //RelativeLayout
        rlMale = v.findViewById(R.id.rlMale);
        rlFemale = v.findViewById(R.id.rlFemale);
        rlAmbiguous = v.findViewById(R.id.rlAmbiguous);
        rlTime = v.findViewById(R.id.rlTime);
        rlDate = v.findViewById(R.id.rlDate);
        rlLMPDate = v.findViewById(R.id.rlLMPDate);
        rlBabyPic = v.findViewById(R.id.rlBabyPic);
        rlInborn = v.findViewById(R.id.rlInborn);
        rlInBornFrom = v.findViewById(R.id.rlInBornFrom);

        //TextView
        tvDeliveryTime = v.findViewById(R.id.tvDeliveryTime);
        tvDeliveryDate = v.findViewById(R.id.tvDeliveryDate);
        tvLmpDate = v.findViewById(R.id.tvLMPDate);
        tvComingFrom = v.findViewById(R.id.tvComingFrom);

        //Spinner
        spinnerDeliveryType = v.findViewById(R.id.spinnerDeliveryType);
        spinnerAdmissionType = v.findViewById(R.id.spinnerAdmissionType);
        spinnerOutbornType = v.findViewById(R.id.spinnerOutbornType);
        spinnerInbornType = v.findViewById(R.id.spinnerInbornType);
        spinnerNurse = v.findViewById(R.id.spinnerNurse);

        //ImageView
        ivMale = v.findViewById(R.id.ivMale);
        ivFemale = v.findViewById(R.id.ivFemale);
        ivAmbiguous = v.findViewById(R.id.ivAmbiguous);
        ivMalePic = v.findViewById(R.id.ivMalePic);
        ivFemalePic = v.findViewById(R.id.ivFemalePic);
        ivAmbiguousPic = v.findViewById(R.id.ivAmbiguousPic);
        ivBabyPic = v.findViewById(R.id.ivBabyPic);

        //RelatievLayout
        rlNext.setVisibility(View.VISIBLE);

        deliveryType.clear();
        deliveryType.add(getString(R.string.selectDeliveryType));
        deliveryType.add(getString(R.string.deliveryType1));
        deliveryType.add(getString(R.string.deliveryType2));
        deliveryType.add(getString(R.string.deliveryType3));
        deliveryType.add(getString(R.string.deliveryType4));
        deliveryType.add(getString(R.string.deliveryType5));

        deliveryTypeValue.clear();
        deliveryTypeValue.add(getString(R.string.selectDeliveryType));
        deliveryTypeValue.add(getString(R.string.deliveryType1Value));
        deliveryTypeValue.add(getString(R.string.deliveryType2Value));
        deliveryTypeValue.add(getString(R.string.deliveryType3Value));
        deliveryTypeValue.add(getString(R.string.deliveryType4Value));
        deliveryTypeValue.add(getString(R.string.deliveryType5Value));

        spinnerDeliveryType.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, deliveryType));
        spinnerDeliveryType.setSelection(0);

        //Spinner for relationList
        spinnerDeliveryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if (position == 0) {
                    delivery = "";
                } else {
                    delivery = deliveryTypeValue.get(position);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        typeOfBorn.clear();
        typeOfBorn.add(getString(R.string.selectBornType));
        typeOfBorn.add(getString(R.string.inborn));
        typeOfBorn.add(getString(R.string.outborn));

        typeOfBornValue.clear();
        typeOfBornValue.add(getString(R.string.selectBornType));
        typeOfBornValue.add(getString(R.string.inbornValue));
        typeOfBornValue.add(getString(R.string.outbornValue));

        spinnerAdmissionType.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, typeOfBorn));
        spinnerAdmissionType.setSelection(0);


//        spinnerAdmissionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                // TODO Auto-generated method stub
//
//                if(position==0)
//                {
//                    typeBorn="";
//                    typeOutBorn="";
//                    rlOutborn.setVisibility(View.GONE);
//                    spinnerOutbornType.setSelection(0);
//                }
//                else if(position==1)
//                {
//                    typeBorn= typeOfBornValue.get(position);
//                    typeOutBorn="";
//                    rlOutborn.setVisibility(View.GONE);
//                    spinnerOutbornType.setSelection(0);
//                }
//                else
//                {
//                    typeBorn = typeOfBornValue.get(position);
//                    typeOutBorn="";
//                    rlOutborn.setVisibility(View.VISIBLE);
//                    spinnerOutbornType.setSelection(0);
//                }
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // TODO Auto-generated method stub
//
//            }
//        });

        spinnerAdmissionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

//                if (position == 0) {
//                    typeBorn = "";
//                    typeOutBorn = "";
//                    tvComingFrom.setText("");
//                    rlOutborn.setVisibility(View.GONE);
//                    rlInborn.setVisibility(View.GONE);
//                    rlOutBornFrom.setVisibility(View.GONE);
//                    rlInBornFrom.setVisibility(View.GONE);
//                    spinnerInbornType.setSelection(0);
//                    spinnerOutbornType.setSelection(0);
//                } else if (position == 1) {
//                    tvComingFrom.setText(getString(R.string.whichWard));
//                    typeBorn = typeOfBornValue.get(position);
//                    typeOutBorn = "";
//                    rlInborn.setVisibility(View.VISIBLE);
//                    rlOutborn.setVisibility(View.GONE);
//                    rlOutBornFrom.setVisibility(View.GONE);
//                    rlInBornFrom.setVisibility(View.VISIBLE);
//                    spinnerInbornType.setSelection(0);
//                    spinnerOutbornType.setSelection(0);
//                } else {
//                    tvComingFrom.setText(getString(R.string.referringHospital));
//                    typeBorn = typeOfBornValue.get(position);
//                    typeOutBorn = "";
//                    rlInborn.setVisibility(View.VISIBLE);
//                    rlOutborn.setVisibility(View.GONE);
//                    rlOutBornFrom.setVisibility(View.VISIBLE);
//                    rlInBornFrom.setVisibility(View.GONE);
//                    spinnerInbornType.setSelection(0);
//                    spinnerOutbornType.setSelection(0);
//                }


                if (position == 0) {
                    typeBorn = "";
                    typeOutBorn = "";
                    rlInborn.setVisibility(View.GONE);
                    rlOutBornFrom.setVisibility(View.GONE);
                    spinnerInbornType.setSelection(0);
                    spinnerOutbornType.setSelection(0);

                } else if (position == 1) {
                    typeBorn = typeOfBornValue.get(position);
                    typeOutBorn = "";

                    rlInborn.setVisibility(View.VISIBLE);
                    rlOutBornFrom.setVisibility(View.GONE);
                    spinnerInbornType.setSelection(0);
                    spinnerOutbornType.setSelection(0);
                } else {
                    typeBorn = typeOfBornValue.get(position);
                    typeOutBorn = "";
                    rlInborn.setVisibility(View.GONE);
                    rlOutBornFrom.setVisibility(View.VISIBLE);
                    spinnerInbornType.setSelection(0);
                    spinnerOutbornType.setSelection(0);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });


        inbornTypeList.clear();
        inbornTypeList.add(getString(R.string.selectInBornType));
        inbornTypeList.add(getString(R.string.labourRoom));
        inbornTypeList.add(getString(R.string.sncu));
        inbornTypeList.add(getString(R.string.pncWard));
        inbornTypeList.add(getString(R.string.msaWard));
        inbornTypeList.add(getString(R.string.postoprativeWard));
        inbornTypeList.add(getString(R.string.other));

        inbornTypeValue.clear();
        inbornTypeValue.add(getString(R.string.selectInBornType));
        inbornTypeValue.add(getString(R.string.labourRoomValues));
        inbornTypeValue.add(getString(R.string.sncuValues));
        inbornTypeValue.add(getString(R.string.pncWardValues));
        inbornTypeValue.add(getString(R.string.msaWardValues));
        inbornTypeValue.add(getString(R.string.postoprativeWard));
        inbornTypeValue.add(getString(R.string.otherValue));

        spinnerInbornType.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, inbornTypeList));
        spinnerInbornType.setSelection(0);

        typeOfOutBorn.clear();
        typeOfOutBorn.add(getString(R.string.selectOutBornType));
        typeOfOutBorn.add(getString(R.string.referral));
        typeOfOutBorn.add(getString(R.string.insideReferral));

        typeOfOutBornValue.clear();
        typeOfOutBornValue.add(getString(R.string.selectOutBornType));
        typeOfOutBornValue.add(getString(R.string.referralValue));
        typeOfOutBornValue.add(getString(R.string.insideReferralValue));

        spinnerOutbornType.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, typeOfOutBorn));
        spinnerOutbornType.setSelection(0);

        //Spinner for relationList
        spinnerOutbornType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if (position == 0) {
                    typeOutBorn = "";
                } else {
                    typeOutBorn = typeOfOutBornValue.get(position);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });


        nurseListId.clear();
        nurseListId.add(getString(R.string.selectNurse));
        nurseListId.addAll(DatabaseController.getNurseIdCheckedInData());

        nurseListName.clear();
        nurseListName.add(getString(R.string.selectNurse));
        nurseListName.addAll(DatabaseController.getNurseNameCheckedInData());

        spinnerNurse.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, nurseListName));
        spinnerNurse.setSelection(0);

        //Spinner for relationList
        spinnerNurse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if (position == 0) {
                    nurseId = "";
                } else {
                    nurseId = nurseListId.get(position).toString();
                    AppSettings.putString(AppSettings.nurseId, nurseId);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });


        rlMale.setOnClickListener(this);
        rlFemale.setOnClickListener(this);
        rlAmbiguous.setOnClickListener(this);
        rlTime.setOnClickListener(this);
        rlDate.setOnClickListener(this);
        rlLMPDate.setOnClickListener(this);
        rlBabyPic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //Step 1 Mother
            case R.id.rlNext:

                /*if(AppSettings.getString(AppSettings.from).equalsIgnoreCase("1"))
                {
                    saveStep();
                    saveBabyData();

                    if(AppSettings.getString(AppSettings.checkHRN).equalsIgnoreCase("2"))
                    {
                        //AlertHRN("","");
                    }
                    else
                    {
                        //((RegistrationNewActivity)getActivity()).displayView(2);
                    }
                }
                else
                {
                    validationStep();
                }*/


                validationStep();

                break;

            case R.id.rlPrevious:

                if (llBaby.getVisibility() == View.VISIBLE) {
                    RegistrationActivity.tvBabyAdmission.setBackgroundResource(R.color.white);
                    RegistrationActivity.tvBabyAdmission.setTextColor(getResources().getColor(R.color.blackNew));
                    llMotherSection.setVisibility(View.VISIBLE);
                    llBaby.setVisibility(View.GONE);
                    rlPrevious.setVisibility(View.GONE);

                    if (AppSettings.getString(AppSettings.from).equalsIgnoreCase("0")) {
                        RegistrationActivity.rlClose.setVisibility(View.VISIBLE);
                    }


                } else
                    mActivity.onBackPressed();

                break;

            //Step 1 Mother
            case R.id.rlMotherPic:

            case R.id.ivMotherPic:

                from = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    fileUri = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(1));

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
                    intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
                    intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, 100);
                } else {
                    // create Intent to take a picture and return control to the calling application
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri(1); // create a file to save the image
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                    intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                    // start the image capture Intent
                    startActivityForResult(intent, 100);

                }

                break;

            //Step 2 Baby
            case R.id.rlTime:

                AppUtils.timeDialog(mActivity, tvDeliveryTime);

                break;

            case R.id.rlDate:

                AppUtils.dateDialog(mActivity, tvDeliveryDate, 4);

                break;

            case R.id.rlLMPDate:

                AppUtils.dateDialog(mActivity, tvLmpDate, 1);

                break;

            case R.id.rlBabyPic:

            case R.id.ivBabyPic:

                from = 1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    fileUri = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(1));

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
                    intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
                    intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, 100);
                } else {
                    // create Intent to take a picture and return control to the calling application
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri(1); // create a file to save the image
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                    intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                    // start the image capture Intent
                    startActivityForResult(intent, 100);

                }

                break;

            case R.id.rlMale:

                setMale();

                break;

            case R.id.rlFemale:

                setFemale();

                break;

            case R.id.rlAmbiguous:

                setAmbiguous();

                break;

            default:

                break;
        }
    }

    private void validationStep() {

        int weeks = 0;
        int weeksLmp = 0;
        System.out.print(delivery);

        if (!tvLmpDate.getText().toString().trim().isEmpty()) {
            weeks = AppUtils.getWeekDifference(tvLmpDate.getText().toString().trim(), tvDeliveryDate.getText().toString().trim());
            weeksLmp = AppUtils.getWeekDifference(tvLmpDate.getText().toString().trim(), AppUtils.getCurrentDateNew());


        }

        int tableName = 0;
        try {
            tableName = Integer.parseInt(etBabyBirthWeight.getText().toString().trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        saveStep();

        if (etHospitalRegistration.getText().toString().isEmpty()) {
            etHospitalRegistration.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorEnterHRN));
        } else if (encodedBabyString.isEmpty() && llBaby.getVisibility() == View.VISIBLE) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorClickBaby));
        } else if (tvLmpDate.getText().toString().isEmpty() && llBaby.getVisibility() == View.VISIBLE) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorSelectLMPDate));
        } else if (!tvLmpDate.getText().toString().isEmpty() && (weeksLmp < 24 || weeksLmp > 44) && llBaby.getVisibility() != View.VISIBLE && tvLmpDate.getVisibility() == View.VISIBLE) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorLmpDate));
        } else if (tvDeliveryDate.getText().toString().isEmpty() && llBaby.getVisibility() == View.VISIBLE) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorSelectDelDate));
        } else if (!tvDeliveryDate.getText().toString().isEmpty() && (weeks < 24 || weeks > 44) && llBaby.getVisibility() == View.VISIBLE) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorProperDelDate));
        } else if (tvDeliveryTime.getText().toString().isEmpty() && llBaby.getVisibility() == View.VISIBLE) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorSelectDelTime));
        } else if (gender.isEmpty() && llBaby.getVisibility() == View.VISIBLE) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorSelectGender));
        } else if (etBabyBirthWeight.getText().toString().isEmpty() && llBaby.getVisibility() == View.VISIBLE) {
            etBabyBirthWeight.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorEnterBWeight));
        } else if ((tableName < 400 || tableName > 6000) && llBaby.getVisibility() == View.VISIBLE) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorWeightLimit));
        }
//        else if(delivery.isEmpty() && llBaby.getVisibility()==View.VISIBLE && spinnerDeliveryType.getVisibility()==View.VISIBLE)
//        {
//            AppUtils.showToastSort(mActivity,getString(R.string.errorSelectDelType));
//        }
        else if (typeBorn.isEmpty() && llBaby.getVisibility() == View.VISIBLE) {
            AppUtils.showToastSort(mActivity, getString(R.string.selectBornType));
        } else if (typeBorn.equalsIgnoreCase(getString(R.string.inbornValue)) && spinnerInbornType.getSelectedItemPosition() == 0 && llMotherSection.getVisibility() == View.GONE) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorWard));
        } else if (typeBorn.equalsIgnoreCase(getString(R.string.outbornValue)) && !edReasionRelation.getText().toString().isEmpty() && llMotherSection.getVisibility() == View.GONE) {
            AppUtils.showToastSort(mActivity, getString(R.string.referringHospital));
        } else if (nurseId.isEmpty() && llBaby.getVisibility() == View.VISIBLE && llMotherSection.getVisibility() == View.GONE) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorSelectYourName));
        } else {
            if (llBaby.getVisibility() == View.GONE) {
                RegistrationActivity.tvBabyAdmission.setBackgroundResource(R.drawable.rectangle_teal_selected);
                RegistrationActivity.tvBabyAdmission.setTextColor(getResources().getColor(R.color.white));
                llMotherSection.setVisibility(View.GONE);
                llBaby.setVisibility(View.VISIBLE);
                rlPrevious.setVisibility(View.VISIBLE);
            } else {
                //createJsonForBabyRegistration();
                if (AppUtils.isNetworkAvailable(mActivity)) {
                    addSiblingApi();
                } else {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                }
            }

        }

        RegistrationActivity.rlClose.setVisibility(View.GONE);
        AppSettings.putString(AppSettings.babyPic, encodedBabyString);

    }

    private JSONObject createJsonForBabyRegistration() {

        JSONObject json = new JSONObject();

        JSONObject jsonData = new JSONObject();

        try {

            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("motherId", AppSettings.getString(AppSettings.motherId));
            jsonData.put("hospitalRegistrationNumber", etHospitalRegistration.getText().toString().trim());
            jsonData.put("staffId", nurseId);

            jsonData.put("deliveryDate", tvDeliveryDate.getText().toString().trim());
            jsonData.put("deliveryTime", AppUtils.convertTimeTo24HoursFormat(tvDeliveryTime.getText().toString().trim()));
            jsonData.put("babyGender", gender);
            jsonData.put("deliveryType", delivery);
            jsonData.put("babyWeight", etBabyBirthWeight.getText().toString().trim());
            jsonData.put("birthWeightAvail", "1");
            jsonData.put("reason", "");
            jsonData.put("localId", RegistrationActivity.uuid);
            jsonData.put("typeOfBorn", typeBorn);
            jsonData.put("typeOfOutBorn", "");

            if (typeBorn.equalsIgnoreCase(getString(R.string.inbornValue))) {
                jsonData.put("infantComingFrom", inbornTypeValue.get(spinnerInbornType.getSelectedItemPosition()));
            } else if (typeBorn.equalsIgnoreCase(getString(R.string.outbornValue))) {
                jsonData.put("infantComingFrom", etOutBornFrom.getText().toString().trim());
            }

            jsonData.put("babyPhoto", encodedBabyString);
            //jsonData.put( "babyPhoto","");

            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));

            Log.v("doMotherBabyRegis", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    @SuppressLint("LongLogTag")
    private void addSiblingApi() {

        WebServices.postApi(mActivity, AppUrls.addSibling, createJsonForBabyRegistration(), true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        //JSONObject ids= resobj.getJSONObject("ids");

                        AppSettings.putString(AppSettings.babyId, jsonObject.getString("babyId"));
                        AppSettings.putString(AppSettings.bAdmId, jsonObject.getString("babyAdmissionId"));
                        try {
                            AppSettings.putString(AppSettings.mAdmId, jsonObject.getString("motherAdmissionId"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ContentValues mContentValues = new ContentValues();

                        mContentValues.put(TableUser.tableColumn.uuid.toString(), String.valueOf(RegistrationActivity.uuid));
                        mContentValues.put(TableUser.tableColumn.babyId.toString(), jsonObject.getString("babyId"));
                        mContentValues.put(TableUser.tableColumn.mRStatus.toString(), "0");
                        mContentValues.put(TableUser.tableColumn.bRStatus.toString(), "0");

                        DatabaseController.insertUpdateData(mContentValues, TableUser.tableName,
                                TableUser.tableColumn.uuid.toString(), String.valueOf(RegistrationActivity.uuid));

                        saveBabyData();

                        ((RegistrationActivity) getActivity()).displayView(1);

                    } else {

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

    public void saveBabyData() {

        ContentValues contentValues = new ContentValues();

        contentValues.put(TableBabyRegistration.tableColumn.uuid.toString(), RegistrationActivity.uuid);
        contentValues.put(TableBabyRegistration.tableColumn.motherId.toString(), AppSettings.getString(AppSettings.motherId));
        contentValues.put(TableBabyRegistration.tableColumn.babyMCTSNumber.toString(), "");
        contentValues.put(TableBabyRegistration.tableColumn.babyId.toString(), AppSettings.getString(AppSettings.babyId));
        contentValues.put(TableBabyRegistration.tableColumn.deliveryDate.toString(), tvDeliveryDate.getText().toString().trim());
        contentValues.put(TableBabyRegistration.tableColumn.deliveryTime.toString(), tvDeliveryTime.getText().toString().trim());
        contentValues.put(TableBabyRegistration.tableColumn.firstTimeFeed.toString(), "");
        contentValues.put(TableBabyRegistration.tableColumn.babyGender.toString(), gender);
        contentValues.put(TableBabyRegistration.tableColumn.deliveryType.toString(), delivery);
        contentValues.put(TableBabyRegistration.tableColumn.babyWeight.toString(), etBabyBirthWeight.getText().toString().trim());
        contentValues.put(TableBabyRegistration.tableColumn.babyCryAfterBirth.toString(), "");
        contentValues.put(TableBabyRegistration.tableColumn.babyNeedBreathingHelp.toString(), "");
        contentValues.put(TableBabyRegistration.tableColumn.birthWeightAvail.toString(), "1");
        contentValues.put(TableBabyRegistration.tableColumn.reason.toString(), "");
        contentValues.put(TableBabyRegistration.tableColumn.babyPhoto.toString(), encodedBabyString);
        contentValues.put(TableBabyRegistration.tableColumn.isDataSynced.toString(), "3");
        contentValues.put(TableBabyRegistration.tableColumn.typeOfBorn.toString(), typeBorn);
        contentValues.put(TableBabyRegistration.tableColumn.typeOfOutBorn.toString(), typeOutBorn);
        contentValues.put(TableBabyRegistration.tableColumn.wasApgarScoreRecorded.toString(), "");
        contentValues.put(TableBabyRegistration.tableColumn.apgarScore.toString(), "");
        contentValues.put(TableBabyRegistration.tableColumn.vitaminKGiven.toString(), "");

        contentValues.put(TableBabyRegistration.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableBabyRegistration.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableBabyRegistration.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableBabyRegistration.tableColumn.status.toString(), "1");

        DatabaseController.insertUpdateData(contentValues, TableBabyRegistration.tableName,
                TableBabyRegistration.tableColumn.uuid.toString(), String.valueOf(RegistrationActivity.uuid));


        ContentValues cvBabyAdm = new ContentValues();

        cvBabyAdm.put(TableBabyAdmission.tableColumn.uuid.toString(), RegistrationActivity.uuid);
        cvBabyAdm.put(TableBabyAdmission.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        cvBabyAdm.put(TableBabyAdmission.tableColumn.serverId.toString(), AppSettings.getString(AppSettings.bAdmId));
        cvBabyAdm.put(TableBabyAdmission.tableColumn.babyId.toString(), AppSettings.getString(AppSettings.babyId));
        cvBabyAdm.put(TableBabyAdmission.tableColumn.babyFileId.toString(), etHospitalRegistration.getText().toString().trim());
        cvBabyAdm.put(TableBabyAdmission.tableColumn.admissionDateTime.toString(), AppUtils.currentTimestampFormat());
        cvBabyAdm.put(TableBabyAdmission.tableColumn.status.toString(), "4");

        DatabaseController.insertUpdateData(cvBabyAdm, TableBabyAdmission.tableName,
                TableBabyAdmission.tableColumn.uuid.toString(), String.valueOf(RegistrationActivity.uuid));

    }

    private void saveStep() {

        ContentValues mContentValues = new ContentValues();

        mContentValues.put(TableUser.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        mContentValues.put(TableUser.tableColumn.uuid.toString(), String.valueOf(RegistrationActivity.uuid));
        mContentValues.put(TableUser.tableColumn.motherId.toString(), AppSettings.getString(AppSettings.motherId));
        mContentValues.put(TableUser.tableColumn.babyId.toString(), "");
        mContentValues.put(TableUser.tableColumn.babyAdmissionId.toString(), "");
        mContentValues.put(TableUser.tableColumn.motherAdmissionId.toString(), "");
        mContentValues.put(TableUser.tableColumn.step1.toString(), createJsonForBabyRegistration().toString());
        mContentValues.put(TableUser.tableColumn.step2.toString(), createJsonForBabyRegistration().toString());
        mContentValues.put(TableUser.tableColumn.step3.toString(), createJsonForBabyRegistration().toString());
        mContentValues.put(TableUser.tableColumn.step4.toString(), "");
        mContentValues.put(TableUser.tableColumn.step5.toString(), "");
        mContentValues.put(TableUser.tableColumn.step6.toString(), "");
        mContentValues.put(TableUser.tableColumn.step7.toString(), "");
        mContentValues.put(TableUser.tableColumn.mRStatus.toString(), "1");
        mContentValues.put(TableUser.tableColumn.bRStatus.toString(), "2");
        mContentValues.put(TableUser.tableColumn.bAStatus.toString(), "2");
        mContentValues.put(TableUser.tableColumn.mAStatus.toString(), "2");
        mContentValues.put(TableUser.tableColumn.dIStatus.toString(), "2");
        mContentValues.put(TableUser.tableColumn.cStatus.toString(), "2");
        mContentValues.put(TableUser.tableColumn.isSibling.toString(), "1");
        mContentValues.put(TableUser.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
        mContentValues.put(TableUser.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());
        mContentValues.put(TableUser.tableColumn.status.toString(), "1");
        mContentValues.put(TableUser.tableColumn.fromStep.toString(), AppSettings.getString(AppSettings.from));

        if (!DatabaseController.getFromData(RegistrationActivity.uuid).equalsIgnoreCase("")) {
            mContentValues.put(TableUser.tableColumn.fromStep.toString(), DatabaseController.getFromData(RegistrationActivity.uuid));
        }

        DatabaseController.insertUpdateData(mContentValues, TableUser.tableName,
                TableUser.tableColumn.uuid.toString(), String.valueOf(RegistrationActivity.uuid));
    }


    /*public void AlertHRN(String hrn, final String from) {
        final Dialog dialog = new Dialog(mActivity,android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_hrn);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        TextView tvyes                  = dialog.findViewById(R.id.tvOk);
        TextView tvCancel          = dialog.findViewById(R.id.tvCancel);
        TextView tvRegistration          = dialog.findViewById(R.id.tvF3);

        Spinner spinnerNurse = dialog.findViewById(R.id.spinnerNurse);

        nurseListId.clear();
        nurseListId.add(getString(R.string.selectNurse));
        nurseListId.addAll(DatabaseController.getNurseIdCheckedInData());

        nurseListName.clear();
        nurseListName.add(getString(R.string.selectNurse));
        nurseListName.addAll(DatabaseController.getNurseNameCheckedInData());

        spinnerNurse.setAdapter(new adapter_spinner(mActivity, R.layout.spinner_textview_new, nurseListName));
        spinnerNurse.setSelection(0);

        //Spinner for relationList
        spinnerNurse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if(position==0)
                {
                    AppSettings.putString(AppSettings.nurseId,"");
                }
                else
                {
                    AppSettings.putString(AppSettings.nurseId, nurseListId.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        if(AppSettings.getString(AppSettings.loungeType).equalsIgnoreCase("1"))
        {
            tvRegistration.setText(getString(R.string.tempId));
        }
        else  if(AppSettings.getString(AppSettings.loungeType).equalsIgnoreCase("2"))
        {
            tvRegistration.setText(getString(R.string.hospitalRegistrationNumber));
        }

        final EditText etHRNumber = dialog.findViewById(R.id.etHRNumber);

        if(from.equalsIgnoreCase("1"))
        {
            etHRNumber.setText(hrn);
        }

        etHRNumber.setKeyListener(DigitsKeyListener.getInstance("0123456789/"));

        tvyes.setText(getString(R.string.submit));
        tvCancel.setText(getString(R.string.cancel));

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        tvyes.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(AppSettings.getString(AppSettings.nurseId).isEmpty())
                {
                    AppUtils.showToastSort(mActivity,getString(R.string.errorSelectNurse));
                }
               else  if(etHRNumber.getText().toString().isEmpty())
                {
                    etHRNumber.requestFocus();
                    if(AppSettings.getString(AppSettings.loungeType).equalsIgnoreCase("1"))
                    {
                        AppUtils.showToastSort(mActivity,getString(R.string.errorHRegisNo));
                    }
                    else  if(AppSettings.getString(AppSettings.loungeType).equalsIgnoreCase("2"))
                    {
                        AppUtils.showToastSort(mActivity,getString(R.string.errorHRNo));
                    }
                }
                else
                {
                    dialog.dismiss();
                    //etBabyFileId.setText(etOther.getText().toString().trim());
                    etBabyFileId.setText(etHRNumber.getText().toString().trim());
                    AppUtils.hideSoftKeyboard(mActivity);

                    AppSettings.putString(AppSettings.hrn,etBabyFileId.getText().toString().trim());

                    saveStep();
                    saveBabyData();

                    ((RegistrationActivity)getActivity()).displayView((2));

                    AppUtils.hideSoftKeyboard(mActivity);
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                AppUtils.hideSoftKeyboard(mActivity);
            }
        });
    }*/

    private void defaultGender() {

        ivMale.setImageResource(R.drawable.ic_check_box);
        ivFemale.setImageResource(R.drawable.ic_check_box);
        ivAmbiguous.setImageResource(R.drawable.ic_check_box);
        ivMale.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
        ivFemale.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
        ivAmbiguous.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);

        ivMalePic.setImageResource(R.drawable.ic_boy);
        ivFemalePic.setImageResource(R.drawable.ic_girl);
        ivAmbiguousPic.setImageResource(R.drawable.ic_ambiguous);
        ivMalePic.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
        ivFemalePic.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
        //ivAmbiguousPic.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
    }

    private void setMale() {

        defaultGender();
        gender = getString(R.string.maleValue);
        ivMale.setImageResource(R.drawable.ic_check_box_selected);
        ivMale.setColorFilter(getResources().getColor(R.color.r_color), PorterDuff.Mode.SRC_IN);
        ivMalePic.setColorFilter(getResources().getColor(R.color.r_color), PorterDuff.Mode.SRC_IN);
    }


    private void setFemale() {

        defaultGender();
        gender = getString(R.string.femaleValue);
        ivFemale.setImageResource(R.drawable.ic_check_box_selected);
        ivFemale.setColorFilter(getResources().getColor(R.color.r_color), PorterDuff.Mode.SRC_IN);
        ivFemalePic.setColorFilter(getResources().getColor(R.color.oo_color), PorterDuff.Mode.SRC_IN);
    }

    private void setAmbiguous() {

        defaultGender();
        gender = getString(R.string.indefiniteValue);
        ivAmbiguous.setImageResource(R.drawable.ic_check_box_selected);
        ivAmbiguous.setColorFilter(getResources().getColor(R.color.r_color), PorterDuff.Mode.SRC_IN);
        ivAmbiguousPic.setImageResource(R.drawable.ic_ambiguous_color);
    }

    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private File getOutputMediaFile(int type) {

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
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + AppConstants.projectName + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {

                //String
                String picturePath = fileUri.getPath();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(picturePath, options);
                final int REQUIRED_SIZE = 500;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                //Bitmap
                Bitmap bitmap = BitmapFactory.decodeFile(picturePath, options);

                Matrix matrix = new Matrix();
                matrix.postRotate(getImageOrientation(picturePath));
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 30, bao);

                rotatedBitmap = AppUtils.setWaterMark(rotatedBitmap, mActivity);

                ivBabyPic.setImageBitmap(rotatedBitmap);
                encodedBabyString = getEncoded64ImageStringFromBitmap(rotatedBitmap);
                rlBabyPic.setBackgroundResource(0);

                File file = new File(picturePath);
                AppUtils.deleteDirectory(file);
            }
        }

    }

    private String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string

        return Base64.encodeToString(byteFormat, Base64.NO_WRAP);
    }

    private int getImageOrientation(String imagePath) {
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

    public void setValues() {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();

        arrayList.addAll(DatabaseController.getMotherRegDataViaId(AppSettings.getString(AppSettings.motherId)));

        if (arrayList.size() > 0) {

            type = arrayList.get(0).get("type");

//            if(type.equals("2")){
//                tvLMPDateTitle.setText(getString(R.string.lmpDateWithoutAsterick));
//            }

            llMotherSection.setVisibility(View.VISIBLE);

            if (type.equalsIgnoreCase("2")) {
                llUnknown.setVisibility(View.VISIBLE);
                llGuardian.setVisibility(View.GONE);
                llBaby.setVisibility(View.GONE);
                llMother.setVisibility(View.GONE);
                rlMotherName.setVisibility(View.GONE);

                RegistrationActivity.admitted = getString(R.string.noValue);
                //reason = getString(R.string.motherStatus3);

                etUMGN.setText( arrayList.get(0).get("guardianName"));
                etUMGNo.setText( arrayList.get(0).get("guardianNumber"));
                etOName.setText( arrayList.get(0).get("organisationName"));
                etONo.setText( arrayList.get(0).get("organisationNumber"));
                etOAddress.setText( arrayList.get(0).get("organisationAddress"));


            } else {
                rlMotherName.setVisibility(View.VISIBLE);

                if (type.equalsIgnoreCase("1")) {
                    llUnknown.setVisibility(View.GONE);
                    llGuardian.setVisibility(View.GONE);
                    llBaby.setVisibility(View.GONE);
                    llMother.setVisibility(View.VISIBLE);
                    RegistrationActivity.admitted = getString(R.string.yesValue);


                } else if (type.equalsIgnoreCase("3")) {
                    llUnknown.setVisibility(View.GONE);
                    llGuardian.setVisibility(View.VISIBLE);
                    llBaby.setVisibility(View.GONE);
                    llMother.setVisibility(View.GONE);
                    RegistrationActivity.admitted = getString(R.string.noValue);
                }

                etMotherName.setText(arrayList.get(0).get("motherName"));
                tvLmpDate.setText(arrayList.get(0).get("motherLmpDate"));

                reason = arrayList.get(0).get("notAdmittedReason");

                if (reason == null) {
                    reason = "";
                }

                if (!arrayList.get(0).get("motherPicture").isEmpty()) {
                    try {
                        byte[] decodedString = Base64.decode(arrayList.get(0).get("motherPicture"), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivMotherPic.setImageBitmap(decodedByte);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                etMotherNumber.setText(arrayList.get(0).get("motherMobileNumber"));

                etGuardianName.setText(arrayList.get(0).get("guardianName"));
                etGuardianNumber.setText(arrayList.get(0).get("guardianNumber"));

                relation = arrayList.get(0).get("relationWithChild");

                if (relation == null)
                {
                    relation = "";
                }

                int pos = 0;

                for (int j = 0; j < relationValue.size(); j++) {
                    if (relationValue.get(j).equalsIgnoreCase(relation)) {
                        pos = j;
                        break;
                    }
                }

                spinnerRelation.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, relationList));
                spinnerRelation.setSelection(pos);


                if(relation.equals(getString(R.string.otherValue))){
                    edReasionRelation.setVisibility(View.VISIBLE);
                    edReasionRelation.setText( arrayList.get(0).get("relationWithChildOther"));
                }else {
                    edReasionRelation.setVisibility(View.GONE);
                }

                int delPos=0;

                for(int j=0;j<deliveryTypeValue.size();j++)
                {
                    if(deliveryTypeValue.get(j).equalsIgnoreCase(delivery))
                    {
                        delPos=j;

                        break;
                    }
                }

                spinnerDeliveryType.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, deliveryType));
                spinnerDeliveryType.setSelection(delPos);


                //khushboo pandey////
                if(!arrayList.get(0).get("typeOfBorn").isEmpty()){
                    pos=0;

                    typeBorn = arrayList.get(0).get("typeOfBorn");
                    for(int j=0;j<typeOfBornValue.size();j++)
                    {
                        if(typeOfBornValue.get(j).equalsIgnoreCase(typeBorn))
                        {
                            pos=j;
                            break;
                        }
                    }
                    spinnerAdmissionType.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, typeOfBorn));
                    spinnerAdmissionType.setSelection(pos);


                    if(!typeBorn.isEmpty() && !typeBorn.equals("")){
                        if (typeBorn.equalsIgnoreCase(getString(R.string.inbornValue))) {
                            int pos1 = 0;

                            for (int j = 0; j < inbornTypeList.size(); j++) {
                                if (inbornTypeList.get(j).equalsIgnoreCase(arrayList.get(0).get("infantComingFrom"))) {
                                    pos1 = j;
                                    break;
                                }
                            }

                            spinnerInbornType.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, inbornTypeList));
                            spinnerInbornType.setSelection(pos1);

                        } else if (typeBorn.equalsIgnoreCase(getString(R.string.outbornValue))) {
                            etOutBornFrom.setText(arrayList.get(0).get("infantComingFrom"));
                        }
                    }


                }




                ArrayList<HashMap<String, String>> arrayListNew = new ArrayList<>(DatabaseController.getStepData(RegistrationActivity.uuid));

                for (int i = 0; i < arrayListNew.size(); i++) {

                    JSONObject jsonObject = null;

                    try {

                        jsonObject = new JSONObject(Objects.requireNonNull(arrayListNew.get(i).get("step3")));

                        JSONObject jsonObjectBaby = jsonObject.getJSONObject(AppConstants.projectName);
                        tvDeliveryDate.setText(jsonObjectBaby.getString("deliveryDate"));
                        tvDeliveryTime.setText(AppUtils.convertTimeTo12HoursFormat(jsonObjectBaby.getString("deliveryTime")));

                        delivery = jsonObjectBaby.getString("deliveryType");
                        delPos=0;

                        for(int j=0;j<deliveryTypeValue.size();j++)
                        {
                            if(deliveryTypeValue.get(j).equalsIgnoreCase(delivery))
                            {
                                delPos=j;

                                break;
                            }
                        }

                        spinnerDeliveryType.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, deliveryType));
                        spinnerDeliveryType.setSelection(delPos);


                        nurseId  = jsonObjectBaby.getString("staffId");
                        int nursePos=0;

                        for(int j=0;j<nurseListId.size();j++)
                        {
                            if(nurseListId.get(j).equalsIgnoreCase(nurseId))
                            {
                                nursePos=j;
                                break;
                            }
                        }

                        spinnerNurse.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, nurseListName));
                        spinnerNurse.setSelection(nursePos);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    private void setStep1and2Values() {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();

        arrayList.addAll(DatabaseController.getMotherRegDataViaId(AppSettings.getString(AppSettings.motherId)));

        if (arrayList.size() > 0) {

            tvLmpDate.setText(arrayList.get(0).get("motherLmpDate"));

            type = arrayList.get(0).get("type");

            llMotherSection.setVisibility(View.VISIBLE);

            if (type.equalsIgnoreCase("2")) {
                llUnknown.setVisibility(View.VISIBLE);
                llGuardian.setVisibility(View.GONE);
                llBaby.setVisibility(View.GONE);
                llMother.setVisibility(View.GONE);
                rlMotherName.setVisibility(View.GONE);

                RegistrationActivity.admitted = getString(R.string.noValue);
                //reason = getString(R.string.motherStatus3);
                etUMGN.setText(arrayList.get(0).get("guardianName"));
                etUMGNo.setText(arrayList.get(0).get("guardianNumber"));
                etOName.setText(arrayList.get(0).get("organisationName"));
                etONo.setText(arrayList.get(0).get("organisationNumber"));
                etOAddress.setText(arrayList.get(0).get("organisationAddress"));

            } else {
                rlMotherName.setVisibility(View.VISIBLE);

                if (type.equalsIgnoreCase("1")) {
                    llUnknown.setVisibility(View.GONE);
                    llGuardian.setVisibility(View.GONE);
                    llBaby.setVisibility(View.GONE);
                    llMother.setVisibility(View.VISIBLE);
                    RegistrationActivity.admitted = getString(R.string.yesValue);

                } else if (type.equalsIgnoreCase("3")) {
                    llUnknown.setVisibility(View.GONE);
                    llGuardian.setVisibility(View.VISIBLE);
                    llBaby.setVisibility(View.GONE);
                    llMother.setVisibility(View.GONE);

                    RegistrationActivity.admitted = getString(R.string.noValue);

                }

                etMotherName.setText(arrayList.get(0).get("motherName"));

                reason = arrayList.get(0).get("notAdmittedReason");

                if (reason == null) {
                    reason = "";
                }

                if (!arrayList.get(0).get("motherPicture").isEmpty()) {
                    try {
                        byte[] decodedString = Base64.decode(arrayList.get(0).get("motherPicture"), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivMotherPic.setImageBitmap(decodedByte);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                etMotherNumber.setText(arrayList.get(0).get("motherMobileNumber"));

                etGuardianName.setText(arrayList.get(0).get("guardianName"));
                etGuardianNumber.setText(arrayList.get(0).get("guardianNumber"));

                relation = arrayList.get(0).get("relationWithChild");

                if (relation == null) {
                    relation = "";
                }

                int pos = 0;

                for (int j = 0; j < relationValue.size(); j++) {
                    if (relationValue.get(j).equalsIgnoreCase(relation)) {
                        pos = j;
                        break;
                    }
                }

                spinnerRelation.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, relationList));
                spinnerRelation.setSelection(pos);

                if (relation.equals(getString(R.string.otherValue))) {
                    edReasionRelation.setVisibility(View.VISIBLE);
                    edReasionRelation.setText(arrayList.get(0).get("relationWithChildOther"));

                } else {
                    edReasionRelation.setVisibility(View.GONE);
                }
            }

            ///khushboo pandey///
            typeBorn = arrayList.get(0).get("typeOfBorn");

            int pos = 0;

            for (int j = 0; j < typeOfBornValue.size(); j++) {
                if (typeOfBornValue.get(j).equalsIgnoreCase(typeBorn)) {
                    pos = j;
                    break;
                }
            }

            spinnerAdmissionType.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, typeOfBorn));
            spinnerAdmissionType.setSelection(pos);


            if (typeBorn.equalsIgnoreCase(getString(R.string.inbornValue))) {
                int pos1 = 0;

                for (int j = 0; j < inbornTypeList.size(); j++) {
                    if (inbornTypeValue.get(j).equalsIgnoreCase(arrayList.get(0).get("infantComingFrom"))) {
                        pos1 = j;
                        break;
                    }
                }

                spinnerInbornType.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, inbornTypeValue));
                spinnerInbornType.setSelection(pos1);

            } else if (typeBorn.equalsIgnoreCase(getString(R.string.outbornValue))) {
                etOutBornFrom.setText(arrayList.get(0).get("infantComingFrom"));
            }
        }


        ArrayList<HashMap<String, String>> arrayListNew = new ArrayList<>(DatabaseController.getStepData(RegistrationActivity.uuid));

        etHospitalRegistration.setText(AppSettings.getString(AppSettings.hrn));

        for (int i = 0; i < arrayListNew.size(); i++) {

            JSONObject jsonObject = null;

            try {

                jsonObject = new JSONObject(Objects.requireNonNull(arrayListNew.get(i).get("step3")));

                JSONObject jsonObjectBaby = jsonObject.getJSONObject(AppConstants.projectName);

                tvDeliveryDate.setText(jsonObjectBaby.getString("deliveryDate"));
                tvDeliveryTime.setText(AppUtils.convertTimeTo12HoursFormat(jsonObjectBaby.getString("deliveryTime")));

                etBabyBirthWeight.setText(jsonObjectBaby.getString("babyWeight"));

                encodedBabyString = jsonObjectBaby.getString("babyPhoto");

                if (jsonObjectBaby.getString("babyPhoto").isEmpty()) {
                    encodedBabyString = "";
                } else {
                    try {
                        byte[] decodedString = Base64.decode(jsonObjectBaby.getString("babyPhoto"), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ivBabyPic.setImageBitmap(decodedByte);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                defaultGender();
                gender = jsonObjectBaby.getString("babyGender");

                if (gender.equalsIgnoreCase(getString(R.string.maleValue))) {
                    setMale();
                } else if (gender.equalsIgnoreCase(getString(R.string.femaleValue))) {
                    setFemale();
                } else if (gender.equalsIgnoreCase(getString(R.string.indefiniteValue))) {
                    setAmbiguous();
                }

                delivery = jsonObjectBaby.getString("deliveryType");

                int delPos = 0;

                for (int j = 0; j < deliveryTypeValue.size(); j++) {
                    if (deliveryTypeValue.get(j).equalsIgnoreCase(delivery)) {
                        delPos = j;

                        break;
                    }
                }

                spinnerDeliveryType.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, deliveryType));
                spinnerDeliveryType.setSelection(delPos);

//                typeBorn = jsonObjectBaby.getString("typeOfBorn");
//
//                int pos=0;
//
//                for(int j=0;j<typeOfBornValue.size();j++)
//                {
//                    if(typeOfBornValue.get(j).equalsIgnoreCase(typeBorn))
//                    {
//                        pos=j;
//                        break;
//                    }
//                }
//
//                spinnerAdmissionType.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, typeOfBorn));
//                spinnerAdmissionType.setSelection(pos);
//
//                typeOutBorn = jsonObjectBaby.getString("typeOfOutBorn");
//
//                pos=0;
//
//                for(int j=0;j<typeOfOutBornValue.size();j++)
//                {
//                    if(typeOfOutBornValue.get(j).equalsIgnoreCase(typeOutBorn))
//                    {
//                        pos=j;
//                        break;
//                    }
//                }
//
//                spinnerOutbornType.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, typeOfOutBorn));
//                spinnerOutbornType.setSelection(pos);
//

                nurseId = jsonObjectBaby.getString("staffId");
                int nursePos = 0;

                for (int j = 0; j < nurseListId.size(); j++) {
                    if (nurseListId.get(j).equalsIgnoreCase(nurseId)) {
                        nursePos = j;
                        break;
                    }
                }

                spinnerNurse.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, nurseListName));
                spinnerNurse.setSelection(nursePos);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private static class adapterSpinner extends ArrayAdapter<String> {

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

            TextView tvName = row.findViewById(R.id.tvName);

            tvName.setText(data.get(position));

            return row;
        }
    }

}


