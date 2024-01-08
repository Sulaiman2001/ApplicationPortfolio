package uk.ac.aston.cs3mdd.mddapp.ui.goals;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoalRepository {
    private GoalDao goalDao;
    private LiveData<List<GoalEntity>> allGoals;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public GoalRepository(Application application) {
        GoalDatabase database = GoalDatabase.getDatabase(application);
        goalDao = database.goalDao();
        allGoals = goalDao.getAllGoals();
    }

    public LiveData<List<GoalEntity>> getAllGoals() {
        return allGoals;
    }

    public void insertGoal(GoalEntity goal) {
        executorService.execute(() -> {
            goalDao.insertGoal(goal);
            Log.d("GoalRepository", "Goal added to database: " + goal.getTitle());
        });
    }

    public void deleteGoal(GoalEntity goal) {
        executorService.execute(() -> {
            goalDao.deleteGoal(goal);
            Log.d("GoalRepository", "Goal deleted from database: " + goal.getTitle());
        });
    }

    public void updateGoal(GoalEntity goal) {
        executorService.execute(() -> {
            goalDao.updateGoal(goal);
            Log.d("GoalRepository", "Goal is updated in the database: " + goal.getTitle());
        });
    }

    public LiveData<List<GoalEntity>> searchGoalsByTitle(String title) {
        return goalDao.searchGoalsByTitle(title);
    }
}
