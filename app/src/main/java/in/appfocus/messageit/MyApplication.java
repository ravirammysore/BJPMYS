package in.appfocus.messageit;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import in.appfocus.messageit.database.MyInitialTransaction;
import in.appfocus.messageit.database.MyMigration;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Ravi on 20/05/2017.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        Realm.init(this);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                //increment this after every change in schema...
                .schemaVersion(0)
                .initialData(new MyInitialTransaction())
                //...and provide the migration object which has the logic for migration
                //.migration(new MyMigration())
                .build();

        Realm.setDefaultConfiguration(realmConfiguration);

        //We want all activities in potrait mode only, we could do this from manifest, but this is easier!
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity,
                                          Bundle savedInstanceState) {

                // new activity created; force its orientation to portrait
                activity.setRequestedOrientation(
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }
}
