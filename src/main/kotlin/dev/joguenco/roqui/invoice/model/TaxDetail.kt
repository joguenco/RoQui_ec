package dev.joguenco.roqui.invoice.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import org.hibernate.annotations.Immutable

@Entity
@Immutable
@Table(name = "v_ele_taxes_detail")
class TaxDetail {

    @Id val id: Long? = null

    @Column(name = "code") val code: String? = null

    @Column(name = "number") val number: String? = null

    @Column(name = "principal_code") val principalCode: String? = null

    @Column(name = "line") val line: Long? = null

    @Column(name = "tax_code") val taxCode: String? = null

    @Column(name = "percentage_code") val percentageCode: String? = null

    @Column(name = "tax_base") val taxBase: BigDecimal? = null

    @Column(name = "tax_iva") val taxIva: BigDecimal? = null

    @Column(name = "value") val value: BigDecimal? = null
}
