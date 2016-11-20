package nitezh.ministock.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;

import nitezh.ministock.R;
import nitezh.ministock.domain.AndroidWidgetRepository;
import nitezh.ministock.domain.Widget;
import nitezh.ministock.domain.WidgetRepository;
import nitezh.ministock.domain.WidgetStock;

/*
        NOTIFICATION_STATE_NONE = 0,
        NOTIFICATION_STATE_PENDING = 1,
        NOTIFICATION_STATE_NOTFIED = 2
*/

public class CustomNotificationManager {

    public CustomNotificationManager(Context context, int widgetId) {
        this.widgetId = widgetId;
    }

    int widgetId = -1;
    NotificationManager notificationManager;
    SharedPreferences sharedpreferences;

    public void updateNotifications(Context context, WidgetStock widgetStock){
        sharedpreferences = context.getSharedPreferences(context.getString(R.string.notification_prefs_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        if (!widgetStock.getLimitHighTriggered() && !widgetStock.getLimitLowTriggered()) {
            editor.putInt(widgetStock.getLongName(), 0);
            editor.commit();
            return;
        }
        if (sharedpreferences.getInt(widgetStock.getLongName(), 0) == 0) {
            editor.putInt(widgetStock.getLongName(), 1);
            editor.commit();
        }

        WidgetRepository repository = new AndroidWidgetRepository(context);
        Widget widget = repository.getWidget(widgetId);

        if (widget.shouldShowNotifications()) {
            if(sharedpreferences.getInt(widgetStock.getLongName(), 0) == 1){
                buildNotification(context, widgetStock);
                editor.putInt(widgetStock.getLongName(), 2);
                editor.commit();
            }
        }
    }

    private void buildNotification(Context context, WidgetStock widgetStock){
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);

        builder
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(widgetStock.getLongName())
                .setContentText("Price: " + widgetStock.getPrice())
                .setSubText((widgetStock.getLimitHighTriggered() ? "High" : "Low") + " limit reached.")
                .setTicker(widgetStock.getLongName() + " " + widgetStock.getPrice())
                .setLights(0xFF00FF00, 500, 500) //setLights (int argb, int onMs, int offMs)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true);

        Notification notification = builder.build();

        notificationManager.notify(widgetStock.getLongName().hashCode(), notification);
    }

}
