package dev.joguenco.roqui.electronic.send

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class WebService {

    @Value("\${sri.url.ws.developer.reception}") lateinit var developmentReception: String

    @Value("\${sri.url.ws.developer.authorization}") lateinit var developmentAuthorization: String

    @Value("\${sri.url.ws.production.reception}") lateinit var productionReception: String

    @Value("\${sri.url.ws.production.authorization}") lateinit var productionAuthorization: String

    fun printPropertyValues() {
        println("Developer reception: $developmentReception")
        println("Developer authorization: $developmentAuthorization")
        println("Production reception: $productionReception")
        println("Production authorization: $productionAuthorization")
    }

    companion object {
        fun isAlive(urlWebServices: String): Boolean {
            var c: HttpURLConnection? = null
            try {
                val u = URI(urlWebServices).toURL()
                c = u.openConnection() as HttpURLConnection
                c.requestMethod = "GET"
                c.inputStream
                if (c.responseCode == 200) {
                    return true
                }
            } catch (e: IOException) {
                println("Error SRI web service connection : " + e.message)
                return false
            } finally {
                c?.disconnect()
            }
            return false
        }
    }
}
