package com.abc.dddtemplate.adapter.portal.api.controller;

import com.abc.dddtemplate.application.commands.order.ModifyOrderCmd;
import com.abc.dddtemplate.share.dto.ResponseData;
import com.abc.dddtemplate.application.commands.order.CloseOrderCmd;
import com.abc.dddtemplate.application.commands.order.PlaceOrderCmd;
import com.abc.dddtemplate.application.queries.SearchOrderQry;
import com.abc.dddtemplate.share.dto.PageData;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author <template/>
 * @date
 */
@Tag(name = "订单")
@RestController
@RequestMapping(value = "/appApi/order")
@Slf4j
public class OrderController {

    @Autowired
    SearchOrderQry.Handler searchOrderQryHandler;

    @PostMapping("search")
    public ResponseData<PageData<SearchOrderQry.SearchOrderQryDto>> search(@RequestBody SearchOrderQry param) {
        var query = searchOrderQryHandler.exec(param);
        return ResponseData.success(query);
    }


    @Autowired
    PlaceOrderCmd.Handler placeOrderCmdHandler;

    @PostMapping("/place")
    public ResponseData<Long> placeOrderCmd(@RequestBody PlaceOrderCmd cmd) {
        Long result = placeOrderCmdHandler.exec(cmd);
        return ResponseData.success(result);
    }

    @Autowired
    ModifyOrderCmd.Handler modifyOrderCmd;

    @PostMapping("modify")
    public ResponseData<Boolean> modify(@RequestBody ModifyOrderCmd cmd) {
        var result = modifyOrderCmd.exec(cmd);
        return ResponseData.success(result);
    }

    @Autowired
    CloseOrderCmd closeOrderCmd;

    @PostMapping("close")
    public ResponseData<Boolean> close(Long orderId) {
        var result = closeOrderCmd.exec(orderId);
        return ResponseData.success(result);
    }
}
