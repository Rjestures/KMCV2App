package code.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import com.kmcapp.android.R;

import code.loungeFragment.LoungeServicesFragment;
import code.loungeFragment.MonthlyDashboardFragment;
import code.loungeFragment.StatusFragment;
import code.loungeFragment.TodaysDashboardFragment;
import code.view.BaseActivity;
import code.view.BaseFragment;


public class LoungeMonitoringFragment extends BaseFragment implements View.OnClickListener {

    //FragmentManager
    public static FragmentManager fragmentManager;

    //BaseFragment
    public static BaseFragment fragment;

    //textview
    public static TextView tvStatus, tvLoungeService, tvTodaysDashboard, tvMonthlyDashboard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.fragment_lounge_monitoring, container, false);

        initialize(v);

        return v;
    }

    private void initialize(View v) {

        //RelativeLayout
        tvStatus = v.findViewById(R.id.tvStatus);
        tvLoungeService = v.findViewById(R.id.tvLoungeService);
        tvTodaysDashboard = v.findViewById(R.id.tvTodaysDashboard);
        tvMonthlyDashboard = v.findViewById(R.id.tvMonthlyDashboard);

        tvStatus.setOnClickListener(this);
        tvLoungeService.setOnClickListener(this);
        tvTodaysDashboard.setOnClickListener(this);
        tvMonthlyDashboard.setOnClickListener(this);

        setDefault(mActivity);
        tvStatus.setBackgroundResource(R.drawable.rectangle_teal_selected);
        tvStatus.setTextColor(getResources().getColor(R.color.white));
        displayView(0,  mActivity);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tvStatus:

                displayView(0,  mActivity);

                break;

            case R.id.tvLoungeService:

                displayView(1,  mActivity);

                break;

            case R.id.tvTodaysDashboard:

                displayView(2,  mActivity);

                break;

            case R.id.tvMonthlyDashboard:

                displayView(3,  mActivity);

                break;

        }

    }


    public static void displayView(int position, BaseActivity mActivity) {

        fragmentManager = mActivity.getSupportFragmentManager();

        switch (position) {

            case 0:
                setDefault(mActivity);
                tvStatus.setBackgroundResource(R.drawable.rectangle_teal_selected);
                tvStatus.setTextColor(mActivity.getResources().getColor(R.color.white));
                fragment = new StatusFragment();

                break;

            case 1:

                setDefault(mActivity);
                tvLoungeService.setBackgroundResource(R.drawable.rectangle_teal_selected);
                tvLoungeService.setTextColor(mActivity.getResources().getColor(R.color.white));
                fragment = new LoungeServicesFragment();

                break;

            case 2:

                setDefault(mActivity);
                tvTodaysDashboard.setBackgroundResource(R.drawable.rectangle_teal_selected);
                tvTodaysDashboard.setTextColor(mActivity.getResources().getColor(R.color.white));
                fragment = new TodaysDashboardFragment();

                break;

            case 3:

                setDefault(mActivity);
                tvMonthlyDashboard.setBackgroundResource(R.drawable.rectangle_teal_selected);
                tvMonthlyDashboard.setTextColor(mActivity.getResources().getColor(R.color.white));
                fragment = new MonthlyDashboardFragment();

                break;

            default:


                break;

        }

        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.flLounge, fragment)
                    .addToBackStack("")
                    .commit();
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }


    public static void setDefault(BaseActivity mActivity){

        tvStatus.setBackgroundResource(0);
        tvLoungeService.setBackgroundResource(0);
        tvTodaysDashboard.setBackgroundResource(0);
        tvMonthlyDashboard.setBackgroundResource(0);

        tvStatus.setTextColor(mActivity.getResources().getColor(R.color.blackNew));
        tvLoungeService.setTextColor(mActivity.getResources().getColor(R.color.blackNew));
        tvTodaysDashboard.setTextColor(mActivity.getResources().getColor(R.color.blackNew));
        tvMonthlyDashboard.setTextColor(mActivity.getResources().getColor(R.color.blackNew));

    }


}
