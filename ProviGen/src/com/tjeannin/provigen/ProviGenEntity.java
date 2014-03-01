package com.tjeannin.provigen;

import java.lang.reflect.Field;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.tjeannin.provigen.annotation.ContentUri;
import com.tjeannin.provigen.annotation.Entity;
import com.tjeannin.provigen.annotation.Persist;
import com.tjeannin.provigen.exception.InvalidEntityException;

/**
 * Base class for a {@link ProviGenProvider} {@link Entity}.<br/>
 * You should <b>annotate implementations of this class with the {@link Entity} annotation</b> to specify the matching {@link ContentUri}.
 */
public class ProviGenEntity {

    @Persist(columnName = ProviGenBaseContract._ID)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Uri getUri() {
        return Uri.withAppendedPath(getContentUri(), String.valueOf(id));
    }

    private Uri getContentUri() {
        Entity entity = this.getClass().getAnnotation(Entity.class);
        return Uri.parse(entity.contentUri());
    }

    public void insert(ContentResolver contentResolver) {
        Uri insertedUri = contentResolver.insert(getContentUri(), getContentValues());
        id = Integer.valueOf(insertedUri.getLastPathSegment());
    }

    public void delete(ContentResolver contentResolver) {
        contentResolver.delete(getUri(), null, null);
    }

    public void update(ContentResolver contentResolver) {
        contentResolver.update(getUri(), getContentValues(), null, null);
    }

    public ProviGenEntity() {
    }

    public ProviGenEntity(Cursor cursor) {

        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            Persist persist = field.getAnnotation(Persist.class);
            if (persist != null) {
                try {
                    field.setAccessible(true);
                    Class<?> type = field.getType();
                    if (type.equals(Boolean.class) || type.getName().equals("boolean")) {
                        field.setBoolean(this, cursor.getInt(cursor.getColumnIndex(persist.columnName())) == 1);
                    } else if (type.equals(Byte.class) || type.getName().equals("byte")) {
                        field.setByte(this, cursor.getBlob(cursor.getColumnIndex(persist.columnName()))[0]);
                    } else if (type.equals(byte[].class)) {
                        field.set(this, cursor.getBlob(cursor.getColumnIndex(persist.columnName())));
                    } else if (type.equals(Double.class) || type.getName().equals("double")) {
                        field.setDouble(this, cursor.getDouble(cursor.getColumnIndex(persist.columnName())));
                    } else if (type.equals(Float.class) || type.getName().equals("float")) {
                        field.setFloat(this, cursor.getFloat(cursor.getColumnIndex(persist.columnName())));
                    } else if (type.equals(Integer.class) || type.getName().equals("int")) {
                        field.setInt(this, cursor.getInt(cursor.getColumnIndex(persist.columnName())));
                    } else if (type.equals(Long.class) || type.getName().equals("long")) {
                        field.setLong(this, cursor.getLong(cursor.getColumnIndex(persist.columnName())));
                    } else if (type.equals(Short.class) || type.getName().equals("short")) {
                        field.setShort(this, cursor.getShort(cursor.getColumnIndex(persist.columnName())));
                    } else if (type.equals(String.class)) {
                        field.set(this, cursor.getString(cursor.getColumnIndex(persist.columnName())));
                    } else if (type.equals(Uri.class)) {
                        field.set(this, Uri.parse(cursor.getString(cursor.getColumnIndex(persist.columnName()))));
                    } else {
                        new InvalidEntityException("The " + field.getName() + " method return type is not supported.").printStackTrace();
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    public ContentValues getContentValues() {

        ContentValues contentValues = new ContentValues();

        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            Persist persist = field.getAnnotation(Persist.class);
            if (persist != null) {

                Object value = new Object();
                try {
                    field.setAccessible(true);
                    value = field.get(this);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                if (value != null) {
                    if (value instanceof Boolean) {
                        contentValues.put(persist.columnName(), (Boolean) value);
                    } else if (value instanceof Byte) {
                        contentValues.put(persist.columnName(), (Byte) value);
                    } else if (value instanceof byte[]) {
                        contentValues.put(persist.columnName(), (byte[]) value);
                    } else if (value instanceof Double) {
                        contentValues.put(persist.columnName(), (Double) value);
                    } else if (value instanceof Float) {
                        contentValues.put(persist.columnName(), (Float) value);
                    } else if (value instanceof Integer) {
                        contentValues.put(persist.columnName(), (Integer) value);
                    } else if (value instanceof Long) {
                        contentValues.put(persist.columnName(), (Long) value);
                    } else if (value instanceof Short) {
                        contentValues.put(persist.columnName(), (Short) value);
                    } else if (value instanceof String) {
                        contentValues.put(persist.columnName(), (String) value);
                    } else if (value instanceof Uri) {
                        contentValues.put(persist.columnName(), ((Uri) value).toString());
                    } else {
                        new InvalidEntityException("The " + field.getName() + " method return type is not supported.").printStackTrace();
                    }
                }
            }
        }
        return contentValues;
    }
}