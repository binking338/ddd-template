package com.abc.dddtemplate.adapter.portal.api.controller;

import com.abc.dddtemplate.share.dto.ResponseData;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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

    @GetMapping(value = "/serviceMock")
    public ResponseData<Object> mock(@RequestParam("delay") long delay){
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return ResponseData.success(LocalDateTime.now());
    }

}
