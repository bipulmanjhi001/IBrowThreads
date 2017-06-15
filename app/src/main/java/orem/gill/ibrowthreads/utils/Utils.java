package orem.gill.ibrowthreads.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import orem.gill.ibrowthreads.R;
import orem.gill.ibrowthreads.apiinterface.Apis;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by admin on 2/5/2016.
 */
public class Utils {
    /**
     * Default api to get data
     *
     * @return Returns Apis
     */
    public static Apis requestApiDefault() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(GeneralValues.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        Apis apis = retrofit.create(Apis.class);
        return apis;
    }

    /**
     * Api with very less response time
     *
     * @return Returns Apis
     */
    public static Apis requestApiLessTime() {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(GeneralValues.BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create()).build();
        Apis apis = retrofit.create(Apis.class);
        return apis;
    }

    public static Apis requestApiLongTime() {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(GeneralValues.BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create()).build();
        Apis apis = retrofit.create(Apis.class);
        return apis;
    }

    /**
     * Check internet availabilty
     *
     * @param mContext Context of activity or fragment
     * @return Returns true is internet connected and false if no internet connected
     */
    public static boolean isNetworkConnected(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    /**
     * Show toast message
     *
     * @param mContext Context of activity or fragment
     * @param message Message that show into the Toast
     */
    public static void showToast(Context mContext, String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Show snackbar message
     *
     * @param mContext Context of activity or fragment
     * @param view View of activity or fragment(getCurrrentFocus())
     * @param message Message that show into the Toast
     */
    public static void showSnackbar(Context mContext, View view, final String message) {
        if (view != null) {
            final Snackbar mSnackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
            View mView = mSnackbar.getView();
            TextView mTextView = (TextView) mView.findViewById(android.support.design.R.id.snackbar_text);
            mTextView.setMaxLines(5);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            else
                mTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            mSnackbar.setActionTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            mSnackbar.show();
        }
    }

    /**
     * Show alert dialog
     *
     * @param mContext Context of activity o fragment
     * @param message Message that shows on Dialog
     * @param title Title for dialog
     * @param positiveText Set positive text
     * @param positiveListener Set functionality on positive button click
     * @param negativeListener Set functionality on negative button click
     * @param negativeText Negative button text
     * @param neutralText Neturat button text
     * @param neutralListener Set Netural button listener
     * @param isCancelable true -> Cancelable True ,false -> Cancelable False
     * @return dialog
     */
    public static AlertDialog.Builder showDialog(Context mContext, String message, String title, String positiveText, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener, String negativeText, String neutralText, DialogInterface.OnClickListener neutralListener, boolean isCancelable) {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setNegativeButton(negativeText, negativeListener);
        alert.setPositiveButton(positiveText, positiveListener);
        alert.setNeutralButton(neutralText, neutralListener);
        alert.setCancelable(isCancelable);
        alert.show();
        return alert;
    }

    /**
     * Make dialog with progress bar
     *
     * @param mContext Context of the Activity or fragment
     * @return Dialog with progress bar
     */
    public static Dialog getProgressDialog(Context mContext) {
        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_progress);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());

        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        window.setAttributes(lp);
        return dialog;
    }

    /**
     * Check weather device is GPS is Enabled or not.
     *
     * @param mContext Context of the Activity or fragment.
     * @return Returns true if GPS Enabled and false when its not.
     */
    public static boolean isGpsEnabled(Context mContext) {
        return ((LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * get IMEI of device
     *
     * @param mContext Context of the Activity or Fragment.
     * @return Returns IMEI number of device(Unique Identification Number)
     */
    public static String getIMEI(Context mContext) {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String imei_no = telephonyManager.getDeviceId();
        return imei_no;
    }

    /**
     * Check weather device support PlayServices or not.
     *
     * @param mContext Context of the Activity od Fragment.
     * @return Returns true if device support PlayServices and false when its not.
     */
    /*public static boolean checkPlayServices(Context mContext) {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(mContext);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog((Activity) mContext, result, 9000).show();
            }
            return false;
        }else{
            return true;
        }
    }*/

    /**
     * Simple Share Intent
     *
     * @param mContext Context of the Activity or Fragment.
     * @param text Text that you want to share with intent
     */
    public static void shareContent(Context mContext, String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        mContext.startActivity(Intent.createChooser(sendIntent, "Go To : "));
    }

    /**
     * Simple Browser Intent
     *
     * @param mContext Context of the Activity or Fragment.
     * @param url Url that you want to open in Browser
     */
    public static void intentToBrowser(Context mContext, String url){
        try{
            Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            mContext.startActivity(Intent.createChooser(intent, "Go To : "));
        }catch(Exception e){

        }
    }

    /**
     * Intent to Mail App
     *
     * @param mContext Context of the Activity or Fragment.
     * @param mail Email Id that you want to intent into Mail App
     * @param subject Subject from email
     * @param message Message for email
     */
    public static void intentToMail(Context mContext, String mail, String subject, String message){
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mail});
        intent.putExtra(Intent.EXTRA_SUBJECT, ""+subject);
        intent.putExtra(Intent.EXTRA_TEXT, ""+message);
        mContext.startActivity(Intent.createChooser(intent, "Go To : "));
    }

    /**
     * Intent to Phone
     *
     * @param mContext Context of the Activity or Fragment.
     * @param number Number on which want to make a call
     */
    public static void intentToPhone(Context mContext, String number){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
        mContext.startActivity(Intent.createChooser(intent, "Go To : "));
    }

    /**
     * Intent to Google Map
     *
     * @param mContext Context of the Activity or Fragment.
     * @param mLat Latitude
     * @param mLong Longitude
     */
    public static void intentToMap(Context mContext, String mLat, String mLong){
        String label = mContext.getString(R.string.app_name);
        String uriBegin = "geo:" + mLat + "," + mLong;
        String query = mLat + "," + mLong+ "(" + label + ")";
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=10";
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        mContext.startActivity(Intent.createChooser(intent, "Go To : "));
    }

    /**
     * Go to playstore link of app
     *
     * @param mContext Context of the Activity or Fragment.
     */
    public static void sharePlayStoreLink(Context mContext){
        try{
            Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+mContext.getPackageName()));
            mContext.startActivity(Intent.createChooser(intent, "Go To : "));
        }catch(Exception e){

        }
    }

    /**
     * Get playstore link of app
     *
     * @param mContext Context of the Activity or Fragment.
     */
    public static String getPlayStoreLink(Context mContext){
        return "https://play.google.com/store/apps/details?id="+mContext.getPackageName();
    }

    /**
     * Check weather device is Tablet or not.
     *
     * @param mContext Context of the Activity.
     * @return Returns true if device is Tablet and false when its not.
     */
    public static boolean isTablet(Context mContext) {
        return (mContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * Get current date
     *
     * @return Returns current date
     */
    public static String getCurrentDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentdate = formatter.format(c.getTime());
        return currentdate;
    }

    /**
     * Get current time
     *
     * @return Returns current Time
     */
    public static String getCurrentTime(){
        //kk:mm:ss 24 Hour Format
        //hh:mm:ss a 12 Hour Format
        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("kk:mm:ss");
        String currentTime = formatter.format(c.getTime());
        return currentTime;
    }

    /**
     * Show Log
     *
     * @param message Message that want to show into Log
     */
    public static void showLog(String message){
        Log.e("Log Message",""+message);
    }

    /**
     * Goto any Fragment
     *
     * @param mContext Context of the Activity of Fragment.
     * @param fragment Fragment that want to open
     * @param container Container in which want to infulate Fragment
     */
    public static void goToFragment(Context mContext, Fragment fragment, int container, boolean addtoBackstack) {
        android.support.v4.app.FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
        transaction.replace(container, fragment);
        if (addtoBackstack)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Goto any Fragment
     *
     * @param mContext Context of the Activity of Fragment.
     * @param fragment Fragment that want to open
     * @param container Container in which want to infulate Fragment
     */
    public static void goToFragmentWithAnimation(Context mContext, Fragment fragment, int container, boolean addtoBackstack) {
        android.support.v4.app.FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.exit_anim, R.anim.enter_anim);
        transaction.replace(container, fragment);
        if (addtoBackstack)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Change status Bar color
     *
     * @param mContext Context of the Activity.
     * @param act Activity,in which want to change status bar color
     * @param color Color that you want
     */
    public static void changeStatusBarColor(Context mContext, Activity act, int color){
        if (Build.VERSION.SDK_INT > 20) {
            Window window = act.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(mContext, color));
        }
    }

    /**
     * Hide Soft Keyboard
     *
     * @param mContext Context of the Activity or Fragment.
     * @param view Current focus of View
     */
    public static void hideKeyboard(Context mContext, View view){
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Set Typeface into TextView
     *
     * @param mContext Context of the Activity or Fragment.
     * @param tv TextView to set Typeface
     */
    public static void typefaceFont(Context mContext, TextView tv){
        Typeface faceb= Typeface.createFromAsset(mContext.getAssets(), "bauhaus.ttf");
        tv.setTypeface(faceb);
    }

    /**
     * Convert Temperature from Kelvin to Celsius
     *
     * @param temp Temperature that want to convert
     * @return Returns Temperature in Celsius
     */
    public static String convertK2C(String temp){
        float result=0;
        result= Float.parseFloat(temp)- Float.parseFloat("273.15");
        return String.format("%.1f", result);
    }

    /**
     * Convert Temprature from Kelvin to Fahrenheit
     *
     * @param temp Temperature that want to convert
     * @return Returns Temperature in Fahrenheit
     */
    public static String convertK2F(String temp){
        float result=0;
        result= (Float.parseFloat(temp) - Float.parseFloat("273.15"))* Float.parseFloat("1.8000") + Float.parseFloat("32.00");
        return String.format("%.1f", result);
    }

    /**
     * Get day from Timestamp
     *
     * @param TimeInMilis TimeStamp
     * @return Returns day according to give Timestamp
     */
    public static String getDayFromTimeStamp(String TimeInMilis) {
        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(TimeInMilis)*1000);
        weekDay = dayFormat.format(calendar.getTime());
        return weekDay;
    }

    /**
     * Get Date from Timestamp
     *
     * @param TimeInMilis Timestamp
     * @return Returns Date according to give Timestamp
     */
    public static String getDateFromTimeStamp(String TimeInMilis) {
        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd-MM-yyyy");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(TimeInMilis)*1000);
        weekDay = dayFormat.format(calendar.getTime());
        return weekDay;
    }

    /**
     * Compare Time with Current Time
     *
     * @param datetime Date-Time that you want to compare with current datetime
     * (Format : yyyy-mm-dd hh:mm:ss, Example : 2016-06-07 21:00:00 Time will be in 24 Hour Format)
     * @return True if Time is smaller than current time otherwise return false
     */
    public static boolean compareTimewithCurrentTime(String datetime){
        Calendar c= Calendar.getInstance();
        Date currentTime;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date timeCompare=sdf.parse(datetime);
            currentTime=c.getTime();
            return timeCompare.compareTo(currentTime) >= 0;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("exp","in catch");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check is Current time is in between Start and End time or not
     *
     * @param starttime Start Date-Time
     * @param endtime End Date-Time
     * (Format : yyyy-mm-dd hh:mm:ss, Example : 2016-06-07 21:00:00 Time will be in 24 Hour Format)
     * @return True if Current Time is in between start and end time than current time otherwise return false
     */
    public static boolean compareTimeBetweenTwoTimes(String starttime, String endtime){
        Calendar c= Calendar.getInstance();
        Date currentTime;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            currentTime=c.getTime();
            Date startCompare=sdf.parse(starttime);
            Date endCompare=sdf.parse(endtime);
            if(startCompare.compareTo(currentTime) <= 0 && endCompare.compareTo(currentTime) >= 0){
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("exp","in catch");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * To get unique number
     *
     * @return Unique Integer
     */
    public static int getUniqueNumber(){
        Random random = new Random();
        return random.nextInt(9999 - 1000) + 1000;
    }

    /**
     * Convert simple Bitmap to Blur Bitmap
     *
     * @param context Avtivity or Fragment Context
     * @param smallBitmap Bitmap of image that you want to make blur
     * @param radius Blur radius(Radius must be in between 0 and 25 otherwise it gives exception)
     * @return Blur Bitmap
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blurRenderScript(Context context, Bitmap smallBitmap, int radius) {
        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bitmap bitmap = Bitmap.createBitmap(smallBitmap.getWidth(), smallBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);
        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;
    }

    private static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];
        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);
        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }

    /**
     * Get Real path while getting image from SD card or internal memory
     *
     * @param mContext Avtivity or Fragment Context
     * @param uri Uri of file
     * @return String path of file
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("NewApi")
    public static String getRealPathFromURI(Context mContext, Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(mContext, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(mContext, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type))

                {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]
                        {
                                split[1]
                        };

                return getDataColumn(mContext, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(mContext, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
        //  return cursor.getString(column_index);
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * Load image from url into imageview
     * @param mContext Context of Activity or Fragment
     * @param url Url that want to load into Imageview
     * @param imageView ImageView in which url loads
     * @param errorPlaceholder Drawable image that load in case of error while loading image
     * @param placeholder Drawable image while loading image from Url
     */
    public static void loadImage(Context mContext, String url, final ImageView imageView, int errorPlaceholder, int placeholder){
        Glide.with(mContext).load(url).error(errorPlaceholder).placeholder(placeholder).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                imageView.setImageDrawable(resource);
            }

            @Override
            public void onLoadStarted(Drawable placeholder) {
                imageView.setImageDrawable(placeholder);
                super.onLoadStarted(placeholder);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                imageView.setImageDrawable(errorDrawable);
                super.onLoadFailed(e, errorDrawable);
            }
        });
    }

    /**
     * To share any text on Twitter
     * @param mContext Context of Activity or Fragment
     * @param shareText Text that you want to share on twitter
     */
    public static void twitterShare(Context mContext, String shareText) {
        //String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s", urlEncode("Testing"),urlEncode("Image Url"));
        String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s", urlEncode(shareText));
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
        // Narrow down to official Twitter app, if available:
        List<ResolveInfo> matches = mContext.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                intent.setPackage(info.activityInfo.packageName);
            }
        }
        mContext.startActivity(Intent.createChooser(intent, "Go To : "));
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            Log.e("Twitter!", "UTF-8 should always be supported", e);
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }

    /**
     * To Bitmap of any drawable image
     * @param mContext Context of Activity or Fragment
     * @param drawableImage Drawable image
     * @return Return Bitmap of any image
     */
    public static Bitmap getBitmapFromDrawable(Context mContext, int drawableImage){
        Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), drawableImage);
        return icon;
    }

    /**
     * To share any text with image on Instagram
     * @param mContext Context of Activity or Fragment
     * @param shareText Text that you want to share on twitter
     * @param drawableImage Drawable image
     */
    public static void instagramShare(Context mContext, String shareText, int drawableImage) {
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage("com.instagram.android");
        if (intent != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage("com.instagram.android");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(MediaStore.Images.Media.insertImage(mContext.getContentResolver(),Utils.getBitmapFromDrawable(mContext,drawableImage) , mContext.getString(R.string.app_name), shareText)));
            shareIntent.setType("image/jpeg");
            mContext.startActivity(shareIntent);
        } else {
            // bring user to the market to download the app.
            // or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id="+"com.instagram.android"));
            mContext.startActivity(Intent.createChooser(intent, "Go To : "));
        }
    }

    /**
     * To make text view text with underline
     * @param text Text that want to set into TextView
     * @param textView TextView that want to underline
     */
    public static void underlineText(String text, TextView textView){
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);
    }

    /**
     * To rating bar colors
     * @param mContext Context of activity/fragment
     * @param ratingBar Rating Bar for which you want to change color
     * @param filledColor Filled star color
     * @param halfFilledColor Half Filled stars color
     * @param emptyStarColor Empty stars color
     */
    public static void changeRatingBarColor(Context mContext, RatingBar ratingBar, int filledColor, int halfFilledColor, int emptyStarColor){
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(mContext,
                filledColor), PorterDuff.Mode.SRC_ATOP); // for filled stars
        stars.getDrawable(1).setColorFilter(ContextCompat.getColor(mContext,
                halfFilledColor), PorterDuff.Mode.SRC_ATOP); // for half filled stars
        stars.getDrawable(0).setColorFilter(ContextCompat.getColor(mContext,
                emptyStarColor), PorterDuff.Mode.SRC_ATOP); // for empty stars
    }

    /**
     * To get time from long time duration
     * @param duration int duration
     * @return return time in hh:mm:ss
     */
    public static String getTimeInMinutes(int duration){
        String hour="",min="",sec="";
        if((TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)))<10){
            sec="0"+(TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        }else{
            sec=""+(TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        }
        if(TimeUnit.MILLISECONDS.toMinutes(duration)<10){
            min="0"+ TimeUnit.MILLISECONDS.toMinutes(duration);
        }else{
            min=""+ TimeUnit.MILLISECONDS.toMinutes(duration);
        }
        if(TimeUnit.MILLISECONDS.toHours(duration)<10){
            hour="0"+ TimeUnit.MILLISECONDS.toHours(duration);
        }else{
            hour=""+ TimeUnit.MILLISECONDS.toHours(duration);
        }
        if(hour.equalsIgnoreCase("00")){
            return min+":"+sec;
        }else{
            return hour+":"+min+":"+sec;
        }
    }

    public static String convertBase64(String text){
        byte[] data = new byte[0];
        try {
            data = text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(data, Base64.DEFAULT).replaceAll("\n", "");
    }
}
