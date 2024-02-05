package com.github.bloodriot.database;

import com.github.bloodriot.pojo.FileDetails;
import com.github.bloodriot.pojo.FileDetailsList;
import com.github.bloodriot.pojo.Tags;
import com.github.bloodriot.protocol.FileDetailsRequest;
import com.github.bloodriot.protocol.FileListRequest;

public abstract class IDatabaseConnection implements AutoCloseable  {

    /**
     * Connects to the database
     * @param jdbcString A string containing the jdbc connection parameters.
     * @return True on connection success, false otherwise.
     */
    public abstract boolean connect(String jdbcString);

    /**
     * Takes a FileListRequest and uses it to fetch a list of files from the database.
     * @param fileListRequest The request to fulfill.
     * @return A populated FileListResponse object if possible, an empty one if not.
     */
    public abstract FileDetailsList getFileList(FileListRequest fileListRequest);

    /**
     * Takes a FileDetailsRequest and uses it to fetch the details of a file from the database.
     * @param fileDetailsRequest The request to fulfill.
     * @return A populated FileDetails object if possible, an empty one if not.
     */
    public abstract FileDetails getFileDetails(FileDetailsRequest fileDetailsRequest);

    /**
     * Attempts to delete a file from the database.
     * @param fileId The fileId of the file to delete.
     * @return True if successful, false if not.
     */
    public abstract boolean deleteFile(String fileId);

    /**
     * Attempts to update a file in the database.
     * @param fileDetails The new details of the file.
     * @return True if successful, false if not.
     */
    public abstract boolean updateFileDetails(FileDetails fileDetails);

    /**
     * Takes a FileDetails object and populates the tags for it.
     * @param fileDetails The file in need of tag population.
     * @return An updated version of the passed in FileDetails object.
     */
    public abstract FileDetails populateTags(FileDetails fileDetails);

    /**
     *
     * @return A TagsColumns pojo populated with the tags in the database.
     */
    public abstract Tags getTags();

    /**
     *
     * @return A TagsColumns pojo populated with the tags assigned to the fileId.
     */
    public abstract Tags getTags(int fileId);

    /**
     * Adds a new tag to the database.
     * @param tag The tag to add.
     * @return True if successful, false if not.
     */
    public abstract boolean addTag(String tag);

    /**
     * Adds a new tag to a file in the database.
     * @param fileId The file to add the tag to.
     * @param tagId The tag to add.
     * @return True if successful, false if not.
     */
    public abstract boolean addTagToFile(int fileId, int tagId);

    /**
     * Removes a tag from the database.
     * @param tagId The tag to remove.
     * @return True if successful, false if not.
     */
    public abstract boolean removeTag(int tagId);

    /**
     * Removes a tag from a file in the database.
     * @param fileId The file to remove the tag from.
     * @param tagId The tag to remove.
     * @return True if successful, false if not.
     */
    public abstract boolean removeTagFromFile(int fileId, int tagId);
}
