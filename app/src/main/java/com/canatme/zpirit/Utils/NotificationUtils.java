package com.canatme.zpirit.Utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.airbnb.lottie.utils.Utils;
import com.canatme.zpirit.Activities.MainActivity;
import com.canatme.zpirit.Activities.OrdersActivity;
import com.canatme.zpirit.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationUtils {

    private static String TAG = NotificationUtils.class.getSimpleName();

    private Context mContext;

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppInForeground(Context context) {
        boolean isInForeground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.
                        IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInForeground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInForeground = false;
            }
        }

        return !isInForeground;
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void billNotification(Context context, long senderId, long attachmentId,
                                        String billAmount, String billerName, String dueDate,
                                        String imageUrl) {
        final int NOTIFICATION_ID = (int) senderId;
        final Notification notification;

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "0");
//        mBuilder.setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mBuilder.setSmallIcon(R.drawable.ic_logozpirit_outlinesfortext);
            mBuilder.setContentTitle("Content Title")
                    .setStyle(
                            new NotificationCompat.BigTextStyle()
                                    .bigText("Podd ·"))
                    .setAutoCancel(true).setDefaults(Notification.DEFAULT_SOUND)
                    .setLights(Color.WHITE, 500, 500)
                    .setContentText("New bill");
            notification = mBuilder.build();
        } else {
            @SuppressLint("RemoteViewLayout") RemoteViews customNotificationView = new RemoteViews(context.getPackageName(),
                    R.layout.customnotif);

            //Making the pay button work
            customNotificationView.setTextViewText(R.id.tvEmail, "Podd");
//            customNotificationView.setTextViewText(R.id.tvBillAmount, "₹" + billAmount);
            customNotificationView.setTextViewText(R.id.tvSenderName, billerName);
            customNotificationView.setTextViewText(R.id.tvDueOn, "");

            Intent paymentIntent = new Intent(context, OrdersActivity.class);
            Bundle paymentBundle = new Bundle();
            paymentBundle.putString("TRIGGER_PAYMENT", "TRIGGER_PAYMENT");
            paymentBundle.putLong("long1", attachmentId);
            paymentBundle.putLong("long2", senderId);
            paymentIntent.putExtras(paymentBundle);
            PendingIntent paymentPendingIntent = PendingIntent.getActivity(context, 0, paymentIntent, 0);
            customNotificationView.setOnClickPendingIntent(R.id.btNotifs, paymentPendingIntent);

            mBuilder.setContent(customNotificationView);
            mBuilder.setSmallIcon(R.drawable.ic_logozpirit_outlinesfortext);
            mBuilder.setAutoCancel(true);
            mBuilder.setDefaults(Notification.DEFAULT_SOUND);
            mBuilder.setLights(Color.WHITE, 500, 500);
            notification = mBuilder.build();

            if (StringUtils.isNotEmpty(imageUrl)) {
                Picasso.get().load(imageUrl).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(customNotificationView, R.id.ivDisplayPicture, NOTIFICATION_ID, notification);
            }
        }
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    public static void newDocumentNotification(Context context, Bitmap image, long senderId,
                                               long attachmentId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "0");
        Intent intent = new Intent(context, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong("long1", attachmentId);
        bundle.putLong("long2", senderId);
        intent.putExtras(bundle);
        PendingIntent p = PendingIntent.getActivity(context, 0, intent, 0);

        builder.setSmallIcon(R.drawable.ic_logozpirit_outlinesfortext)
                .setContentTitle("New documents are here")
                .setContentText("Click to open.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(p);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            if (image != null) {
                builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image)
                        .setSummaryText("Preview"));
            }
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        notificationManager.notify(Constants.BULK_DOWNLOAD_NOTIFICATION_ID, notification);
    }

    public static void bulkDownloadNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "0");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            Intent i = new Intent(context, MainActivity.class);
            PendingIntent p = PendingIntent.getActivity(context, 0, i, 0);
            builder.setSmallIcon(R.drawable.ic_logozpirit_outlinesfortext)
                    .setContentTitle("New documents are here")
                    .setContentText("Click to view.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(p);
        } else {
            Intent i = new Intent(context, MainActivity.class);
            PendingIntent p = PendingIntent.getActivity(context, 0, i, 0);
            builder.setSmallIcon(R.drawable.ic_logozpirit_outlinesfortext)
                    .setContentTitle("New documents are here")
                    .setContentText("Click to view.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(p);
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        notificationManager.notify(Constants.BULK_DOWNLOAD_NOTIFICATION_ID, notification);
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.ENGLISH);
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void showNotificationMessage(String title, String message, String timeStamp,
                                        Intent intent) {
        showNotificationMessage(title, message, timeStamp, intent, null);
    }

    public void showNotificationMessage(String title, String message, String timeStamp,
                                        PendingIntent intent) {
        showNotificationMessage(title, message, timeStamp, intent, null);
    }

    public void showNotificationMessage(final String title, final String message, final String
            timeStamp, Intent intent, String imageUrl) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;

        // notification icon
        final int icon = R.drawable.ic_logozpirit_outlinesfortext;

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext);

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + mContext.getPackageName() + "/raw/notification");

        if (!TextUtils.isEmpty(imageUrl)) {

            if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl)
                    .matches()) {

                Bitmap bitmap = getBitmapFromURL(imageUrl);

                if (bitmap != null) {
                    showBigNotification(bitmap, mBuilder, icon, title, message, timeStamp,
                            resultPendingIntent, alarmSound);
                } else {
                    showSmallNotification(mBuilder, icon, title, message, timeStamp,
                            resultPendingIntent, alarmSound);
                }
            }
        } else {
            showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent,
                    alarmSound);
            playNotificationSound();
        }
    }

    public void showNotificationMessage(final String title, final String message, final String
            timeStamp, PendingIntent intent, String imageUrl) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;

        // notification icon
        final int icon = R.drawable.ic_logozpirit_outlinesfortext;

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext);

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + mContext.getPackageName() + "/raw/notification");

        if (!TextUtils.isEmpty(imageUrl)) {

            if (imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {

                Bitmap bitmap = getBitmapFromURL(imageUrl);

                if (bitmap != null) {
                    showBigNotification(bitmap, mBuilder, icon, title, message, timeStamp, intent,
                            alarmSound);
                } else {
                    showSmallNotification(mBuilder, icon, title, message, timeStamp, intent,
                            alarmSound);
                }
            }
        } else {
            showSmallNotification(mBuilder, icon, title, message, timeStamp, intent, alarmSound);
            playNotificationSound();
        }
    }

    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title,
                                       String message, String timeStamp, PendingIntent
                                               resultPendingIntent, Uri alarmSound) {

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(message);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(
                Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(inboxStyle)
                .setWhen(new Date().getTime())
                .setSmallIcon(R.drawable.ic_logozpirit_outlinesfortext)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .setPriority(Notification.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "podd_notif_channel";
            NotificationChannel channel = new NotificationChannel(
                    "1",
                    "Podd channel",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        notificationManager.notify(Constants.NOTIFICATION_ID, builder.build());
    }

    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon,
                                     String title, String message, String timeStamp, PendingIntent
                                             resultPendingIntent, Uri alarmSound) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);
        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(bigPictureStyle)
//                .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(R.drawable.ic_logozpirit_outlinesfortext)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.NOTIFICATION_ID_BIG_IMAGE, notification);
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Playing notification sound
    public void playNotificationSound() {
        try {
//            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
//                    + "://" + mContext.getPackageName() + "/raw/notification");
//            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
//            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
