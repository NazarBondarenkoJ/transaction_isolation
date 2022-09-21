package com.example.transactionisolation.bootstrap;

import com.example.transactionisolation.model.Account;
import com.example.transactionisolation.repository.AccountRepository;
import com.example.transactionisolation.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class BootStrapLoader implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final AccountService accountService;

    public BootStrapLoader(AccountRepository accountRepository, AccountService accountService) {
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }

    @Override
    public void run(String... args) {
        Account accountFirst = new Account();
        accountFirst.setId(1);
        accountFirst.setMoney(1000);
        Account accountSecond = new Account();
        accountSecond.setId(2);
        accountSecond.setMoney(1500);
        accountRepository.saveAll(List.of(accountFirst, accountSecond));

        ExecutorService executor = Executors.newFixedThreadPool(2);

        System.out.println(accountRepository.findAll());

        executor.submit(() -> {
            accountService.wireTransfer(accountFirst.getId(),accountSecond.getId(),500);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("FIRST THREAD: " + accountRepository.findAll());
        });

        executor.submit(() -> {
            System.out.println("SECOND THREAD: " + accountRepository.findAll());
            accountService.wireTransfer(accountFirst.getId(),accountSecond.getId(),500);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(accountRepository.findAll());
        });

        executor.shutdown();

    }
}
