package com.example.livechatting;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

public class Ba_SignUp extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_profile;
    private EditText et_id;
    private EditText et_nick;
    private EditText et_pw1;
    private EditText et_pw2;

    private static File imageFile = null;
    /* 프로필 이미지 설정 여부
     * true  - add menu '기본이미지로 변경'
     * false - nothing */
    boolean isSetImage = false;

    private boolean isId = false;
    private boolean isNick = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ba_sign_up);

        iv_profile = findViewById(R.id.ba_iv_photo);
        Glide.with(getApplicationContext())
                .load(R.drawable.profile_default)
                .apply(new RequestOptions()
                        .circleCrop())
                .into(iv_profile);
        et_id = findViewById(R.id.ba_et_id);
        et_nick = findViewById(R.id.ba_et_nickname);
        et_pw1 = findViewById(R.id.ba_et_pw);
        et_pw2 = findViewById(R.id.ba_et_pwCheck);
    }

    @Override
    public void onClick(View v) {

    }
/*
    @Override
    protected void onResume() {
        super.onResume();
        if (Selected_Bitmap != null) {
            Glide.with(getApplicationContext())
                    .load(Selected_Bitmap)
                    .apply(new RequestOptions()
                            .circleCrop())
                    .into(iv_profile);
            isSetImage = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // (여기) 곳곳에서 활약 & 얼굴검출 이미지와 (갤러리 or 카메라) 이미지를 구별하여 회원가입함
        Selected_Bitmap = null;
        isSetImage = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int granted = PackageManager.PERMISSION_GRANTED;
        if (grantResults[0] != granted) {
            Toast.makeText(getApplicationContext(), "This feature is not available without permission", Toast.LENGTH_LONG).show();
            return;
        }

        imageFile = Image.createImageFile();
        switch (requestCode) {

            case Information.PERMISSION_CODE_CAMERA:

                if (grantResults[1] == granted) {
                    Image.invokeCamera(this, imageFile, Information.REQUEST_CODE_CAMERA);
                } else {
                    Toast.makeText(getApplicationContext(), "This feature is not available without permission", Toast.LENGTH_LONG).show();
                }
                break;

            case Information.PERMISSION_CODE_ALBUM:
                Image.invokeAlbum(this, Information.REQUEST_CODE_ALBUM);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        Uri uri24 = Image.uriFromFileProvider(this, imageFile);
        Uri uri = Uri.fromFile(imageFile);
        switch (requestCode) {
            case Information.REQUEST_CODE_CAMERA:
                Image.cropImage(this, uri24, uri, Information.REQUEST_CODE_CROP);
                break;
            case Information.REQUEST_CODE_ALBUM:
                *//* use Uri.fromFile() at any cost *//*
                Image.cropImage(this, data.getData(), uri, Information.REQUEST_CODE_CROP);
                break;
            case Information.REQUEST_CODE_CROP:
                *//* use Uri.fromFile() at any cost *//*
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                sendBroadcast(intent);

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    Glide.with(getApplicationContext())
                            .load(bitmap)
                            .apply(new RequestOptions()
                                    .circleCrop())
                            .into(iv_profile);
                    isSetImage = true;
                    Selected_Bitmap = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ba_iv_photo:
                showDialog();
                break;
            case R.id.ba_bt_checkId:
                checkId();
                break;
            case R.id.ba_bt_checkNick:
                checkNick();
                break;
            case R.id.ba_bt_Join:
                join();
                break;
            case R.id.ba_tv_goLogin:
                finish();
                break;
        }
    }

    private void showDialog() {
        List<String> ListItems = new ArrayList<>();
        ListItems.add(getString(R.string.dialog_profile_menu1));
        ListItems.add("얼굴검출 카메라");
        ListItems.add(getString(R.string.dialog_profile_menu2));
        if (isSetImage) {
            ListItems.add(getString(R.string.dialog_profile_menu3));
        }

        CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("프로필");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] cameraPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                String[] albumPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                switch (which) {
                    case 0:
                        ActivityCompat.requestPermissions(Ba_SignUp.this, cameraPermissions, Information.PERMISSION_CODE_CAMERA);
                        break;
                    case 1:
                        Intent intent = new Intent(Ba_SignUp.this, openCV_Main.class);
                        startActivity(intent);
                        break;
                    case 2:
                        ActivityCompat.requestPermissions(Ba_SignUp.this, albumPermissions, Information.PERMISSION_CODE_ALBUM);
                        break;
                    case 3:
                        Glide.with(getApplicationContext())
                                .load(R.drawable.profile_default)
                                .apply(new RequestOptions()
                                        .circleCrop())
                                .into(iv_profile);
                        imageFile = null;
                        Selected_Bitmap = null;
                        isSetImage = false;
                        *//*Uri uri = Image.defaultImageUri;
                          Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                          iv_profile.setImageBitmap(bitmap);*//*
                        break;
                }
            }
        });
        builder.show();
    }

    private void checkId() {
        String serverURL = Information.IP_ADDRESS + Information.PHP_CHECK_ID;
        String postParameters = "id=" + et_id.getText().toString();

        Network.RequestServer async = new Network.RequestServer();
        async.setListener(new Network.OnPostListener() {
            @Override
            public void onPost(String result) {
                if (result.equals("1")) {
                    Toast.makeText(getApplicationContext(), "아이디 사용 가능", Toast.LENGTH_SHORT).show();
                    isId = true;
                } else {
                    Toast.makeText(getApplicationContext(), "아이디 사용 불가", Toast.LENGTH_SHORT).show();
                    isId = false;
                }
            }
        });
        async.execute(serverURL, postParameters);
    }

    private void checkNick() {
        String serverURL = Information.IP_ADDRESS + Information.PHP_CHECK_NICK;
        String postParameters = "nick=" + et_nick.getText().toString();

        Network.RequestServer async = new Network.RequestServer();
        async.setListener(new Network.OnPostListener() {
            @Override
            public void onPost(String result) {
                if (result.equals("1")) {
                    Toast.makeText(getApplicationContext(), "닉네임 사용 가능", Toast.LENGTH_SHORT).show();
                    isNick = true;
                } else {
                    Toast.makeText(getApplicationContext(), "닉네임 사용 불가", Toast.LENGTH_SHORT).show();
                    isNick = false;
                }
            }
        });
        async.execute(serverURL, postParameters);
    }

    private void join() {
        if (!isId) {
            Toast.makeText(getApplicationContext(), "아이디를 확인해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isNick) {
            Toast.makeText(getApplicationContext(), "닉네임을 확인해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!et_pw1.getText().toString().equals(et_pw2.getText().toString())) {
            Toast.makeText(getApplicationContext(), "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = et_id.getText().toString();
        String nick = et_nick.getText().toString();
        String pw = et_pw2.getText().toString();

        if (imageFile == null) {
            if (Selected_Bitmap == null) {
                uploadJoinInfo(id, nick, pw);
                return;
            }
            Uri uri = getImageUri(getApplicationContext(), Selected_Bitmap);
            String image = uri.toString();
            Log.e("URI String", image);
            uploadJoinInfoWithImg(id, nick, pw, image);
        } else {
            String image = imageFile.toURI().toString();
            Log.e("URI String", image);
            uploadJoinInfoWithImg(id, nick, pw, image);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void uploadJoinInfo(String id, String nick, String pw) {
        String serverURL = Information.IP_ADDRESS + Information.PHP_JOIN_NOT_IMAGE;
        String postParameters = "id=" + id + "&nick=" + nick + "&pw=" + pw;

        Network.RequestServer async = new Network.RequestServer();
        async.setListener(new Network.OnPostListener() {
            @Override
            public void onPost(String result) {
                if (result.equals("1")) {
                    Toast.makeText(getApplicationContext(), "회원가입 완료", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
        async.execute(serverURL, postParameters);
    }

    private void uploadJoinInfoWithImg(String id, String nick, String pw, String image) {
        Network.Join join = new Network.Join();
        join.setListener(new Network.OnPostListener() {
            @Override
            public void onPost(String result) {
                if (result.equals("1")) {
                    Toast.makeText(getApplicationContext(), "회원가입 완료", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
        join.execute(image, id, nick, pw);
    }*/
}