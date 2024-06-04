package com.abc.dddtemplate.adapter.application.clients;

import com.abc.dddtemplate.adapter.application.clients.rest.ServiceMockRest;
import com.abc.dddtemplate.application._share.clients.InventoryClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author <template/>
 * @date
 */
@Service
@RequiredArgsConstructor
public class InventoryClientImpl implements InventoryClient {
    private final ServiceMockRest serviceMockRest;

    @Override
    public Boolean reduce(String productName, Integer num) {
        serviceMockRest.mock(500);
        return true;
    }
}
