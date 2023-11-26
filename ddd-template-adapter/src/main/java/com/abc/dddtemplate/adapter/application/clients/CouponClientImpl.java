package com.abc.dddtemplate.adapter.application.clients;

import com.abc.dddtemplate.adapter.application.clients.rest.ServiceMockRest;
import com.abc.dddtemplate.application.clients.CouponClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author <template/>
 * @date
 */
@Service
@RequiredArgsConstructor
public class CouponClientImpl implements CouponClient {
    private final ServiceMockRest serviceMockRest;

    @Override
    public Boolean deduct(String name, Integer amount) {
        serviceMockRest.mock(500);
        return true;
    }
}
