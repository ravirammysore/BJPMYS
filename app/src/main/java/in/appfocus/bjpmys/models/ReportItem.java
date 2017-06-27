package in.appfocus.bjpmys.models;

/**
 * Created by Ravi on 24/06/2017.
 */

public class ReportItem {
    private String smsid;
    private String mobile;
    private String status;

    public String getSmsid() {
        return smsid;
    }

    public void setSmsid(String smsid) {
        this.smsid = smsid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
