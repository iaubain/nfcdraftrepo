package beans;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JacksonInject;

/**
 * Created by user on 3/26/2016.
 */
public class TransactionResp {

    @JsonProperty("cardpaymentonposrequestmodel")
    private
    Transaction cardpaymentonposrequestmodel;
    @JsonProperty("message")
    private
    String message;

    public Transaction getCardpaymentonposrequestmodel() {
        return cardpaymentonposrequestmodel;
    }

    public void setCardpaymentonposrequestmodel(Transaction cardpaymentonposrequestmodel) {
        this.cardpaymentonposrequestmodel = cardpaymentonposrequestmodel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
