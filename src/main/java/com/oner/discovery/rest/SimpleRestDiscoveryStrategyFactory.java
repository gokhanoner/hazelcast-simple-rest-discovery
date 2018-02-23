package com.oner.discovery.rest;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryStrategyFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class SimpleRestDiscoveryStrategyFactory implements DiscoveryStrategyFactory {

    @Override
    public Class<? extends DiscoveryStrategy> getDiscoveryStrategyType() {
        return SimpleRestDiscoveryStrategy.class;
    }

    @Override
    public DiscoveryStrategy newDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger, Map<String, Comparable> properties) {
        return new SimpleRestDiscoveryStrategy(properties);
    }

    @Override
    public Collection<PropertyDefinition> getConfigurationProperties() {
        final SimpleRestDiscoveryConfiguration[] props = SimpleRestDiscoveryConfiguration.values();
        final ArrayList<PropertyDefinition> definitions = new ArrayList<PropertyDefinition>(props.length);
        for (SimpleRestDiscoveryConfiguration prop : props) {
            definitions.add(prop.getDefinition());
        }
        return definitions;
    }
}
