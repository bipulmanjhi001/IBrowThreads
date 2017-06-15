package orem.gill.ibrowthreads.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import okhttp3.ResponseBody;
import orem.gill.ibrowthreads.AppBaseActivity;
import orem.gill.ibrowthreads.R;
import orem.gill.ibrowthreads.utils.GeneralValues;
import orem.gill.ibrowthreads.utils.TinyDB;
import orem.gill.ibrowthreads.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppBaseActivity {

    Context mContext;
    ImageView ivBackground;
    TextView tvText;
    LinearLayout llSignIn;
    LoginButton login_button;
    CallbackManager callbackManager;
    Dialog dialog;
    TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        mContext = MainActivity.this;
        tinyDB=new TinyDB(mContext);
        dialog = Utils.getProgressDialog(mContext);

        llSignIn = (LinearLayout) findViewById(R.id.llSignIn);
        ivBackground = (ImageView) findViewById(R.id.ivBackground);
        tvText = (TextView) findViewById(R.id.tvText);
        tvText.setTypeface(tvText.getTypeface(), Typeface.BOLD);

        Glide.with(mContext).load(R.drawable.ic_background).into(ivBackground);

        FacebookSdk.sdkInitialize(mContext);
        login_button = (LoginButton) findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        login_button.setReadPermissions(Arrays.asList("email", "public_profile", "user_friends"));
        // If using in a fragment
        //login_button.setFragment(this);

        if(!tinyDB.getString(GeneralValues.SESSION_ID).equals("")){
            startActivity(new Intent(mContext,HomeActivity.class));
            MainActivity.this.finish();
        }

        // Callback registration
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Utils.showLog("Success : " + loginResult);
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (response.getError() == null) {
                            String femail = object.optString("email");
                            String fid = object.optString("id");
                            String fname = object.optString("name");
                            if (femail == null) {
                                femail = "";
                            }
                            Utils.showLog("Email : " + femail + " Id : " + fid + " Name : " + fname);
                            apiLogin(fname,fid);
                        } else {
                            Utils.showToast(mContext, getString(R.string.failed_to_login));
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Utils.showToast(mContext, getString(R.string.login_cancel));
            }

            @Override
            public void onError(FacebookException exception) {
                Utils.showToast(mContext, getString(R.string.failed_to_login));
            }
        });

        llSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(mContext,HomeActivity.class));
                login_button.performClick();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Logout facebook
        LoginManager.getInstance().logOut();
    }

    public void apiLogin(final String name, final String fid) {
        dialog.show();
        HashMap<String,String> map=new HashMap<>();
        map.put("username",""+name);
        map.put("userid",""+fid);

        Call<ResponseBody> call = Utils.requestApiDefault().requestJson_withValues(GeneralValues.URL_LOGIN,map);
        Utils.showLog("Url : " + call.request().url()+" Map : "+map);
        //Utils.showToast(mContext,"Map : "+map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialog.dismiss();
                try {
                    String jsonResponse = response.body().string();
                    Utils.showLog("Res = " + jsonResponse);

                    //Utils.showToast(mContext,"Res :::: "+jsonResponse);

                    JSONObject result = new JSONObject(jsonResponse);
                    String res = result.optString(getString(R.string.j_response));
                    String message = result.optString(getString(R.string.j_message));
                    if (res != null && res.equals("1")) {
                        Utils.showLog("Id : "+result.optString("data"));
                        tinyDB.putString(GeneralValues.SESSION_ID,""+result.optString("data"));
                        tinyDB.putString(GeneralValues.SESSION_NAME,name);
                        tinyDB.putString(GeneralValues.SESSION_SID,fid);

                        startActivity(new Intent(mContext,HomeActivity.class));
                        MainActivity.this.finish();
                    } else {
                        dialog.dismiss();
                        Utils.showToast(mContext, message);
                    }
                } catch (Exception e) {
                    dialog.dismiss();
                    Utils.showToast(mContext, getString(R.string.server_error));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
                Utils.showToast(mContext, getString(R.string.server_not_responding));
            }
        });
    }
}
