package dev.joguenco.roqui.electronic.xml

import java.io.File
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory
import org.xml.sax.SAXException

fun validateXmlAgainstXsd(xmlFile: File, xsdFile: File): Pair<Boolean, String> {
    return try {
        val factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        val schema = factory.newSchema(xsdFile)
        val validator = schema.newValidator()
        validator.validate(StreamSource(xmlFile))

        Pair(true, "")
    } catch (e: SAXException) {
        Pair(false, e.message ?: "Unknown validation error")
    } catch (e: Exception) {
        Pair(false, e.message ?: "Unknown validation error")
    }
}
