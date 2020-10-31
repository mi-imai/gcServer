package jp.mydns.mii.gcFileServer

class Data {
    private val repository = DataRepository.getInstance()
    fun getSession(ip: String, id: String): SessionData? {
        var sessionData: SessionData? = null
        if (repository.sessions.containsKey(id)) {
            sessionData = DataRepository.getInstance().sessions[id]
        } else {
            sessionData = SessionData(ip, "", id)
            DataRepository.getInstance().sessions[id] = sessionData
        }

        return if (sessionData?.ip == ip) {
            sessionData
        } else {
            null
        }
    }
}
//a