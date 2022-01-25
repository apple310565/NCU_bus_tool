package flag.com.ncubus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import flag.com.ncubus.MainActivity;
import flag.com.ncubus.ui.home.HomeFragment;

public class MainActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;
//    private static double lat = 24.968438302080717;
//    private static double lng = 121.1943910820179;
    private static double lat = 0;
    private static double lng = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        FloatingActionButton fab = findViewById(R.id.btn_prepage);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController nc = Navigation.findNavController(new MainActivity(), R.id.nav_host_fragment_activity_main);
                nc.navigate(R.id.map);
                // TODO: 目前只能導回奇怪的 map頁面，才出現 navbar，才能再點回去
                //  還沒想到可以怎麼解決qq
            }
        });

        // 接收從 MainActivity 傳過來的座標
        Bundle bundle = getIntent().getExtras();
        String msg= bundle.getString("msg");
        String[] splited = msg.split(" ");
        this.lat = Double.parseDouble(splited[0]);
        this.lng = Double.parseDouble(splited[1]);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(this.lat, this.lng);
        mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17));

    }


}