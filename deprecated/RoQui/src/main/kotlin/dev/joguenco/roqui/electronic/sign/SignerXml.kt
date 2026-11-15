package dev.joguenco.roqui.electronic.sign

import dev.joguenco.roqui.signer.Signer
import dev.joguenco.roqui.util.DateUtil
import dev.joguenco.roqui.util.FilesUtil
import java.io.File
import org.slf4j.LoggerFactory

class SignerXml(
    private val accessKey: String,
    private val baseDirectory: String,
    private val certificatePath: String,
    private val certificatePassword: String,
) {

    private val log = LoggerFactory.getLogger(SignerXml::class.java)

    fun sign(): Pair<Boolean, String> {
        val dateAccessKey = DateUtil.accessKeyToDate(accessKey)

        val pathGenerated =
            FilesUtil.directory(baseDirectory + "${File.separatorChar}generated", dateAccessKey)
        val pathSigned =
            FilesUtil.directory(baseDirectory + "${File.separatorChar}signed", dateAccessKey)

        val signer = Signer()
        try {
            signer.sign(
                certificatePath,
                certificatePassword,
                "$pathGenerated${File.separatorChar}$accessKey.xml",
                "$pathSigned${File.separatorChar}$accessKey.xml",
            )
            return true to "Successfully signed"
        } catch (e: Exception) {
            log.error("Error signing the XML: ${e.message}")
            return false to e.message.toString().substring(e.message.toString().indexOf(":") + 1)
        }
    }
}
