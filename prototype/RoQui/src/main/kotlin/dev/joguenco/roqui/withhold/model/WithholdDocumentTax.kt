package dev.joguenco.roqui.withhold.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID
import org.hibernate.annotations.Immutable

/**
 * Impuestos del documento de sustento (impuestosDocSustento en el XML).
 *
 * No son las retenciones: son los impuestos que ya traia la compra. El XSD V2.0.0 los exige dentro
 * de cada docSustento, agrupados por tarifa.
 */
@Entity
@Immutable
@Table(name = "v_ele_withholds_document_taxes")
class WithholdDocumentTax {

    @Id val id: UUID? = null

    @Column(name = "code") val code: String? = null

    @Column(name = "number") val number: String? = null

    @Column(name = "code_support") val codeSupport: String? = null

    @Column(name = "tax_code") val taxCode: String? = null

    @Column(name = "percentage_code") val percentageCode: String? = null

    @Column(name = "tax_base") val taxBase: BigDecimal? = null

    @Column(name = "tax_iva") val taxIva: BigDecimal? = null

    @Column(name = "value") val value: BigDecimal? = null
}
