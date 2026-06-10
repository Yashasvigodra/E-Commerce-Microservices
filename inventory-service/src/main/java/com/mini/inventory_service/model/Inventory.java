package com.mini.inventory_service.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String skuCode;

    private Integer quantity;

    private String warehouseLocation;

}
