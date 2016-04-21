package com.tjeannin.provigen.model;

import android.net.Uri;

/**
 * This model needed to execute a join-query
 *
 * Created by Dre on 07.04.2016.
 */
public class JoinEntity {

    private Uri contentUri;
    private String leftField;
    private String rightField;

    public JoinEntity() { }

    /**
     * For cross join.
     * @param contentUri URI of right join table
     */
    public JoinEntity(Uri contentUri) {
        this.contentUri = contentUri;
    }

    /**
     * @param contentUri URI of right join table
     * @param leftField Name of left field
     * @param rightField Name of right field
     */
    public JoinEntity(Uri contentUri, String leftField, String rightField) {
        this.contentUri = contentUri;
        this.leftField = leftField;
        this.rightField = rightField;
    }

    public Uri getContentUri() {
        return contentUri;
    }

    public void setContentUri(Uri contentUri) {
        this.contentUri = contentUri;
    }

    public String getLeftField() {
        return leftField;
    }

    public void setLeftField(String leftField) {
        this.leftField = leftField;
    }

    public String getRightField() {
        return rightField;
    }

    public void setRightField(String rightField) {
        this.rightField = rightField;
    }

    public String getTableName() {
        return contentUri.getLastPathSegment();
    }

    @Override
    public String toString() {
        return "JoinEntity{" +
                "contentUri=" + contentUri +
                ", leftField='" + leftField + '\'' +
                ", rightField='" + rightField + '\'' +
                '}';
    }
}
