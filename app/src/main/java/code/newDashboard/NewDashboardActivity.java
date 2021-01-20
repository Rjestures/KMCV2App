package code.newDashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kmcapp.android.R;

import code.view.BaseActivity;
import code.view.BaseFragment;

public class NewDashboardActivity extends BaseActivity implements View.OnClickListener {

    //FrameLayout
    private FrameLayout flDashboard;

    //FragmentManager
    private FragmentManager fragmentManager;

    //BaseFragment
    private BaseFragment fragment;

    //TextView
    private TextView tvScoreBoard, tvFacilityReview, tvKmcPerformance, tvUpkeepKmc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dashboard);


        init();
        setListeners();

    }

    private void init() {

        flDashboard = findViewById(R.id.flDashboard);

        tvScoreBoard = findViewById(R.id.tvScoreBoard);
        tvFacilityReview = findViewById(R.id.tvFacilityReview);
        tvKmcPerformance = findViewById(R.id.tvKmcPerformance);
        tvUpkeepKmc = findViewById(R.id.tvUpkeepKmc);

        displayView(0);
    }

    private void setListeners() {

        tvScoreBoard.setOnClickListener(this);
        tvFacilityReview.setOnClickListener(this);
        tvKmcPerformance.setOnClickListener(this);
        tvUpkeepKmc.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.tvScoreBoard:

                setDefault();
                tvScoreBoard.setBackgroundResource(R.drawable.rectangle_teal_round_new);
                tvScoreBoard.setTextColor(getResources().getColor(R.color.white));
                ((NewDashboardActivity)mActivity).displayView(0);

                break;


            case R.id.tvFacilityReview:

                setDefault();
                tvFacilityReview.setBackgroundResource(R.drawable.rectangle_teal_round_new);
                tvFacilityReview.setTextColor(getResources().getColor(R.color.white));
                ((NewDashboardActivity)mActivity).displayView(1);

                break;

            case R.id.tvKmcPerformance:

                setDefault();
                tvKmcPerformance.setBackgroundResource(R.drawable.rectangle_teal_round_new);
                tvKmcPerformance.setTextColor(getResources().getColor(R.color.white));
                ((NewDashboardActivity)mActivity).displayView(2);

                break;

            case R.id.tvUpkeepKmc:

                setDefault();
                tvUpkeepKmc.setBackgroundResource(R.drawable.rectangle_teal_round_new);
                tvUpkeepKmc.setTextColor(getResources().getColor(R.color.white));
                ((NewDashboardActivity)mActivity).displayView(3);

                break;

        }

    }

    public void displayView(int position) {

        fragmentManager = getSupportFragmentManager();

        switch (position) {

            case 0:

                fragment = new ScoreBoardFragment();

                break;

            case 1:

                fragment = new FacilityOverviewFragment();

                break;

            case 2:

                fragment = new KmcPerformanceFragment();

                break;

            case 3:

                fragment = new UpkeepKmcLoungeFragment();

                break;

            default:

                break;



        }

        fragmentManager.beginTransaction()
                .replace(R.id.flDashboard, fragment)
                .commit();

    }
    private void setDefault() {

        tvScoreBoard.setBackgroundResource(0);
        tvScoreBoard.setTextColor(getResources().getColor(R.color.blackNew));


        tvFacilityReview.setBackgroundResource(0);
        tvFacilityReview.setTextColor(getResources().getColor(R.color.blackNew));

        tvKmcPerformance.setBackgroundResource(0);
        tvKmcPerformance.setTextColor(getResources().getColor(R.color.blackNew));

        tvUpkeepKmc.setBackgroundResource(0);
        tvUpkeepKmc.setTextColor(getResources().getColor(R.color.blackNew));
    }
}