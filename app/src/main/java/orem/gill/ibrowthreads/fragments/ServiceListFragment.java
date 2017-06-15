package orem.gill.ibrowthreads.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import orem.gill.ibrowthreads.R;
import orem.gill.ibrowthreads.adapters.ServicesAdapter;
import orem.gill.ibrowthreads.pojoclasses.ServicesPOJO;
import orem.gill.ibrowthreads.utils.GeneralValues;
import orem.gill.ibrowthreads.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServiceListFragment extends Fragment {

    Context mContext;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView tvMessage;
    LinearLayoutManager layoutManager;

    public ServiceListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_service_list, container, false);

        mContext=getActivity();
        iniUI(v);

        if(Utils.isNetworkConnected(mContext)){
            apiGetServices();
        }else{
            errorDialog(getString(R.string.no_internet_connection));
        }

        return v;
    }

    public void iniUI(View v){
        recyclerView=(RecyclerView)v.findViewById(R.id.recyclerView);
        progressBar=(ProgressBar)v.findViewById(R.id.progressBar);
        tvMessage=(TextView)v.findViewById(R.id.tvMessage);

        layoutManager=new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void showDialog(String message){
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        tvMessage.setVisibility(View.VISIBLE);
        tvMessage.setText(message);
    }

    public void hideDialog(){
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        tvMessage.setVisibility(View.GONE);
    }

    public void errorDialog(String message){
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        tvMessage.setVisibility(View.VISIBLE);
        tvMessage.setText(message);
    }

    public void apiGetServices(){
        showDialog(getString(R.string.loading));
        Call<ResponseBody> call=Utils.requestApiDefault().requestJson_simple(GeneralValues.URL_GET_SERVICES);
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
                        JSONArray dataArr=new JSONArray(result.optString("data"));
                        ArrayList<ServicesPOJO> arr=new ArrayList<ServicesPOJO>();
                        for(int i=0;i<dataArr.length();i++){
                            JSONObject item=dataArr.getJSONObject(i);
                            arr.add(new ServicesPOJO(item.optString("id"),
                                    item.optString("name"),
                                    item.optString("description"),
                                    item.optString("image"),
                                    item.optString("services_url")));
                        }
                        if(isAdded()){
                            ServicesAdapter adapter=new ServicesAdapter(mContext,arr);
                            recyclerView.setAdapter(adapter);
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
