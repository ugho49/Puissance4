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
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MainActivity extends AppCompatActivity
        implements SettingsDialog.SettingsDialogListener {

    @Bind(R.id.view)
    CoordinatorLayout view;
    @Bind(R.id.gridView)
    GridView gridView;
    @Bind(R.id.toolbar_progress_bar)
    ProgressBar toolbarProgressBar;
    @Bind(R.id.toolbar_top)
    Toolbar toolbarTop;
    @Bind(R.id.toolbar_bottom)
    Toolbar toolbarBottom;
    @Bind(R.id.btn_replay)
    ImageButton btnReplay;
    @Bind(R.id.btn_params)
    ImageButton btnParams;
    @Bind(R.id.btn_info)
    ImageButton btnInfo;
    @Bind(R.id.fab_play)
    FloatingActionButton fabPlay;

    private GridAdapter adapter;
    private SettingsDialog settingsDialog;
    private AnalyticsDialog analyticsDialog;
    private String FIRST_PLAYER;
    private int DEPTH;
    private String COLOR_PIECE_USER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbarTop);

        // INIT DIALOGS
        settingsDialog = new SettingsDialog();
        analyticsDialog = new AnalyticsDialog(getApplicationContext());

        // START INIT ELEMENTS
        DEPTH = GameUtils.DEPTH_EASY;
        COLOR_PIECE_USER = GameUtils.YELLOW_PIECE;
        FIRST_PLAYER = GameUtils.PLAYER;

        // INIT GAME
        initOrResetGame();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void replay() {
        if (adapter.gameEnd() || !adapter.gameHasBegin()) {
            initOrResetGame();
        } else {
            Snackbar snackbar = Snackbar.make(view, R.string.do_you_want_replay, Snackbar.LENGTH_LONG)
                    .setAction(R.string.snackbar_yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initOrResetGame();
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
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Snackbar.make(view, "Action settings", Snackbar.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @OnItemClick(R.id.gridView)
    public void OnItemClick(int position) {
        adapter.placeGamerPiece(position);
        adapter.notifyDataSetChanged();
        gridView.setAdapter(adapter);
    }

    @OnClick({R.id.btn_replay, R.id.btn_params, R.id.btn_info, R.id.fab_play})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_replay:
                replay();
                break;

            case R.id.btn_params:
                settingsDialog.show(getSupportFragmentManager(), "settingsDialog");
                break;

            case R.id.btn_info:
                analyticsDialog.show(getSupportFragmentManager(), "analyticsDialog");
                break;

            case R.id.fab_play:
                if (!adapter.gameHasBegin()) {
                    adapter.placeIAPiece();
                } else {
                    Snackbar.make(view, R.string.game_already_begin, Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initOrResetGame() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        adapter = new GridAdapter(getApplicationContext(), FIRST_PLAYER, metrics.widthPixels);
        adapter.setView(view);
        adapter.setProgressBar(toolbarProgressBar);
        adapter.setGridView(gridView);
        adapter.setColor_piece_user(COLOR_PIECE_USER);
        adapter.setDepthToIA(DEPTH);

        if (FIRST_PLAYER.equals(GameUtils.COMPUTER)) {
            fabPlay.setVisibility(View.VISIBLE);
        } else {
            fabPlay.setVisibility(View.GONE);
        }

        gridView.setAdapter(adapter);
    }

    @Override
    public void onSlide(int index) {
        switch (index) {
            case 0:
                DEPTH = GameUtils.DEPTH_EASY;
                break;

            case 1:
                DEPTH = GameUtils.DEPTH_MEDIUM;
                break;

            case 2:
                DEPTH = GameUtils.DEPTH_HARD;
                break;

            default:
                DEPTH = GameUtils.DEPTH_EASY;
                break;
        }

        settingsDialog.setDifficulty(DEPTH);
        switchDepth();
    }

    private void switchDepth() {
        if (adapter.gameHasBegin()) {
            Snackbar.make(settingsDialog.getViewForSnackBar(), R.string.level_change_at_next_game, Snackbar.LENGTH_SHORT).show();
        } else {
            adapter.setDepthToIA(DEPTH);
        }
    }

    private void switchPlayer() {
        if (adapter.gameHasBegin()) {
            Snackbar.make(settingsDialog.getViewForSnackBar(), R.string.first_user_change_at_next_game, Snackbar.LENGTH_SHORT).show();
        } else {
            initOrResetGame();
        }
    }

    private void switchColor() {
        if (adapter.gameHasBegin()) {
            Snackbar.make(settingsDialog.getViewForSnackBar(), R.string.color_piece_user_change_at_next_game, Snackbar.LENGTH_SHORT).show();
        } else {
            adapter.setColor_piece_user(COLOR_PIECE_USER);
        }
    }

    @Override
    public void afterInflateView() {
        settingsDialog.setPlayer(FIRST_PLAYER);
        settingsDialog.setColor(COLOR_PIECE_USER);
        settingsDialog.setDifficulty(DEPTH);
    }

    @Override
    public void onDialogViewClick(View v) {
        switch (v.getId()) {
            case R.id.radioButtonPlayerUser:
                FIRST_PLAYER = GameUtils.PLAYER;
                switchPlayer();
                break;

            case R.id.radioButtonPlayerComputer:
                FIRST_PLAYER = GameUtils.COMPUTER;
                switchPlayer();
                break;

            case R.id.redPiece:
                COLOR_PIECE_USER = GameUtils.RED_PIECE;
                switchColor();
                break;

            case R.id.yellowPiece:
                COLOR_PIECE_USER = GameUtils.YELLOW_PIECE;
                switchColor();
                break;
        }
    }
}
