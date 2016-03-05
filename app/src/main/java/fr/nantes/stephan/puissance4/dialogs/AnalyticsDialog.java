package fr.nantes.stephan.puissance4.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import fr.nantes.stephan.puissance4.R;
import fr.nantes.stephan.puissance4.utils.Constantes;

/**
 * Created by ughostephan on 05/03/2016.
 */
public class AnalyticsDialog extends AppCompatDialogFragment {

    @Bind(R.id.winGames)
    TextView winGames;
    @Bind(R.id.looseGames)
    TextView looseGames;
    @Bind(R.id.equalGames)
    TextView equalGames;

    private Context context;
    private SharedPreferences preferences;

    public AnalyticsDialog(Context context) {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View rootView = inflater.inflate(R.layout.dialog_analytics, container, false);
        ButterKnife.bind(this, rootView);

        getDialog().setTitle(R.string.action_analytics);

        fillTexViewBySharedPreferences();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void fillTexViewBySharedPreferences() {
        final String wins = String.valueOf(preferences.getInt(Constantes.PREF_WINS, 0));
        final String looses = String.valueOf(preferences.getInt(Constantes.PREF_LOOSE, 0));
        final String equals = String.valueOf(preferences.getInt(Constantes.PREF_EQUAL, 0));

        winGames.setText(wins);
        looseGames.setText(looses);
        equalGames.setText(equals);
    }
}
