package jp.mydns.mii.gcFileServer

import javax.servlet.http.HttpServletRequest

class DataRepository {

    val sessions: MutableMap<String, SessionData> = mutableMapOf()


    companion object {
        private var instance: DataRepository? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: DataRepository().also { instance = it }
        }
    }
}