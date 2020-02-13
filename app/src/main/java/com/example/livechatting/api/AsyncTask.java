package com.example.livechatting.api;

import com.example.livechatting.data.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AsyncTask {

    public interface OnPostListener {
        void onPostExecute(String result);
    }

    public static class RequestServer extends android.os.AsyncTask<String, Void, String> {

        OnPostListener listener = null;

        public void setOnPostListener(OnPostListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(params[1].getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();

                return sb.toString();
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (listener != null)
                listener.onPostExecute(s);
        }
    }

    public static class SignUp extends android.os.AsyncTask<String, Void, String> {
        OnPostListener listener;

        public void setOnPostListener(OnPostListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... params) {
            String id = params[0];
            String nick = params[1];
            String pw = params[2];

            try {
                // RequestBody: 서버에 보낼 데이터 작성
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM)
                        .addFormDataPart("id", id)
                        .addFormDataPart("nick", nick)
                        .addFormDataPart("pw", pw);
                if (params.length == 4 && params[3] != null) {
                    File file = new File(params[3]);
                    builder.addFormDataPart("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                }
                RequestBody requestBody = builder.build();

                // Attach RequestBody and Url to Request
                Request request = new Request.Builder()
                        .url(Constants.URL + Constants.SIGN_UP)
                        .post(requestBody)
                        .build();

                // Set Request to Client
                // Create a callback to process response from server
                Response response = new OkHttpClient().newCall(request).execute();
                if (response.body() != null)
                    return response.body().string();
                return "2";
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            listener.onPostExecute(s);
        }
    }
}
