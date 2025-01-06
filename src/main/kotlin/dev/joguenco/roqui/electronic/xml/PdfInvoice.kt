package dev.joguenco.roqui.electronic.xml

import dev.joguenco.pdf.InvoiceReport
import dev.joguenco.roqui.util.DateUtil
import dev.joguenco.roqui.util.FilesUtil
import java.io.File


class PdfInvoice(
        private val accessKey: String,
        private val baseDirectory: String,
        private val pathLogo: String
) {

    fun pdf(): Boolean {
        val classLoader = PdfInvoice::class.java.classLoader
        val dateAccessKey = DateUtil.accessKeyToDate(accessKey)
        val reportFolder = classLoader.getResource("./report").path

        val pathXmlFile = FilesUtil
                .directory(
                        baseDirectory + "${File.separatorChar}Generated",
                        dateAccessKey
                )

        val pdfOutFolder = FilesUtil
                .directory(
                        baseDirectory + "${File.separatorChar}Pdf",
                        dateAccessKey
                )

        println("pathXmlFile: $pathXmlFile")
        println("reportFolder: $reportFolder")
        println("pathLogo: $pathLogo")

        val report = InvoiceReport(
                "$pathXmlFile${File.separatorChar}$accessKey.xml",
                reportFolder,
                pathLogo,
                pdfOutFolder
        )
        return report.pdf("", "")
    }
}