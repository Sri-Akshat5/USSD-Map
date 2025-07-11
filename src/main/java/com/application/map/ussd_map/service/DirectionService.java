package com.application.map.ussd_map.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.application.map.ussd_map.entity.LocationRequest;
import com.application.map.ussd_map.repository.LocationRequestRepository;

import java.util.ArrayList;
import java.util.List;

import org.json.*;

@Service
@RequiredArgsConstructor
public class DirectionService {

    @Value("${google.api.key}")
    private String apiKey;

    private final LocationRequestRepository locationRepo;

    public String getFirstDirectionStep(String from, String to, String sessionId, String phoneNumber) {
        try {
            long start = System.currentTimeMillis(); 

            String url = String.format(
                "https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&key=%s",
                from.replace(" ", "+"),
                to.replace(" ", "+"),
                apiKey
            );

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            JSONObject json = new JSONObject(response);

            if (!json.getString("status").equals("OK")) {
                return "END Unable to find directions. Try again.";
            }

            JSONArray steps = json.getJSONArray("routes")
                                  .getJSONObject(0)
                                  .getJSONArray("legs")
                                  .getJSONObject(0)
                                  .getJSONArray("steps");

            JSONObject firstStep = steps.getJSONObject(0);
            String instruction = firstStep.getString("html_instructions").replaceAll("<[^>]*>", "");
            String distance = firstStep.getJSONObject("distance").getString("text");

          
            LocationRequest request = LocationRequest.builder()
                    .sessionId(sessionId)
                    .phoneNumber(phoneNumber)
                    .fromLocation(from)
                    .toLocation(to)
                    .stepCount(steps.length())
                    .responseTime((System.currentTimeMillis() - start) / 1000F)
                    .build();
            locationRepo.save(request);

            return "Step 1: " + instruction + " (" + distance + ")\n1. Next\n2. Exit";

        } catch (Exception e) {
            return "END Something went wrong. Please try again.";
        }
    }

    public List<String> getAllSteps(String from, String to) throws Exception {
        String url = String.format(
            "https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&key=%s",
            from.replace(" ", "+"),
            to.replace(" ", "+"),
            apiKey
        );
        


        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        JSONObject json = new JSONObject(response);

        if (!json.getString("status").equals("OK")) {
            throw new RuntimeException("Google API Error");
        }
        

        JSONArray steps = json.getJSONArray("routes")
                              .getJSONObject(0)
                              .getJSONArray("legs")
                              .getJSONObject(0)
                              .getJSONArray("steps");

        List<String> directions = new ArrayList<>();

        for (int i = 0; i < steps.length(); i++) {
            JSONObject step = steps.getJSONObject(i);
            String instruction = step.getString("html_instructions").replaceAll("<[^>]*>", "");
            String distance = step.getJSONObject("distance").getString("text");
            directions.add("Step " + (i + 1) + ": " + instruction + " (" + distance + ")");
        }
      

        return directions;
    }
}
