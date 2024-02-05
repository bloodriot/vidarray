package com.github.bloodriot.pojo;

import com.github.bloodriot.serialization.SerializationBase;
import com.google.gson.annotations.SerializedName;

public class Tag extends SerializationBase {
    @SerializedName("id")
    public int tagId;
    @SerializedName("tag")
    public String tagName;
}
