package dev.joguenco.roqui.withhold.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID
import org.hibernate.annotations.Immutable

/** Una linea de retencion: que se retuvo, sobre que base y cuanto. */
@Entity
@Immutable
@Table(name = "v_ele_withholds_detail")
class WithholdDetail {

    @Id val id: UUID? = null

    @Column(name = "code") val code: String? = null

    @Column(name = "number") val number: String? = null

    @Column(name = "line") val line: Long? = null

    // 1 = Renta, 2 = IVA
    @Column(name = "tax_code") val taxCode: String? = null

    @Column(name = "withhold_code") val withholdCode: String? = null

    @Column(name = "base_value") val baseValue: BigDecimal? = null

    @Column(name = "percentage") val percentage: BigDecimal? = null

    @Column(name = "withholded_value") val withholdedValue: BigDecimal? = null

    @Column(name = "code_support") val codeSupport: String? = null
}
