package com.techbank.account.query.api.controllers;

import com.techbank.account.query.api.dto.AccountLookupResponse;
import com.techbank.account.query.api.dto.EqualityType;
import com.techbank.account.query.api.queries.FindAccountByBalanceQuery;
import com.techbank.account.query.api.queries.FindAccountByHolderQuery;
import com.techbank.account.query.api.queries.FindAccountByIdQuery;
import com.techbank.account.query.api.queries.FindAllAccountsQuery;
import com.techbank.account.query.domain.BankAccount;
import com.techbank.cqrs.core.infrastructure.QueryDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/bankAccountLookup")
public class AccountLookupController {

    @Autowired
    private QueryDispatcher queryDispatcher;

    @GetMapping("/")
    public ResponseEntity<AccountLookupResponse> findAllBankAccounts() {

        try {
            List<BankAccount> bankAccounts = this.queryDispatcher.send(new FindAllAccountsQuery());

            if(bankAccounts == null || bankAccounts.isEmpty()) {
                return new ResponseEntity<>(new AccountLookupResponse("Did not find any account(s)"), HttpStatus.NO_CONTENT);
            }
            var response = AccountLookupResponse.builder()
                    .accounts(bankAccounts)
                    .message(MessageFormat.format("Successfully retrieved {0} bank accounts.", bankAccounts.size()))
                    .build();

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            var message = "Unable to complete request to find all accounts.";
            log.error(message);
            return new ResponseEntity<>(new AccountLookupResponse(message), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/byId/{id}")
    public ResponseEntity<AccountLookupResponse> findBankAccountsById(@PathVariable("id") String id) {

        try {
            List<BankAccount> bankAccounts = this.queryDispatcher.send(new FindAccountByIdQuery(id));

            if(bankAccounts == null || bankAccounts.isEmpty()) {
                return new ResponseEntity<>(new AccountLookupResponse("Did not find any account(s)"), HttpStatus.NO_CONTENT);
            }
            var response = AccountLookupResponse.builder()
                    .accounts(bankAccounts)
                    .message("Successfully retrieved bank account.").build();

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            var message = "Unable to complete request to find bank account.";
            log.error(message);
            return new ResponseEntity<>(new AccountLookupResponse(message), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/byHolder/{holder}")
    public ResponseEntity<AccountLookupResponse> findBankAccountsByHolder(@PathVariable("holder") String accountHolder) {
        ResponseEntity<AccountLookupResponse> result;

        try {
            List<BankAccount> bankAccounts = this.queryDispatcher.send(new FindAccountByHolderQuery(accountHolder));

            if (bankAccounts == null || bankAccounts.isEmpty()) {
                result = new ResponseEntity<>(new AccountLookupResponse("Did not find any account."), HttpStatus.NO_CONTENT);
            } else {
                var response = AccountLookupResponse.builder()
                        .accounts(bankAccounts)
                        .message("Successfully retrieved bank account.").build();
                result = new ResponseEntity<>(response, HttpStatus.OK);
            }

        } catch (Exception e) {
            var message = "Unable to complete request to find bank account.";
            log.error(message);
            result = new ResponseEntity<>(new AccountLookupResponse(message), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    @GetMapping("/byBalance/{equalityType}/{balance}")
    public ResponseEntity<AccountLookupResponse> findBankAccountsByBalance(@PathVariable("equalityType") EqualityType equalityType,
                                                                           @PathVariable("balance") double balance)  {
        ResponseEntity<AccountLookupResponse> result;

        try {
            List<BankAccount> bankAccounts = this.queryDispatcher.send(new FindAccountByBalanceQuery(equalityType, balance));

            if (bankAccounts == null || bankAccounts.isEmpty()) {
                result = new ResponseEntity<>(new AccountLookupResponse("Did not find any account(s)"), HttpStatus.NO_CONTENT);
            } else {
                var response = AccountLookupResponse.builder()
                        .accounts(bankAccounts)
                        .message(MessageFormat.format("Successfully retrieved {0} bank accounts.", bankAccounts.size()))
                        .build();
                result = new ResponseEntity<>(response, HttpStatus.OK);
            }

        } catch (Exception e) {
            var message = "Unable to complete request to find bank account.";
            log.error(message);
            result = new ResponseEntity<>(new AccountLookupResponse(message), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }
}
