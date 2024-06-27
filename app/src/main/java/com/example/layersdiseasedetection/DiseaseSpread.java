package com.example.layersdiseasedetection;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class DiseaseSpread extends AppCompatActivity {

    private EditText etFlockNumber, etInitialInfected, etSimulationDays;
    private Button btnSimulate;

    LinearLayout LLholder;

    private TextView tvFinalInfected, tvFinalRecovered, tvFinalDead;
    private Spinner spVaccination;
    private LineChart lineChart;

    private double vaccinationRate = 0.0;
    private double deathRate = 0.001;
    private int numBirds;
    private int initialInfected;
    private double spreadRate = 0.3;
    private double recoveryRate = 0.1;
    private int days;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_spread);

        etFlockNumber = findViewById(R.id.etFlockNumber);
        etInitialInfected = findViewById(R.id.etInitialInfected);
        etSimulationDays = findViewById(R.id.etSimulationDays);
        btnSimulate = findViewById(R.id.btnSimulate);
        tvFinalInfected = findViewById(R.id.tvFinalInfected);
        tvFinalRecovered = findViewById(R.id.tvFinalRecovered);
        tvFinalDead = findViewById(R.id.tvFinalDead);
        spVaccination = findViewById(R.id.spinnerVaccination);
        lineChart = findViewById(R.id.lineChart);
        LLholder=findViewById(R.id.LLholder);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.vaccination_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spVaccination.setAdapter(adapter);
        spVaccination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selected = parentView.getItemAtPosition(position).toString();
                if (selected.equals("Yes")) {
                    vaccinationRate = 1.0;
                    deathRate = 0.001;
                } else {
                    vaccinationRate = 0.0;
                    deathRate = 0.09;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                vaccinationRate = 0.0;
            }
        });

        btnSimulate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    numBirds = Integer.parseInt(etFlockNumber.getText().toString());
                    initialInfected = Integer.parseInt(etInitialInfected.getText().toString());
                    days = Integer.parseInt(etSimulationDays.getText().toString());

                    SimulationResult result = simulateDiseaseSpread(numBirds, initialInfected, spreadRate, recoveryRate, vaccinationRate, days, deathRate);

                    tvFinalInfected.setText(String.valueOf((int) result.finalInfected));
                    tvFinalRecovered.setText(String.valueOf((int) result.finalRecovered));
                    tvFinalDead.setText(String.valueOf((int) result.finalDead));


                    LLholder.setVisibility(View.VISIBLE);


                    Toast.makeText(DiseaseSpread.this, "Simulation Complete", Toast.LENGTH_SHORT).show();

                    // Call drawGraph method after simulation
                    drawGraph(result);

                } catch (NumberFormatException e) {
                    Toast.makeText(DiseaseSpread.this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
                    LLholder.setVisibility(View.GONE);
                }
            }
        });
    }

    public static class SimulationResult {
        double finalInfected;
        double finalRecovered;
        double finalDead;

        public SimulationResult(double finalInfected, double finalRecovered, double finalDead) {
            this.finalInfected = finalInfected;
            this.finalRecovered = finalRecovered;
            this.finalDead = finalDead;
        }
    }

    public static SimulationResult simulateDiseaseSpread(int numBirds, int initialInfected, double spreadRate, double recoveryRate, double vaccinationRate, int days, double deathRate) {
        double susceptible = numBirds - initialInfected;
        double infected = initialInfected;
        double recovered = 0;
        double dead = 0;

        // Apply vaccination
        double vaccinated = vaccinationRate * susceptible;
        susceptible -= vaccinated;

        for (int day = 0; day < days; day++) {
            // Calculate the number of new infections, recoveries, and deaths
            double newInfected = spreadRate * susceptible * infected / numBirds;
            double newRecovered = recoveryRate * infected;
            double newDeaths = deathRate * infected;

            // Update the states
            susceptible -= newInfected;
            infected += newInfected - newRecovered - newDeaths;
            recovered += newRecovered;
            dead += newDeaths;
        }

        return new SimulationResult(infected, recovered, dead);
    }

    public void drawGraph(SimulationResult result) {
        List<Entry> entriesInfected = new ArrayList<>();
        List<Entry> entriesRecovered = new ArrayList<>();
        List<Entry> entriesDead = new ArrayList<>();

        // Example data, replace with your simulation result values
        for (int i = 0; i < days; i++) {
            // Use the class-level fields directly here
            SimulationResult dailyResult = simulateDiseaseSpread(numBirds, initialInfected, spreadRate, recoveryRate, vaccinationRate, i + 1, deathRate);

            entriesInfected.add(new Entry(i, (float) dailyResult.finalInfected));
            entriesRecovered.add(new Entry(i, (float) dailyResult.finalRecovered));
            entriesDead.add(new Entry(i, (float) dailyResult.finalDead));
        }

        LineDataSet dataSetInfected = new LineDataSet(entriesInfected, "Infected");
        dataSetInfected.setColor(Color.BLUE);
        dataSetInfected.setCircleColor(Color.BLUE);
        dataSetInfected.setCircleRadius(3f);
        dataSetInfected.setLineWidth(2f);
        dataSetInfected.setValueTextSize(10f);

        LineDataSet dataSetRecovered = new LineDataSet(entriesRecovered, "Recovered");
        dataSetRecovered.setColor(Color.GREEN);
        dataSetRecovered.setCircleColor(Color.GREEN);
        dataSetRecovered.setCircleRadius(3f);
        dataSetRecovered.setLineWidth(2f);
        dataSetRecovered.setValueTextSize(10f);

        LineDataSet dataSetDead = new LineDataSet(entriesDead, "Dead");
        dataSetDead.setColor(Color.RED);
        dataSetDead.setCircleColor(Color.RED);
        dataSetDead.setCircleRadius(3f);
        dataSetDead.setLineWidth(2f);
        dataSetDead.setValueTextSize(10f);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSetInfected);
        dataSets.add(dataSetRecovered);
        dataSets.add(dataSetDead);

        LineData lineData = new LineData(dataSets);

        // Retrieve existing LayoutParams and modify them
        ViewGroup.LayoutParams layoutParams = lineChart.getLayoutParams();
        layoutParams.height = 800; // Adjust height as needed
        lineChart.setLayoutParams(layoutParams);

        lineChart.setData(lineData);
        lineChart.invalidate();

        LLholder.setVisibility(View.VISIBLE);
    }
}
