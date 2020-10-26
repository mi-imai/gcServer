package jp.mydns.mii.gcFileServer

import org.apache.tomcat.util.http.fileupload.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/file")
class FileController {

    @Autowired
    var jdbcTemplate: JdbcTemplate? = null

    @RequestMapping("/upload", method = [RequestMethod.POST])
    fun uploadFile(@RequestParam("files") files: List<MultipartFile>, model: Model?, request: HttpServletRequest): String? {
        val sessionData = Data().getSession(request.remoteAddr, request.cookies.first { it.name == "JSESSIONID" }.value)
        if (sessionData?.id == "") { return "home" }
        val basePath = "/home/mii/server/files/${sessionData?.id}/"

        val dataBaseFiles = jdbcTemplate?.queryForList("SELECT * FROM files WHERE user_name = ?", sessionData?.id)
        var size: Long = 0
        dataBaseFiles?.forEach {
            size += File(it["path"] as String).length()
        }
        files.forEach {
            size += it.size
        }

        if (size >= 10737418240) {
            return "home"
        }

        for (file in files) {
            println("getOriginalFilename=" + file.originalFilename)
            saveFile(file, basePath, sessionData!!)
        }
        return "home"
    }
    @RequestMapping("/download/{id}", method = [RequestMethod.GET])
    fun downloadFile(@PathVariable("id") id: String, request: HttpServletRequest, response: HttpServletResponse) {
        val sessionData = Data().getSession(request.remoteAddr, request.cookies.first { it.name == "JSESSIONID" }.value)
        if (sessionData?.id != "") {
            val files = jdbcTemplate?.queryForList("SELECT * FROM files WHERE id = ? LIMIT 1;", id)
            if (files?.size != 0) {
                if (files?.get(0)?.get("user_name") == sessionData?.id) {
                    val path = files?.get(0)?.get("path") as String
                    try {
                        FileInputStream(path).use {
                            response.outputStream.use { os ->

                                response.contentType = "application/octet-stream"
                                response.setHeader("Content-Disposition", "attachment; filename=${files[0]["name"]}")
                                response.setContentLength(it.channel.size().toInt())
                                os.write(it.readBytes())
                                os.flush()
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }


    /*
    @RequestMapping("/download/{id}", method = [RequestMethod.GET])
    fun downloadFile(@PathVariable("id") id: String, request: HttpServletRequest): ResponseEntity<Resource> {
        val sessionData = Data().getSession(request.remoteAddr)
        if (sessionData?.id != "") {
            val files = jdbcTemplate?.queryForList("SELECT * FROM files WHERE id = ? LIMIT 1;", id)
            if (files?.size != 0) {
                if (files?.get(0)?.get("user_name") == sessionData?.id) {
                    val path = files?.get(0)?.get("path") as String
                    val file = File(path)
                    val header = HttpHeaders()
                    header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${files[0]["name"]}")
                    header.add("Cache-Control", "no-cache, no-store, must-revalidate")
                    header.add("Pragma", "no-cache")
                    header.add("Expires", "0")


                    val resource = ByteArrayResource(Files.readAllBytes(Paths.get(path)))


                    var contentType: String? = null
                    try {
                        contentType = request.servletContext.getMimeType(resource.file.absolutePath)
                    } catch (ex: IOException) {
                        println("Could not determine file type.")
                    }

                    if (contentType == null) {
                        contentType = "application/octet-stream"
                    }

                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType))
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${files[0]["name"]}\"")
                            .body(resource)
                }
            }
        }
        return ResponseEntity.ok().build()
    }

     */

    @RequestMapping("/delete/{id}", method = [RequestMethod.GET])
    fun deleteFile(@PathVariable("id") id: String, request: HttpServletRequest, response: HttpServletResponse) {
        val sessionData = Data().getSession(request.remoteAddr, request.cookies.first { it.name == "JSESSIONID" }.value)
        if (sessionData?.id == "") { response.sendRedirect("/login"); return }

        val file = jdbcTemplate?.queryForList("SELECT * FROM files WHERE id = ? LIMIT 1;", id)?.get(0)!!
        if (file["user_name"] as String != sessionData?.id) { response.sendRedirect("/"); return }

        val physicalFile = File(file["path"] as String)

        if (!physicalFile.delete()) {
            response.sendRedirect("/")
            return
        }

        jdbcTemplate?.update("DELETE FROM files WHERE id = ?", file["id"] as String)
        response.sendRedirect("/")
        return
    }



    private fun saveFile(file: MultipartFile, basePath: String, sessionData: SessionData) {
        val filename: String? = file.originalFilename
        val uuid = UUID.randomUUID().toString()
        val uploadFile = basePath + uuid

        try {
            Files.copy(file.inputStream, Paths.get(basePath).resolve(uuid))
            /*
            Files.newOutputStream(Paths.get(uploadFile), StandardOpenOption.CREATE).use { os ->
                val bytes = file.bytes

                os.write(bytes)
            }
             */
            jdbcTemplate?.update("INSERT INTO files VALUES (?, ?, ?, ?, NOW(), ?);", sessionData.id, file.originalFilename?.replace("<", "")?.replace(">", ""), uuid, uploadFile, file.size)

        } catch (e: IOException) {

        }
    }
}