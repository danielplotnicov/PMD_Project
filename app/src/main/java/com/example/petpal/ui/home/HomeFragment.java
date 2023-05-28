package com.example.petpal.ui.home;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petpal.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PetAdapter petAdapter;
    private List<Pet> petList;
    private PetDatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        databaseHelper = new PetDatabaseHelper(getActivity());

        // Create a new list to hold pets
        petList = new ArrayList<>();

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.pet_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Create and set the adapter
        petAdapter = new PetAdapter(petList, databaseHelper, this);
        recyclerView.setAdapter(petAdapter);

        // Set onClickListener for the FloatingActionButton
        FloatingActionButton addPetButton = view.findViewById(R.id.add_pet_btn);
        addPetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddPetPopup();
            }
        });

        return view;
    }


    private void showAddPetPopup() {
        // Inflate the popup layout
        View popupView = getLayoutInflater().inflate(R.layout.popup_layout, null);

        // Create the popup window
        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        // Find views in the popup layout
        final EditText nameEditText = popupView.findViewById(R.id.name_edit_text);
        final EditText ageEditText = popupView.findViewById(R.id.age_edit_text);
        final EditText descriptionEditText = popupView.findViewById(R.id.description_edit_text);
        Button addButton = popupView.findViewById(R.id.add_pet_button);

        // Set onClickListener for the add button
// Set onClickListener for the add button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the entered data from the EditText fields
                String name = nameEditText.getText().toString().trim();
                String age = ageEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();

                // Validate the data
                if (name.isEmpty() || age.isEmpty() || description.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new Pet object with the entered data
                Pet newPet = new Pet(name, age, description);
                // Add the new Pet to the database
                databaseHelper.addPet(newPet);

                // Refresh the pet list from the database
                petList.clear();
                petList.addAll(databaseHelper.getAllPets());
                petAdapter.notifyDataSetChanged();

                // Dismiss the popup window
                popupWindow.dismiss();
            }
        });


        // Show the popup window at the center of the screen
        popupWindow.showAtLocation(recyclerView, Gravity.CENTER, 0, 0);
    }


    @Override
    public void onResume() {
        super.onResume();
        // Load pet list from the database
        petList.clear();
        petList.addAll(databaseHelper.getAllPets());
        petAdapter.notifyDataSetChanged();
    }
}
