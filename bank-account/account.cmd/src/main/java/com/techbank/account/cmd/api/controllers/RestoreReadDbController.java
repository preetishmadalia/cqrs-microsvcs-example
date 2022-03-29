package com.techbank.account.cmd.api.controllers;

import com.techbank.account.cmd.api.commands.RestoreReadDbCommand;
import com.techbank.account.common.dto.BaseResponse;
import com.techbank.cqrs.core.infrastructure.CommandDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;

@RestController
@RequestMapping("/api/v1/restoreReadDb")
public class RestoreReadDbController {

    @Autowired
    private CommandDispatcher commandDispatcher;

    @PostMapping
    public ResponseEntity restoreReadDb() {
        try {
            this.commandDispatcher.send(new RestoreReadDbCommand());
            return new ResponseEntity(new BaseResponse("Completed restore of ReadDB."), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity(new BaseResponse(MessageFormat.format("Cannot complete restore of ReadDB. {0}", e.getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
