package com.example.igor.myapplication;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


public class MainActivity extends AppCompatActivity {

    public final String LOG_TAG = "mediadecoderexample";

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        String ac3FilePath = "/storage/self/primary/Download/jigglybuttsurround.ac3";
        String mp3FilePath = "/storage/self/primary/Download/mp3.mp3";
        String m4vFilePath = "/storage/self/primary/Download/hellblade-recrop.m4v";

        File ac3File = new File(ac3FilePath);
        File mp3File = new File(mp3FilePath);
        File m4vFile = new File(m4vFilePath);

        tryCreateDecoderForAudioFile(mp3File);

        // tryCreateDecoderForAudioFile(ac3File);



        tryCreateCodecForVideoFile(m4vFile);


    }

    private void tryCreateCodecForVideoFile(File file) {

        try {
            MediaExtractor extractor = new MediaExtractor();

            extractor.setDataSource(file.toString());

            for (int i = 0; i < extractor.getTrackCount(); i++) {
                MediaFormat mediaFormat = extractor.getTrackFormat(i);

                String strMime = mediaFormat.getString(MediaFormat.KEY_MIME);

                Log.i(LOG_TAG, "mime tipe of track is " + strMime);

                if (strMime.startsWith("video/")) {
                    Log.i(LOG_TAG, "video track");
                } else if (strMime.startsWith("audio/")) {

                    Log.i(LOG_TAG, "audio track");

                    extractor.selectTrack(i);
                    if (MediaFormat.MIMETYPE_AUDIO_AC3.equals(strMime)) {
                        ByteBuffer inputBuffer = ByteBuffer.allocate(1024);
                        while (extractor.readSampleData(inputBuffer, 0) >= 0) {

                            Log.i(LOG_TAG, "extracto read something");
                            Log.i(LOG_TAG, "=======================");
                            Log.i(LOG_TAG, "bytes = " + inputBuffer.toString());
                            Log.i(LOG_TAG, "=======================");
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void tryCreateDecoderForAudioFile(File file)
    {
        MediaExtractor extractor = new MediaExtractor();
        try {
            extractor.setDataSource(file.toString());
            int tracks = extractor.getTrackCount();
        } catch (IOException e) {
                e.printStackTrace();
        }


        MediaFormat format = extractor.getTrackFormat(0);
        String mime = format.getString(MediaFormat.KEY_MIME);
        int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);

        Log.i(LOG_TAG, "===========================");
        Log.i(LOG_TAG, "filepath " + file.getAbsolutePath());
        Log.i(LOG_TAG, "mime type : " + mime);
        Log.i(LOG_TAG, "sample rate : " + sampleRate);
        Log.i(LOG_TAG, "===========================");

        try {
            MediaCodec codec = MediaCodec.createDecoderByType(mime);
            codec.configure(format, null, null, 0);
            codec.start();
            MediaCodecInfo codecInfo = codec.getCodecInfo();
            Log.i(LOG_TAG, "codec was successfully created");
            Log.i(LOG_TAG, "codec name = " + codecInfo.getName()); // for mp3 OMX.google.mp3.decoder, for ac3 should be OMX.igor.ac3.decode
            Log.i(LOG_TAG, "codec supported types = + " + codecInfo.getSupportedTypes().toString());
            Log.i(LOG_TAG, "codec class name = " + codecInfo.getClass().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void forceCreateDecoderForAc3()
    {
        MediaFormat mediaFormat = new MediaFormat();

        try {
            MediaCodec mDecoder = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_AUDIO_AC3);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getMymeTypeOfFile(File file)
    {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        Log.i(LOG_TAG, "mime type of file is " + type);
        return type;
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showExplanation("Permission Needed", "Rationale", Manifest.permission.READ_EXTERNAL_STORAGE, 1);
            } else {
                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 1);
            }
        }
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
