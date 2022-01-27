package flag.com.ncubus.ui.home;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import javax.net.ssl.HttpsURLConnection;

import flag.com.ncubus.MySQLiteHelper;
import flag.com.ncubus.R;
import flag.com.ncubus.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private SQLiteDatabase db;
    MySQLiteHelper dbHelper;

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
                bycycle();
                bindClick_toMap();

                dbHelper = new MySQLiteHelper(getActivity(),"Course_sub",null,1);
                db = dbHelper.getWritableDatabase();

                Button sweepButton = (Button) getView().findViewById(R.id.button);
                sweepButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        copy_to_clipboard(db);
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
                        AvailableRentBikes1.setText(jsonObject.getString("AvailableRentBikes"));
                        TextView AvailableReturnBikes1=(TextView)getView().findViewById(R.id.AvailableReturnBikes1);
                        AvailableReturnBikes1.setText(jsonObject.getString("AvailableReturnBikes"));
                        TextView ServiceStatus1=(TextView)getView().findViewById(R.id.ServiceStatus1);
                        if(jsonObject.getInt("ServiceStatus")==1)ServiceStatus1.setText("正常營運");
                        else if(jsonObject.getInt("ServiceStatus")==2)ServiceStatus1.setText("暫停營運");
                        else ServiceStatus1.setText("停止營運");
                        //依仁堂站
                        jsonObject = jsonArray.getJSONObject(1);
                        TextView AvailableRentBikes2=(TextView)getView().findViewById(R.id.AvailableRentBikes2);
                        AvailableRentBikes2.setText(jsonObject.getString("AvailableRentBikes"));
                        TextView AvailableReturnBikes2=(TextView)getView().findViewById(R.id.AvailableReturnBikes2);
                        AvailableReturnBikes2.setText(jsonObject.getString("AvailableReturnBikes"));
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
    public void copy_to_clipboard(SQLiteDatabase db){
        String code="7630361912";
        Cursor EasyCard_Code=db.rawQuery("SELECT _Code FROM EasyCard",null);
        EasyCard_Code.moveToFirst();
        if(EasyCard_Code.getCount()==0){
            new AlertDialog.Builder(getActivity())
                    .setTitle("Message")
                    .setMessage("請問是否要存入卡片外觀卡號，這樣以後按此按鈕時都會自動將存入的卡號貼入您的剪貼簿中。")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.e("[LOG]","yes!!!!");

                            LayoutInflater inflater = LayoutInflater.from(getActivity());
                            final View v = inflater.inflate(R.layout.input_easy_card_code, null);
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("請輸入卡片外觀卡號")
                                    .setView(v)
                                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.e("[LOG]","yes 2!!!!");
                                            EditText code_tv=(EditText)v.findViewById(R.id.EasyCardCode_tv);
                                            String Digit = "[0-9]+";
                                            if(!code_tv.getText().toString().matches(Digit)||code_tv.getText().toString().equals("")){
                                                Toast.makeText(getContext(), "輸入不合格式", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                ContentValues cv = new ContentValues();
                                                cv.put("_Code", code_tv.getText().toString());
                                                db.insert("EasyCard", null, cv);
                                                Vibrator myVibrator = (Vibrator)getActivity().getSystemService(Service.VIBRATOR_SERVICE);//取得震動
                                                myVibrator.vibrate(50);
                                                ClipboardManager cm  = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                                cm.setText(code_tv.getText().toString());
                                                Toast.makeText(getContext(), "已複製: " + cm.getText(), Toast.LENGTH_SHORT).show();
                                                EasyCard_Search_url();
                                            }
                                        }
                                    })
                                    .setNegativeButton("no", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.e("[LOG]","cancel");
                                            EasyCard_Search_url();
                                        }
                                    })
                                    .show();
                        }
                    })
                    .setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EasyCard_Search_url();
                        }
                    })
                    .show();
        }
        else{
            code = EasyCard_Code.getString(0);
            Vibrator myVibrator = (Vibrator)getActivity().getSystemService(Service.VIBRATOR_SERVICE);//取得震動
            myVibrator.vibrate(50);
            ClipboardManager cm  = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setText(code);
            Toast.makeText(getContext(), "已複製: " + cm.getText(), Toast.LENGTH_SHORT).show();
            EasyCard_Search_url();
        }
    }

    public void bindClick_toMap(){
        TextView textview = (TextView)getView().findViewById(R.id.AvailableRentBikes1);
        textview.setOnClickListener(getBike_map1);
        textview = (TextView)getView().findViewById(R.id.AvailableReturnBikes1);
        textview.setOnClickListener(getBike_map1);
        textview = (TextView)getView().findViewById(R.id.AvailableRentBikes2);
        textview.setOnClickListener(getBike_map2);
        textview = (TextView)getView().findViewById(R.id.AvailableReturnBikes2);
        textview.setOnClickListener(getBike_map2);
    }

    final private View.OnClickListener getBike_map1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("[MAP]","圖書館的地圖~");
            Bundle result = new Bundle();
            result.putDouble("lat", 24.968438302080717);
            result.putDouble("lng", 121.1943910820179);
            getParentFragmentManager().setFragmentResult("bikemap", result);
            //導向下一頁
            NavController nc = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
            nc.navigate(R.id.navigation_bikemap);
        }
    };

    final private View.OnClickListener getBike_map2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("[MAP]","依仁堂的地圖~");
            Bundle result = new Bundle();
            result.putDouble("lat", 24.968967179889386);
            result.putDouble("lng", 121.1908966);
            getParentFragmentManager().setFragmentResult("bikemap", result);
            //導向下一頁
            NavController nc = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
            nc.navigate(R.id.navigation_bikemap);
        }
    };

}