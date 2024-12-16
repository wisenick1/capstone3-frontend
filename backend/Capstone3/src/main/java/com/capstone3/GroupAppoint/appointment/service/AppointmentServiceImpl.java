package com.capstone3.GroupAppoint.appointment.service;

import com.capstone3.GroupAppoint.appointment.dto.*;
import com.capstone3.GroupAppoint.appointment.entity.Appointment;
import com.capstone3.GroupAppoint.appointment.entity.Participant;
import com.capstone3.GroupAppoint.appointment.repository.AppointmentRepository;
import com.capstone3.GroupAppoint.appointment.repository.ParticipantRepository;
import com.capstone3.GroupAppoint.kakao.auth.entity.User;
import com.capstone3.GroupAppoint.kakao.auth.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final UserService userService;
    private final AppointmentRepository appointmentRepository;
    private final ParticipantService participantService;
    private final ParticipantRepository participantRepository;

    @Override
    public Long createAppointment(Long userId, String title, LocalDate appointmentDate, LocalTime appointmentTime, Double latitude, Double longitude, String location, String address, String participantIds) {
        User user = userService.findByUserId(userId);

        Appointment appointment = appointmentRepository.save(
                Appointment.builder()
                        .title(title)
                        .appointmentDate(appointmentDate)
                        .appointmentTime(appointmentTime)
                        .latitude(latitude)
                        .longitude(longitude)
                        .location(location)
                        .address(address)
                        .build()
        );

        participantService.addParticipant(user, appointment, true);

        if (participantIds != null && !participantIds.isEmpty()) {
            String[] participantIdArray = participantIds.split(",");
            for (String participantId : participantIdArray) {
                if (Long.valueOf(participantId).equals(user.getUserId())) {
                    continue;
                }
                User participant = userService.findByUserId(Long.valueOf(participantId));
                if (participant == null) {
                    log.error("Participant with userId {} not found.", participantId);
                    continue;  // 참가자가 존재하지 않으면 건너뛰기
                }
                participantService.addParticipant(participant, appointment, false);
            }
        }

        return appointment.getAppointmentId();
    }

    @Override
    public void updateAppointmentAndParticipant(Appointment appointment, String title, LocalDate appointmentDate, LocalTime appointmentTime, Double latitude, Double longitude, String location, String address, String newParticipantIds) {
        // 참가자 수정
        participantService.updateParticipant(appointment, newParticipantIds);

        appointment.setTitle(title);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setLocation(location);
        appointment.setAddress(address);
        appointment.setLatitude(latitude);
        appointment.setLongitude(longitude);

        appointmentRepository.save(appointment);
    }

    @Override
    public void deleteAppointment(Appointment appointment) {
        appointmentRepository.delete(appointment);
    }

    @Override
    public OngoingAppointmentDto getAppointmentDetails(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약속의 정보가 없습니다."));

        // 시간에 따른 약속 상태 변경
        updateAppointmentStatus(appointment);

        OngoingAppointmentDto appointmentDto = new OngoingAppointmentDto();
        appointmentDto.setAppointmentId(appointmentId);
        appointmentDto.setTitle(appointment.getTitle());
        appointmentDto.setAppointmentDate(appointment.getAppointmentDate());
        appointmentDto.setAppointmentTime(appointment.getAppointmentTime());
        appointmentDto.setLatitude(appointment.getLatitude());
        appointmentDto.setLongitude(appointment.getLongitude());
        appointmentDto.setLocation(appointment.getLocation());
        appointmentDto.setAddress(appointment.getAddress());
        appointmentDto.setState(appointment.getState());

        // D-day 계산
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        long diffDay = ChronoUnit.DAYS.between(appointment.getAppointmentDate(), today);

        // D-day
        appointmentDto.setDiffDay(diffDay);

        // 참가자 리스트 정보
        List<Participant> participants = participantRepository.findByAppointment(appointment);

        // 참가자 수
        appointmentDto.setParticipantCount(participants.size());

        // 참가자 정보 dto 추가
        List<OngoingParticipantDto> participantDtoList = new ArrayList<>();
        for (Participant participant : participants) {

            // 지각 or not
            boolean isOnTime = checkIsOnTime(participant.getUser().getAccumulatedTime());

            OngoingParticipantDto participantDto = new OngoingParticipantDto();
            participantDto.setUserId(participant.getUser().getUserId());
            participantDto.setIsHost(participant.getIsHost());
            participantDto.setIsArrived(participant.getIsArrived());
            participantDto.setIsOnTime(isOnTime);
            participantDtoList.add(participantDto);
        }
        appointmentDto.setParticipantList(participantDtoList);

        return appointmentDto;
    }

    @Override
    public EndAppointmentDto getEndAppointmentDetails(Long appointmentId, Long userId) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약속의 정보가 없습니다."));

        // 시간에 따른 약속 상태 변경
        //updateAppointmentStatus(appointment);

        EndAppointmentDto endAppointmentDto = new EndAppointmentDto();
        endAppointmentDto.setAppointmentId(appointmentId);
        endAppointmentDto.setTitle(appointment.getTitle());
        endAppointmentDto.setAppointmentDate(appointment.getAppointmentDate());
        endAppointmentDto.setAppointmentTime(appointment.getAppointmentTime());
        endAppointmentDto.setLatitude(appointment.getLatitude());
        endAppointmentDto.setLongitude(appointment.getLongitude());
        endAppointmentDto.setLocation(appointment.getLocation());
        endAppointmentDto.setAddress(appointment.getAddress());
        endAppointmentDto.setState(appointment.getState());

        // 참가자 리스트 정보
        List<Participant> endParticipants = participantRepository.findByAppointment(appointment);

        // 참가자 정보 dto 추가
        List<EndParticipantDto> endParticipantList = new ArrayList<>();
        for (Participant endParticipant : endParticipants) {

            User user = endParticipant.getUser();

            Boolean isOnTime = checkIsOnTime(user.getAccumulatedTime());

            EndParticipantDto endParticipantDto = new EndParticipantDto();
            endParticipantDto.setUserId(user.getUserId());
            endParticipantDto.setProfile(user.getProfile());
            endParticipantDto.setIsOnTime(isOnTime);
            endParticipantDto.setLateTime(endParticipant.getLateTime());
            endParticipantList.add(endParticipantDto);
        }
        endAppointmentDto.setEndParticipantDto(endParticipantList);

        return endAppointmentDto;

    }

    @Override
    public List<AppointmentListDto> getAppointmentList(Long userId, LocalDate appointmentDate) {
        User user = userService.findByUserId(userId);

        // 해당 user가 참여하고 있는 participant 목록 가져오기
        List<Participant> participants = participantRepository.findByUser(user);

        List<AppointmentListDto> appointmentListDtoS = new ArrayList<>();

        for (Participant participant : participants) {
            // 참여한 약속
            Appointment appointment = participant.getAppointment();

            // 입력한 날짜와 같은 약속이면
            if (appointment.getAppointmentDate().equals(appointmentDate)) {

                // 시간에 따른 약속 상태 변경
                updateAppointmentStatus(appointment);

                // 해당 약속 정보 저장
                AppointmentListDto appointmentListDto = new AppointmentListDto();
                appointmentListDto.setAppointmentId(appointment.getAppointmentId());
                appointmentListDto.setTitle(appointment.getTitle());
                appointmentListDto.setAppointmentDate(appointmentDate);
                appointmentListDto.setAppointmentTime(appointment.getAppointmentTime());
                appointmentListDto.setLocation(appointment.getLocation());
                appointmentListDto.setAddress(appointment.getAddress());
                appointmentListDto.setLatitude(appointment.getLatitude());
                appointmentListDto.setLongitude(appointment.getLongitude());
                appointmentListDto.setState(appointment.getState());
                // 남은 시간 (-1이면 약속 시간 1시간 전)
                appointmentListDto.setDiffHours(getTimeHoursDifference(appointment.getAppointmentDate(), appointment.getAppointmentTime()));
                // 남은 분 (-40이면 약속 시간 40분 전)
                appointmentListDto.setDiffMinutes(getTimeMinutesDifference(appointment.getAppointmentDate(), appointment.getAppointmentTime()));

                // 해당 약속의 참가자 리스트 정보
                List<Participant> participantList = participantRepository.findByAppointment(appointment);
                // 해당 약속의 참가자 수 저장
                appointmentListDto.setParticipantCount(participantList.size());

                // 참가자 정보 dto 추가
                List<AppointmentListParticipantDto> appointmentHomeListParticipantDto = new ArrayList<>();
                for (Participant participant1 : participantList) {
                    AppointmentListParticipantDto appointmentListParticipantDto = new AppointmentListParticipantDto();
                    appointmentListParticipantDto.setUserId(participant1.getUser().getUserId());
                    appointmentListParticipantDto.setProfile(participant1.getUser().getProfile());
                    appointmentHomeListParticipantDto.add(appointmentListParticipantDto);
                }
                // 해당 약속의 참가자 저장
                appointmentListDto.setParticipantList(appointmentHomeListParticipantDto);

                // 해당 날짜의 약속 리스트에 현재 약속 정보 저장
                appointmentListDtoS.add(appointmentListDto);
            }
        }

        return appointmentListDtoS.stream()
                .sorted(Comparator.comparingInt((AppointmentListDto dto) -> dto.getState() == 1 ? 0 : 1)
                        .thenComparing(AppointmentListDto::getAppointmentTime))
                .collect(Collectors.toList());
    }

    @Override
    public Appointment getAppointmentById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 appointmentId"));
    }


    public Boolean checkIsOnTime(int arrivalTime){
        boolean isOnTime = false;

        if(arrivalTime < 0){
            isOnTime = true;
        }

        return isOnTime;
    }

    @Override
    public void updateAppointmentStatus(Appointment appointment) {
        LocalDateTime appointmentDateTime = LocalDateTime.of(appointment.getAppointmentDate(), appointment.getAppointmentTime());
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        long diffMinutes = ChronoUnit.MINUTES.between(now, appointmentDateTime);

        if (diffMinutes > 60) {
            appointment.setState(0);
        } else if (diffMinutes >= 0 && appointment.getState() <= 0) {
            appointment.setState(1); // gps 공유

        } else if(diffMinutes < -60 && appointment.getState() <= 1){
            appointment.setState(2); // 약속 끝
        }
        appointmentRepository.save(appointment);
    }

    @Override
    public List<AppointmentListDto> getAllAppointmentsByState(Long userId, int state) {
        // 상태 검증
        if (state < 0 || state > 2) {
            throw new IllegalArgumentException("유효하지 않은 state 값입니다.");
        }

        // 사용자 조회
        User user = userService.findByUserId(userId);

        // 해당 사용자와 관련된 참가자 목록 조회
        List<Participant> participants = participantRepository.findByUser(user);

        List<AppointmentListDto> filteredAppointments = new ArrayList<>();

        for (Participant participant : participants) {
            // 참가자가 속한 약속 정보
            Appointment appointment = participant.getAppointment();

            // 약속 상태를 최신화
            updateAppointmentStatus(appointment);

            // 해당 약속의 상태가 요청한 state와 일치하는 경우만 처리
            if (appointment.getState() == state) {
                filteredAppointments.add(convertToAppointmentListDto(appointment));
            }
        }

        // 결과 리스트 반환
        return filteredAppointments.stream()
                .sorted(Comparator.comparing(AppointmentListDto::getAppointmentDate)
                        .thenComparing(AppointmentListDto::getAppointmentTime))
                .collect(Collectors.toList());
    }

    //(현재 시간 - 약속 시간) 반환 - 시간
    @Override
    public long getTimeHoursDifference(LocalDate appointmentDate, LocalTime appointmentTime) {
        LocalDateTime nowTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);
        return ChronoUnit.HOURS.between(appointmentDateTime, nowTime);
    }

    //(현재 시간 - 약속 시간) 반환 - 분
    @Override
    public long getTimeMinutesDifference(LocalDate appointmentDate, LocalTime appointmentTime) {
        LocalDateTime nowTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);
        return ChronoUnit.MINUTES.between(appointmentDateTime, nowTime);
    }

    // Appointment 엔티티를 AppointmentListDto로 변환
    private AppointmentListDto convertToAppointmentListDto(Appointment appointment) {
        AppointmentListDto dto = new AppointmentListDto();
        dto.setAppointmentId(appointment.getAppointmentId());
        dto.setTitle(appointment.getTitle());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setAppointmentTime(appointment.getAppointmentTime());
        dto.setLocation(appointment.getLocation());
        dto.setAddress(appointment.getAddress());
        dto.setLatitude(appointment.getLatitude());
        dto.setLongitude(appointment.getLongitude());
        dto.setState(appointment.getState());
        dto.setDiffHours(getTimeHoursDifference(appointment.getAppointmentDate(), appointment.getAppointmentTime()));
        dto.setDiffMinutes(getTimeMinutesDifference(appointment.getAppointmentDate(), appointment.getAppointmentTime()));

        // 참가자 정보 포함
        List<Participant> participantList = participantRepository.findByAppointment(appointment);
        dto.setParticipantCount(participantList.size());

        List<AppointmentListParticipantDto> participantDtoList = participantList.stream()
                .map(participant -> {
                    AppointmentListParticipantDto participantDto = new AppointmentListParticipantDto();
                    participantDto.setUserId(participant.getUser().getUserId());
                    participantDto.setProfile(participant.getUser().getProfile());
                    return participantDto;
                })
                .collect(Collectors.toList());
        dto.setParticipantList(participantDtoList);

        return dto;
    }

}
