package ldemyanenko.com.flashlight;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * Created by ldemyanenko on 07.03.2017.
 */

public class NotificationUtils {
    private static final int NOTIFICATION_ID = 10000;


    public static void createNotification(Activity activity) {
        Context context = activity.getApplicationContext();
        final NotificationCompat.Builder builder = buildNotification(context,activity);
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationmanager.notify(NOTIFICATION_ID, builder.build());
    }

    private static NotificationCompat.Builder buildNotification(Context context, Activity activity) {

        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(
                context,
                0,
                activity.getIntent(),
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                // Set Icon
                .setSmallIcon(R.mipmap.ic_launcher)
                // Set Ticker Message
                .setTicker("Ticker")
                .setOngoing(true)
                // Dismiss Notification
                //.setAutoCancel(true)
                // Set PendingIntent into Notification
                .setContentIntent(pIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // build a complex notification, with buttons and such
            //
            builder = builder.setContent(getComplexNotificationView(context));
        } else {
            // Build a simpler notification, without buttons
            //
            builder = builder.setContentTitle("Flashlight")
                    .setContentText("You can use your flashlight");
        }
        return builder;
    }

    private static RemoteViews getComplexNotificationView(Context context) {
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews notificationView = new RemoteViews(
                context.getPackageName(),
                R.layout.activity_custom_notification
        );

        // Locate and set the Image into customnotificationtext.xml ImageViews
        notificationView.setImageViewResource(
                R.id.imagenotileft,
                R.mipmap.ic_launcher);


        PendingIntent flashlightPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, MainActivity.FlashlightActionListener.class), 0);
        notificationView.setOnClickPendingIntent(R.id.flashlightButton, flashlightPendingIntent);
        PendingIntent whiteScreenPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, MainActivity.WhiteScreeActionListener.class), 0);
        notificationView.setOnClickPendingIntent(R.id.whiteScreenButton, whiteScreenPendingIntent);

        // Locate and set the Text into customnotificationtext.xml TextViews
        notificationView.setTextViewText(R.id.title, "Flashlight");
        notificationView.setTextViewText(R.id.text, "Use your flashlight");

        return notificationView;
    }

    public static void removeNotification(Context context) {
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationmanager.cancel(NOTIFICATION_ID);
    }


    public static boolean debugModeTwoDaysDateCheck(String s) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try {
             date = sdf.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -3);
        Date limitDaysBefore = cal.getTime();
        if(date==null){
            return false;
        }
        return limitDaysBefore.compareTo(date)==1;
    }
}

