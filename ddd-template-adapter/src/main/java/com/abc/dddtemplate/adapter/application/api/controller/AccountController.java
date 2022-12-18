package com.abc.dddtemplate.adapter.application.api.controller;

import com.abc.dddtemplate.share.dto.ResponseData;
import com.abc.dddtemplate.application.commands.account.ChargeAccountCmd;
import com.abc.dddtemplate.application.commands.account.OpenAccountCmd;
import com.abc.dddtemplate.application.queries.ListAccountQry;
import com.abc.dddtemplate.application.queries.ListTransferQry;
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
@Tag(name="账户")
@RestController
@RequestMapping(value = "/appApi/account")
@Slf4j
public class AccountController {
    @Autowired
    ListAccountQry.Handler listAccountQryHandler;

    @PostMapping("list")
    public ResponseData<PageData<ListAccountQry.ListAccountQryDto>> list(@RequestBody ListAccountQry listAccountQry) {
        var result = listAccountQryHandler.exec(listAccountQry);
        return ResponseData.success(result);
    }

    @Autowired
    ListTransferQry.Handler listTransferQryHandler;

    @PostMapping("transfer/list")
    public ResponseData<PageData<ListTransferQry.ListTransferQryDto>> transferList(@RequestBody ListTransferQry listTransferQry) {
        var result = listTransferQryHandler.exec(listTransferQry);
        return ResponseData.success(result);
    }

    @Autowired
    OpenAccountCmd.Handler openAccountCmdHandler;

    @PostMapping("open")
    public ResponseData<Long> open(@RequestBody OpenAccountCmd openAccountCmd) {
        var result = openAccountCmdHandler.exec(openAccountCmd);
        return ResponseData.success(result);
    }


    @Autowired
    ChargeAccountCmd.Handler chargeAccountCmdHandler;

    @PostMapping("charge")
    public ResponseData<Boolean> charge(@RequestBody ChargeAccountCmd chargeAccountCmd) {
        var result = chargeAccountCmdHandler.exec(chargeAccountCmd);
        return ResponseData.success(result);
    }
}
