package uk.ac.aston.cs3mdd.mddapp.ui.runHistory;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import uk.ac.aston.cs3mdd.mddapp.R;
import uk.ac.aston.cs3mdd.mddapp.ui.newRun.RunEntity;

public class RunAdapter extends RecyclerView.Adapter<RunAdapter.RunViewHolder> {

    private List<RunEntity> runList;
    private OnItemClickListener mListener;
    private Context context;


    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public RunAdapter(Context context, List<RunEntity> runList) {
        this.context = context;
        this.runList = runList;
    }

    public RunAdapter(List<RunEntity> runList) {
        this.runList = runList;
    }

    public List<RunEntity> getRunList() {
        return runList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public RunViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_run, parent, false);
        return new RunViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RunViewHolder holder, int position) {
        RunEntity currentRun = runList.get(position);

        String formattedDistance = String.format("%.2f", currentRun.getDistance());
        String formattedPace = String.format("%.2f", currentRun.getPace());


        // Bind data to the TextViews
        holder.textViewDate.setText("Date: " + currentRun.getFormattedDate());
        holder.textViewTime.setText("Duration: " + currentRun.getDuration());
        holder.textViewDistance.setText("Distance: " + formattedDistance + " km");
        holder.textViewPace.setText("Pace: " + formattedPace + " km/h");

        holder.deleteButton.setOnClickListener(v -> {
            // Show the delete confirmation dialog
            showDeleteConfirmationDialog(position);
        });

        holder.routeButton.setOnClickListener(view -> {
            long selectedRunId = runList.get(position).getId();

            // Prepare run statistics as a string
            String runStats = "Date: " + currentRun.getFormattedDate() + "\n" +
                    "Duration: " + currentRun.getDuration() + "\n" +
                    "Distance: " + formattedDistance + " km" + "\n" +
                    "Pace: " + formattedPace + " km/h";

            // Navigate to RouteFragment
            Bundle bundle = new Bundle();
            bundle.putLong("selectedRunId", selectedRunId);
            bundle.putString("runStats", runStats);
            Navigation.findNavController(view).navigate(R.id.action_run_history_to_route, bundle);
        });
    }


    @Override
    public int getItemCount() {
        return runList.size();
    }

    public void setRunList(List<RunEntity> runList) {
        this.runList = runList;
        notifyDataSetChanged();  // Notify the adapter that the data has changed
    }

    static class RunViewHolder extends RecyclerView.ViewHolder {
        FloatingActionButton routeButton;
        TextView textViewDate;
        TextView textViewTime;
        TextView textViewDistance;
        TextView textViewPace;
        FloatingActionButton deleteButton;  // Add a delete button or image as needed

        RunViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewDistance = itemView.findViewById(R.id.textViewDistance);
            textViewPace = itemView.findViewById(R.id.textViewPace);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            routeButton = itemView.findViewById(R.id.routeButton);
        }
    }
    public void showDeleteConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this run?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            if (mListener != null) {
                mListener.onDeleteClick(position);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
