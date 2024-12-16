package com.capstone3.GroupAppoint.appointment.service;

import com.capstone3.GroupAppoint.appointment.dto.OngoingParticipantArrivalDto;
import com.capstone3.GroupAppoint.appointment.entity.Appointment;
import com.capstone3.GroupAppoint.appointment.entity.Participant;
import com.capstone3.GroupAppoint.appointment.repository.AppointmentRepository;
import com.capstone3.GroupAppoint.appointment.repository.ParticipantRepository;
import com.capstone3.GroupAppoint.game.GameParticipantDto;
import com.capstone3.GroupAppoint.kakao.auth.entity.User;
import com.capstone3.GroupAppoint.kakao.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantRepository participantRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;


    @Override
    public void addParticipant(User user, Appointment appointment, boolean host) {
        Participant participant = Participant.builder()
                .user(user)
                .appointment(appointment)
                .isArrived(false)
                .isHost(host)
                .build();
        participantRepository.save(participant);
        log.info("{} 님이 참가했습니다", user.getUserId());
    }

    @Override
    public void updateParticipant(Appointment appointment, String newParticipantIds) {
        // 이전 참가자 목록 조회
        List<Participant> oldParticipants  = participantRepository.findByAppointment(appointment);

        // 이전 참가자들의 ID 집합
        Set<Long> oldParticipantIds = oldParticipants.stream()
                .map(participant -> participant.getUser().getUserId())
                .collect(Collectors.toSet());

        // 새로운 참가자 ID 목록 파싱
        List<Long> newParticipantIdlist = new ArrayList<>();
        if (newParticipantIds != null && !newParticipantIds.isEmpty()) {
            newParticipantIdlist = Arrays.stream(newParticipantIds.split(","))
                    .map(Long::parseLong)
                    .toList();
        }

        // 새로 추가해야 할 참가자 리스트 생성
        List<Participant> participantsToAdd = new ArrayList<>();
        for (Long newParticipantId : newParticipantIdlist) {
            // 기존 참가자가 아니라면 추가
            if (!oldParticipantIds.contains(newParticipantId)) {
                // 새로운 참가자 객체 생성
                User newUser = userRepository.findById(newParticipantId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다.: " + newParticipantId));
                Participant newParticipant = new Participant();
                newParticipant.setAppointment(appointment);
                newParticipant.setUser(newUser);
                newParticipant.setIsArrived(false);
                newParticipant.setIsHost(false);
                participantsToAdd.add(newParticipant);
                log.info("약속 참가자 추가 : {} " ,newUser.getUserId());
            }
        }

        // 제거해야 할 참가자 리스트 생성
        List<Participant> participantsToRemove = new ArrayList<>();
        for (Participant oldParticipant : oldParticipants) {
            // 새로운 참가자 목록에 없는 참가자이고 방장이 아닐 경우 삭제 대상에 추가
            if (!newParticipantIdlist.contains(oldParticipant.getUser().getUserId())
                    && !oldParticipant.getIsHost()) {
                participantsToRemove.add(oldParticipant);
                log.info("약속 참가자 삭제 : {} " ,oldParticipant.getUser().getUserId());
            }

        }

        // 제거 대상 삭제, 추가 대상 추가
        participantRepository.deleteAll(participantsToRemove);
        participantRepository.saveAll(participantsToAdd);
    }

    @Override
    public boolean findIsHostofAppointment(Appointment appointment, User user) {
        Participant participant = participantRepository.findByAppointmentAndUser(appointment, user)
                .orElseThrow(() -> new IllegalArgumentException("참가자가 아닙니다."));

        return participant.getIsHost();
    }


    @Override
    public List<OngoingParticipantArrivalDto> getParticipantsWithEstimatedArrival(Long appointmentId) {
        // 약속 정보 조회
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약속 정보가 존재하지 않습니다."));

        // 참여자 리스트 가져오기
        List<Participant> participants = participantRepository.findByAppointment(appointment);

        // DTO로 변환하며 도착 예정 시간을 설정
        return participants.stream()
                .map(participant -> {
                    OngoingParticipantArrivalDto participantDto = new OngoingParticipantArrivalDto();
                    participantDto.setUserId(participant.getUser().getUserId());
                    participantDto.setProfile(participant.getUser().getProfile());
                    participantDto.setIsHost(participant.getIsHost());
                    participantDto.setIsArrived(participant.getIsArrived());
                    participantDto.setIsOnTime(checkIsOnTime(participant.getUser().getAccumulatedTime()));

                    // 외부 API 또는 별도 로직으로 도착 예정 시간 가져오기
                    String estimatedArrivalTime = getEstimatedArrivalTime(participant.getUser().getUserId(), appointment);
                    participantDto.setEstimatedArrivalTime(estimatedArrivalTime);

                    return participantDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public String getEstimatedArrivalTime(Long userId, Appointment appointment) {
        //Naver api 설계
        return "2024-11-27T15:30:00";
    }

    @Override
    public List<GameParticipantDto> calculateLateTimesAndGetLateParticipants(LocalTime appointmentTime) {
        List<Participant> allParticipants = participantRepository.findAll();

        return allParticipants.stream()
                .filter(participant -> {
                    // 지각 시간 계산
                    if (participant.getArrivalTime() != null) {
                        long lateTime = Duration.between(appointmentTime, participant.getArrivalTime()).toMinutes();
                        if (lateTime > 0) {
                            participant.setLateTime(lateTime); // 지각 시간 업데이트
                            participantRepository.save(participant);
                            return true; // 지각한 참가자만 필터링
                        }
                    }
                    return false;
                })
                .map(participant -> new GameParticipantDto(
                        participant.getUser().getUserId(),
                        participant.getLateTime()
                ))
                .collect(Collectors.toList());

    }

    public Boolean checkIsOnTime(int arrivalTime){
        boolean isOnTime = false;

        if(arrivalTime < 0){
            isOnTime = true;
        }

        return isOnTime;
    }

}
