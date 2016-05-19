package beans;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by user on 3/25/2016.
 */
public class Transaction {
    @JsonProperty("deviceId")
    private
    String deviceId;
    @JsonProperty("cardId")
    private
    String cardId;
    @JsonProperty("itemId")
    private
    int itemId;
    @JsonProperty("quantity")
    private
    int quantity;
    @JsonProperty("totalAmount")
    private
    int totalAmount;
    @JsonProperty("profileId")
    private
    int profileId;
    @JsonProperty("transporterId")
    private
    int transporterId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public int getTransporterId() {
        return transporterId;
    }

    public void setTransporterId(int transporterId) {
        this.transporterId = transporterId;
    }
}
