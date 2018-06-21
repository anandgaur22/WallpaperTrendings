package in.co.androidadda.wallpapertrending.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import in.co.androidadda.wallpapertrending.R;

/**
 * Created by Anand
 */


public class DownloadHandler extends AppCompatActivity {

    public static class ImageDownloadAndSave extends AsyncTask<String, Void, Bitmap>
    {

        Context context;

        public ImageDownloadAndSave(Context context) {
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(String... params)
        {
            downloadImagesToSdCard(params[0],params[1]);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            Toast toast = Toast.makeText(context, "Downloaded!", Toast.LENGTH_SHORT);
            //toast.setGravity(Gravity.BOTTOM, 0, 330);
            toast.show();
            Intent intent = new Intent("Refresh");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

        private void downloadImagesToSdCard(String downloadUrl, String imageName)
        {
            try
            {
                URL url = new URL(downloadUrl);
            /* making a directory in sdcard */
                String sdCard= Environment.getExternalStorageDirectory().toString();
                File myDir = new File(sdCard + context.getResources().getString(R.string.downloadLocation));

            /* checks the file and if it already exist delete */
                File file = new File(myDir, imageName);
                Log.i("DIRECTORY", myDir.toString());
                Log.i("IMAGE", imageName);
                if (file.exists ()){
                    file.delete ();
                }
                file.createNewFile();

                /* Open a connection */
                URLConnection ucon = url.openConnection();
                InputStream inputStream = null;
                HttpURLConnection httpConn = (HttpURLConnection)ucon;
                httpConn.setDoInput(true);
                //httpConn.setInstanceFollowRedirects(false);
                httpConn.setRequestMethod("GET");
                httpConn.connect();
                inputStream = httpConn.getInputStream();
                Log.i("STREAM", inputStream.toString());

                if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK)
                {
                    inputStream = httpConn.getInputStream();
                }
                else
                    Log.i("HTTP ERROR ", String.valueOf(httpConn.getResponseCode()));

                FileOutputStream fos = new FileOutputStream(file);
                int totalSize = httpConn.getContentLength();
                int downloadedSize = 0;
                byte[] buffer = new byte[2048];
                int bufferLength = 0;
                while ( (bufferLength = inputStream.read(buffer)) >0 )
                {
                    fos.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                    Log.i("Progress:","downloadedSize:"+downloadedSize+"totalSize:"+ totalSize) ;
                }

            // AFTER IMAGE DOWNLOADED
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent mediaScanIntent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(file); //out is your file you saved/deleted/moved/copied
                mediaScanIntent.setData(contentUri);
                this.context.sendBroadcast(mediaScanIntent);
            } else {
                this.context.sendBroadcast(new Intent(
                        Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("file://"
                                + Environment.getExternalStorageDirectory())));
            }
                fos.close();
                ((HttpURLConnection) ucon).disconnect();
                inputStream.close();
                Log.i("Result", "DOWNLOADED!");
            }
            catch(IOException io)
            {
                io.printStackTrace();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

}

