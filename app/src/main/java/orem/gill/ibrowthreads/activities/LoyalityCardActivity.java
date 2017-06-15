package orem.gill.ibrowthreads.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import orem.gill.ibrowthreads.AppBaseActivity;
import orem.gill.ibrowthreads.R;
import orem.gill.ibrowthreads.adapters.CardAdapter;
import orem.gill.ibrowthreads.pojoclasses.CardPOJO;
import orem.gill.ibrowthreads.utils.GeneralValues;
import orem.gill.ibrowthreads.utils.TinyDB;
import orem.gill.ibrowthreads.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoyalityCardActivity extends AppBaseActivity {

    Context mContext;
    TinyDB tinyDB;
    Dialog dialog;
    ProgressBar progressBar;
    TextView tvMessage,tvTag;
    RecyclerView recyclerView;
    GridLayoutManager layoutManager;
    int stamps=0,off=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loyality_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = LoyalityCardActivity.this;
        tinyDB=new TinyDB(mContext);
        dialog=Utils.getProgressDialog(mContext);
        iniUI();

        if (Utils.isNetworkConnected(mContext)) {
            apiGetCardInfo();
        } else {
            errorDialog(getString(R.string.no_internet_connection));
        }
    }

    public void iniUI() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        tvTag = (TextView) findViewById(R.id.tvTag);

        layoutManager = new GridLayoutManager(mContext,6);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void showDialog(String message) {
        dialog.show();
        recyclerView.setVisibility(View.GONE);
        //progressBar.setVisibility(View.VISIBLE);
        tvMessage.setVisibility(View.GONE);
        tvMessage.setText(message);
        tvTag.setVisibility(View.GONE);
    }

    public void hideDialog() {
        dialog.hide();
        recyclerView.setVisibility(View.VISIBLE);
        //progressBar.setVisibility(View.GONE);
        tvMessage.setVisibility(View.GONE);
        tvTag.setVisibility(View.VISIBLE);
    }

    public void errorDialog(String message) {
        dialog.hide();
        recyclerView.setVisibility(View.GONE);
        //progressBar.setVisibility(View.GONE);
        tvMessage.setVisibility(View.VISIBLE);
        tvMessage.setText(message);
        tvTag.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                LoyalityCardActivity.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void apiGetCardInfo() {
        showDialog(getString(R.string.loading));

        HashMap<String,String> map=new HashMap<>();
        map.put("id",""+tinyDB.getString(GeneralValues.SESSION_ID));

        Call<ResponseBody> call = Utils.requestApiDefault().requestJson_withValues(GeneralValues.URL_GET_LOYALITY_CARD,map);
        Utils.showLog("Url : " + call.request().url()+" Map : "+map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hideDialog();
                try {
                    String jsonResponse = response.body().string();
                    Utils.showLog("Res = " + jsonResponse);

                    JSONObject result = new JSONObject(jsonResponse);
                    String res = result.optString(getString(R.string.j_response));
                    String message = result.optString(getString(R.string.j_message));
                    if (res != null && res.equals("1")) {
                        JSONObject data=new JSONObject(result.optString("data"));
                        if(data.optString("discount_amount").equals("")){
                            off=0;
                        }else{
                            off=Integer.parseInt(data.optString("discount_amount"));
                        }
                        if(data.optString("no_of_stamps").equals("")){
                            stamps=0;
                        }else{
                            stamps=Integer.parseInt(data.optString("no_of_stamps"));
                        }
                        setCardStamps();
                    } else {
                        errorDialog(message);
                    }
                } catch (Exception e) {
                    errorDialog(getString(R.string.server_error));
                    Utils.showLog("Exception -->" + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                errorDialog(getString(R.string.server_not_responding));
                Utils.showLog("Error : " + t.getMessage());
            }
        });
    }

    public void apiCheckCode(String code) {
        dialog.show();

        HashMap<String,String> map=new HashMap<>();
        map.put("userid",""+tinyDB.getString(GeneralValues.SESSION_ID));
        map.put("security_code",Utils.convertBase64(code));
        if((stamps+1)>23){
            map.put("no_of_stamps",""+0);
        }else{
            map.put("no_of_stamps",""+(stamps+1));
        }

        Call<ResponseBody> call = Utils.requestApiDefault().requestJson_withValues(GeneralValues.URL_CHECK_SECURITY_CODE,map);
        Utils.showLog("Url : " + call.request().url()+" Map : "+map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog.dismiss();
                try {
                    String jsonResponse = response.body().string();
                    Utils.showLog("Res = " + jsonResponse);

                    JSONObject result = new JSONObject(jsonResponse);
                    String res = result.optString(getString(R.string.j_response));
                    String message = result.optString(getString(R.string.j_message));
                    if (res != null && res.equals("1")) {
                        if((stamps+1)>23){
                            stamps=0;
                        }else{
                            stamps=stamps+1;
                        }
                        setCardStamps();
                    } else {
                        dialog.dismiss();
                        Utils.showToast(mContext,message);
                    }
                } catch (Exception e) {
                    dialog.dismiss();
                    Utils.showToast(mContext,getString(R.string.server_error));
                    Utils.showLog("Exception -->" + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
                Utils.showToast(mContext,getString(R.string.server_not_responding));
                Utils.showLog("Error : " + t.getMessage());
            }
        });
    }

    public void setCardStamps(){
        ArrayList<CardPOJO> arr=new ArrayList<>();
        arr.add(new CardPOJO("1","1"));
        arr.add(new CardPOJO("2","2"));
        arr.add(new CardPOJO("3","3"));
        arr.add(new CardPOJO("4","4"));
        arr.add(new CardPOJO("5","5"));
        arr.add(new CardPOJO("6","$"+off+"\nOFF"));
        arr.add(new CardPOJO("7","7"));
        arr.add(new CardPOJO("8","8"));
        arr.add(new CardPOJO("9","9"));
        arr.add(new CardPOJO("10","10"));
        arr.add(new CardPOJO("11","11"));
        arr.add(new CardPOJO("12","$"+(off*2)+"\nOFF"));
        arr.add(new CardPOJO("13","13"));
        arr.add(new CardPOJO("14","14"));
        arr.add(new CardPOJO("15","15"));
        arr.add(new CardPOJO("16","16"));
        arr.add(new CardPOJO("17","17"));
        arr.add(new CardPOJO("18","$"+(off*3)+"\nOFF"));
        arr.add(new CardPOJO("19","19"));
        arr.add(new CardPOJO("20","20"));
        arr.add(new CardPOJO("21","21"));
        arr.add(new CardPOJO("22","22"));
        arr.add(new CardPOJO("23","23"));
        arr.add(new CardPOJO("24","$"+(off*4)+"\nOFF"));

        CardAdapter adapter=new CardAdapter(mContext,arr,stamps);
        recyclerView.setAdapter(adapter);
    }

    public void showSecurityCodeDialog(){
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_security_code);
        dialog.setCancelable(true);

        final EditText etSecurityCode=(EditText)dialog.findViewById(R.id.etSecurityCode);
        Button btVerify=(Button)dialog.findViewById(R.id.btVerify);

        btVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeyboard(mContext,dialog.getCurrentFocus());
                if(etSecurityCode.getText().toString().equals("")){
                    etSecurityCode.setError(getString(R.string.enter_security_code));
                }else{
                    if(Utils.isNetworkConnected(mContext)){
                        dialog.dismiss();
                        apiCheckCode(etSecurityCode.getText().toString());
                    }else{
                        Utils.showToast(mContext,getString(R.string.no_internet_connection));
                    }
                }
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        dialog.show();
    }
}
