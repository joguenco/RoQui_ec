package dev.joguenco.roqui.version.service

import dev.joguenco.roqui.version.repository.IVersionRepository
import org.springframework.stereotype.Service

@Service
class VersionService(private val versionRepository: IVersionRepository) {

    fun getVersion() = versionRepository.findById(1).get().versionDatabase!!
}