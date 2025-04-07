package dev.joguenco.roqui.version.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Immutable

@Entity
@Immutable
@Table(name = "v_version")
class Version {
    @Id val id: Int? = null

    @Column(name = "version_database") val versionDatabase: String? = null
}
