package com.tjeannin.provigen;

import android.net.Uri;
import com.tjeannin.provigen.annotation.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ContractHolder {

	private String authority;
	private String idField;
	private String tableName;
	private List<DatabaseField> databaseFields;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ContractHolder(Class contractClass) throws InvalidContractException {

		databaseFields = new ArrayList<DatabaseField>();

		Field[] fields = contractClass.getFields();
		for (Field field : fields) {

			ContentUri contentUri = field.getAnnotation(ContentUri.class);
			if (contentUri != null) {
				if (authority != null || tableName != null) {
					throw new InvalidContractException("A contract can not have several @ContentUri.");
				}
				try {
					Uri uri = (Uri) field.get(null);
					authority = uri.getAuthority();
					tableName = uri.getLastPathSegment();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			Id id = field.getAnnotation(Id.class);
			if (id != null) {
				if (idField != null) {
					throw new InvalidContractException("A contract can not have several fields annotated with @Id.");
				}
				try {
					idField = (String) field.get(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			Column column = field.getAnnotation(Column.class);
			if (column != null) {
				try {
					DatabaseField databaseField = new DatabaseField((String) field.get(null), column.value());
					databaseFields.add(databaseField);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (authority == null || tableName == null) {
			throw new InvalidContractException("The contract is missing a @ContentUri.");
		}
	}

	public String getAuthority() {
		return authority;
	}

	public String getIdField() {
		return idField;
	}

	public String getTable() {
		return tableName;
	}

	public List<DatabaseField> getFields() {
		return databaseFields;
	}
}
