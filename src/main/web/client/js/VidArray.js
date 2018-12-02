/*
 * ==========================================================
 * File: VidArray.js
 * Project: new
 * Author: Jason Creswell (Bloodriot)
 * Copyright: 2018 - 2018 <COMPANY NAME>
 * ==========================================================
 * 
 * TODO: Populate a description of what this file is used for
 */
class VidArray {
    constructor() {
        this.serverPort = 9098;
        this.serverAddress = 'http://127.0.0.1';
        this.apiProvider = this.serverAddress + ':' + this.serverPort;
    }

    // Makes a simple get request and returns the results as a json object.
    getResponse(url) {

        var settings = {
            method: 'GET',
            mode: 'cors'
        }

        return (
            fetch(this.apiProvider + url, settings).then(function(response) {
                if (response.ok) {
                    return response.json;
                }
                throw new Error('Unexpected return when fetching details.');
            })
        );
    }

    // Request file details for a single file.
    // get: "api/v1/files/{fileId}"
    getFileDetails(fileId) {
        return this.getResponse('/api/v1/files/' + fileId);
    }

    // Request file details for multiple files.
    // get: "api/v1/files/"
    getFileList() {
        return this.getResponse('/api/v1/files/');
    }

    // Add a tag to a file
    // put: "api/v1/files/{fileId}/tags"
    addFileTag(fileId, tagId) {
        var body = {
            tagId: tagId
        }

        var settings = {
            method: 'PUT',
            body: JSON.stringify(body),
            mode: 'cors'
        }

        return (
            fetch(this.apiProvider + '/api/v1/files/' + fileId + '/tags', settings).then(function(response) {
                if (response.ok) {
                    return response.json;
                }
                throw new Error('Unexpected return when adding tag.');
            })
        );
    }
    
    // Remove a tag from a file
    // delete: "api/v1/files/{fileId}/tags/"
    removeFileTag(fileId, tagId) {
        var body = {
            tagId: tagId
        }

        var settings = {
            method: 'DELETE',
            body: JSON.stringify(body),
            mode: 'cors'
        }

        return (
            fetch(this.apiProvider + '/api/v1/files/' + fileId + '/tags', settings).then(function(response) {
                if (response.ok) {
                    return response.json;
                }
                throw new Error('Unexpected return when removing tag.');
            })
        );
    }

    // Get a list of all the tags
    // get: "api/v1/tags"
    getTagsList() {
        return this.getResponse('/api/v1/tags/');
    }
}

export {VidArray}