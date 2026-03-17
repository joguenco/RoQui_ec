package dev.joguenco.roqui.electronic.xml

import dev.joguenco.pdf.CreditNoteReport
import dev.joguenco.pdf.InvoiceReport
import dev.joguenco.roqui.electronic.TypeDocument
import dev.joguenco.roqui.util.DateUtil
import dev.joguenco.roqui.util.FilesUtil
import java.io.File

class PdfBuilder(
    private val accessKey: String,
    private val baseDirectory: String,
    private val pathLogo: String,
) {
    private var authorization: String = ""
    private var authorizationDate: String = ""

    constructor(
        accessKey: String,
        baseDirectory: String,
        pathLogo: String,
        authorization: String,
        authorizationDate: String,
    ) : this(accessKey, baseDirectory, pathLogo) {
        this.authorization = authorization
        this.authorizationDate = authorizationDate
    }

    fun pdf(): Boolean {
        val classLoader = PdfBuilder::class.java.classLoader
        val dateAccessKey = DateUtil.accessKeyToDate(accessKey)
        val reportFolder = classLoader.getResource("./report").path

        val pathXmlFile =
            FilesUtil.directory(baseDirectory + "${File.separatorChar}generated", dateAccessKey)

        val pdfOutFolder =
            FilesUtil.directory(baseDirectory + "${File.separatorChar}pdf", dateAccessKey)

        val typeDocument = getTypeDocument(accessKey)

        if (typeDocument == TypeDocument.FACTURA) {
            val report =
                InvoiceReport(
                    "$pathXmlFile${File.separatorChar}$accessKey.xml",
                    reportFolder,
                    pathLogo,
                    pdfOutFolder,
                )
            return report.pdf(authorization, authorizationDate)
        } else if (typeDocument == TypeDocument.NOTA_CREDITO) {
            return CreditNoteReport(
                    "$pathXmlFile${File.separatorChar}$accessKey.xml",
                    reportFolder,
                    pathLogo,
                    pdfOutFolder,
                )
                .pdf(authorization, authorizationDate)
        }
        return false
    }

    private fun getTypeDocument(accessKey: String): TypeDocument {
        return when (accessKey.substring(8, 10)) {
            "01" -> TypeDocument.FACTURA
            "04" -> TypeDocument.NOTA_CREDITO
            else -> throw IllegalArgumentException("Invalid type document code in accessKey")
        }
    }
}
