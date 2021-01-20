package code.fragment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.kmcapp.android.BuildConfig;
import com.kmcapp.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import code.algo.SyncAllRecord;
import code.algo.SyncBabyRecord;
import code.algo.WebServices;
import code.algo.WebServicesCallback;
import code.database.AppSettings;
import code.database.DatabaseController;
import code.database.TableBabyAdmission;
import code.database.TableUser;
import code.main.ChangePasswordActivity;
import code.main.MainActivity;
import code.registration.RegistrationActivity;
import code.utils.AppConstants;
import code.utils.AppUrls;
import code.utils.AppUtils;
import code.view.BaseFragment;


public class SettingFragment extends BaseFragment implements View.OnClickListener {

    //RelativeLayout
    private RelativeLayout rlLanguage,rlPassword,rlSync;

    //TextView
    private TextView tvVersion, tvLastSync;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.fragment_setting, container, false);

        initialize(v);

        return v;
    }

    @SuppressLint("SetTextI18n")
    private void initialize(View v) {

        //RelativeLayout
        rlLanguage = v.findViewById(R.id.rlLanguage);
        rlPassword = v.findViewById(R.id.rlPassword);
        rlSync = v.findViewById(R.id.rlSync);

        //TextView
        tvVersion = v.findViewById(R.id.tvVersion);
        tvLastSync = v.findViewById(R.id.tvLastSync);

        tvVersion.setText(getString(R.string.version)+" "+BuildConfig.VERSION_NAME );

        if (!AppSettings.getString(AppSettings.syncTime).isEmpty())
        tvLastSync.setText(getString(R.string.lastSynced)+" "+AppUtils.convertDateTimeTo12HoursFormat(AppSettings.getString(AppSettings.syncTime)));
        else
            tvLastSync.setText(getString(R.string.lastSynced)+" "+getString(R.string.notApplicableShortValue));

        //setOnClickListener
        rlLanguage.setOnClickListener(this);
        rlPassword.setOnClickListener(this);
        rlSync.setOnClickListener(this);

        if(AppSettings.getString(AppSettings.userType).equalsIgnoreCase("1"))
        {
            rlPassword.setVisibility(View.GONE);
            rlSync.setVisibility(View.GONE);
        }

        AppUtils.hideSoftKeyboard(mActivity);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rlLanguage:

                AppUtils.AlertLanguageConfirm(mActivity,getString(R.string.languageAlert));

                break;

             case R.id.rlPassword:
                 startActivity(new Intent(mActivity, ChangePasswordActivity.class));
                break;

             case R.id.rlSync:

                 if (AppUtils.isNetworkAvailable(mActivity)) {
                     AppSettings.putString(AppSettings.syncTime,AppUtils.currentTimestampFormat());
                     tvLastSync.setText(getString(R.string.lastSynced)+" "+AppUtils.convertDateTimeTo12HoursFormat(AppSettings.getString(AppSettings.syncTime)));
                     SyncAllRecord.postDutyChange(mActivity);
                 } else {
                     AppUtils.showToastSort(mActivity, getString(R.string.errorInternet));
                 }

                break;

            default:

                break;
        }
    }


}
