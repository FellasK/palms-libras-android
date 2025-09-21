package etec.com.tcc.palmslibras.fragments;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import etec.com.tcc.palmslibras.R;
import etec.com.tcc.palmslibras.adapters.MemoryCardAdapter;
import etec.com.tcc.palmslibras.models.Gesture;
import etec.com.tcc.palmslibras.models.Lesson;
import etec.com.tcc.palmslibras.models.MemoryCard;
import etec.com.tcc.palmslibras.utils.OnLessonCompleteListener;

public class MemoryFragment extends Fragment implements MemoryCardAdapter.OnCardClickListener {

    private OnLessonCompleteListener listener;
    private Lesson lessonData;
    private RecyclerView memoryGrid;
    private MemoryCardAdapter adapter;

    private List<MemoryCard> cards = new ArrayList<>();
    private Integer firstSelectedIndex = null;
    private boolean isChecking = false;
    private int matchedPairs = 0;

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
        View view = inflater.inflate(R.layout.fragment_memory, container, false);

        if (getArguments() != null) {
            lessonData = (Lesson) getArguments().getSerializable("lesson_data");
        }

        memoryGrid = view.findViewById(R.id.memoryGrid);
        setupMemoryGame();
        return view;
    }

    private void setupMemoryGame() {
        for(Gesture g : lessonData.getMemoryPairs()){
            cards.add(new MemoryCard(g, true));
            cards.add(new MemoryCard(g, false));
        }
        Collections.shuffle(cards);

        adapter = new MemoryCardAdapter(getContext(), cards, this);
        memoryGrid.setLayoutManager(new GridLayoutManager(getContext(), 2));
        memoryGrid.setAdapter(adapter);
    }

    @Override
    public void onCardClick(int position) {
        if (isChecking || cards.get(position).isFlipped() || cards.get(position).isMatched()) {
            return;
        }

        flipCard(position);

        if (firstSelectedIndex == null) {
            firstSelectedIndex = position;
            adapter.setSelectedPosition(position);
            adapter.notifyItemChanged(position); // Atualiza para mostrar borda azul
        } else {
            isChecking = true;
            adapter.setSelectedPosition(RecyclerView.NO_POSITION); // Limpa seleção
            adapter.notifyItemChanged(firstSelectedIndex); // Remove borda azul da anterior
            adapter.notifyItemChanged(position); // Apenas vira, sem borda azul

            checkForMatch(firstSelectedIndex, position);
            firstSelectedIndex = null;
        }
    }

    private void flipCard(int position) {
        MemoryCard card = cards.get(position);
        card.setFlipped(!card.isFlipped());

        View view = memoryGrid.getLayoutManager().findViewByPosition(position);
        if (view == null) return;

        AnimatorSet outAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.card_flip_right_out);
        AnimatorSet inAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.card_flip_left_in);

        View cardFront = view.findViewById(R.id.card_front);
        View cardBack = view.findViewById(R.id.card_back);

        if (card.isFlipped()) {
            outAnimator.setTarget(cardBack);
            inAnimator.setTarget(cardFront);
            outAnimator.start();
            inAnimator.start();
            cardFront.setVisibility(View.VISIBLE);
            cardBack.setVisibility(View.INVISIBLE);
        } else {
            outAnimator.setTarget(cardFront);
            inAnimator.setTarget(cardBack);
            outAnimator.start();
            inAnimator.start();
            cardFront.setVisibility(View.INVISIBLE);
            cardBack.setVisibility(View.VISIBLE);
        }
    }

    private void checkForMatch(int firstPos, int secondPos) {
        MemoryCard firstCard = cards.get(firstPos);
        MemoryCard secondCard = cards.get(secondPos);

        if (firstCard.getGesture().getLetter().equals(secondCard.getGesture().getLetter()) &&
                firstCard.isImage() != secondCard.isImage()) {
            // Deu Match
            firstCard.setMatched(true);
            secondCard.setMatched(true);
            matchedPairs++;

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                adapter.notifyItemChanged(firstPos);
                adapter.notifyItemChanged(secondPos);
                isChecking = false;
                if (matchedPairs == lessonData.getMemoryPairs().size()) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> listener.onLessonCompleted(true), 800);
                }
            }, 300); // Pequeno delay para a animação terminar antes de mudar a cor
        } else {
            // Não deu Match
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                flipCard(firstPos);
                flipCard(secondPos);
                isChecking = false;
            }, 1000);
        }
    }
}