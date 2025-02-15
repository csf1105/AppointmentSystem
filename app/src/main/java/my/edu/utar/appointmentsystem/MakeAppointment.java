package my.edu.utar.appointmentsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MakeAppointment extends AppCompatActivity {

    private ArrayList<Lecturer> lecturerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_appointment);
        // customize action bar
        getSupportActionBar().setTitle("Make Appointment");
        getData();
    }

    // inflate menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);


        Intent intent1 = getIntent();
        String studentID = intent1.getStringExtra("studentID");

        Intent intent = new Intent(MakeAppointment.this, StudentUpcoming.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("studentID", studentID);
        menu.add("Upcoming Booking").setIntent(intent);

        Intent intent2 = new Intent(MakeAppointment.this, MakeAppointment.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent2.putExtra("studentID", studentID);
        menu.add("Make Appointment").setIntent(intent2);

        Intent intent3 = new Intent(MakeAppointment.this, StudentMainPage.class);
        intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent3.putExtra("studentID", studentID);
        menu.add("My booking").setIntent(intent3);

        Intent intent4 = new Intent(MakeAppointment.this, roleSelection.class);
        intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        menu.add("Logout").setIntent(intent4);

        return true;
    }

    // menu item selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void getData() {
        LinearLayoutCompat progressBar = findViewById(R.id.available_lecturer_progressBar);
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        String studentID = intent.getStringExtra("studentID");
        String url2 = "https://appointmentmobileapi.herokuapp.com/listAllLecturer";
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, url2, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        progressBar.setVisibility(View.GONE);
//                        Log.e("Lecturer", "onResponse: " + response.toString());
                        try {
                            lecturerData = formatResponse(response);
                            if (lecturerData == null) {
                                Log.e("Lecturer Length", "formatResponse: null");
                                RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_make_appointment);
                                LinearLayoutCompat ll = (LinearLayoutCompat) rl.findViewById(R.id.available_lecturer_container);
                                TextView tv = new TextView(MakeAppointment.this);
                                tv.setText("No Available Lecturer");
                                tv.setGravity(Gravity.CENTER);
                                ll.setGravity(Gravity.CENTER);
                                ll.addView(tv);
                            } else {
                                Log.e("Lecturer Length", "formatResponse: " + lecturerData.size());
                                ListView bookingList = (ListView) findViewById(R.id.available_lecturer_list);
                                LecturerAdapter lecturerAdapter = new LecturerAdapter(MakeAppointment.this, lecturerData, studentID, response);
                                bookingList.setAdapter(lecturerAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("JSONException", "e: " + e.getMessage(), e.getCause());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            byte[] htmlBodyBytes = error.networkResponse.data;
                            JSONObject errorRes =new JSONObject(new String(htmlBodyBytes));
                            //System.out.println(errorRes.getString("status"));
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };
        VolleySingleton.getInstance(MakeAppointment.this).addToRequestQueue(jsonObjectRequest);
    }

    private ArrayList<Lecturer> formatResponse(JSONArray response) throws JSONException {
        // format each lecturer into Lecturer class
        // place all Lecturer into ArrayList<Lecturer>
        Log.e("Lecturer Length", "formatResponse: " + response.length());
        ArrayList<Lecturer> appointments = new ArrayList<Lecturer>();
        for (int i = 0 ; i < response.length(); i++) {
            JSONObject obj = response.getJSONObject(i);
            appointments.add(new Lecturer(obj));
        }

        if (!appointments.isEmpty()) {
            return appointments;
        }
        return null;
    }
}