package dev.joguenco.roqui.note.delivery.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date
import org.hibernate.annotations.Immutable

/** Listado de guias con su estado en el SRI. */
@Entity
@Immutable
@Table(name = "v_ele_report_delivery_notes")
class ReportDeliveryNote {

    @Id val id: Long? = null
    @Column(name = "code") val code: String? = null

    @Column(name = "number") val number: String? = null

    @Column(name = "access_key") val accessKey: String? = null

    @Column(name = "date", columnDefinition = "DATE") val date: Date? = null

    /** El transportista, no el destinatario. */
    @Column(name = "identification") val identification: String? = null

    @Column(name = "legal_name") val legalName: String? = null

    @Column(name = "plate") val plate: String? = null

    @Column(name = "status") val status: String? = null
}
