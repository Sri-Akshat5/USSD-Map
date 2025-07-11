package com.application.map.ussd_map.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;


@Service
public class TwilioSmsService {

    private final String ACCOUNT_SID = "<ACCOUNT SID>";
    private final String AUTH_TOKEN = "<AUTH TOKEN>";
    private final String FROM_NUMBER = "<FROM NUMBER>";

    public String sendSms(String to, String msg) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(FROM_NUMBER),
                msg
        ).create();

        return message.getSid();
    }
}
