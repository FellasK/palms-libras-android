package etec.com.tcc.palmslibras.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import etec.com.tcc.palmslibras.R;
import etec.com.tcc.palmslibras.models.MemoryCard;

public class MemoryCardAdapter extends RecyclerView.Adapter<MemoryCardAdapter.CardViewHolder> {

    public interface OnCardClickListener {
        void onCardClick(int position);
    }

    private final List<MemoryCard> cardList;
    private final OnCardClickListener listener;
    private final Context context;
    private int selectedPosition = RecyclerView.NO_POSITION;


    public MemoryCardAdapter(Context context, List<MemoryCard> cardList, OnCardClickListener listener) {
        this.context = context;
        this.cardList = cardList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memory_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        MemoryCard card = cardList.get(position);
        holder.bind(card, position);
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardFront, cardBack;
        ImageView ivMemoryImage;
        TextView tvMemoryText;

        CardViewHolder(@NonNull View itemView) {
            super(itemView);
            // Assumindo que os IDs no seu item_memory_card.xml são estes
            cardFront = itemView.findViewById(R.id.card_front);
            cardBack = itemView.findViewById(R.id.card_back);
            ivMemoryImage = itemView.findViewById(R.id.ivMemoryImage);
            tvMemoryText = itemView.findViewById(R.id.tvMemoryText);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onCardClick(position);
                }
            });
        }

        void bind(MemoryCard card, int position) {
            // 1. Configura o conteúdo da frente da carta (imagem ou texto)
            if (card.isImage()) {
                ivMemoryImage.setVisibility(View.VISIBLE);
                tvMemoryText.setVisibility(View.GONE);
                ivMemoryImage.setImageResource(card.getGesture().getDrawableId());
            } else {
                tvMemoryText.setVisibility(View.VISIBLE);
                ivMemoryImage.setVisibility(View.GONE);
                tvMemoryText.setText(card.getGesture().getLetter());
            }

            // 2. Define a cor de fundo para a parte de trás da carta
            cardBack.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_back_color));
            // Opcional: Para remover a borda padrão do MaterialCardView se houver
            cardBack.setStrokeWidth(0);

            // 3. Define a cor de fundo para a parte da frente da carta com base no estado
            if (card.isMatched()) {
                cardFront.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_matched_color));
            } else if (position == selectedPosition) {
                cardFront.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_selected_color));
            } else {
                cardFront.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_front_default_color));
            }
            // Opcional: Para remover a borda padrão do MaterialCardView se houver
            cardFront.setStrokeWidth(0);

            // 4. Define a visibilidade inicial (sem animação, isso é controlado pelo Fragment)
            cardFront.setVisibility(card.isFlipped() ? View.VISIBLE : View.INVISIBLE);
            cardBack.setVisibility(card.isFlipped() ? View.INVISIBLE : View.VISIBLE);
        }
    }
}