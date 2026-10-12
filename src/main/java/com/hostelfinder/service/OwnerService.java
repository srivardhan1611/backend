package com.hostelfinder.service;

import com.hostelfinder.dto.Dtos.*;
import com.hostelfinder.entity.*;
import com.hostelfinder.exception.ApiException;
import com.hostelfinder.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OwnerService {
    private final UserRepository users;
    private final CollegeRepository colleges;
    private final HostelRepository hostels;
    private final RoomRepository rooms;
    private final HostelService hostelService;

    private Hostel own(Long id, String email) {
        Hostel h = hostels.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Hostel not found"));
        if (!h.getOwner().getEmail().equals(email)) throw new ApiException(HttpStatus.FORBIDDEN, "Not your hostel");
        return h;
    }

    private void apply(Hostel h, HostelRequest r) {
        h.setCollege(colleges.findById(r.collegeId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Unknown college")));
        h.setName(r.name());
        h.setAddress(r.address());
        h.setLatitude(r.latitude());
        h.setLongitude(r.longitude());
        h.setRent(r.rent());
        h.setHostelType(r.hostelType());
        h.setFoodAvailable(r.foodAvailable());
        h.setContact(r.contact());
        h.setStatus(Status.PENDING);          // every create/edit needs admin verification
        h.setRejectionReason(null);
    }

    public List<HostelResponse> list(String email) {
        User o = users.findByEmail(email).orElseThrow();
        return hostels.findByOwner(o).stream().map(hostelService::toResponse).toList();
    }

    public HostelResponse create(String email, HostelRequest r) {
        Hostel h = new Hostel();
        h.setOwner(users.findByEmail(email).orElseThrow());
        apply(h, r);
        return hostelService.toResponse(hostels.save(h));
    }

    public HostelResponse update(String email, Long id, HostelRequest r) {
        Hostel h = own(id, email);
        apply(h, r);
        return hostelService.toResponse(h);
    }

    public void delete(String email, Long id) { hostels.delete(own(id, email)); }

    public HostelResponse addRoom(String email, Long hostelId, RoomRequest r) {
        if (r.availableBeds() > r.totalBeds()) throw new ApiException(HttpStatus.BAD_REQUEST, "availableBeds cannot exceed totalBeds");
        Hostel h = own(hostelId, email);
        Room room = new Room();
        room.setHostel(h);
        room.setRoomType(r.roomType());
        room.setTotalBeds(r.totalBeds());
        room.setAvailableBeds(r.availableBeds());
        room.setRent(r.rent());
        h.getRooms().add(room);
        hostels.save(h);
        return hostelService.toResponse(h);
    }

    public HostelResponse setAvailability(String email, Long roomId, int availableBeds) {
        Room room = rooms.findById(roomId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Room not found"));
        Hostel h = own(room.getHostel().getHostelId(), email);
        if (availableBeds < 0 || availableBeds > room.getTotalBeds())
            throw new ApiException(HttpStatus.BAD_REQUEST, "availableBeds must be between 0 and totalBeds");
        room.setAvailableBeds(availableBeds);   // availability updates do not need re-verification
        return hostelService.toResponse(h);
    }

    public HostelResponse addFacility(String email, Long hostelId, String name) {
        Hostel h = own(hostelId, email);
        Facility f = new Facility();
        f.setHostel(h);
        f.setFacilityName(name);
        h.getFacilities().add(f);
        hostels.save(h);
        return hostelService.toResponse(h);
    }

    public HostelResponse addImage(String email, Long hostelId, String url) {
        Hostel h = own(hostelId, email);
        HostelImage i = new HostelImage();
        i.setHostel(h);
        i.setImageUrl(url);
        h.getImages().add(i);
        hostels.save(h);
        return hostelService.toResponse(h);
    }
}
