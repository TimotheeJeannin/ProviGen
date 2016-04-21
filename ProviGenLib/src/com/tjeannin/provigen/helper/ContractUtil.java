package com.tjeannin.provigen.helper;

/**
 * Created by Dre on 08.04.2016.
 *
 */
public class ContractUtil {

    /**
     * Return full name of field
     * @param table Table
     * @param field Field
     * @return Full name of field
     */
    public static String fullName(String table, String field) {
        return table + "." + field;
    }

    /**
     * Return name of field after join
     * @param table Table
     * @param field Field
     * @return Name of field after join
     */
    public static String joinName(String table, String field) {
        if(table.endsWith("_") && field.startsWith("_")) {
            return table + field.substring(1 , field.length());
        }
        else if(table.endsWith("_") || field.startsWith("_")) {
            return table + field;
        }
        else {
            return table + "_" + field;
        }
    }

}
