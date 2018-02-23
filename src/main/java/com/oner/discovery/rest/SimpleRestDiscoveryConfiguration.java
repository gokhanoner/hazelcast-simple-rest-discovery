package com.oner.discovery.rest;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.config.properties.PropertyTypeConverter;
import com.hazelcast.config.properties.SimplePropertyDefinition;
import com.hazelcast.config.properties.ValidationException;
import com.hazelcast.config.properties.ValueValidator;

import static com.hazelcast.config.properties.PropertyTypeConverter.*;

public enum SimpleRestDiscoveryConfiguration {

    /**
     * URL for the service endpoint that'll return member addresses
     */
    ENDPOINT_URL("endpoint-url", STRING),

    /**
     * Additional parameters to be sent
     */
    REQUEST_PARAMS("request-params", STRING, true),

    /**
     * Connection timeout in milis
     */
    CONNECTION_TIMEOUT("conn-timeout", INTEGER, true),

    /**
     * Read timeout in milis
     */
    READ_TIMEOUT("read-timeout", INTEGER, true),

    /**
     * Response property that'll contain the private addres of the member
     */
    PRIVATE_ADDRESS("private-address-property", STRING, true),

    /**
     * Response property that'll contain the public addres of the member
     */
    PUBLIC_ADDRESS("public-address-property", STRING, true),

    /**
     * Default port for hosts. Will be used if address don't contains the port information
     */
    MEMBER_PORT("member-port", INTEGER, true, new PortValueValidator());

    SimpleRestDiscoveryConfiguration(String key, PropertyTypeConverter typeConverter, boolean optional, ValueValidator validator) {
        this.propertyDefinition = new SimplePropertyDefinition(key, optional, typeConverter, validator);
    }

    SimpleRestDiscoveryConfiguration(String key, PropertyTypeConverter typeConverter, boolean optional) {
        this.propertyDefinition = new SimplePropertyDefinition(key, optional, typeConverter);
    }

    SimpleRestDiscoveryConfiguration(String key, PropertyTypeConverter typeConverter) {
        this.propertyDefinition = new SimplePropertyDefinition(key, false, typeConverter);
    }

    private final PropertyDefinition propertyDefinition;

    public PropertyDefinition getDefinition() {
        return propertyDefinition;
    }

    /**
     * Validator for valid network ports
     */
    private static class PortValueValidator implements ValueValidator<Integer> {

        private static final int MIN_PORT = 0;
        private static final int MAX_PORT = 65535;

        /**
         * Returns a validation
         *
         * @param value the integer to validate
         * @throws com.hazelcast.config.properties.ValidationException if value does not fall in valid port number range
         */
        public void validate(Integer value) throws ValidationException {
            if (value < MIN_PORT) {
                throw new ValidationException("member-port number must be greater 0");
            }
            if (value > MAX_PORT) {
                throw new ValidationException("member-port number must be less or equal to 65535");
            }
        }
    }
}
