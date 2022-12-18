package com.abc.dddtemplate.adapter.external.clients;

import com.abc.dddtemplate.adapter.external.clients.rest.ServiceMockRest;
import com.abc.dddtemplate.external.clients.InventoryClient;
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
