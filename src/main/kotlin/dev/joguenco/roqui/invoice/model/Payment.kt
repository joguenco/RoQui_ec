package dev.joguenco.roqui.invoice.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Immutable
import java.math.BigDecimal

@Entity
@Immutable
@Table(name = "v_ele_payments")
class Payment {

    @Id
    val id: Long? = null

    @Column(name = "code")
    val code: String? = null

    @Column(name = "number")
    val number: String? = null

    @Column(name = "way_pay")
    val wayPay: String? = null

    @Column(name = "name")
    val name: String? = null

    @Column(name = "total")
    val total: BigDecimal? = null

    @Column(name = "payment_deadline")
    val paymentDeadline: BigDecimal? = null

    @Column(name = "unit_time")
    val unitTime: String? = null
}