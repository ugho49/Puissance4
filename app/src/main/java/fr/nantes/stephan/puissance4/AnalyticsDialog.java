package fr.nantes.stephan.puissance4;

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
        winGames.setText("1");
        looseGames.setText("2");
        equalGames.setText("3");
    }
}
