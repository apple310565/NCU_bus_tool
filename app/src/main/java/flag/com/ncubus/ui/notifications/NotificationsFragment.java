package flag.com.ncubus.ui.notifications;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.Calendar;
import java.util.TimeZone;

import flag.com.ncubus.R;
import flag.com.ncubus.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;
    private String temp_date = "2022/01/01";
    private String temp_time = "00:00";
    private String temp_srcstop = "中壢";
    private String temp_deststop = "彰化";

    private boolean init_date = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                if (init_date) {
                    setDatetime_now();
                    init_date = false;
                }
                else{
                    TextView date = (TextView) getView().findViewById(R.id.date);
                    date.setText(temp_date);
                    TextView time = (TextView) getView().findViewById(R.id.time);
                    time.setText(temp_time);
                    TextView src = (TextView) getView().findViewById(R.id.source_stop);
                    src.setText(temp_srcstop);
                    TextView dest = (TextView) getView().findViewById(R.id.dest_stop);
                    dest.setText(temp_deststop);
                }
                //綁定所有按鈕的事件
                btn_click_binding();
                // TODO: 綁定搜尋，導向班次清單
                search_btn_binding();
            }
        });

        //接收使用者選好的地點
        getParentFragmentManager().setFragmentResultListener("train_new", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                String src_new = bundle.getString("src_new");
                String dest_new = bundle.getString("dest_new");
                Log.d("[TRAIN_TEST]: 新目的地",src_new+" -> "+dest_new);
                temp_srcstop = src_new;
                temp_deststop = dest_new;
                TextView src = (TextView) getView().findViewById(R.id.source_stop);
                src.setText(temp_srcstop);
                TextView dest = (TextView) getView().findViewById(R.id.dest_stop);
                dest.setText(temp_deststop);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void btn_click_binding() {
        //交換按鈕
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

        //日期按鈕
        TextView date = (TextView) getView().findViewById(R.id.date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] tmp = date.getText().toString().split("/");
                int year = Integer.valueOf(tmp[0]);
                int month = Integer.valueOf(tmp[1])-1;
                int day = Integer.valueOf(tmp[2]);
                DatePickerDialog datadialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String datetime = String.valueOf(year) + "/" + String.format("%02d", month+1) + "/" + String.format("%02d", day);
                        date.setText(datetime);
                        temp_date = datetime;
                    }
                }, year, month, day);
                datadialog.show();
                // 限制時間日期只能從現在開始
                Calendar cal = Calendar.getInstance();
                TimeZone tz = TimeZone.getTimeZone("Asia/Chongqing");
                cal.setTimeZone(tz);
                datadialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                init_date = false;
            }
        });

        //時間按鈕
        TextView time = (TextView) getView().findViewById(R.id.time);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] tmp = time.getText().toString().split(":");
                int hourOfDay = Integer.valueOf(tmp[0]);
                int minute = Integer.valueOf(tmp[1]);
                new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String datetime = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
                        time.setText(datetime);
                        temp_time = datetime;
                    }
                }, hourOfDay, minute, true).show();

            }
        });

        //設定為目前時間的按鈕
        TextView now = (TextView) getView().findViewById(R.id.setnow_btn);
        now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDatetime_now();
            }
        });

        //設定目的地 or 抵達地
        // 目的地
        TextView source = (TextView) getView().findViewById(R.id.source_stop);
        source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String src_old = source.getText().toString();
                TextView dest = (TextView) getView().findViewById(R.id.dest_stop);
                String dest_old = dest.getText().toString();
                Bundle result = new Bundle();
                result.putString("src_old", src_old);
                result.putString("dest_old", dest_old);
                result.putBoolean("setSrc", true);
                temp_srcstop = src_old;
                temp_deststop = dest_old;
                getParentFragmentManager().setFragmentResult("train_old", result);
                //導向下一頁
                NavController nc = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                nc.navigate(R.id.navigation_trainList);
            }
        });
        //抵達地
        TextView dest = (TextView) getView().findViewById(R.id.dest_stop);
        dest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView source = (TextView) getView().findViewById(R.id.source_stop);
                String src_old = source.getText().toString();
                String dest_old = dest.getText().toString();
                Bundle result = new Bundle();
                result.putString("src_old", src_old);
                result.putString("dest_old", dest_old);
                result.putBoolean("setSrc", false);
                temp_srcstop = src_old;
                temp_deststop = dest_old;
                getParentFragmentManager().setFragmentResult("train_old", result);
                //導向下一頁
                NavController nc = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                nc.navigate(R.id.navigation_trainList);
            }
        });

    }

    private void setDatetime_now() {
        //把日期時間預設為現在
        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone("Asia/Chongqing");
        calendar.setTimeZone(tz);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hourOfDay = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        // 日期
        TextView date = (TextView) getView().findViewById(R.id.date);
        String datetime = String.valueOf(year) + "/" + String.format("%02d", month+1) + "/" + String.format("%02d", day);
        date.setText(datetime);
        temp_date = datetime;
        // 時間
        TextView time = (TextView) getView().findViewById(R.id.time);
        datetime = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
        time.setText(datetime);  //取得選定的時間指定給時間編輯框
        temp_time = datetime;
    }

    private void search_btn_binding() {
        //綁定 submit 按鈕
        Button btn = (Button) getView().findViewById(R.id.submit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView source = (TextView) getView().findViewById(R.id.source_stop); String src = source.getText().toString();
                TextView destination = (TextView) getView().findViewById(R.id.dest_stop); String dest = destination.getText().toString();
                TextView date = (TextView) getView().findViewById(R.id.date); String str_date = date.getText().toString();
                TextView time = (TextView) getView().findViewById(R.id.time); String str_time = time.getText().toString();
                Bundle result = new Bundle();
                result.putString("src", src);
                result.putString("dest", dest);
                result.putString("date", str_date);
                result.putString("time", str_time);
                getParentFragmentManager().setFragmentResult("train_search", result);
                //導向下一頁
                NavController nc = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                nc.navigate(R.id.navigation_trainItem);
            }
        });
    }
}