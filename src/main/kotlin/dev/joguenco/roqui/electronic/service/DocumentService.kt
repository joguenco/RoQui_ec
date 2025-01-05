package dev.joguenco.roqui.electronic.service

import dev.joguenco.roqui.electronic.model.Document
import dev.joguenco.roqui.electronic.repository.DocumentRepository
import org.springframework.stereotype.Service

@Service
class DocumentService(private val documentRepository: DocumentRepository) {

    fun saveDocument(document: Document) {
        documentRepository.saveDocument(document)
    }

    fun getByCodeAndNumber(code: String, number: String): Document {
        return documentRepository.findByCodeAndNumber(code, number)
    }
}