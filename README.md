# URL Shortener

This is a URL Shortener built with Java, Spring Boot, and Maven. It provides functionality to shorten URLs, retrieve original URLs, and track click counts.

## Features

- Shorten a given URL
- Retrieve the original URL from a shortened URL
- Track the number of clicks on a shortened URL
- Cache URLs using Redis for faster access

## Technologies Used

- Java
- Spring Boot
- Maven
- Redis
- JUnit 5
- Mockito
- AssertJ

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven
- Redis

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/MarketaKubikova/url-shortener.git
    cd url-shortener-service
    ```

2. Install dependencies:
    ```sh
    mvn clean install
   

3. Start Redis server:
    ```sh
   docker run --name redis -d -p 6379:6379 redis
   ```
   
4. Start the postgresSQL server:
    ```sh
   docker run --name url_shortener_db -e POSTGRES_USER=<your_postgres_user> -e POSTGRES_PASSWORD=<your_postgres_password> -e POSTGRES_DB=url_shortener -p 5432:5432 -d postgres
   ```

5. Run the application:
    ```sh
    DB_USERNAME=<your_db_username> DB_PASSWORD=<your_db_password> mvn spring-boot:run
    ```

## License

This project is licensed under the MIT License.
