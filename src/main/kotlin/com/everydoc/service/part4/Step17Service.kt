package com.everydoc.service.part4

import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

/**
 * 17단계: 파일 업로드 (WebFlux) (STEP17.md)
 * - FilePart : multipart/form-data 요청에서 파일을 표현하는 인터페이스
 * - DataBufferUtils : FilePart의 바이트 스트림을 읽거나 파일로 저장
 * - @RequestPart : Controller에서 multipart 파트를 바인딩하는 어노테이션
 */
@Service
class Step17Service {

    // ──────────────────────────────────────────────
    // 1. FilePart 메타데이터 추출
    // ──────────────────────────────────────────────

    /**
     * FilePart에서 파일 이름, Content-Type 등 메타데이터를 꺼낸다.
     * filePart.content() 를 구독하지 않으므로 파일 본문은 읽지 않는다.
     */
    fun fileInfo(filePart: FilePart): Mono<String> =
        Mono.just(
            """
            파일명: ${filePart.filename()}
            Content-Type: ${filePart.headers().contentType}
            """.trimIndent()
        )

    // ──────────────────────────────────────────────
    // 2. 파일 내용 읽기 (DataBufferUtils.join)
    // ──────────────────────────────────────────────

    /**
     * FilePart.content()는 Flux<DataBuffer> 를 반환한다.
     * DataBufferUtils.join() 으로 모든 청크를 하나의 DataBuffer로 합친 뒤
     * ByteArray로 변환해서 처리한다.
     *
     * ※ 대용량 파일을 메모리로 모두 읽으면 OOM 위험이 있다.
     *   대용량은 스트리밍 방식(아래 saveFile)을 쓴다.
     */
    fun readContent(filePart: FilePart): Mono<String> =
        DataBufferUtils.join(filePart.content())
            .map { dataBuffer ->
                val bytes = ByteArray(dataBuffer.readableByteCount())
                dataBuffer.read(bytes)
                DataBufferUtils.release(dataBuffer) // 반드시 해제
                "파일 내용 (${bytes.size} bytes): ${String(bytes).take(100)}"
            }

    // ──────────────────────────────────────────────
    // 3. 파일 저장 (DataBufferUtils.write)
    // ──────────────────────────────────────────────

    /**
     * FilePart.transferTo(path) 를 쓰면 가장 간단하게 파일을 저장할 수 있다.
     * 내부적으로 DataBufferUtils.write() 를 사용한다.
     *
     * 저장 경로: 시스템 임시 디렉터리 / 업로드된 파일명
     */
    fun saveFile(filePart: FilePart): Mono<String> {
        val uploadDir: Path = Paths.get(System.getProperty("java.io.tmpdir"), "everydoc-uploads")
        Files.createDirectories(uploadDir)
        val dest: Path = uploadDir.resolve(filePart.filename())

        return filePart.transferTo(dest)
            .thenReturn("저장 완료: ${dest.toAbsolutePath()}")
    }

    // ──────────────────────────────────────────────
    // 4. 파일 + 폼 필드 함께 받기
    // ──────────────────────────────────────────────

    /**
     * multipart 요청에서 파일(@RequestPart "file")과
     * 일반 텍스트 필드(@RequestPart "description")를 동시에 받는 패턴.
     *
     * Controller 시그니처 예시:
     *   fun upload(
     *       @RequestPart("file") filePart: FilePart,
     *       @RequestPart("description") description: String,
     *   ): Mono<String>
     */
    fun uploadWithMeta(filePart: FilePart, description: String): Mono<String> =
        saveFile(filePart)
            .map { savedPath -> "설명: $description\n$savedPath" }

    // ──────────────────────────────────────────────
    // 5. DataBufferUtils.write 직접 사용 예시
    // ──────────────────────────────────────────────

    /**
     * transferTo() 없이 DataBufferUtils.write() 를 직접 쓰는 방법.
     * 저장 경로나 OpenOption 을 세밀하게 제어할 때 사용한다.
     */
    fun saveWithDataBufferUtils(filePart: FilePart): Mono<String> {
        val uploadDir: Path = Paths.get(System.getProperty("java.io.tmpdir"), "everydoc-uploads")
        Files.createDirectories(uploadDir)
        val dest: Path = uploadDir.resolve("dbu_${filePart.filename()}")

        return DataBufferUtils.write(
            filePart.content(),
            dest,
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE,
        ).thenReturn("DataBufferUtils.write 저장 완료: ${dest.toAbsolutePath()}")
    }

    // ──────────────────────────────────────────────
    // 6. 핵심 정리
    // ──────────────────────────────────────────────

    fun summary(): Mono<String> = Mono.just(
        """
        [Step17 — 파일 업로드 (WebFlux) 핵심 요약]

        ① FilePart
           - multipart/form-data 요청에서 파일을 표현
           - filePart.filename()   : 파일명
           - filePart.headers()    : Content-Type 등 헤더
           - filePart.content()    : Flux<DataBuffer> (실제 바이트 스트림)

        ② 파일 저장 방법 (권장 순서)
           1순위: filePart.transferTo(path)          — 가장 간단
           2순위: DataBufferUtils.write(flux, path)  — 세밀한 제어 필요 시

        ③ 파일 내용 읽기
           DataBufferUtils.join(filePart.content())  — 모든 청크 → DataBuffer 1개
           → 소용량 파일에만 사용 (대용량은 스트리밍 저장 권장)
           → DataBufferUtils.release(dataBuffer) 반드시 호출!

        ④ Controller 어노테이션
           @PostMapping(consumes = [MULTIPART_FORM_DATA_VALUE])
           @RequestPart("file")  FilePart
           @RequestPart("field") String

        ⑤ MVC FileUpload와 차이
           MVC: MultipartFile (Blocking)
           WebFlux: FilePart + Flux<DataBuffer> (Non-Blocking)
        """.trimIndent()
    )
}
