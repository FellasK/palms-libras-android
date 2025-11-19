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

public class ConnectGameFragment extends Fragment implements ConnectCardAdapter.OnCardClickListener {

    private OnLessonCompleteListener listener;
    private Exercices lessonData; // Mude o tipo da variável de Lesson para Exercices
    private RecyclerView connectGrid;
    private ConnectCardAdapter adapter;
    private TextView tvPairsCounter;

    private List<MemoryCard> cards = new ArrayList<>();
    private Integer firstSelectedIndex = null;
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

        connectGrid = view.findViewById(R.id.connectGrid);
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

        // Limpa a lista de cards antes de adicionar novos
        cards.clear();

        // ⭐ CORREÇÃO 2: Iterar sobre a lista correta, que é 'getOptions()'
        List<Gesture> opts = lessonData.getOptions();
        for (int i = 0; i < opts.size(); i++) {
            Gesture g = opts.get(i);
            MemoryCard imgCard = new MemoryCard(g, true);  // Card com a imagem
            cards.add(imgCard);
            cards.add(new MemoryCard(g, false)); // Card com o texto
        }
        Collections.shuffle(cards);

        // Guarda o número total de pares
        totalPairs = lessonData.getOptions().size();
        // Atribui variantes garantindo diversidade visual
        java.util.List<Integer> variants = etec.com.tcc.palmslibras.utils.SkinToneManager.assignVariantsEnsuringDiversity(totalPairs);
        int assigned = 0;
        for (int i = 0; i < cards.size(); i += 2) { // cada par: imagem + texto
            MemoryCard img = cards.get(i); // imagem
            if (img.isImage()) {
                int v = (assigned < variants.size()) ? variants.get(assigned) : etec.com.tcc.palmslibras.utils.SkinToneManager.pickVariant();
                img.setVariant(v);
                assigned++;
            }
        }

        // Configura o adapter e o RecyclerView
        adapter = new ConnectCardAdapter(getContext(), cards, this);
        connectGrid.setLayoutManager(new GridLayoutManager(getContext(), 4)); // 4 colunas
        connectGrid.setAdapter(adapter); // Esta linha faz os cards aparecerem

        updatePairsCounter();
    }

    private void updatePairsCounter() {
        // Atualiza o texto do contador de pares
        tvPairsCounter.setText(getString(R.string.memory_pairs_found_template, matchedPairs, totalPairs));
    }

    @Override
    public void onCardClick(int position) {
        if (cards.get(position).isMatched()) {
            return; // Impede o clique em cards já combinados
        }

        if (firstSelectedIndex == null) {
            // Primeiro card selecionado
            firstSelectedIndex = position;
            adapter.setSelectedPosition(position);
            adapter.notifyItemChanged(position);
        } else {
            // Segundo card selecionado
            if (firstSelectedIndex == position) {
                // Clicou no mesmo card novamente, desmarca ele
                adapter.setSelectedPosition(RecyclerView.NO_POSITION);
                adapter.notifyItemChanged(firstSelectedIndex);
                firstSelectedIndex = null;
                return;
            }
            checkForMatch(firstSelectedIndex, position);
        }
    }

    private void checkForMatch(int firstPos, int secondPos) {
        MemoryCard firstCard = cards.get(firstPos);
        MemoryCard secondCard = cards.get(secondPos);

        if (firstCard.getGesture() == secondCard.getGesture() &&
                firstCard.isImage() != secondCard.isImage()) {
            // Par correto (Match)
            firstCard.setMatched(true);
            secondCard.setMatched(true);
            matchedPairs++;
            updatePairsCounter();

            // Notifica as mudanças para atualizar a cor
            adapter.notifyItemChanged(firstPos);
            adapter.notifyItemChanged(secondPos);

            // Verifica se o jogo terminou
            if (matchedPairs == totalPairs) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (listener != null) {
                        listener.onLessonCompleted(true);
                    }
                }, 800);
            }

            // Limpa a seleção para a próxima jogada
            firstSelectedIndex = null;
            adapter.setSelectedPosition(RecyclerView.NO_POSITION);

        } else {
            // Par incorreto (Não deu Match)
            int tempFirstPos = firstSelectedIndex;
            adapter.setSelectedPosition(RecyclerView.NO_POSITION); // Remove o estado de seleção visualmente

            // Notifica a segunda carta para que ela seja redesenhada sem o destaque de seleção
            adapter.notifyItemChanged(secondPos);

            // Adiciona um pequeno atraso para o jogador ver o erro
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                adapter.notifyItemChanged(tempFirstPos);
            }, 500);

            // Limpa a seleção para a próxima jogada
            firstSelectedIndex = null;
        }
    }
}
