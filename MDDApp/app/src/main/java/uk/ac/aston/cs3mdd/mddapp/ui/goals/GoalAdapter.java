package uk.ac.aston.cs3mdd.mddapp.ui.goals;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import uk.ac.aston.cs3mdd.mddapp.R;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {

    private List<GoalEntity> goals;
    private OnItemClickListener mListener;
    private OnUpdateClickListener mUpdateListener;
    private Context context;


    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public interface OnUpdateClickListener{
        void onUpdateClick(int position);
    }

    public List<GoalEntity> getGoals() {
        return goals;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void setOnUpdateClickListener(OnUpdateClickListener listener){mUpdateListener = listener;}

    public GoalAdapter() {
        this.goals = new ArrayList<>();  // Initialize with an empty list
    }

    public GoalAdapter(Context context, List<GoalEntity> goals) {
        this.context = context;
        this.goals = goals;
    }
    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        GoalEntity currentGoal = goals.get(position);

        // Bind data to the TextViews
        holder.textViewTitle.setText(currentGoal.title);
        holder.textViewDate.setText(currentGoal.date);
        holder.textViewDescription.setText(currentGoal.description);

        holder.deleteButton.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onDeleteClick(position);
            }
        });

        holder.editButton.setOnClickListener(v -> {
            if (mUpdateListener != null){
                mUpdateListener.onUpdateClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    public void setGoals(List<GoalEntity> goals) {
        this.goals = goals;
        notifyDataSetChanged();  // Notify the adapter that the data has changed
    }

    static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDate;
        TextView textViewDescription;
        FloatingActionButton deleteButton;
        FloatingActionButton editButton;

        GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);
        }
    }

    public void showDeleteConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this goal?");

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
