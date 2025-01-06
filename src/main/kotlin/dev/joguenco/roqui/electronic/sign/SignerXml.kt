package dev.joguenco.roqui.electronic.sign

import dev.joguenco.roqui.signer.Signer
import dev.joguenco.roqui.util.DateUtil
import dev.joguenco.roqui.util.FilesUtil
import java.io.File

class SignerXml(
    private val accessKey: String,
    private val baseDirectory: String,
    private val certificatePath: String,
    private val certificatePassword: String
) {

    fun sign(): Boolean {
        val dateAccessKey = DateUtil.accessKeyToDate(accessKey)

        val pathGenerated = FilesUtil
            .directory(
                baseDirectory + "${File.separatorChar}Generated",
                dateAccessKey
            )
        val pathSigned = FilesUtil
            .directory(
                baseDirectory + "${File.separatorChar}Signed",
                dateAccessKey
            )

        val signer = Signer()
        signer.sign(
            certificatePath,
            certificatePassword,
            "$pathGenerated${File.separatorChar}$accessKey.xml",
            "$pathSigned${File.separatorChar}$accessKey.xml"
        )

        return true
    }
}