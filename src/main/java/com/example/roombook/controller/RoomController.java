package com.example.roombook.controller;

import com.example.roombook.model.Room;
import com.example.roombook.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/{id}")
    public Room getRoom(@PathVariable Long id) {
        return roomService.getRoomById(id);
    }

    @PostMapping
    public Room createRoom(@Valid @RequestBody Room room) {
        return roomService.saveRoom(room);
    }

    @PutMapping("/{id}")
    public Room updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody Room room) {

        return roomService.updateRoom(id, room);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok("Room deleted successfully");
    }
}