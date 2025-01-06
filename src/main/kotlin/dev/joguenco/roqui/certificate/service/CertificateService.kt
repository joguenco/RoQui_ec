package dev.joguenco.roqui.certificate.service

import dev.joguenco.roqui.certificate.dto.CertificateDto
import dev.joguenco.roqui.parameter.service.ParameterService
import org.springframework.stereotype.Service
import java.io.FileInputStream
import java.security.KeyStore
import java.security.cert.X509Certificate
import java.util.*

@Service
class CertificateService(private val parameterService: ParameterService) {

    fun getCertificateInformation(): CertificateDto {
        val certificateFile = parameterService.getCertificatePath()
        val certificatePassword = parameterService.getCertificatePassword()

        val keyStore = KeyStore.getInstance("PKCS12")
        FileInputStream(certificateFile).use { fis ->
            keyStore.load(fis, certificatePassword.toCharArray())
        }

        val aliases = keyStore.aliases()

        while (aliases.hasMoreElements()) {
            val alias = aliases.nextElement()
            val certificate = keyStore.getCertificate(alias) as X509Certificate

            val dateNow = Date()
            val daysToExpiry = ((certificate.notAfter.time - dateNow.time) / (1000 * 60 * 60 * 24)).toInt()

            return CertificateDto(
                ownerCertificate = certificate.subjectDN.toString(),
                issuerCertificate = certificate.issuerDN.toString(),
                dateExpiry = certificate.notAfter,
                dateIssued = certificate.notBefore,
                daysToExpiry = daysToExpiry,
                serialNumber = certificate.serialNumber.toString()
            )
        }

        return CertificateDto(
            ownerCertificate = "Not found",
            issuerCertificate = "Not found",
            dateExpiry = null,
            dateIssued = null,
            daysToExpiry = 0,
            serialNumber = "Not found"
        )
    }
}