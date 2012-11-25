ProviGen
========

ProviGen allows you to easily generate a [ContentProvider] from a given [ContractClass].

[ContentProvider]: https://developer.android.com/reference/android/content/ContentProvider.html

[ContractClass]: http://developer.android.com/guide/topics/providers/content-provider-basics.html#ContractClasses

How to install and use
----------------------

* Put the ProviGen [jar] in your `libs` folder or add ProviGen as a library project.

[jar]: https://github.com/TimotheeJeannin/ProviGen/downloads

* Annotate your ContractClass as follows:

```java
@Contract(version = 1)
public static class MyContract extends ProviGenBaseContract {

	@Column(type = Type.INTEGER)
	public static final String MY_INT_COLUMN = "int";

	@Column(type = Type.TEXT)
	public static final String MY_STRING_COLUMN = "string";

	@ContentUri
	public static final Uri CONTENT_URI = Uri.parse("content://com.myapp/table_name");
}
```

* Extend the ProviGenProvider as follows:

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

	@Override
	public void onUpgradeDatabase(SQLiteDatabase database, int oldVersion, int newVersion) {
		// Automatically adds new columns.
		super.onUpgradeDatabase(database, oldVersion, newVersion);

		// Anything else related to database upgrade should be done here. 
	}
}
```

* Add your provider in your manifest:

```xml
<provider
    android:name="com.myapp.MyContentProvider"
    android:authorities="com.myapp" >
</provider>
```