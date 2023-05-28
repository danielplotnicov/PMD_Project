package com.example.petpal.ui.gallery;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.petpal.R;
import com.google.android.flexbox.FlexboxLayout;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private static final int REQUEST_CAMERA_PERMISSION = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 102;
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 103;
    private static final int REQUEST_SELECT_IMAGE = 104;

    private FlexboxLayout photoGallery;
    private List<Bitmap> photos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        photoGallery = rootView.findViewById(R.id.photo_gallery);

        // Initialize the list of photos
        photos = new ArrayList<>();

        // Check and request permission to access the camera
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        }

        // Check and request permission to read external storage
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestReadExternalStoragePermission();
        }

        // Set click listener for the "Add Photo" button
        rootView.findViewById(R.id.add_photo_btn).setOnClickListener(v -> {
            // Show options to either capture a photo or select one from the gallery
            showPhotoOptionsDialog();
        });

        return rootView;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    private void requestReadExternalStoragePermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
    }

    private void showPhotoOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Photo");
        String[] options = {"Capture Photo", "Select from Gallery"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Capture photo
                capturePhoto();
            } else if (which == 1) {
                // Select from gallery
                selectPhotoFromGallery();
            }
        });
        builder.show();
    }


    private void capturePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(requireContext(), "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectPhotoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    if (imageBitmap != null) {
                        addPhotoToGallery(imageBitmap);
                    } else {
                        Toast.makeText(requireContext(), "Failed to capture photo", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (requestCode == REQUEST_SELECT_IMAGE) {
                if (data != null && data.getData() != null) {
                    Uri selectedImageUri = data.getData();
                    try {
                        InputStream inputStream = requireActivity().getContentResolver().openInputStream(selectedImageUri);
                        if (inputStream != null) {
                            Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
                            if (imageBitmap != null) {
                                addPhotoToGallery(imageBitmap);
                            } else {
                                Toast.makeText(requireContext(), "Failed to select photo", Toast.LENGTH_SHORT).show();
                            }
                            inputStream.close();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "File not found", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error reading file", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void addPhotoToGallery(Bitmap photo) {
        // Determine the desired width for the images (0.48 of the screen width)
        WindowManager windowManager = (WindowManager) requireContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int desiredWidth = (int) (screenWidth * 0.45);

        // Resize the image to a square size
        Bitmap resizedPhoto = resizeToSquare(photo, desiredWidth);

        // Add the resized photo to the gallery
        photos.add(resizedPhoto);

        // Create and configure the ImageView
        ImageView imageView = new ImageView(requireContext());
        imageView.setImageBitmap(resizedPhoto);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setAdjustViewBounds(true);
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(desiredWidth, desiredWidth);
        layoutParams.setMargins(10, 10, 10, 10);
        imageView.setLayoutParams(layoutParams);

        // Set click listener to open fullscreen dialog
        imageView.setOnClickListener(v -> {
            // Create a DialogFragment and pass the clicked image as an argument
            FullscreenDialogFragment dialogFragment = new FullscreenDialogFragment();
            Bundle args = new Bundle();
            args.putParcelable("image", resizedPhoto);
            dialogFragment.setArguments(args);

            // Show the fullscreen dialog
            dialogFragment.show(requireFragmentManager(), "fullscreen_dialog");
        });

        // Add the ImageView to the photoGallery layout
        photoGallery.addView(imageView);
    }



    private Bitmap resizeToSquare(Bitmap photo, int width) {
        int originalWidth = photo.getWidth();
        int originalHeight = photo.getHeight();

        int newSize = Math.min(originalWidth, originalHeight);
        int x = (originalWidth - newSize) / 2;
        int y = (originalHeight - newSize) / 2;

        // Crop the image to a square size
        Bitmap croppedPhoto = Bitmap.createBitmap(photo, x, y, newSize, newSize);

        // Resize the cropped image to the desired width while maintaining the aspect ratio
        float scaleFactor = (float) width / newSize;
        int scaledWidth = (int) (newSize * scaleFactor);
        int scaledHeight = (int) (newSize * scaleFactor);
        Bitmap resizedPhoto = Bitmap.createScaledBitmap(croppedPhoto, scaledWidth, scaledHeight, false);

        // Recycle the cropped image if needed
        if (croppedPhoto != resizedPhoto) {
            croppedPhoto.recycle();
        }

        return resizedPhoto;
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, handle accordingly
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_READ_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Read external storage permission granted, handle accordingly
            } else {
                Toast.makeText(requireContext(), "Read external storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
