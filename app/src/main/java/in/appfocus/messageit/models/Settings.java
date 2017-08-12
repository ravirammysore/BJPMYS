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

    private String field1;
    private String field2;
    private String field3;

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
