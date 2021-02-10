package com.spidchenko.week2task;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavDeepLinkBuilder;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.spidchenko.week2task.db.AppDatabase;
import com.spidchenko.week2task.db.dao.SyncImageDao;
import com.spidchenko.week2task.db.models.SyncImage;
import com.spidchenko.week2task.network.ServiceGenerator;
import com.spidchenko.week2task.network.models.Image;
import com.spidchenko.week2task.repositories.ImageRepository;
import com.spidchenko.week2task.ui.MainActivity;

import java.util.List;
import java.util.concurrent.Executor;

public class SyncWorker extends Worker {
    private static final String TAG = "SyncWorker.LOG_TAG";
    private static final String CHANNEL_ID = "sync_images_channel";
    private static final int NOTIFICATION_ID = 42;
    public static final String SEARCH_STRING = "SEARCH_STRING";

    private final Executor mExecutor = new AppExecutors().diskIO();

    public SyncWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        createNotificationChannel();
        syncImages(getInputData().getString(SEARCH_STRING));

        return Result.success();
    }

    private void syncImages(String searchRequest) {
        ImageRepository imageRepository = ImageRepository.getInstance(ServiceGenerator.getFlickrApi());

        imageRepository.updateImages(searchRequest, result -> {
            if (result instanceof com.spidchenko.week2task.network.Result.Error) {
//                handleError((com.spidchenko.week2task.network.Result.Error<List<Image>>) result);
                Log.d(TAG, "syncImages: Error");
                // TODO return failure from worker
            } else {
                mExecutor.execute(() -> {
                    int numNewImages = 0;
                    SyncImageDao syncImageDao = AppDatabase.getInstance(getApplicationContext()).syncImageDao();
                    for (Image image : ((com.spidchenko.week2task.network.Result.Success<List<Image>>) result).data) {
                        Log.d(TAG, "syncImages: " + image.getUrl(Image.PIC_SIZE_MEDIUM));
                        SyncImage syncImage = new SyncImage(image, searchRequest);
                        long insertedId = syncImageDao.addSyncImage(syncImage);
                        if (insertedId > 0) {
                            numNewImages++;
                        }
                        Log.d(TAG, "syncImages: new ID " + insertedId);
                    }
                    showNotification(numNewImages, searchRequest);
                });
            }
        });

    }

    private void showNotification(int numNewImages, String searchRequest) {
        PendingIntent intent = new NavDeepLinkBuilder(getApplicationContext())
                .setComponentName(MainActivity.class)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.syncImagesFragment)
                .createPendingIntent();

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_sync)
                        .setContentTitle(getApplicationContext()
                                .getString(R.string.sync_notification_title, searchRequest))
                        .setContentText(getApplicationContext().getString(R.string.sync_notification_text, numNewImages))
                        .setContentIntent(intent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getApplicationContext().getString(R.string.channel_name);
            String description = getApplicationContext().getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getApplicationContext()
                    .getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

