package etec.com.tcc.palmslibras.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton; // Importação adicionada
import android.widget.TextView;
import android.widget.LinearLayout;

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
    private List<View> optionViews = new ArrayList<>();
    private boolean isAnswered = false;

    // Mapeia os IDs dos RadioButtons e ImageViews para facilitar o acesso
    private final int[] radioButtonIds = {R.id.rbOptionA, R.id.rbOptionB, R.id.rbOptionC, R.id.rbOptionD};
    private final int[] imageViewIds = {R.id.ivOptionA, R.id.ivOptionB, R.id.ivOptionC, R.id.ivOptionD};


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

        optionViews.add(view.findViewById(R.id.optionA));
        optionViews.add(view.findViewById(R.id.optionB));
        optionViews.add(view.findViewById(R.id.optionC));
        optionViews.add(view.findViewById(R.id.optionD));

        for (int i = 0; i < optionViews.size(); i++) {
            View optionContainer = optionViews.get(i);
            ImageView imageView = optionContainer.findViewById(imageViewIds[i]);

            Gesture gesture = lessonData.getOptions().get(i);

            imageView.setImageResource(gesture.getDrawableId());
            optionContainer.setTag(gesture);

            String contentDesc = getString(R.string.qa_option_image_description, gesture.getLetter());
            optionContainer.setContentDescription(contentDesc);

            optionContainer.setOnClickListener(this);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        if (isAnswered) return;
        isAnswered = true;

        // Encontra o RadioButton dentro do LinearLayout que foi clicado (v) e o marca
        if (v instanceof ViewGroup && ((ViewGroup) v).getChildAt(0) instanceof RadioButton) {
            RadioButton selectedRb = (RadioButton) ((ViewGroup) v).getChildAt(0);
            selectedRb.setChecked(true);
        }

        Gesture selectedGesture = (Gesture) v.getTag();
        Gesture correctGesture = lessonData.getCorrectAnswer();

        boolean isCorrect = selectedGesture.getLetter().equals(correctGesture.getLetter());

        if (isCorrect) {
            v.setBackgroundResource(R.drawable.choice_correct_background);
        } else {
            v.setBackgroundResource(R.drawable.choice_incorrect_background);
            View correctOptionView = findCorrectOptionView(correctGesture);
            if (correctOptionView != null) {
                correctOptionView.setBackgroundResource(R.drawable.choice_correct_background);

                // Marca também o RadioButton da resposta correta
                if (correctOptionView instanceof ViewGroup && ((ViewGroup) correctOptionView).getChildAt(0) instanceof RadioButton) {
                    RadioButton correctRb = (RadioButton) ((ViewGroup) correctOptionView).getChildAt(0);
                    correctRb.setChecked(true);
                }
            }
        }

        listener.onLessonCompleted(isCorrect);
    }

    private View findCorrectOptionView(Gesture correctGesture) {
        for (View optionView : optionViews) {
            Gesture gesture = (Gesture) optionView.getTag();
            if (gesture.getLetter().equals(correctGesture.getLetter())) {
                return optionView;
            }
        }
        return null;
    }
}