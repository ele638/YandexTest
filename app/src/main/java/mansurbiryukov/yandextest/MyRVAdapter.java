package mansurbiryukov.yandextest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by ele638 on 05.04.16.
 */
public class MyRVAdapter extends RecyclerView.Adapter<MyRVAdapter.ViewHolder> {

    List<MainActivity.Artist> values;
    private Context context;
    ImageLoader loader;

    public MyRVAdapter(List<MainActivity.Artist> values, Context context, ImageLoader loader) {
        this.values = values;
        this.context = context;
        this.loader = loader;
    }

    @Override
    public MyRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listviewelement, parent, false);
        ViewHolder mvh = new ViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(final MyRVAdapter.ViewHolder holder, final int position) {
        MainActivity.Artist current = values.get(position);
        holder.name.setText(current.name);
        //Собираем нормальную строку из массива строк
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < current.genres.size(); i++) {
            strBuilder.append(current.genres.get(i));
            if(i+1!=current.genres.size()) strBuilder.append(", ");
        }
        String genres = strBuilder.toString();
        holder.genre.setText(genres);
        //Используется форматированная строка, см. strings.xml
        holder.summ.setText(String.format(context.getResources().getString(R.string.summary), current.albums, current.tracks));
        //Обработка изображения (в cover лежит массив из двух элементов (small и big), которые являются ссылками на картинки
        loader.displayImage(current.cover.get(0), holder.img, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                view.setVisibility(View.VISIBLE);
                //Рисуем восклицательный знак, если что-то пошло не так
                holder.img.setImageResource(R.drawable.ic_action_name);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                view.setVisibility(View.VISIBLE);
                holder.img.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                //Метода отмены загрузки нет
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Detail.class);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView img;
        TextView name, genre, summ;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardview);
            img = (ImageView) itemView.findViewById(R.id.artistImage);
            name = (TextView) itemView.findViewById(R.id.artistTitle);
            genre = (TextView) itemView.findViewById(R.id.artistGenres);
            summ = (TextView) itemView.findViewById(R.id.atristSummary);
        }
    }
}
