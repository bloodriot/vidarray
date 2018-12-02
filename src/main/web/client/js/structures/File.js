/*
 * ==========================================================
 * File: File.js
 * Project: new
 * Author: Jason Creswell (Bloodriot)
 * Copyright: 2018 - 2018 <COMPANY NAME>
 * ==========================================================
 * 
 * TODO: Populate a description of what this file is used for
 */

class File {
    withId(Id) {
        this.id = id;
        return this;
    }
    getId() {
        return this.id;
    }

    withName(name) {
        this.name = name;
        return this;
    }
    getName() {
        return this.name;
    }

    withTags(tags) {
        this.tags = tags;
        return this;
    }
    getTags() {
        return this.tags;
    }

    withDuration(duration) {
        this.duration = duration;
        return this;
    }
    getDuration() {
        return this.duration;
    }

    withDate(date) {
        this.date = date;
        return this;
    }
    getDate() {
        return this.date;
    }

    withPath(path) {
        this.path = path;
        return this;
    }

    withPreviewSrc(previewSrc) {
        this.previewSrc = previewSrc;
        return this;
    }
    getPreviewSrc() {
        return this.previewSrc;
    }

    withType(type) {
        this.type = type;
        return this;
    }
    getType() {
        return this.type;
    }
}