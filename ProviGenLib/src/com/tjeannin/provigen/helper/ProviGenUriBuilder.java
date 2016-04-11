package com.tjeannin.provigen.helper;

import android.net.Uri;

/**
 * Created by Dre on 07.04.2016.
 *
 */
public class ProviGenUriBuilder {

    /**
     * Create content URI for contract class
     * @param authority authority
     * @param tableName tableName
     * @return Content URI
     */
    public static Uri contentUri(String authority, String tableName) {
        return contentUri(authority, tableName, null);
    }

    /**
     * Create content URI with database name for contract class
     * @param authority authority
     * @param tableName tableName
     * @param dbName database name
     * @return Content URI
     */
    public static Uri contentUri(String authority, String tableName, String dbName) {
        return Uri.parse("content://" + authority + "/" + (dbName != null ? dbName + "/" : "") + tableName);
    }
}
