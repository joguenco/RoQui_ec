package dev.joguenco.roqui.information.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Immutable

@Entity
@Immutable
@Table(name = "v_ele_information")
class Information {

    @Id val id: Long? = null

    @Column(name = "identification") val identification: String? = null

    @Column(name = "name") val name: String? = null

    @Column(name = "value") val value: String? = null
}
