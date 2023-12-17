package com.bank.cards.repository;


import com.bank.cards.model.Cards;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardsRepository extends CrudRepository<Cards, Long> {

    public List<Cards> findByCustomerId(int customerId);
}
