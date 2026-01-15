package com.pavzar.stocktracker.repository;

import com.pavzar.stocktracker.entity.FavoriteStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteStockRepository extends JpaRepository<FavoriteStock, Integer> {
    boolean existsBySymbol(String symbol);
}
