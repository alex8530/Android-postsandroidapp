package com.example.postsandroidapp.model.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import androidx.annotation.Nullable; 
import androidx.recyclerview.widget.RecyclerView;

import com.example.postsandroidapp.model.api.APIUtils;
import com.example.postsandroidapp.model.api.PostsAPI;
import com.github.simonpercic.oklog3.OkLogInterceptor;
import com.orhanobut.hawk.BuildConfig;
import com.orhanobut.hawk.Hawk;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JavaUtils {
    public static String baseUrlFull;
    public static Retrofit retrofit;
    public static PostsAPI postsAPI;

    public static void CreateUrlAPI() {

        baseUrlFull = APIUtils.BASE_URL;


        if (BuildConfig.DEBUG) {
            //add only in debug mode
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            //okhttp logging
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);

            //oklog
            OkLogInterceptor okLogInterceptor = OkLogInterceptor.builder()
                    .withAllLogData()
                    .build();
            httpClient.addInterceptor(okLogInterceptor);

            retrofit = new Retrofit.Builder().baseUrl(baseUrlFull)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        } else {
            retrofit = new Retrofit.Builder().baseUrl(baseUrlFull)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }


        postsAPI = retrofit.create(PostsAPI.class);
    }



    public static String checkErrorRequest(  String message) {
        return
                 !getStringWithoutNull(message).isEmpty()
                && (message.toLowerCase().startsWith("Unable to resolve host".toLowerCase())
                || message.toLowerCase().startsWith("failed to connect to".toLowerCase())
                || message.toLowerCase().startsWith("timeout".toLowerCase()))
                ? "يرجى التأكد من إتصالك بالإنترنت والمحاولة لاحقا"
                : message;
    }

    public static String getStringWithoutNull(String string) {
         return string == null ? "" : ("" + string).trim();
    }


    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Nullable
    public static String getNetworkType(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return activeNetwork.getTypeName();
        }
        return null;
    }



    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        Bitmap image = null;
        try {
            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static String getMessagesErrorFromErrorValidation(String string) {
        if (string == null) {
            return "";
        }
        return string.trim().replace("[", "")
                .replace("]", "")
                .replace(", ", "\n")
                .replace(" ,", "\n")
                .replace("\"", "")
                .replace("'", "").trim();
    }

    public static void hideKeyboardAlways(  Activity activity ) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    public static float dpTopixel(Context c, float dp) {
        float density = c.getResources().getDisplayMetrics().density;
        float pixel = dp * density;
        return pixel;
    }
    public static float pixelTodp(Context c, float pixel) {
        float density = c.getResources().getDisplayMetrics().density;
        float dp = pixel / density;
        return dp;
    }


    public static void setupAnimationChangeDataOnRecyclerView(Context mContext
            , RecyclerView mRecyclerView
            , RecyclerView.Adapter mAdapter
            , int animResourcesId) {
        if (mContext == null
                || mRecyclerView == null
                || mAdapter == null) {
            return;
        }
        try {
            final LayoutAnimationController controller =
                    AnimationUtils.loadLayoutAnimation(mContext, animResourcesId);

            mRecyclerView.setLayoutAnimation(controller);
            mAdapter.notifyDataSetChanged();
            mRecyclerView.scheduleLayoutAnimation();
        } catch (Exception e) {

        }
    }



    public static boolean isInternetAvailable(Context mContext) {
        if (mContext == null) {
            return false;
        }
        return isNetworkAvailable(mContext) /*&& isConnected(mContext)*/ /*isConnected()*/ /*&& isInternetAvailable()*/;
    }

    public static boolean isNetworkAvailable(Context mContext) {
        if (mContext == null) {
            return false;
        }
        ConnectivityManager connectivityManager = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d("clearCookies", "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            Log.d("clearCookies", "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }



}
