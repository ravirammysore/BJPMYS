package in.appfocus.messageit.models;

import io.realm.RealmObject;

/**
 * Created by Ravi on 31/05/2017.
 */

public class Settings extends RealmObject {
    private String smsGatewayUrl;
    private int noOfSends=0;

    public int getNoOfSends() {
        return noOfSends;
    }

    public void incrementNoOfSends(){
        noOfSends++;
    }
    public String getSmsGatewayUrl() {
        return smsGatewayUrl;
    }

    public void setSmsGatewayUrl(String smsGatewayUrl) {
        this.smsGatewayUrl = smsGatewayUrl;
    }
}
