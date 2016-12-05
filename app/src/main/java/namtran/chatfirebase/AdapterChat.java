package namtran.chatfirebase;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterChat extends ArrayAdapter<ChatModel> {

    Context context;
    List<ChatModel> chatModels;
    int layoutID;
    public AdapterChat(Context context, int resource, List<ChatModel> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutID = resource;
        this.chatModels = objects;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutID,null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else
            viewHolder = (ViewHolder) convertView.getTag();
        ChatModel chatModel = chatModels.get(position);
        viewHolder.user.setText(chatModel.getUser() + " : ");
        if (chatModel.getMessage().contains("https://firebasestorage.googleapis.com") ||
                chatModel.getMessage().contains(".jpg") || chatModel.getMessage().contains(".png")){
            viewHolder.message.setVisibility(View.GONE);
            viewHolder.image.setVisibility(View.VISIBLE);
            Picasso.with(context).load(chatModel.getMessage()).placeholder(R.drawable.empty_photo).into(viewHolder.image);
        }else {
            viewHolder.message.setText(chatModel.getMessage());
        }
        return convertView;
    }
    public class ViewHolder{
        public TextView user,message;
        public ImageView image;
        public ViewHolder(View rootView){
            user = (TextView) rootView.findViewById(R.id.chat_firebase_txt_user);
            message = (TextView) rootView.findViewById(R.id.chat_firebase_txt_message_text);
            image = (ImageView) rootView.findViewById(R.id.chat_firebase_txt_message_image);
        }
    }
}