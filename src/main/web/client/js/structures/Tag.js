/*
 * ==========================================================
 * File: Tag
 * Project: new
 * Author: Jason Creswell (Bloodriot)
 * Copyright: 2018 - 2018 <COMPANY NAME>
 * ==========================================================
 * 
 * TODO: Populate a description of what this file is used for
 */

class Tag {
    withId(id) {
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
}