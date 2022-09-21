package com.example.transactionisolation.service;

import com.example.transactionisolation.model.Account;
import com.example.transactionisolation.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountService {

    private final
    AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void wireTransfer(int senderId, int recipientId, int transferAmount) {
        Account sender = accountRepository.findById(senderId).orElse(null);
        Account recipient = accountRepository.findById(recipientId).orElse(null);

        if (sender != null && recipient != null) {
            sender.setMoney(sender.getMoney() - transferAmount);
            recipient.setMoney(recipient.getMoney() + transferAmount);
            accountRepository.saveAll(List.of(sender, recipient));
        }
    }

}
