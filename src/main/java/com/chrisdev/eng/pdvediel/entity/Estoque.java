package com.chrisdev.eng.pdvediel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

//entidade que representa o estoque disp de um item
@Entity
@Table(name = "estoques")
@Getter
@Setter

public class Estoque {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "item_id", nullable = false, unique = true)
    private Item item;

    @Column(nullable = false)
    private Integer quantidade = 0;
}
