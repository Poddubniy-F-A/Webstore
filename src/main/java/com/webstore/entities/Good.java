package com.webstore.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "goods")
public class Good {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String label;

    @Column
    private String description;

    @Column(name = "picture_path")
    private String picturePath;

    @Column
    private String category;

    @Column
    private String brand;

    @Column
    private int price;

    @Column(columnDefinition = "integer default 0")
    private int count;

    @Column(name = "orders_count", columnDefinition = "integer default 0")
    private int ordersCount;

    @Column(columnDefinition = "double default 0")
    private double rate;

    @Column(columnDefinition = "integer default 0")
    private int ratingsCount;
}
