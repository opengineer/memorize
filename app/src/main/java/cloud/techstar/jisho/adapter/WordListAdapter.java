package cloud.techstar.jisho.adapter;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import cloud.techstar.jisho.AppMain;
import cloud.techstar.jisho.R;
import cloud.techstar.jisho.database.WordTable;
import cloud.techstar.jisho.models.Words;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.ViewHolder> {
    private List<Words> words;
    private WordTable wordTable;
    private TextToSpeech kanaSpeech;
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView characterText;
        private TextView meaningText;
        private TextView meaningMnText;
        private ImageButton favButton;
        private ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            wordTable = new WordTable();
            characterText = v.findViewById(R.id.character_text);
            meaningText = v.findViewById(R.id.meaning_text);
            meaningMnText = v.findViewById(R.id.meaning_mn_text);
            favButton = v.findViewById(R.id.fav_button);
        }

        @Override
        public void onClick(View view) {
        }
    }

    public WordListAdapter(Context context, List<Words> words) {
        Context context1 = context;
        this.words = words;
        kanaSpeech = new TextToSpeech(AppMain.getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    kanaSpeech.setLanguage(Locale.JAPAN);
                }
            }
        });
    }

    @Override
    public WordListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.word_recycler_item, parent, false);
        WordListAdapter.ViewHolder vh = new WordListAdapter.ViewHolder(v);


        return vh;
    }

    @Override
    public void onBindViewHolder(final WordListAdapter.ViewHolder holder, final int position) {
        holder.characterText.setText(words.get(position).getCharacter());
        holder.meaningText.setText(words.get(position).getMeaning());
        holder.meaningMnText.setText(words.get(position).getMeaningMon());
        if (words.get(position).getIsFavorite().equals("true")) {
            holder.favButton.setImageResource(R.drawable.ic_favorite_full);
        }

        holder.favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Words word = words.get(position);
                word.setIsFavorite("true");
                wordTable.update(word);
                Snackbar.make(v, "Added to favorite", Snackbar.LENGTH_SHORT)
                        .setAction("Undo", null).show();
                kanaSpeech.speak("はい", TextToSpeech.QUEUE_FLUSH, null);
            }
        });

    }

    @Override
    public int getItemCount() {
        return words.size();
    }
}