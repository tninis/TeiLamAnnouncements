package announcements.tninis.cloud.teilamannouncements;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.*;
import android.os.*;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class AnnouncementsService extends Service {
    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    private static  String CHANNEL_ID  = "TEST_ID";
    NotificationCompat.Builder mBuilder;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name ="Test Channel";
            String description = "Test Channel Descr";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public void onCreate() {

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("ΤΕΙ Στερεάς Ελλάδας")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("Έχουν Δημοσιευθεί Νέες Ανακοινώσεις")
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(contentIntent);
        createNotificationChannel();
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                isDifferent();
                handler.postDelayed(runnable, 10000);
            }
        };

        handler.postDelayed(runnable, 10000);
    }

    @Override
    public void onDestroy() {
       handler.removeCallbacks(runnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;

    }


    private void isDifferent()
    {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final int currentValue = preferences.getInt("CurreCounter",0);

        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request= new okhttp3.Request.Builder()
                .url(Constants.PAGE)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Document doc = Jsoup.parse(response.body().string());
                    Elements items = doc.select("li:not(.menu-item) >a[href]");
                    if (currentValue < items.size()) {
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                        notificationManager.notify(1, mBuilder.setContentText("Current Annou: "+items.size()).build());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("CurreCounter", items.size());
                        editor.commit();
                    }
                    }
                }

        });
    }

}