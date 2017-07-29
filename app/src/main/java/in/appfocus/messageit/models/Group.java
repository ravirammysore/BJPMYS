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
    String id;
    String name;
    String note;
    RealmList<Contact> contacts;

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
}
