package com.abc.dddtemplate.adapter.portal.api.controller;

import com.abc.dddtemplate.adapter.configure.orika.DemoCustomMapper;
import com.abc.dddtemplate.adapter.configure.orika.DemoCustomMapperBuilder;
import com.abc.dddtemplate.adapter.configure.orika.DemoDefault;
import com.abc.dddtemplate.application.sagas.DemoAnnotationSaga;
import com.abc.dddtemplate.application.sagas.DemoSagaService;
import com.abc.dddtemplate.application.subscribers.external.ExampleExternalDomainEventSubscriber;
import com.abc.dddtemplate.convention.DomainEventSupervisor;
import com.abc.dddtemplate.convention.SagaSupervisor;
import com.abc.dddtemplate.share.dto.ResponseData;
import com.abc.dddtemplate.external.clients.SysTime;
import com.abc.dddtemplate.external.clients.TimeServiceClient;
import com.abc.dddtemplate.share.util.MapperUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * TODO 后续可以考虑基于Command和Query的注解，自动生成Controller
 * @author <template/>
 * @date
 */
@Tag(name="测试控制器")
@RestController
@RequestMapping(value = "/appApi/test")
@Slf4j
public class TestAppController {

    @Autowired
    SysTime.Client sysTimeClient;

    @PostMapping("sysTimeClient")
    public ResponseData<String> sysTimeClient(){
        var result = sysTimeClient.get();
        return ResponseData.success(result.getData());
    }

    @Autowired
    TimeServiceClient timeServiceClient;

    @PostMapping("timeServiceClient")
    public ResponseData<String> testClient(){
        var result = timeServiceClient.getSysTime();
        return ResponseData.success(result.getTimestamp());
    }

    @GetMapping("serviceMock")
    public ResponseData<Object> serviceMock(@RequestParam("delay") long delay){
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResponseData.success(0);
    }

    @Autowired
    DemoSagaService demoSagaService;
    @GetMapping("saga")
    public String saga(String input){
        var context = new DemoSagaService.Context();
        context.setInput(input);
        var result = demoSagaService.run(context);
        return result.toString();
    }

    @Autowired
    SagaSupervisor sagaSupervisor;
    @GetMapping("sagaAnno")
    public String sagaAnno(String input){
        var context = new DemoAnnotationSaga();
        context.setInput(input);
        var result = sagaSupervisor.run(context);
        return result.toString();
    }

    @Autowired
    DomainEventSupervisor domainEventSupervisor;
    @GetMapping("domainEvent")
    public String domainevent(String input){
        var msg = new ExampleExternalDomainEventSubscriber.ExampleExternalDomainEvent();
        msg.setMsg(input);
        domainEventSupervisor.dispatchRawImmediately(msg);
        return input;
    }

    @PostMapping("customMapper")
    public String customMapper(@RequestBody DemoCustomMapper.A a){
        DemoCustomMapper.B b = MapperUtil.map(a, DemoCustomMapper.B.class);
        return JSON.toJSONString(b);
    }

    @PostMapping("customMapperBuilder")
    public String customMapperBuilder(@RequestBody DemoCustomMapperBuilder.A a){
        DemoCustomMapperBuilder.B b = MapperUtil.map(a, DemoCustomMapperBuilder.B.class);
        return JSON.toJSONString(b);
    }

    @PostMapping("mapperDefault")
    public String mapperDefault(@RequestBody DemoDefault.A a){
        DemoDefault.B b = MapperUtil.map(a, DemoDefault.B.class);
        return JSON.toJSONString(b);
    }

}
