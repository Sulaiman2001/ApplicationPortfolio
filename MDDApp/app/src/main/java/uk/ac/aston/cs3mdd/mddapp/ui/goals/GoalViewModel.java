package uk.ac.aston.cs3mdd.mddapp.ui.goals;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class GoalViewModel extends AndroidViewModel {

    private GoalRepository repository;
    private LiveData<List<GoalEntity>> allGoals;

    public GoalViewModel(@NonNull Application application) {
        super(application);
        repository = new GoalRepository(application);
        allGoals = repository.getAllGoals();
    }

    public LiveData<List<GoalEntity>> getAllGoals() {
        return allGoals;
    }

    public void insertGoal(GoalEntity goal) {
        repository.insertGoal(goal);
    }

    public void deleteGoal(GoalEntity goal) {
        repository.deleteGoal(goal);
    }

    public void updateGoal(GoalEntity goal){repository.updateGoal(goal);}

    public LiveData<List<GoalEntity>> searchGoalsByTitle(String title) {
        return repository.searchGoalsByTitle(title);
    }

}
