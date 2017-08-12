package in.appfocus.messageit.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ravi on 28/06/2017.
 */

public class History extends RealmObject {
    @PrimaryKey
    private String pushid;
    private String message;
    private String group;
    private String date;
    private String field1;
    private String field2;
    private String field3;

    public String getPushid() {
        return pushid;
    }

    public void setPushid(String pushid) {
        this.pushid = pushid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }
}
