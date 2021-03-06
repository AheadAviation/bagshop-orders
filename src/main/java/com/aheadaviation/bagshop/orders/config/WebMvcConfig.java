package com.aheadaviation.bagshop.orders.config;


import com.aheadaviation.bagshop.orders.middleware.HTTPMonitoringInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.MappedInterceptor;

@Configuration
public class WebMvcConfig {
  @Bean
  HTTPMonitoringInterceptor httpMonitoringInterceptor() {
    return new HTTPMonitoringInterceptor();
  }

  @Bean
  public MappedInterceptor myMappedInterceptor(HTTPMonitoringInterceptor interceptor) {
    return new MappedInterceptor(new String[]{"/**"}, interceptor);
  }
}
