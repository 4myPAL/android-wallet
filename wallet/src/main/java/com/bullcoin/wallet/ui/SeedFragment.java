package com.bullcoin.wallet.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bullcoin.core.wallet.Wallet;
import com.bullcoin.wallet.Constants;
import com.bullcoin.wallet.R;
import com.bullcoin.wallet.util.Fonts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates and shows a new passphrase
 */
public class SeedFragment extends Fragment {
    private static final Logger log = LoggerFactory.getLogger(SeedFragment.class);

    private WelcomeFragment.Listener mListener;
    private boolean hasExtraEntropy = false;
    private TextView mnemonicView;

    public SeedFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seed, container, false);

        TextView seedFontIcon = (TextView) view.findViewById(R.id.seed_icon);
        Fonts.setTypeface(seedFontIcon, Fonts.Font.COINOMI_FONT_ICONS);

        final Button buttonNext = (Button) view.findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.info("Clicked restore wallet");
                if (mListener != null) {
                    mListener.onSeedCreated(mnemonicView.getText().toString());
                }
            }
        });
        buttonNext.setEnabled(false);

        mnemonicView = (TextView) view.findViewById(R.id.seed);
        generateNewMnemonic();

        // Touch the seed icon to generate extra long seed
        seedFontIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasExtraEntropy = !hasExtraEntropy; // toggle
                generateNewMnemonic();
                if (hasExtraEntropy) {
                    Toast.makeText(getActivity(), R.string.extra_entropy, Toast.LENGTH_SHORT).show();
                }
            }
        });

        final CheckBox backedUpSeed = (CheckBox) view.findViewById(R.id.backed_up_seed);
        backedUpSeed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonNext.setEnabled(isChecked);
            }
        });

        View.OnClickListener generateNewSeedListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateNewMnemonic();
            }
        };

        mnemonicView.setOnClickListener(generateNewSeedListener);
        view.findViewById(R.id.seed_regenerate_title).setOnClickListener(generateNewSeedListener);

        return view;
    }

    private void generateNewMnemonic() {
        log.info("Clicked generate a new mnemonic");
        String mnemonic;
        if (hasExtraEntropy) {
            mnemonic = Wallet.generateMnemonicString(Constants.SEED_ENTROPY_EXTRA);
        } else {
            mnemonic = Wallet.generateMnemonicString(Constants.SEED_ENTROPY_DEFAULT);
        }
        mnemonicView.setText(mnemonic);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (WelcomeFragment.Listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement WelcomeFragment.OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
