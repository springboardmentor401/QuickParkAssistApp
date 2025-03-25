package com.qpa.service;

import com.qpa.entity.Location;
import com.qpa.exception.ResourceNotFoundException;
import com.qpa.repository.LocationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    // ✅ Add a new location
    public Location addLocation(Location location) {
        return locationRepository.save(location);
    }

    // ✅ Update an existing location
    public Location updateLocation(Long id, Location newLocation) {
        return locationRepository.findById(id).map(location -> {
            location.setLatitude(newLocation.getLatitude());
            location.setLongitude(newLocation.getLongitude());
            location.setBuildingName(newLocation.getBuildingName());
            location.setStreetAddress(newLocation.getStreetAddress());
            location.setArea(newLocation.getArea());
            location.setCity(newLocation.getCity());
            location.setState(newLocation.getState());
            location.setPincode(newLocation.getPincode());
            location.setFloorNumber(newLocation.getFloorNumber());
            location.setLandmark(newLocation.getLandmark());
            return locationRepository.save(location);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found with ID: " + id));
    }

    // ✅ Get all locations
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    // ✅ Get user-specific locations (assuming userId is stored)
    public List<Location> getUserLocations(Long userId) {
        return locationRepository.findByUser_UserId(userId);
    }

    // ✅ Get location by ID
    public Location getLocationById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found with ID: " + id));
    }

    // ✅ Delete a location by ID
    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found with ID: " + id);
        }
        locationRepository.deleteById(id);
    }

    public boolean checkOwner(Long locationId, Long userId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("location not found with id : " + locationId));
        return location.getUser().getUserId().equals(userId);
    }

}
