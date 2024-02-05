package com.github.bloodriot;

import com.github.bloodriot.annotations.DatabaseConnector;
import com.github.bloodriot.controllers.HealthCheck;
import com.github.bloodriot.controllers.VidArray;
import com.github.bloodriot.database.IDatabaseConnection;
import com.github.bloodriot.protocol.HealthGrpc;
import com.github.bloodriot.protocol.HealthGrpcJerseyResource;
import com.github.bloodriot.protocol.VidArrayGrpc;
import com.github.bloodriot.protocol.VidArrayGrpcJerseyResource;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import java.util.Set;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    private int grpcPort = 9099;
    private int httpPort = 9098;
    private String hostName = "0.0.0.0";
    private Injector injector;
    private IDatabaseConnection connection;
    private Properties properties;

    public static void main(String[] args) throws Exception {
        App app = new App();
        app.startup();
    }

    public void startup() throws IOException, InterruptedException {
        // Load the properties file.
        properties = new Properties();
        properties.load(new FileReader("App.properties"));
        logger.error(properties.getProperty("databaseConnectionString"));
        hostName = properties.getProperty("host-address");
        grpcPort = Integer.valueOf(properties.getProperty("grpc-port", String.valueOf(9099)));
        httpPort = Integer.valueOf(properties.getProperty("httpPort", String.valueOf(9098)));
        initialize();

        // Start gRPC server
        Server grpcServer = startGrpcServer();

        //Start http server
        HttpServer restServer = startServer();
        if (logger.isInfoEnabled()) {
            logger.info(String.format("HTTP Server started at %s:%s", hostName, httpPort));
        }

        // Wait until terminated
        grpcServer.awaitTermination();
        restServer.shutdown();
    }

    public void initialize() {
        registerWorkers();

        // Setup DI
        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(IDatabaseConnection.class).toInstance(connection);
            }
        });
    }

    /*
        This likely seems like overkill, but I wanted to be able to use any database / storage backend.
        This allows you to create plugins by adding the @DatabaseConnector annotation
     */

    /**
     * Look for a registered database connectors and instantiates them.
     */
    public void registerWorkers() {
        // Fetch a list of classes that are tied to @RegexHandler
        Reflections reflections = new Reflections("com.github.bloodriot");
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(DatabaseConnector.class);

        // Create a new injector to feed the databaseConnectionString to the registered database handler.
        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(String.class).toInstance(properties.getProperty("databaseConnectionString"));
            }
        });

        // Register the classes found to the event bus.
        for (Class<?> entry : annotatedClasses) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Looking at: %s", entry.getName()));
            }

            Object worker = injector.getInstance(entry);
            if (IDatabaseConnection.class.isAssignableFrom(entry)) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Registering: %s", entry.getName()));
                }
                connection = (IDatabaseConnection) worker;
            }
        }
    }

    /**
     * Starts the grpc server
     * @return A GRPC Server object
     * @throws IOException when unable to start the server.
     */
    private Server startGrpcServer() throws IOException {
        Server server = ServerBuilder
                .forPort(grpcPort)
                .addService(injector.getInstance(VidArray.class))
                .addService(injector.getInstance(HealthCheck.class))
                .build();
        server.start();

        if (logger.isInfoEnabled()) {
            logger.info(String.format("gRPC Server started at %s:%s", hostName, grpcPort));
        }

        return server;
    }

    /**
     * Starts the GRPC -> HTTP bridge.
     * @return A HttpServer object
     */
    private HttpServer startServer() {
        //Add resource that will handle http requests
        ResourceConfig resourceConfig = new ResourceConfig();
        ManagedChannel managedChannel = ManagedChannelBuilder
                .forTarget(String.format("%s:%s", hostName, grpcPort))
                .usePlaintext(true)
                .build();

        VidArrayGrpcJerseyResource vidArrayGrpcJerseyResource = new VidArrayGrpcJerseyResource(
                VidArrayGrpc.newStub(managedChannel)
        );

        HealthGrpcJerseyResource healthGrpcJerseyResource = new HealthGrpcJerseyResource(
                HealthGrpc.newStub(managedChannel)
        );

        resourceConfig.register(vidArrayGrpcJerseyResource);
        resourceConfig.register(healthGrpcJerseyResource);

        return GrizzlyHttpServerFactory.createHttpServer(
                URI.create(String.format("http://%s:%d/", hostName, httpPort)),
                resourceConfig
        );
    }
}