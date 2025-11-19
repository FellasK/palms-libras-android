package etec.com.tcc.palmslibras.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import etec.com.tcc.palmslibras.R;
import etec.com.tcc.palmslibras.models.MemoryCard;

public class ConnectCardAdapter extends RecyclerView.Adapter<ConnectCardAdapter.ConnectViewHolder> {

    private final Context context;
    private final List<MemoryCard> cards;
    private final OnCardClickListener listener;

    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnCardClickListener {
        void onCardClick(int position);
    }

    public ConnectCardAdapter(Context context, List<MemoryCard> cards, OnCardClickListener listener) {
        this.context = context;
        this.cards = cards;
        this.listener = listener;
    }
//
    @NonNull
    @Override
    public ConnectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Você precisará criar este layout. Ele contém um CardView com uma ImageView e um TextView.
        View view = LayoutInflater.from(context).inflate(R.layout.item_connect_card, parent, false);
        return new ConnectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConnectViewHolder holder, int position) {
        MemoryCard card = cards.get(position);
        holder.bind(card, position);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }

    class ConnectViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivCardImage;
        TextView tvCardText;

        public ConnectViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.connectCardView);
            ivCardImage = itemView.findViewById(R.id.ivConnectCardImage);
            tvCardText = itemView.findViewById(R.id.tvConnectCardText);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onCardClick(getAdapterPosition());
                }
            });
        }

        // ...
        void bind(MemoryCard card, int position) {
            // Mostra imagem ou texto baseado no tipo de carta
            if (card.isImage()) {
                ivCardImage.setVisibility(View.VISIBLE);
                tvCardText.setVisibility(View.GONE);
                int resId;
                if (card.getVariant() > 0) {
                    resId = etec.com.tcc.palmslibras.utils.SkinToneManager.getVariantResId(context, card.getGesture(), card.getVariant());
                } else {
                    resId = card.getGesture().getDrawableId();
                }
                ivCardImage.setImageResource(resId);
            } else {
                ivCardImage.setVisibility(View.GONE);
                tvCardText.setVisibility(View.VISIBLE);
                tvCardText.setText(card.getGesture().getLetter());
            }


            // Lógica visual para os estados da carta
            if (card.isMatched()) {
                // Estado: Combinado
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.connect_card_matched_bg)); // Cor verde claro
                cardView.setAlpha(0.7f); // Deixa um pouco transparente
            } else if (position == selectedPosition) {
                // Estado: Selecionado
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.connect_card_selected_bg)); // Cor azul claro
                cardView.setAlpha(1.0f);
            } else {
                // Estado: Padrão
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.connect_card_default_bg)); // Cor padrão (branco ou cinza)
                cardView.setAlpha(1.0f);
            }
        }
    }
}
