package fr.nantes.stephan.puissance4.activities;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import fr.nantes.stephan.puissance4.R;
import fr.nantes.stephan.puissance4.adapters.GridAdapter;
import fr.nantes.stephan.puissance4.dialogs.AnalyticsDialog;
import fr.nantes.stephan.puissance4.dialogs.SettingsDialog;
import fr.nantes.stephan.puissance4.utils.Constantes;

public class MainActivity extends AppCompatActivity
        implements SettingsDialog.SettingsDialogListener,
        GridAdapter.GridAdapterListener {


    @Bind(R.id.toolbar_progress_bar)
    ProgressBar toolbarProgressBar;
    @Bind(R.id.toolbar_top)
    Toolbar toolbarTop;
    @Bind(R.id.gridView)
    GridView gridView;
    @Bind(R.id.fab_play)
    FloatingActionButton fabPlay;
    @Bind(R.id.view)
    CoordinatorLayout view;
    @Bind(R.id.btn_info)
    ImageButton btnInfo;
    @Bind(R.id.btn_params)
    ImageButton btnParams;
    @Bind(R.id.toolbar_bottom)
    Toolbar toolbarBottom;
    @Bind(R.id.btn_replay)
    FloatingActionButton btnReplay;

    private GridAdapter adapter;
    private SettingsDialog settingsDialog;
    private AnalyticsDialog analyticsDialog;
    private String FIRST_PLAYER;
    private int DEPTH;
    private String COLOR_PIECE_USER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbarTop);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btnReplay.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorPieceYellow)));
        } else {
            btnReplay.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPieceYellow)));
        }

        // INIT DIALOGS
        settingsDialog = new SettingsDialog();
        analyticsDialog = new AnalyticsDialog(getApplicationContext());

        // START INIT ELEMENTS
        DEPTH = Constantes.DEPTH_EASY;
        COLOR_PIECE_USER = Constantes.YELLOW_PIECE;
        FIRST_PLAYER = Constantes.PLAYER;

        // INIT GAME
        initOrResetGame();
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

    @OnItemClick(R.id.gridView)
    public void OnItemClick(int position) {
        if (Constantes.COMPUTER.equals(FIRST_PLAYER) && !adapter.gameHasBegin()) {
            Snackbar.make(view, R.string.touch_play_for_begin, Snackbar.LENGTH_SHORT).show();
        } else {
            adapter.placeGamerPiece(position);
        }
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

        adapter = new GridAdapter(this, FIRST_PLAYER, metrics.widthPixels);
        adapter.setColor_piece_user(COLOR_PIECE_USER);
        adapter.setDepthToIA(DEPTH);

        if (FIRST_PLAYER.equals(Constantes.COMPUTER)) {
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
                DEPTH = Constantes.DEPTH_EASY;
                break;

            case 1:
                DEPTH = Constantes.DEPTH_MEDIUM;
                break;

            case 2:
                DEPTH = Constantes.DEPTH_HARD;
                break;

            default:
                DEPTH = Constantes.DEPTH_EASY;
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
                FIRST_PLAYER = Constantes.PLAYER;
                switchPlayer();
                break;

            case R.id.radioButtonPlayerComputer:
                FIRST_PLAYER = Constantes.COMPUTER;
                switchPlayer();
                break;

            case R.id.redPiece:
                COLOR_PIECE_USER = Constantes.RED_PIECE;
                switchColor();
                break;

            case R.id.yellowPiece:
                COLOR_PIECE_USER = Constantes.YELLOW_PIECE;
                switchColor();
                break;
        }
    }

    @Override
    public void onBeginComputerLoad() {
        toolbarProgressBar.setVisibility(View.VISIBLE);
        gridView.setEnabled(false);
        btnReplay.setEnabled(false);

        if (Constantes.COMPUTER.equals(FIRST_PLAYER)) {
            fabPlay.setEnabled(false);
        }
    }

    @Override
    public void onFinishComputerLoad() {
        toolbarProgressBar.setVisibility(View.INVISIBLE);
        gridView.setEnabled(true);
        btnReplay.setEnabled(true);

        if (Constantes.COMPUTER.equals(FIRST_PLAYER)) {
            fabPlay.setEnabled(true);
        }
    }

    @Override
    public void showSnackMessage(String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }
}
