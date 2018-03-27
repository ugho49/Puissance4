package fr.nantes.stephan.puissance4.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.nantes.stephan.puissance4.R;
import fr.nantes.stephan.puissance4.objects.IA;
import fr.nantes.stephan.puissance4.utils.Constantes;

/**
 * Created by ughostephan on 26/02/2016.
 */
public class GridAdapter extends BaseAdapter {
    
    private final Context context;
    private final LayoutInflater inflater;
    private final int screenWidth;
    private final IA mIA;
    private String[][] mPiecesPlayed = new String[7][6]; // [column][line]
    private int[] mNumberOfPiecesByColumn = new int[7];
    private int[] mThumbs = new int[42];
    private String nextPlayer;
    private String color_piece_user;

    // Shared Preferences
    private SharedPreferences preferences;
    private SharedPreferences.Editor preferencesEditor;

    // Use this instance of the interface to deliver action events
    private GridAdapterListener mListener;

    public GridAdapter(final Activity activity, final String firstPlayer, final int screenWidth) {
        this.context = activity.getApplicationContext();
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.nextPlayer = firstPlayer;
        this.screenWidth = screenWidth;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.preferencesEditor = this.preferences.edit();
        mIA = new IA();

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the GridAdapterListener so we can send events to the host
            mListener = (GridAdapterListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() + " must implement SettingsDialogListener");
        }

        cleanGrid();
    }

    public interface GridAdapterListener {
        void onBeginComputerLoad();

        void onFinishComputerLoad();

        void showSnackMessage(String message);
    }

    public void setDepthToIA(int depth) {
        mIA.setDEPTH(depth);
    }

    public void setColor_piece_user(String color_piece_user) {
        this.color_piece_user = color_piece_user;
    }

    public boolean gameHasBegin() {
        boolean b = false;

        for(int i = 0; i<=6; i++) //vérif  par colonnes
        {
            if(this.mNumberOfPiecesByColumn[i] > 0)
            {
                b = true;
            }
        }

        return b;
    }

    static class ViewHolder {
        @BindView(R.id.imageViewPion)
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

        return view;
    }
    
    private void cleanGrid() {
        for(int i = 0; i<=41; i++) {
            mThumbs[i] = R.drawable.ic_vide;
        }

        for(int i = 0; i<=6; i++) {
            mNumberOfPiecesByColumn[i] = 0;
        }

        for(int i = 0; i<=6; i++) {
            for(int z = 0; z<=5; z++) {
                mPiecesPlayed[i][z] = null;
            }
        }
    }
    
    public void placeGamerPiece(int position) {
        if(gameEnd()) {
            showMessage(context.getString(R.string.game_over));
        } else {
            final int column = position % 7;

            if (mNumberOfPiecesByColumn[column] < 6) {
                int li = 5;
                boolean b = false;

                do {
                    if (mPiecesPlayed[column][li] == null) {
                        b = true;

                        mNumberOfPiecesByColumn[column] = mNumberOfPiecesByColumn[column] + 1;
                        mPiecesPlayed[column][li] = Constantes.PLAYER;

                        int positionAjouer = column + (li * 7);

                        if (Constantes.YELLOW_PIECE.equals(color_piece_user)) {
                            mThumbs[positionAjouer] = R.drawable.ic_orange;
                        } else if (Constantes.RED_PIECE.equals(color_piece_user)) {
                            mThumbs[positionAjouer] = R.drawable.ic_rouge;
                        }

                        notifyDataSetChanged();

                        if (!mIA.playerWin(mPiecesPlayed, Constantes.PLAYER)) {
                            if(stillPlay()){
                                nextPlayer = Constantes.COMPUTER;
                                placeIAPiece();
                            }
                            else {
                                increaseAnalytics(Constantes.PREF_EQUAL);
                                showMessage(context.getString(R.string.equal_game));
                            }
                        } else {
                            increaseAnalytics(Constantes.PREF_WINS);
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
                mListener.onBeginComputerLoad();
            }

            @Override
            protected Integer doInBackground(Void... params) {
                int column = -1;

                if (gameEnd()) {
                    showMessage(context.getString(R.string.game_over));
                } else {
                    column = mIA.getColumn(mPiecesPlayed);

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
                    final int positionAjouer = ((5 - mNumberOfPiecesByColumn[column]) * 7) + column;
                    final int ligne = (int) Math.floor(positionAjouer / 7);

                    mNumberOfPiecesByColumn[column] = mNumberOfPiecesByColumn[column] + 1;
                    mPiecesPlayed[column][ligne] = Constantes.COMPUTER;

                    if (Constantes.YELLOW_PIECE.equals(color_piece_user)) {
                        mThumbs[positionAjouer] = R.drawable.ic_rouge;
                    } else if (Constantes.RED_PIECE.equals(color_piece_user)) {
                        mThumbs[positionAjouer] = R.drawable.ic_orange;
                    }

                    notifyDataSetChanged();

                    if (!mIA.playerWin(mPiecesPlayed, nextPlayer)) {
                        if(stillPlay()){
                            nextPlayer = Constantes.PLAYER;
                        }
                        else {
                            increaseAnalytics(Constantes.PREF_EQUAL);
                            showMessage(context.getString(R.string.equal_game));
                        }
                    } else {
                        increaseAnalytics(Constantes.PREF_LOOSE);
                        showMessage(context.getString(R.string.you_lose));
                    }
                }

                mListener.onFinishComputerLoad();
            }
        }.execute();

    }

    public boolean gameEnd() {
        return mIA.playerWin(mPiecesPlayed, Constantes.COMPUTER) || mIA.playerWin(mPiecesPlayed, Constantes.PLAYER) || !stillPlay();
    }

    private boolean stillPlay() {
        boolean b = false;

        for(int i = 0; i<=6; i++) {
            if(this.mNumberOfPiecesByColumn[i] < 6) {
                b = true;
            }
        }

        return b;
    }

    private void showMessage(final String m) {
        mListener.showSnackMessage(m);
    }

    private void increaseAnalytics(final String key) {
        preferencesEditor.putInt(key, preferences.getInt(key, 0) + 1);
        preferencesEditor.commit();
    }
}
