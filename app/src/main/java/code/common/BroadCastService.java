package code.common;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

/**
 * Created method Faiz on 5/31/2016.
 */
public class BroadCastService extends Service {


    private static final String TAG = "BroadcastService";
    public static final String BROADCAST_ACTION = "ASD";
    private final Handler handler = new Handler();
    Intent intent;

    private static long changePositionBy = 300000; // 5 Minutes

    double latitude =0;

    // Getting longitude of the current location
    double longitude =0;

    @Override
    public void onCreate() {
        super.onCreate();

        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, changePositionBy);

        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }


    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            //broadcastUpdateInfo();
            //NotificationAlgo.createAssessment();
            handler.postDelayed(this, changePositionBy);
        }
    };

    //call your API in this method
    private void broadcastUpdateInfo() {
        //Log.d(TAG, "entered DisplayLoggingInfo");
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        //super.onDestroy();
        handler.removeCallbacks(sendUpdatesToUI);
        //sendBroadcast(new Intent("YouWillNeverKillMe"));
    }
}
