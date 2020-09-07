package jp.mydns.mii.gcFileServer
import com.mysql.cj.Session
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
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
}
