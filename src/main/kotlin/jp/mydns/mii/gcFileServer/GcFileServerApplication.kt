package jp.mydns.mii.gcFileServer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GcFileServerApplication

fun main(args: Array<String>) {
    runApplication<GcFileServerApplication>(*args)
}
