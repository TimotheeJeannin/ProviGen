package com.tjeannin.provigen;

import java.util.Calendar;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.SparseBooleanArray;

public class Alarm {

	private int id;
	private int hour;
	private int minute;
	private long snoozeTime;
	private long ringTime;
	private boolean active;
	private boolean snoozed;
	private String name;
	private SparseBooleanArray activeDays;

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

	public static final int[] CALENDAR_WEEK_DAYS = { Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY };

	public static final Uri CONTENT_URI = Uri.parse("content://com.tjeannin.provigen/alarm");

	public Alarm(Cursor cursor) {
		id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
		active = cursor.getInt(cursor.getColumnIndex(COLUMN_ACTIVE)) == 0 ? false : true;
		snoozed = cursor.getInt(cursor.getColumnIndex(COLUMN_SNOOZED)) == 0 ? false : true;
		hour = cursor.getInt(cursor.getColumnIndex(COLUMN_HOUR));
		minute = cursor.getInt(cursor.getColumnIndex(COLUMN_MINUTE));
		snoozeTime = cursor.getLong(cursor.getColumnIndex(COLUMN_SNOOZETIME));
		ringTime = cursor.getLong(cursor.getColumnIndex(COLUMN_RINGTIME));
		name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));

		String activeDaysString = cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVE_DAYS));
		activeDays = new SparseBooleanArray(CALENDAR_WEEK_DAYS.length);
		for (int i = 0; i < CALENDAR_WEEK_DAYS.length; i++) {
			activeDays.put(CALENDAR_WEEK_DAYS[i], (activeDaysString.charAt(i) == '1') ? true : false);
		}
	}

	/**
	 * Creates a new {@link ContentValues} object filled with the <b>current alarm values</b>.
	 * @return A {@link ContentValues} object suited to be used as parameter with the {@link AlarmContentProvider}.
	 */
	public ContentValues getContentValues() {

		StringBuilder days = new StringBuilder();
		for (int day : CALENDAR_WEEK_DAYS) {
			days.append((activeDays.get(day)) ? "1" : "0");
		}

		ContentValues contentValues = new ContentValues();
		contentValues.put(COLUMN_ACTIVE_DAYS, days.toString());
		contentValues.put(COLUMN_HOUR, hour);
		contentValues.put(COLUMN_MINUTE, minute);
		contentValues.put(COLUMN_SNOOZETIME, snoozeTime);
		contentValues.put(COLUMN_RINGTIME, ringTime);
		contentValues.put(COLUMN_ACTIVE, active);
		contentValues.put(COLUMN_SNOOZED, snoozed);
		contentValues.put(COLUMN_NAME, name);
		return contentValues;
	}

	/**
	 * Creates a new {@link ContentValues} object filled with alarm <b>default values</b>.
	 * @return A {@link ContentValues} object suited to be used as parameter with the {@link AlarmContentProvider}.
	 */
	public static ContentValues getDefaultContentValues() {

		ContentValues contentValues = new ContentValues();
		contentValues.put(Alarm.COLUMN_HOUR, 8);
		contentValues.put(Alarm.COLUMN_MINUTE, 30);
		contentValues.put(Alarm.COLUMN_SNOOZETIME, 0);
		contentValues.put(Alarm.COLUMN_RINGTIME, 0);
		contentValues.put(Alarm.COLUMN_ACTIVE, false);
		contentValues.put(Alarm.COLUMN_SNOOZED, false);
		contentValues.put(Alarm.COLUMN_NAME, "");
		contentValues.put(Alarm.COLUMN_ACTIVE_DAYS, "1111111");
		return contentValues;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public long getSnoozeTime() {
		return snoozeTime;
	}

	public void setSnoozeTime(long snoozeTime) {
		this.snoozeTime = snoozeTime;
	}

	public long getRingTime() {
		return ringTime;
	}

	public void setRingTime(long ringTime) {
		this.ringTime = ringTime;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isSnoozed() {
		return snoozed;
	}

	public void setSnoozed(boolean snoozed) {
		this.snoozed = snoozed;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SparseBooleanArray getActiveDays() {
		return activeDays;
	}

	public void setActiveDays(SparseBooleanArray activeDays) {
		this.activeDays = activeDays;
	}
}
