package com.hostelfinder.controller;

import com.hostelfinder.dto.Dtos.*;
import com.hostelfinder.entity.College;
import com.hostelfinder.service.CollegeService;
import com.hostelfinder.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/owner")
@RequiredArgsConstructor
public class OwnerController {
    private final OwnerService owner;
    private final CollegeService collegeService;

    @GetMapping("/hostels")
    public List<HostelResponse> mine(Authentication a) { return owner.list(a.getName()); }

    @PostMapping("/hostels")
    public HostelResponse create(Authentication a, @Valid @RequestBody HostelRequest r) { return owner.create(a.getName(), r); }

    @PutMapping("/hostels/{id}")
    public HostelResponse update(Authentication a, @PathVariable Long id, @Valid @RequestBody HostelRequest r) {
        return owner.update(a.getName(), id, r);
    }

    @DeleteMapping("/hostels/{id}")
    public void delete(Authentication a, @PathVariable Long id) { owner.delete(a.getName(), id); }

    @PostMapping("/hostels/{id}/rooms")
    public HostelResponse addRoom(Authentication a, @PathVariable Long id, @Valid @RequestBody RoomRequest r) {
        return owner.addRoom(a.getName(), id, r);
    }

    @PutMapping("/rooms/{roomId}/availability")
    public HostelResponse availability(Authentication a, @PathVariable Long roomId, @RequestParam int availableBeds) {
        return owner.setAvailability(a.getName(), roomId, availableBeds);
    }

    /** Body: {"name":"Wifi"} */
    @PostMapping("/hostels/{id}/facilities")
    public HostelResponse facility(Authentication a, @PathVariable Long id, @RequestBody Map<String, String> body) {
        return owner.addFacility(a.getName(), id, body.get("name"));
    }

    /** Body: {"url":"https://..."} (store the image in cloud storage, send only the URL) */
    @PostMapping("/hostels/{id}/images")
    public HostelResponse image(Authentication a, @PathVariable Long id, @RequestBody Map<String, String> body) {
        return owner.addImage(a.getName(), id, body.get("url"));
    }

    /** Owner adds a new college (with optional photo). multipart/form-data */
    @PostMapping(value = "/colleges", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public College addCollege(@RequestParam("collegeName") String collegeName,
                              @RequestParam(value = "address", required = false) String address,
                              @RequestParam(value = "latitude", defaultValue = "0") double latitude,
                              @RequestParam(value = "longitude", defaultValue = "0") double longitude,
                              @RequestParam(value = "image", required = false) MultipartFile image) {
        return collegeService.create(collegeName, address, latitude, longitude, image);
    }
}
