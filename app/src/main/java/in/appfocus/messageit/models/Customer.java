package in.appfocus.messageit.models;

import io.realm.RealmObject;

/**
 * Created by Ravi on 31/05/2017.
 */

public class Customer extends RealmObject {
    private String senderId;
    private String uid;
    private String apiPin;
    private String route;
    private String deviceGmailAccount;
    private String deviceId;
    private String appTitle;
    private String appSubTitle;
    private String field1;
    private String field2;
    private String field3;
    private String field4;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getApiPin() {
        return apiPin;
    }

    public void setApiPin(String apiPin) {
        this.apiPin = apiPin;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getDeviceGmailAccount() {
        return deviceGmailAccount;
    }

    public void setDeviceGmailAccount(String deviceGmailAccount) {
        this.deviceGmailAccount = deviceGmailAccount;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAppTitle() {
        return appTitle;
    }

    public void setAppTitle(String appTitle) {
        this.appTitle = appTitle;
    }

    public String getAppSubTitle() {
        return appSubTitle;
    }

    public void setAppSubTitle(String appSubTitle) {
        this.appSubTitle = appSubTitle;
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
