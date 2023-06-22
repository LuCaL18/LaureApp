package it.uniba.dib.sms222327.laureapp.ui.bookings.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import it.uniba.dib.sms222327.laureapp.R;
import it.uniba.dib.sms222327.laureapp.data.model.Booking;
import it.uniba.dib.sms222327.laureapp.ui.bookings.interfaces.BookingItemClickCallback;
import it.uniba.dib.sms222327.laureapp.ui.bookings.viewHolder.BookingViewHolder;

/**
 *  La classe BookingAdapter estende FirestoreRecyclerAdapter,
 *  che è una classe di supporto fornita da FirestoreUI per la gestione di un elenco di dati Firestore in un RecyclerView.
 */
public class BookingAdapter extends FirestoreRecyclerAdapter<Booking, BookingViewHolder> {

    private final BookingItemClickCallback callback;

    public BookingAdapter(@NonNull FirestoreRecyclerOptions<Booking> options, BookingItemClickCallback callback) {
        super(options);
        this.callback = callback;
    }

    /**
     * Il metodo onCreateViewHolder viene chiamato quando viene creato un nuovo ViewHolder per un elemento della lista.
     * Viene inizializzata una nuova view utilizzando il layout
     * booking_list_item e viene creato un nuovo BookingViewHolder con la vista appena creata e il callback passato al costruttore
     * per gestire i clic sull'elemento.
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public BookingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_list_item, parent, false);
        return new BookingViewHolder(view, callback);
    }

    /**
     * Il metodo onBindViewHolder viene chiamato per associare i dati Booking al ViewHolder.
     * Viene chiamato il metodo bind del ViewHolder,
     * che si occupa di impostare i dati sulla view corrispondente all'interno del ViewHolder.
     * @param holder
     * @param position
     * @param model
     */
    @Override
    protected void onBindViewHolder(BookingViewHolder holder, int position, @NonNull Booking model) {
        holder.bind(model);
    }
}
