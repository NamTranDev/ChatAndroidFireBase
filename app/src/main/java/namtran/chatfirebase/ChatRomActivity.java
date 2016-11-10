package namtran.chatfirebase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatRomActivity extends AppCompatActivity{

    private TextView mTxtConversation;
    private EditText mEdtMessage;
    private Button mBtnSend;

    private String mUserName,mRoomName;
    private DatabaseReference root;
    private String temp_key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mTxtConversation = (TextView) findViewById(R.id.chat_firebase_txt_message);
        mEdtMessage = (EditText) findViewById(R.id.chat_firebase_edt_message);
        mBtnSend = (Button) findViewById(R.id.chat_firebase_btn_sent);

        mUserName = getIntent().getExtras().getString("user_name").toString();
        mRoomName = getIntent().getExtras().getString("room_name").toString();

        setTitle(" Room : " + mRoomName);

        root = FirebaseDatabase.getInstance().getReference().child(mRoomName);

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> map = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(map);

                DatabaseReference message_root = root.child(temp_key);

                Map<String,Object> map2 = new HashMap<String, Object>();
                map2.put("user_name",mUserName);
                map2.put("message",mEdtMessage.getText().toString());

                message_root.updateChildren(map2);
                mEdtMessage.setText("");
            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                appendChatConversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                appendChatConversation(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String mNameUser, mMessage;
    private void appendChatConversation(DataSnapshot dataSnapshot) {

        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()){
            mMessage = ((DataSnapshot)i.next()).getValue().toString();
            mNameUser = ((DataSnapshot)i.next()).getValue().toString();

            mTxtConversation.append(mNameUser + " : " + mMessage + "\n");
        }
    }
}
