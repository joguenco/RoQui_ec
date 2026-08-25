package dev.joguenco.roqui.information.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Immutable

@Entity
@Immutable
@Table(name = "v_ele_general_observations")
class GeneralObservation {

    @Id val id: Int? = null

    @Column(name = "name") val name: String? = null

    @Column(name = "value") val value: String? = null
}
