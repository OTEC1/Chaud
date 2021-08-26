package com.example.chaudelivery.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.net.URISyntaxException;

public class Find {
    @SuppressLint("NewApi")
    public static String get_file_selected_path(Uri uri, Context context) throws URISyntaxException {
        final boolean check_uri_path = Build.VERSION.SDK_INT >= 19;
        String[] selected_value = null;
        String selected = null;

        if (check_uri_path && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (its_external_file(uri)) {
                final String file_id = DocumentsContract.getDocumentId(uri);
                final String[] strip = file_id.split(":");
                return Environment.getExternalStorageDirectory() + File.separator + strip[1];
            } else if (its_downloaded_file(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (its_media_file(uri)) {
                final String file_id = DocumentsContract.getDocumentId(uri);
                final String[] strip = file_id.split(":");
                final String raw_data = strip[0];

                if ("video".equals(raw_data)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(raw_data)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                } else if ("image".equals(raw_data)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }

                selected = "_id=?";
                selected_value = new String[]{strip[1]};
            }
        }

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] property = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver()
                        .query(uri, property, selected, selected_value, null);
                int cursor_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst())
                    return cursor.getString(cursor_index);
            } catch (Exception ex) {
                System.out.println(ex.getLocalizedMessage());
            }
        } else
        if ("file".equalsIgnoreCase(uri.getScheme()))
            return uri.getPath();

        return  null;

    }




    public static boolean its_external_file(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean its_downloaded_file(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean its_media_file(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
