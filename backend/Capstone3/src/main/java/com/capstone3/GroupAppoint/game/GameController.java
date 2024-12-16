package com.capstone3.GroupAppoint.game;

import com.capstone3.GroupAppoint.appointment.service.ParticipantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/game")
public class GameController {
    private ParticipantService participantService;

    @GetMapping("/participants")
    public List<GameParticipantDto> getLateParticipants(@RequestParam("appointmentTime") LocalTime appointmentTime) {
        return participantService.calculateLateTimesAndGetLateParticipants(appointmentTime);
    }
}
