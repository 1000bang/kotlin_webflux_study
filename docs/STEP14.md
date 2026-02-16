# 14단계: WebFlux 프로젝트 구성

## 목표
- Spring Boot WebFlux + Kotlin 프로젝트의 **기본 구성 요소**를 이해한다.
- `spring-boot-starter-webflux` 와 Kotlin 플러그인, Reactor 의존성을 어디서 설정하는지 안다.
- 이 프로젝트가 어떻게 **WebFlux 애플리케이션으로 기동**되는지 큰 그림을 본다.

---

## 1. Gradle 설정

이 프로젝트는 `build.gradle.kts` 로 관리한다.

- **플러그인**
  - `org.springframework.boot` : Spring Boot 애플리케이션 빌드/실행
  - `io.spring.dependency-management` : Spring BOM 기반 의존성 관리
  - `kotlin("jvm")`, `kotlin("plugin.spring")` : Kotlin + Spring 연동
- **의존성(핵심)**
  - `spring-boot-starter-webflux` : WebFlux (Netty 기반 논블로킹 웹 스택)
  - `jackson-module-kotlin` : Kotlin 데이터 클래스를 위한 JSON 직렬화
  - `reactor-kotlin-extensions` : Reactor Kotlin 확장 (예: `toMono()`)
  - `kotlinx-coroutines-reactor` : 코루틴과 Reactor 연동

요약하면, **WebFlux를 쓰려면 `spring-boot-starter-webflux` 를 포함**하고, Kotlin 프로젝트이면 위 Kotlin 플러그인/의존성을 더해 준다.

---

## 2. 애플리케이션 진입점

- `EverydocApiApplication.kt` 에서 `@SpringBootApplication` + `runApplication<...>()` 로 시작한다.
- Spring Boot가 자동으로 WebFlux 설정(Netty 서버, WebFlux Handler/Adapter 등)을 구성한다.
- `@RestController` 를 스캔해서, `Mono/Flux` 를 반환하는 Handler를 만들어 준다.

---

## 3. 패키지 구조

- `com.everydoc.controller.partX` : 단계별 Controller (`/test/stepN` 엔드포인트)
- `com.everydoc.service.partX` : 각 Step의 Service (설명 문자열, 예제 코드)
- `docs/STEPN.md` : Step별 설명 문서

Step 14 이후부터는 이 구조 위에서 **리액티브 Controller / Service 실습**이 이어진다.

---

## 4. 실전에서 어떻게 쓰는지

| 상황 | 어떻게 |
|------|--------|
| 새 WebFlux 프로젝트 시작 | Spring Initializr에서 WebFlux + Kotlin 선택, 이후 build.gradle.kts 에 의존성/플러그인 확인 |
| 기존 MVC 프로젝트를 WebFlux로 | `spring-boot-starter-web` 대신 `spring-boot-starter-webflux` 를 사용하고, Controller/Service를 Mono/Flux 패턴으로 점진 전환 |
| 이 프로젝트 이해 | build.gradle.kts, `EverydocApiApplication`, Controller/Service 패키지 구조를 한 번 훑어보면 전체 그림이 잡힌다. |

---

