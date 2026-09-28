package com.example.roombook.service;

import com.example.roombook.model.Room;
import com.example.roombook.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }

    public Room updateRoom(Long id, Room room) {
        Room existing = getRoomById(id);

        existing.setRoomName(room.getRoomName());
        existing.setCapacity(room.getCapacity());
        existing.setAmenities(room.getAmenities());
        existing.setLocation(room.getLocation());
        existing.setStatus(room.getStatus());

        return roomRepository.save(existing);
    }

    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Room not found");
        }

        roomRepository.deleteById(id);
    }
}