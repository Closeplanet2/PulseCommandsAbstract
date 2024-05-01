package com.pandapulsestudios.api.Interface;

import com.pandapulsestudios.api.Enum.TabType;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(PCTabs.class)
public @interface PCTab {
    public int pos();
    public TabType type();
    public String data();
}