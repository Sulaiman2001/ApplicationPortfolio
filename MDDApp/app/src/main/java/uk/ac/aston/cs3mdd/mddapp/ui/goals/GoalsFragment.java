package uk.ac.aston.cs3mdd.mddapp.ui.goals;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import uk.ac.aston.cs3mdd.mddapp.R;

public class GoalsFragment extends Fragment {

    private GoalViewModel goalViewModel;
    private GoalAdapter goalAdapter;
    private EditText textSearch;
    private Button buttonSearch;
    private TextView textViewResult;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the GoalViewModel
        goalViewModel = new ViewModelProvider(this).get(GoalViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        // Initialize RecyclerView and its adapter
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        goalAdapter = new GoalAdapter(requireContext(), new ArrayList<>());

        // Enable the delete button
        goalAdapter.setOnItemClickListener(position -> {
            GoalEntity goalToDelete = goalAdapter.getGoals().get(position);
            showDeleteConfirmationDialog(goalToDelete);
        });

        // Enable the update button
        goalAdapter.setOnUpdateClickListener(position -> {
            GoalEntity goalToUpdate = goalAdapter.getGoals().get(position);
            showUpdatePopup(goalToUpdate);
        });


        recyclerView.setAdapter(goalAdapter);

        FloatingActionButton fabAdd = view.findViewById(R.id.floatingActionButton2);
        fabAdd.setOnClickListener(v -> showPopup());

        buttonSearch = view.findViewById(R.id.buttonSearch);
        textSearch = view.findViewById(R.id.textSearch);
        buttonSearch.setOnClickListener(v -> search());
        textViewResult = view.findViewById(R.id.textViewResult);


        // Observe the LiveData
        goalViewModel.getAllGoals().observe(getViewLifecycleOwner(), goals -> {
            goalAdapter.setGoals(goals);
        });

        return view;
    }

    private void search() {
        String searchQuery = textSearch.getText().toString().trim().toLowerCase();

        // If the search query is empty, show all goals
        if (searchQuery.isEmpty()) {
            goalViewModel.getAllGoals().observe(getViewLifecycleOwner(), goals -> {
                goalAdapter.setGoals(goals);
                textViewResult.setText("");
            });
        } else {
            // Perform the partial match search using the ViewModel
            goalViewModel.searchGoalsByTitle(searchQuery).observe(getViewLifecycleOwner(), goals -> {
                List<GoalEntity> filteredGoals = new ArrayList<>();

                for (GoalEntity goal : goals) {
                    // Convert the goal title to lowercase for case-insensitive comparison
                    String goalTitle = goal.getTitle().toLowerCase();

                    // Check if the lowercase goal title contains the lowercase search query
                    if (goalTitle.contains(searchQuery) || searchQuery.contains(goalTitle)) {
                        filteredGoals.add(goal);
                    }
                }

                goalAdapter.setGoals(filteredGoals);

                if (goals.isEmpty()) {
                    // Update the TextView with the search result text
                    textViewResult.setText("No goals found with the specified title");
                } else {
                    textViewResult.setText(""); // Clear the result text
                }
            });
        }
    }





    // Method to show the add popup dialog
    private void showPopup() {
        // Inflate the popup layout
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_layout, null);

        // Create the AlertDialog and set its properties
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle("Add Goal")
                .setPositiveButton("Add", (dialog, which) -> {
                    // Handle the "Add" button click
                    EditText titleEditText = dialogView.findViewById(R.id.titleEditText);
                    EditText dateEditText = dialogView.findViewById(R.id.dateEditText);
                    EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);

                    String title = titleEditText.getText().toString();
                    String date = dateEditText.getText().toString();
                    String description = descriptionEditText.getText().toString();

                    // Save data to Room Database or perform any other necessary operations
                    // Check if any field is empty before adding to the database
                    if (!title.isEmpty() && !date.isEmpty() && !description.isEmpty()) {
                        GoalEntity newGoal = new GoalEntity();
                        newGoal.title = title;
                        newGoal.date = date;
                        newGoal.description = description;

                        // Insert the new goal into the database using the ViewModel
                        goalViewModel.insertGoal(newGoal);

                    }
                    // Dismiss the dialog
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Handle the "Cancel" button click
                    // Dismiss the dialog
                    dialog.dismiss();
                });

        // Create and show the AlertDialog
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void showUpdatePopup(GoalEntity goalToUpdate) {
        // Inflate the update popup layout
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_layout, null);

        // Pre-fill the fields with the existing goal details
        EditText titleEditText = dialogView.findViewById(R.id.titleEditText);
        EditText dateEditText = dialogView.findViewById(R.id.dateEditText);
        EditText descriptionEditText = dialogView.findViewById(R.id.descriptionEditText);

        titleEditText.setText(goalToUpdate.getTitle());
        dateEditText.setText(goalToUpdate.getDate());
        descriptionEditText.setText(goalToUpdate.getDescription());

        // Create the AlertDialog and set its properties
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle("Update Goal")
                .setPositiveButton("Update", (dialog, which) -> {
                    // Handle the "Update" button click
                    String updatedTitle = titleEditText.getText().toString();
                    String updatedDate = dateEditText.getText().toString();
                    String updatedDescription = descriptionEditText.getText().toString();

                    // Check if any field is empty before updating the database
                    if (!updatedTitle.isEmpty() && !updatedDate.isEmpty() && !updatedDescription.isEmpty()) {
                        GoalEntity updatedGoal = new GoalEntity();
                        updatedGoal.setId(goalToUpdate.getId());
                        updatedGoal.setTitle(updatedTitle);
                        updatedGoal.setDate(updatedDate);
                        updatedGoal.setDescription(updatedDescription);

                        // Update the goal in the database using the ViewModel
                        goalViewModel.updateGoal(updatedGoal);
                    }
                    // Dismiss the dialog
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Handle the "Cancel" button click
                    // Dismiss the dialog
                    dialog.dismiss();
                });

        // Create and show the AlertDialog
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }


    private void showDeleteConfirmationDialog(GoalEntity goal) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext())
                .setTitle("Delete Goal")
                .setMessage("Are you sure you want to delete this goal?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Handle the "Delete" button click
                    goalViewModel.deleteGoal(goal);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Handle the "Cancel" button click
                    dialog.dismiss();
                });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
}

