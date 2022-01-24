package flag.com.ncubus.ui.bikeMap;

import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import flag.com.ncubus.R;

public class bikeMapFragment extends Fragment {

    private BikeMapViewModel mViewModel;

    public static bikeMapFragment newInstance() {
        return new bikeMapFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getParentFragmentManager().setFragmentResultListener("map_requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                String test = bundle.getString("test");
                TextView textview = (TextView)getView().findViewById(R.id.test_map_title);
                textview.setText(test);

            }
        });
        return inflater.inflate(R.layout.bike_map_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BikeMapViewModel.class);
        // TODO: Use the ViewModel
    }

}