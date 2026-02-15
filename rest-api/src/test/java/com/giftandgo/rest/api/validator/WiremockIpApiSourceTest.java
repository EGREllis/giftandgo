package com.giftandgo.rest.api.validator;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.Test;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@EnableWireMock(
        @ConfigureWireMock(name="api", port=8081)
)
class WiremockIpApiSourceTest {
    @InjectWireMock("api")
    WireMockServer mockIpApiService;

    @Test
    void verifyHappyPath() {
        mockIpApiService.stubFor(
                get(urlEqualTo("/json/24.48.0.1?fields=status,countryCode,isp,org"))
                .willReturn(ok("womble")));

        Source<String> source = IpApiSource.getWireMockIpApiSource(8081);

        String actual = source.load("24.48.0.1");

        assertThat(actual).isEqualTo("womble");
    }
}
