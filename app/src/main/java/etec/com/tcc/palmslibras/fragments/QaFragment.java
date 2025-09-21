package etec.com.tcc.palmslibras.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import etec.com.tcc.palmslibras.R;
import etec.com.tcc.palmslibras.models.Gesture;
import etec.com.tcc.palmslibras.models.Lesson;
import etec.com.tcc.palmslibras.utils.OnLessonCompleteListener;

public class QaFragment extends Fragment implements View.OnClickListener {

    private OnLessonCompleteListener listener;
    private Lesson lessonData;
    private List<ImageView> optionViews = new ArrayList<>();
    private boolean isAnswered = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnLessonCompleteListener) {
            listener = (OnLessonCompleteListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnLessonCompleteListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qa, container, false);

        if (getArguments() != null) {
            lessonData = (Lesson) getArguments().getSerializable("lesson_data");
        }

        TextView tvQuestion = view.findViewById(R.id.tvQuestion);
        String questionText = getString(R.string.qa_question_template, lessonData.getCorrectAnswer().getLetter());
        tvQuestion.setText(questionText);

        optionViews.add(view.findViewById(R.id.ivOptionA));
        optionViews.add(view.findViewById(R.id.ivOptionB));
        optionViews.add(view.findViewById(R.id.ivOptionC));
        optionViews.add(view.findViewById(R.id.ivOptionD));

        for (int i = 0; i < optionViews.size(); i++) {
            ImageView imageView = optionViews.get(i);
            Gesture gesture = lessonData.getOptions().get(i);
            imageView.setImageResource(gesture.getDrawableId());
            imageView.setTag(gesture);

            String contentDesc = getString(R.string.qa_option_image_description, gesture.getLetter());
            imageView.setContentDescription(contentDesc);

            imageView.setOnClickListener(this);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        if (isAnswered) return;
        isAnswered = true;

        Gesture selectedGesture = (Gesture) v.getTag();
        Gesture correctGesture = lessonData.getCorrectAnswer();

        boolean isCorrect = selectedGesture.getLetter().equals(correctGesture.getLetter());

        if (isCorrect) {
            v.setBackgroundResource(R.drawable.choice_correct_background);
        } else {
            v.setBackgroundResource(R.drawable.choice_incorrect_background);
            findCorrectOptionView(correctGesture).setBackgroundResource(R.drawable.choice_correct_background);
        }

        listener.onLessonCompleted(isCorrect);
    }

    private View findCorrectOptionView(Gesture correctGesture) {
        for (ImageView iv : optionViews) {
            Gesture gesture = (Gesture) iv.getTag();
            if (gesture.getLetter().equals(correctGesture.getLetter())) {
                return iv;
            }
        }
        return null;
    }
}