package com.bank.account.controller;

import com.bank.account.model.Accounts;
import com.bank.account.model.Customer;
import com.bank.account.repository.AccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountsController {
    @Autowired
    private AccountsRepository accountsRepository;
    @PostMapping("/myAccount")
    public Accounts getAccountDetails(@RequestBody Customer customer ) {
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        if(accounts != null){
            return accounts;
        }else{
            return null;
        }

    }
}
