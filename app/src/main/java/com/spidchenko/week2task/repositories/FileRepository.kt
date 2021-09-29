package com.spidchenko.week2task.repositories

import android.app.Application
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.FileObserver
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.spidchenko.week2task.db.models.Favourite
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*
import java.util.regex.Pattern

class FileRepository private constructor(application: Application) {
    private val mImageFiles = MutableLiveData<List<File>>()
    val photosDirectory: File
    private val mImagesDirectory: File
    private var mImagesObserver: DirectoryChangeObserver? = null
    private var mPhotosObserver: DirectoryChangeObserver? = null
    val imageFiles: LiveData<List<File>>
        get() {
            updatedImageList
            if (mImagesObserver == null && mPhotosObserver == null) {
                if (Build.VERSION.SDK_INT >= 29) {
                    mPhotosObserver = DirectoryChangeObserver(photosDirectory)
                    mImagesObserver = DirectoryChangeObserver(mImagesDirectory)
                } else {
                    mPhotosObserver = DirectoryChangeObserver(photosDirectory.toString())
                    mImagesObserver = DirectoryChangeObserver(mImagesDirectory.toString())
                }
                mPhotosObserver!!.startWatching()
                mImagesObserver!!.startWatching()
            }
            return mImageFiles
        }

    private fun getMediaUri(contentResolver: ContentResolver, path: String): Uri? {
        val mediaUri = MediaStore.Files.getContentUri("external")
        val cursor = contentResolver.query(
            mediaUri,
            arrayOf(MediaStore.MediaColumns._ID),
            MediaStore.MediaColumns.DATA + "=?",
            arrayOf(path),
            null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            cursor.close()
            return MediaStore.Files.getContentUri("external", id.toLong())
        }
        cursor?.close()
        return null
    }

    fun deleteFile(contentResolver: ContentResolver, file: File) {
        val counter: Int
        val mediaUri = getMediaUri(contentResolver, file.absolutePath)
        Log.d(TAG, "deleteFile: getUri=$mediaUri")
        if (mediaUri != null) {
            counter = contentResolver.delete(mediaUri, null, null)
            Log.d(TAG, "Informing ContentResolver. $counter public file deleted")
        } else {
            if (file.delete()) {
                Log.d(TAG, "deleteFile: Deleted private photo")
            }
        }
        updatedImageList
    }

    fun saveImage(glide: RequestManager, contentResolver: ContentResolver, favourite: Favourite) {
        //TODO implement Kotlin-style regexp
        var fileName = "_"
        val r = Pattern.compile("(?<=_)\\w+(?=_)") //Substring between "_" and "_"
        val m = r.matcher(favourite.url)
        if (m.find()) {
            fileName = m.group(0) + ".jpg"
            Log.d(TAG, "saveImage: imageName = $fileName")
        }
        val finalFileName = fileName
        glide.asBitmap()
            .load(favourite.url)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    try {
                        saveImage(contentResolver, resource, finalFileName)
                    } catch (e: FileNotFoundException) {
                        Log.d(TAG, "onResourceReady: ERROR $e")
                        e.printStackTrace()
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    @Throws(FileNotFoundException::class)
    private fun saveImage(contentResolver: ContentResolver, bitmap: Bitmap, fileName: String) {
        if (Build.VERSION.SDK_INT >= 29) {
            val values = contentValues()
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$APP_FILES_DIR")
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                saveImageToStream(bitmap, contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                contentResolver.update(uri, values, null, null)
                Log.d(TAG, "saveImage: Uri=$uri")
            }
        } else {
            val file = File(publicDirectory, fileName)
            saveImageToStream(bitmap, FileOutputStream(file))
            val values = contentValues()
            values.put(MediaStore.Images.Media.DATA, file.absolutePath)
            // .DATA is deprecated in API 29
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        }
    }

    private fun contentValues(): ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        //values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        Log.d(TAG, "saveImageToStream")
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val updatedImageList: Unit
        get() {
            Log.d(TAG, "getUpdatedImageList: UPDATING...")
            val imageFiles: MutableList<File> = getFilesInDirectory(
                photosDirectory
            )
            imageFiles.addAll(getFilesInDirectory(mImagesDirectory))
            mImageFiles.postValue(imageFiles)
        }

    // getExternalStorageDirectory is deprecated in API 29
    private val publicDirectory: File
        get() {
            // getExternalStorageDirectory is deprecated in API 29
            val pictureFolder = File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                ), APP_FILES_DIR
            )
            Log.d(TAG, "getPublicDirectory: picFolder: $pictureFolder")
            if (!pictureFolder.exists()) {
                if (!pictureFolder.mkdir()) {
                    Log.d(TAG, "Failed to create public directory: $pictureFolder")
                }
            }
            return pictureFolder
        }

    private fun getAppSpecificAlbumStorageDir(context: Context): File {
        val file = File(
            context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
            ), APP_FILES_DIR
        )
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created")
        }
        Log.d(TAG, "getAppSpecificAlbumStorageDir: $file")
        return file
    }

    private fun getFilesInDirectory(directory: File?): ArrayList<File> {
        if (directory != null) {
            val files = directory.listFiles()
            if (files != null) {
                Log.d(
                    TAG,
                    "getFilesInDirectory:  We have ${Objects.requireNonNull(directory.listFiles()?.size)} files!"
                )
                return ArrayList(listOf(*files))
            }
        }
        return ArrayList()
    }

    private inner class DirectoryChangeObserver : FileObserver {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        constructor(path: File) : super(path, CREATE or DELETE or MOVED_FROM or MOVED_TO) {
            Log.d(TAG, "DirectoryChangeObserver: created on $path")
        }

        constructor(path: String) : super(path, CREATE or DELETE or MOVED_FROM or MOVED_TO) {
            Log.d(TAG, "DirectoryChangeObserver: created on $path")
        }

        override fun onEvent(event: Int, path: String?) {
            Log.d(TAG, "onEvent: Event $event. path $path")
            updatedImageList
        }
    }

    companion object {
        private const val TAG = "FileRepository.LOG_TAG"
        private const val APP_FILES_DIR = "Simple flickr client"

        @Volatile
        private var sInstance: FileRepository? = null
        @JvmStatic
        fun getInstance(application: Application): FileRepository? {
            if (sInstance == null) {
                synchronized(FavouriteRepository::class.java) {
                    if (sInstance == null) {
                        sInstance = FileRepository(application)
                    }
                }
            }
            return sInstance
        }
    }

    init {
        photosDirectory = getAppSpecificAlbumStorageDir(application)
        mImagesDirectory = publicDirectory
    }
}