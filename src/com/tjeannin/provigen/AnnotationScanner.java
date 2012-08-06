package com.tjeannin.provigen;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import android.content.Context;
import android.util.Log;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

public class AnnotationScanner {

	private Context mContext;

	public AnnotationScanner(Context mContext) {
		super();
		this.mContext = mContext;
	}

	private static final String TAG = AnnotationScanner.class.getSimpleName();

	@SuppressWarnings("rawtypes")
	public <T extends Annotation> List<Class> getClassesAnnotatedWith(Class<T> theAnnotation) {

		// In theory, the class loader is not required to be a PathClassLoader
		PathClassLoader classLoader = (PathClassLoader) Thread.currentThread().getContextClassLoader();
		ArrayList<Class> candidates = new ArrayList<Class>();

		DexFile dexFile = null;
		try {
			String sourceDir = mContext.getApplicationInfo().sourceDir;
			dexFile = new DexFile(sourceDir);
		} catch (Exception e) {
			Log.e(TAG, "Failed to get DexFile", e);
		}

		Enumeration<String> entries = dexFile.entries();
		while (entries.hasMoreElements()) {

			// Each entry is a class name, like "foo.bar.MyClass"
			String entry = entries.nextElement();

			// Load the class
			Class<?> entryClass = dexFile.loadClass(entry, classLoader);
			if (entryClass != null && entryClass.getAnnotation(theAnnotation) != null) {
				Log.d(TAG, "Found: " + entryClass.getName());
				candidates.add(entryClass);
			}
		}

		return candidates;
	}

}
