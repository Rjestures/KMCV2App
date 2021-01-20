package code.checkOut;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kmcapp.android.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import code.database.AppSettings;
import code.database.DatabaseController;
import code.utils.AppUtils;
import code.view.BaseFragment;


public class NurseEnsureFragment extends BaseFragment implements View.OnClickListener {

    //RelativeLayout
    private RelativeLayout rlNext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.fragment_nurse_ensure, container, false);

        initialize(v);

        return v;
    }

    private void initialize(View v) {

        //RelativeLayout
        rlNext   = v.findViewById(R.id.rlNext);

        //setOnClickListener
        rlNext.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.rlNext:

                ((CheckOutActivity)getActivity()).displayView(2);

                break;

            default:

                break;
        }
    }
}
