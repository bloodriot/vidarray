package com.github.bloodriot.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public abstract class SerializationBase {
    /**
     * Serializes this class to a json string.
     * @return The json representation of this class.
     */
    public String toJson() {
        return toJson(true);
    }

    /**
     * Serializes this class to a json string.
     * @param prettyPrint Should the output be printed with pretty print or should it be compact.
     * @return The json representation of this class.
     */
    public String toJson(final boolean prettyPrint) {
        GsonBuilder gsonBuilder;
        if (prettyPrint) {
            gsonBuilder = new GsonBuilder().setPrettyPrinting();
        } else {
            gsonBuilder = new GsonBuilder();
        }
        Gson gson = gsonBuilder.create();
        return gson.toJson(this);
    }

    /**
     * Deserializes this class from a json string.
     * @param type The class that this json string represents.
     * @param jsonString The json string to deserialize.
     * @return A new instance of the class.
     * @throws JsonSyntaxException If the given jsonString is unparsable into the requested object.
     */
    public static <T extends SerializationBase> T fromJson(Class<T> type, final String jsonString) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.fromJson(jsonString, type);
    }

    /**
     * Deserializes this class from a json string.
     * @param jsonString The json string to deserialize.
     * @return A new instance of this class.
     */
    @SuppressWarnings("unchecked")
    public <T extends SerializationBase> T fromJson(final String jsonString) {
        return (T) fromJson(this.getClass(), jsonString);
    }
}