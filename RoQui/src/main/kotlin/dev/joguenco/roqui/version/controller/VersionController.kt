package dev.joguenco.roqui.version.controller

import dev.joguenco.roqui.parameter.service.ParameterService
import dev.joguenco.roqui.security.util.isValidApiKey
import dev.joguenco.roqui.version.service.VersionService
import org.apache.commons.lang3.SystemUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class VersionController {

    @Autowired lateinit var parameterService: ParameterService

    @Autowired lateinit var versionService: VersionService

    @Value("\${app.version}") lateinit var appVersion: String

    @GetMapping("/version") fun getVersionV1() = Application(versionService.getVersion(), appVersion)

    @GetMapping("/roqui/v2/version")
    fun getVersionV2(
        @RequestHeader("X-API-KEY", required = false) requestApiKey: String?
    ): ResponseEntity<Any> {
        if (!isValidApiKey(requestApiKey, parameterService)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        return ResponseEntity.ok(Application(versionService.getVersion(), appVersion))
    }

    class Application(versionDatabase: String, appVersion: String) {
        val application = Properties(versionDatabase, appVersion)
    }

    class Properties(val versionDatabase: String, appVersion: String) {
        val name = "RoQui E-Invoicing for Ecuador"
        val author = "Jorge Luis"
        val versionOS: String =
            SystemUtils.OS_NAME + " " + SystemUtils.OS_VERSION + " " + SystemUtils.OS_ARCH
        val versionJava: String = SystemUtils.JAVA_VERSION
        val version = appVersion
    }
}
