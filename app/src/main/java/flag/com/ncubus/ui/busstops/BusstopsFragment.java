package flag.com.ncubus.ui.busstops;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.HttpsURLConnection;

import flag.com.ncubus.R;
import flag.com.ncubus.databinding.FragmentDashboardBinding;
import flag.com.ncubus.ui.dashboard.DashboardFragment;
import flag.com.ncubus.ui.dashboard.DashboardViewModel;

public class BusstopsFragment extends Fragment {

    private BusstopsViewModel busstopsViewModel;
    public static volatile List<String> Zh_tws = new ArrayList<String>();
    public static volatile List<String> NextBusTimes = new ArrayList<String>();
    public static volatile List<Integer> StopSequences = new ArrayList<Integer>();
    public static volatile AtomicReference<String> global_SrcUpdateTime= new AtomicReference<>("");
    String[] ArriveTimes = { "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00"  };
    String[] BusStops = { "台北", "中壢", "苗栗", "台中", "彰化", "嘉義", "屏東" };

    public static BusstopsFragment newInstance() {
        return new BusstopsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                String busNumber = bundle.getString("busNumber");
                Integer direction = bundle.getInt("direction");
                //取得班次和方向，開始查所有站名+抵達時間

                String url = "https://ptx.transportdata.tw/MOTC/v2/Bus/EstimatedTimeOfArrival/City/Taoyuan/"+busNumber
                        +"?%24select=StopName%20%2C%20NextBusTime&%24filter=Direction%20eq%20"+direction.toString()+"&%24format=JSON";
                bus(url);
                //只要能把站名和抵達時間，存到上面的 String[]就可以了
                //有存到，但是有神奇的bug: 點第一次沒有蓋到，但在繼續點就都有......
                //然後會有點當掉 -> 有時候沒有更新，比方說不管點哪個都是2分鐘前的某一班的清單
                TextView textview = (TextView)getView().findViewById(R.id.busNumber);
                textview.setText(busNumber);
                //上面提到的問題修正了，會有那個問題應該是因為thread是獨立執行的，所以主程式不會等thread將資料改完才回傳，處理方式是把底下的東西搬到下面

            }
        });
        return inflater.inflate(R.layout.busstops_fragment, container, false);
    }

    public void updateBuslist() {
        //把所有站名和抵達時間放進去
        ListView lstPrefer = (ListView)getView().findViewById(R.id.BusStop_list);
        BusstopsFragment.MyAdapter adapter = new BusstopsFragment.MyAdapter(getActivity());
        lstPrefer.setAdapter(adapter);
    }
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
            convertView = myInflater.inflate(R.layout.busstop_list_layout, null);

            // 取得 bus_number_list_layout.xml 元件
            TextView arriveTime = ((TextView) convertView.findViewById(R.id.arriveTime));
            TextView busStop = ((TextView) convertView.findViewById(R.id.busStop));

            // 設定元件內容
            arriveTime.setText(ArriveTimes[position]);
            if(ArriveTimes[position]=="末班駛離"){
                arriveTime.setBackgroundResource(R.drawable.circle_border_solid_gray);
                arriveTime.setTextColor(getResources().getColor(R.color.main_gray));
            }
            else if(ArriveTimes[position]=="即將到站"){
                arriveTime.setBackgroundResource(R.drawable.circle_border_solid);
                arriveTime.setTextColor(getResources().getColor(R.color.white));
            }
            busStop.setText(BusStops[position]);

            return convertView;
        }
    }

    public void bus(String urlString){
        ProgressDialog dialog = ProgressDialog.show(getContext(),"讀取中"
                ,"請稍候",true);
        new Thread(() -> { //一次性Thread
            Log.e("[Connect]",urlString);
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
                    //要清空，不然會一直加上去
                    Zh_tws = new ArrayList<String>();
                    NextBusTimes = new ArrayList<String>();
                    StopSequences = new ArrayList<Integer>();
                    global_SrcUpdateTime= new AtomicReference<>("");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String Zh_tw = jsonObject.getJSONObject("StopName").getString("Zh_tw");
                        String SrcUpdateTime = jsonObject.getString("SrcUpdateTime");
                        int StopSequence = jsonObject.getInt("StopSequence");
                        SrcUpdateTime = SrcUpdateTime.substring(11, 16);
                        String NextBusTime="";
                        if(!jsonObject.isNull("NextBusTime")){
                            NextBusTime = jsonObject.getString("NextBusTime");
                            NextBusTime=NextBusTime.substring(11, 16);
                        }
                        else{
                            NextBusTime="末班駛離";
                        }
                        if(!jsonObject.isNull("EstimateTime")){
                            //出於嚴謹起見減掉了當前時間後最後更新時間的差額，但是雖然在模擬器上沒事，但手機上會出問題就先放棄減掉時間差額
                            Log.d("[LOG Estimates] ", jsonObject.getString("EstimateTime"));
                            //Calendar calendar = Calendar.getInstance();
                            //int hour=calendar.get(Calendar.HOUR_OF_DAY)+8;
                            //if(hour>24)hour-=24;
                            //int minute=calendar.get(Calendar.MINUTE);
                            //int second=calendar.get(Calendar.SECOND);
                            //Log.d("[Time]",String.valueOf(hour)+":"+String.valueOf(minute)+":"+String.valueOf(second));
                            //String [] tmp_calendar=(jsonObject.getString("SrcUpdateTime").substring(11,19)).split(":");
                            //double s = (Double.valueOf(tmp_calendar[0]))*3600+Double.valueOf(tmp_calendar[1])*60+Double.valueOf(tmp_calendar[2]);
                            //double s2 = Double.valueOf(hour*3600+minute*60+second);
                            //int num=(int)((jsonObject.getDouble("EstimateTime")-s2+s)/60);
                            int num=(int)((jsonObject.getDouble("EstimateTime"))/60);
                            if(num<=2)NextBusTime="即將到站";
                            else NextBusTime=String.valueOf(num)+"分鐘";
                        }
                        else{
                            Log.d("[LOG Estimates] ", "None");
                        }
                        Log.e("[LOG] ", Zh_tw + ": " + NextBusTime);
                        //add items
                        global_SrcUpdateTime.set(SrcUpdateTime);
                        Zh_tws.add(Zh_tw);
                        NextBusTimes.add(NextBusTime);
                        StopSequences.add(StopSequence);
                    }
                }
                //Child Thread和 Parents對接的通道
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        dialog.dismiss();
                        TextView Update_time =(TextView)getView().findViewById(R.id.Update_time);
                        if (NextBusTimes.size()!=0) {
                            //用 StopSequences 排序的序號排序
                            List<String> tmp_stops = new ArrayList<String>();
                            List<String> tmp_times = new ArrayList<String>();
                            for( int i=1; i<=StopSequences.size(); i++){
                                int index = StopSequences.indexOf(i);
                                tmp_stops.add(Zh_tws.get(index));
                                tmp_times.add(NextBusTimes.get(index));
                            }
                            ArriveTimes = tmp_times.toArray(new String[0]);
                            BusStops = tmp_stops.toArray(new String[0]);
                            updateBuslist(); //更新站名+抵達時間
                            Update_time.setText("最後更新時間: " + global_SrcUpdateTime);
                        }
                        else{
                            updateBuslist(); //更新站名+抵達時間
                            Update_time.setText("最後更新時間: None");
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Message")
                                    .setMessage("此IP今日的的API呼叫次數用盡，請問是否要前往桃園公車動態資訊網查看公車動態?")
                                    .setNegativeButton("no", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Uri uri = Uri.parse("https://ebus.tycg.gov.tw/ebus");//要跳轉的網址
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            startActivity(intent);
                                        }
                                    })
                                    .show();
                        }
                    }
                });
            }
            catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

}