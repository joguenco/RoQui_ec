package dev.joguenco.roqui.electronic.xml

import java.io.File
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory
import org.slf4j.LoggerFactory
import org.xml.sax.SAXException

private val log = LoggerFactory.getLogger("XsdValidator")

fun validateXmlAgainstXsd(xmlFile: File, xsdFile: File): Pair<Boolean, String> {
    return try {
        val factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        val schema = factory.newSchema(xsdFile)
        val validator = schema.newValidator()
        validator.validate(StreamSource(xmlFile))

        Pair(true, "")
    } catch (e: SAXException) {
        log.warn("The XML does not match the XSD: ${e.message}")
        Pair(false, e.message ?: "Unknown validation error")
    } catch (e: Exception) {
        log.error("Error validating the XML against the XSD: ${e.message}")
        Pair(false, e.message ?: "Unknown validation error")
    }
}
