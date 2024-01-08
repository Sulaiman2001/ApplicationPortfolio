package uk.ac.aston.cs3mdd.mddapp.ui.goals;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GoalDao {
    @Query("SELECT * FROM goals")
    LiveData<List<GoalEntity>> getAllGoals();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGoal(GoalEntity goal);

    @Delete
    void deleteGoal(GoalEntity goal);

    @Update
    void updateGoal(GoalEntity goal);

    @Query("SELECT * FROM goals WHERE title LIKE :searchTitle")
    LiveData<List<GoalEntity>> searchGoalsByTitle(String searchTitle);

}