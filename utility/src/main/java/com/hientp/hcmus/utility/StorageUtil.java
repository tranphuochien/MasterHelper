package com.hientp.hcmus.utility;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import timber.log.Timber;

public class StorageUtil {

    public static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    public static String getAvailableMemorySize(String pPathFile) {
        File path = new File(pPathFile);
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return formatSize(availableBlocks * blockSize);
    }

    /***
     * internal folder to extract resource file
     * @return: path to unzip folder
     */
    public static String prepareUnzipFolder(Context pContext, String folderResource) {
        //try on internal storage first
        String unzipFolder = null;
        try {
            unzipFolder = pContext.getFilesDir().getPath() + folderResource;
            File f = new File(unzipFolder);
            if (!f.isDirectory() || !f.exists()) {
                f.mkdirs();
            }
            Timber.d("folder to extract resource file :" + unzipFolder + ", available size : " + getAvailableMemorySize(unzipFolder));
            return unzipFolder;
        } catch (Exception e) {
            Timber.d(e.getMessage());
        }
        //try on external storage if can not access internal storage
        try {
            if (StorageUtil.isExternalStorageAvailable()) {
                unzipFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + folderResource;
                File f = new File(unzipFolder);
                if (!f.isDirectory() || !f.exists()) {
                    f.mkdirs();
                }
            } else {
                // Use Internal Storage to unzip file
                File downloadFolder = pContext.getDir("temp", Context.MODE_PRIVATE);
                if (downloadFolder != null && downloadFolder.isDirectory()) {
                    unzipFolder = downloadFolder.getAbsolutePath();
                }
            }
            Timber.d("folder to extract resource file :" + unzipFolder + ",available size : " + getAvailableMemorySize(unzipFolder));
            return unzipFolder;
        } catch (Exception e) {
            Timber.d(e.getMessage());
        }
        return null;
    }

    public static void decompress(byte[] compressed, String location) throws IOException {

        InputStream is;
        ZipInputStream zis;

        String filename;
        is = new ByteArrayInputStream(compressed);
        zis = new ZipInputStream(new BufferedInputStream(is));
        ZipEntry ze;
        byte[] buffer = new byte[1024];
        int count;

        while ((ze = zis.getNextEntry()) != null) {
            filename = ze.getName();

            // Need to create directories if not exists, or
            // it will generate an Exception...
            if (ze.isDirectory()) {
                String path = location + File.separator + filename;
                File fmd = new File(path);
                fmd.mkdirs();

                hideImageFromGallery(path + File.separator);

                continue;
            }

            FileOutputStream fout = new FileOutputStream(location + File.separator + filename);

            while ((count = zis.read(buffer)) != -1) {
                fout.write(buffer, 0, count);
            }

            fout.close();
            zis.closeEntry();
        }
        zis.close();

    }

    /**
     * Create .nomedia file in order to prevent gallery application shows this
     * folder into album
     *
     * @param path Local path
     * @throws IOException if it's not possible to create the file.
     */
    private static void hideImageFromGallery(String path) throws IOException {
        String NOMEDIA = ".nomedia";
        File nomediaFile = new File(path + NOMEDIA);
        if (!nomediaFile.exists()) {
            nomediaFile.createNewFile();
        }
    }

    public static void deleteRecursive(File fileOrDirectory) {
        try {
            if (fileOrDirectory.isDirectory()) {
                for (File child : fileOrDirectory.listFiles()) {
                    deleteRecursive(child);
                }
            }
            fileOrDirectory.delete();
        } catch (Exception ignored) {
            Timber.d(ignored);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String getTotalExternalMemorySize() {
        try {
            if (isExternalStorageAvailable()) {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSizeLong();
                long totalBlocks = stat.getBlockCountLong();
                return formatSize(totalBlocks * blockSize);
            }
        } catch (Exception e) {
            Timber.d("getTotalMemorySize: Exception get total memory size");
        }
        return "";
    }
}
