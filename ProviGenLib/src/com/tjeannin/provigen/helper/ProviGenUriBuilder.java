package com.tjeannin.provigen.helper;

import android.net.Uri;

import com.tjeannin.provigen.model.JoinEntity;

/**
 * Created by Dre on 07.04.2016.
 *
 */
public class ProviGenUriBuilder {

    public enum JoinType {
        INNER_JOIN, LEFT_OUTER_JOIN, CROSS_JOIN
    }

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

    /**
     * Create URI for [inner, left outer, cross] join query
     *
     * @param joinType Type of join - INNER_JOIN, LEFT_OUTER_JOIN or CROSS_JOIN
     * @param uriTable1 URI of left table
     * @param joinEntities List of join entities (right table)
     * @return URI
     */
    public static Uri joinUri(JoinType joinType, Uri uriTable1, JoinEntity ... joinEntities) {
        if(joinEntities.length == 0) {
            throw new IllegalArgumentException("joinEntities is empty");
        }

        boolean isCrossJoin = false;
        String joinPath;
        switch (joinType) {
            case INNER_JOIN:
                joinPath = "inner_join/";
                break;

            case LEFT_OUTER_JOIN:
                joinPath = "left_outer_join/";
                break;

            case CROSS_JOIN:
                joinPath = "cross_join/";
                isCrossJoin = true;
                break;

            default:
                joinPath = null;
                break;
        }

        Uri joinUri = Uri.withAppendedPath(uriTable1, joinPath);
        for(int i = 0; i < joinEntities.length; i++) {
            String pathSegment;
            if(isCrossJoin) {
                pathSegment = joinEntities[i].getTableName();
            } else {
                pathSegment = joinEntities[i].getTableName() + "/" + joinEntities[i].getLeftField() + ":" + joinEntities[i].getRightField();
            }
            if(i < joinEntities.length - 1) {
                pathSegment += "/";
            }
            joinUri = Uri.withAppendedPath(joinUri, pathSegment);
        }

        return joinUri;
    }
}
