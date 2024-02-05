package com.github.bloodriot.pojo;

import com.github.bloodriot.serialization.SerializationBase;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FileDetailsList extends SerializationBase {
    @SerializedName("files")
    public List<FileDetails> files;

    public FileDetailsList() {
        files = new ArrayList<>();
    }
}
