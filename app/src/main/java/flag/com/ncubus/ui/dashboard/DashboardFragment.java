package flag.com.ncubus.ui.dashboard;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
//import android.database.Cursor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
//import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.HttpsURLConnection;

import flag.com.ncubus.MySQLiteHelper;
import flag.com.ncubus.R;
//import flag.com.ncubus.MainActivity;
import flag.com.ncubus.databinding.FragmentDashboardBinding;
import flag.com.ncubus.ui.FragmentChangeListener;
import flag.com.ncubus.ui.busstops.BusstopsFragment;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;

    private MySQLiteHelper dbHelper ;
    private SQLiteDatabase db;
    private final String DB_name = "Blossom.db";
    private final String bus_table = "FarvoriteBus";

    private final String[] BusStops = {"132",
            "133",
            "172",
            "173"};
    private ArrayList<String> BusStops1 = new ArrayList<>();
    private ArrayList<String> BusStops2 = new ArrayList<>();
    Map<String, Integer> check1 = new HashMap<>();
    Map<String, Integer> check2 = new HashMap<>();
    final Map<String, String> BusRoutes = new HashMap<String, String>() {{
        put("132","?????? - ????????????"); put("133","?????? - ????????????"); put("172","???????????? - ???????????????"); put("173","???????????? - ???????????????");
    }};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // ?????????????????????
        dbHelper = new MySQLiteHelper(getContext(), DB_name,null,1);
        db = dbHelper.getWritableDatabase();

        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                updateBusList(0);
                updateBusList(1);
                addBuslist(); // ????????????????????????????????????
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ????????????????????????---??????
    public void addBuslist() {
        //??????????????????????????? listView???????????????????????????????????? (e.g., ??????-?????? & ??????-??????)
        // 1?????????(direction=0)???2?????????(direction=1)
        ListView lstPrefer1 = (ListView)getView().findViewById(R.id.BusStop_list1);
        MyAdapter adapter = new MyAdapter(getActivity(), 0, BusStops1);
        lstPrefer1.setAdapter(adapter);
        ListView lstPrefer2 = (ListView)getView().findViewById(R.id.BusStop_list2);
        adapter = new MyAdapter(getActivity(), 1, BusStops2);
        lstPrefer2.setAdapter(adapter);

        lstPrefer1.setOnItemClickListener(lstPrefer_Listener1); //?????? click ??????
        lstPrefer2.setOnItemClickListener(lstPrefer_Listener2); //?????? click ??????
    }

    // click listener???1????????????2?????????
    private final ListView.OnItemClickListener lstPrefer_Listener1 =
        new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                //???????????????????????????
                String busNumber = parent.getItemAtPosition(position).toString();
                //?????????+??????????????????(BusstopsFragment)?????????????????????+??????
                Bundle result = new Bundle();
                result.putString("busNumber", busNumber);
                result.putInt("direction", 0);
                getParentFragmentManager().setFragmentResult("requestKey", result);
                //???????????????
                NavController nc = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                nc.navigate(R.id.navigation_busstops);
        }
    };
    // ?????????????????????????????????????????? direction ???????????????
    private final ListView.OnItemClickListener lstPrefer_Listener2 =
        new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                //???????????????????????????
                String busNumber = parent.getItemAtPosition(position).toString();
                //?????????+??????????????????(BusstopsFragment)?????????????????????+??????
                Bundle result = new Bundle();
                result.putString("busNumber", busNumber);
                result.putInt("direction", 1);
                getParentFragmentManager().setFragmentResult("requestKey", result);
                //???????????????
                NavController nc = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                nc.navigate(R.id.navigation_busstops);
        }
    };

    public class MyAdapter extends BaseAdapter {
        final private LayoutInflater myInflater;
        final private int Direction;
        private ArrayList<String> BusStop;

        public MyAdapter(Context c, int direction, ArrayList<String> busStop) {
            myInflater = LayoutInflater.from(c);
            Direction = direction;
            BusStop = busStop;
        }
        @Override
        public int getCount() {
            return BusStop.size();
        }
        @Override
        public Object getItem(int position) {
            return BusStop.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            convertView = myInflater.inflate(R.layout.bus_number_list_layout, null);
            Drawable filled_heart = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_heart_solid, null);
            Drawable regular_heart = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_heart_regular, null);
            TextView busNumber = ((TextView) convertView.findViewById(R.id.busNumber));
            TextView busRoute = ((TextView) convertView.findViewById(R.id.busRoute));
            busNumber.setText(BusStop.get(position));
            busRoute.setText(BusRoutes.get(BusStop.get(position)));
            //????????????/??????????????????
            ImageView heart = (ImageView) convertView.findViewById(R.id.like);
            if(Direction==0){
                if(check1.get(BusStop.get(position))==1)
                    heart.setImageDrawable(filled_heart);
            }
            else{
                if(check2.get(BusStop.get(position))==1)
                    heart.setImageDrawable(filled_heart);
            }
            heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(heart.getDrawable().getConstantState().equals(regular_heart.getConstantState())) {
                        heart.setImageDrawable(filled_heart);
                        Log.d("[Bus Test]", BusStop.get(position)+"????????????");
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("_BusID", BusStop.get(position));
                        contentValues.put("_Direction", Direction);
                        db.insert(bus_table,null,contentValues);
                    }
                    else{
                        heart.setImageDrawable(regular_heart);
                        Log.d("[Bus Test]", BusStop.get(position)+"????????????");
                        db.delete(bus_table, "_BusID = "+BusStop.get(position)+" AND _Direction = "+
                                Integer.valueOf(Direction).toString(),null);
                    }
                    updateBusList(Direction);
                    addBuslist();
                }
            });

            return convertView;
        }
    }

    private void updateBusList(int direction) {
        if(direction==0) {
            BusStops1 = new ArrayList<>();
            check1 = new HashMap<>();
        }
        else {
            BusStops2 = new ArrayList<>();
            check2 = new HashMap<>();
        }
        //??????????????????????????????
        Cursor c = db.rawQuery("SELECT * FROM " + bus_table +" WHERE _Direction = "+Integer.valueOf(direction).toString()+
                " ORDER BY _BusID",null);
        c.moveToFirst();
        for(int i=0; i<c.getCount(); i++){
            String busNumber = c.getString(0);
            if(direction==0) {
                BusStops1.add(busNumber);
                check1.put(busNumber,1);
            }
            else {
                BusStops2.add(c.getString(0));
                check2.put(busNumber,1);
            }
            c.moveToNext();
        }
        for(int i=0; i<BusStops.length; i++){
            if(direction==0 ){
                if(BusStops1.contains(BusStops[i])==false) {
                    BusStops1.add(BusStops[i]);
                    check1.put(BusStops[i], 0);
                }
            }
            else {
                if(BusStops2.contains(BusStops[i])==false) {
                    BusStops2.add(BusStops[i]);
                    check2.put(BusStops[i], 0);
                }
            }
        }
    }
}