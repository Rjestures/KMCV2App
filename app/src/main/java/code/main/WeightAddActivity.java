package code.main;


import android.content.ContentValues;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import code.algo.SyncAllRecord;
import code.algo.SyncBabyRecord;
import code.common.LocaleHelper;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableWeight;
import code.utils.AppConstants;
import code.utils.AppUtils;
import code.view.BaseActivity;

public class WeightAddActivity extends BaseActivity implements View.OnClickListener {

    //ImageView
    private ImageView ivNote,ivNoteCamera;

    //TextView
    private TextView tvSubmit;

    //EditText
    private EditText etWeight;

    private String uuid  = UUID.randomUUID().toString();

    private ArrayList<String> nurseId = new ArrayList<String>();
    private ArrayList<String> nurseName = new ArrayList<String>();

    //String
    private String picturePath="",filename="",ext="", encodedString ="";

    //Uri
    private Uri fileUri;

    //Static Int
    private static final int mediaType = 1;

    //Bitmap
    private Bitmap bitmap;

    //RelativeLayout
    private RelativeLayout rlPic;

    //Spinner
    private Spinner spinnerEnteredByNurse;

    //LinearLayout
    private LinearLayout llSync,llHelp,llLogout,llLanguage;

    //RelativeLayout
    private RelativeLayout rlHelp,rlStuck,rlOwn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_add);

        findViewById();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void findViewById() {

        //TextView
        tvSubmit  = findViewById(R.id.tvSubmit);

        //Edittext
        etWeight= findViewById(R.id.etWeight);

        //LinearLayout
        llSync      = findViewById(R.id.llSync);
        llHelp      = findViewById(R.id.llHelp);
        llLogout = findViewById(R.id.llLogout);
        llLanguage = findViewById(R.id.llLanguage);

        //RelativeLayout
        rlHelp= findViewById(R.id.rlHelp);
        rlStuck= findViewById(R.id.rlStuck);
        rlOwn= findViewById(R.id.rlOwn);

        //RelativeLayout
        rlPic = findViewById(R.id.rlPic);

        //Spinner
        spinnerEnteredByNurse = findViewById(R.id.spinnerEnteredByNurse);

        nurseId.clear();
        nurseId.add(getString(R.string.selectNurse));
        nurseId.addAll(DatabaseController.getNurseIdCheckedInData());

        nurseName.clear();
        nurseName.add(getString(R.string.selectNurse));
        nurseName.addAll(DatabaseController.getNurseNameCheckedInData());

        spinnerEnteredByNurse.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, nurseName));
        spinnerEnteredByNurse.setSelection(0);

        //ClickListener
        tvSubmit.setOnClickListener(this);
        llSync.setOnClickListener(this);
        llHelp.setOnClickListener(this);
        llLogout.setOnClickListener(this);
        llLanguage.setOnClickListener(this);
        rlHelp.setOnClickListener(this);
        rlStuck.setOnClickListener(this);
        rlOwn.setOnClickListener(this);

        setHeader();
    }

    private void setHeader() {

        //ImageView
        ivNote= findViewById(R.id.ivNotePic);
        ivNoteCamera= findViewById(R.id.ivCam);

        ivNoteCamera.setOnClickListener(this);
        rlPic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.llLanguage:

                AppUtils.AlertLanguageConfirm(mActivity,getString(R.string.languageAlert));

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

                if(rlHelp.getVisibility()==View.VISIBLE)
                {
                    rlHelp.setVisibility(View.GONE);
                }
                else
                {
                    rlHelp.setVisibility(View.VISIBLE);
                }

                break;

            case R.id.rlStuck:

                rlHelp.setVisibility(View.GONE);
                DatabaseController.saveHelp(mActivity);
                AppUtils.AlertHelpConfirm(getString(R.string.callRegardingIssue),mActivity,1,"");

                break;

            case R.id.rlOwn:

                rlHelp.setVisibility(View.GONE);
                startActivity(new Intent(mActivity, TutorialActivity.class));

                break;

            case R.id.rlPic:

            case R.id.ivCam:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                {
                    fileUri = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(mediaType));

                    Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    it.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(it, 104);
                }
                else
                {
                    // create Intent to take a picture and return control to the calling application
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri(mediaType); // create a file to save the image
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                    // start the image capture Intent
                    startActivityForResult(intent, 104);

                }

                break;

            case R.id.tvSubmit:

                int weight=0;
                try {
                    weight = Integer.parseInt(etWeight.getText().toString().trim());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }


                if(spinnerEnteredByNurse.getSelectedItemPosition()==0)
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectYourName));
                }
                else if(etWeight.getText().toString().isEmpty())
                {
                    etWeight.requestFocus();
                    AppUtils.showToastSort(mActivity, getString(R.string.errorEnterWeight));
                }
                else if((weight<400||weight>6000))
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorWeightLimit));
                }
                else if(encodedString.isEmpty()){
                    AppUtils.showToastSort(mActivity, getString(R.string.errorClickWheightImage));
                }
                else
                {
                    ContentValues cvBabyWeight = new ContentValues();

                    cvBabyWeight.put(TableWeight.tableColumn.uuid.toString(), uuid);
                    cvBabyWeight.put(TableWeight.tableColumn.babyAdmissionId.toString(), AppSettings.getString(AppSettings.bAdmId));
                    cvBabyWeight.put(TableWeight.tableColumn.babyId.toString(), AppSettings.getString(AppSettings.babyId));
                    cvBabyWeight.put(TableWeight.tableColumn.nurseId.toString(),  nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()));
                    cvBabyWeight.put(TableWeight.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
                    cvBabyWeight.put(TableWeight.tableColumn.weightDate.toString(), AppUtils.getDateInFormat());
                    cvBabyWeight.put(TableWeight.tableColumn.isWeighingAvail.toString(),getString(R.string.yesValue));

                    cvBabyWeight.put(TableWeight.tableColumn.babyWeight.toString(),etWeight.getText().toString().trim());
                    cvBabyWeight.put(TableWeight.tableColumn.weightImage.toString(),encodedString);
                    cvBabyWeight.put(TableWeight.tableColumn.weighingReason.toString(),"");

                    cvBabyWeight.put(TableWeight.tableColumn.addDate.toString(), AppUtils.currentTimestampFormat());

                    DatabaseController.insertUpdateData(cvBabyWeight, TableWeight.tableName,
                            TableWeight.tableColumn.uuid.toString(), String.valueOf(uuid));

                    if (AppUtils.isNetworkAvailable(mActivity)) {
                        SyncBabyRecord.getBabyDataToUpdate(mActivity, AppSettings.getString(AppSettings.babyId));
                        onBackPressed();
                    }
                    else
                    {
                        AppUtils.showToastSort(mActivity, getString(R.string.dataSaved));
                        onBackPressed();
                    }
                }

                break;

            default:
                break;
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

        if (requestCode == 104) {
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

                ivNote.setImageBitmap(rotatedBitmap);
                encodedString = getEncoded64ImageStringFromBitmap(rotatedBitmap);
                rlPic.setBackgroundResource(0);

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
