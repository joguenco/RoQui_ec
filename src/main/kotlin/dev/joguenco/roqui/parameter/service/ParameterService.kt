package dev.joguenco.roqui.parameter.service

import dev.joguenco.roqui.parameter.model.Parameter
import dev.joguenco.roqui.parameter.repository.CustomParameterRepository
import dev.joguenco.roqui.util.OwnEncryption
import java.io.File
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@Service
class ParameterService(private val parameterRepository: CustomParameterRepository) {

    @Value("\${key.property}") lateinit var keyProperty: String

    fun getBaseDirectory(): String {
        return parameterRepository.findValueByName("Base Directory")
    }

    fun getResourceDirectory(): String {
        return getBaseDirectory() + File.separatorChar + "resource"
    }

    fun getCertificatePath(): String {
        val certificatePath =
            getBaseDirectory() +
                File.separatorChar +
                "certificate" +
                File.separatorChar +
                parameterRepository.findValueByName("Certificate")

        return certificatePath
    }

    fun getCertificatePassword(): String {
        try {
            val password = parameterRepository.findValueByName("Certificate Password")
            OwnEncryption.setKey(keyProperty)
            return OwnEncryption.decrypt(password)
        } catch (e: Exception) {
            throw NoSuchElementException("No se pudo obtener la contraseña del certificado")
        }
    }

    fun getLogoJpegPath(): String {
        return getResourceDirectory() + File.separatorChar + findValueByName("Logo JPEG")
    }

    fun getLogoPngPath(): String {
        return getResourceDirectory() + File.separatorChar + findValueByName("Logo PNG")
    }

    fun getEmailTemplate(): String {
        return getResourceDirectory() + File.separatorChar + findValueByName("Template Email")
    }

    fun findByName(name: String): Parameter {
        return parameterRepository.findByName(name)
    }

    fun findValueByName(name: String): String {
        return parameterRepository.findValueByName(name)
    }

    fun update(parameter: Parameter) {
        parameterRepository.update(parameter)
    }

    fun getEmailSmtpConfiguration(): MutableList<Parameter> {
        return parameterRepository.findEmailSmtpConfiguration()
    }

    fun getSubscription(): Date {
        val value = parameterRepository.findValueByName("Subscription")
        return try {
            val suscripcionEncryptedData: String = value
            toDate(suscripcionEncryptedData, keyProperty)
        } catch (ex :Exception) {
            errorDate()
        }
    }

    fun toDate(suscripcionEncryptedData: String, key: String): Date {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        return try {
            OwnEncryption.setKey(keyProperty)
            val suscripcion = OwnEncryption.decrypt(suscripcionEncryptedData)
            formatter.parse(suscripcion)
        } catch (e: ParseException) {
            errorDate()
        } catch (e: Exception) {
            errorDate()
        }
    }

    fun errorDate(): Date {
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.add(Calendar.DATE, -1)
        return cal.time
    }
}
