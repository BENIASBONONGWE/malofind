package com.example.malofind.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage

@Composable
fun MaloFindImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderRes: Int = android.R.drawable.ic_menu_gallery
) {
    val modelStr = model?.toString() ?: ""

    if (modelStr.isNotBlank() && modelStr.startsWith("data:image")) {
        val bitmap = rememberBitmapFromBase64(modelStr)
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
            return
        }
    }

    if (modelStr.isNotBlank()) {
        AsyncImage(
            model = model,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        Image(
            painter = painterResource(id = placeholderRes),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}

private fun rememberBitmapFromBase64(base64Str: String): Bitmap? {
    return try {
        val pureBase64 = if (base64Str.contains(",")) base64Str.substringAfter(",") else base64Str
        val decodedBytes = Base64.decode(pureBase64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        null
    }
}
