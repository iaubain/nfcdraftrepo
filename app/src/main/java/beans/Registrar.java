package beans;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by user on 4/11/2016.
 */
public class Registrar {
    @JsonProperty("transporterName")
    private String transporterName;
    @JsonProperty("transporterId")
    private int transporterId;
    @JsonProperty("password")
    private String password;
    @JsonProperty("deviceName")
    private String deviceName;

    public String getTransporterName() {
        return transporterName;
    }

    public void setTransporterName(String transporterName) {
        this.transporterName = transporterName;
    }

    public int getTransporterId() {
        return transporterId;
    }

    public void setTransporterId(int transporterId) {
        this.transporterId = transporterId;
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
