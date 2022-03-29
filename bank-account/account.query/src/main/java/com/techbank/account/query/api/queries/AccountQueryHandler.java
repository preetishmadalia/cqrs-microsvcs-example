package com.techbank.account.query.api.queries;

import com.techbank.account.query.api.dto.EqualityType;
import com.techbank.account.query.domain.AccountRepository;
import com.techbank.cqrs.core.domain.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountQueryHandler implements QueryHandler {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<BaseEntity> handle(FindAccountByBalanceQuery query) {
        var bankAccounts = (query.getEqualityType() == EqualityType.GREATER_THAN) ?
                this.accountRepository.findByBalanceGreaterThan(query.getBalance())
                : this.accountRepository.findByBalanceLessThan(query.getBalance());
        return bankAccounts;
    }

    @Override
    public List<BaseEntity> handle(FindAccountByHolderQuery query) {
        var bankAccount = this.accountRepository.findByAccountHolder(query.getAccountHolder());
        if(!bankAccount.isEmpty()) {
            List<BaseEntity> list = new ArrayList<>();
            list.add(bankAccount.get());
            return list;
        }
        return null;
    }

    @Override
    public List<BaseEntity> handle(FindAccountByIdQuery query) {
        var bankAccount = this.accountRepository.findById(query.getId());

        if(!bankAccount.isEmpty()) {
            List<BaseEntity> list = new ArrayList<>();
            list.add(bankAccount.get());
            return list;
        }
        return null;
    }

    @Override
    public List<BaseEntity> handle(FindAllAccountsQuery query) {
        var bankAccounts = this.accountRepository.findAll();
        List<BaseEntity> accounts = new ArrayList<>();
        bankAccounts.forEach(accounts::add);
        return accounts;
    }
}
