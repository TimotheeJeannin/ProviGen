package com.tjeannin.provigen.test;

import java.lang.reflect.Field;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;

import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.Id;

public abstract class ExtendedProviderTestCase<T extends ProviGenProvider> extends ProviderTestCase2<T> {

	public ExtendedProviderTestCase(Class<T> providerClass, String providerAuthority) {
		super(providerClass, providerAuthority);
	}

	@Override
	protected void setUp() throws Exception {
		getContext().deleteDatabase("ProviGenDatabase");
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		getContext().deleteDatabase("ProviGenDatabase");
		super.tearDown();
	}

	/**
	 * @param uri The {@link Uri} where to count rows.
	 * @return The number of rows matching the given {@link Uri}.
	 */
	protected int getRowCount(Uri uri) {
		Cursor cursor = getMockContentResolver().query(uri, null, "", null, "");
		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	/**
	 * @param contractClass The contract class to build {@link ContentValues} for.
	 * @return A {@link ContentValues} object with: <br>
	 *         <ul>
	 *         <li>"blob" for a column of {@link Type#BLOB}</li>
	 *         <li>"aText" for a column of {@link Type#TEXT}</li>
	 *         <li>3 for a column of {@link Type#INTEGER}</li>
	 *         <li>4.58 for a column of {@link Type#REAL}</li>
	 *         </ul>
	 *         except for the id column.
	 */
	@SuppressWarnings("rawtypes")
	protected ContentValues getContentValues(Class contractClass) {

		ContentValues contentValues = new ContentValues();
		try {
			Field[] fields = contractClass.getFields();
			for (Field field : fields) {

				Column column = field.getAnnotation(Column.class);
				Id id = field.getAnnotation(Id.class);
				if (column != null && id == null) {

					String columnName = (String) field.get(null);
					String columnType = column.value();

					if (columnType.equals(Type.BLOB)) {
						contentValues.put(columnName, "blob");
					} else if (columnType.equals(Type.INTEGER)) {
						contentValues.put(columnName, 3);
					} else if (columnType.equals(Type.REAL)) {
						contentValues.put(columnName, 4.58);
					} else if (columnType.equals(Type.TEXT)) {
						contentValues.put(columnName, "aText");
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return contentValues;
	}
}
