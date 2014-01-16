package com.tjeannin.provigen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.net.Uri;

import com.tjeannin.provigen.annotation.*;

class ContractHolder {

	private int version;
	private String authority;
	private String idField;
	private String tableName;
	private List<DatabaseField> databaseFields;
	private String conflictResolution;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ContractHolder(Class contractClass) throws InvalidContractException {

		Contract contract = (Contract) contractClass.getAnnotation(Contract.class);
		if (contract != null) {
			version = contract.version();
		} else {
			throw new InvalidContractException("The given class does not have a @Contract annotation.");
		}

		OnConflict onConflict = (OnConflict) contractClass.getAnnotation(OnConflict.class);
		if(onConflict != null){
			conflictResolution = onConflict.value();
		}

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

					Unique unique = field.getAnnotation(Unique.class);
					if (unique != null) {
						if(conflictResolution != null){
							databaseField.getConstraints().add(new Constraint(Constraint.UNIQUE, conflictResolution));
						} else {
							throw new InvalidContractException("Contract is missing an @OnConflict annotation for the @Unique constraint.");
						}
					}

					NotNull notNull = field.getAnnotation(NotNull.class);
					if (notNull != null) {
						if(conflictResolution != null){
							databaseField.getConstraints().add(new Constraint(Constraint.NOT_NULL, conflictResolution));
						} else {
							throw new InvalidContractException("Contract is missing an @OnConflict annotation for the @NotNull constraint.");
						}
					}

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

	public boolean hasColumnConstraint(String constraint) {
		for (DatabaseField field : databaseFields) {
			for (Constraint fieldConstraint : field.getConstraints()) {
				if (fieldConstraint.getType().equals(constraint)) {
					return true;
				}
			}
		}
		return false;
	}

	public int getVersion() {
		return version;
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
