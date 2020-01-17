package com.adafruit.bluefruit_playground;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;


public class AccelerometerModelWebServer extends NanoHTTPD {
    private final String TAG = AccelerometerModelWebServer.class.getSimpleName();


    /**
     * Common mime types for dynamic content
     */
    public static final String
            MIME_PLAINTEXT = "text/plain",
            MIME_HTML = "text/html",
            MIME_JS = "application/javascript",
            MIME_CSS = "text/css",
            MIME_PNG = "image/png",
            MIME_JSON = "application/json",
            MIME_DEFAULT_BINARY = "application/octet-stream",
            MIME_XML = "text/xml";

    private Context mContext;

    public AccelerometerModelWebServer(int port, Context ctx) {
        super(port);
        this.mContext = ctx;
    }

    //@Override
    //public Response serve(String uri, String method, Properties header, Properties parms, Properties files) {
    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Map<String,String> header = session.getHeaders();
        Log.d(TAG,"SERVE ::  URI "+uri);
        final StringBuilder buf = new StringBuilder();
        for (Map.Entry<String, String> kv : header.entrySet())
            buf.append(kv.getKey() + " : " + kv.getValue() + "\n");
        InputStream mbuffer = null;



        try {
            if(uri!=null){

                if(uri.contains(".js")){
                    mbuffer = mContext.getAssets().open("www/cpb_3d_model_wgt/" + uri.substring(1));
                    //String file_content = loadTextFromAssets(mContext, "www/cpb_3d_model_wgt/" + uri.substring(1), StandardCharsets.UTF_8);

                    //return newFixedLengthResponse(Response.Status.OK, MIME_JS, file_content);
                    return newChunkedResponse(Response.Status.OK, MIME_JS, mbuffer);
                    //return new NanoHTTPD.Response(HTTP_OK, MIME_JS, mbuffer);

                }else if(uri.contains(".css")){
                    //mbuffer = mContext.getAssets().open(uri.substring(1));
                    //return new NanoHTTPD.Response(HTTP_OK, MIME_CSS, mbuffer);
                    String file_content = loadTextFromAssets(mContext, "www/cpb_3d_model_wgt/" + uri.substring(1), StandardCharsets.UTF_8);
                    return newFixedLengthResponse(Response.Status.OK, MIME_CSS, file_content);

                }else if(uri.contains(".png")){
                    //mbuffer = mContext.getAssets().open(uri.substring(1));
                    // HTTP_OK = "200 OK" or HTTP_OK = Status.OK;(check comments)
                    //return new NanoHTTPD.Response(HTTP_OK, MIME_PNG, mbuffer);
                    Log.d(TAG, "serving: " + uri);
                    InputStreamWrapper fileStream = loadInStreamFromAssets(mContext, "www/cpb_3d_model_wgt/" + uri.substring(1));
                    //String file_content = loadTextFromAssets(mContext, "www/cpb_3d_model_wgt/" + uri.substring(1), StandardCharsets.UTF_8);
                    Log.d(TAG, "length: " + fileStream.length);
                    return newFixedLengthResponse(Response.Status.OK, MIME_PNG, fileStream.is, fileStream.length);
                }else if(uri.contains(".json")){
                    mbuffer = mContext.getAssets().open("www/cpb_3d_model_wgt/" + uri.substring(1));
                    // HTTP_OK = "200 OK" or HTTP_OK = Status.OK;(check comments)
                    //return new NanoHTTPD.Response(HTTP_OK, MIME_PNG, mbuffer);
                    //String file_content = loadTextFromAssets(mContext, "www/cpb_3d_model_wgt/" + uri.substring(1), StandardCharsets.UTF_8);
                    //return newFixedLengthResponse(Response.Status.OK, MIME_JSON, file_content);
                    return newChunkedResponse(Response.Status.OK, MIME_JSON, mbuffer);

                }else{
                    String file_content = loadTextFromAssets(mContext, "www/cpb_3d_model_wgt/index.html", StandardCharsets.UTF_8);
                    return newFixedLengthResponse(Response.Status.OK, MIME_HTML, file_content);

                    //mbuffer = mContext.getAssets().open("index.html");
                    //return new NanoHTTPD.Response(HTTP_OK, MIME_HTML, mbuffer);
                }
            }

        } catch (IOException e) {
            Log.d(TAG,"Error opening file"+uri.substring(1));
            e.printStackTrace();
        }

        return null;

    }

    public static String loadTextFromAssets(Context context, String assetsPath, Charset charset) throws IOException {
        InputStream is = context.getResources().getAssets().open(assetsPath);
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int length = is.read(buffer); length != -1; length = is.read(buffer)) {
            baos.write(buffer, 0, length);
        }
        is.close();
        baos.close();
        return charset == null ? new String(baos.toByteArray()) : new String(baos.toByteArray(), charset);
    }

    public static InputStreamWrapper loadInStreamFromAssets(Context context, String assetsPath) throws IOException {
        InputStream is = context.getResources().getAssets().open(assetsPath);
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int length;
        for (length = is.read(buffer); length != -1; length = is.read(buffer)) {
            baos.write(buffer, 0, length);
        }
        is.close();
        baos.close();
        is = context.getResources().getAssets().open(assetsPath);
        return new InputStreamWrapper(is, length);
    }

}
