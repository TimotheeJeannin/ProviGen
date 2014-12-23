package com.tjeannin.provigen.model;

public class ContractField {

    public String name;
    public String type;
    public String defaultValue;

    public ContractField(String name, String type, String defaultValue) {
        super();
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }
}
