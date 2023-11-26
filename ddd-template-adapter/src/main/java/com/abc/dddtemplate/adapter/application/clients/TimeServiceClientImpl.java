package com.abc.dddtemplate.adapter.application.clients;

import com.alibaba.fastjson.JSON;
import com.abc.dddtemplate.share.dto.ResponseData;
import com.abc.dddtemplate.adapter.application.clients.rest.ServiceMockRest;
import com.abc.dddtemplate.application.clients.TimeServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 外部服务的防腐接口
 * @author <template/>
 * @date
 */
@Service
@RequiredArgsConstructor
public class TimeServiceClientImpl implements TimeServiceClient {

    private final ServiceMockRest serviceMockRest;

    @Override
    public SysTimeDto getSysTime() {
        // 使用feign实现
        ResponseData<Object> data = serviceMockRest.mock(0);
        SysTimeDto sysTimeDto = SysTimeDto.builder()
                .timestamp(JSON.toJSONString(data.getData()))
                .build();
        return sysTimeDto;
    }
}
