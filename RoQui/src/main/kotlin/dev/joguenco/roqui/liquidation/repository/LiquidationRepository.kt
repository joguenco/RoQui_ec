package dev.joguenco.roqui.liquidation.repository

import dev.joguenco.roqui.liquidation.model.Liquidation
import dev.joguenco.roqui.liquidation.model.LiquidationDetail
import dev.joguenco.roqui.liquidation.model.LiquidationTax
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class LiquidationRepository : CustomLiquidationRepository {
    @PersistenceContext lateinit var entityManager: EntityManager

    override fun countByCodeAndNumber(code: String, number: String): Long {
        val count =
            entityManager
                .createQuery(
                    "select count(*) from Liquidation " +
                        "where code = :code " +
                        "and number = :number"
                )
                .setParameter("code", code)
                .setParameter("number", number)
                .singleResult as Long

        return count
    }

    override fun findByCodeAndNumber(code: String, number: String): Liquidation {
        return entityManager
            .createQuery("from Liquidation where code = :code and number = :number")
            .setParameter("code", code)
            .setParameter("number", number)
            .resultList
            .get(0) as Liquidation
    }

    override fun findDetailByCodeAndNumber(
        code: String,
        number: String,
    ): MutableList<LiquidationDetail> {
        return entityManager
            .createQuery("from LiquidationDetail where code = :code and number = :number")
            .setParameter("code", code)
            .setParameter("number", number)
            .resultList as MutableList<LiquidationDetail>
    }

    override fun findDetailTax(
        code: String,
        number: String,
        principalCode: String,
        line: Long,
    ): MutableList<LiquidationTax> {
        return entityManager
            .createQuery(
                "from LiquidationTax " +
                    "where code = :code " +
                    "and number = :number " +
                    "and principalCode = :principalCode " +
                    "and line = :line"
            )
            .setParameter("code", code)
            .setParameter("number", number)
            .setParameter("principalCode", principalCode)
            .setParameter("line", line)
            .resultList as MutableList<LiquidationTax>
    }

    override fun findTotalTaxByCodeAndNumber(
        code: String,
        number: String,
    ): MutableList<LiquidationTax> {
        return entityManager
            .createQuery("from LiquidationTax where code = :code and number = :number ")
            .setParameter("code", code)
            .setParameter("number", number)
            .resultList as MutableList<LiquidationTax>
    }
}
