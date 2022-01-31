package flag.com.ncubus.ui.trainList;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import flag.com.ncubus.MainActivity;
import flag.com.ncubus.MySQLiteHelper;
import flag.com.ncubus.R;
import flag.com.ncubus.databinding.FragmentHomeBinding;
import flag.com.ncubus.databinding.FragmentNotificationsBinding;
import flag.com.ncubus.ui.dashboard.DashboardFragment;
import flag.com.ncubus.ui.home.HomeViewModel;
import flag.com.ncubus.ui.notifications.NotificationsViewModel;

public class trainListFragment extends Fragment {

    private TrainListViewModel mViewModel;

    public static trainListFragment newInstance() {
        return new trainListFragment();
    }

    final String[] Areas = {"北北基", "桃園", "新竹", "苗栗", "台中", "彰化", "南投", "雲林",
            "嘉義", "台南", "高雄", "屏東", "台東", "花蓮", "宜蘭" };
    final String[][] all_stop ={
            { "南港", "松山", "台北", "萬華", "五堵", "汐止", "汐科", "板橋", "浮洲", "樹林", "南樹林","山佳", "鶯歌",
                "福隆", "貢寮", "雙溪", "牡丹", "三貂嶺", "大華", "十分", "望古", "嶺腳", "平溪", "菁桐", "猴硐", "瑞芳", "八斗子", "四腳亭",
                "基隆", "三坑", "八堵", "七堵", "百福", "海科館", "暖暖" },
            { "桃園", "內壢", "中壢", "埔心", "楊梅", "富岡", "新富" },
            { "北湖", "湖口", "新豐", "竹北", "竹中", "六家", "上員", "榮華", "竹東", "橫山", "九讚頭", "合興", "富貴", "內灣", "北新竹",
                "千甲", "新莊", "新竹", "三姓橋", "香山" },
            { "崎頂", "竹南", "談文", "大山", "後龍", "龍港", "白沙屯", "新埔", "通霄", "苑裡","造橋", "豐富", "苗栗", "南勢", "銅鑼", "三義" },
            { "日南", "大甲", "台中港", "清水", "沙鹿", "龍井", "大肚", "追分", "泰安", "后里","豐原", "栗林", "潭子", "頭家厝", "松竹","太原","精武",
                "台中","五罐","大慶","烏日", "新烏日", "成功" },
            { "彰化", "花壇", "大村", "員林", "永靖", "社頭", "田中", "二水", "源泉" },
            { "濁水", "龍泉", "集集", "水里", "車程" },
            { "林內", "石榴", "斗六", "斗南", "石龜" },
            { "大林", "民雄", "水上", "南靖", "嘉北", "嘉義" },
            { "後壁", "新營", "柳營", "林鳳營", "隆田", "拔林", "善化", "南科", "新市", "永康","大橋", "台南", "保安", "仁德", "中洲", "長榮大學", "沙崙" },
            { "大湖", "路竹", "岡山", "橋頭", "楠梓", "新左營", "左營", "內惟", "美術館", "鼓山", "三塊厝", "高雄", "民族", "科工館", "正義", "鳳山",
                "後庄", "九曲堂" },
            { "六塊厝", "屏東", "歸來", "麟洛", "西勢", "竹田", "潮州", "崁頂", "南州", "鎮安","林邊", "佳冬", "東海","枋寮","加祿","內獅","枋山" },
            { "大武", "瀧溪", "金崙", "太麻里", "知本", "康樂", "台東", "山里", "鹿野", "瑞源","瑞和", "關山", "海端","池上" },
            { "富里", "東竹", "東里", "玉里", "三民", "瑞穗", "富源", "大富", "光復", "萬榮","鳳林","南平","林榮新光","豐田","壽豐","平和","志學",
                "吉安","花蓮","北埔","景美","新城","崇德","和仁","和平" },
            { "漢本", "武塔", "南澳", "東澳", "永樂", "蘇澳", "蘇澳新", "新馬", "冬山", "羅東","中里","二結","宜蘭","四城","礁溪","頂埔",
                "頭城","外澳","龜山","大溪","大里","石城" },
    };
    private boolean temp_setSrc = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParentFragmentManager().setFragmentResultListener("train_old", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                //取得+設定原本的出發和抵達站
                String src_old = bundle.getString("src_old");
                String dest_old = bundle.getString("dest_old");
                temp_setSrc = bundle.getBoolean("setSrc");
                setStop_color();
                TextView source = (TextView) getView().findViewById(R.id.source_stop);
                source.setText(src_old);
                TextView dest = (TextView) getView().findViewById(R.id.dest_stop);
                dest.setText(dest_old);
                //綁定按鈕事件
                btn_binding();

                //新增地區清單+綁定地區清單點擊後會更新站名的ListView事件
                addArealist();
            }
        });

        return inflater.inflate(R.layout.train_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TrainListViewModel.class);
        // Use the ViewModel
    }

    public void btn_binding() {
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

        TextView source = (TextView) getView().findViewById(R.id.source_stop);
        source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp_setSrc = true;
                setStop_color();
            }
        });
        TextView dest = (TextView) getView().findViewById(R.id.dest_stop);
        dest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp_setSrc = false;
                setStop_color();
            }
        });
    }

    // 建立地區清單
    public void addArealist() {
        //把地區塞進去 listView
        ListView lstPrefer = (ListView)getView().findViewById(R.id.area);
        trainListFragment.MyAdapter adapter = new trainListFragment.MyAdapter(getActivity(), Areas);
        lstPrefer.setAdapter(adapter);

        lstPrefer.setOnItemClickListener(lstPrefer_Listener); //綁定 click 事件
    }

    public static class MyAdapter extends BaseAdapter {
        final private LayoutInflater myInflater;
        private String[] data ={};

        public MyAdapter(Context c, String[] d) {
            myInflater = LayoutInflater.from(c);
            data = d;
        }
        @Override
        public int getCount() {
            return data.length;
        }
        @Override
        public Object getItem(int position) {
            return data[position];
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            convertView = myInflater.inflate(R.layout.train_list_layout, null);
            TextView area = ((TextView) convertView.findViewById(R.id.area_name));
            area.setText(data[position]);
            return convertView;
        }
    }

    private ListView.OnItemClickListener lstPrefer_Listener =
        new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                //被點擊時要做的事情 -> 更新站名的清單
                String[] stops = all_stop[position];
                ListView lstPrefer = (ListView)getView().findViewById(R.id.stop);
                MyAdapter adapter = new MyAdapter(getActivity(), stops);
                lstPrefer.setAdapter(adapter);
                lstPrefer.setOnItemClickListener(setStop_Listener); //綁定 click 事件
        }
    };

    private ListView.OnItemClickListener setStop_Listener =
        new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                //被點擊時要做的事情 -> 改變目的地或抵達地的站名
                String stop_name = parent.getItemAtPosition(position).toString();
                if(temp_setSrc==true){
                    TextView stop = ((TextView) getView().findViewById(R.id.source_stop));
                    stop.setText(stop_name);
                }
                else{
                    TextView stop = ((TextView) getView().findViewById(R.id.dest_stop));
                    stop.setText(stop_name);
                }
        }
    };

    private void setStop_color() {
        TextView set_stop;
        TextView another_stop;
        if(temp_setSrc==true) {
            set_stop = ((TextView) getView().findViewById(R.id.source_stop));
            another_stop = ((TextView) getView().findViewById(R.id.dest_stop));
        }
        else{
            set_stop = ((TextView) getView().findViewById(R.id.dest_stop));
            another_stop = ((TextView) getView().findViewById(R.id.source_stop));
        }
        Drawable mDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.rectangle_border_regular);
        set_stop.setBackground(mDrawable);
        mDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.rectangle_border_regular2);
        another_stop.setBackground(mDrawable);
    }

}