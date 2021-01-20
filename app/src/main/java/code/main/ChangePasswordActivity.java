package code.main;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.kmcapp.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.common.LocaleHelper;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.infantsFragment.KMCFragment;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseActivity;

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {

    //TextView
    private TextView tvHeading,tvSubmit;

    //LinearLayout
    private LinearLayout llBack;

    //EditText
    private EditText etOldPassword,etNewPassword,etConfirmPassword;

    private ArrayList<String> nurseId = new ArrayList<String>();
    private ArrayList<String> nurseName = new ArrayList<String>();

    //Spinner
    private Spinner spinnerEnteredByNurse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initialize();
    }

    private void initialize() {

        //TextView
        tvHeading = findViewById(R.id.tvHeading);
        tvSubmit = findViewById(R.id.tvSubmit);

        //EditText
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        //Spinner
        spinnerEnteredByNurse = findViewById(R.id.spinnerEnteredByNurse);

        //LinearLayout
        llBack = findViewById(R.id.llBack);

        nurseId.clear();
        nurseId.add(getString(R.string.selectNurse));
        nurseId.addAll(DatabaseController.getNurseIdCheckedInData());

        nurseName.clear();
        nurseName.add(getString(R.string.selectNurse));
        nurseName.addAll(DatabaseController.getNurseNameCheckedInData());

        spinnerEnteredByNurse.setAdapter(new adapterSpinner(mActivity, R.layout.inflate_spinner, nurseName));
        spinnerEnteredByNurse.setSelection(0);

        tvHeading.setText(getString(R.string.changePassword));

        //setOnClickListener
        llBack.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.llBack:

                onBackPressed();

                break;

            case R.id.tvSubmit:

                if(spinnerEnteredByNurse.getSelectedItemPosition()==0)
                {
                    AppUtils.showToastSort(mActivity, getString(R.string.selectNurse));
                }
                else  if(etOldPassword.getText().toString().trim().isEmpty())
                {
                    AppUtils.showToastSort(mActivity,getString(R.string.errorPassword));
                }
                else  if(etNewPassword.getText().toString().trim().isEmpty())
                {
                    AppUtils.showToastSort(mActivity,getString(R.string.enterNewPassword));
                }
                else  if(etConfirmPassword.getText().toString().trim().isEmpty())
                {
                    AppUtils.showToastSort(mActivity,getString(R.string.enterConfirmPassword));
                }
                else if(!etNewPassword.getText().toString().trim().equals(etConfirmPassword.getText().toString().trim()))
                {
                    AppUtils.showToastSort(mActivity,getString(R.string.passwordNotMatched));
                }
                else  if (AppUtils.isNetworkAvailable(mActivity)) {
                    postChangePasswordApi();
                } else {
                    AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorInternet));
                }

                break;

            default:

                break;
        }
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

            View row=inflater.inflate(R.layout.inflate_spinner, parent, false);

            TextView tvName=row.findViewById(R.id.tvName);

            tvName.setText(data.get(position));

            return row;
        }
    }


    //postChangePasswordApi
    private void postChangePasswordApi() {

        JSONObject json = new JSONObject();
        JSONObject jsonData = new JSONObject();

        try {

            jsonData.put("loungeId", AppSettings.getString(AppSettings.loungeId));
            jsonData.put("nurseId", nurseId.get(spinnerEnteredByNurse.getSelectedItemPosition()));
            jsonData.put("oldPassword", etOldPassword.getText().toString().trim());
            jsonData.put("newPassword", etNewPassword.getText().toString().trim());

            json.put(AppConstants.projectName, jsonData);
            json.put(AppConstants.md5Data, AppUtils.md5(jsonData.toString()));

            Log.v("postChangePasswordApi", json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        WebServices.postApi(mActivity, AppUrls.updateLoungePassword, json,true,true, new WebServicesCallback() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnJsonSuccess(JSONObject jsonObject1) {

                try {

                    JSONObject jsonObject = jsonObject1.getJSONObject(AppConstants.projectName);

                    if (jsonObject.getString("resCode").equals("1")) {

                        onBackPressed();
                        AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));

                    } else {
                        AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void OnFail(String responce) {

                AppUtils.showToastSort(mActivity, responce);
                AppUtils.hideDialog();
            }
        });
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
