# ProviGen

Easily make a [ContentProvider] from a [ContractClass].    

## Installation

* Put the ProviGen [jar] in your `libs` folder or add ProviGen as a library project.

[jar]: https://github.com/TimotheeJeannin/ProviGen/tree/master/ProviGenDownloads

* Annotate your ContractClass.

```java
@Contract(version = 1)
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

	public MyContentProvider() throws InvalidContractException {
		super(MyContract.class);
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

### Multiple contact classes

You can use ProviGen with several [ContractClass] just by passing an array of contract classes to the ProviGenProvider constructor.
```java
public class MyContentProvider extends ProviGenProvider {

	public MyContentProvider() throws InvalidContractException {
		super(new Class[] { FirstContract.class, SecondContract.class });
	}
}
```
ProviGen will create a table for each contract class.     
The table name will be the last path segment of the contract's content uri.

### Notifications and observers

ProviGen fully supports the uri notification mechanism.   
You can safely use it with [CursorLoader]s and [ContentObserver]s.

### Initial population

ProviGen will automatically create the needed table for you.    
Initial population can be done overriding the `onCreateDatabase` method.
```java
public class MyContentProvider extends ProviGenProvider {

	public MyContentProvider() throws InvalidContractException {
		super(MyContract.class);
	}

	@Override
	public void onCreateDatabase(SQLiteDatabase database) {
		// Automatically creates table and needed columns.
		super.onCreateDatabase(database); 

		// If needed, populate table here.
	}
}
```
If you want to create the table yourself, just don't call `super.onCreateDatabase(database)`.

### Contract upgrades

If you increase the version of a contract class, ProviGen will automatically add missing columns for you.    
Any other changes should be done overriding the `onUpgradeDatabase` method.
```java
public class MyContentProvider extends ProviGenProvider {

	public MyContentProvider() throws InvalidContractException {
		super(MyContract.class);
	}

	@Override
	public void onUpgradeDatabase(SQLiteDatabase database, int oldVersion, int newVersion) {
		// Automatically adds new columns.
		super.onUpgradeDatabase(database, oldVersion, newVersion);

		// Anything else related to database upgrade should be done here. 
	}
}
```
If you want to add missing columns yourself, just don't call `super.onUpgradeDatabase(database, oldVersion, newVersion)`

### Data constraint

You can apply a `UNIQUE` constraint to a column using the `@Unique` annotation.

```java
@Unique(OnConflict.REPLACE)
@Column(Type.INTEGER)
public static final String MY_INT = "my_int";
```

You can apply a `NOT_NULL` constraint to a column using the `@NotNull` annotation.

```java
@NotNull(OnConflict.ABORT)
@Column(Type.INTEGER)
public static final String MY_INT = "my_int";
```

`CHECK` constraint is not supported yet.

## License

This content is released under the MIT License.

[ContentObserver]: https://developer.android.com/reference/android/database/ContentObserver.html

[CursorLoader]: http://developer.android.com/reference/android/content/CursorLoader.html

[ContentProvider]: https://developer.android.com/reference/android/content/ContentProvider.html

[ContractClass]: http://developer.android.com/guide/topics/providers/content-provider-basics.html#ContractClasses

[ContentResolver]: https://developer.android.com/reference/android/content/ContentResolver.html
