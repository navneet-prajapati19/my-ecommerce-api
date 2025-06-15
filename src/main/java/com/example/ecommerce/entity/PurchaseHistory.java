package com.example.ecommerce.entity;
import jakarta.persistence.*; import lombok.Data; import java.time.LocalDateTime;
@Entity @Data public class PurchaseHistory { @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; private String userId; private Long productId; private int quantity; private LocalDateTime purchaseDate; }
