package com.giftandgo.rest.api.validator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IpApiSourceTest {

    @Test
    void verifyHappyPath() {
        Source<String> source = IpApiSource.getProductionIpApiSource();

        String actual = source.load("24.48.0.1");

        assertThat(actual).isEqualTo("{\"status\":\"success\",\"countryCode\":\"CA\",\"isp\":\"Le Groupe Videotron Ltee\",\"org\":\"Videotron Ltee\"}");
    }
}
