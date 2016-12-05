package namtran.chatfirebase;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
public class MainActivity extends AppCompatActivity {
    private EditText mEdtRoom;
    private Button mBtnAdd;
    private ListView mLvRoom;
    private TextView mTxtUser;
    private ArrayAdapter<String> mAdapterRoom;
    private List<String> mListRoom = new ArrayList<>();
    private String mUserName;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEdtRoom = (EditText) findViewById(R.id.chat_firebase_edt_room);
        mBtnAdd = (Button) findViewById(R.id.chat_firebase_btn_addroom);
        mLvRoom = (ListView) findViewById(R.id.chat_firebase_lv_room_chat);
        mTxtUser = (TextView) findViewById(R.id.chat_firebase_txt_user);
        mUserName = AppSetting.getUserName(this);
        mAdapterRoom = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mListRoom);
        mLvRoom.setAdapter(mAdapterRoom);
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> map = new HashMap<String, Object>();
                map.put(mEdtRoom.getText().toString(),mUserName);
                root.updateChildren(map);
                mEdtRoom.setText("");
            }
        });
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<String>();
                Iterator item = dataSnapshot.getChildren().iterator();
                while (item.hasNext()){
                    set.add(((DataSnapshot)item.next()).getKey());
                }
                mListRoom.clear();
                mListRoom.addAll(set);
                mAdapterRoom.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mLvRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),ChatRomActivity.class);
                intent.putExtra("room_name",((TextView)view).getText().toString());
                intent.putExtra("user_name",mUserName);
                startActivity(intent);
            }
        });
        mLvRoom.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final Dialog dialog = new Dialog(MainActivity.this);
                Button button = new Button(MainActivity.this);
                button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                button.setText("Delete");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String chatRoomToDelete = mAdapterRoom.getItem(i);
                        root.child(chatRoomToDelete).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    mAdapterRoom.remove(chatRoomToDelete);
                                } else {
// show databaseError to user
                                    Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        });
                    }
                });
                dialog.setTitle("Delete Room Chat");
                dialog.setContentView(button);
                dialog.show();
                return true;
            }
        });
        if (mUserName.equals("")){
            requestUser();
        }else {
            setUser();
        }
    }
    private void requestUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Name : ");
        final EditText editText = new EditText(this);
        builder.setView(editText);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mUserName = editText.getText().toString();
                AppSetting.setUserName(MainActivity.this,mUserName);
                setUser();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                requestUser();
            }
        });
        builder.show();
    }
    private void setUser(){
        mTxtUser.setText(mUserName);
        mTxtUser.setVisibility(View.VISIBLE);
    }
}