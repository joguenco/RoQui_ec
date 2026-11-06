package dev.joguenco.roqui.electronic.controller

import dev.joguenco.roqui.parameter.service.ParameterService
import dev.joguenco.roqui.util.DateUtil
import dev.joguenco.roqui.util.FilesUtil
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/files")
class FileController {

    @Autowired lateinit var parameterService: ParameterService

    @GetMapping("/pdf/{accessKey}")
    fun getPdf(@PathVariable(value = "accessKey") accessKey: String): ResponseEntity<ByteArray> {
        if (accessKey.isEmpty()) {
            return ResponseEntity.badRequest().body("The access key is empty".toByteArray())
        }

        val baseDirectory = parameterService.getBaseDirectory()
        val dateAccessKey = DateUtil.accessKeyToDate(accessKey)

        val pdfFolder =
            FilesUtil.onlyGetDirectory(baseDirectory + "${File.separatorChar}pdf", dateAccessKey)

        val path = Paths.get("$pdfFolder${File.separatorChar}${accessKey}.pdf")
        val file = path.toFile()

        if (!file.exists()) {
            return ResponseEntity.notFound().build()
        }
        val pdf = Files.readAllBytes(path)

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdf)
    }

    @GetMapping("/xml/{accessKey}")
    fun getXml(@PathVariable(value = "accessKey") accessKey: String): ResponseEntity<ByteArray> {
        if (accessKey.isEmpty()) {
            return ResponseEntity.badRequest().body("The access key is empty".toByteArray())
        }

        val baseDirectory = parameterService.getBaseDirectory()
        val dateAccessKey = DateUtil.accessKeyToDate(accessKey)

        val folders = listOf("authorized", "refused", "signed", "generated")

        // bucle para cada carpeta de esta lista, si el archivo está ahí, devuélvelo
        for (folder in folders) {
            val xmlFolder =
                FilesUtil.onlyGetDirectory(
                    baseDirectory + "${File.separatorChar}$folder",
                    dateAccessKey,
                )

            val path = Paths.get("$xmlFolder${File.separatorChar}${accessKey}.xml")

            if (path.toFile().exists()) {
                val xml = Files.readAllBytes(path)
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(xml)
            }
        }

        return ResponseEntity.notFound().build()
    }
}
