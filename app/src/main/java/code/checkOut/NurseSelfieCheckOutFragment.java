package code.checkOut;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.kmcapp.android.BuildConfig;
import com.kmcapp.android.R;
import com.squareup.picasso.Picasso;

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
import java.util.UUID;

import code.algo.SyncAllRecord;
import code.checkIn.CheckInActivity;
import code.database.AppSettings;
import code.database.DataBaseHelper;
import code.database.DatabaseController;
import code.database.TableDutyChange;
import code.main.MainActivity;
import code.utils.AppConstants;
import code.utils.AppUtils;
import code.view.BaseFragment;

import static android.app.Activity.RESULT_OK;


public class NurseSelfieCheckOutFragment extends BaseFragment implements View.OnClickListener {

    //RelativeLayout
    private RelativeLayout rlCircle,rlNext,rlSelfie;

    //TextView
    private TextView tvNurseName,tvSingIn;

    //String
    private String picturePath="",encodedString="";

    //Uri
    private Uri fileUri;

    //Static Int
    private static final int CameraCode = 100, MediaType = 1;

    //Bitmap
    private Bitmap bitmap;

    //ImageView
    private ImageView ivSelfie,ivTick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.fragment_nurse_selfie, container, false);

        initialize(v);

        return v;
    }

    private void initialize(View v) {

        //RelativeLayout
        rlCircle   = v.findViewById(R.id.rlCircle);
        rlNext   = v.findViewById(R.id.rlNext);
        rlSelfie   = v.findViewById(R.id.rlSelfie);

        //ImageView
        ivSelfie   = v.findViewById(R.id.ivSelfie);
        ivTick   = v.findViewById(R.id.ivTick);

        //TextView
        tvNurseName = v.findViewById(R.id.tvNurseName);
        tvSingIn = v.findViewById(R.id.tvSingIn);

        rlCircle.setBackgroundResource(R.drawable.circle_grey);

        tvNurseName.setText(getString(R.string.hi)+", "+DatabaseController.getNurseName(AppSettings.getString(AppSettings.newNurseId)));
        tvSingIn.setText(getString(R.string.pleaseClickSelfieOut));

        //setOnClickListener
        rlNext.setOnClickListener(this);
        rlSelfie.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.rlNext:

                if(encodedString.isEmpty())
                {
                    AppUtils.showToastSort(mActivity,getString(R.string.pleaseClickSelfieOut));
                }
                else
                {
                    //checkCheckoutStatus();
                    saveDutyChange();
                }


                break;

            case R.id.rlSelfie:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                {
                    fileUri = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(MediaType));

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
                    intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
                    intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, CameraCode);
                }
                else
                {
                    // create Intent to take a picture and return control to the calling application
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri(MediaType); // create a file to save the image
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                    intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                    // start the image capture Intent
                    startActivityForResult(intent, CameraCode);

                }

                break;

            default:

                break;
        }
    }

/*
    private void checkCheckoutStatus() {

        String time = AppUtils.getCurrentTime();
        String time12Hour = AppUtils.getCurrentTimeIn12Hour();

        Log.d("time-before",time);

        String[] parts = time.split(":");

        Log.d("time-after",parts[0]);

        String mainString="",slotSection1="",slotSection2="",loungeName="";

        int newTime = Integer.parseInt(time.replace(":",""));

        if(newTime>=730&&newTime<=800)
        {
            ivSmiley.setImageResource(R.drawable.ic_happy_smily);
            tvResult.setText(getString(R.string.congratulations));
            tvOnOffTime.setText(getString(R.string.youAreOnTime));
        }
        else if(newTime>=1330&&newTime<=1400)
        {
            ivSmiley.setImageResource(R.drawable.ic_happy_smily);
            tvResult.setText(getString(R.string.congratulations));
            tvOnOffTime.setText(getString(R.string.youAreOnTime));
        }
        else if(newTime>=1930&&newTime<=2000)
        {
            ivSmiley.setImageResource(R.drawable.ic_happy_smily);
            tvResult.setText(getString(R.string.congratulations));
            tvOnOffTime.setText(getString(R.string.youAreOnTime));
        }
        else
        {
            ivSmiley.setImageResource(R.drawable.ic_sad_smily);
            tvResult.setText(getString(R.string.sorry));
            tvOnOffTime.setText(getString(R.string.youAreLate));
        }

        int currentTime= Integer.parseInt(parts[0]);

        if (newTime <730){

            slotSection1 = getString(R.string.slot8pm);
            slotSection2 = getString(R.string.slot8am);

        }
        else if (newTime<1330){

            slotSection1 = getString(R.string.slot8am);
            slotSection2 = getString(R.string.slot2pm);

        }

        else if (newTime <1930){

            slotSection1 = getString(R.string.slot2pm);
            slotSection2 = getString(R.string.slot8pm);

        }

        else {
            slotSection1 = getString(R.string.slot8pm);
            slotSection2 = getString(R.string.slot8am);

        }



       */
/* if(currentTime>=8&&currentTime<14)
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
        }*//*


        loungeName = getString(R.string.shift)
                + " "+getString(R.string.at)
                + " "+DatabaseController.getLoungeNameData(AppSettings.getString(AppSettings.loungeId))
                + " "+getString(R.string.at);

        mainString = getString(R.string.youHaveCheckedIn)
                + " "+slotSection1
                + " "+getString(R.string.to)
                + " "+slotSection2
                + " "+loungeName
                + " "+time12Hour;

        tvTime.setText(AppUtils.setSpannableForTime(mainString,slotSection1,slotSection2,loungeName,time12Hour,mActivity));

        ArrayList<HashMap<String, String>> arrayList = new ArrayList();
        arrayList.addAll(DatabaseController.getStatsNurses(AppSettings.getString(AppSettings.newNurseId)));

        if(arrayList.size()>0)
        {
            tvTotalShifts.setText(arrayList.get(0).get("count"));
            tvOnTime.setText(arrayList.get(0).get("onTime"));
            tvLate.setText(arrayList.get(0).get("late"));
        }

    }
*/


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
        if (type == MediaType) {
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

        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {

                picturePath = fileUri.getPath().toString();

                String selectedImagePath = picturePath;

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

                ivSelfie.setImageBitmap(rotatedBitmap);
                encodedString = getEncoded64ImageStringFromBitmap(rotatedBitmap);

                AppSettings.putString(AppSettings.newNurseSelfie,encodedString);

                rlCircle.setBackgroundResource(R.drawable.circle_teal);
                rlSelfie.setBackgroundResource(0);
//                ivTick.setImageResource(R.drawable.ic_tick);

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

    private void saveDutyChange() {
        AppUtils.showRequestDialog(mActivity);
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(TableDutyChange.tableColumn.loungeId.toString(), AppSettings.getString(AppSettings.loungeId));
        mContentValues.put(TableDutyChange.tableColumn.serverId.toString(), "");
        mContentValues.put(TableDutyChange.tableColumn.uuid.toString(), AppSettings.getString(AppSettings.uuid));
        mContentValues.put(TableDutyChange.tableColumn.nurseId.toString(),AppSettings.getString(AppSettings.newNurseId));
        mContentValues.put(TableDutyChange.tableColumn.json.toString(),createJson().toString());
        mContentValues.put(TableDutyChange.tableColumn.selfieCheckOut.toString(),encodedString);
        mContentValues.put(TableDutyChange.tableColumn.type.toString(),"2");
        mContentValues.put(TableDutyChange.tableColumn.isDataSyncedCheckOut.toString(), "0");
        mContentValues.put(TableDutyChange.tableColumn.modifyDate.toString(), AppUtils.currentTimestampFormat());
        mContentValues.put(TableDutyChange.tableColumn.syncTimeCheckOut.toString(),"");
        mContentValues.put(TableDutyChange.tableColumn.status.toString(), "2");

        DatabaseController.insertUpdateData(mContentValues, TableDutyChange.tableName,
                TableDutyChange.tableColumn.uuid.toString(),AppSettings.getString(AppSettings.uuid));

        if (AppUtils.isNetworkAvailable(mActivity)) {
            AppSettings.putString(AppSettings.newNurseSelfie,"");
            AppSettings.putString(AppSettings.newNurseId,"");
            AppSettings.putString(AppSettings.uuid,"");
            if (AppUtils.isNetworkAvailable(mActivity)) {
                AppSettings.putString(AppSettings.syncTime,AppUtils.currentTimestampFormat());
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        SyncAllRecord.postDutyChange(mActivity);
                    }
                }, 5000);

            } else {

                Log.v("First","checkout ke liya gya yha se");

                AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
            }
        }
        else
        {
            AppSettings.putString(AppSettings.newNurseSelfie,"");
            AppSettings.putString(AppSettings.newNurseId,"");
            AppSettings.putString(AppSettings.uuid,"");
            AppUtils.showToastSort(mActivity, getString(R.string.successfullyCheckedOut));
        }


        new Thread(new Runnable() {
            public void run(){
                DataBaseHelper.copyDatabase(mActivity);
            }
        }).start();


       Log.v("checkoutksize", String.valueOf(DatabaseController.getDutyCheckOutChange(AppSettings.getString(AppSettings.loungeId)).size()));


        if(DatabaseController.getNurseIdCheckedInData().size()==0)
        {
            Intent is=new Intent(mActivity, CheckInActivity.class);
            is.putExtra("fromcheckout","1331");
            startActivity(is);
            getActivity().finishAffinity();
        }
        else
        {
            AppSettings.putString(AppSettings.from, "0");
            startActivity(new Intent(mActivity, MainActivity.class));
            getActivity().finishAffinity();
        }

    }

    private JSONObject createJson()
    {
        JSONObject jsonData = new JSONObject();

        try {

            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("nurseId",AppSettings.getString(AppSettings.newNurseId));
            jsonData.put("selfie", encodedString);
            jsonData.put("localId", AppSettings.getString(AppSettings.uuid));
            jsonData.put("localDateTime", AppUtils.currentTimestampFormat());
            jsonData.put("latitude",AppSettings.getString(AppSettings.latitude));
            jsonData.put("longitude",AppSettings.getString(AppSettings.longitude));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonData;
    }
}
