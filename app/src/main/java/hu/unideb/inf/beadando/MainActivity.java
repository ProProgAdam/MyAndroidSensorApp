package hu.unideb.inf.beadando;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MainActivity extends AppCompatActivity {

    TextView txt_jelenlegiGyors, txt_elozoGyorsitas, txt_gyorsulas;
    ProgressBar prog_shakeMeter;


    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private double gyorsulasJelenlegiErteke;
    private double gyorsulasElozoErteke;
    private double gyorsulasMax;
    private Viewport viewport;

    float MAXx = 0;
    float MAXy = 0;
    float MAXz = 0;

    //grafikon
    private int pointsPlotted = 5;
    private int graphIntervalCounter = 0;
    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0, 1),
            new DataPoint(1, 5),
            new DataPoint(2, 3),
            new DataPoint(3, 2),
            new DataPoint(4, 6)
    });

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];






            gyorsulasJelenlegiErteke = Math.sqrt((x*x + y*y + z*z));
            if((gyorsulasJelenlegiErteke) < (Math.sqrt( (MAXx*MAXx + MAXy*MAXy + MAXz*MAXz)))  )
            {
                gyorsulasElozoErteke = Math.sqrt( (MAXx*MAXx + MAXy*MAXy + MAXz*MAXz));
            }

            double gyorsulasValtozasMerteke = Math.abs(gyorsulasJelenlegiErteke - gyorsulasElozoErteke);

            //update
            txt_jelenlegiGyors.setText("Jelenlegi = " +  gyorsulasJelenlegiErteke);
            txt_gyorsulas.setText("                                         ");
            txt_elozoGyorsitas.setText("Gyorsulás mértéke");
            //txt_elozoGyorsitas.setText("Elozo = " +  gyorsulasElozoErteke);
            //txt_gyorsulas.setText("Gyorsulas merteke =" +  gyorsulasValtozasMerteke);

            prog_shakeMeter.setProgress((int) gyorsulasValtozasMerteke);



            if(gyorsulasValtozasMerteke > 12)
            {
                txt_gyorsulas.setBackgroundColor(Color.RED);
            }
            else if (gyorsulasValtozasMerteke > 7)
            {
                txt_gyorsulas.setBackgroundColor(Color.GREEN);
            }
            else if (gyorsulasValtozasMerteke > 3)
            {
                txt_gyorsulas.setBackgroundColor(Color.BLUE);
            }
            else{
                txt_gyorsulas.setBackgroundColor(getResources().getColor(R.color.design_default_color_background));
            }

            //grafikon folyt
            pointsPlotted++;
            series.appendData(new DataPoint(pointsPlotted, gyorsulasValtozasMerteke), true, pointsPlotted);
            viewport.setMaxX(pointsPlotted);
            viewport.setMinX(pointsPlotted - 200);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_gyorsulas = findViewById(R.id.txt_gyorsit);
        txt_jelenlegiGyors = findViewById(R.id.txt_jelenlegiGyors);
        txt_elozoGyorsitas = findViewById(R.id.txt_elozoGyorsitas);
        prog_shakeMeter = findViewById(R.id.prog_shakeMeter);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        viewport = graph.getViewport();
        viewport.setScrollable(true);
        viewport.setXAxisBoundsManual(true);
        graph.addSeries(series);
    }




    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }
}