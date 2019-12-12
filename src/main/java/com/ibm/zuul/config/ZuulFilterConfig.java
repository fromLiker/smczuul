package com.ibm.zuul.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author PingXue
 *
 */
@Configuration
public class ZuulFilterConfig {
	
	  @Bean
	  public PreFilter preFilter() {
		  return new PreFilter();
	  }

}
