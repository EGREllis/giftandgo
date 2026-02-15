package com.giftandgo.rest.api;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;
import static com.github.tomakehurst.wiremock.client.WireMock.*;


@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableWireMock(@ConfigureWireMock(name="api", port=8082))
class RestApiApplicationTests {
	@LocalServerPort
	private int localPort;

	@InjectWireMock("api")
	WireMockServer mockIpApiService;

	TestRestTemplate restTemplate = new TestRestTemplate();

	@Test
	void contextLoads() {
	}

	@Test
	void validationFailureResponseTest() {
		mockIpApiService.stubFor(
				get("/json/0.0.0.0?fields=status,countryCode,isp,org")
				.willReturn(ok("{\"status\":\"fail\"}"))
		);

		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Forwarded-For", "0.0.0.0");
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String,Object> body = new LinkedMultiValueMap<>();
		body.add("input", new FileSystemResource("./src/main/resources/EntryFile.txt"));

		HttpEntity<MultiValueMap<String,Object>> entity = new HttpEntity<>(body, headers);

		ResponseEntity<Resource> response = restTemplate.postForEntity(
				createUrlWithPort("/process"), entity, Resource.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(403));
	}

	@Test
	void validationBlackListCountryResponseTest() {
		mockIpApiService.stubFor(
				get("/json/0.0.0.0?fields=status,countryCode,isp,org")
						.willReturn(ok("{\"status\":\"success\",\"countryCode\":\"CN\",\"isp\":\"Le Groupe Videotron Ltee\",\"org\":\"Videotron Ltee\"}"))
		);

		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Forwarded-For", "0.0.0.0");
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String,Object> body = new LinkedMultiValueMap<>();
		body.add("input", new FileSystemResource("./src/main/resources/EntryFile.txt"));

		HttpEntity<MultiValueMap<String,Object>> entity = new HttpEntity<>(body, headers);

		ResponseEntity<Resource> response = restTemplate.postForEntity(
				createUrlWithPort("/process"), entity, Resource.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(403));
	}

	@Test
	void validationBlackListIspResponseTest() {
		mockIpApiService.stubFor(
				get("/json/0.0.0.0?fields=status,countryCode,isp,org")
						.willReturn(ok("{\"status\":\"success\",\"countryCode\":\"CA\",\"isp\":\"AWS\",\"org\":\"Videotron Ltee\"}"))
		);

		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Forwarded-For", "0.0.0.0");
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String,Object> body = new LinkedMultiValueMap<>();
		body.add("input", new FileSystemResource("./src/main/resources/EntryFile.txt"));

		HttpEntity<MultiValueMap<String,Object>> entity = new HttpEntity<>(body, headers);

		ResponseEntity<Resource> response = restTemplate.postForEntity(
				createUrlWithPort("/process"), entity, Resource.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(403));
	}

	@Test
	void validationSuccessNoBlackListResponseTest() throws Exception {
		mockIpApiService.stubFor(
				get("/json/0.0.0.0?fields=status,countryCode,isp,org")
						.willReturn(ok("{\"status\":\"success\",\"countryCode\":\"CA\",\"isp\":\"Le Groupe Videotron Ltee\",\"org\":\"Videotron Ltee\"}"))
		);

		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Forwarded-For", "0.0.0.0");
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String,Object> body = new LinkedMultiValueMap<>();
		body.add("input", new FileSystemResource("./src/main/resources/EntryFile.txt"));

		HttpEntity<MultiValueMap<String,Object>> entity = new HttpEntity<>(body, headers);

		ResponseEntity<Resource> response = restTemplate.postForEntity(
				createUrlWithPort("/process"), entity, Resource.class);

		assertThat(response.getStatusCode())
				.isEqualTo(HttpStatusCode.valueOf(200));
		assertThat(response.getBody().getContentAsString(Charset.defaultCharset()))
				.isEqualTo("[{\"name\":\"John Smith\",\"transport\":\"Rides A Bike\",\"topSpeed\":12.1},{\"name\":\"Mike Smith\",\"transport\":\"Drives an SUV\",\"topSpeed\":95.5},{\"name\":\"Jenny Walters\",\"transport\":\"Rides A Scooter\",\"topSpeed\":15.3}]");
	}

	private String createUrlWithPort(String uri) {
		return "http://localhost:"+localPort+uri;
	}

}
