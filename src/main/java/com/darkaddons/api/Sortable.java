package com.darkaddons.api;

import java.util.Comparator;

public interface Sortable<T, S extends Sortable<T, S>> {
    String getDisplayName();

    Comparator<T> getSortRule();

    S next();

    S prev();
}
