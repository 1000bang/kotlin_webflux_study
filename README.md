# everyDoc API

Kotlin + Spring WebFlux 단계별 학습용 프로젝트.

## 요구 사항
- Java 17

---

## Step 1~20 정리

| Step | 주제 | 다루는 내용 |
|------|------|-------------|
| **Part 1 — Kotlin 기초** |
| 1 | 코틀린 기본 문법 | 패키지, fun, 변수(val/var), 기본 타입, if/when, for/while |
| 2 | 함수와 람다 | 함수 정의, 기본/이름 인자, 단일 표현식 함수, 람다 { }, 고차 함수 |
| 3 | 클래스와 객체 | class, 생성자, 프로퍼티, data class, object(싱글톤) |
| 4 | 널 안정성 | ?, ?., ?:, !!, let, require/check |
| 5 | 컬렉션 | List/Set/Map, listOf/mapOf, 불변/가변, filter/map/forEach |
| **Part 2 — Kotlin 특징** |
| 6 | 코틀린 특징 요약 | 정적 타입, 간결한 문법, null-safe, 데이터 클래스, 확장 함수, 코루틴 등 |
| 7 | 확장 함수·프로퍼티 | fun Type.함수() 문법, 유틸을 멤버처럼 쓰기 |
| 8 | 스코프 함수 | let, run, with, apply, also 차이와 사용처 |
| 9 | 왜 코틀린을 쓰는가 | JVM 호환, 간결함, null 안전, Spring/Android 지원, 실무 채택 이유 |
| **Part 3 — WebFlux 개념** |
| 10 | 웹플럭스란 | 논블로킹 I/O, 리액티브 스트림, Spring WebFlux의 위치 |
| 11 | 웹플럭스 특징 | MVC vs WebFlux, 백프레셔, 적은 스레드로 많은 요청 |
| 12 | Reactor 기초 | Mono, Flux, subscribe, map/flatMap/filter |
| 13 | 블로킹 vs 논블로킹 | blocking 호출 시 이점 감소, subscribeOn/publishOn 개념 |
| **Part 4 — WebFlux + Kotlin 실습** |
| 14 | WebFlux 프로젝트 구성 | Spring Boot WebFlux, Kotlin 설정, 기본 의존성 |
| 15 | 리액티브 Controller | @RestController, Mono/Flux 반환, @RequestBody/@RequestPart |
| 16 | 리액티브 Service | Mono.fromCallable, map/flatMap 체이닝, onErrorResume |
| 17 | 파일 업로드 (WebFlux) | FilePart, DataBufferUtils, multipart |
| 18 | 테스트 | @WebFluxTest, WebTestClient, StepVerifier |
| **Part 5 — 정리** |
| 19 | 실전 패턴 | Controller → Service 흐름, 예외 처리, 로깅 |
| 20 | 다음 단계 | R2DBC, 코루틴 연동, 성능/모니터링 등 |

---

## 실행
```bash
./gradlew bootRun
```
- Step 1~7 확인: `GET http://localhost:8080/test/step1` ~ `/test/step7`
- 문서: `docs/STEP1.md` ~ `docs/STEP7.md`
