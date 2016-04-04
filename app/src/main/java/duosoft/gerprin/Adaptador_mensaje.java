package duosoft.gerprin;


import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class Adaptador_mensaje extends RecyclerView.Adapter<Adaptador_mensaje.MensajeViewHolder> {
    List<Mensaje> mensajelista;
    public Adaptador_mensaje(List<Mensaje> mensajelista) {
        this.mensajelista = mensajelista;
    }

    @Override
    public MensajeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item,parent,false);
        MensajeViewHolder holder = new MensajeViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MensajeViewHolder holder, int position) {

        holder.imagen.setImageResource(R.drawable.ic_action_mensajeb);
        holder.titulo.setTextSize(15);
        holder.titulo.setText(mensajelista.get(position).getTitulo());
        if (mensajelista.get(position).isLeido()){
            holder.leido.setText(Html.fromHtml("<b>Leido</b>"));
        }   else {
            holder.leido.setText(Html.fromHtml("<b>No leido</b>"));
        }

    }


    @Override
    public int getItemCount() {
        return mensajelista.size();
    }
    public static class MensajeViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, leido;
        LinearLayout layout;
        ImageView imagen;

        public MensajeViewHolder(View itemView) {
            super(itemView);
            titulo = (TextView) itemView.findViewById(R.id.TV_ItemRow_Titulo);
            leido = (TextView) itemView.findViewById(R.id.TV_ItemRow_Subtitulo);
            layout = (LinearLayout) itemView.findViewById(R.id.layout);
            imagen = (ImageView) itemView.findViewById(R.id.IV_ItemRow_imagen);

        }
    }
}
