package mansurbiryukov.yandextest;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class Detail extends AppCompatActivity {

    int position = -1;
    boolean full = false;
    ImageView imageView;
    ImageLoader loader;
    ProgressBar progressBar;
    LinearLayout layout;
    TextView genres, summ, desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        position = getIntent().getIntExtra("position", -1);
        MainActivity.Artist current = MainActivity.Artists.get(position);
        imageView = (ImageView) findViewById(R.id.photo);
        loader = ImageLoader.getInstance();
        progressBar = (ProgressBar) findViewById(R.id.prgbar);
        layout = (LinearLayout) findViewById(R.id.detaillayout);
        layout.setVisibility(View.GONE);
        loader.displayImage(current.cover.get(1), imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(View.GONE);
                imageView.setImageBitmap(loadedImage);
                layout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        genres = (TextView) findViewById(R.id.genres);
        summ = (TextView) findViewById(R.id.summary);
        desc = (TextView) findViewById(R.id.desc);


        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < current.genres.size(); i++) {
            strBuilder.append(current.genres.get(i));
            if(i+1!=current.genres.size()) strBuilder.append(", ");
        }
        String genrestext = strBuilder.toString();
        String description = current.desc;
        description = description.substring(0, 1).toUpperCase()+description.substring(1);

        genres.setText(genrestext);
        summ.setText(String.format(getApplicationContext().getString(R.string.summary), current.albums, current.tracks));
        desc.setText(description);

        setTitle(current.name);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
