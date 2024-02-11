package com.pedrovh.tortuga.discord.music.infrastructure.config;

import io.jsondb.JsonDBTemplate;

public class Jsondb {

    private static final String dbFilesLocation = "./db";
    private static final String baseScanPackage = "com.pedrovh.tortuga.discord.music.persistence.model";
    private static final JsonDBTemplate jsondb = new JsonDBTemplate(dbFilesLocation, baseScanPackage);

    public static JsonDBTemplate getJsonDB() {
        return jsondb;
    }

}
