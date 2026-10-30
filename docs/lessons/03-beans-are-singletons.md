# Lesson 03: Spring beans are singletons by default

Tag: `lesson-03`

This lesson adds two small pieces: a `Clock` bean, so time can be controlled in tests, and a map of money formatters that Spring assembles for us. The idea behind both is how the container creates and shares beans.

## What's in the tag

| File | What it is |
|---|---|
| `time/TimeConfig.java` | `@Configuration` with one `@Bean`: the system UTC `Clock` |
| `time/BusinessCalendar.java` | `today()` in `Asia/Ho_Chi_Minh`, built on the injected `Clock` |
| `money/MoneyFormatter.java` + `PlainMoneyFormatter`, `GroupedMoneyFormatter` | One interface, two `@Component` implementations |
| `money/MoneyFormatters.java` | Receives `Map<String, MoneyFormatter>` and picks a formatter by style |
| `lesson03/SingletonScopeTest.java` | Shows one shared instance, a prototype bean, and a field leaking between requests |
| `time/BusinessCalendarTest.java`, `money/MoneyFormattersTest.java` | Tests for the two pieces above |

Run everything with `./mvnw test`.

## The container and its beans

Spring's IoC container creates the objects you mark with `@Component`, `@Service`, `@Repository`, `@RestController` or declare with `@Bean`, and hands them to whoever needs them. Those objects are called beans.

- **`@Component`** is the generic marker. `@Service` and `@Repository` are the same thing with a clearer name (`@Repository` also translates persistence exceptions, which matters from lesson 11).
- **`@Configuration` + `@Bean`** is for objects you can't annotate, such as `Clock`, or that need setup code.
- **Constructor injection.** A class with a single constructor needs no `@Autowired`: Spring passes in the beans the constructor asks for. Fields can then be `final`.

## Singleton is the default scope

The container creates each bean once and gives that same instance to everyone. `SingletonScopeTest` checks it:

- `context.getBean(X.class)` twice returns the same object (`isSameAs`).
- A bean declared with `@Scope("prototype")` is a new object each time.

For a web app this means every HTTP request, on whichever thread Tomcat picks, goes through the same service object.

### What that does to a service

`ForgetfulService` in the test is written the way a plain Java class would be: it keeps the current payer in a field.

```java
service.startRequest("An");    // request A
service.startRequest("Binh");  // request B starts before A is done
service.describe();            // request A reads "paid by Binh"
```

The test runs this on one thread, so it is deterministic. With real threads it gets worse, because updates can be lost. A plain-class replica (no Spring) with 8 threads each adding 1 a hundred thousand times to a shared field should end at 800,000. Three runs on my machine ended at 129,990, 254,399 and 800,000. The numbers differ on every run, which is exactly why this kind of bug survives local testing.

**The rule I'm following:** a service keeps no per-request state. Per-request data travels through parameters and return values. What a service does keep are `final` collaborators, injected through the constructor.

**Limits:**
- `@Scope("prototype")` exists, but then you manage the lifecycle yourself, and a prototype injected into a singleton is created only once, at injection time. Stateless singletons are the simpler path.
- `final` is not enough on its own. A `final List` that gets mutated is still shared state.

## The `Clock` bean

`BusinessCalendar` doesn't call `LocalDate.now()`. It receives a `Clock` and asks that:

```java
public BusinessCalendar(Clock clock) { this.clock = clock; }

public LocalDate today() { return LocalDate.now(clock.withZone(ZONE)); }
```

A test builds the object by hand with `Clock.fixed(...)`, so it needs no Spring context and no sleeping:

- `2026-03-05T23:00:00Z` is 6 March in Hanoi.
- `2026-03-05T16:59:59Z` is still 5 March.

This is the same trap as detail 7 in lesson 01: a date taken in UTC is not the user's date.

The zone is a constant for now. It becomes a configuration property in lesson 05.

## Injecting every bean of a type

If a constructor asks for `Map<String, MoneyFormatter>`, Spring passes all `MoneyFormatter` beans, keyed by bean name. `@Component("plain")` and `@Component("grouped")` set those names. `List<MoneyFormatter>` would work too.

```java
formatters.format("plain", Money.of(800_000));    // "800000"
formatters.format("grouped", Money.of(800_000));  // "800.000"
```

A new style is one new class. Nothing in `MoneyFormatters` changes. The same shape will carry the CSV exporters in lesson 27.

## Not used yet, but worth knowing

- **`@Primary` / `@Qualifier`:** when two beans of the same type exist and one is requested by type, Spring fails with `NoUniqueBeanDefinitionException`. `@Primary` marks the default; `@Qualifier("name")` picks one at the injection point. The map above avoids the problem by asking for all of them.
- **Why not field injection (`@Autowired` on a field):** the field can't be `final`, and a test can't build the object without the container.
