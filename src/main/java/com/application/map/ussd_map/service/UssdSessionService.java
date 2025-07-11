package com.application.map.ussd_map.service;

import com.application.map.ussd_map.model.UssdSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@RequiredArgsConstructor
public class UssdSessionService {

    private final Map<String, UssdSession> store = new HashMap<>();

    

    public UssdSession getOrCreateSession(String id, String phone) {
        return store.computeIfAbsent(id, k -> {
            UssdSession s = new UssdSession();
            s.setSessionId(id);
            s.setPhoneNumber(phone);
            s.setStep(0);
            s.setStarted(false);
            return s;
        });
    }

    public void updateStep(String sessionId, int step) {
        UssdSession s = store.get(sessionId);
        if (s != null) {
            s.setStep(step);
            s.setStarted(true);
        }
    }

    /* ───────────────────── Parsing helpers ───────────────────── */

    /** Return the last token after the final '*' in a USSD text string. */
    public String extractLast(String text) {
        if (text == null || text.isBlank()) return "";
        String[] parts = text.split("\\*");
        return parts[parts.length - 1];
    }

   

   
    public String joinStepsForUssd(List<String> steps) {
        final int LIMIT = 160;        // safest USSD limit
        StringBuilder sb = new StringBuilder();
        for (String step : steps) {
            if (sb.length() + step.length() + 1 > LIMIT) { // +1 for '\n'
                sb.append("...");
                break;
            }
            if (sb.length() != 0) sb.append('\n');
            sb.append(step);
        }
        return sb.toString();
    }

    
    public List<String> splitForTwilioTrial(String fullMessage) {
        final int LIMIT = 160;   
        List<String> chunks = new ArrayList<>();
        int i = 0;
        while (i < fullMessage.length()) {
            int end = Math.min(i + LIMIT, fullMessage.length());
            chunks.add(fullMessage.substring(i, end));
            i = end;
        }
        return chunks;
    }
}
