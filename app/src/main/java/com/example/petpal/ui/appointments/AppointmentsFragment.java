package com.example.petpal.ui.appointments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.petpal.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AppointmentsFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private EditText appointmentNameEditText;
    private TextView appointmentDate;
    private TextView appointmentTime;
    private Button setAppointmentButton;


    private Calendar calendar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointments, container, false);

        appointmentNameEditText = view.findViewById(R.id.appointment_name);
        appointmentDate = view.findViewById(R.id.appointment_date);
        appointmentTime = view.findViewById(R.id.appointment_time);
        setAppointmentButton = view.findViewById(R.id.set_appointment_button);

        calendar = Calendar.getInstance();

        appointmentDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), AppointmentsFragment.this, year, month, day);
                datePickerDialog.show();
            }
        });

        appointmentTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), AppointmentsFragment.this, hour, minute, false);
                timePickerDialog.show();
            }
        });

        setAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle setting the appointment here
                String selectedDate = appointmentDate.getText().toString();
                String selectedTime = appointmentTime.getText().toString();
                String appointmentName = appointmentNameEditText.getText().toString();

                // Convert the selected date and time to milliseconds
                long appointmentDateTimeInMillis = convertDateTimeToMillis(selectedDate, selectedTime);

                // Set the appointment in the calendar
                setAppointmentInCalendar(appointmentName, appointmentDateTimeInMillis);
            }
        });



        return view;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String appointmentDateFormat = DateFormat.getDateInstance().format(calendar.getTime());
        appointmentDate.setText(appointmentDateFormat);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        String appointmentTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
        appointmentTime.setText(appointmentTimeFormat);
    }

    private long convertDateTimeToMillis(String date, String time) {
        try {
            String dateTimeString = date + " " + time;
            SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
            Date dateTime = format.parse(dateTimeString);
            return dateTime.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void setAppointmentInCalendar(String appointmentName, long appointmentDateTimeInMillis) {
        Intent calendarIntent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, appointmentName)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, appointmentDateTimeInMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, appointmentDateTimeInMillis + (60 * 60 * 1000)) // Add an hour duration
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "Your appointment location")
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

        startActivity(calendarIntent);
    }


}
