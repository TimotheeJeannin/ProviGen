# ProviGen [![Build Status](https://travis-ci.org/TimotheeJeannin/ProviGen.png?branch=master)](https://travis-ci.org/TimotheeJeannin/ProviGen) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.provigen/ProviGen-lib/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.provigen/ProviGen-lib/)

Easily make a [ContentProvider] from a [ContractClass].    

## Setup

* Follow the [installation guide](https://github.com/TimotheeJeannin/ProviGen/wiki/Installation-Guide).

* Annotate your ContractClass.

```java
public interface MyContract extends ProviGenBaseContract {

	@Column(Type.INTEGER)
	public static final String MY_INT_COLUMN = "int";

	@Column(Type.TEXT)
	public static final String MY_STRING_COLUMN = "string";

	@ContentUri
	public static final Uri CONTENT_URI = Uri.parse("content://com.myapp/table_name");
}
```

* Extend the ProviGenProvider.

```java
public class MyContentProvider extends ProviGenProvider {

    private static Class[] contracts = new Class[]{MyContract.class};

    @Override
    public SQLiteOpenHelper openHelper(Context context) {
        return new ProviGenOpenHelper(getContext(), "dbName", null, 1, contracts);
    }

    @Override
    public Class[] contractClasses() {
        return contracts;
    }
}
```

* Add your provider in your manifest.

```xml
<provider
    android:name="com.myapp.MyContentProvider"
    android:authorities="com.myapp" >
</provider>
```

* You're done.

## Usage

You can make the usual insert, update, delete and query using a [ContentResolver].    
For example querying a single row boils down to:
```java
getContentResolver().query(	
	Uri.withAppendedPath(MyContract.CONTENT_URI, myId),
	null, "", null, "");
```
or 
```java
getContentResolver().query(
	MyContract.CONTENT_URI, null, 
	MyContract._ID + " = ? ", new String[]{ myId }, "");
```

## Features

### Table creation and contract upgrades

ProviGen comes with an implementation of the [SQLiteOpenHelper] called `ProviGenOpenHelper`.
This default implementation will

* automatically create the needed tables on the first application launch
* automatically add missing columns every time the database version increases

### Notifications and observers

ProviGen fully supports the uri notification mechanism.   
You can safely use it with [CursorLoader]s and [ContentObserver]s.

### Custom SQLiteOpenHelper

You can provide your own implementation of the [SQLiteOpenHelper] for initial population, complex contract upgrades
or anything else database related you want to achieve.

```java
public class MyContentProvider extends ProviGenProvider {

    @Override
    public Class[] contractClasses() {
        return new Class[]{MyContract.class};
    }

    @Override
    public SQLiteOpenHelper openHelper(Context context) {
    
        return new SQLiteOpenHelper(getContext(), "databaseName", null, 1) {
        
            @Override
            public void onCreate(SQLiteDatabase database) {
                // Automatically creates table and needed columns.
                new TableBuilder(MyContract.class).createTable(database);

                // Do initial population here.
            }

            @Override
            public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
                // Automatically adds new columns.
                TableUpdater.addMissingColumns(database, MyContract.class);

                // Anything else related to database upgrade should be done here.
            }
        };
    }
}
```

### Data constraint

You can apply a `UNIQUE` or a `NOT_NULL` constraint to a column using the appropriate `TableBuilder` methods.

```java
new TableBuilder(MyContract.class)
        .addConstraint(MyContract.MY_INT, Constraint.UNIQUE, OnConflict.ABORT)
        .addConstraint(MyContract.MY_STRING, Constraint.NOT_NULL, OnConflict.IGNORE)
        .createTable(database);
```

## License

This content is released under the MIT License.

[SQLiteOpenHelper]: https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper.html

[ContentObserver]: https://developer.android.com/reference/android/database/ContentObserver.html

[CursorLoader]: http://developer.android.com/reference/android/content/CursorLoader.html

[ContentProvider]: https://developer.android.com/reference/android/content/ContentProvider.html

[ContractClass]: http://developer.android.com/guide/topics/providers/content-provider-basics.html#ContractClasses

[ContentResolver]: https://developer.android.com/reference/android/content/ContentResolver.html
