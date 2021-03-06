package iavanish.collegepal.Courses;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import iavanish.collegepal.R;
import iavanish.collegepal.Start.NothingSelectedSpinnerAdapter;
import iavanish.collegepal.Start.User;
import iavanish.collegepal.Start.UserClientApi;
import retrofit.RestAdapter;

public class EnrollCourse extends Activity implements AdapterView.OnItemSelectedListener {

    private final String URL = "http://192.168.55.74:8080";
    private CourseClientApi courseService = new RestAdapter.Builder()
            .setEndpoint(URL).setLogLevel(RestAdapter.LogLevel.FULL).build()
            .create(CourseClientApi.class);
    private UserClientApi userService = new RestAdapter.Builder()
            .setEndpoint(URL).setLogLevel(RestAdapter.LogLevel.FULL).build()
            .create(UserClientApi.class);
    private Button mEnrollCourseButton;
    private Spinner spinnerCourse;
    String _courseName,_admin;
    Collection<Course> course;
    Collection<User> user;
    Boolean enrollUser,enrollCourse;
    private ArrayList<Course> mListCourse = new ArrayList<Course>();
    private List <String> courseList = new ArrayList <String>();
    Collection<Course> searchCourse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll_course);
        findAllViewsId();
        Intent in = getIntent();
        courseList = in.getStringArrayListExtra("course_list");
        Bundle b = in.getExtras();
        if(b!=null) {
            _admin = b.getString("Email");
        }

        ArrayAdapter<String> dataAdapter_course= new ArrayAdapter <String> (this, android.R.layout.simple_spinner_item, courseList);


        dataAdapter_course.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);


        spinnerCourse.setPrompt("Select Course");
        spinnerCourse.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        dataAdapter_course,
                        R.layout.spinner_row_nothing_selected,
                        this));

        spinnerCourse.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                System.out.println("Selected Course name"+_courseName);
                switch (parent.getId())
                {
                    case R.id.spinner_course:
                        if(parent.getItemAtPosition(position)!=null) {
                            _courseName=parent.getItemAtPosition(position).toString();
                            System.out.println("Selected Course name"+_courseName);

                        }
                    break;
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


        mEnrollCourseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (_courseName.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Choose the course correctly", Toast.LENGTH_SHORT).show();
                } else {
                    CourseEnrollmentTask tsk = new CourseEnrollmentTask();
                    tsk.execute();
                }
            }
        });

    }
    private void findAllViewsId() {


        spinnerCourse = (Spinner) findViewById(R.id.spinner_course);
        mEnrollCourseButton = (Button) findViewById(R.id.button_enrollCourse);

    }

    private class CourseEnrollmentTask extends AsyncTask<String, Void, Boolean>
    {

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean ok;
            User tempUser;
            User newUser;
            user = userService.findByEmailIdIgnoreCase(_admin);
            if (!user.isEmpty()) {
                tempUser = Iterables.getFirst(user, null);
                Vector<String> coursesEnrolled=new Vector<String>();
                coursesEnrolled.addAll(tempUser.getCourseEnrolled());
                if(coursesEnrolled.contains(_courseName)) {
                    enrollUser = true;
                    return enrollUser;
                }
                else {

                    coursesEnrolled.add(_courseName);
                    newUser = new User(tempUser.getStr(), tempUser.getStr(),
                            tempUser.getEmailId(), tempUser.getName(), tempUser.getCourse(), tempUser.getBranch(),
                            tempUser.getInstitution(),
                            tempUser.getSkills(),
                            coursesEnrolled,
                            tempUser.getCoursesOffering()
                    );
                    enrollUser=false;
                    ok = userService.addUser(newUser);
                    return ok;
                }
            }
            else
                return false;
        }
        @Override
        protected void onPostExecute(Boolean b)
        {
            if(!enrollUser) {
                System.out.println("hello");
                RegisteredStudentUpdateTask tsk = new RegisteredStudentUpdateTask();
                tsk.execute();
            }
            else {
                Toast.makeText(getApplicationContext(), "You are already registered in this course", Toast.LENGTH_LONG).show();
                System.out.println("Already registered");
            }
        }
    }
    private class RegisteredStudentUpdateTask extends AsyncTask<String, Void, Boolean>
    {

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean ok;
            Course tempCourse;
            Course newCourse;

            course = courseService.findByCourseNameIgnoreCase(_courseName);
            if (!course.isEmpty()) {
                tempCourse = Iterables.getFirst(course, null);

                Vector<String> numberOfStudentRegistered=new Vector<String>();

                numberOfStudentRegistered.addAll(tempCourse.getStudentRegistered());
                if(numberOfStudentRegistered.contains(_admin)) {
                    enrollCourse = true;
                    return enrollCourse;
                }
                else {

                    numberOfStudentRegistered.add(_admin);
                    enrollCourse=false;
                    newCourse = new Course(tempCourse.getStr(), tempCourse.getStr(), tempCourse.getCourseId(),
                            _courseName, tempCourse.getAdmin(), tempCourse.getOverview(),
                            tempCourse.getInstitution(),
                            tempCourse.getPreRequisites(),
                            tempCourse.getPostConditions(),
                            tempCourse.getInstructors(),
                            tempCourse.getCourseTA(),
                            numberOfStudentRegistered);
                    ok = courseService.addCourse(newCourse);
                    return ok;
                }
            }
            else
                return false;

        }
        @Override
        protected void onPostExecute(Boolean b)
        {
            if(!enrollCourse) {
                Toast.makeText(getApplicationContext(), "Jai mata Di!! Enrollment Done", Toast.LENGTH_LONG).show();
                System.out.println("test");
                Bundle bundle = new Bundle();
                bundle.putString("Email", _admin);
                Intent intent = new Intent(getApplicationContext(), CourseActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                onBackPressed();

                System.out.println("Enrollment done");
            }
            else
                Toast.makeText(getApplicationContext(), "You are already registered in this course", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


        switch (parent.getId())
        {
            case R.id.spinner_course:
                if(parent.getItemAtPosition(position)!=null) {
                    _courseName=parent.getItemAtPosition(position).toString();
                    System.out.println("Selected Course name"+_courseName);

                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_enroll_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
