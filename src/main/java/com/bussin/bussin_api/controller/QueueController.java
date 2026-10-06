package com.bussin.bussin_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bussin.bussin_api.dto.JoinQueueRequest;
import com.bussin.bussin_api.dto.QueueResponse;
import com.bussin.bussin_api.dto.UpdateQueueStatusRequest;
import com.bussin.bussin_api.service.QueueService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/queue")
public class QueueController {

    private final QueueService queueService;

    public QueueController(
            QueueService queueService) {

        this.queueService = queueService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QueueResponse joinQueue(
            @Valid @RequestBody JoinQueueRequest request) {

        return QueueResponse.from(
                queueService.joinQueue(
                        request.getTripId()));
    }

    @GetMapping("/trip/{tripId}")
    public List<QueueResponse> getTripQueue(
            @PathVariable Long tripId) {

        return queueService
                .getTripQueue(tripId)
                .stream()
                .map(QueueResponse::from)
                .toList();
    }

    @GetMapping("/employee")
    public List<QueueResponse> getEmployeeQueue() {
        return queueService.getEmployeeQueue()
                .stream()
                .map(QueueResponse::from)
                .toList();
    }

    @GetMapping("/{queueEntryId}")
    public QueueResponse getQueueEntry(
            @PathVariable Long queueEntryId) {

        return QueueResponse.from(
                queueService.getQueueEntry(
                        queueEntryId));
    }

    @GetMapping("/trip/{tripId}/me")
    public QueueResponse getMyQueueEntry(
            @PathVariable Long tripId) {

        return QueueResponse.from(
                queueService.getMyQueueEntry(
                        tripId));
    }

    @PutMapping("/{queueEntryId}/status")
    public QueueResponse updateQueueStatus(
            @PathVariable Long queueEntryId,
            @Valid @RequestBody UpdateQueueStatusRequest request) {

        return QueueResponse.from(
                queueService.updateQueueStatus(
                        queueEntryId,
                        request.getStatus()));
    }

    @DeleteMapping("/{queueEntryId}")
    public QueueResponse cancelQueueEntry(
            @PathVariable Long queueEntryId) {

        return QueueResponse.from(
                queueService.cancelQueueEntry(
                        queueEntryId));
    }
}
