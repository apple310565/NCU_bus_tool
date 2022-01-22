package flag.com.ncubus.ui.dashboard;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import flag.com.ncubus.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                    //Todo
                bus();
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public void renew_buslist(String url){

    }
    public void bus(){
        TextView route_Name =(TextView)getView().findViewById(R.id.route_Name);
        TextView Update_time =(TextView)getView().findViewById(R.id.Update_time);
        route_Name.setText("(132) 中央大學 -> 中壢");
        ProgressDialog dialog = ProgressDialog.show(getContext(),"讀取中"
                ,"請稍候",true);
        AtomicReference<String> global_SrcUpdateTime= new AtomicReference<>("");
        List<String> Zh_tws=new ArrayList<String>();
        List<String> NextBusTimes=new ArrayList<String>();
        LinearLayout out = (LinearLayout) getView().findViewById(R.id.sc);
        new Thread(()->{//一次性Thread
            String urlString = "https://ptx.transportdata.tw/MOTC/v2/Bus/EstimatedTimeOfArrival/City/Taoyuan/132?%24select=StopName%20%2C%20NextBusTime&%24filter=Direction%20eq%201&%24format=JSON\n";
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
                        dialog.dismiss();
                        for (int i = 0; i < Zh_tws.size(); i++) {
                            LinearLayout in = new LinearLayout(getActivity());
                            LinearLayout margin_tool = new LinearLayout(getActivity());
                            margin_tool.setPadding(15, 15, 15, 15);
                            in.setPadding(40, 40, 40, 40);
                            in.setBackgroundColor(Color.parseColor("#FFFFFF"));
                            //in.setOrientation(1);
                            margin_tool.addView(in, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            out.addView(margin_tool, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            final TextView Zh_tw_tv = new TextView(getActivity());
                            //Zh_tw_tv.setGravity(Gravity.CENTER);
                            Zh_tw_tv.setText(Zh_tws.get(i)+"| ");
                            Zh_tw_tv.setTextSize(18);
                            final TextView NextBusTimes_tv = new TextView(getActivity());
                            NextBusTimes_tv.setText("預計到站時間: " + NextBusTimes.get(i));
                            //final TextView UpdateTime_tv = new TextView(getActivity());
                            //UpdateTime_tv.setText("最後更新時間: " + global_SrcUpdateTime);
                            in.addView(Zh_tw_tv);
                            in.addView(NextBusTimes_tv);
                            //in.addView(UpdateTime_tv);
                        }
                        Update_time.setText("最後更新時間: " + global_SrcUpdateTime);
                    }
                });
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }).start();
    }


}