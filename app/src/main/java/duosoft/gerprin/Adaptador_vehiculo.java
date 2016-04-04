package duosoft.gerprin;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class Adaptador_vehiculo extends RecyclerView.Adapter<Adaptador_vehiculo.VehiculoViewHolder> {
    List<Vehiculo> vehiculolista;

    public Adaptador_vehiculo(List<Vehiculo> vehiculolista) {
        this.vehiculolista = vehiculolista;
    }

    @Override
    public VehiculoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item,parent,false);
        return new VehiculoViewHolder(v);
    }


    @Override
    public void onBindViewHolder(VehiculoViewHolder holder, int position) {
        holder.imagen.setImageResource(R.drawable.ic_action_vehiculo);
        holder.patente.setText(vehiculolista.get(position).getPatente());
        if (vehiculolista.get(position).isEnrrolador()){
            holder.dueño.setText(" ");
        }else {
            if (vehiculolista.get(position).isDueño()){
                holder.dueño.setText(R.string.Eres_dueño);
            }else{
                holder.dueño.setText(R.string.Eres_conductor);
            }
        }


    }


    @Override
    public int getItemCount() {

        return vehiculolista.size();
    }

    public static class VehiculoViewHolder extends RecyclerView.ViewHolder
    {
        TextView patente, dueño;
        ImageView imagen;

        public VehiculoViewHolder(View itemView) {
           super(itemView);
            patente = (TextView) itemView.findViewById(R.id.TV_ItemRow_Titulo) ;
            dueño = (TextView) itemView.findViewById(R.id.TV_ItemRow_Subtitulo) ;
            imagen = (ImageView) itemView.findViewById(R.id.IV_ItemRow_imagen);

        }
    }
}
