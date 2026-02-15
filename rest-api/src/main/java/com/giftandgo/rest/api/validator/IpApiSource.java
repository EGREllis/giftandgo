package com.giftandgo.rest.api.validator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class IpApiSource implements Source<String> {
    private static final int READ_TIMEOUT = 5000;
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final String IP_QUERY_FORMAT = "/json/%1$s?fields=status,countryCode,isp,org";

    private final String stem;

    private IpApiSource(String stem) {
        this.stem = stem;
    }

    public static IpApiSource getProductionIpApiSource() {
        return new IpApiSource("http://ip-api.com");
    }

    public static IpApiSource getWireMockIpApiSource(int wireMockPort) {
        return new IpApiSource("http://localhost:%1$d".formatted(wireMockPort));
    }

    @Override
    public String load(String ipAddress) {
        String result;
        String completeUrl = stem + IP_QUERY_FORMAT.formatted(ipAddress);
        try {
            URL url = new URL(completeUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ( (inputLine = in.readLine()) != null ) {
                content.append(inputLine);
            }
            result = content.toString();
        } catch(MalformedURLException mfue) {
            throw new RuntimeException("Bad Url: "+completeUrl, mfue);
        } catch(IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return result;
    }
}
