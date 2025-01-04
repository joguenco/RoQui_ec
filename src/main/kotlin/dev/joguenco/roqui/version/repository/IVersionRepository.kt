package dev.joguenco.roqui.version.repository

import dev.joguenco.roqui.version.model.Version
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IVersionRepository : JpaRepository<Version, Long>