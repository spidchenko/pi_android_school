package com.spidchenko.week2task;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.spidchenko.week2task.db.models.Favourite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.os.Environment.DIRECTORY_PICTURES;

public class FileRepository {
    private static final String TAG = "FileRepository.LOG_TAG";
    private static final String APP_FILES_DIR = "Simple flickr client";
    private final MutableLiveData<List<File>> mImageFiles = new MutableLiveData<>();
    private final File mPhotosDirectory;
    private final File mImagesDirectory;
    private DirectoryChangeObserver mImagesObserver;
    private DirectoryChangeObserver mPhotosObserver;

    public FileRepository(Context context) {

        Log.d(TAG, "FileRepository: created");

        mPhotosDirectory = getAppSpecificAlbumStorageDir(context.getApplicationContext());
        mImagesDirectory = getPublicDirectory();
    }

    public File getPhotosDirectory() {
        return mPhotosDirectory;
    }

    public LiveData<List<File>> getImageFiles() {
        getUpdatedImageList();

        if ((mImagesObserver == null) && (mPhotosObserver == null)) {
            if (android.os.Build.VERSION.SDK_INT >= 29) {
                mPhotosObserver = new DirectoryChangeObserver(mPhotosDirectory);
                mImagesObserver = new DirectoryChangeObserver(mImagesDirectory);
            } else {
                mPhotosObserver = new DirectoryChangeObserver(mPhotosDirectory.toString());
                mImagesObserver = new DirectoryChangeObserver(mImagesDirectory.toString());
            }
            mPhotosObserver.startWatching();
            mImagesObserver.startWatching();
        }

        return mImageFiles;
    }

    private Uri getMediaUri(ContentResolver cr, String path) {
        Uri mediaUri = MediaStore.Files.getContentUri("external");
        Cursor ca = cr.query(mediaUri, new String[]{MediaStore.MediaColumns._ID}, MediaStore.MediaColumns.DATA + "=?", new String[]{path}, null);
        if (ca != null && ca.moveToFirst()) {
            int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            ca.close();
            return MediaStore.Files.getContentUri("external", id);
        }
        if (ca != null) {
            ca.close();
        }
        return null;
    }

    public void deleteFile(ContentResolver contentResolver, @NonNull File file) {
        int counter;
        Uri mediaUri = getMediaUri(contentResolver, file.getAbsolutePath());
        Log.d(TAG, "deleteFile: getUri=" + mediaUri);
        if (mediaUri != null) {
            counter = contentResolver.delete(mediaUri, null, null);
            Log.d(TAG, "Informing ContentResolver. " + counter + " public file deleted");
        } else {
            if (file.delete()) {
                Log.d(TAG, "deleteFile: Deleted private photo");
            }
        }
    }

    public void saveImage(RequestManager glide, ContentResolver contentResolver, Favourite favourite) {

        String fileName = "_";
        Pattern r = Pattern.compile("(?<=_)\\w+(?=_)");//Substring between "_" and "_"
        Matcher m = r.matcher(favourite.getUrl());
        if (m.find()) {
            fileName = m.group(0) + ".jpg";
            Log.d(TAG, "saveImage: imageName = " + fileName);
        }

        String finalFileName = fileName;
        glide.asBitmap()
                .load(favourite.getUrl())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                        try {
                            saveImage(contentResolver, resource, finalFileName);
                        } catch (FileNotFoundException e) {
                            Log.d(TAG, "onResourceReady: ERROR" + e);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    private void saveImage(ContentResolver contentResolver, Bitmap bitmap, String fileName) throws FileNotFoundException {

        if (android.os.Build.VERSION.SDK_INT >= 29) {
            ContentValues values = contentValues();
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + APP_FILES_DIR);
            values.put(MediaStore.Images.Media.IS_PENDING, true);

            Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                saveImageToStream(bitmap, contentResolver.openOutputStream(uri));
                values.put(MediaStore.Images.Media.IS_PENDING, false);
                contentResolver.update(uri, values, null, null);
                Log.d(TAG, "saveImage: Uri=" + uri);
            }
        } else {
            File file = new File(getPublicDirectory(), fileName);
            saveImageToStream(bitmap, new FileOutputStream(file));
            ContentValues values = contentValues();
            values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            // .DATA is deprecated in API 29
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
    }

    private ContentValues contentValues() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        //values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values;
    }

    private void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
        Log.d(TAG, "saveImageToStream");
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getUpdatedImageList() {
        Log.d(TAG, "getUpdatedImageList: UPDATING...");
        List<File> imageFiles = getFilesInDirectory(mPhotosDirectory);
        imageFiles.addAll(getFilesInDirectory(mImagesDirectory));
        mImageFiles.postValue(imageFiles);
    }


    @NonNull
    private File getPublicDirectory() {
        // getExternalStorageDirectory is deprecated in API 29
        File pictureFolder = new File(Environment.getExternalStoragePublicDirectory(
                DIRECTORY_PICTURES), APP_FILES_DIR);
        Log.d(TAG, "getPublicDirectory: picFolder: " + pictureFolder);
        if (!pictureFolder.exists()) {
            if (!pictureFolder.mkdir()) {
                Log.d(TAG, "Failed to create public directory: " + pictureFolder);
            }
        }
        return pictureFolder;
    }

    @NonNull
    private File getAppSpecificAlbumStorageDir(@NonNull Context context) {
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), APP_FILES_DIR);
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        Log.d(TAG, "getAppSpecificAlbumStorageDir: " + file);
        return file;
    }

    private ArrayList<File> getFilesInDirectory(File directory) {
        if (directory != null) {
            File[] files = directory.listFiles();
            if (files != null) {
                Log.d(TAG, "getFilesInDirectory:  We have " + Objects.requireNonNull(directory.listFiles()).length + " files!");
                return new ArrayList<>(Arrays.asList(files));
            }
        }
        return new ArrayList<>();
    }


    private class DirectoryChangeObserver extends FileObserver {

        @RequiresApi(api = Build.VERSION_CODES.Q)
        public DirectoryChangeObserver(@NonNull File path) {
            super(path, CREATE | DELETE | MOVED_FROM | MOVED_TO);
            Log.d(TAG, "DirectoryChangeObserver: created on " + path);
        }

        public DirectoryChangeObserver(@NonNull String path) {
            super(path, CREATE | DELETE | MOVED_FROM | MOVED_TO);
            Log.d(TAG, "DirectoryChangeObserver: created on " + path);
        }

        @Override
        public void onEvent(int event, @Nullable String path) {
            Log.d(TAG, "onEvent: Event " + event + ". path " + path);
            getUpdatedImageList();
        }
    }

}
