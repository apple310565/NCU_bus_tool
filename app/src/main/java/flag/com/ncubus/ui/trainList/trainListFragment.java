package flag.com.ncubus.ui.trainList;

import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import flag.com.ncubus.MySQLiteHelper;
import flag.com.ncubus.R;
import flag.com.ncubus.databinding.FragmentHomeBinding;
import flag.com.ncubus.databinding.FragmentNotificationsBinding;
import flag.com.ncubus.ui.home.HomeViewModel;
import flag.com.ncubus.ui.notifications.NotificationsViewModel;

public class trainListFragment extends Fragment {

    private TrainListViewModel mViewModel;

    public static trainListFragment newInstance() {
        return new trainListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParentFragmentManager().setFragmentResultListener("train_old", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                //綁定交換按鈕
                ImageView exchange = (ImageView) getView().findViewById(R.id.exchange_btn);
                exchange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView textview1 = (TextView)getView().findViewById(R.id.source_stop);
                        TextView textview2 = (TextView)getView().findViewById(R.id.dest_stop);
                        String source_stop = textview1.getText().toString();
                        String dest_stop = textview2.getText().toString();
                        textview2.setText(source_stop);
                        textview1.setText(dest_stop);
                    }
                });

                String src_old = bundle.getString("src_old");
                String dest_old = bundle.getString("dest_old");
                //取得原本的出發和抵達站
                TextView source = (TextView) getView().findViewById(R.id.source_stop);
                source.setText(src_old);
                TextView dest = (TextView) getView().findViewById(R.id.dest_stop);
                dest.setText(dest_old);

                //綁定 submit 按鈕
                Button btn = (Button) getView().findViewById(R.id.submit);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView source = (TextView) getView().findViewById(R.id.source_stop);
                        String src_new = source.getText().toString();
                        TextView dest = (TextView) getView().findViewById(R.id.dest_stop);
                        String dest_new = dest.getText().toString();
                        Bundle result = new Bundle();
                        result.putString("src_new", src_new);
                        result.putString("dest_new", dest_new);
                        getParentFragmentManager().setFragmentResult("train_new", result);
                        //導向下一頁
                        NavController nc = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                        nc.navigate(R.id.navigation_notifications);
                    }
                });

            }
        });

        return inflater.inflate(R.layout.train_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TrainListViewModel.class);
        // TODO: Use the ViewModel
    }

}