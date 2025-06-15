package com.example.ecommerce.repository;
import com.example.ecommerce.entity.PurchaseHistory; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long> { List<PurchaseHistory> findByUserId(String userId); }
