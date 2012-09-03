package com.tjeannin.provigen.test;

import android.net.Uri;

import com.tjeannin.provigen.ProviGenProvider;

public class AlarmContentProvider extends ProviGenProvider {

	public AlarmContentProvider() {
		super(AlarmContract.class);
	}

	public static class AlarmContract {

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

		public static final Uri CONTENT_URI = Uri
				.parse("content://com.tjeannin.provigen/alarm");
	}

}
