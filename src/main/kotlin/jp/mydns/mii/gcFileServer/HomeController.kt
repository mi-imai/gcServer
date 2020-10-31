package jp.mydns.mii.gcFileServer

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.math.roundToInt


@Controller
class HomeController {

    @Autowired
    var jdbcTemplate: JdbcTemplate? = null
    @Autowired
    var mailSender: MailSender? = null


    val repository = DataRepository.getInstance()

    @GetMapping("")
    fun index(model: Model, request: HttpServletRequest): String {

        if (request.getSession(true) == null) {
            return "redirect:/login"
        }

        val list = jdbcTemplate?.queryForList("SELECT * FROM users")

        println(list)

        if (request.cookies == null) {
            return "redirect:/login"
        }

        val sessionData = Data().getSession(request.remoteAddr, request.cookies.first { it.name == "JSESSIONID" }.value)
                ?: return "redirect:/login"

        model.addAttribute("sessionData", sessionData)


        val stringBuilder = StringBuilder()
        val path = "/home/mii/server/files/${sessionData.id}/"
        val folder = File(path)
        if (!folder.exists()) {
            folder.mkdirs()
        }

        model.addAttribute("files", stringBuilder.toString())

        println("sessionID : ${sessionData?.id}")

        return if (sessionData?.id != "") {
            "home"
        } else {
            "redirect:/login"
        }
    }






    fun calcFileSize(size: Long): String {

        val b = 1024
        val mb = 1048576
        val gb = 1073741824
        var target = 0
        var unit = ""

        when {
            size >= gb -> {
                target = gb
                unit = "GB"
            }

            size >= mb -> {
                target = mb
                unit = "MB"
            }

            else -> {
                target = b
                unit = "KB"
            }
        }

        val newSize = size / target


        return "$newSize$unit"
    }
}
