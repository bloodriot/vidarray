package com.github.bloodriot.pojo;

import com.github.bloodriot.serialization.SerializationBase;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Tags extends SerializationBase {
    @SerializedName("tags")
    public List<Tag> contents;

    public Tags() {
        contents = new ArrayList<>();
    }
}
