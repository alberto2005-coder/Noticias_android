package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.URL

/**
 * Interfaz para analizar un flujo de entrada (InputStream) y convertirlo en un Bitmap.
 * Esto permite cambiar la lógica de decodificación de imágenes si es necesario.
 */
fun interface BitmapParser {
    fun parse(inputStream: InputStream): Bitmap?
}

/**
 * Implementación predeterminada de BitmapParser que utiliza BitmapFactory.
 */
class DefaultBitmapParser : BitmapParser {
    override fun parse(inputStream: InputStream): Bitmap? {
        return try {
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

/**
 * Descarga y decodifica una imagen desde una URL.
 *
 * @param bitmapParser El "parser" o decodificador a utilizar para convertir los datos de la imagen en un Bitmap.
 *                     Por defecto, usa DefaultBitmapParser.
 */
class ImageDownloader(private val bitmapParser: BitmapParser = DefaultBitmapParser()) {

    // NOTA: El uso de GlobalScope no se recomienda en código de producción,
    // ya que el trabajo no se cancela si el ImageView se destruye.
    // Para una app real, se debería usar un CoroutineScope ligado al ciclo de vida
    // del componente que llama a esta función (ej. viewModelScope).
    fun download(urlString: String, imageView: ImageView) {
        GlobalScope.launch(Dispatchers.IO) {
            val bitmap = try {
                val inputStream = URL(urlString).openStream()
                inputStream.use { // 'use' cierra el stream automáticamente
                    bitmapParser.parse(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            withContext(Dispatchers.Main) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap)
                }
            }
        }
    }
}
