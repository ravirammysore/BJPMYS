package in.appfocus.messageit.models;

import io.realm.RealmObject;

/**
 * Created by Ravi on 31/05/2017.
 */

public class Settings extends RealmObject {
    private String smsGatewayUrl;
    private int noOfSends=0;
    private String smsBalanceUrl;

    private String smsDeliveryReportUrl;
    private String customerCheckUrl;

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

    public String getSmsBalanceUrl() {
        return smsBalanceUrl;
    }

    public void setSmsBalanceUrl(String smsBalanceUrl) {
        this.smsBalanceUrl = smsBalanceUrl;
    }

    public String getSmsDeliveryReportUrl() {
        return smsDeliveryReportUrl;
    }

    public void setSmsDeliveryReportUrl(String smsDeliveryReportUrl) {
        this.smsDeliveryReportUrl = smsDeliveryReportUrl;
    }

    public String getCustomerCheckUrl() {
        return customerCheckUrl;
    }

    public void setCustomerCheckUrl(String customerCheckUrl) {
        this.customerCheckUrl = customerCheckUrl;
    }
}
