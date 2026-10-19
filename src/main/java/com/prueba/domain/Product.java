package com.prueba.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "products")
@Getter 
@Setter
@NoArgsConstructor

public class Product {

    @Id
    @Column (name = "product_id", length = 6)
    private String productId;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated (EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ProductType type;

}