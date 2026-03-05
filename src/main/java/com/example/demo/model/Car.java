package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq_gen")
    @SequenceGenerator(
            name = "order_seq_gen",
            sequenceName = "order_seq",
            allocationSize = 5
    )
    private int id;
    private String model;

    public Car(String model) {
        this.model = model;
    }
}