package com.example.ceritagabut.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class Utils {
    companion object {
        private const val FILE_FORMAT = "yyyyMMdd_HHmmss"

        fun getCurrentTimeStamp(): String {
            val currentTimeMillis = System.currentTimeMillis()
            val calendar = Calendar.getInstance().apply {
                timeInMillis = currentTimeMillis
            }
            val simpleDateFormat = SimpleDateFormat(FILE_FORMAT, Locale.US)
            return simpleDateFormat.format(calendar.time)
        }
    }
}

fun customTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return try {
        val timeStamp = System.currentTimeMillis()
        val fileName = "IMG_${timeStamp}.jpg"
        File.createTempFile(Utils.getCurrentTimeStamp(), null, storageDir).apply {

        }
    } catch (e: IOException) {
        e.printStackTrace()
        throw IOException("Gagal membuat file sementara.")
    }
}

fun appUriFile(selectedImg: Uri, context: Context): File {
    val content: ContentResolver = context.contentResolver
    val pickFile = customTempFile(context)

    var fileInputStream: InputStream? = null
    var fileOutputStream: OutputStream? = null
    try {
        fileInputStream = content.openInputStream(selectedImg)
        fileOutputStream = FileOutputStream(pickFile)
        val bufFile = ByteArray(DEFAULT_BUFFER_SIZE)
        var len: Int
        while (fileInputStream!!.read(bufFile).also { len = it } > 0) {
            fileOutputStream.write(bufFile, 0, len)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        throw IOException("Gagal menyalin file dari URI ke file sementara.")
    } finally {
        try {
            fileInputStream?.close()
            fileOutputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
            throw IOException("Gagal menutup InputStream atau OutputStream.")
        }
    }
    return pickFile
}


fun appReduceImage(file: File): File {
    val bitmapImage = BitmapFactory.decodeFile(file.path)
    var qualityImage = 100
    var lengthStreamImage: Int
    do {
        val bipmapStream = ByteArrayOutputStream()
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, qualityImage, bipmapStream)
        val bmpPicByteArray = bipmapStream.toByteArray()
        lengthStreamImage = bmpPicByteArray.size
        qualityImage -= 5
    } while (lengthStreamImage > 1000000)

    return try {
        val outputStream = FileOutputStream(file)
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, qualityImage, outputStream)
        outputStream.close()
        file
    } catch (e: IOException) {
        e.printStackTrace()
        throw IOException("Gagal mengompres file gambar.")
    } finally {
        bitmapImage.recycle()
    }
}

fun String.withDateFormat(): String {
    val inputFormatDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    inputFormatDate.timeZone = TimeZone.getTimeZone("UTC")
    val outputFormatDate = SimpleDateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault())
    try {
        val date = inputFormatDate.parse(this)
        return if (date != null) {
            outputFormatDate.format(date)
        } else {
            throw IllegalArgumentException("Gagal mengonversi tanggal.")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        throw IllegalArgumentException("Gagal mengonversi tanggal.")
    }
}

fun getMapAddress(context: Context, lat: Double, lon: Double): String? {
    var addressLoc: String? = null
    val geo = Geocoder(context, Locale.getDefault())
    try {
        val list = geo.getFromLocation(lat, lon, 1)
        if (!list.isNullOrEmpty()) {
            val address = list[0]
            addressLoc = buildString {
                append("Alamat: ")
                append(address.getAddressLine(0))
                append("\n")
                append("Kota: ")
                append(address.locality)
                append("\n")
                append("Provinsi: ")
                append(address.adminArea)
                append("\n")
                append("Kode Pos: ")
                append(address.postalCode)
                append("\n")
                append("Negara: ")
                append(address.countryName)
                append("\n")
            }
            Log.d("getAddressName", "Nama Alamat: $addressLoc")
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Log.e("getAddressName", "Gagal mendapatkan alamat: ${e.message}")
        addressLoc = "Gagal mendapatkan alamat: ${e.message}"
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("getAddressName", "Terjadi kesalahan: ${e.message}")
        addressLoc = "Terjadi kesalahan: ${e.message}"
    }
    return addressLoc
}