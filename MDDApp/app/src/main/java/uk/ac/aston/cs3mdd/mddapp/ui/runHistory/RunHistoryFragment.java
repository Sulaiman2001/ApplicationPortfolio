package uk.ac.aston.cs3mdd.mddapp.ui.runHistory;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import uk.ac.aston.cs3mdd.mddapp.R;
import uk.ac.aston.cs3mdd.mddapp.ui.newRun.RunEntity;
import uk.ac.aston.cs3mdd.mddapp.ui.newRun.RunRepository;

public class RunHistoryFragment extends Fragment implements RunAdapter.OnItemClickListener {

    private RunRepository runRepository;
    private RunAdapter runAdapter;
    private RunViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_run_history, container, false);

        RecyclerView recyclerViewRuns = rootView.findViewById(R.id.recyclerView);
        recyclerViewRuns.setLayoutManager(new LinearLayoutManager(requireContext()));

        runRepository = new RunRepository(requireActivity().getApplication());
        viewModel = new ViewModelProvider(this).get(RunViewModel.class);

        // Initialize the RunAdapter with the correct context
        runAdapter = new RunAdapter(requireContext(), new ArrayList<>());
        recyclerViewRuns.setAdapter(runAdapter);

        // Set the click listener for the delete action
        runAdapter.setOnItemClickListener(this);

        LiveData<List<RunEntity>> liveDataRunList = viewModel.getRunsInDescendingOrder();

        liveDataRunList.observe(getViewLifecycleOwner(), new Observer<List<RunEntity>>() {
            @Override
            public void onChanged(List<RunEntity> runList) {
                Log.d("RunHistoryFragment", "Retrieved runList: " + runList);

                if (runList != null) {
                    runAdapter.setRunList(runList);
                } else {
                    Log.e("RunHistoryFragment", "Error fetching runs from the database");
                }
            }
        });

        return rootView;
    }

    @Override
    public void onDeleteClick(int position) {
        RunEntity runToDelete = runAdapter.getRunList().get(position);

        viewModel.deleteRun(runToDelete);
    }
}
