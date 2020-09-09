package jp.mydns.mii.gcFileServer

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/file")
class FileController {

    @Autowired
    var jdbcTemplate: JdbcTemplate? = null

    @RequestMapping("/upload", method = [RequestMethod.POST])
    fun uploadFile(@RequestParam("files") files: List<MultipartFile>, model: Model?, request: HttpServletRequest): String? {
        val sessionData = Data().getSession(request.remoteAddr)
        if (sessionData?.id == "") { return "home" }
        val basePath = System.getProperty("user.dir") + "\\files\\${sessionData?.id}\\"
        for (file in files) {
            println("getOriginalFilename=" + file.originalFilename)
            savefile(file, basePath + file.originalFilename?.replace("<","")?.replace(">", ""), sessionData!!)
        }
        return "home"
    }




    private fun savefile(file: MultipartFile, path: String, sessionData: SessionData) {
        val filename: String? = file.originalFilename
        val uploadfile: Path = Paths.get(path)
        try {
            Files.newOutputStream(uploadfile, StandardOpenOption.CREATE).use { os ->
                val bytes = file.bytes
                os.write(bytes)
            }
            jdbcTemplate?.update("INSERT INTO files VALUES (?, ?, ?, ?, NOW(), ?);", sessionData.id, file.originalFilename, UUID.randomUUID().toString(), path, file.size)
        } catch (e: IOException) {

        }
    }
}