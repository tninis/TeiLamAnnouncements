package announcements.tninis.cloud.teilamannouncements;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static announcements.tninis.cloud.teilamannouncements.NetworkOperations.*;
import android.support.v7.app.ActionBar;


public class MainActivity extends AppCompatActivity implements AnnouncementsAdapter.OnItemClicked {
    List<AnnouncementsItems> AnnounList=new ArrayList<AnnouncementsItems>();
    AnnouncementsAdapter adapter;
    RecyclerView rv;
    SwipeRefreshLayout mySwipeRefreshLayout;
    ProgressBar Progress;
    TextView progSearch;
    SharedPreferences preferences ;
    SharedPreferences.Editor editor;
    CoordinatorLayout coordinatorLayout;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar  ab =getSupportActionBar();
        ab.setTitle(R.string.main_title);
        ab.setSubtitle(R.string.sub_title);
        stopService(new Intent(getApplicationContext(),AnnouncementsService.class));

        isFirstTime();
        InitializeComponents();

        doMainWork();
        mySwipeRefreshLayout.setOnRefreshListener
        (
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    doMainWork();
                }
            }
        );
    }

    private class GetAnnouncements extends AsyncTask<String, Integer, List<AnnouncementsItems>> {
        protected List<AnnouncementsItems> doInBackground(String... url) {
            int countAnn=0;
            try
            {
                Document doc = Jsoup.connect(url[0]).get();
                Elements items = doc.select("li:not(.menu-item) >a[href]");
                AnnounList.clear();
                for (Element item:items) {
                    String link=item.attr("href");
                    String date="-";
                    String text=item.select("a[href]").html().replace("&amp;","").replace("&nbsp;","");

                    if(text.contains("("))
                    {
                       date = text.substring(text.indexOf("(") + 1, text.indexOf(")"));
                    }

                    AnnounList.add(new AnnouncementsItems(text.substring(text.indexOf(")")+1,text.length()).trim(),link,date));
                    countAnn++;
                }
            }
            catch(IOException e)
            {

            }
            editor.putInt("PrevCounter", preferences.getInt("CurreCounter",0));
            editor.putInt("CurreCounter", countAnn);
            editor.commit();
            return AnnounList;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(List<AnnouncementsItems> result) {

            rv.setAdapter(adapter);
            rv.setVisibility(View.VISIBLE);
            progSearch.setVisibility(View.GONE);
            Progress.setVisibility(View.GONE);
            if(mySwipeRefreshLayout.isRefreshing()) {
                Toast.makeText(MainActivity.this, "Η Ανανέωση ολοκληρώθηκε", Toast.LENGTH_SHORT).show();
                mySwipeRefreshLayout.setRefreshing(false);
            }


        }
    }
    @Override
    public void onItemClick(int position) {
        String link=AnnounList.get(position).getUrl();
        new AnnouncentAction().execute(link);
    }

    private class AnnouncentAction extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... url) {
            String link="";
            try
            {
                Document doc = Jsoup.connect(url[0]).get();
                Element find = doc.select("a[href~=(http|https)://inf.teiste.gr/wp-content/uploads]").first();

                if(find==null)
                    link=url[0];
                else
                    link=find.attr("href");

            }
            catch(IOException e)
            {

            }
            return link;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(String result) {
            Uri uri = Uri.parse(result);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onRestart(){
        super.onRestart();
        stopService(new Intent(getApplicationContext(),AnnouncementsService.class));
        new GetAnnouncements().execute(Constants.PAGE);
    }

    public void showNoInternet(){
        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        progSearch.setVisibility(View.GONE);
        Progress.setVisibility(View.GONE);
        snackbar = Snackbar .make(coordinatorLayout , "Δέν υπάρχει σύνδεση στο Internet", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    public void doMainWork(){
        if(isNetworkAvailable(this)){
          if(snackbar!=null)
              snackbar.dismiss();

          new GetAnnouncements().execute(Constants.PAGE);
        }
        else {
            if(mySwipeRefreshLayout.isRefreshing())
                mySwipeRefreshLayout.setRefreshing(false);

            showNoInternet();
        }

    }

    public void isFirstTime(){
        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        Boolean first=preferences.getBoolean("isFirst",true);
        if(first==null || first==true)
        {
            editor.putInt("PrevCounter", 0);
            editor.putInt("CurreCounter", 0);
            editor.putBoolean("isFirst",false);
            editor.commit();
        }
    }

    private void InitializeComponents(){
        mySwipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        Progress=(ProgressBar) findViewById(R.id.progressBar);
        progSearch=(TextView)findViewById((R.id.progressSearch));
        rv=(RecyclerView) findViewById(R.id.recycler_view_main);
        adapter = new AnnouncementsAdapter(AnnounList);
        rv.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        rv.addItemDecoration(new VerticalSpacingDecoration(10));
        adapter.setOnClick(this);

    }

    private void startService()
    {
        Intent startIntent = new Intent(getApplicationContext(), AnnouncementsService.class);
        startIntent.setAction("ACTION_START_SERVICE");
        startService(startIntent);

    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
