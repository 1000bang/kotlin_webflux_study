package com.everydoc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EverydocApiApplication

fun main(args: Array<String>) {
    runApplication<EverydocApiApplication>(*args)
}
