package jp.mydns.mii.gcFileServer

class Data {
    private val repository = DataRepository.getInstance()
    fun getSession(ip: String): SessionData? {
        var sessionData: SessionData? = null
        if (repository.sessions.containsKey(ip)) {
            sessionData = DataRepository.getInstance().sessions[ip]
        } else {
            sessionData = SessionData(ip, "")
            DataRepository.getInstance().sessions[ip] = sessionData
        }

        return sessionData
    }
}
