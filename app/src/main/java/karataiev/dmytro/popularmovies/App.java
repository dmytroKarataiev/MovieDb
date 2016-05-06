package karataiev.dmytro.popularmovies;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.RefWatcher;

/**
 * Application to start if necessary leak watcher
 * Created by karataev on 12/28/15.
 */
public class App extends Application {

    public static RefWatcher getRefWatcher(Context context) {
        App application = (App) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;


    @Override
    public void onCreate() {
        super.onCreate();
        //refWatcher = LeakCanary.install(this);
    }
}
