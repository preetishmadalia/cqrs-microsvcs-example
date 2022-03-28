package com.techbank.account.cmd.api.commands;

import com.techbank.account.cmd.domain.AccountAggregate;
import com.techbank.cqrs.core.handlers.EventSouringHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountCommandHandler implements CommandHandler {

    @Autowired
    private EventSouringHandler<AccountAggregate> eventSouringHandler;

    @Override
    public void handle(OpenAccountCommand command) {
        var accountAggregate = new AccountAggregate(command);
        eventSouringHandler.save(accountAggregate);
    }

    @Override
    public void handle(CloseAccountCommand command) {
        var accountAggregate = eventSouringHandler.getById(command.getId());
        accountAggregate.closeAccount();
        this.eventSouringHandler.save(accountAggregate);
    }

    @Override
    public void handle(DepositFundsCommand command) {
        var accountAggregate = eventSouringHandler.getById(command.getId());
        accountAggregate.depositFunds(command.getAmount());
        this.eventSouringHandler.save(accountAggregate);
    }

    @Override
    public void handle(WithdrawFundsCommand command) {
        var accountAggregate = eventSouringHandler.getById(command.getId());
        if(command.getAmount() > accountAggregate.getBalance()) {
            throw new IllegalStateException("Insufficient Funds!");
        }
        accountAggregate.withdrawFunds(command.getAmount());
        this.eventSouringHandler.save(accountAggregate);
    }
}
