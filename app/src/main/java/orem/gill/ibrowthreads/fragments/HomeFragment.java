package orem.gill.ibrowthreads.fragments;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import orem.gill.ibrowthreads.R;
import orem.gill.ibrowthreads.activities.HomeActivity;
import orem.gill.ibrowthreads.adapters.ShopsAdapter;
import orem.gill.ibrowthreads.pojoclasses.ShopsPOJO;
import orem.gill.ibrowthreads.utils.GeneralValues;
import orem.gill.ibrowthreads.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    Context mContext;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView tvMessage;
    LinearLayoutManager layoutManager;
    Dialog dialog;

    private int PERMISSION_REQUEST_CODE = 23;
    private GoogleApiClient mGoogleApiClient;
    private int LOCATION_SETTINGS_REQUEST_CODE = 0x1;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    boolean isGetLocation=false;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_home, container, false);

        mContext=getActivity();
        dialog=Utils.getProgressDialog(mContext);
        iniUI(v);

        ((HomeActivity)mContext).fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isGetLocation=false;
                googleApiBasicVariablesInit();
            }
        });

        if(Utils.isNetworkConnected(mContext)){
            apiGetShops();
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

    public void apiGetShops(){
        showDialog(getString(R.string.loading));
        Call<ResponseBody> call=Utils.requestApiDefault().requestJson_simple(GeneralValues.URL_GET_SHOPS);
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
                        ArrayList<ShopsPOJO> arr=new ArrayList<ShopsPOJO>();
                        for(int i=0;i<dataArr.length();i++){
                            JSONObject item=dataArr.getJSONObject(i);
                            arr.add(new ShopsPOJO(item.optString("id"),
                                    item.optString("name"),
                                    item.optString("email"),
                                    item.optString("description"),
                                    item.optString("address"),
                                    item.optString("contact"),
                                    item.optString("latitude"),
                                    item.optString("longitude"),
                                    item.optString("image"),
                                    item.optString("sunday_time"),
                                    item.optString("monday_time"),
                                    item.optString("tuesday_time"),
                                    item.optString("wednesday_time"),
                                    item.optString("thrusday_time"),
                                    item.optString("friday_time"),
                                    item.optString("saturday_time")));
                        }
                        if(isAdded()){
                            ShopsAdapter adapter=new ShopsAdapter(mContext,arr);
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

    public void apiGetShopsByLocation(String lat,String lng){
        showDialog(getString(R.string.loading));

        HashMap<String,String> map=new HashMap<>();
        map.put("latitude",""+lat);
        map.put("longitude",""+lng);

        Call<ResponseBody> call=Utils.requestApiDefault().requestJson_withValues(GeneralValues.URL_NEAR_SHOPS,map);
        Utils.showLog("Url : "+call.request().url()+ "Map : "+map);
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
                        ArrayList<ShopsPOJO> arr=new ArrayList<ShopsPOJO>();
                        for(int i=0;i<dataArr.length();i++){
                            JSONObject item=dataArr.getJSONObject(i);
                            arr.add(new ShopsPOJO(item.optString("id"),
                                    item.optString("name"),
                                    item.optString("email"),
                                    item.optString("description"),
                                    item.optString("address"),
                                    item.optString("contact"),
                                    item.optString("latitude"),
                                    item.optString("longitude"),
                                    item.optString("image"),
                                    item.optString("sunday_time"),
                                    item.optString("monday_time"),
                                    item.optString("tuesday_time"),
                                    item.optString("wednesday_time"),
                                    item.optString("thrusday_time"),
                                    item.optString("friday_time"),
                                    item.optString("saturday_time")));
                        }
                        if(isAdded()){
                            ShopsAdapter adapter=new ShopsAdapter(mContext,arr);
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

    @Override
    public void onStop() {
        super.onStop();
        stopLocationUpdates();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    private void stopLocationUpdates() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) this).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
                mGoogleApiClient.disconnect();
            }
        }
    }

    private void googleApiBasicVariablesInit() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                checkLocationSettings();
            }
        } else {
            checkLocationSettings();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationSettings();
            } else {
                Utils.showToast(mContext,getString(R.string.location_permission_required));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_SETTINGS_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                checkLocationSettings();
            } else {
                Utils.showToast(mContext, getString(R.string.gps_turn_off_error));
            }
        }
    }

    private void checkLocationSettings() {
        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        final PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        if(!isGetLocation){
                            dialog.show();
                            startLocationUpdates();
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            startIntentSenderForResult(status.getResolution().getIntentSender(), LOCATION_SETTINGS_REQUEST_CODE, null, 0, 0, 0, null);
                            //status.startResolutionForResult(getActivity(), LOCATION_SETTINGS_REQUEST_CODE);
                        } catch (Exception e) {
                            Log.e("location dialog error", e.getStackTrace().toString());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Utils.showLog("Setting value change");
                        //startLocationUpdates();
                        Utils.showToast(mContext, getString(R.string.gps_turn_off_error));
                        break;
                }
            }
        });
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        getLocation(mLastLocation);
    }

    @SuppressWarnings("MissingPermission")
    protected void startLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationRequest,
                    this
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    Utils.showLog("Result : "+status);
                }
            });
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        getLocation(location);
    }

    @SuppressWarnings("MissingPermission")
    private void getLocation(Location location) {
        if (location != null) {
            isGetLocation=true;
            dialog.dismiss();
            String sLat = String.valueOf(location.getLatitude());
            String sLong = String.valueOf(location.getLongitude());

            Utils.showLog("Lat : " + sLat + " Long : " + sLong);
            stopLocationUpdates();

            apiGetShopsByLocation(sLat,sLong);
        }
    }
}
