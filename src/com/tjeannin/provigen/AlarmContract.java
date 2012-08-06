package com.tjeannin.provigen;

import android.content.ContentValues;
import android.net.Uri;

public class AlarmContract {

	// Provider contract.
	public static final String TABLE_NAME = "alarms";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_HOUR = "hour";
	public static final String COLUMN_MINUTE = "minute";
	public static final String COLUMN_RINGTIME = "ring_time";
	public static final String COLUMN_SNOOZETIME = "snooze_time";
	public static final String COLUMN_ACTIVE = "active";
	public static final String COLUMN_SNOOZED = "snoozed";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_ACTIVE_DAYS = "active_days";

	public static final Uri CONTENT_URI = Uri.parse("content://com.tjeannin.provigen/alarm");

	/**
	 * Creates a new {@link ContentValues} object filled with alarm <b>default values</b>.
	 * @return A {@link ContentValues} object suited to be used as parameter with the {@link AlarmContentProvider}.
	 */
	public static ContentValues getDefaultContentValues() {

		ContentValues contentValues = new ContentValues();
		contentValues.put(COLUMN_HOUR, 8);
		contentValues.put(COLUMN_MINUTE, 30);
		contentValues.put(COLUMN_SNOOZETIME, 0);
		contentValues.put(COLUMN_RINGTIME, 0);
		contentValues.put(COLUMN_ACTIVE, false);
		contentValues.put(COLUMN_SNOOZED, false);
		contentValues.put(COLUMN_NAME, "");
		contentValues.put(COLUMN_ACTIVE_DAYS, "1111111");
		return contentValues;
	}
}
