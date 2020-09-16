package jp.mydns.mii.gcFileServer

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
class UserFormController {

    @Autowired
    var jdbcTemplate: JdbcTemplate? = null

    @GetMapping("/login")
    fun login(model: Model, request: HttpServletRequest): String {
        return "login"
    }

    @GetMapping("/register")
    fun register(model: Model, request: HttpServletRequest): String {
        return "register"
    }

    @GetMapping("/registerCheck/{id}")
    fun checkRegister(model: Model, request: HttpServletRequest, response: HttpServletResponse, @PathVariable("id") id: String): String {
        val checkList = jdbcTemplate?.queryForList("SELECT * FROM registerCheck WHERE id = ?",id)

        if (checkList?.get(0)?.get("id") as String == id) {

        }

        return "login"
    }
}
