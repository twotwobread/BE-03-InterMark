package com.prgrms.be.intermark.domain.ticket.service;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prgrms.be.intermark.domain.schedule.Schedule;
import com.prgrms.be.intermark.domain.schedule.repository.ScheduleRepository;
import com.prgrms.be.intermark.domain.schedule_seat.ScheduleSeat;
import com.prgrms.be.intermark.domain.schedule_seat.repository.ScheduleSeatRepository;
import com.prgrms.be.intermark.domain.seat.Seat;
import com.prgrms.be.intermark.domain.seat.repository.SeatRepository;
import com.prgrms.be.intermark.domain.ticket.Ticket;
import com.prgrms.be.intermark.domain.ticket.dto.TicketCreateRequestDTO;
import com.prgrms.be.intermark.domain.ticket.dto.TicketDeleteResponseDto;
import com.prgrms.be.intermark.domain.ticket.repository.TicketRepository;
import com.prgrms.be.intermark.domain.user.User;
import com.prgrms.be.intermark.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;
    private final ScheduleSeatRepository scheduleSeatRepository;

    public Long createTicket(TicketCreateRequestDTO ticketCreateRequestDTO) {

        User user = userRepository.findById(ticketCreateRequestDTO.userId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Seat seat = seatRepository.findById(ticketCreateRequestDTO.seatId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좌석입니다."));

        Schedule schedule = scheduleRepository.findById(ticketCreateRequestDTO.scheduleId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스케줄입니다."));

        ScheduleSeat scheduleSeat = scheduleSeatRepository.findByScheduleAndSeat(schedule, seat)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스케줄_좌석입니다."));

        Ticket ticket = ticketCreateRequestDTO.toEntity(user, scheduleSeat);

        ticketRepository.save(ticket);

        return ticket.getId();
    }

    @Transactional
    public TicketDeleteResponseDto deleteTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> {
                throw new EntityNotFoundException("해당하는 예매가 없습니다.");
            });

        ticket.deleteTicket();

        return TicketDeleteResponseDto.builder()
            .ticketId(ticketId)
            .status(ticket.getTicketStatus())
            .message("티켓이 삭제 처리 되었습니다.")
            .build();
    }
}