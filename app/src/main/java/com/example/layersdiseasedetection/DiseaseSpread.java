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
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class DiseaseSpread extends AppCompatActivity {

    private EditText etFlockNumber, etInitialInfected, etSimulationDays;
    private Button btnSimulate;

    private LinearLayout LLholder;

    private TextView tvFinalInfected, tvFinalRecovered, tvFinalDead,tvdisease;
    private Spinner spVaccination;
    private LineChart lineChart;

    private double vaccinationRate = 0.0;
    private double deathRate;
    private int numBirds;
    private int initialInfected;
    private double spreadRate;
    private double recoveryRate;
    private int days;
    private String selected;
    private Integer results;

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
        LLholder = findViewById(R.id.LLholder);
        tvdisease=findViewById(R.id.tvPredicted);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.vaccination_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spVaccination.setAdapter(adapter);

        // Get the predicted disease
        results = getIntent().getIntExtra("result", 0);

        // Set default rates based on the disease prediction
        if (results == 0) { // coccidiosis
            tvdisease.setText("coccidiosis");
            spreadRate = 0.01;
            deathRate = 0.5;
            recoveryRate = 0.01;
        } else if (results == 2) { // newcastle
            tvdisease.setText("Newcastle Disease virus");
            spreadRate = 0.05;
            deathRate = 0.02;
            recoveryRate = 0.04;
        } else if (results == 3) { // salmonella
            tvdisease.setText("Salmonella Diseases");
            spreadRate = 0.01;
            deathRate = 0.03;
            recoveryRate = 0.06;
        }

        spVaccination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selected = parentView.getItemAtPosition(position).toString();

                if (selected.equals("Yes")) {
                    if (results == 0) { // coccidiosis
                        tvdisease.setText("coccidiosis");
                        spreadRate = 0.001;
                        deathRate = 0.0002;
                        recoveryRate = 0.5;
                    } else if (results == 2) { // newcastle
                        tvdisease.setText("Newcastle Disease virus");
                        spreadRate = 0.005;
                        deathRate = 0.002;
                        recoveryRate = 0.4;
                    } else if (results == 3) { // salmonella
                        tvdisease.setText("Salmonella Diseases");
                        spreadRate = 0.001;
                        deathRate = 0.003;
                        recoveryRate = 0.6;
                    }
                } else if (selected.equals("No")) {
                    if (results == 0) { // coccidiosis
                        spreadRate = 0.01;
                        deathRate = 0.5;
                        recoveryRate = 0.01;
                    } else if (results == 2) { // newcastle
                        spreadRate = 0.05;
                        deathRate = 0.02;
                        recoveryRate = 0.04;
                    } else if (results == 3) { // salmonella
                        spreadRate = 0.01;
                        deathRate = 0.03;
                        recoveryRate = 0.06;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Default behavior if nothing is selected
            }
        });

        btnSimulate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etFlockNumber.getText().toString().equals("")) {
                    etFlockNumber.setError("Fill these empty");
                    etFlockNumber.requestFocus();
                } else if (etInitialInfected.getText().toString().equals("")) {
                    etFlockNumber.setError("Fill these empty");
                    etFlockNumber.requestFocus();
                } else if (etSimulationDays.getText().toString().equals("")) {
                    etFlockNumber.setError("Fill these empty");
                    etFlockNumber.requestFocus();
                } else {
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

            }
        });
    }

    public static class SimulationResult {
        double finalInfected;
        double finalRecovered;
        double finalDead;

        public SimulationResult(double finalInfected, double finalRecovered, double finalDead) {
            this.finalInfected = finalInfected;
            this.finalRecovered = finalRecovered + 1;
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

        for (int i = 0; i < days; i++) {
            SimulationResult dailyResult = simulateDiseaseSpread(numBirds, initialInfected, spreadRate, recoveryRate, vaccinationRate, i + 1, deathRate);

            entriesInfected.add(new Entry(i, (float) dailyResult.finalInfected));
            entriesRecovered.add(new Entry(i, (float) dailyResult.finalRecovered));
            entriesDead.add(new Entry(i, (float) dailyResult.finalDead));
        }

        LineDataSet dataSetInfected = new LineDataSet(entriesInfected, "Infected");
        dataSetInfected.setColor(Color.BLUE);
        dataSetInfected.setCircleColor(Color.BLUE);
        dataSetInfected.setCircleHoleColor(Color.BLUE);
        dataSetInfected.setCircleRadius(4f);
        dataSetInfected.setLineWidth(2f);
        dataSetInfected.setDrawValues(false);
        dataSetInfected.setDrawCircles(true);

        LineDataSet dataSetRecovered = new LineDataSet(entriesRecovered, "Recovered");
        dataSetRecovered.setColor(Color.GREEN);
        dataSetRecovered.setCircleColor(Color.GREEN);
        dataSetRecovered.setCircleHoleColor(Color.GREEN);
        dataSetRecovered.setCircleRadius(2f);
        dataSetRecovered.setLineWidth(2f);
        dataSetRecovered.setDrawValues(false);
        dataSetRecovered.setDrawCircles(true);

        LineDataSet dataSetDead = new LineDataSet(entriesDead, "Dead");
        dataSetDead.setColor(Color.RED);
        dataSetDead.setCircleColor(Color.RED);
        dataSetDead.setCircleHoleColor(Color.RED);
        dataSetDead.setCircleRadius(2f);
        dataSetDead.setLineWidth(2f);
        dataSetDead.setDrawValues(false);
        dataSetDead.setDrawCircles(true);

        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSetInfected);
        dataSets.add(dataSetRecovered);
        dataSets.add(dataSetDead);

        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);
        lineChart.invalidate();

        Description description = new Description();
        description.setText("Disease Spread Over Time");
        lineChart.setDescription(description);
    }
}
