package com.arielsrv.core;

import io.avaje.inject.BeanScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RestClientFactoryTest {

	BeanScope beanScope;
	RestClientFactory factory;

	@BeforeEach
	void setup() {
		beanScope = mock(BeanScope.class);
		when(beanScope.get(RestClient.class, "user"))
			.thenReturn(RestClient.createRestClient("https://gorest.co.in", null));
		when(beanScope.get(RestClient.class, "foo"))
			.thenReturn(RestClient.createRestClient("https://foo.com", null));
		when(beanScope.get(RestClient.class, "notfound"))
			.thenThrow(new IllegalStateException("no bean for RestClient:notfound"));
		factory = new RestClientFactory(beanScope);
	}

	@Test
	void get_returns_client_by_name() {
		RestClient user = factory.get("user");
		RestClient foo = factory.get("foo");
		assertThat(user).isNotNull();
		assertThat(foo).isNotNull();
		assertThat(user).isNotSameAs(foo);
	}

	@Test
	void get_throws_if_name_not_bound() {
		assertThatThrownBy(() -> factory.get("notfound")).isInstanceOf(Exception.class);
	}
}
