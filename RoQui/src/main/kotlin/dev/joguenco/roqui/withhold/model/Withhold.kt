package dev.joguenco.roqui.withhold.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date
import java.util.UUID
import org.hibernate.annotations.Immutable

@Entity
@Immutable
@Table(name = "v_ele_withholds")
class Withhold {
    @Id val id: UUID? = null

    @Column(name = "code") val code: String? = null

    @Column(name = "number") val number: String? = null

    @Column(name = "code_document") val codeDocument: String? = null

    @Column(name = "establishment") val establishment: String? = null

    @Column(name = "emission_point") val emissionPoint: String? = null

    @Column(name = "sequence") val sequence: String? = null

    @Column(name = "date", columnDefinition = "DATE") val date: Date? = null

    @Column(name = "fiscal_period") val fiscalPeriod: String? = null

    @Column(name = "identification_type") val identificationType: String? = null

    @Column(name = "identification") val identification: String? = null

    @Column(name = "legal_name") val legalName: String? = null

    @Column(name = "related") val related: String? = null

    @Column(name = "establishment_address") val establishmentAddress: String? = null

    @Column(name = "access_key") val accessKey: String? = null
}
