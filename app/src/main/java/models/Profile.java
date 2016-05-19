package models;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by user on 4/9/2016.
 */
public class Profile {
    @JsonProperty("profileonsellingdevice")
    private ProfileItems profileonsellingdevice;
    @JsonProperty("message")
    private String message;

    public ProfileItems getProfileOnSellingDevice() {
        return profileonsellingdevice;
    }

    public void setProfileOnSellingDevice(ProfileItems profileOnSellingDevice) {
        this.profileonsellingdevice = profileOnSellingDevice;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}