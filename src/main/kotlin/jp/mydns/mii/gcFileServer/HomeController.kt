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
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import javax.servlet.http.HttpServletRequest


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
        val path = "D:\\FileServer\\test"
        val fileList = File(path).listFiles()

        stringBuilder.append("<ul class=\"list-group\">")
        fileList?.forEach {
            if (it.isFile) {
                stringBuilder.append("<li class=\"list-group-item\"><div class=\"fileObject file\"><i class=\"fas fa-file\"></i><span class=\"fileName\">${it.name.replace("<", "\\<").replace(">", "\\>")}</span></div></li>")
            } else {
                stringBuilder.append("<li class=\"list-group-item\"><div class=\"fileObject directory\"><i class=\"fas fa-folder\"></i><span class=\"fileName\">${it.name.replace("<", "\\<").replace(">", "\\>")}</span></div></li>")
            }
        }
        stringBuilder.append("</ul>")

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

    @RequestMapping("/upload", method = [RequestMethod.POST])
    fun index(@RequestParam("files") files: List<MultipartFile>, model: Model?, request: HttpServletRequest): String? {
        val sessionData = Data().getSession(request.remoteAddr)
        if (sessionData?.id == "") { return "home" }

        for (file in files) {
            println("getOriginalFilename=" + file.originalFilename)
            savefile(file)
        }
        return "home"
    }

    private fun savefile(file: MultipartFile) {
        val filename: String? = file.originalFilename
        val uploadfile: Path = Paths.get("D:\\FileServer\\test\\$filename")
        try {
            Files.newOutputStream(uploadfile, StandardOpenOption.CREATE).use { os ->
                val bytes = file.bytes
                os.write(bytes)
            }
        } catch (e: IOException) {

        }
    }
}
