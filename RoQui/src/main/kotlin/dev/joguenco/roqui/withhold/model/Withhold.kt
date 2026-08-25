package dev.joguenco.roqui.withhold.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
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

    @Column(name = "address") val address: String? = null

    // Datos del documento de sustento (la compra sobre la que se retiene)
    @Column(name = "code_support") val codeSupport: String? = null

    @Column(name = "code_document_support") val codeDocumentSupport: String? = null

    @Column(name = "number_document_support") val numberDocumentSupport: String? = null

    @Column(name = "date_document_support", columnDefinition = "DATE")
    val dateDocumentSupport: Date? = null

    @Column(name = "authorization_document_support")
    val authorizationDocumentSupport: String? = null

    @Column(name = "total_without_taxes") val totalWithoutTaxes: BigDecimal? = null

    @Column(name = "total") val total: BigDecimal? = null

    @Column(name = "establishment_address") val establishmentAddress: String? = null

    @Column(name = "access_key") val accessKey: String? = null
}
