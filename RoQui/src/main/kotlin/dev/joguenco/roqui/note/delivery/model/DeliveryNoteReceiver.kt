package dev.joguenco.roqui.note.delivery.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date
import java.util.UUID
import org.hibernate.annotations.Immutable

/**
 * Un destinatario de la guia: el cliente de cada factura del viaje.
 *
 * El SRI admite varios por guia, y cada uno lleva su propio motivo de traslado y su documento de
 * sustento.
 */
@Entity
@Immutable
@Table(name = "v_ele_delivery_notes_receiver")
class DeliveryNoteReceiver {
    @Id val id: UUID? = null

    @Column(name = "code") val code: String? = null

    @Column(name = "number") val number: String? = null

    @Column(name = "line") val line: Long? = null

    @Column(name = "identification_type") val identificationType: String? = null

    @Column(name = "identification") val identification: String? = null

    @Column(name = "legal_name") val legalName: String? = null

    @Column(name = "address") val address: String? = null

    @Column(name = "transfer_reason") val transferReason: String? = null

    @Column(name = "code_document_support") val codeDocumentSupport: String? = null

    @Column(name = "number_document_support") val numberDocumentSupport: String? = null

    @Column(name = "authorization_document_support")
    val authorizationDocumentSupport: String? = null

    @Column(name = "date_document_support", columnDefinition = "DATE")
    val dateDocumentSupport: Date? = null
}
