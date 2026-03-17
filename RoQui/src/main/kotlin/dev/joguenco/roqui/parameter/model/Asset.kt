package dev.joguenco.roqui.parameter.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "v_assets")
class Asset {
    @Id @Column(name = "id") var id: String? = null

    @Column(name = "name") var name: String? = null

    @Column(name = "value") var value: String? = null
}
