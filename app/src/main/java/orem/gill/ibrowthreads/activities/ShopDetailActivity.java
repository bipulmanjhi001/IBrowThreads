package orem.gill.ibrowthreads.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import orem.gill.ibrowthreads.AppBaseActivity;
import orem.gill.ibrowthreads.R;
import orem.gill.ibrowthreads.pojoclasses.ShopsPOJO;
import orem.gill.ibrowthreads.utils.GeneralValues;
import orem.gill.ibrowthreads.utils.TinyDB;
import orem.gill.ibrowthreads.utils.Utils;

public class ShopDetailActivity extends AppBaseActivity implements OnMapReadyCallback {

    Context mContext;
    TinyDB tinyDB;
    ImageView ivImage;
    TextView tvHeader,tvDes,tvEmail,tvContact,tvAddress,tvSunday,tvMonday,tvTuesday,tvWednesday,tvThursday,tvFriday,tvSaturday;
    ShopsPOJO item;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext=ShopDetailActivity.this;
        tinyDB=new TinyDB(mContext);
        iniUI();
    }

    public void iniUI(){
        ivImage=(ImageView)findViewById(R.id.ivImage);
        tvHeader=(TextView)findViewById(R.id.tvHeader);
        tvDes=(TextView)findViewById(R.id.tvDes);
        tvEmail=(TextView)findViewById(R.id.tvEmail);
        tvContact=(TextView)findViewById(R.id.tvContact);
        tvAddress=(TextView)findViewById(R.id.tvAddress);
        tvSunday=(TextView)findViewById(R.id.tvSunday);
        tvMonday=(TextView)findViewById(R.id.tvMonday);
        tvTuesday=(TextView)findViewById(R.id.tvTuesday);
        tvWednesday=(TextView)findViewById(R.id.tvWednesday);
        tvThursday=(TextView)findViewById(R.id.tvThursday);
        tvFriday=(TextView)findViewById(R.id.tvFriday);
        tvSaturday=(TextView)findViewById(R.id.tvSaturday);

        item=new Gson().fromJson(tinyDB.getString(GeneralValues.SELECTED_SHOP),ShopsPOJO.class);

        Utils.loadImage(mContext,item.getImage(),ivImage,R.drawable.ic_placeholder,R.drawable.ic_placeholder);
        tvHeader.setText(item.getName());
        tvDes.setText(item.getDes());
        tvEmail.setText(getString(R.string.email)+" "+item.getEmail());
        tvContact.setText(getString(R.string.contact)+" "+item.getContact());
        tvAddress.setText(getString(R.string.address)+" "+item.getAddress());
        tvSunday.setText(getString(R.string.sunday1)+" "+item.getSunTime());
        tvMonday.setText(getString(R.string.monday)+" "+item.getMonTime());
        tvTuesday.setText(getString(R.string.tuesday)+" "+item.getTuesTime());
        tvWednesday.setText(getString(R.string.wednesday)+" "+item.getWedTime());
        tvThursday.setText(getString(R.string.thursday)+" "+item.getThurTime());
        tvFriday.setText(getString(R.string.friday)+" "+item.getFriTime());
        tvSaturday.setText(getString(R.string.saturday)+" "+item.getSatTime());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shop_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mitem) {
        switch (mitem.getItemId()) {
            case android.R.id.home:
                ShopDetailActivity.this.finish();
                return true;
            case R.id.action_call:
                if(item.getContact().equals("")){
                    Utils.showToast(mContext,getString(R.string.can_not_able_to_call));
                }else{
                    Utils.intentToPhone(mContext,item.getContact());
                }
                return true;
        }
        return super.onOptionsItemSelected(mitem);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        if(item.getLat()!=null&&!item.getLat().equals("")&&item.getLng()!=null&&!item.getLng().equals("")){
            double lat=Double.parseDouble(item.getLat());
            double lng=Double.parseDouble(item.getLng());
            LatLng sydney = new LatLng(lat,lng);
            mMap.addMarker(new MarkerOptions().position(sydney).title(""+item.getName()).snippet(""+item.getAddress()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),14));
        }
    }
}
