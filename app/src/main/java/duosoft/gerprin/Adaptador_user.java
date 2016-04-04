package duosoft.gerprin;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class Adaptador_user extends RecyclerView.Adapter<Adaptador_user.UserViewHolder>{
    List<User> userlista;
    public Adaptador_user(List<User> userlista) {
        this.userlista = userlista;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item,parent,false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        if (userlista.get(position).isHuella()){
            holder.imagen.setImageResource(R.drawable.ic_action_huella_ok);
        }else {
            holder.imagen.setImageResource(R.drawable.ic_action_huella_notok);
        }
        holder.nombre.setText(userlista.get(position).getNombre()+" "+userlista.get(position).getApellido());
        if (userlista.get(position).isDueño()){
            holder.conductor.setText(R.string.Es_dueño);
        }   else {
            holder.conductor.setText(R.string.Es_conductor);
        }

    }


    @Override
    public int getItemCount() {
        return userlista.size();
    }
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, conductor;
        ImageView imagen;

        public UserViewHolder(View itemView) {
            super(itemView);
            nombre = (TextView) itemView.findViewById(R.id.TV_ItemRow_Titulo);
            conductor = (TextView) itemView.findViewById(R.id.TV_ItemRow_Subtitulo);
            imagen = (ImageView) itemView.findViewById(R.id.IV_ItemRow_imagen);

        }
    }
}

