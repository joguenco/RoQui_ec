package dev.joguenco.roqui.electronic.repository

import dev.joguenco.roqui.electronic.model.Document
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
class DocumentRepository : CustomDocumentRepository {

    @PersistenceContext
    lateinit var entityManager: EntityManager

    override fun findByCodeAndNumber(code: String, number: String): Document {
        val documentResult = entityManager.createQuery(
            "from Document " +
                    "where code = :code " +
                    "and number = :number"
        )
            .setParameter("code", code)
            .setParameter("number", number)
            .resultList

        if (documentResult.isEmpty()){
            throw NoSuchElementException("No document found with code=$code and number=$number")
        }

        return documentResult.get(0) as Document
    }

    override fun saveDocument(document: Document) {
        entityManager.persist(document)
    }
}