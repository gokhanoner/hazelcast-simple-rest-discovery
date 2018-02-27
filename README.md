# Simple HTTP (REST) Based Discovery for Hazelcast

Provides a HTTP REST endpoint based discovery strategy for Hazlecast 3.6+ enabled applications.

 * [Requirements](#requirements)
 * [Install](#install)
 * [REST Service Details](#service)
 * [Usage](#usage)

 
 ## <a id="requirements"></a>Requirements
 
 * Java 6+
 * [Hazelcast 3.6+](https://hazelcast.org/)
 * A REST endpoint to return member list (see details in [REST Service Details](#service) section)
 
 ## <a id="install"></a>Install
 
You can use [JitPack](https://jitpack.io/#gokhanoner/hazelcast-simple-rest-discovery/) to add the library to your project.

## <a id="service"></a>REST Service Details

This discovery plugin expect user to expose a REST service to return members.

Expected REST output is `List<Map<Sting, String>`.

In the map, user must return a `private-address` property.
If `public-address` is used, it can be defined as well.

Expected address format:  `ip/host-name[:port]`

Valid address examples: `127.0.0.1`, `my-member-node:5701`

Example output (JSON Formatted):

```
[
    {
        "private-address": "10.0.0.1:5703"
    },
    {
        "private-address": "10.0.0.2"
    }
]
```


## <a id="usage"></a>Usage

- Add this project as a dependency to your project.
- Disable join over multicast TCP/IP and AWS by setting the `enabled` attribute of the related tags to `false`.
- Enable Discovery SPI by adding `hazelcast.discovery.enabled` property to your config.

Following are example declarative and programmatic configuration snippets:

```xml
 <hazelcast>
   ...
  <properties>
     <property name="hazelcast.discovery.enabled">true</property>
  </properties>
  <network>
    ...
    <join>
        <tcp-ip enabled="false"></tcp-ip>
        <multicast enabled="false"/>
        <aws enabled="false" />
        <discovery-strategies>
            <!-- class equals to the DiscoveryStrategy not the factory! -->
            <discovery-strategy enabled="true" class="com.oner.discovery.rest.SimpleRestDiscoveryStrategy">
                <properties>
                   <property name="endpoint-url">http://localhost:8080/my-discovery-service</property>
                   <property name="request-params">my-request-params</property>
                   <property name="private-address-property">private-address</property>
                   <property name="public-address-property">public-address</property>
                   <property name="conn-timeout">10000</property>
                   <property name="read-timeout">10000</property>
                   <property name="member-port">5701</property>
                </properties>
            </discovery-strategy>
        </discovery-strategies>
    </join>
  </network>
 </hazelcast>
```

Here are the definitions of the properties

* `endpoint-url`: REST service url.
* `request-params`: Request parameters, if any. Format: `param1=val1&param2=val2`. It is optional. When using multiple parameters, `&` need to be escaped as `&amp;`. 
* `private-address-property`: Name of the private address property. Default value `private-address`. It is optional.
* `public-address-property`: Name of the public address property. Default value `public-address`. It is optional.
* `conn-timeout`: Connection timeout to REST service, in milis. Default value `10000`. It is optional.
* `read-timeout`: Read timeout to REST service, in milis. Default value `10000`. It is optional.
* `member-port`: If address doesn't contain port information or if you've members on different ports than 5701. It is optional.
