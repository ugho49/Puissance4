package fr.nantes.stephan.puissance4;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.view)
    CoordinatorLayout view;
    @Bind(R.id.gridView)
    GridView gridView;
    @Bind(R.id.toolbar_progress_bar)
    ProgressBar toolbarProgressBar;

    private GridAdapter adapter;
    private final String FIRST_PLAYER = IA.PLAYER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        initOrResetGame(FIRST_PLAYER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    public void onClick() {

        if (adapter.gameEnd()) {
            initOrResetGame(FIRST_PLAYER);
        } else {
            Snackbar snackbar = Snackbar.make(view, "Etes-vous sûr de recommencer ?", Snackbar.LENGTH_LONG)
                    .setAction("OUI", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initOrResetGame(FIRST_PLAYER);
                        }
                    });

            snackbar.setActionTextColor(Color.parseColor("#2196f3"));
            snackbar.setDuration(Snackbar.LENGTH_SHORT);

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);

            snackbar.show();
        }
    }

    @OnItemClick(R.id.gridView)
    public void OnItemClick(int position) {
        adapter.placeGamerPiece(position);
        adapter.notifyDataSetChanged();
        gridView.setAdapter(adapter);
    }

    private void initOrResetGame(final String first) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        adapter = new GridAdapter(getApplicationContext(), FIRST_PLAYER, metrics.widthPixels);
        adapter.setView(view);
        adapter.setProgressBar(toolbarProgressBar);
        adapter.setGridView(gridView);
        gridView.setAdapter(adapter);

        if (first.equals(IA.COMPUTER)) {
            adapter.placeIAPiece();
            adapter.notifyDataSetChanged();
            gridView.setAdapter(adapter);
        }
    }
}
