package code.newDashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.kmcapp.android.R;

import code.view.BaseFragment;

public class ScoreBoardFragment extends BaseFragment implements View.OnClickListener {

    //CardView
    private CardView cvStateRanking;

    //LinearLayout
    private LinearLayout llOverAllRating;

    //RelativeLayout
    private RelativeLayout rlUpkeepRating, rlPerformanceRating, rlUserExpRating;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_score_board, container, false);

        init(view);
        setListeners();

        return view;
    }

    private void init(View view) {

        cvStateRanking = view.findViewById(R.id.cvStateRanking);

        llOverAllRating = view.findViewById(R.id.llOverAllRating);

        rlUpkeepRating = view.findViewById(R.id.rlUpkeepRating);
        rlPerformanceRating = view.findViewById(R.id.rlPerformanceRating);
        rlUserExpRating = view.findViewById(R.id.rlUserExpRating);

    }

    private void setListeners() {

        cvStateRanking.setOnClickListener(this);

        llOverAllRating.setOnClickListener(this);

        rlUpkeepRating.setOnClickListener(this);
        rlPerformanceRating.setOnClickListener(this);
        rlUserExpRating.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.cvStateRanking:

                startActivity(new Intent(mActivity, StateRankingActivity.class));

                break;

            case R.id.llOverAllRating:

                Intent intent=new Intent(mActivity, RatingActivity.class);
                intent.putExtra("pageFrom","1");
                startActivity(intent);

                break;

            case R.id.rlUpkeepRating:

                Intent intent2=new Intent(mActivity, RatingActivity.class);
                intent2.putExtra("pageFrom","2");
                startActivity(intent2);

                break;

            case R.id.rlPerformanceRating:

                Intent intent3=new Intent(mActivity, RatingActivity.class);
                intent3.putExtra("pageFrom","3");
                startActivity(intent3);

                break;

            case R.id.rlUserExpRating:

                Intent intent4=new Intent(mActivity, RatingActivity.class);
                intent4.putExtra("pageFrom","4");
                startActivity(intent4);

                break;
        }
    }
}