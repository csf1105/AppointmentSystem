package my.edu.utar.appointmentsystem;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LecturerUpcoming extends AppCompatActivity {

    private ArrayList<Appointment> appointmentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_upcoming);
        // customize action bar
        getSupportActionBar().setTitle("Appointment With Student");
        getData();
    }

    // inflate menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        Intent intent = getIntent();
        String lecturerID = intent.getStringExtra("lecturerID");
        System.out.println(lecturerID);

        Intent intent1 = new Intent(LecturerUpcoming.this, LecturerUpcoming.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent1.putExtra("lecturerID", lecturerID);
        menu.add("Upcoming Booking").setIntent(intent1);

        Intent intent2 = new Intent(LecturerUpcoming.this, UploadSchedule.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent2.putExtra("lecturerID", lecturerID);
        menu.add("Upload Schedule").setIntent(intent2);

        Intent intent3 = new Intent(LecturerUpcoming.this, LecturerMainPage.class);
        intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent3.putExtra("lecturerID", lecturerID);
        menu.add("My booking").setIntent(intent3);

        Intent intent5 = new Intent(LecturerUpcoming.this, ManageSchedule.class);
        intent5.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent5.putExtra("lecturerID", lecturerID);
        menu.add("Manage Schedule").setIntent(intent5);

        Intent intent4 = new Intent(LecturerUpcoming.this, roleSelection.class);
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
        LinearLayoutCompat progressBar = findViewById(R.id.upcoming_ProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        String lecturerID = intent.getStringExtra("lecturerID");
        String url2 = "https://appointmentmobileapi.herokuapp.com/listUpcomingBookingLecturer/" + lecturerID;
//        String url2 = "https://appointmentmobileapi.herokuapp.com/listUpcomingBookingLecturer/623f0b0f53bea9bf72cc44ae";
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, url2, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            appointmentData = formatResponse(response);
                            if (appointmentData == null) {
                                Log.e("Appointment Length", "formatResponse: null");
                                RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_lecturer_upcoming);
                                LinearLayoutCompat ll = (LinearLayoutCompat) rl.findViewById(R.id.lecturer_upcoming_container);
                                TextView tv = new TextView(LecturerUpcoming.this);
                                tv.setText("No Upcoming Booking");
                                tv.setGravity(Gravity.CENTER);
                                ll.setGravity(Gravity.CENTER);
                                ll.addView(tv);
                            } else {
                                Log.e("Appointment Length", "formatResponse: " + appointmentData.size());
                                ListView bookingList = (ListView) findViewById(R.id.lecturer_upcoming_list);
                                BookingAdapter bookingAdapter = new BookingAdapter(LecturerUpcoming.this, appointmentData, lecturerID);
                                bookingList.setAdapter(bookingAdapter);
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
        VolleySingleton.getInstance(LecturerUpcoming.this).addToRequestQueue(jsonObjectRequest);
    }

    private ArrayList<Appointment> formatResponse(JSONArray response) throws JSONException {
        // format each appointment into Appointment class
        // place all booking into ArrayList<Appointment>
        Log.e("Response Length", "formatResponse: " + response.length());
        ArrayList<Appointment> appointments = new ArrayList<Appointment>();
        for (int i = 0 ; i < response.length(); i++) {
            JSONObject obj = response.getJSONObject(i);
            appointments.add(new Appointment(obj));
        }

        if (!appointments.isEmpty()) {
            return appointments;
        }
        return null;
    }
}