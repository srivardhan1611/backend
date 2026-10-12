package com.hostelfinder.service;

import com.hostelfinder.entity.College;
import com.hostelfinder.exception.ApiException;
import com.hostelfinder.repository.CollegeRepository;
import com.hostelfinder.repository.HostelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CollegeService {
    private final CollegeRepository colleges;
    private final FileStorageService files;
    private final HostelRepository hostels;

    @Transactional
    public College create(String name, String address, double latitude, double longitude, MultipartFile image) {
        if (name == null || name.isBlank()) throw new ApiException(HttpStatus.BAD_REQUEST, "College name is required");
        String clean = name.trim();
        if (clean.length() > 255) throw new ApiException(HttpStatus.BAD_REQUEST, "College name is too long (max 255 characters)");
        if (address != null && address.length() > 500) throw new ApiException(HttpStatus.BAD_REQUEST, "Address is too long (max 500 characters)");
        boolean exists = colleges.findByCollegeNameContainingIgnoreCase(clean).stream()
                .anyMatch(c -> c.getCollegeName().equalsIgnoreCase(clean));
        if (exists) throw new ApiException(HttpStatus.CONFLICT, "This college already exists");
        College c = new College();
        c.setCollegeName(clean);
        c.setAddress(address);
        c.setLatitude(latitude);
        c.setLongitude(longitude);
        c.setImageUrl(files.saveImage(image));
        return colleges.save(c);
    }

    @Transactional
    public College setImage(Long id, MultipartFile image) {
        College c = colleges.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "College not found"));
        String url = files.saveImage(image);
        if (url == null) throw new ApiException(HttpStatus.BAD_REQUEST, "Please choose an image");
        files.delete(c.getImageUrl());   // remove the old photo file
        c.setImageUrl(url);
        return c;
    }

    /** Admin only. A college that still has hostels cannot be removed. */
    @Transactional
    public void delete(Long id) {
        College c = colleges.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "College not found"));
        long n = hostels.countByCollegeCollegeId(id);
        if (n > 0) throw new ApiException(HttpStatus.CONFLICT,
                "Cannot remove this college: it still has " + n + " hostel(s). Remove or move those hostels first.");
        files.delete(c.getImageUrl());
        colleges.delete(c);
    }
}
