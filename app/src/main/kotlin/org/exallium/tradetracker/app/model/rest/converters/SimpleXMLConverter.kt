package org.exallium.tradetracker.app.model.rest.converters

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.lang.reflect.Type

import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.Serializer

import retrofit.converter.ConversionException
import retrofit.converter.Converter
import retrofit.mime.TypedByteArray
import retrofit.mime.TypedInput
import retrofit.mime.TypedOutput

/**
 * A [Converter] which uses SimpleXML for reading and writing entities.

 * @author Fabien Ric (fabien.ric@gmail.com)
 */
public class SimpleXMLConverter(private val serializer: Serializer, private val strict: Boolean = SimpleXMLConverter.DEFAULT_STRICT) : Converter {

    public constructor() : this(DEFAULT_STRICT) {
    }

    public constructor(strict: Boolean) : this(Persister(), strict) {
    }

    throws(javaClass<ConversionException>())
    override fun fromBody(body: TypedInput, type: Type): Any {
        try {
            return serializer.read<Any>(type as Class<*>, body.`in`(), strict)
        } catch (e: Exception) {
            throw ConversionException(e)
        }

    }

    override fun toBody(source: Any): TypedOutput {
        var osw: OutputStreamWriter? = null

        try {
            val bos = ByteArrayOutputStream()
            osw = OutputStreamWriter(bos, CHARSET)
            serializer.write(source, osw)
            osw!!.flush()
            return TypedByteArray(MIME_TYPE, bos.toByteArray())
        } catch (e: Exception) {
            throw AssertionError(e)
        } finally {
            try {
                if (osw != null) {
                    osw!!.close()
                }
            } catch (e: IOException) {
                throw AssertionError(e)
            }

        }
    }

    public fun isStrict(): Boolean {
        return strict
    }

    companion object {
        private val DEFAULT_STRICT = true
        private val CHARSET = "UTF-8"
        private val MIME_TYPE = "application/xml; charset=" + CHARSET
    }
}
