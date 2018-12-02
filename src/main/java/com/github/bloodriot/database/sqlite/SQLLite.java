package com.github.bloodriot.database.sqlite;

import com.github.bloodriot.annotations.DatabaseConnector;
import com.github.bloodriot.database.IDatabaseConnection;
import com.github.bloodriot.fields.FileTagsColumns;
import com.github.bloodriot.fields.FilesColumns;
import com.github.bloodriot.fields.TagsColumns;
import com.github.bloodriot.pojo.FileDetails;
import com.github.bloodriot.pojo.FileDetailsList;
import com.github.bloodriot.pojo.Tag;
import com.github.bloodriot.pojo.Tags;
import com.github.bloodriot.protocol.FileDetailsRequest;
import com.github.bloodriot.protocol.FileListRequest;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.JDBC;

import java.sql.*;

@DatabaseConnector
public class SQLLite extends IDatabaseConnection {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Connection connection;
    private static final String SQLEXCEPTIONMESSAGE = "Caught a SQLException: %s";
    private boolean isConnected = false;

    @Inject
    public SQLLite(String jdbcString) {
        if (!connect(jdbcString)) {
            throw new IllegalStateException("Unable to connect to database.");
        }
    }

    @Override
    public boolean connect(String jdbcString) {
        if (null != connection) {
            return true;
        }

        try {
            DriverManager.registerDriver(new JDBC());
        } catch (SQLException e) {
            logger.error("Class not found", e);
            return false;
        }

        try {
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Attempting to connect to database: %s", jdbcString));
            }
            connection = DriverManager.getConnection(jdbcString);
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Connected to database: %s", jdbcString));
            }
        } catch (Exception error) {
            logger.error("Caught exception while attempting to connect to the database.", error);
            return false;
        }
        isConnected = true;
        return true;
    }

    @Override
    public FileDetails populateTags(FileDetails fileDetails) {
        if (null == fileDetails) {
            return null;
        }

        String query = "SELECT `tags`.`tag` FROM `tags`" +
                " LEFT JOIN `file_tags` ON `tags`.`id` = `file_tags`.`tag_id`" +
                " WHERE `file_tags`.`file_id` = ?";
        try ( PreparedStatement statement = connection.prepareStatement(query) ) {
            statement.setInt(1, fileDetails.fileId);
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Fetching TagsColumns: Query => %s", query.replace("?", String.valueOf(fileDetails.fileId))));
            }
            try ( ResultSet resultSet = statement.executeQuery() ) { //NOSONAR This should still be readable.
                fileDetails.tags.clear();
                while (resultSet.next()) {
                    String tag = resultSet.getString(1);
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Found tag: %s", tag));
                    }
                    fileDetails.tags.add(tag);
                }
            }
            catch (SQLException error) {
                    logger.error(SQLEXCEPTIONMESSAGE, error);
            }
        } catch (SQLException error) {
            logger.error(SQLEXCEPTIONMESSAGE, error);
        }
        return fileDetails;
    }

    @Override
    public Tags getTags(int fileId) {
        Tags tags = new Tags();

        String query = "SELECT `tags`.`tag`, `tags`.`id` FROM `tags`" +
                " LEFT JOIN `file_tags` ON `tags`.`id` = `file_tags`.`tag_id`" +
                " WHERE `file_tags`.`file_id` = ?";
        try ( PreparedStatement statement = connection.prepareStatement(query) ) {
            statement.setInt(1, fileId);
            if (logger.isInfoEnabled()) {
                logger.info(
                        String.format(
                                "Fetching Tags assigned to file: Query => %s",
                                query.replace("?", String.valueOf(fileId))
                        )
                );
            }

            try ( ResultSet resultSet = statement.executeQuery() ) { //NOSONAR This should still be readable.
                while (resultSet.next()) {
                    String tagName = resultSet.getString(TagsColumns.TAG);
                    int tagId = resultSet.getInt(TagsColumns.ID);

                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Found tag: %s", tagName));
                    }
                    Tag tag = new Tag();
                    tag.tagName = tagName;
                    tag.tagId = tagId;
                    tags.contents.add(tag);
                }
            }
            catch (SQLException error) {
                logger.error(SQLEXCEPTIONMESSAGE, error);
            }
        } catch (SQLException error) {
            logger.error(SQLEXCEPTIONMESSAGE, error);
        }
        return tags;
    }

    @Override
    public Tags getTags() {
        Tags tags = new Tags();

        String query = "SELECT `tags`.`id`, `tags`.`tag` FROM `tags`";
        try ( PreparedStatement statement = connection.prepareStatement(query) ) {
            if (logger.isInfoEnabled()) {
                logger.info(String.format("Fetching All TagsColumns: Query => %s", query));
            }
            try ( ResultSet resultSet = statement.executeQuery() ) { //NOSONAR This should still be readable.
                while (resultSet.next()) {
                    Tag tag = new Tag();
                    tag.tagId = resultSet.getInt(TagsColumns.ID);
                    tag.tagName = resultSet.getString(TagsColumns.TAG);
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Found tag: %s", tag.toJson(true)));
                    }
                    tags.contents.add(tag);
                }
            }
            catch (SQLException error) {
                logger.error(SQLEXCEPTIONMESSAGE, error);
            }
        } catch (SQLException error) {
            logger.error(SQLEXCEPTIONMESSAGE, error);
        }
        return tags;
    }

    @Override
    public boolean addTag(String tag) {
        if (Strings.isNullOrEmpty(tag) || !isConnected) {
            return false;
        }

        String query = "INSERT INTO `tags` (`tag`) VALUES (?)";

        try ( PreparedStatement statement = connection.prepareStatement(query) ) {
            statement.setString(1, tag);
            return statement.execute();
        } catch (SQLException error) {
            logger.error(SQLEXCEPTIONMESSAGE, error);
            return false;
        }
    }

    @Override
    public boolean addTagToFile(int fileId, int tagId) {
        if (!isConnected) {
            return false;
        }

        String query = "INSERT INTO `file_tags` (`file_id`, `tag_id`) VALUES (?, ?)";

        try ( PreparedStatement statement = connection.prepareStatement(query) ) {
            statement.setInt(1, fileId);
            statement.setInt(2, tagId);
            return statement.execute();
        } catch (SQLException error) {
            logger.info("false");
            logger.error(SQLEXCEPTIONMESSAGE, error);
            return false;
        }
    }

    @Override
    public boolean removeTag(int tagId) {
        if (!isConnected) {
            return false;
        }

        String query = "DELETE FROM `tags` WHERE `id` = ?";

        try ( PreparedStatement statement = connection.prepareStatement(query) ) {
            statement.setInt(1, tagId);
            return statement.execute();
        } catch (SQLException error) {
            logger.error(SQLEXCEPTIONMESSAGE, error);
            return false;
        }
    }

    @Override
    public boolean removeTagFromFile(int fileId, int tagId) {
        if (!isConnected) {
            return false;
        }

        String query = "DELETE FROM `file_tags` WHERE `file_id` = ? AND `tag_id` = ?";

        try ( PreparedStatement statement = connection.prepareStatement(query) ) {
            statement.setInt(1, fileId);
            statement.setInt(2, tagId);
            return statement.execute();
        } catch (SQLException error) {
            logger.info("false");
            logger.error(SQLEXCEPTIONMESSAGE, error);
            return false;
        }
    }

    @Override
    public FileDetailsList getFileList(FileListRequest fileListRequest) {
        FileDetailsList fileDetailsList = new FileDetailsList();
        if (null == fileListRequest || !isConnected) {
            return fileDetailsList;
        }

        String query = "SELECT `id`, `name`, `type`, `duration`, `date`, `path`, `preview` FROM `files`;";

        try ( PreparedStatement statement = connection.prepareStatement(query) ) {
            try ( ResultSet resultSet = statement.executeQuery() ) { //NOSONAR This should still be readable.
                while (resultSet.next()) {
                    FileDetails fileDetails = new FileDetails();
                    fileDetails.fileId = resultSet.getInt(FilesColumns.ID);
                    fileDetails.fileName = resultSet.getString(FilesColumns.NAME);
                    fileDetails.fileType = resultSet.getString(FilesColumns.TYPE);
                    fileDetails.fileDuration = resultSet.getInt(FilesColumns.DURATION);
                    fileDetails.fileDate = resultSet.getString(FilesColumns.DATE);
                    fileDetails.filePath = resultSet.getString(FilesColumns.PATH);
                    fileDetails.filePreviewSrc = resultSet.getString(FilesColumns.PREVIEW);
                    fileDetails = populateTags(fileDetails);
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Found file: %s", fileDetails.toJson(true)));
                    }
                    fileDetailsList.files.add(fileDetails);
                }
            }
            catch (SQLException error) {
                logger.error(SQLEXCEPTIONMESSAGE, error);
            }
        } catch (SQLException error) {
            logger.error(SQLEXCEPTIONMESSAGE, error);
        }
        return fileDetailsList;
    }

    @Override
    public FileDetails getFileDetails(FileDetailsRequest fileDetailsRequest) {
        FileDetails fileDetails = new FileDetails();
        if (null == fileDetailsRequest || !isConnected) {
            return fileDetails;
        }

        String query = "SELECT `file_id`, `name`, `type`, `duration`, `date`, `path`, `preview` " +
                "FROM `files` " +
                "WHERE `files`.`file_id` = ?";

        try ( PreparedStatement statement = connection.prepareStatement(query) ) {
            statement.setInt(1, fileDetailsRequest.getFileId());
            if (logger.isInfoEnabled()) {
                logger.info(
                        String.format(
                                "Executing Query: %s",
                                query.replace("?", String.valueOf(fileDetailsRequest.getFileId()))
                        )
                );
            }
            try ( ResultSet resultSet = statement.executeQuery() ) { //NOSONAR This should still be readable.
                while (resultSet.next()) {
                    fileDetails.fileId = resultSet.getInt(FilesColumns.ID);
                    fileDetails.fileName = resultSet.getString(FilesColumns.NAME);
                    fileDetails.fileType = resultSet.getString(FilesColumns.TYPE);
                    fileDetails.fileDuration = resultSet.getInt(FilesColumns.DURATION);
                    fileDetails.fileDate = resultSet.getString(FilesColumns.DATE);
                    fileDetails.filePath = resultSet.getString(FilesColumns.PATH);
                    fileDetails.filePreviewSrc = resultSet.getString(FilesColumns.PREVIEW);
                    fileDetails = populateTags(fileDetails);
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Found file: %s", fileDetails.toJson(true)));
                    }
                }
            }
            catch (SQLException error) {
                logger.error(SQLEXCEPTIONMESSAGE, error);
            }
        } catch (SQLException error) {
            logger.error(SQLEXCEPTIONMESSAGE, error);
        }
        return fileDetails;
    }

    @Override
    public boolean updateFileDetails(FileDetails fileDetails) {
        if (null == fileDetails || !isConnected) {
            if (null == fileDetails) {
                logger.error("FileDetails can not be null");
                return false;
            }
            return false;
        }

        String query = "UPDATE `files` SET" +
                " `name` = ?" +
                " `type` = ?" +
                " `duration` = ?" +
                " `date` = ?" +
                " `path` = ?" +
                " `preview` = ?" +
                " WHERE `files`.`file_id` = ?";

        try ( PreparedStatement statement = connection.prepareStatement(query) ) {
            statement.setString(1, fileDetails.fileName);
            statement.setString(2, fileDetails.fileType);
            statement.setString(3, String.valueOf(fileDetails.fileDuration));
            statement.setString(4, fileDetails.fileDate);
            statement.setString(5, fileDetails.filePath);
            statement.setString(6, fileDetails.filePreviewSrc);

            if (logger.isInfoEnabled()) {
                logger.info(String.format("Updating file: %s", fileDetails.toJson(true)));
            }

            statement.executeUpdate();
            return true;
        } catch (SQLException error) {
            logger.error(SQLEXCEPTIONMESSAGE, error);
        }
        return true;
    }

    @Override
    public boolean deleteFile(String fileId) {
        if (null == fileId || Strings.isNullOrEmpty(fileId) || !isConnected) {
            if (null == fileId) {
                logger.error("fileId can not be null");
                return false;
            } else {
                if (logger.isErrorEnabled()) {
                    logger.error(
                            String.format(
                                    "Database is connected: %s - FileId is null or empty: %s",
                                    String.valueOf(isConnected),
                                    Strings.isNullOrEmpty(fileId)
                            )
                    );
                }
                return false;
            }
        }

        String query = "DELETE FROM `files` WHERE `file_id` = ?";

        try ( PreparedStatement statement = connection.prepareStatement(query) ) {
            statement.setString(1, fileId);

            if (logger.isInfoEnabled()) {
                logger.info(String.format("Executing Query: %s", query.replace("?", fileId)));
            }

            try { //NOSONAR This should still be readable.
                return statement.execute();
            }
            catch (SQLException error) {
                logger.error(SQLEXCEPTIONMESSAGE, error);
            }
        } catch (SQLException error) {
            logger.error(SQLEXCEPTIONMESSAGE, error);
        }
        return true;
    }

    @Override
    public void close() {
        if (null != connection) {
            try {
                connection.close();
            } catch (SQLException error) {
                logger.error(SQLEXCEPTIONMESSAGE, error);
            }
        }
    }
}
