package com.tjeannin.provigen.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tjeannin.provigen.Type;

@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
	Type value();
}
