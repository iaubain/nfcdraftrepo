package beans;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by user on 4/11/2016.
 */
public class Record {
    @JsonProperty("phoneNumber")
    private
    String phoneNumber;
    @JsonProperty("password")
    private
    String password;
    @JsonProperty("deviceName")
    private
    String deviceName;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
