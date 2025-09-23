package etec.com.tcc.palmslibras.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private List<View> optionViews = new ArrayList<>();
    private Button btnVerify;
    private View selectedOption = null;
    private boolean isAnswered = false;

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

        initializeViews(view);
        setupQuestion(view);
        setupOptions();

        return view;
    }

    private void initializeViews(View view) {
        btnVerify = view.findViewById(R.id.btnVerify);
        btnVerify.setOnClickListener(v -> verifyAnswer());

        optionViews.add(view.findViewById(R.id.optionA));
        optionViews.add(view.findViewById(R.id.optionB));
        optionViews.add(view.findViewById(R.id.optionC));
        optionViews.add(view.findViewById(R.id.optionD));
    }

    private void setupQuestion(View view) {
        TextView tvQuestion = view.findViewById(R.id.tvQuestion);
        String questionText = getString(R.string.qa_question_template, lessonData.getCorrectAnswer().getLetter());
        tvQuestion.setText(questionText);
    }

    private void setupOptions() {
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
    }

    @Override
    public void onClick(View v) {
        if (isAnswered) return;

        // Limpa a seleção anterior
        for (View optionView : optionViews) {
            optionView.setBackgroundResource(R.drawable.choice_default_background);
        }

        // Define a nova seleção
        selectedOption = v;
        selectedOption.setBackgroundResource(R.drawable.choice_selected_background);

        // Habilita o botão de verificar
        btnVerify.setEnabled(true);
    }

    private void verifyAnswer() {
        if (isAnswered || selectedOption == null) return;
        isAnswered = true;

        // Desabilita as opções para evitar novos cliques
        for (View optionView : optionViews) {
            optionView.setClickable(false);
        }

        Gesture selectedGesture = (Gesture) selectedOption.getTag();
        Gesture correctGesture = lessonData.getCorrectAnswer();
        boolean isCorrect = selectedGesture.getLetter().equals(correctGesture.getLetter());

        // ** LÓGICA DE FEEDBACK VISUAL APENAS NAS ALTERNATIVAS **
        if (isCorrect) {
            selectedOption.setBackgroundResource(R.drawable.choice_correct_background);
        } else {
            selectedOption.setBackgroundResource(R.drawable.choice_incorrect_background);
            View correctOptionView = findCorrectOptionView(correctGesture);
            if (correctOptionView != null) {
                correctOptionView.setBackgroundResource(R.drawable.choice_correct_background);
            }
        }

        // Esconde o botão e notifica a activity
        btnVerify.setVisibility(View.GONE);
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