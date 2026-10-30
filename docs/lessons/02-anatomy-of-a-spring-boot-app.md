# Lesson 02: Anatomy of a Spring Boot app

Tag: `lesson-02`

This lesson adds the first endpoint, `GET /api/v1/health`, and looks at what start.spring.io generated. The one thing worth a test is the component-scan root.

## What's in the tag

| File | What it is |
|---|---|
| `health/HealthController.java` | `GET /api/v1/health` returns `{"status":"UP"}` |
| `elsewhere/StrayController.java` (test sources) | A valid controller placed outside the scan root, on purpose |
| `lesson02/ComponentScanTest.java` | Shows the health route works and the stray one returns 404 |

Run everything with `./mvnw test`.

## What start.spring.io gave us

| Piece | What it does |
|---|---|
| `pom.xml` with `spring-boot-starter-parent` | Pins compatible versions for every Spring Boot dependency, so most dependencies need no version |
| `spring-boot-starter-webmvc` | One dependency that brings Spring MVC, embedded Tomcat, Jackson and Logback. 38 more jars at runtime (checked with `dependency:list`). Spring Boot 4 calls it `webmvc`; older guides say `starter-web` |
| `spring-boot-devtools` | Restarts the app when classes change. Runtime and optional, so it is left out of a packaged build |
| `mvnw` and `.mvn/wrapper` | The Maven Wrapper: builds with the Maven version the project pins, with no Maven installed |
| `spring-boot-maven-plugin` | Packages the app as one runnable jar (19 MB here) with Tomcat inside; `java -jar` is the whole deployment |
| `FairsharemonSpringApplication` | The class with `main` and `@SpringBootApplication` |
| `application.yaml` | Configuration. Only the application name for now; lesson 05 goes deeper |

## `@SpringBootApplication` is three annotations

- `@SpringBootConfiguration`: marks the class as a source of bean definitions.
- `@EnableAutoConfiguration`: lets Boot configure beans based on what is on the classpath. Lesson 04 opens this up.
- `@ComponentScan`: finds `@Component`, `@Service`, `@RestController` and friends.

I confirmed with `javap` that on Spring Boot 4.1.2 `@SpringBootApplication` still carries all three.

## The scan root

`@ComponentScan` with no arguments scans the package of the annotated class and its sub-packages. Here that is `io.github.minhkakart.fairsharemon`.

`ComponentScanTest` puts a normal `@RestController` in `io.github.minhkakart.elsewhere`, a sibling package:

- The context starts with no warning or error.
- `context.getBeanNamesForType(StrayController.class)` is empty.
- `GET /api/v1/stray` returns 404, and the log has no WARN or ERROR line.

If you really need another package scanned, `@SpringBootApplication(scanBasePackages = ...)` works. Moving the main class to the common root is simpler.

## Package by feature

```
io.github.minhkakart.fairsharemon
├─ FairsharemonSpringApplication
├─ health/   HealthController
├─ money/    Money, EvenSplit
└─ ...       members/, expenses/ and so on, each with its own controller, service and repository
```

Compared with top-level `controllers/`, `services/` and `repositories/`:

- Everything for a feature sits in one place, so it is read and changed together.
- Each feature stays under the scan root without any configuration.

**Limit:** this is a convention. Nothing stops one feature from importing another's internals. A tool like ArchUnit can enforce it as a test.

## The health endpoint

`HealthController` returns a plain record, so the body is `{"status":"UP"}`. Lesson 08 wraps every response in one envelope. Lesson 36 adds Spring Boot Actuator for liveness and readiness; this endpoint exists only so there is something to call from day one.

## Running it

```sh
./mvnw spring-boot:run          # during development
./mvnw package && java -jar target/fairsharemon-spring-0.0.1-SNAPSHOT.jar
curl localhost:8080/api/v1/health
```

On my machine the packaged jar logged `Started FairsharemonSpringApplication in 1.549 seconds` (one run, not an average) on Apache Tomcat 11.0.27.
