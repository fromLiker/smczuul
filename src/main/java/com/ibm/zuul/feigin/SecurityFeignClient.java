package com.ibm.zuul.feigin;

/**
 * @author PingXue
 *
 */
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ibm.zuul.bean.ResponseBean;

@FeignClient(name = "SMC-Users")
public interface SecurityFeignClient {
	
    // verify admin role
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    ResponseEntity<ResponseBean> isAdmin(@RequestHeader(name = "Authorization") String authHeader);

    // verify token
    @RequestMapping(value = "/authenticated", method = RequestMethod.GET)
    ResponseEntity<ResponseBean> hasToken(@RequestHeader(name = "Authorization") String authHeader);

}
