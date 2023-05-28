package com.example.petpal.ui.gallery;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;

import com.example.petpal.R;

public class FullscreenDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Retrieve the image from the arguments
        Bitmap image = getArguments().getParcelable("image");

        // Create a Dialog with a custom layout
        Dialog dialog = new Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_fullscreen_image);

        // Find the ImageView in the dialog layout and set the image
        ImageView imageView = dialog.findViewById(R.id.fullscreen_image);
        imageView.setImageBitmap(image);

        // Set click listener to dismiss the dialog when clicked
        imageView.setOnClickListener(v -> dismiss());

        return dialog;
    }
}
