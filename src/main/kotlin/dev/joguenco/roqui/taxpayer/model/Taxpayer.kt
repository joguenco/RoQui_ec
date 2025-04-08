package dev.joguenco.roqui.taxpayer.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Immutable

@Entity
@Immutable
@Table(name = "v_ele_taxpayer")
class Taxpayer {
    @Id val id: Int? = null

    @Column(name = "identification") val identification: String? = null

    @Column(name = "legal_name") val legalName: String? = null

    @Column(name = "forced_accounting") val forcedAccounting: String? = null

    @Column(name = "special_taxpayer") val specialTaxpayer: String? = null

    @Column(name = "retention_agent") val retentionAgent: String? = null

    @Column(name = "other") val other: String? = null
}
