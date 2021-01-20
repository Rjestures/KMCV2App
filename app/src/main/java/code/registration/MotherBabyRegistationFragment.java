package code.registration;

import android.annotation.SuppressLint;
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
import android.text.method.DigitsKeyListener;
import android.util.Base64;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.androidnetworking.AndroidNetworking;
import com.kmcapp.android.BuildConfig;
import com.kmcapp.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.algo.WebServicesImageCallback;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableBabyAdmission;
import code.database.TableBabyRegistration;
import code.database.TableMotherAdmission;
import code.database.TableMotherRegistration;
import code.database.TableUser;
import code.main.ReferralListActivity;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;

import static android.app.Activity.RESULT_OK;
import static code.utils.AppUtils.hideSoftKeyboard;


public class MotherBabyRegistationFragment extends BaseFragment implements View.OnClickListener {

    String currentTime = "";
    RelativeLayout rlInfantComingFromOther;
    EditText etInfantComingFromOther;
    private ArrayList<String> nurseListId = new ArrayList<String>();
    private ArrayList<String> nurseListName = new ArrayList<String>();
    private String type = "", reason = "";
    //Mother Section
    private ArrayList<String> motherConditionList = new ArrayList<String>();
    private ArrayList<String> motherConditionListValue = new ArrayList<String>();
    private String motherCondition = "";
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
    private RelativeLayout rlAdmission, rlMale, rlFemale, rlAmbiguous, rlTime, rledOutborn, rlDate, rlLMPDate, rlBabyPic, rlOutborn, rlMotherName, rlNurse, rlInBornFrom, rlOutBornFrom, rlInborn;
    //EditText
    private EditText etBabyBirthWeight, etOutBornFrom, edReasionRelation;
    //TextView
    private TextView tvDeliveryTime, tvDeliveryDate, tvLmpDate, tvClickBabyPic, tvComingFrom, tvLMPDateTitle;
    //ImageView
    private ImageView ivMale, ivFemale, ivAmbiguous, ivMalePic, ivFemalePic, ivAmbiguousPic, ivBabyPic;
    //Spinner
    private Spinner spinnerDeliveryType, spinnerAdmissionType, spinnerOutbornType, spinnerNurse, spinnerInbornType;
    private String encodedString = "";
    private String encodedBabyString = "";
    private String getcurrentTime = "";
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
    private ArrayList<String> inbornTypeList = new ArrayList<String>();
    private ArrayList<String> inbornTypeValue = new ArrayList<String>();
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

        if (AppSettings.getString(AppSettings.from).equalsIgnoreCase("0")) {
            RegistrationActivity.uuid = UUID.randomUUID().toString();
            AlertReferral();
        } else if (AppSettings.getString(AppSettings.from).equalsIgnoreCase("1")) {
            AppUtils.enableDisable(llMotherSection, false);
            AppUtils.enableDisable(llBaby, false);
            AppUtils.enableDisable(rlAdmission, true);
            AppUtils.enableDisable(rlOutborn, true);
            AppUtils.enableDisable(rlInborn, true);
            AppUtils.enableDisable(rledOutborn, true);
//            AppUtils.enableDisable(rlNurse,true);
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
        edReasionRelation = v.findViewById(R.id.edReasionRelation);

        //RelativeLayout
        rlNext = v.findViewById(R.id.rlNext);
        rlPrevious = v.findViewById(R.id.rlPrevious);
        rledOutborn = v.findViewById(R.id.rledOutborn);

        rlInfantComingFromOther = v.findViewById(R.id.rlInfantComingFromOther);
        etInfantComingFromOther = v.findViewById(R.id.etInfantComingFromOther);

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

        ////EditText for Known Mother
        etMotherName = v.findViewById(R.id.etMotherName);
        etMotherNumber = v.findViewById(R.id.etMotherNumber);
        etHospitalRegistration = v.findViewById(R.id.etHospitalRegistration);

        //Spinner
        spinnerRelation = v.findViewById(R.id.spinnerRelation);

        //ImageView
        ivMotherPic = v.findViewById(R.id.ivMotherPic);

        //For Known Mother Pic
        rlMotherName = v.findViewById(R.id.rlMotherName);
        rlMotherPic = v.findViewById(R.id.rlMotherPic);
        rlNurse = v.findViewById(R.id.rlNurse);
        rlInBornFrom = v.findViewById(R.id.rlInBornFrom);
        rlInborn = v.findViewById(R.id.rlInborn);
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

                if (spinnerRelation.getSelectedItem().equals(getString(R.string.other))) {
                    edReasionRelation.setVisibility(View.VISIBLE);
                } else {
                    edReasionRelation.setVisibility(View.GONE);
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
        etOutBornFrom = v.findViewById(R.id.etOutBornFrom);

        //RelativeLayout
        rlMale = v.findViewById(R.id.rlMale);
        rlFemale = v.findViewById(R.id.rlFemale);
        rlAmbiguous = v.findViewById(R.id.rlAmbiguous);
        rlTime = v.findViewById(R.id.rlTime);
        rlDate = v.findViewById(R.id.rlDate);
        rlLMPDate = v.findViewById(R.id.rlLMPDate);
        rlBabyPic = v.findViewById(R.id.rlBabyPic);
        rlOutborn = v.findViewById(R.id.rlOutborn);

        //TextView
        tvDeliveryTime = v.findViewById(R.id.tvDeliveryTime);
        tvDeliveryDate = v.findViewById(R.id.tvDeliveryDate);
        tvLmpDate = v.findViewById(R.id.tvLMPDate);
        tvLMPDateTitle = v.findViewById(R.id.tvLMPDateTitle);
        tvClickBabyPic = v.findViewById(R.id.tvClickBabyPic);
        tvComingFrom = v.findViewById(R.id.tvComingFrom);

        //Spinner
        spinnerDeliveryType = v.findViewById(R.id.spinnerDeliveryType);
        spinnerAdmissionType = v.findViewById(R.id.spinnerAdmissionType);
        spinnerOutbornType = v.findViewById(R.id.spinnerOutbornType);
        spinnerNurse = v.findViewById(R.id.spinnerNurse);
        spinnerInbornType = v.findViewById(R.id.spinnerInbornType);

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

        //Spinner for relationList
        spinnerAdmissionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

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
                    etOutBornFrom.setText("");
                    rlInborn.setVisibility(View.VISIBLE);
                    rlOutBornFrom.setVisibility(View.GONE);
                    //spinnerInbornType.setSelection(0);
                    spinnerOutbornType.setSelection(0);
                    if (spinnerInbornType.getSelectedItem().equals(getString(R.string.other))) {
                        rlInfantComingFromOther.setVisibility(View.VISIBLE);
                    }


                } else {
                    typeBorn = typeOfBornValue.get(position);
                    typeOutBorn = "";
                    rlInborn.setVisibility(View.GONE);
                    rlOutBornFrom.setVisibility(View.VISIBLE);
                    // spinnerInbornType.setSelection(0);
                    spinnerOutbornType.setSelection(0);
                    etInfantComingFromOther.setText("");
                    rlInfantComingFromOther.setVisibility(View.GONE);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

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

        //Spinner for relationList
        spinnerInbornType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (inbornTypeList.get(position).equalsIgnoreCase(getString(R.string.other))) {
                    rlInfantComingFromOther.setVisibility(View.VISIBLE);
                } else {
                    etInfantComingFromOther.setText("");
                    rlInfantComingFromOther.setVisibility(View.GONE);

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

                if (AppSettings.getString(AppSettings.from).equalsIgnoreCase("1")) {
                    if (AppSettings.getString(AppSettings.checkHRN).equalsIgnoreCase("2")) {
                        if (llBaby.getVisibility() == View.GONE) {
                            RegistrationActivity.tvBabyAdmission.setBackgroundResource(R.drawable.rectangle_teal_selected);
                            RegistrationActivity.tvBabyAdmission.setTextColor(getResources().getColor(R.color.white));
                            llMotherSection.setVisibility(View.GONE);
                            llBaby.setVisibility(View.VISIBLE);
                            rlPrevious.setVisibility(View.VISIBLE);


                            if (type.equalsIgnoreCase("1") && llBaby.getVisibility() == View.VISIBLE) {
                                tvClickBabyPic.setText(getString(R.string.pleaseClickBabyPhoto));
                            } else if (type.equalsIgnoreCase("2")) {
                                tvLMPDateTitle.setText(getString(R.string.lmpDateWithoutAsterick));
                                tvClickBabyPic.setText(getString(R.string.pleaseClickBabyPhotoGuardian));
                            } else
                                tvClickBabyPic.setText(getString(R.string.pleaseClickBabyPhotoGuardian));

                            tvClickBabyPic.setText("");


                        } else {
                            if (typeBorn.isEmpty() && llBaby.getVisibility() == View.VISIBLE && spinnerAdmissionType.getSelectedItemPosition() == 0) {
                                AppUtils.showToastSort(mActivity, getString(R.string.errorAdmissionType));
                            } else if (typeBorn.equalsIgnoreCase(getString(R.string.inbornValue)) && spinnerInbornType.getSelectedItemPosition() == 0 && llMotherSection.getVisibility() == View.GONE) {
                                AppUtils.showToastSort(mActivity, getString(R.string.errorWard));
                            } else if (typeBorn.equalsIgnoreCase(getString(R.string.inbornValue)) && etInfantComingFromOther.getText().toString().isEmpty() && llMotherSection.getVisibility() == View.GONE && spinnerInbornType.getSelectedItem().equals(getString(R.string.other))) {
                                AppUtils.showToastSort(mActivity, getString(R.string.anyOtherPleaseSpecify));
                            } else if (typeBorn.equalsIgnoreCase(getString(R.string.outbornValue)) && etOutBornFrom.getText().toString().isEmpty() && llMotherSection.getVisibility() == View.GONE) {
                                AppUtils.showToastSort(mActivity, getString(R.string.referringHospital));
                            } else {
                                AlertHRN();
                            }
                        }
                    } else {
                        //Toast.makeText(mActivity,"Welcome",Toast.LENGTH_LONG).show();
                        ((RegistrationActivity) getActivity()).displayView(1);
                    }
                } else {
                    validationStep();
                }


                //validationStep();

                break;

            case R.id.rlPrevious:

                if (llBaby.getVisibility() == View.VISIBLE) {
                    RegistrationActivity.tvBabyAdmission.setBackgroundResource(0x00000000);
                    RegistrationActivity.tvBabyAdmission.setTextColor(getResources().getColor(R.color.blackNew));
                    llMotherSection.setVisibility(View.VISIBLE);
                    llBaby.setVisibility(View.GONE);
                    //  rlPrevious.setVisibility(View.GONE);
                    rlPrevious.setVisibility(View.VISIBLE);
                } else {

                    mActivity.onBackPressed();

                }

                break;

            //Step 1 Mother
            case R.id.rlMotherPic:

            case R.id.ivMotherPic:

                from = 0;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    fileUri = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(1));
                    Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    it.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    it.putExtra("android.intent.extras.CAMERA_FACING", 0);
                    startActivityForResult(it, 100);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri(1); // create a file to save the image
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                    intent.putExtra("android.intent.extras.CAMERA_FACING", 0);
                    startActivityForResult(intent, 100);
                }


//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
//                    fileUri = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(1));
//
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
//                    intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 0);
//                    intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//                    startActivityForResult(intent, 100);
//                } else {
//                    // create Intent to take a picture and return control to the calling application
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    fileUri = getOutputMediaFileUri(1); // create a file to save the image
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
//                    intent.putExtra("android.intent.extras.CAMERA_FACING", 0);
//                    // start the image capture Intent
//                    startActivityForResult(intent, 100);
//
//                }

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


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    fileUri = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(1));
                    Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    it.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    it.putExtra("android.intent.extras.CAMERA_FACING", 0);
                    startActivityForResult(it, 100);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri(1); // create a file to save the image
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                    intent.putExtra("android.intent.extras.CAMERA_FACING", 0);
                    startActivityForResult(intent, 100);
                }


//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
//                    fileUri = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(1));
//
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
//                    intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
//                    intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//                    startActivityForResult(intent, 100);
//                } else {
//                    // create Intent to take a picture and return control to the calling application
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    fileUri = getOutputMediaFileUri(1); // create a file to save the image
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
//                    intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
//                    // start the image capture Intent
//                    startActivityForResult(intent, 100);
//
//                }

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

        Log.v("sdfsdfsdf", type);
        Log.v("nnnnnasdasfsdnnnn", etMotherNumber.getText().toString());
        Log.v("fafasfasdfasd", etGuardianNumber.getText().toString());
        Log.v("rwertwert", etUMGNo.getText().toString());
        Log.v("vbdfgfgd", delivery);

        int weeks = 0;
        int weeksLmp = 0;

        if (!tvLmpDate.getText().toString().trim().isEmpty()) {
            weeks = AppUtils.getWeekDifference(tvLmpDate.getText().toString().trim(), tvDeliveryDate.getText().toString().trim());
            weeksLmp = AppUtils.getWeekDifference(tvLmpDate.getText().toString().trim(), AppUtils.getCurrentDateNew());

            Log.v("weekskfhsd", String.valueOf(weeksLmp));


        }

        int tableName = 0;
        try {
            tableName = Integer.parseInt(etBabyBirthWeight.getText().toString().trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        /*if(type.equalsIgnoreCase("1")||type.equalsIgnoreCase("3"))
        {
            saveStep1Step2();
        }
        else if(type.equalsIgnoreCase("2"))
        {
            saveStepUnknown();
        }*/

        saveStep1Step2();
        saveStep();

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        Date currentTime = Calendar.getInstance().getTime();
        getcurrentTime = formatter.format(currentTime);

        if (etMotherName.getText().toString().isEmpty() && (type.equalsIgnoreCase("1") || type.equalsIgnoreCase("3"))) {
            etMotherName.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorEnterMother));
        } else if (RegistrationActivity.admitted.equals(getString(R.string.yesValue))
                && encodedString.isEmpty() && type.equalsIgnoreCase("1")) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorClickPic));
        } else if (!AppUtils.isValidMobileNo(etMotherNumber.getText().toString())
                && type.equalsIgnoreCase("1")) {
            etMotherNumber.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorEnterMMobile));
        } else if (etUMGN.getText().toString().isEmpty() && type.equalsIgnoreCase("2")) {
            etUMGN.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorGuardianName));
        } else if (!AppUtils.isValidMobileNo(etUMGNo.getText().toString()) && type.equalsIgnoreCase("2")) {
            etUMGNo.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorGuardianMoNumber));
        } else if (etOName.getText().toString().isEmpty() && type.equalsIgnoreCase("2")) {
            etOName.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorOrganisationName));
        } else if (!AppUtils.isValidMobileNo(etONo.getText().toString()) && type.equalsIgnoreCase("2")) {
            etONo.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorOrganisationNumber));
        } else if (etOAddress.getText().toString().isEmpty() && type.equalsIgnoreCase("2")) {
            etOAddress.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorOrganisationAddress));
        } else if (etGuardianName.getText().toString().isEmpty() && type.equalsIgnoreCase("3")) {
            etGuardianName.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorGuardianName));
        } else if (relation.isEmpty() && type.equalsIgnoreCase("3")) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorRelation));
        } else if (etGuardianNumber.getText().toString().isEmpty() && type.equalsIgnoreCase("3")) {
            etGuardianName.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorGuardianMoNumber));
        } else if (!AppUtils.isValidMobileNo(etGuardianNumber.getText().toString()) && type.equalsIgnoreCase("3")) {
            etGuardianName.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorGuardianMoNumber));
        } else if (encodedBabyString.isEmpty() && llBaby.getVisibility() == View.VISIBLE) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorClickBaby));
        } else if (tvLmpDate.getText().toString().isEmpty() && llBaby.getVisibility() != View.VISIBLE && type.equals("1")) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorSelectLMPDate));
        } else if (!tvLmpDate.getText().toString().isEmpty() && (weeksLmp < 24 || weeksLmp > 44) && llBaby.getVisibility() != View.VISIBLE && tvLmpDate.getVisibility() == View.VISIBLE) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorLmpDate));
        } else if (tvDeliveryDate.getText().toString().isEmpty() && llBaby.getVisibility() != View.VISIBLE) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorSelectDelDate));
        } else if (!tvDeliveryDate.getText().toString().isEmpty() && (weeks < 24 || weeks > 44) && llBaby.getVisibility() != View.VISIBLE && tvDeliveryDate.getVisibility() == View.VISIBLE) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorProperDelDate));
        } else if (tvDeliveryTime.getText().toString().isEmpty() && llBaby.getVisibility() != View.VISIBLE) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorSelectDelTime));
        }
//        else if(getcurrentTime.compareTo(tvDeliveryTime.getText().toString())<=0) {
//                AppUtils.showToastSort(mActivity, getString(R.string.errorSelectDelTimegreater));
//        }
        else if (etBabyBirthWeight.getText().toString().isEmpty() && llBaby.getVisibility() == View.VISIBLE) {
            etBabyBirthWeight.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorEnterBWeight));
        } else if (gender.isEmpty() && llBaby.getVisibility() == View.VISIBLE) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorSelectGender));
        } else if ((tableName < 400 || tableName > 6000) && llBaby.getVisibility() == View.VISIBLE) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorWeightLimit));
        } else if (delivery.isEmpty() && llBaby.getVisibility() != View.VISIBLE && spinnerDeliveryType.getVisibility() == View.VISIBLE) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorSelectDelType));
        } else if (etHospitalRegistration.getText().toString().isEmpty()) {
            etHospitalRegistration.requestFocus();
            AppUtils.showToastSort(mActivity, getString(R.string.errorEnterHRN));
        } else if (typeBorn.isEmpty() && llBaby.getVisibility() == View.VISIBLE && spinnerAdmissionType.getSelectedItemPosition() == 0) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorAdmissionType));
        }

//        else if(spinnerInbornType.getSelectedItemPosition() == 0){
//            typeBorn="";
//        }

        else if (typeBorn.equalsIgnoreCase(getString(R.string.inbornValue)) && spinnerInbornType.getSelectedItemPosition() == 0 && llMotherSection.getVisibility() == View.GONE) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorWard));
        } else if (typeBorn.equalsIgnoreCase(getString(R.string.inbornValue)) && etInfantComingFromOther.getText().toString().equalsIgnoreCase("") && llMotherSection.getVisibility() == View.GONE && spinnerInbornType.getSelectedItem().equals(getString(R.string.other))) {
            AppUtils.showToastSort(mActivity, getString(R.string.anyOtherPleaseSpecify));
        }

        /*
        else if(typeBorn.equalsIgnoreCase(getString(R.string.outbornValue))&&typeOutBorn.isEmpty())
        {
            AppUtils.showToastSort(mActivity,getString(R.string.selectOutBornType));
        }*/
        else if (typeBorn.equalsIgnoreCase(getString(R.string.outbornValue)) && etOutBornFrom.getText().toString().isEmpty() && llMotherSection.getVisibility() == View.GONE) {
            AppUtils.showToastSort(mActivity, getString(R.string.referringHospital));
        } else if (nurseId.isEmpty() && llBaby.getVisibility() == View.VISIBLE) {
            AppUtils.showToastSort(mActivity, getString(R.string.errorSelectYourName));
        } else {
            if (llBaby.getVisibility() == View.GONE) {
                RegistrationActivity.tvBabyAdmission.setBackgroundResource(R.drawable.rectangle_teal_selected);
                RegistrationActivity.tvBabyAdmission.setTextColor(getResources().getColor(R.color.white));
                llMotherSection.setVisibility(View.GONE);
                llBaby.setVisibility(View.VISIBLE);
                rlPrevious.setVisibility(View.VISIBLE);

                if (type.equals("2"))
                    tvLMPDateTitle.setText(getString(R.string.lmpDateWithoutAsterick));
            } else {
                //createJsonForBabyRegistration();
                if (AppUtils.isNetworkAvailable(mActivity)) {
                    doMotherBabyRegistrationApi();
                } else {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                }
            }
        }

        AppSettings.putString(AppSettings.babyPic, encodedBabyString);

    }

    private JSONObject createJsonForBabyRegistration() {

        JSONObject json = new JSONObject();

        JSONObject jsonData = new JSONObject();

        try {

            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("motherName", etMotherName.getText().toString().trim());
            jsonData.put("isMotherAdmitted", RegistrationActivity.admitted);
            jsonData.put("notAdmittedReason", reason);
            jsonData.put("hospitalRegistrationNumber", etHospitalRegistration.getText().toString().trim());
            jsonData.put("motherMobileNumber", etMotherNumber.getText().toString().trim());
            jsonData.put("motherLmpDate", tvLmpDate.getText().toString().trim());
            jsonData.put("type", type);
            jsonData.put("staffId", nurseId);

            jsonData.put("guardianName", "");
            jsonData.put("guardianNumber", "");
            jsonData.put("relationWithChild", "");
            jsonData.put("relationWithChildOther", "");

            jsonData.put("organisationName", "");
            jsonData.put("organisationNumber", "");
            jsonData.put("organisationAddress", "");

            if (type.equals("3")) {
                jsonData.put("guardianName", etGuardianName.getText().toString().trim());
                jsonData.put("guardianNumber", etGuardianNumber.getText().toString().trim());
                jsonData.put("relationWithChild", relation);
                jsonData.put("relationWithChildOther", edReasionRelation.getText().toString().trim());
            } else if (type.equals("2")) {
                jsonData.put("guardianName", etUMGN.getText().toString().trim());
                jsonData.put("guardianNumber", etUMGNo.getText().toString().trim());
                jsonData.put("organisationName", etOName.getText().toString().trim());
                jsonData.put("organisationNumber", etONo.getText().toString().trim());
                jsonData.put("organisationAddress", etOAddress.getText().toString().trim());
            }

            jsonData.put("deliveryDate", tvDeliveryDate.getText().toString().trim());
            try {
                jsonData.put("deliveryTime",
                        AppUtils.convertTimeTo24HoursFormat(tvDeliveryTime.getText().toString().trim()));
            } catch (JSONException e) {
                e.printStackTrace();
                jsonData.put("deliveryTime", tvDeliveryTime.getText().toString().trim());
            }
            jsonData.put("babyGender", gender);
            jsonData.put("deliveryType", delivery);
            jsonData.put("babyWeight", etBabyBirthWeight.getText().toString().trim());
            jsonData.put("birthWeightAvail", "1");
            jsonData.put("reason", "");
            jsonData.put("localId", RegistrationActivity.uuid);
            jsonData.put("typeOfBorn", typeBorn);
//            jsonData.put("typeOfOutBorn",typeOutBorn);
            jsonData.put("infantComingFrom", "");

            if (typeBorn.equalsIgnoreCase(getString(R.string.inbornValue))) {
                jsonData.put("infantComingFrom", inbornTypeValue.get(spinnerInbornType.getSelectedItemPosition()));
            } else if (typeBorn.equalsIgnoreCase(getString(R.string.outbornValue))) {
                jsonData.put("infantComingFrom", etOutBornFrom.getText().toString().trim());
            }
            jsonData.put("infantComingFromOther", etInfantComingFromOther.getText().toString());
            jsonData.put("babyPhoto", encodedBabyString);
//            jsonData.put("babyPhoto", "");
            jsonData.put("motherPicture", encodedString);
//            jsonData.put("motherPicture", "");

            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));
            Log.v("doMotherBabyRegis", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    @SuppressLint("LongLogTag")
    private void doMotherBabyRegistrationApi() {

        WebServices.postApi(mActivity, AppUrls.motherBabyRegistration, createJsonForBabyRegistration(), true, true, new WebServicesCallback() {

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
                        AppSettings.putString(AppSettings.motherId, jsonObject.getString("motherId"));

                        ContentValues mContentValues = new ContentValues();

                        mContentValues.put(TableUser.tableColumn.uuid.toString(), String.valueOf(RegistrationActivity.uuid));
                        mContentValues.put(TableUser.tableColumn.motherId.toString(), jsonObject.getString("motherId"));
                        mContentValues.put(TableUser.tableColumn.babyId.toString(), jsonObject.getString("babyId"));
                        mContentValues.put(TableUser.tableColumn.mRStatus.toString(), "0");
                        mContentValues.put(TableUser.tableColumn.bRStatus.toString(), "0");

                        DatabaseController.insertUpdateData(mContentValues, TableUser.tableName,
                                TableUser.tableColumn.uuid.toString(), String.valueOf(RegistrationActivity.uuid));

                        saveBabyData();

                        if (type.equalsIgnoreCase("2")) {
                            saveUnknownMotherData();
                        } else {
                            saveMotherData();
                        }

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

    public void saveUnknownMotherData() {

        ContentValues contentValues = new ContentValues();
        contentValues.put(TableMotherRegistration.tableColumn.uuid.toString(), RegistrationActivity.uuid);
        contentValues.put(TableMotherRegistration.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        contentValues.put(TableMotherRegistration.tableColumn.guardianName.toString(), etUMGN.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.guardianNumber.toString(), etUMGNo.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.motherId.toString(), AppSettings.getString(AppSettings.motherId));

        contentValues.put(TableMotherRegistration.tableColumn.type.toString(), "2");
        contentValues.put(TableMotherRegistration.tableColumn.organisationAddress.toString(), etOAddress.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.organisationName.toString(), etOName.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.organisationNumber.toString(), etONo.getText().toString().trim());

        ///khushboo pandey//
        contentValues.put(TableMotherRegistration.tableColumn.motherLmpDate.toString(), tvLmpDate.getText().toString().trim());
        ///khushboo pandey//

        contentValues.put(TableMotherRegistration.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableMotherRegistration.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableMotherRegistration.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableMotherRegistration.tableColumn.status.toString(), "1");
        contentValues.put(TableMotherRegistration.tableColumn.isDataSynced.toString(), "3");
        contentValues.put(TableMotherRegistration.tableColumn.admittedSign.toString(), "");

        DatabaseController.insertUpdateData(contentValues, TableMotherRegistration.tableName, TableMotherRegistration.tableColumn.uuid.toString(), String.valueOf(RegistrationActivity.uuid));

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
        cvBabyAdm.put(TableBabyAdmission.tableColumn.infantComingFrom.toString(), "");


        if (typeBorn.equalsIgnoreCase(getString(R.string.inbornValue))) {
            cvBabyAdm.put(TableBabyAdmission.tableColumn.infantComingFrom.toString(), inbornTypeValue.get(spinnerInbornType.getSelectedItemPosition()));
        } else if (typeBorn.equalsIgnoreCase(getString(R.string.outbornValue))) {
            cvBabyAdm.put(TableBabyAdmission.tableColumn.infantComingFrom.toString(), etOutBornFrom.getText().toString().trim());
        }

        DatabaseController.insertUpdateData(cvBabyAdm, TableBabyAdmission.tableName,
                TableBabyAdmission.tableColumn.uuid.toString(), String.valueOf(RegistrationActivity.uuid));

    }

    public void saveMotherData() {

        ContentValues contentValues = new ContentValues();

        contentValues.put(TableMotherRegistration.tableColumn.uuid.toString(), RegistrationActivity.uuid);
        contentValues.put(TableMotherRegistration.tableColumn.motherId.toString(), AppSettings.getString(AppSettings.motherId));
        contentValues.put(TableMotherRegistration.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        contentValues.put(TableMotherRegistration.tableColumn.motherName.toString(), etMotherName.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.isMotherAdmitted.toString(), RegistrationActivity.admitted);
        contentValues.put(TableMotherRegistration.tableColumn.reasonForNotAdmitted.toString(), reason);
        contentValues.put(TableMotherRegistration.tableColumn.motherPicture.toString(), encodedString);
        contentValues.put(TableMotherRegistration.tableColumn.motherMCTSNumber.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.motherAadharNumber.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.motherDob.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.motherAge.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.motherEducation.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.motherCaste.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.motherReligion.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.motherMoblieNo.toString(), etMotherNumber.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.fatherName.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.fatherAadharNumber.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.fatherMoblieNo.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.rationCardType.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.guardianName.toString(), etGuardianName.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.guardianNumber.toString(), etGuardianNumber.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.guardianRelation.toString(), relation);
        contentValues.put(TableMotherRegistration.tableColumn.relationWithChildOther.toString(), edReasionRelation.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.presentCountry.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.presentState.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.presentResidenceType.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.presentAddress.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.presentVillageName.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.presentBlockName.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.presentDistrictName.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.presentPinCode.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.presentAddressNearBy.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.permanentResidenceType.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.permanentCountry.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.permanentState.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.permanentAddress.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.permanentVillageName.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.permanentBlockName.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.permanentDistrictName.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.permanentPinCode.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.permanentAddressNearBy.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.staffId.toString(), nurseId);
        contentValues.put(TableMotherRegistration.tableColumn.type.toString(), type);
        contentValues.put(TableMotherRegistration.tableColumn.para.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.gravida.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.abortion.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.live.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.multipleBirth.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.consanguinity.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.motherWeight.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.ageOfMarriage.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.estimatedDateOfDelivery.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.birthSpacing.toString(), "");

        contentValues.put(TableMotherRegistration.tableColumn.motherLmpDate.toString(), tvLmpDate.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.typeOfBorn.toString(), typeBorn);
        contentValues.put(TableMotherRegistration.tableColumn.typeOfOutBorn.toString(), typeOutBorn);

        if (typeBorn.equalsIgnoreCase(getString(R.string.inbornValue))) {
            contentValues.put(TableMotherRegistration.tableColumn.infantComingFrom.toString(), String.valueOf(spinnerInbornType.getSelectedItem()));
        } else if (typeBorn.equalsIgnoreCase(getString(R.string.outbornValue))) {
            contentValues.put(TableMotherRegistration.tableColumn.infantComingFrom.toString(), etOutBornFrom.getText().toString().trim());
        }
        contentValues.put(TableMotherRegistration.tableColumn.infantComingFromOther.toString(), etInfantComingFromOther.getText().toString().trim());

        contentValues.put(TableMotherRegistration.tableColumn.motherDeliveryDistrict.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.sameaddress.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.ashaID.toString(), "");

        contentValues.put(TableMotherRegistration.tableColumn.ashaName.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.ashaNumber.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.facilityID.toString(), "");

        contentValues.put(TableMotherRegistration.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableMotherRegistration.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableMotherRegistration.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableMotherRegistration.tableColumn.status.toString(), "1");
        contentValues.put(TableMotherRegistration.tableColumn.admittedSign.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.isDataSynced.toString(), "3");
        contentValues.put(TableMotherRegistration.tableColumn.motherDeliveryPlace.toString(), "");


        Log.v("infantcomingfroms", String.valueOf(spinnerInbornType.getSelectedItem()));
        Log.v("tyodfsd", typeBorn);

        DatabaseController.insertUpdateData(contentValues, TableMotherRegistration.tableName,
                TableMotherRegistration.tableColumn.uuid.toString(), String.valueOf(RegistrationActivity.uuid));

        if (type.equalsIgnoreCase("1")) {
            ContentValues cvMotherAdm = new ContentValues();

            cvMotherAdm.put(TableMotherAdmission.tableColumn.uuid.toString(), RegistrationActivity.uuid);
            cvMotherAdm.put(TableMotherAdmission.tableColumn.serverId.toString(), AppSettings.getString(AppSettings.mAdmId));
            cvMotherAdm.put(TableMotherAdmission.tableColumn.motherId.toString(), AppSettings.getString(AppSettings.motherId));
            cvMotherAdm.put(TableMotherAdmission.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
            cvMotherAdm.put(TableMotherAdmission.tableColumn.hospitalRegistrationNumber.toString(), etHospitalRegistration.getText().toString().trim());
            cvMotherAdm.put(TableMotherAdmission.tableColumn.status.toString(), "4");

            DatabaseController.insertUpdateData(cvMotherAdm, TableMotherAdmission.tableName,
                    TableMotherAdmission.tableColumn.uuid.toString(), String.valueOf(RegistrationActivity.uuid));
        }
    }

    private void saveStep1Step2() {

        Log.v("RegistrationActivity", RegistrationActivity.uuid);

        if (!etMotherName.getText().toString().isEmpty() || !etUMGN.getText().toString().isEmpty()) {
            ContentValues mContentValues = new ContentValues();
            mContentValues.put(TableUser.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
            mContentValues.put(TableUser.tableColumn.uuid.toString(), String.valueOf(RegistrationActivity.uuid));
            mContentValues.put(TableUser.tableColumn.motherId.toString(), "");
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
            mContentValues.put(TableUser.tableColumn.isSibling.toString(), "0");
            mContentValues.put(TableUser.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
            mContentValues.put(TableUser.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());
            mContentValues.put(TableUser.tableColumn.status.toString(), "1");
            mContentValues.put(TableUser.tableColumn.fromStep.toString(), AppSettings.getString(AppSettings.from));

            if (AppSettings.getString(AppSettings.from).equals("1")) {
                mContentValues.put(TableUser.tableColumn.fromStep.toString(), "0");
            }

            if (!DatabaseController.getFromData(RegistrationActivity.uuid).equalsIgnoreCase("")) {
                mContentValues.put(TableUser.tableColumn.fromStep.toString(), DatabaseController.getFromData(RegistrationActivity.uuid));
            }

            DatabaseController.insertUpdateData(mContentValues, TableUser.tableName,
                    TableUser.tableColumn.uuid.toString(), String.valueOf(RegistrationActivity.uuid));

        }
    }

    private void saveStep() {

        ContentValues mContentValues = new ContentValues();

        mContentValues.put(TableUser.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        mContentValues.put(TableUser.tableColumn.uuid.toString(), String.valueOf(RegistrationActivity.uuid));
        mContentValues.put(TableUser.tableColumn.step3.toString(), createJsonForBabyRegistration().toString());
        mContentValues.put(TableUser.tableColumn.isSibling.toString(), "0");
        mContentValues.put(TableUser.tableColumn.bRStatus.toString(), "1");

        DatabaseController.insertUpdateData(mContentValues, TableUser.tableName,
                TableUser.tableColumn.uuid.toString(), String.valueOf(RegistrationActivity.uuid));

    }

    public void AlertHRN() {
        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_hrn);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);

        Spinner spinnerEnteredByNurse = dialog.findViewById(R.id.spinnerEnteredByNurse);

        final EditText etHospitalRegistrationNo = dialog.findViewById(R.id.etHospitalRegistration);

        nurseListId.clear();
        nurseListId.add(getString(R.string.erroselectNurse));
        nurseListId.addAll(DatabaseController.getNurseIdCheckedInData());

        nurseListName.clear();
        nurseListName.add(getString(R.string.erroselectNurse));
        nurseListName.addAll(DatabaseController.getNurseNameCheckedInData());

        spinnerEnteredByNurse.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, nurseListName));
        spinnerEnteredByNurse.setSelection(0);

        //Spinner for relationList
        spinnerEnteredByNurse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if (position == 0) {
                    AppSettings.putString(AppSettings.nurseId, "");
                } else {
                    AppSettings.putString(AppSettings.nurseId, nurseListId.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        etHospitalRegistrationNo.setKeyListener(DigitsKeyListener.getInstance("0123456789/"));

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        rlOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(mActivity);
                if (spinnerEnteredByNurse.getSelectedItemPosition() == 0) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectYourName));
                } else if (etHospitalRegistrationNo.getText().toString().isEmpty()) {
                    etHospitalRegistrationNo.requestFocus();
                    AppUtils.showToastSort(mActivity, getString(R.string.errorEnterHRN));
                } else {
                    dialog.dismiss();

                    etHospitalRegistration.setText(etHospitalRegistrationNo.getText().toString().trim());

                    AppSettings.putString(AppSettings.hrn, etHospitalRegistrationNo.getText().toString().trim());
                    AppSettings.putString(AppSettings.nurseId, nurseListId.get(spinnerEnteredByNurse.getSelectedItemPosition()));

                    if (AppUtils.isNetworkAvailable(mActivity)) {
                        doReAdmitApi();
                    } else {
                        AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                    }
                }
            }
        });

        rlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                hideSoftKeyboard(mActivity);
            }
        });
    }

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

    public void setMale() {

        defaultGender();
        gender = getString(R.string.maleValue);
        ivMale.setImageResource(R.drawable.ic_check_box_selected);
        ivMale.setColorFilter(getResources().getColor(R.color.r_color), PorterDuff.Mode.SRC_IN);
        ivMalePic.setColorFilter(getResources().getColor(R.color.r_color), PorterDuff.Mode.SRC_IN);
    }

    public void setFemale() {

        defaultGender();
        gender = getString(R.string.femaleValue);
        ivFemale.setImageResource(R.drawable.ic_check_box_selected);
        ivFemale.setColorFilter(getResources().getColor(R.color.r_color), PorterDuff.Mode.SRC_IN);
        ivFemalePic.setColorFilter(getResources().getColor(R.color.oo_color), PorterDuff.Mode.SRC_IN);
    }

    public void setAmbiguous() {

        defaultGender();
        gender = getString(R.string.indefiniteValue);
        ivAmbiguous.setImageResource(R.drawable.ic_check_box_selected);
        ivAmbiguous.setColorFilter(getResources().getColor(R.color.r_color), PorterDuff.Mode.SRC_IN);
        ivAmbiguousPic.setImageResource(R.drawable.ic_ambiguous_color);
    }

    //Alert For Referral
    private void AlertReferral() {
        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_yes_no);
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
        TextView tvOk = dialog.findViewById(R.id.tvOk);
        TextView tvCancel = dialog.findViewById(R.id.tvCancel);

        //RelativeLayout
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        tvMessage.setText(getString(R.string.areYouReferred));
        tvOk.setText(getString(R.string.yes));
        tvCancel.setText(getString(R.string.no));

        ivImage.setVisibility(View.VISIBLE);
        ivImage.setImageResource(R.drawable.ic_warning);

        rlCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();
                AlertMotherCondition();
            }
        });

        rlOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();
                startActivity(new Intent(mActivity, ReferralListActivity.class));
                getActivity().finish();
            }
        });
    }

    //Alert For Mother Condition
    private void AlertMotherCondition() {
        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        dialog.setContentView(R.layout.alert_mother_condition);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        //TextView
        TextView tvSubmit = dialog.findViewById(R.id.tvSubmit);

        //EditText
        EditText etReason = dialog.findViewById(R.id.etReason);

        //ImageView
        ImageView ivImage = dialog.findViewById(R.id.ivImage);

        //Spinner
        Spinner spinnerMotherCondition = dialog.findViewById(R.id.spinnerMotherCondition);

        //RelativeLayout
        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);
        RelativeLayout rlReason = dialog.findViewById(R.id.rlReason);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        motherConditionList.clear();
        motherConditionList.add(getString(R.string.reason));
        motherConditionList.add(getString(R.string.dead));
        motherConditionList.add(getString(R.string.motherNotReferred));
        motherConditionList.add(getString(R.string.motherInWard));
        motherConditionList.add(getString(R.string.unknownOrphan));
        motherConditionList.add(getString(R.string.other));

        motherConditionListValue.clear();
        motherConditionListValue.add(getString(R.string.reason));
        motherConditionListValue.add(getString(R.string.dead));
        motherConditionListValue.add(getString(R.string.motherNotReferred));
        motherConditionListValue.add(getString(R.string.motherInWard));
        motherConditionListValue.add(getString(R.string.unknownOrphan));
        motherConditionListValue.add(getString(R.string.other));

        spinnerMotherCondition.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, motherConditionList));
        spinnerMotherCondition.setSelection(0);

        //Spinner for District
        spinnerMotherCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if (position == 0) {
                    motherCondition = "";
                    etReason.setVisibility(View.GONE);
                    etReason.setText("");
                } else {
                    motherCondition = motherConditionListValue.get(position);

                    if (position == 5) {
                        etReason.setVisibility(View.VISIBLE);
                    } else {
                        etReason.setText("");
                        etReason.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        ivImage.setVisibility(View.VISIBLE);
        ivImage.setImageResource(R.drawable.ic_warning);

        rlCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                rlReason.setVisibility(View.VISIBLE);
                tvSubmit.setVisibility(View.VISIBLE);
                tvLMPDateTitle.setText(getString(R.string.lmpDateWithoutAsterick));
            }
        });

        rlOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                RegistrationActivity.admitted = getString(R.string.yesValue);
                type = "1";
                reason = "";
                llMotherSection.setVisibility(View.VISIBLE);
                llMother.setVisibility(View.VISIBLE);
                tvSubmit.setVisibility(View.GONE);
                rlReason.setVisibility(View.GONE);
                dialog.dismiss();

                if (type.equalsIgnoreCase("1")) {
                    tvClickBabyPic.setText(getString(R.string.pleaseClickBabyPhoto));
                } else {
                    tvClickBabyPic.setText(getString(R.string.pleaseClickBabyPhotoGuardian));
                }
            }
        });

        tvSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (spinnerMotherCondition.getSelectedItemPosition() == 0) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectReason));
                } else if (spinnerMotherCondition.getSelectedItemPosition() == 5
                        && etReason.getText().toString().trim().isEmpty()) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorEnterReason));
                } else {
                    dialog.dismiss();

                    if (motherCondition.equalsIgnoreCase(getString(R.string.unknownOrphan))) {
                        RegistrationActivity.admitted = getString(R.string.noValue);
                        type = "2";
                        reason = getString(R.string.unknownOrphan);
                        llMotherSection.setVisibility(View.VISIBLE);
                        llUnknown.setVisibility(View.VISIBLE);
                        rlMotherName.setVisibility(View.GONE);
                    } else {
                        RegistrationActivity.admitted = getString(R.string.noValue);
                        type = "3";
                        int pos = spinnerMotherCondition.getSelectedItemPosition();
                        reason = motherConditionListValue.get(pos);
                        if (spinnerMotherCondition.getSelectedItemPosition() == 5
                                && etReason.getText().toString().trim().isEmpty()) {
                            reason = etReason.getText().toString().trim();
                        }
                        llMotherSection.setVisibility(View.VISIBLE);
                        llGuardian.setVisibility(View.VISIBLE);
                        rlMotherName.setVisibility(View.VISIBLE);
                    }
                }

                if (type.equalsIgnoreCase("1")) {
                    tvClickBabyPic.setText(getString(R.string.pleaseClickBabyPhoto));
                } else {
                    tvClickBabyPic.setText(getString(R.string.pleaseClickBabyPhotoGuardian));
                }
            }
        });
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
                String picturePath = fileUri.getPath().toString();

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
                //Bitmap
                Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, options);

                Matrix matrix = new Matrix();
                matrix.postRotate(getImageOrientation(picturePath));
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 30, bao);

                rotatedBitmap = AppUtils.setWaterMark(rotatedBitmap, mActivity);

                if (from == 0) {
                    ivMotherPic.setImageBitmap(rotatedBitmap);
                    encodedString = getEncoded64ImageStringFromBitmap(rotatedBitmap);
                    rlMotherPic.setBackgroundResource(0);
                } else {
                    ivBabyPic.setImageBitmap(rotatedBitmap);
                    encodedBabyString = getEncoded64ImageStringFromBitmap(rotatedBitmap);
                    rlBabyPic.setBackgroundResource(0);
                }

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
        try {
            Log.d("AppConstants.jsonObject", AppConstants.jsonObject.toString());
            type = AppConstants.jsonObject.getString("type");
            AppSettings.putString(AppSettings.babyId, AppConstants.jsonObject.getString("babyId"));
            AppSettings.putString(AppSettings.motherId, AppConstants.jsonObject.getString("motherId"));
            if (AppConstants.jsonObject.getString("babyPhoto").isEmpty()) {
                encodedBabyString = "";
            } else {
                try {
                    encodedBabyString = "";
                    downloadImage(AppConstants.jsonObject.getString("babyPhoto"), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                    encodedBabyString = "";
                }
            }

            llMotherSection.setVisibility(View.VISIBLE);

            if (type.equalsIgnoreCase("2")) {
                llUnknown.setVisibility(View.VISIBLE);
                llGuardian.setVisibility(View.GONE);
                llBaby.setVisibility(View.GONE);
                llMother.setVisibility(View.GONE);

                RegistrationActivity.admitted = getString(R.string.noValue);

                //reason = getString(R.string.motherStatus3);
            } else {
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

                etMotherName.setText(AppConstants.jsonObject.getString("motherName"));

                etGuardianName.setText(AppConstants.jsonObject.getString("guardianName"));
                etGuardianNumber.setText(AppConstants.jsonObject.getString("guardianNumber"));

                relation = AppConstants.jsonObject.getString("guardianRelation");

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
                    edReasionRelation.setText(AppConstants.jsonObject.getString("guardianRelationOther"));
                } else {
                    edReasionRelation.setVisibility(View.GONE);
                }
                reason = AppConstants.jsonObject.getString("notAdmittedReason");
            }

            rlNurse.setVisibility(View.GONE);

            etHospitalRegistration.setText(AppConstants.jsonObject.getString("babyFileId"));

            etMotherNumber.setText(AppConstants.jsonObject.getString("motherMobileNumber"));

            etUMGN.setText(AppConstants.jsonObject.getString("guardianName"));
            etUMGNo.setText(AppConstants.jsonObject.getString("guardianNumber"));
            etOName.setText(AppConstants.jsonObject.getString("organisationName"));
            etONo.setText(AppConstants.jsonObject.getString("organisationNumber"));
            etOAddress.setText(AppConstants.jsonObject.getString("organisationAddress"));

            tvDeliveryDate.setText(AppConstants.jsonObject.getString("deliveryDate"));
//            tvDeliveryTime.setText(AppConstants.jsonObject.getString("deliveryTime"));
            tvDeliveryTime.setText(AppUtils.convertTimeTo12HoursFormat(AppConstants.jsonObject.getString("deliveryTime")));
            tvLmpDate.setText(AppConstants.jsonObject.getString("motherLmpDate"));

            defaultGender();
            gender = AppConstants.jsonObject.getString("babyGender");

            if (gender.equalsIgnoreCase(getString(R.string.maleValue))) {
                setMale();
            } else if (gender.equalsIgnoreCase(getString(R.string.femaleValue))) {
                setFemale();
            } else if (gender.equalsIgnoreCase(getString(R.string.indefiniteValue))) {
                setAmbiguous();
            }

            delivery = AppConstants.jsonObject.getString("deliveryType");

            int delPos = 0;

            for (int j = 0; j < deliveryTypeValue.size(); j++) {
                if (deliveryTypeValue.get(j).equalsIgnoreCase(delivery)) {
                    delPos = j;

                    break;
                }
            }

            spinnerDeliveryType.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, deliveryType));
            spinnerDeliveryType.setSelection(delPos);

            etBabyBirthWeight.setText(AppConstants.jsonObject.getString("babyWeight"));

////            typeBorn = AppConstants.jsonObject.getString("typeOfBorn");
////
//            int pos = 0;
////
////            for (int j = 0; j < typeOfBornValue.size(); j++) {
////                if (typeOfBornValue.get(j).equalsIgnoreCase(typeBorn)) {
////                    pos = j;
////                    break;
////                }
////            }

            spinnerAdmissionType.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, typeOfBorn));
            spinnerAdmissionType.setSelection(0);

            typeOutBorn = AppConstants.jsonObject.getString("typeOfOutBorn");

//            pos = 0;
//
//            for (int j = 0; j < typeOfOutBornValue.size(); j++) {
//                if (typeOfOutBornValue.get(j).equalsIgnoreCase(typeOutBorn)) {
//                    pos = j;
//                    break;
//                }
//            }

            spinnerOutbornType.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, typeOfOutBorn));
            spinnerOutbornType.setSelection(0);

//            if (typeBorn.equalsIgnoreCase(getString(R.string.inbornValue))) {
//                pos = 0;
//
//                for (int j = 0; j < inbornTypeValue.size(); j++) {
//                    if (inbornTypeValue.get(j).equalsIgnoreCase(AppConstants.jsonObject.getString("infantComingFrom"))) {
//                        pos = j;
//                        break;
//                    }
//                }
//
//                spinnerInbornType.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, inbornTypeValue));
//                spinnerInbornType.setSelection(pos);

//            } else if (typeBorn.equalsIgnoreCase(getString(R.string.outbornValue))) {
//                etOutBornFrom.setText(AppConstants.jsonObject.getString("infantComingFrom"));
//            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setStep1and2Values() {

        ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
        arrayList.addAll(DatabaseController.getStepData(AppSettings.getString(AppSettings.uuid)));
        etHospitalRegistration.setText(AppSettings.getString(AppSettings.hrn));

        for (int i = 0; i < arrayList.size(); i++) {

            JSONObject jsonObjectMain = null;
            JSONObject jsonObject = null;

            try {

                AppSettings.putString(AppSettings.motherId, arrayList.get(i).get("motherId"));
                AppSettings.putString(AppSettings.babyId, arrayList.get(i).get("babyId"));


                jsonObjectMain = new JSONObject(arrayList.get(i).get("step1"));

                JSONObject jsonObjectMother = jsonObjectMain.getJSONObject(AppConstants.projectName);

                type = jsonObjectMother.getString("type");

                llMotherSection.setVisibility(View.VISIBLE);

                if (type.equalsIgnoreCase("2")) {
                    llUnknown.setVisibility(View.VISIBLE);
                    llGuardian.setVisibility(View.GONE);
                    llBaby.setVisibility(View.GONE);
                    llMother.setVisibility(View.GONE);
                    rlMotherName.setVisibility(View.GONE);

                    RegistrationActivity.admitted = getString(R.string.noValue);
                    //reason = getString(R.string.motherStatus3);

                    etUMGN.setText(jsonObjectMother.getString("guardianName"));
                    etUMGNo.setText(jsonObjectMother.getString("guardianNumber"));
                    etOName.setText(jsonObjectMother.getString("organisationName"));
                    etONo.setText(jsonObjectMother.getString("organisationNumber"));
                    etOAddress.setText(jsonObjectMother.getString("organisationAddress"));
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

                    if (type.equalsIgnoreCase("1")) {
                        tvClickBabyPic.setText(getString(R.string.pleaseClickBabyPhoto));
                    } else {
                        tvClickBabyPic.setText(getString(R.string.pleaseClickBabyPhotoGuardian));
                    }

                    etMotherName.setText(jsonObjectMother.getString("motherName"));

                    reason = jsonObjectMother.getString("notAdmittedReason");

                    if (jsonObjectMother.getString("motherPicture").isEmpty()) {
                        encodedString = "";
                    } else {
                        try {
                            encodedString = jsonObjectMother.getString("motherPicture");
                            byte[] decodedString = Base64.decode(jsonObjectMother.getString("motherPicture"), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            ivMotherPic.setImageBitmap(decodedByte);
                        } catch (Exception e) {
                            e.printStackTrace();
                            encodedString = "";
                        }
                    }

                    etMotherNumber.setText(jsonObjectMother.getString("motherMobileNumber"));

                    etGuardianName.setText(jsonObjectMother.getString("guardianName"));
                    etGuardianNumber.setText(jsonObjectMother.getString("guardianNumber"));

                    relation = jsonObjectMother.getString("relationWithChild");

                    int pos = 0;

                    for (int j = 0; j < relationValue.size(); j++) {
                        if (relationValue.get(j).equalsIgnoreCase(relation)) {
                            pos = j;
                            break;
                        }
                    }

                    spinnerRelation.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, relationList));
                    spinnerRelation.setSelection(pos);


                }
                etHospitalRegistration.setText(jsonObjectMother.getString("hospitalRegistrationNumber"));


                jsonObject = new JSONObject(arrayList.get(i).get("step2"));

                Log.v("step11", String.valueOf(jsonObject));

                JSONObject jsonObjectBaby = jsonObject.getJSONObject(AppConstants.projectName);

                Log.v("jsonObjectBaby", String.valueOf(jsonObjectBaby));

                tvDeliveryDate.setText(jsonObjectBaby.getString("deliveryDate"));
                tvDeliveryTime.setText(AppUtils.convertTimeTo12HoursFormat(jsonObjectBaby.getString("deliveryTime")));
                tvLmpDate.setText(jsonObjectBaby.getString("motherLmpDate"));
                etBabyBirthWeight.setText(jsonObjectBaby.getString("babyWeight"));
                encodedBabyString = jsonObjectBaby.getString("babyPhoto");

                if (relation.equals(getString(R.string.otherValue))) {
                    edReasionRelation.setVisibility(View.VISIBLE);
                    edReasionRelation.setText(jsonObjectBaby.getString("relationWithChildOther"));
                } else {
                    edReasionRelation.setVisibility(View.GONE);
                }

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


                typeBorn = jsonObjectBaby.getString("typeOfBorn");

                Log.v("typeBorngrtg", String.valueOf(typeBorn));

                int pos = 0;
                for (int j = 0; j < typeOfBornValue.size(); j++) {
                    if (typeOfBornValue.get(j).equalsIgnoreCase(typeBorn)) {
                        pos = j;
                        break;
                    }
                }
                spinnerAdmissionType.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, typeOfBorn));
                spinnerAdmissionType.setSelection(pos);

//                typeOutBorn = jsonObjectBaby.getString("typeOfOutBorn");
//                int posl = 0;
//                for (int j = 0; j < typeOfOutBornValue.size(); j++) {
//                    if (typeOfOutBornValue.get(j).equalsIgnoreCase(typeOutBorn)) {
//                        posl = j;
//                        break;
//                    }
//                }
//                spinnerOutbornType.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, typeOfOutBorn));
//                spinnerOutbornType.setSelection(posl);

                Log.v("typeofbornsds", typeBorn);

                if (typeBorn.equalsIgnoreCase(getString(R.string.inbornValue))) {

                    Log.v("infantcomingfromPos", String.valueOf(pos));


                    try {


                        int post = 0;
                        for (int j = 0; j < inbornTypeValue.size(); j++) {
                            if (inbornTypeValue.get(j).equalsIgnoreCase(jsonObjectBaby.getString("infantComingFrom"))) {
                                post = j;
                                break;
                            }
                        }

                        Log.v("inbornTypeValueSizes", String.valueOf(inbornTypeValue.size()));

                        try {
                            spinnerInbornType.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, inbornTypeValue));
                            spinnerInbornType.setSelection(post);
                        } catch (Exception e) {
                            Log.v("edfvsd", e.getMessage());
                            e.printStackTrace();
                        }
                        Log.v("fsgsgsdfgdf", String.valueOf(spinnerInbornType.getSelectedItem()));
                        if (spinnerInbornType.getSelectedItem().equals(getString(R.string.other))) {
                            rlInfantComingFromOther.setVisibility(View.VISIBLE);
                            etInfantComingFromOther.setText(jsonObjectBaby.getString("infantComingFromOther"));
                        }
                    } catch (Exception e) {
                        Log.v("edfvsdfsgd", e.getMessage());
                        e.printStackTrace();
                    }


                } else if (typeBorn.equalsIgnoreCase(getString(R.string.outbornValue))) {
                    Log.v("infantcomingfrom", jsonObjectBaby.getString("infantComingFrom"));

                    etOutBornFrom.setText(jsonObjectBaby.getString("infantComingFrom"));
                }

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

    private JSONObject createJsonForReAdmit() {

        JSONObject json = new JSONObject();

        JSONObject jsonData = new JSONObject();

        try {

            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("babyId", AppSettings.getString(AppSettings.babyId));
            jsonData.put("hospitalRegistrationNumber", AppSettings.getString(AppSettings.hrn));
            jsonData.put("nurseId", AppSettings.getString(AppSettings.nurseId));
            jsonData.put("localId", RegistrationActivity.uuid);

            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));

            Log.v("doMotherBabyRegis", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    private void doReAdmitApi() {
        WebServices.postApi(mActivity, AppUrls.registeredBabyAdmission, createJsonForReAdmit(), true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        saveStep1Step2();
                        saveStep();

                        AppSettings.putString(AppSettings.bAdmId, jsonObject.getString("babyAdmissionId"));

                        ContentValues mContentValues = new ContentValues();

                        mContentValues.put(TableUser.tableColumn.uuid.toString(), String.valueOf(RegistrationActivity.uuid));
                        mContentValues.put(TableUser.tableColumn.motherId.toString(), AppSettings.getString(AppSettings.motherId));
                        mContentValues.put(TableUser.tableColumn.babyId.toString(), AppSettings.getString(AppSettings.babyId));
                        mContentValues.put(TableUser.tableColumn.mRStatus.toString(), "0");
                        mContentValues.put(TableUser.tableColumn.bRStatus.toString(), "0");

                        DatabaseController.insertUpdateData(mContentValues, TableUser.tableName,
                                TableUser.tableColumn.uuid.toString(), String.valueOf(RegistrationActivity.uuid));

                        saveBabyData();

                        if (type.equalsIgnoreCase("2")) {
                            saveUnknownMotherData();
                        } else {
                            saveMotherData();
                        }

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

    private void downloadImage(String url, int from) {
        WebServices.downloadImageApi(mActivity, url, new WebServicesImageCallback() {

            @Override
            public void OnFail(String responce) {

                AppUtils.showToastSort(mActivity, responce);
            }

            @Override
            public void OnBitmapSuccess(Bitmap bitmap) {

                Log.d("bitmap-" + from, String.valueOf(bitmap));

                if (from == 2) {
                    ivMotherPic.setImageBitmap(bitmap);
                    encodedString = getEncoded64ImageStringFromBitmap(bitmap);
                    rlMotherPic.setBackgroundResource(0);

                    AndroidNetworking.evictAllBitmap(); // clear LruCache

                } else if (from == 1) {
                    ivBabyPic.setImageBitmap(bitmap);
                    encodedBabyString = getEncoded64ImageStringFromBitmap(bitmap);
                    rlBabyPic.setBackgroundResource(0);

                    try {
                        if (AppConstants.jsonObject.getString("motherPicture").isEmpty()) {
                            encodedString = "";
                        } else {
                            try {
                                encodedString = "";
                                downloadImage(AppConstants.jsonObject.getString("motherPicture"), 2);
                            } catch (Exception e) {
                                e.printStackTrace();
                                encodedString = "";
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public boolean checktimings(String time, String endtime) {

        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if (date1.before(date2)) {
                return true;
            } else {

                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public class adapterSpinner extends ArrayAdapter<String> {

        ArrayList<String> data;

        public adapterSpinner(Context context, int textViewResourceId, ArrayList<String> arraySpinner_time) {

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


