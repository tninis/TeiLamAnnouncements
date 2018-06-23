package announcements.tninis.cloud.teilamannouncements;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkOperations {

    public static boolean isNetworkAvailable(Context context) {

        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
