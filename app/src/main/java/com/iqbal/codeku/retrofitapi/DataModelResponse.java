package com.iqbal.codeku.retrofitapi;

import com.google.gson.annotations.Expose;

public class DataModelResponse {
    @Expose
    private String message;
    @Expose
    private int id;

    public DataModelResponse(String message, int id) {
        this.message = message;
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
