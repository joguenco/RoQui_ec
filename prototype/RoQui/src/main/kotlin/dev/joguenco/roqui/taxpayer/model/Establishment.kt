package dev.joguenco.roqui.taxpayer.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Immutable

@Entity
@Immutable
@Table(name = "v_ele_establishments")
class Establishment {

    // La clave va en el codigo: en MariaDB el id se repite en todas las filas.
    @Id @Column(name = "code") val code: String? = null

    @Column(name = "identification") val identification: String? = null

    @Column(name = "business_name") val businessName: String? = null

    @Column(name = "address") val address: String? = null

    @Column(name = "principal") val principal: String? = null
}
