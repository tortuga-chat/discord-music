package com.pedrovh.tortuga.discord.music.infrastructure.config;

import io.jsondb.JsonDBTemplate;

public class JsonDB {

    private static final String DB_FILES_LOCATION = "./db";
    private static final String BASE_SCAN_PACKAGE = "com.pedrovh.tortuga.discord.music.persistence.model";
    private static final JsonDBTemplate db = new JsonDBTemplate(DB_FILES_LOCATION, BASE_SCAN_PACKAGE);

    private JsonDB(){}

    public static JsonDBTemplate getJsonDB() {
        return db;
    }

}
