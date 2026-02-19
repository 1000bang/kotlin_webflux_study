# Spring MVC vs WebFlux — 토스 같은 서비스는 왜 WebFlux를 쓸까?

최근 면접이나 기술 블로그에서 자주 등장하는 질문이 있다.

> "토스 같은 대규모 서비스는 어떤 방식으로 개발할까?"
> "왜 WebFlux가 자주 언급될까?"

이 글에서는 Spring MVC vs WebFlux 차이, Tomcat vs Netty 구조, Reactive Streams, 그리고 토스 같은 서비스에서 왜 WebFlux가 유리한지까지 정리해본다.

---

## 1. Spring MVC vs WebFlux

### Spring MVC (Blocking 모델)

Spring MVC는 전통적인 Servlet 기반 웹 프레임워크다.

**동작 방식**
- 요청 1개 → 스레드 1개 할당
- DB or 외부 API 대기 → 스레드 점유
- 응답 반환 → 스레드 반환

**특징**
- Thread-Per-Request 모델
- Blocking I/O
- 이해하기 쉽고 안정적
- 대부분의 기업 서비스에서 사용

**한계**
- 요청 수가 많아질수록 스레드 증가
- I/O 대기 시간이 길면 스레드 낭비
- 스레드 수 = 처리량 한계

### Spring WebFlux (Non-Blocking 모델)

WebFlux는 Reactive + Non-Blocking 기반 프레임워크다.

**동작 방식**
- 요청 수신 → Event Loop 처리 → I/O 대기 시 스레드 반환 → 이벤트 발생 시 다시 처리

**특징**
- Non-Blocking I/O
- Event Loop 기반
- 적은 스레드로 많은 요청 처리
- `Mono` / `Flux` 반환

### 핵심 차이

| 구분 | Spring MVC | WebFlux |
|---|---|---|
| 처리 방식 | Blocking | Non-Blocking |
| 스레드 모델 | 요청당 1개 | Event Loop |
| 동시성 한계 | 스레드 수 | CPU + 이벤트 처리 |

---

## 2. Tomcat vs Netty

### Tomcat
- Servlet Container
- Blocking 기반
- 요청마다 스레드 점유
- 안정성과 호환성 강함

### Netty
- 비동기 네트워크 프레임워크
- Event Loop 기반
- 적은 스레드로 고동시성 처리
- HTTP뿐 아니라 TCP/UDP 지원

### 차이 핵심

| 구분 | Tomcat | Netty |
|---|---|---|
| I/O 방식 | Blocking | Non-Blocking |
| 구조 | Thread Pool | Event Loop |
| 확장성 | 스레드 수에 의존 | 높은 확장성 |

WebFlux는 기본적으로 Netty 위에서 동작한다.

---

## 3. WebFlux 내부 흐름

WebFlux 요청 흐름을 간단히 정리하면 다음과 같다.

```
Client Request
↓
Netty Event Loop
↓
DispatcherHandler
↓
Controller
↓
Mono / Flux 반환
↓
subscribe 시 실행
↓
비동기 이벤트 처리
↓
Response
```

**중요한 포인트**
- `Mono` / `Flux`는 **Lazy** — `subscribe()` 시점에 실행된다
- 대기 중 스레드를 점유하지 않는다

---

## 4. Reactive Streams란?

WebFlux는 Reactive Streams 표준을 따른다.

**핵심 구성요소**
- `Publisher` → 데이터 생산
- `Subscriber` → 데이터 소비
- `Subscription` → 구독 관리
- `Backpressure` → 처리 속도 조절

**Reactor 타입**
- `Mono<T>` → 0~1개 데이터
- `Flux<T>` → 0~N개 데이터

> Reactive의 본질은 "데이터를 동기적으로 처리하는 것이 아니라, 흐름으로 다룬다"

---

## 5. 토스는 왜 WebFlux를 사용할까?

토스 같은 서비스의 특징을 생각해보자.

**외부 API 호출이 많다**
- 계좌 조회, 카드 승인, 결제 연동, 인증 시스템, 포인트 조회
- 하나의 요청에서 여러 마이크로서비스 호출이 발생한다

**고동시성 트래픽**
- 실시간 송금, 결제 승인, 대량 알림 처리

**실시간 연결**
- 알림 시스템, 이벤트 스트리밍, 채팅 / 실시간 상태 업데이트

### WebFlux가 유리한 이유

**① 여러 외부 API를 동시에 호출**

```kotlin
Mono.zip(
    userService.getUser(id),
    accountService.getAccount(id),
    pointService.getPoint(id)
).map { (user, account, point) ->
    DashboardResponse(user, account, point)
}
```

병렬 비동기 호출로 전체 응답 시간을 단축할 수 있다.

**② 실시간 알림 시스템**
- SSE 기반 스트리밍
- 장시간 연결 유지
- 적은 리소스로 많은 연결 처리

**③ 실시간 채팅 / 게임 서버**
- WebSocket
- 낮은 지연 시간
- 높은 동시 연결 처리

---

## 6. 하지만 WebFlux가 항상 정답은 아니다

WebFlux가 불리한 경우:
- 단순 CRUD 서비스
- CPU 연산 중심 서비스
- JPA 중심 구조

JPA는 Blocking이기 때문에 WebFlux 위에서 그대로 사용하면 장점이 줄어든다.

진짜 Reactive 환경을 구성하려면:
- R2DBC 사용
- 외부 API도 비동기
- 전 구간 Non-Blocking 유지

---

## 7. 결론

토스 같은 서비스는 단순히 "트래픽이 많아서" WebFlux를 쓰는 것이 아니다.

- I/O가 많고
- 여러 서비스를 병렬로 호출하고
- 고동시성을 효율적으로 처리해야 하기 때문에

WebFlux 같은 비동기 구조가 유리한 것이다.

> WebFlux는 더 빠른 기술이 아니라, **스레드를 덜 쓰는 기술**이다.

---

## 한 줄 요약

- Spring MVC는 안정적인 Blocking 모델
- WebFlux는 고동시성 I/O 중심 서비스에 적합
- 토스 같은 금융 서비스는 Reactive 아키텍처가 잘 맞는다
