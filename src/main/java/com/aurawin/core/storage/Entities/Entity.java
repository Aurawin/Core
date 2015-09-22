package com.aurawin.core.storage.entities;

public interface Entity<T> {
    public void Created(T);
    public void Deleted(T);
}
