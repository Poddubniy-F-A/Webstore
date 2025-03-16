package com.webstore.model.entities;

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

    @Column(columnDefinition = "integer default 0")
    private int ratingsSum;

    @Column(columnDefinition = "integer default 0")
    private int ratingsCount;

    public double getRating() {
        if (ratingsCount == 0) {
            return 0;
        }
        return (double) ratingsSum / ratingsCount;
    }
}
