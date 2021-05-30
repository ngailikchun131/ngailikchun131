package com.example.mall;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

public class RequestImage extends AsyncTask<String, Integer, Bitmap> {

        private RequestImageListener listener;

        @SuppressWarnings("unchecked")
        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL imageURL = new URL(params[0]);
                InputStream im = imageURL.openConnection().getInputStream();
                BufferedInputStream bis = new BufferedInputStream(im, 512);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(bis, null, options);

                @SuppressWarnings("IntegerDivisionInFloatingPointContext")
                int heightRatio = Math.round(options.outHeight / 192);
                @SuppressWarnings("IntegerDivisionInFloatingPointContext")
                int widthRatio = Math.round(options.outWidth / 192);

                if (heightRatio > 1 || widthRatio > 1) {
                    options.inSampleSize = Math.max(heightRatio, widthRatio);
                }

                options.inJustDecodeBounds = false;
                bis.close();
                im.close();
                im = imageURL.openConnection().getInputStream();
                bis = new BufferedInputStream(im, 512);
                Bitmap bitmap = BitmapFactory.decodeStream(bis, null, options);

                bis.close();
                im.close();

                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            super.onPostExecute(image);
            if (listener != null) {
                listener.onRequestImageFinished(image);
            }
        }

        RequestImage setListener(RequestImageListener listener) {
            this.listener = listener;
            return this;
        }

        public interface RequestImageListener {
            void onRequestImageFinished(Bitmap image);
        }
    }
