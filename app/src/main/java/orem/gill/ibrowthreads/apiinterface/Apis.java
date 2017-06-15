package orem.gill.ibrowthreads.apiinterface;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Dawinder on 16/02/2016.
 */
public interface Apis {

    @POST
    Call<ResponseBody> requestJson_simple(@Url String url);

    //Post simple data into server
    @FormUrlEncoded
    @POST
    Call<ResponseBody> requestJson_withValues(@Url String url_part, @FieldMap HashMap<String, String> map);

    //Post data with array
    @FormUrlEncoded
    @POST
    Call<ResponseBody> requestJson_withMultipleArrays(@Url String url_part, @FieldMap HashMap<String, String> data, @FieldMap HashMap<String, ArrayList<String>> order_arr);

    //Post data with images
    @Multipart
    @POST
    Call<ResponseBody> requestData_withImage(@Url String url, @PartMap Map<String, RequestBody> image);

    //Download File from server
    @GET
    @Streaming
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);

    //Post data into server with json object
    @FormUrlEncoded
    @POST
    Call<ResponseBody> requestJson_withJsonObject(@Url String url_part, @FieldMap HashMap<String, String> map, @FieldMap HashMap<String, JSONArray> mapJson);
}
