package com.giftandgo.rest.api.converter;

public interface Converter<I, O> {
    O convert(I input);
}
