package dev.joguenco.roqui.electronic.repository

import dev.joguenco.roqui.electronic.model.Document

interface CustomDocumentRepository {

    fun findByCodeAndNumber(code: String, number: String): Document

    fun saveDocument(document: Document)
}
