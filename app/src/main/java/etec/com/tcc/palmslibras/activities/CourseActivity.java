package etec.com.tcc.palmslibras.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import etec.com.tcc.palmslibras.R;
import etec.com.tcc.palmslibras.database.DatabaseHelper;
import etec.com.tcc.palmslibras.utils.SessionManager;

public class CourseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        ImageView backButton = findViewById(R.id.backButton);
        TextView tvUnitsCount = findViewById(R.id.tvUnitsCount);
        TextView tvTotalXp = findViewById(R.id.tvTotalXp);
        View cardUnit1 = findViewById(R.id.cardUnit1);
        View cardUnit2 = findViewById(R.id.cardUnit2);

        SessionManager sessionManager = new SessionManager(this);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        long userId = sessionManager.getUserId();
        if (userId != -1 && tvTotalXp != null) {
            int xp = dbHelper.getUserById(userId) != null ? dbHelper.getUserById(userId).getXp() : 0;
            tvTotalXp.setText(xp + " XP");
        }
        if (tvUnitsCount != null) {
            int completed = sessionManager.isUnit1Completed() ? 1 : 0;
            tvUnitsCount.setText(getString(R.string.units_count_format, completed, 2));
        }

        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        if (cardUnit1 != null) {
            cardUnit1.setOnClickListener(v -> {
                Intent intent = new Intent(this, UnitActivity.class);
                intent.putExtra("UNIT_NUMBER", 1);
                startActivity(intent);
            });
        }

        if (cardUnit2 != null) {
            boolean unlocked = sessionManager.isUnit2Unlocked();
            ImageView unit2IconImage = findViewById(R.id.unit2IconImage);
            View unit2Container = findViewById(R.id.unit2Container);
            if (unlocked) {
                ((androidx.cardview.widget.CardView) cardUnit2).setCardBackgroundColor(getColor(R.color.white));
                if (unit2IconImage != null) {
                    unit2IconImage.setImageResource(R.drawable.ic_play_circle);
                    unit2IconImage.setColorFilter(getColor(R.color.purple_700));
                }
                if (unit2Container != null) {
                    unit2Container.setBackgroundColor(getColor(android.R.color.transparent));
                }
            }
            cardUnit2.setOnClickListener(v -> {
                if (sessionManager.isUnit2Unlocked()) {
                    Intent intent = new Intent(this, UnitActivity.class);
                    intent.putExtra("UNIT_NUMBER", 2);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, getString(R.string.in_development), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
