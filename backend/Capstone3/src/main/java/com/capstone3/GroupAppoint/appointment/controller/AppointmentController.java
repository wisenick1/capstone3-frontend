package com.capstone3.GroupAppoint.appointment.controller;

import com.capstone3.GroupAppoint.appointment.dto.*;
import com.capstone3.GroupAppoint.appointment.entity.Appointment;
import com.capstone3.GroupAppoint.appointment.service.AppointmentService;
import com.capstone3.GroupAppoint.appointment.service.ParticipantService;
import com.capstone3.GroupAppoint.kakao.auth.entity.User;
////import com.capstone3.GroupAppoint.kakao.auth.service.OAuth2UserService;
import com.capstone3.GroupAppoint.kakao.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/appoint")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final UserService userService;
    private final ParticipantService participantService;

    @PostMapping
    public ResponseEntity<?> createPlan(
            HttpServletRequest request,
            @RequestBody CreateAppointmentDto createAppointmentDto
    ) {

        // 인증된 사용자 ID 가져오기 (테스트용 userId 설정)
        //String userIdString = request.getRemoteUser();
        //Long userId = Long.parseLong(userIdString);
        Long userId = 1L;

        try {
            // 약속 시간이 현재 시간 이후인지 확인
            if (appointmentService.getTimeMinutesDifference(createAppointmentDto.getAppointmentDate(), createAppointmentDto.getAppointmentTime()) >= 0) {
                String errorMessage = "생성 실패: 약속 시간은 현재 시간 이후 시간으로 설정해야 합니다.";
                log.warn(errorMessage);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("message", errorMessage));
            }

            // 약속 생성
            Long appointmentId = appointmentService.createAppointment(
                    userId,
                    createAppointmentDto.getTitle(),
                    createAppointmentDto.getAppointmentDate(),
                    createAppointmentDto.getAppointmentTime(),
                    createAppointmentDto.getLatitude(),
                    createAppointmentDto.getLongitude(),
                    createAppointmentDto.getLocation(),
                    createAppointmentDto.getAddress(),
                    createAppointmentDto.getParticipantIds()
            );

            // 성공 메시지 반환
            String successMessage = "약속이 생성되었습니다. appointmentId: " + appointmentId;
            log.info(successMessage);
            return ResponseEntity.ok(Collections.singletonMap("message", successMessage));

        } catch (Exception e) {
            // 기타 에러 처리
            String errorMessage = "약속 생성 중 오류가 발생했습니다.";
            log.error(errorMessage, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", errorMessage));
        }
    }


    @PutMapping("/{appointment_id}")
    public ResponseEntity<Map<String, String>> updateAppointment(
            HttpServletRequest request,
            @PathVariable("appointment_id") Long appointmentId,
            @RequestBody UpdateAppointmentDto updateAppointmentDto
    ) {
        Map<String, String> response = new HashMap<>();

        try {
            // 약속 조회
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);

            // 사용자 인증 (테스트용 userId 설정)
            //String userIdString = request.getRemoteUser();
            //Long userId = Long.parseLong(userIdString);

            Long userId = 1L;
            // 사용자 확인
            User user = userService.findByUserId(userId);

            // 약속 상태 업데이트
            appointmentService.updateAppointmentStatus(appointment);

            // 방장 여부 확인
            if (!participantService.findIsHostofAppointment(appointment, user)) {
                log.warn("방장이 아닙니다.");
                response.put("message", "방장이 아닙니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // 약속 상태 확인
            if (appointment.getState() > 1) {
                log.warn("약속이 끝나 수정할 수 없습니다.");
                response.put("message", "약속이 끝나 수정할 수 없습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // 약속 시간이 유효한지 확인
            if (appointmentService.getTimeMinutesDifference(updateAppointmentDto.getAppointmentDate(), updateAppointmentDto.getAppointmentTime()) >= 0) {
                log.warn("수정 실패: 약속 시간은 현재 시간 이후로 설정해야 합니다.");
                response.put("message", "약속 시간은 현재 시간 이후로 설정해야 합니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // 약속 및 참가자 정보 업데이트
            appointmentService.updateAppointmentAndParticipant(
                    appointment,
                    updateAppointmentDto.getTitle(),
                    updateAppointmentDto.getAppointmentDate(),
                    updateAppointmentDto.getAppointmentTime(),
                    updateAppointmentDto.getLatitude(),
                    updateAppointmentDto.getLongitude(),
                    updateAppointmentDto.getLocation(),
                    updateAppointmentDto.getAddress(),
                    updateAppointmentDto.getParticipantIds()
            );

            String successMessage = String.format(
                    "appointmentId: %d, title: '%s', 수정되었습니다!",
                    appointmentId, updateAppointmentDto.getTitle()
            );
            log.info(successMessage);
            response.put("message", successMessage);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("수정 실패: appointmentId {}는 존재하지 않습니다.", appointmentId, e);
            response.put("message", "해당 appointmentId가 존재하지 않습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("수정 실패: appointmentId {}", appointmentId, e);
            response.put("message", "서버 오류가 발생했습니다. 다시 시도해주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @DeleteMapping("/{appointment_id}")
    public ResponseEntity<Map<String, String>> deletePlan(
            HttpServletRequest request,
            @PathVariable("appointment_id") Long appointmentId
    ) {
        Map<String, String> response = new HashMap<>();

        try {
            // 약속 조회
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);

            // 사용자 인증 (테스트용 userId 설정)
            Long userId = 1L;

            // 사용자 확인
            User user = userService.findByUserId(userId);

            // 약속 상태 업데이트
            appointmentService.updateAppointmentStatus(appointment);

            // 방장 여부 확인
            if (!participantService.findIsHostofAppointment(appointment, user)) {
                log.warn("방장이 아닙니다.");
                response.put("message", "방장이 아닙니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // 약속 상태 확인
            if (appointment.getState() > 1) {
                log.warn("약속이 끝나 삭제할 수 없습니다.");
                response.put("message", "약속이 끝나 삭제할 수 없습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // 약속 삭제
            appointmentService.deleteAppointment(appointment);

            String successMessage = String.format(
                    "appointmentId: %d, title: '%s', 삭제되었습니다!",
                    appointmentId, appointment.getTitle()
            );
            log.info(successMessage);
            response.put("message", successMessage);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("삭제 실패: appointmentId {}는 존재하지 않습니다.", appointmentId, e);
            response.put("message", "해당 appointmentId가 존재하지 않습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("삭제 실패: appointmentId {}", appointmentId, e);
            response.put("message", "서버 오류가 발생했습니다. 다시 시도해주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/{appointment_id}")
    public ResponseEntity<?> getAppointmentDetails(
            @PathVariable("appointment_id") Long appointmentId,
            @RequestParam("state") int state,
            HttpServletRequest request
    ) {
        try {
            if (state == 0 || state == 1) {
                // 진행 중인 약속 정보 조회
                OngoingAppointmentDto ongoingAppointmentDto = appointmentService.getAppointmentDetails(appointmentId);
                log.info("약속 ID {}의 상태는 진행 중 (state={})입니다.", appointmentId, state);
                return ResponseEntity.ok(ongoingAppointmentDto);
            } else if (state == 2) {
                // 종료된 약속 정보 조회
                //String userIdString = request.getRemoteUser();
                //Long userId = Long.parseLong(userIdString);
                Long userId = 1L;
                EndAppointmentDto endPlanDto = appointmentService.getEndAppointmentDetails(appointmentId, userId);
                log.info("약속 ID {}의 상태는 종료됨 (state=2)입니다.", appointmentId);
                return ResponseEntity.ok(endPlanDto);
            } else {
                // 유효하지 않은 상태 값
                log.error("잘못된 상태 값: {}", state);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("state는 0, 1, 또는 2여야 합니다.");
            }
        } catch (IllegalArgumentException e) {
            log.error("조회 실패: appointmentId {}는 존재하지 않습니다.", appointmentId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("조회 실패: appointmentId {}", appointmentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @GetMapping
    public ResponseEntity<List<AppointmentListDto>> getAppointmentListByState(
            HttpServletRequest request,
            @RequestParam("state") int state
    ){
        //String userIdString = request.getRemoteUser();
        //Long userId = Long.parseLong(userIdString);
        Long userId = 1L;

        List<AppointmentListDto> appointmentList;
        try {
            appointmentList = appointmentService.getAllAppointmentsByState(userId, state);
        } catch (IllegalArgumentException e) {
            log.error("조회 실패: state {}가 올바르지 않습니다.", state, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // 결과 반환
        return new ResponseEntity<>(appointmentList, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<AppointmentListDto>> DayAppointmentList(
            HttpServletRequest request,
            @RequestParam("appointmentDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate planDate) {

        //String userIdString = request.getRemoteUser();
        //Long userId = Long.parseLong(userIdString);
        Long userId = 1L;
        List<AppointmentListDto> appointmentListDto = appointmentService.getAppointmentList(userId, planDate);
        return new ResponseEntity<>(appointmentListDto, HttpStatus.OK);
    }

    /*
    참여자 도착 예정시간에 대한 정보
    @GetMapping("/{appointment_id}/participants")
    public ResponseEntity<List<OngoingParticipantDto>> getParticipants(
            @PathVariable("appointment_id") Long appointmentId
    ) {
        List<OngoingParticipantDto> participants = appointmentService.getParticipantsWithEstimatedArrival(appointmentId);
        return new ResponseEntity<>(participants, HttpStatus.OK);
    }
    */



}
