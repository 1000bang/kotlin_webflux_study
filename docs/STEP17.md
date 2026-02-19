# 17단계: 파일 업로드 (WebFlux)

## 목표
- `FilePart`로 multipart 요청에서 파일을 논블로킹으로 수신하는 방법을 안다.
- `DataBufferUtils`와 `transferTo()`로 파일을 저장하는 두 가지 방법을 구분한다.
- MVC의 `MultipartFile`과 WebFlux의 `FilePart` 차이를 이해한다.

---

## 1. MVC vs WebFlux 파일 업로드 비교

| 구분 | Spring MVC | Spring WebFlux |
|---|---|---|
| 파일 타입 | `MultipartFile` | `FilePart` |
| I/O 방식 | Blocking | Non-Blocking |
| 파일 스트림 | `byte[]` / `InputStream` | `Flux<DataBuffer>` |
| 저장 방법 | `transferTo(File)` | `transferTo(Path)` / `DataBufferUtils.write()` |

---

## 2. Controller 기본 형태

```kotlin
@PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
fun upload(@RequestPart("file") filePart: FilePart): Mono<String> =
    step17Service.saveFile(filePart)
```

- `consumes = [MULTIPART_FORM_DATA_VALUE]` — multipart 요청만 받겠다고 명시
- `@RequestPart("file")` — 폼의 파트 이름이 `file`인 항목을 바인딩

---

## 3. FilePart 메타데이터

```kotlin
fun fileInfo(filePart: FilePart): Mono<String> =
    Mono.just(
        """
        파일명: ${filePart.filename()}
        Content-Type: ${filePart.headers().contentType}
        """.trimIndent()
    )
```

- `filePart.filename()` — 클라이언트가 전송한 원본 파일명
- `filePart.headers()` — Content-Type 등 파트 헤더
- `filePart.content()` — `Flux<DataBuffer>` (실제 바이트 스트림, 이 시점엔 구독 안 함)

---

## 4. 파일 저장 — transferTo (권장)

```kotlin
fun saveFile(filePart: FilePart): Mono<String> {
    val dest = Paths.get(System.getProperty("java.io.tmpdir")).resolve(filePart.filename())
    return filePart.transferTo(dest)
        .thenReturn("저장 완료: ${dest.toAbsolutePath()}")
}
```

- `transferTo(path)` 는 내부적으로 `DataBufferUtils.write()` 를 호출한다.
- 반환 타입이 `Mono<Void>` 이므로, 이후 값을 돌려줄 때는 `.thenReturn(value)` 를 쓴다.

---

## 5. 파일 저장 — DataBufferUtils.write (세밀한 제어)

```kotlin
fun saveWithDataBufferUtils(filePart: FilePart): Mono<String> {
    val dest = Paths.get(System.getProperty("java.io.tmpdir")).resolve(filePart.filename())
    return DataBufferUtils.write(
        filePart.content(),
        dest,
        StandardOpenOption.CREATE,
        StandardOpenOption.WRITE,
    ).thenReturn("저장 완료: ${dest.toAbsolutePath()}")
}
```

- `StandardOpenOption` 으로 덮어쓰기/추가 쓰기 등 세밀하게 제어할 수 있다.

---

## 6. 파일 내용 읽기 — DataBufferUtils.join

```kotlin
fun readContent(filePart: FilePart): Mono<String> =
    DataBufferUtils.join(filePart.content())
        .map { dataBuffer ->
            val bytes = ByteArray(dataBuffer.readableByteCount())
            dataBuffer.read(bytes)
            DataBufferUtils.release(dataBuffer) // 메모리 누수 방지 — 반드시 호출!
            String(bytes)
        }
```

> **주의:** `join()`은 파일 전체를 메모리에 올린다. 소용량 파일에만 사용하고,
> 대용량 파일은 `transferTo()` 로 바로 저장하는 것이 안전하다.

---

## 7. 파일 + 폼 필드 함께 받기

```kotlin
@PostMapping("/upload-with-meta", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
fun uploadWithMeta(
    @RequestPart("file") filePart: FilePart,
    @RequestPart("description") description: String,
): Mono<String> = step17Service.uploadWithMeta(filePart, description)
```

- 파일 파트와 텍스트 파트를 `@RequestPart`로 각각 바인딩한다.
- 클라이언트는 `multipart/form-data` 로 요청을 보내야 한다.

**curl 테스트 예시**

```bash
# 파일만 업로드
curl -X POST http://localhost:8080/test/step17/upload \
  -F "file=@/path/to/sample.txt"

# 파일 + 설명 함께
curl -X POST http://localhost:8080/test/step17/upload-with-meta \
  -F "file=@/path/to/sample.txt" \
  -F "description=테스트 파일입니다"
```

---

## 8. 엔드포인트 정리

| Method | URL | 설명 |
|---|---|---|
| GET | `/test/step17/summary` | 핵심 요약 |
| POST | `/test/step17/info` | FilePart 메타데이터 확인 |
| POST | `/test/step17/read` | 파일 내용 읽기 (소용량) |
| POST | `/test/step17/upload` | 파일 저장 (`transferTo`) |
| POST | `/test/step17/upload-with-meta` | 파일 + 폼 필드 함께 저장 |
| POST | `/test/step17/upload-dbu` | `DataBufferUtils.write` 직접 사용 |
