package com.abc.dddtemplate.adapter.external.clients.rest;

import com.abc.dddtemplate.share.dto.ResponseData;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "other-service-id", path = "/ddd-template/appApi/test", url = "http://localhost:${server.port}", configuration = FeignAutoConfiguration.class)
public interface ServiceMockRest {

    @GetMapping(value = "/serviceMock", consumes = "application/json")
    ResponseData<Object> mock(@RequestParam("delay") long delay);
}