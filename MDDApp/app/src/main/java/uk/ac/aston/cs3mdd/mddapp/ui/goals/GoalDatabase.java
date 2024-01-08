package uk.ac.aston.cs3mdd.mddapp.ui.goals;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {GoalEntity.class}, version = 1, exportSchema = false)
public abstract class GoalDatabase extends RoomDatabase {

    public abstract GoalDao goalDao();

    private static volatile GoalDatabase INSTANCE;

    public static GoalDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (GoalDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    GoalDatabase.class, "goal_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

