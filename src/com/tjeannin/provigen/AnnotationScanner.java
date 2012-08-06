package com.tjeannin.provigen;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import android.util.Log;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

public class AnnotationScanner {

	private static final String TAG = AnnotationScanner.class.getSimpleName();

	@SuppressWarnings("rawtypes")
	public <T extends Annotation> List<Class> getClassesAnnotatedWith(Class<T> theAnnotation) {

		// In theory, the class loader is not required to be a PathClassLoader
		PathClassLoader classLoader = (PathClassLoader) Thread.currentThread().getContextClassLoader();
		Field field = null;
		ArrayList<Class> candidates = new ArrayList<Class>();

		try {
			field = PathClassLoader.class.getDeclaredField("mDexs");
			field.setAccessible(true);
		} catch (Exception e) {

			// nobody promised that this field will always be there
			Log.e(TAG, "Failed to get mDexs field", e);
		}

		DexFile[] dexFile = null;
		try {
			dexFile = (DexFile[]) field.get(classLoader);
		} catch (Exception e) {
			Log.e(TAG, "Failed to get DexFile", e);
		}

		for (DexFile dex : dexFile) {
			Enumeration<String> entries = dex.entries();
			while (entries.hasMoreElements()) {

				// Each entry is a class name, like "foo.bar.MyClass"
				String entry = entries.nextElement();

				// Load the class
				Class<?> entryClass = dex.loadClass(entry, classLoader);
				if (entryClass != null && entryClass.getAnnotation(theAnnotation) != null) {
					Log.d(TAG, "Found: " + entryClass.getName());
					candidates.add(entryClass);
				}
			}
		}

		return candidates;
	}

}
