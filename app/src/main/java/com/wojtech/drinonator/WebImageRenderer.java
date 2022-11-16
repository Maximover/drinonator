package com.wojtech.drinonator;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class WebImageRenderer implements Runnable{
    private final URL url;
    private final ImageView target;
    private final Activity activity;

    public WebImageRenderer(Activity activity, String image_url, ImageView target) throws MalformedURLException {
        this.activity = activity;
        this.url = new URL(image_url+"/preview");
        this.target = target;
    }

    @Override
    public void run() {
        try {
            InputStream is = (InputStream) this.url.getContent();
            Drawable d = Drawable.createFromStream(is, "cocktailDB");
            is.close();
            // altering views only on UI thread
            activity.runOnUiThread(() -> target.setImageDrawable(d));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
