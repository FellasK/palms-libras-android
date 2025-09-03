package etec.com.tcc.palmslibras.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import etec.com.tcc.palmslibras.R;
import etec.com.tcc.palmslibras.adapters.RankingAdapter;
import etec.com.tcc.palmslibras.database.DatabaseHelper;
import etec.com.tcc.palmslibras.models.User;

public class RankingFragment extends Fragment {

    private RecyclerView rankingRecyclerView;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        dbHelper = new DatabaseHelper(getContext());
        rankingRecyclerView = view.findViewById(R.id.rankingRecyclerView);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<User> userList = dbHelper.getAllUsersSortedByXP();
        RankingAdapter adapter = new RankingAdapter(userList, getContext());
        rankingRecyclerView.setAdapter(adapter);

        return view;
    }
}