package etec.com.tcc.palmslibras.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import etec.com.tcc.palmslibras.R;
import etec.com.tcc.palmslibras.activities.CourseActivity;
import etec.com.tcc.palmslibras.models.Course;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private final List<Course> courseList;
    private final Context context;

    public CourseAdapter(List<Course> courseList, Context context) {
        this.courseList = courseList;
        this.context = context;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.title.setText(course.getTitle());
        holder.description.setText(course.getDescription());
        int bgColor = ContextCompat.getColor(context, course.getBackgroundColorResId());
        holder.cardContainer.setBackgroundColor(bgColor);
        holder.rootCard.setCardBackgroundColor(bgColor);
        holder.icon.setImageResource(course.getIconResId());
        boolean isAlphabet = course.getTitle().equals(context.getString(R.string.course_alphabet_title));
        if (isAlphabet) {
            holder.icon.setImageResource(R.drawable.ic_abc_placeholder);
            holder.icon.setVisibility(View.VISIBLE);
            holder.iconText.setVisibility(View.GONE);
        } else {
            holder.icon.setVisibility(View.GONE);
            holder.iconText.setVisibility(View.GONE);
        }
        if (course.isEnabled()) {
            holder.continueButton.setEnabled(true);
            holder.continueButton.setText(R.string.continue_button);
            holder.continueButton.setBackgroundResource(R.drawable.button_beige_background);
            holder.continueButton.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            holder.continueButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, CourseActivity.class);
                context.startActivity(intent);
            });
        } else {
            holder.continueButton.setEnabled(true);
            holder.continueButton.setText(R.string.in_development);
            holder.continueButton.setBackgroundResource(R.drawable.button_secondary_background);
            holder.continueButton.setTextColor(ContextCompat.getColor(context, R.color.gray_dark));
            holder.continueButton.setOnClickListener(v -> {
                android.widget.Toast.makeText(context, R.string.in_development_toast, android.widget.Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        TextView iconText;
        Button continueButton;
        android.widget.LinearLayout cardContainer;
        android.widget.ImageView icon;
        androidx.cardview.widget.CardView rootCard;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.courseTitle);
            description = itemView.findViewById(R.id.courseDescription);
            continueButton = itemView.findViewById(R.id.continueButton);
            cardContainer = itemView.findViewById(R.id.courseCardContainer);
            icon = itemView.findViewById(R.id.courseIcon);
            iconText = itemView.findViewById(R.id.iconText);
            rootCard = itemView.findViewById(R.id.rootCard);
        }
    }
}