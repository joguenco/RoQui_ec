package dev.joguenco.roqui.parameter.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "ele_parameters")
class Parameter {

    @Id @Column(name = "id") var id: Long? = null

    @Column(name = "name") var name: String? = null

    @Column(name = "value") var value: String? = null

    @Column(name = "observation") var observation: String? = null

    @Column(name = "type") var type: String? = null

    @Column(name = "status") var status: Boolean? = true
}
