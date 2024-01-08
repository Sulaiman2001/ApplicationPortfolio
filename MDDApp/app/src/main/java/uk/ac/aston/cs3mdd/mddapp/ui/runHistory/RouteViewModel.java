package uk.ac.aston.cs3mdd.mddapp.ui.runHistory;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import uk.ac.aston.cs3mdd.mddapp.ui.newRun.RunEntity;
import uk.ac.aston.cs3mdd.mddapp.ui.newRun.RunRepository;

public class RouteViewModel extends AndroidViewModel {

    private RunRepository runRepository;

    public RouteViewModel(Application application) {
        super(application);
        runRepository = new RunRepository(application);
    }

    public LiveData<RunEntity> getRouteLiveData(long runId) {
        return runRepository.getRouteLiveData(runId);
    }

    public LiveData<List<LatLng>> getPolylineLiveData(long runId) {
        return runRepository.getPolylineLiveData(runId);
    }
}
