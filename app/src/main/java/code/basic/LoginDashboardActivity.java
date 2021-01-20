package code.basic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.kmcapp.android.R;

import code.newDashboard.NewDashboardActivity;
import code.view.BaseActivity;

public class LoginDashboardActivity extends BaseActivity implements View.OnClickListener {

    //RelativeLayout
    private RelativeLayout rlLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_dasboard);

        init();
        setListeners();

    }

    private void setListeners() {

        rlLogin.setOnClickListener(this);

    }

    private void init() {

        rlLogin = findViewById(R.id.rlLogin);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.rlLogin:

                startActivity(new Intent(mActivity, NewDashboardActivity.class));

                break;

        }

    }
}