package com.github.bloodriot.controllers;

import com.github.bloodriot.database.IDatabaseConnection;
import com.github.bloodriot.pojo.FileDetails;
import com.github.bloodriot.pojo.FileDetailsList;
import com.github.bloodriot.pojo.Tag;
import com.github.bloodriot.pojo.Tags;
import com.github.bloodriot.protocol.*;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import io.grpc.stub.StreamObserver;

public class VidArray extends VidArrayGrpc.VidArrayImplBase {
    private IDatabaseConnection databaseConnection;

    @Inject
    public VidArray(IDatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public FileDetailsResponse buildFileDetailsResponse(FileDetails fileDetails) {
        FileDetailsResponse.Builder responseBuilder = FileDetailsResponse.newBuilder();
        responseBuilder.setFileId(fileDetails.fileId);
        if (!Strings.isNullOrEmpty(fileDetails.fileName)) {
            responseBuilder.setFileName(fileDetails.fileName);
        }
        if (!Strings.isNullOrEmpty(fileDetails.fileDate)) {
            responseBuilder.setFileDate(fileDetails.fileDate);
        }
        if (!Strings.isNullOrEmpty(String.valueOf(fileDetails.fileDuration))) {
            responseBuilder.setFileDuration(String.valueOf(fileDetails.fileDuration));
        }
        if (!Strings.isNullOrEmpty(fileDetails.filePath)) {
            responseBuilder.setFilePath(fileDetails.filePath);
        }
        if (!Strings.isNullOrEmpty(fileDetails.filePreviewSrc)) {
            responseBuilder.setFilePreviewSrc(fileDetails.filePreviewSrc);
        }
        if (!Strings.isNullOrEmpty(fileDetails.fileType)) {
            responseBuilder.setFileType(String.valueOf(fileDetails.fileType));
        }
        if (!fileDetails.tags.isEmpty()) {
            responseBuilder.addAllFileTags(fileDetails.tags);
        }
        return responseBuilder.build();
    }

    /*
     * RPC Operations:
     * getFileDetails - Implemented
     * getFileList - Implemented
     * addFileTag - Implemented
     * removeFileTag - Implemented
     * getTagsList - Implemented
     */

    @Override
    public void getFileDetails(FileDetailsRequest request, StreamObserver<FileDetailsResponse> responseObserver) {
        if (null == request || 0 == request.getFileId()) {
            FileDetailsResponse response = FileDetailsResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        FileDetails details = databaseConnection.getFileDetails(request);
        FileDetailsResponse response = buildFileDetailsResponse(details);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getFileList(FileListRequest request, StreamObserver<FileListResponse> responseObserver) {
        if (null == request) {
            FileListResponse response = FileListResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        FileListResponse.Builder responseBuilder = FileListResponse.newBuilder();
        FileDetailsList details = databaseConnection.getFileList(request);
        int index = 0;
        for (FileDetails fileDetails : details.files) {
            responseBuilder.addFileDetails(index, buildFileDetailsResponse(fileDetails));
            index++;
        }
        responseBuilder.setCursor("test");

        FileListResponse response = responseBuilder.build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void addFileTag(AddTagRequest request, StreamObserver<ModifyTagResponse> responseObserver) {
        if (null == request || 0 == request.getFileId() || 0 == request.getTagId()) {
            ModifyTagResponse response = ModifyTagResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        databaseConnection.addTagToFile(request.getFileId(), request.getTagId());
        Tags tags = databaseConnection.getTags(request.getFileId());
        ModifyTagResponse.Builder responseBuilder = ModifyTagResponse.newBuilder();
        for (Tag tag : tags.contents) {
            responseBuilder.addTags(tag.tagName);
        }
        ModifyTagResponse response = responseBuilder.build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void removeFileTag(RemoveTagRequest request, StreamObserver<ModifyTagResponse> responseObserver) {
        if (null == request || 0 == request.getFileId() || 0 == request.getTagId()) {
            ModifyTagResponse response = ModifyTagResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        databaseConnection.removeTagFromFile(request.getFileId(), request.getTagId());
        Tags tags = databaseConnection.getTags(request.getFileId());
        ModifyTagResponse.Builder responseBuilder = ModifyTagResponse.newBuilder();
        for (Tag tag : tags.contents) {
            responseBuilder.addTags(tag.tagName);
        }
        ModifyTagResponse response = responseBuilder.build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getTagsList(ListTagsRequest request, StreamObserver<ListTagsResponse> responseObserver) {
        Tags tags = databaseConnection.getTags();
        ListTagsResponse.Builder responseBuilder = ListTagsResponse.newBuilder();
        for (Tag tag : tags.contents) {
            responseBuilder.addTag(tag.tagName);
        }

        ListTagsResponse response = responseBuilder.build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}