package in.appfocus.messageit.models;

import java.util.Date;
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
    private Date dob;
    private Date doa;
    private String field1;
    private String field2;
    private String field3;
    private String field4;

    public Contact(){
        this.id= UUID.randomUUID().toString();
    }
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

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public Date getDoa() {
        return doa;
    }

    public void setDoa(Date doa) {
        this.doa = doa;
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

    public String getField4() {
        return field4;
    }

    public void setField4(String field4) {
        this.field4 = field4;
    }
}
