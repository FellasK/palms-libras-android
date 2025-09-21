package etec.com.tcc.palmslibras.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;
import etec.com.tcc.palmslibras.R;
import etec.com.tcc.palmslibras.models.MemoryCard;

public class MemoryCardAdapter extends RecyclerView.Adapter<MemoryCardAdapter.CardViewHolder> {

    public interface OnCardClickListener {
        void onCardClick(int position);
    }

    private List<MemoryCard> cardList;
    private OnCardClickListener listener;
    private Context context;
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
        LinearLayout cardFrontContent;
        ImageView ivMemoryImage;
        TextView tvMemoryText;

        CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardFront = itemView.findViewById(R.id.card_front);
            cardBack = itemView.findViewById(R.id.card_back);
            cardFrontContent = itemView.findViewById(R.id.card_front_content);
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
            // Configura o conteúdo da frente da carta
            if (card.isImage()) {
                ivMemoryImage.setVisibility(View.VISIBLE);
                tvMemoryText.setVisibility(View.GONE);
                ivMemoryImage.setImageResource(card.getGesture().getDrawableId());
            } else {
                tvMemoryText.setVisibility(View.VISIBLE);
                ivMemoryImage.setVisibility(View.GONE);
                tvMemoryText.setText(card.getGesture().getLetter());
            }

            // Define visibilidade inicial (sem animação)
            cardFront.setVisibility(card.isFlipped() ? View.VISIBLE : View.INVISIBLE);
            cardBack.setVisibility(card.isFlipped() ? View.INVISIBLE : View.VISIBLE);

            // Ajusta a aparência com base no estado
            if (card.isMatched()) {
                cardFront.setStrokeColor(context.getColor(R.color.memory_card_matched_stroke));
                cardFrontContent.setBackgroundColor(context.getColor(R.color.memory_card_matched_tint));
            } else if (position == selectedPosition) {
                // Estado Selecionado
                cardFront.setStrokeColor(context.getColor(R.color.memory_card_selected_stroke));
                cardFrontContent.setBackgroundColor(context.getColor(R.color.white));
            } else {
                // Estado Padrão
                cardFront.setStrokeColor(context.getColor(R.color.memory_card_default_stroke));
                cardFrontContent.setBackgroundColor(context.getColor(R.color.white));
            }
        }
    }
}