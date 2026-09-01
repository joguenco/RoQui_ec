package dev.joguenco.roqui.note.delivery.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date
import java.util.UUID
import org.hibernate.annotations.Immutable

/** Cabecera de la guia de remision: quien transporta, desde donde y cuando. */
@Entity
@Immutable
@Table(name = "v_ele_delivery_notes")
class DeliveryNote {
    @Id val id: UUID? = null

    @Column(name = "code") val code: String? = null

    @Column(name = "number") val number: String? = null

    @Column(name = "code_document") val codeDocument: String? = null

    @Column(name = "establishment") val establishment: String? = null

    @Column(name = "emission_point") val emissionPoint: String? = null

    @Column(name = "sequence") val sequence: String? = null

    @Column(name = "date", columnDefinition = "DATE") val date: Date? = null

    @Column(name = "address_start") val addressStart: String? = null

    @Column(name = "carrier_legal_name") val carrierLegalName: String? = null

    @Column(name = "carrier_identification_type") val carrierIdentificationType: String? = null

    @Column(name = "carrier_identification") val carrierIdentification: String? = null

    @Column(name = "plate") val plate: String? = null

    @Column(name = "date_start_transport", columnDefinition = "DATE")
    val dateStartTransport: Date? = null

    @Column(name = "date_end_transport", columnDefinition = "DATE")
    val dateEndTransport: Date? = null

    @Column(name = "observation") val observation: String? = null

    @Column(name = "establishment_address") val establishmentAddress: String? = null

    @Column(name = "access_key") val accessKey: String? = null
}
