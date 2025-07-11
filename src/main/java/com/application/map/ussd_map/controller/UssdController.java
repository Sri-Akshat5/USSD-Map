package com.application.map.ussd_map.controller;

import com.application.map.ussd_map.model.UssdSession;
import com.application.map.ussd_map.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UssdController {

    private final UssdSessionService sessionService;
    private final DirectionService   directionService;
    private final TwilioSmsService   twilioSmsService;

    @PostMapping(value = "/ussd", consumes = "application/x-www-form-urlencoded")
    public String handleUssd(@RequestParam Map<String, String> params) {

        String sessionId   = params.get("sessionId");
        String phoneNumber = params.get("phoneNumber");
        String text        = params.getOrDefault("text", "");

        /* fetch or create session */
        UssdSession session = sessionService.getOrCreateSession(sessionId, phoneNumber);

        /* ───── Step‑0 : first contact ───── */
        if (!session.isStarted()) {
            sessionService.updateStep(sessionId, 1);
            return "CON Welcome to RouteFinder\n1. Get Directions\n2. Exit";
        }

        switch (session.getStep()) {

            /* ── Menu choice ── */
            case 1 -> {
                if (text.endsWith("1")) {
                    sessionService.updateStep(sessionId, 2);
                    return "CON Enter Starting Location:";
                }
                return "END Thank you for using RouteFinder.";
            }

            /* ── Capture FROM ── */
            case 2 -> {
                session.setFromLocation(sessionService.extractLast(text));
                sessionService.updateStep(sessionId, 3);
                return "CON Enter Destination:";
            }

            /* ── Capture TO & ask delivery method ── */
            case 3 -> {
                session.setToLocation(sessionService.extractLast(text));
                sessionService.updateStep(sessionId, 4);
                return """
                       CON How would you like the directions?
                       1. Show step‑by‑step
                       2. Send full SMS
                       """;
            }

            /* ── Handle delivery choice ── */
            case 4 -> {
                String choice = sessionService.extractLast(text);
                try {
                    List<String> steps = directionService.getAllSteps(
                            session.getFromLocation(), session.getToLocation());

                    /* 1️⃣  Pagination path */
                    if ("1".equals(choice)) {
                        session.setDirectionSteps(steps);
                        session.setCurrentStepIndex(0);
                        sessionService.updateStep(sessionId, 5);   // go to paging mode
                        return "CON " + steps.get(0) +
                               "\n1. Next\n2. Exit";
                    }

                    /* 2️⃣  SMS path */
                    if ("2".equals(choice)) {
                        String smsBody = "RouteFinder:\n" +
                                         String.join("\n", steps) +
                                         "\n\nHappy journey! 🧭";
                        twilioSmsService.sendSms(phoneNumber, smsBody);
                        return "END Full directions sent via SMS.";
                    }
                    return "END Invalid choice.";
                } catch (Exception e) {
                    e.printStackTrace();
                    return "END Unable to fetch directions. Please try again later.";
                }
            }

            /* ── Pagination mode (Next / Exit) ── */
            case 5 -> {
                String input = sessionService.extractLast(text);   // 1 = next, 2 = exit
                List<String> steps = session.getDirectionSteps();
                int index = session.getCurrentStepIndex();

                if ("2".equals(input)) {         // user chose Exit
                    return "END Journey ended. Safe travels!";
                }

                // default to "Next"
                index++;
                if (index >= steps.size()) {
                    return "END ✅ You’ve reached your destination!";
                }
                session.setCurrentStepIndex(index);
                return "CON " + steps.get(index) +
                       "\n1. Next\n2. Exit";
            }

            default -> {
                return "END Invalid session. Please retry.";
            }
        }
    }
}
