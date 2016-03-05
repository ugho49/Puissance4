package fr.nantes.stephan.puissance4;

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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ughostephan on 04/03/2016.
 */
public class SettingsDialog extends AppCompatDialogFragment implements RangeSliderView.OnSlideListener {

    @Bind(R.id.difficulty)
    TextView difficulty;
    @Bind(R.id.rsv)
    RangeSliderView rsv;
    @Bind(R.id.radioButtonPlayerUser)
    RadioButton radioButtonPlayerUser;
    @Bind(R.id.radioButtonPlayerComputer)
    RadioButton radioButtonPlayerComputer;
    @Bind(R.id.redPiece)
    RadioButton redPiece;
    @Bind(R.id.yellowPiece)
    RadioButton yellowPiece;
    @Bind(R.id.viewForSnackBar)
    CoordinatorLayout viewForSnackBar;

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
        ButterKnife.bind(this, rootView);

        //rsv.setInitialIndex(0);
        rsv.setOnSlideListener(this);

        getDialog().setTitle(R.string.action_settings);

        mListener.afterInflateView();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
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
        if (GameUtils.COMPUTER.equals(player)) {
            radioButtonPlayerComputer.setChecked(true);
            radioButtonPlayerUser.setChecked(false);
        } else if (GameUtils.PLAYER.equals(player)) {
            radioButtonPlayerComputer.setChecked(false);
            radioButtonPlayerUser.setChecked(true);
        }
    }

    public void setColor(String color_piece) {

        if (GameUtils.YELLOW_PIECE.equals(color_piece)) {
            yellowPiece.setChecked(true);
            //redPiece.setChecked(false);
        } else if (GameUtils.RED_PIECE.equals(color_piece)) {
            //yellowPiece.setChecked(false);
            redPiece.setChecked(true);
        }
    }

    public void setDifficulty(int depth) {
        switch (depth) {
            case GameUtils.DEPTH_EASY:
                difficulty.setText(R.string.level_easy);
                break;

            case GameUtils.DEPTH_MEDIUM:
                difficulty.setText(R.string.level_medium);
                break;

            case GameUtils.DEPTH_HARD:
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
