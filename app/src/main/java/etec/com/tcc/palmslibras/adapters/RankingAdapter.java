package etec.com.tcc.palmslibras.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import etec.com.tcc.palmslibras.R;
import etec.com.tcc.palmslibras.models.User;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

    private final List<User> userList;
    private final Context context;

    public RankingAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ranking, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        User user = userList.get(position);
        holder.position.setText(String.valueOf(position + 1));
        holder.name.setText(user.getName());
        holder.xp.setText(String.format(Locale.getDefault(), "%d XP", user.getXp()));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class RankingViewHolder extends RecyclerView.ViewHolder {
        TextView position, name, xp;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            position = itemView.findViewById(R.id.rankingPosition);
            name = itemView.findViewById(R.id.rankingName);
            xp = itemView.findViewById(R.id.rankingXp);
        }
    }
}