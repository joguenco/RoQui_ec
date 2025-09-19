package dev.joguenco.roqui.note.credit.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.Date
import org.hibernate.annotations.Immutable

@Entity
@Immutable
@Table(name = "v_ele_report_credit_notes")
class ReportCreditNote {
    @Id val id: Long? = null

    @Column(name = "code") val code: String? = null

    @Column(name = "number") val number: String? = null

    @Column(name = "access_key") val accessKey: String? = null

    @Column(name = "date", columnDefinition = "DATE") val date: Date? = null

    @Column(name = "total") val total: BigDecimal? = null

    @Column(name = "identification") val identification: String? = null

    @Column(name = "legal_name") val legalName: String? = null

    @Column(name = "email") val email: String? = null

    @Column(name = "status") val status: String? = null
}
