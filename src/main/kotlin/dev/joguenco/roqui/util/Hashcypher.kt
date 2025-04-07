package dev.joguenco.roqui.util

import dev.joguenco.roqui.util.StringUtils.byte2hex
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import org.springframework.security.crypto.password.PasswordEncoder

class Hashcypher : PasswordEncoder {
    override fun encode(rawPassword: CharSequence?): String {
        return hashString(rawPassword.toString())
    }

    override fun matches(rawPassword: CharSequence?, encodedPassword: String?): Boolean {
        val password = rawPassword.toString()
        if ("" == encodedPassword || encodedPassword!!.startsWith("empty:")) {
            return false
        } else if (encodedPassword.startsWith("sha1:")) {
            return encodedPassword == hashString(password)
        }
        return false
    }

    private fun hashString(sPassword: String?): String {
        if (sPassword == null || sPassword == "") {
            return "empty:"
        } else {
            try {
                val md = MessageDigest.getInstance("SHA-1")
                md.update(sPassword.toByteArray(charset("UTF-8")))
                val res = md.digest()
                return "sha1:" + byte2hex(res)
            } catch (e: NoSuchAlgorithmException) {
                return "plain:$sPassword"
            } catch (e: UnsupportedEncodingException) {
                return "plain:$sPassword"
            }
        }
    }
}
