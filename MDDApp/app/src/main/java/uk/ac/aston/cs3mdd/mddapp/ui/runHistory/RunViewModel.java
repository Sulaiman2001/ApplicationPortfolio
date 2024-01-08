package uk.ac.aston.cs3mdd.mddapp.ui.runHistory;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;
import uk.ac.aston.cs3mdd.mddapp.ui.newRun.RunEntity;
import uk.ac.aston.cs3mdd.mddapp.ui.newRun.RunRepository;

public class RunViewModel extends AndroidViewModel {

    private RunRepository runRepository;

    public RunViewModel(Application application) {
        super(application);
        runRepository = new RunRepository(application);
    }

    public void deleteRun(RunEntity run) {
        runRepository.deleteRun(run);
    }

    public LiveData<List<RunEntity>> getAllRuns() {
        return runRepository.getAllRuns();
    }
    public LiveData<List<RunEntity>> getRunsInDescendingOrder() {
        return runRepository.getRunsInDescendingOrder();
    }

}
