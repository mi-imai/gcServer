package jp.mydns.mii.gcFileServer

import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode

@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
data class SessionData(var ip: String, var id: String, var sessionid: String)