package com.techbank.account.cmd.api.controllers;

import com.techbank.account.cmd.api.commands.OpenAccountCommand;
import com.techbank.account.cmd.api.dto.OpenAccountResponse;
import com.techbank.account.common.dto.BaseResponse;
import com.techbank.cqrs.core.infrastructure.CommandDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/openBankAccount")
@Slf4j
public class OpenAccountController {

    @Autowired
    private CommandDispatcher commandDispatcher;

    @PostMapping
    public ResponseEntity<BaseResponse> openBankAccount(@RequestBody OpenAccountCommand command) {
        var id = UUID.randomUUID().toString();
        command.setId(id);

        try {
            this.commandDispatcher.send(command);
            return new ResponseEntity<>(new OpenAccountResponse("Congratulations! You opened bank account successfully!", id), HttpStatus.CREATED);
        } catch (IllegalStateException ise) {
            log.warn(MessageFormat.format("User made a bad request : {0}", ise.getMessage()));
            return new ResponseEntity<>(new BaseResponse(MessageFormat.format("User made a bad request : {0}", ise.getMessage())), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(MessageFormat.format("Your request cannot be completed at this time : {0}", ex.getMessage()));
            return new ResponseEntity<>(new BaseResponse(MessageFormat.format("Your request cannot be completed at this time : {0}", ex.getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
