package com.techbank.account.cmd.api.controllers;

import com.techbank.account.cmd.api.commands.WithdrawFundsCommand;
import com.techbank.account.common.dto.BaseResponse;
import com.techbank.cqrs.core.infrastructure.CommandDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

@Slf4j
@RestController
@RequestMapping("/api/v1/withdrawFunds")
public class WithdrawFundsController {

    @Autowired
    private CommandDispatcher commandDispatcher;

    @PutMapping(path = "/{id}")
    public ResponseEntity<BaseResponse> withdrawFunds(
            @PathVariable("id") String id,
            @RequestBody WithdrawFundsCommand command) {

        try {
            command.setId(id);
            this.commandDispatcher.send(command);
            return new ResponseEntity<>(new BaseResponse("Amount withdraw completed successfully!"), HttpStatus.OK);
        } catch (IllegalStateException ise) {
            log.warn(MessageFormat.format("Cannot complete withdraw funds at this time : {0}", ise.getMessage()));
            return new ResponseEntity<>(new BaseResponse(MessageFormat.format("Cannot complete withdraw funds at this time : {0}", ise.getMessage())), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(MessageFormat.format("Something went wrong cannot complete your request at this time : {0}", ex.getMessage()));
            return new ResponseEntity<>(new BaseResponse(MessageFormat.format("Something went wrong cannot complete your request at this time : {0}", ex.getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
