package com.giftandgo.rest.api.validator;

public interface Source<T> {
    T load(String ip);
}
