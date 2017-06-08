package in.appfocus.bjpmys.models;

import io.realm.RealmObject;

/**
 * Created by Ravi on 31/05/2017.
 */

public class Customer extends RealmObject {
    private String senderId;
    private String uid;
    private String apiPin;
    private String route;

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
}
