package code.view;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {
    protected BaseActivity mActivity;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mActivity = (BaseActivity) getActivity();
    }

}
