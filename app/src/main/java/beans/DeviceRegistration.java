package beans;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by user on 4/11/2016.
 */
public class DeviceRegistration {
    @JsonProperty("sellingdeviceregistrationresponse")
    private
    Registrar reg;
    @JsonProperty("message")
    private
    String message;

    public Registrar getReg() {
        return reg;
    }

    public void setReg(Registrar reg) {
        this.reg = reg;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
