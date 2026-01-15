package com.pavzar.stocktracker.exception;

public class FavoriteStockAlreadyExistsException extends RuntimeException{
    public FavoriteStockAlreadyExistsException(String symbol) {
        super("Favorite stock with symbol '" + symbol + "' already exists.");
    }
}
