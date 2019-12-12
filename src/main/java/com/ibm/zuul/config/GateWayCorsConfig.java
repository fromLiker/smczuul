package com.ibm.zuul.config;

/**
 * @author PingXue
 *
 */
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class GateWayCorsConfig {
	@Bean
	public CorsFilter corsFilter() {
	final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	final CorsConfiguration corsConfiguration = new CorsConfiguration();
	corsConfiguration.setAllowCredentials(true); // 允许cookies跨域
	corsConfiguration.addAllowedHeader("*"); // 允许访问的头信息,*表示全部
	corsConfiguration.addAllowedOrigin("*"); // 允许向该服务器提交请求的URI，*表示全部允许。。这里尽量限制来源域，比如http://xxxx:8080 ,以降低安全风险。。
	corsConfiguration.addAllowedMethod("*"); // 允许提交请求的方法，*表示全部允许，也可以单独设置GET、PUT等
	source.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(source);
	}
}
