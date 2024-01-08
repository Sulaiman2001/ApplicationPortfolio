package uk.ac.aston.cs3mdd.mddapp.ui.newRun;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;


@Dao
public interface RunDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertRun(RunEntity run);

    @Delete
    void deleteRun(RunEntity run);

    @Query("SELECT * FROM run_table")
    LiveData<List<RunEntity>> getAllRuns();

    @Query("SELECT * FROM run_table WHERE id = :runId")
    LiveData<RunEntity> getRunById(long runId);

    @Query("SELECT * FROM run_table ORDER BY date DESC")
    LiveData<List<RunEntity>> getSortedRunsDescending();

}
