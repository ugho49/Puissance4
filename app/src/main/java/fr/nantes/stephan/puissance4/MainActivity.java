package fr.nantes.stephan.puissance4;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
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

import com.github.channguyen.rsv.RangeSliderView;
import com.michaldrabik.tapbarmenulib.TapBarMenu;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tapBarMenu)
    TapBarMenu tapBarMenu;
    @Bind(R.id.view)
    CoordinatorLayout view;
    @Bind(R.id.gridView)
    GridView gridView;
    @Bind(R.id.toolbar_progress_bar)
    ProgressBar toolbarProgressBar;
    @Bind(R.id.rsv)
    RangeSliderView rsv;
    @Bind(R.id.difficulty)
    TextView difficulty;

    private GridAdapter adapter;
    private final String FIRST_PLAYER = IA.PLAYER;
    private int DEPTH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        DEPTH = 2;
        difficulty.setText(getResources().getString(R.string.level_easy));

        initOrResetGame(FIRST_PLAYER);

        rsv.setOnSlideListener(new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                switch (index) {
                    case 0:
                        DEPTH = 2;
                        difficulty.setText(getResources().getString(R.string.level_easy));
                        break;

                    case 1:
                        DEPTH = 4;
                        difficulty.setText(getResources().getString(R.string.level_medium));
                        break;

                    case 2:
                        DEPTH = 6;
                        difficulty.setText(getResources().getString(R.string.level_hard));
                        break;

                    default:
                        DEPTH = 2;
                        difficulty.setText(getResources().getString(R.string.level_easy));
                        break;
                }

                if (adapter.gameHasBegin()) {
                    Snackbar.make(view, getResources().getString(R.string.level_change_at_next_game), Snackbar.LENGTH_SHORT).show();
                } else {
                    adapter.setDepthToIA(DEPTH);
                }
            }
        });
    }

    @OnClick(R.id.tapBarMenu)
    public void onClick() {
        tapBarMenu.toggle();
    }

    @OnClick({ R.id.item_replay, R.id.item_level })
    public void onMenuItemClick(View view) {
        tapBarMenu.close();

        switch (view.getId()) {
            case R.id.item_replay:
                replay();
                break;
            case R.id.item_level:
                Snackbar.make(view, "Futur button", Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    private void replay() {
        if (adapter.gameEnd()) {
            initOrResetGame(FIRST_PLAYER);
        } else {
            Snackbar snackbar = Snackbar.make(view, getResources().getString(R.string.do_you_want_replay), Snackbar.LENGTH_LONG)
                    .setAction(getResources().getString(R.string.snackbar_yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initOrResetGame(FIRST_PLAYER);
                        }
                    });

            snackbar.setActionTextColor(getResources().getColor(R.color.snackAction));
            snackbar.setDuration(Snackbar.LENGTH_SHORT);

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);

            snackbar.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Snackbar.make(view, "TODO", Snackbar.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
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
        adapter.setDepthToIA(DEPTH);
        gridView.setAdapter(adapter);

        if (first.equals(IA.COMPUTER)) {
            adapter.placeIAPiece();
            adapter.notifyDataSetChanged();
            gridView.setAdapter(adapter);
        }
    }
}
