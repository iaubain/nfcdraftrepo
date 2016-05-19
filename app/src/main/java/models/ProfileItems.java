package models;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Created by user on 4/9/2016.
 */
public class ProfileItems {
    @JsonProperty("id")
    private int id;
    @JsonProperty("transporterId")
    private int transporterId;
    @JsonProperty("items")
    private List<Product> items;
    private String name;

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

    public List<Product> getItems() {
        return items;
    }

    public void setItems(List<Product> items) {
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
