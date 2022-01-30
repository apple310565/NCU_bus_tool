package flag.com.ncubus.ui.Item;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import flag.com.ncubus.R;

/**
 * A fragment representing a list of Items.
 */
public class ItemFragment extends Fragment {

    private Map<String, Integer> price_map = new HashMap<>();
    private ArrayList<String> src_times = new ArrayList<>();
    private ArrayList<String> dest_times = new ArrayList<>();
    private ArrayList<String> train_nos = new ArrayList<>();
    private ArrayList<String> train_types = new ArrayList<>();
    private ArrayList<String> spend_times = new ArrayList<>();
    private ArrayList<String> status = new ArrayList<>();
    private ArrayList<Integer> costs = new ArrayList<>();
    private final Map<String, String> IDs = new HashMap<String, String>() {{
        put("基隆","0900");put("八堵","0920");put("七堵","0930");put("五堵","0950");put("汐止","0960");put("南港","0980");put("松山","0990");put("臺北","1000");put("萬華","1010");
        put("板橋","1020");put("浮洲","1030");put("樹林","1040");put("山佳","1060");put("鶯歌","1070");put("桃園","1080");put("內壢","1090");put("中壢","1100");
        put("埔心","1110");put("楊梅","1120");put("富岡","1130");put("湖口","1160");put("新豐","1170");put("竹北","1180");put("北新竹","1190");put("新竹","1210");
        put("香山","1230");put("崎頂","1240");put("竹南","1250");put("三坑","0910");put("百福","0940");put("汐科","0970");put("南樹林","1050");put("談文","2110");
        put("大山","2120");put("後龍","2130");put("龍港","2140");put("白沙屯","2150");put("新埔","2160");put("通霄","2170");put("苑裡","2180");put("日南","2190");
        put("大甲","2200");put("臺中港","2210");put("清水","2220");put("沙鹿","2230");put("龍井","2240");put("大肚","2250");put("追分","2260");put("彰化","3360");
        put("花壇","3370");put("員林","3390");put("永靖","3400");put("社頭","3410");put("田中","3420");put("二水","3430");put("林內","3450");put("石榴","3460");
        put("斗六","3470");put("斗南","3480");put("石龜","3490");put("大林","4050");put("民雄","4060");put("嘉義","4080");put("水上","4090");put("南靖","4100");
        put("後壁","4110");put("新營","4120");put("柳營","4130");put("林鳳營","4140");put("隆田","4150");put("拔林","4160");put("善化","4170");put("新市","4190");
        put("永康","4200");put("臺南","4220");put("保安","4250");put("中洲","4270");put("大湖","4290");put("路竹","4300");put("岡山","4310");put("橋頭","4320");
        put("楠梓","4330");put("左營","4350");put("鼓山","4380");put("高雄","4400");put("大橋","4210");put("大村","3380");put("嘉北","4070");put("新左營","4340");
        put("造橋","3140");put("豐富","3150");put("苗栗","3160");put("南勢","3170");put("銅鑼","3180");put("三義","3190");put("泰安","3210");put("后里","3220");put("豐原","3230");
        put("潭子","3250");put("臺中","3300");put("烏日","3330");put("成功","3350");put("大慶","3320");put("太原","3280");put("新烏日","3340");put("鳳山","4440");put("後庄","4450");
        put("九曲堂","4460");put("六塊厝","4470");put("屏東","5000");put("歸來","5010");put("麟洛","5020");put("西勢","5030");put("竹田","5040");put("潮州","5050");put("崁頂","5060");
        put("南州","5070");put("鎮安","5080");put("林邊","5090");put("佳冬","5100");put("東海","5110");put("枋寮","5120");put("5130", "加祿");put("內獅","5140");put("枋山","5160");
        put("枋野","5170");put("古莊","5180");put("大武","5190");put("瀧溪","5200");put("金崙","5210");put("太麻里","5220");put("知本","5230");put("康樂","5240");put("吉安","6250");
        put("志學","6240");put("平和","6230");put("壽豐","6220");put("豐田","6210");put("南平","6190");put("鳳林","6180");put("萬榮","6170");put("光復","6160");put("大富","6150");
        put("富源","6140");put("瑞穗","6130");put("三民","6120");put("玉里","6110");put("東里","6100");put("東竹","6090");put("富里","6080");put("池上","6070");put("海端","6060");
        put("關山","6050");put("瑞和","6040");put("瑞源","6030");put("鹿野","6020");put("山里","6010");put("臺東","6000");put("永樂","7110");put("東澳","7100");put("南澳","7090");
        put("武塔","7080");put("漢本","7070");put("和平","7060");put("和仁","7050");put("崇德","7040");put("新城","7030");put("景美","7020");put("北埔","7010");put("花蓮","7000");
        put("暖暖","7390");put("四腳亭","7380");put("瑞芳","7360");put("猴硐", "7350");put("三貂嶺","7330");put("牡丹","7320");put("雙溪","7310");put("貢寮","7300");put("福隆","7290");
        put("石城","7280");put("大里","7270");put("大溪","7260");put("龜山","7250");put("外澳","7240");put("頭城","7230");put("頂埔","7220");put("礁溪","7210");put("栗林","3240");
        put("頭家厝","3260");put("松竹","3270");put("四城","7200");put("宜蘭","7190");put("二結","7180");put("中里","7170");put("羅東","7160");put("冬山","7150");put("新馬","7140");
        put("蘇澳新","7130");put("蘇澳","7120");put("大華","7331");put("十分","7332");put("望古","7333");put("嶺腳","7334");put("平溪","7335");put("菁桐","7336");put("千甲","1191");
        put("新莊","1192");put("竹中","1193");put("六家","1194");put("上員","1201");put("竹東","1203");put("橫山","1204");put("九讚頭","1205");put("合興","1206");put("源泉","3431");
        put("濁水","3432");put("龍泉","3433");put("集集","3434");put("水里","3435");put("車埕","3436");put("南科","4180");put("長榮大學","4271");put("沙崙","4272");put("北湖","1150");
        put("海科館","7361");put("仁德","4260");put("三姓橋","1220");put("八斗子","7362");put("新富","1140");put("林榮新光","6200");put("精武","3290");put("五權","3310");put("內惟","4360");
        put("美術館","4370");put("三塊厝","4390");put("民族","4410");put("科工館","4420");put("正義","4430");put("榮華","1202");put("富貴","1207");put("內灣","1208");
    }};

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    @SuppressWarnings("unused")
    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        // Customize parameter initialization, e.g., args.put......
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
                //mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //接收使用者選好的資訊
        getParentFragmentManager().setFragmentResultListener("train_search", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                String src = bundle.getString("src");
                String dest = bundle.getString("dest");
                String date = bundle.getString("date").replaceAll("/","-");
                String time = bundle.getString("time");
                Log.d("[TRAIN_TEST]",src+" -> "+dest+" at "+date+" "+time);

                //顯示所有班次的出發時間,抵達時間,車種,票價
                getPrice(IDs.get(src), IDs.get(dest));
                getAlltrain(IDs.get(src), IDs.get(dest), date, time);
            }
        });
        return inflater.inflate(R.layout.fragment_item_list, container, false);
    }

    private void getAlltrain(String src_ID, String dest_ID, String date, String selectedTime) {
        ProgressDialog dialog = ProgressDialog.show(getContext(),"讀取中"
                ,"請稍候",true);
        new Thread(() -> { //一次性Thread
            HttpURLConnection connection = null;
            String urlString = "https://ptx.transportdata.tw/MOTC/v3/Rail/TRA/DailyTrainTimetable/OD/"+src_ID+"/to/"+dest_ID+"/"+date+"?%24top=214748364&%24format=JSON";
            try {
                // 初始化 URL
                URL url = new URL(urlString); //取得連線物件
                connection = (HttpURLConnection) url.openConnection(); //設定 request timeout
                connection.setReadTimeout(1800);
                connection.setConnectTimeout(1800); //模擬 Chrome 的 user agent, 因為手機的網頁內容較不完整
                connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36");

                // 設定開啟自動轉址
                connection.setInstanceFollowRedirects(true);
                // 若要求回傳 200 OK 表示成功取得網頁內容
                if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) { // 讀取網頁內容
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String tempStr;
                    StringBuffer stringBuffer = new StringBuffer();
                    while ((tempStr = bufferedReader.readLine()) != null) {
                        stringBuffer.append(tempStr);
                        //Log.d("[LOG] ", tempStr);
                    }
                    bufferedReader.close();
                    inputStream.close();
                    String responseString = stringBuffer.toString();
                    JSONObject jsonObj = new JSONObject(String.valueOf(responseString));
                    JSONArray jsonArray = jsonObj.getJSONArray("TrainTimetables");
                    //暫存出發時間, 抵達時間, 車次, 車種
                    // TODO: 車況還沒加
                    src_times = new ArrayList<>();
                    dest_times = new ArrayList<>();
                    train_nos = new ArrayList<>();
                    train_types = new ArrayList<>();
                    spend_times = new ArrayList<>();
                    status = new ArrayList<>();
                    costs = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String src_time = jsonObject.getJSONArray("StopTimes").getJSONObject(0).getString("DepartureTime");
                        String dest_time = jsonObject.getJSONArray("StopTimes").getJSONObject(1).getString("ArrivalTime");
                        String train_no = jsonObject.getJSONObject("TrainInfo").getString("TrainNo");
                        String train_type = jsonObject.getJSONObject("TrainInfo").getJSONObject("TrainTypeName").getString("Zh_tw");
                        String train_typecode = jsonObject.getJSONObject("TrainInfo").getString("TrainTypeCode");
                        if(train_type.contains("("))
                            train_type = train_type.substring(0,2);
                        //Log.e("[LOG] ", train_no+"("+train_type+"): "+src_time+" -> "+dest_time );

                        //用出發時間排序
                        boolean insert_succes = false;
                        for( int j=0; j<src_times.size(); j++){
                            if( src_time.compareTo(src_times.get(j) )<0){
                                src_times.add(j, src_time);
                                dest_times.add(j, dest_time);
                                train_nos.add(j, train_no);
                                train_types.add(j, train_type);
                                costs.add(j, price_map.get(train_typecode));
                                spend_times.add(j, getSpendingTime(src_time, dest_time));
                                insert_succes = true;
                                break;
                            }
                        }
                        if(insert_succes==false){
                            src_times.add(src_time);
                            dest_times.add(dest_time);
                            train_nos.add(train_no);
                            train_types.add(train_type);
                            costs.add(price_map.get(train_typecode));
                            spend_times.add(getSpendingTime(src_time, dest_time));
                        }
                    }
                }
                //Child Thread和 Parents對接的通道
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        dialog.dismiss();
                        RecyclerView recyclerView = (RecyclerView) getView();
                        recyclerView.setAdapter(new MyItemRecyclerViewAdapter(src_times, dest_times, train_nos,
                                train_types,costs,spend_times));
                        Log.d("Number of Train",Integer.valueOf(src_times.size()).toString());
                        int position;
                        for( position=0; position<src_times.size(); position++) {
                            if (selectedTime.compareTo(src_times.get(position)) <= 0) {
                                break;
                            }
                        }
                        if(position== src_times.size()) position--;
                        recyclerView.scrollToPosition(position);
                    }
                });
            }
            catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void getPrice(String src_ID, String dest_ID) {
        new Thread(() -> { //一次性Thread
            HttpURLConnection connection = null;
            String urlString = "https://ptx.transportdata.tw/MOTC/v3/Rail/TRA/ODFare/"+src_ID+"/to/"+dest_ID+"?%24top=214748364&%24format=JSON";
            try {
                // 初始化 URL
                URL url = new URL(urlString); //取得連線物件
                connection = (HttpURLConnection) url.openConnection(); //設定 request timeout
                connection.setReadTimeout(1800);
                connection.setConnectTimeout(1800); //模擬 Chrome 的 user agent, 因為手機的網頁內容較不完整
                connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36");
                // 設定開啟自動轉址
                connection.setInstanceFollowRedirects(true);
                // 若要求回傳 200 OK 表示成功取得網頁內容
                if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) { // 讀取網頁內容
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
                    JSONObject jsonObj = new JSONObject(String.valueOf(responseString));
                    JSONArray jsonArray = jsonObj.getJSONArray("ODFares");
                    //暫存出發時間, 抵達時間, 車次, 車種
                    // TODO: 之後要新增車況(準點或誤點), 票價
                    price_map = new HashMap<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String train_type = Integer.valueOf(jsonObject.getInt("TrainType")).toString();
                        int price = jsonObject.getJSONArray("Fares").getJSONObject(0).getInt("Price");
                        if(price_map.containsKey(train_type)==true && price_map.get(train_type)<=price)
                            continue;
                        else{
                            price_map.put(train_type, price);
                        }
                    }
                }
            }
            catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private String getSpendingTime(String src_time, String dest_time) {
        final int total_min = Integer.valueOf(dest_time.substring(0,2))*60+Integer.valueOf(dest_time.substring(3))-Integer.valueOf(src_time.substring(0,2))*60-Integer.valueOf(src_time.substring(3));
        final int h = total_min/60;
        final int m = total_min%60;
        return Integer.valueOf(h).toString()+"時"+Integer.valueOf(m).toString()+"分";
    }
}