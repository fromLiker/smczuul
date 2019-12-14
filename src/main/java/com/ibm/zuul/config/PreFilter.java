/**
 * 
 */
package com.ibm.zuul.config;

import com.ibm.zuul.feigin.SecurityFeignClient;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_DECORATION_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author PingXue
 *
 */
public class PreFilter extends ZuulFilter {

	  private static Logger log = LoggerFactory.getLogger(PreFilter.class);
	  private static final String LOGIN_URI = "/login"; // permit
	  private static final String SIGNUP_URI = "/signup"; // permit
	  private static final String ADMIN_URI = "/admin"; // verify admin token
	  private static final String USER_CONFIRMED_URI = "/confirmed"; // permit
	  private static final String INVALID_TOKEN = "Invalid Token";

	  @Autowired
	  private SecurityFeignClient securityFeignClient;

	  @Override
	  public String filterType() {
	    return PRE_TYPE;
	  }

	  @Override
	  public int filterOrder() {
	    return PRE_DECORATION_FILTER_ORDER - 1;
	  }

	  @Override
	  public boolean shouldFilter() {
	    RequestContext requestContext = RequestContext.getCurrentContext();
	    HttpServletRequest request = requestContext.getRequest();

	    if (request.getRequestURI().indexOf(LOGIN_URI) >= 0 || request.getRequestURI().indexOf(SIGNUP_URI) >= 0 || request.getRequestURI().indexOf(
	      USER_CONFIRMED_URI) >= 0) {
	      log.debug("PreRequestFilter-getRequestURI: {}", request.getRequestURI());
	      System.out.println("PreRequestFilter-getRequestURI: {} >>>" + request.getRequestURI());
	      return false;
	    }
	    return true;
	  }

	  @Override
	  public Object run() throws ZuulException {
	    // verify token before routing to the services
	    RequestContext ctx = RequestContext.getCurrentContext();
	    HttpServletRequest request = ctx.getRequest();
	    String authHeader = request.getHeader("Authorization");
	    log.debug("PreRequestFilter-run:Authorization = {}", authHeader);
	    System.out.println("authHeader >>>" + authHeader);

	    if (StringUtils.isNotBlank(authHeader)) {
	      HttpStatus authChkStatus = INTERNAL_SERVER_ERROR;
	    //   System.out.println("authChkStatus >>>" + authChkStatus);
	      try {
	        if (request.getRequestURI().indexOf(ADMIN_URI) >= 0) {
	          authChkStatus = securityFeignClient.isAdmin(authHeader).getStatusCode();
	        } else {
	          authChkStatus = securityFeignClient.hasToken(authHeader).getStatusCode();
	        }
	      } catch (Exception e) {
	        log.error(e.getMessage(), e);
	        String status = e.getMessage().substring(7, 10);
	        if (StringUtils.isNumeric(status)) {
	          authChkStatus = HttpStatus.valueOf(Integer.valueOf(status));
	        }
	      }
	      log.debug("PreRequestFilter-run:authChkStatus = {}", authChkStatus.toString());
	      System.out.println("authChkStatus.toString() >>>" + authChkStatus.toString());

	      if (authChkStatus.equals(OK)) {
	        // router the request
	        ctx.setSendZuulResponse(true);
	        ctx.setResponseStatusCode(OK.value());
	        ctx.set("isSuccess", true);
	      } else {
	        // block the rquest
	        ctx.setSendZuulResponse(false);
	        ctx.setResponseStatusCode(authChkStatus.value());
	        ctx.setResponseBody(authChkStatus.getReasonPhrase());
	        ctx.set("isSuccess", false);
	      }
	    } else {
	      // block the rquest
	      ctx.setSendZuulResponse(false);
	      ctx.setResponseStatusCode(403);
	      ctx.setResponseBody(INVALID_TOKEN);
	      ctx.set("isSuccess", false);
	    }
	    return null;
	  }
	  
}
