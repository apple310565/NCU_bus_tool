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
                //Todo: 抵達時間那些我移動到 BusstopsFragment 那邊去做了
                // 這個fragment主要應該只做顯示班次就好
                //produce_spinner();
                //bus();
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

    // 以下都是若軒原本的部分---都搬過去 BusstopsFragment 那邊去做了
    public void renew_buslist(String url){

    }

    public void bus(String urlString){
        TextView Update_time =(TextView)getView().findViewById(R.id.Update_time);
        ProgressDialog dialog = ProgressDialog.show(getContext(),"讀取中"
                ,"請稍候",true);
        AtomicReference<String> global_SrcUpdateTime= new AtomicReference<>("");
        List<String> Zh_tws=new ArrayList<String>();
        List<String> NextBusTimes=new ArrayList<String>();
        final LinearLayout[] out = {(LinearLayout) getView().findViewById(R.id.sc)};
        new Thread(()->{//一次性Thread
            //String urlString = "https://ptx.transportdata.tw/MOTC/v2/Bus/EstimatedTimeOfArrival/City/Taoyuan/132?%24select=StopName%20%2C%20NextBusTime&%24filter=Direction%20eq%201&%24format=JSON\n";
            try {
                // 初始化 URL
                URL url = new URL(urlString);
                // 取得連線物件
                HttpURLConnection connection = null;
                connection = (HttpURLConnection) url.openConnection();
                // 設定 request timeout
                connection.setReadTimeout(1800);
                connection.setConnectTimeout(1800);
                // 模擬 Chrome 的 user agent, 因為手機的網頁內容較不完整
                connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36");
                // 設定開啟自動轉址
                connection.setInstanceFollowRedirects(true);
                // 若要求回傳 200 OK 表示成功取得網頁內容
                if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    // 讀取網頁內容
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String tempStr;
                    StringBuffer stringBuffer = new StringBuffer();
                    while ((tempStr = bufferedReader.readLine()) != null) {
                        stringBuffer.append(tempStr);
                        Log.d("[LOG] ", tempStr);
                    }
                    bufferedReader.close();
                    inputStream.close();
                    String responseString = stringBuffer.toString();
                    JSONArray jsonArray = new JSONArray(String.valueOf(responseString));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String Zh_tw = jsonObject.getJSONObject("StopName").getString("Zh_tw");
                        String NextBusTime = jsonObject.getString("NextBusTime");
                        String SrcUpdateTime = jsonObject.getString("SrcUpdateTime");
                        NextBusTime=NextBusTime.substring(11,18);
                        SrcUpdateTime=SrcUpdateTime.substring(11,18);
                        Log.e("[LOG] ", Zh_tw+": "+NextBusTime);
                        //add items
                        global_SrcUpdateTime.set(SrcUpdateTime);
                        Zh_tws.add(Zh_tw);
                        NextBusTimes.add(NextBusTime);
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        LinearLayout L=(LinearLayout)getView().findViewById(R.id.sc) ;
                        if(OUT.size()!=0){
                            for(int i=0;i<OUT.size();i++)L.removeView(OUT.get(i));
                        }
                        OUT.clear();
                        LinearLayout tmp =new LinearLayout(getActivity());
                        OUT.add(tmp);
                        tmp.setOrientation(LinearLayout.VERTICAL);
                        L.addView(tmp, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        dialog.dismiss();
                        for (int i = 0; i < Zh_tws.size(); i++) {
                            LinearLayout in = new LinearLayout(getActivity());
                            LinearLayout margin_tool = new LinearLayout(getActivity());
                            margin_tool.setPadding(15, 15, 15, 15);
                            in.setPadding(40, 40, 40, 40);
                            in.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            margin_tool.addView(in, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            tmp.addView(margin_tool, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            final TextView Zh_tw_tv = new TextView(getActivity());
                            Zh_tw_tv.setText(Zh_tws.get(i)+"| ");
                            Zh_tw_tv.setTextSize(18);
                            final TextView NextBusTimes_tv = new TextView(getActivity());
                            NextBusTimes_tv.setText("預計到站時間: " + NextBusTimes.get(i));
                            in.addView(Zh_tw_tv);
                            in.addView(NextBusTimes_tv);

                        }
                        Update_time.setText("最後更新時間: " + global_SrcUpdateTime);
                    }
                });
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }

//    public void produce_spinner(){
//        route_spinner = getView().findViewById(R.id.route);
//        items.add("[132]中壢 - 中央大學(去程)");
//        items.add("[132]中壢 - 中央大學(返程)");
//        items.add("[133]中壢 - 中央大學(去程)");
//        items.add("[133]中壢 - 中央大學(返程)");
//        items.add("[172]中央大學 - 高鐵桃園站(去程)");
//        items.add("[172]中央大學 - 高鐵桃園站(返程)");
//        items.add("[173]中央大學 - 高鐵桃園站(去程)");
//        items.add("[173]中央大學 - 高鐵桃園站(返程)");
//        route_name="";
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
//        route_spinner.setAdapter(adapter);
//        route_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if(!items.get(position).equals(route_name)){
//                    route_name=items.get(position);
//                    Log.i("[SELECT]",route_name);
//                    TextView route_tv=(TextView)getView().findViewById(R.id.route_Name);
//                    route_tv.setText(route_name);
//                    String direction="";
//                    String route_id="";
//                    direction=(route_name.split("\\(")[1]).split("\\)")[0];
//                    if(direction.equals("去程"))direction="0";
//                    else direction="1";
//                    route_id=(route_name.split("\\[")[1]).split("\\]")[0];
//                    String url = "https://ptx.transportdata.tw/MOTC/v2/Bus/EstimatedTimeOfArrival/City/Taoyuan/"+route_id+"?%24select=StopName%20%2C%20NextBusTime&%24filter=Direction%20eq%20"+direction+"&%24format=JSON";
//                    bus(url);
//                }
//
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//    }


}