package com.darkaddons.api;

public interface MenuOption {
    String getDisplayName();

    MenuOption next();

    MenuOption prev();
}
