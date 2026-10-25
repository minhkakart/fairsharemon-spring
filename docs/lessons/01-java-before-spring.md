# Lesson 01: 7 Java details to check before writing Spring code

Tag: `lesson-01`

This lesson adds no Spring feature. It adds one type the whole backend will use, `Money`, and a test for each Java detail that can hurt in code that handles money.

## What's in the tag

| File | What it is |
|---|---|
| `money/Money.java` | A record holding a non-negative amount of dong, normalized to scale 0 |
| `money/EvenSplit.java` | Splits a total into shares that add up exactly, giving the leftover dong to the first shares |
| `money/MoneyTest.java`, `money/EvenSplitTest.java` | Tests for the two types above |
| `lesson01/JavaDetailsTest.java` | One test per detail below, so each claim can be checked by running it |

Run everything with `./mvnw test`.

## The 7 details

1. **`BigDecimal.equals()` compares scale too.** `new BigDecimal("2.0").equals(new BigDecimal("2.00"))` is `false`. Use `compareTo(...) == 0`, or `isEqualByComparingTo()` in AssertJ. `Money` sidesteps this by always storing scale 0, so the record's own `equals()` works.
2. **Never build a `BigDecimal` from a `double`.** `new BigDecimal(0.1)` carries the binary error of the double: 55 digits after the point. Use the `String` constructor or `BigDecimal.valueOf(0.1)`.
3. **`divide()` can throw.** `800000 / 3` has no exact decimal result, so `divide(three)` throws `ArithmeticException: Non-terminating decimal expansion; no exact representable decimal result.` Pass a scale and a rounding mode, then decide who pays the leftover. `EvenSplit` gives it one dong at a time to the first shares: 266,667 + 266,667 + 266,666.
4. **`==` compares references.** `Integer` caches -128..127, so `127 == 127` is `true` and `128 == 128` is `false` for boxed values. Use `equals()` for boxed types, `UUID` and `String`.
5. **Records are for data, not for entities.** Great for requests, responses and value objects. As a JPA entity, a record can be mapped and saved, but loading it fails with `No default constructor for entity`. Entities need a no-arg constructor and shouldn't be final.
6. **Checked exceptions change transaction behavior.** By default, `@Transactional` rolls back only on unchecked exceptions (`RuntimeException` and `Error`). Throw a checked exception and the work done so far commits. Fix it with `@Transactional(rollbackFor = Exception.class)` or by making business exceptions extend `RuntimeException`. Lesson 15 covers this in full.
7. **`Instant` for "when", `LocalDate` for "which day".** 06:00 on 6 March in `Asia/Ho_Chi_Minh` is `2026-03-05T23:00:00Z`. Take the date in UTC and you get the 5th, not the 6th.

Details 1 to 4 and 7 are covered by `JavaDetailsTest`. Details 5 and 6 need JPA and transactions, which this project doesn't have yet. I checked them in a throwaway project with Spring Data JPA and H2 on Spring Boot 4.1.2:

- Detail 5: persisting a record entity worked; `find` threw `org.hibernate.InstantiationException: No default constructor for entity`.
- Detail 6: after a checked exception, the row was still there (1 row). With `rollbackFor = Exception.class`, or with an unchecked exception, there were 0 rows.

## Why `Money` looks the way it does

```java
public record Money(BigDecimal amount) {

    public Money {
        Objects.requireNonNull(amount, "amount");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount must be >= 0");
        }
        // VND has no minor unit: 2.00 -> 2, 1.5 -> ArithmeticException
        amount = amount.setScale(0, RoundingMode.UNNECESSARY);
    }
}
```

- The compact constructor runs on every instance, so no `Money` can hold a negative or fractional amount.
- `setScale(0, RoundingMode.UNNECESSARY)` turns `2.00` into `2`, and throws on `1.5` instead of silently rounding someone's money.
- **Limit:** this only fits VND. Several currencies would need a currency code, and a scale per currency.

## Extras that didn't fit the post

- **`Optional` is for return values.** Don't use it for fields, method parameters or JPA entity attributes. Return `Optional<User>` from a lookup; don't store one.
- **Type erasure.** At runtime a `List<Foo>` is just a `List`, so `restClient.get().retrieve().body(List.class)` can't know the element type. Spring's `ParameterizedTypeReference<List<Foo>>` captures the type through an anonymous subclass, which keeps its generic superclass in the class file.
- **Virtual threads.** Java 21+ can run each request on a cheap virtual thread, so blocking calls don't tie up a platform thread. In Spring Boot it is one property: `spring.threads.virtual.enabled=true`. Worth knowing before reaching for reactive code. Lesson 39 comes back to it.
