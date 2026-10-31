package dev.joguenco.roqui.version.service

import dev.joguenco.roqui.version.repository.VersionRepository
import org.springframework.stereotype.Service

@Service
class VersionService(private val versionRepository: VersionRepository) {

    fun getVersion() = versionRepository.findById(1).get().versionDatabase!!
}
