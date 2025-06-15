package com.example.ecommerce.entity;
import jakarta.persistence.*; import lombok.Data;
@Entity @Data public class Product { @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; private String name; private String description; private double price; }
