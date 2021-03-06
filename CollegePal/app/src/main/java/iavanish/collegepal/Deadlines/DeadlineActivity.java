package iavanish.collegepal.Deadlines;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;

import iavanish.collegepal.Courses.Course;
import iavanish.collegepal.Courses.CourseActivity;
import iavanish.collegepal.Courses.CourseClientApi;
import iavanish.collegepal.Courses.UpdateCourse;
import iavanish.collegepal.R;
import retrofit.RestAdapter;

public class DeadlineActivity extends Activity {

    private final String URL = "http://192.168.55.74:8080";
    private CourseClientApi courseService = new RestAdapter.Builder()
            .setEndpoint(URL).setLogLevel(RestAdapter.LogLevel.FULL).build()
            .create(CourseClientApi.class);
    private DeadlineClientApi deadlineService = new RestAdapter.Builder()
            .setEndpoint(URL).setLogLevel(RestAdapter.LogLevel.FULL).build()
            .create(DeadlineClientApi.class);
    Button button_addDeadline, button_viewDeadline;
    TextView txt_viewDeadline;
    String _courseName,_emailId, _courseID, _deadlineId, _deadlineDate,_deadlineType, _deadlineDetails,_admin;
    Collection<Deadline> deadline;
    Collection<Course> course, searchAdmin;
    private ArrayList<Course> mListCourse = new ArrayList<Course>();
    StringBuilder sb = new StringBuilder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deadline);
        findAllViewsId();
        Intent in = getIntent();
        Bundle b = in.getExtras();
        if(b!=null) {
            _courseName = b.getString("CourseName");
            _emailId = b.getString("Email");
        }
        button_addDeadline.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Resource Code
                //Course check

                CourseCheckTask tsk = new CourseCheckTask();
                tsk.execute();

            }
        });
        button_viewDeadline.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Resource Code
                txt_viewDeadline.setText("");
                ViewDeadlineTask tsk = new ViewDeadlineTask();
                tsk.execute();

            }
        });


    }
    private void findAllViewsId() {

        txt_viewDeadline =(TextView) findViewById(R.id.textView_viewDeadline);
        button_addDeadline =(Button) findViewById(R.id.button_addDeadline);
        button_viewDeadline =(Button) findViewById(R.id.button_viewDeadline);

    }
    private class ViewDeadlineTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean ok;
            Course tempCourse;
            course = courseService.findByCourseNameIgnoreCase(_courseName);
            if (!course.isEmpty()) {
                tempCourse = Iterables.getFirst(course, null);
                _courseID = tempCourse.getCourseId();
                System.out.println("CourseId"+_courseID);

            }

            Boolean appendSeparator = false;
            Deadline tempDeadline;
            deadline = deadlineService.findByCourseIdIgnoreCase(_courseID);
            if (!deadline.isEmpty()) {
                for (int i = 0; i < deadline.size(); i++) {
                    tempDeadline = Iterables.get(deadline, i);
                    _courseID = tempDeadline.getCourseId();
                    _deadlineId = tempDeadline.getDeadlineId();
                    _emailId = tempDeadline.getEmailId();
                    _deadlineDate = tempDeadline.getDate();
                    _deadlineDetails = tempDeadline.getDeadlineDetails();
                    _deadlineType = tempDeadline.getDeadlineType();
                    if (appendSeparator)
                        sb.append(',');
                    appendSeparator = true;
                    sb.append(_courseID);
                    sb.append(_deadlineId);
                    sb.append(_emailId);
                    sb.append(_deadlineDate);
                    sb.append(_deadlineDate);
                    sb.append(_deadlineType);
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean b)
        {
            /*if(b) {*/
            //txt_viewDeadline.setText(txt_viewDeadline.getText()+","+_deadlineId+","+_courseID+","+_emailId+","+_deadlineDate+","+_deadlineDetails+","+_deadlineType);
               txt_viewDeadline.setText(sb);
            Toast.makeText(getApplicationContext(), "Jai mata Di Done", Toast.LENGTH_LONG).show();
            //}
            System.out.println("Display Deadline Done");
        }
    }

    private class CourseCheckTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            Course tempCourse;
            course = courseService.findByCourseNameIgnoreCase(_courseName);
            if (!course.isEmpty()) {
                tempCourse = Iterables.getFirst(course, null);
                _courseID = tempCourse.getCourseId();
                _admin=tempCourse.getAdmin();
                System.out.println("CourseId"+_courseID);
                System.out.println("Admin"+_admin);
                return true;
            } else {

                return false;
            }

            //searchAdmin = courseService.findByAdminContainingIgnoreCase(_emailId);
            /*
            if (searchAdmin.isEmpty()) {
                return false;
            } else {

                return true;
            }*/

        }

        @Override
        protected void onPostExecute(Boolean b) {
            if (b) {
               /* if (!searchAdmin.isEmpty()) {
                    if (!mListCourse.containsAll(searchAdmin))
                        mListCourse.addAll(searchAdmin);


                }
                int position = 0;
                Boolean flag=false;
                //_courseID=txtCourseId.getText().toString();
                while (mListCourse.size() > position) {

                    Course currentCourse = mListCourse.get(position);
                    if(_courseID.equalsIgnoreCase(currentCourse.getCourseId()))
                        flag=true;
                    else
                        flag=false;
                    position++;
                }*/
                System.out.println("Email"+_emailId);
                System.out.println("Admin"+_admin);
                if(_emailId.equalsIgnoreCase(_admin)){

                    Bundle bundle = new Bundle();
                    bundle.putString("CourseId",  _courseID);
                    bundle.putString("Email",  _emailId);
                    Intent intent = new Intent(getApplicationContext(), AddDeadline.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else
                    Toast.makeText(getApplicationContext(), "You can't add Deadline, You are not admin!!", Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(getApplicationContext(), "You can't add Deadline now", Toast.LENGTH_LONG).show();

            System.out.print("Search Profile");
        }
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
        getMenuInflater().inflate(R.menu.menu_deadline, menu);
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
