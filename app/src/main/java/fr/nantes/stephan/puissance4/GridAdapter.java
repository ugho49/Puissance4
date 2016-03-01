package fr.nantes.stephan.puissance4;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ughostephan on 26/02/2016.
 */
public class GridAdapter extends BaseAdapter {
    
    private final Context context;
    private final LayoutInflater inflater;
    private final int screenWidth;
    private String[][] mPionsJouee = new String[7][6]; // [colonne][ligne]
    private int[] mNombrePionsParColonnes = new int[7]; // [colonne]
    private int[] mThumbs = new int[42];
    private String nextPlayer;
    private ProgressBar progressBar;
    private GridView gridView;
    private final IA mIA;
    private CoordinatorLayout view;

    public GridAdapter(final Context context, final String nextPlayer, final int screenWidth) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.nextPlayer = nextPlayer;
        this.screenWidth = screenWidth;
        mIA = new IA();
        cleanGrid();
    }

    public void setView(CoordinatorLayout view) {
        this.view = view;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void setGridView(GridView gridView) {
        this.gridView = gridView;
    }

    public void setDepthToIA(int depth) {
        mIA.setDEPTH(depth);
    }

    public boolean gameHasBegin() {
        boolean b = false;

        for(int i = 0; i<=6; i++) //vérif  par colonnes
        {
            if(this.mNombrePionsParColonnes[i] > 0)
            {
                b = true;
            }
        }

        return b;
    }

    static class ViewHolder {
        @Bind(R.id.imageViewPion)
        ImageView image;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
    
    @Override
    public int getCount() {
        return mThumbs.length;
    }
    
    @Override
    public Integer getItem(int position) {
        return mThumbs[position];
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.row_item_pion, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        holder.image.getLayoutParams().height = screenWidth / 7;
        holder.image.getLayoutParams().width = screenWidth / 7;
        holder.image.requestLayout();

        holder.image.setImageResource(mThumbs[position]);

        // Disable HW acceleration for SVGs
        holder.image.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        return view;
    }
    
    private void cleanGrid() {

        for(int i = 0; i<=41; i++) {
            mThumbs[i] = R.drawable.vide_svg;
        }

        for(int i = 0; i<=6; i++) {
            mNombrePionsParColonnes[i] = 0;
        }

        for(int i = 0; i<=6; i++) {
            for(int z = 0; z<=5; z++) {
                mPionsJouee[i][z] = null;
            }
        }
    }
    
    public void placeGamerPiece(int position) {

        if(gameEnd()) {
            showMessage(context.getString(R.string.game_over));
        } else {
            final int column = position % 7;

            if (mNombrePionsParColonnes[column] < 6) {
                int li = 5;
                boolean b = false;

                do {
                    if (mPionsJouee[column][li] == null) {
                        b = true;

                        mNombrePionsParColonnes[column] = mNombrePionsParColonnes[column] + 1;
                        mPionsJouee[column][li] = IA.PLAYER;

                        int positionAjouer = column + (li * 7);

                        mThumbs[positionAjouer] = R.drawable.orange_svg;

                        if (!mIA.playerWin(mPionsJouee, IA.PLAYER)) {
                            if(stillPlay()){
                                nextPlayer = IA.COMPUTER;
                                notifyDataSetChanged();
                                placeIAPiece();
                            }
                            else {
                                showMessage(context.getString(R.string.equal_game));
                            }
                        } else {
                            showMessage(context.getString(R.string.you_win));
                        }
                    } else {
                        li--;
                    }
                } while (!b);
            } else {
                showMessage(context.getString(R.string.no_more_space));
            }
        }

    }

    public void placeIAPiece() {
        new AsyncTask<Void, Void, Integer>() {
            private double startTime;

            @Override
            protected void onPreExecute() {
                startTime = System.nanoTime();
                progressBar.setVisibility(View.VISIBLE);
                gridView.setEnabled(false);
            }

            @Override
            protected Integer doInBackground(Void... params) {
                int column = -1;

                if (gameEnd()) {
                    showMessage(context.getString(R.string.game_over));
                } else {
                    column = mIA.getColumn(mPionsJouee);

                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(5);

                    final double exectime = (System.nanoTime() - startTime) / 1000000000;
                    Log.i("Execution time", exectime + "s.");

                    final long restTime = Double.valueOf((0.5 - exectime) * 1000).longValue();

                    if (restTime > 0) {
                        Log.i("Rest time", restTime + "ms.");
                        try {
                            Thread.sleep(restTime);
                        } catch (InterruptedException e) {
                            Log.e("Err", e.getMessage());
                        }
                    }
                }

                return column;
            }

            @Override
            protected void onPostExecute(Integer column) {
                if (column != -1) {
                    final int positionAjouer = ((5 - mNombrePionsParColonnes[column]) * 7) + column;
                    final int ligne = (int) Math.floor(positionAjouer / 7);

                    mNombrePionsParColonnes[column] = mNombrePionsParColonnes[column] + 1;
                    mPionsJouee[column][ligne] = IA.COMPUTER;

                    mThumbs[positionAjouer] = R.drawable.rouge_svg;

                    notifyDataSetChanged();

                    if (!mIA.playerWin(mPionsJouee, nextPlayer)) {
                        if(stillPlay()){
                            nextPlayer = IA.PLAYER;
                        }
                        else {
                            showMessage(context.getString(R.string.equal_game));
                        }
                    } else {
                        showMessage(context.getString(R.string.you_lose));
                    }
                }

                progressBar.setVisibility(View.INVISIBLE);
                gridView.setEnabled(true);
            }
        }.execute();

    }

    public boolean gameEnd() {
        return mIA.playerWin(mPionsJouee, IA.COMPUTER) || mIA.playerWin(mPionsJouee, IA.PLAYER) || !stillPlay();
    }

    private boolean stillPlay() {
        boolean b = false;

        for(int i = 0; i<=6; i++) {
            if(this.mNombrePionsParColonnes[i] < 6) {
                b = true;
            }
        }

        return b;
    }

    private void showMessage(final String m) {
        if (view == null) {
            Toast.makeText(context, m, Toast.LENGTH_SHORT).show();
        } else {
            Snackbar.make(view, m, Snackbar.LENGTH_SHORT).show();
        }
    }
}
