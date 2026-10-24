# FairShareMon: a Spring Boot backend, one lesson at a time

FairShareMon is a shared-expense ledger for trips, flatmates and team lunches. One person keeps the book, and friends don't need an account. You record who paid, who shares each bill and how much, and group bills into an event. When you close the event, you see who owes whom, with a bank-transfer QR code for each person.

This repository is the app's backend, and it doubles as a learning log. I'm learning Spring Boot in public. Each lesson adds one Spring concept and one working piece of the backend, with tests.

## Rules this backend must never break

- **Money is exact.** No floating point, and the balances in an event always sum to zero.
- **Data is private.** Another user's data must look like it doesn't exist: 404, not 403.
- **A closed event is read-only.** Forever.
- **Every change to an expense is audited** in a log nobody can edit.
- **A bank webhook can arrive twice.** The payment must count once.

## Roadmap

| Part | Topic | Lessons |
|---|---|---|
| 1 | Java and Spring Boot foundations | 01–05 |
| 2 | REST APIs with Spring MVC | 06–10 |
| 3 | Data: JPA, Flyway, transactions, locking | 11–18 |
| 4 | Security with Spring Security | 19–23 |
| 5 | Business rules: audit log, balances, usage limits, time zones | 24–30 |
| 6 | Integrations: external APIs, QR codes, webhooks, realtime updates | 31–34 |
| 7 | Production: logging, Docker, CI, performance | 35–40 |

## Following the lessons

- Each lesson is a git tag: `lesson-01`, `lesson-02`, and so on. Check one out to see the code exactly as it was at the end of that lesson:
  ```sh
  git checkout lesson-01
  ```
- Longer notes for each lesson are in [`docs/lessons/`](docs/lessons/).
- A short version of each lesson is posted on LinkedIn.

## Stack

Java 25 · Spring Boot 4 · Maven. More pieces come in as the lessons reach them: Spring Data JPA with MariaDB, Flyway, Spring Security, Redis, Testcontainers, Docker.

## Running locally

You need JDK 25.

```sh
./mvnw test              # run the tests
./mvnw spring-boot:run   # start the app
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

## Feedback

I'm learning as I go. If you see a better way to do something, open an issue or comment on the lesson post.

## License

[MIT](LICENSE)
