package com.foodmgmt.dontstarve.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.foodmgmt.dontstarve.R;

public class Verify extends Fragment {
    private String name, email, regno;
    Boolean isVerified,isOnboardingDone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_verify, container, false);
        name = getArguments().getString("name");
        email = getArguments().getString("email");
        regno = getArguments().getString("regno");
        isVerified = false;
        isOnboardingDone = false;

        /**
         * Open camera to take a picture
         * then send the input to a url to verify and move to verification screen
         * give option to take new image or wait to be approved by admin
         *
         * or
         *
         * We can do is approve and limit features as we decided before, thus no need for verification screen,
         * but needs to regularly fetch from a "Database/FS" and see if the user has been approved
         */

        return v;
    }
}