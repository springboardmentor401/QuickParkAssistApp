package com.qpa.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.qpa.dto.SpotSearchCriteria;
import com.qpa.dto.SpotStatistics;
import com.qpa.entity.Spot;
import com.qpa.entity.SpotStatus;
import com.qpa.entity.VehicleType;
import com.qpa.exception.InvalidEntityException;
import com.qpa.exception.ResourceNotFoundException;
import com.qpa.repository.SpotBookingInfoRepository;
import com.qpa.repository.SpotRepository;
import com.qpa.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SpotService {
    private final SpotRepository spotRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final SpotBookingInfoRepository bookingRepository;

    public SpotService(SpotRepository spotRepository,
            UserRepository userRepository, SpotBookingInfoRepository bookingRepository,
            CloudinaryService cloudinaryService) {
        this.spotRepository = spotRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public Spot createSpot(Spot spot, MultipartFile spotImage, Long userId) throws IOException {

        spot.setOwner(userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId)));

        String imageUrl = cloudinaryService.uploadImage(spotImage);
        spot.setImageUrl(imageUrl);

        return spotRepository.save(spot);
    }

    public void updateSpot(Long spotId, Spot spot, MultipartFile spotImage)
            throws InvalidEntityException, IOException {

        Spot existingSpot = spotRepository.findById(spotId).get();
        if (existingSpot.getImageUrl() != null) {
            cloudinaryService.deleteImage(spot.getImageUrl());
        }
        String imageUrl = cloudinaryService.uploadImage(spotImage);
        spot.setImageUrl(imageUrl);

        spotRepository.save(spot);
    }

    public void deleteSpot(Long spotId) {
        spotRepository.deleteById(spotId);
    }

    public Spot getSpot(Long spotId) {
        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new ResourceNotFoundException("Spot not found with id : " + spotId));
        return spot;
    }

    public List<Spot> getAllSpots() {
        return spotRepository.findAll();
    }

    public List<Spot> getSpotByOwner(Long userId) {
        return spotRepository.findByOwner_UserId(userId);
    }

    public List<Spot> searchSpots(SpotSearchCriteria criteria) {
        List<Spot> spots = spotRepository.findByLocationFilters(
                criteria.getCity());

        List<Spot> filteredSpots = spots.stream()
                .filter(spot -> spot.getIsActive() == true)
                .filter(spot -> criteria.getSpotType() == null || spot.getSpotType() == criteria.getSpotType())
                .filter(spot -> criteria.getHasEVCharging() == null
                        || spot.getHasEVCharging() == criteria.getHasEVCharging())
                .filter(spot -> criteria.getPriceType() == null || spot.getPriceType() == criteria.getPriceType())
                .filter(spot -> criteria.getSupportedVehicleType() == null ||
                        spot.getSupportedVehicleTypes().contains(criteria.getSupportedVehicleType()))
                .filter(spot -> criteria.getStatus() == null || spot.getStatus() == criteria.getStatus())
                .collect(Collectors.toList());

        return filteredSpots;
    }

    public void rateSpot(Long spotId, Double rating) {
        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new ResourceNotFoundException("Spot not found with id : " + spotId));

        spot.setRating(rating);
        spotRepository.save(spot);

    }

    public SpotStatistics getStatistics() {
        List<Spot> allSpots = spotRepository.findAll();

        // Calculate total spots
        long totalSpots = allSpots.size();

        // Calculate available and unavailable spots
        long availableSpots = allSpots.stream()
                .filter(spot -> spot.getStatus() == SpotStatus.AVAILABLE)
                .count();
        long unavailableSpots = totalSpots - availableSpots;

        return new SpotStatistics(
                totalSpots,
                availableSpots,
                unavailableSpots);
    }

    public List<Spot> getSpotsByEVCharging(boolean hasEVCharging) {
        return spotRepository.findByHasEVCharging(hasEVCharging);
    }

    public List<Spot> getAvailableSpotsByCityAndVehicle(String city, VehicleType vehicleType) {
        return spotRepository.findAvailableSpotsByCityAndVehicleType(city, vehicleType);
    }

    public List<Spot> getAvailableSpots() {
        return spotRepository.findByStatus(SpotStatus.AVAILABLE);

    }

    public Spot getSpotByBookingId(long bookingId) throws InvalidEntityException {
        return bookingRepository.findSpotByBookingId(bookingId);
    }

    public List<Spot> getBookedSpots() {
        return bookingRepository.findBookedSpots();

    }

    public List<Spot> getAvailableSpotsByStartAndEndDate(LocalDate startDate, LocalDate endDate) {
        return bookingRepository.findSpotsByStartAndEndDate(startDate, endDate);
    }

    public void toggleSpotActivation(Long spotId) {
        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new ResourceNotFoundException("Spot not found with id : " + spotId));
        spot.setIsActive(!spot.getIsActive());
        spotRepository.save(spot);
    }

    public boolean checkOwner(Long spotId, Long userId) {
        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new ResourceNotFoundException("Spot not found with id : " + spotId));
        return spot.getOwner().getUserId().equals(userId);
    }

}
