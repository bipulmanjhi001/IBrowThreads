package orem.gill.ibrowthreads.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import orem.gill.ibrowthreads.R;
import orem.gill.ibrowthreads.utils.GeneralValues;
import orem.gill.ibrowthreads.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUsFragment extends Fragment {

    Context mContext;
    TextView tvAboutUs;
    ProgressBar progressBar;
    TextView tvMessage;

    public AboutUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_about_us, container, false);

        mContext=getActivity();
        iniUI(v);

        if(Utils.isNetworkConnected(mContext)){
            apiGetAboutUsData();
        }else{
            errorDialog(getString(R.string.no_internet_connection));
        }
        return v;
    }

    public void iniUI(View v){
        tvAboutUs=(TextView)v.findViewById(R.id.tvAboutUs);
        tvAboutUs.setMovementMethod(new ScrollingMovementMethod());

        progressBar=(ProgressBar)v.findViewById(R.id.progressBar);
        tvMessage=(TextView)v.findViewById(R.id.tvMessage);
    }

    public void showDialog(String message){
        tvAboutUs.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        tvMessage.setVisibility(View.VISIBLE);
        tvMessage.setText(message);
    }

    public void hideDialog(){
        tvAboutUs.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        tvMessage.setVisibility(View.GONE);
    }

    public void errorDialog(String message){
        tvAboutUs.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        tvMessage.setVisibility(View.VISIBLE);
        tvMessage.setText(message);
    }

    public void apiGetAboutUsData(){
        showDialog(getString(R.string.loading));
        Call<ResponseBody> call= Utils.requestApiDefault().requestJson_simple(GeneralValues.URL_GET_ABOUTUS);
        Utils.showLog("Url : "+call.request().url());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hideDialog();
                try {
                    String jsonResponse = response.body().string();
                    Utils.showLog("Res = "+jsonResponse);

                    JSONObject result = new JSONObject(jsonResponse);
                    String res = result.optString(getString(R.string.j_response));
                    String message = result.optString(getString(R.string.j_message));
                    if (res != null && res.equals("1")) {
                        if(isAdded()){
                            JSONObject data=new JSONObject(result.optString("data"));
                            tvAboutUs.setText(data.optString("description"));
                        }
                    }else{
                        if(isAdded()){
                            errorDialog(message);
                        }
                    }
                } catch (Exception e) {
                    if(isAdded()){
                        errorDialog(getString(R.string.server_error));
                        Utils.showLog("Exception -->"+ e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if(isAdded()){
                    errorDialog(getString(R.string.server_not_responding));
                    Utils.showLog("Error : "+t.getMessage());
                }
            }
        });
    }
}
