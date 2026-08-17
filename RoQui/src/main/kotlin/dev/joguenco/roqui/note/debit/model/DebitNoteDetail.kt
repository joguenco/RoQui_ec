package dev.joguenco.roqui.note.debit.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID
import org.hibernate.annotations.Immutable

@Entity
@Immutable
@Table(name = "v_ele_debit_notes_detail")
class DebitNoteDetail {

    @Id val id: UUID? = null

    @Column(name = "code") val code: String? = null

    @Column(name = "number") val number: String? = null

    @Column(name = "line") val line: Long? = null

    @Column(name = "reason") val reason: String? = null

    @Column(name = "value") val value: BigDecimal? = null
}
