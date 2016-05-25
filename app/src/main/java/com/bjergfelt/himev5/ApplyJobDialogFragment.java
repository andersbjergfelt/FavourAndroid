package com.bjergfelt.himev5;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * Created by ABjergfelt on 25-05-2016.
 */
public class ApplyJobDialogFragment extends DialogFragment {
    public ApplyJobDialogFragment()
    {
    }


    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Apply for job");

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.apply_job,null))
                // Add action buttons
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ApplyJobDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }



}
