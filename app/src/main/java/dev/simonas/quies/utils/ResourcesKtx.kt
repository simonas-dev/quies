package dev.simonas.quies.utils

import android.content.Context
import android.content.res.Resources
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

@Suppress("TooGenericExceptionThrown")
fun Context.getTextFile(resourceId: Int): String {
    val body = StringBuilder()
    try {
        val inputStream = resources.openRawResource(resourceId)
        val inputStreamReader = InputStreamReader(inputStream)
        val bufferedReader = BufferedReader(inputStreamReader)
        var nextLine: String?
        while (bufferedReader.readLine().also { nextLine = it } != null) {
            body.append(nextLine)
            body.append('\n')
        }
    } catch (e: IOException) {
        throw RuntimeException(
            "Could not open resource: $resourceId",
            e
        )
    } catch (nfe: Resources.NotFoundException) {
        throw RuntimeException("Resource not found: $resourceId", nfe)
    }
    return body.toString()
}
