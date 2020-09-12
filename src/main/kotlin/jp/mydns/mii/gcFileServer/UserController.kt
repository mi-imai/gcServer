package jp.mydns.mii.gcFileServer

import com.fasterxml.jackson.annotation.JsonCreator
import jp.mydns.mii.gcFileServer.encrypt.Digest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

data class LoginData @JsonCreator constructor(
        val email: String,
        val password: String
)

@RestController
@RequestMapping("/api/user")
class UserController {

    @Autowired
    var jdbc: JdbcTemplate? = null
    
    @RequestMapping("/login", method = [RequestMethod.POST])
    fun loginPost(@RequestBody loginData: MultiValueMap<String, String>, request: HttpServletRequest, response: HttpServletResponse) {
        val encryptedPassword = Digest().getSHA256(loginData["password"]?.get(0)!!)
        val email = loginData["email"]?.get(0)
        val user = jdbc?.queryForList("SELECT * FROM users WHERE email = ? and encrypted_password = ? LIMIT 1;", email, encryptedPassword)
        loginData.clear()

        if (user?.count() == 0) { response.sendRedirect("/login"); return }

        val sessionData = Data().getSession(request.remoteAddr)
        sessionData?.id = user?.get(0)?.get("id") as String

        response.sendRedirect("/")
    }

    @RequestMapping("/register", method = [RequestMethod.POST])
    fun registerPost(@RequestBody registerData: MultiValueMap<String, String>, request: HttpServletRequest, response: HttpServletResponse) {
        val email = registerData["email"]?.get(0)
        val name = registerData["name"]?.get(0)
        val registerPassword = registerData["register_password"]?.get(0)
        if (registerPassword != "65536") {
            response.sendRedirect("/register")
            return
        }
        val encryptedPassword = Digest().getSHA256(registerData["password"]?.get(0)!!)

        registerData.clear()

        if (jdbc?.queryForList("SELECT * FROM users WHERE email = ? LIMIT 1;", email)?.count() != 0) { response.sendRedirect("/register"); return }
        if (jdbc?.queryForList("SELECT * FROM users WHERE name = ? LIMIT 1;", name)?.count() != 0) { response.sendRedirect("/register"); return }

        val emailPattern = Regex(pattern = "^[a-zA-Z0-9.!#\$%&'*+\\/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*\$")
        if (!emailPattern.containsMatchIn(email!!)) { response.sendRedirect("/register"); return }

        val uuid = UUID.randomUUID().toString()

        jdbc?.update("INSERT INTO users VALUES (?, ?, ?, ?, NOW(), NOW());", uuid, name, email, encryptedPassword)

        println("PASS: $encryptedPassword\nEMAIL: $email\nUUID: $uuid")

        val sessionData = Data().getSession(request.remoteAddr)
        sessionData?.id = uuid

        response.sendRedirect("/")
    }
}