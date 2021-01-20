package code.algo;

import android.graphics.Bitmap;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mohammad Faiz on 2/2/2020.
 */

public interface WebServicesImageCallback {

    void OnFail(String responce);

    void OnBitmapSuccess(Bitmap bitmap);
}
