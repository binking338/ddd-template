package com.abc.dddtemplate.adapter.external.clients;

import com.alibaba.fastjson.JSON;
import com.abc.dddtemplate.share.dto.ResponseData;
import com.abc.dddtemplate.adapter.external.clients.rest.ServiceMockRest;
import com.abc.dddtemplate.application.clients.SysTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 外部服务的防腐接口
 * @author <template/>
 * @date
 */
@Service
@RequiredArgsConstructor
public class SysTimeClientImpl implements SysTime.Client {

    private final ServiceMockRest serviceMockRest;

    @Override
    public SysTime get() {
        // 使用feign实现
        ResponseData<Object> data = serviceMockRest.mock(0);
        SysTime sysTime = SysTime.builder()
                .data(JSON.toJSONString(data.getData()))
                .build();
        return sysTime;
    }
}
