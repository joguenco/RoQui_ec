package dev.joguenco.roqui.version.controller

import dev.joguenco.roqui.version.service.VersionService
import org.apache.commons.lang3.SystemUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/roqui/v1")
class VersionController {

    @Autowired
    lateinit var versionService: VersionService

    @Value("\${app.version}")
    lateinit var appVersion : String

    @GetMapping("/version")
    fun getVersion() = Application(versionService.getVersion(), appVersion)


    class Application(versionDatabase: String, appVersion: String) {
        val application = Properties(versionDatabase, appVersion)
    }

    class Properties(val versionDatabase: String, appVersion: String) {
        val name = "RoQui E-Invoicing for Ecuador"
        val author = "Jorge Luis"
        val versionOS: String = SystemUtils.OS_NAME + " " + SystemUtils.OS_VERSION + " " + SystemUtils.OS_ARCH
        val versionJava: String = SystemUtils.JAVA_VERSION
        val version = appVersion
    }
}