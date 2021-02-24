package com.miguel_santos.com.example.drink_water;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class NotificationPublisher extends BroadcastReceiver {

    private static final String KEY_NOTIFICATION = "key_notification";
    private static final String KEY_NOTIFICATION_ID = "key_notification_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(KEY_NOTIFICATION_ID, 0);
        String message = intent.getStringExtra(KEY_NOTIFICATION);

        intent = new Intent(context.getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Pegando o contexto de notificação de serviço e passando para um objeto do tipo manager.
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = getNotification(message, context, notificationManager, pendingIntent);
        notificationManager.notify(id, notification);
    }

    //Método próprio para criar uma notificação.
    private Notification getNotification(String content, Context context, NotificationManager manager, PendingIntent pendingIntent) {
        // Criação do conteúdo da notificação.
        Notification.Builder builder = new Notification.Builder(context.getApplicationContext())
                .setContentText(content)
                .setTicker("Alerta")
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_baseline_invert_colors_24);

        // Pesquisar sobre erros de notificação a partir da sdk 26(Oreo) e canais de notificação.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelID = "YOUR_CHANNEL_ID";
            NotificationChannel channel = new NotificationChannel(channelID, "channel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
            builder.setChannelId(channelID);
        }
        return builder.build();
    }


    public static String getKeyNotification() {
        return KEY_NOTIFICATION;
    }


    public static String getKeyNotificationId() {
        return KEY_NOTIFICATION_ID;
    }

}
