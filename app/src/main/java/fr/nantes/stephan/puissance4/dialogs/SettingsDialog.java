package fr.nantes.stephan.puissance4.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.github.channguyen.rsv.RangeSliderView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import fr.nantes.stephan.puissance4.R;
import fr.nantes.stephan.puissance4.utils.Constantes;

/**
 * Created by ughostephan on 04/03/2016.
 */
public class SettingsDialog extends AppCompatDialogFragment implements RangeSliderView.OnSlideListener {

    @BindView(R.id.difficulty)
    TextView difficulty;
    @BindView(R.id.rsv)
    RangeSliderView rsv;
    @BindView(R.id.radioButtonPlayerUser)
    RadioButton radioButtonPlayerUser;
    @BindView(R.id.radioButtonPlayerComputer)
    RadioButton radioButtonPlayerComputer;
    @BindView(R.id.redPiece)
    RadioButton redPiece;
    @BindView(R.id.yellowPiece)
    RadioButton yellowPiece;
    @BindView(R.id.viewForSnackBar)
    CoordinatorLayout viewForSnackBar;

    private Unbinder unbinder;

    // Use this instance of the interface to deliver action events
    private SettingsDialogListener mListener;

    public interface SettingsDialogListener {
        void onDialogViewClick(View v);

        void onSlide(int index);

        void afterInflateView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View rootView = inflater.inflate(R.layout.dialog_settings, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        rsv.setOnSlideListener(this);

        getDialog().setTitle(R.string.action_settings);

        mListener.afterInflateView();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the SettingsDialogListener so we can send events to the host
            mListener = (SettingsDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement SettingsDialogListener");
        }
    }

    @OnClick({R.id.radioButtonPlayerUser, R.id.radioButtonPlayerComputer,
            R.id.redPiece, R.id.yellowPiece})
    public void onClick(View view) {
        mListener.onDialogViewClick(view);
    }

    @Override
    public void onSlide(int index) {
        mListener.onSlide(index);
    }

    public void setPlayer(String player) {
        if (Constantes.COMPUTER.equals(player)) {
            radioButtonPlayerComputer.setChecked(true);
            radioButtonPlayerUser.setChecked(false);
        } else if (Constantes.PLAYER.equals(player)) {
            radioButtonPlayerComputer.setChecked(false);
            radioButtonPlayerUser.setChecked(true);
        }
    }

    public void setColor(String color_piece) {
        if (Constantes.YELLOW_PIECE.equals(color_piece)) {
            yellowPiece.setChecked(true);
            //redPiece.setChecked(false);
        } else if (Constantes.RED_PIECE.equals(color_piece)) {
            //yellowPiece.setChecked(false);
            redPiece.setChecked(true);
        }
    }

    public void setDifficulty(int depth) {
        switch (depth) {
            case Constantes.DEPTH_EASY:
                difficulty.setText(R.string.level_easy);
                break;

            case Constantes.DEPTH_MEDIUM:
                difficulty.setText(R.string.level_medium);
                break;

            case Constantes.DEPTH_HARD:
                difficulty.setText(R.string.level_hard);
                break;

            default:
                difficulty.setText(R.string.level_easy);
                break;
        }
    }

    public CoordinatorLayout getViewForSnackBar() {
        return viewForSnackBar;
    }
}
