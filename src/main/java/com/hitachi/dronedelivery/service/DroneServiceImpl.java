package com.hitachi.dronedelivery.service;

import com.hitachi.dronedelivery.constant.Message;
import com.hitachi.dronedelivery.entity.Drone;
import com.hitachi.dronedelivery.entity.Medicine;
import com.hitachi.dronedelivery.enums.Model;
import com.hitachi.dronedelivery.enums.State;
import com.hitachi.dronedelivery.repository.DroneRepository;
import com.hitachi.dronedelivery.repository.MedicineRepository;
import com.hitachi.dronedelivery.request.DroneRegisterRequest;
import com.hitachi.dronedelivery.response.CommonResponse;
import com.hitachi.dronedelivery.response.DroneDetails;
import com.hitachi.dronedelivery.response.MedicineDetails;
import com.hitachi.dronedelivery.util.MessageUtil;
import com.hitachi.dronedelivery.util.ValidationUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DroneServiceImpl implements DroneService {

    private static final Logger logger = LoggerFactory.getLogger(DroneServiceImpl.class);
    
    private final MessageUtil messageUtil;
    
    private final DroneRepository droneRepository;

    private final MedicineRepository medicineRepository;

    @Autowired
    public DroneServiceImpl(MessageUtil messageUtil, DroneRepository droneRepository, MedicineRepository medicineRepository) {
        this.messageUtil = messageUtil;
        this.droneRepository = droneRepository;
        this.medicineRepository = medicineRepository;
    }

    @Transactional
    public CommonResponse<Object> registerDrone(DroneRegisterRequest request) {
        // Create Drone object and save
        Drone drone = createDroneFromRequest(request);
        Drone savedDrone = droneRepository.save(drone);

        // Create DroneDetails object for the saved drone
        DroneDetails droneDetails = createDroneDetails(savedDrone);

        // Return success response with drone details
        return CommonResponse.builder()
                .message(messageUtil.getMessage(Message.DRONE_REGISTER_SUCCESS))
                .data(Arrays.asList(droneDetails))
                .build();
    }

    public List<DroneDetails> getAllDrones() {
        return createAllDroneDetails(droneRepository.findAll());
    }

    public DroneDetails getDroneById(Long id) {
        Optional<Drone> drone = droneRepository.findById(id);
        if (drone.isPresent()) {
            return createDroneDetails(drone.get());
        }
        return null;
    }

    @Transactional
    public CommonResponse<Object> loadMedication(String name, Double weight, String code, MultipartFile image, DroneDetails droneDetails) throws IOException {
        Optional<Drone> droneData = droneRepository.findById(droneDetails.getId());

        if (droneData.isEmpty()) {
            return CommonResponse.builder()
                    .message(messageUtil.getMessage(Message.DRONE_NOT_FOUND))
                    .build();
        }
        Drone drone = droneData.get();

        // Calculate the total weight of existing medicines
        double totalWeight = drone.getMedicines().stream()
                .mapToDouble(Medicine::getWeight)
                .sum();

        // Update the load weight of the drone
        double newLoadWeight = totalWeight + weight;
        droneDetails.setLoadWeight(newLoadWeight);

        // If the load weight matches max weight, update the state of the drone
        if (droneDetails.getModel().getMaxWeight() == newLoadWeight) {
            drone.setState(State.LOADED);
        }

        Medicine medicine = Medicine.builder()
                .name(name)
                .weight(weight)
                .code(code)
                .image(image.getBytes())
                .drone(drone)
                .build();
        drone.getMedicines().add(medicine);

        droneRepository.save(drone);
        return CommonResponse.builder()
                .message(messageUtil.getMessage(Message.DRONE_LOADING_SUCCESS))
                .data(Arrays.asList(createMedicineDetails(drone.getMedicines())))
                .build();
    }


    @Scheduled(cron = "0 */5 * * * *") // Runs every 5 minutes
    public void updateStateAndBattery() {
        logger.info("start battery percentage and state update job");
        List<Drone> drones = droneRepository.findAll();

        // Simulate battery percentage update
        drones.forEach(drone -> {
            int newBatteryPercentage = simulateBattery(drone.getState(), drone.getBatteryPercentage());
            drone.setBatteryPercentage(newBatteryPercentage);
            State newState = simulateState(drone.getState());
            drone.setState(newState);
            logger.info("Updated drone {} battery to {}% state to {}", drone.getSerialNumber(), newBatteryPercentage, newState);
        });

        // Save all updated drones
        droneRepository.saveAll(drones);

        logger.info("end battery percentage and state update job");
    }

    public int simulateBattery(State state, int currentBattery) {
        int drainRate = 5; // Decrease by 5% per minute
        int chargeRate = 10; // Decrease by 10% per minute
        return state.equals(State.IDLE) ? Math.min(100, currentBattery + chargeRate) : Math.max(0, currentBattery - drainRate);
    }

    private static final Map<State, State> STATE_TRANSITIONS = Map.of(
            State.LOADING, State.LOADED,
            State.LOADED, State.DELIVERING,
            State.DELIVERING, State.DELIVERED,
            State.DELIVERED, State.RETURNING,
            State.RETURNING, State.IDLE
    );

    private State simulateState(State state) {
        return STATE_TRANSITIONS.getOrDefault(state, State.IDLE);
    }

    private List<MedicineDetails> createMedicineDetails(List<Medicine> medicineList) {
        return medicineList.stream()
            .map(meds -> MedicineDetails.builder()
                .name(meds.getName())
                .weight(meds.getWeight())
                .code(meds.getCode())
                .build())
            .collect(Collectors.toList());
    }

    private Drone createDroneFromRequest(DroneRegisterRequest request) {
        return Drone.builder()
                .serialNumber(request.getSerialNumber())
                .model(Model.valueOf(request.getModel().toUpperCase()))
                .loadWeight(0.0)
                .batteryPercentage(request.getBatteryPercentage())
                .state(State.IDLE)
                .build();
    }

    private List<DroneDetails> createAllDroneDetails(List<Drone> droneList) {
        return droneList.stream()
                .map(drone -> DroneDetails.builder()
                        .id(drone.getId())
                        .serialNumber(drone.getSerialNumber())
                        .model(drone.getModel())
                        .loadWeight(drone.getLoadWeight())
                        .batteryPercentage(drone.getBatteryPercentage())
                        .state(drone.getState())
                        .build())
                .collect(Collectors.toList());
    }

    private DroneDetails createDroneDetails(Drone savedDrone) {
        return DroneDetails.builder()
                .id(savedDrone.getId())
                .serialNumber(savedDrone.getSerialNumber())
                .model(savedDrone.getModel())
                .loadWeight(savedDrone.getLoadWeight())
                .batteryPercentage(savedDrone.getBatteryPercentage())
                .state(savedDrone.getState())
                .build();
    }

    public CommonResponse<Object> validateMedication(String name, Double weight, String code, DroneDetails drone) {
        if (!(drone.getState().equals(State.IDLE) || drone.getState().equals(State.LOADING))) {
            return createErrorResponse(Message.LOADING_STATE_INVALID, new String[]{drone.getState().name()});
        }

        if (drone.getBatteryPercentage() < 25) {
            return createErrorResponse(Message.DRONE_BATTERY_INSUFFICIENT);
        }

        if (!ValidationUtil.isValidName(name)) {
            return createErrorResponse(Message.MEDICINE_NAME_INVALID);
        }

        if (weight < 0 || weight > drone.getModel().getMaxWeight()) {
            return createErrorResponse(Message.MODEL_WEIGHT_INVALID, new String[]{drone.getModel().name(), drone.getModel().getMaxWeight().toString()});
        }

        List<Medicine> medicines = medicineRepository.findByDroneId(drone.getId());
        double totalWeight = medicines.stream()
                .mapToDouble(Medicine::getWeight)  // Extracts the weight of each medicine
                .sum();
        if ((totalWeight + weight) > drone.getModel().getMaxWeight()) {
            return createErrorResponse(Message.MODEL_WEIGHT_OVERLOAD);
        }

        if (!ValidationUtil.isValidCode(code)) {
            return createErrorResponse(Message.MEDICINE_CODE_INVALID);
        }

        return null;  // No errors found
    }

    public CommonResponse<Object> validateDroneRequest(DroneRegisterRequest request) {
        if (request.getSerialNumber().length() > 100 || request.getSerialNumber().length() < 0) {
            return createErrorResponse(Message.SERIAL_NUMBER_INVALID);
        }

        if (!isValidModel(request.getModel())) {
            return createErrorResponse(Message.MODEL_INVALID);
        }

        List<Drone> drones = droneRepository.findAll();
        if (drones.size() == 10) {
            return createErrorResponse(Message.DRONE_MAXIMUM_LIMIT);
        }

        boolean exists = drones.stream()
                .anyMatch(drone -> drone.getSerialNumber().equals(request.getSerialNumber()));
        if (exists) {
            return createErrorResponse(Message.DRONE_EXISTS, request.getSerialNumber());
        }

        return null;  // No errors found
    }

    public boolean isValidModel(String model) {
        try {
            Model.valueOf(model.toUpperCase());
            return true;
        } catch (IllegalArgumentException ie) {
            return false;
        }
    }

    private static CommonResponse<Object> createErrorResponse(String message, String... args) {
        return CommonResponse.builder()
                .message(MessageUtil.getMessage(message, args))
                .build();
    }
}
