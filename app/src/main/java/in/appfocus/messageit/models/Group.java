package in.appfocus.messageit.models;

import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ravi on 20/05/2017.
 */

public class Group extends RealmObject {

    @PrimaryKey
    private String id;
    private String name;
    private String note;
    private RealmList<Contact> contacts;
    private String field1;
    private String field2;
    private String field3;

    public Group(){
        this.id = UUID.randomUUID().toString();
    }

    public Group(String name,String note){
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.note = note;
    }
    public Group(String id,String name,String note){
        this.id = id;
        this.name = name;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public RealmList<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(RealmList<Contact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public String toString() {
        return name;
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
