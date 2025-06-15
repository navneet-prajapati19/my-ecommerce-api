// In file: src/main/java/com/example/ecommerce/entity/Rating.java
package com.example.ecommerce.entity;
import jakarta.persistence.*; import lombok.Data;
@Entity @Data public class Rating { @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id; private Long productId; private String userId; private int score; private String comment; }
