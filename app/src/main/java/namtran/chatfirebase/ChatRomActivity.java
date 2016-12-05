package namtran.chatfirebase;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChatRomActivity extends AppCompatActivity {

    public static final String URL_STORAGE_REFERENCE = "gs://chatandroidfirebase-b2180.appspot.com";
    public static final String FOLDER_STORAGE_IMG = "images";
    public static final String TAG = "ChatRomActivity";
    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;
    private ListView mLvConvention;
    private EditText mEdtMessage;
    private ImageView mBtnPickImage;
    private Button mBtnSend;
    private String mUserName, mRoomName;
    private DatabaseReference root;
    private String temp_key;
    //File
    private File filePathImageCamera;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private List<ChatModel> chatModels;
    private AdapterChat adapterChat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mLvConvention = (ListView) findViewById(R.id.chat_firebase_lv_convention);
        mEdtMessage = (EditText) findViewById(R.id.chat_firebase_edt_message);
        mBtnPickImage = (ImageView) findViewById(R.id.chat_firebase_btn_pick_image);
        mBtnSend = (Button) findViewById(R.id.chat_firebase_btn_sent);
        chatModels = new ArrayList<>();
        adapterChat = new AdapterChat(ChatRomActivity.this, R.layout.adapter_chat, chatModels);
        mLvConvention.setAdapter(adapterChat);
        mUserName = getIntent().getExtras().getString("user_name").toString();
        mRoomName = getIntent().getExtras().getString("room_name").toString();
        setTitle(" Room : " + mRoomName);
        root = FirebaseDatabase.getInstance().getReference().child(mRoomName);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(map);
                DatabaseReference message_root = root.child(temp_key);
                Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put("user_name", mUserName);
                map2.put("message", mEdtMessage.getText().toString());
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

        mBtnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(ChatRomActivity.this);
                dialog.setContentView(R.layout.dialog_pick_image);
                dialog.setTitle("Pick Image");
                dialog.show();
                Button btnGallery = (Button) dialog.findViewById(R.id.chat_firebase_dialog_btn_gallery);
                Button btnCamera = (Button) dialog.findViewById(R.id.chat_firebase_dialog_btn_camera);
                btnGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        photoGalleryIntent();
                        dialog.dismiss();
                    }
                });
                btnCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        photoCameraIntent();
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        StorageReference storageRef = storage.getReferenceFromUrl(URL_STORAGE_REFERENCE).child(FOLDER_STORAGE_IMG);
        if (requestCode == IMAGE_GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    sendFileFirebase(storageRef, selectedImageUri);
                } else {
                        //URI IS NULL
                }
            }
        } else if (requestCode == IMAGE_CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (filePathImageCamera != null && filePathImageCamera.exists()) {
                    StorageReference imageCameraRef = storageRef.child(filePathImageCamera.getName() + "_camera");
                    sendFileFirebase(imageCameraRef, Uri.fromFile(filePathImageCamera));
                } else {
                        //IS NULL
                }
            }
        }
    }

    /**
     * Envia o arvquivo para o firebase
     */
    private void sendFileFirebase(StorageReference storageReference, final Uri file) {
        if (storageReference != null) {
            final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
            StorageReference imageGalleryRef = storageReference.child(name + "_gallery");
            UploadTask uploadTask = imageGalleryRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure sendFileFirebase " + e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG, "onSuccess sendFileFirebase");
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.d("URIIMAGE",downloadUrl.toString());
                    Map<String, Object> map = new HashMap<String, Object>();
                    temp_key = root.push().getKey();
                    root.updateChildren(map);
                    DatabaseReference message_root = root.child(temp_key);
                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("user_name", mUserName);
                    map2.put("message", downloadUrl.toString());
                    message_root.updateChildren(map2);
                }
            });
        } else {
//IS NULL
        }
    }

    private String mNameUser, mMessage;

    private void appendChatConversation(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()) {
            mMessage = ((DataSnapshot) i.next()).getValue().toString();
            mNameUser = ((DataSnapshot) i.next()).getValue().toString();
            chatModels.add(new ChatModel(mNameUser, mMessage));
            adapterChat.notifyDataSetChanged();
            mLvConvention.smoothScrollToPosition(chatModels.size());
        }
    }

    /**
     * Enviar foto tirada pela camera
     */
    private void photoCameraIntent() {
        String nomeFoto = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        filePathImageCamera = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), nomeFoto + "camera.jpg");
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(filePathImageCamera));
        startActivityForResult(it, IMAGE_CAMERA_REQUEST);
    }

    /**
     * Enviar foto pela galeria
     */
    private void photoGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
    }
}