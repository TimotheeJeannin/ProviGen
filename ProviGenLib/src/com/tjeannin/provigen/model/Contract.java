package com.tjeannin.provigen.model;

import android.net.Uri;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.ContentUri;
import com.tjeannin.provigen.annotation.ForeignKey;
import com.tjeannin.provigen.annotation.Id;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Contract {

    private String authority;
    private String dbName;
    private String tableName;
    private List<ContractField> contractFields;
    private Map<String, Boolean> idFieldMap;
    private List<ForeignKeyConstraint> foreignKeys;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Contract(Class contractClass) {

        idFieldMap = new HashMap<>();
        contractFields = new ArrayList<>();
        foreignKeys = new ArrayList<>();

        Field[] fields = contractClass.getFields();
        for (Field field : fields) {

            ContentUri contentUri = field.getAnnotation(ContentUri.class);
            if (contentUri != null) {
                try {
                    Uri uri = (Uri) field.get(null);
                    authority = uri.getAuthority();
                    tableName = uri.getLastPathSegment();

                    List<String> pathSegments = uri.getPathSegments();
                    if(pathSegments.size() == 2) {
                        dbName = pathSegments.get(0);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                try {
                    String idField = (String) field.get(null);
                    idFieldMap.put(idField, id.autoincrement());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
            if(foreignKey != null) {
                try {
                    ForeignKeyConstraint foreignKeyConstraint = null;
                    if(foreignKey.table() != null && foreignKey.column() != null) {
                        foreignKeyConstraint = new ForeignKeyConstraint((String) field.get(null), foreignKey.table(), foreignKey.column());
                    }
                    if(foreignKeyConstraint != null) {
                        foreignKeys.add(foreignKeyConstraint);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                try {
                    contractFields.add(new ContractField((String) field.get(null), column.value()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getAuthority() {
        return authority;
    }

    public String getTable() {
        return tableName;
    }

    public String getDbName() {
        return dbName;
    }

    public List<String> getIdFields() {
        return new ArrayList<>(idFieldMap.keySet());
    }

    public List<ContractField> getFields() {
        return contractFields;
    }

    public boolean isAutoincrement(String field) {
        return idFieldMap.get(field);
    }

    public List<ForeignKeyConstraint> getForeignKeys() {
        return foreignKeys;
    }
}
