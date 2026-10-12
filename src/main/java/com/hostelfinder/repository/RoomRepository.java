package com.hostelfinder.repository;

import com.hostelfinder.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> { }
