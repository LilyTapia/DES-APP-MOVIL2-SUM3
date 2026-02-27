package cl.duoc.veterinaria.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import cl.duoc.veterinaria.data.VeterinariaRepository

/**
 * Content Provider para exponer el listado de mascotas a otras aplicaciones.
 */
class VeterinariaProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "cl.duoc.veterinaria.provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/mascotas")
    }

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        val cursor = MatrixCursor(arrayOf("_id", "descripcion"))
        
        // Obtenemos los datos actuales del repositorio
        val mascotas = VeterinariaRepository.listaMascotas.value

        mascotas.forEachIndexed { index, descripcion ->
            cursor.addRow(arrayOf(index.toLong(), descripcion))
        }

        return cursor
    }

    override fun getType(uri: Uri): String = "vnd.android.cursor.dir/vnd.$AUTHORITY.mascotas"
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
}
