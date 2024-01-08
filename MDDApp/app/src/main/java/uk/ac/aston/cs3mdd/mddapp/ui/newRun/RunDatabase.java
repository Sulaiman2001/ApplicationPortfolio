package uk.ac.aston.cs3mdd.mddapp.ui.newRun;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import uk.ac.aston.cs3mdd.mddapp.ui.goals.GoalEntity;

@Database(entities = {RunEntity.class}, version = 2, exportSchema = false)
public abstract class RunDatabase extends RoomDatabase {

    public abstract RunDao runDao();

    private static RunDatabase instance;

    // Create a singleton instance of the database
    public static synchronized RunDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            RunDatabase.class, "run_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}