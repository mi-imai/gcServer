package codes.mii.gcFileServer

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage

@SpringBootApplication
class GcFileServerApplication

fun main(args: Array<String>) {
    runApplication<GcFileServerApplication>(*args)
}
