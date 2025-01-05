package dev.joguenco.roqui.invoice.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Immutable
import java.math.BigDecimal

@Entity
@Immutable
@Table(name = "v_ele_invoices_detail")
class InvoiceDetail {

    @Id
    val id: Long? = null

    @Column(name = "code")
    val code: String? = null

    @Column(name = "number")
    val number: String? = null

    @Column(name = "principal_code")
    val principalCode: String? = null

    @Column(name = "line")
    val line: Long? = null

    @Column(name = "name")
    val name: String? = null

    @Column(name = "quantity")
    val quantity: BigDecimal? = null

    @Column(name = "unit")
    val unit: String? = null

    @Column(name = "unit_price")
    val unitPrice: BigDecimal? = null

    @Column(name = "tax_code")
    val taxCode: String? = null

    @Column(name = "tax_iva")
    val taxIva: BigDecimal? = null

    @Column(name = "value_iva")
    val valueIva: BigDecimal? = null

    @Column(name = "discount")
    val discount: BigDecimal? = null

    @Column(name = "total_price_without_tax")
    val totalPriceWithoutTax: BigDecimal? = null
}