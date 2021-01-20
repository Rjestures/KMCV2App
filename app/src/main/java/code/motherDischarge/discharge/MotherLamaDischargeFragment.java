package code.motherDischarge.discharge;

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


public class MotherLamaDischargeFragment extends BaseFragment implements View.OnClickListener {

    //EditText
    private EditText etDischargeNotes;

    //RelativeLayout
    private RelativeLayout rlTrainedYes,rlTrainedNo,rlConsentPic,rlNext,rlPrevious;

    //ImageView
    private ImageView ivConsentPic, ivTrainedYes,ivTrainedNo,ivClear;

    //LinearLayout
    private LinearLayout llStep1,llStep2;

    //TextView
    private TextView tvSubmit;

    //Spinner
    private Spinner spinnerDoctor,spinnerNurse,spinnerTransportation;

    //SignaturePad
    private SignaturePad signaturePad;

    private ArrayList<String> nurseId = new ArrayList<String>();
    private ArrayList<String> nurseName = new ArrayList<String>();
    private ArrayList<String> doctorId = new ArrayList<String>();
    private ArrayList<String> doctorName = new ArrayList<String>();
    private ArrayList<String> transportationName = new ArrayList<String>();
    private ArrayList<String> transportationNameValue = new ArrayList<String>();

    //String
    public String picturePath="",filename="",ext="", encodedString ="",trained="",leaving="", encodedSign ="";

    //Uri
    Uri fileUri;

    //Static Int
    private static final int mediaType = 1;

    //Bitmap
    Bitmap bitmap;

    private  int step=1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_mother_lama_discharge,container,false);

        initialize(view);

        return view;
    }

    private void initialize(View v) {

        //EditText
        etDischargeNotes= v.findViewById(R.id.etDischargeNotes);

        //RelativeLayout
        rlTrainedYes= v.findViewById(R.id.rlTrainedYes);
        rlTrainedNo= v.findViewById(R.id.rlTrainedNo);
        rlConsentPic= v.findViewById(R.id.rlConsentPic);
        rlNext= v.findViewById(R.id.rlNext);
        rlPrevious= v.findViewById(R.id.rlPrevious);

        //LinearLayout
        llStep1 =  v.findViewById(R.id.llStep1);
        llStep2 =  v.findViewById(R.id.llStep2);

        //TextView
        tvSubmit =  v.findViewById(R.id.tvSubmit);

        //Spinner
        spinnerDoctor =  v.findViewById(R.id.spinnerDoctor);
        spinnerNurse =  v.findViewById(R.id.spinnerNurse);
        spinnerTransportation =  v.findViewById(R.id.spinnerTransportation);

        //SignaturePad
        signaturePad=  v.findViewById(R.id.signaturePad);

        //ImageView
        ivTrainedYes= v.findViewById(R.id.ivTrainedYes);
        ivTrainedNo= v.findViewById(R.id.ivTrainedNo);
        ivConsentPic = v.findViewById(R.id.ivConsentPic);
        ivClear= v.findViewById(R.id.ivClear);

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

        //setOnClickListener
        rlTrainedYes.setOnClickListener(this);
        rlTrainedNo.setOnClickListener(this);
        rlConsentPic.setOnClickListener(this);
        ivConsentPic.setOnClickListener(this);
        rlNext.setOnClickListener(this);
        rlPrevious.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
        ivClear.setOnClickListener(this);

        step=1;
        llStep1.setVisibility(View.VISIBLE);
        llStep2.setVisibility(View.GONE);
        rlNext.setVisibility(View.VISIBLE);
        rlPrevious.setVisibility(View.GONE);
        tvSubmit.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tvSubmit:
            case R.id.rlNext:

                validation();

                break;

            case R.id.rlPrevious:

                if(step==2)
                {
                    step=1;
                    llStep1.setVisibility(View.VISIBLE);
                    llStep2.setVisibility(View.GONE);
                    rlNext.setVisibility(View.VISIBLE);
                    rlPrevious.setVisibility(View.GONE);
                    tvSubmit.setVisibility(View.GONE);
                }


                break;

            case R.id.rlTrainedYes:

                setDefaultTrained();
                ivTrainedYes.setImageResource(R.drawable.ic_check_box_selected);
                trained = getString(R.string.yesValue);

                break;

            case R.id.rlTrainedNo:

                setDefaultTrained();
                ivTrainedNo.setImageResource(R.drawable.ic_check_box_selected);
                trained = getString(R.string.noValue);

                break;

            case R.id.ivClear:

                signaturePad.clear();

                break;

            case R.id.ivConsentPic:

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

        if(encodedString.isEmpty()&&step==1)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.photoOfConsentNote));
        }
        else if(step==1)
        {
            step=2;
            llStep1.setVisibility(View.GONE);
            llStep2.setVisibility(View.VISIBLE);
            rlNext.setVisibility(View.GONE);
            rlPrevious.setVisibility(View.VISIBLE);
            tvSubmit.setVisibility(View.VISIBLE);
        }
        else if(trained.isEmpty()&&step==2)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.motherSufficientlyTrained));
        }
        else if(etDischargeNotes.getText().toString().trim().isEmpty()&&step==2)
        {
            etDischargeNotes.requestFocus();
            AppUtils.showToastSort(mActivity,getString(R.string.errorDischargeNotes));
        }
        else if(spinnerDoctor.getSelectedItemPosition()==0&&step==2)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.selectDoctor));
        }
        else if(spinnerNurse.getSelectedItemPosition()==0&&step==2)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.selectNurse));
        }
        else if(signaturePad.isEmpty()&&step==2)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.nurseSignature));
        }
        else if(spinnerTransportation.getSelectedItemPosition()==0&&step==2)
        {
            AppUtils.showToastSort(mActivity,getString(R.string.selectTransportation));
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

    private void setDefaultTrained() {
        trained="";
        ivTrainedYes.setImageResource(R.drawable.ic_check_box);
        ivTrainedNo.setImageResource(R.drawable.ic_check_box);
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

    private void doDischargeApi() {

        JSONObject json = new JSONObject();

        JSONObject jsonData = new JSONObject();

        try {

            jsonData.put( "loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put( "motherId", AppSettings.getString(AppSettings.motherId));
            jsonData.put( "trainForKMCAtHome","");
            jsonData.put( "dischargeNotes",etDischargeNotes.getText().toString().trim());
            jsonData.put( "attendantName","");
            jsonData.put( "relationWithMother","");
            jsonData.put( "otherRelation","");
            jsonData.put( "dischargeByDoctor",doctorId.get(spinnerDoctor.getSelectedItemPosition()));
            jsonData.put( "dischargeByNurse",nurseId.get(spinnerNurse.getSelectedItemPosition()));
            jsonData.put( "transportation",transportationNameValue.get(spinnerTransportation.getSelectedItemPosition()));
            jsonData.put( "signOfNurse",encodedSign);
            jsonData.put( "typeOfDischarge",getString(R.string.discharge2Value));
            jsonData.put( "guardianName","");
            jsonData.put( "referredDistrict","");
            jsonData.put( "bodyHandover","");
            jsonData.put( "referredFacilityName","");
            jsonData.put( "referredUnit","");
            jsonData.put( "referredReason","");
            jsonData.put( "referralNotes",etDischargeNotes.getText().toString().trim());
            jsonData.put( "sehmatiPatr",encodedString);
            jsonData.put( "earlyDishargeReason","");
            jsonData.put( "isAbsconded","");
            jsonData.put( "dateOfDischarge","");

            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));

            Log.v("doDischargeApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.motherDischarge, json,true,true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));

                        DatabaseController.delete(TableMotherRegistration.tableName,"motherId = '"+AppSettings.getString(AppSettings.motherId)+"'",null);
                        DatabaseController.delete(TableMotherAdmission.tableName,"motherId = '"+AppSettings.getString(AppSettings.motherId)+"'",null);
                        DatabaseController.delete(TableMotherMonitoring.tableName,"motherId = '"+AppSettings.getString(AppSettings.motherId)+"'",null);

                        AppUtils.AlertOkMother(getString(R.string.motherDischargeSuccessfullt),mActivity);

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

                ivConsentPic.setImageBitmap(rotatedBitmap);
                encodedString = getEncoded64ImageStringFromBitmap(rotatedBitmap);
                rlConsentPic.setBackgroundResource(0);

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

}
