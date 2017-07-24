package in.appfocus.messageit.models;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ravi on 20/05/2017.
 */

public class Contact extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private String mobileNo;
    private String note;

    public Contact(){}
    public Contact(String name,String mobileNo,String note){
        this.id= UUID.randomUUID().toString();
        this.name = name;
        this.mobileNo = mobileNo;
        this.note = note;
    }
    public Contact(String id,String name,String mobileNo,String note){
        this.id=id;
        this.name = name;
        this.mobileNo = mobileNo;
        this.note = note;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
