package com.vfi.android.domain.entities.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Param created on 2/27/18 10:37 AM
 *
 * @author yunlongg1@verifone.com
 * @version 1.0
 * description:
 * TODO
 */

public class Param {
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface TLV {
        String tag();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ID {
        String key();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Default{
        String String() default "";
        int Integer() default 0;
        boolean Boolean() default false;
        long Long() default 0;
    }
}
