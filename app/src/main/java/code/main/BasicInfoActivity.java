package code.main;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.kmcapp.android.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import code.algo.SyncMotherRecord;
import code.common.LocaleHelper;
import code.common.Verhoeff;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableMotherRegistration;
import code.utils.AppUtils;
import code.view.BaseActivity;

import static code.utils.AppUtils.formatDate;


public class BasicInfoActivity extends BaseActivity implements View.OnClickListener {

    //LinearLayout
    LinearLayout llStep1,llStep2,llAddress;

    //ScrollView
    ScrollView scrollView;

    //TextView
    TextView tvBasicInfo,tvContactInfo;

    int from=0;

    //String
    public String picturePath="",filename="",ext="",encodedString="",encodedBabyString="";

    //Uri
    Uri picUri,fileUri;

    //Static Int
    private static final int CameraCode = 100, MediaType = 1;
    //Bitmap
    Bitmap bitmap;

    //Step 1
    //EditText
    EditText etMName,etFName,etMAadhar,etFAadhar,etMCTS,
            etGravida,etPara,etAbortion,etLive,etMWeight,etMMAge;

    private EditText etDobNotKnown;

    //Spinner
    private Spinner spinnerCaste,spinnerReligion,spinnerEducation,spinnerBirthSpacing;

    //TextView
    TextView tvDOB;

    //ImageView
    ImageView ivDob,ivCamera,ivMultipleYes,ivMultipleNo, ivMDobDelete,ivNext;

    //RelativeLayout
    RelativeLayout rlMultipleYes,rlMultipleNo,rlNext,rlPrevious,rlFacility,rlFacilityType,rlDistrict,rlBlock,rlPlaceOfDelivery;

    //ArrayList
    public static ArrayList<String> religionName = new ArrayList<String>();
    public static ArrayList<String> religionNameValue = new ArrayList<String>();
    public static ArrayList<String> casteName = new ArrayList<String>();
    public static ArrayList<String> casteNameValue = new ArrayList<String>();
    public static ArrayList<String> educationName = new ArrayList<String>();
    public static ArrayList<String> educationNameValue = new ArrayList<String>();
    public static ArrayList<String> birthSpacing = new ArrayList<String>();
    public static ArrayList<String> birthSpacingValue = new ArrayList<String>();

    String type="1",multiple="";

    ArrayList<HashMap<String, String>> motherDetail = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> motherAdmission = new ArrayList<HashMap<String, String>>();

    //Step 2
    //EditText
    EditText etMobileNumber,etFMobileNumber,etGuardianName,etGuardianNumber,etPresentAddress,etPermanentAddress,etPresentNear,etPermanentNear
            ,etPresentPincode,etPermanentPincode;

    //Spinner
    Spinner spinnerRelation,spinnerPresentHamlet,spinnerPresentGram,spinnerPresentBlock,spinnerPresentDistrict,
            spinnerPermanentHamlet,spinnerPermanentGram,spinnerPermanentBlock,spinnerPermanentDistrict,spinnerAsha
            ,spinnerNurse,spinnerRation,spinnerPresentCountry,spinnerPresentState,spinnerPermanentCountry,spinnerPermanentState;

    //RelativeLayout
    RelativeLayout rlMain,rlUrbanPresent,rlRuralPresent, rlUrbanPermanent,rlRuralPermanent,rlAshaOther;
    LinearLayout llGuardian;

    //Spinner
    Spinner spinnerPlaceOfBirth;

    //ImageView
    ImageView ivUrbanPresent, ivRuralPresent,ivUrbanPermanent,ivRuralPermanent;

    //ArrayList
    public static ArrayList<String> relation = new ArrayList<String>();
    public static ArrayList<String> relationValue = new ArrayList<String>();
    public static ArrayList<String> rationCard = new ArrayList<String>();
    public static ArrayList<String> rationCardValue = new ArrayList<String>();

    public static ArrayList<String> districtId = new ArrayList<String>();
    public static ArrayList<String> districtName = new ArrayList<String>();

    public static ArrayList<String> blockId = new ArrayList<String>();
    public static ArrayList<String> blockName = new ArrayList<String>();

    public static ArrayList<String> gramId = new ArrayList<String>();
    public static ArrayList<String> gramName = new ArrayList<String>();

    public static ArrayList<String> HamletId = new ArrayList<String>();
    public static ArrayList<String> HamletName = new ArrayList<String>();

    public static ArrayList<String> perDistrictId = new ArrayList<String>();
    public static ArrayList<String> perDistrictName = new ArrayList<String>();

    public static ArrayList<String> perBlockId = new ArrayList<String>();
    public static ArrayList<String> perBlockName = new ArrayList<String>();

    public static ArrayList<String> perGramId = new ArrayList<String>();
    public static ArrayList<String> perGramName = new ArrayList<String>();

    public static ArrayList<String> PerHamletId = new ArrayList<String>();
    public static ArrayList<String> PerHamletName = new ArrayList<String>();

    public static ArrayList<String> nurseId = new ArrayList<String>();
    public static ArrayList<String> nurseName = new ArrayList<String>();

    public static ArrayList<String> AshaId = new ArrayList<String>();
    public static ArrayList<String> AshaName = new ArrayList<String>();

    public static ArrayList<String> presentCountry = new ArrayList<String>();
    public static ArrayList<String> presentState = new ArrayList<String>();

    public static ArrayList<String> permanentCountry = new ArrayList<String>();
    public static ArrayList<String> permanentState = new ArrayList<String>();

    String district="",perdistrict="",block="",perblock="",gram="",pergram="",preType="",perType=""
            ,asha="",delivery="",place="",preCountry="",preState="",perCountry="",perState="",
            sameAddress ="0";

    LinearLayout llFacililtyOther;

    //RelativeLayout
    RelativeLayout rlPresentState,rlPresentDistrict,rlPresentBlock,rlPresentGram,rlPermanentState, rlPermanentDistrict,rlPermanentBlock,rlPermanentGram;

    //TextView tvBlockPlace,tvLMPDate,tvEDDate,tvFacility,tvFacilityType,tvPlaceName;

    RelativeLayout rlOther,rlSameAddress;

    Spinner spinnerBlockPlace,spinnerDisPlace,spinnerFacility,spinnerFacilityType;

    EditText etPlaceName,etHospital;

    ImageView ivLMPD,ivSameAddress,ivEDDate;

    //ArrayList
    public static ArrayList<String> deliveryType = new ArrayList<String>();
    public static ArrayList<String> placeofbirth = new ArrayList<String>();
    public static ArrayList<String> placeofbirthValue = new ArrayList<String>();

    public static ArrayList<String> disPlaceId = new ArrayList<String>();
    public static ArrayList<String> disPlaceName = new ArrayList<String>();

    public static ArrayList<String> blockPlaceId = new ArrayList<String>();
    public static ArrayList<String> blockPlaceName = new ArrayList<String>();

    public static ArrayList<String> facId = new ArrayList<String>();
    public static ArrayList<String> facName = new ArrayList<String>();

    String disPlace="",blockPlace="",facility="",facTypeType="";

    int check=0;

    String uuid;

    //ImageView ivPicHeader;

    TextView tvEDDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info);

        AppUtils.hideSoftKeyboard(mActivity);

        initialize();
        iniStep1();
        iniStep2();

        type = AppSettings.getString(AppSettings.type);

        check=0;
        setStep1ColorNVisible();
        checkValues();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void initialize() {

        //ScrollView
        scrollView= findViewById(R.id.scrollView);

        //LinearLayout
        llStep1 = findViewById(R.id.llStep1);
        llStep2 = findViewById(R.id.llStep2);
        llAddress = findViewById(R.id.llAddress);

        tvBasicInfo = findViewById(R.id.tvBasicInfo);
        tvContactInfo = findViewById(R.id.tvContactInfo);

        //setOnClickListener
        //ivLMPD.setOnClickListener(this);

        /*try {

            byte[] decodedString = Base64.decode(AppSettings.getString(AppSettings.motherPic), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            ivPicHeader.setImageBitmap(decodedByte);

        } catch (Exception e) {
            e.printStackTrace();
            ivPicHeader.setImageResource(R.mipmap.mother1);
        }*/

    }


    private void iniStep1() {

        //EditText
        etMName = findViewById(R.id.etMotherName);
        etFName = findViewById(R.id.etFatherName);
        etMAadhar = findViewById(R.id.etMotherAadhaarName);
        etFAadhar = findViewById(R.id.etFatherAadhaarName);
        etMCTS = findViewById(R.id.etMctsNumber);
        etDobNotKnown = findViewById(R.id.etDobNotKnown);

        etGravida= findViewById(R.id.etGravida);
        etPara= findViewById(R.id.etPara);
        etAbortion= findViewById(R.id.etAbortion);
        etLive= findViewById(R.id.etLive);
        etMWeight= findViewById(R.id.etMotherWeight);
        etMMAge= findViewById(R.id.etMMAge);

        //Spinner
        spinnerCaste= findViewById(R.id.spinnerCaste);
        spinnerReligion= findViewById(R.id.spinnerReligion);
        spinnerEducation= findViewById(R.id.spinnerEducation);
        spinnerBirthSpacing= findViewById(R.id.spinnerBirthSpacing);

        //TextView
        tvDOB = findViewById(R.id.tvMotherDob);
        tvEDDate = findViewById(R.id.tvEDDate);
        //tvLMPDate = findViewById(R.id.tvLMPDate);
        //tvEDDate = findViewById(R.id.tvEDDate);

        //ImageView
        ivDob= findViewById(R.id.ivDob);
        ivMDobDelete= findViewById(R.id.ivMDelete);
        ivNext= findViewById(R.id.ivNext);
        //ivMAdmitYes= findViewById(R.id.imageView6);
        //ivMAdmitNo= findViewById(R.id.imageView7);
        //ivPic= findViewById(R.id.ivProfile);
        ivCamera= findViewById(R.id.ivCam);
        ivMultipleYes= findViewById(R.id.ivMultipleYes);
        ivMultipleNo= findViewById(R.id.ivMultipleNo);
        //ivConYes = findViewById(R.id.ivConYes);
        //ivConNo = findViewById(R.id.ivConNo);

        //RelativeLayout
        rlMultipleYes= findViewById(R.id.rlMultipleYes);
        rlMultipleNo= findViewById(R.id.rlMultipleNo);
        rlNext= findViewById(R.id.rlNext);
        rlPrevious= findViewById(R.id.rlPrevious);
        rlFacility= findViewById(R.id.rlFacility);
        rlFacilityType= findViewById(R.id.rlFacilityType);
        rlDistrict= findViewById(R.id.rlDistrict);
        rlBlock= findViewById(R.id.rlBlock);
        rlPlaceOfDelivery= findViewById(R.id.rlPlaceOfDelivery);

        religionName.clear();
        religionName.add(getString(R.string.selectReligion));
        religionName.add(getString(R.string.hindu));
        religionName.add(getString(R.string.muslim));
        religionName.add(getString(R.string.sikh));
        religionName.add(getString(R.string.isai));
        religionName.add(getString(R.string.buddhism));

        religionNameValue.clear();
        religionNameValue.add(getString(R.string.selectReligion));
        religionNameValue.add(getString(R.string.hinduValue));
        religionNameValue.add(getString(R.string.muslimValue));
        religionNameValue.add(getString(R.string.sikhValue));
        religionNameValue.add(getString(R.string.isaiValue));
        religionNameValue.add(getString(R.string.buddhismValue));

        spinnerReligion.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, religionName));
        spinnerReligion.setSelection(0);

        casteName.clear();
        casteName.add(getString(R.string.selectCaste));
        casteName.add(getString(R.string.general));
        casteName.add(getString(R.string.obc));
        casteName.add(getString(R.string.sc));
        casteName.add(getString(R.string.st));

        casteNameValue.clear();
        casteNameValue.add(getString(R.string.selectCaste));
        casteNameValue.add(getString(R.string.generalValue));
        casteNameValue.add(getString(R.string.obcValue));
        casteNameValue.add(getString(R.string.scValue));
        casteNameValue.add(getString(R.string.stValue));

        spinnerCaste.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, casteName));
        spinnerCaste.setSelection(0);

        educationName.clear();
        educationName.add(getString(R.string.selectEducation));
        educationName.add(getString(R.string.edu1));
        educationName.add(getString(R.string.edu2));
        educationName.add(getString(R.string.edu3));
        educationName.add(getString(R.string.edu4));
        educationName.add(getString(R.string.edu5));
        educationName.add(getString(R.string.edu6));
        educationName.add(getString(R.string.edu7));
        educationName.add(getString(R.string.edu8));
        educationName.add(getString(R.string.edu9));
        educationName.add(getString(R.string.edu10));

        educationNameValue.clear();
        educationNameValue.add(getString(R.string.selectEducation));
        educationNameValue.add(getString(R.string.edu1Value));
        educationNameValue.add(getString(R.string.edu2Value));
        educationNameValue.add(getString(R.string.edu3Value));
        educationNameValue.add(getString(R.string.edu4Value));
        educationNameValue.add(getString(R.string.edu5Value));
        educationNameValue.add(getString(R.string.edu6Value));
        educationNameValue.add(getString(R.string.edu7Value));
        educationNameValue.add(getString(R.string.edu8Value));
        educationNameValue.add(getString(R.string.edu9Value));
        educationNameValue.add(getString(R.string.edu10Value));

        spinnerEducation.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, educationName));
        spinnerEducation.setSelection(0);

        birthSpacing.clear();
        birthSpacing.add(getString(R.string.selectSpacing));
        birthSpacing.add(getString(R.string.spacing1));
        birthSpacing.add(getString(R.string.spacing2));
        birthSpacing.add(getString(R.string.spacing3));
        birthSpacing.add(getString(R.string.spacing4));
        birthSpacing.add(getString(R.string.notApplicable));

        birthSpacingValue.clear();
        birthSpacingValue.add(getString(R.string.selectSpacing));
        birthSpacingValue.add(getString(R.string.spacing1));
        birthSpacingValue.add(getString(R.string.spacing2));
        birthSpacingValue.add(getString(R.string.spacing3));
        birthSpacingValue.add(getString(R.string.spacing4));
        birthSpacingValue.add(getString(R.string.notApplicableValue));

        spinnerBirthSpacing.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, birthSpacing));
        spinnerBirthSpacing.setSelection(0);

        ivDob.setOnClickListener(this);
        ivMDobDelete.setOnClickListener(this);
        tvDOB.setOnClickListener(this);
        //ivCamera.setOnClickListener(this);
        rlMultipleYes.setOnClickListener(this);
        rlMultipleNo.setOnClickListener(this);
        rlNext.setOnClickListener(this);
        rlPrevious.setOnClickListener(this);
       // tvLMPDate.setOnClickListener(this);

        //setMotherAdmittedYes();
    }

    private void iniStep2() {

        //EditText
        etMobileNumber= findViewById(R.id.etMobileNumber);
        etFMobileNumber= findViewById(R.id.etFMobileNumber);
        etGuardianName= findViewById(R.id.etGuardianName);
        etGuardianNumber= findViewById(R.id.etGuardianNumber);
        etPresentAddress= findViewById(R.id.etPresentAddress);
        etPresentPincode= findViewById(R.id.etPresentPincode);
        etPermanentAddress= findViewById(R.id.etPermanentAddress);
        etPresentNear= findViewById(R.id.etPresentNear);
        etPermanentNear= findViewById(R.id.etPermanentNear);
        etPermanentPincode= findViewById(R.id.etPermanentPincode);
        etPlaceName= findViewById(R.id.etPlaceOfDelivery);
        etHospital= findViewById(R.id.etHospital);
        /*etAshaName= findViewById(R.id.etAshaName);
        etAshaNumber= findViewById(R.id.etAshaNumber);
       */

        //LinearLayout
        llFacililtyOther= findViewById(R.id.llFacililtyOther);
        llGuardian= findViewById(R.id.llGuardian);

        //Spinner
        spinnerRelation= findViewById(R.id.spinnerRelation);
        spinnerPresentHamlet= findViewById(R.id.spinnerPresentHamlet);
        spinnerPresentGram= findViewById(R.id.spinnerPresentGram);
        spinnerPresentBlock= findViewById(R.id.spinnerPresentBlock);
        spinnerPresentDistrict= findViewById(R.id.spinnerPresentDistrict);
        spinnerPermanentHamlet= findViewById(R.id.spinnerPermanentHamlet);
        spinnerPermanentGram= findViewById(R.id.spinnerPermanentGram);
        spinnerPermanentBlock= findViewById(R.id.spinnerPermanentBlock);
        spinnerPermanentDistrict= findViewById(R.id.spinnerPermanentDistrict);
        spinnerPlaceOfBirth= findViewById(R.id.spinnerPlaceOfBirth);
        spinnerBlockPlace = findViewById(R.id.spinnerBlockPlace);
        spinnerDisPlace= findViewById(R.id.spinnerDisPlace);
        spinnerFacility= findViewById(R.id.spinnerFacility);
        spinnerFacilityType= findViewById(R.id.spinnerFacilityType);
        spinnerNurse= findViewById(R.id.spinnerNurse);
        /*spinnerAsha= findViewById(R.id.spinnerAsha);

        */
        spinnerRation= findViewById(R.id.spinnerRation);
        spinnerPresentCountry= findViewById(R.id.spinnerPresentCountry);
        spinnerPresentState= findViewById(R.id.spinnerPresentState);
        spinnerPermanentCountry= findViewById(R.id.spinnerPermanentCountry);
        spinnerPermanentState= findViewById(R.id.spinnerPermanentState);

        //TextView
        /*tvBlockPlace= findViewById(R.id.tvBlockPlace);
        tvFacility= findViewById(R.id.tvFacility);
        tvFacilityType= findViewById(R.id.tvFacilityType);
        tvPlaceName= findViewById(R.id.tvPlaceName);*/

        //RelativeLayout
        rlUrbanPresent= findViewById(R.id.rlUrbanPresent);
        rlRuralPresent= findViewById(R.id.rlRuralPresent);
        rlUrbanPermanent = findViewById(R.id.rlUrbanPermanent);
        rlRuralPermanent= findViewById(R.id.rlRuralPermanent);
        rlOther= findViewById(R.id.rlOther);
        rlPresentState= findViewById(R.id.rlPresentState);
        rlPresentDistrict= findViewById(R.id.rlPresentDistrict);
        rlPresentBlock= findViewById(R.id.rlPresentBlock);
        rlPresentGram= findViewById(R.id.rlPresentGram);
        rlPermanentState= findViewById(R.id.rlPermanentState);
        rlPermanentDistrict = findViewById(R.id.rlPermanentDistrict);
        rlPermanentBlock= findViewById(R.id.rlPermanentBlock);
        rlPermanentGram= findViewById(R.id.rlPermanentGram);

        /*rlAshaOther= findViewById(R.id.rlAshaOther);
        */
        rlSameAddress= findViewById(R.id.rlSameAddress);


        //ImageView
        ivUrbanPresent= findViewById(R.id.ivUrbanPresent);
        ivRuralPresent = findViewById(R.id.ivRuralPresent);
        ivUrbanPermanent= findViewById(R.id.ivUrbanPermanent);
        ivRuralPermanent= findViewById(R.id.ivRuralPermanent);
        ivSameAddress= findViewById(R.id.ivSameAddress);
        ivEDDate= findViewById(R.id.ivEDDate);

        //llGuardian.setVisibility(View.GONE);

        rationCard.clear();
        rationCard.add(getString(R.string.selectRationCard));
        rationCard.add(getString(R.string.bpl));
        rationCard.add(getString(R.string.apl));
        rationCard.add(getString(R.string.arc));
        rationCard.add(getString(R.string.noRation));

        rationCardValue.clear();
        rationCardValue.add(getString(R.string.selectRationCard));
        rationCardValue.add(getString(R.string.bplValue));
        rationCardValue.add(getString(R.string.aplValue));
        rationCardValue.add(getString(R.string.arcValue));
        rationCardValue.add(getString(R.string.noRationValue));

        spinnerRation.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, rationCard));
        spinnerRation.setSelection(0);

        relation.clear();
        relation.add(getString(R.string.relation));
        relation.add(getString(R.string.mother));
        relation.add(getString(R.string.father));
        relation.add(getString(R.string.sister));
        relation.add(getString(R.string.brother));
        relation.add(getString(R.string.grandMother));
        relation.add(getString(R.string.grandFather));
        relation.add(getString(R.string.uncle));
        relation.add(getString(R.string.aunty));
        relation.add(getString(R.string.other));

        relationValue.clear();
        relationValue.add(getString(R.string.relation));
        relationValue.add(getString(R.string.motherValue));
        relationValue.add(getString(R.string.fatherValue));
        relationValue.add(getString(R.string.sisterValue));
        relationValue.add(getString(R.string.brotherValue));
        relationValue.add(getString(R.string.grandMotherValue));
        relationValue.add(getString(R.string.grandFatherValue));
        relationValue.add(getString(R.string.uncleValue));
        relationValue.add(getString(R.string.auntyValue));
        relationValue.add(getString(R.string.otherValue));

        spinnerRelation.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, relation));
        spinnerRelation.setSelection(0);

        /*//Spinner for Relation
        spinnerRelation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if(position==0)
                {
                    relation="";
                }
                else if(position==1||position==2)
                {
                    llGuardian.setVisibility(View.GONE);
                    relation = relationValue.get(position).toString();
                }
                else
                {
                    llGuardian.setVisibility(View.VISIBLE);
                    relation = relationValue.get(position).toString();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });*/

        nurseId.clear();
        nurseId.add(getString(R.string.selectNurse));
        nurseId.addAll(DatabaseController.getNurseIdCheckedInData());

        nurseName.clear();
        nurseName.add(getString(R.string.selectNurse));
        nurseName.addAll(DatabaseController.getNurseNameCheckedInData());

        spinnerNurse.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, nurseName));
        spinnerNurse.setSelection(0);

        placeofbirth.clear();
        placeofbirth.add(getString(R.string.selectPlaceOfBirth));
        placeofbirth.add(getString(R.string.placeOfBirthOption1));
        placeofbirth.add(getString(R.string.placeOfBirthOption2));
        placeofbirth.add(getString(R.string.hospital));

        placeofbirthValue.clear();
        placeofbirthValue.add(getString(R.string.selectPlaceOfBirth));
        placeofbirthValue.add(getString(R.string.pb_1Value));
        placeofbirthValue.add(getString(R.string.pb_2Value));
        placeofbirthValue.add(getString(R.string.hospitalValue));

        spinnerPlaceOfBirth.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, placeofbirth));
        spinnerPlaceOfBirth.setSelection(0);

        //Spinner for Relation
        spinnerPlaceOfBirth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if(position==0)
                {
                    place="";
                    setPlaceVisibility(0);
                    llFacililtyOther.setVisibility(View.GONE);
                    rlOther.setVisibility(View.GONE);
                }
                else  if(position==1||position==2)
                {
                    place = placeofbirthValue.get(position);
                    llFacililtyOther.setVisibility(View.GONE);
                    rlOther.setVisibility(View.GONE);
                    setPlaceVisibility(0);
                }
                else
                {
                    place = placeofbirthValue.get(position);
                    setPlaceVisibility(0);
                    llFacililtyOther.setVisibility(View.VISIBLE);
                    rlOther.setVisibility(View.GONE);

                    disPlaceId.clear();
                    disPlaceId.add(getString(R.string.selectDistrict));
                    disPlaceId.addAll(DatabaseController.getDistrictIdData("Rural"));
                    disPlaceId.add("0");

                    disPlaceName.clear();
                    disPlaceName.add(getString(R.string.selectDistrict));
                    disPlaceName.addAll(DatabaseController.getDistrictNameData("Rural"));
                    disPlaceName.add(getString(R.string.other));

                    spinnerDisPlace.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, disPlaceName));
                    spinnerDisPlace.setSelection(0);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        //Spinner for Relation
        spinnerDisPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                {
                    disPlace = disPlaceId.get(position);
                    setPlaceVisibility(1);
                    //etPlaceName.setText("");
                    //getDisBlock(disPlace);

                    facId.clear();
                    facName.clear();

                    facId.add(getString(R.string.selectFacility));
                    facName.add(getString(R.string.selectFacility));

                    facId.addAll(DatabaseController.getFacIdData(disPlace));
                    facName.addAll(DatabaseController.getFacNameData(disPlace));

                    facId.add("0");
                    facName.add(getString(R.string.other));

                    spinnerFacility.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, facName));
                    spinnerFacility.setSelection(0);

                    int facPos=0;

                    for(int j=0;j<facId.size();j++)
                    {
                        if(facId.get(j).equalsIgnoreCase(facility))
                        {
                            facPos=j;
                            break;
                        }
                    }

                    spinnerFacility.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, facName));
                    spinnerFacility.setSelection(facPos);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });


        //Spinner for Relation
        spinnerFacility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if(position==0)
                {
                    rlPlaceOfDelivery.setVisibility(View.GONE);
                    facility="";
                    etPlaceName.setText("");
                }
                else  if(facId.get(position).equals("0"))
                {
                    rlPlaceOfDelivery.setVisibility(View.VISIBLE);
                    facility = facId.get(position);
                    etPlaceName.setText("");
                }
                else
                {
                    rlPlaceOfDelivery.setVisibility(View.GONE);
                    facility = facId.get(position);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        rlUrbanPresent.setOnClickListener(this);
        rlRuralPresent.setOnClickListener(this);
        rlUrbanPermanent.setOnClickListener(this);
        rlRuralPermanent.setOnClickListener(this);
        rlSameAddress.setOnClickListener(this);
        ivEDDate.setOnClickListener(this);
    }

    public void setPlaceVisibility(int step) {

        setPlaceVisibilityInvi();
        if(step==1)
        {
            rlFacility.setVisibility(View.VISIBLE);
        }
        else  if(step==2)
        {
            rlBlock.setVisibility(View.VISIBLE);
        }
        else if(step==3)
        {
            rlBlock.setVisibility(View.VISIBLE);
            rlFacility.setVisibility(View.VISIBLE);
        }
        else if(step==4)
        {
            rlBlock.setVisibility(View.VISIBLE);
            rlFacility.setVisibility(View.VISIBLE);
            rlFacilityType.setVisibility(View.VISIBLE);
            rlPlaceOfDelivery.setVisibility(View.VISIBLE);
        }
        else if(step==5)
        {
            rlPlaceOfDelivery.setVisibility(View.VISIBLE);
        }

    }

    public void setPlaceVisibilityInvi() {

        rlBlock.setVisibility(View.GONE);
        rlFacility.setVisibility(View.GONE);
        rlFacilityType.setVisibility(View.GONE);
        rlPlaceOfDelivery.setVisibility(View.GONE);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.llLanguage:

                AppUtils.AlertLanguageConfirm(mActivity,getString(R.string.languageAlert));

                break;

            case R.id.rlSameAddress:

                if(sameAddress.isEmpty()|| sameAddress.equalsIgnoreCase("1"))
                {
                    sameAddress ="2";
                    setSameAddress(2);
                }
                else
                {
                    sameAddress ="1";
                    setSameAddress(1);
                }

                break;

            case R.id.rlNext:

                if(llStep1.getVisibility()==View.VISIBLE)
                {
                    validationStep1();
                }
                else if(llStep2.getVisibility()==View.VISIBLE)
                {
                    validationStep2();
                }

                break;

            case R.id.rlPrevious:

                check=0;
                setStep1ColorNVisible();

                break;

            case R.id.ivDob:

                dateDialog(mActivity,tvDOB,3);

                break;

            case R.id.tvMotherDob:

                dateDialog(mActivity,tvDOB,3);

                break;

            case R.id.ivMDelete:

                tvDOB.setText("");
                ivMDobDelete.setVisibility(View.GONE);
                etDobNotKnown.setEnabled(true);

                break;

             case R.id.rlMultipleYes:

                multiple=getString(R.string.yesValue);
                ivMultipleYes.setImageResource(R.drawable.ic_check_box_selected);
                ivMultipleNo.setImageResource(R.drawable.ic_check_box);

                break;

            case R.id.rlMultipleNo:

                multiple=getString(R.string.noValue);
                ivMultipleYes.setImageResource(R.drawable.ic_check_box);
                ivMultipleNo.setImageResource(R.drawable.ic_check_box_selected);

                break;


            /*
            case R.id.rlConYes:

                consanguinity=getString(R.string.yesValue);
                ivConYes.setImageResource(R.mipmap.selected);
                ivConNo.setImageResource(R.mipmap.unselected);

                break;

            case R.id.rlConNo:

                consanguinity=getString(R.string.noValue);
                ivConYes.setImageResource(R.mipmap.unselected);
                ivConNo.setImageResource(R.mipmap.selected);

                break;

            case R.id.ivCam:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                {
                    fileUri = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(MediaType));

                    Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    it.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(it, CameraCode);
                }
                else
                {
                    // create Intent to take a picture and return control to the calling application
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri(MediaType); // create a file to save the image
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                    // start the image capture Intent
                    startActivityForResult(intent, CameraCode);
                }

                break;



            case R.id.ivLMPDate:

                dateDialog(mActivity,tvLMPDate,1);

                break;

            case R.id.tvLMPDate:

                dateDialog(mActivity,tvLMPDate,1);

                break;

            case R.id.ivEDDate:

                AppUtils.dateDialog(mActivity,tvEDDate,5);

                break;*/

            case R.id.ivEDDate:

                AppUtils.dateDialog(mActivity,tvEDDate,5);

                break;

            //Step 2 OnClickListner
            case R.id.rlUrbanPresent:

                setUrban();

                break;

            case R.id.rlRuralPresent:

                setRural();

                break;

            case R.id.rlUrbanPermanent:

                setPUrban();

                break;

            case R.id.rlRuralPermanent:

                setPRural();

                break;

            default:

                break;
        }
    }

    private void setSameAddress(int i) {

        if(i==1)
        {
            llAddress.setVisibility(View.GONE);
           ivSameAddress.setImageResource(R.drawable.ic_check_box_selected);
        }
        else
        {
            llAddress.setVisibility(View.VISIBLE);
           ivSameAddress.setImageResource(R.drawable.ic_check_box);
        }
    }

    public void visibility() {
        llStep1.setVisibility(View.GONE);
        llStep2.setVisibility(View.GONE);
        rlPrevious.setVisibility(View.GONE);
        tvContactInfo.setTextColor(getResources().getColor(R.color.blackNew));
        tvBasicInfo.setTextColor(getResources().getColor(R.color.blackNew));
        tvContactInfo.setBackgroundResource(0);
        tvBasicInfo.setBackgroundResource(0);
    }

    public void setStep1ColorNVisible() {
        scrollToTop();
        visibility();
        llStep1.setVisibility(View.VISIBLE);
        rlNext.setVisibility(View.VISIBLE);
        ivNext.setImageResource(R.drawable.ic_next);
        ivNext.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        tvBasicInfo.setTextColor(getResources().getColor(R.color.white));
        tvBasicInfo.setBackgroundResource(R.drawable.rectangle_teal_round_new);
    }

    public void setStep2ColorNVisible() {
        scrollToTop();
        visibility();
        llStep2.setVisibility(View.VISIBLE);
        rlPrevious.setVisibility(View.VISIBLE);
        rlNext.setVisibility(View.VISIBLE);
        ivNext.setImageResource(R.drawable.ic_tick);
        ivNext.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        tvContactInfo.setTextColor(getResources().getColor(R.color.white));
        tvContactInfo.setBackgroundResource(R.drawable.rectangle_teal_round_new);
    }


    public void scrollToTop() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);
                scrollView.pageScroll(View.FOCUS_UP);
                scrollView.smoothScrollTo(0,0);
            }
        });
    }


    public void setStep1Step2Values() {

        motherDetail.clear();
        motherDetail.addAll(DatabaseController.getMotherRegDataViaId(AppSettings.getString(AppSettings.motherId)));

        Log.d("MotherDetail", motherDetail.toString());

        if(motherDetail.size()>0)
        {
            motherAdmission.clear();
            motherAdmission.addAll(DatabaseController.getMotherAdmissionDataViaId(AppSettings.getString(AppSettings.motherId)));

            uuid = motherDetail.get(0).get("uuid");

            etMName.setText(motherDetail.get(0).get("motherName"));
            etFName.setText(motherDetail.get(0).get("fatherName"));

           /* if(motherDetail.get(0).get("isMotherAdmitted").equalsIgnoreCase("No"))
            {
                setMotherAdmittedNo();
            }
            else
            {
                setMotherAdmittedYes();
            }*/

            etMAadhar.setText(motherDetail.get(0).get("motherAadharNumber"));
            etFAadhar.setText(motherDetail.get(0).get("fatherAadharNumber"));

            encodedString = motherDetail.get(0).get("motherPicture");

            //etHRN.setText(motherAdmission.get(0).get("hospitalRegistrationNumber"));
            etMCTS.setText(motherDetail.get(0).get("motherMCTSNumber"));
            tvDOB.setText(motherDetail.get(0).get("motherDob"));

            ivMDobDelete.setVisibility(View.GONE);
            if(!motherDetail.get(0).get("motherDob").isEmpty())
            {
                ivMDobDelete.setVisibility(View.VISIBLE);
                etDobNotKnown.setEnabled(false);
            }

            etDobNotKnown.setText(motherDetail.get(0).get("motherAge"));
            //education= motherDetail.get(0).get("motherEducation");

            int pos=0;

            for(int j=0;j<educationNameValue.size();j++)
            {
                if(educationNameValue.get(j).equalsIgnoreCase(motherDetail.get(0).get("motherEducation")))
                {
                    pos=j;
                    break;
                }
            }

            spinnerEducation.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, educationName));
            spinnerEducation.setSelection(pos);

            //caste= motherDetail.get(0).get("motherCaste");

            int posCaste=0;

            for(int j=0;j<casteNameValue.size();j++)
            {
                if(casteNameValue.get(j).equalsIgnoreCase(motherDetail.get(0).get("motherCaste")))
                {
                    posCaste=j;
                    break;
                }
            }

            spinnerCaste.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, casteName));
            spinnerCaste.setSelection(posCaste);

            //religion= motherDetail.get(0).get("motherReligion");

            int posReligion=0;

            for(int j=0;j<religionNameValue.size();j++)
            {
                if(religionNameValue.get(j).equalsIgnoreCase(motherDetail.get(0).get("motherReligion")))
                {
                    posReligion=j;
                    break;
                }
            }

            spinnerReligion.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, religionName));
            spinnerReligion.setSelection(posReligion);


            etMobileNumber.setText(motherDetail.get(0).get("motherMobileNumber"));
            etFMobileNumber.setText(motherDetail.get(0).get("fatherMobileNumber"));
            etGuardianName.setText(motherDetail.get(0).get("guardianName"));
            etGuardianNumber.setText(motherDetail.get(0).get("guardianNumber"));
            etPresentAddress.setText(motherDetail.get(0).get("presentAddress"));
            etPermanentAddress.setText(motherDetail.get(0).get("permanentAddress"));
            etPresentNear.setText(motherDetail.get(0).get("presentAddressNearBy"));
            etPermanentNear.setText(motherDetail.get(0).get("permanentAddressNearBy"));
            //etAshaName.setText(motherDetail.get(0).get("ashaName"));
            //etAshaNumber.setText(motherDetail.get(0).get("ashaNumber"));
            //tvLMPDate.setText(motherDetail.get(0).get("motherLmpDate"));
            etPresentPincode.setText(motherDetail.get(0).get("presentPinCode"));
            etPermanentPincode.setText(motherDetail.get(0).get("permanentPinCode"));

            etPara.setText(motherDetail.get(0).get("para"));
            etGravida.setText(motherDetail.get(0).get("gravida"));
            etAbortion.setText(motherDetail.get(0).get("abortion"));
            etLive.setText(motherDetail.get(0).get("live"));
            type = motherDetail.get(0).get("type");
            multiple = motherDetail.get(0).get("multipleBirth");

            if(multiple.equalsIgnoreCase(getString(R.string.yesValue)))
            {
                multiple=getString(R.string.yesValue);
                ivMultipleYes.setImageResource(R.drawable.ic_check_box_selected);
                ivMultipleNo.setImageResource(R.drawable.ic_check_box);
            }
            else  if(multiple.equalsIgnoreCase(getString(R.string.noValue)))
            {
                multiple=getString(R.string.noValue);
                ivMultipleYes.setImageResource(R.drawable.ic_check_box);
                ivMultipleNo.setImageResource(R.drawable.ic_check_box_selected);
            }

            //ration = motherDetail.get(0).get("rationCardType");

            int rationPos=0;

            for(int j=0;j<rationCardValue.size();j++)
            {
                if(rationCardValue.get(j).equalsIgnoreCase(motherDetail.get(0).get("rationCardType")))
                {
                    rationPos=j;
                    break;
                }
            }

            spinnerRation.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, rationCard));
            spinnerRation.setSelection(rationPos);

            etMWeight.setText(motherDetail.get(0).get("motherWeight"));
            etMMAge.setText(motherDetail.get(0).get("ageOfMarriage"));
            tvEDDate.setText(motherDetail.get(0).get("estimatedDateOfDelivery"));


            //birthSpacing = motherDetail.get(0).get("birthSpacing");

            int birthSpacingPos=0;

            for(int j=0;j<birthSpacingValue.size();j++)
            {
                if(birthSpacingValue.get(j).equalsIgnoreCase(motherDetail.get(0).get("birthSpacing")))
                {
                    birthSpacingPos=j;
                    break;
                }
            }

            spinnerBirthSpacing.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, birthSpacing));
            spinnerBirthSpacing.setSelection(birthSpacingPos);

            //relation = motherDetail.get(0).get("guardianRelation");

            int relPos=0;

            for(int j=0;j<relationValue.size();j++)
            {
                if(relationValue.get(j).equalsIgnoreCase(motherDetail.get(0).get("guardianRelation")))
                {
                    relPos=j;
                    break;
                }
            }

            spinnerRelation.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, relation));
            spinnerRelation.setSelection(relPos);

            preType = motherDetail.get(0).get("presentResidenceType");
            preCountry = motherDetail.get(0).get("presentCountry");
            preState = motherDetail.get(0).get("presentState");
            gram = motherDetail.get(0).get("presentVillageName");
            block = motherDetail.get(0).get("presentBlockName");
            district = motherDetail.get(0).get("presentDistrictName");

            if(preType.equalsIgnoreCase("Rural"))
            {
                setRural();
            }
            else  if(preType.equalsIgnoreCase("Urban"))
            {
                setUrban();
            }

            if(preCountry.equalsIgnoreCase(getString(R.string.indiaValue)))
            {
                setPreVisibility(1);
                spinnerPresentCountry.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, presentCountry));
                spinnerPresentCountry.setSelection(1);
            }
            else  if(preCountry.equalsIgnoreCase(getString(R.string.otherValue)))
            {
                spinnerPresentCountry.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, presentCountry));
                spinnerPresentCountry.setSelection(2);

                setPreVisibility(0);
                //setAshaOther();
            }
            else
            {
                spinnerPresentCountry.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, presentCountry));
                spinnerPresentCountry.setSelection(0);
            }

            if(preState.equalsIgnoreCase(getString(R.string.uttarPradeshValue)))
            {
                spinnerPresentState.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, presentState));
                spinnerPresentState.setSelection(1);

                setPreVisibility(1);
            }
            else  if(preState.equalsIgnoreCase(getString(R.string.otherValue)))
            {
                spinnerPresentState.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, presentState));
                spinnerPresentState.setSelection(2);

                setPreVisibility(2);
            }
            else
            {
                spinnerPresentState.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, presentState));
                spinnerPresentState.setSelection(0);
            }

            sameAddress = motherDetail.get(0).get("sameAddress");

            if(sameAddress.equalsIgnoreCase("1"))
            {
                llAddress.setVisibility(View.GONE);
                ivSameAddress.setImageResource(R.drawable.ic_check_box_selected);
                setSameAddress(2);
            }
            else
            {
                llAddress.setVisibility(View.VISIBLE);
                ivSameAddress.setImageResource(R.drawable.ic_check_box);
                setSameAddress(1);
            }

            perType = motherDetail.get(0).get("permanentResidenceType");
            perCountry = motherDetail.get(0).get("permanentCountry");
            perState = motherDetail.get(0).get("permanentState");
            pergram = motherDetail.get(0).get("permanentVillageName");
            perblock = motherDetail.get(0).get("permanentBlockName");
            perdistrict = motherDetail.get(0).get("permanentDistrictName");
            etPlaceName.setText(motherDetail.get(0).get("motherDeliveryPlace"));

            if(perType.equalsIgnoreCase("Rural"))
            {
                setPRural();
            }
            else if(perType.equalsIgnoreCase("Urban"))
            {
                setPUrban();
            }

            if(perCountry.equalsIgnoreCase(getString(R.string.indiaValue)))
            {
                setPreVisibility(1);
                spinnerPermanentCountry.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, permanentCountry));
                spinnerPermanentCountry.setSelection(1);
            }
            else  if(perCountry.equalsIgnoreCase(getString(R.string.otherValue)))
            {
                spinnerPermanentCountry.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, permanentCountry));
                spinnerPermanentCountry.setSelection(2);

                setPreVisibility(0);
               // setAshaOther();
            }
            else
            {
                spinnerPermanentCountry.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, permanentCountry));
                spinnerPermanentCountry.setSelection(0);
            }

            if(perState.equalsIgnoreCase(getString(R.string.uttarPradeshValue)))
            {
                spinnerPermanentState.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, permanentState));
                spinnerPermanentState.setSelection(1);

                setPreVisibility(1);
            }
            else  if(perState.equalsIgnoreCase(getString(R.string.otherValue)))
            {
                spinnerPermanentState.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, permanentState));
                spinnerPermanentState.setSelection(2);

                setPreVisibility(2);
                //setAshaOther();
            }
            else
            {
                spinnerPermanentState.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, permanentState));
                spinnerPermanentState.setSelection(0);
            }

            //nurseId  = motherDetail.get(0).get("staffId");

            /*int nursePos=0;

            for(int j=0;j<nurseId.size();j++)
            {
                if(nurseId.get(j).equalsIgnoreCase(nurseId))
                {
                    nursePos=j;
                    break;
                }
            }

            spinnerNurse.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, nurseName));
            spinnerNurse.setSelection(nursePos);*/

            place  = motherDetail.get(0).get("motherDeliveryPlace");
            facility = motherDetail.get(0).get("deliveryFacilityId");
            disPlace = motherDetail.get(0).get("motherDeliveryDistrict");

            try {

                if(place.equalsIgnoreCase(getString(R.string.pb_1Value)))
                {
                    spinnerPlaceOfBirth.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, placeofbirth));
                    spinnerPlaceOfBirth.setSelection(1);
                }
                else if(place.equalsIgnoreCase(getString(R.string.pb_2Value)))
                {
                    spinnerPlaceOfBirth.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, placeofbirth));
                    spinnerPlaceOfBirth.setSelection(2);
                }
                else  if(!facility.isEmpty())
                {
                    spinnerPlaceOfBirth.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, placeofbirth));
                    spinnerPlaceOfBirth.setSelection(3);
                }

                if(facility.equalsIgnoreCase("0"))
                {
                    setPlaceVisibility(5);
                    etPlaceName.setVisibility(View.VISIBLE);
                }
                else
                {
                    setPlaceVisibility(0);
                    llFacililtyOther.setVisibility(View.VISIBLE);
                    rlOther.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();

                setPlaceVisibility(5);
                etPlaceName.setVisibility(View.VISIBLE);
            }

            //disPlace = DatabaseController.getDisId(facility);

            disPlaceId.clear();
            disPlaceId.add(getString(R.string.selectDistrict));
            disPlaceId.addAll(DatabaseController.getDistrictIdData("Rural"));
            disPlaceId.add("0");

            disPlaceName.clear();
            disPlaceName.add(getString(R.string.selectDistrict));
            disPlaceName.addAll(DatabaseController.getDistrictNameData("Rural"));
            disPlaceName.add(getString(R.string.other));

            int disPos=0;

            for(int j=0;j<disPlaceId.size();j++)
            {
                if(disPlaceId.get(j).equalsIgnoreCase(disPlace))
                {
                    disPos=j;
                    break;
                }
            }

            spinnerDisPlace.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, disPlaceName));
            spinnerDisPlace.setSelection(disPos);

            int facPos=0;

            for(int j=0;j<facId.size();j++)
            {
                if(facId.get(j).equalsIgnoreCase(facility))
                {
                    facPos=j;
                    break;
                }
            }

            spinnerFacility.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, facName));
            spinnerFacility.setSelection(facPos);
        }
    }

    public void setPreAddressVisibilityInvi() {

        rlPresentState.setVisibility(View.GONE);
        rlPresentDistrict.setVisibility(View.GONE);
        rlPresentBlock.setVisibility(View.GONE);
        rlPresentGram.setVisibility(View.GONE);
    }

    public void setPreVisibility(int step) {

        setPreAddressVisibilityInvi();
        if(step==1)
        {
            rlPresentState.setVisibility(View.VISIBLE);
            rlPresentDistrict.setVisibility(View.VISIBLE);
            rlPresentBlock.setVisibility(View.VISIBLE);
            rlPresentGram.setVisibility(View.VISIBLE);
        }
        else  if(step==2)
        {
            rlPresentState.setVisibility(View.VISIBLE);
        }
        else  if(step==3)
        {
            rlPresentState.setVisibility(View.VISIBLE);
            rlPresentDistrict.setVisibility(View.VISIBLE);
            rlPresentBlock.setVisibility(View.VISIBLE);
        }


    }

    public void setPermanentAddressVisibilityInvi() {

        rlPermanentState.setVisibility(View.GONE);
        rlPermanentDistrict.setVisibility(View.GONE);
        rlPermanentBlock.setVisibility(View.GONE);
        rlPermanentGram.setVisibility(View.GONE);
    }

    public void setPerVisibility(int step) {

        setPermanentAddressVisibilityInvi();
        if(step==1)
        {
            rlPermanentState.setVisibility(View.VISIBLE);
            rlPermanentDistrict.setVisibility(View.VISIBLE);
            rlPermanentBlock.setVisibility(View.VISIBLE);
            rlPermanentGram.setVisibility(View.VISIBLE);
        }
        else  if(step==2)
        {
            rlPermanentState.setVisibility(View.VISIBLE);
        }
        else  if(step==3)
        {
            rlPermanentState.setVisibility(View.VISIBLE);
            rlPermanentDistrict.setVisibility(View.VISIBLE);
            rlPermanentBlock.setVisibility(View.VISIBLE);
        }

    }


    public void setRural() {

        setPreVisibility(1);
        preType="Rural";
        ivRuralPresent.setImageResource(R.drawable.ic_check_box_selected);
        ivUrbanPresent.setImageResource(R.drawable.ic_check_box);

        presentCountry.clear();
        presentCountry.add(getString(R.string.selectCountry));
        presentCountry.add(getString(R.string.india));
        presentCountry.add(getString(R.string.other));

        spinnerPresentCountry.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, presentCountry));
        spinnerPresentCountry.setSelection(0);

        //Spinner for Relation
        spinnerPresentCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if(position==0)
                {
                    preCountry="";
                }
                else if(presentCountry.get(position).equals(getString(R.string.other)))
                {
                    preCountry=getString(R.string.otherValue);
                    setPreVisibility(0);
                }
                else
                {
                    preCountry = getString(R.string.indiaValue);
                    setPreVisibility(1);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });


        presentState.clear();
        presentState.add(getString(R.string.selectState));
        presentState.add(getString(R.string.uttarPradesh));
        presentState.add(getString(R.string.other));

        spinnerPresentState.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, presentState));
        spinnerPresentState.setSelection(0);

        //Spinner for Relation
        spinnerPresentState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if(position==0)
                {
                    preState="";
                }
                else if(presentState.get(position).equals(getString(R.string.other)))
                {
                    preState=getString(R.string.otherValue);
                    setPreVisibility(2);
                }
                else
                {
                    setPreVisibility(1);
                    preState = getString(R.string.uttarPradeshValue);

                    getData("",preType,"",1);

                    if(AppSettings.getString(AppSettings.from).equalsIgnoreCase("1"))
                    {
                        getDistrict();

                        int disPos=0;

                        for(int j=0;j<districtId.size();j++)
                        {
                            if(districtId.get(j).equalsIgnoreCase(district))
                            {
                                disPos=j;
                                break;
                            }
                        }

                        spinnerPresentDistrict.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, districtName));
                        spinnerPresentDistrict.setSelection(disPos);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        //Spinner for Relation
        spinnerPresentDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if(position==0)
                {
                    district="";
                }
                else
                {
                    district = districtId.get(position).toString();

                    getData(district,preType,"",1);

                    if(AppSettings.getString(AppSettings.from).equalsIgnoreCase("1")) {
                        getBlock(district);

                        int blockPos = 0;

                        for (int j = 0; j < blockId.size(); j++) {
                            if (blockId.get(j).equalsIgnoreCase(block)) {
                                blockPos = j;
                                break;
                            }
                        }

                        spinnerPresentBlock.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, blockName));
                        spinnerPresentBlock.setSelection(blockPos);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }

    public void setUrban() {

        setPreVisibility(1);
        preType="Urban";
        ivRuralPresent.setImageResource(R.drawable.ic_check_box);
        ivUrbanPresent.setImageResource(R.drawable.ic_check_box_selected);

        rlPresentBlock.setVisibility(View.GONE);

        presentCountry.clear();
        presentCountry.add(getString(R.string.selectCountry));
        presentCountry.add(getString(R.string.india));
        presentCountry.add(getString(R.string.other));

        spinnerPresentCountry.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, presentCountry));
        spinnerPresentCountry.setSelection(0);

        //Spinner for Relation
        spinnerPresentCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if(position==0)
                {
                    preCountry="";
                }
                else if(presentCountry.get(position).equals(getString(R.string.other)))
                {
                    preCountry=getString(R.string.otherValue);
                    setPreVisibility(0);
                    //setAshaOther();
                }
                else
                {
                    preCountry = getString(R.string.indiaValue);
                    setPreVisibility(1);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });


        presentState.clear();
        presentState.add(getString(R.string.selectState));
        presentState.add(getString(R.string.uttarPradesh));
        presentState.add(getString(R.string.other));

        spinnerPresentState.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, presentState));
        spinnerPresentState.setSelection(0);

        //Spinner for Relation
        spinnerPresentState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if(position==0)
                {
                    preState="";
                }
                else if(presentState.get(position).equals(getString(R.string.other)))
                {
                    preState=getString(R.string.otherValue);
                    setPreVisibility(2);
                    //setAshaOther();
                }
                else
                {
                    setPreVisibility(1);
                    preState = getString(R.string.uttarPradeshValue);

                    getData("",preType,"",1);

                    if(AppSettings.getString(AppSettings.from).equalsIgnoreCase("1"))
                    {
                        getDistrict();

                        int disPos=0;

                        for(int j=0;j<districtId.size();j++)
                        {
                            if(districtId.get(j).equalsIgnoreCase(district))
                            {
                                disPos=j;
                                break;
                            }
                        }

                        spinnerPresentDistrict.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, districtName));
                        spinnerPresentDistrict.setSelection(disPos);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        //Spinner for Relation
        spinnerPresentDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if(position==0)
                {
                    district="";
                }
                else
                {
                    district = districtId.get(position).toString();
                    //getBlock(district);

                    getData(district,preType,"",1);

                    if(AppSettings.getString(AppSettings.from).equalsIgnoreCase("1"))
                    {
                        int gramPos=0;

                        for(int j = 0; j< gramId.size(); j++)
                        {
                            if(gramId.get(j).equalsIgnoreCase(gram))
                            {
                                gramPos=j;
                                break;
                            }
                        }

                        spinnerPresentGram.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, gramName));
                        spinnerPresentGram.setSelection(gramPos);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }

    public void setPRural() {

        setPerVisibility(1);
        //etAshaName.setText("");
        //etAshaNumber.setText("");
        perType="Rural";
        ivRuralPermanent.setImageResource(R.drawable.ic_check_box_selected);
        ivUrbanPermanent.setImageResource(R.drawable.ic_check_box);

        permanentCountry.clear();
        permanentCountry.add(getString(R.string.selectCountry));
        permanentCountry.add(getString(R.string.india));
        permanentCountry.add(getString(R.string.other));

        spinnerPermanentCountry.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, permanentCountry));
        spinnerPermanentCountry.setSelection(0);

        //Spinner for Relation
        spinnerPermanentCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if(position==0)
                {
                    perCountry="";
                }
                else if(permanentCountry.get(position).equals(getString(R.string.other)))
                {
                    perCountry=getString(R.string.otherValue);
                    setPerVisibility(0);
                    //setAshaOther();
                }
                else
                {
                    perCountry = getString(R.string.indiaValue);
                    setPerVisibility(1);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });


        permanentState.clear();
        permanentState.add(getString(R.string.selectState));
        permanentState.add(getString(R.string.uttarPradesh));
        permanentState.add(getString(R.string.other));

        spinnerPermanentState.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, permanentState));
        spinnerPermanentState.setSelection(0);

        //Spinner for Relation
        spinnerPermanentState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if(position==0)
                {
                    perState="";
                }
                else if(permanentState.get(position).equals(getString(R.string.other)))
                {
                    perState=getString(R.string.otherValue);
                    setPerVisibility(2);
                    //setAshaOther();
                }
                else
                {
                    setPerVisibility(1);
                    //perState = PerState.get(position);
                    perState = getString(R.string.uttarPradeshValue);

                    getData("",perType,"",2);

                    if(AppSettings.getString(AppSettings.from).equalsIgnoreCase("1"))
                    {
                        int disPos=0;

                        for(int j = 0; j< perDistrictId.size(); j++)
                        {
                            if(perDistrictId.get(j).equalsIgnoreCase(perdistrict))
                            {
                                disPos=j;
                                break;
                            }
                        }

                        spinnerPermanentDistrict.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, perDistrictName));
                        spinnerPermanentDistrict.setSelection(disPos);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        //Spinner for Relation
        spinnerPermanentDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if(position==0)
                {
                    perdistrict="";
                }
                else
                {
                    perdistrict = perDistrictId.get(position).toString();

                    getData(perdistrict,perType,"",2);

                    if(AppSettings.getString(AppSettings.from).equalsIgnoreCase("1"))
                    {
                        int blockPos=0;

                        for(int j = 0; j< perBlockId.size(); j++)
                        {
                            if(perBlockId.get(j).equalsIgnoreCase(perblock))
                            {
                                blockPos=j;
                                break;
                            }
                        }

                        spinnerPermanentBlock.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, perBlockName));
                        spinnerPermanentBlock.setSelection(blockPos);
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }

    public void setPUrban() {

        setPerVisibility(1);
        //etAshaName.setText("");
        //etAshaNumber.setText("");
        perType="Urban";
        ivRuralPermanent.setImageResource(R.drawable.ic_check_box);
        ivUrbanPermanent.setImageResource(R.drawable.ic_check_box_selected);

        permanentCountry.clear();
        permanentCountry.add(getString(R.string.selectCountry));
        permanentCountry.add(getString(R.string.india));
        permanentCountry.add(getString(R.string.other));

        spinnerPermanentCountry.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, permanentCountry));
        spinnerPermanentCountry.setSelection(0);

        //Spinner for Relation
        spinnerPermanentCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if(position==0)
                {
                    perCountry="";
                }
                else if(permanentCountry.get(position).equals(getString(R.string.other)))
                {
                    perCountry=getString(R.string.otherValue);
                    setPerVisibility(0);
                    //setAshaOther();
                }
                else
                {
                    //perCountry = PerCountry.get(position);
                    perCountry = getString(R.string.indiaValue);
                    setPerVisibility(1);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });


        permanentState.clear();
        permanentState.add(getString(R.string.selectState));
        permanentState.add(getString(R.string.uttarPradesh));
        permanentState.add(getString(R.string.other));

        spinnerPermanentState.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, permanentState));
        spinnerPermanentState.setSelection(0);

        //Spinner for Relation
        spinnerPermanentState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if(position==0)
                {
                    perState="";
                }
                else if(permanentState.get(position).equals(getString(R.string.other)))
                {
                    perState=getString(R.string.otherValue);
                    setPerVisibility(2);
                    //setAshaOther();
                }
                else
                {
                    setPerVisibility(1);
                    //perState = PerState.get(position);
                    perState = getString(R.string.uttarPradeshValue);

                    getData("",perType,"",2);

                    if(AppSettings.getString(AppSettings.from).equalsIgnoreCase("1"))
                    {
                        int disPos=0;

                        for(int j = 0; j< perDistrictId.size(); j++)
                        {
                            if(perDistrictId.get(j).equalsIgnoreCase(perdistrict))
                            {
                                disPos=j;
                                break;
                            }
                        }

                        spinnerPermanentDistrict.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, perDistrictName));
                        spinnerPermanentDistrict.setSelection(disPos);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        //Spinner for Relation
        spinnerPermanentDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if(position==0)
                {
                    perdistrict="";
                }
                else
                {
                    perdistrict = perDistrictId.get(position).toString();

                    getData(perdistrict,perType,"",2);

                    if(AppSettings.getString(AppSettings.from).equalsIgnoreCase("1"))
                    {
                        int gramPos=0;

                        for(int j = 0; j< perGramId.size(); j++)
                        {
                            if(perGramId.get(j).equalsIgnoreCase(pergram))
                            {
                                gramPos=j;
                                break;
                            }
                        }

                        spinnerPermanentGram.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, perGramName));
                        spinnerPermanentGram.setSelection(gramPos);
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }

    public void getData(final String district, final String type, final String block,int from) {

        if(district.isEmpty()&&type.equalsIgnoreCase("Rural")&&from==1)
        {
            getDistrict();

            spinnerPresentDistrict.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, districtName));
            spinnerPresentDistrict.setSelection(0);
        }
        else  if(!district.isEmpty()&&type.equalsIgnoreCase("Rural")&&block.isEmpty()&&from==1)
        {
            getBlock(district);
        }
        else  if(!district.isEmpty()&&type.equalsIgnoreCase("Rural")&&!block.isEmpty()&&from==1)
        {
            setPreVisibility(1);
            getGram(block);
        }
        else if(district.isEmpty()&&type.equalsIgnoreCase("Urban")&&from==1)
        {
            rlPresentBlock.setVisibility(View.GONE);
            getDistrict();

            spinnerPresentDistrict.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, districtName));
            spinnerPresentDistrict.setSelection(0);
        }
        else if(!district.isEmpty()&&type.equalsIgnoreCase("Urban")&&from==1)
        {
            rlPresentBlock.setVisibility(View.GONE);
            getGram(district);
        }

        else if(district.isEmpty()&&type.equalsIgnoreCase("Rural")&&from==2)
        {
            getPerDistrict();

            spinnerPermanentDistrict.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, perDistrictName));
            spinnerPermanentDistrict.setSelection(0);
        }
        else  if(!district.isEmpty()&&type.equalsIgnoreCase("Rural")&&block.isEmpty()&&from==2)
        {
            getPerBlock(perdistrict);
        }
        else  if(!district.isEmpty()&&type.equalsIgnoreCase("Rural")&&!block.isEmpty()&&from==2)
        {
            setPerVisibility(1);
            getPerGram(perblock);
            //llAsha.setVisibility(View.VISIBLE);

            AshaId.clear();
            AshaName.clear();

            /*AshaId.add(getString(R.string.selectAsha));
            AshaName.add(getString(R.string.selectAsha));

            AshaId.addAll(DatabaseController.getAllAshaId(perblock));
            AshaName.addAll(DatabaseController.getAllAshaNamePhone(perblock));

            AshaId.add("0");
            AshaName.add(getString(R.string.other));*/
        }
        else if(district.isEmpty()&&type.equalsIgnoreCase("Urban")&&from==2)
        {
            rlPermanentBlock.setVisibility(View.GONE);
            getPerDistrict();

            spinnerPermanentDistrict.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, perDistrictName));
            spinnerPermanentDistrict.setSelection(0);
        }
        else if(!district.isEmpty()&&type.equalsIgnoreCase("Urban")&&from==2)
        {
            rlPermanentBlock.setVisibility(View.GONE);
            getPerGram(district);
        }
        else
        {
            disPlaceId.clear();
            disPlaceId.add(getString(R.string.selectDistrict));
            disPlaceId.addAll(DatabaseController.getDistrictIdData(""));
            disPlaceId.add("0");

            disPlaceName.clear();
            disPlaceName.add(getString(R.string.selectDistrict));
            disPlaceName.addAll(DatabaseController.getDistrictNameData(""));
            disPlaceName.add(getString(R.string.other));

            spinnerDisPlace.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, disPlaceName));
            spinnerDisPlace.setSelection(0);
        }
    }

    public void getDistrict() {
        districtId.clear();
        districtId.add(getString(R.string.selectDistrict));
        districtId.addAll(DatabaseController.getDistrictIdData(preType));

        districtName.clear();
        districtName.add(getString(R.string.selectDistrict));
        districtName.addAll(DatabaseController.getDistrictNameData(preType));
    }

    public void getPerDistrict() {
        perDistrictId.clear();
        perDistrictId.add(getString(R.string.selectDistrict));
        perDistrictId.addAll(DatabaseController.getDistrictIdData(perType));

        perDistrictName.clear();
        perDistrictName.add(getString(R.string.selectDistrict));
        perDistrictName.addAll(DatabaseController.getDistrictNameData(perType));
    }

    public void getBlock(String dId) {
        blockId.clear();
        blockId.add(getString(R.string.selectBlock));
        blockId.addAll(DatabaseController.getBlockIdData(preType,dId));
        blockId.add(getString(R.string.other));

        blockName.clear();
        blockName.add(getString(R.string.selectBlock));
        blockName.addAll(DatabaseController.getBlockNameData(preType,dId));
        blockName.add(getString(R.string.other));

        spinnerPresentBlock.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, blockName));
        spinnerPresentBlock.setSelection(0);

        gramId.clear();
        gramId.add(getString(R.string.selectGram));
        gramId.add(getString(R.string.other));

        gramName.clear();
        gramName.add(getString(R.string.selectGram));
        gramName.add(getString(R.string.other));

        spinnerPresentGram.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, gramName));
        spinnerPresentGram.setSelection(0);

        //Spinner for Relation
        spinnerPresentBlock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if(position==0)
                {
                    block="";
                }
                else if(blockId.get(position).toString().equals(getString(R.string.other)))
                {
                    block = getString(R.string.otherValue);
                    setPreVisibility(3);
                }
                else
                {
                    block = blockId.get(position).toString();

                    getData(district,preType,block,1);

                    if(AppSettings.getString(AppSettings.from).equalsIgnoreCase("1"))
                    {
                        int gramPos=0;

                        for(int j = 0; j< gramId.size(); j++)
                        {
                            if(gramId.get(j).equalsIgnoreCase(gram))
                            {
                                gramPos=j;
                                break;
                            }
                        }

                        spinnerPresentGram.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, gramName));
                        spinnerPresentGram.setSelection(gramPos);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }

    public void getPerBlock(String dId) {
        perBlockId.clear();
        perBlockId.add(getString(R.string.selectBlock));
        perBlockId.addAll(DatabaseController.getBlockIdData(perType,dId));
        perBlockId.add(getString(R.string.other));

        perBlockName.clear();
        perBlockName.add(getString(R.string.selectBlock));
        perBlockName.addAll(DatabaseController.getBlockNameData(perType,dId));
        perBlockName.add(getString(R.string.other));

        spinnerPermanentBlock.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, perBlockName));
        spinnerPermanentBlock.setSelection(0);

        //Spinner for Relation
        spinnerPermanentBlock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if(position==0)
                {
                    perblock="";
                }
                else if(perBlockId.get(position).toString().equals(getString(R.string.other)))
                {
                    perblock = getString(R.string.otherValue);
                    setPerVisibility(3);
                }
                else
                {
                    perblock = perBlockId.get(position).toString();

                    getData(perdistrict,perType,perblock,2);

                    if(AppSettings.getString(AppSettings.from).equalsIgnoreCase("1"))
                    {
                        int gramPos=0;

                        for(int j = 0; j< perGramId.size(); j++)
                        {
                            if(perGramId.get(j).equalsIgnoreCase(pergram))
                            {
                                gramPos=j;
                                break;
                            }
                        }

                        spinnerPermanentGram.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, perGramName));
                        spinnerPermanentGram.setSelection(gramPos);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }

    public void getGram(String bId) {
        gramId.clear();
        gramId.add(getString(R.string.selectGram));
        gramId.addAll(DatabaseController.getGramIdData(preType,bId));
        gramId.add(getString(R.string.other));

        gramName.clear();
        gramName.add(getString(R.string.selectGram));
        gramName.addAll(DatabaseController.getGramNameData(preType,bId));
        gramName.add(getString(R.string.other));

        spinnerPresentGram.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, gramName));
        spinnerPresentGram.setSelection(0);

        //Spinner for Relation
        spinnerPresentGram.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if(position==0)
                {
                    gram="";
                }
                else if(gramName.get(position).equals(getString(R.string.other)))
                {
                    gram = getString(R.string.otherValue);
                }
                else
                {
                    gram = gramId.get(position);
                    //getBlock(district);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }

    public void getPerGram(String bId) {
        perGramId.clear();
        perGramId.add(getString(R.string.selectGram));
        perGramId.addAll(DatabaseController.getGramIdData(perType,bId));
        perGramId.add(getString(R.string.other));

        perGramName.clear();
        perGramName.add(getString(R.string.selectGram));
        perGramName.addAll(DatabaseController.getGramNameData(perType,bId));
        perGramName.add(getString(R.string.other));

        spinnerPermanentGram.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, perGramName));
        spinnerPermanentGram.setSelection(0);

        //Spinner for Relation
        spinnerPermanentGram.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if(position==0)
                {
                    pergram="";
                }
                else if(perGramName.get(position).equals(getString(R.string.other)))
                {
                    pergram = getString(R.string.otherValue);
                }
                else
                {
                    pergram = perGramId.get(position).toString();
                    //getBlock(district);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }

    public void getDisBlock(String dId) {

        blockPlaceId.clear();
        blockPlaceId.add(getString(R.string.selectBlock));
        blockPlaceId.addAll(DatabaseController.getBlockIdData(dId));

        blockPlaceName.clear();
        blockPlaceName.add(getString(R.string.selectBlock));
        blockPlaceName.addAll(DatabaseController.getBlockNameData(dId));

        spinnerPermanentBlock.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, blockPlaceName));
        spinnerPermanentBlock.setSelection(0);

        //Spinner for Relation
        spinnerPermanentBlock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if(position==0)
                {
                    blockPlace="";
                    setPlaceVisibility(3);
                }
                else
                {
                    blockPlace = blockPlaceId.get(position);
                    setPlaceVisibility(3);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }


    private void validationStep1() {

        int  g=0;
        try {
            g = Integer.parseInt(etGravida.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        int  p=0;
        try {
            p = Integer.parseInt(etPara.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        int  a=0;
        try {
            a = Integer.parseInt(etAbortion.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        int  l=0;
        try {
            l = Integer.parseInt(etLive.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        int bm = p+a;

        int age=0;
        try {
            age = Integer.parseInt(etDobNotKnown.getText().toString().trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        int ageAtMarriage=0;
        try {
            ageAtMarriage = Integer.parseInt(etMMAge.getText().toString().trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        int dob=0;
        try {
            dob = AppUtils.getAgeFromDOB(tvDOB.getText().toString().trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        if(spinnerNurse.getSelectedItemPosition()==0)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.selectNurse));
        }
        else if(etMName.getText().toString().isEmpty())
        {
            etMName.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.errorEnterMother));
        }
        else if(etFName.getText().toString().isEmpty())
        {
            etFName.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.errorEnterFather));
        }
        else if(!etMAadhar.getText().toString().isEmpty() && etMAadhar.getText().toString().length()<12)
        {
            etMAadhar.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.errorEnterMAadhar));
        }
        else  if(!etMAadhar.getText().toString().isEmpty() && !Verhoeff.validateVerhoeff(etMAadhar.getText().toString().trim()))
        {
            etMAadhar.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.errorEnterMAadhar));
        }
        else if(!etFAadhar.getText().toString().isEmpty()&&etFAadhar.getText().toString().length()<12)
        {
            etFAadhar.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.errorEnterFAadhar));
        }
        else  if(!etFAadhar.getText().toString().isEmpty()&&!Verhoeff.validateVerhoeff(etFAadhar.getText().toString().trim()))
        {
            etFAadhar.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.errorEnterFAadhar));
        }
        else if(!isEqual(etMAadhar.getText().toString(),etFAadhar.getText().toString()))
        {
            etMAadhar.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.errorSameAadhar));
        }
        else if(etMWeight.getText().toString().isEmpty())
        {
            etMWeight.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.errorMotherWeight));
        }
        else if(tvDOB.getText().toString().isEmpty()&& etDobNotKnown.getText().toString().isEmpty())
        {
            etDobNotKnown.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.errorEnterAge));
        }
        else if(!etDobNotKnown.getText().toString().isEmpty()&&(age>14 && age<61))
        {
            etDobNotKnown.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.enterAge));
        }
        else if(etMMAge.getText().toString().isEmpty())
        {
            etMMAge.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.errorMotherMarAge));
        }
        else if(spinnerEducation.getSelectedItemPosition()==0)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.errorSelectEducation));
        }
        else if(spinnerReligion.getSelectedItemPosition()==0)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.errorSelectReligion));
        }
        else if(spinnerCaste.getSelectedItemPosition()==0)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.errorSelectCaste));
        }
        else if(multiple.isEmpty())
        {
            AppUtils.showToastSort(mActivity,getString(R.string.multipleBirths));
        }
        else if(etGravida.getText().toString().isEmpty()||g==0)
        {
            etGravida.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.totalPregnancies));
        }
        else if(etPara.getText().toString().isEmpty())
        {
            etPara.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.nOfBirths));
        }
        else if(etAbortion.getText().toString().isEmpty())
        {
            etAbortion.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.nOfMiscarriage));
        }
        else if(bm!=g&&multiple.equalsIgnoreCase(getString(R.string.noValue)))
        {
            AppUtils.showToastSort(mActivity,getString(R.string.errorBirthNo));
        }
        else if(bm<g&&multiple.equalsIgnoreCase(getString(R.string.yesValue)))
        {
            AppUtils.showToastSort(mActivity,getString(R.string.errorBirthYes));
        }
        else if(etLive.getText().toString().isEmpty())
        {
            etLive.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.totalNoOfChildren));
        }
        else if(l>p)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.errorLiveBirth));
        }
        else if(spinnerBirthSpacing.getSelectedItemPosition()==0)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.selectSpacing));
        }
        else
        {
            check=1;
            setStep2ColorNVisible();
        }

    }

    private boolean isEqual(String motherAadhar,String fatherAadhar) {

        if(motherAadhar.isEmpty()||fatherAadhar.isEmpty())
        {
            return true;
        }
        else if(motherAadhar.equalsIgnoreCase(fatherAadhar))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    private void validationStep2() {

        if(!AppUtils.isValidMobileNo(etMobileNumber.getText().toString()))
        {
            etMobileNumber.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.errorEnterMMobile));
        }
        else  if(!AppUtils.isValidMobileNo(etFMobileNumber.getText().toString()))
        {
            etFMobileNumber.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.errorEnterFMobile));
        }
        else if(spinnerRation.getSelectedItemPosition()==0)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.selectRationCard));
        }
        else if(preType.equals(""))
        {
            AppUtils.showToastSort(mActivity,getString(R.string.errorSelectPreAddType));
        }
        else  if(preCountry.equals(""))
        {
            AppUtils.showToastSort(mActivity,getString(R.string.countryName));
        }
        else if(!preCountry.equals(getString(R.string.otherValue))&&preState.equalsIgnoreCase(""))
        {
            AppUtils.showToastSort(mActivity,getString(R.string.stateName));
        }
        else  if(district.equals("")&&!preCountry.equals(getString(R.string.otherValue))&&!preState.equals(getString(R.string.otherValue)))
        {
            AppUtils.showToastSort(mActivity,getString(R.string.errorSelectPreDis));
        }
        else  if(block.equals("")&&!preCountry.equals(getString(R.string.otherValue))&&!preState.equals(getString(R.string.otherValue))
                         &&preType.equalsIgnoreCase("Rural"))
        {
            AppUtils.showToastSort(mActivity,getString(R.string.errorSelectPreBlock));
        }
        else   if(gram.equals("")&&!preCountry.equals(getString(R.string.otherValue))&&!preState.equals(getString(R.string.otherValue))&&!block.equals(getString(R.string.otherValue)))
        {
            AppUtils.showToastSort(mActivity,getString(R.string.errorSelectPreGram));
        }
        else if(etPresentAddress.getText().toString().isEmpty())
        {
            etPresentAddress.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.errorEnterPreAdd));
        }
        else  if(perType.isEmpty()&&sameAddress.equalsIgnoreCase("2"))
        {
            AppUtils.showToastSort(mActivity,getString(R.string.errorSelectPerAddType));
        }
        else  if(perCountry.equals("")&&sameAddress.equalsIgnoreCase("2"))
        {
            AppUtils.showToastSort(mActivity,getString(R.string.countryName));
        }
        else if(!perCountry.equals(getString(R.string.otherValue))&&perState.equalsIgnoreCase("")&&sameAddress.equalsIgnoreCase("2"))
        {
            AppUtils.showToastSort(mActivity,getString(R.string.stateName));
        }
        else  if(perdistrict.equals("")&&!perCountry.equals(getString(R.string.otherValue))&&!perState.equals(getString(R.string.otherValue))
                         &&sameAddress.equalsIgnoreCase("2"))
        {
            AppUtils.showToastSort(mActivity,getString(R.string.errorSelectPerDis));
        }
        else  if(perblock.equals("")&&!perCountry.equals(getString(R.string.otherValue))&&!perState.equals(getString(R.string.otherValue))
                         &&perType.equalsIgnoreCase("Rural")&&sameAddress.equalsIgnoreCase("2"))
        {
            AppUtils.showToastSort(mActivity,getString(R.string.errorSelectPerBlock));
        }
        else   if(pergram.equals("")&&!perCountry.equals(getString(R.string.otherValue))&&!perState.equals(getString(R.string.otherValue))
                          &&!perblock.equals(getString(R.string.otherValue))
                          &&sameAddress.equalsIgnoreCase("2"))
        {
            AppUtils.showToastSort(mActivity,getString(R.string.errorSelectPerGram));
        }
        else if(etPermanentAddress.getText().toString().isEmpty()&&sameAddress.equalsIgnoreCase("2"))
        {
            etPermanentAddress.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.errorEnterPerAdd));
        }
        else  if(place.isEmpty())
        {
            AppUtils.showToastSort(mActivity,getString(R.string.errorSelectDelPlace));
        }
        else  if(place.equals(getString(R.string.hospitalValue))&&disPlace.isEmpty())
        {
            AppUtils.showToastSort(mActivity,getString(R.string.selectDistrict));
        }
        else  if(place.equals(getString(R.string.hospitalValue))&&facility.isEmpty()&&!disPlace.equalsIgnoreCase(getString(R.string.otherValue)))
        {
            AppUtils.showToastSort(mActivity,getString(R.string.errorSelectFacPlace));
        }
        else  if(place.equals(getString(R.string.hospitalValue))
                         &&etPlaceName.getText().toString().isEmpty()
                         &&disPlace.equalsIgnoreCase(getString(R.string.otherValue)))
        {
            etPlaceName.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.enterHospitalName));
        }
        else
        {
            saveMotherData();
        }

    }


    public void saveMotherData() {

        ContentValues contentValues = new ContentValues();

        //contentValues.put(TableMotherRegistration.tableColumn.uuid.toString(), uuid);
        contentValues.put(TableMotherRegistration.tableColumn.motherName.toString(), etMName.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.motherPicture.toString(), encodedString);
        contentValues.put(TableMotherRegistration.tableColumn.motherMCTSNumber.toString(), etMCTS.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.motherAadharNumber.toString(), etMAadhar.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.motherDob.toString(), tvDOB.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.motherAge.toString(), etDobNotKnown.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.motherEducation.toString(), educationNameValue.get(spinnerEducation.getSelectedItemPosition()));
        contentValues.put(TableMotherRegistration.tableColumn.motherCaste.toString(), casteNameValue.get(spinnerCaste.getSelectedItemPosition()));
        contentValues.put(TableMotherRegistration.tableColumn.motherReligion.toString(), religionNameValue.get(spinnerReligion.getSelectedItemPosition()));
        contentValues.put(TableMotherRegistration.tableColumn.motherMoblieNo.toString(), etMobileNumber.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.fatherName.toString(), etFName.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.fatherAadharNumber.toString(),etFAadhar.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.fatherMoblieNo.toString(), etFMobileNumber.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.rationCardType.toString(), rationCardValue.get(spinnerRation.getSelectedItemPosition()));
        contentValues.put(TableMotherRegistration.tableColumn.guardianName.toString(),  etGuardianName.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.guardianNumber.toString(),etGuardianNumber.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.guardianRelation.toString(),relationValue.get(spinnerRelation.getSelectedItemPosition()));
        contentValues.put(TableMotherRegistration.tableColumn.presentCountry.toString(), preCountry);
        contentValues.put(TableMotherRegistration.tableColumn.presentState.toString(), preState);
        contentValues.put(TableMotherRegistration.tableColumn.presentResidenceType.toString(), preType);
        contentValues.put(TableMotherRegistration.tableColumn.presentAddress.toString(), etPresentAddress.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.presentVillageName.toString(), gram);
        contentValues.put(TableMotherRegistration.tableColumn.presentBlockName.toString(), block);
        contentValues.put(TableMotherRegistration.tableColumn.presentDistrictName.toString(), district);
        contentValues.put(TableMotherRegistration.tableColumn.permanentResidenceType.toString(),perType);
        contentValues.put(TableMotherRegistration.tableColumn.permanentCountry.toString(), perCountry);
        contentValues.put(TableMotherRegistration.tableColumn.permanentState.toString(), perState);
        contentValues.put(TableMotherRegistration.tableColumn.permanentAddress.toString(), etPermanentAddress.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.permanentVillageName.toString(), pergram);
        contentValues.put(TableMotherRegistration.tableColumn.permanentBlockName.toString(), perblock);
        contentValues.put(TableMotherRegistration.tableColumn.permanentDistrictName.toString(), perdistrict);
        contentValues.put(TableMotherRegistration.tableColumn.presentPinCode.toString(), etPresentPincode.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.permanentPinCode.toString(), etPermanentPincode.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.presentAddressNearBy.toString(),etPresentNear.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.permanentAddressNearBy.toString(),  etPermanentNear.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.staffId.toString(), nurseName.get(spinnerNurse.getSelectedItemPosition()));
        contentValues.put(TableMotherRegistration.tableColumn.type.toString(), type);
        contentValues.put(TableMotherRegistration.tableColumn.para.toString(), etPara.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.gravida.toString(), etGravida.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.abortion.toString(),  etAbortion.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.live.toString(), etLive.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.multipleBirth.toString(), multiple);
        contentValues.put(TableMotherRegistration.tableColumn.motherDeliveryDistrict.toString(), disPlace);
        contentValues.put(TableMotherRegistration.tableColumn.sameaddress.toString(), sameAddress);

        contentValues.put(TableMotherRegistration.tableColumn.motherWeight.toString(), etMWeight.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.ageOfMarriage.toString(), etMMAge.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.estimatedDateOfDelivery.toString(), tvEDDate.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.birthSpacing.toString(), birthSpacingValue.get(spinnerBirthSpacing.getSelectedItemPosition()));

        if(sameAddress.equalsIgnoreCase("1"))
        {
            contentValues.put(TableMotherRegistration.tableColumn.permanentCountry.toString(), preCountry);
            contentValues.put(TableMotherRegistration.tableColumn.permanentState.toString(), preState);
            contentValues.put(TableMotherRegistration.tableColumn.permanentResidenceType.toString(), preType);
            contentValues.put(TableMotherRegistration.tableColumn.permanentAddress.toString(), etPresentAddress.getText().toString().trim());
            contentValues.put(TableMotherRegistration.tableColumn.permanentVillageName.toString(), gram);
            contentValues.put(TableMotherRegistration.tableColumn.permanentBlockName.toString(), block);
            contentValues.put(TableMotherRegistration.tableColumn.permanentDistrictName.toString(), district);
            contentValues.put(TableMotherRegistration.tableColumn.permanentPinCode.toString(), etPresentPincode.getText().toString().trim());
            contentValues.put(TableMotherRegistration.tableColumn.permanentAddressNearBy.toString(),etPresentNear.getText().toString().trim());
        }

        contentValues.put(TableMotherRegistration.tableColumn.facilityID.toString(), facility);

        contentValues.put(TableMotherRegistration.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableMotherRegistration.tableColumn.status.toString(), "1");
        contentValues.put(TableMotherRegistration.tableColumn.admittedSign.toString(), "");
        contentValues.put(TableMotherRegistration.tableColumn.isDataSynced.toString(), "2");
        contentValues.put(TableMotherRegistration.tableColumn.motherDeliveryPlace.toString(), "");

        try {
            if(facility.isEmpty())
            {
                contentValues.put(TableMotherRegistration.tableColumn.motherDeliveryPlace.toString(), place);
            }
            else if(facility.equalsIgnoreCase("0"))
            {
                contentValues.put(TableMotherRegistration.tableColumn.motherDeliveryPlace.toString(), etPlaceName.getText().toString().trim());
            }
            else
            {
                contentValues.put(TableMotherRegistration.tableColumn.motherDeliveryPlace.toString(), "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("contentValues",contentValues.toString());

        DatabaseController.updateEqual(contentValues, TableMotherRegistration.tableName,
                TableMotherRegistration.tableColumn.motherId.toString(), AppSettings.getString(AppSettings.motherId));

        if (AppUtils.isNetworkAvailable(mActivity)) {
            SyncMotherRecord.getMotherDataToUpdate(mActivity,
                    AppSettings.getString(AppSettings.motherId));

            onBackPressed();
        }
        else
        {
            AppUtils.showToastSort(mActivity, getString(R.string.dataSaved));
            onBackPressed();
        }
    }


    /*public void setStepUnknownValues() {

        motherDetail.clear();
        motherDetail.addAll(DatabaseController.getMotherRegDataViaId(AppSettings.getString(AppSettings.motherId)));

        if(motherDetail.size()>0)
        {
            etUMGN.setText(motherDetail.get(0).get("guardianName"));
            etUMGNo.setText(motherDetail.get(0).get("guardianNumber"));
            etON.setText(motherDetail.get(0).get("organisationName"));
            etONo.setText(motherDetail.get(0).get("organisationNumber"));
            etOA.setText(motherDetail.get(0).get("organisationAddress"));
            etUHRegistration.setText("");
        }
    }*/

    public void checkValues() {

        setStep1ColorNVisible();
        setStep1Step2Values();

       /* if(type.equalsIgnoreCase("1"))
        {
            setStep1ColorNVisible();
            setStep1Step2Values();
        }
        else if(type.equalsIgnoreCase("3"))
        {
            setStep1ColorNVisible();
            setStep1Step2Values();
        }*/
    }

    /*public void saveUnknownMotherData() {

        ContentValues contentValues = new ContentValues();

        //contentValues.put(TableMotherRegistration.tableColumn.uuid.toString(), uuid);
        contentValues.put(TableMotherRegistration.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        contentValues.put(TableMotherRegistration.tableColumn.guardianName.toString(),  etUMGN.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.guardianNumber.toString(),  etUMGNo.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.motherId.toString(), AppSettings.getString(AppSettings.motherId));

        contentValues.put(TableMotherRegistration.tableColumn.type.toString(), type);
        contentValues.put(TableMotherRegistration.tableColumn.organisationAddress.toString(), etOA.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.organisationName.toString(), etON.getText().toString().trim());
        contentValues.put(TableMotherRegistration.tableColumn.organisationNumber.toString(),  etONo.getText().toString().trim());

        contentValues.put(TableMotherRegistration.tableColumn.syncTime.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableMotherRegistration.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableMotherRegistration.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());
        contentValues.put(TableMotherRegistration.tableColumn.status.toString(), "1");
        contentValues.put(TableMotherRegistration.tableColumn.isDataSynced.toString(), "2");
        contentValues.put(TableMotherRegistration.tableColumn.admittedSign.toString(), "");

        DatabaseController.insertUpdateData(contentValues, TableMotherRegistration.motherRegistration,
                TableMotherRegistration.tableColumn.motherId.toString(), AppSettings.getString(AppSettings.motherId));

        if (SimpleHTTPConnection.isNetworkAvailable(mActivity)) {
            SyncMotherRecord.getMotherDataToUpdate(mActivity,
                    AppSettings.getString(AppSettings.motherId));
        }
        else
        {
            AppUtils.showToastSort(mActivity, getString(R.string.dataSaved));
            onBackPressed();
        }
    }*/

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

    private String dateDialog(Context context, final TextView textView, int type) {
        final String[] times = {""};

        Calendar mcurrentDate= Calendar.getInstance();
        int year=mcurrentDate.get(Calendar.YEAR);
        int month=mcurrentDate.get(Calendar.MONTH);
        int day=mcurrentDate.get(Calendar.DAY_OF_MONTH);

        if(!textView.getText().toString().isEmpty())
        {
            String[] parts = textView.getText().toString().split("-");
            String part1 = parts[2];
            String part2 = parts[1];
            String part3 = parts[0];

            day = Integer.parseInt(part1);
            month = Integer.parseInt(part2);
            year = Integer.parseInt(part3);
        }

        DatePickerDialog mDatePicker1 =new DatePickerDialog(context, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday)
            {
                String dob = String.valueOf(new StringBuilder().append(selectedday).append("-").append(selectedmonth+1).append("-").append(selectedyear));
                Log.d("dob",dob);
                Log.d("dob",formatDate(selectedyear,selectedmonth,selectedday));
                //AppSettings.putString(AppSettings.from,formatDate(selectedyear,selectedmonth,selectedday));
                //tvDob.setText(dob);
                etDobNotKnown.setText("");
                ivMDobDelete.setVisibility(View.VISIBLE);
                etDobNotKnown.setEnabled(false);
                textView.setText(formatDate(selectedyear,selectedmonth,selectedday));
            }
        },year, month, day);
        //mDatePicker1.setTitle("Select Date");
        // TODO Hide Future Date Here

        if(type==1)
        {
            mDatePicker1.getDatePicker().setMaxDate(System.currentTimeMillis());
        }
        else if(type==2)
        {
            mDatePicker1.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        }
        else if(type==3)
        {
            mDatePicker1.getDatePicker().setMaxDate(System.currentTimeMillis());

            mcurrentDate.add(Calendar.YEAR, -60);
            mDatePicker1.getDatePicker().setMinDate(mcurrentDate.getTimeInMillis());
            mcurrentDate.add(Calendar.YEAR, +60);
            // Subtract 6 days from Calendar updated date
            mcurrentDate.add(Calendar.YEAR, -15);
            mDatePicker1.getDatePicker().setMaxDate(mcurrentDate.getTimeInMillis());
            //mDatePicker1.getDatePicker().setMinDate(System.currentTimeMillis());
            //mDatePicker1.getDatePicker().setMinDate(mcurrentDate.getTimeInMillis());
        }
        else if(type==4)
        {
            mDatePicker1.getDatePicker().setMaxDate(System.currentTimeMillis());
            mcurrentDate.add(Calendar.DATE, -30);
            mDatePicker1.getDatePicker().setMinDate(mcurrentDate.getTimeInMillis());
        }
        else if(type==5)
        {
            mDatePicker1.getDatePicker().setMaxDate(System.currentTimeMillis());
            mDatePicker1.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        }
        else
        {
            mDatePicker1.getDatePicker().setMaxDate(System.currentTimeMillis());
            // Subtract 6 days from Calendar updated date
            mcurrentDate.add(Calendar.DATE, -1);
            mDatePicker1.getDatePicker().setMinDate(mcurrentDate.getTimeInMillis());
        }

        // TODO Hide Past Date Here
        //set min todays date
        //mDatePicker1.getDatePicker().setMinDate(System.currentTimeMillis());

        mDatePicker1.show();

        return times[0];
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
                ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

}
