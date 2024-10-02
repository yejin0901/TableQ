package com.project.team11_tabling.domain.booking.service;


import lombok.RequiredArgsConstructor;
import com.project.team11_tabling.domain.booking.entity.BookingType;
import com.project.team11_tabling.domain.booking.entity.Booking;
import com.project.team11_tabling.domain.booking.repository.BookingRepository;

@Service
@RequiredArgsConstructor
public class BookingService {
    private static final String SUCCESS_WAITING = "님 웨이팅이 등록되었습니다"; //나중에 메시지 만들기
    private final WaitingQueueService queue;
    private final BookingRepository bookingRepository;

    public void registerWaiting(Long userId, Long shopId) {
        bookingRepository.save(new Booking(shopId, userId, BookingType.WAITING));
        queue.addQueue(shopId.toString(), userId.toString());
    }

    public void completeWaiting(Long userId, Long shopId) {
        bookingRepository.save(new Booking(shopId, userId, BookingType.DONE));
        queue.popQueue(shopId.toString());
    }

    public void cancelWaiting(Long waitingId) {
        Booking waiting = findWaiting(waitingId);
        queue.removeElement(
                String.valueOf(waiting.getShopId()),
                String.valueOf(waiting.getUserId())
        );
        waiting.updateStatus(BookingType.CANCEL);
    }

    public Long getWaitingInfo(Long shopId) {
        return queue.queueSize(String.valueOf(shopId));
    }

    public Booking findWaiting(Long waitingId) {
        return bookingRepository.findById(waitingId)
                .orElseThrow(() -> new IllegalArgumentException("해당 웨이팅 정보가 없습니다."));
    }

}
