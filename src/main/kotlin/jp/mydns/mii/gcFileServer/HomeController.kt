package jp.mydns.mii.gcFileServer

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
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
import kotlin.math.roundToInt


@Controller
class HomeController {

    @Autowired
    var jdbcTemplate: JdbcTemplate? = null

    val repository = DataRepository.getInstance()

    @GetMapping("")
    fun index(model: Model, request: HttpServletRequest): String {




        val list = jdbcTemplate?.queryForList("SELECT * FROM users")

        println(list)

        val sessionData = Data().getSession(request.remoteAddr)

        model.addAttribute("sessionData", sessionData)


        val stringBuilder = StringBuilder()
        val path = System.getProperty("user.dir") + "\\files\\${sessionData?.id}"
        val folder = File(path)
        if (!folder.exists()) {
            folder.mkdirs()
        }

        val fileList = jdbcTemplate?.queryForList("SELECT * FROM files WHERE user_name = ?;", sessionData?.id)!!
        var fileSize: BigInteger = BigInteger.ZERO

        stringBuilder.append("<ul class=\"list-group\">")
        fileList.forEach {
            println(it["path"] as String)
            val file = File(it["path"] as String)
            if (file.exists()) {
                //if (file.isFile) {
                    stringBuilder.append("<li class=\"list-group-item\">" +
                                             "<div class=\"fileObject file\">" +
                                                 "<i class=\"fileIcon fas fa-file\"></i><span class=\"fileName\">${(it["name"] as String).replace("<", "\\<").replace(">", "\\>")}</span>" +
                                             "</div>" +
                                             "<button type=\"button\" class=\"btn btn-raised btn-info\" onclick=\"location.href='/file/download/${it["id"] as String}'\"><i class=\"fas fa-download\"></i></button>" +
                                             "<button type=\"button\" class=\"btn btn-raised btn-danger\" onclick=\"location.href='/file/delete/${it["id"] as String}'\"><i class=\"fas fa-trash-alt\"></i></button>" +
                                         "</li>")
                //} else {
                //    stringBuilder.append("<li class=\"list-group-item\"><div class=\"fileObject directory\"><i class=\"fileIcon fas fa-folder\"></i><span class=\"fileName\">${(it["name"] as String).replace("<", "\\<").replace(">", "\\>")}</span></div></li>")
                //}
                fileSize += it["file_size"] as BigInteger
            } else {
                jdbcTemplate?.update("DELETE FROM files WHERE id = ?", it["id"] as String)
            }
        }

        stringBuilder.append("</ul>")




        model.addAttribute("filesSize", "Files ${calcFileSize(fileSize.toLong())}/10GB<br/>Using ${((fileSize.toDouble() / 10737418240.0) * 1000).roundToInt().toDouble() / 10}%")

        /*
        val fileList = File(path).listFiles()

        stringBuilder.append("<ul class=\"list-group\">")
        fileList?.forEach {
            if (it.isFile) {
                stringBuilder.append("<li class=\"list-group-item\"><div class=\"fileObject file\"><i class=\"fileIcon fas fa-file\"></i><span class=\"fileName\">${it.name.replace("<", "\\<").replace(">", "\\>")}</span></div></li>")
            } else {
                stringBuilder.append("<li class=\"list-group-item\"><div class=\"fileObject directory\"><i class=\"fileIcon fas fa-folder\"></i><span class=\"fileName\">${it.name.replace("<", "\\<").replace(">", "\\>")}</span></div></li>")
            }
        }
        stringBuilder.append("</ul>")
        */



        model.addAttribute("files", stringBuilder.toString())

        println("sessionID : ${sessionData?.id}")

        return if (sessionData?.id != "") {
            "home"
        } else {
            "redirect:/login"
        }
    }

    @GetMapping("/login")
    fun login(model: Model, request: HttpServletRequest): String {
        return "login"
    }

    @GetMapping("/register")
    fun register(model: Model, request: HttpServletRequest): String {
        return "register"
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
