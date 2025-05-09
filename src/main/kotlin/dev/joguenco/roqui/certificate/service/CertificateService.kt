package dev.joguenco.roqui.certificate.service

import dev.joguenco.roqui.certificate.dto.CertificateDto
import dev.joguenco.roqui.parameter.service.ParameterService
import dev.joguenco.roqui.util.FilesUtil.Companion.isDirectoryExists
import dev.joguenco.roqui.util.FilesUtil.Companion.isFileExists
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore
import java.security.cert.X509Certificate
import java.util.*
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class CertificateService(
    private val parameterService: ParameterService,
    private val certificateFileService: CertificateFileService,
) {

    fun getCertificateInformation(): CertificateDto {
        val certificateFile = parameterService.getCertificatePath()
        val certificatePassword = parameterService.getCertificatePassword()

        val keyStore = KeyStore.getInstance("PKCS12")
        FileInputStream(certificateFile).use { fis ->
            keyStore.load(fis, certificatePassword.toCharArray())
        }

        val aliases = keyStore.aliases()
        var certificate: X509Certificate? = null
        var daysToExpiry: Int = 0

        while (aliases.hasMoreElements()) {
            val alias = aliases.nextElement()
            certificate = keyStore.getCertificate(alias) as X509Certificate

            val dateNow = Date()
            daysToExpiry =
                ((certificate.notAfter.time - dateNow.time) / (1000 * 60 * 60 * 24)).toInt()
        }

        if (certificate != null) {
            return CertificateDto(
                ownerCertificate = certificate.subjectDN.toString(),
                issuerCertificate = certificate.issuerDN.toString(),
                dateExpiry = certificate.notAfter,
                dateIssued = certificate.notBefore,
                daysToExpiry = daysToExpiry,
                serialNumber = certificate.serialNumber.toString(),
            )
        }

        return CertificateDto(
            ownerCertificate = "Not found",
            issuerCertificate = "Not found",
            dateExpiry = null,
            dateIssued = null,
            daysToExpiry,
            serialNumber = "Not found",
        )
    }

    fun uploadFile(file: MultipartFile, password: String): Boolean {
        val directory = parameterService.getBaseDirectory() + File.separatorChar + "certificate"
        if (!isDirectoryExists(directory)) {
            return false
        }

        certificateFileService.uploadFile(file, password)
        return true
    }

    fun checkCertificateFile(): Boolean {
        val file = parameterService.getCertificatePath()
        return isFileExists(file)
    }
}
