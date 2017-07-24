package in.appfocus.messageit;

import android.app.Application;

import in.appfocus.messageit.database.MyInitialTransaction;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Ravi on 20/05/2017.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        //This can be helpful when tinkering with models early in the development cycle of your app.
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .initialData(new MyInitialTransaction())
                .build();

        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
