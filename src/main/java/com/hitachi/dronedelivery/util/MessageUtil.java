package com.hitachi.dronedelivery.util;

import com.hitachi.dronedelivery.constant.Message;
import com.hitachi.dronedelivery.enums.Model;
import com.hitachi.dronedelivery.enums.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageUtil {
    private static MessageSource messageSource;

    @Autowired
    public MessageUtil(MessageSource messageSource) {
        MessageUtil.messageSource = messageSource;
    }

    public static String getMessage(String code) {
        return messageSource.getMessage(code, null, Locale.ENGLISH);
    }

    public static String getMessage(String code, String[] args) {
        return messageSource.getMessage(code, args, Locale.ENGLISH);
    }

    public static String getDroneStatusMessage(State state, int batteryPercentage) {
        if (state.equals(State.IDLE) && batteryPercentage >= 25) {
            return messageSource.getMessage(Message.DRONE_AVAILABLE, null, Locale.ENGLISH);
        } else {
            return messageSource.getMessage(Message.DRONE_NOT_AVAILABLE, new String[]{ state.name() }, Locale.ENGLISH);
        }
    }
}
