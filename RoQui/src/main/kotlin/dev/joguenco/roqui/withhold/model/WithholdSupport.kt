package dev.joguenco.roqui.withhold.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.Date
import java.util.UUID
import org.hibernate.annotations.Immutable

/**
 * Un bloque docSustento de la retencion.
 *
 * Hay uno por cada sustento tributario que use la compra: si mezcla mercaderia y servicios salen
 * dos, ambos apuntando a la misma factura pero con su propio codigo, sus totales y sus retenciones.
 */
@Entity
@Immutable
@Table(name = "v_ele_withholds_support")
class WithholdSupport {
    @Id val id: UUID? = null

    @Column(name = "code") val code: String? = null

    @Column(name = "number") val number: String? = null

    @Column(name = "code_support") val codeSupport: String? = null

    @Column(name = "code_document_support") val codeDocumentSupport: String? = null

    @Column(name = "number_document_support") val numberDocumentSupport: String? = null

    @Column(name = "date_document_support", columnDefinition = "DATE")
    val dateDocumentSupport: Date? = null

    @Column(name = "authorization_document_support")
    val authorizationDocumentSupport: String? = null

    @Column(name = "total_without_taxes") val totalWithoutTaxes: BigDecimal? = null

    @Column(name = "total") val total: BigDecimal? = null
}
