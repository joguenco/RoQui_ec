package dev.joguenco.roqui.note.delivery.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID
import org.hibernate.annotations.Immutable

/**
 * La mercaderia que va en el camion, por destinatario.
 *
 * El campo line dice a que destinatario pertenece: son las lineas de la factura de ese cliente.
 */
@Entity
@Immutable
@Table(name = "v_ele_delivery_notes_receiver_detail")
class DeliveryNoteDetail {
    @Id val id: UUID? = null

    @Column(name = "code") val code: String? = null

    @Column(name = "number") val number: String? = null

    @Column(name = "line") val line: Long? = null

    @Column(name = "principal_code") val principalCode: String? = null

    @Column(name = "name") val name: String? = null

    @Column(name = "quantity") val quantity: BigDecimal? = null
}
