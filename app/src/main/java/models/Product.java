package models;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by Owner on 3/16/2016.
 */
public class Product {
    @JsonProperty("id")
    private int id;
    @JsonProperty("transporterId")
    private int transporterId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("price")
    private int price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTransporterId() {
        return transporterId;
    }

    public void setTransporterId(int transporterId) {
        this.transporterId = transporterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
