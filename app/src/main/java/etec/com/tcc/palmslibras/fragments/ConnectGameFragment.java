package etec.com.tcc.palmslibras.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import etec.com.tcc.palmslibras.R;
import etec.com.tcc.palmslibras.adapters.ConnectCardAdapter;
import etec.com.tcc.palmslibras.models.Gesture;
import etec.com.tcc.palmslibras.models.Exercices; // Mude de Lesson para Exercices
import etec.com.tcc.palmslibras.models.MemoryCard;
import etec.com.tcc.palmslibras.utils.OnLessonCompleteListener;

public class ConnectGameFragment extends Fragment {

    private OnLessonCompleteListener listener;
    private Exercices lessonData; // Mude o tipo da variável de Lesson para Exercices
    private RecyclerView leftColumn;
    private RecyclerView rightColumn;
    private ConnectCardAdapter leftAdapter;
    private ConnectCardAdapter rightAdapter;
    private TextView tvPairsCounter;

    private List<MemoryCard> leftCards = new ArrayList<>();
    private List<MemoryCard> rightCards = new ArrayList<>();
    private Integer selectedLeftIndex = null;
    private Integer selectedRightIndex = null;
    private int matchedPairs = 0;
    private int totalPairs = 0;

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
        View view = inflater.inflate(R.layout.fragment_connect_game, container, false);

        if (getArguments() != null) {
            // Faça o cast para Exercices
            lessonData = (Exercices) getArguments().getSerializable("lesson_data");
        }

        leftColumn = view.findViewById(R.id.leftColumn);
        rightColumn = view.findViewById(R.id.rightColumn);
        tvPairsCounter = view.findViewById(R.id.tvPairsCounter);

        setupConnectGame();
        return view;
    }

    // O resto do arquivo permanece como está, pois a lógica interna já utiliza
    // lessonData.getOptions(), que agora funcionará corretamente.

    // ... (nenhuma outra mudança é necessária no resto do arquivo)
    private void setupConnectGame() {
        // ⭐ CORREÇÃO 1: Usar getOptions() em vez de getMemoryPairs()
        if (lessonData == null || lessonData.getOptions() == null || lessonData.getOptions().isEmpty()) {
            // Se não houver dados, não continue.
            return;
        }

        // Prepara listas de cartas por coluna

        // ⭐ CORREÇÃO 2: Iterar sobre a lista correta, que é 'getOptions()'
        List<Gesture> opts = lessonData.getOptions();
        List<MemoryCard> textCards = new ArrayList<>();
        List<MemoryCard> imageCards = new ArrayList<>();
        for (int i = 0; i < opts.size(); i++) {
            Gesture g = opts.get(i);
            imageCards.add(new MemoryCard(g, true));
            textCards.add(new MemoryCard(g, false));
        }
        Collections.shuffle(textCards);
        Collections.shuffle(imageCards);

        // Guarda o número total de pares
        totalPairs = lessonData.getOptions().size();
        // Atribui variantes garantindo diversidade visual
        java.util.List<Integer> variants = etec.com.tcc.palmslibras.utils.SkinToneManager.assignVariantsLimitTwoPerTone(totalPairs);
        int assigned = 0;
        for (int i = 0; i < imageCards.size(); i++) {
            MemoryCard c = imageCards.get(i);
            int v = (assigned < variants.size()) ? variants.get(assigned) : etec.com.tcc.palmslibras.utils.SkinToneManager.pickVariant();
            c.setVariant(v);
            assigned++;
        }

        // Configura o adapter e o RecyclerView
        leftCards = textCards;
        rightCards = imageCards;
        leftAdapter = new ConnectCardAdapter(getContext(), leftCards, position -> onLeftClick(position));
        rightAdapter = new ConnectCardAdapter(getContext(), rightCards, position -> onRightClick(position));
        leftColumn.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
        rightColumn.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
        leftColumn.setAdapter(leftAdapter);
        rightColumn.setAdapter(rightAdapter);

        updatePairsCounter();
    }

    private void updatePairsCounter() {
        // Atualiza o texto do contador de pares
        tvPairsCounter.setText(getString(R.string.memory_pairs_found_template, matchedPairs, totalPairs));
    }

    private void onLeftClick(int position) {
        MemoryCard card = leftCards.get(position);
        if (card.isMatched()) return;
        selectedLeftIndex = position;
        leftAdapter.setSelectedPosition(position);
        leftAdapter.notifyItemChanged(position);
        if (selectedRightIndex != null) {
            checkForMatch(selectedLeftIndex, selectedRightIndex);
        }
    }

    private void onRightClick(int position) {
        MemoryCard card = rightCards.get(position);
        if (card.isMatched()) return;
        selectedRightIndex = position;
        rightAdapter.setSelectedPosition(position);
        rightAdapter.notifyItemChanged(position);
        if (selectedLeftIndex != null) {
            checkForMatch(selectedLeftIndex, selectedRightIndex);
        }
    }

    private void checkForMatch(int leftPos, int rightPos) {
        MemoryCard firstCard = leftCards.get(leftPos);
        MemoryCard secondCard = rightCards.get(rightPos);

        if (firstCard.getGesture() == secondCard.getGesture() &&
                firstCard.isImage() != secondCard.isImage()) {
            // Par correto (Match)
            firstCard.setMatched(true);
            secondCard.setMatched(true);
            matchedPairs++;
            updatePairsCounter();

            leftAdapter.notifyItemChanged(leftPos);
            rightAdapter.notifyItemChanged(rightPos);

            // Verifica se o jogo terminou
            if (matchedPairs == totalPairs) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (listener != null) {
                        listener.onLessonCompleted(true);
                    }
                }, 800);
            }

            // Limpa a seleção para a próxima jogada
            selectedLeftIndex = null;
            selectedRightIndex = null;
            leftAdapter.setSelectedPosition(RecyclerView.NO_POSITION);
            rightAdapter.setSelectedPosition(RecyclerView.NO_POSITION);

        } else {
            // Par incorreto (Não deu Match)
            firstCard.setError(true);
            secondCard.setError(true);
            leftAdapter.notifyItemChanged(leftPos);
            rightAdapter.notifyItemChanged(rightPos);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                firstCard.setError(false);
                secondCard.setError(false);
                leftAdapter.notifyItemChanged(leftPos);
                rightAdapter.notifyItemChanged(rightPos);
                leftAdapter.setSelectedPosition(RecyclerView.NO_POSITION);
                rightAdapter.setSelectedPosition(RecyclerView.NO_POSITION);
                selectedLeftIndex = null;
                selectedRightIndex = null;
            }, 600);
        }
    }
}
