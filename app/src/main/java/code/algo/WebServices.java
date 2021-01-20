package code.algo;

import android.graphics.Bitmap;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.kmcapp.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import code.database.AppSettings;
import code.utils.AppConstants;
import code.utils.AppUtils;
import code.view.BaseActivity;

public class WebServices {

    public static void postApi(final BaseActivity mActivity,
                               String url,
                               JSONObject jsonObject,
                               final boolean loader,
                               final boolean loaderCancel,
                               final WebServicesCallback apiCallback) {

        if (loader)
            AppUtils.showRequestDialog(mActivity);

        AppUtils.hideSoftKeyboard(mActivity);

        Log.v("postApi-URL", url);
        Log.v("postApi-jsonObject", jsonObject.toString());

        Log.v("fgdfdfgdfgdfg", AppUtils.md5(mActivity.getPackageName()));
        Log.v("fgdfgdfgdfgdfg", AppSettings.getString(AppSettings.token));

        AndroidNetworking.post(url)
                .addJSONObjectBody(jsonObject)
                .setPriority(Priority.HIGH)
               // .addHeaders("package", AppUtils.md5(mActivity.getPackageName()))
                .addHeaders("package", AppUtils.md5("com.kmc.android"))
                .addHeaders("token", AppSettings.getString(AppSettings.token))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (loaderCancel)
                            AppUtils.hideDialog();
                        Log.v("Fifth", response.toString());
                        Log.v("postApi-response", response.toString());

                        try {

                            JSONObject jsonObject = response.getJSONObject(AppConstants.projectName);

                            if (jsonObject.getString("resCode").equals("1")) {
                                apiCallback.OnJsonSuccess(response);
                            }
                            //only using in getToken Api
                            else if (jsonObject.getString("resCode").equals("3")) {
                                apiCallback.OnJsonSuccess(response);
                            } else {


                                AppUtils.hideDialog();
                                if (jsonObject.getString("resMsg").equals("Invalid Password")) {
                                    AppUtils.showToastSort(mActivity, mActivity.getString(R.string.passwordWorng));
                                } else if (jsonObject.getString("resMsg").equals("Please Enter Unique Hospital Registration Number")) {
                                    AppUtils.showToastSort(mActivity, mActivity.getString(R.string.errorUniqueHospitalNo));
                                } else {
                                    apiCallback.OnFail(mActivity.getString(R.string.noDataFound));
                                }
//                                else if (jsonObject.getString("resMsg").equals("Data Not Found")) {
//                                    AppUtils.showToastSort(mActivity, mActivity.getString(R.string.noDataFound));
//                                }
//                                else {
//                                    AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));
//                                }
//                                AppUtils.showToastSort(mActivity, jsonObject.getString("resMsg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            apiCallback.OnFail(e.getMessage());
                            AppUtils.hideDialog();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {

                        AppUtils.hideDialog();

                        apiCallback.OnFail(anError.getErrorBody());
                        Log.v("Fifth", anError.getErrorBody());
                        Log.v("apiCallback", String.valueOf(anError));
                        Log.v("apiCallback", String.valueOf(anError.getErrorCode()) + "  " + url + " -- " + jsonObject);
                        Log.v("apiCallback", String.valueOf(anError.getErrorBody()));
                        Log.v("apiCallback", String.valueOf(anError.getErrorDetail()));
                    }
                });
    }


    public static void getApi(final BaseActivity mActivity,
                              String url,
                              final boolean loader,
                              final boolean loaderCancel,
                              final WebServicesCallback apiCallback) {

        if (loader)
            AppUtils.showRequestDialog(mActivity);

        AppUtils.hideSoftKeyboard(mActivity);

        Log.v("getApi - apiUrl", url);

        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .addHeaders("package", AppUtils.md5(mActivity.getPackageName()))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (loaderCancel)
                            AppUtils.hideDialog();

                        Log.v("getApi-response", response.toString());

                        apiCallback.OnJsonSuccess(response);

                    }

                    @Override
                    public void onError(ANError anError) {

                        AppUtils.hideDialog();

                        apiCallback.OnFail(anError.getErrorBody());

                        Log.v("respSendComment", String.valueOf(anError.getErrorCode()));
                        Log.v("respSendComment", String.valueOf(anError.getErrorBody()));
                        Log.v("respSendComment", String.valueOf(anError.getErrorDetail()));
                    }
                });

    }

    public static void downloadImageApi(final BaseActivity mActivity,
                                        String url,
                                        final WebServicesImageCallback apiCallback) {

        AppUtils.hideSoftKeyboard(mActivity);

        Log.v("downloadImageApi-URL", url);

        AndroidNetworking.get(url)
                .setTag(url)
                .setPriority(Priority.HIGH)
                .setBitmapMaxHeight(300)
                .setBitmapMaxWidth(300)
                .setBitmapConfig(Bitmap.Config.ARGB_8888)
                .build()
                .getAsBitmap(new BitmapRequestListener() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        // do anything with bitmap

                        apiCallback.OnBitmapSuccess(bitmap);

                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error

                        apiCallback.OnFail(error.getErrorBody());

                        /*Log.v("apiCallback", String.valueOf(error));
                        Log.v("apiCallback", String.valueOf(error.getErrorCode()));
                        Log.v("apiCallback", String.valueOf(error.getErrorBody()));
                        Log.v("apiCallback", String.valueOf(error.getErrorDetail()));*/
                    }
                });
    }

    public static void downloadImageApi_new(final BaseActivity mActivity, String url, final WebServicesImageCallback apiCallback) {

        AppUtils.hideSoftKeyboard(mActivity);

        Log.v("downloadImageApi-URL", url);

        AndroidNetworking.get(url)
                .setTag(url)
                .setPriority(Priority.HIGH)
                .setBitmapConfig(Bitmap.Config.ARGB_8888)
                .build()
                .getAsBitmap(new BitmapRequestListener() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        apiCallback.OnBitmapSuccess(bitmap);

                    }

                    @Override
                    public void onError(ANError error) {
                        apiCallback.OnFail(error.getErrorBody());
                    }
                });
    }
}
