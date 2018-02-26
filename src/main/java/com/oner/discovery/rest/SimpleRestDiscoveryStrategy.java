package com.oner.discovery.rest;

import com.hazelcast.config.NetworkConfig;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.AbstractDiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;
import com.hazelcast.util.StringUtil;
import com.oner.discovery.rest.util.HTTPUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.hazelcast.util.ExceptionUtil.rethrow;
import static com.oner.discovery.rest.SimpleRestDiscoveryConfiguration.CONNECTION_TIMEOUT;
import static com.oner.discovery.rest.SimpleRestDiscoveryConfiguration.ENDPOINT_URL;
import static com.oner.discovery.rest.SimpleRestDiscoveryConfiguration.MEMBER_PORT;
import static com.oner.discovery.rest.SimpleRestDiscoveryConfiguration.PRIVATE_ADDRESS;
import static com.oner.discovery.rest.SimpleRestDiscoveryConfiguration.PUBLIC_ADDRESS;
import static com.oner.discovery.rest.SimpleRestDiscoveryConfiguration.READ_TIMEOUT;
import static com.oner.discovery.rest.SimpleRestDiscoveryConfiguration.REQUEST_PARAMS;

public class SimpleRestDiscoveryStrategy extends AbstractDiscoveryStrategy {

    private static final ILogger LOGGER = Logger.getLogger(SimpleRestDiscoveryStrategy.class);

    private static final String PREFIX = "discovery.simple-rest";
    private static final String PRIVATE_ADRESS_PROPERTY = "private-address";
    private static final String PUBLIC_ADRESS_PROPERTY = "public-address";
    private static final int DEFAULT_TIMEOUT = 10000;

    private final String endpointUrl;
    private final String requestParams;
    private final String privateAddressProp;
    private final String publicAddressProp;
    private final int memberPort;
    private final int connTimeout;
    private final int readTimeout;


    public SimpleRestDiscoveryStrategy(Map<String, Comparable> properties) {
        super(LOGGER, properties);
        this.endpointUrl = getOrNull(PREFIX, ENDPOINT_URL.getDefinition());
        this.requestParams = getOrNull(PREFIX, REQUEST_PARAMS.getDefinition());
        this.privateAddressProp = getOrDefault(PREFIX, PRIVATE_ADDRESS.getDefinition(), PRIVATE_ADRESS_PROPERTY);
        this.publicAddressProp = getOrDefault(PREFIX, PUBLIC_ADDRESS.getDefinition(), PUBLIC_ADRESS_PROPERTY);
        this.memberPort = getOrDefault(PREFIX, MEMBER_PORT.getDefinition(), NetworkConfig.DEFAULT_PORT);
        this.connTimeout = getOrDefault(PREFIX, CONNECTION_TIMEOUT.getDefinition(), DEFAULT_TIMEOUT);
        this.readTimeout = getOrDefault(PREFIX, READ_TIMEOUT.getDefinition(), DEFAULT_TIMEOUT);
    }

    @Override
    public Iterable<DiscoveryNode> discoverNodes() {
        try {
            return parseResponse(callService());
        } catch (Exception e) {
            throw rethrow(e);
        }
    }

    private List<DiscoveryNode> parseResponse(List<Map<String, String>> response) {
        try {
            List<DiscoveryNode> nodes = new ArrayList<DiscoveryNode>(response.size());
            for (Map<String, String> nodeDetails : response) {
                String privateAddress = nodeDetails.get(privateAddressProp);

                String publicAdress = nodeDetails.get(publicAddressProp);
                publicAdress = StringUtil.isNullOrEmptyAfterTrim(publicAdress) ? privateAddress : publicAdress;

                nodes.add(new SimpleDiscoveryNode(createAddress(privateAddress), createAddress(publicAdress)));
            }

            return nodes;
        } catch (Exception e) {
            throw rethrow(e);
        }
    }

    private Address createAddress(String hostname) throws IOException, URISyntaxException {
        URI uri = new URI("dummy://" + hostname);
        String host = uri.getHost();
        int port = uri.getPort() < 0 ? memberPort : uri.getPort();
        return new Address(host, port);
    }

    // visible for testing
    List<Map<String, String>> callService() throws IOException {
        StringBuilder stringBuilder = new StringBuilder(endpointUrl);
        if (!StringUtil.isNullOrEmptyAfterTrim(requestParams)) {
            stringBuilder.append('?');
            stringBuilder.append(HTTPUtil.escapeParams(requestParams));
        }

        URL url = new URL(stringBuilder.toString());

        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setRequestMethod("GET");
        httpConnection.setConnectTimeout(connTimeout);
        httpConnection.setReadTimeout(readTimeout);
        httpConnection.setRequestProperty("Accept-Charset", "UTF-8");
        httpConnection.connect();
        return HTTPUtil.parseResponse(httpConnection.getInputStream());
    }

}
