package flag.com.ncubus.ui.dashboard;

import android.app.ProgressDialog;
import android.content.Context;
//import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
//import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.HttpsURLConnection;

import flag.com.ncubus.R;
//import flag.com.ncubus.MainActivity;
import flag.com.ncubus.databinding.FragmentDashboardBinding;
import flag.com.ncubus.ui.FragmentChangeListener;
import flag.com.ncubus.ui.busstops.BusstopsFragment;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class DashboardFragment extends Fragment {
    Spinner route_spinner;
    String route_name;
    ArrayList<String> items =new ArrayList<>();
    //static final LinearLayout out = null;
    ArrayList<LinearLayout> OUT =new ArrayList<>();
    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;

    // ListView要建立的項目，會一一對應到 bus_number_list_layout裡的東西
    // 再逐一顯示在這個 fragment的 ListView區塊裡面
    String[] BusStops = {"132",
            "133",
            "172",
            "173"};
    String[] BusRoute = {"中壢 - 中央大學",
            "中壢 - 中央大學",
            "中央大學 - 高鐵桃園站",
            "中央大學 - 高鐵桃園站"};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 這邊的 onChanged 不能改，會掛掉
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                // 抵達時間那些我移動到 BusstopsFragment 那邊去做了
                // 這個fragment主要應該只做顯示班次就好
                addBuslist(); // 建立一開始的公車班次清單
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // 建立公車班次清單---開始
    public void addBuslist() {
        //把班次和路線塞進去 listView，之後路線可以考慮改方向 (e.g., 中壢-中央 & 中央-中壢)
        // 1是去程，2是返程
        ListView lstPrefer1 = (ListView)getView().findViewById(R.id.BusStop_list1);
        MyAdapter adapter = new MyAdapter(getActivity());
        lstPrefer1.setAdapter(adapter);
        ListView lstPrefer2 = (ListView)getView().findViewById(R.id.BusStop_list2);
        lstPrefer2.setAdapter(adapter);

        lstPrefer1.setOnItemClickListener(lstPrefer_Listener1); //綁定 click 事件
        lstPrefer2.setOnItemClickListener(lstPrefer_Listener2); //綁定 click 事件
    }

    // click listener，1是去程，2是返程
    private ListView.OnItemClickListener lstPrefer_Listener1 =
        new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                //被點擊時要做的事情
                String busNumber = parent.getItemAtPosition(position).toString();
                //傳班次+方向給下一頁(BusstopsFragment)，讓它去找班次+顯示
                Bundle result = new Bundle();
                result.putString("busNumber", busNumber);
                result.putInt("direction", 0);
                getParentFragmentManager().setFragmentResult("requestKey", result);
                //導向下一頁
                NavController nc = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                nc.navigate(R.id.navigation_busstops);
        }
    };
    // 目前先複製，和上面的差別只有 direction 的參數而已
    private ListView.OnItemClickListener lstPrefer_Listener2 =
        new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                //被點擊時要做的事情
                String busNumber = parent.getItemAtPosition(position).toString();
                //傳班次+方向給下一頁(BusstopsFragment)，讓它去找班次+顯示
                Bundle result = new Bundle();
                result.putString("busNumber", busNumber);
                result.putInt("direction", 1);
                getParentFragmentManager().setFragmentResult("requestKey", result);
                //導向下一頁
                NavController nc = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                nc.navigate(R.id.navigation_busstops);
        }
    };
    // 建立公車班次清單需要的類別
    public class MyAdapter extends BaseAdapter {
        final private LayoutInflater myInflater;

        public MyAdapter(Context c) {
            myInflater = LayoutInflater.from(c);
        }
        @Override
        public int getCount() {
            return BusStops.length;
        }
        @Override
        public Object getItem(int position) {
            return BusStops[position];
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            convertView = myInflater.inflate(R.layout.bus_number_list_layout, null);

            // 取得 bus_number_list_layout.xml 元件
            TextView busNumber = ((TextView) convertView.findViewById(R.id.busNumber));
            TextView busRoute = ((TextView) convertView.findViewById(R.id.busRoute));

            // 設定元件內容
            busNumber.setText(BusStops[position]);
            busRoute.setText(BusRoute[position]);

            return convertView;
        }
    }
    // 建立公車班次清單---結束

}