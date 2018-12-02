package com.github.bloodriot.pojo;

import com.github.bloodriot.serialization.SerializationBase;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FileDetails extends SerializationBase {
    @SerializedName("id")
    public int fileId;
    @SerializedName("name")
    public String fileName;
    @SerializedName("tags")
    public List<String> tags;
    @SerializedName("duration")
    public int fileDuration;
    @SerializedName("date")
    public String fileDate;
    @SerializedName("path")
    public String filePath;
    @SerializedName("preview")
    public String filePreviewSrc;
    @SerializedName("type")
    public String fileType;

    public FileDetails() {
        tags = new ArrayList<>();
    }
}
