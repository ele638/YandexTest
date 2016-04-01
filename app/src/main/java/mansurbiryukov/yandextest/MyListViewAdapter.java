package mansurbiryukov.yandextest;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by ele638 on 01.04.16.
 */

//Кастомный адаптер для ListView
public class MyListViewAdapter extends BaseAdapter {

    List<MainActivity.Artist> values;
    private Context context;
    ImageLoader loader;

    public MyListViewAdapter(List<MainActivity.Artist> values, Context context, ImageLoader loader) {
        this.context=context;
        this.values = values;
        this.loader = loader;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return  values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listviewelement, parent, false);
        MainActivity.Artist current = values.get(position);
        TextView artistTitle = (TextView) v.findViewById(R.id.artistTitle);
        TextView artistGenres = (TextView) v.findViewById(R.id.artistGenres);
        TextView artistSummary = (TextView) v.findViewById(R.id.atristSummary);
        final ImageView imageView = (ImageView) v.findViewById(R.id.artistImage);
        artistTitle.setText(current.name);
        //Собираем нормальную строку из массива строк
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < current.genres.size(); i++) {
            strBuilder.append(current.genres.get(i));
            if(i+1!=current.genres.size()) strBuilder.append(", ");
        }
        String genres = strBuilder.toString();
        artistGenres.setText(genres);
        //Используется форматированная строка, см. strings.xml
        artistSummary.setText(String.format(context.getResources().getString(R.string.summary), current.albums, current.tracks));
        //Обработка изображения (в cover лежит массив из двух элементов (small и big), которые являются ссылками на картинки
        loader.displayImage(current.cover.get(0), imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                view.setVisibility(View.VISIBLE);
                //Рисуем восклицательный знак, если что-то пошло не так
                imageView.setImageResource(R.drawable.ic_action_name);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                view.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                //Метода отмены загрузки нет
            }
        });
        return v;
    }
}
