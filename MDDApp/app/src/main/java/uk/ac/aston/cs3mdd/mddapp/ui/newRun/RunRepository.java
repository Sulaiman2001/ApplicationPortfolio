package uk.ac.aston.cs3mdd.mddapp.ui.newRun;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunRepository {

    private RunDao runDao;
    private LiveData<List<RunEntity>> allRuns;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public RunRepository(Application application) {
        RunDatabase database = RunDatabase.getInstance(application);
        runDao = database.runDao();
        allRuns = runDao.getAllRuns();
    }

    public void insertRun(RunEntity run, InsertRunCallback callback) {
        executorService.execute(() -> {
            long runId = runDao.insertRun(run);
            Log.d("RunRepository", "Run added to database with ID: " + runId);

            // Notify the callback with the inserted ID
            if (callback != null) {
                callback.onRunInserted(runId);
            }
        });
    }

    public interface InsertRunCallback {
        void onRunInserted(long runId);
    }

    public void deleteRun(RunEntity run) {
        executorService.execute(() -> {
            runDao.deleteRun(run);
            Log.d("RunRepository", "Run deleted from database: " + run.getDate());
        });
    }

//

    // Get All Runs
    public LiveData<List<RunEntity>> getAllRuns() {
        return allRuns;
    }

    public LiveData<RunEntity> getRouteLiveData(long runId) {
        return runDao.getRunById(runId);
    }

    public LiveData<List<LatLng>> getPolylineLiveData(long runId) {
        return Transformations.map(runDao.getRunById(runId), runEntity -> {
            if (runEntity != null) {
                return PolylineConverter.parsePolyline(runEntity.getPolylinePoints());
            } else {
                return new ArrayList<>(); // Return an empty list if the runEntity is null
            }
        });
    }
    public LiveData<List<RunEntity>> getRunsInDescendingOrder() {
        return runDao.getSortedRunsDescending();
    }


}
