package com.example.andrey.lastfmapp.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Field;

public class TestHelper {

    public static void setField(
            @NonNull Object object, @NonNull String fieldName, @Nullable Object valueToSet) {
        Class<?> aClass = object.getClass();
        do {
            try {
                Field field = aClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, valueToSet);
                return;
            } catch (NoSuchFieldException e) {
                aClass = aClass.getSuperclass();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Can't access field: " + aClass + "." + fieldName, e);
            }
        } while (aClass != null);
        throw new RuntimeException("Can't find field: " + object.getClass() + "." + fieldName);
    }
}
