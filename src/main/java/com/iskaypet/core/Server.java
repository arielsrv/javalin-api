package com.iskaypet.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.micrometer.MicrometerPlugin;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.DiskSpaceMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.reactivex.rxjava3.core.Observable;
import java.io.File;
import java.util.function.Consumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record Server(Javalin javalin) {

    static final Logger logger = LoggerFactory.getLogger(Server.class);

    static PrometheusMeterRegistry prometheusMeterRegistry = new PrometheusMeterRegistry(
        PrometheusConfig.DEFAULT);
    static MicrometerPlugin micrometerPlugin = new MicrometerPlugin(
        micrometerPluginConfig -> micrometerPluginConfig.registry = prometheusMeterRegistry);


    public static Server create() {
        new ClassLoaderMetrics().bindTo(prometheusMeterRegistry);
        new JvmMemoryMetrics().bindTo(prometheusMeterRegistry);
        new JvmGcMetrics().bindTo(prometheusMeterRegistry);
        new JvmThreadMetrics().bindTo(prometheusMeterRegistry);
        new UptimeMetrics().bindTo(prometheusMeterRegistry);
        new ProcessorMetrics().bindTo(prometheusMeterRegistry);
        new DiskSpaceMetrics(new File(System.getProperty("user.dir"))).bindTo(
            prometheusMeterRegistry);

        ObjectMapper objectMapper = ContainerRegistry.get(ObjectMapper.class);

        return create(config -> {
            config.useVirtualThreads = true;
            config.showJavalinBanner = false;
            config.registerPlugin(micrometerPlugin);
            config.jsonMapper(new CustomJacksonMapper(objectMapper));
        });
    }

    public static Server create(Consumer<JavalinConfig> config) {
        return new Server(Javalin.create(config));
    }

    public <T> void get(String path, Function<Context, Observable<T>> handler) {
        this.javalin.get(path, RxHttpHandler.intercept(handler));
    }

    public void start(int port) {
        this.javalin.get("/metrics",
            ctx -> ctx.contentType("text/plain; version=0.0.4; charset=utf-8")
                .result(prometheusMeterRegistry.scrape()));

        this.javalin.get("/ping",
            ctx -> ctx.contentType("text/plain; version=0.0.4; charset=utf-8")
                .result("pong"));

        logger.info("Starting app ... {}", Config.getStringValue("app.name"));
        this.javalin.start(Config.getStringValue("app.host"), port);
    }
}
