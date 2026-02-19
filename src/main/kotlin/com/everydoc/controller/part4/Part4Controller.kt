package com.everydoc.controller.part4

import com.everydoc.domain.Order
import com.everydoc.service.part4.Step14Service
import com.everydoc.service.part4.Step15Service
import com.everydoc.service.part4.Step16Service
import com.everydoc.service.part4.Step17Service
import com.everydoc.service.part4.Step18Service
import com.everydoc.service.part4.Step19Service
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Part 4 — WebFlux + Kotlin 실습 (Step 14~18)
 * - WebFlux 프로젝트 구성, 리액티브 Controller/Service, 파일 업로드 등
 */
@RestController
@RequestMapping("/test")
class Part4Controller(
    private val step14Service: Step14Service,
    private val step15Service: Step15Service,
    private val step16Service: Step16Service,
    private val step17Service: Step17Service,
    private val step18Service: Step18Service,
    private val step19Service: Step19Service,
) {

    @GetMapping("/step14")
    fun step14(): Mono<String> = step14Service.info()

    // ── Step 15: 리액티브 Controller ──────────────────────────

    /** GET /test/step15 — Mono 단건 반환 예시 */
    @GetMapping("/step15")
    fun step15(): Mono<String> = step15Service.monoExample()

    /** GET /test/step15/summary — 핵심 요약 */
    @GetMapping("/step15/summary")
    fun step15Summary(): Mono<String> = step15Service.summary()

    /** GET /test/step15/flux — Flux 다건 스트림 반환 예시 */
    @GetMapping("/step15/flux")
    fun step15Flux(): Flux<String> = step15Service.fluxExample()

    /** POST /test/step15/echo — @RequestBody를 Mono<T>로 받는 예시 */
    @PostMapping("/step15/echo")
    fun step15Echo(@RequestBody body: Mono<String>): Mono<String> =
        step15Service.echoBody(body)

    /** GET /test/step15/greet/{name}?repeat=N — @PathVariable, @RequestParam 예시 */
    @GetMapping("/step15/greet/{name}")
    fun step15Greet(
        @PathVariable name: String,
        @RequestParam(defaultValue = "1") repeat: Int,
    ): Mono<String> = step15Service.greet(name, repeat)

    // ── Step 16: 리액티브 Service ──────────────────────────

    /** GET /test/step16 — Mono.fromCallable 예시 */
    @GetMapping("/step16")
    fun step16(): Mono<String> = step16Service.fromCallableExample()

    /** GET /test/step16/summary — 핵심 요약 */
    @GetMapping("/step16/summary")
    fun step16Summary(): Mono<String> = step16Service.summary()

    /** GET /test/step16/map — map 체이닝 예시 */
    @GetMapping("/step16/map")
    fun step16Map(): Mono<String> = step16Service.mapExample()

    /** GET /test/step16/flatmap — flatMap 체이닝 예시 */
    @GetMapping("/step16/flatmap")
    fun step16FlatMap(): Mono<String> = step16Service.flatMapExample()

    /** GET /test/step16/error-resume — onErrorResume 예시 */
    @GetMapping("/step16/error-resume")
    fun step16ErrorResume(): Mono<String> = step16Service.onErrorResumeExample()

    /** GET /test/step16/error-return — onErrorReturn 예시 */
    @GetMapping("/step16/error-return")
    fun step16ErrorReturn(): Mono<String> = step16Service.onErrorReturnExample()

    /** GET /test/step16/combined — fromCallable + flatMap + onErrorResume 조합 */
    @GetMapping("/step16/combined")
    fun step16Combined(): Mono<String> = step16Service.combined()

    // ── Step 17: 파일 업로드 (WebFlux) ──────────────────────────

    /** GET /test/step17/summary — 핵심 요약 */
    @GetMapping("/step17/summary")
    fun step17Summary(): Mono<String> = step17Service.summary()

    /** POST /test/step17/info — FilePart 메타데이터 확인 */
    @PostMapping("/step17/info", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun step17Info(@RequestPart("file") filePart: FilePart): Mono<String> =
        step17Service.fileInfo(filePart)

    /** POST /test/step17/read — 파일 내용 읽기 (소용량) */
    @PostMapping("/step17/read", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun step17Read(@RequestPart("file") filePart: FilePart): Mono<String> =
        step17Service.readContent(filePart)

    /** POST /test/step17/upload — 파일 저장 (transferTo) */
    @PostMapping("/step17/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun step17Upload(@RequestPart("file") filePart: FilePart): Mono<String> =
        step17Service.saveFile(filePart)

    /** POST /test/step17/upload-with-meta — 파일 + 폼 필드 함께 받기 */
    @PostMapping("/step17/upload-with-meta", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun step17UploadWithMeta(
        @RequestPart("file") filePart: FilePart,
        @RequestPart("description") description: String,
    ): Mono<String> = step17Service.uploadWithMeta(filePart, description)

    /** POST /test/step17/upload-dbu — DataBufferUtils.write 직접 사용 */
    @PostMapping("/step17/upload-dbu", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun step17UploadDbu(@RequestPart("file") filePart: FilePart): Mono<String> =
        step17Service.saveWithDataBufferUtils(filePart)

    // ── Step 18: R2DBC ──────────────────────────

    /** GET /test/step18/summary — 핵심 요약 */
    @GetMapping("/step18/summary")
    fun step18Summary(): Mono<String> = step18Service.summary()

    /** GET /test/step18/orders — 전체 주문 조회 */
    @GetMapping("/step18/orders")
    fun step18FindAll(): Flux<Order> = step18Service.findAll()

    /** GET /test/step18/orders/{id} — id로 단건 조회 */
    @GetMapping("/step18/orders/{id}")
    fun step18FindById(@PathVariable id: Long): Mono<Order> =
        step18Service.findById(id)

    /** GET /test/step18/orders/status/{status} — status로 조회 */
    @GetMapping("/step18/orders/status/{status}")
    fun step18FindByStatus(@PathVariable status: String): Flux<Order> =
        step18Service.findByStatus(status)

    /** GET /test/step18/orders/user/{userId} — userId로 조회 */
    @GetMapping("/step18/orders/user/{userId}")
    fun step18FindByUserId(@PathVariable userId: Long): Flux<Order> =
        step18Service.findByUserId(userId)

    /** POST /test/step18/orders — 주문 저장 */
    @PostMapping("/step18/orders")
    fun step18Save(@RequestBody order: Order): Mono<Order> =
        step18Service.save(order)

    /** DELETE /test/step18/orders/{id} — 주문 삭제 */
    @DeleteMapping("/step18/orders/{id}")
    fun step18Delete(@PathVariable id: Long): Mono<String> =
        step18Service.delete(id)

    // ── Step 18: @Query (Native SQL) ──────────────────────────

    /** GET /test/step18/orders/amount/{min} — 금액 이상 조회 (@Query Native SQL) */
    @GetMapping("/step18/orders/amount/{min}")
    fun step18FindByAmount(@PathVariable min: Long): Flux<Order> =
        step18Service.findByAmountGreaterThan(min)

    /** GET /test/step18/orders/user/{userId}/sum — 총 주문 금액 합산 (@Query 집계) */
    @GetMapping("/step18/orders/user/{userId}/sum")
    fun step18SumAmount(@PathVariable userId: Long): Mono<Long> =
        step18Service.sumAmountByUserId(userId)

    /** PATCH /test/step18/orders/{id}/status — status 업데이트 (@Modifying @Query) */
    @PatchMapping("/step18/orders/{id}/status")
    fun step18UpdateStatus(
        @PathVariable id: Long,
        @RequestParam status: String,
    ): Mono<String> = step18Service.updateStatus(id, status)

    // ── Step 19: 테스트 ──────────────────────────

    /** GET /test/step19/summary — 핵심 요약 */
    @GetMapping("/step19/summary")
    fun step19Summary(): Mono<String> = step19Service.summary()

    /** GET /test/step19/greet?name=철수 — StepVerifier 테스트 대상 */
    @GetMapping("/step19/greet")
    fun step19Greet(@RequestParam name: String): Mono<String> =
        step19Service.greet(name)

    /** GET /test/step19/numbers — Flux StepVerifier 테스트 대상 */
    @GetMapping("/step19/numbers")
    fun step19Numbers(): Flux<Int> = step19Service.numbers()

    /** GET /test/step19/sum — 집계 WebTestClient 테스트 대상 */
    @GetMapping("/step19/sum")
    fun step19Sum(): Mono<Long> = step19Service.sum()
}

