package com.chrisdev.eng.pdvediel.repository;

import com.chrisdev.eng.pdvediel.entity.ItemVenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemVendaRepository extends JpaRepository<ItemVenda, Long> {

    List<ItemVenda> findByVendaId(Long vendaId);
}