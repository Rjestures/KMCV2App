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
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kmcapp.android.BuildConfig;
import com.kmcapp.android.R;

import org.json.JSONArray;
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

import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableBabyAdmission;
import code.database.TableUser;
import code.main.MainActivity;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;

import static android.app.Activity.RESULT_OK;
import static code.registration.RegistrationActivity.uuid;


public class ChecklistFragment extends BaseFragment implements View.OnClickListener {

    //Static Int
    private static final int MediaType = 1;
    //String
    public String picturePath = "", filename = "", ext = "", encodedBabyString = "";
    //ArrayList
    ArrayList<HashMap<String, String>> CheckList = new ArrayList();
    //GridLayoutManager
    GridLayoutManager mGridLayoutManager;
    //CheckListAdapter
    CheckListAdapter checkListAdapter;
    //RecyclerView
    RecyclerView checkListRecView;
    //RelativeLayout
    RelativeLayout rlNext, rlSehmatiPic;
    //ImageView
    ImageView ivSehmati;
    //Uri
    Uri fileUri;
    int count = 0;
    //Bitmap
    Bitmap bitmap;
    Spinner spinnerEnteredByNurse;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_checklist, container, false);
        initialize(v);
        RegistrationActivity.tvMotherAdmission.setBackgroundResource(R.drawable.rectangle_teal_selected);
        RegistrationActivity.tvMotherAdmission.setTextColor(getResources().getColor(R.color.white));
        RegistrationActivity.tvBabyAdmission.setBackgroundResource(R.drawable.rectangle_teal_selected);
        RegistrationActivity.tvBabyAdmission.setTextColor(getResources().getColor(R.color.white));
        RegistrationActivity.tvAssessment.setBackgroundResource(R.drawable.rectangle_teal_selected);
        RegistrationActivity.tvAssessment.setTextColor(getResources().getColor(R.color.white));
        RegistrationActivity.tvAdmission.setBackgroundResource(R.drawable.rectangle_teal_selected);
        RegistrationActivity.tvAdmission.setTextColor(getResources().getColor(R.color.white));

        return v;
    }

    private void initialize(View v) {

        //ImageView
        ivSehmati = v.findViewById(R.id.ivSehmatiPic);

        //checkListRecView
        checkListRecView = v.findViewById(R.id.checkListRecView);

        mGridLayoutManager = new GridLayoutManager(mActivity, 1);
        checkListRecView.setLayoutManager(mGridLayoutManager);

        //Spinner
        spinnerEnteredByNurse = v.findViewById(R.id.spinnerEnteredByNurse);

        //RelativeLayout
        rlNext = v.findViewById(R.id.rlNext);
        rlSehmatiPic = v.findViewById(R.id.rlSehmatiPic);

        nurseId.clear();
        nurseId.add(getString(R.string.selectNurse));
        nurseId.addAll(DatabaseController.getNurseIdCheckedInData());

        nurseName.clear();
        nurseName.add(getString(R.string.selectNurse));
        nurseName.addAll(DatabaseController.getNurseNameCheckedInData());

        spinnerEnteredByNurse.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner_new, nurseName));
        spinnerEnteredByNurse.setSelection(0);

        CheckList.clear();

        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                HashMap<String, String> jobObject = new HashMap();

                jobObject.put("name", getString(R.string.check_list_1));
                jobObject.put("nameValue", getString(R.string.check_list_1Value));
                jobObject.put("status", "0");

                CheckList.add(jobObject);
            } else if (i == 1) {
                HashMap<String, String> jobObject = new HashMap();

                jobObject.put("name", getString(R.string.check_list_2));
                jobObject.put("nameValue", getString(R.string.check_list_2Value));
                jobObject.put("status", "0");

                CheckList.add(jobObject);
            } else if (i == 2) {
                HashMap<String, String> jobObject = new HashMap();

                jobObject.put("name", getString(R.string.check_list_3));
                jobObject.put("nameValue", getString(R.string.check_list_3Value));
                jobObject.put("status", "0");

                CheckList.add(jobObject);
            } else if (i == 3) {
                HashMap<String, String> jobObject = new HashMap();

                jobObject.put("name", getString(R.string.check_list_4));
                jobObject.put("nameValue", getString(R.string.check_list_4Value));
                jobObject.put("status", "0");

                CheckList.add(jobObject);
            }
        }

        checkListAdapter = new CheckListAdapter(CheckList);
        checkListRecView.setAdapter(checkListAdapter);

        ivSehmati.setOnClickListener(this);
        rlSehmatiPic.setOnClickListener(this);
        rlNext.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rlNext:
                if (spinnerEnteredByNurse.getSelectedItemPosition() == 0) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorSelectYourName));
                } else if (encodedBabyString.isEmpty()) {
                    AppUtils.showToastSort(mActivity, getString(R.string.clickConsentForm));
                } else if (count != 4) {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorcheckList));
                } else if (AppUtils.isNetworkAvailable(mActivity)) {
                    doCheckListApi();
                } else {
                    AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                }

                break;

            case R.id.ivSehmatiPic:

            case R.id.rlSehmatiPic:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    fileUri = FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(MediaType));

                    Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    it.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(it, 101);
                } else {
                    // create Intent to take a picture and return control to the calling application
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri(MediaType); // create a file to save the image
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                    // start the image capture Intent
                    startActivityForResult(intent, 101);

                }

                break;

            default:

                break;
        }
    }

    private JSONObject createJsonForCheckList() {

        JSONObject json = new JSONObject();

        JSONObject jsonData = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {

            jsonData.put("motherId", AppSettings.getString(AppSettings.motherId));
            jsonData.put("babyId", AppSettings.getString(AppSettings.babyId));
            jsonData.put("nurseId", nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()));
            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("nurseDigitalSign", "");
            jsonData.put("localId", uuid);
            jsonData.put("sehmatiPatr", encodedBabyString);

            for (int i = 0; i < CheckList.size(); i++) {
                if (CheckList.get(i).get("status").equals("1")) {
                    JSONObject jsonNewData = new JSONObject();

                    jsonNewData.put("name", CheckList.get(i).get("nameValue"));

                    jsonArray.put(jsonNewData);
                }
            }

            jsonData.put("checkList", jsonArray);
            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    private void doCheckListApi() {

        WebServices.postApi(mActivity, AppUrls.checkList, createJsonForCheckList(), true, true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        DatabaseController.delete(TableUser.tableName, TableUser.tableColumn.uuid + " = '" + uuid + "'", null);

                        ContentValues mNewContentValues = new ContentValues();
                        mNewContentValues.put(TableBabyAdmission.tableColumn.sehmatiPhoto.toString(),
                                encodedBabyString);

                        DatabaseController.insertUpdateData(mNewContentValues, TableBabyAdmission.tableName,
                                TableBabyAdmission.tableColumn.uuid.toString(), String.valueOf(uuid));

                        if (!AppSettings.getString(AppSettings.from).equals("1")) {
                            AlertOk();
                        } else {
                            startActivity(new Intent(mActivity, MainActivity.class));
                            mActivity.finishAffinity();
                        }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
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

                ivSehmati.setImageBitmap(rotatedBitmap);
                encodedBabyString = getEncoded64ImageStringFromBitmap(rotatedBitmap);
                rlSehmatiPic.setBackgroundResource(0);

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

    //Alert for multiple babies
    private void AlertOk() {
        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_yes_no);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        RelativeLayout rlOk = dialog.findViewById(R.id.rlOk);
        RelativeLayout rlCancel = dialog.findViewById(R.id.rlCancel);
        TextView tvCancel = dialog.findViewById(R.id.tvCancel);
        TextView tvOk = dialog.findViewById(R.id.tvOk);

        tvCancel.setText(R.string.no);
        tvOk.setText(R.string.yes);

        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        tvMessage.setText(getString(R.string.newRegis));

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        rlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(mActivity, MainActivity.class));
                mActivity.finishAffinity();
                dialog.dismiss();

            }
        });

        rlOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.dismiss();

                AppSettings.putString(AppSettings.isSibling, "1");
                AppSettings.putString(AppSettings.babyId, "");
                AppSettings.putString(AppSettings.babyAdmissionId, "");
                AppSettings.putString(AppSettings.from, "0");
                startActivity(new Intent(mActivity, RegistrationActivity.class));
                mActivity.finish();
            }
        });
    }

    private static class CheckListHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        ImageView ivSelected;
        RelativeLayout rlMain;

        private CheckListHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.text);
            ivSelected = itemView.findViewById(R.id.imageView10);
            rlMain = itemView.findViewById(R.id.rlMain);

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

        private View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            View row = inflater.inflate(R.layout.inflate_spinner_new, parent, false);

            TextView tvName = row.findViewById(R.id.tvName);

            tvName.setText(data.get(position));

            return row;
        }
    }

    private class CheckListAdapter extends RecyclerView.Adapter<CheckListHolder> {
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        public CheckListAdapter(ArrayList<HashMap<String, String>> favList) {
            data = favList;
        }

        public CheckListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CheckListHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_checkbox, parent, false));
        }

        @SuppressLint("SetTextI18n")
        public void onBindViewHolder(CheckListHolder holder, final int position) {

            holder.tvName.setText(data.get(position).get("name"));

            if (data.get(position).get("status").equals("1")) {
                holder.ivSelected.setImageResource(R.drawable.ic_check_box_selected);
                holder.ivSelected.setColorFilter(getResources().getColor(R.color.lightTeal), PorterDuff.Mode.SRC_IN);
            } else {
                holder.ivSelected.setImageResource(R.drawable.ic_check_box);
                holder.ivSelected.setColorFilter(getResources().getColor(R.color.lightTeal), PorterDuff.Mode.SRC_IN);
            }

            holder.rlMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (data.get(position).get("status").equals("0")) {
                        HashMap<String, String> jobObject = new HashMap();

                        jobObject.put("name", data.get(position).get("name"));
                        jobObject.put("nameValue", data.get(position).get("nameValue"));
                        jobObject.put("status", "1");

                        CheckList.set(position, jobObject);
                        count++;
                    } else {
                        HashMap<String, String> jobObject = new HashMap();

                        jobObject.put("name", data.get(position).get("name"));
                        jobObject.put("nameValue", data.get(position).get("nameValue"));
                        jobObject.put("status", "0");

                        CheckList.set(position, jobObject);
                        count--;
                    }

                    checkListAdapter.notifyDataSetChanged();

                }
            });
        }

        public int getItemCount() {
            return data.size();
        }
    }

}
