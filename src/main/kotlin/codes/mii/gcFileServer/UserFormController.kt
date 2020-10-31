package codes.mii.gcFileServer

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
}
