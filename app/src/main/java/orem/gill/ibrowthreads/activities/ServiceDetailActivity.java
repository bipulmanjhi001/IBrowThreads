package orem.gill.ibrowthreads.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import orem.gill.ibrowthreads.AppBaseActivity;
import orem.gill.ibrowthreads.R;
import orem.gill.ibrowthreads.pojoclasses.ServicesPOJO;
import orem.gill.ibrowthreads.utils.GeneralValues;
import orem.gill.ibrowthreads.utils.TinyDB;
import orem.gill.ibrowthreads.utils.Utils;

public class ServiceDetailActivity extends AppBaseActivity{

    Context mContext;
    TinyDB tinyDB;
    ImageView ivImage;
    TextView tvHeader,tvDes,tvWebUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext=ServiceDetailActivity.this;
        tinyDB=new TinyDB(mContext);
        iniUI();
    }

    public void iniUI(){
        ivImage=(ImageView)findViewById(R.id.ivImage);
        tvHeader=(TextView)findViewById(R.id.tvHeader);
        tvDes=(TextView)findViewById(R.id.tvDes);
        tvWebUrl=(TextView)findViewById(R.id.tvWebUrl);

        ServicesPOJO item=new Gson().fromJson(tinyDB.getString(GeneralValues.SELECTED_SERVICE),ServicesPOJO.class);

        Utils.loadImage(mContext,item.getImage(),ivImage,R.drawable.ic_placeholder,R.drawable.ic_placeholder);
        tvHeader.setText(item.getName());
        tvDes.setText(item.getDes());
        if(item.getWebUrl().equals("")){
            tvWebUrl.setVisibility(View.GONE);
        }else{
            tvWebUrl.setVisibility(View.VISIBLE);
            tvWebUrl.setText(item.getWebUrl());
            Utils.underlineText(item.getWebUrl(),tvWebUrl);
        }

        tvWebUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.intentToBrowser(mContext,tvWebUrl.getText().toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ServiceDetailActivity.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
