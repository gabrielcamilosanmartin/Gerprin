package duosoft.gerprin;


import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class Adaptador_ruta extends RecyclerView.Adapter<Adaptador_ruta.RutaViewHolder> {
    List<Ruta> rutalista;

    public Adaptador_ruta(List<Ruta> rutalista) {
        this.rutalista = rutalista;
    }

    @Override
    public RutaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item,parent,false);
        RutaViewHolder holder = new RutaViewHolder(v);
        return holder;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RutaViewHolder holder, int position) {
        holder.imagen.setImageResource(R.drawable.ic_action_rutas);
        holder.fecha.setTextSize(15);
        holder.fecha.setText(rutalista.get(position).getStart_time());
        holder.nombre.setText(Html.fromHtml("Ruta realizada por <b>" + rutalista.get(position).getNombre_user() + "</b>"));

    }


    @Override
    public int getItemCount() {

        return rutalista.size();
    }

    public static class RutaViewHolder extends RecyclerView.ViewHolder
    {
        TextView fecha, nombre;
        ImageView imagen;

        public RutaViewHolder(View itemView) {
            super(itemView);
            fecha = (TextView) itemView.findViewById(R.id.TV_ItemRow_Titulo) ;
            nombre = (TextView) itemView.findViewById(R.id.TV_ItemRow_Subtitulo) ;
            imagen = (ImageView) itemView.findViewById(R.id.IV_ItemRow_imagen);

        }
    }
}
