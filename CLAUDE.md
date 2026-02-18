# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Educational step-by-step project for learning Kotlin + Spring WebFlux. Each "step" has a corresponding controller endpoint, service class, and docs file.

## Commands

```bash
# Build
./gradlew build

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.everydoc.dto.DtoNullHandlingTest"

# Run the application (available at http://localhost:8080)
./gradlew bootRun
```

## Architecture

**Stack:** Kotlin 1.9.24 + Spring Boot 3.2.5 + WebFlux (Netty) + Project Reactor

**Layer structure:**
- `controller/part{N}/Part{N}Controller.kt` — HTTP handlers, expose `/test/step{N}` endpoints, return `Mono<T>` or `Flux<T>` directly
- `service/part{N}/Step{N}Service.kt` — Business logic, also return `Mono<T>` or `Flux<T>`

**Step groupings:**
| Part | Steps | Topic |
|------|-------|-------|
| part1 | 1–5 | Kotlin fundamentals (vars, functions, classes, null safety, collections) |
| part2 | 6–9 | Kotlin features (extension functions, scope functions) |
| part3 | 10–13 | WebFlux & Reactor concepts (blocking/non-blocking, Mono/Flux) |
| part4 | 14+ | Practical WebFlux + Kotlin implementation |

**Adding a new step:** Create `Step{N}Service.kt` in the matching `part{N}/` folder and wire it into the corresponding `Part{N}Controller.kt`. Add a `docs/STEP{N}.md` file documenting the concept.

## Key Conventions

- All services are annotated `@Service` and return `Mono<String>` (or other reactive types) — controllers pass these through without blocking.
- Use `Mono.just(...)` for synchronous values; `Mono.delay()` or `Schedulers.boundedElastic()` for async/blocking work.
- Docs live in `docs/STEP{N}.md` and mirror each service's purpose.
- Tests live in `src/test/kotlin/com/everydoc/` and use JUnit 5 (`useJUnitPlatform()`).
