package code.newDashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kmcapp.android.R;

import code.view.BaseFragment;

public class FacilityOverviewFragment extends BaseFragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_facility_overview, container, false);

        return view;
    }

    @Override
    public void onClick(View v) {

    }
}
