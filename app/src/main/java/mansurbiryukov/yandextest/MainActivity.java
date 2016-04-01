package mansurbiryukov.yandextest;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static String LOG_TAG = "my_log"; //Тэг для логов
    public static List<Artist> Artists = new ArrayList<>(); //Массив всех исполнителей
    public ImageLoader loader; //Загрузчик изображений
    MyListViewAdapter adapter; //Кастомный адаптер для ListView
    ListView listview;
    ProgressBar progressBar;
    int position = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        //Старовая конфигурация загрузчика изображений UIL
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024)
                .build();
        loader = ImageLoader.getInstance();
        loader.init(config);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        //Создание листвью и передача ему кастомного адаптера
        listview = (ListView) findViewById(R.id.list);
        adapter = new MyListViewAdapter(Artists, this, loader);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);




        if(Artists.isEmpty()){
            //Запуск заглушки
            listview.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            //Запуск асинхронного парсера
            new ParsingJSON().execute();
        }else{
            if (position!=-1) listview.smoothScrollToPosition(position);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (id != -1) {
            Intent intent = new Intent(this, Detail.class);
            intent.putExtra("position", position);
            this.position = position;
            startActivity(intent);
        }
    }

    //Класс, описывающий все поля из JSON файла
    public class Artist {
        String id;
        String name;
        List<String> genres;
        int tracks;
        int albums;
        String link;
        String desc;
        List<String> cover;


        public Artist(String id, String name, List<String> genres, int tracks, int albums, String link, String desc,
                      List<String> cover) {
            this.id = id;
            this.name = name;
            this.genres = genres;
            this.tracks = tracks;
            this.albums = albums;
            this.link = link;
            this.desc = desc;
            this.cover = cover;
        }

    }

    //Парсер JSON
    private class ParsingJSON extends AsyncTask<Void, Void, String> {
        HttpURLConnection addres = null;
        BufferedReader bufferedReader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            //Стандартная обработка ошибок для openConnection()
            try {
                //Подключение к файлу
                URL url = new URL("http://download.cdn.yandex.net/mobilization-2016/artists.json");
                addres = (HttpURLConnection) url.openConnection();
                addres.setInstanceFollowRedirects(true);
                addres.setRequestMethod("GET");
                addres.connect();
                //Подготовка к чтению
                InputStream inputStream = addres.getInputStream();
                StringBuffer buffer = new StringBuffer();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                //Чтение JSON
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line);
                }
                resultJson = buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }


        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            //Массив всех строк, полученных из JSON
            JSONArray baseArray = null;
            try {
                baseArray = new JSONArray(strJson);
                for (int i = 0; i < baseArray.length(); i++) {
                    JSONObject item = baseArray.getJSONObject(i);
                    //Заполняем поля
                    String id = item.getString("id");
                    String name = item.getString("name");
                    List<String> genres = new ArrayList<>();
                    JSONArray genresArray = item.getJSONArray("genres");
                    for (int j = 0; j < genresArray.length(); j++) {
                        String gen = genresArray.getString(j);
                        genres.add(gen);
                    }
                    int tracks = item.getInt("tracks");
                    int albums = item.getInt("albums");
                    String link = "";
                    //Были обнаружены случаи, когда ссылок не было и парсер громко матерился на это, игнорируем
                    try {
                        link = item.getString("link");
                    } catch (Exception e) {
                    }
                    String description = item.getString("description");
                    List<String> covers = new ArrayList<>();
                    JSONObject coversobj = item.getJSONObject("cover");
                    covers.add(coversobj.getString("small"));
                    covers.add(coversobj.getString("big"));
                    Artist artist = new Artist(id, name, genres, tracks, albums, link, description, covers);
                    Artists.add(artist);
                }
                Log.d(LOG_TAG, "Finishied parsing");
                //Сообщаем адаптеру, что данные обновились и необходимо перерисовать ListView
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                listview.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
