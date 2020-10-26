package jp.mydns.mii.gcFileServer

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Controller
@RequestMapping("/test")
class TestController {

    @RequestMapping("")
    fun cookieTest(httpServletResponse: HttpServletResponse, httpServletRequest: HttpServletRequest) {
        httpServletResponse.addCookie(Cookie("test","tea"))
        val cookies = httpServletRequest.cookies
        cookies.forEach {
            println("${it.name}:${it.value}")
        }
    }
}