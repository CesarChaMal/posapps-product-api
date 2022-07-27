package io.posapps.product

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import groovy.json.JsonOutput
import groovy.transform.Memoized
import groovy.util.logging.Log4j
import io.posapps.product.model.Request
import io.posapps.product.model.Response
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration
import org.springframework.boot.autoconfigure.cloud.CloudAutoConfiguration
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration
import org.springframework.boot.autoconfigure.data.cassandra.CassandraRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.data.ldap.LdapDataAutoConfiguration
import org.springframework.boot.autoconfigure.data.ldap.LdapRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration
import org.springframework.boot.autoconfigure.data.solr.SolrRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.elasticsearch.jest.JestAutoConfiguration
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration
import org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration
import org.springframework.boot.autoconfigure.hazelcast.HazelcastJpaDependencyAutoConfiguration
import org.springframework.boot.autoconfigure.integration.IntegrationAutoConfiguration
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration
import org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration
import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapAutoConfiguration
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration
import org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration
import org.springframework.boot.autoconfigure.mobile.DeviceDelegatingViewResolverAutoConfiguration
import org.springframework.boot.autoconfigure.mobile.DeviceResolverAutoConfiguration
import org.springframework.boot.autoconfigure.mobile.SitePreferenceAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration
import org.springframework.boot.autoconfigure.reactor.ReactorAutoConfiguration
import org.springframework.boot.autoconfigure.security.FallbackWebSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2AutoConfiguration
import org.springframework.boot.autoconfigure.sendgrid.SendGridAutoConfiguration
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration
import org.springframework.boot.autoconfigure.social.FacebookAutoConfiguration
import org.springframework.boot.autoconfigure.social.LinkedInAutoConfiguration
import org.springframework.boot.autoconfigure.social.SocialWebAutoConfiguration
import org.springframework.boot.autoconfigure.social.TwitterAutoConfiguration
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration
import org.springframework.boot.autoconfigure.web.*
import org.springframework.boot.autoconfigure.webservices.WebServicesAutoConfiguration
import org.springframework.boot.autoconfigure.websocket.WebSocketAutoConfiguration
import org.springframework.boot.autoconfigure.websocket.WebSocketMessagingAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.support.SpringBootServletInitializer
import org.springframework.context.ApplicationContext
import org.springframework.core.env.AbstractEnvironment
import static org.springframework.core.env.AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME

@Log4j
@SpringBootApplication
@EntityScan(basePackages = "io.posapps")
@EnableAutoConfiguration(exclude = [ActiveMQAutoConfiguration, ArtemisAutoConfiguration, BatchAutoConfiguration,
        CacheAutoConfiguration, CassandraAutoConfiguration, CassandraDataAutoConfiguration, CloudAutoConfiguration,
        CassandraRepositoriesAutoConfiguration, DeviceDelegatingViewResolverAutoConfiguration, DeviceResolverAutoConfiguration,
        DispatcherServletAutoConfiguration, ElasticsearchAutoConfiguration, ElasticsearchDataAutoConfiguration,
        ElasticsearchRepositoriesAutoConfiguration, EmbeddedLdapAutoConfiguration, EmbeddedMongoAutoConfiguration,
        EmbeddedServletContainerAutoConfiguration, ErrorMvcAutoConfiguration, FacebookAutoConfiguration,
        FallbackWebSecurityAutoConfiguration, FreeMarkerAutoConfiguration, GsonAutoConfiguration,
        H2ConsoleAutoConfiguration, HazelcastAutoConfiguration, HazelcastJpaDependencyAutoConfiguration,
        HttpEncodingAutoConfiguration, HttpMessageConvertersAutoConfiguration, HypermediaAutoConfiguration,
        IntegrationAutoConfiguration, JacksonAutoConfiguration, JestAutoConfiguration, JmsAutoConfiguration,
        JndiConnectionFactoryAutoConfiguration, JndiDataSourceAutoConfiguration, JooqAutoConfiguration, KafkaAutoConfiguration,
        LdapAutoConfiguration, LdapDataAutoConfiguration, LdapRepositoriesAutoConfiguration, LinkedInAutoConfiguration,
        LiquibaseAutoConfiguration, MailSenderAutoConfiguration, MailSenderValidatorAutoConfiguration,
        MessageSourceAutoConfiguration, MongoAutoConfiguration, MongoDataAutoConfiguration, MongoRepositoriesAutoConfiguration,
        MultipartAutoConfiguration, MustacheAutoConfiguration, Neo4jDataAutoConfiguration, Neo4jRepositoriesAutoConfiguration,
        OAuth2AutoConfiguration, RabbitAutoConfiguration, ReactorAutoConfiguration, RedisAutoConfiguration,
        RedisRepositoriesAutoConfiguration, RepositoryRestMvcAutoConfiguration, SendGridAutoConfiguration,
        ServerPropertiesAutoConfiguration, SessionAutoConfiguration, SitePreferenceAutoConfiguration, SocialWebAutoConfiguration,
        SolrAutoConfiguration, SolrRepositoriesAutoConfiguration, SpringApplicationAdminJmxAutoConfiguration,
        SpringDataWebAutoConfiguration, ThymeleafAutoConfiguration, TwitterAutoConfiguration, ValidationAutoConfiguration,
        WebMvcAutoConfiguration, WebServicesAutoConfiguration, WebSocketAutoConfiguration, WebSocketMessagingAutoConfiguration,
        XADataSourceAutoConfiguration
])

//class Application implements RequestHandler<Map, Response> {
class Application extends SpringBootServletInitializer implements RequestHandler<Map, Response> {

    private final DATA_SOURCE_URL = "spring.datasource.url"
    private final DATA_SOURCE_USERNAME = "spring.datasource.username"
    private final DATA_SOURCE_PASSWORD = "spring.datasource.password"

    static void main(String[] args) {
        Application.newInstance().getApplicationContext(args)
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }

    @Memoized
    ApplicationContext getApplicationContext(String[] args = []) {
        return SpringApplication.run(Application, args)
    }

    @Override
    Response handleRequest(Map input, Context context) {
        log.debug("request input: ${JsonOutput.toJson(input)}")
        log.debug("request context: ${JsonOutput.toJson(context)}")
        final Request request = new Request(input, context)
        if (request.profile()) {
            log.debug("Setting Spring profile: ${request.profile()}")
            System.setProperty(ACTIVE_PROFILES_PROPERTY_NAME, request.profile())
            System.setProperty(DATA_SOURCE_URL, request.dataSourceUrl())
            System.setProperty(DATA_SOURCE_USERNAME, request.dataSourceUsername())
            System.setProperty(DATA_SOURCE_PASSWORD, request.dataSourcePassword())
        }
        def routingService = getApplicationContext().getBean(Dispatcher)
        def response = routingService.dispatch(request)
        log.debug("response: $response")
        return response
    }

}
