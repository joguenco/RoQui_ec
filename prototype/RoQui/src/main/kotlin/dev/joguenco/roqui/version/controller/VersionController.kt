package dev.joguenco.roqui.version.controller

import dev.joguenco.roqui.version.service.VersionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class VersionController {

    @Autowired lateinit var versionService: VersionService

    @Value("\${app.version}") lateinit var appVersion: String

    @GetMapping("/version")
    fun getVersion(): ResponseEntity<Any> {
        return ResponseEntity.ok(Application(versionService.getVersion(), appVersion))
    }

    class Application(versionDatabase: String, appVersion: String) {
        val application = Properties(versionDatabase, appVersion)
    }

    class Properties(val versionDatabase: String, appVersion: String) {
        val name = "RoQui E-Invoicing for Ecuador"
        val author = "Jorge Luis"
        val versionOS: String =
            System.getProperty("os.name") +
                " " +
                System.getProperty("os.version") +
                " " +
                System.getProperty("os.arch")
        val versionJava: String = System.getProperty("java.version")
        val version = appVersion
    }
}
