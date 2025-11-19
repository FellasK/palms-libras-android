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
import android.widget.TextView; // Importação adicionada

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
import etec.com.tcc.palmslibras.models.Exercices;
import etec.com.tcc.palmslibras.models.MemoryCard;
import etec.com.tcc.palmslibras.utils.OnLessonCompleteListener;

public class MemoryFragment extends Fragment implements MemoryCardAdapter.OnCardClickListener {

    private OnLessonCompleteListener listener;
    private Exercices exercices;
    private RecyclerView memoryGrid;
    private MemoryCardAdapter adapter;
    private TextView tvPairsCounter; // View para o contador

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
            exercices = (Exercices) getArguments().getSerializable("lesson_data");
        }

        // Inicializa as views do novo layout
        memoryGrid = view.findViewById(R.id.memoryGrid);
        tvPairsCounter = view.findViewById(R.id.tvPairsCounter);

        setupMemoryGame();
        return view;
    }

    private void setupMemoryGame() {
        java.util.List<Integer> variants = etec.com.tcc.palmslibras.utils.SkinToneManager.assignVariantsLimitTwoPerTone(exercices.getMemoryPairs().size());
        int idx = 0;
        for(Gesture g : exercices.getMemoryPairs()){
            MemoryCard img = new MemoryCard(g, true);
            int v = (idx < variants.size()) ? variants.get(idx) : etec.com.tcc.palmslibras.utils.SkinToneManager.pickVariant();
            img.setVariant(v);
            cards.add(img);
            cards.add(new MemoryCard(g, false));
            idx++;
        }
        Collections.shuffle(cards);

        adapter = new MemoryCardAdapter(getContext(), cards, this);
        memoryGrid.setLayoutManager(new GridLayoutManager(getContext(), calculateSpanCount()));
        memoryGrid.setAdapter(adapter);
        memoryGrid.setHasFixedSize(true);

        // Inicia o contador
        updatePairsCounter();

        memoryGrid.post(this::startWithPreview);
    }

    // Novo método para atualizar o texto do contador
    private void updatePairsCounter() {
        int totalPairs = exercices.getMemoryPairs().size();
        tvPairsCounter.setText(getString(R.string.memory_pairs_found_template, matchedPairs, totalPairs));
    }

    private int calculateSpanCount() {
        Context ctx = getContext();
        if (ctx == null) return 4;
        float density = ctx.getResources().getDisplayMetrics().density;
        int widthPx = ctx.getResources().getDisplayMetrics().widthPixels;
        float widthDp = widthPx / density;
        if (widthDp >= 600) return 6;
        if (widthDp >= 400) return 4;
        return 3;
    }

    private void startWithPreview() {
        isChecking = true;
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            for (int i = 0; i < cards.size(); i++) {
                if (!cards.get(i).isMatched()) {
                    if (!cards.get(i).isFlipped()) {
                        flipCard(i);
                    }
                }
            }
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                for (int i = 0; i < cards.size(); i++) {
                    if (!cards.get(i).isMatched()) {
                        if (cards.get(i).isFlipped()) {
                            flipCard(i);
                        }
                    }
                }
                isChecking = false;
            }, 3000);
        }, 1000);
    }

    @Override
    public void onCardClick(int position) {
        if (isChecking || cards.get(position).isMatched()) {
            return;
        }

        if (firstSelectedIndex != null && position == firstSelectedIndex) {
            adapter.setSelectedPosition(RecyclerView.NO_POSITION);
            flipCard(position);
            firstSelectedIndex = null;
            return;
        }

        if (cards.get(position).isFlipped()) {
            return;
        }

        flipCard(position);

        if (firstSelectedIndex == null) {
            firstSelectedIndex = position;
            adapter.setSelectedPosition(position);
            adapter.notifyItemChanged(position);
        } else {
            isChecking = true;
            adapter.setSelectedPosition(RecyclerView.NO_POSITION);
            adapter.notifyItemChanged(firstSelectedIndex);
            // Não precisa notificar o segundo item, pois a checagem já vai acontecer

            checkForMatch(firstSelectedIndex, position);
            firstSelectedIndex = null;
        }
    }

    private void flipCard(int position) {
        // A lógica de flipCard permanece a mesma
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
            updatePairsCounter(); // Atualiza o contador

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                adapter.notifyItemChanged(firstPos);
                adapter.notifyItemChanged(secondPos);
                isChecking = false;
                if (matchedPairs == exercices.getMemoryPairs().size()) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> listener.onLessonCompleted(true), 800);
                }
            }, 300);
        } else {
            firstCard.setError(true);
            secondCard.setError(true);
            adapter.notifyItemChanged(firstPos);
            adapter.notifyItemChanged(secondPos);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                flipCard(firstPos);
                flipCard(secondPos);
                firstCard.setError(false);
                secondCard.setError(false);
                adapter.notifyItemChanged(firstPos);
                adapter.notifyItemChanged(secondPos);
                isChecking = false;
            }, 1000);
        }
    }
}