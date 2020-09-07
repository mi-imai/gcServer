package jp.mydns.mii.gcFileServer

import javax.servlet.http.HttpServletRequest

class DataRepository {

    val sessions: MutableMap<String, SessionData> = mutableMapOf()


    companion object {
        // シングルトンインスタンスの宣言
        private var instance: DataRepository? = null

        // インスタンス取得
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: DataRepository().also { instance = it }
        }
    }
}