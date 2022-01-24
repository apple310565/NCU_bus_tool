package flag.com.ncubus.ui.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import flag.com.ncubus.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    String responseString;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                // Todo
                bycycle();

                Button sweepButton = (Button) getView().findViewById(R.id.button);
                sweepButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EasyCard_Search_url();
                    }
                });

            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public void EasyCard_Search_url(){
        Log.e("[LOG]","IN!!!!");
        Uri uri = Uri.parse("https://ezweb.easycard.com.tw/search/CardSearch.php");//要跳轉的網址
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
    public void bycycle(){
        String urlString="https://ptx.transportdata.tw/MOTC/v2/Bike/Availability/Taoyuan?%24filter=StationUID%20eq%20'TAO2001'%20or%20StationUID%20eq%20'TAO2085'&%24orderby=StationUID%20&%24format=JSON";
        Log.e("[CONNECT 1]",urlString);
        ProgressDialog dialog = ProgressDialog.show(getContext(),"讀取中"
                ,"請稍候",true);
        new Thread(() -> { //一次性Thread
            Log.e("[Connect 2]",urlString);
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
                    responseString = stringBuffer.toString();
                    }
            } catch (IOException  e) {
                    e.printStackTrace();
            }
            //Child Thread和Parents對接的通道
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        dialog.dismiss();
                        JSONArray jsonArray = null;
                        jsonArray = new JSONArray(String.valueOf(responseString));
                        JSONObject jsonObject = null;
                        //圖書館站
                        jsonObject = jsonArray.getJSONObject(0);
                        TextView AvailableRentBikes1=(TextView)getView().findViewById(R.id.AvailableRentBikes1);
                        AvailableRentBikes1.setText("可租借車數: "+jsonObject.getString("AvailableRentBikes"));
                        TextView AvailableReturnBikes1=(TextView)getView().findViewById(R.id.AvailableReturnBikes1);
                        AvailableReturnBikes1.setText("可歸還車數: "+jsonObject.getString("AvailableReturnBikes"));
                        TextView ServiceStatus1=(TextView)getView().findViewById(R.id.ServiceStatus1);
                        if(jsonObject.getInt("ServiceStatus")==1)ServiceStatus1.setText("正常營運");
                        else if(jsonObject.getInt("ServiceStatus")==2)ServiceStatus1.setText("暫停營運");
                        else ServiceStatus1.setText("停止營運");
                        //依仁堂站
                        jsonObject = jsonArray.getJSONObject(1);
                        TextView AvailableRentBikes2=(TextView)getView().findViewById(R.id.AvailableRentBikes2);
                        AvailableRentBikes2.setText("可租借車數: "+jsonObject.getString("AvailableRentBikes"));
                        TextView AvailableReturnBikes2=(TextView)getView().findViewById(R.id.AvailableReturnBikes2);
                        AvailableReturnBikes2.setText("可歸還車數: "+jsonObject.getString("AvailableReturnBikes"));
                        TextView ServiceStatus2=(TextView)getView().findViewById(R.id.ServiceStatus2);
                        if(jsonObject.getInt("ServiceStatus")==1)ServiceStatus2.setText("正常營運");
                        else if(jsonObject.getInt("ServiceStatus")==2)ServiceStatus2.setText("暫停營運");
                        else ServiceStatus2.setText("停止營運");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }).start();
    }

}