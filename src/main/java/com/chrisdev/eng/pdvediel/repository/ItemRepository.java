package com.chrisdev.eng.pdvediel.repository;

import com.chrisdev.eng.pdvediel.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}