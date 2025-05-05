package dev.joguenco.roqui.parameter.controller

import dev.joguenco.roqui.parameter.dto.BaseDirectoryDto
import dev.joguenco.roqui.parameter.service.ParameterService
import dev.joguenco.roqui.shared.dto.Message
import dev.joguenco.roqui.util.FilesUtil.Companion.createDirectory
import dev.joguenco.roqui.util.FilesUtil.Companion.isDirectoryExists
import java.io.File
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/roqui/v1")
class ParameterController {

    @Autowired lateinit var parameterService: ParameterService

    @GetMapping("/parameter/base/directory")
    fun getBaseDirectory(): ResponseEntity<Any> {
        val baseDirectory = parameterService.getBaseDirectory()

        if (baseDirectory.isEmpty()) {
            return ResponseEntity.noContent().build()
        }

        return ResponseEntity.ok(BaseDirectoryDto(baseDirectory))
    }

    @PostMapping("/parameter/base/directory")
    fun setBaseDirectory(@RequestBody baseDirectoryDto: BaseDirectoryDto): ResponseEntity<Any> {
        val baseDirectory = baseDirectoryDto.baseDirectory

        if (baseDirectory.isNullOrEmpty()) {
            return ResponseEntity(
                Message("Parameter base directory is required"),
                HttpStatus.BAD_REQUEST,
            )
        }

        if (!isDirectoryExists(baseDirectory)) {
            return ResponseEntity(Message("Directory does not exist"), HttpStatus.BAD_REQUEST)
        }

        val parameter = parameterService.findByName("Base Directory")
        parameter.value = baseDirectory
        parameterService.update(parameter)

        createDirectory(baseDirectory + File.separatorChar + "certificate")
        createDirectory(baseDirectory + File.separatorChar + "image")

        return ResponseEntity.ok().build()
    }
}
