package com.example.layersdiseasedetection;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class DiseaseSpread extends AppCompatActivity {

    private EditText etFlockNumber, etInitialInfected, etSimulationDays;
    private Button btnSimulate;
    private TextView tvFinalInfected, tvFinalRecovered, tvFinalDead;
    private Spinner spVaccination;
    private double vaccinationRate = 0.0;
    private double deathRate  = 0.001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_disease_spread);

        etFlockNumber = findViewById(R.id.etFlockNumber);
        etInitialInfected = findViewById(R.id.etInitialInfected);
        etSimulationDays = findViewById(R.id.etSimulationDays);
        btnSimulate = findViewById(R.id.btnSimulate);
        tvFinalInfected = findViewById(R.id.tvFinalInfected);
        tvFinalRecovered = findViewById(R.id.tvFinalRecovered);
        tvFinalDead = findViewById(R.id.tvFinalDead);
        spVaccination = findViewById(R.id.spinnerVaccination);

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
                    deathRate=0.00;
                } else {
                    vaccinationRate = 0.0;
                    deathRate=0.09;
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
                    int numBirds = Integer.parseInt(etFlockNumber.getText().toString());
                    int initialInfected = Integer.parseInt(etInitialInfected.getText().toString());
                    int days = Integer.parseInt(etSimulationDays.getText().toString());

                    double spreadRate = 0.3;
                    double recoveryRate = 0.1;


                    SimulationResult result = simulateDiseaseSpread(numBirds, initialInfected, spreadRate, recoveryRate, vaccinationRate, days, deathRate);

                    tvFinalInfected.setText(String.valueOf((int) result.finalInfected));
                    tvFinalRecovered.setText(String.valueOf((int) result.finalRecovered));
                    tvFinalDead.setText(String.valueOf((int) result.finalDead));

                    Toast.makeText(DiseaseSpread.this, "Simulation Complete", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(DiseaseSpread.this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
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
}
