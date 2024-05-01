package com.pandapulsestudios.api.Interface;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(PCAutoTabs.class)
public @interface PCAutoTab {
    public int pos();
}