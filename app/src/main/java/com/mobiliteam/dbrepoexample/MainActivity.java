package com.mobiliteam.dbrepoexample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.j256.ormlite.stmt.PreparedQuery;
import com.mobiliteam.dbrepo.IDatabaseRepository;
import com.mobiliteam.dbrepo.OrmLiteRepository;
import com.mobiliteam.dbrepoexample.model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private IDatabaseRepository iDatabaseRepository;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iDatabaseRepository = OrmLiteRepository.getInstance(ApplicationEx.getContext());
        TextView awesomeTv = (TextView) findViewById(R.id.tv_awesome);
        awesomeTv.setText("DB Repository");

        createUserTable();
        seedData();

        Log.d("getById(Class<T> modelClass, int id)", iDatabaseRepository.getById(User.class, 45).toString());
        Log.d("getById(Class<T> modelClass, String recordId)", iDatabaseRepository.getById(User.class, "46").toString());
        Log.d("update(Class<T> modelClass,T item)", update());
        //delete();
        //deleteAll();
        Log.d("count", "" + iDatabaseRepository.count(User.class));
        Log.d("findAll()", findAll().toString());

        // import java.util.concurrent.Callable;
//        try {
//            iDatabaseRepository.inTransaction(new Callable() {
//                @Override
//                public Object call() throws Exception {
//                    iDatabaseRepository.myDao(User.class).create(user);
//                    return null; // Sending null for Transaction Successful
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    private List<User> findAll() {
        PreparedQuery<User> preparedQuery = null;
        try {
            preparedQuery = iDatabaseRepository.getQueryBuilder(User.class)
                    .where()
                    .eq("id", 38)
                    .prepare();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (List<User>) iDatabaseRepository.findAll(User.class, preparedQuery);
    }

    private String update() {
        User user = iDatabaseRepository.getById(User.class, 49);
        user.setEmail("updated" + user.getId() + "@abc.com");
        iDatabaseRepository.update(User.class, user);
        return iDatabaseRepository.getById(User.class, 49).toString();
    }

    private void delete() {
        User user = iDatabaseRepository.getById(User.class, 91);
        iDatabaseRepository.delete(User.class, user);
    }

    private void deleteAll() {
        List<User> users = (List<User>) iDatabaseRepository.getAll(User.class);
        iDatabaseRepository.deleteAll(User.class, users);
    }

    private void seedData() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            User user = new User();
            user.setId(i);
            user.setEmail("example_" + i + ".com");
            user.setMobile("900000000" + i);
            user.setUserName("Username_" + i);
            users.add(user);
            //iDatabaseRepository.add(User.class, user);
        }
        iDatabaseRepository.add(User.class, users);
    }

    private void createUserTable() {
        iDatabaseRepository.createTable(User.class);
    }

}
