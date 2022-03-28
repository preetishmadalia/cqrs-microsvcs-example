package com.techbank.account.query.infrastructure.handlers;

import com.techbank.account.common.events.AccountClosedEvent;
import com.techbank.account.common.events.AccountOpenedEvent;
import com.techbank.account.common.events.FundsDepositedEvent;
import com.techbank.account.common.events.FundsWithdrawnEvent;
import com.techbank.account.query.domain.AccountRepository;
import com.techbank.account.query.domain.BankAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountEventHandler implements EventHandler {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public void on(AccountOpenedEvent event) {
        var bankAccount = BankAccount.builder()
                .id(event.getId())
                .accountHolder(event.getAccountHolder())
                .balance(event.getOpeningBalance())
                .createdDate(event.getCreatedDate())
                .accountType(event.getAccountType()).build();

        this.accountRepository.save(bankAccount);
    }

    @Override
    public void on(AccountClosedEvent event) {
        this.accountRepository.deleteById(event.getId());
    }

    @Override
    public void on(FundsDepositedEvent event) {
        var bankAccount = this.accountRepository.findById(event.getId());
        if(bankAccount.isEmpty()) return;

        double balance = bankAccount.get().getBalance() + event.getAmount();
        bankAccount.get().setBalance(balance);
        this.accountRepository.save(bankAccount.get());
    }

    @Override
    public void on(FundsWithdrawnEvent event) {
        var bankAccount = this.accountRepository.findById(event.getId());
        if(bankAccount.isEmpty()) return;

        double balance = bankAccount.get().getBalance() - event.getAmount();
        bankAccount.get().setBalance(balance);
        this.accountRepository.save(bankAccount.get());
    }
}
