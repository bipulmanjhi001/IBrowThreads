package orem.gill.ibrowthreads.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import orem.gill.ibrowthreads.AppBaseActivity;
import orem.gill.ibrowthreads.R;
import orem.gill.ibrowthreads.adapters.TabsPagerAdapter;
import orem.gill.ibrowthreads.fragments.AboutUsFragment;
import orem.gill.ibrowthreads.fragments.HomeFragment;
import orem.gill.ibrowthreads.fragments.NotificationsFragment;
import orem.gill.ibrowthreads.fragments.OffersFragment;
import orem.gill.ibrowthreads.fragments.ServiceListFragment;
import orem.gill.ibrowthreads.utils.GeneralValues;
import orem.gill.ibrowthreads.utils.TinyDB;
import orem.gill.ibrowthreads.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppBaseActivity implements View.OnClickListener {

    Context mContext;
    TinyDB tinyDB;
    Dialog dialog;
    ViewPager viewPager;
    TabLayout tabLayout;
    private ArrayList<TabsPagerAdapter.TabsPagerPojo> mPagerList;
    Toolbar toolbar;
    RelativeLayout rlOther;
    TextView tvTitle, tvLoyalityCard;
    LinearLayout llHome;
    ImageView ivFB, ivTwitter, ivInsta, ivWeb;
    public FloatingActionButton fab;

    boolean doubleBackToExitPressedOnce=false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Utils.showToast(mContext,getString(R.string.back_again_to_exit));
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = HomeActivity.this;
        tinyDB = new TinyDB(mContext);
        dialog = Utils.getProgressDialog(mContext);

        Utils.showToast(mContext, getString(R.string.welcome) + " " + tinyDB.getString(GeneralValues.SESSION_NAME).toUpperCase());

        iniUI();
        setListener();
        apiRegisterDevice();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    rlOther.setVisibility(View.GONE);
                    llHome.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);
                } else if (position == 1) {
                    llHome.setVisibility(View.GONE);
                    rlOther.setVisibility(View.VISIBLE);
                    tvTitle.setText(getString(R.string.services));
                    fab.setVisibility(View.GONE);
                } else if (position == 2) {
                    llHome.setVisibility(View.GONE);
                    rlOther.setVisibility(View.VISIBLE);
                    tvTitle.setText(getString(R.string.offers_title));
                    fab.setVisibility(View.GONE);
                } else if (position == 3) {
                    llHome.setVisibility(View.GONE);
                    rlOther.setVisibility(View.VISIBLE);
                    tvTitle.setText(getString(R.string.notifications));
                    fab.setVisibility(View.GONE);
                } else if (position == 4) {
                    llHome.setVisibility(View.GONE);
                    rlOther.setVisibility(View.VISIBLE);
                    tvTitle.setText(getString(R.string.about_us));
                    fab.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void iniUI() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        setupViewPager();
        //changeTabsFont();

        fab = (FloatingActionButton) findViewById(R.id.fab);

        ivFB = (ImageView) toolbar.findViewById(R.id.ivFB);
        ivTwitter = (ImageView) toolbar.findViewById(R.id.ivTwitter);
        ivInsta = (ImageView) toolbar.findViewById(R.id.ivInsta);
        ivWeb = (ImageView) toolbar.findViewById(R.id.ivWeb);
        rlOther = (RelativeLayout) toolbar.findViewById(R.id.rlOther);
        llHome = (LinearLayout) toolbar.findViewById(R.id.llHome);
        tvLoyalityCard = (TextView) toolbar.findViewById(R.id.tvLoyalityCard);
        tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);

        rlOther.setVisibility(View.GONE);
        llHome.setVisibility(View.VISIBLE);
    }

    public void setListener() {
        ivFB.setOnClickListener(this);
        ivTwitter.setOnClickListener(this);
        ivInsta.setOnClickListener(this);
        ivWeb.setOnClickListener(this);
        tvLoyalityCard.setOnClickListener(this);
    }

    private void setupViewPager() {
        mPagerList = new ArrayList<>();
        TabsPagerAdapter pagerAdapter = new TabsPagerAdapter(getSupportFragmentManager(), mContext, mPagerList);
        mPagerList.add(new TabsPagerAdapter.TabsPagerPojo(new HomeFragment(), getString(R.string.home)));
        mPagerList.add(new TabsPagerAdapter.TabsPagerPojo(new ServiceListFragment(), getString(R.string.services)));
        mPagerList.add(new TabsPagerAdapter.TabsPagerPojo(new OffersFragment(), getString(R.string.offers)));
        mPagerList.add(new TabsPagerAdapter.TabsPagerPojo(new NotificationsFragment(), getString(R.string.notifications)));
        mPagerList.add(new TabsPagerAdapter.TabsPagerPojo(new AboutUsFragment(), getString(R.string.about_us)));
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(pagerAdapter);

        View view1 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_tab_layout, null);
        ImageView icon1 = (ImageView) view1.findViewById(R.id.icon);
        icon1.setImageResource(R.drawable.ic_action_action_home);
        TextView title1 = (TextView) view1.findViewById(R.id.title);
        title1.setText(getString(R.string.home));

        View view2 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_tab_layout, null);
        ImageView icon2 = (ImageView) view2.findViewById(R.id.icon);
        icon2.setImageResource(R.drawable.ic_action_maps_local_laundry_service);
        TextView title2 = (TextView) view2.findViewById(R.id.title);
        title2.setText(getString(R.string.services));

        View view3 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_tab_layout, null);
        ImageView icon3 = (ImageView) view3.findViewById(R.id.icon);
        icon3.setImageResource(R.drawable.ic_action_maps_local_offer);
        TextView title3 = (TextView) view3.findViewById(R.id.title);
        title3.setText(getString(R.string.offers));

        View view4 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_tab_layout, null);
        ImageView icon4 = (ImageView) view4.findViewById(R.id.icon);
        icon4.setImageResource(R.drawable.ic_action_social_notifications);
        TextView title4 = (TextView) view4.findViewById(R.id.title);
        title4.setText(getString(R.string.notifications));

        View view5 = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_tab_layout, null);
        ImageView icon5 = (ImageView) view5.findViewById(R.id.icon);
        icon5.setImageResource(R.drawable.ic_action_action_info_outline);
        TextView title5 = (TextView) view5.findViewById(R.id.title);
        title5.setText(getString(R.string.about_us));

        tabLayout.getTabAt(0).setCustomView(view1);
        tabLayout.getTabAt(1).setCustomView(view2);
        tabLayout.getTabAt(2).setCustomView(view3);
        tabLayout.getTabAt(3).setCustomView(view4);
        tabLayout.getTabAt(4).setCustomView(view5);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivFB:
                Utils.intentToBrowser(mContext, getString(R.string.fb_link));
                break;
            case R.id.ivTwitter:
                Utils.intentToBrowser(mContext, getString(R.string.twitter_link));
                break;
            case R.id.ivInsta:
                Utils.intentToBrowser(mContext, getString(R.string.insta_link));
                break;
            case R.id.ivWeb:
                Utils.intentToBrowser(mContext, getString(R.string.web_link));
                break;
            case R.id.tvLoyalityCard:
                startActivity(new Intent(mContext,LoyalityCardActivity.class));
                break;
        }
    }

    public void apiRegisterDevice() {
        HashMap<String, String> map = new HashMap<>();
        map.put("userid",tinyDB.getString(GeneralValues.SESSION_ID));
        map.put("type", "0");
        map.put("token", ""+ FirebaseInstanceId.getInstance().getToken());

        Call<ResponseBody> call = Utils.requestApiDefault().requestJson_withValues(GeneralValues.URL_REG_TOKEN, map);
        Utils.showLog("Url : " + call.request().url()+" Map : "+map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String jsonResponse = response.body().string();
                    Utils.showLog("Res = " + jsonResponse);

                    JSONObject result = new JSONObject(jsonResponse);
                    String res = result.optString(getString(R.string.j_response));
                    String message = result.optString(getString(R.string.j_message));
                    if (res != null && res.equals("1")) {
                        Utils.showLog("Token updated success");
                    } else {
                        Utils.showLog(message);
                    }
                } catch (Exception e) {
                    Utils.showLog("Exception -->" + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utils.showLog("Error : " + t.getMessage());
            }
        });
    }
}
