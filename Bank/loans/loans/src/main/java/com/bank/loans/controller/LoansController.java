package com.bank.loans.controller;

import com.bank.loans.model.Customer;
import com.bank.loans.model.Loans;
import com.bank.loans.repository.LoansRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoansController {
    @Autowired
    LoansRepository loansRepository;
    @PostMapping("/myloans")
    public List<Loans> getLoansDetails(@RequestBody Customer customer){
        List<Loans> loansList = loansRepository.findByCustomerIdOrderByStartDtDesc(customer.getCustomerId());

        if(loansList == null){
            return null;
        } else {
            return loansList;
        }
    }
}
