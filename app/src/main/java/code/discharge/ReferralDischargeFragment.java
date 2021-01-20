package code.discharge;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.github.gcacace.signaturepad.views.SignaturePad;
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

import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableBabyAdmission;
import code.database.TableBabyMonitoring;
import code.database.TableBabyRegistration;
import code.database.TableBreastFeeding;
import code.database.TableComments;
import code.database.TableDoctorRound;
import code.database.TableInvestigation;
import code.database.TableKMC;
import code.database.TableMotherAdmission;
import code.database.TableMotherMonitoring;
import code.database.TableMotherRegistration;
import code.database.TableNotification;
import code.database.TableTreatment;
import code.database.TableVaccination;
import code.database.TableWeight;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;

import static android.app.Activity.RESULT_OK;


public class ReferralDischargeFragment extends BaseFragment implements View.OnClickListener {

    //EditText
    private EditText etDischargeNotes,etGuardianName,etReason,etSpecify;

    //RelativeLayout
    private RelativeLayout rlReason,rlRelation,rlHandoverPic,rlId,rlSpecify,rlNext,rlPrevious,rlYes,rlNo;

    //ImageView
    private ImageView ivHandoverPic,ivIdPic,ivClear,ivYes,ivNo;

    //LinearLayout
    private LinearLayout llData,llStep2,llHandover,llMotherAlsoSend;

    //Spinner
    private Spinner spinnerRelation,spinnerDoctor,spinnerNurse,spinnerTransportation;

    //SignaturePad
    private SignaturePad signaturePad;

    //TextView
    private TextView tvSubmit,tvGuardianName;

    String uuid  = UUID.randomUUID().toString();

    private ArrayList<String> nurseId = new ArrayList<String>();
    private ArrayList<String> nurseName = new ArrayList<String>();
    private ArrayList<String> doctorId = new ArrayList<String>();
    private ArrayList<String> doctorName = new ArrayList<String>();
    private ArrayList<String> relationList = new ArrayList<String>();
    private ArrayList<String> relationValue = new ArrayList<String>();
    private ArrayList<String> transportationName = new ArrayList<String>();
    private ArrayList<String> transportationNameValue = new ArrayList<String>();

    //String
    private String picturePath="",filename="",ext="", encodedString ="",encodedStringId="", encodedSign ="";

    //Uri
    Uri fileUri;

    //Static Int
    private static final int mediaType = 1;

    //Bitmap
    private Bitmap bitmap;

    private  int step=1,from=0;

    private ArrayList<String> districtId = new ArrayList<String>();
    private ArrayList<String> districtName = new ArrayList<String>();

    private ArrayList<String> facilityId = new ArrayList<String>();
    private ArrayList<String> facilityName = new ArrayList<String>();

    //Spinner
    private Spinner spinnerDistrict,spinnerFacility;

    private String motherDischarge="", type = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_referral_discharge,container,false);

        initialize(view);

        return view;
    }

    private void initialize(View v) {

        //EditText
        etDischargeNotes= v.findViewById(R.id.etDischargeNotes);
        etGuardianName= v.findViewById(R.id.etGuardianName);
        etReason= v.findViewById(R.id.etReason);
        etSpecify= v.findViewById(R.id.etSpecify);

        //RelativeLayout
        rlRelation= v.findViewById(R.id.rlRelation);
        rlReason= v.findViewById(R.id.rlReason);
        rlHandoverPic= v.findViewById(R.id.rlHandoverPic);
        rlId= v.findViewById(R.id.rlId);
        rlSpecify= v.findViewById(R.id.rlSpecify);
        rlNext= v.findViewById(R.id.rlNext);
        rlPrevious= v.findViewById(R.id.rlPrevious);
        rlYes= v.findViewById(R.id.rlYes);
        rlNo= v.findViewById(R.id.rlNo);

        //TextView
        tvSubmit = v.findViewById(R.id.tvSubmit);
        tvGuardianName = v.findViewById(R.id.tvGuardianName);

        //LinearLayout
        llData =  v.findViewById(R.id.llStep1);
        llStep2 =  v.findViewById(R.id.llStep2);
        llHandover =  v.findViewById(R.id.llHandover);
        llMotherAlsoSend =  v.findViewById(R.id.llMotherAlsoSend);

        //Spinner
        spinnerRelation =  v.findViewById(R.id.spinnerRelation);
        spinnerDoctor =  v.findViewById(R.id.spinnerDoctor);
        spinnerNurse =  v.findViewById(R.id.spinnerNurse);
        spinnerTransportation =  v.findViewById(R.id.spinnerTransportation);

        //SignaturePad
        signaturePad=  v.findViewById(R.id.signaturePad);

        //ImageView
        ivHandoverPic= v.findViewById(R.id.ivHandoverPic);
        ivIdPic= v.findViewById(R.id.ivIdPic);
        ivClear= v.findViewById(R.id.ivClear);
        ivYes= v.findViewById(R.id.ivYes);
        ivNo= v.findViewById(R.id.ivNo);

        nurseId.clear();
        nurseId.add(getString(R.string.selectNurse));
        nurseId.addAll(DatabaseController.getNurseIdCheckedInData());

        nurseName.clear();
        nurseName.add(getString(R.string.selectNurse));
        nurseName.addAll(DatabaseController.getNurseNameCheckedInData());

        spinnerNurse.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, nurseName));
        spinnerNurse.setSelection(0);

        doctorId.clear();
        doctorId.add(getString(R.string.selectDoctor));
        doctorId.addAll(DatabaseController.getDocIdData());

        doctorName.clear();
        doctorName.add(getString(R.string.selectDoctor));
        doctorName.addAll(DatabaseController.getDocNameData());

        spinnerDoctor.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, doctorName));
        spinnerDoctor.setSelection(0);

        relationList.clear();
        relationList.add(getString(R.string.relation));
        relationList.add(getString(R.string.mother));
        relationList.add(getString(R.string.father));
//        relationList.add(getString(R.string.otherRelative));
        relationList.add(getString(R.string.otherSpecify));

        relationValue.clear();
        relationValue.add(getString(R.string.relation));
        relationValue.add(getString(R.string.motherValue));
        relationValue.add(getString(R.string.fatherValue));
//        relationValue.add(getString(R.string.otherRelativeValue));
        relationValue.add(getString(R.string.otherValue));

        spinnerRelation.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, relationList));
        spinnerRelation.setSelection(0);

        //Spinner for relationList
        spinnerRelation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                etSpecify.setText("");
                if(position==3)
                {
                    rlSpecify.setVisibility(View.VISIBLE);
                }
                else
                {
                    rlSpecify.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        transportationName.clear();
        transportationName.add(getString(R.string.selectTransportation));
        transportationName.add(getString(R.string.ambulance));
        transportationName.add(getString(R.string.privateTransportation));

        transportationNameValue.clear();
        transportationNameValue.add(getString(R.string.selectTransportation));
        transportationNameValue.add(getString(R.string.ambulanceValue));
        transportationNameValue.add(getString(R.string.trans2Value));

        spinnerTransportation.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, transportationName));
        spinnerTransportation.setSelection(0);

        //Spinner
        spinnerDistrict = v.findViewById(R.id.spinnerDistrict);
        spinnerFacility = v.findViewById(R.id.spinnerFacility);

        getDistrict();

        //Spinner
        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                if(position==0)
                {
                    facilityName.clear();
                    facilityName.add(getString(R.string.selectFacility));
                    //facilityName.addAll(DatabaseController.getFacNameData(districtId.get(position)));

                    facilityId.clear();
                    facilityId.add(getString(R.string.selectFacility));
                    //facilityId.addAll(DatabaseController.getFacIdData(districtId.get(position)));

                    spinnerFacility.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, facilityName));
                    spinnerFacility.setSelection(0);
                }
                else
                {
                    facilityName.clear();
                    facilityName.add(getString(R.string.selectFacility));
                    facilityName.addAll(DatabaseController.getFacNameData(districtId.get(position)));

                    facilityId.clear();
                    facilityId.add(getString(R.string.selectFacility));
                    facilityId.addAll(DatabaseController.getFacIdData(districtId.get(position)));

                    spinnerFacility.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, facilityName));
                    spinnerFacility.setSelection(0);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        //Spinner for Facility
        spinnerFacility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        //setOnClickListener
        rlHandoverPic.setOnClickListener(this);
        rlId.setOnClickListener(this);
        ivHandoverPic.setOnClickListener(this);
        ivIdPic.setOnClickListener(this);
        rlNext.setOnClickListener(this);
        rlPrevious.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
        ivClear.setOnClickListener(this);
        rlYes.setOnClickListener(this);
        rlNo.setOnClickListener(this);

        type = DatabaseController.getTypeViaMotherId(AppSettings.getString(AppSettings.motherId));

        llHandover.setVisibility(View.GONE);
        rlRelation.setVisibility(View.GONE);
        rlSpecify.setVisibility(View.GONE);
        llMotherAlsoSend.setVisibility(View.GONE);
        if(type.equals("2"))
        {
            llHandover.setVisibility(View.VISIBLE);
        }
        else
        {
            rlRelation.setVisibility(View.VISIBLE);

            if(type.equals("1"))
            {
                llMotherAlsoSend.setVisibility(View.VISIBLE);
            }
        }

        step=1;
        llData.setVisibility(View.VISIBLE);
        llStep2.setVisibility(View.GONE);
        tvSubmit.setVisibility(View.GONE);
        rlNext.setVisibility(View.VISIBLE);
        rlPrevious.setVisibility(View.GONE);

    }

    private void getDistrict() {

        districtId.clear();
        districtId.add(getString(R.string.selectDistrict));
        districtId.addAll(DatabaseController.getDistrictIdData("Rural"));
        districtId.add("0");

        districtName.clear();
        districtName.add(getString(R.string.selectDistrict));
        districtName.addAll(DatabaseController.getDistrictNameData("Rural"));
        districtName.add(getString(R.string.other));

        spinnerDistrict.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, districtName));
        spinnerDistrict.setSelection(0);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tvSubmit:
            case R.id.rlNext:

                validation();

                break;

            case R.id.rlPrevious:

                step=1;
                llData.setVisibility(View.VISIBLE);
                llStep2.setVisibility(View.GONE);
                rlNext.setVisibility(View.VISIBLE);
                tvSubmit.setVisibility(View.GONE);
                rlPrevious.setVisibility(View.GONE);

                break;

            case R.id.rlYes:

                motherDischarge = getString(R.string.yesValue);
                ivNo.setImageResource(R.drawable.ic_check_box);
                ivYes.setImageResource(R.drawable.ic_check_box_selected);
                rlReason.setVisibility(View.GONE);

                break;

            case R.id.rlNo:

                motherDischarge = getString(R.string.noValue);
                ivNo.setImageResource(R.drawable.ic_check_box_selected);
                ivYes.setImageResource(R.drawable.ic_check_box);
                rlReason.setVisibility(View.VISIBLE);

                break;

            case R.id.ivClear:

                signaturePad.clear();

                break;

            case R.id.rlHandoverPic:

            case R.id.ivHandoverPic:

                from=0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                {
                    fileUri = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(mediaType));

                    Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    it.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(it, 1);
                }
                else
                {
                    // create Intent to take a picture and return control to the calling application
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri(mediaType); // create a file to save the image
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                    // start the image capture Intent
                    startActivityForResult(intent, 1);

                }

                break;


            case R.id.rlId:

            case R.id.ivIdPic:

                from=1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                {
                    fileUri = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(mediaType));

                    Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    it.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(it, 1);
                }
                else
                {
                    // create Intent to take a picture and return control to the calling application
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri(mediaType); // create a file to save the image
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                    // start the image capture Intent
                    startActivityForResult(intent, 1);

                }

                break;

            default:

                break;
        }
    }

    private void validation() {

        if(spinnerDistrict.getSelectedItemPosition()==0&&step==1)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.errorselectDistrict));
        }
        else if(spinnerFacility.getSelectedItemPosition()==0&&step==1)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.errorselectFacility));
        }
        else if(motherDischarge.isEmpty()&&step==1&&type.equals("1"))
        {
            AppUtils.showToastSort(mActivity,getString(R.string.errormotherAlsoSend));
        }
        else if(etReason.getText().toString().trim().isEmpty()&&motherDischarge.equalsIgnoreCase(getString(R.string.noValue)))
        {
            etReason.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.errorEnterReason));
        }
        else if(etGuardianName.getText().toString().trim().isEmpty()&&step==1)
        {
            etGuardianName.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.errorGuardianName));
        }
        else if(spinnerRelation.getSelectedItemPosition()==0&&step==1&&!type.equals("2"))
        {
            AppUtils.showToastSort(mActivity,getString(R.string.errorrelationWithChildMand));
        }
        else if((spinnerRelation.getSelectedItemPosition()==3
                         ||spinnerRelation.getSelectedItemPosition()==4)
                        &&etSpecify.getText().toString().trim().isEmpty()
                        &&step==1
                        &&!type.equals("2"))
        {
            etSpecify.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.anyOtherPleaseSpecify));
        }
        else if(encodedString.trim().isEmpty()&&step==1&&type.equals("2"))
        {
            AppUtils.showToastSort(mActivity,getString(R.string.handoverPicture));
        }
        else if(encodedStringId.trim().isEmpty()&&step==1&&type.equals("2"))
        {
            AppUtils.showToastSort(mActivity,getString(R.string.idOfGuardian));
        }
        else  if(step==1)
        {
            step=2;
            llData.setVisibility(View.GONE);
            llStep2.setVisibility(View.VISIBLE);
            rlNext.setVisibility(View.GONE);
            tvSubmit.setVisibility(View.VISIBLE);
            rlPrevious.setVisibility(View.VISIBLE);
        }
        else if(spinnerDoctor.getSelectedItemPosition()==0&&step==2)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.errorselectDoctor));
        }
        else if(spinnerNurse.getSelectedItemPosition()==0&&step==2)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.erroselectNurse));
        }
        else if(signaturePad.isEmpty()&&step==2)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.errornurseSignature));
        }
        else if(spinnerTransportation.getSelectedItemPosition()==0&&step==2)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.selectTransportation));
        }
        else if(etDischargeNotes.getText().toString().trim().isEmpty()&&step==2)
        {
            etDischargeNotes.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.errorDischargeNotes));
        }
        else
        {
            Bitmap bm =  signaturePad.getSignatureBitmap();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();

            encodedSign = Base64.encodeToString(byteArray, Base64.DEFAULT);

            if (AppUtils.isNetworkAvailable(mActivity)) {
                doDischargeApi();
            } else {
                AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
            }
        }
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

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

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
        if (type == mediaType) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                                         + AppConstants.projectName+ timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                picturePath = fileUri.getPath().toString();
                filename = picturePath.substring(picturePath.lastIndexOf("/") + 1);

                String selectedImagePath = picturePath;

                ext = "jpg";

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 500;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE) scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFile(selectedImagePath, options);

                Matrix matrix = new Matrix();
                matrix.postRotate(getImageOrientation(picturePath));
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 30, bao);

                rotatedBitmap = AppUtils.setWaterMark(rotatedBitmap,mActivity);

                if(from==0)
                {
                    ivHandoverPic.setImageBitmap(rotatedBitmap);
                    encodedString = getEncoded64ImageStringFromBitmap(rotatedBitmap);
                    rlHandoverPic.setBackgroundResource(0);
                }
                else  if(from==1)
                {
                    ivIdPic.setImageBitmap(rotatedBitmap);
                    encodedStringId = getEncoded64ImageStringFromBitmap(rotatedBitmap);
                    rlId.setBackgroundResource(0);
                }
                
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

    public static int getImageOrientation(String imagePath){
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


    private void doDischargeApi() {

        JSONObject json = new JSONObject();

        JSONObject jsonData = new JSONObject();

        try {

            jsonData.put( "loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put( "babyId", AppSettings.getString(AppSettings.babyId));
            jsonData.put( "trainForKMCAtHome","");
            jsonData.put( "infantometerAvailability","");
            jsonData.put( "babyLength","");
            jsonData.put( "measuringTapeAvailability","");
            jsonData.put( "headCircumference","");
            jsonData.put( "isMotherDischarge",motherDischarge);
            jsonData.put( "dischargeNotes",etDischargeNotes.getText().toString().trim());
            jsonData.put( "attendantName",etGuardianName.getText().toString().trim());
            jsonData.put( "relationWithInfant",relationValue.get(spinnerRelation.getSelectedItemPosition()));
            jsonData.put( "otherRelation",etSpecify.getText().toString().trim());
            jsonData.put( "dischargeByDoctor",doctorId.get(spinnerDoctor.getSelectedItemPosition()));
            jsonData.put( "dischargeByNurse",nurseId.get(spinnerNurse.getSelectedItemPosition()));
            jsonData.put( "transportation",transportationNameValue.get(spinnerTransportation.getSelectedItemPosition()));
            jsonData.put( "typeOfDischarge",getString(R.string.discharge1Value));
            jsonData.put( "bodyHandover","");
            jsonData.put( "guardianName",etGuardianName.getText().toString().trim());
            jsonData.put( "babyHandoverImage",encodedString);
            jsonData.put( "referredDistrict",districtId.get(spinnerDistrict.getSelectedItemPosition()));
            jsonData.put( "referredFacility",facilityId.get(spinnerFacility.getSelectedItemPosition()));
            jsonData.put( "referredUnit","");
            jsonData.put( "referredReason",etReason.getText().toString().trim());
            jsonData.put( "referralNotes",etDischargeNotes.getText().toString().trim());
            jsonData.put( "sehmatiPatr","");
            jsonData.put( "earlyDishargeReason","");
            jsonData.put( "isAbsconded","");
            jsonData.put( "dateOfDischarge","");
            jsonData.put( "signOfNurse",encodedSign);
            jsonData.put( "guardianIdImage",encodedStringId);
            /*jsonData.put( "signOfNurse","");
            jsonData.put( "guardianIdImage","");*/

            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));

            Log.v("doDischargeApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.babyDischarge, json,true,true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

//                        AppUtils.showToastSort(mActivity, "");

                        DatabaseController.delete(TableBabyRegistration.tableName,"babyId = '"+AppSettings.getString(AppSettings.babyId)+"'",null);
                        DatabaseController.delete(TableBabyAdmission.tableName,"babyId = '"+AppSettings.getString(AppSettings.babyId)+"'",null);
                        DatabaseController.delete(TableBabyMonitoring.tableName,"babyId = '"+AppSettings.getString(AppSettings.babyId)+"'",null);
                        DatabaseController.delete(TableBreastFeeding.tableName,"babyId = '"+AppSettings.getString(AppSettings.babyId)+"'",null);
                        DatabaseController.delete(TableComments.tableName,"motherOrBabyId = '"+AppSettings.getString(AppSettings.babyId)+"'",null);
                        DatabaseController.delete(TableWeight.tableName,"babyId = '"+AppSettings.getString(AppSettings.babyId)+"'",null);
                        DatabaseController.delete(TableKMC.tableName,"babyId = '"+AppSettings.getString(AppSettings.babyId)+"'",null);
                        DatabaseController.delete(TableDoctorRound.tableName,"babyId = '"+AppSettings.getString(AppSettings.babyId)+"'",null);
                        DatabaseController.delete(TableInvestigation.tableName,"babyId = '"+AppSettings.getString(AppSettings.babyId)+"'",null);
                        DatabaseController.delete(TableTreatment.tableName,"babyId = '"+AppSettings.getString(AppSettings.babyId)+"'",null);
                        DatabaseController.delete(TableVaccination.tableName,"babyId = '"+AppSettings.getString(AppSettings.babyId)+"'",null);
                        DatabaseController.delete(TableNotification.tableName,"babyId = '"+AppSettings.getString(AppSettings.babyId)+"'",null);

                        if(motherDischarge.equals(getString(R.string.yesValue)))
                        {
                            DatabaseController.delete(TableMotherRegistration.tableName,"motherId = '"+AppSettings.getString(AppSettings.motherId)+"'",null);
                            DatabaseController.delete(TableMotherAdmission.tableName,"motherId = '"+AppSettings.getString(AppSettings.motherId)+"'",null);
                            DatabaseController.delete(TableMotherMonitoring.tableName,"motherId = '"+AppSettings.getString(AppSettings.motherId)+"'",null);
                        }

                        AppUtils.AlertOkMother(getString(R.string.kmcPositionDuringDischarge),mActivity);

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

}
