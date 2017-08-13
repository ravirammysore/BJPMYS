package in.appfocus.messageit.database;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by User on 13-08-2017.
 */

public class MyMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema realmSchema = realm.getSchema();

        //for our first migration in future
        if(oldVersion==0){
            //sample modification
            /*realmSchema.get("Contact").addField("email",String.class);
            oldVersion++;*/
        }

        //for our second migration in future
        if(oldVersion==1){
            /*realmSchema.get("Contact").addField("city",String.class);
            oldVersion++;*/
        }

        //and so on, for every migration
    }
}
