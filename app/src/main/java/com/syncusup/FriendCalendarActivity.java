package com.syncusup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

@TargetApi(9)
public class FriendCalendarActivity extends Activity implements OnClickListener{
    private static final String tag = "MyCalendarActivity";
    protected ProgressDialog proDialog;
    private TextView currentMonth;
    private Button selectedDayMonthYearButton;
    private ImageView prevMonth;
    private ImageView nextMonth;
    private GridView calendarView;
    private GridCellAdapter adapter;
    private Calendar _calendar;
    @SuppressLint("NewApi")
    private int month, year;
    private final ParseUser currentUser = ParseUser.getCurrentUser();
    @SuppressWarnings("unused")
    @SuppressLint({ "NewApi", "NewApi", "NewApi", "NewApi" })
    private final DateFormat dateFormatter = new DateFormat();
    private static final String dateTemplate = "MMMM yyyy";
    private String value;
    private Integer test = 0;

    private final HashMap<String, Integer> idMap = new HashMap<String, Integer>();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_calendar_view);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("EXTRA_SESSION_ID");
        }

        _calendar = Calendar.getInstance(Locale.getDefault());
        month = _calendar.get(Calendar.MONTH) + 1;
        year = _calendar.get(Calendar.YEAR);
        Log.d(tag, "Calendar Instance:= " + "Month: " + month + " " + "Year: "
                + year);

        selectedDayMonthYearButton = (Button) this
                .findViewById(R.id.selectedDayMonthYear);
        selectedDayMonthYearButton.setText("Selected: ");

        prevMonth = (ImageView) this.findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(this);

        currentMonth = (TextView) this.findViewById(R.id.currentMonth);
        currentMonth.setText(DateFormat.format(dateTemplate,
                _calendar.getTime()));

        nextMonth = (ImageView) this.findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(this);

        calendarView = (GridView) this.findViewById(R.id.calendar);

        // Initialised
        adapter = new GridCellAdapter(getApplicationContext(),
                R.id.calendar_day_gridcell, month, year);
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);

    }
    protected void startLoading() {
        proDialog = new ProgressDialog(this);
        proDialog.setMessage("loading...");
        proDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        proDialog.setCancelable(false);
        proDialog.show();
    }

    protected void stopLoading() {
        proDialog.dismiss();
        proDialog = null;
    }

    /**
     *
     * @param month
     * @param year
     */
    private void setGridCellAdapterToDate(int month, int year) {
        adapter = new GridCellAdapter(getApplicationContext(),
                R.id.calendar_day_gridcell, month, year);
        _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
        currentMonth.setText(DateFormat.format(dateTemplate,
                _calendar.getTime()));
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v == prevMonth) {
            if (month <= 1) {
                month = 12;
                year--;
            } else {
                month--;
            }
            Log.d(tag, "Setting Prev Month in GridCellAdapter: " + "Month: "
                    + month + " Year: " + year);
            setGridCellAdapterToDate(month, year);
        }
        if (v == nextMonth) {
            if (month > 11) {
                month = 1;
                year++;
            } else {
                month++;
            }
            Log.d(tag, "Setting Next Month in GridCellAdapter: " + "Month: "
                    + month + " Year: " + year);
            setGridCellAdapterToDate(month, year);
        }

    }

    @Override
    public void onDestroy() {
        Log.d(tag, "Destroying View ...");
        super.onDestroy();
    }

    // Inner Class
    public class GridCellAdapter extends BaseAdapter implements OnClickListener, OnLongClickListener {
        private static final String tag = "GridCellAdapter";
        private final Context _context;

        private final List<String> list;
        private static final int DAY_OFFSET = 1;
        private final String[] weekdays = new String[] { "Sun", "Mon", "Tue",
                "Wed", "Thu", "Fri", "Sat" };
        private final String[] months = { "January", "February", "March",
                "April", "May", "June", "July", "August", "September",
                "October", "November", "December" };
        private final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
                31, 30, 31 };
        private int daysInMonth;
        private int currentDayOfMonth;
        private int currentWeekDay;
        private Button gridcell;
        private TextView num_events_per_day;
        private final HashMap<String, Integer> eventsPerMonthMap;
        private final SimpleDateFormat dateFormatter = new SimpleDateFormat(
                "dd-MMM-yyyy");

        // Days in Current Month
        public GridCellAdapter(Context context, int textViewResourceId,
                               int month, int year){
            super();
            this._context = context;
            this.list = new ArrayList<String>();
            Log.d(tag, "==> Passed in Date FOR Month: " + month + " "
                    + "Year: " + year);
            Calendar calendar = Calendar.getInstance();
            setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
            setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
            Log.d(tag, "New Calendar:= " + calendar.getTime().toString());
            Log.d(tag, "CurrentDayOfWeek :" + getCurrentWeekDay());
            Log.d(tag, "CurrentDayOfMonth :" + getCurrentDayOfMonth());

            // Print Month
            printMonth(month, year);

            // Find Number of Events
            eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
        }

        private String getMonthAsString(int i) {
            return months[i];
        }

        private String getWeekDayAsString(int i) {
            return weekdays[i];
        }

        private int getNumberOfDaysOfMonth(int i) {
            return daysOfMonth[i];
        }

        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        /**
         * Prints Month
         *
         * @param mm
         * @param yy
         */
        private void printMonth(int mm, int yy) {
            Log.d(tag, "==> printMonth: mm: " + mm + " " + "yy: " + yy);
            int trailingSpaces = 0;
            int daysInPrevMonth = 0;
            int prevMonth = 0;
            int prevYear = 0;
            int nextMonth = 0;
            int nextYear = 0;

            int currentMonth = mm - 1;
            String currentMonthName = getMonthAsString(currentMonth);
            daysInMonth = getNumberOfDaysOfMonth(currentMonth);

            Log.d(tag, "Current Month: " + " " + currentMonthName + " having "
                    + daysInMonth + " days.");

            GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);
            Log.d(tag, "Gregorian Calendar:= " + cal.getTime().toString());

            if (currentMonth == 11) {
                prevMonth = currentMonth - 1;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                nextMonth = 0;
                prevYear = yy;
                nextYear = yy + 1;
                Log.d(tag, "*->PrevYear: " + prevYear + " PrevMonth:"
                        + prevMonth + " NextMonth: " + nextMonth
                        + " NextYear: " + nextYear);
            } else if (currentMonth == 0) {
                prevMonth = 11;
                prevYear = yy - 1;
                nextYear = yy;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                nextMonth = 1;
                Log.d(tag, "**--> PrevYear: " + prevYear + " PrevMonth:"
                        + prevMonth + " NextMonth: " + nextMonth
                        + " NextYear: " + nextYear);
            } else {
                prevMonth = currentMonth - 1;
                nextMonth = currentMonth + 1;
                nextYear = yy;
                prevYear = yy;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                Log.d(tag, "***---> PrevYear: " + prevYear + " PrevMonth:"
                        + prevMonth + " NextMonth: " + nextMonth
                        + " NextYear: " + nextYear);
            }

            int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
            trailingSpaces = currentWeekDay;

            Log.d(tag, "Week Day:" + currentWeekDay + " is "
                    + getWeekDayAsString(currentWeekDay));
            Log.d(tag, "No. Trailing space to Add: " + trailingSpaces);
            Log.d(tag, "No. of Days in Previous Month: " + daysInPrevMonth);

            if (cal.isLeapYear(cal.get(Calendar.YEAR)))
                if (mm == 2)
                    ++daysInMonth;
                else if (mm == 3)
                    ++daysInPrevMonth;

            // Trailing Month days
            for (int i = 0; i < trailingSpaces; i++) {
                Log.d(tag,
                        "PREV MONTH:= "
                                + prevMonth
                                + " => "
                                + getMonthAsString(prevMonth)
                                + " "
                                + String.valueOf((daysInPrevMonth
                                - trailingSpaces + DAY_OFFSET)
                                + i));
                list.add(String
                        .valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
                                + i)
                        + "-GREY"
                        + "-"
                        + getMonthAsString(prevMonth)
                        + "-"
                        + prevYear);
            }

            // Current Month Days
            for (int i = 1; i <= daysInMonth; i++) {
                Log.d(currentMonthName, String.valueOf(i) + " "
                        + getMonthAsString(currentMonth) + " " + yy);
                if (i == getCurrentDayOfMonth()) {
                    list.add(String.valueOf(i) + "-BLUE" + "-"
                            + getMonthAsString(currentMonth) + "-" + yy);
                } else {
                    list.add(String.valueOf(i) + "-WHITE" + "-"
                            + getMonthAsString(currentMonth) + "-" + yy);
                }
            }

            // Leading Month days
            for (int i = 0; i < list.size() % 7; i++) {
                Log.d(tag, "NEXT MONTH:= " + getMonthAsString(nextMonth));
                list.add(String.valueOf(i + 1) + "-GREY" + "-"
                        + getMonthAsString(nextMonth) + "-" + nextYear);
            }
        }

        /**
         * NOTE: YOU NEED TO IMPLEMENT THIS PART Given the YEAR, MONTH, retrieve
         * ALL entries from a SQLite database for that month. Iterate over the
         * List of All entries, and get the dateCreated, which is converted into
         * day.
         *
         * @param year
         * @param month
         * @return
         */
        private List<ParseObject> returnList;
        private List<ParseObject> p;

        private List<ParseObject> returnList2;
        private HashMap<String, Integer> findNumberOfEventsPerMonth(int year,
                                                                    int month) {
            final HashMap<String, Integer> map = new HashMap<String, Integer>();
            final int days = getNumberOfDaysOfMonth(month);
            final int year1 = year;
            final int month1 = month;
            List<String> permissions = new ArrayList<>();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Friends");
            query.whereEqualTo("status", "friend");
            query.whereEqualTo("friend_id", currentUser.getObjectId());
            query.whereEqualTo("friend_id2", value);

            try {
                p = query.find();
            } catch (com.parse.ParseException e) {
                for (int i = 0; i < days; i++) {
                    String day;
                    if (i < 10) day = "0" + i;
                    else day = "" + i;
                    map.put(day, 0);
                }
                return map;
            }
            if(p.size() != 0) {
                if (p.get(0).getBoolean("all")) {
                    permissions.add("all");
                } else {
                    permissions.add("everyone");
                    if (p.get(0).getBoolean("work")) permissions.add("work");
                    if (p.get(0).getBoolean("family")) permissions.add("family");
                    if (p.get(0).getBoolean("friend")) permissions.add("friend");
                    if (p.get(0).getBoolean("school")) permissions.add("school");
                    if (p.get(0).getBoolean("personal")) permissions.add("personal");
                }
            }else{
                permissions.add("everyone");
            }
            /*
            if (permissions.get(0).equals("all")) {
                ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Event");
                query2.whereEqualTo("creator", value);
                query2.whereEqualTo("startMonth", month1);
                query2.whereEqualTo("startYear", year1);

                try {
                    returnList2 = query2.find();
                } catch (com.parse.ParseException e) {
                    for (int i = 0; i < days; i++) {
                        String day;
                        if (i < 10) day = "0" + i;
                        else day = "" + i;
                        map.put(day, 0);
                    }
                    return map;
                }
                if (returnList2.size() > 0) {

                    for (int i = 0; i < returnList2.size(); i++) {
                        if (returnList2.get(i).getBoolean("private")) continue;
                        String day;
                        Integer d = returnList2.get(i).getInt("startDay");
                        if (d < 10) day = "0" + d;
                        else day = "" + d;
                        if (map.containsKey(day)) {
                            Integer val = (Integer) map.get(day) + 1;
                            map.put(day, val);
                        } else {
                            map.put(day, 1);
                        }
                    }
                } else {
                    for (int i = 0; i < days; i++) {
                        String day;
                        if (i < 10) day = "0" + i;
                        else day = "" + i;
                        map.put(day, 0);
                        return map;
                    }
                }
            } else {*/
                ParseQuery<ParseObject> mainQuery;
                if (permissions.size() == 1) {
                    mainQuery = ParseQuery.getQuery("Event");
                    mainQuery.whereEqualTo(permissions.get(0), true);
                    mainQuery.whereEqualTo("creator", value);
                    mainQuery.whereEqualTo("startMonth", month1);
                    mainQuery.whereEqualTo("startYear", year1);
                    mainQuery.whereNotEqualTo("private", true);
                }
                else if (permissions.size() == 2) {
                    ParseQuery<ParseObject> first = ParseQuery.getQuery("Event");
                    first.whereEqualTo(permissions.get(0), true);
                    ParseQuery<ParseObject> second = ParseQuery.getQuery("Event");
                    second.whereEqualTo(permissions.get(1), true);
                    List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
                    queries.add(first);
                    queries.add(second);
                    mainQuery = ParseQuery.or(queries);
                    mainQuery.whereEqualTo("creator", value);
                    mainQuery.whereEqualTo("startMonth", month1);
                    mainQuery.whereEqualTo("startYear", year1);
                    mainQuery.whereNotEqualTo("private", true);
                } else if (permissions.size() == 3) {
                    ParseQuery<ParseObject> first = ParseQuery.getQuery("Event");
                    first.whereEqualTo(permissions.get(0), true);
                    ParseQuery<ParseObject> second = ParseQuery.getQuery("Event");
                    second.whereEqualTo(permissions.get(1), true);
                    ParseQuery<ParseObject> third = ParseQuery.getQuery("Event");
                    third.whereEqualTo(permissions.get(2), true);
                    List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
                    queries.add(first);
                    queries.add(second);
                    queries.add(third);
                    mainQuery = ParseQuery.or(queries);
                    mainQuery.whereEqualTo("creator", value);
                    mainQuery.whereEqualTo("startMonth", month1);
                    mainQuery.whereEqualTo("startYear", year1);
                    mainQuery.whereNotEqualTo("private", true);
                } else if (permissions.size() == 4) {
                    ParseQuery<ParseObject> first = ParseQuery.getQuery("Event");
                    first.whereEqualTo(permissions.get(0), true);
                    ParseQuery<ParseObject> second = ParseQuery.getQuery("Event");
                    second.whereEqualTo(permissions.get(1), true);
                    ParseQuery<ParseObject> third = ParseQuery.getQuery("Event");
                    third.whereEqualTo(permissions.get(2), true);
                    ParseQuery<ParseObject> fourth = ParseQuery.getQuery("Event");
                    fourth.whereEqualTo(permissions.get(3), true);
                    List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
                    queries.add(first);
                    queries.add(second);
                    queries.add(third);
                    queries.add(fourth);
                    mainQuery = ParseQuery.or(queries);
                    mainQuery.whereEqualTo("creator", value);
                    mainQuery.whereEqualTo("startMonth", month1);
                    mainQuery.whereEqualTo("startYear", year1);
                    mainQuery.whereNotEqualTo("private", true);
                } else if (permissions.size() == 5) {
                    ParseQuery<ParseObject> first = ParseQuery.getQuery("Event");
                    first.whereEqualTo(permissions.get(0), true);
                    ParseQuery<ParseObject> second = ParseQuery.getQuery("Event");
                    second.whereEqualTo(permissions.get(1), true);
                    ParseQuery<ParseObject> third = ParseQuery.getQuery("Event");
                    third.whereEqualTo(permissions.get(2), true);
                    ParseQuery<ParseObject> fourth = ParseQuery.getQuery("Event");
                    fourth.whereEqualTo(permissions.get(3), true);
                    ParseQuery<ParseObject> fifth = ParseQuery.getQuery("Event");
                    fifth.whereEqualTo(permissions.get(4), true);
                    List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
                    queries.add(first);
                    queries.add(second);
                    queries.add(third);
                    queries.add(fourth);
                    queries.add(fifth);
                    mainQuery = ParseQuery.or(queries);
                    mainQuery.whereEqualTo("creator", value);
                    mainQuery.whereEqualTo("startMonth", month1);
                    mainQuery.whereEqualTo("startYear", year1);
                    mainQuery.whereNotEqualTo("private", true);
                } else {
                    ParseQuery<ParseObject> first = ParseQuery.getQuery("Event");
                    first.whereEqualTo(permissions.get(0), true);
                    ParseQuery<ParseObject> second = ParseQuery.getQuery("Event");
                    second.whereEqualTo(permissions.get(1), true);
                    ParseQuery<ParseObject> third = ParseQuery.getQuery("Event");
                    third.whereEqualTo(permissions.get(2), true);
                    ParseQuery<ParseObject> fourth = ParseQuery.getQuery("Event");
                    fourth.whereEqualTo(permissions.get(3), true);
                    ParseQuery<ParseObject> fifth = ParseQuery.getQuery("Event");
                    fifth.whereEqualTo(permissions.get(4), true);
                    ParseQuery<ParseObject> sixth = ParseQuery.getQuery("Event");
                    sixth.whereEqualTo(permissions.get(5), true);
                    List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
                    queries.add(first);
                    queries.add(second);
                    queries.add(third);
                    queries.add(fourth);
                    queries.add(fifth);
                    queries.add(sixth);
                    mainQuery = ParseQuery.or(queries);
                    mainQuery.whereEqualTo("creator", value);
                    mainQuery.whereEqualTo("startMonth", month1);
                    mainQuery.whereEqualTo("startYear", year1);
                    mainQuery.whereNotEqualTo("private", true);
               // }

                try {
                    returnList2 = mainQuery.find();
                } catch (com.parse.ParseException e1) {
                    for (int i = 0; i < days; i++) {
                        String day;
                        if (i < 10) day = "0" + i;
                        else day = "" + i;
                        map.put(day, 0);
                    }
                    return map;
                }

                if (returnList2.size() > 0) {
                    Toast.makeText(getApplicationContext(), "" + returnList2.size(),
                            Toast.LENGTH_LONG).show();
                    for (int i = 0; i < returnList2.size(); i++) {
                        String day;
                        Integer d = returnList2.get(i).getInt("startDay");
                        if (d < 10) day = "0" + d;
                        else day = "" + d;
                        if (map.containsKey(day)) {
                            Integer val = (Integer) map.get(day) + 1;
                            map.put(day, val);
                        } else {
                            map.put(day, 1);
                        }
                    }
                } else {
                    for (int i = 0; i < days; i++) {
                        String day;
                        if (i < 10) day = "0" + i;
                        else day = "" + i;
                        map.put(day, 0);
                    }
                }
            }
            return map;
        }


        @Override
        public long getItemId ( int position){
            return position;
        }

        @Override
        public View getView ( int position, View convertView, ViewGroup parent){
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) _context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.screen_gridcell, parent, false);
            }

            // Get a reference to the Day gridcell
            gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
            gridcell.setOnClickListener(this);
            gridcell.setOnLongClickListener(this);

            // ACCOUNT FOR SPACING

            Log.d(tag, "Current Day: " + getCurrentDayOfMonth());
            String[] day_color = list.get(position).split("-");
            String theday = day_color[0];
            String themonth = day_color[2];
            String theyear = day_color[3];
            Integer numEvents = 0;
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
            String month_name = month_date.format(cal.getTime());
            if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null)) {
                if (eventsPerMonthMap.containsKey(theday)) {
                    num_events_per_day = (TextView) row
                            .findViewById(R.id.num_events_per_day);
                    numEvents = (Integer) eventsPerMonthMap.get(theday);
                    if (numEvents > 0 && themonth.equals(month_name))
                        num_events_per_day.setText(numEvents.toString());
                }
            }

            // Set the Day GridCell
            gridcell.setText(theday);

            gridcell.setTag(theday + "-" + themonth + "-" + theyear);
            Log.d(tag, "Setting GridCell " + theday + "-" + themonth + "-"
                    + theyear);

            if (day_color[1].equals("GREY")) {
                gridcell.setTextColor(getResources()
                        .getColor(R.color.lightgray));
            }
            if (day_color[1].equals("WHITE")) {
                gridcell.setTextColor(getResources().getColor(
                        R.color.lightgray02));
            }

            if (day_color[1].equals("BLUE")) {
                if (!themonth.equals(month_name)) {
                    gridcell.setTextColor(getResources().getColor(R.color.lightgray02));
                } else
                    gridcell.setTextColor(getResources().getColor(R.color.orrange));
            } else {
                if (numEvents > 0 && themonth.equals(month_name)) gridcell.setTextColor(getIntFromColor(0, 0, 0));
            }

            return row;
        }

        @Override
        public void onClick (View view){
            final String date_month_year = (String) view.getTag();
            selectedDayMonthYearButton.setText("Selected: " + date_month_year);
            Log.e("Selected date", date_month_year);

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Friends");
            query.whereEqualTo("status", "friend");
            query.whereEqualTo("friend_id", currentUser.getObjectId());
            query.whereEqualTo("friend_id2", value);
            query.getFirstInBackground(new GetCallback<ParseObject>() {

                @Override
                public void done(ParseObject friendObject, com.parse.ParseException e) {
                    Intent intent = new Intent(getBaseContext(), ViewFriendEventsActivity.class);
                    String pass = date_month_year;
                    pass += ","+value;
                    if (friendObject.getBoolean("all")) {
                        pass+=",all";
                    } else {
                        pass+=",everyone";
                        if (friendObject.getBoolean("work"))
                            pass+=",work";
                        if (friendObject.getBoolean("family"))
                            pass+=",family";
                        if (friendObject.getBoolean("friend"))
                            pass+=",friend";
                        if (friendObject.getBoolean("school"))
                            pass+=",school";
                        if (friendObject.getBoolean("personal"))
                            pass+=",personal";

                    }
                    intent.putExtra("EXTRA_SESSION_ID3",pass);
                    startActivity(intent);
                }
            });


			/*try {
				Date parsedDate = dateFormatter.parse(date_month_year);
				Log.d(tag, "Parsed Date: " + parsedDate.toString());

			} catch (ParseException e) {
				e.printStackTrace();
			}*/
        }

        public int getIntFromColor(int Red, int Green, int Blue) {
            Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
            Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
            Blue = Blue & 0x000000FF; //Mask out anything not blue.

            return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
        }

        public boolean onLongClick(View view) {

            //String date_month_year = (String) view.getTag();
            //Intent intent = new Intent(getBaseContext(), AddEventActivity.class);
            //intent.putExtra("EXTRA_SESSION_ID",
            //        date_month_year);
            //startActivity(intent);
            return false;
        }


        public int getCurrentDayOfMonth() {
            return currentDayOfMonth;
        }

        private void setCurrentDayOfMonth(int currentDayOfMonth) {
            this.currentDayOfMonth = currentDayOfMonth;
        }

        public void setCurrentWeekDay(int currentWeekDay) {
            this.currentWeekDay = currentWeekDay;
        }

        public int getCurrentWeekDay() {
            return currentWeekDay;
        }
    }
}
