package com.abc.dddtemplate.adapter.portal.api.controller;

import com.abc.dddtemplate.share.dto.ResponseData;
import com.abc.dddtemplate.application.commands.bill.PayBillCmd;
import com.abc.dddtemplate.application.queries.ListBillQry;
import com.abc.dddtemplate.share.dto.PageData;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <template/>
 * @date
 */
@Tag(name="账单")
@RestController
@RequestMapping(value = "/appApi/bill")
@Slf4j
public class BillController {

    @Autowired
    ListBillQry.Handler listBillQryHandler;

    @PostMapping("list")
    public ResponseData<PageData<ListBillQry.ListBillQryDto>> list(@RequestBody ListBillQry listBillQry){
        var result = listBillQryHandler.exec(listBillQry);
        return ResponseData.success(result);
    }

    @Autowired
    PayBillCmd.Handler payBillCmdHandler;

    @PostMapping("pay")
    public ResponseData<Boolean> pay(@RequestBody PayBillCmd payBillDTO) {
        var result = payBillCmdHandler.exec(payBillDTO);
        return ResponseData.success(result);
    }
}
