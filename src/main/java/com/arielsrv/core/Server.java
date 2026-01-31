package com.arielsrv.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.micrometer.MicrometerPlugin;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.redoc.ReDocPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.function.Consumer;
import java.util.function.Function;

public record Server(Javalin javalin) {

	static final Logger logger = LoggerFactory.getLogger(Server.class);

	static final PrometheusMeterRegistry prometheusMeterRegistry = new PrometheusMeterRegistry(
		PrometheusConfig.DEFAULT);
	static final MicrometerPlugin micrometerPlugin = new MicrometerPlugin(
		micrometerPluginConfig -> micrometerPluginConfig.registry = prometheusMeterRegistry);


	@SuppressWarnings("resource")
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

			config.registerPlugin(new OpenApiPlugin(pluginConfig -> {
				pluginConfig.withDefinitionConfiguration((version, definition) -> {
					definition.withInfo(info -> {
						info.title("Javalin API");
						info.version("1.0.0");
						info.description("Documentación OpenAPI generada automáticamente");
					});
				});
				pluginConfig.withDocumentationPath("/openapi");
			}));

			config.registerPlugin(new SwaggerPlugin(pluginConfig -> {
				pluginConfig.setUiPath("/swagger");
				pluginConfig.setDocumentationPath("/openapi");
			}));

			config.registerPlugin(new ReDocPlugin(pluginConfig -> {
				pluginConfig.setUiPath("/redoc");
				pluginConfig.setDocumentationPath("/openapi");
			}));
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
