package com.techbank.account.cmd.api.controllers;

import com.techbank.account.cmd.api.commands.CloseAccountCommand;
import com.techbank.account.common.dto.BaseResponse;
import com.techbank.cqrs.core.infrastructure.CommandDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;

@Slf4j
@RestController
@RequestMapping("/api/v1/closeBankAccount")
public class CloseAccountController {

    @Autowired
    private CommandDispatcher commandDispatcher;

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<BaseResponse> closeBankAccount(@PathVariable("id") String id) {

        try {

            this.commandDispatcher.send(new CloseAccountCommand(id));
            return new ResponseEntity<>(new BaseResponse("Bank account close completed successfully!"), HttpStatus.OK);
        } catch (IllegalStateException ise) {
            log.warn(MessageFormat.format("Cannot complete close account request at this time : {0}", ise.getMessage()));
            return new ResponseEntity<>(new BaseResponse(MessageFormat.format("Cannot complete close account request at this time : {0}", ise.getMessage())), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(MessageFormat.format("Something went wrong cannot complete your request at this time : {0}", ex.getMessage()));
            return new ResponseEntity<>(new BaseResponse(MessageFormat.format("Something went wrong cannot complete your request at this time : {0}", ex.getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
