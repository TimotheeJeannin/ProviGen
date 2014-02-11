package com.tjeannin.provigen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.net.Uri;

import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.ContentUri;
import com.tjeannin.provigen.annotation.Contract;
import com.tjeannin.provigen.annotation.Id;
import com.tjeannin.provigen.annotation.Index;
import com.tjeannin.provigen.annotation.NotNull;
import com.tjeannin.provigen.annotation.Unique;
import com.tjeannin.provigen.exceptions.IndexException;
import com.tjeannin.provigen.exceptions.InvalidContractException;
import com.tjeannin.provigen.utils.ContractHelper;
import com.tjeannin.provigen.utils.IndexInformation;

class ContractHolder {

	private int version;
	private String authority;
	private String idField;
	private String tableName;
	private final List<DatabaseField> databaseFields = new ArrayList<DatabaseField>(0);
	private final List<IndexInformation> indexInformation = new ArrayList<IndexInformation>(0);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ContractHolder(Class contractClass) throws InvalidContractException {

		Contract contract = (Contract) contractClass.getAnnotation(Contract.class);
		if (contract != null) {
			version = contract.version();
		} else {
			throw new InvalidContractException("The given class does not have a @Contract annotation.");
		}

		final Field[] fields = contractClass.getFields();
		for (final Field field : fields) {
			final ContentUri contentUri = field.getAnnotation(ContentUri.class);
			if (contentUri != null) {
				if (authority != null || tableName != null) {
					throw new InvalidContractException("A contract can not have several @ContentUri.");
				}
				try {
					final Uri uri = (Uri) field.get(null);
					authority = uri.getAuthority();
					tableName = uri.getLastPathSegment();
				} catch (final IllegalAccessException e) {
					throw new InvalidContractException("Getting information from @ContentUri annotation failed", e);
				}
			} else {
				final String columnName;
				try {
					columnName = (String) field.get(null);
				} catch (final IllegalAccessException e) {
					throw new InvalidContractException(String.format("Could not read from field %s", field.getName()), e);
				}

				final Id id = field.getAnnotation(Id.class);
				if (id != null) {
					if (idField != null) {
						throw new InvalidContractException("A contract can not have several fields annotated with @Id.");
					} else {
						idField = columnName;
					}
				}

				final Column column = field.getAnnotation(Column.class);
				if (column != null) {
					final DatabaseField databaseField = new DatabaseField(columnName, column.value());

					final Unique unique = field.getAnnotation(Unique.class);
					if (unique != null) {
						databaseField.getConstraints().add(new Constraint(Constraint.Type.UNIQUE, unique.value()));
					}

					final NotNull notNull = field.getAnnotation(NotNull.class);
					if (notNull != null) {
						databaseField.getConstraints().add(new Constraint(Constraint.Type.NOT_NULL, notNull.value()));
					}

					final Index index = field.getAnnotation(Index.class);
					if (index != null) {
						try {
							ContractHelper.addIndexInformation(indexInformation, databaseField.getName(), index);
						} catch (final IndexException e) {
							throw new InvalidContractException("Index information creation failed.", e);
						}
					}
					databaseFields.add(databaseField);
				}
			}
		}

		if (authority == null || tableName == null) {
			throw new InvalidContractException("The contract is missing a @ContentUri.");
		}
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
		return Collections.unmodifiableList(databaseFields);
	}

	public List<IndexInformation> getIndexInformation() {
		return Collections.unmodifiableList(indexInformation);
	}
}
