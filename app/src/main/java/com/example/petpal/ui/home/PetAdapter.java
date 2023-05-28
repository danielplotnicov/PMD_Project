package com.example.petpal.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petpal.R;

import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {
    private List<Pet> petList;
    private PetDatabaseHelper databaseHelper;
    private HomeFragment homeFragment;

    public PetAdapter(List<Pet> petList, PetDatabaseHelper databaseHelper, HomeFragment homeFragment) {
        this.petList = petList;
        this.databaseHelper = databaseHelper;
        this.homeFragment = homeFragment;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pet, parent, false);
        return new PetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PetViewHolder holder, final int position) {
        final Pet pet = petList.get(position);
        holder.nameTextView.setText(pet.getName());
        holder.ageTextView.setText(pet.getAge());
        holder.descriptionTextView.setText(pet.getDescription());

        // Set click listener for the delete button
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int deletedPosition = holder.getAdapterPosition();
                String petName = pet.getName();

                // Delete the pet from the database
                databaseHelper.deletePet(petName);

                // Remove the pet from the list
                petList.remove(deletedPosition);
                notifyItemRemoved(deletedPosition);
                notifyItemRangeChanged(deletedPosition, petList.size());
            }
        });
    }




    @Override
    public int getItemCount() {
        return petList.size();
    }

    public static class PetViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView ageTextView;
        public TextView descriptionTextView;
        public Button deleteButton;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.pet_name_text);
            ageTextView = itemView.findViewById(R.id.pet_age_text);
            descriptionTextView = itemView.findViewById(R.id.pet_description_text);
            deleteButton = itemView.findViewById(R.id.delete_pet_btn);
        }
    }

    public int getPetId(int position) {
        Pet pet = petList.get(position);
        return pet.getId();
    }

}
