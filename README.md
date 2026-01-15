# StockTracker

A Spring Boot REST API that fetches stock market data from **Alpha Vantage** and lets users maintain a list of favorite tickers in a database.  
Built as a portfolio project to demonstrate backend API design, external API integration, persistence, caching, and resiliency patterns.

## Diagram
<img width="1369" height="704" alt="image" src="https://github.com/user-attachments/assets/2a128be5-34f5-4dac-a0bf-7260f0c65d19" />

## Features

- Live quote lookup for a ticker (Alpha Vantage `GLOBAL_QUOTE`)
- Daily historical prices for the last _N_ days (Alpha Vantage `TIME_SERIES_DAILY`)
- Company overview (Alpha Vantage `OVERVIEW`)
- Favorites:
  - Save a ticker symbol as a favorite
  - List favorites with **live prices**
- **Rate limiting** with Resilience4j to respect external API limits
- **Caching** for quote requests to reduce repeated external calls
- **Database persistence** with Spring Data JPA
  - Default: H2 file DB
  - Optional: MySQL

## Tech Stack

- Java 21
- Spring Boot 3
  - Spring Web + WebFlux `WebClient`
  - Spring Data JPA
  - Spring Cache (simple in-memory cache)
- Resilience4j RateLimiter
- H2 (default) / MySQL (optional)

## Caching & Rate Limiting

- **Caching:** Quote lookups are cached via Spring Cache (`@Cacheable`) to reduce repeated calls for the same symbol.
- **Rate limiting:** Outbound Alpha Vantage quote requests go through a **Resilience4j RateLimiter** (configured in `AlphaVantageRateLimiterConfig`) to prevent throttling.

## Project Structure
```text
src/main/java/com/pavzar/stocktracker
├── controller/      # REST endpoints
├── service/         # orchestration + caching + rate limiting
├── client/          # WebClient integration with Alpha Vantage
├── repository/      # JPA repositories
├── entity/          # database entities
├── dto/             # request/response DTOs
└── config/          # WebClient + RateLimiter configuration
```
