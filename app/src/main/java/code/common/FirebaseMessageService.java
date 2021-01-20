package code.common;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kmcapp.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import code.utils.AppUtils;

//Created by Mohammad Faiz on 3/29/2017.

public class FirebaseMessageService extends FirebaseMessagingService {

    private static final String TAG = "MyFMService";
    String CHANNEL_ID = "com.mncu.android";
    private NotificationManager mManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d(TAG, "FCM Notification Message: " + remoteMessage.getData() + "...." + remoteMessage.getFrom());

        if (remoteMessage.getData() != null) {

            Map<String, String> params = remoteMessage.getData();
            JSONObject json = new JSONObject(params);

            try {

                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                long time = System.currentTimeMillis();

                String message = json.getString("message");

                String imageUrl = "";
                imageUrl = json.getString("ProfilePic");

                String datetime = String.valueOf(time);
                String transid;

                if (json.has("transid")) {
                    transid = json.getString("transid");
                } else {
                    transid = "0";
                }

                String type = json.getString("msgtype");

                String flat = "";
                if(json.has("flatno")) {
                    flat = json.getString("flatno");
                }
                String email = "";

                if (json.has("email")) {
                    email = json.getString("email");
                }
                String mobile = json.getString("mobile");

                Log.e(TAG, "message: " + message);
                Log.e(TAG, "transid: " + transid);
                Log.e(TAG, "datetime: " + datetime);
                Log.e(TAG, "imageUrl: " + imageUrl);
                Log.e(TAG, "timestamp: " + timeStamp);
                Log.e(TAG, "flatid: " + flat);
                Log.e(TAG, "type: " + type);

                Intent resultIntent = null;

                /*if (type.equals("2")) {
                    // app is in background, show the tableName in tableName tray
                    resultIntent = new Intent(getApplicationContext(), AcceptRejectPushActivity.class);
                    resultIntent.putExtra("message", message);
                    resultIntent.putExtra("transid", transid);
                } else if (type.equals("5")) {
                    // app is in background, show the tableName in tableName tray
                    resultIntent = new Intent(getApplicationContext(), NoticeBoardDetailActivity.class);
                    resultIntent.putExtra("message", message);
                    resultIntent.putExtra("transid", transid);
                }else if (type.equals("9")) {
                    resultIntent = new Intent(getApplicationContext(), ComplaintDetailActivity.class);
                    resultIntent.putExtra("message", message);
                    resultIntent.putExtra("transid", transid);
                } else if (type.equals("8")) {
                    resultIntent = new Intent(getApplicationContext(), ClassifiedDetailActivity.class);
                    resultIntent.putExtra("message", message);
                    resultIntent.putExtra("transid", transid);
                }else {
                    resultIntent = new Intent(getApplicationContext(), PushListingActivity.class);
                    resultIntent.putExtra("message", message);
                    resultIntent.putExtra("transid", transid);
                }*/

                createNotification (getString(R.string.app_name),message,resultIntent,transid);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void createNotification( String title, String message,Intent intent,String transid) {

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel androidChannel = new NotificationChannel(CHANNEL_ID,
                    title, NotificationManager.IMPORTANCE_HIGH);
            // Sets whether notifications posted to this channel should display tableName lights
            androidChannel.enableLights(true);
            // Sets whether tableName posted to this channel should vibrate.
            androidChannel.enableVibration(true);
            // Sets the tableName light color for notifications posted to this channel
            androidChannel.setLightColor(R.color.red);
            //androidChannel.setSound(null, null);

            // Sets whether notifications posted to this channel appear on the lockscreen or not
            androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(androidChannel);

            NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.mipmap.logo)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setTicker(title)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setSound(defaultUri)
                    .setContentIntent(contentIntent);

            getManager().notify(Integer.parseInt(transid), notification.build());

        } else {
            try {

                playNotificationSound();

                @SuppressLint({"NewApi", "LocalSuppress"}) NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logo))
                        .setSmallIcon(R.mipmap.logo)
                        .setContentTitle(title)
                        .setTicker(title)
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setContentIntent(contentIntent)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setLights(0xFF760193, 300, 1000)
                        .setAutoCancel(true)
                        .setVibrate(new long[]{200, 400});

                long timestamp = AppUtils.currentTimestamp();

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(Integer.parseInt(transid)/* ID of tableName */, notificationBuilder.build());

            } catch (SecurityException se) {
                se.printStackTrace();
            }
        }
    }


    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Playing tableName sound
    public void playNotificationSound() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(this, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
