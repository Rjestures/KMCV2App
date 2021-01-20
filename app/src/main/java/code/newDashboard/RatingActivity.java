package code.newDashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kmcapp.android.R;

import code.view.BaseActivity;

public class RatingActivity extends BaseActivity implements View.OnClickListener {

    //TextView
    private TextView tvRatingTitle;

    //String
    private String pageFrom = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        init();
        setListeners();
        getIntentValues();

    }

    private void getIntentValues() {

        if (getIntent().getExtras()!=null){

            Intent intent=getIntent();

            pageFrom = intent.getStringExtra("pageFrom");

            switch (pageFrom){

                case "1":

                    tvRatingTitle.setText(getString(R.string.overallRating));

                    break;

                case "2":

                    tvRatingTitle.setText(getString(R.string.upkeepOfMaintenanceKmcLounge));

                    break;

                case "3":

                    tvRatingTitle.setText(getString(R.string.kmcPerformance));

                    break;

                case "4":

                    tvRatingTitle.setText(getString(R.string.userExperience));

                    break;

            }


        }

    }

    private void init() {

        tvRatingTitle = findViewById(R.id.tvRatingTitle);

    }

    private void setListeners() {
    }

    @Override
    public void onClick(View v) {

    }
}